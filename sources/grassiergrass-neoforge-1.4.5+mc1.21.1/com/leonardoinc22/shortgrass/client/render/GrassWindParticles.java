package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

final class GrassWindParticles {
   private static final ResourceLocation BLADE_SPRITE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "block/wind_blade");
   private static final int SPAWN_INTERVAL_TICKS = 3;
   private static final int MAX_ATTEMPTS = 8;
   private static final int HORIZONTAL_RADIUS = 16;
   private static final float BLADE_SIZE = 0.2F;
   private static final float MIN_WIND_SPEED = 20.0F;
   private static final float GLIDE_SPEED = 0.32F;
   private static final RandomSource RANDOM = RandomSource.create();
   private static long lastSpawnTick;

   private GrassWindParticles() {
   }

   static void tick(ClientLevel level, Vec3 cameraPos, long gameTime) {
      if (GrassConfig.bladeParticles) {
         if (gameTime < lastSpawnTick || gameTime - lastSpawnTick >= 3L) {
            lastSpawnTick = gameTime;
            float windSpeed = GrassConfig.effectiveWindSpeed();
            if (!(windSpeed < 20.0F)) {
               float baseGlide = 0.32F * windSpeed / 100.0F;
               int attempts = Mth.clamp(Mth.ceil(8.0F * windSpeed / 150.0F), 1, 8);
               Minecraft minecraft = Minecraft.getInstance();
               TextureAtlasSprite sprite = (TextureAtlasSprite)minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(BLADE_SPRITE);
               int camX = Mth.floor(cameraPos.x);
               int camZ = Mth.floor(cameraPos.z);
               MutableBlockPos pos = new MutableBlockPos();

               for (int i = 0; i < attempts; i++) {
                  int x = camX + RANDOM.nextInt(33) - 16;
                  int z = camZ + RANDOM.nextInt(33) - 16;
                  int surfaceY = level.getHeight(Types.WORLD_SURFACE, x, z);
                  pos.set(x, surfaceY - 1, z);
                  BlockState state = level.getBlockState(pos);
                  if (GrassGeometry.RENDERS_GRASS.test(state)) {
                     int tint = minecraft.getBlockColors().getColor(state, level, pos, 0);
                     if (tint != -1) {
                        double sx = x + RANDOM.nextDouble();
                        double sz = z + RANDOM.nextDouble();
                        double sy = grassCanopyTopY(state, surfaceY - 1);
                        float glideSpeed = baseGlide * (0.75F + RANDOM.nextFloat() * 0.5F);
                        minecraft.particleEngine.add(new GrassBladeParticle(level, sx, sy, sz, glideSpeed, tint, 0.2F, sprite));
                     }
                  }
               }
            }
         }
      }
   }

   private static double grassCanopyTopY(BlockState state, int blockY) {
      float canopy = GrassGeometry.visualBladeHeight();
      if (state.is(Blocks.GRASS_BLOCK)) {
         return blockY + 1 + canopy;
      } else if (state.is(Blocks.TALL_GRASS)) {
         int baseY = state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? blockY - 1 : blockY;
         return baseY + canopy * 3.0F;
      } else {
         return blockY + canopy;
      }
   }
}
