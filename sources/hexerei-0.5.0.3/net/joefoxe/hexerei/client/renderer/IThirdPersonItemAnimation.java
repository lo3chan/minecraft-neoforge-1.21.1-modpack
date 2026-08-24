package net.joefoxe.hexerei.client.renderer;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IThirdPersonItemAnimation {
   <T extends LivingEntity> boolean poseRightArm(ItemStack var1, HumanoidModel<T> var2, T var3, HumanoidArm var4, TwoHandedItemAnimation var5);

   default <T extends LivingEntity> boolean poseRightArmMixin(
      ItemStack stack, AgeableListModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded
   ) {
      return this.poseRightArm(stack, (HumanoidModel<T>)model, entity, mainHand, twoHanded);
   }

   <T extends LivingEntity> boolean poseLeftArm(ItemStack var1, HumanoidModel<T> var2, T var3, HumanoidArm var4, TwoHandedItemAnimation var5);

   default <T extends LivingEntity> boolean poseleftArmMixin(
      ItemStack stack, AgeableListModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded
   ) {
      return this.poseLeftArm(stack, (HumanoidModel<T>)model, entity, mainHand, twoHanded);
   }

   default boolean isTwoHanded() {
      return false;
   }
}
