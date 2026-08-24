package net.mehvahdjukaar.moonlight.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.mehvahdjukaar.moonlight.api.map.CustomMapData;
import net.mehvahdjukaar.moonlight.api.map.ExpandedMapData;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.mehvahdjukaar.moonlight.core.misc.IMapDataPacketExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientboundMapItemDataPacket.class})
public abstract class MapItemDataPacketMixin implements IMapDataPacketExtension {
   @Shadow
   @Final
   private MapId mapId;
   @Unique
   @Nullable
   private List<MLMapDecoration> moonlight$customDecorations = null;
   @Unique
   @Nullable
   private List<CustomMapData.DirtyDataPatch<?, ?>> moonlight$customDataPatches = null;
   @Unique
   private int moonlight$mapCenterX = 0;
   @Unique
   private int moonlight$mapCenterZ = 0;
   @Unique
   private ResourceLocation moonlight$dimension = Level.OVERWORLD.location();

   @Inject(
      method = {"<init>(Lnet/minecraft/world/level/saveddata/maps/MapId;BZLjava/util/Optional;Ljava/util/Optional;)V"},
      at = {@At("RETURN")}
   )
   private void moonlight$addExtraCenterAndDimension(MapId mapId, byte b, boolean bl, Optional optional, Optional optional2, CallbackInfo ci) {
      MinecraftServer server = PlatHelper.getCurrentServer();
      if (server != null) {
         ServerLevel data = server.overworld();
         if (data instanceof ServerLevel) {
            MapItemSavedData datax = data.getMapData(mapId);
            if (datax != null) {
               this.moonlight$mapCenterX = datax.centerX;
               this.moonlight$mapCenterZ = datax.centerZ;
               this.moonlight$dimension = datax.dimension.location();
            }
         }
      }
   }

   @ModifyExpressionValue(
      method = {"<clinit>"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function5;)Lnet/minecraft/network/codec/StreamCodec;"
      )}
   )
   private static StreamCodec<RegistryFriendlyByteBuf, ClientboundMapItemDataPacket> moonlight$modifyMapPacketCodec(
      StreamCodec<RegistryFriendlyByteBuf, ClientboundMapItemDataPacket> original
   ) {
      return StreamCodec.composite(
         original,
         Function.identity(),
         MLMapDecoration.CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs::optional),
         p -> ((IMapDataPacketExtension)p).moonlight$getCustomDecorations(),
         CustomMapData.DirtyDataPatch.STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs::optional),
         p -> ((IMapDataPacketExtension)p).moonlight$getDirtyCustomData(),
         ResourceLocation.STREAM_CODEC,
         p -> ((IMapDataPacketExtension)p).moonlight$getDimension(),
         ByteBufCodecs.INT,
         p -> ((IMapDataPacketExtension)p).moonlight$getMapCenterX(),
         ByteBufCodecs.INT,
         p -> ((IMapDataPacketExtension)p).moonlight$getMapCenterZ(),
         (old, deco, dataPatch, res, x, z) -> {
            if (old == null) {
               return null;
            } else {
               IMapDataPacketExtension ext = (IMapDataPacketExtension)old;
               ext.moonlight$setCustomDecorations(deco);
               ext.moonlight$setDirtyCustomData(dataPatch);
               ext.moonlight$setDimension(res);
               ext.moonlight$setMapCenter(x, z);
               return old;
            }
         }
      );
   }

   @Override
   public Optional<List<CustomMapData.DirtyDataPatch<?, ?>>> moonlight$getDirtyCustomData() {
      return Optional.ofNullable(this.moonlight$customDataPatches);
   }

   @Override
   public Optional<List<MLMapDecoration>> moonlight$getCustomDecorations() {
      return Optional.ofNullable(this.moonlight$customDecorations);
   }

   @Override
   public void moonlight$setCustomDecorations(Optional<List<MLMapDecoration>> deco) {
      this.moonlight$customDecorations = deco.map(List::copyOf).orElse(null);
   }

   @Override
   public void moonlight$setDirtyCustomData(Optional<List<CustomMapData.DirtyDataPatch<?, ?>>> tag) {
      this.moonlight$customDataPatches = tag.map(List::copyOf).orElse(null);
   }

   @NotNull
   @Override
   public ResourceLocation moonlight$getDimension() {
      return this.moonlight$dimension;
   }

   @Override
   public void moonlight$setDimension(ResourceLocation dim) {
      this.moonlight$dimension = dim;
   }

   @Override
   public int moonlight$getMapCenterX() {
      return this.moonlight$mapCenterX;
   }

   @Override
   public int moonlight$getMapCenterZ() {
      return this.moonlight$mapCenterZ;
   }

   @Override
   public void moonlight$setMapCenter(int x, int z) {
      this.moonlight$mapCenterX = x;
      this.moonlight$mapCenterZ = z;
   }

   @Inject(
      method = {"applyToMap"},
      at = {@At("HEAD")}
   )
   private void handleExtraData(MapItemSavedData mapData, CallbackInfo ci) {
      List<MLMapDecoration> serverDeco = this.moonlight$customDecorations;
      List<CustomMapData.DirtyDataPatch<?, ?>> serverDataPatches = this.moonlight$customDataPatches;
      mapData.centerX = this.moonlight$mapCenterX;
      mapData.centerZ = this.moonlight$mapCenterZ;
      mapData.dimension = ResourceKey.create(Registries.DIMENSION, this.moonlight$dimension);
      if (mapData instanceof ExpandedMapData ed) {
         Map<String, MLMapDecoration> decorations = ed.ml$getCustomDecorations();
         if (serverDeco != null) {
            decorations.clear();

            for (int i = 0; i < serverDeco.size(); i++) {
               MLMapDecoration customDecoration = serverDeco.get(i);
               if (customDecoration != null) {
                  decorations.put("icon-" + i, customDecoration);
               } else {
                  Moonlight.LOGGER.warn("Failed to load custom map decoration, skipping");
               }
            }
         }

         if (serverDataPatches != null) {
            for (CustomMapData.DirtyDataPatch<?, ?> p : serverDataPatches) {
               Map<CustomMapData.Type<?, ?>, CustomMapData<?, ?>> customData = ed.ml$getCustomData();
               p.apply(customData);
            }
         }

         for (MLMapMarker<?> m : MapDataInternal.getDynamicClient(this.mapId, mapData)) {
            MLMapDecoration d = m.createDecorationFromMarker(mapData);
            if (d != null) {
               decorations.put(m.getMarkerUniqueId(), d);
            }
         }
      }
   }
}
