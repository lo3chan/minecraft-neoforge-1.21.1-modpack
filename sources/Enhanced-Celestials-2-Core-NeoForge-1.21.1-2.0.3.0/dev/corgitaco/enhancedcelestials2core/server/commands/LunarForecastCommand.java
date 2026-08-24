package dev.corgitaco.enhancedcelestials2core.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class LunarForecastCommand {
   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
      return ((LiteralArgumentBuilder)Commands.literal("lunarForecast").executes(cs -> displayLunarForecast((CommandSourceStack)cs.getSource())))
         .then(Commands.literal("recompute").executes(cs -> recompute((CommandSourceStack)cs.getSource())));
   }

   public static int recompute(CommandSourceStack source) {
      ServerLevel world = source.getLevel();
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
      if (lunarForecastWorldData.isEmpty()) {
         source.sendFailure(Component.translatable("enhancedcelestials2core.commands.disabled"));
         return 0;
      } else {
         LunarForecast data = lunarForecastWorldData.orElseThrow();
         data.recomputeForecast();
         source.sendSuccess(() -> Component.translatable("enhancedcelestials2core.lunarforecast.recompute"), true);
         return 1;
      }
   }

   public static int displayLunarForecast(CommandSourceStack source) {
      ServerLevel world = source.getLevel();
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
      if (lunarForecastWorldData.isEmpty()) {
         source.sendFailure(Component.translatable("enhancedcelestials2core.commands.disabled"));
         return 0;
      } else {
         LunarForecast data = lunarForecastWorldData.orElseThrow();
         source.sendSuccess(data::getForecastComponent, true);
         return 1;
      }
   }
}
