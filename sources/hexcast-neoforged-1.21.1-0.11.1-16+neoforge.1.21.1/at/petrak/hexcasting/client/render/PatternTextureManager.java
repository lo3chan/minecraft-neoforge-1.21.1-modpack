package at.petrak.hexcasting.client.render;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PatternTextureManager {
   public static boolean useTextures = true;
   public static int repaintIndex = 0;
   public static int resolutionScaler = 4;
   public static int fastRenderScaleFactor = 8;
   public static int resolutionByBlockSize = 128 * resolutionScaler;
   public static int paddingByBlockSize = 16 * resolutionScaler;
   public static int circleRadiusByBlockSize = 2 * resolutionScaler;
   public static int scaleLimit = 4 * resolutionScaler;
   public static int scrollLineWidth = 3 * resolutionScaler;
   public static int otherLineWidth = 4 * resolutionScaler;
   private static final ConcurrentMap<String, ResourceLocation> patternTexturesToAdd = new ConcurrentHashMap<>();
   private static final ExecutorService executor = new ThreadPoolExecutor(0, 16, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
   private static final HashMap<String, ResourceLocation> patternTextures = new HashMap<>();

   public static void setResolutionScaler(int resolutionScaler) {
      PatternTextureManager.resolutionScaler = resolutionScaler;
      resolutionByBlockSize = 128 * resolutionScaler;
      paddingByBlockSize = 16 * resolutionScaler;
      circleRadiusByBlockSize = 2 * resolutionScaler;
      scaleLimit = 4 * resolutionScaler;
      scrollLineWidth = 3 * resolutionScaler;
      otherLineWidth = 4 * resolutionScaler;
   }

   public static String getPointsKey(List<Vec2> zappyPoints) {
      return zappyPoints.stream().map(p -> String.format("(%f,%f)", p.x, p.y)).collect(Collectors.joining(";"));
   }

   public static HexPatternPoints generateHexPatternPoints(HexBlockEntity tile, HexPattern pattern, float flowIrregular) {
      int stupidHash = tile.getBlockPos().hashCode();
      List<Vec2> lines1 = pattern.toLines(1.0F, Vec2.ZERO);
      List<Vec2> zappyPoints = RenderLib.makeZappy(lines1, RenderLib.findDupIndices(pattern.positions()), 10, 0.5F, 0.0F, flowIrregular, 0.0F, 1.0F, stupidHash);
      return new HexPatternPoints(zappyPoints);
   }

   public static void renderPatternForScroll(
      String pointsKey, PoseStack ps, MultiBufferSource bufSource, int light, List<Vec2> zappyPoints, int blockSize, boolean showStrokeOrder
   ) {
      renderPattern(pointsKey, ps, bufSource, light, zappyPoints, blockSize, showStrokeOrder, false, true, false, false, true, -1);
   }

   public static void renderPatternForSlate(BlockEntitySlate tile, HexPattern pattern, PoseStack ps, MultiBufferSource buffer, int light, BlockState bs) {
      if (tile.points == null) {
         tile.points = generateHexPatternPoints(tile, pattern, 0.2F);
      }

      boolean isOnWall = bs.getValue(BlockSlate.ATTACH_FACE) == AttachFace.WALL;
      boolean isOnCeiling = bs.getValue(BlockSlate.ATTACH_FACE) == AttachFace.CEILING;
      int facing = ((Direction)bs.getValue(BlockSlate.FACING)).get2DDataValue();
      renderPatternForBlockEntity(tile.points, ps, buffer, light, isOnWall, isOnCeiling, true, facing);
   }

   public static void renderPatternForAkashicBookshelf(
      BlockEntityAkashicBookshelf tile, HexPattern pattern, PoseStack ps, MultiBufferSource buffer, int light, BlockState bs
   ) {
      if (tile.points == null) {
         tile.points = generateHexPatternPoints(tile, pattern, 0.0F);
      }

      int facing = ((Direction)bs.getValue(BlockAkashicBookshelf.FACING)).get2DDataValue();
      renderPatternForBlockEntity(tile.points, ps, buffer, light, true, false, false, facing);
   }

   public static void renderPatternForBlockEntity(
      HexPatternPoints points, PoseStack ps, MultiBufferSource buffer, int light, boolean isOnWall, boolean isOnCeiling, boolean isSlate, int facing
   ) {
      ShaderInstance oldShader = RenderSystem.getShader();
      ps.pushPose();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      renderPattern(points.pointsKey, ps, buffer, light, points.zappyPoints, 1, false, true, isOnWall, isOnCeiling, isSlate, false, facing);
      ps.popPose();
      RenderSystem.setShader(() -> oldShader);
   }

   public static void renderPattern(
      String pointsKey,
      PoseStack ps,
      MultiBufferSource bufSource,
      int light,
      List<Vec2> zappyPoints,
      int blockSize,
      boolean showStrokeOrder,
      boolean useFullSize,
      boolean isOnWall,
      boolean isOnCeiling,
      boolean isSlate,
      boolean isScroll,
      int facing
   ) {
      ps.pushPose();
      Pose last = ps.last();
      Matrix4f mat = last.pose();
      Matrix3f normal = last.normal();
      float x = blockSize;
      float y = blockSize;
      float z = -0.0725F;
      float nx = 0.0F;
      float ny = 0.0F;
      float nz = 0.0F;
      if (isOnWall) {
         if (isScroll) {
            ps.translate(-blockSize / 2.0F, -blockSize / 2.0F, 0.03125F);
            nz = -1.0F;
         } else {
            ps.mulPose(Axis.ZP.rotationDegrees(180.0F));
            if (isSlate) {
               if (facing == 0) {
                  ps.translate(0.0F, -1.0F, 0.0F);
               }

               if (facing == 1) {
                  ps.translate(-1.0F, -1.0F, 0.0F);
               }

               if (facing == 2) {
                  ps.translate(-1.0F, -1.0F, 1.0F);
               }

               if (facing == 3) {
                  ps.translate(0.0F, -1.0F, 1.0F);
               }
            } else {
               z = -0.01F;
               if (facing == 0) {
                  ps.translate(0.0F, -1.0F, 1.0F);
               }

               if (facing == 1) {
                  ps.translate(0.0F, -1.0F, 0.0F);
               }

               if (facing == 2) {
                  ps.translate(-1.0F, -1.0F, 0.0F);
               }

               if (facing == 3) {
                  ps.translate(-1.0F, -1.0F, 1.0F);
               }
            }

            if (facing == 0) {
               ps.mulPose(Axis.YP.rotationDegrees(180.0F));
            }

            if (facing == 1) {
               ps.mulPose(Axis.YP.rotationDegrees(270.0F));
            }

            if (facing == 3) {
               ps.mulPose(Axis.YP.rotationDegrees(90.0F));
            }

            if (facing == 0 || facing == 2) {
               nz = -1.0F;
            }

            if (facing == 1 || facing == 3) {
               nx = -1.0F;
            }

            ps.translate(0.0F, 0.0F, 0.0F);
         }
      } else {
         if (facing == 0) {
            ps.translate(0.0F, 0.0F, 0.0F);
         }

         if (facing == 1) {
            ps.translate(1.0F, 0.0F, 0.0F);
         }

         if (facing == 2) {
            ps.translate(1.0F, 0.0F, 1.0F);
         }

         if (facing == 3) {
            ps.translate(0.0F, 0.0F, 1.0F);
         }

         ps.mulPose(Axis.YP.rotationDegrees(facing * -90));
         if (isOnCeiling) {
            ps.mulPose(Axis.XP.rotationDegrees(-90.0F));
            ps.translate(0.0F, -1.0F, 1.0F);
         } else {
            ps.mulPose(Axis.XP.rotationDegrees(90.0F));
         }

         nz = -1.0F;
      }

      int lineWidth = otherLineWidth;
      int outerColor = -2963256;
      int innerColor = -936236237;
      if (isScroll) {
         lineWidth = scrollLineWidth;
      }

      ResourceLocation texture = getTexture(
         zappyPoints, pointsKey, blockSize, showStrokeOrder, lineWidth, useFullSize, new Color(innerColor), new Color(outerColor)
      );
      VertexConsumer verts = bufSource.getBuffer(RenderType.entityCutout(texture));
      vertex(mat, normal, light, verts, 0.0F, 0.0F, z, 0.0F, 0.0F, nx, ny, nz);
      vertex(mat, normal, light, verts, 0.0F, y, z, 0.0F, 1.0F, nx, ny, nz);
      vertex(mat, normal, light, verts, x, y, z, 1.0F, 1.0F, nx, ny, nz);
      vertex(mat, normal, light, verts, x, 0.0F, z, 1.0F, 0.0F, nx, ny, nz);
      ps.popPose();
   }

   private static void vertex(
      Matrix4f mat, Matrix3f normal, int light, VertexConsumer verts, float x, float y, float z, float u, float v, float nx, float ny, float nz
   ) {
      verts.addVertex(mat, x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
   }

   public static ResourceLocation getTexture(
      List<Vec2> points, String pointsKey, int blockSize, boolean showsStrokeOrder, float lineWidth, boolean useFullSize, Color innerColor, Color outerColor
   ) {
      if (patternTexturesToAdd.containsKey(pointsKey)) {
         ResourceLocation patternTexture = patternTexturesToAdd.remove(pointsKey);
         ResourceLocation oldPatternTexture = patternTextures.put(pointsKey, patternTexture);
         if (oldPatternTexture != null) {
            Minecraft.getInstance().getTextureManager().getTexture(oldPatternTexture).close();
         }

         return patternTexture;
      } else if (patternTextures.containsKey(pointsKey)) {
         return patternTextures.get(pointsKey);
      } else {
         executor.submit(() -> {
            DynamicTexture slowTexture = createTexture(points, blockSize, showsStrokeOrder, lineWidth, useFullSize, innerColor, outerColor, false);
            Minecraft.getInstance().execute(() -> registerTexture(points, pointsKey, slowTexture, true));
         });
         DynamicTexture fastTexture = createTexture(points, blockSize, showsStrokeOrder, lineWidth, useFullSize, innerColor, outerColor, true);
         return registerTexture(points, pointsKey, fastTexture, false);
      }
   }

   private static DynamicTexture createTexture(
      List<Vec2> points, int blockSize, boolean showsStrokeOrder, float lineWidth, boolean useFullSize, Color innerColor, Color outerColor, boolean fastRender
   ) {
      int resolution = resolutionByBlockSize * blockSize;
      int padding = paddingByBlockSize * blockSize;
      if (fastRender) {
         resolution /= fastRenderScaleFactor;
         padding /= fastRenderScaleFactor;
         lineWidth /= fastRenderScaleFactor;
      }

      double minX = 1.7976931348623157E308;
      double maxX = 5.0E-324;
      double minY = 1.7976931348623157E308;
      double maxY = 5.0E-324;

      for (Vec2 point : points) {
         minX = Math.min(minX, (double)point.x);
         maxX = Math.max(maxX, (double)point.x);
         minY = Math.min(minY, (double)point.y);
         maxY = Math.max(maxY, (double)point.y);
      }

      double rangeX = maxX - minX;
      double rangeY = maxY - minY;
      double scale = Math.min((resolution - 2 * padding) / rangeX, (resolution - 2 * padding) / rangeY);
      double limit = blockSize * scaleLimit;
      if (!useFullSize && scale > limit) {
         scale = limit;
      }

      double offsetX = (resolution - 2 * padding - rangeX * scale) / 2.0;
      double offsetY = (resolution - 2 * padding - rangeY * scale) / 2.0;
      BufferedImage img = new BufferedImage(resolution, resolution, 2);
      Graphics2D g2d = img.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setColor(outerColor);
      g2d.setStroke(new BasicStroke(blockSize * 5.0F / 3.0F * lineWidth, 1, 1));
      drawLines(g2d, points, minX, minY, scale, offsetX, offsetY, padding);
      g2d.setColor(innerColor);
      g2d.setStroke(new BasicStroke(blockSize * 2.0F / 3.0F * lineWidth, 1, 1));
      drawLines(g2d, points, minX, minY, scale, offsetX, offsetY, padding);
      if (showsStrokeOrder) {
         g2d.setColor(new Color(-2655397));
         Tuple<Integer, Integer> point = getTextureCoordinates(points.get(0), minX, minY, scale, offsetX, offsetY, padding);
         int spotRadius = circleRadiusByBlockSize * blockSize;
         drawHexagon(g2d, (Integer)point.getA(), (Integer)point.getB(), spotRadius);
      }

      g2d.dispose();
      NativeImage nativeImage = new NativeImage(img.getWidth(), img.getHeight(), true);

      for (int y = 0; y < img.getHeight(); y++) {
         for (int x = 0; x < img.getWidth(); x++) {
            nativeImage.setPixelRGBA(x, y, img.getRGB(x, y));
         }
      }

      return new DynamicTexture(nativeImage);
   }

   private static ResourceLocation registerTexture(List<Vec2> points, String pointsKey, DynamicTexture dynamicTexture, boolean isSlow) {
      String name = "hex_pattern_texture_" + points.hashCode() + "_" + repaintIndex + "_" + (isSlow ? "slow" : "fast") + ".png";
      ResourceLocation resourceLocation = Minecraft.getInstance().getTextureManager().register(name, dynamicTexture);
      patternTexturesToAdd.put(pointsKey, resourceLocation);
      return resourceLocation;
   }

   private static void drawLines(Graphics2D g2d, List<Vec2> points, double minX, double minY, double scale, double offsetX, double offsetY, int padding) {
      for (int i = 0; i < points.size() - 1; i++) {
         Tuple<Integer, Integer> pointFrom = getTextureCoordinates(points.get(i), minX, minY, scale, offsetX, offsetY, padding);
         Tuple<Integer, Integer> pointTo = getTextureCoordinates(points.get(i + 1), minX, minY, scale, offsetX, offsetY, padding);
         g2d.drawLine((Integer)pointFrom.getA(), (Integer)pointFrom.getB(), (Integer)pointTo.getA(), (Integer)pointTo.getB());
      }
   }

   private static Tuple<Integer, Integer> getTextureCoordinates(Vec2 point, double minX, double minY, double scale, double offsetX, double offsetY, int padding) {
      int x = (int)((point.x - minX) * scale + offsetX) + padding;
      int y = (int)((point.y - minY) * scale + offsetY) + padding;
      return new Tuple(x, y);
   }

   private static void drawHexagon(Graphics2D g2d, int x, int y, int radius) {
      int fracOfCircle = 6;
      Polygon hexagon = new Polygon();

      for (int i = 0; i < fracOfCircle; i++) {
         double theta = (double)i / fracOfCircle * 3.141592653589793 * 2.0;
         int hx = (int)(x + Math.cos(theta) * radius);
         int hy = (int)(y + Math.sin(theta) * radius);
         hexagon.addPoint(hx, hy);
      }

      g2d.fill(hexagon);
   }

   public static void repaint() {
      repaintIndex++;
      patternTexturesToAdd.clear();
      patternTextures.clear();
   }
}
