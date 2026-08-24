package net.mehvahdjukaar.moonlight.api.map;

import java.util.List;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker;
import net.mehvahdjukaar.moonlight.api.map.decoration.SimpleMapMarker;
import net.mehvahdjukaar.moonlight.core.CompatHandler;
import net.mehvahdjukaar.moonlight.core.integration.MapAtlasCompat;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class MapHelper {
   @Nullable
   public static MapItemSavedData getMapData(ItemStack stack, Level level, @Nullable Player player) {
      MapItemSavedData data = MapItem.getSavedData(stack, level);
      if (data == null && CompatHandler.MAP_ATLASES && player != null) {
         data = MapAtlasCompat.getSavedDataFromAtlas(stack, level, player);
      }

      return data;
   }

   public static void addVanillaTargetDecorationToItem(ItemStack stack, BlockPos pos, Holder<MapDecorationType> type, int mapColor) {
      MapItemSavedData.addTargetDecoration(stack, pos, "+", type);
      if (mapColor != 0) {
         stack.set(DataComponents.MAP_COLOR, new MapItemColor(mapColor));
      }
   }

   public static void addCustomTargetDecorationToItem(ItemStack stack, BlockPos pos, Holder<MLMapDecorationType<?, ?>> type, int mapColor) {
      MLMapDecorationsComponent customDecoMap = (MLMapDecorationsComponent)stack.getOrDefault(
         MoonlightRegistry.CUSTOM_MAP_DECORATIONS.get(), MLMapDecorationsComponent.EMPTY
      );
      MLMapMarker<?> marker = new SimpleMapMarker(type, pos, 0.0F, Optional.empty());
      customDecoMap = customDecoMap.copyAndAdd(marker);
      stack.set(MoonlightRegistry.CUSTOM_MAP_DECORATIONS.get(), customDecoMap);
      if (mapColor != 0) {
         stack.set(DataComponents.MAP_COLOR, new MapItemColor(mapColor));
      }
   }

   public static void addTargetDecorationToItem(Level level, ItemStack stack, BlockPos pos, ResourceLocation id, int mapColor) {
      Optional<Reference<MapDecorationType>> vanillaType = BuiltInRegistries.MAP_DECORATION_TYPE.getHolder(id);
      if (vanillaType.isPresent()) {
         addVanillaTargetDecorationToItem(stack, pos, (Holder<MapDecorationType>)vanillaType.get(), mapColor);
      } else {
         Registry<MLMapDecorationType<?, ?>> reg = MapDataRegistry.getMapDecorationRegistry(level.registryAccess());
         Reference<MLMapDecorationType<?, ?>> moddedType = (Reference<MLMapDecorationType<?, ?>>)reg.getHolder(id).orElse(null);
         if (moddedType != null) {
            addCustomTargetDecorationToItem(stack, pos, moddedType, mapColor);
         } else {
            addVanillaTargetDecorationToItem(stack, pos, MapDecorationTypes.TARGET_X, mapColor);
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static void addTargetDecorationToItem(ItemStack stack, BlockPos pos, ResourceLocation id, int mapColor) {
      Optional<Reference<MapDecorationType>> vanillaType = BuiltInRegistries.MAP_DECORATION_TYPE.getHolder(id);
      if (vanillaType.isPresent()) {
         addVanillaTargetDecorationToItem(stack, pos, (Holder<MapDecorationType>)vanillaType.get(), mapColor);
      } else {
         Holder<MLMapDecorationType<?, ?>> moddedType = MapDataRegistry.getHolder(id);
         if (moddedType != null) {
            addCustomTargetDecorationToItem(stack, pos, moddedType, mapColor);
         } else {
            addVanillaTargetDecorationToItem(stack, pos, MapDecorationTypes.TARGET_X, mapColor);
         }
      }
   }

   public static boolean toggleMarkersAtPos(Level level, BlockPos pos, ItemStack stack, @Nullable Player player) {
      return getMapData(stack, level, player) instanceof ExpandedMapData expandedMapData ? expandedMapData.ml$toggleCustomDecoration(level, pos) : false;
   }

   public static boolean removeAllCustomMarkers(Level level, ItemStack stack, @Nullable Player player) {
      if (getMapData(stack, level, player) instanceof ExpandedMapData expandedMapData && !level.isClientSide) {
         expandedMapData.ml$resetCustomDecoration();
         return true;
      } else {
         return false;
      }
   }

   public static boolean addSimpleDecorationToMap(
      MapItemSavedData data, Holder<MLMapDecorationType<?, ?>> type, BlockPos pos, float rotation, @Nullable Component name
   ) {
      if (((MLMapDecorationType)type.value()).getMarkerCodec() == SimpleMapMarker.DIRECT_CODEC) {
         SimpleMapMarker marker = new SimpleMapMarker(type, pos, rotation, Optional.ofNullable(name));
         ((ExpandedMapData)data).ml$addCustomMarker(marker);
         return true;
      } else {
         return false;
      }
   }

   public static List<MLMapMarker<?>> getMarkersAtPos(LevelAccessor reader, BlockPos pos) {
      return MapDataInternal.getMarkersFromWorld(reader, pos);
   }
}
