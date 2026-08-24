package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;

public class BookImageEffect {
   public BookImage hoverImage;
   public String type;
   public float speed;
   public float amount;
   private static final Codec<BookImageEffect> LAYERED_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "none").forGetter(e -> e.type),
            Codec.FLOAT.optionalFieldOf("speed", 0.0F).forGetter(e -> e.speed),
            Codec.FLOAT.optionalFieldOf("amount", 0.0F).forGetter(e -> e.amount)
         )
         .apply(instance, BookImageEffect::new)
   );
   private static final Codec<BookImage> IMAGE_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("x", 0.0F).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0.0F).forGetter(e -> e.y),
            Codec.FLOAT.optionalFieldOf("z", 0.0F).forGetter(e -> e.z),
            Codec.FLOAT.optionalFieldOf("u", 0.0F).forGetter(e -> e.u),
            Codec.FLOAT.optionalFieldOf("v", 0.0F).forGetter(e -> e.v),
            Codec.FLOAT.optionalFieldOf("width", 16.0F).forGetter(e -> e.width),
            Codec.FLOAT.optionalFieldOf("height", 16.0F).forGetter(e -> e.height),
            Codec.FLOAT.optionalFieldOf("imageWidth", 16.0F).forGetter(e -> e.imageWidth),
            Codec.FLOAT.optionalFieldOf("imageHeight", 16.0F).forGetter(e -> e.imageHeight),
            Codec.FLOAT.optionalFieldOf("scale", 0.0F).forGetter(e -> e.scale),
            Codec.STRING.optionalFieldOf("texture", "").forGetter(e -> e.texture),
            BookHyperlink.CODEC.optionalFieldOf("hyperlink", new BookHyperlink(-1, -1)).forGetter(e -> e.hyperlink),
            BookTooltipExtra.CODEC.listOf().optionalFieldOf("tooltip", new ArrayList()).forGetter(e -> e.extra_tooltips_raw),
            LAYERED_CODEC.listOf().optionalFieldOf("effects", new ArrayList()).forGetter(e -> e.effects)
         )
         .apply(instance, BookImage::new)
   );
   public static final Codec<BookImageEffect> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "none").forGetter(e -> e.type),
            Codec.FLOAT.optionalFieldOf("speed", 0.0F).forGetter(e -> e.speed),
            Codec.FLOAT.optionalFieldOf("amount", 0.0F).forGetter(e -> e.amount),
            IMAGE_CODEC.optionalFieldOf("image", null).forGetter(e -> e.hoverImage)
         )
         .apply(instance, BookImageEffect::new)
   );

   public BookImageEffect(String type, float speed, float amount, BookImage hoverImage) {
      this.type = type;
      this.speed = speed;
      this.amount = amount;
      this.hoverImage = hoverImage;
   }

   public BookImageEffect(String type, float speed, float amount) {
      this.type = type;
      this.speed = speed;
      this.amount = amount;
      this.hoverImage = null;
   }
}
