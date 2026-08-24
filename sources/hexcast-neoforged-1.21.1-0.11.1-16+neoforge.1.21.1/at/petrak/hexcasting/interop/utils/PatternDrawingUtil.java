package at.petrak.hexcasting.interop.utils;

import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.client.render.RenderLib;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;

public final class PatternDrawingUtil {
   public static void drawPattern(
      GuiGraphics graphics,
      int x,
      int y,
      List<PatternEntry> patterns,
      List<Vec2> dots,
      boolean strokeOrder,
      int outer,
      int innerLight,
      int innerDark,
      int dotColor
   ) {
      PoseStack poseStack = graphics.pose();
      poseStack.pushPose();
      poseStack.translate(x, y, 1.0F);
      Matrix4f mat = poseStack.last().pose();
      ShaderInstance prevShader = RenderSystem.getShader();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.disableCull();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

      for (PatternEntry pat : patterns) {
         RenderLib.drawLineSeq(mat, pat.zappyPoints(), 5.0F, 0.0F, outer, outer);
         RenderLib.drawLineSeq(mat, pat.zappyPoints(), 2.0F, 0.0F, strokeOrder ? innerDark : innerLight, innerLight);
         if (strokeOrder) {
            RenderLib.drawSpot(mat, pat.zappyPoints().get(0), 2.5F, 1.0F, 0.1F, 0.15F, 0.6F);
         }
      }

      float dotR = ARGB32.red(dotColor) / 255.0F;
      float dotG = ARGB32.green(dotColor) / 255.0F;
      float dotB = ARGB32.blue(dotColor) / 255.0F;
      float dotA = ARGB32.alpha(dotColor) / 255.0F;

      for (Vec2 dot : dots) {
         RenderLib.drawSpot(mat, dot, 1.5F, dotR, dotG, dotB, dotA);
      }

      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(() -> prevShader);
      RenderSystem.enableCull();
      poseStack.popPose();
   }

   public static PatternDrawingUtil.PatternRenderingData loadPatterns(List<Pair<HexPattern, HexCoord>> patterns, float readabilityOffset, float lastLineLenProp) {
      ArrayList<PatternEntry> patternEntries = new ArrayList<>(patterns.size());
      int fakeScale = 1;
      ArrayList<Vec2> seenFakePoints = new ArrayList<>();
      HashSet<HexCoord> seenCoords = new HashSet<>();

      for (Pair<HexPattern, HexCoord> pair : patterns) {
         HexPattern pattern = (HexPattern)pair.getFirst();
         HexCoord origin = (HexCoord)pair.getSecond();

         for (HexCoord pos : pattern.positions(origin)) {
            Vec2 px = HexUtils.coordToPx(pos, fakeScale, Vec2.ZERO);
            seenFakePoints.add(px);
         }

         patternEntries.add(new PatternEntry(pattern, origin, new ArrayList<>()));
         seenCoords.addAll(pattern.positions(origin));
      }

      Vec2 fakeCom = HexUtils.findCenter(seenFakePoints);
      float maxDx = -1.0F;
      float maxDy = -1.0F;

      for (Vec2 dot : seenFakePoints) {
         float dx = Mth.abs(dot.x - fakeCom.x);
         if (dx > maxDx) {
            maxDx = dx;
         }

         float dy = Mth.abs(dot.y - fakeCom.y);
         if (dy > maxDy) {
            maxDy = dy;
         }
      }

      float hexSize = Math.min(12.0F, Math.min(48.0F / maxDx, 28.0F / maxDy));
      ArrayList<Vec2> seenRealPoints = new ArrayList<>();

      for (PatternEntry pat : patternEntries) {
         for (HexCoord pos : pat.pattern().positions(pat.origin())) {
            Vec2 px = HexUtils.coordToPx(pos, hexSize, Vec2.ZERO);
            seenRealPoints.add(px);
         }
      }

      Vec2 realCom = HexUtils.findCenter(seenRealPoints);

      for (int i = 0; i < patternEntries.size(); i++) {
         PatternEntry pat = patternEntries.get(i);
         Vec2 localOrigin = HexUtils.coordToPx(pat.origin(), hexSize, realCom.negated());
         List<Vec2> points = pat.pattern().toLines(hexSize, localOrigin);
         pat.zappyPoints()
            .addAll(
               RenderLib.makeZappy(points, RenderLib.findDupIndices(pat.pattern().positions()), 10, 0.8F, 0.0F, 0.0F, readabilityOffset, lastLineLenProp, i)
            );
      }

      List<Vec2> pathfinderDots = seenCoords.stream().map(coord -> HexUtils.coordToPx(coord, hexSize, realCom.negated())).toList();
      return new PatternDrawingUtil.PatternRenderingData(patternEntries, pathfinderDots, hexSize);
   }

   public record PatternRenderingData(List<PatternEntry> patterns, List<Vec2> pathfinderDots, float hexSize) {
   }
}
