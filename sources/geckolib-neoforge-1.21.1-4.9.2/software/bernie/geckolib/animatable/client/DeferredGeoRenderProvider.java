package software.bernie.geckolib.animatable.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public interface DeferredGeoRenderProvider extends GeoRenderProvider {
   MutableObject<GeoRenderProvider> getRenderProvider();

   @Nullable
   @Override
   default BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
      return ((GeoRenderProvider)this.getRenderProvider().getValue()).getGeoItemRenderer();
   }

   @Nullable
   @Override
   default <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
      @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original
   ) {
      return ((GeoRenderProvider)this.getRenderProvider().getValue()).getGeoArmorRenderer(livingEntity, itemStack, equipmentSlot, original);
   }
}
