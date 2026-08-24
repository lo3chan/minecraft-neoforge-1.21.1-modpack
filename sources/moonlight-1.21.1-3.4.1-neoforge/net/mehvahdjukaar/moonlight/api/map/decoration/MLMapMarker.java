package net.mehvahdjukaar.moonlight.api.map.decoration;

import com.mojang.datafixers.Products.P7;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Objects;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry;
import net.mehvahdjukaar.moonlight.api.util.codec.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MLMapMarker<D extends MLMapDecoration> {
   private final Holder<MLMapDecorationType<?, ?>> type;
   @NotNull
   protected final BlockPos pos;
   protected final float rot;
   protected final Optional<Component> name;
   protected final boolean preventsExtending;
   protected final boolean shouldRefresh;
   protected final boolean shouldSave;
   public static final Codec<MLMapMarker<?>> CODEC = MLMapDecorationType.REFERENCE_CODEC
      .dispatch("type", MLMapMarker::getType, mapWorldMarker -> ((MLMapDecorationType)mapWorldMarker.value()).getMarkerCodec());
   @Deprecated(
      forRemoval = true
   )
   public static final Codec<MLMapMarker<?>> REFERENCE_CODEC = CODEC;
   public static final int HAS_SMALL_TEXTURE_FLAG = 1;

   public static void assertCanSerialize(Provider lookupProvider) {
      CodecUtils.assertHasRegistry(lookupProvider, MapDataRegistry.MAP_DECORATION_REGISTRY_KEY);
   }

   public static <T extends MLMapMarker<?>> P7<Mu<T>, Holder<MLMapDecorationType<?, ?>>, BlockPos, Float, Optional<Component>, Optional<Boolean>, Optional<Boolean>, Boolean> baseCodecGroup(
      Instance<T> instance
   ) {
      return instance.group(
         MLMapDecorationType.REFERENCE_CODEC.fieldOf("type").forGetter(m -> m.getType()),
         BlockPos.CODEC.fieldOf("pos").forGetter(m -> m.getPos()),
         Codec.FLOAT.optionalFieldOf("rot", 0.0F).forGetter(m -> m.getRotation()),
         ComponentSerialization.FLAT_CODEC.optionalFieldOf("name").forGetter(m -> m.getDisplayName()),
         Codec.BOOL.optionalFieldOf("should_refresh").forGetter(m -> Optional.of(m.shouldRefreshFromWorld())),
         Codec.BOOL.optionalFieldOf("should_save").forGetter(m -> Optional.of(m.shouldSave())),
         Codec.BOOL.optionalFieldOf("prevents_extending", false).forGetter(m -> m.preventsExtending())
      );
   }

   public MLMapMarker(
      Holder<MLMapDecorationType<?, ?>> type,
      BlockPos pos,
      float rotation,
      Optional<Component> component,
      Optional<Boolean> shouldRefresh,
      Optional<Boolean> shouldSave,
      boolean preventsExtending
   ) {
      this.type = type;
      this.pos = pos;
      this.rot = rotation;
      this.name = component;
      this.shouldRefresh = shouldRefresh.orElse(((MLMapDecorationType)type.value()).isFromWorld());
      this.shouldSave = shouldSave.orElse(((MLMapDecorationType)type.value()).isFromWorld());
      this.preventsExtending = preventsExtending;
   }

   public Holder<MLMapDecorationType<?, ?>> getType() {
      return this.type;
   }

   public boolean shouldRefreshFromWorld() {
      return this.shouldRefresh;
   }

   public boolean shouldSave() {
      return this.shouldSave;
   }

   public boolean preventsExtending() {
      return false;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MLMapMarker<?> that = (MLMapMarker<?>)o;
         return Objects.equals(this.type, that.type) && Objects.equals(this.pos, that.pos) && Objects.equals(this.name, that.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.pos, this.name);
   }

   public String getMarkerUniqueId() {
      return this.type.getRegisteredName() + "-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public float getRotation() {
      return this.rot;
   }

   public Optional<Component> getDisplayName() {
      return this.name;
   }

   @NotNull
   protected abstract D doCreateDecoration(byte var1, byte var2, byte var3);

   @Nullable
   public D createDecorationFromMarker(MapItemSavedData data) {
      BlockPos pos = this.getPos();
      if (pos == null) {
         return null;
      } else {
         double worldX = pos.getX();
         double worldZ = pos.getZ();
         double rotation = this.getRotation();
         int i = 1 << data.scale;
         float f = (float)(worldX - data.centerX) / i;
         float f1 = (float)(worldZ - data.centerZ) / i;
         byte mapX = (byte)(f * 2.0F + 0.5);
         byte mapY = (byte)(f1 * 2.0F + 0.5);
         if (f >= -64.0F && f1 >= -64.0F && f <= 64.0F && f1 <= 64.0F) {
            rotation += rotation < 0.0 ? -8.0 : 8.0;
            byte rot = (byte)(rotation * 16.0 / 360.0);
            return this.doCreateDecoration(mapX, mapY, rot);
         } else {
            return null;
         }
      }
   }

   public int getFlags() {
      return 0;
   }

   public boolean hasFlag(int flag) {
      return (this.getFlags() & flag) != 0;
   }
}
