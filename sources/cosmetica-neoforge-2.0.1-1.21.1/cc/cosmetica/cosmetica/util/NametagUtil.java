package cc.cosmetica.cosmetica.util;

import cc.cosmetica.core.api.Accessory;
import cc.cosmetica.core.render.HumanoidAccessoriesLayer;
import cc.cosmetica.core.render.HumanoidAccessoriesLayer.ArmourEquipper;
import cc.cosmetica.cosmetica.gui.player.AccessoriesAttachment;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.GUIPlayer.Attachment;
import cc.cosmetica.kupe.api.gui.GUIPlayer.CapeProperties;
import com.mojang.blaze3d.vertex.PoseStack;
import gg.cloaks.javaclient.model.Accessory.AttachmentEnum;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class NametagUtil {
   public static int extraSpaceTaken = 69;
   public static boolean isSnipe = false;

   public static void shiftNametags(PoseStack stack, GUIPlayer player, int nametags) {
      if (!player.pose.upsideDown) {
         float hatTopY = 0.0F;
         Collection<Accessory> accessories = (Collection<Accessory>)player.getConfiguration(AccessoriesAttachment.INSTANCE);
         if (accessories == null) {
            return;
         }

         Iterator<Attachment<?>> iterator = player.getRenderingAttachments();
         boolean cloak = false;
         boolean elytra = false;

         while (iterator.hasNext()) {
            Attachment<?> attachment = iterator.next();
            if (attachment == GUIPlayer.ELYTRA) {
               elytra = true;
            }

            if (attachment == GUIPlayer.CAPE) {
               CapeProperties properties = (CapeProperties)player.getConfiguration(GUIPlayer.CAPE);
               if (properties != null && properties.getTexture().isPresent()) {
                  cloak = true;
               }
            }
         }

         for (Accessory accessory : accessories) {
            if (HumanoidAccessoriesLayer.canRenderAccessory(accessory, new NametagUtil.GuiPlayerEquipper(elytra), cloak)
               && accessory.getAttachment() == AttachmentEnum.HEAD) {
               hatTopY = Math.max(hatTopY, (float)(accessory.getModel().getBoundingBox().maxY + accessory.getOffset().y * 16.0 - 12.0));
            }
         }

         if (hatTopY > 0.0F) {
            float normalizedAngleMultiplier = (float)(-(Math.abs(Math.toRadians(player.pose.xRot)) / 1.57 - 1.0));
            float lookAngleMultiplier;
            if (player.pose.sneaking) {
               lookAngleMultiplier = 0.0F;
            } else {
               lookAngleMultiplier = normalizedAngleMultiplier;
            }

            double shift = Math.max(hatTopY * lookAngleMultiplier, 0.0F) / 16.0;
            int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int outfitPlayerHeightApprox = 2 * (isSnipe ? (int)(width * 0.12) : Math.min(90, Math.max(50, 10 + (int)(width * 0.0625))));
            int remainingSpace = (height - (outfitPlayerHeightApprox + extraSpaceTaken)) / 2;
            double m = 0.0186;
            double c = -0.469;
            double cap = Math.max(0.275, 0.0186 * remainingSpace + -0.469);
            stack.translate(0.0, Math.min(shift, cap), 0.0);
         }
      }
   }

   private static final class GuiPlayerEquipper implements ArmourEquipper {
      private final boolean elytra;

      public GuiPlayerEquipper(boolean elytra) {
         this.elytra = elytra;
      }

      public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
         if (equipmentSlot != EquipmentSlot.CHEST) {
            return ItemStack.EMPTY;
         } else {
            return this.elytra ? new ItemStack(Items.ELYTRA) : ItemStack.EMPTY;
         }
      }

      public boolean hasLeftShoulderEntity() {
         return false;
      }

      public boolean hasRightShoulderEntity() {
         return false;
      }
   }
}
