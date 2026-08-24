package vazkii.psi.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import vazkii.psi.client.core.handler.ClientTickHandler;
import vazkii.psi.client.gui.GuiProgrammer;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.tile.TileProgrammer;

public class RenderTileProgrammer implements BlockEntityRenderer<TileProgrammer> {
   public RenderTileProgrammer(Context ctx) {
   }

   public void render(TileProgrammer te, float partialticks, @NotNull PoseStack ms, @NotNull MultiBufferSource buffers, int worldLight, int overlay) {
      if (te.isEnabled()) {
         ms.pushPose();
         int light = Psi.magical ? worldLight : 15728880;
         ms.translate(0.0F, 1.62F, 0.0F);
         ms.mulPose(Axis.ZP.rotationDegrees(180.0F));
         ms.mulPose(Axis.YP.rotationDegrees(-90.0F));
         float rot = 90.0F;
         BlockState state = te.getBlockState();
         Direction facing = (Direction)state.getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
         switch (facing) {
            case SOUTH:
               rot = -90.0F;
               break;
            case EAST:
               rot = 180.0F;
               break;
            case WEST:
               rot = 0.0F;
         }

         ms.translate(0.5F, 0.0F, 0.5F);
         ms.mulPose(Axis.YP.rotationDegrees(rot));
         ms.translate(-0.5F, 0.0F, -0.5F);
         float f = 0.0033333334F;
         ms.scale(f, f, -f);
         if (Psi.magical) {
            ms.mulPose(Axis.XP.rotationDegrees(90.0F));
            ms.translate(70.0, -220.0, -100.0 + Math.sin(ClientTickHandler.total / 50.0F) * 10.0);
            ms.mulPose(Axis.XP.rotationDegrees(-16.0F + (float)Math.cos(ClientTickHandler.total / 100.0F) * 10.0F));
         } else {
            ms.translate(70.0F, 0.0F, -200.0F);
         }

         te.spell.draw(ms, buffers, light);
         ms.pushPose();
         ms.translate(0.0F, 0.0F, -0.01F);
         VertexConsumer buffer = buffers.getBuffer(GuiProgrammer.LAYER);
         float x = -7.0F;
         float y = -7.0F;
         float width = 174.0F;
         float height = 184.0F;
         float u = 0.0F;
         float v = 0.0F;
         float rescale = 0.00390625F;
         float a = Psi.magical ? 1.0F : 0.5F;
         Matrix4f mat = ms.last().pose();
         buffer.addVertex(mat, x, y + height, 0.0F).setColor(1.0F, 1.0F, 1.0F, a).setUv(u * rescale, (v + height) * rescale).setLight(light);
         buffer.addVertex(mat, x + width, y + height, 0.0F).setColor(1.0F, 1.0F, 1.0F, a).setUv((u + width) * rescale, (v + height) * rescale).setLight(light);
         buffer.addVertex(mat, x + width, y, 0.0F).setColor(1.0F, 1.0F, 1.0F, a).setUv((u + width) * rescale, v * rescale).setLight(light);
         buffer.addVertex(mat, x, y, 0.0F).setColor(1.0F, 1.0F, 1.0F, a).setUv(u * rescale, v * rescale).setLight(light);
         ms.popPose();
         int color = Psi.magical ? 0 : 16777215;
         Minecraft mc = Minecraft.getInstance();
         mc.font.drawInBatch(I18n.get("psimisc.name", new Object[0]), 0.0F, 164.0F, color, false, ms.last().pose(), buffers, DisplayMode.NORMAL, 0, 15728880);
         mc.font.drawInBatch(te.spell.name, 38.0F, 164.0F, color, false, ms.last().pose(), buffers, DisplayMode.NORMAL, 0, 15728880);
         ms.popPose();
      }
   }
}
