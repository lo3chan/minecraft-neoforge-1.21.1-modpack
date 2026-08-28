/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.minecraft;

import net.diebuddies.math.Math;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class ParticleSpawner {
    public static void spawnEatingPhysicsParticle(ItemStack itemStack, Level level, double x, double y, double z, double vx, double vy, double vz) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getItemRenderer().getModel(itemStack, level, null, 0).getParticleIcon();
        PhysicsMod mod = PhysicsMod.getInstance(level);
        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
        entity.getTransformation().translation(x, y, z);
        entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
        Model model = entity.models.get(0);
        model.texture = sprite;
        model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
        entity.scale = Math.random() * 0.06f + 0.04f;
        entity.backfaceCulling = true;
        model.mesh = PhysicsMod.brokenBlock.get(0);
        entity.physicsGroup = (byte)16;
        entity.physicsMask = (byte)19;
        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
        mod.physicsWorld.queue(() -> {
            PxRigidActor patt0$temp = body.getRigidBody();
            if (patt0$temp instanceof PxRigidDynamic) {
                PxRigidDynamic rigidBody = (PxRigidDynamic)patt0$temp;
                try (MemoryStack mem = MemoryStack.stackPush();){
                    PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)vx, (float)vy * 1.3f + 1.0f, (float)vz);
                    rigidBody.setLinearVelocity(velocity);
                    PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0f, Math.random() * 4.0f, Math.random() * 4.0f);
                    rigidBody.setAngularVelocity(avelocity);
                }
            }
        });
        float uo = Math.random() * 3.0f;
        float vo = Math.random() * 3.0f;
        Vector4f customUVs = new Vector4f(sprite.getU(uo / 4.0f), sprite.getU(uo / 4.0f), sprite.getV(vo / 4.0f), sprite.getV(vo / 4.0f));
        float xScale = customUVs.y - customUVs.x;
        float yScale = customUVs.w - customUVs.z;
        model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0f).scale(xScale, yScale, 0.0f);
    }

    public static void spawnSprintingPhysicsParticle(BlockState state, BlockPos pos, Level level, double x, double y, double z) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
        PhysicsMod mod = PhysicsMod.getInstance(level);
        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
        entity.getTransformation().translation(x, y, z);
        entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
        Model model = entity.models.get(0);
        model.texture = sprite;
        model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
        entity.scale = Math.random() * 0.06f + 0.11f;
        entity.backfaceCulling = true;
        model.mesh = PhysicsMod.brokenBlock.get(0);
        entity.physicsGroup = (byte)16;
        entity.physicsMask = (byte)19;
        int color = Minecraft.getInstance().getBlockColors().getColor(state, (BlockAndTintGetter)level, pos, 0);
        if (color == -1) {
            color = -1;
        }
        entity.setColor(color);
        if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.GRASS_BLOCK) {
            entity.setColor(-1);
        }
        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
        mod.physicsWorld.queue(() -> {
            PxRigidActor patt0$temp = body.getRigidBody();
            if (patt0$temp instanceof PxRigidDynamic) {
                PxRigidDynamic rigidBody = (PxRigidDynamic)patt0$temp;
                float strength = 3.0f;
                Vector3f speed = new Vector3f(0.0f, 0.3f, 0.0f);
                speed.x += (Math.random() - 0.5f) * 0.4f;
                speed.y += Math.random() * 0.4f;
                speed.z += (Math.random() - 0.5f) * 0.4f;
                speed.normalize();
                try (MemoryStack mem = MemoryStack.stackPush();){
                    PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                    rigidBody.setLinearVelocity(velocity);
                    PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0f, Math.random() * 4.0f, Math.random() * 4.0f);
                    rigidBody.setAngularVelocity(avelocity);
                }
            }
        });
        float uo = Math.random() * 3.0f;
        float vo = Math.random() * 3.0f;
        Vector4f customUVs = new Vector4f(sprite.getU(uo / 4.0f), sprite.getU((uo + 1.0f) / 4.0f), sprite.getV(vo / 4.0f), sprite.getV((vo + 1.0f) / 4.0f));
        float xScale = customUVs.y - customUVs.x;
        float yScale = customUVs.w - customUVs.z;
        model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0f).scale(xScale, yScale, 0.0f);
    }

    public static void spawnBloodPhysicsParticle(Level level, double x, double y, double z) {
        PhysicsMod mod = PhysicsMod.getInstance(level);
        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
        entity.getTransformation().translation(x, y, z);
        entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
        Model model = entity.models.get(0);
        model.textureID = PhysicsMod.whiteTexture.getID();
        entity.scale = Math.random() * 0.03f + 0.08f;
        entity.backfaceCulling = true;
        model.mesh = PhysicsMod.brokenBlock.get(0);
        float bloodModifier = Math.random() * 0.2f + 0.8f;
        entity.setColor(Pack.color(0.1f * bloodModifier, 0.1f * bloodModifier, 0.77f * bloodModifier));
        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
        mod.physicsWorld.queue(() -> {
            PxRigidActor patt0$temp = body.getRigidBody();
            if (patt0$temp instanceof PxRigidDynamic) {
                PxRigidDynamic rigidBody = (PxRigidDynamic)patt0$temp;
                Vector3f speed = new Vector3f();
                speed.x = Math.random() - 0.5f;
                speed.y = Math.random() - 0.5f;
                speed.z = Math.random() - 0.5f;
                float strength = Math.random() * 2.0f + 1.0f;
                if (speed.lengthSquared() > 0.0f) {
                    speed.normalize();
                }
                try (MemoryStack mem = MemoryStack.stackPush();){
                    PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                    rigidBody.setLinearVelocity(velocity);
                    PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0f, Math.random() * 4.0f, Math.random() * 4.0f);
                    rigidBody.setAngularVelocity(avelocity);
                }
            }
        });
    }

    public static void spawnOceanPhysicsParticle(Level level, double x, double y, double z, double vx, double vz) {
        PhysicsMod mod = PhysicsMod.getInstance(level);
        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
        entity.getTransformation().translation(x, y, z);
        entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
        Model model = entity.models.get(0);
        model.textureID = PhysicsMod.whiteTexture.getID();
        entity.scale = Math.random() * 0.05f + 0.11f;
        entity.backfaceCulling = true;
        model.mesh = PhysicsMod.brokenBlock.get(0);
        entity.physicsGroup = (byte)16;
        entity.physicsMask = (byte)19;
        entity.setColor(-1);
        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
        mod.physicsWorld.queue(() -> {
            PxRigidActor patt0$temp = body.getRigidBody();
            if (patt0$temp instanceof PxRigidDynamic) {
                PxRigidDynamic rigidBody = (PxRigidDynamic)patt0$temp;
                Vector3f speed = new Vector3f((float)vx, 0.0f, (float)vz);
                speed.x *= Math.random();
                speed.y = 0.0f;
                speed.z += Math.random();
                float strength = Math.random() * 2.0f + 1.0f;
                if (speed.lengthSquared() > 0.0f) {
                    speed.normalize();
                }
                try (MemoryStack mem = MemoryStack.stackPush();){
                    PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                    rigidBody.setLinearVelocity(velocity);
                    PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0f, Math.random() * 4.0f, Math.random() * 4.0f);
                    rigidBody.setAngularVelocity(avelocity);
                }
            }
        });
    }

    public static void spawnServerBlockPhysicsParticle(BlockState state, Level level, double x, double y, double z, double vx, double vy, double vz) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
        PhysicsMod mod = PhysicsMod.getInstance(level);
        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
        entity.getTransformation().translation(x, y, z);
        entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
        Model model = entity.models.get(0);
        model.texture = sprite;
        model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
        entity.scale = Math.random() * 0.06f + 0.11f;
        entity.backfaceCulling = true;
        model.mesh = PhysicsMod.brokenBlock.get(0);
        entity.physicsGroup = (byte)16;
        entity.physicsMask = (byte)19;
        int color = Minecraft.getInstance().getBlockColors().getColor(state, (BlockAndTintGetter)level, BlockPos.containing((double)x, (double)y, (double)z), 0);
        if (color == -1) {
            color = -1;
        }
        entity.setColor(color);
        if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.GRASS_BLOCK) {
            entity.setColor(-1);
        }
        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
        mod.physicsWorld.queue(() -> {
            PxRigidActor patt0$temp = body.getRigidBody();
            if (patt0$temp instanceof PxRigidDynamic) {
                PxRigidDynamic rigidBody = (PxRigidDynamic)patt0$temp;
                float strength = 2.0f;
                try (MemoryStack mem = MemoryStack.stackPush();){
                    PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)vx * strength, (float)vy * strength, (float)vz * strength);
                    rigidBody.setLinearVelocity(velocity);
                    PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0f, Math.random() * 4.0f, Math.random() * 4.0f);
                    rigidBody.setAngularVelocity(avelocity);
                }
            }
        });
        float uo = Math.random() * 3.0f;
        float vo = Math.random() * 3.0f;
        Vector4f customUVs = new Vector4f(sprite.getU(uo / 4.0f), sprite.getU((uo + 1.0f) / 4.0f), sprite.getV(vo / 4.0f), sprite.getV((vo + 1.0f) / 4.0f));
        float xScale = customUVs.y - customUVs.x;
        float yScale = customUVs.w - customUVs.z;
        model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0f).scale(xScale, yScale, 0.0f);
    }

    public static void spawnItemPhysicsParticle(TextureAtlasSprite sprite, Level level, double x, double y, double z, float size, float depthScale, float uvx, float uvy, Matrix4f transformation) {
        PhysicsMod mod = PhysicsMod.getInstance(level);
        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
        entity.getTransformation().set((Matrix4fc)transformation).translate(x, y, z).scale(1.0, 1.0, (double)depthScale);
        entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
        Model model = entity.models.get(0);
        model.texture = sprite;
        model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
        entity.scale = size;
        entity.backfaceCulling = true;
        model.mesh = PhysicsMod.brokenBlock.get(0);
        entity.physicsGroup = (byte)16;
        entity.physicsMask = (byte)19;
        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
        mod.physicsWorld.queue(() -> {
            PxRigidActor patt0$temp = body.getRigidBody();
            if (patt0$temp instanceof PxRigidDynamic) {
                PxRigidDynamic rigidBody = (PxRigidDynamic)patt0$temp;
                float strength = 2.0f;
                try (MemoryStack mem = MemoryStack.stackPush();){
                    PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, (Math.random() - 0.5f) * strength, (Math.random() - 0.5f) * strength * 1.6f, (Math.random() - 0.5f) * strength);
                    rigidBody.setLinearVelocity(velocity);
                    PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0f, Math.random() * 4.0f, Math.random() * 4.0f);
                    rigidBody.setAngularVelocity(avelocity);
                }
            }
        });
        Vector4f customUVs = new Vector4f(sprite.getU(uvx), sprite.getU(uvx), sprite.getV(uvy), sprite.getV(uvy));
        float xScale = customUVs.y - customUVs.x;
        float yScale = customUVs.w - customUVs.z;
        model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0f).scale(xScale, yScale, 0.0f);
    }
}

