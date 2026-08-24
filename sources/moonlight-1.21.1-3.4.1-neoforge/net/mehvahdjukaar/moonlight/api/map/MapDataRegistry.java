package net.mehvahdjukaar.moonlight.api.map;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLSpecialMapDecorationType;
import net.mehvahdjukaar.moonlight.api.misc.HolderReference;
import net.mehvahdjukaar.moonlight.api.misc.TriFunction;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class MapDataRegistry {
   @Deprecated(
      forRemoval = true
   )
   public static final ResourceKey<Registry<MLMapDecorationType<?, ?>>> REGISTRY_KEY = MapDataInternal.MAP_DECORATION_REGISTRY_KEY;
   public static final ResourceKey<Registry<MLMapDecorationType<?, ?>>> MAP_DECORATION_REGISTRY_KEY = MapDataInternal.MAP_DECORATION_REGISTRY_KEY;
   public static final HolderReference<MLMapDecorationType<?, ?>> GENERIC_STRUCTURE_MARKER = HolderReference.of(
      MapDataInternal.GENERIC_STRUCTURE_ID, MAP_DECORATION_REGISTRY_KEY
   );

   public static <P, T extends CustomMapData<?, P>> CustomMapData.Type<P, T> registerCustomMapSavedData(CustomMapData.Type<P, T> type) {
      return MapDataInternal.registerCustomMapSavedData(type);
   }

   public static <P, T extends CustomMapData<?, P>> CustomMapData.Type<P, T> registerCustomMapSavedData(
      ResourceLocation id, Supplier<T> factory, StreamCodec<? super RegistryFriendlyByteBuf, P> patchCodec
   ) {
      return registerCustomMapSavedData(new CustomMapData.Type<>(id, factory, patchCodec));
   }

   public static void registerSpecialMapDecorationTypeFactory(ResourceLocation factoryId, Supplier<MLSpecialMapDecorationType<?, ?>> decorationTypeFactory) {
      MapDataInternal.registerCustomType(factoryId, decorationTypeFactory);
   }

   public static void addDynamicClientMarkersEvent(BiFunction<MapId, MapItemSavedData, Set<MLMapMarker<?>>> event) {
      MapDataInternal.addDynamicClientMarkersEvent(event);
   }

   public static void addDynamicServerMarkersEvent(TriFunction<Player, MapId, MapItemSavedData, Set<MLMapMarker<?>>> event) {
      MapDataInternal.addDynamicServerMarkersEvent(event);
   }

   @Deprecated(
      forRemoval = true
   )
   public static MLMapDecorationType<?, ?> getAssociatedType(Holder<Structure> structure) {
      return MapDataInternal.getAssociatedType(structure);
   }

   public static Holder<MLMapDecorationType<?, ?>> getDecorationFoStructure(Level level, Holder<Structure> structure) {
      return MapDataInternal.getDecorationFoStructure(level, structure);
   }

   public static Registry<MLMapDecorationType<?, ?>> getMapDecorationRegistry(RegistryAccess registryAccess) {
      return MapDataInternal.getMapDecorationRegistry(registryAccess);
   }

   public static Registry<CustomMapData.Type<?, ?>> getMapDataRegistry() {
      return MapDataInternal.getMapDataRegistry();
   }

   @Deprecated(
      forRemoval = true
   )
   public static Registry<MLMapDecorationType<?, ?>> getRegistry(RegistryAccess registryAccess) {
      return MapDataInternal.getRegistry(registryAccess);
   }

   @Deprecated(
      forRemoval = true
   )
   public static MLMapDecorationType<?, ?> getOrDefault(ResourceLocation id) {
      return MapDataInternal.getOrDefault(id);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Optional<MLMapDecorationType<?, ?>> getOptional(ResourceLocation id) {
      return MapDataInternal.getOptional(id);
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static Holder<MLMapDecorationType<?, ?>> getHolder(ResourceLocation id) {
      return MapDataInternal.getHolder(id);
   }

   @Deprecated(
      forRemoval = true
   )
   public static MLMapDecorationType<?, ?> getDefaultType() {
      return MapDataInternal.getGenericStructure();
   }
}
