package net.mcreator.borninchaosv.init;

import net.mcreator.borninchaosv.item.BonescallerStaffItem;
import net.mcreator.borninchaosv.item.PumpkinhandgunItem;
import net.mcreator.borninchaosv.item.PumpkinstaffaItem;
import net.mcreator.borninchaosv.item.StaffOfMagicArrowsAItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import software.bernie.geckolib.animatable.GeoItem;

@EventBusSubscriber
public class ItemAnimationFactory {
   @SubscribeEvent
   public static void animatedItems(Post event) {
      String animation = "";
      ItemStack mainhandItem = event.getEntity().getMainHandItem().copy();
      ItemStack offhandItem = event.getEntity().getOffhandItem().copy();
      if (mainhandItem.getItem() instanceof GeoItem || offhandItem.getItem() instanceof GeoItem) {
         if (mainhandItem.getItem() instanceof StaffOfMagicArrowsAItem animatable) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckoAnim");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckoAnim", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((StaffOfMagicArrowsAItem)event.getEntity().getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof StaffOfMagicArrowsAItem animatablex) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckoAnim");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckoAnim", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((StaffOfMagicArrowsAItem)event.getEntity().getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (mainhandItem.getItem() instanceof BonescallerStaffItem animatablexx) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckoAnim");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckoAnim", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((BonescallerStaffItem)event.getEntity().getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof BonescallerStaffItem animatablexxx) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckoAnim");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckoAnim", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((BonescallerStaffItem)event.getEntity().getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (mainhandItem.getItem() instanceof PumpkinstaffaItem animatablexxxx) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckoAnim");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckoAnim", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((PumpkinstaffaItem)event.getEntity().getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof PumpkinstaffaItem animatablexxxxx) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckoAnim");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckoAnim", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((PumpkinstaffaItem)event.getEntity().getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (mainhandItem.getItem() instanceof PumpkinhandgunItem animatablexxxxxx) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckoAnim");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckoAnim", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((PumpkinhandgunItem)event.getEntity().getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof PumpkinhandgunItem animatablexxxxxxx) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckoAnim");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckoAnim", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((PumpkinhandgunItem)event.getEntity().getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }
      }
   }
}
