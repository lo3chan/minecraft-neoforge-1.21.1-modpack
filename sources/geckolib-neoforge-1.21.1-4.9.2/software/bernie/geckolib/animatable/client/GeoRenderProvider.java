package software.bernie.geckolib.animatable.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

public interface GeoRenderProvider {
   GeoRenderProvider DEFAULT = new GeoRenderProvider() {};

   static GeoRenderProvider of(ItemStack itemStack) {
      return of(itemStack.getItem());
   }

   static GeoRenderProvider of(Item item) {
      return item instanceof GeoItem geoItem ? (GeoRenderProvider)geoItem.getRenderProvider() : DEFAULT;
   }

   @Nullable
   default BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
      return null;
   }

   @Nullable
   default <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
      @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original
   ) {
      return null;
   }
}
