package software.bernie.geckolib.platform;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.service.GeckoLibClient;

public class GeckoLibClientNeoForge implements GeckoLibClient {
   @NotNull
   @Override
   public <T extends LivingEntity & GeoAnimatable> HumanoidModel<?> getArmorModelForItem(
      T animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> defaultModel
   ) {
      Item item = stack.getItem();
      HumanoidModel<?> model = IClientItemExtensions.of(item).getHumanoidArmorModel(animatable, stack, slot, defaultModel);
      return (HumanoidModel<?>)(model == defaultModel
            && GeoRenderProvider.of(item).getGeoArmorRenderer(animatable, stack, slot, (HumanoidModel<T>)defaultModel) instanceof GeoArmorRenderer<?> geoArmorRenderer
         ? geoArmorRenderer
         : model);
   }

   @Nullable
   @Override
   public GeoModel<?> getGeoModelForItem(ItemStack item) {
      if (IClientItemExtensions.of(item).getCustomRenderer() instanceof GeoRenderer<?> geoRenderer) {
         return geoRenderer.getGeoModel();
      } else {
         return GeoRenderProvider.of(item).getGeoItemRenderer() instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
      }
   }

   @Nullable
   @Override
   public GeoModel<?> getGeoModelForArmor(ItemStack armour) {
      if (IClientItemExtensions.of(armour).getHumanoidArmorModel(null, armour, null, null) instanceof GeoArmorRenderer<?> armorRenderer) {
         return armorRenderer.getGeoModel();
      } else {
         return GeoRenderProvider.of(armour).getGeoArmorRenderer(null, armour, null, null) instanceof GeoArmorRenderer<?> armorRenderer
            ? armorRenderer.getGeoModel()
            : null;
      }
   }
}
