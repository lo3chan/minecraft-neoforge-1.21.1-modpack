package snownee.jade.api.ui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ColorPalette(int normal, int info, int title, int success, int warning, int danger, int failure) {
   public static final ColorPalette DEFAULT = new ColorPalette(-6250336, -1, -1, -11141291, -16121, -43691, -5636096);
   public static final Codec<ColorPalette> CODEC = RecordCodecBuilder.create(
      i -> i.group(
            Color.CODEC.optionalFieldOf("normal", DEFAULT.normal).forGetter(ColorPalette::normal),
            Color.CODEC.optionalFieldOf("info", DEFAULT.info).forGetter(ColorPalette::info),
            Color.CODEC.optionalFieldOf("title", DEFAULT.title).forGetter(ColorPalette::title),
            Color.CODEC.optionalFieldOf("success", DEFAULT.success).forGetter(ColorPalette::success),
            Color.CODEC.optionalFieldOf("warning", DEFAULT.warning).forGetter(ColorPalette::warning),
            Color.CODEC.optionalFieldOf("danger", DEFAULT.danger).forGetter(ColorPalette::danger),
            Color.CODEC.optionalFieldOf("failure", DEFAULT.failure).forGetter(ColorPalette::failure)
         )
         .apply(i, ColorPalette::new)
   );

   public int get(MessageType type) {
      return switch (type) {
         case INFO -> this.info;
         case TITLE -> this.title;
         case SUCCESS -> this.success;
         case WARNING -> this.warning;
         case DANGER -> this.danger;
         case FAILURE -> this.failure;
         default -> this.normal;
      };
   }
}
