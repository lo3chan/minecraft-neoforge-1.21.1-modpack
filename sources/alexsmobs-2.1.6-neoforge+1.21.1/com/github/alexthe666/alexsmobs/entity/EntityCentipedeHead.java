package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFleeLight;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityCentipedeHead extends Monster {
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(
      EntityCentipedeHead.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(EntityCentipedeHead.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SEGMENT_COUNT = SynchedEntityData.defineId(EntityCentipedeHead.class, EntityDataSerializers.INT);
   public final float[] ringBuffer = new float[64];
   public int ringBufferIndex = -1;
   private EntityCentipedeBody[] parts;

   protected EntityCentipedeHead(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.xpReward = 13;
      AMCompat.setMaxUpStep(this, 3.0F);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 35.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ARMOR, 6.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.MOVEMENT_SPEED, 0.2199999988079071);
   }

   public static <T extends Mob> boolean canCentipedeSpawn(
      EntityType<EntityCentipedeHead> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER
         || !iServerWorld.canSeeSky(pos)
            && pos.getY() <= AMConfig.caveCentipedeSpawnHeight
            && checkMonsterSpawnRules(entityType, iServerWorld, reason, pos, random);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.caveCentipedeSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, false));
      this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0, 13, false));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(5, new AnimalAIFleeLight(this, 1.0, 75, 5));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 20, true, true, null));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, AbstractVillager.class, 20, true, true, null));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, EntityCockroach.class, 45, true, true, null));
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.CENTIPEDE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.CENTIPEDE_HURT.get();
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(AMSoundRegistry.CENTIPEDE_WALK.get(), 1.0F, 1.0F);
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   public int getHeadRotSpeed() {
      return 1;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHILD_UUID, Optional.empty());
      builder.define(CHILD_ID, -1);
      builder.define(SEGMENT_COUNT, 5);
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (super.doHurtTarget(entityIn)) {
         if (entityIn instanceof LivingEntity) {
            Difficulty difficulty = this.level().getDifficulty();
            int i;
            if (difficulty == Difficulty.NORMAL) {
               i = 10;
            } else if (difficulty == Difficulty.HARD) {
               i = 20;
            } else {
               i = 3;
            }

            ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 1));
         }

         this.playSound(AMSoundRegistry.CENTIPEDE_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         return true;
      } else {
         return false;
      }
   }

   public int getSegmentCount() {
      return Math.max((Integer)this.entityData.get(SEGMENT_COUNT), 1);
   }

   public void setSegmentCount(int segments) {
      this.entityData.set(SEGMENT_COUNT, segments);
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void pushEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
      entities.stream().filter(entity -> !(entity instanceof EntityCentipedeBody) && entity.isPushable()).forEach(entity -> entity.push(this));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setSegmentCount(this.random.nextInt(4) + 5);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getChildId() != null) {
         AMCompat.putUUID(compound, "ChildUUID", this.getChildId());
      }

      compound.putInt("SegCount", this.getSegmentCount());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "ChildUUID")) {
         this.setChildId(AMCompat.getUUID(compound, "ChildUUID"));
      }

      this.setSegmentCount(AMCompat.getInt(compound, "SegCount"));
   }

   private boolean shouldReplaceParts() {
      if (this.parts != null && this.parts[0] != null && this.parts.length == this.getSegmentCount()) {
         for (int i = 0; i < this.getSegmentCount(); i++) {
            if (this.parts[i] == null) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public void tick() {
      super.tick();
      this.portalProcess = null;
      this.yBodyRot = this.yBodyRot + Mth.clamp(Mth.wrapDegrees(this.getYRot() - this.yBodyRot), -10.0F, 10.0F);
      this.yHeadRot = this.yBodyRot;
      if (this.ringBufferIndex < 0) {
         Arrays.fill(this.ringBuffer, this.yBodyRot);
      }

      if (this.updateRingBuffer() || this.ringBufferIndex < 0) {
         this.ringBufferIndex++;
      }

      if (this.ringBufferIndex == this.ringBuffer.length) {
         this.ringBufferIndex = 0;
      }

      this.ringBuffer[this.ringBufferIndex] = this.getYRot();
      if (!this.level().isClientSide()) {
         Entity child = this.getChild();
         if (child == null) {
            LivingEntity partParent = this;
            this.parts = new EntityCentipedeBody[this.getSegmentCount()];
            Vec3 prevPos = this.position();
            float backOffset = 0.45F;

            for (int i = 0; i < this.getSegmentCount(); i++) {
               EntityCentipedeBody part = this.createBody(partParent, i == this.getSegmentCount() - 1);
               part.setParent(partParent);
               part.setBodyIndex(i);
               if (partParent == this) {
                  this.setChildId(part.getUUID());
                  this.entityData.set(CHILD_ID, part.getId());
               }

               if (partParent instanceof EntityCentipedeBody body) {
                  body.setChildId(part.getUUID());
               }

               part.setPos(part.tickMultipartPosition(this.getId(), backOffset, prevPos, this.getXRot(), this.getYawForPart(i), false));
               this.level().addFreshEntity(part);
               this.parts[i] = part;
               partParent = part;
               backOffset = part.getBackOffset();
               prevPos = part.position();
            }
         }

         if (this.tickCount > 1) {
            if (this.shouldReplaceParts() && this.getChild() instanceof EntityCentipedeBody) {
               this.parts = new EntityCentipedeBody[this.getSegmentCount()];
               this.parts[0] = (EntityCentipedeBody)this.getChild();
               this.entityData.set(CHILD_ID, this.parts[0].getId());

               for (int i = 1; i < this.parts.length && this.parts[i - 1].getChild() instanceof EntityCentipedeBody; i++) {
                  this.parts[i] = (EntityCentipedeBody)this.parts[i - 1].getChild();
               }
            }

            Vec3 prev = this.position();
            float xRot = this.getXRot();
            float backOffset = 0.45F;

            for (int i = 0; i < this.getSegmentCount(); i++) {
               if (this.parts[i] != null) {
                  float reqRot = this.getYawForPart(i);
                  prev = this.parts[i].tickMultipartPosition(this.getId(), backOffset, prev, xRot, reqRot, true);
                  xRot = this.parts[i].getXRot();
                  backOffset = this.parts[i].getBackOffset();
               }
            }
         }
      }
   }

   private boolean updateRingBuffer() {
      return this.getDeltaMovement().lengthSqr() >= 0.005;
   }

   public EntityCentipedeBody createBody(LivingEntity parent, boolean tail) {
      return tail
         ? new EntityCentipedeBody(AMEntityRegistry.CENTIPEDE_TAIL.get(), parent, 0.84F, 180.0F, 0.0F)
         : new EntityCentipedeBody(AMEntityRegistry.CENTIPEDE_BODY.get(), parent, 0.84F, 180.0F, 0.0F);
   }

   public boolean canBeLeashed(Player player) {
      return true;
   }

   private float getYawForPart(int i) {
      return this.getRingBuffer(4 + i * 4, 1.0F);
   }

   public float getRingBuffer(int bufferOffset, float partialTicks) {
      if (this.isDeadOrDying()) {
         partialTicks = 0.0F;
      }

      partialTicks = 1.0F - partialTicks;
      int i = this.ringBufferIndex - bufferOffset & 63;
      int j = this.ringBufferIndex - bufferOffset - 1 & 63;
      float d0 = this.ringBuffer[i];
      float d1 = this.ringBuffer[j] - d0;
      return Mth.wrapDegrees(d0 + d1 * partialTicks);
   }
}
