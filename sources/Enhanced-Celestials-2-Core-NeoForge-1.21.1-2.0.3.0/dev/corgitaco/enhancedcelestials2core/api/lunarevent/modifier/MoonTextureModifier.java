package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record MoonTextureModifier(ResourceLocation textureLocation) implements LunarEventModifier {
   public static final MapCodec<MoonTextureModifier> CODEC = ResourceLocation.CODEC
      .fieldOf("texture_location")
      .xmap(MoonTextureModifier::new, MoonTextureModifier::textureLocation);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.MOON_TEXTURE;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.moon_texture", new Object[]{this.textureLocation.toString()});
   }
}
