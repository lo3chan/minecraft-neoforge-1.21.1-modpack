package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockEnderResidue;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public class EntityVoidWorm extends Monster {
   public static final ResourceLocation SPLITTER_LOOT = AMCompat.rl("alexsmobs", "entities/void_worm_splitter");
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Optional<UUID>> SPLIT_FROM_UUID = SynchedEntityData.defineId(
      EntityVoidWorm.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> SEGMENT_COUNT = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> JAW_TICKS = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> WORM_ANGLE = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> SPEEDMOD = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> SPLITTER = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> PORTAL_TICKS = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.INT);
   private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.BLUE, BossBarOverlay.PROGRESS)
      .setDarkenScreen(true);
   public float prevWormAngle;
   public float prevJawProgress;
   public float jawProgress;
   public Vec3 teleportPos = null;
   public EntityVoidPortal portalTarget = null;
   public boolean fullyThrough = true;
   public boolean updatePostSummon = false;
   private int makePortalCooldown = 0;
   private int stillTicks = 0;
   private int blockBreakCounter;
   private int makeIdlePortalCooldown = 200 + this.random.nextInt(800);

   protected EntityVoidWorm(EntityType<? extends Monster> type, Level worldIn) {
      super(type, worldIn);
      this.xpReward = 10;
      this.moveControl = new FlightMoveController(this, 1.0F, false, true);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.VOID_WORM_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.VOID_WORM_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.VOID_WORM_HURT.get();
   }

   protected float getSoundVolume() {
      return this.isSilent() ? 0.0F : 5.0F;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.voidWormSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public static boolean canVoidWormSpawn(EntityType animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return true;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, AMConfig.voidWormMaxHealth)
         .add(Attributes.ARMOR, 4.0)
         .add(Attributes.FOLLOW_RANGE, 256.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.ATTACK_DAMAGE, 5.0);
   }

   @Nullable
   protected ResourceKey<LootTable> getDefaultLootTable() {
      return this.isSplitter() ? AMCompat.lootKey(SPLITTER_LOOT) : super.getDefaultLootTable();
   }

   public void kill() {
      this.remove(RemovalReason.DISCARDED);
   }

   public void die(DamageSource cause) {
      super.die(cause);
      if (!this.level().isClientSide() && !this.isSplitter() && cause != null && cause.getEntity() instanceof ServerPlayer) {
         AMAdvancementTriggerRegistry.VOID_WORM_SLAY_HEAD.trigger((ServerPlayer)cause.getEntity());
      }
   }

   public ItemEntity spawnAtLocation(ItemStack stack) {
      ItemEntity itementity = AMCompat.spawnAtLocation(this, stack, 0.0F);
      if (itementity != null) {
         itementity.setNoGravity(true);
         itementity.setGlowingTag(true);
         itementity.setExtendedLifetime();
      }

      return itementity;
   }

   protected void dropAllDeathLoot(ServerLevel level, DamageSource source) {
   }

   private void placeDropsSafely(Collection<ItemEntity> drops) {
      BlockPos pos = this.blockPosition();

      while (!this.level().getBlockState(pos).canBeReplaced() && pos.getY() < AMCompat.maxBuildHeight(this.level()) - 2) {
         pos = pos.above();
      }

      int radius = 2;
      BlockState residue = (BlockState)AMBlockRegistry.ENDER_RESIDUE.get().defaultBlockState().setValue(BlockEnderResidue.SLOW_DECAY, true);

      for (int x = -radius; x <= radius; x++) {
         for (int y = -radius; y <= radius; y++) {
            for (int z = -radius; z <= radius; z++) {
               double sq = x * x + y * y + z * z;
               BlockPos pos1 = pos.offset(x, y, z);
               BlockState state = this.level().getBlockState(pos1);
               if (sq <= radius * radius && sq >= radius * radius - 2.0F && (state.canBeReplaced() || state.is(AMBlockRegistry.ENDER_RESIDUE.get()))) {
                  this.level().setBlockAndUpdate(pos1, residue);
               }
            }
         }
      }

      this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

      for (ItemEntity drop : drops) {
         drop.setPos(Vec3.atBottomCenterOf(pos));
         drop.setGlowingTag(true);
         drop.setNoGravity(true);
         drop.setDefaultPickUpDelay();
         drop.setUnlimitedLifetime();
         drop.setDeltaMovement(Vec3.ZERO);
         this.level().addFreshEntity(drop);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.FALL)
         || source.is(DamageTypes.DROWN)
         || source.is(DamageTypes.IN_WALL)
         || source.is(DamageTypes.LAVA)
         || source.is(DamageTypes.FELL_OUT_OF_WORLD)
         || source.is(DamageTypeTags.IS_FIRE)
         || super.isInvulnerableTo(source);
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new EntityVoidWorm.AIEnterPortal());
      this.goalSelector.addGoal(2, new EntityVoidWorm.AIAttack());
      this.goalSelector.addGoal(3, new EntityVoidWorm.AIFlyIdle());
      this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, Player.class, 10, false, true, null));
      this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EnderDragon.class, 10, false, true, null));
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new DirectPathNavigator(this, this.level());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.hasUUID(compound, "ChildUUID")) {
         this.setChildId(AMCompat.getUUID(compound, "ChildUUID"));
      }

      this.setWormSpeed(AMCompat.getFloat(compound, "WormSpeed"));
      this.setSplitter(AMCompat.getBoolean(compound, "Splitter"));
      this.setPortalTicks(AMCompat.getInt(compound, "PortalTicks"));
      this.makeIdlePortalCooldown = AMCompat.getInt(compound, "MakePortalTime");
      this.makePortalCooldown = AMCompat.getInt(compound, "MakePortalCooldown");
      if (this.hasCustomName()) {
         this.bossInfo.setName(this.getDisplayName());
      }
   }

   public void setCustomName(@Nullable Component name) {
      super.setCustomName(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   public boolean isNoGravity() {
      return true;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getChildId() != null) {
         AMCompat.putUUID(compound, "ChildUUID", this.getChildId());
      }

      compound.putInt("PortalTicks", this.getPortalTicks());
      compound.putInt("MakePortalTime", this.makeIdlePortalCooldown);
      compound.putInt("MakePortalCooldown", this.makePortalCooldown);
      compound.putFloat("WormSpeed", this.getWormSpeed());
      compound.putBoolean("Splitter", this.isSplitter());
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public boolean canBeLeashed(Player player) {
      return true;
   }

   public int getExperienceReward() {
      return this.isSplitter() ? 8 : 50;
   }

   public void tick() {
      super.tick();
      this.prevWormAngle = this.getWormAngle();
      this.prevJawProgress = this.jawProgress;
      float threshold = 0.05F;
      if (this.isSplitter()) {
         this.xpReward = 10;
      } else {
         this.xpReward = 70;
      }

      if (this.yRotO - this.getYRot() > threshold) {
         this.setWormAngle(this.getWormAngle() + 15.0F);
      } else if (this.yRotO - this.getYRot() < -threshold) {
         this.setWormAngle(this.getWormAngle() - 15.0F);
      } else if (this.getWormAngle() > 0.0F) {
         this.setWormAngle(Math.max(this.getWormAngle() - 20.0F, 0.0F));
      } else if (this.getWormAngle() < 0.0F) {
         this.setWormAngle(Math.min(this.getWormAngle() + 20.0F, 0.0F));
      }

      if (!this.level().isClientSide()) {
         if (!this.fullyThrough) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8999999761581421, 0.8999999761581421, 0.8999999761581421).add(0.0, -0.01, 0.0));
         } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.01, 0.0));
         }
      }

      if (Math.abs(this.xo - this.getX()) < 0.009999999776482582
         && Math.abs(this.yo - this.getY()) < 0.009999999776482582
         && Math.abs(this.zo - this.getZ()) < 0.009999999776482582) {
         this.stillTicks++;
      } else {
         this.stillTicks = 0;
      }

      if (this.stillTicks > 40 && this.makePortalCooldown == 0) {
         this.createStuckPortal();
      }

      if (this.makePortalCooldown > 0) {
         this.makePortalCooldown--;
      }

      if (this.makeIdlePortalCooldown > 0) {
         this.makeIdlePortalCooldown--;
      }

      if (this.makeIdlePortalCooldown == 0 && this.random.nextInt(100) == 0) {
         this.createPortalRandomDestination();
         this.makeIdlePortalCooldown = 200 + this.random.nextInt(1000);
      }

      if ((Integer)this.entityData.get(JAW_TICKS) > 0) {
         if (this.jawProgress < 5.0F) {
            this.jawProgress++;
         }

         this.entityData.set(JAW_TICKS, (Integer)this.entityData.get(JAW_TICKS) - 1);
      } else if (this.jawProgress > 0.0F) {
         this.jawProgress--;
      }

      if (this.isAlive()) {
         for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0))) {
            if (!entity.is(this) && !(entity instanceof EntityVoidWormPart) && !entity.isAlliedTo(this) && entity != this) {
               this.launch(entity, false);
            }
         }

         AMCompat.setMaxUpStep(this, 2.0F);
      } else {
         this.setDeltaMovement(new Vec3(0.0, 0.029999999329447746, 0.0));
      }

      this.yBodyRot = this.getYRot();
      float f2 = (float)(-((float)this.getDeltaMovement().y * 57.2957763671875));
      this.setXRot(f2);
      AMCompat.setMaxUpStep(this, 2.0F);
      if (!this.level().isClientSide()) {
         Entity child = this.getChild();
         if (child == null) {
            LivingEntity partParent = this;
            int tailstart = Math.min(3 + this.random.nextInt(2), this.getSegmentCount());
            int segments = this.getSegmentCount();

            for (int i = 0; i < segments; i++) {
               float scale = 1.0F + (float)i / segments * 0.5F;
               boolean tail = false;
               if (i >= segments - tailstart) {
                  tail = true;
                  scale *= 0.85F;
               }

               EntityVoidWormPart part = new EntityVoidWormPart(
                  AMEntityRegistry.VOID_WORM_PART.get(),
                  partParent,
                  1.0F + scale * (tail ? 0.65F : 0.3F) + (i == 0 ? 0.8F : 0.0F),
                  180.0F,
                  i == 0 ? -0.0F : (i == segments - tailstart ? -0.3F : 0.0F)
               );
               part.setInvulnerable(partParent.isInvulnerable());
               part.setParent(partParent);
               if (this.updatePostSummon) {
                  part.setPortalTicks(i * 2);
               }

               part.setBodyIndex(i);
               part.setTail(tail);
               part.setWormScale(scale);
               if (partParent == this) {
                  this.setChildId(part.getUUID());
               } else if (partParent instanceof EntityVoidWormPart) {
                  ((EntityVoidWormPart)partParent).setChildId(part.getUUID());
               }

               part.setInitialPartPos(this);
               partParent = part;
               this.level().addFreshEntity(part);
            }
         }
      }

      if (this.getPortalTicks() > 0) {
         this.setPortalTicks(this.getPortalTicks() - 1);
         if (this.getPortalTicks() == 2 && this.teleportPos != null) {
            this.setPos(this.teleportPos.x, this.teleportPos.y, this.teleportPos.z);
            this.teleportPos = null;
         }
      }

      if (this.portalTarget != null && this.portalTarget.getLifespan() < 5) {
         this.portalTarget = null;
      }

      this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
      this.breakBlock();
      if (this.updatePostSummon) {
         this.updatePostSummon = false;
      }

      if (!this.isSilent() && !this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)67);
      }
   }

   public double getBaseMaxHealth() {
      return this.getAttributeBaseValue(Attributes.MAX_HEALTH);
   }

   public void setBaseMaxHealth(double maxHealth, boolean heal) {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
      if (heal) {
         this.heal(this.getMaxHealth());
      }
   }

   protected void tickDeath() {
      this.deathTime++;
      if (this.deathTime == (this.isSplitter() ? 20 : 80) && !this.level().isClientSide()) {
         DamageSource source = this.getLastDamageSource() == null ? this.damageSources().generic() : this.getLastDamageSource();
         Entity entity = source.getEntity();
         ServerLevel serverLevel = (ServerLevel)this.level();
         this.captureDrops(new ArrayList());
         boolean flag = this.lastHurtByPlayerTime > 0;
         if (this.shouldDropLoot() && AMCompat.gameRule(this.level(), AMCompat.Rule.MOB_LOOT)) {
            this.dropFromLootTable(source, flag);
            this.dropCustomDeathLoot(serverLevel, source, flag);
         }

         this.dropEquipment();
         this.dropExperience(entity);
         Collection<ItemEntity> drops = this.captureDrops(null);
         if (!((LivingDropsEvent)NeoForge.EVENT_BUS.post(new LivingDropsEvent(this, source, drops, this.lastHurtByPlayerTime > 0))).isCanceled()
            && !drops.isEmpty()) {
            this.placeDropsSafely(drops);
         }

         this.level().broadcastEntityEvent(this, (byte)60);
         this.remove(RemovalReason.KILLED);
      }
   }

   public void startSeenByPlayer(ServerPlayer player) {
      super.startSeenByPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public void stopSeenByPlayer(ServerPlayer player) {
      super.stopSeenByPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public void teleportTo(Vec3 vec) {
      this.setPortalTicks(10);
      this.teleportPos = vec;
      this.fullyThrough = false;
      if (this.getChild() instanceof EntityVoidWormPart) {
         ((EntityVoidWormPart)this.getChild()).teleportTo(this.position(), this.teleportPos);
      }
   }

   private void launch(Entity e, boolean huge) {
      if (e.onGround()) {
         double d0 = e.getX() - this.getX();
         double d1 = e.getZ() - this.getZ();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = huge ? 2.0F : 0.5F;
         e.push(d0 / d2 * f, huge ? 0.5 : 0.20000000298023224, d1 / d2 * f);
      }
   }

   public void resetWormScales() {
      if (!this.level().isClientSide()) {
         Entity child = this.getChild();
         if (child == null) {
            LivingEntity nextPart = this;
            int tailstart = Math.min(3 + this.random.nextInt(2), this.getSegmentCount());
            int segments = this.getSegmentCount();
            int i = 0;

            while (nextPart instanceof EntityVoidWormPart) {
               EntityVoidWormPart part = (EntityVoidWormPart)((EntityVoidWormPart)nextPart).getChild();
               i++;
               float scale = 1.0F + (float)i / segments * 0.5F;
               boolean tail = i >= segments - tailstart;
               part.setTail(tail);
               part.setWormScale(scale);
               part.radius = 1.0F + scale * (tail ? 0.65F : 0.3F) + (i == 0 ? 0.8F : 0.0F);
               part.offsetY = i == 0 ? -0.0F : (i == segments - tailstart ? -0.3F : 0.0F);
               nextPart = part;
            }
         }
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setSegmentCount(25 + this.random.nextInt(15));
      this.setXRot(0.0F);
      this.setBaseMaxHealth(AMConfig.voidWormMaxHealth, true);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SPLIT_FROM_UUID, Optional.empty());
      builder.define(CHILD_UUID, Optional.empty());
      builder.define(SEGMENT_COUNT, 10);
      builder.define(JAW_TICKS, 0);
      builder.define(WORM_ANGLE, 0.0F);
      builder.define(SPEEDMOD, 1.0F);
      builder.define(SPLITTER, false);
      builder.define(PORTAL_TICKS, 0);
   }

   public float getWormAngle() {
      return (Float)this.entityData.get(WORM_ANGLE);
   }

   public void setWormAngle(float progress) {
      this.entityData.set(WORM_ANGLE, progress);
   }

   public float getWormSpeed() {
      return (Float)this.entityData.get(SPEEDMOD);
   }

   public void setWormSpeed(float progress) {
      if (this.getWormSpeed() != progress) {
         this.moveControl = new FlightMoveController(this, progress, false, true);
      }

      this.entityData.set(SPEEDMOD, progress);
   }

   public boolean isSplitter() {
      return (Boolean)this.entityData.get(SPLITTER);
   }

   public void setSplitter(boolean splitter) {
      this.entityData.set(SPLITTER, splitter);
   }

   public void openMouth(int time) {
      this.entityData.set(JAW_TICKS, time);
   }

   public boolean isMouthOpen() {
      return ((Integer)this.entityData.get(JAW_TICKS)).intValue() >= 5.0F;
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public UUID getSplitFromUUID() {
      return (UUID)((Optional)this.entityData.get(SPLIT_FROM_UUID)).orElse(null);
   }

   public void setSplitFromUuid(@Nullable UUID uniqueId) {
      this.entityData.set(SPLIT_FROM_UUID, Optional.ofNullable(uniqueId));
   }

   public int getPortalTicks() {
      return (Integer)this.entityData.get(PORTAL_TICKS);
   }

   public void setPortalTicks(int ticks) {
      this.entityData.set(PORTAL_TICKS, ticks);
   }

   public int getSegmentCount() {
      return (Integer)this.entityData.get(SEGMENT_COUNT);
   }

   public void setSegmentCount(int command) {
      this.entityData.set(SEGMENT_COUNT, command);
   }

   public void pushEntities() {
      List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.20000000298023224, 0.0, 0.20000000298023224));
      entities.stream().filter(entity -> !(entity instanceof EntityVoidWormPart) && entity.isPushable()).forEach(entity -> entity.push(this));
   }

   public void push(Entity entityIn) {
   }

   public void createStuckPortal() {
      if (this.getTarget() != null) {
         this.createPortal(this.getTarget().position().add(this.random.nextInt(8) - 4, 2 + this.random.nextInt(3), this.random.nextInt(8) - 4));
      } else {
         Vec3 vec = Vec3.atCenterOf(this.level().getHeightmapPos(Types.MOTION_BLOCKING, this.blockPosition().above(this.random.nextInt(10) + 10)));
         this.createPortal(vec);
      }
   }

   public void createPortal(Vec3 to) {
      this.createPortal(this.position().add(this.getLookAngle().scale(20.0)), to, null);
   }

   public void createPortalRandomDestination() {
      Vec3 vec = null;

      for (int i = 0; i < 15; i++) {
         BlockPos pos = AMBlockPos.fromCoords(this.getX() + this.random.nextInt(60) - 30.0, 0.0, this.getZ() + this.random.nextInt(60) - 30.0);
         BlockPos height = this.level().getHeightmapPos(Types.MOTION_BLOCKING, pos);
         if (height.getY() < 10) {
            height = height.above(50 + this.random.nextInt(50));
         } else {
            height = height.above(this.random.nextInt(30));
         }

         if (this.level().isEmptyBlock(height)) {
            vec = Vec3.atBottomCenterOf(height);
         }
      }

      if (vec != null) {
         this.createPortal(this.position().add(this.getLookAngle().scale(20.0)), vec, null);
      }
   }

   public void createPortal(Vec3 from, Vec3 to, @Nullable Direction outDir) {
      if (!this.level().isClientSide() && this.portalTarget == null) {
         Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
         HitResult result = this.level().clip(new ClipContext(Vector3d, from, Block.COLLIDER, Fluid.NONE, this));
         Vec3 vec = result.getLocation() != null ? result.getLocation() : this.position();
         if (result instanceof BlockHitResult result1) {
            vec = vec.add(Vec3.atLowerCornerOf(result1.getDirection().getNormal()));
         }

         EntityVoidPortal portal = AMCompat.create(AMEntityRegistry.VOID_PORTAL.get(), this.level());
         portal.setPos(vec.x, vec.y, vec.z);
         Vec3 dirVec = vec.subtract(this.position());
         Direction dir = Direction.getNearest(dirVec.x, dirVec.y, dirVec.z);
         portal.setAttachmentFacing(dir);
         portal.setLifespan(10000);
         if (!this.level().isClientSide()) {
            this.level().addFreshEntity(portal);
         }

         this.portalTarget = portal;
         portal.setDestination(AMBlockPos.fromCoords(to.x, to.y, to.z), outDir);
         this.makePortalCooldown = 300;
      }
   }

   public void resetPortalLogic() {
      this.portalTarget = null;
      this.stillTicks = 0;
   }

   public boolean isPushable() {
      return false;
   }

   public void breakBlock() {
      if (this.blockBreakCounter > 0) {
         this.blockBreakCounter--;
      } else {
         boolean flag = false;
         if (!this.level().isClientSide() && this.blockBreakCounter == 0 && AMPlatform.mobGriefing(this.level(), this)) {
            for (int a = (int)Math.round(this.getBoundingBox().minX); a <= (int)Math.round(this.getBoundingBox().maxX); a++) {
               for (int b = (int)Math.round(this.getBoundingBox().minY) - 1; b <= (int)Math.round(this.getBoundingBox().maxY) + 1 && b <= 127; b++) {
                  for (int c = (int)Math.round(this.getBoundingBox().minZ); c <= (int)Math.round(this.getBoundingBox().maxZ); c++) {
                     BlockPos pos = new BlockPos(a, b, c);
                     BlockState state = this.level().getBlockState(pos);
                     FluidState fluidState = this.level().getFluidState(pos);
                     net.minecraft.world.level.block.Block block = state.getBlock();
                     if (!state.isAir()
                        && !state.getShape(this.level(), pos).isEmpty()
                        && state.is(AMTagRegistry.VOID_WORM_BREAKABLES)
                        && fluidState.isEmpty()
                        && block != Blocks.AIR) {
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.6000000238418579, 1.0, 0.6000000238418579));
                        flag = true;
                        this.level().destroyBlock(pos, true);
                        if (state.is(BlockTags.ICE)) {
                           this.level().setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                        }
                     }
                  }
               }
            }
         }

         if (flag) {
            this.blockBreakCounter = 10;
         }
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).getType() != Type.MISS;
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = (-9.45F - this.getRandom().nextInt(24)) * radiusAdd;
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, 0.0, fleePos.z() + extraZ);
      BlockPos ground = this.getGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 10 + this.getRandom().nextInt(20);
      BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : this.getRandom().nextInt(10) + 15);
      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   public Vec3 getBlockInViewAwaySlam(Vec3 fleePos, int slamHeight) {
      float radius = 3 + this.random.nextInt(3);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, 0.0, fleePos.z() + extraZ);
      BlockPos ground = this.getHeighestAirAbove(radialPos, slamHeight);
      return !this.isTargetBlocked(Vec3.atCenterOf(ground)) && this.distanceToSqr(Vec3.atCenterOf(ground)) > 1.0 ? Vec3.atCenterOf(ground) : null;
   }

   private BlockPos getHeighestAirAbove(BlockPos radialPos, int limit) {
      BlockPos position = AMBlockPos.fromCoords(radialPos.getX(), this.getY(), radialPos.getZ());

      while (position.getY() < 256 && position.getY() < this.getY() + limit && this.level().isEmptyBlock(position)) {
         position = position.above();
      }

      return position;
   }

   private BlockPos getGround(BlockPos in) {
      BlockPos position = AMBlockPos.fromCoords(in.getX(), this.getY(), in.getZ());

      while (position.getY() > -63 && !this.level().getBlockState(position).isSolid()) {
         position = position.below();
      }

      return position.getY() < -62 ? position.above(120 + this.random.nextInt(5)) : position;
   }

   public boolean isAlliedTo(Entity entityIn) {
      return super.isAlliedTo(entityIn)
         || this.getSplitFromUUID() != null && this.getSplitFromUUID().equals(entityIn.getUUID())
         || entityIn instanceof EntityVoidWorm
            && ((EntityVoidWorm)entityIn).getSplitFromUUID() != null
            && ((EntityVoidWorm)entityIn).getSplitFromUUID().equals(entityIn.getUUID());
   }

   private void spit(Vec3 shotAt, boolean portal) {
      shotAt = shotAt.yRot(-this.getYRot() * 0.017453292F);
      EntityVoidWormShot shot = new EntityVoidWormShot(this.level(), this);
      double d0 = shotAt.x;
      double d1 = shotAt.y;
      double d2 = shotAt.z;
      float f = Mth.sqrt((float)(d0 * d0 + d2 * d2)) * 0.35F;
      shot.shoot(d0, d1 + f, d2, 0.5F, 3.0F);
      if (!this.isSilent()) {
         this.gameEvent(GameEvent.PROJECTILE_SHOOT);
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.DROWNED_SHOOT,
               this.getSoundSource(),
               1.0F,
               1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
            );
      }

      this.openMouth(5);
      this.level().addFreshEntity(shot);
   }

   private boolean wormAttack(Entity entity, DamageSource source, float dmg) {
      dmg = (float)(dmg * AMConfig.voidWormDamageModifier);
      return entity instanceof EnderDragon ? ((EnderDragon)entity).reallyHurt(source, dmg * 0.5F) : entity.hurt(source, dmg);
   }

   public void playHurtSoundWorm(DamageSource source) {
      this.playHurtSound(source);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 67) {
         AlexsMobs.PROXY.onEntityStatus(this, id);
      } else {
         super.handleEntityEvent(id);
      }
   }

   public class AIAttack extends Goal {
      private EntityVoidWorm.AttackMode mode = EntityVoidWorm.AttackMode.CIRCLE;
      private int modeTicks = 0;
      private int maxCircleTime = 500;
      private Vec3 moveTo = null;

      public AIAttack() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return EntityVoidWorm.this.getTarget() != null && EntityVoidWorm.this.getTarget().isAlive();
      }

      public void stop() {
         this.mode = EntityVoidWorm.AttackMode.CIRCLE;
         this.modeTicks = 0;
      }

      public void start() {
         this.mode = EntityVoidWorm.AttackMode.CIRCLE;
         this.maxCircleTime = 60 + EntityVoidWorm.this.random.nextInt(200);
      }

      public void tick() {
         LivingEntity target = EntityVoidWorm.this.getTarget();
         boolean flag = false;
         float speed = 1.0F;

         for (Entity entity : EntityVoidWorm.this.level().getEntitiesOfClass(LivingEntity.class, EntityVoidWorm.this.getBoundingBox().inflate(2.0))) {
            if (!entity.is(EntityVoidWorm.this)
               && !(entity instanceof EntityVoidWormPart)
               && !entity.isAlliedTo(EntityVoidWorm.this)
               && entity != EntityVoidWorm.this) {
               if (EntityVoidWorm.this.isMouthOpen()) {
                  EntityVoidWorm.this.launch(entity, true);
                  flag = true;
                  EntityVoidWorm.this.wormAttack(
                     entity, EntityVoidWorm.this.damageSources().mobAttack(EntityVoidWorm.this), 8.0F + EntityVoidWorm.this.random.nextFloat() * 8.0F
                  );
               } else {
                  EntityVoidWorm.this.openMouth(15);
               }
            }
         }

         if (target != null) {
            if (this.mode == EntityVoidWorm.AttackMode.CIRCLE) {
               if (this.moveTo == null || EntityVoidWorm.this.distanceToSqr(this.moveTo) < 16.0 || EntityVoidWorm.this.horizontalCollision) {
                  this.moveTo = EntityVoidWorm.this.getBlockInViewAway(target.position(), 0.4F + EntityVoidWorm.this.random.nextFloat() * 0.2F);
               }

               int interval = EntityVoidWorm.this.getHealth() < EntityVoidWorm.this.getMaxHealth() && !EntityVoidWorm.this.isSplitter() ? 15 : 40;
               if (this.modeTicks % interval == 0) {
                  EntityVoidWorm.this.spit(new Vec3(3.0, 3.0, 0.0), false);
                  EntityVoidWorm.this.spit(new Vec3(-3.0, 3.0, 0.0), false);
                  EntityVoidWorm.this.spit(new Vec3(3.0, -3.0, 0.0), false);
                  EntityVoidWorm.this.spit(new Vec3(-3.0, -3.0, 0.0), false);
               }

               this.modeTicks++;
               if (this.modeTicks > this.maxCircleTime) {
                  this.maxCircleTime = 60 + EntityVoidWorm.this.random.nextInt(200);
                  this.mode = EntityVoidWorm.AttackMode.SLAM_RISE;
                  this.modeTicks = 0;
                  this.moveTo = null;
               }
            } else if (this.mode == EntityVoidWorm.AttackMode.SLAM_RISE) {
               if (this.moveTo == null) {
                  this.moveTo = EntityVoidWorm.this.getBlockInViewAwaySlam(target.position(), 20 + EntityVoidWorm.this.random.nextInt(20));
               }

               if (this.moveTo != null && EntityVoidWorm.this.getY() > target.getY() + 15.0) {
                  this.moveTo = null;
                  this.modeTicks = 0;
                  this.mode = EntityVoidWorm.AttackMode.SLAM_FALL;
               }
            } else if (this.mode == EntityVoidWorm.AttackMode.SLAM_FALL) {
               speed = 2.0F;
               EntityVoidWorm.this.lookAt(target, 360.0F, 360.0F);
               this.moveTo = target.position();
               if (EntityVoidWorm.this.horizontalCollision) {
                  this.moveTo = new Vec3(target.getX(), EntityVoidWorm.this.getY() + 3.0, target.getZ());
               }

               EntityVoidWorm.this.openMouth(20);
               if (EntityVoidWorm.this.distanceToSqr(this.moveTo) < 4.0 || flag) {
                  this.mode = EntityVoidWorm.AttackMode.CIRCLE;
                  this.moveTo = null;
                  this.modeTicks = 0;
               }
            }
         }

         if (!EntityVoidWorm.this.hasLineOfSight(target) && EntityVoidWorm.this.random.nextInt(100) == 0 && EntityVoidWorm.this.makePortalCooldown == 0) {
            Vec3 to = new Vec3(target.getX(), target.getBoundingBox().maxY + 0.1, target.getZ());
            EntityVoidWorm.this.createPortal(EntityVoidWorm.this.position().add(EntityVoidWorm.this.getLookAngle().scale(20.0)), to, Direction.UP);
            EntityVoidWorm.this.makePortalCooldown = 50;
            this.mode = EntityVoidWorm.AttackMode.SLAM_FALL;
         }

         if (this.moveTo != null && EntityVoidWorm.this.portalTarget == null) {
            EntityVoidWorm.this.getMoveControl().setWantedPosition(this.moveTo.x, this.moveTo.y, this.moveTo.z, speed);
         }
      }
   }

   public class AIEnterPortal extends Goal {
      public AIEnterPortal() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return EntityVoidWorm.this.portalTarget != null;
      }

      public void tick() {
         if (EntityVoidWorm.this.portalTarget != null) {
            EntityVoidWorm.this.noPhysics = true;
            double centerX = EntityVoidWorm.this.portalTarget.getX();
            double centerY = EntityVoidWorm.this.portalTarget.getY(0.5);
            double centerZ = EntityVoidWorm.this.portalTarget.getZ();
            double d0 = centerX - EntityVoidWorm.this.getX();
            double d1 = centerY - EntityVoidWorm.this.getY(0.5);
            double d2 = centerZ - EntityVoidWorm.this.getZ();
            Vec3 vec = new Vec3(d0, d1, d2);
            if (vec.length() > 1.0) {
               vec = vec.normalize();
            }

            vec = vec.scale(0.4000000059604645);
            EntityVoidWorm.this.setDeltaMovement(EntityVoidWorm.this.getDeltaMovement().add(vec));
         }
      }

      public void stop() {
         EntityVoidWorm.this.noPhysics = false;
      }
   }

   private class AIFlyIdle extends Goal {
      protected final EntityVoidWorm voidWorm;
      protected double x;
      protected double y;
      protected double z;

      public AIFlyIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.voidWorm = EntityVoidWorm.this;
      }

      public boolean canUse() {
         if (!this.voidWorm.isVehicle()
            && this.voidWorm.portalTarget == null
            && (this.voidWorm.getTarget() == null || !this.voidWorm.getTarget().isAlive())
            && !this.voidWorm.isPassenger()) {
            Vec3 lvt_1_1_ = this.getPosition();
            if (lvt_1_1_ == null) {
               return false;
            } else {
               this.x = lvt_1_1_.x;
               this.y = lvt_1_1_.y;
               this.z = lvt_1_1_.z;
               return true;
            }
         } else {
            return false;
         }
      }

      public void tick() {
         this.voidWorm.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.voidWorm.position();
         return this.voidWorm.getBlockInViewAway(vector3d, 1.0F);
      }

      public boolean canContinueToUse() {
         return this.voidWorm.distanceToSqr(this.x, this.y, this.z) > 20.0
            && this.voidWorm.portalTarget == null
            && !this.voidWorm.horizontalCollision
            && (this.voidWorm.getTarget() == null || !this.voidWorm.getTarget().isAlive());
      }

      public void start() {
         this.voidWorm.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
      }

      public void stop() {
         this.voidWorm.getNavigation().stop();
         super.stop();
      }
   }

   private static enum AttackMode {
      CIRCLE,
      SLAM_RISE,
      SLAM_FALL,
      PORTAL;
   }
}
