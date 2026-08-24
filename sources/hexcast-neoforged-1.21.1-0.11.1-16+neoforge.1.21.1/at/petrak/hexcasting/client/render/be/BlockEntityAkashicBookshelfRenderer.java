package at.petrak.hexcasting.client.render.be;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.client.render.PatternTextureManager;
import at.petrak.hexcasting.client.render.RenderLib;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

public class BlockEntityAkashicBookshelfRenderer implements BlockEntityRenderer<BlockEntityAkashicBookshelf> {
   public BlockEntityAkashicBookshelfRenderer(Context ctx) {
   }

   public void render(BlockEntityAkashicBookshelf tile, float pPartialTick, PoseStack ps, MultiBufferSource buffer, int light, int overlay) {
      HexPattern pattern = tile.getPattern();
      if (pattern != null) {
         BlockState bs = tile.getBlockState();
         if (PatternTextureManager.useTextures) {
            PatternTextureManager.renderPatternForAkashicBookshelf(tile, pattern, ps, buffer, light, bs);
         } else {
            ShaderInstance oldShader = RenderSystem.getShader();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.enableDepthTest();
            ps.pushPose();
            ps.translate(0.5, 0.5, 0.5);
            int quarters = -((Direction)bs.getValue(BlockAkashicBookshelf.FACING)).get2DDataValue() % 4;
            ps.mulPose(Axis.YP.rotation(1.5707964F * quarters));
            ps.mulPose(Axis.ZP.rotation(3.1415927F));
            ps.translate(0.0, 0.0, 0.5);
            ps.scale(0.0625F, 0.0625F, 0.0625F);
            ps.translate(0.0, 0.0, 0.01);
            Vec2 com1 = pattern.getCenter(1.0F);
            List<Vec2> lines1 = pattern.toLines(1.0F, Vec2.ZERO);
            float maxDx = -1.0F;
            float maxDy = -1.0F;

            for (Vec2 dot : lines1) {
               float dx = Mth.abs(dot.x - com1.x);
               if (dx > maxDx) {
                  maxDx = dx;
               }

               float dy = Mth.abs(dot.y - com1.y);
               if (dy > maxDy) {
                  maxDy = dy;
               }
            }

            float scale = Math.min(3.8F, Math.min(6.4F / maxDx, 6.4F / maxDy));
            Vec2 com2 = pattern.getCenter(scale);
            List<Vec2> lines2 = pattern.toLines(scale, com2.negated());

            for (int j = 0; j < lines2.size(); j++) {
               Vec2 v = lines2.get(j);
               lines2.set(j, new Vec2(-v.x, v.y));
            }

            int stupidHash = tile.getBlockPos().hashCode();
            List<Vec2> zappy = RenderLib.makeZappy(lines2, RenderLib.findDupIndices(pattern.positions()), 10, 0.5F, 0.0F, 0.0F, 0.0F, 1.0F, stupidHash);
            int outer = -2963256;
            int inner = -936236237;
            RenderLib.drawLineSeq(ps.last().pose(), zappy, 1.0F, 0.0F, outer, outer);
            RenderLib.drawLineSeq(ps.last().pose(), zappy, 0.4F, 0.01F, inner, inner);
            ps.popPose();
            RenderSystem.setShader(() -> oldShader);
         }
      }
   }
}
