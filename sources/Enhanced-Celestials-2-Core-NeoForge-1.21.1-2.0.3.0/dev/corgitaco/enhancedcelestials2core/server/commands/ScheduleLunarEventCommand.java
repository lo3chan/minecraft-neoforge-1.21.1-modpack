package dev.corgitaco.enhancedcelestials2core.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;

public class ScheduleLunarEventCommand {
   private static final DynamicCommandExceptionType ERROR_LUNAR_EVENT_INVALID = new DynamicCommandExceptionType(
      obj -> Component.translatable("enhancedcelestials2core.commands.lunarevent_missing", new Object[]{String.valueOf(obj)})
   );
   private static final SuggestionProvider<CommandSourceStack> UPCOMING_DAYS = (context, builder) -> {
      EnhancedCelestials.lunarForecastWorldData(((CommandSourceStack)context.getSource()).getLevel()).ifPresent(lunarForecast -> {
         Long2ObjectRBTreeMap<Holder<LunarEvent>> upcomingEvents = lunarForecast.upcomingEventsByDaysInAdvance();
         long maxDaysInAdvance = lunarForecast.maxScheduledDaysInAdvance();

         for (long daysInAdvance = 0L; daysInAdvance <= maxDaysInAdvance && daysInAdvance < 10L; daysInAdvance++) {
            Holder<LunarEvent> scheduledLunarEvent = (Holder<LunarEvent>)upcomingEvents.get(daysInAdvance);
            if (scheduledLunarEvent == null) {
               builder.suggest(Long.toString(daysInAdvance));
            } else {
               builder.suggest(Long.toString(daysInAdvance), Component.translatable(LunarEvent.getTranslationKey(scheduledLunarEvent)));
            }
         }
      });
      return builder.buildFuture();
   };
   private static final SuggestionProvider<CommandSourceStack> SCHEDULED_DAYS = (context, builder) -> {
      EnhancedCelestials.lunarForecastWorldData(((CommandSourceStack)context.getSource()).getLevel())
         .ifPresent(
            lunarForecast -> lunarForecast.upcomingEventsByDaysInAdvance()
               .forEach(
                  (daysInAdvance, lunarEvent) -> builder.suggest(Long.toString(daysInAdvance), Component.translatable(LunarEvent.getTranslationKey(lunarEvent)))
               )
         );
      return builder.buildFuture();
   };

   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
      return ((LiteralArgumentBuilder)Commands.literal("scheduleLunarEvent")
            .then(
               Commands.literal("insert")
                  .then(
                     Commands.argument("lunarEvent", ResourceKeyArgument.key(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY))
                        .then(
                           Commands.argument("daysInAdvance", LongArgumentType.longArg(0L))
                              .suggests(UPCOMING_DAYS)
                              .executes(
                                 cs -> insertLunarEvent((CommandSourceStack)cs.getSource(), getLunarEventKey(cs), LongArgumentType.getLong(cs, "daysInAdvance"))
                              )
                        )
                  )
            ))
         .then(
            ((LiteralArgumentBuilder)Commands.literal("remove")
                  .then(
                     Commands.literal("day")
                        .then(
                           Commands.argument("daysInAdvance", LongArgumentType.longArg(0L))
                              .suggests(SCHEDULED_DAYS)
                              .executes(cs -> removeLunarEvent((CommandSourceStack)cs.getSource(), LongArgumentType.getLong(cs, "daysInAdvance")))
                        )
                  ))
               .then(
                  Commands.literal("event")
                     .then(
                        Commands.argument("lunarEvent", ResourceOrTagKeyArgument.resourceOrTagKey(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY))
                           .executes(
                              cs -> removeLunarEvents(
                                 (CommandSourceStack)cs.getSource(),
                                 ResourceOrTagKeyArgument.getResourceOrTagKey(
                                    cs, "lunarEvent", EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, ERROR_LUNAR_EVENT_INVALID
                                 )
                              )
                           )
                     )
               )
         );
   }

   private static ResourceKey<LunarEvent> getLunarEventKey(CommandContext<CommandSourceStack> context) {
      ResourceKey<?> lunarEventKey = (ResourceKey<?>)context.getArgument("lunarEvent", ResourceKey.class);
      return (ResourceKey<LunarEvent>)lunarEventKey.cast(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).orElseThrow();
   }

   public static int insertLunarEvent(CommandSourceStack source, ResourceKey<LunarEvent> lunarEventKey, long daysInAdvance) {
      ServerLevel world = source.getLevel();
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
      if (lunarForecastWorldData.isEmpty()) {
         source.sendFailure(Component.translatable("enhancedcelestials2core.commands.disabled"));
         return 0;
      } else {
         LunarForecast data = lunarForecastWorldData.orElseThrow();
         Registry<LunarEvent> lunarEvents = (Registry<LunarEvent>)world.registryAccess().registry(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).orElseThrow();
         if (lunarEvents.containsKey(lunarEventKey) && lunarEvents.getHolderOrThrow(lunarEventKey).isBound()) {
            long maxDaysInAdvance = data.maxScheduledDaysInAdvance();
            if (daysInAdvance > maxDaysInAdvance) {
               source.sendFailure(Component.translatable("enhancedcelestials2core.commands.scheduleevent.too_far_in_advance", new Object[]{maxDaysInAdvance}));
               return 0;
            } else if (!data.insertLunarEvent(lunarEvents.getHolderOrThrow(lunarEventKey), daysInAdvance)) {
               source.sendFailure(Component.translatable("enhancedcelestials2core.commands.scheduleevent.insert_failed"));
               return 0;
            } else {
               source.sendSuccess(
                  () -> Component.translatable(
                     "enhancedcelestials2core.commands.scheduleevent.inserted", new Object[]{lunarEventKey.location().toString(), daysInAdvance}
                  ),
                  true
               );
               return 1;
            }
         } else {
            source.sendFailure(Component.translatable("enhancedcelestials2core.commands.lunarevent_missing", new Object[]{lunarEventKey.location().toString()}));
            return 0;
         }
      }
   }

   public static int removeLunarEvent(CommandSourceStack source, long daysInAdvance) {
      ServerLevel world = source.getLevel();
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
      if (lunarForecastWorldData.isEmpty()) {
         source.sendFailure(Component.translatable("enhancedcelestials2core.commands.disabled"));
         return 0;
      } else if (!lunarForecastWorldData.orElseThrow().removeLunarEvent(daysInAdvance)) {
         source.sendFailure(Component.translatable("enhancedcelestials2core.commands.scheduleevent.remove_day_not_found", new Object[]{daysInAdvance}));
         return 0;
      } else {
         source.sendSuccess(() -> Component.translatable("enhancedcelestials2core.commands.scheduleevent.removed_day", new Object[]{daysInAdvance}), true);
         return 1;
      }
   }

   public static int removeLunarEvents(CommandSourceStack source, Result<LunarEvent> lunarEventResult) {
      ServerLevel world = source.getLevel();
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
      if (lunarForecastWorldData.isEmpty()) {
         source.sendFailure(Component.translatable("enhancedcelestials2core.commands.disabled"));
         return 0;
      } else {
         String printableLunarEvent = (String)lunarEventResult.unwrap()
            .map(lunarEventKey -> lunarEventKey.location().toString(), lunarEventTag -> "#" + lunarEventTag.location());
         int removed = lunarForecastWorldData.orElseThrow()
            .removeLunarEvents(lunarEventHolder -> (Boolean)lunarEventResult.unwrap().map(lunarEventHolder::is, lunarEventHolder::is));
         if (removed == 0) {
            source.sendFailure(
               Component.translatable("enhancedcelestials2core.commands.scheduleevent.remove_event_not_found", new Object[]{printableLunarEvent})
            );
            return 0;
         } else {
            source.sendSuccess(
               () -> Component.translatable("enhancedcelestials2core.commands.scheduleevent.removed_events", new Object[]{removed, printableLunarEvent}), true
            );
            return removed;
         }
      }
   }
}
