package software.bernie.geckolib.service;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public interface GeckoLibClient {
   @NotNull
   <T extends LivingEntity & GeoAnimatable> HumanoidModel<?> getArmorModelForItem(T var1, ItemStack var2, EquipmentSlot var3, HumanoidModel<LivingEntity> var4);

   @Nullable
   GeoModel<?> getGeoModelForItem(ItemStack var1);

   @Nullable
   GeoModel<?> getGeoModelForArmor(ItemStack var1);
}
