package at.petrak.hexcasting.client.render.be;

import at.petrak.hexcasting.client.render.PatternTextureManager;
import at.petrak.hexcasting.client.render.RenderLib;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec2;

public class BlockEntitySlateRenderer implements BlockEntityRenderer<BlockEntitySlate> {
   public BlockEntitySlateRenderer(Context ctx) {
   }

   public void render(BlockEntitySlate tile, float pPartialTick, PoseStack ps, MultiBufferSource buffer, int light, int overlay) {
      if (tile.pattern != null) {
         BlockState bs = tile.getBlockState();
         if (PatternTextureManager.useTextures && !(Boolean)bs.getValue(BlockSlate.ENERGIZED)) {
            PatternTextureManager.renderPatternForSlate(tile, tile.pattern, ps, buffer, light, bs);
         } else {
            ShaderInstance oldShader = RenderSystem.getShader();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.enableDepthTest();
            ps.pushPose();
            ps.translate(0.5, 0.5, 0.5);
            AttachFace attchFace = (AttachFace)bs.getValue(BlockSlate.ATTACH_FACE);
            if (attchFace == AttachFace.WALL) {
               int quarters = -((Direction)bs.getValue(BlockSlate.FACING)).get2DDataValue() % 4;
               ps.mulPose(Axis.YP.rotation(1.5707964F * quarters));
               ps.mulPose(Axis.ZP.rotation(3.1415927F));
            } else {
               int neg = attchFace == AttachFace.FLOOR ? -1 : 1;
               ps.mulPose(Axis.XP.rotation(neg * 1.5707964F));
               int quarters = (((Direction)bs.getValue(BlockSlate.FACING)).get2DDataValue() + 2) % 4;
               ps.mulPose(Axis.ZP.rotation(neg * 1.5707964F * quarters));
            }

            int resolution = 16;
            int padding = resolution * PatternTextureManager.paddingByBlockSize / PatternTextureManager.resolutionByBlockSize;
            ps.translate(0.0, 0.0, -0.5);
            ps.scale(1.0F / resolution, 1.0F / resolution, 1.0F / resolution);
            ps.translate(0.0, 0.0, 1.01);
            Boolean isLit = (Boolean)bs.getValue(BlockSlate.ENERGIZED);
            float variance = isLit ? 2.5F : 0.5F;
            float speed = isLit ? 0.1F : 0.0F;
            List<Vec2> lines1 = tile.pattern.toLines(1.0F, Vec2.ZERO);
            int stupidHash = tile.getBlockPos().hashCode();
            List<Vec2> zappyPattern = RenderLib.makeZappy(
               lines1, RenderLib.findDupIndices(tile.pattern.positions()), 10, variance, speed, 0.2F, 0.0F, 1.0F, stupidHash
            );
            List<Vec2> zappyPatternSpace = RenderLib.makeZappy(
               lines1, RenderLib.findDupIndices(tile.pattern.positions()), 10, 0.5F, 0.0F, 0.2F, 0.0F, 1.0F, stupidHash
            );
            double minX = 1.7976931348623157E308;
            double maxX = 5.0E-324;
            double minY = 1.7976931348623157E308;
            double maxY = 5.0E-324;

            for (Vec2 point : zappyPatternSpace) {
               minX = Math.min(minX, (double)point.x);
               maxX = Math.max(maxX, (double)point.x);
               minY = Math.min(minY, (double)point.y);
               maxY = Math.max(maxY, (double)point.y);
            }

            double rangeX = maxX - minX;
            double rangeY = maxY - minY;
            double scale = Math.min((resolution - 2 * padding) / rangeX, (resolution - 2 * padding) / rangeY);
            double offsetX = (-2 * padding - rangeX * scale) / 2.0;
            double offsetY = (-2 * padding - rangeY * scale) / 2.0;
            ArrayList<Vec2> zappyRenderSpace = new ArrayList<>();

            for (Vec2 point : zappyPattern) {
               zappyRenderSpace.add(new Vec2((float)((point.x - minX) * scale + offsetX + padding), (float)((point.y - minY) * scale + offsetY + padding)));
            }

            for (int i = 0; i < zappyRenderSpace.size(); i++) {
               Vec2 v = zappyRenderSpace.get(i);
               zappyRenderSpace.set(i, new Vec2(-v.x, v.y));
            }

            int outer = isLit ? -10172161 : -2963256;
            int inner = isLit ? RenderLib.screenCol(outer) : -936236237;
            RenderLib.drawLineSeq(ps.last().pose(), zappyRenderSpace, 1.0F, 0.0F, outer, outer);
            RenderLib.drawLineSeq(ps.last().pose(), zappyRenderSpace, 0.4F, 0.01F, inner, inner);
            ps.popPose();
            RenderSystem.setShader(() -> oldShader);
         }
      }
   }
}
