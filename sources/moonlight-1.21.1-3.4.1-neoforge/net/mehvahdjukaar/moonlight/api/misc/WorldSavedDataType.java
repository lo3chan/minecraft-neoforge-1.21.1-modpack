package net.mehvahdjukaar.moonlight.api.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import org.jetbrains.annotations.Nullable;

public final class WorldSavedDataType<D extends WorldSavedData> {
   public static final StreamCodec<RegistryFriendlyByteBuf, WorldSavedDataType<? extends WorldSavedData>> STREAM_CODEC = ByteBufCodecs.registry(
      MoonlightRegistry.WORLD_SAVED_DATA_TYPE_REGISTRY.key()
   );
   public static final Codec<WorldSavedDataType<? extends WorldSavedData>> CODEC = MoonlightRegistry.WORLD_SAVED_DATA_TYPE_REGISTRY.byNameCodec();
   private final Supplier<Codec<D>> codec;
   @Nullable
   private final Supplier<StreamCodec<? super RegistryFriendlyByteBuf, D>> streamCodec;
   private final Factory<D> factory;
   private final String name;
   private final WorldSavedDataType.Scope scope;
   private D clientInstance = (D)null;

   public WorldSavedDataType(
      ResourceLocation id,
      Function<ServerLevel, D> overworldToDataConstructor,
      Supplier<Codec<D>> codec,
      @Nullable Supplier<StreamCodec<? super RegistryFriendlyByteBuf, D>> streamCodec
   ) {
      this(id, overworldToDataConstructor, codec, streamCodec, WorldSavedDataType.Scope.SINGLE_OVERWORLD);
   }

   public WorldSavedDataType(
      ResourceLocation id,
      Function<ServerLevel, D> overworldToDataConstructor,
      Supplier<Codec<D>> codec,
      @Nullable Supplier<StreamCodec<? super RegistryFriendlyByteBuf, D>> streamCodec,
      WorldSavedDataType.Scope scope
   ) {
      this.codec = codec;
      this.streamCodec = streamCodec;
      this.name = id.toDebugFileName();
      this.scope = scope;
      this.factory = new Factory(() -> overworldToDataConstructor.apply(PlatHelper.getCurrentServer().overworld()), this::load, null);
   }

   @Nullable
   public D getData(Level level) {
      if (level.isClientSide && !this.isSyncable()) {
         throw new IllegalStateException("Tried to access unsyncable world saved data on client side!");
      } else if (level instanceof ServerLevel server) {
         ServerLevel targetLevel = this.scope.getTargetLevel(server);
         return (D)targetLevel.getDataStorage().computeIfAbsent(this.factory, this.name);
      } else {
         return this.clientInstance;
      }
   }

   public void setData(Level level, D data) {
      if (level instanceof ServerLevel server) {
         ServerLevel targetLevel = this.scope.getTargetLevel(server);
         targetLevel.getDataStorage().set(this.name, data);
      } else {
         this.clientInstance = data;
      }

      data.onReassigned(level);
   }

   private D load(CompoundTag tag, Provider provider) {
      Tag t = tag.get(this.getName());
      RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
      DataResult<Pair<D, Tag>> dataResult = this.codec.get().decode(ops, t);
      return (D)((Pair)dataResult.getOrThrow()).getFirst();
   }

   public Codec<D> getCodec() {
      return this.codec.get();
   }

   @Nullable
   public StreamCodec<? super RegistryFriendlyByteBuf, D> getStreamCodec() {
      return this.streamCodec == null ? null : this.streamCodec.get();
   }

   public boolean isSyncable() {
      return this.streamCodec != null;
   }

   public String getName() {
      return this.name;
   }

   public static enum Scope {
      SINGLE_OVERWORLD,
      PER_LEVEL;

      private ServerLevel getTargetLevel(ServerLevel sl) {
         ServerLevel targetLevel;
         if (this == PER_LEVEL) {
            targetLevel = sl;
         } else {
            targetLevel = sl.getServer().overworld();
         }

         return targetLevel;
      }
   }
}
