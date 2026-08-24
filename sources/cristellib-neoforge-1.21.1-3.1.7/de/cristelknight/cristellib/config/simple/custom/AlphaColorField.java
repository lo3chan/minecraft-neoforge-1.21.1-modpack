package de.cristelknight.cristellib.config.simple.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.FastColor.ARGB32;

public record AlphaColorField(int foreground, int red, int green, int blue) {
   public static final Codec<AlphaColorField> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.INT.fieldOf("foreground").forGetter(AlphaColorField::foreground),
            Codec.INT.fieldOf("red").forGetter(AlphaColorField::red),
            Codec.INT.fieldOf("green").forGetter(AlphaColorField::green),
            Codec.INT.fieldOf("blue").forGetter(AlphaColorField::blue)
         )
         .apply(builder, AlphaColorField::new)
   );

   public int toInt() {
      return ARGB32.color(this.foreground, this.red, this.green, this.blue);
   }

   public static AlphaColorField fromInt(int color) {
      return new AlphaColorField(ARGB32.alpha(color), ARGB32.red(color), ARGB32.green(color), ARGB32.blue(color));
   }
}
