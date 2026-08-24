package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import com.mojang.datafixers.util.Pair;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.level.SyncedLevelTrackedData;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsEvents;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.core.EC2Constants;
import dev.corgitaco.enhancedcelestials2core.core.lunarevent.DefaultLunarEvents;
import dev.corgitaco.enhancedcelestials2core.lunarevent.LunarEventInstance;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class LunarForecast extends SyncedLevelTrackedData implements TickableTrackedData {
   public static final long EVENT_FADE_LENGTH = 100L;
   private static final float ACTIVE_BLEND_THRESHOLD = 0.01F;
   private final Long2ObjectOpenHashMap<List<LunarEventInstance>> lunarEventsByYear = new Long2ObjectOpenHashMap();
   private final Holder<LunarEvent> defaultLunarEvent;
   private long lastCheckedDay = -1L;
   @Nullable
   private Holder<LunarEvent> lastTickEvent;
   private boolean eventChangedThisTick = false;
   private final Holder<LunarDimensionSettings> lunarDimensionSettings;
   private final SimpleWeightedRandomList<LunarEventScheduler.Candidate<Holder<LunarEvent>>> lunarEventSpawnRequirements;

   public LunarForecast(TrackedDataKey<? extends SyncedLevelTrackedData> trackedDataKey, Level level, Holder<LunarDimensionSettings> lunarDimensionSettings) {
      super(trackedDataKey, level);
      RegistryAccess registryAccess = level.registryAccess();
      Registry<LunarEvent> lunarEventRegistry = (Registry<LunarEvent>)registryAccess.registry(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).orElseThrow();
      this.defaultLunarEvent = lunarEventRegistry.getHolderOrThrow(DefaultLunarEvents.DEFAULT);
      this.lunarDimensionSettings = lunarDimensionSettings;
      this.lunarEventSpawnRequirements = createLunarEventSpawnRequirements(
         level.dimension(),
         (Registry<LunarEventProbabilities>)registryAccess.registry(EnhancedCelestialsRegistry.LUNAR_EVENT_PROBABILITIES_KEY).orElseThrow(),
         lunarEventRegistry,
         this.defaultLunarEvent
      );
   }

   @Nullable
   public CompoundTag save() {
      CompoundTag compoundTag = new CompoundTag();
      RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, this.level.registryAccess());
      CompoundTag lunarEventsByYearTag = new CompoundTag();
      ObjectIterator var4 = this.lunarEventsByYear.long2ObjectEntrySet().iterator();

      while (var4.hasNext()) {
         Entry<List<LunarEventInstance>> entry = (Entry<List<LunarEventInstance>>)var4.next();
         lunarEventsByYearTag.put(
            Long.toString(entry.getLongKey()), (Tag)LunarEventInstance.CODEC.listOf().encodeStart(registryOps, (List)entry.getValue()).getOrThrow()
         );
      }

      compoundTag.put("lunarEventsByYear", lunarEventsByYearTag);
      compoundTag.putLong("lastCheckedDay", this.lastCheckedDay);
      return compoundTag;
   }

   public void load(CompoundTag tag) {
      RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, this.level.registryAccess());
      this.lunarEventsByYear.clear();
      CompoundTag lunarEventsByYearTag = tag.getCompound("lunarEventsByYear");

      for (String yearKey : lunarEventsByYearTag.getAllKeys()) {
         this.lunarEventsByYear
            .put(
               Long.parseLong(yearKey),
               new ArrayList(
                  (Collection)((Pair)LunarEventInstance.CODEC.listOf().decode(registryOps, lunarEventsByYearTag.get(yearKey)).getOrThrow()).getFirst()
               )
            );
      }

      this.lastCheckedDay = tag.getLong("lastCheckedDay");
   }

   public void tick() {
      if (!this.level.isClientSide) {
         this.buildSchedule();
      }

      Holder<LunarEvent> currentEvent = this.currentLunarEventHolder();
      if (this.lastTickEvent == null) {
         this.lastTickEvent = currentEvent;
         this.eventChangedThisTick = false;
      } else if (!currentEvent.equals(this.lastTickEvent)) {
         this.eventChangedThisTick = true;
         if (!this.level.isClientSide) {
            this.notifyEventSwitched(this.lastTickEvent, currentEvent);
         }

         this.lastTickEvent = currentEvent;
      } else {
         this.eventChangedThisTick = false;
      }

      if (!this.level.isClientSide && this.level.getGameTime() % 200L == 0L) {
         this.saveAndSync();
      }
   }

   public boolean didEventChangeThisTick() {
      return this.eventChangedThisTick;
   }

   public boolean isEventActive() {
      return !this.currentLunarEventHolder().equals(this.defaultLunarEvent) && this.getBlend() > 0.01F;
   }

   private void notifyEventSwitched(Holder<LunarEvent> lastEvent, Holder<LunarEvent> nextEvent) {
      ServerLevel serverLevel = (ServerLevel)this.level;

      for (Player player : serverLevel.players()) {
         ((LunarEvent)lastEvent.value()).endNotification().ifPresent(notification -> sendNotification(player, notification));
         ((LunarEvent)nextEvent.value()).startNotification().ifPresent(notification -> sendNotification(player, notification));
      }

      EnhancedCelestialsEvents.fireLunarEventChanged(serverLevel, lastEvent, nextEvent);
   }

   private static void sendNotification(Player player, LunarTextComponents.Notification notification) {
      if (notification.notificationType() != LunarTextComponents.NotificationType.NONE) {
         player.displayClientMessage(
            notification.customTranslationTextComponent().getComponent(), notification.notificationType() == LunarTextComponents.NotificationType.HOT_BAR
         );
      }
   }

   private void buildSchedule() {
      LunarDimensionSettings dimensionSettings = (LunarDimensionSettings)this.lunarDimensionSettings.value();
      long dayLength = dimensionSettings.dayLength();
      long yearLengthInDays = dimensionSettings.yearLengthInDays();
      long today = this.level.getDayTime() / dayLength;
      long currentYear = today / yearLengthInDays;
      if (today != this.lastCheckedDay) {
         this.lastCheckedDay = today;
         this.lunarEventsByYear.long2ObjectEntrySet().removeIf(entry -> Math.abs(entry.getLongKey() - currentYear) > 1L);
      }

      boolean addedYear = false;

      for (long year = Math.max(0L, currentYear - 1L); year <= currentYear + 1L; year++) {
         if (!this.lunarEventsByYear.containsKey(year)) {
            this.lunarEventsByYear.put(year, this.computeYearEvents(year, dimensionSettings));
            addedYear = true;
         }
      }

      if (addedYear) {
         this.saveAndSync();
      }
   }

   private List<LunarEventInstance> computeYearEvents(long year, LunarDimensionSettings dimensionSettings) {
      long dayLength = dimensionSettings.dayLength();
      List<LunarEventScheduler.ScheduledDay<Holder<LunarEvent>>> days = LunarEventScheduler.rollYear(
         this.lunarEventSpawnRequirements,
         this.defaultLunarEvent,
         ((ServerLevel)this.level).getSeed(),
         this.level.dimension().location().hashCode(),
         dimensionSettings,
         year,
         dayTime -> this.level.dimensionType().moonPhase(dayTime)
      );
      List<LunarEventInstance> yearEvents = new ArrayList<>();

      for (LunarEventScheduler.ScheduledDay<Holder<LunarEvent>> day : days) {
         if (day.hasEvent()) {
            long dayStartTick = day.day() * dayLength;
            long nightStart = dayStartTick + dimensionSettings.nightStartTime();
            long nightEnd = dayStartTick + dayLength - 1L;
            yearEvents.add(new LunarEventInstance(day.event(), nightStart, nightEnd, false));
         }
      }

      return yearEvents;
   }

   private void saveAndSync() {
      this.markDirty();
      this.sync();
   }

   public void setLunarEventTonight(Holder<LunarEvent> lunarEvent) {
      if (this.level.isClientSide) {
         throw new IllegalStateException("MUST BE CALLED FROM SERVER SIDE ONLY!");
      } else {
         LunarDimensionSettings dimensionSettings = (LunarDimensionSettings)this.lunarDimensionSettings.value();
         long dayLength = dimensionSettings.dayLength();
         long today = this.level.getDayTime() / dayLength;
         this.buildSchedule();
         long dayTime = this.level.getDayTime();
         List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)this.lunarEventsByYear.get(today / dimensionSettings.yearLengthInDays());

         for (int i = yearEvents.size() - 1; i >= 0; i--) {
            LunarEventInstance scheduledLunarEvent = yearEvents.get(i);
            if (scheduledLunarEvent.startTime() / dayLength == today) {
               if (scheduledLunarEvent.isActive(dayTime) && dayTime > scheduledLunarEvent.startTime()) {
                  yearEvents.set(
                     i,
                     new LunarEventInstance(scheduledLunarEvent.lunarEvent(), scheduledLunarEvent.startTime(), dayTime - 1L, scheduledLunarEvent.setByCommand())
                  );
               } else {
                  yearEvents.remove(i);
               }
            }
         }

         if (!lunarEvent.equals(this.defaultLunarEvent)) {
            long dayStartTick = today * dayLength;
            long nightStart = Math.max(dayStartTick + dimensionSettings.nightStartTime(), dayTime);
            long nightEnd = dayStartTick + dayLength - 1L;
            yearEvents.add(new LunarEventInstance(lunarEvent, nightStart, nightEnd, true));
         }

         this.saveAndSync();
      }
   }

   public boolean insertLunarEvent(Holder<LunarEvent> lunarEvent, long daysInAdvance) {
      if (this.level.isClientSide) {
         throw new IllegalStateException("MUST BE CALLED FROM SERVER SIDE ONLY!");
      } else if (daysInAdvance == 0L) {
         this.setLunarEventTonight(lunarEvent);
         return true;
      } else {
         LunarDimensionSettings dimensionSettings = (LunarDimensionSettings)this.lunarDimensionSettings.value();
         long dayLength = dimensionSettings.dayLength();
         long day = this.level.getDayTime() / dayLength + daysInAdvance;
         this.buildSchedule();
         List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)this.lunarEventsByYear.get(day / dimensionSettings.yearLengthInDays());
         if (yearEvents == null) {
            return false;
         } else {
            yearEvents.removeIf(scheduledLunarEvent -> scheduledLunarEvent.startTime() / dayLength == day);
            if (!lunarEvent.equals(this.defaultLunarEvent)) {
               long dayStartTick = day * dayLength;
               yearEvents.add(new LunarEventInstance(lunarEvent, dayStartTick + dimensionSettings.nightStartTime(), dayStartTick + dayLength - 1L, true));
            }

            this.saveAndSync();
            return true;
         }
      }
   }

   public boolean removeLunarEvent(long daysInAdvance) {
      if (this.level.isClientSide) {
         throw new IllegalStateException("MUST BE CALLED FROM SERVER SIDE ONLY!");
      } else {
         LunarDimensionSettings dimensionSettings = (LunarDimensionSettings)this.lunarDimensionSettings.value();
         long dayLength = dimensionSettings.dayLength();
         long dayTime = this.level.getDayTime();
         long day = dayTime / dayLength + daysInAdvance;
         this.buildSchedule();
         List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)this.lunarEventsByYear.get(day / dimensionSettings.yearLengthInDays());
         if (yearEvents == null) {
            return false;
         } else {
            boolean removed = false;

            for (int i = yearEvents.size() - 1; i >= 0; i--) {
               LunarEventInstance scheduledLunarEvent = yearEvents.get(i);
               if (scheduledLunarEvent.startTime() / dayLength == day) {
                  if (scheduledLunarEvent.isActive(dayTime) && dayTime > scheduledLunarEvent.startTime()) {
                     yearEvents.set(
                        i,
                        new LunarEventInstance(
                           scheduledLunarEvent.lunarEvent(), scheduledLunarEvent.startTime(), dayTime - 1L, scheduledLunarEvent.setByCommand()
                        )
                     );
                  } else {
                     yearEvents.remove(i);
                  }

                  removed = true;
               }
            }

            if (removed) {
               this.saveAndSync();
            }

            return removed;
         }
      }
   }

   public long maxScheduledDaysInAdvance() {
      LunarDimensionSettings dimensionSettings = (LunarDimensionSettings)this.lunarDimensionSettings.value();
      long today = this.level.getDayTime() / dimensionSettings.dayLength();
      long currentYear = today / dimensionSettings.yearLengthInDays();
      return (currentYear + 2L) * dimensionSettings.yearLengthInDays() - 1L - today;
   }

   public Long2ObjectRBTreeMap<Holder<LunarEvent>> upcomingEventsByDaysInAdvance() {
      long dayLength = ((LunarDimensionSettings)this.lunarDimensionSettings.value()).dayLength();
      long today = this.level.getDayTime() / dayLength;
      Long2ObjectRBTreeMap<Holder<LunarEvent>> upcomingEvents = new Long2ObjectRBTreeMap();
      ObjectIterator var6 = this.lunarEventsByYear.values().iterator();

      while (var6.hasNext()) {
         List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)var6.next();

         for (LunarEventInstance scheduledLunarEvent : yearEvents) {
            long dayOffset = scheduledLunarEvent.startTime() / dayLength - today;
            if (dayOffset >= 0L) {
               upcomingEvents.put(dayOffset, scheduledLunarEvent.lunarEvent());
            }
         }
      }

      return upcomingEvents;
   }

   public long currentYear() {
      LunarDimensionSettings dimensionSettings = (LunarDimensionSettings)this.lunarDimensionSettings.value();
      return this.level.getDayTime() / dimensionSettings.dayLength() / dimensionSettings.yearLengthInDays();
   }

   public long currentDay() {
      return this.level.getDayTime() / ((LunarDimensionSettings)this.lunarDimensionSettings.value()).dayLength();
   }

   public boolean hasScheduleFor(long year) {
      return this.lunarEventsByYear.containsKey(year);
   }

   public Long2ObjectRBTreeMap<Holder<LunarEvent>> scheduledEventsByDay(long year) {
      Long2ObjectRBTreeMap<Holder<LunarEvent>> eventsByDay = new Long2ObjectRBTreeMap();
      List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)this.lunarEventsByYear.get(year);
      if (yearEvents == null) {
         return eventsByDay;
      } else {
         long dayLength = ((LunarDimensionSettings)this.lunarDimensionSettings.value()).dayLength();

         for (LunarEventInstance scheduledLunarEvent : yearEvents) {
            eventsByDay.put(scheduledLunarEvent.startTime() / dayLength, scheduledLunarEvent.lunarEvent());
         }

         return eventsByDay;
      }
   }

   public int removeLunarEvents(Predicate<Holder<LunarEvent>> filter) {
      if (this.level.isClientSide) {
         throw new IllegalStateException("MUST BE CALLED FROM SERVER SIDE ONLY!");
      } else {
         long dayLength = ((LunarDimensionSettings)this.lunarDimensionSettings.value()).dayLength();
         long dayTime = this.level.getDayTime();
         long today = dayTime / dayLength;
         this.buildSchedule();
         int removed = 0;
         ObjectIterator var9 = this.lunarEventsByYear.values().iterator();

         while (var9.hasNext()) {
            List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)var9.next();

            for (int i = yearEvents.size() - 1; i >= 0; i--) {
               LunarEventInstance scheduledLunarEvent = yearEvents.get(i);
               if (scheduledLunarEvent.startTime() / dayLength >= today && filter.test(scheduledLunarEvent.lunarEvent())) {
                  if (scheduledLunarEvent.isActive(dayTime) && dayTime > scheduledLunarEvent.startTime()) {
                     yearEvents.set(
                        i,
                        new LunarEventInstance(
                           scheduledLunarEvent.lunarEvent(), scheduledLunarEvent.startTime(), dayTime - 1L, scheduledLunarEvent.setByCommand()
                        )
                     );
                  } else {
                     yearEvents.remove(i);
                  }

                  removed++;
               }
            }
         }

         if (removed > 0) {
            this.saveAndSync();
         }

         return removed;
      }
   }

   @Nullable
   private LunarEventInstance activeLunarEventInstance(long dayTime) {
      ObjectIterator var3 = this.lunarEventsByYear.values().iterator();

      while (var3.hasNext()) {
         List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)var3.next();

         for (LunarEventInstance scheduledLunarEvent : yearEvents) {
            if (scheduledLunarEvent.isActive(dayTime)) {
               return scheduledLunarEvent;
            }
         }
      }

      return null;
   }

   @Nullable
   public Holder<LunarEvent> currentScheduledLunarEvent() {
      LunarEventInstance activeLunarEvent = this.activeLunarEventInstance(this.level.dayTime());
      return activeLunarEvent == null ? this.defaultLunarEvent : activeLunarEvent.lunarEvent();
   }

   public LunarEvent currentLunarEvent() {
      return (LunarEvent)this.currentLunarEventHolder().value();
   }

   public Holder<LunarEvent> currentLunarEventHolder() {
      return this.getDimensionSettings().requiresClearSkies() && this.level.isRaining() ? this.defaultLunarEvent : this.currentLunarEventOrDefault();
   }

   public LunarEvent lastLunarEvent() {
      return (LunarEvent)this.lastLunarEventHolder().value();
   }

   public Holder<LunarEvent> lastLunarEventHolder() {
      long dayTime = this.level.dayTime();
      LunarEventInstance activeLunarEvent = this.activeLunarEventInstance(dayTime);
      if (activeLunarEvent == null) {
         return this.getLastScheduledLunarEvent();
      } else {
         LunarEventInstance lastEndedLunarEvent = this.lastEndedLunarEventInstance(activeLunarEvent.startTime());
         return lastEndedLunarEvent != null && activeLunarEvent.startTime() - lastEndedLunarEvent.endTime() <= 100L
            ? lastEndedLunarEvent.lunarEvent()
            : this.defaultLunarEvent;
      }
   }

   public float getBlend() {
      long dayTime = this.level.dayTime();
      LunarEventInstance activeLunarEvent = this.activeLunarEventInstance(dayTime);
      if (activeLunarEvent != null) {
         return activeLunarEvent.getFadeInProgress(100L, dayTime);
      } else {
         LunarEventInstance lastEndedLunarEvent = this.lastEndedLunarEventInstance(dayTime);
         return lastEndedLunarEvent != null ? 1.0F - lastEndedLunarEvent.getPostEndFadeProgress(100L, dayTime) : 1.0F;
      }
   }

   public Holder<LunarEvent> currentLunarEventOrDefault() {
      LunarEventInstance activeLunarEvent = this.activeLunarEventInstance(this.level.dayTime());
      return activeLunarEvent == null ? this.defaultLunarEvent : activeLunarEvent.lunarEvent();
   }

   public Holder<LunarEvent> getLastScheduledLunarEvent() {
      LunarEventInstance lastEndedLunarEvent = this.lastEndedLunarEventInstance(this.level.dayTime());
      return lastEndedLunarEvent == null ? this.defaultLunarEvent : lastEndedLunarEvent.lunarEvent();
   }

   @Nullable
   private LunarEventInstance lastEndedLunarEventInstance(long time) {
      LunarEventInstance lastEndedLunarEvent = null;
      ObjectIterator var4 = this.lunarEventsByYear.values().iterator();

      while (var4.hasNext()) {
         List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)var4.next();

         for (LunarEventInstance scheduledLunarEvent : yearEvents) {
            if (scheduledLunarEvent.endTime() < time && (lastEndedLunarEvent == null || scheduledLunarEvent.endTime() > lastEndedLunarEvent.endTime())) {
               lastEndedLunarEvent = scheduledLunarEvent;
            }
         }
      }

      return lastEndedLunarEvent;
   }

   public Holder<LunarEvent> getLastLunarEvent() {
      long dayTime = this.level.dayTime();
      long checkTime = dayTime;
      LunarEventInstance activeLunarEvent = this.activeLunarEventInstance(dayTime);
      if (activeLunarEvent != null) {
         checkTime = activeLunarEvent.startTime() - 1L;
      }

      LunarEventInstance lastActiveLunarEvent = this.activeLunarEventInstance(checkTime);
      return lastActiveLunarEvent == null ? this.defaultLunarEvent : lastActiveLunarEvent.lunarEvent();
   }

   public Holder<LunarEvent> getNextScheduledLunarEvent() {
      long dayTime = this.level.dayTime();
      LunarEventInstance nextScheduledLunarEvent = null;
      ObjectIterator var4 = this.lunarEventsByYear.values().iterator();

      while (var4.hasNext()) {
         List<LunarEventInstance> yearEvents = (List<LunarEventInstance>)var4.next();

         for (LunarEventInstance scheduledLunarEvent : yearEvents) {
            if (scheduledLunarEvent.startTime() > dayTime
               && (nextScheduledLunarEvent == null || scheduledLunarEvent.startTime() < nextScheduledLunarEvent.startTime())) {
               nextScheduledLunarEvent = scheduledLunarEvent;
            }
         }
      }

      return nextScheduledLunarEvent == null ? this.defaultLunarEvent : nextScheduledLunarEvent.lunarEvent();
   }

   public LunarDimensionSettings getDimensionSettings() {
      return (LunarDimensionSettings)this.lunarDimensionSettings.value();
   }

   public List<Component> debugInfo() {
      List<Component> lines = new ArrayList<>();
      lines.add(
         Component.translatable(this.level.isClientSide ? "enhancedcelestials2core.debug.side_client" : "enhancedcelestials2core.debug.side_server")
            .withStyle(ChatFormatting.BOLD)
      );
      lines.add(Component.translatable("enhancedcelestials2core.debug.dimension", new Object[]{this.level.dimension().location().toString()}));
      lines.add(Component.translatable("enhancedcelestials2core.debug.day_time", new Object[]{this.level.getDayTime()}));
      lines.add(Component.translatable("enhancedcelestials2core.debug.moon_phase", new Object[]{this.level.dimensionType().moonPhase(this.level.getDayTime())}));
      lines.add(Component.translatable("enhancedcelestials2core.debug.current_event", new Object[]{debugEventComponent(this.currentLunarEventHolder())}));
      lines.add(Component.translatable("enhancedcelestials2core.debug.event_active", new Object[]{this.isEventActive()}));
      lines.add(Component.translatable("enhancedcelestials2core.debug.changed_this_tick", new Object[]{this.eventChangedThisTick}));
      lines.add(Component.translatable("enhancedcelestials2core.debug.blend", new Object[]{String.format("%.2f", this.getBlend())}));
      lines.add(Component.translatable("enhancedcelestials2core.debug.last_event", new Object[]{debugEventComponent(this.lastLunarEventHolder())}));
      lines.add(Component.translatable("enhancedcelestials2core.debug.next_event", new Object[]{debugEventComponent(this.getNextScheduledLunarEvent())}));
      return lines;
   }

   private static Component debugEventComponent(Holder<LunarEvent> holder) {
      return holder.unwrapKey().isEmpty()
         ? Component.translatable("enhancedcelestials2core.debug.unbound")
         : Component.translatable(LunarEvent.getTranslationKey(holder));
   }

   public void recomputeForecast() {
      if (this.level.isClientSide) {
         throw new IllegalStateException("MUST BE CALLED FROM SERVER SIDE ONLY!");
      } else {
         this.lunarEventsByYear.clear();
         this.lastCheckedDay = -1L;
         this.buildSchedule();
      }
   }

   public Component getForecastComponent() {
      long dayLength = ((LunarDimensionSettings)this.lunarDimensionSettings.value()).dayLength();
      long today = this.level.dayTime() / dayLength;
      List<LunarEventInstance> sortedLunarEvents = new ArrayList<>();
      this.lunarEventsByYear.values().forEach(sortedLunarEvents::addAll);
      sortedLunarEvents.removeIf(
         lunarEventInstancex -> lunarEventInstancex.startTime() / dayLength < today || lunarEventInstancex.startTime() / dayLength > today + 100L
      );
      sortedLunarEvents.sort(Comparator.comparingLong(LunarEventInstance::startTime));
      MutableComponent textComponent = null;

      for (int i = Math.min(100, sortedLunarEvents.size() - 1); i >= 0; i--) {
         LunarEventInstance lunarEventInstance = sortedLunarEvents.get(i);
         Holder<LunarEvent> event = lunarEventInstance.lunarEvent();
         String translationKey = LunarEvent.getTranslationKey(event);
         TextColor color = ((LunarEvent)event.value()).getNameColor().orElse(null);
         if (textComponent == null) {
            textComponent = Component.translatable(translationKey).withStyle(Style.EMPTY.withColor(color));
         } else {
            textComponent.append(Component.literal(", ").withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))
               .append(Component.translatable(translationKey).withStyle(Style.EMPTY.withColor(color)));
         }

         textComponent.append(
            Component.translatable("enhancedcelestials2core.lunarforecast.days_left", new Object[]{lunarEventInstance.startTime() / dayLength - today})
               .withStyle(Style.EMPTY.withColor(color))
         );
      }

      return textComponent != null
         ? Component.translatable(
            "enhancedcelestials2core.lunarforecast.header",
            new Object[]{textComponent.append(Component.literal(".").withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))}
         )
         : Component.translatable("enhancedcelestials2core.lunarforecast.empty").withStyle(ChatFormatting.YELLOW);
   }

   private static SimpleWeightedRandomList<LunarEventScheduler.Candidate<Holder<LunarEvent>>> createLunarEventSpawnRequirements(
      ResourceKey<Level> dimension,
      Registry<LunarEventProbabilities> lunarEventProbabilitiesRegistry,
      Registry<LunarEvent> lunarEvents,
      Holder<LunarEvent> defaultLunarEvent
   ) {
      Object2ObjectOpenHashMap<Holder<LunarEvent>, LunarEvent.SpawnRequirements> lunarEventSpawnRequirements = new Object2ObjectOpenHashMap();
      insertOverrides(lunarEventProbabilitiesRegistry, dimension, lunarEventSpawnRequirements, lunarEvents);
      List<LunarEventScheduler.Candidate<Holder<LunarEvent>>> candidates = new ArrayList<>();
      LunarEvent.SpawnRequirements defaultSpawnRequirements = (LunarEvent.SpawnRequirements)lunarEventSpawnRequirements.getOrDefault(
         defaultLunarEvent, new LunarEvent.SpawnRequirements(100, List.of())
      );
      candidates.add(new LunarEventScheduler.Candidate<>(defaultLunarEvent, defaultSpawnRequirements));
      lunarEventSpawnRequirements.forEach((lunarEventHolder, spawnRequirements) -> {
         if (!lunarEventHolder.equals(defaultLunarEvent)) {
            candidates.add(new LunarEventScheduler.Candidate<>(lunarEventHolder, spawnRequirements));
         }
      });
      return LunarEventScheduler.table(candidates, defaultLunarEvent, holder -> holder.unwrapKey().map(key -> key.location().toString()).orElse(""));
   }

   private static void insertOverrides(
      Registry<LunarEventProbabilities> lunarEventProbabilitiesRegistry,
      ResourceKey<Level> dimension,
      Object2ObjectOpenHashMap<Holder<LunarEvent>, LunarEvent.SpawnRequirements> lunarEventSpawnRequirements,
      Registry<LunarEvent> lunarEvents
   ) {
      Object2ObjectOpenHashMap<ResourceKey<LunarEvent>, List<Pair<ResourceKey<LunarEventProbabilities>, LunarEventProbabilities.DimensionProbability>>> candidatesByEvent = new Object2ObjectOpenHashMap();

      for (java.util.Map.Entry<ResourceKey<LunarEventProbabilities>, LunarEventProbabilities> entry : lunarEventProbabilitiesRegistry.entrySet()) {
         LunarEventProbabilities probabilities = entry.getValue();
         LunarEventProbabilities.DimensionProbability dimensionProbability = probabilities.dimensionProbabilities().get(dimension);
         if (dimensionProbability != null) {
            ((List)candidatesByEvent.computeIfAbsent(probabilities.lunarEvent(), key -> new ArrayList())).add(Pair.of(entry.getKey(), dimensionProbability));
         }
      }

      Object2ObjectRBTreeMap<ResourceKey<LunarEvent>, StringBuilder> loggerData = new Object2ObjectRBTreeMap(Comparator.comparing(ResourceKey::location));
      candidatesByEvent.forEach(
         (lunarEventKey, candidates) -> {
            candidates.sort(Comparator.comparingInt(candidate -> ((LunarEventProbabilities.DimensionProbability)candidate.getSecond()).priority()));
            Pair<ResourceKey<LunarEventProbabilities>, LunarEventProbabilities.DimensionProbability> winner = (Pair<ResourceKey<LunarEventProbabilities>, LunarEventProbabilities.DimensionProbability>)candidates.get(
               candidates.size() - 1
            );
            lunarEventSpawnRequirements.put(
               lunarEvents.getHolderOrThrow(lunarEventKey), ((LunarEventProbabilities.DimensionProbability)winner.getSecond()).spawnRequirements()
            );
            StringBuilder logBuilder = new StringBuilder(
               "[%s]: Lunar Event probability for \"%s\" was set by highest priority lunar event probability \"%s\" with priority %d."
                  .formatted(
                     dimension.location().toString(),
                     lunarEventKey.location().toString(),
                     ((ResourceKey)winner.getFirst()).location().toString(),
                     ((LunarEventProbabilities.DimensionProbability)winner.getSecond()).priority()
                  )
            );
            if (candidates.size() > 1) {
               logBuilder.append(" | Ignored the following lunar event probabilities due to having a lower priority: ");

               for (int i = 0; i < candidates.size() - 1; i++) {
                  if (i > 0) {
                     logBuilder.append(", ");
                  }

                  Pair<ResourceKey<LunarEventProbabilities>, LunarEventProbabilities.DimensionProbability> ignored = (Pair<ResourceKey<LunarEventProbabilities>, LunarEventProbabilities.DimensionProbability>)candidates.get(
                     i
                  );
                  logBuilder.append(
                     "[Probability=%s,Priority=%d]"
                        .formatted(
                           ((ResourceKey)ignored.getFirst()).location().toString(),
                           ((LunarEventProbabilities.DimensionProbability)ignored.getSecond()).priority()
                        )
                  );
               }
            }

            loggerData.put(lunarEventKey, logBuilder);
         }
      );
      loggerData.values().forEach(stringBuilder -> EC2Constants.LOGGER.info(stringBuilder.toString()));
   }
}
