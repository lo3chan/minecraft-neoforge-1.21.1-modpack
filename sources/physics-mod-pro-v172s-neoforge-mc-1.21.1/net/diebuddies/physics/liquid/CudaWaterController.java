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

public class CudaWaterController implements LiquidController {
   private double width;
   private double height;
   private double depth;
   private double radius = -1.0;
   private boolean sphere;
   private BlockPos pos;
   private AABB aabb;

   public CudaWaterController(BlockPos pos, double width, double height, double depth) {
      this.width = width;
      this.height = height;
      this.depth = depth;
      this.pos = pos;
      this.aabb = new AABB(pos);
   }

   public CudaWaterController(BlockPos pos, double radius) {
      this.radius = radius;
      this.pos = pos;
      this.aabb = new AABB(pos);
   }

   @Override
   public void init(PhysicsWorld world, Liquid liquid) {
      liquid.blockPos = this.pos;
      liquid.sourceAlive = true;
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
      if (liquid instanceof LiquidCuda liquidCuda) {
         if (this.radius > 0.0) {
            liquidCuda.initSphereParticles(world, liquid.origin.x, liquid.origin.y, liquid.origin.z, this.radius);
         } else {
            liquidCuda.initBoxParticles(
               world,
               liquid.origin.x - this.width * 0.5,
               liquid.origin.y - this.height * 0.5,
               liquid.origin.z - this.depth * 0.5,
               this.width,
               this.height,
               this.depth
            );
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
   }
}
