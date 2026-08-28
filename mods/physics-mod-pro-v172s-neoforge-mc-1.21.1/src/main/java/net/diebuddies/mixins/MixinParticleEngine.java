/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import java.util.Map;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.blocks.BlockPhysicsType;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

@Mixin(value={ParticleEngine.class})
public class MixinParticleEngine {
    @Shadow
    @Final
    private Map<ResourceLocation, ParticleProvider<?>> providers;
    @Shadow
    protected ClientLevel level;
    @Shadow
    @Final
    private RandomSource random;

    @Inject(at={@At(value="HEAD")}, method={"destroy"}, cancellable=true)
    public void destroyParticles(BlockPos pos, BlockState state, CallbackInfo info) {
        BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
        if (blockSetting.getType() == BlockPhysicsType.PARTICLES && !state.isAir()) {
            info.cancel();
        } else if (!ConfigClient.minecraftBlockBreakParticles) {
            info.cancel();
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"crack"}, cancellable=true)
    public void crack(BlockPos blockPos, Direction direction, CallbackInfo info) {
        BlockState blockState = this.level.getBlockState(blockPos);
        if (ConfigClient.crackPhysicsParticles && blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.shouldSpawnTerrainParticles()) {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            int blockX = blockPos.getX();
            int blockY = blockPos.getY();
            int blockZ = blockPos.getZ();
            if (camera.isInitialized() && camera.getPosition().distanceToSqr((double)blockX, (double)blockY, (double)blockZ) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
                float offset = 0.1f;
                AABB aabb = blockState.getShape((BlockGetter)this.level, blockPos).bounds();
                double posX = (double)blockX + this.random.nextDouble() * (aabb.maxX - aabb.minX - (double)(offset * 2.0f)) + (double)offset + aabb.minX;
                double posY = (double)blockY + this.random.nextDouble() * (aabb.maxY - aabb.minY - (double)(offset * 2.0f)) + (double)offset + aabb.minY;
                double posZ = (double)blockZ + this.random.nextDouble() * (aabb.maxZ - aabb.minZ - (double)(offset * 2.0f)) + (double)offset + aabb.minZ;
                if (direction == Direction.DOWN) {
                    posY = (double)blockY + aabb.minY - (double)offset;
                }
                if (direction == Direction.UP) {
                    posY = (double)blockY + aabb.maxY + (double)offset;
                }
                if (direction == Direction.NORTH) {
                    posZ = (double)blockZ + aabb.minZ - (double)offset;
                }
                if (direction == Direction.SOUTH) {
                    posZ = (double)blockZ + aabb.maxZ + (double)offset;
                }
                if (direction == Direction.WEST) {
                    posX = (double)blockX + aabb.minX - (double)offset;
                }
                if (direction == Direction.EAST) {
                    posX = (double)blockX + aabb.maxX + (double)offset;
                }
                Vec3i normal = direction.getNormal();
                TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(blockState);
                PhysicsMod mod = PhysicsMod.getInstance((Level)this.level);
                PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
                entity.getTransformation().translation(posX, posY, posZ);
                entity.getOldTransformation().set((Matrix4dc)entity.getTransformation());
                Model model = entity.models.get(0);
                model.texture = sprite;
                model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
                entity.scale = Math.random() * 0.06f + 0.04f;
                entity.backfaceCulling = true;
                model.mesh = PhysicsMod.brokenBlock.get(0);
                int color = Minecraft.getInstance().getBlockColors().getColor(blockState, (BlockAndTintGetter)this.level, blockPos, 0);
                if (color == -1) {
                    color = -1;
                }
                entity.setColor(color);
                if (blockState.getBlock() == Blocks.CAULDRON || blockState.getBlock() == Blocks.GRASS_BLOCK) {
                    entity.setColor(-1);
                }
                IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
                mod.physicsWorld.queue(() -> {
                    PxRigidActor patt0$temp = body.getRigidBody();
                    if (patt0$temp instanceof PxRigidDynamic) {
                        PxRigidDynamic rigidBody = (PxRigidDynamic)patt0$temp;
                        float strength = 2.0f;
                        Vector3f speed = new Vector3f((float)normal.getX() * 0.3f, (float)normal.getY() * 0.3f, (float)normal.getZ() * 0.3f);
                        speed.x += (Math.random() - 0.5f) * 0.3f;
                        speed.y += (Math.random() - 0.5f) * 0.3f;
                        speed.z += (Math.random() - 0.5f) * 0.3f;
                        speed.normalize();
                        try (MemoryStack mem = MemoryStack.stackPush();){
                            PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                            rigidBody.setLinearVelocity(velocity);
                        }
                    }
                });
                float uo = Math.random() * 3.0f;
                float vo = Math.random() * 3.0f;
                Vector4f customUVs = new Vector4f(sprite.getU(uo / 4.0f), sprite.getU((uo + 1.0f) / 4.0f), sprite.getV(vo / 4.0f), sprite.getV((vo + 1.0f) / 4.0f));
                float xScale = customUVs.y - customUVs.x;
                float yScale = customUVs.w - customUVs.z;
                model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0f).scale(xScale, yScale, 0.0f);
                info.cancel();
            }
        }
    }
}

