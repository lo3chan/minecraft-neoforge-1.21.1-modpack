package net.mehvahdjukaar.moonlight.core.mixins.platform;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.map.CustomMapData;
import net.mehvahdjukaar.moonlight.api.map.ExpandedMapData;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker;
import net.mehvahdjukaar.moonlight.core.map.MapDataInternal;
import net.mehvahdjukaar.moonlight.core.misc.IHoldingPlayerExtension;
import net.mehvahdjukaar.moonlight.core.misc.IMapDataPacketExtension;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData.HoldingPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({HoldingPlayer.class})
public abstract class HoldingPlayerMixin implements IHoldingPlayerExtension {
   @Unique
   private final ReentrantLock moonlight$concurrentLock = new ReentrantLock();
   @Unique
   private final Map<CustomMapData.Type<?, ?>, CustomMapData.DirtyCounter> moonlight$customDataDirty = new IdentityHashMap<>();
   @Unique
   private boolean moonlight$customMarkersDirty = true;
   @Unique
   private int moonlight$dirtyDecorationTicks = 0;
   @Unique
   private int moonlight$volatileDecorationRefreshTicks = 0;
   @Shadow
   @Final
   public Player player;
   @Shadow
   private boolean dirtyData;
   @Shadow
   @Final
   MapItemSavedData this$0;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void initializeDirty(MapItemSavedData mapItemSavedData, Player player, CallbackInfo ci) {
      this.moonlight$customMarkersDirty = true;

      for (CustomMapData<?, ?> v : ((ExpandedMapData)mapItemSavedData).ml$getCustomData().values()) {
         this.moonlight$customDataDirty.put(v.getType(), v.createDirtyCounter());
      }
   }

   @Inject(
      method = {"nextUpdatePacket"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void checkLocked(MapId mapId, CallbackInfoReturnable<Packet<?>> cir) {
      if (this.moonlight$concurrentLock.isLocked()) {
         cir.setReturnValue(null);
      }
   }

   @ModifyReturnValue(
      method = {"nextUpdatePacket"},
      at = {@At("TAIL")}
   )
   public Packet<?> addExtraPacketData(@Nullable Packet<?> packet, MapId mapId) {
      MapItemSavedData data = this.this$0;
      ExpandedMapData ed = (ExpandedMapData)data;
      boolean updateData = false;
      boolean updateDeco = false;
      List<Entry<CustomMapData.Type<?, ?>, CustomMapData.DirtyCounter>> dirtyData = new ArrayList<>();

      for (Entry<CustomMapData.Type<?, ?>, CustomMapData.DirtyCounter> e : this.moonlight$customDataDirty.entrySet()) {
         CustomMapData.DirtyCounter value = e.getValue();
         if (value.isDirty()) {
            dirtyData.add(e);
            updateData = true;
         }
      }

      if (this.moonlight$customMarkersDirty && this.moonlight$dirtyDecorationTicks++ % 5 == 0) {
         this.moonlight$customMarkersDirty = false;
         updateDeco = true;
      }

      List<MLMapDecoration> extra = new ArrayList<>();

      for (MLMapMarker<?> m : MapDataInternal.getDynamicServer(this.player, mapId, data)) {
         MLMapDecoration d = m.createDecorationFromMarker(data);
         if (d != null) {
            extra.add(d);
         }
      }

      if (!extra.isEmpty() || this.moonlight$volatileDecorationRefreshTicks++ % 80 == 0) {
         updateDeco = true;
      }

      if (updateData || updateDeco) {
         if (packet == null) {
            packet = new ClientboundMapItemDataPacket(mapId, this.this$0.scale, this.this$0.locked, Optional.empty(), Optional.empty());
         }

         IMapDataPacketExtension ep = (IMapDataPacketExtension)packet;
         if (updateData) {
            List<CustomMapData.DirtyDataPatch<?, ?>> dirtyPatch = new ArrayList<>();

            for (Entry<CustomMapData.Type<?, ?>, CustomMapData.DirtyCounter> ex : dirtyData) {
               dirtyPatch.add(ml$createDirtyDataPatch(ed, ex.getKey(), ex.getValue()));
               ex.getValue().clearDirty();
            }

            if (!dirtyData.isEmpty()) {
               ep.moonlight$setDirtyCustomData(Optional.of(dirtyPatch));
            }
         }

         if (updateDeco) {
            List<MLMapDecoration> decorations = new ArrayList<>(ed.ml$getCustomDecorations().values());
            decorations.addAll(extra);
            ep.moonlight$setCustomDecorations(Optional.of(decorations));
         }
      }

      return packet;
   }

   @Unique
   private static <P, C extends CustomMapData.DirtyCounter, D extends CustomMapData<C, P>> CustomMapData.DirtyDataPatch<?, ?> ml$createDirtyDataPatch(
      ExpandedMapData ed, CustomMapData.Type<?, ?> type, CustomMapData.DirtyCounter dirtyCounter
   ) {
      D d = (D)ed.ml$getCustomData().get(type);
      P patch = d.createUpdatePatch((C)dirtyCounter);
      return new CustomMapData.DirtyDataPatch<>(type, patch);
   }

   @Override
   public <H extends CustomMapData.DirtyCounter> void moonlight$setCustomDataDirty(CustomMapData.Type<?, ?> type, Consumer<H> dirtySetter) {
      try {
         this.moonlight$concurrentLock.lock();
         CustomMapData.DirtyCounter t = this.moonlight$customDataDirty.get(type);
         dirtySetter.accept((H)t);
      } finally {
         this.moonlight$concurrentLock.unlock();
      }
   }

   @Override
   public void moonlight$setCustomMarkersDirty() {
      this.moonlight$customMarkersDirty = true;
   }

   @Inject(
      method = {"markColorsDirty"},
      at = {@At("HEAD")}
   )
   public void lockData(int x, int z, CallbackInfo ci) {
      this.moonlight$concurrentLock.lock();
   }

   @Inject(
      method = {"markColorsDirty"},
      at = {@At("RETURN")}
   )
   public void sanityCheck(int x, int z, CallbackInfo ci) {
      this.moonlight$concurrentLock.unlock();
   }
}
