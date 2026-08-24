package net.mehvahdjukaar.moonlight.api.map;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomMapData<C extends CustomMapData.DirtyCounter, P> {
   CustomMapData.Type<P, ?> getType();

   default boolean persistOnCopyOrLock() {
      return true;
   }

   default boolean persistOnRescale() {
      return true;
   }

   default boolean onItemUpdate(MapItemSavedData data, Entity entity) {
      return false;
   }

   @Nullable
   default Component onItemTooltip(MapItemSavedData data, ItemStack stack) {
      return null;
   }

   C createDirtyCounter();

   void load(CompoundTag var1, Provider var2);

   void save(CompoundTag var1, Provider var2);

   P createUpdatePatch(C var1);

   void applyUpdatePatch(P var1);

   default void setDirty(MapItemSavedData data, Consumer<C> dirtySetter) {
      CustomMapData.Type<P, ?> type = this.getType();
      ((ExpandedMapData)data).ml$setCustomDataDirty(type, dirtySetter);
   }

   public interface DirtyCounter {
      boolean isDirty();

      void clearDirty();
   }

   public record DirtyDataPatch<P, D extends CustomMapData<?, P>>(CustomMapData.Type<P, D> type, P patch) {
      public static final StreamCodec<RegistryFriendlyByteBuf, CustomMapData.DirtyDataPatch<?, ?>> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, CustomMapData.DirtyDataPatch<?, ?>>() {
         public void encode(RegistryFriendlyByteBuf buf, CustomMapData.DirtyDataPatch<?, ?> dirtyData) {
            CustomMapData.Type.STREAM_CODEC.encode(buf, dirtyData.type);
            encodeTyped(buf, (CustomMapData.DirtyDataPatch<P, ?>)dirtyData);
         }

         private static <P> void encodeTyped(RegistryFriendlyByteBuf buf, CustomMapData.DirtyDataPatch<P, ?> dirtyData) {
            dirtyData.type.patchCodec().encode(buf, dirtyData.patch);
         }

         public CustomMapData.DirtyDataPatch<?, ?> decode(RegistryFriendlyByteBuf buf) {
            CustomMapData.Type<?, ?> type = (CustomMapData.Type<?, ?>)CustomMapData.Type.STREAM_CODEC.decode(buf);
            return decodeTyped(buf, (CustomMapData.Type<P, D>)type);
         }

         private static <P, D extends CustomMapData<?, P>> CustomMapData.DirtyDataPatch<P, D> decodeTyped(
            RegistryFriendlyByteBuf buf, CustomMapData.Type<P, D> type
         ) {
            P decode = (P)type.patchCodec().decode(buf);
            return new CustomMapData.DirtyDataPatch<>(type, decode);
         }
      };

      public void apply(Map<CustomMapData.Type<?, ?>, CustomMapData<?, ?>> customData) {
         CustomMapData<?, P> data = (CustomMapData<?, P>)customData.get(this.type);
         data.applyUpdatePatch(this.patch);
      }
   }

   public abstract static class Simple<O> implements CustomMapData<CustomMapData.SimpleDirtyCounter, O> {
      protected O value;

      public Simple(O defaultValue) {
         this.value = defaultValue;
      }

      @Deprecated(
         forRemoval = true
      )
      public Simple() {
      }

      @Override
      public CustomMapData.Type<O, ?> getType() {
         return null;
      }

      public CustomMapData.SimpleDirtyCounter createDirtyCounter() {
         return new CustomMapData.SimpleDirtyCounter();
      }

      public O createUpdatePatch(CustomMapData.SimpleDirtyCounter dirtyCounter) {
         return this.value;
      }

      @Override
      public void applyUpdatePatch(O patch) {
         this.value = patch;
      }
   }

   public static class SimpleDirtyCounter implements CustomMapData.DirtyCounter {
      private boolean dirty = true;

      public void markDirty() {
         this.dirty = true;
      }

      @Override
      public boolean isDirty() {
         return this.dirty;
      }

      @Override
      public void clearDirty() {
         this.dirty = false;
      }
   }

   public record Type<P, T extends CustomMapData<?, P>>(ResourceLocation id, Supplier<T> factory, StreamCodec<? super RegistryFriendlyByteBuf, P> patchCodec) {
      public static final Codec<CustomMapData.Type<?, ?>> CODEC = MapDataInternal.getMapDataRegistry().byNameCodec();
      public static final StreamCodec<RegistryFriendlyByteBuf, CustomMapData.Type<?, ?>> STREAM_CODEC = ByteBufCodecs.registry(
         MapDataInternal.getMapDataRegistry().key()
      );

      @NotNull
      public T get(MapItemSavedData mapData) {
         return (T)((ExpandedMapData)mapData).ml$getCustomData().get(this);
      }
   }
}
