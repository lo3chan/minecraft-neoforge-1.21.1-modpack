package com.iafenvoy.origins.attachment;

import carpet.patches.EntityPlayerMPFake;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableSet.Builder;
import com.iafenvoy.origins.Proxies;
import com.iafenvoy.origins.data.ItemPowersComponent;
import com.iafenvoy.origins.data.Sided;
import com.iafenvoy.origins.data.global_powers.GlobalPowersRegistries;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.MultiplePower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.ComponentHolderProvider;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.event.GrantOriginEvent;
import com.iafenvoy.origins.event.GrantPowerEvent;
import com.iafenvoy.origins.event.RevokeOriginEvent;
import com.iafenvoy.origins.event.RevokePowerEvent;
import com.iafenvoy.origins.network.payload.OpenChooseOriginScreenS2CPayload;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.registry.OriginsKeyMappings;
import com.iafenvoy.origins.util.HolderHelper;
import com.iafenvoy.origins.util.RandomHelper;
import com.iafenvoy.origins.util.annotation.Comment;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@EventBusSubscriber
public final class OriginDataHolder {
   public static final ResourceLocation DEFAULT_SOURCE = ResourceLocation.fromNamespaceAndPath("origins", "command");
   private final Entity entity;
   private final EntityOriginAttachment data;
   private final RegistryAccess access;
   private final PowerHelper helper;

   public OriginDataHolder(Entity entity, EntityOriginAttachment data) {
      this.entity = entity;
      this.data = data;
      this.access = entity.registryAccess();
      this.helper = new PowerHelperImpl(this);
   }

   public Entity getEntity() {
      return this.entity;
   }

   public EntityOriginAttachment getData() {
      return this.data;
   }

   public RegistryAccess getAccess() {
      return this.access;
   }

   public PowerHelper getHelper() {
      return this.helper;
   }

   public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
      return Map.copyOf(this.data.getOrigins());
   }

   public Holder<Origin> getOrigin(Holder<Layer> layer) {
      return this.data.getOrigins().get(layer);
   }

   public void setOrigin(@NotNull Holder<Layer> layer, @NotNull Holder<Origin> origin) {
      this.clearOrigin(layer);
      if (origin.value() != Origin.EMPTY) {
         this.data.getOrigins().put(layer, origin);
         ResourceLocation id = HolderHelper.id(origin);
         RegistryCodecs.listAll(((Origin)origin.value()).powers(), this.access, PowerRegistries.POWER_KEY).forEach(x -> this.grantPower(id, (Holder<Power>)x));
         NeoForge.EVENT_BUS.post(new GrantOriginEvent(this.entity, layer, origin));
      }
   }

   public void clearOrigin(@NotNull Holder<Layer> layer) {
      Holder<Origin> origin = this.data.getOrigins().remove(layer);
      if (origin != null) {
         this.revokeAllPowers(HolderHelper.id(origin));
         NeoForge.EVENT_BUS.post(new RevokeOriginEvent(this.entity, layer, origin));
      }
   }

   public boolean hasOrigin(Holder<Layer> layer, Holder<Origin> origin) {
      return this.data.getOrigins().containsKey(layer) && ((Origin)this.data.getOrigins().get(layer).value()).equals(origin.value());
   }

   public boolean hasOrigin(Holder<Origin> origin) {
      return this.data.getOrigins().containsValue(origin);
   }

   public boolean hasOriginInLayer(Holder<Layer> layer) {
      return this.data.getOrigins().containsKey(layer) && this.data.getOrigins().get(layer).value() != Origin.EMPTY;
   }

   public boolean fillAutoChoosing() {
      boolean changed = false;

      for (Holder<Layer> layer : LayerRegistries.streamAutoChooseLayers(this.entity.registryAccess()).toList()) {
         if (!this.data.getOrigins().containsKey(layer)) {
            changed |= this.randomOrigin(layer);
         }
      }

      if (changed) {
         this.sync();
      }

      return changed;
   }

   public boolean randomOrigin(Holder<Layer> layer) {
      List<Holder<Origin>> available = ((Layer)layer.value()).collectRandomizableOrigins(this.entity).toList();
      if (!available.isEmpty()) {
         Holder<Origin> origin = RandomHelper.randomOne(available);
         this.clearOrigin(layer);
         if (origin.value() != Origin.EMPTY) {
            if (this.entity.level().isClientSide) {
               this.entity
                  .sendSystemMessage(
                     Component.translatable(
                        "commands.origin.set.success.single", new Object[]{this.entity.getDisplayName(), Layer.getName(layer), Origin.getName(origin)}
                     )
                  );
            }

            this.setOrigin(layer, origin);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean hasAllOrigins() {
      for (Holder<Layer> layer : LayerRegistries.streamAvailableLayers(this.access).toList()) {
         if (!this.data.getOrigins().containsKey(layer)) {
            return false;
         }
      }

      return true;
   }

   public void grantPower(ResourceLocation source, Holder<Power> power) {
      this.data.getPowers().put(source, power);
      this.grantPower(new PowerHolder(power));
      NeoForge.EVENT_BUS.post(new GrantPowerEvent(this.entity, power, source));
      this.sync();
   }

   private void grantPower(PowerHolder power) {
      ComponentCollector collector = ComponentCollector.create();
      power.power().createComponents(collector);
      this.data.getComponents().put(power.id(), collector.build());
      power.power().grant(this);
      if (power.power() instanceof MultiplePower multiple) {
         multiple.getPowers(power.id()).forEach(this::grantPower);
      }

      if (this.entity.level().isClientSide()) {
         OriginsKeyMappings.INSTANCE.registerKeyMappingsFromPowers(this.getAllPowers());
      }
   }

   public void revokePower(ResourceLocation source, Holder<Power> power) {
      this.data.getPowers().remove(source, power);
      this.revokePower(new PowerHolder(power));
      NeoForge.EVENT_BUS.post(new RevokePowerEvent(this.entity, power, source));
      this.sync();
   }

   private void revokePower(PowerHolder power) {
      power.power().revoke(this);
      this.data.getComponents().remove(power.id());
      if (power.power() instanceof MultiplePower multiple) {
         multiple.getPowers(power.id()).forEach(this::revokePower);
      }
   }

   public void revokeAllPowers(ResourceLocation source) {
      this.data
         .getPowers()
         .entries()
         .stream()
         .filter(x -> ((ResourceLocation)x.getKey()).equals(source))
         .map(Entry::getValue)
         .toList()
         .forEach(p -> this.revokePower(source, p));
   }

   public void revokeAllPowers(Holder<Power> power) {
      this.data.getPowers().entries().stream().filter(x -> ((Holder)x.getValue()).equals(power)).map(Entry::getKey).forEach(s -> this.revokePower(s, power));
   }

   public Multimap<ResourceLocation, Holder<Power>> getEntityPowers() {
      return this.data.getPowers();
   }

   public Set<PowerHolder> getAllPowers() {
      Builder<PowerHolder> builder = ImmutableSet.builder();

      for (Holder<Power> power : this.data.getPowers().values()) {
         if (power.value() instanceof MultiplePower multiple) {
            multiple.getPowers(HolderHelper.id(power)).forEach(builder::add);
         } else {
            builder.add(new PowerHolder(power));
         }
      }

      GlobalPowersRegistries.streamPowersForType(this.access, this.entity.getType()).map(PowerHolder::new).forEach(builder::add);
      if (this.entity instanceof LivingEntity living) {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            ((ItemPowersComponent)living.getItemBySlot(slot).getOrDefault(OriginsDataComponents.ITEM_POWERS, ItemPowersComponent.EMPTY))
               .powers()
               .values()
               .stream()
               .map(ItemPowersComponent.Entry::power)
               .map(PowerHolder::new)
               .forEach(builder::add);
         }
      }

      return builder.build();
   }

   private <T> Stream<T> streamPowers(Class<T> clazz, Predicate<ResourceLocation> idChecker) {
      Stream<Power> powers = this.getAllPowers()
         .stream()
         .filter(x -> idChecker.test(x.id()))
         .map(PowerHolder::power)
         .filter(power -> clazz.isAssignableFrom(power.getClass()));
      if (this.entity.level().isClientSide()) {
         powers = powers.filter(power -> power.getSettings().condition() instanceof Sided condition ? !condition.server() : true);
      }

      Stream<T> results = powers.map(clazz::cast);
      return Prioritized.class.isAssignableFrom(clazz)
         ? results.map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::getPriority)).map(clazz::cast)
         : results;
   }

   public <T> Stream<T> streamPowers(Class<T> clazz) {
      return this.streamPowers(clazz, id -> true);
   }

   public <T extends Power> Stream<T> streamPowers(ResourceLocation id, Class<T> clazz) {
      return this.streamPowers(clazz, i -> Objects.equals(i, id));
   }

   public <T extends Power> Stream<T> streamActivePowers(Class<T> clazz) {
      return this.streamPowers(clazz).filter(x -> x.isActive(this));
   }

   public boolean hasPower(Holder<Power> power) {
      return this.getAllPowers().stream().anyMatch(p -> Objects.equals(p, new PowerHolder(power)));
   }

   public boolean hasPower(ResourceLocation id, Class<Power> clazz) {
      return this.streamPowers(id, clazz).findAny().isPresent();
   }

   @Comment("Use helper first")
   @Internal
   public <T extends Power> boolean hasActivePower(ResourceLocation id, Class<T> clazz) {
      return this.streamPowers(id, clazz).filter(x -> x.isActive(this)).anyMatch(p -> clazz.isAssignableFrom(p.getClass()));
   }

   public <T> Optional<T> getComponent(ResourceLocation id, Class<T> clazz) {
      return Optional.ofNullable(this.data.getComponents().get(id)).map(x -> x.get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast);
   }

   public <T> Optional<T> getComponentFor(Power power, Class<T> clazz) {
      return this.getComponent(power.getId(this.access), clazz);
   }

   public <H, T extends ComponentHolderProvider<H>> Optional<H> getComponentHolder(ResourceLocation id, Class<T> clazz) {
      return Optional.ofNullable(this.data.getComponents().get(id))
         .map(x -> x.get(clazz))
         .filter(x -> clazz.isAssignableFrom(x.getClass()))
         .map(clazz::cast)
         .map(x -> x.constructHolder(this, id));
   }

   public static Optional<OriginDataHolder> optional(@Nullable Entity entity) {
      try {
         return Optional.ofNullable(entity).map(x -> new OriginDataHolder(entity, (EntityOriginAttachment)x.getData(OriginsAttachments.ENTITY_ORIGIN)));
      } catch (Exception var2) {
         return Optional.empty();
      }
   }

   public static Stream<OriginDataHolder> optionalStream(@Nullable Entity entity) {
      return optional(entity).stream();
   }

   public static OriginDataHolder get(@Nullable Entity entity) {
      return optional(entity).orElse(null);
   }

   public void sync() {
      this.entity.syncData(OriginsAttachments.ENTITY_ORIGIN);
   }

   public void tick() {
      long currentTick = Proxies.TICK_COUNT.getAsLong();
      Set<PowerHolder> powers = this.getAllPowers();
      powers.stream().map(PowerHolder::power).filter(p -> p.tickInterval() <= 0 || currentTick % p.tickInterval() == 0L).forEach(p -> p.tick(this));
      boolean changed = false;

      for (PowerHolder power : powers) {
         for (Entry<Class<? extends PowerComponent>, PowerComponent> entry : this.data
            .getComponents()
            .getOrDefault(power.id(), new LinkedHashMap<>())
            .entrySet()) {
            PowerComponent component = entry.getValue();
            component.tick(this, power);
            if (component.isDirty()) {
               changed = true;
            }
         }
      }

      if (changed) {
         this.sync();
      }
   }

   @SubscribeEvent
   @Internal
   public static void onEntityTick(Post event) {
      optional(event.getEntity()).ifPresent(OriginDataHolder::tick);
   }

   @SubscribeEvent
   @Internal
   public static void onRespawn(PlayerRespawnEvent event) {
      PowerHelper.get(event.getEntity()).execute(Power.class, (h, p) -> p.respawn(h, event.isEndConquered()));
   }

   @SubscribeEvent
   @Internal
   public static void onSyncDatapack(OnDatapackSyncEvent event) {
      if (event.getPlayer() != null) {
         forEachPlayer(event.getPlayer());
      } else {
         for (ServerPlayer player : event.getPlayerList().getPlayers()) {
            forEachPlayer(player);
         }
      }
   }

   private static void forEachPlayer(@NotNull ServerPlayer player) {
      OriginDataHolder holder = get(player);
      holder.sync();
      if (!holder.hasAllOrigins()) {
         holder.fillAutoChoosing();
         if (!holder.hasAllOrigins() && !isFakePlayer(player)) {
            holder.data.setSelecting(true);
            holder.sync();
            PacketDistributor.sendToPlayer(player, new OpenChooseOriginScreenS2CPayload(true), new CustomPacketPayload[0]);
         } else {
            holder.sync();
         }
      }
   }

   private static boolean isFakePlayer(ServerPlayer player) {
      return ModList.get().isLoaded("bedsheet") && player instanceof EntityPlayerMPFake;
   }

   @SubscribeEvent
   @Internal
   public static void onGrantAdvancement(AdvancementEarnEvent event) {
      Player player = event.getEntity();
      AdvancementHolder advancement = event.getAdvancement();
      OriginDataHolder holder = get(player);
      Map<Holder<Layer>, Origin.Upgrade> upgrades = new LinkedHashMap<>();

      for (Entry<Holder<Layer>, Holder<Origin>> origin : holder.getOrigins().entrySet()) {
         for (Origin.Upgrade x : ((Origin)origin.getValue().value()).upgrades()) {
            if (Objects.equals(advancement.id(), x.condition())) {
               upgrades.put(origin.getKey(), x);
               break;
            }
         }
      }

      for (Entry<Holder<Layer>, Origin.Upgrade> entry : upgrades.entrySet()) {
         Origin.Upgrade upgrade = entry.getValue();
         holder.setOrigin(entry.getKey(), upgrade.origin());
         upgrade.announcement().ifPresent(player::sendSystemMessage);
      }
   }
}
