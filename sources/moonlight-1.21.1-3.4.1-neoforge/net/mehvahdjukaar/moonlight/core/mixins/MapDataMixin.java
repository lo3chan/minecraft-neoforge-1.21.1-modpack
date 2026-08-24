package net.mehvahdjukaar.moonlight.core.mixins;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.map.CustomMapData;
import net.mehvahdjukaar.moonlight.api.map.ExpandedMapData;
import net.mehvahdjukaar.moonlight.api.map.MLMapDecorationsComponent;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.mehvahdjukaar.moonlight.core.misc.IHoldingPlayerExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData.HoldingPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MapItemSavedData.class})
public abstract class MapDataMixin extends SavedData implements ExpandedMapData {
   @Final
   @Shadow
   public byte scale;
   @Final
   @Shadow
   Map<String, MapDecoration> decorations;
   @Shadow
   @Final
   private Map<String, MapBanner> bannerMarkers;
   @Shadow
   public int centerX;
   @Shadow
   public int centerZ;
   @Shadow
   @Final
   private List<HoldingPlayer> carriedBy;
   @Unique
   public final Map<String, MLMapDecoration> moonlight$customDecorations = Maps.newLinkedHashMap();
   @Unique
   private final Map<String, MLMapMarker<?>> moonlight$customMapMarkers = Maps.newHashMap();
   @Unique
   public final Map<CustomMapData.Type<?, ?>, CustomMapData<?, ?>> moonlight$customData = new LinkedHashMap<>();

   @Override
   public void ml$setCustomDecorationsDirty() {
      this.setDirty();
      this.carriedBy.forEach(h -> ((IHoldingPlayerExtension)h).moonlight$setCustomMarkersDirty());
   }

   @Override
   public <H extends CustomMapData.DirtyCounter> void ml$setCustomDataDirty(CustomMapData.Type<?, ?> type, Consumer<H> dirtySetter) {
      this.setDirty();
      this.carriedBy.forEach(h -> ((IHoldingPlayerExtension)h).moonlight$setCustomDataDirty(type, dirtySetter));
   }

   @Override
   public Map<CustomMapData.Type<?, ?>, CustomMapData<?, ?>> ml$getCustomData() {
      return this.moonlight$customData;
   }

   @Override
   public Map<String, MLMapDecoration> ml$getCustomDecorations() {
      return this.moonlight$customDecorations;
   }

   @Override
   public Map<String, MLMapMarker<?>> ml$getCustomMarkers() {
      return this.moonlight$customMapMarkers;
   }

   @Override
   public int ml$getVanillaDecorationSize() {
      return this.decorations.size();
   }

   @Override
   public <M extends MLMapMarker<?>> void ml$addCustomMarker(M marker) {
      MLMapDecoration decoration = marker.createDecorationFromMarker((MapItemSavedData)this);
      if (decoration != null) {
         this.moonlight$customDecorations.put(marker.getMarkerUniqueId(), decoration);
         if (marker.shouldSave()) {
            this.moonlight$customMapMarkers.put(marker.getMarkerUniqueId(), marker);
         }

         this.ml$setCustomDecorationsDirty();
      }
   }

   @Override
   public boolean ml$removeCustomMarker(String key) {
      this.moonlight$customDecorations.remove(key);
      if (this.moonlight$customMapMarkers.containsKey(key)) {
         this.moonlight$customMapMarkers.remove(key);
         this.ml$setCustomDecorationsDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public MapItemSavedData ml$copy() {
      MapItemSavedData newData = MapItemSavedData.load(this.save(new CompoundTag(), Utils.hackyGetRegistryAccess()), Utils.hackyGetRegistryAccess());
      newData.setDirty();
      return newData;
   }

   @Override
   public void ml$resetCustomDecoration() {
      if (!this.bannerMarkers.isEmpty() || !this.moonlight$customMapMarkers.isEmpty()) {
         this.ml$setCustomDecorationsDirty();
      }

      for (String key : this.moonlight$customMapMarkers.keySet()) {
         this.moonlight$customDecorations.remove(key);
      }

      this.moonlight$customMapMarkers.clear();

      for (String key : this.bannerMarkers.keySet()) {
         this.decorations.remove(key);
      }

      this.bannerMarkers.clear();
   }

   @Override
   public boolean ml$toggleCustomDecoration(LevelAccessor world, BlockPos pos) {
      if (world.isClientSide()) {
         List<MLMapMarker<?>> markers = MapDataInternal.getMarkersFromWorld(world, pos);
         return !markers.isEmpty();
      } else {
         double d0 = pos.getX() + 0.5;
         double d1 = pos.getZ() + 0.5;
         int i = 1 << this.scale;
         double d2 = (d0 - this.centerX) / i;
         double d3 = (d1 - this.centerZ) / i;
         if (d2 >= -63.0 && d3 >= -63.0 && d2 <= 63.0 && d3 <= 63.0) {
            List<MLMapMarker<?>> markers = MapDataInternal.getMarkersFromWorld(world, pos);
            boolean changed = false;

            for (MLMapMarker<?> marker : markers) {
               if (marker != null) {
                  String id = marker.getMarkerUniqueId();
                  if (marker.equals(this.moonlight$customMapMarkers.get(id))) {
                     this.ml$removeCustomMarker(id);
                  } else {
                     this.ml$addCustomMarker(marker);
                  }

                  changed = true;
               }
            }

            return changed;
         } else {
            return false;
         }
      }
   }

   @Inject(
      method = {"locked"},
      at = {@At("RETURN")}
   )
   public void locked(CallbackInfoReturnable<MapItemSavedData> cir) {
      MapItemSavedData data = (MapItemSavedData)cir.getReturnValue();
      if (data instanceof ExpandedMapData expandedMapData) {
         expandedMapData.ml$getCustomMarkers().putAll(this.ml$getCustomMarkers());
         expandedMapData.ml$getCustomDecorations().putAll(this.ml$getCustomDecorations());
      }

      this.moonlight$copyCustomData(data, false, Utils.hackyGetRegistryAccess());
   }

   @Inject(
      method = {"scaled"},
      at = {@At("RETURN")}
   )
   public void scaled(CallbackInfoReturnable<MapItemSavedData> cir) {
      MapItemSavedData data = (MapItemSavedData)cir.getReturnValue();
      this.moonlight$copyCustomData(data, true, Utils.hackyGetRegistryAccess());
   }

   @Unique
   private void moonlight$copyCustomData(MapItemSavedData data, boolean isScaled, Provider reg) {
      if (data instanceof ExpandedMapData ed) {
         for (Entry<CustomMapData.Type<?, ?>, CustomMapData<?, ?>> entry : this.moonlight$customData.entrySet()) {
            CustomMapData<?, ?> customData = entry.getValue();
            boolean persists = isScaled ? customData.persistOnRescale() : customData.persistOnCopyOrLock();
            if (persists) {
               CompoundTag t = new CompoundTag();
               customData.save(t, reg);
               ed.ml$getCustomData().get(entry.getKey()).load(t, reg);
            }
         }
      }
   }

   @Inject(
      method = {"tickCarriedBy"},
      at = {@At("TAIL")}
   )
   public void tickCarriedBy(Player player, ItemStack stack, CallbackInfo ci) {
      MLMapDecorationsComponent customDecoComponent = (MLMapDecorationsComponent)stack.get(MoonlightRegistry.CUSTOM_MAP_DECORATIONS.get());
      if (customDecoComponent != null) {
         customDecoComponent.addToMapIfAbsent(this.moonlight$customMapMarkers.keySet(), this);
      }
   }

   @Inject(
      method = {"load"},
      at = {@At("RETURN")}
   )
   private static void load(CompoundTag compound, Provider registries, CallbackInfoReturnable<MapItemSavedData> cir) {
      MapItemSavedData data = (MapItemSavedData)cir.getReturnValue();
      if (compound.contains("customMarkers") && data instanceof ExpandedMapData mapData) {
         ListTag listNBT = compound.getList("customMarkers", 10);
         MLMapMarker.assertCanSerialize(registries);
         RegistryOps<Tag> registryOps = registries.createSerializationContext(NbtOps.INSTANCE);

         for (int j = 0; j < listNBT.size(); j++) {
            MLMapMarker.CODEC
               .parse(registryOps, listNBT.getCompound(j))
               .resultOrPartial(string -> Moonlight.LOGGER.warn("Failed to parse moonlight map marker: '{}'", string))
               .ifPresent(marker -> {
                  mapData.ml$getCustomMarkers().put(marker.getMarkerUniqueId(), (MLMapMarker<?>)marker);
                  mapData.ml$addCustomMarker(marker);
               });
         }

         mapData.ml$getCustomData().values().forEach(customMapData -> customMapData.load(compound, registries));
      }
   }

   @Inject(
      method = {"save"},
      at = {@At("RETURN")}
   )
   public void save(CompoundTag tag, Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
      CompoundTag com = (CompoundTag)cir.getReturnValue();
      ListTag listNBT = new ListTag();
      if (!this.moonlight$customMapMarkers.isEmpty()) {
         MLMapMarker.assertCanSerialize(registries);
      }

      RegistryOps<Tag> registryOps = registries.createSerializationContext(NbtOps.INSTANCE);

      for (MLMapMarker<?> marker : this.moonlight$customMapMarkers.values()) {
         if (marker.shouldSave()) {
            listNBT.add((Tag)MLMapMarker.CODEC.encodeStart(registryOps, marker).getOrThrow());
         }
      }

      com.put("customMarkers", listNBT);
      this.moonlight$customData.forEach((s, o) -> o.save(tag, registries));
   }

   @Inject(
      method = {"checkBanners"},
      at = {@At("TAIL")}
   )
   public void checkCustomDeco(BlockGetter world, int x, int z, CallbackInfo ci) {
      if (world instanceof LevelAccessor la) {
         ArrayList toRemove = new ArrayList();
         ArrayList toAdd = new ArrayList();

         for (Entry<String, MLMapMarker<?>> e : this.moonlight$customMapMarkers.entrySet()) {
            MLMapMarker<?> marker = e.getValue();
            if (marker.getPos().getX() == x && marker.getPos().getZ() == z && marker.shouldRefreshFromWorld()) {
               MLMapMarker<?> newMarker = ((MLMapDecorationType)marker.getType().value()).createMarkerFromWorld(la, marker.getPos());
               String id = e.getKey();
               if (newMarker == null) {
                  toRemove.add(id);
               } else if (!Objects.equals(marker, newMarker)) {
                  toRemove.add(id);
                  toAdd.add(newMarker);
               }
            }
         }

         toRemove.forEach(this::ml$removeCustomMarker);
         toAdd.forEach(this::ml$addCustomMarker);
      }
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void initCustomData(int i, int j, byte b, boolean bl, boolean bl2, boolean bl3, ResourceKey<Level> resourceKey, CallbackInfo ci) {
      for (CustomMapData.Type<?, ?> d : MapDataInternal.getMapDataRegistry()) {
         this.moonlight$customData.put(d, (CustomMapData<?, ?>)d.factory().get());
      }
   }

   @ModifyReturnValue(
      method = {"isExplorationMap"},
      at = {@At("RETURN")}
   )
   public boolean ml$isExplorationMap(boolean original) {
      if (original) {
         return true;
      } else {
         for (MLMapMarker<?> mapDecoration : this.moonlight$customMapMarkers.values()) {
            if (mapDecoration.preventsExtending()) {
               return true;
            }
         }

         return false;
      }
   }
}
