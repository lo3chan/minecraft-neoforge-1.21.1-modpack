package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BookHoverOffset {
   public float x;
   public float y;
   public float scale;
   public static final Codec<BookHoverOffset> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("x", 0.0F).forGetter(e -> e.x),
            Codec.FLOAT.optionalFieldOf("y", 0.0F).forGetter(e -> e.y),
            Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(e -> e.scale)
         )
         .apply(instance, BookHoverOffset::new)
   );

   BookHoverOffset(float x, float y, float scale) {
      this.x = x;
      this.y = y;
      this.scale = scale;
   }
}
