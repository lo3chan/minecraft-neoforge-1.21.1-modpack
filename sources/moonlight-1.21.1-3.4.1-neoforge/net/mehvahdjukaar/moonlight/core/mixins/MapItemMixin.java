package net.mehvahdjukaar.moonlight.core.mixins;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.mehvahdjukaar.moonlight.api.map.ExpandedMapData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MapItem.class})
public abstract class MapItemMixin {
   @Inject(
      method = {"appendHoverText"},
      at = {@At("HEAD")}
   )
   private void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag, CallbackInfo ci) {
      MapId mapId = (MapId)stack.get(DataComponents.MAP_ID);
      MapItemSavedData mapData = context.mapData(mapId);
      if (mapData instanceof ExpandedMapData data) {
         data.ml$getCustomData().forEach((s, o) -> {
            Component c = o.onItemTooltip(mapData, stack);
            if (c != null) {
               tooltipComponents.add(c);
            }
         });
      }
   }

   @Inject(
      method = {"update"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void update(Level level, Entity entity, MapItemSavedData data, CallbackInfo ci) {
      AtomicBoolean b = new AtomicBoolean(false);
      if (data instanceof ExpandedMapData d) {
         d.ml$getCustomData().forEach((s, o) -> {
            if (o.onItemUpdate(data, entity)) {
               b.set(true);
            }
         });
      }

      if (b.get()) {
         ci.cancel();
      }
   }
}
