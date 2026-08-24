package dev.corgitaco.enhancedcelestials2core.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;

public class SetLunarEventCommand {
   private static final DynamicCommandExceptionType ERROR_LUNAR_EVENT_INVALID = new DynamicCommandExceptionType(
      obj -> Component.translatable("enhancedcelestials2core.commands.lunarevent_missing", new Object[]{String.valueOf(obj)})
   );

   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
      return Commands.literal("setLunarEvent")
         .then(
            Commands.argument("lunarEvent", ResourceOrTagKeyArgument.resourceOrTagKey(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY))
               .executes(
                  cs -> setLunarEvent(
                     (CommandSourceStack)cs.getSource(),
                     ResourceOrTagKeyArgument.getResourceOrTagKey(cs, "lunarEvent", EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, ERROR_LUNAR_EVENT_INVALID)
                  )
               )
         );
   }

   public static int setLunarEvent(CommandSourceStack source, Result<LunarEvent> lunarEventResult) {
      ServerLevel world = source.getLevel();
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
      if (lunarForecastWorldData.isEmpty()) {
         source.sendFailure(Component.translatable("enhancedcelestials2core.commands.disabled"));
         return 0;
      } else {
         LunarForecast data = lunarForecastWorldData.orElseThrow();
         if (world.isRaining() && data.getDimensionSettings().requiresClearSkies()) {
            source.sendFailure(Component.translatable("enhancedcelestials2core.commands.setlunarevent.requires_clear_skies"));
            return 0;
         } else {
            Either<ResourceKey<LunarEvent>, TagKey<LunarEvent>> unwrap = lunarEventResult.unwrap();
            if (unwrap.left().isPresent()) {
               ResourceKey<LunarEvent> lunarEventResourceKey = (ResourceKey<LunarEvent>)unwrap.left().orElseThrow();
               Registry<LunarEvent> lunarEvents = (Registry<LunarEvent>)world.registryAccess()
                  .registry(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY)
                  .orElseThrow();
               if (lunarEvents.containsKey(lunarEventResourceKey) && lunarEvents.getHolderOrThrow(lunarEventResourceKey).isBound()) {
                  startLunarEvent(world, data, lunarEvents.getHolderOrThrow(lunarEventResourceKey));
                  return 1;
               } else {
                  source.sendFailure(
                     Component.translatable("enhancedcelestials2core.commands.lunarevent_missing", new Object[]{lunarEventResourceKey.location().toString()})
                  );
                  return 0;
               }
            } else if (unwrap.right().isPresent()) {
               Optional<Named<LunarEvent>> possibleTag = ((Registry)world.registryAccess().registry(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).orElseThrow())
                  .getTag((TagKey)unwrap.right().orElseThrow());
               if (possibleTag.isPresent()) {
                  Named<LunarEvent> possibleLunarEvents = possibleTag.orElseThrow();
                  Optional<Holder<LunarEvent>> randomLunarEvent = possibleLunarEvents.getRandomElement(world.random);
                  if (randomLunarEvent.isPresent()) {
                     source.getServer().submit(() -> startLunarEvent(world, data, randomLunarEvent.orElseThrow()));
                     return 1;
                  } else {
                     source.sendFailure(
                        Component.translatable(
                           "enhancedcelestials2core.commands.setlunarevent.empty_tag", new Object[]{possibleLunarEvents.key().location().toString()}
                        )
                     );
                     return 0;
                  }
               } else {
                  source.sendFailure(Component.translatable("enhancedcelestials2core.commands.setlunarevent.unknown_tag"));
                  return 0;
               }
            } else {
               source.sendFailure(Component.translatable("enhancedcelestials2core.commands.setlunarevent.failed"));
               return 0;
            }
         }
      }
   }

   private static void startLunarEvent(ServerLevel world, LunarForecast data, Holder<LunarEvent> lunarEvent) {
      if (!world.isNight()) {
         long dayLength = data.getDimensionSettings().dayLength();
         world.setDayTime(world.getDayTime() / dayLength * dayLength + data.getDimensionSettings().nightStartTime());
      }

      data.setLunarEventTonight(lunarEvent);
   }
}
