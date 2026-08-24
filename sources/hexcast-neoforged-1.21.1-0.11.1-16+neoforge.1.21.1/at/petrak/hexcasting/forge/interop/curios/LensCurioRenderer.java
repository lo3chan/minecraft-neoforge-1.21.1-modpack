package at.petrak.hexcasting.forge.interop.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class LensCurioRenderer implements ICurioRenderer {
   public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("hexcasting", "lens"), "lens");
   private final HumanoidModel<LivingEntity> model;

   public LensCurioRenderer(ModelPart part) {
      this.model = new HumanoidModel(part);
   }

   public <T extends LivingEntity, M extends EntityModel<T>> void render(
      ItemStack stack,
      SlotContext slotContext,
      PoseStack matrixStack,
      RenderLayerParent<T, M> renderLayerParent,
      MultiBufferSource renderTypeBuffer,
      int light,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      this.model.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      this.model.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);
      ICurioRenderer.followHeadRotations(slotContext.entity(), new ModelPart[]{this.model.head});
      matrixStack.pushPose();
      matrixStack.translate(this.model.head.x / 16.0, this.model.head.y / 16.0, this.model.head.z / 16.0);
      matrixStack.mulPose(Axis.YP.rotation(this.model.head.yRot));
      matrixStack.mulPose(Axis.XP.rotation(this.model.head.xRot));
      matrixStack.translate(0.0, -0.25, 0.0);
      matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      matrixStack.scale(0.625F, 0.625F, 0.625F);
      Minecraft instance = Minecraft.getInstance();
      instance.getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, instance.level, 0);
      matrixStack.popPose();
   }
}
