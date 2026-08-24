package net.mehvahdjukaar.moonlight.api.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.misc.IExtendedItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IFirstPersonAnimationProvider {
   @OnlyIn(Dist.CLIENT)
   void animateItemFirstPerson(
      Player var1, ItemStack var2, InteractionHand var3, HumanoidArm var4, PoseStack var5, float var6, float var7, float var8, float var9
   );

   static void attachToItem(Item target, IFirstPersonAnimationProvider object) {
      if (PlatHelper.getPhysicalSide().isClient()) {
         IExtendedItem extendedItem = (IExtendedItem)target;
         if (extendedItem.moonlight$getClientAnimationExtension() != null && PlatHelper.isDev()) {
            throw new AssertionError("A client animation extension was already registered for this item");
         }

         extendedItem.moonlight$setClientAnimationExtension(object);
      }
   }

   static IFirstPersonAnimationProvider get(Item target) {
      if (target instanceof IFirstPersonAnimationProvider p) {
         return p;
      } else {
         return ((IExtendedItem)target).moonlight$getClientAnimationExtension() instanceof IFirstPersonAnimationProvider p ? p : null;
      }
   }
}
