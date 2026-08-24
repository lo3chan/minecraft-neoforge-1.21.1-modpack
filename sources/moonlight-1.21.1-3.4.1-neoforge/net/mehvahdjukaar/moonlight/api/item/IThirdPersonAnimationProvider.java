package net.mehvahdjukaar.moonlight.api.item;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.misc.IExtendedItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IThirdPersonAnimationProvider {
   @OnlyIn(Dist.CLIENT)
   <T extends LivingEntity> boolean poseRightArm(ItemStack var1, HumanoidModel<T> var2, T var3, HumanoidArm var4);

   @OnlyIn(Dist.CLIENT)
   <T extends LivingEntity> boolean poseLeftArm(ItemStack var1, HumanoidModel<T> var2, T var3, HumanoidArm var4);

   @OnlyIn(Dist.CLIENT)
   default boolean isTwoHanded() {
      return false;
   }

   static void attachToItem(Item target, IThirdPersonAnimationProvider object) {
      if (PlatHelper.getPhysicalSide().isClient()) {
         IExtendedItem extendedItem = (IExtendedItem)target;
         if (extendedItem.moonlight$getClientAnimationExtension() != null && PlatHelper.isDev()) {
            throw new AssertionError("A client animation extension was already registered for this item");
         }

         extendedItem.moonlight$setClientAnimationExtension(object);
      }
   }

   static IThirdPersonAnimationProvider get(Item target) {
      if (target instanceof IThirdPersonAnimationProvider p) {
         return p;
      } else {
         return ((IExtendedItem)target).moonlight$getClientAnimationExtension() instanceof IThirdPersonAnimationProvider p ? p : null;
      }
   }
}
