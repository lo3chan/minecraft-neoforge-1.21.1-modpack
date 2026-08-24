package net.joefoxe.hexerei.item;

import net.joefoxe.hexerei.item.custom.DowsingRodItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;

public class ModItemProperties {
   public static double angleDifference(double angle1, double angle2) {
      double diff = (angle2 - angle1 + 180.0) % 360.0 - 180.0;
      return diff < -180.0 ? diff + 360.0 : diff;
   }

   public static void setup() {
      ItemProperties.register(
         (Item)ModItems.DOWSING_ROD.get(),
         HexereiUtil.getResource("angle"),
         (ClampedItemPropertyFunction)(itemStack, level, p_174667_, p_174668_) -> {
            Entity entity = (Entity)(p_174667_ != null ? p_174667_ : itemStack.getEntityRepresentation());
            if (entity instanceof Player && ((DowsingRodItem)itemStack.getItem()).nearestPos != null) {
               float viewRot = Mth.wrapDegrees(entity.getViewYRot(1.0F));
               float rotationFromPlayer = (float)(
                  Math.atan2(
                        ((DowsingRodItem)itemStack.getItem()).nearestPos.getZ() - p_174667_.getZ() + 0.5,
                        ((DowsingRodItem)itemStack.getItem()).nearestPos.getX() - p_174667_.getX() + 0.5
                     )
                     * 180.0
                     / 3.141592653589793
               );
               if (Math.abs(angleDifference(Mth.wrapDegrees(viewRot + 90.0F), rotationFromPlayer)) < 15.0) {
                  return 0.0F;
               } else if (Math.abs(angleDifference(Mth.wrapDegrees(viewRot + 90.0F), rotationFromPlayer)) < 45.0) {
                  return 0.1F;
               } else {
                  return Math.abs(angleDifference(Mth.wrapDegrees(viewRot + 90.0F), rotationFromPlayer)) < 75.0 ? 0.2F : 0.3F;
               }
            } else {
               return 0.3F;
            }
         }
      );
      ItemProperties.register(
         (Item)ModItems.COURIER_PACKAGE.get(), HexereiUtil.getResource("open"), (ClampedItemPropertyFunction)(pStack, pLevel, pEntity, pSeed) -> {
            CustomData data = (CustomData)pStack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null) {
               CompoundTag tag = data.copyTag();
               if (tag.contains("Items") && !tag.getList("Items", 10).isEmpty()) {
                  if (tag.contains("Sealed") && tag.getBoolean("Sealed")) {
                     return 0.0F;
                  }

                  return 0.5F;
               }
            }

            return 1.0F;
         }
      );
      ItemProperties.register(
         (Item)ModItems.COURIER_LETTER.get(), HexereiUtil.getResource("open"), (ClampedItemPropertyFunction)(itemStack, level, p_174667_, p_174668_) -> {
            CustomData data = (CustomData)itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null) {
               CompoundTag tag = data.copyTag();
               if (tag.contains("Sealed") && tag.getBoolean("Sealed")) {
                  return 0.0F;
               }
            }

            return 1.0F;
         }
      );
   }
}
