package net.astralya.hexalia.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.astralya.hexalia.block.custom.SmallCauldronBlock;
import net.astralya.hexalia.block.entity.custom.SmallCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

public final class SmallCauldronBlockEntityRenderer implements BlockEntityRenderer<SmallCauldronBlockEntity> {
   private static final ResourceLocation WATER_STILL = ResourceLocation.withDefaultNamespace("block/water_still");
   private static final float LIQUID_MIN_Y = 0.375625F;
   private static final float LIQUID_MAX_Y = 0.9365F;
   private static final float LIQUID_MIN_X = 0.125F;
   private static final float LIQUID_MAX_X = 0.875F;
   private static final float LIQUID_MIN_Z = 0.1875F;
   private static final float LIQUID_MAX_Z = 0.8125F;
   private static final float INGREDIENT_FLOOR_Y = 0.38125F;
   private static final float INGREDIENT_SCALE = 0.45F;
   private static final float CENTER_X = 0.5F;
   private static final float CENTER_Z = 0.5F;
   private static final float MARKER_Y = 0.96875F;
   private static final float[][] MARKER_POSITIONS = new float[][]{
      {0.3125F, 0.96875F, 0.5F}, {0.6875F, 0.96875F, 0.5F}, {0.5F, 0.96875F, 0.34375F}, {0.5F, 0.96875F, 0.65625F}
   };
   private final ItemRenderer itemRenderer;
   private final Map<SmallCauldronBlockEntity, SmallCauldronBlockEntityRenderer.SwirlState> swirlStates = new WeakHashMap<>();

   public SmallCauldronBlockEntityRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(
      SmallCauldronBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      Level level = blockEntity.getLevel();
      if (level != null) {
         poseStack.pushPose();
         Direction facing = (Direction)blockEntity.getBlockState().getValue(SmallCauldronBlock.FACING);
         poseStack.translate(0.5F, 0.0F, 0.5F);
         poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
         poseStack.translate(-0.5F, 0.0F, -0.5F);
         int light = LevelRenderer.getLightColor(level, blockEntity.getBlockPos().above());
         this.renderLiquid(blockEntity, poseStack, buffer, light);
         this.renderIngredients(blockEntity, level, partialTick, poseStack, buffer, light);
         poseStack.popPose();
      }
   }

   private void renderIngredients(
      SmallCauldronBlockEntity blockEntity, Level level, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight
   ) {
      List<ItemStack> stacks = blockEntity.getIngredientsForRender();
      float fill = blockEntity.getVisualLiquidFill01();
      float baseY = 0.38125F;
      if (fill > 0.0F) {
         float liquidY = Mth.lerp(Mth.clamp(fill, 0.0F, 1.0F), 0.375625F, 0.9365F);
         baseY = Math.max(0.38125F, liquidY + 0.034375F);
      }

      float swirl = this.computeSwirlAngleRad(blockEntity, level, partialTick);
      long baseSeed = blockEntity.getBlockPos().asLong();
      int visibleIndex = 0;

      for (ItemStack stack : stacks) {
         if (!stack.isEmpty() && visibleIndex < MARKER_POSITIONS.length) {
            float[] marker = MARKER_POSITIONS[visibleIndex];
            long seed = baseSeed + visibleIndex * 31L;
            float phase = (float)(seed & 1023L) / 1023.0F * 6.2831855F;
            float angle = swirl + phase * 0.15F;
            float offsetX = marker[0] - 0.5F;
            float offsetZ = marker[2] - 0.5F;
            float x = 0.5F + offsetX * Mth.cos(angle) - offsetZ * Mth.sin(angle);
            float z = 0.5F + offsetX * Mth.sin(angle) + offsetZ * Mth.cos(angle);
            float time = ((float)level.getGameTime() + partialTick) * 0.05F + phase;
            float y = baseY + Mth.sin(time * 0.9F) * 0.002F;
            poseStack.pushPose();
            poseStack.translate(Mth.clamp(x, 0.203125F, 0.796875F), y, Mth.clamp(z, 0.265625F, 0.734375F));
            poseStack.mulPose(Axis.YP.rotationDegrees(swirl * 57.295776F + (float)(seed >> 11 & 255L) / 255.0F * 35.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(0.45F, 0.45F, 0.45F);
            this.itemRenderer
               .renderStatic(
                  stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, level, (int)(baseSeed ^ visibleIndex * 17L)
               );
            poseStack.popPose();
            visibleIndex++;
         }
      }
   }

   private void renderLiquid(SmallCauldronBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      float fill = blockEntity.getVisualLiquidFill01();
      if (!(fill <= 0.0F)) {
         int rgb = blockEntity.getVisualLiquidColor();
         TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(WATER_STILL);
         VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));
         Matrix4f matrix = poseStack.last().pose();
         float height = Mth.lerp(Mth.clamp(fill, 0.0F, 1.0F), 0.375625F, 0.9365F);
         int red = rgb >> 16 & 0xFF;
         int green = rgb >> 8 & 0xFF;
         int blue = rgb & 0xFF;
         int alpha = 217;
         consumer.addVertex(matrix, 0.125F, height, 0.1875F)
            .setColor(red, green, blue, alpha)
            .setUv(sprite.getU0(), sprite.getV0())
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(0.0F, 1.0F, 0.0F);
         consumer.addVertex(matrix, 0.125F, height, 0.8125F)
            .setColor(red, green, blue, alpha)
            .setUv(sprite.getU0(), sprite.getV1())
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(0.0F, 1.0F, 0.0F);
         consumer.addVertex(matrix, 0.875F, height, 0.8125F)
            .setColor(red, green, blue, alpha)
            .setUv(sprite.getU1(), sprite.getV1())
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(0.0F, 1.0F, 0.0F);
         consumer.addVertex(matrix, 0.875F, height, 0.1875F)
            .setColor(red, green, blue, alpha)
            .setUv(sprite.getU1(), sprite.getV0())
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(0.0F, 1.0F, 0.0F);
      }
   }

   private float computeSwirlAngleRad(SmallCauldronBlockEntity blockEntity, Level level, float partialTick) {
      SmallCauldronBlockEntityRenderer.SwirlState state = this.swirlStates
         .computeIfAbsent(blockEntity, key -> new SmallCauldronBlockEntityRenderer.SwirlState());
      float progress = blockEntity.getStirProgress(partialTick);
      float eased = progress * progress * (3.0F - 2.0F * progress);
      float kick = progress > 0.0F ? Mth.lerp(eased, 0.015F, 0.06F) : 0.0F;
      state.velocity = Math.min(0.12F, (state.velocity + kick) * 0.985F);
      if (Math.abs(state.velocity) < 6.0E-4F) {
         state.velocity = 0.0F;
      }

      long now = level.getGameTime();
      state.angle = state.angle + state.velocity * (float)Math.max(1L, state.lastTick == 0L ? 1L : now - state.lastTick);
      state.lastTick = now;
      state.angle %= 6.2831855F;
      return state.angle < 0.0F ? state.angle + 6.2831855F : state.angle;
   }

   private static final class SwirlState {
      private float angle;
      private float velocity;
      private long lastTick;
   }
}
