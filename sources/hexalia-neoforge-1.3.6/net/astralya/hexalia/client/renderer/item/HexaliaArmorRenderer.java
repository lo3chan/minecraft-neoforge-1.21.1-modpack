package net.astralya.hexalia.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.astralya.hexalia.item.custom.armor.HexaliaGeoArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HexaliaArmorRenderer extends GeoArmorRenderer<HexaliaGeoArmorItem> {
   public HexaliaArmorRenderer(HexaliaGeoArmorItem item) {
      super(new HexaliaArmorModel(item));
   }

   public static GeoRenderProvider createRenderProvider(HexaliaGeoArmorItem item) {
      return new GeoRenderProvider() {
         private GeoArmorRenderer<?> renderer;
         private HumanoidModel<?> firstPersonHiddenRenderer;

         public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack stack, EquipmentSlot slot, HumanoidModel<T> original) {
            if (HexaliaArmorRenderer.isLocalFirstPerson(livingEntity)) {
               if (this.firstPersonHiddenRenderer == null) {
                  this.firstPersonHiddenRenderer = HexaliaArmorRenderer.createFirstPersonHiddenRenderer();
               }

               return this.firstPersonHiddenRenderer;
            } else {
               if (this.renderer == null) {
                  this.renderer = new HexaliaArmorRenderer(item);
               }

               return this.renderer;
            }
         }
      };
   }

   private static boolean isLocalFirstPerson(LivingEntity livingEntity) {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft.player != null
         && minecraft.player == livingEntity
         && minecraft.getCameraEntity() == livingEntity
         && minecraft.options.getCameraType().isFirstPerson();
   }

   private static HumanoidModel<?> createFirstPersonHiddenRenderer() {
      return new HumanoidModel<LivingEntity>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)) {
         public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
         }
      };
   }
}
