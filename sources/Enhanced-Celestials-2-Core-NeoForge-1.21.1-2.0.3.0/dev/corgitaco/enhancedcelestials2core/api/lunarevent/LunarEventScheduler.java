package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.SpawnRuleContext;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.LongToIntFunction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.SimpleWeightedRandomList.Builder;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import org.jetbrains.annotations.Nullable;

public final class LunarEventScheduler {
   private LunarEventScheduler() {
   }

   public static <T> SimpleWeightedRandomList<LunarEventScheduler.Candidate<T>> table(
      List<LunarEventScheduler.Candidate<T>> candidates, T defaultEvent, Function<T, String> idOf
   ) {
      List<LunarEventScheduler.Candidate<T>> ordered = new ArrayList<>(candidates);
      ordered.sort(
         Comparator.<LunarEventScheduler.Candidate<T>, Integer>comparing(candidatex -> candidatex.event().equals(defaultEvent) ? 0 : 1)
            .thenComparing(candidatex -> idOf.apply((T)candidatex.event()))
      );
      Builder<LunarEventScheduler.Candidate<T>> builder = SimpleWeightedRandomList.builder();

      for (LunarEventScheduler.Candidate<T> candidate : ordered) {
         if (candidate.spawnRequirements().weight() > 0) {
            builder.add(candidate, candidate.spawnRequirements().weight());
         }
      }

      return builder.build();
   }

   public static <T> List<LunarEventScheduler.ScheduledDay<T>> rollYear(
      SimpleWeightedRandomList<LunarEventScheduler.Candidate<T>> table,
      T defaultEvent,
      long seed,
      int dimensionHash,
      LunarDimensionSettings settings,
      long year,
      LongToIntFunction moonPhaseAt
   ) {
      long dayLength = settings.dayLength();
      long yearLengthInDays = settings.yearLengthInDays();
      long minDaysBetweenEvents = settings.minDaysBetweenEvents();
      long maxDaysBetweenEvents = settings.maxDaysBetweenEvents();
      long yearStart = year * yearLengthInDays;
      List<LunarEventScheduler.ScheduledDay<T>> days = new ArrayList<>();
      long lastScheduledEventDay = -1L;
      Object2LongOpenHashMap<T> lastScheduledDayByEvent = new Object2LongOpenHashMap();
      lastScheduledDayByEvent.defaultReturnValue(-1L);

      for (long day = yearStart; day < yearStart + yearLengthInDays; day++) {
         int moonPhase = moonPhaseAt.applyAsInt(day * dayLength);
         RandomSource randomSource = RandomSource.create(seed + dimensionHash + day);
         LunarEventScheduler.Candidate<T> rolled = (LunarEventScheduler.Candidate<T>)table.getRandomValue(randomSource).orElse(null);
         if (rolled == null) {
            days.add(new LunarEventScheduler.ScheduledDay<>(day, moonPhase, null));
         } else {
            boolean rolledRealEvent = !rolled.event().equals(defaultEvent);
            long lastDayOfThisEvent = lastScheduledDayByEvent.getLong(rolled.event());
            boolean pastMinNightsBetweenAllEvents = lastScheduledEventDay == -1L || day - lastScheduledEventDay > minDaysBetweenEvents;
            SpawnRuleContext context = new SpawnRuleContext(day, dayLength, moonPhase, lastDayOfThisEvent, lastScheduledEventDay);
            boolean checksPass = rolledRealEvent && pastMinNightsBetweenAllEvents && rolled.spawnRequirements().passes(context);
            boolean forced = !checksPass && lastScheduledEventDay != -1L && day - lastScheduledEventDay >= maxDaysBetweenEvents;
            T scheduled = rolled.event();
            if (forced) {
               LunarEventScheduler.Candidate<T> forcedPick = pickForcedEvent(
                  table, defaultEvent, randomSource, day, dayLength, lastScheduledDayByEvent, moonPhase
               );
               if (forcedPick == null) {
                  days.add(new LunarEventScheduler.ScheduledDay<>(day, moonPhase, null));
                  continue;
               }

               scheduled = forcedPick.event();
            } else if (!checksPass) {
               days.add(new LunarEventScheduler.ScheduledDay<>(day, moonPhase, null));
               continue;
            }

            lastScheduledEventDay = day;
            lastScheduledDayByEvent.put(scheduled, day);
            days.add(new LunarEventScheduler.ScheduledDay<>(day, moonPhase, scheduled));
         }
      }

      return days;
   }

   @Nullable
   private static <T> LunarEventScheduler.Candidate<T> pickForcedEvent(
      SimpleWeightedRandomList<LunarEventScheduler.Candidate<T>> table,
      T defaultEvent,
      RandomSource randomSource,
      long day,
      long dayLength,
      Object2LongOpenHashMap<T> lastScheduledDayByEvent,
      int moonPhase
   ) {
      List<LunarEventScheduler.Candidate<T>> realEvents = new ArrayList<>();
      List<LunarEventScheduler.Candidate<T>> validEvents = new ArrayList<>();

      for (Wrapper<LunarEventScheduler.Candidate<T>> wrapper : table.unwrap()) {
         LunarEventScheduler.Candidate<T> candidate = (LunarEventScheduler.Candidate<T>)wrapper.data();
         if (!candidate.event().equals(defaultEvent)) {
            realEvents.add(candidate);
            long lastDayOfThisEvent = lastScheduledDayByEvent.getLong(candidate.event());
            if (candidate.spawnRequirements().passes(new SpawnRuleContext(day, dayLength, moonPhase, lastDayOfThisEvent, -1L))) {
               validEvents.add(candidate);
            }
         }
      }

      List<LunarEventScheduler.Candidate<T>> candidates = validEvents.isEmpty() ? realEvents : validEvents;
      Builder<LunarEventScheduler.Candidate<T>> candidateListBuilder = SimpleWeightedRandomList.builder();
      candidates.forEach(candidatex -> candidateListBuilder.add(candidatex, candidatex.spawnRequirements().weight()));
      return (LunarEventScheduler.Candidate<T>)candidateListBuilder.build().getRandomValue(randomSource).orElse(null);
   }

   public record Candidate<T>(T event, LunarEvent.SpawnRequirements spawnRequirements) {
   }

   public record ScheduledDay<T>(long day, int moonPhase, @Nullable T event) {
      public boolean hasEvent() {
         return this.event != null;
      }
   }
}
