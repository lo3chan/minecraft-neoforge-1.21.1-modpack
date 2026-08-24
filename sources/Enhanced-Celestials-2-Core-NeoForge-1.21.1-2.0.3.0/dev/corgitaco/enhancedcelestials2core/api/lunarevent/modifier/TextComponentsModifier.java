package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarTextComponents;
import net.minecraft.network.chat.Component;

public record TextComponentsModifier(LunarTextComponents components) implements LunarEventModifier {
   public static final MapCodec<TextComponentsModifier> CODEC = LunarTextComponents.CODEC
      .fieldOf("components")
      .xmap(TextComponentsModifier::new, TextComponentsModifier::components);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.TEXT_COMPONENTS;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.text_components", new Object[]{this.components.description()});
   }
}
