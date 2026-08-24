package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.enhancedcelestials2core.util.ColorUtil;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;

public record GlowColorModifier(int color, float glowIntensity) implements LunarEventModifier {
   public static final MapCodec<GlowColorModifier> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
            ColorUtil.FLEXIBLE_COLOR_CODEC.fieldOf("color").forGetter(GlowColorModifier::color),
            Codec.FLOAT.optionalFieldOf("glow_intensity", 1.0F).forGetter(GlowColorModifier::glowIntensity)
         )
         .apply(builder, GlowColorModifier::new)
   );

   public Vector3f getGLColor() {
      return ColorUtil.glColor(ColorUtil.unpack(this.color));
   }

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.GLOW_COLOR;
   }

   @Override
   public Component description() {
      return Component.translatable(
         "enhancedcelestials2core.lunar_event_modifier.glow_color", new Object[]{ColorUtil.toHexString(this.color), this.glowIntensity}
      );
   }
}
