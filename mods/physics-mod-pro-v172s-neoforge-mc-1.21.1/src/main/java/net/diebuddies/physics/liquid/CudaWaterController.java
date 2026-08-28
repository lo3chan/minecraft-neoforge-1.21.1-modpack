/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BiomeColors
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  org.joml.Vector3d
 */
package net.diebuddies.physics.liquid;

import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.liquid.LiquidController;
import net.diebuddies.physics.liquid.LiquidCuda;
import net.diebuddies.physics.liquid.SimpleTextureDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;

public class CudaWaterController
implements LiquidController {
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
        SimpleTextureDimension dimension;
        liquid.blockPos = this.pos;
        liquid.sourceAlive = true;
        liquid.origin = new Vector3d((this.aabb.minX + this.aabb.maxX) * 0.5, (this.aabb.minY + this.aabb.maxY) * 0.5, (this.aabb.minZ + this.aabb.maxZ) * 0.5);
        if (StarterClient.iris) {
            liquid.materialID = Iris.getMaterialID(Blocks.WATER.defaultBlockState());
        } else if (StarterClient.optifabric) {
            liquid.materialID = (short)Optifine.getMaterialID(Blocks.WATER.defaultBlockState());
        }
        int color = BiomeColors.getAverageWaterColor((BlockAndTintGetter)liquid.world.getWorld(), (BlockPos)this.pos);
        liquid.color = Pack.color((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f);
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(ResourceLocation.withDefaultNamespace((String)"textures/block/water_flow.png"));
        if (texture instanceof SimpleTextureDimension && (dimension = (SimpleTextureDimension)texture).getWidth() != dimension.getHeight()) {
            liquid.textureScale.y = (float)dimension.getWidth() / (float)dimension.getHeight();
        }
        liquid.textureScale.mul(0.66666f);
        liquid.textureID = texture.getId();
        if (liquid instanceof LiquidCuda) {
            LiquidCuda liquidCuda = (LiquidCuda)liquid;
            if (this.radius > 0.0) {
                liquidCuda.initSphereParticles(world, liquid.origin.x, liquid.origin.y, liquid.origin.z, this.radius);
            } else {
                liquidCuda.initBoxParticles(world, liquid.origin.x - this.width * 0.5, liquid.origin.y - this.height * 0.5, liquid.origin.z - this.depth * 0.5, this.width, this.height, this.depth);
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

