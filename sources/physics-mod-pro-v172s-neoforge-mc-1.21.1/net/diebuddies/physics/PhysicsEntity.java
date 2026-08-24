package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.math.AABBf;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.ParticleSpawn;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PhysicsEntity {
   private final Matrix4d transformation;
   private final Matrix4d oldTransformation;
   private final Matrix4f renderTransformation = new Matrix4f();
   private Quaternionf rotation;
   private Quaternionf oldRotation;
   public List<Model> models = new ObjectArrayList(1);
   public PhysicsEntity.Type type;
   public float time;
   public AABBf rescale;
   public final Vector3f pivot = new Vector3f();
   public float scale = 1.0F;
   private int color = -1;
   public final Vector3f scalePhysics = new Vector3f(1.0F);
   public final Vector3f enlargeHitbox = new Vector3f(1.0F);
   public byte physicsGroup = 2;
   public byte physicsMask = 23;
   public Object info;
   public SoundType sound;
   public long lastSoundTime = 0L;
   public RenderLayer feature = null;
   private int cachedBrightness;
   private MutableBlockPos cachedBrightnessPos;
   public float lifetime;
   public float lifetimeVariance;
   private Animation animation;
   private float boundingSphereRadius = -1.0F;
   public boolean noVolume;
   public boolean backfaceCulling = true;
   public boolean staticPhysics = false;
   public boolean shade = true;
   private boolean dead;
   public List<PhysicsEntity> children = new ObjectArrayList();

   public PhysicsEntity(PhysicsEntity.Type type, Object info) {
      this.info = info;
      this.animation = ConfigAnimations.DEFAULT_ANIMATION;
      if (info != null) {
         if (info instanceof BlockState state) {
            this.sound = state.getSoundType();
            if (type == PhysicsEntity.Type.BLOCK || type == PhysicsEntity.Type.VINE) {
               BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
               if (type != PhysicsEntity.Type.VINE) {
                  this.scale = (float)blockSetting.getScale();
               }

               this.animation = blockSetting.getAnimation();
               this.lifetime = (float)blockSetting.getLifetime();
               this.lifetimeVariance = (float)blockSetting.getLifetimeVariance();
            }
         } else if (info instanceof EntityType<?> entityType && type == PhysicsEntity.Type.MOB) {
            MobSetting mobSetting = ConfigMobs.getMobSetting(entityType);
            this.animation = mobSetting.getAnimation();
            this.lifetime = (float)mobSetting.getLifetime();
            this.lifetimeVariance = (float)mobSetting.getLifetimeVariance();
         }
      }

      this.transformation = new Matrix4d();
      this.oldTransformation = new Matrix4d();
      this.models.add(new Model());
      this.type = type;
   }

   public Matrix4d getTransformation() {
      return this.transformation;
   }

   public Matrix4d getOldTransformation() {
      return this.oldTransformation;
   }

   public Matrix4f getRenderTransformation() {
      return this.renderTransformation;
   }

   public Quaternionf getRotation() {
      return this.rotation;
   }

   public Quaternionf getOldRotation() {
      return this.oldRotation;
   }

   public void setRotation(Quaternionf rotation) {
      this.rotation = rotation;
   }

   public void setOldRotation(Quaternionf oldRotation) {
      this.oldRotation = oldRotation;
   }

   public float getBoundingSphereRadius() {
      if (this.boundingSphereRadius < 0.0) {
         for (int i = 0; i < this.models.size(); i++) {
            this.boundingSphereRadius = Math.max(this.boundingSphereRadius, this.models.get(i).mesh.getRadius());
         }

         this.boundingSphereRadius = this.boundingSphereRadius
            * (Math.max(this.scalePhysics.x, Math.max(this.scalePhysics.y, this.scalePhysics.z)) * this.scale);
      }

      return this.boundingSphereRadius;
   }

   public void destroy() {
      this.destroyModels();
   }

   public void destroyModels() {
      if (this.models != null) {
         for (int i = 0; i < this.models.size(); i++) {
            Model model = this.models.get(i);
            if (model.memorySegment != null) {
               model.memorySegment.free();
               model.memorySegment = null;
            }
         }
      }
   }

   public int getLight(Level level, MutableBlockPos blockPos) {
      if (!StarterClient.disableLightingCache) {
         if (this.cachedBrightnessPos == null) {
            this.cachedBrightnessPos = new MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
         } else if (this.cachedBrightnessPos.getX() == blockPos.getX()
            && this.cachedBrightnessPos.getY() == blockPos.getY()
            && this.cachedBrightnessPos.getZ() == blockPos.getZ()) {
            return this.cachedBrightness;
         }
      }

      BlockState bState = level.getBlockState(blockPos);
      int x = blockPos.getX();
      int y = blockPos.getY();
      int z = blockPos.getZ();
      int brightness = 0;
      if (!bState.canOcclude()) {
         brightness = LevelRenderer.getLightColor(level, blockPos);
      } else {
         bState = level.getBlockState(blockPos.set(x, y + 1, z));
         if (!bState.canOcclude()) {
            brightness = LevelRenderer.getLightColor(level, blockPos);
         } else {
            bState = level.getBlockState(blockPos.set(x, y - 1, z));
            if (!bState.canOcclude()) {
               brightness = LevelRenderer.getLightColor(level, blockPos);
            } else {
               bState = level.getBlockState(blockPos.set(x, y, z - 1));
               if (!bState.canOcclude()) {
                  brightness = LevelRenderer.getLightColor(level, blockPos);
               } else {
                  bState = level.getBlockState(blockPos.set(x + 1, y, z));
                  if (!bState.canOcclude()) {
                     brightness = LevelRenderer.getLightColor(level, blockPos);
                  } else {
                     bState = level.getBlockState(blockPos.set(x, y, z + 1));
                     if (!bState.canOcclude()) {
                        brightness = LevelRenderer.getLightColor(level, blockPos);
                     } else {
                        bState = level.getBlockState(blockPos.set(x - 1, y, z));
                        if (!bState.canOcclude()) {
                           brightness = LevelRenderer.getLightColor(level, blockPos);
                        }
                     }
                  }
               }
            }
         }
      }

      blockPos.set(x, y, z);
      if (!StarterClient.disableLightingCache) {
         this.cachedBrightness = brightness;
         this.cachedBrightnessPos.set(x, y, z);
      }

      return brightness;
   }

   public void invalidateBrightness() {
      this.cachedBrightnessPos = null;
   }

   public MutableBlockPos getCachedBrightnessPos() {
      return this.cachedBrightnessPos;
   }

   public double getDespawnSpeed() {
      return this.animation.speed;
   }

   public void spawnDeathAnimation(PhysicsWorld world, boolean playSound) {
      if (!this.dead) {
         Level level = world.getWorld();
         List<ParticleSpawn> particleSpawns = this.animation.particleSpawns;

         for (int i = 0; i < particleSpawns.size(); i++) {
            ParticleSpawn particleSpawn = particleSpawns.get(i);
            if (particleSpawn.particle != null && net.diebuddies.math.Math.random() < particleSpawn.spawnChance) {
               for (int j = 0; j < particleSpawn.amount; j++) {
                  double halfSpread = particleSpawn.spread * 0.5;
                  double px = this.transformation.m30() + world.getOffset().x + net.diebuddies.math.Math.random() * particleSpawn.spread - halfSpread;
                  double py = this.transformation.m31() + world.getOffset().y + net.diebuddies.math.Math.random() * particleSpawn.spread - halfSpread;
                  double pz = this.transformation.m32() + world.getOffset().z + net.diebuddies.math.Math.random() * particleSpawn.spread - halfSpread;
                  level.addParticle(particleSpawn.particle, px, py, pz, particleSpawn.vx, particleSpawn.vy, particleSpawn.vz);
               }

               if (particleSpawn.sound != null && playSound) {
                  float pitch = 0.85F + net.diebuddies.math.Math.random() * 0.3F;
                  level.playLocalSound(
                     this.transformation.m30() + world.getOffset().x,
                     this.transformation.m31() + world.getOffset().y,
                     this.transformation.m32() + world.getOffset().z,
                     particleSpawn.sound,
                     SoundSource.HOSTILE,
                     (float)particleSpawn.soundVolume,
                     pitch,
                     true
                  );
               }
            }
         }

         this.dead = true;
      }
   }

   public float getVolume() {
      if (this.rescale == null) {
         return 1.0F;
      } else {
         Vector3f max = this.rescale.end;
         Vector3f min = this.rescale.start;
         return (max.x - min.x) * (max.y - min.y) * (max.z - min.z);
      }
   }

   public void startDespawnAnimation(Level level) {
      if (this.time > this.animation.speed) {
         this.time = this.animation.speed;
      }
   }

   public boolean isDespawning() {
      return this.time <= this.animation.speed;
   }

   public float getDespawnScale(Level level) {
      if (this.time > this.animation.speed) {
         return 1.0F;
      } else {
         return this.time <= 0.0 ? 0.0F : this.animation.getCurve().get(this.time / this.animation.speed);
      }
   }

   public void setAnimation(Animation animation) {
      this.animation = animation;
   }

   public AnimationType getAnimationType() {
      return this.animation.despawnType;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public float getRed() {
      return (this.color >> 16 & 0xFF) * 0.003921569F;
   }

   public float getGreen() {
      return (this.color >> 8 & 0xFF) * 0.003921569F;
   }

   public float getBlue() {
      return (this.color & 0xFF) * 0.003921569F;
   }

   public float getAlpha() {
      return (this.color >> 24 & 0xFF) * 0.003921569F;
   }

   public int getBGRA() {
      return this.color;
   }

   public static enum Type {
      MOB,
      BLOCK,
      VINE,
      ITEM,
      PARTICLE,
      LIQUID,
      SMOKE,
      OTHER;
   }
}
