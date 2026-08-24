package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.util.thread.EffectiveSide;

public class BookPaintElement {
   public float x;
   public float y;
   public float z;
   public float width;
   public float height;
   public float scale;
   public int index;
   public ResourceLocation parentLocation;
   public BookPaintElement.Client client;
   public static final Codec<BookPaintElement> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("parentLocation", HexereiUtil.getResource("missing")).forGetter(p -> p.parentLocation),
            Codec.FLOAT.optionalFieldOf("x", 0.0F).forGetter(p -> p.x),
            Codec.FLOAT.optionalFieldOf("y", 0.0F).forGetter(p -> p.y),
            Codec.FLOAT.optionalFieldOf("z", 0.0F).forGetter(p -> p.z),
            Codec.FLOAT.optionalFieldOf("width", 16.0F).forGetter(p -> p.width),
            Codec.FLOAT.optionalFieldOf("height", 16.0F).forGetter(p -> p.height),
            Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(p -> p.scale),
            Codec.INT.optionalFieldOf("index", 0).forGetter(p -> p.index)
         )
         .apply(instance, BookPaintElement::new)
   );

   BookPaintElement(ResourceLocation parentLocation, float x, float y, float z, float width, float height, float scale, int index) {
      this.parentLocation = parentLocation;
      this.x = x;
      this.y = y;
      this.z = z;
      this.width = width;
      this.height = height;
      this.scale = scale;
      this.index = index;
      this.client = EffectiveSide.get().isClient() ? new BookPaintElement.Client(this) : null;
   }

   public static class Client {
      BookPaintElement parent;
      Map<UUID, PaintSystem> paintSystems;

      public PaintSystem getPaintSystem(UUID uuid) {
         if (!this.paintSystems.containsKey(uuid)) {
            PaintSystem ps = new PaintSystem((int)this.parent.width, (int)this.parent.height, this.parent.parentLocation, uuid);
            this.paintSystems.put(uuid, ps);
            ps.addAndUpdateTexture();
         }

         return this.paintSystems.get(uuid);
      }

      public Client(BookPaintElement parent) {
         this.parent = parent;
         this.paintSystems = new HashMap<>();
      }
   }
}
