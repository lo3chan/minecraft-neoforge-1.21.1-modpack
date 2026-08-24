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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
import physx.physics.PxRigidDynamic;

@Mixin({ParticleEngine.class})
public class MixinParticleEngine {
   @Shadow
   @Final
   private Map<ResourceLocation, ParticleProvider<?>> providers;
   @Shadow
   protected ClientLevel level;
   @Shadow
   @Final
   private RandomSource random;

   @Inject(
      at = {@At("HEAD")},
      method = {"destroy"},
      cancellable = true
   )
   public void destroyParticles(BlockPos pos, BlockState state, CallbackInfo info) {
      BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
      if (blockSetting.getType() == BlockPhysicsType.PARTICLES && !state.isAir()) {
         info.cancel();
      } else if (!ConfigClient.minecraftBlockBreakParticles) {
         info.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"crack"},
      cancellable = true
   )
   public void crack(BlockPos blockPos, Direction direction, CallbackInfo info) {
      BlockState blockState = this.level.getBlockState(blockPos);
      if (ConfigClient.crackPhysicsParticles && blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.shouldSpawnTerrainParticles()) {
         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         int blockX = blockPos.getX();
         int blockY = blockPos.getY();
         int blockZ = blockPos.getZ();
         if (camera.isInitialized()
            && camera.getPosition().distanceToSqr(blockX, blockY, blockZ) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
            float offset = 0.1F;
            AABB aabb = blockState.getShape(this.level, blockPos).bounds();
            double posX = blockX + this.random.nextDouble() * (aabb.maxX - aabb.minX - offset * 2.0F) + offset + aabb.minX;
            double posY = blockY + this.random.nextDouble() * (aabb.maxY - aabb.minY - offset * 2.0F) + offset + aabb.minY;
            double posZ = blockZ + this.random.nextDouble() * (aabb.maxZ - aabb.minZ - offset * 2.0F) + offset + aabb.minZ;
            if (direction == Direction.DOWN) {
               posY = blockY + aabb.minY - offset;
            }

            if (direction == Direction.UP) {
               posY = blockY + aabb.maxY + offset;
            }

            if (direction == Direction.NORTH) {
               posZ = blockZ + aabb.minZ - offset;
            }

            if (direction == Direction.SOUTH) {
               posZ = blockZ + aabb.maxZ + offset;
            }

            if (direction == Direction.WEST) {
               posX = blockX + aabb.minX - offset;
            }

            if (direction == Direction.EAST) {
               posX = blockX + aabb.maxX + offset;
            }

            Vec3i normal = direction.getNormal();
            TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(blockState);
            PhysicsMod mod = PhysicsMod.getInstance(this.level);
            PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
            entity.getTransformation().translation(posX, posY, posZ);
            entity.getOldTransformation().set(entity.getTransformation());
            Model model = entity.models.get(0);
            model.texture = sprite;
            model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getId();
            entity.scale = Math.random() * 0.06F + 0.04F;
            entity.backfaceCulling = true;
            model.mesh = PhysicsMod.brokenBlock.get(0);
            int color = Minecraft.getInstance().getBlockColors().getColor(blockState, this.level, blockPos, 0);
            if (color == -1) {
               color = -1;
            }

            entity.setColor(color);
            if (blockState.getBlock() == Blocks.CAULDRON || blockState.getBlock() == Blocks.GRASS_BLOCK) {
               entity.setColor(-1);
            }

            IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
            mod.physicsWorld.queue(() -> {
               if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
                  float strength = 2.0F;
                  Vector3f speed = new Vector3f(normal.getX() * 0.3F, normal.getY() * 0.3F, normal.getZ() * 0.3F);
                  speed.x = speed.x + (Math.random() - 0.5F) * 0.3F;
                  speed.y = speed.y + (Math.random() - 0.5F) * 0.3F;
                  speed.z = speed.z + (Math.random() - 0.5F) * 0.3F;
                  speed.normalize();
                  MemoryStack mem = MemoryStack.stackPush();

                  try {
                     PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                     rigidBody.setLinearVelocity(velocity);
                  } catch (Throwable var9x) {
                     if (mem != null) {
                        try {
                           mem.close();
                        } catch (Throwable var8x) {
                           var9x.addSuppressed(var8x);
                        }
                     }

                     throw var9x;
                  }

                  if (mem != null) {
                     mem.close();
                  }
               }
            });
            float uo = Math.random() * 3.0F;
            float vo = Math.random() * 3.0F;
            Vector4f customUVs = new Vector4f(sprite.getU(uo / 4.0F), sprite.getU((uo + 1.0F) / 4.0F), sprite.getV(vo / 4.0F), sprite.getV((vo + 1.0F) / 4.0F));
            float xScale = customUVs.y - customUVs.x;
            float yScale = customUVs.w - customUVs.z;
            model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0F).scale(xScale, yScale, 0.0F);
            info.cancel();
         }
      }
   }
}
