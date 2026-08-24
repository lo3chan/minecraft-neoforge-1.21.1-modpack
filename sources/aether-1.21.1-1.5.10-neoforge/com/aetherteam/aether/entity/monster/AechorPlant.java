package com.aetherteam.aether.entity.monster;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AechorPlant extends PathfinderMob implements RangedAttackMob {
   private static final EntityDataAccessor<Integer> DATA_SIZE_ID = SynchedEntityData.defineId(AechorPlant.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_POISON_REMAINING_ID = SynchedEntityData.defineId(AechorPlant.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_TARGETING_ENTITY_ID = SynchedEntityData.defineId(AechorPlant.class, EntityDataSerializers.BOOLEAN);
   private float sinage;
   private float sinageAdd;

   public AechorPlant(EntityType<? extends AechorPlant> type, Level level) {
      super(type, level);
      this.xpReward = 5;
      this.setPoisonRemaining(2);
      if (level.isClientSide()) {
         this.sinage = this.getRandom().nextFloat() * 6.0F;
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new RangedAttackGoal(this, 1.0, 60, 10.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.0).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_SIZE_ID, 0);
      builder.define(DATA_POISON_REMAINING_ID, 0);
      builder.define(DATA_TARGETING_ENTITY_ID, false);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
      if (DATA_SIZE_ID.equals(dataAccessor)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(dataAccessor);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      this.setSize(this.getRandom().nextInt(4) + 1);
      this.setPos(Vec3.atBottomCenterOf(this.blockPosition()));
      return spawnData;
   }

   public static boolean checkAechorPlantSpawnRules(
      EntityType<? extends AechorPlant> aechorPlant, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return level.getBlockState(pos.below()).is(AetherTags.Blocks.AECHOR_PLANT_SPAWNABLE_ON)
         && level.getRawBrightness(pos, 0) > 8
         && level.getDifficulty() != Difficulty.PEACEFUL
         && (reason != MobSpawnType.NATURAL || random.nextInt(10) == 0 && !inRadiusOfFlowers(level, pos, 10, 40));
   }

   public static boolean inRadiusOfFlowers(LevelAccessor level, BlockPos pos, int radius, int radiusEnchanted) {
      for (ChunkPos chunk : ChunkPos.rangeClosed(new ChunkPos(pos), radiusEnchanted).toList()) {
         ChunkAccess chunkAccess = level.getChunk(chunk.x, chunk.z, ChunkStatus.FULL, false);
         if (chunkAccess != null) {
            for (BlockPos blockEntityPos : chunkAccess.getBlockEntitiesPos()) {
               if (blockEntityPos.distSqr(pos) <= radius * radius) {
                  BlockEntity blockEntity = level.getBlockEntity(blockEntityPos);
                  if (blockEntity != null && blockEntity.getBlockState().is(AetherTags.Blocks.AECHOR_PLANT_SPAWNABLE_DETERRENT)) {
                     return true;
                  }
               } else if (blockEntityPos.distSqr(pos) <= radiusEnchanted * radiusEnchanted) {
                  BlockEntity blockEntity = level.getBlockEntity(blockEntityPos);
                  if (blockEntity != null
                     && blockEntity.getBlockState().is(AetherTags.Blocks.AECHOR_PLANT_SPAWNABLE_DETERRENT)
                     && level.getBlockState(blockEntityPos.below()).is(AetherTags.Blocks.ENCHANTED_GRASS)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public void tick() {
      super.tick();
      if (!this.level().getBlockState(this.blockPosition().below()).is(AetherTags.Blocks.AECHOR_PLANT_SPAWNABLE_ON) && !this.isPassenger()) {
         this.kill();
      }

      if (!this.level().isClientSide()) {
         if (this.getTarget() != null) {
            this.setTargetingEntity(true);
         } else if (this.getTarget() == null && this.getTargetingEntity()) {
            this.setTargetingEntity(false);
         }
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.level().isClientSide()) {
         this.sinage = this.sinage + this.sinageAdd;
         if (this.hurtTime > 0) {
            this.sinageAdd = 0.45F;
         } else if (this.getTargetingEntity()) {
            this.sinageAdd = 0.3F;
         } else {
            this.sinageAdd = 0.15F;
         }

         if (this.sinage >= 6.2831855F) {
            this.sinage -= 6.2831855F;
         }
      }
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.is((Item)AetherItems.SKYROOT_BUCKET.get()) && this.getPoisonRemaining() > 0) {
         this.setPoisonRemaining(this.getPoisonRemaining() - 1);
         ItemStack itemStack1 = ItemUtils.createFilledResult(itemStack, player, ((Item)AetherItems.SKYROOT_POISON_BUCKET.get()).getDefaultInstance());
         player.setItemInHand(hand, itemStack1);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.mobInteract(player, hand);
      }
   }

   public void push(double x, double y, double z) {
   }

   public void jumpFromGround() {
   }

   public boolean canBeLeashed() {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (this.hurtTime == 0) {
         for (int i = 0; i < 8; i++) {
            double d1 = this.getX() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.5;
            double d2 = this.getY() + 0.25 + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.5;
            double d3 = this.getZ() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.5;
            double d4 = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.5;
            double d5 = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.5;
            this.level().addParticle(ParticleTypes.PORTAL, d1, d2, d3, d4, 0.25, d5);
         }
      }

      return super.hurt(source, amount);
   }

   public void performRangedAttack(LivingEntity target, float distanceFactor) {
      PoisonNeedle needle = new PoisonNeedle(this.level(), this);
      double x = target.getX() - this.getX();
      double z = target.getZ() - this.getZ();
      double sqrt = Math.sqrt(x * x + z * z + 0.1);
      double y = 0.1 + sqrt * 0.5 + (this.getY() - target.getY()) * 0.25;
      double distance = 1.5 / sqrt;
      x *= distance;
      z *= distance;
      needle.shoot(x, y + 0.5, z, 0.285F + (float)y * 0.08F, 1.0F);
      this.playSound((SoundEvent)AetherSoundEvents.ENTITY_AECHOR_PLANT_SHOOT.get(), 2.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level().addFreshEntity(needle);
   }

   public int getSize() {
      return (Integer)this.getEntityData().get(DATA_SIZE_ID);
   }

   public void setSize(int size) {
      this.getEntityData().set(DATA_SIZE_ID, size);
   }

   public int getPoisonRemaining() {
      return (Integer)this.getEntityData().get(DATA_POISON_REMAINING_ID);
   }

   public void setPoisonRemaining(int poisonRemaining) {
      this.getEntityData().set(DATA_POISON_REMAINING_ID, poisonRemaining);
   }

   public boolean getTargetingEntity() {
      return (Boolean)this.getEntityData().get(DATA_TARGETING_ENTITY_ID);
   }

   public void setTargetingEntity(boolean targetingEntity) {
      this.getEntityData().set(DATA_TARGETING_ENTITY_ID, targetingEntity);
   }

   public float getSinage() {
      return this.sinage;
   }

   public float getSinageAdd() {
      return this.sinageAdd;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_AECHOR_PLANT_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_AECHOR_PLANT_DEATH.get();
   }

   public boolean hasLineOfSight(Entity entity) {
      return this.distanceTo(entity) <= 8.0 && super.hasLineOfSight(entity);
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      float width = 0.75F + this.getSize() * 0.125F;
      float height = 0.5F + this.getSize() * 0.075F;
      return EntityDimensions.fixed(width, height).withEyeHeight(height / 1.15F);
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   public boolean canBeAffected(MobEffectInstance effect) {
      return effect.getEffect().value() != AetherEffects.INEBRIATION.get() && super.canBeAffected(effect);
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("Size", this.getSize());
      tag.putInt("Poison Remaining", this.getPoisonRemaining());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("Size")) {
         this.setSize(tag.getInt("Size"));
      }

      if (tag.contains("Poison Remaining")) {
         this.setPoisonRemaining(tag.getInt("Poison Remaining"));
      }
   }
}
