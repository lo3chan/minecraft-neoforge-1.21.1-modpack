package dev.tr7zw.notenoughanimations.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.transition.mc.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;

public class MapRenderer {
   private static final RenderType MAP_BACKGROUND = RenderType.text(GeneralUtil.getResourceLocation("textures/map/map_background.png"));
   private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.text(
      GeneralUtil.getResourceLocation("textures/map/map_background_checkerboard.png")
   );

   public static void renderFirstPersonMap(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, boolean small, boolean leftHanded) {
      Minecraft client = Minecraft.getInstance();
      if (small) {
         matrices.mulPose(MathUtil.YP.rotationDegrees(160.0F));
         matrices.mulPose(MathUtil.ZP.rotationDegrees(180.0F));
         matrices.scale(0.38F, 0.38F, 0.38F);
         matrices.translate(-0.1, -1.2, 0.0);
         matrices.scale(0.0098125F, 0.0098125F, 0.0098125F);
      } else {
         if (leftHanded) {
            matrices.mulPose(MathUtil.YP.rotationDegrees(154.5F));
            matrices.mulPose(MathUtil.ZP.rotationDegrees(166.5F));
            matrices.scale(0.38F, 0.38F, 0.38F);
            matrices.translate(0.585, -1.225, 0.15);
         } else {
            matrices.mulPose(MathUtil.YP.rotationDegrees(155.0F));
            matrices.mulPose(MathUtil.ZP.rotationDegrees(213.5F));
            matrices.scale(0.38F, 0.38F, 0.38F);
            matrices.translate(-0.955, -1.8, 0.0);
         }

         matrices.scale(0.0138125F, 0.0138125F, 0.0138125F);
      }

      MapId mapid = (MapId)stack.get(DataComponents.MAP_ID);
      MapItemSavedData mapState = MapItem.getSavedData(stack, client.level);
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
      Matrix4f matrix4f = matrices.last().pose();
      addVertex(vertexConsumer, matrix4f, -7.0F, 135.0F, 0.0F, 0.0F, 1.0F, light);
      addVertex(vertexConsumer, matrix4f, 135.0F, 135.0F, 0.0F, 1.0F, 1.0F, light);
      addVertex(vertexConsumer, matrix4f, 135.0F, -7.0F, 0.0F, 1.0F, 0.0F, light);
      addVertex(vertexConsumer, matrix4f, -7.0F, -7.0F, 0.0F, 0.0F, 0.0F, light);
      vertexConsumer = vertexConsumers.getBuffer(MAP_BACKGROUND);
      addVertex(vertexConsumer, matrix4f, -7.0F, -7.0F, 0.0F, 0.0F, 0.0F, light);
      addVertex(vertexConsumer, matrix4f, 135.0F, -7.0F, 0.0F, 1.0F, 0.0F, light);
      addVertex(vertexConsumer, matrix4f, 135.0F, 135.0F, 0.0F, 1.0F, 1.0F, light);
      addVertex(vertexConsumer, matrix4f, -7.0F, 135.0F, 0.0F, 0.0F, 1.0F, light);
      if (mapState != null) {
         client.gameRenderer.getMapRenderer().render(matrices, vertexConsumers, mapid, mapState, false, light);
      }
   }

   public static void addVertex(VertexConsumer cons, Matrix4f matrix4f, float x, float y, float z, float u, float v, int lightmapUV) {
      cons.addVertex(matrix4f, x, y, z).setColor(-1).setUv(u, v).setLight(lightmapUV);
   }
}
