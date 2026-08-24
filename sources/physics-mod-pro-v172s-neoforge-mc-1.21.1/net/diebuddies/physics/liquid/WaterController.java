package net.diebuddies.physics.liquid;

import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;

public class WaterController implements LiquidController {
   private BlockPos pos;
   private AABB aabb;
   private int density;
   private boolean continous;
   private int spawnAmount;
   private int spawnCorner;

   public WaterController(BlockPos pos, int density, boolean continous, int spawnAmount) {
      this.density = density;
      this.continous = continous;
      this.spawnAmount = spawnAmount;
      this.pos = pos;
      this.aabb = new AABB(pos);
   }

   @Override
   public void init(PhysicsWorld world, Liquid liquid) {
      liquid.blockPos = this.pos;
      liquid.density = this.density;
      liquid.damping = 0.0F;
      liquid.range = 1.5;
      liquid.sourceAlive = this.continous;
      liquid.gridSize = this.density;
      liquid.origin = new Vector3d((this.aabb.minX + this.aabb.maxX) * 0.5, (this.aabb.minY + this.aabb.maxY) * 0.5, (this.aabb.minZ + this.aabb.maxZ) * 0.5);
      if (StarterClient.iris) {
         liquid.materialID = Iris.getMaterialID(Blocks.WATER.defaultBlockState());
      } else if (StarterClient.optifabric) {
         liquid.materialID = (short)Optifine.getMaterialID(Blocks.WATER.defaultBlockState());
      }

      int color = BiomeColors.getAverageWaterColor(liquid.world.getWorld(), this.pos);
      liquid.color = Pack.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
      AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(ResourceLocation.withDefaultNamespace("textures/block/water_flow.png"));
      if (texture instanceof SimpleTextureDimension dimension && dimension.getWidth() != dimension.getHeight()) {
         liquid.textureScale.y = (float)dimension.getWidth() / dimension.getHeight();
      }

      liquid.textureScale.mul(0.66666F);
      liquid.textureID = texture.getId();
      double diameter = 1.0 / liquid.density;
      double radius = diameter * 0.5;

      for (int i = 0; i < this.spawnAmount; i++) {
         for (int x = 0; x < liquid.density; x++) {
            for (int y = 0; y < liquid.density; y++) {
               for (int z = 0; z < liquid.density; z++) {
                  double xo = diameter * x + radius + this.aabb.minX;
                  double yo = diameter * y + radius + this.aabb.minY;
                  double zo = diameter * z + radius + this.aabb.minZ;
                  liquid.spawnParticle(radius, xo, yo, zo);
               }
            }
         }
      }
   }

   @Override
   public void update(Liquid liquid, double diff) {
      if (StarterClient.iris) {
         liquid.materialID = Iris.getMaterialID(Blocks.WATER.defaultBlockState());
      } else if (StarterClient.optifabric) {
         liquid.materialID = (short)Optifine.getMaterialID(Blocks.WATER.defaultBlockState());
      }

      if (liquid.sourceAlive) {
         double diameter = 1.0 / liquid.density;
         double radius = diameter * 0.5;
         int x = (this.spawnCorner & 1) == 1 ? 1 : 0;
         int y = (this.spawnCorner & 2) == 1 ? 1 : 0;
         int z = (this.spawnCorner & 4) == 1 ? 1 : 0;
         int amount = Math.round(net.diebuddies.math.Math.remap((float)this.density, 3.0F, 7.0F, 1.0F, 4.0F));
         amount *= 4;

         for (int i = 0; i < amount; i++) {
            double xo = net.diebuddies.math.Math.clamp((double)x, radius, 1.0 - radius) + this.aabb.minX;
            double yo = net.diebuddies.math.Math.clamp((double)y, radius, 1.0 - radius) + this.aabb.minY;
            double zo = net.diebuddies.math.Math.clamp((double)z, radius, 1.0 - radius) + this.aabb.minZ;
            liquid.spawnParticle(
               radius,
               xo + net.diebuddies.math.Math.random() * 0.1 - 0.05,
               yo + net.diebuddies.math.Math.random() * 0.1 - 0.05,
               zo + net.diebuddies.math.Math.random() * 0.1 - 0.05
            );
            this.spawnCorner++;
            this.spawnCorner %= 8;
         }
      }
   }
}
