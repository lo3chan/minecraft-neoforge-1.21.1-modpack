package net.joefoxe.hexerei.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

public class DynamicTextureHandler {
   public static FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");
   public static Map<ResourceLocation, DynamicTextureHandler.DynamicBaseSprite> textures = new HashMap<>();

   public static DynamicTextureHandler.DynamicBaseSprite addNewSprite(ResourceLocation location, BlockState state) {
      TextureAtlasSprite sprite = getFirstSprite(state);
      if (sprite != null && !textures.containsKey(location)) {
         try {
            NativeImage image = sprite.contents().getOriginalImage();
            AnimationMetadataSection metadata = getAnimationMetadata(sprite.contents().name());
            FrameSize frameSize = metadata.calculateFrameSize(image.getWidth(), image.getHeight());
            int scale = 2;
            int width = 3;
            int height = 2;
            Tuple<NativeImage, Map<Direction, Integer>> tuple = createCubeTexture(state, frameSize, sprite, scale, width, height);
            DynamicTextureHandler.DynamicBaseSprite baseSprite = new DynamicTextureHandler.DynamicBaseSprite(
               (NativeImage)tuple.getA(), location, scale, width, height
            );
            textures.put(location, baseSprite);
            Minecraft.getInstance().getTextureManager().register(location, baseSprite);
            return baseSprite;
         } catch (IOException var11) {
         }
      }

      return null;
   }

   public static Tuple<NativeImage, Map<Direction, Integer>> createCubeTexture(
      BlockState state, FrameSize frameSize, TextureAtlasSprite defaultSprite, int texScale, int width, int height
   ) {
      Map<Direction, Integer> tintIndex = new HashMap<>();

      for (Direction dir : Direction.values()) {
         tintIndex.put(dir, -1);
      }

      BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
      frameSize = new FrameSize(32, 32);
      NativeImage newImage = new NativeImage(frameSize.width(), frameSize.height(), true);
      int currentY = 0;
      int scale = (int)(frameSize.width() / 16.0F);
      int rotation = 0;
      TextureAtlasSprite sprite = defaultSprite;
      Direction dir = Direction.UP;
      List<BakedQuad> list = model.getQuads(state, dir, RandomSource.create());
      if (list.size() > 0) {
         sprite = list.get(0).getSprite();
         rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
         tintIndex.put(dir, list.get(0).getTintIndex());
      }

      for (BakedQuad quad : model.getQuads(state, null, RandomSource.create())) {
         if (quad.getDirection() == dir) {
            sprite = quad.getSprite();
            rotation = detectRotation(normalizeUVs(getUVs(quad.getVertices())));
            tintIndex.put(dir, quad.getTintIndex());
         }
      }

      BlockColors blockColors = Minecraft.getInstance().getBlockColors();
      int col = tintIndex.get(dir) == -1 ? -1 : blockColors.getColor(state, null, null, tintIndex.get(dir));
      rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), frameSize), newImage, 0, currentY, scale, width, width, rotation, col);
      rotation = 0;
      sprite = defaultSprite;
      dir = Direction.DOWN;
      list = model.getQuads(state, dir, RandomSource.create());
      if (list.size() > 0) {
         sprite = list.get(0).getSprite();
         rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
         tintIndex.put(dir, list.get(0).getTintIndex());
      }

      for (BakedQuad quadx : model.getQuads(state, null, RandomSource.create())) {
         if (quadx.getDirection() == dir) {
            sprite = quadx.getSprite();
            rotation = detectRotation(normalizeUVs(getUVs(quadx.getVertices())));
            tintIndex.put(dir, quadx.getTintIndex());
         }
      }

      col = tintIndex.get(dir) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(dir));
      rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), frameSize), newImage, width * scale, currentY, scale, width, width, rotation, col);
      currentY += width * scale;
      rotation = 0;
      sprite = defaultSprite;
      list = model.getQuads(state, Direction.NORTH, RandomSource.create());
      if (list.size() > 0) {
         sprite = list.get(0).getSprite();
         rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
         tintIndex.put(Direction.NORTH, list.get(0).getTintIndex());
      }

      col = tintIndex.get(Direction.NORTH) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(Direction.NORTH));
      rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), frameSize), newImage, 0, currentY, scale, height, width, rotation, col);
      rotation = 0;
      sprite = defaultSprite;
      list = model.getQuads(state, Direction.SOUTH, RandomSource.create());
      if (list.size() > 0) {
         sprite = list.get(0).getSprite();
         rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
         tintIndex.put(Direction.SOUTH, list.get(0).getTintIndex());
      }

      col = tintIndex.get(Direction.SOUTH) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(Direction.SOUTH));
      rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), frameSize), newImage, height * scale, currentY, scale, height, width, rotation, col);
      currentY += width * scale;
      rotation = 0;
      sprite = defaultSprite;
      list = model.getQuads(state, Direction.WEST, RandomSource.create());
      if (list.size() > 0) {
         sprite = list.get(0).getSprite();
         rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
         tintIndex.put(Direction.WEST, list.get(0).getTintIndex());
      }

      col = tintIndex.get(Direction.WEST) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(Direction.WEST));
      rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), frameSize), newImage, 0, currentY, scale, height, width, rotation, col);
      rotation = 0;
      sprite = defaultSprite;
      list = model.getQuads(state, Direction.EAST, RandomSource.create());
      if (list.size() > 0) {
         sprite = list.get(0).getSprite();
         rotation = detectRotation(normalizeUVs(getUVs(list.get(0).getVertices())));
         tintIndex.put(Direction.EAST, list.get(0).getTintIndex());
      }

      col = tintIndex.get(Direction.EAST) == -1 ? -1 : Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex.get(Direction.EAST));
      rebuildFace(firstFrameTexture(sprite.contents().getOriginalImage(), frameSize), newImage, height * scale, currentY, scale, height, width, rotation, col);
      return new Tuple(newImage, tintIndex);
   }

   public static Vec2[] getUVs(int[] verts) {
      Vec2[] uvs = new Vec2[4];

      for (int i = 0; i < 4; i++) {
         float u = Float.intBitsToFloat(verts[i * 8 + 4]);
         float v = Float.intBitsToFloat(verts[i * 8 + 5]);
         uvs[i] = new Vec2(u, v);
      }

      return uvs;
   }

   public static Vec2[] normalizeUVs(Vec2[] uvs) {
      float minU = 3.4028235E38F;
      float maxU = 1.0E-45F;
      float minV = 3.4028235E38F;
      float maxV = 1.0E-45F;
      Vec2[] returnUVs = new Vec2[4];

      for (Vec2 uv : uvs) {
         if (uv.x < minU) {
            minU = uv.x;
         }

         if (uv.x > maxU) {
            maxU = uv.x;
         }

         if (uv.y < minV) {
            minV = uv.y;
         }

         if (uv.y > maxV) {
            maxV = uv.y;
         }
      }

      for (int i = 0; i < uvs.length; i++) {
         float normalizedU = (uvs[i].x - minU) / (maxU - minU);
         float normalizedV = (uvs[i].y - minV) / (maxV - minV);
         returnUVs[i] = new Vec2(normalizedU, normalizedV);
      }

      return returnUVs;
   }

   public static int detectRotation(Vec2[] uvs) {
      Vec2[] rotation0 = new Vec2[]{new Vec2(0.0F, 0.0F), new Vec2(0.0F, 1.0F), new Vec2(1.0F, 1.0F), new Vec2(1.0F, 0.0F)};
      Vec2[] rotation90 = new Vec2[]{new Vec2(0.0F, 1.0F), new Vec2(1.0F, 1.0F), new Vec2(1.0F, 0.0F), new Vec2(0.0F, 0.0F)};
      Vec2[] rotation180 = new Vec2[]{new Vec2(1.0F, 1.0F), new Vec2(1.0F, 0.0F), new Vec2(0.0F, 0.0F), new Vec2(0.0F, 1.0F)};
      Vec2[] rotation270 = new Vec2[]{new Vec2(1.0F, 0.0F), new Vec2(0.0F, 0.0F), new Vec2(0.0F, 1.0F), new Vec2(1.0F, 1.0F)};
      if (matchUVs(uvs, rotation0)) {
         return 0;
      } else if (matchUVs(uvs, rotation90)) {
         return 90;
      } else if (matchUVs(uvs, rotation180)) {
         return 180;
      } else {
         return matchUVs(uvs, rotation270) ? 270 : -1;
      }
   }

   private static boolean matchUVs(Vec2[] uvs, Vec2[] rotation) {
      for (int i = 0; i < uvs.length; i++) {
         if (!uvs[i].equals(rotation[i])) {
            return false;
         }
      }

      return true;
   }

   public static void rebuildFace(NativeImage src, NativeImage dest, int destX, int destY, int scale, int sizeX, int sizeY, int rotation, int col) {
      int gridSizeX = Math.round((float)(sizeX * scale));
      int gridSizeY = Math.round((float)(sizeY * scale));
      int totalPoints = gridSizeX * gridSizeY;
      float centerX = gridSizeX / 2.0F;
      float centerY = gridSizeY / 2.0F;
      float[][] points = new float[totalPoints][2];

      for (int y = 0; y < gridSizeY; y++) {
         for (int x = 0; x < gridSizeX; x++) {
            points[y * gridSizeX + x] = new float[]{x, y};
            points[y * gridSizeX + x][0] = points[y * gridSizeX + x][0] / Math.max(1, gridSizeX - 1);
            points[y * gridSizeX + x][1] = points[y * gridSizeX + x][1] / Math.max(1, gridSizeY - 1);
         }
      }

      for (float[] point : points) {
         int writeX = Mth.clamp(destX + Mth.floor(Mth.clamp(point[0] * (sizeX * scale), 0.0F, sizeX * scale - 1)), 0, dest.getWidth() - 1);
         int writeY = Mth.clamp(destY + Mth.floor(Mth.clamp(point[1] * (sizeY * scale), 0.0F, sizeY * scale - 1)), 0, dest.getHeight() - 1);
         float dx = 0.5F - point[0];
         float dy = 0.5F - point[1];
         float dist = (float)Math.sqrt(dx * dx + dy * dy);
         float offsetX = centerX / 2.0F * Math.max(0.0F, 0.8F - dist) * dx;
         float offsetY = centerY / 2.0F * Math.max(0.0F, 0.8F - dist) * dy;
         Vec2 uv1 = new Vec2(point[0], point[1]);
         Vec2 uv2 = rotateUV(uv1, rotation);
         float getXf = Mth.clamp(src.getWidth() * uv2.x + offsetX, 0.0F, src.getWidth() - 1);
         float getYf = Mth.clamp(src.getHeight() * uv2.y + offsetY, 0.0F, src.getHeight() - 1);
         int getX = Mth.clamp(Mth.floor(getYf), 0, src.getWidth() - 1);
         int getY = Mth.clamp(Mth.floor(getXf), 0, src.getHeight() - 1);
         int color = src.getPixelRGBA(getX, getY);
         color = mergeColors(col, color);
         float alpha = (color >> Format.RGBA.alphaOffset() & 0xFF) / 255.0F;
         if (alpha > 0.0F) {
            dest.setPixelRGBA(writeX, writeY, color);
         }
      }
   }

   public static int mergeColors(int col, int color) {
      if (col == -1) {
         return color;
      } else {
         float r = (col >> 16 & 0xFF) / 255.0F;
         float g = (col >> 8 & 0xFF) / 255.0F;
         float b = (col & 0xFF) / 255.0F;
         float newR = (color >> Format.RGBA.redOffset() & 0xFF) / 255.0F * r;
         float newG = (color >> Format.RGBA.greenOffset() & 0xFF) / 255.0F * g;
         float newB = (color >> Format.RGBA.blueOffset() & 0xFF) / 255.0F * b;
         float alpha = (color >> Format.RGBA.alphaOffset() & 0xFF) / 255.0F;
         return HexereiUtil.getColorValueAlpha(newR, newG, newB, alpha);
      }
   }

   public static Vec2 rotateUV(Vec2 uv, int angle) {
      double radians = Math.toRadians(angle);
      float u = uv.x - 0.5F;
      float v = uv.y - 0.5F;
      float rotatedU = (float)(u * Math.cos(radians) - v * Math.sin(radians));
      float rotatedV = (float)(u * Math.sin(radians) + v * Math.cos(radians));
      rotatedU += 0.5F;
      rotatedV += 0.5F;
      return new Vec2(rotatedU, rotatedV);
   }

   public static NativeImage firstFrameTexture(NativeImage originalImage, FrameSize frameSize) {
      int width = frameSize.width();
      int height = frameSize.height();
      NativeImage newImage = new NativeImage(width, height, true);

      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            int color = originalImage.getPixelRGBA(x, y);
            newImage.setPixelRGBA(x, y, color);
         }
      }

      return newImage;
   }

   public static AnimationMetadataSection getAnimationMetadata(ResourceLocation textureLocation) throws IOException {
      Minecraft minecraft = Minecraft.getInstance();
      ResourceManager resourceManager = minecraft.getResourceManager();
      Optional<Resource> optional = resourceManager.getResource(TEXTURE_ID_CONVERTER.idToFile(textureLocation));
      AnimationMetadataSection defaultMetadata = AnimationMetadataSection.EMPTY;
      if (optional.isPresent()) {
         Optional<AnimationMetadataSection> optional2 = optional.get().metadata().getSection(AnimationMetadataSection.SERIALIZER);
         if (optional2.isPresent()) {
            return optional2.get();
         }
      }

      return defaultMetadata;
   }

   public static TextureAtlasSprite getFirstSprite(BlockState blockState) {
      Minecraft minecraft = Minecraft.getInstance();
      BakedModel model = minecraft.getModelManager().getBlockModelShaper().getBlockModel(blockState);

      for (Direction direction : Direction.values()) {
         List<BakedQuad> quads = model.getQuads(blockState, direction, RandomSource.create());
         if (!quads.isEmpty()) {
            return quads.get(0).getSprite();
         }
      }

      List<BakedQuad> unculledQuads = model.getQuads(blockState, null, RandomSource.create());
      return !unculledQuads.isEmpty() ? unculledQuads.get(0).getSprite() : null;
   }

   public static BakedQuad getFirstQuad(BlockState blockState) {
      Minecraft minecraft = Minecraft.getInstance();
      BakedModel model = minecraft.getModelManager().getBlockModelShaper().getBlockModel(blockState);

      for (Direction direction : Direction.values()) {
         List<BakedQuad> quads = model.getQuads(blockState, direction, RandomSource.create());
         if (!quads.isEmpty()) {
            return quads.get(0);
         }
      }

      List<BakedQuad> unculledQuads = model.getQuads(blockState, null, RandomSource.create());
      return !unculledQuads.isEmpty() ? unculledQuads.get(0) : null;
   }

   public static class DynamicBaseSprite extends DynamicTexture {
      public ResourceLocation location;
      public float scale;
      public int width;
      public int height;

      public DynamicBaseSprite(NativeImage image, ResourceLocation location, float scale, int width, int height) {
         super(image);
         this.location = location;
         this.scale = scale;
         this.width = width;
         this.height = height;
      }
   }
}
