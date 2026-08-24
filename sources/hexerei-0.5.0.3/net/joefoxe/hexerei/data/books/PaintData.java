package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class PaintData {
   private final List<PaintData.LayerData> layers;
   private final int width;
   private final int height;
   public ResourceLocation page;
   public UUID uuid;
   public boolean locked = false;
   public UUID lockedByUUID = new UUID(0L, 0L);
   public Component lockedByName = Component.empty();
   public static final Codec<PaintData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("width").forGetter(PaintData::getWidth),
            Codec.INT.fieldOf("height").forGetter(PaintData::getHeight),
            PaintData.LayerData.CODEC.listOf().fieldOf("layers").forGetter(PaintData::getLayers),
            ResourceLocation.CODEC.fieldOf("page").forGetter(PaintData::getPage),
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(PaintData::getUuid),
            Codec.BOOL.optionalFieldOf("locked", false).forGetter(PaintData::isLocked),
            UUIDUtil.CODEC.optionalFieldOf("lockedByUUID", new UUID(0L, 0L)).forGetter(PaintData::getLockedByUUID),
            ComponentSerialization.CODEC.optionalFieldOf("lockedByName", Component.empty()).forGetter(PaintData::getLockedByName)
         )
         .apply(instance, PaintData::new)
   );
   public static StreamCodec<ByteBuf, PaintData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

   public PaintData(int width, int height, List<PaintData.LayerData> layers, ResourceLocation page, UUID uuid) {
      this.width = width;
      this.height = height;
      this.layers = layers;
      this.page = page;
      this.uuid = uuid;
   }

   public PaintData(
      int width, int height, List<PaintData.LayerData> layers, ResourceLocation page, UUID uuid, boolean locked, UUID lockedByUUID, Component lockedByName
   ) {
      this.width = width;
      this.height = height;
      this.layers = layers;
      this.page = page;
      this.uuid = uuid;
      this.locked = locked;
      this.lockedByUUID = lockedByUUID;
      this.lockedByName = lockedByName;
   }

   public List<PaintData.LayerData> getLayers() {
      return this.layers;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public ResourceLocation getPage() {
      return this.page;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public UUID getLockedByUUID() {
      return this.lockedByUUID;
   }

   public Component getLockedByName() {
      return this.lockedByName;
   }

   public record LayerData(int width, int height, List<Integer> pixels, float opacity, String blendMode, String name) {
      public static final Codec<PaintData.LayerData> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.INT.fieldOf("width").forGetter(PaintData.LayerData::width),
               Codec.INT.fieldOf("height").forGetter(PaintData.LayerData::height),
               Codec.INT.listOf().fieldOf("pixels").forGetter(PaintData.LayerData::pixels),
               Codec.FLOAT.fieldOf("opacity").forGetter(PaintData.LayerData::opacity),
               Codec.STRING.fieldOf("blendMode").forGetter(PaintData.LayerData::blendMode),
               Codec.STRING.fieldOf("name").forGetter(PaintData.LayerData::name)
            )
            .apply(instance, PaintData.LayerData::new)
      );
   }
}
