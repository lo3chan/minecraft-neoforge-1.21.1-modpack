package dev.tr7zw.notenoughanimations.renderlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.transition.mc.ItemUtil;
import dev.tr7zw.transition.mc.MathUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SwordRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   private boolean lazyInit = true;
   private static Set<Item> items = new HashSet<>();
   private boolean disabled = false;

   public SwordRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
      super(renderLayerParent);
   }

   public static void update(Player player) {
      PlayerData data = (PlayerData)player;
      if (items.contains(player.getMainHandItem().getItem())) {
         data.setSideSword(player.getMainHandItem());
      }

      if (items.contains(player.getOffhandItem().getItem())) {
         data.setSideSword(player.getOffhandItem());
      }
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int light,
      AbstractClientPlayer player,
      float paramFloat1,
      float paramFloat2,
      float paramFloat3,
      float paramFloat4,
      float paramFloat5,
      float paramFloat6
   ) {
      if (!this.disabled && player != null) {
         if (this.lazyInit) {
            this.lazyInit = false;
            this.init();
         }

         if (NEABaseMod.config.showLastUsedSword) {
            if (!player.isInvisible() && !player.isSleeping()) {
               if (player instanceof PlayerData) {
                  if (!player.isPassenger()) {
                     PlayerData data = (PlayerData)player;
                     ItemStack itemStack = data.getSideSword();
                     if (!itemStack.isEmpty()) {
                        if (player.getMainHandItem() != itemStack && player.getOffhandItem() != itemStack) {
                           poseStack.pushPose();
                           ((PlayerModel)this.getParentModel()).body.translateAndRotate(poseStack);
                           boolean lefthanded = player.getMainArm() == HumanoidArm.LEFT;
                           boolean wearingArmor = !player.getItemBySlot(EquipmentSlot.LEGS).isEmpty();
                           if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && player.getItemBySlot(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
                              wearingArmor = true;
                           }

                           double offsetX = wearingArmor ? 0.3 : 0.28;
                           float swordRotation = -80.0F;
                           if (lefthanded) {
                              offsetX *= -1.0;
                           }

                           poseStack.translate(offsetX, 0.85, 0.25);
                           poseStack.mulPose(MathUtil.XP.rotationDegrees(swordRotation));
                           poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0F));
                           Minecraft.getInstance()
                              .getEntityRenderDispatcher()
                              .getItemInHandRenderer()
                              .renderItem(
                                 player,
                                 itemStack,
                                 lefthanded ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                                 lefthanded,
                                 poseStack,
                                 multiBufferSource,
                                 light
                              );
                           poseStack.popPose();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void init() {
      for (String itemKey : NEABaseMod.config.sheathSwords) {
         if (itemKey.contains(":")) {
            Item item = ItemUtil.getItem(GeneralUtil.getResourceLocation(itemKey.split(":")[0], itemKey.split(":")[1]));
            if (item != Items.AIR) {
               items.add(item);
            }
         }
      }

      try {
         Class.forName("net.backslot.BackSlotMain");
         this.disabled = true;
      } catch (Throwable var4) {
      }
   }
}
