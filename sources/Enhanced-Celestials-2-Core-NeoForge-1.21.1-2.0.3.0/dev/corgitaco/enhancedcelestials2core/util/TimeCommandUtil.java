package dev.corgitaco.enhancedcelestials2core.util;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class TimeCommandUtil {
   private static final long DEFAULT_DAY_LENGTH = 24000L;

   public static LiteralArgumentBuilder<CommandSourceStack> setNode(String name, int time) {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)Commands.literal(name)
            .executes(context -> setTime((CommandSourceStack)context.getSource(), time, false)))
         .then(
            Commands.argument("reset", BoolArgumentType.bool())
               .executes(context -> setTime((CommandSourceStack)context.getSource(), time, BoolArgumentType.getBool(context, "reset")))
         );
   }

   public static RequiredArgumentBuilder<CommandSourceStack, Integer> setTimeArgumentNode() {
      return (RequiredArgumentBuilder<CommandSourceStack, Integer>)((RequiredArgumentBuilder)Commands.argument("time", TimeArgument.time())
            .executes(context -> setTime((CommandSourceStack)context.getSource(), IntegerArgumentType.getInteger(context, "time"), false)))
         .then(
            Commands.argument("reset", BoolArgumentType.bool())
               .executes(
                  context -> setTime(
                     (CommandSourceStack)context.getSource(), IntegerArgumentType.getInteger(context, "time"), BoolArgumentType.getBool(context, "reset")
                  )
               )
         );
   }

   private static int setTime(CommandSourceStack source, int time, boolean reset) {
      ServerLevel sourceLevel = source.getLevel();
      long sourceDayLength = dayLength(sourceLevel);
      long oldDayTime = sourceLevel.getDayTime();
      long oldDay = Math.floorDiv(oldDayTime, sourceDayLength);

      for (ServerLevel serverLevel : source.getServer().getAllLevels()) {
         serverLevel.setDayTime(reset ? time : nextDayTime(serverLevel, time));
      }

      long newDayTime = sourceLevel.getDayTime();
      long newDay = Math.floorDiv(newDayTime, sourceDayLength);
      if (reset) {
         source.sendSuccess(
            () -> Component.translatable("enhancedcelestials2core.command.time.set.reset", new Object[]{time, oldDayTime, newDay, oldDay})
               .withStyle(ChatFormatting.YELLOW),
            true
         );
      } else {
         source.sendSuccess(() -> Component.translatable("enhancedcelestials2core.command.time.set", new Object[]{time, newDay, newDayTime}), true);
      }

      return (int)(newDayTime % sourceDayLength);
   }

   private static long nextDayTime(ServerLevel level, int time) {
      long dayLength = dayLength(level);
      long currentDayTime = level.getDayTime();
      long nextDayStart = currentDayTime - Math.floorMod(currentDayTime, dayLength) + dayLength;
      return nextDayStart + time;
   }

   private static long dayLength(ServerLevel level) {
      return EnhancedCelestials.lunarForecastWorldData(level).map(forecast -> forecast.getDimensionSettings().dayLength()).orElse(24000L);
   }
}
