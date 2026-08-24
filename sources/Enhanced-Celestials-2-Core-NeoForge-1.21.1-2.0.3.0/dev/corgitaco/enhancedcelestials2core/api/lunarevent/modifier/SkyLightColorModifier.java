package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import dev.corgitaco.enhancedcelestials2core.util.ColorUtil;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;

public record SkyLightColorModifier(int color) implements LunarEventModifier {
   public static final MapCodec<SkyLightColorModifier> CODEC = ColorUtil.COLOR_CODEC.xmap(SkyLightColorModifier::new, SkyLightColorModifier::color);

   public SkyLightColorModifier(String hexColor) {
      this(ColorUtil.tryParseColor(hexColor));
   }

   public Vector3f getGLColor() {
      return ColorUtil.glColor(ColorUtil.unpack(this.color));
   }

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.SKY_LIGHT_COLOR;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.sky_light_color", new Object[]{ColorUtil.toHexString(this.color)});
   }
}
