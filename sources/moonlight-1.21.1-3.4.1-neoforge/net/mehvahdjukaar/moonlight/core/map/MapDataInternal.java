package net.mehvahdjukaar.moonlight.core.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.map.CustomMapData;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLSpecialMapDecorationType;
import net.mehvahdjukaar.moonlight.api.misc.MapRegistry;
import net.mehvahdjukaar.moonlight.api.misc.TriFunction;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class MapDataInternal {
   public static final Registry<CustomMapData.Type<?, ?>> CUSTOM_MAP_DATA_REGISTRY = RegHelper.registerRegistry(Moonlight.res("custom_map_data_types"), true);
   public static final ResourceKey<Registry<MLMapDecorationType<?, ?>>> MAP_DECORATION_REGISTRY_KEY = ResourceKey.createRegistryKey(Moonlight.res("map_marker"));
   public static final ResourceLocation GENERIC_STRUCTURE_ID = Moonlight.res("generic_structure");
   private static final MapRegistry<Supplier<MLSpecialMapDecorationType<?, ?>>> CODE_TYPES_FACTORIES = new MapRegistry<>("code_map_decoration_types_factories");
   private static final List<TriFunction<Player, MapId, MapItemSavedData, Set<MLMapMarker<?>>>> DYNAMIC_SERVER = Collections.synchronizedList(new ArrayList<>());
   private static final List<BiFunction<MapId, MapItemSavedData, Set<MLMapMarker<?>>>> DYNAMIC_CLIENT = Collections.synchronizedList(new ArrayList<>());

   public static <P, T extends CustomMapData<?, P>> CustomMapData.Type<P, T> registerCustomMapSavedData(CustomMapData.Type<P, T> type) {
      if (CUSTOM_MAP_DATA_REGISTRY.containsKey(type.id())) {
         throw new IllegalArgumentException("Duplicate custom map data registration " + type.id());
      } else {
         RegHelper.register(type.id(), () -> type, CUSTOM_MAP_DATA_REGISTRY.key());
         return type;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static MLMapDecorationType<?, ?> getGenericStructure() {
      return getOrDefault(GENERIC_STRUCTURE_ID);
   }

   public static void registerCustomType(ResourceLocation id, Supplier<MLSpecialMapDecorationType<?, ?>> decorationType) {
      CODE_TYPES_FACTORIES.register(id, decorationType);
   }

   public static MLSpecialMapDecorationType<?, ?> createCustomType(ResourceLocation factoryID) {
      Supplier<MLSpecialMapDecorationType<?, ?>> factory = Objects.requireNonNull(
         CODE_TYPES_FACTORIES.getValue(factoryID), "No map decoration type with id: " + factoryID
      );
      MLSpecialMapDecorationType<?, ? extends MLMapMarker<?>> specialType = (MLSpecialMapDecorationType<?, ? extends MLMapMarker<?>>)factory.get();
      specialType.factoryID = factoryID;
      return specialType;
   }

   @Deprecated(
      forRemoval = true
   )
   public static MLMapDecorationType<?, ?> getAssociatedType(Holder<Structure> structure) {
      for (MLMapDecorationType<?, ?> v : getValues()) {
         Optional<HolderSet<Structure>> associatedStructure = v.getAssociatedStructure();
         if (associatedStructure.isPresent() && associatedStructure.get().contains(structure)) {
            return v;
         }
      }

      return getGenericStructure();
   }

   public static Holder<MLMapDecorationType<?, ?>> getDecorationFoStructure(Level level, Holder<Structure> structure) {
      Registry<MLMapDecorationType<?, ?>> reg = getMapDecorationRegistry(level.registryAccess());
      Optional<Reference<MLMapDecorationType<?, ?>>> matched = reg.holders()
         .filter(h -> ((MLMapDecorationType)h.value()).getAssociatedStructure().map(s -> s.contains(structure)).orElse(false))
         .findFirst();
      return (Holder<MLMapDecorationType<?, ?>>)matched.orElseGet(() -> (Reference<MLMapDecorationType<?, ?>>)reg.getHolder(GENERIC_STRUCTURE_ID).orElseThrow());
   }

   @Internal
   public static void init() {
      RegHelper.registerDataPackRegistry(MAP_DECORATION_REGISTRY_KEY, MLMapDecorationType.DIRECT_CODEC, MLMapDecorationType.DIRECT_CODEC);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Registry<MLMapDecorationType<?, ?>> hackyGetRegistry() {
      return Utils.hackyGetRegistryAccess().registryOrThrow(MAP_DECORATION_REGISTRY_KEY);
   }

   public static Registry<CustomMapData.Type<?, ?>> getMapDataRegistry() {
      return CUSTOM_MAP_DATA_REGISTRY;
   }

   public static Registry<MLMapDecorationType<?, ?>> getMapDecorationRegistry(RegistryAccess registryAccess) {
      return registryAccess.registryOrThrow(MAP_DECORATION_REGISTRY_KEY);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Registry<MLMapDecorationType<?, ?>> getRegistry(RegistryAccess registryAccess) {
      return getMapDecorationRegistry(registryAccess);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Collection<MLMapDecorationType<?, ?>> getValues() {
      return hackyGetRegistry().stream().toList();
   }

   @Deprecated(
      forRemoval = true
   )
   public static Set<Entry<ResourceKey<MLMapDecorationType<?, ?>>, MLMapDecorationType<?, ?>>> getEntries() {
      return hackyGetRegistry().entrySet();
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static MLMapDecorationType<? extends MLMapDecoration, ?> getOrDefault(String id) {
      return (MLMapDecorationType<? extends MLMapDecoration, ?>)getOrDefault(ResourceLocation.parse(id));
   }

   @Deprecated(
      forRemoval = true
   )
   public static MLMapDecorationType<?, ?> getOrDefault(ResourceLocation id) {
      Registry<MLMapDecorationType<?, ?>> reg = hackyGetRegistry();
      MLMapDecorationType<?, ? extends MLMapMarker<?>> r = (MLMapDecorationType<?, ? extends MLMapMarker<?>>)reg.get(id);
      return r == null ? (MLMapDecorationType)reg.get(GENERIC_STRUCTURE_ID) : r;
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static Holder<MLMapDecorationType<?, ?>> getHolder(ResourceLocation id) {
      return (Holder<MLMapDecorationType<?, ?>>)hackyGetRegistry().getHolder(id).orElse(null);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Optional<MLMapDecorationType<?, ?>> getOptional(ResourceLocation id) {
      return hackyGetRegistry().getOptional(id);
   }

   public static Set<MLMapMarker<?>> getDynamicServer(Player player, MapId mapId, MapItemSavedData data) {
      Set<MLMapMarker<?>> dynamic = new HashSet<>();

      for (TriFunction<Player, MapId, MapItemSavedData, Set<MLMapMarker<?>>> v : DYNAMIC_SERVER) {
         dynamic.addAll(v.apply(player, mapId, data));
      }

      return dynamic;
   }

   public static Set<MLMapMarker<?>> getDynamicClient(MapId mapId, MapItemSavedData data) {
      Set<MLMapMarker<?>> dynamic = new HashSet<>();

      for (BiFunction<MapId, MapItemSavedData, Set<MLMapMarker<?>>> v : DYNAMIC_CLIENT) {
         dynamic.addAll(v.apply(mapId, data));
      }

      return dynamic;
   }

   public static List<MLMapMarker<?>> getMarkersFromWorld(LevelAccessor reader, BlockPos pos) {
      List<MLMapMarker<?>> list = new ArrayList<>();

      for (MLMapDecorationType<?, ?> type : getMapDecorationRegistry(reader.registryAccess())) {
         MLMapMarker<?> c = type.createMarkerFromWorld(reader, pos);
         if (c != null) {
            list.add(c);
         }
      }

      return list;
   }

   public static void addDynamicClientMarkersEvent(BiFunction<MapId, MapItemSavedData, Set<MLMapMarker<?>>> event) {
      DYNAMIC_CLIENT.add(event);
   }

   public static void addDynamicServerMarkersEvent(TriFunction<Player, MapId, MapItemSavedData, Set<MLMapMarker<?>>> event) {
      DYNAMIC_SERVER.add(event);
   }
}
