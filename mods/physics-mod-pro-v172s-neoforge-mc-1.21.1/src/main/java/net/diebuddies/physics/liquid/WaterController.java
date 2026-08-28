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
import net.diebuddies.math.Math;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.liquid.LiquidController;
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

public class WaterController
implements LiquidController {
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
        SimpleTextureDimension dimension;
        liquid.blockPos = this.pos;
        liquid.density = this.density;
        liquid.damping = 0.0f;
        liquid.range = 1.5;
        liquid.sourceAlive = this.continous;
        liquid.gridSize = this.density;
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
        double diameter = 1.0 / (double)liquid.density;
        double radius = diameter * 0.5;
        for (int i = 0; i < this.spawnAmount; ++i) {
            for (int x = 0; x < liquid.density; ++x) {
                for (int y = 0; y < liquid.density; ++y) {
                    for (int z = 0; z < liquid.density; ++z) {
                        double xo = diameter * (double)x + radius + this.aabb.minX;
                        double yo = diameter * (double)y + radius + this.aabb.minY;
                        double zo = diameter * (double)z + radius + this.aabb.minZ;
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
            double diameter = 1.0 / (double)liquid.density;
            double radius = diameter * 0.5;
            boolean x = (this.spawnCorner & 1) == 1;
            boolean y = (this.spawnCorner & 2) == 1;
            boolean z = (this.spawnCorner & 4) == 1;
            int amount = java.lang.Math.round(Math.remap(this.density, 3.0f, 7.0f, 1.0f, 4.0f));
            amount *= 4;
            for (int i = 0; i < amount; ++i) {
                double xo = Math.clamp((double)x, radius, 1.0 - radius) + this.aabb.minX;
                double yo = Math.clamp((double)y, radius, 1.0 - radius) + this.aabb.minY;
                double zo = Math.clamp((double)z, radius, 1.0 - radius) + this.aabb.minZ;
                liquid.spawnParticle(radius, xo + (double)Math.random() * 0.1 - 0.05, yo + (double)Math.random() * 0.1 - 0.05, zo + (double)Math.random() * 0.1 - 0.05);
                ++this.spawnCorner;
                this.spawnCorner %= 8;
            }
        }
    }
}

