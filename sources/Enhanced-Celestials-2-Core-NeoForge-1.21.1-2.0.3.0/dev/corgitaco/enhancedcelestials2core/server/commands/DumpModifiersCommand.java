package dev.corgitaco.enhancedcelestials2core.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifier;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class DumpModifiersCommand {
   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
      return ((LiteralArgumentBuilder)Commands.literal("dumpModifiers").executes(cs -> dumpModifiers((CommandSourceStack)cs.getSource(), null)))
         .then(
            Commands.argument("lunarEvent", ResourceKeyArgument.key(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY))
               .executes(cs -> dumpModifiers((CommandSourceStack)cs.getSource(), getLunarEventKey(cs)))
         );
   }

   private static ResourceKey<LunarEvent> getLunarEventKey(CommandContext<CommandSourceStack> context) {
      ResourceKey<?> lunarEventKey = (ResourceKey<?>)context.getArgument("lunarEvent", ResourceKey.class);
      return (ResourceKey<LunarEvent>)lunarEventKey.cast(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).orElseThrow();
   }

   public static int dumpModifiers(CommandSourceStack source, @Nullable ResourceKey<LunarEvent> lunarEventKey) {
      ServerLevel world = source.getLevel();
      Holder<LunarEvent> lunarEvent;
      if (lunarEventKey == null) {
         Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(world);
         if (lunarForecastWorldData.isEmpty()) {
            source.sendFailure(Component.translatable("enhancedcelestials2core.commands.disabled"));
            return 0;
         }

         lunarEvent = lunarForecastWorldData.orElseThrow().currentLunarEventHolder();
      } else {
         Registry<LunarEvent> lunarEvents = (Registry<LunarEvent>)world.registryAccess().registry(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).orElseThrow();
         if (!lunarEvents.containsKey(lunarEventKey) || !lunarEvents.getHolderOrThrow(lunarEventKey).isBound()) {
            source.sendFailure(Component.translatable("enhancedcelestials2core.commands.lunarevent_missing", new Object[]{lunarEventKey.location().toString()}));
            return 0;
         }

         lunarEvent = lunarEvents.getHolderOrThrow(lunarEventKey);
      }

      Component lunarEventName = Component.translatable(LunarEvent.getTranslationKey(lunarEvent));
      List<Holder<LunarEventModifier>> modifiers = ((LunarEvent)lunarEvent.value()).getModifiers();
      if (modifiers.isEmpty()) {
         source.sendSuccess(() -> Component.translatable("enhancedcelestials2core.commands.dumpmodifiers.empty", new Object[]{lunarEventName}), false);
         return 0;
      } else {
         MutableComponent result = Component.translatable("enhancedcelestials2core.commands.dumpmodifiers.header", new Object[]{lunarEventName});

         for (Holder<LunarEventModifier> modifierHolder : modifiers) {
            result.append(Component.literal("\n- ")).append(((LunarEventModifier)modifierHolder.value()).description());
         }

         source.sendSuccess(() -> result, false);
         return modifiers.size();
      }
   }
}
