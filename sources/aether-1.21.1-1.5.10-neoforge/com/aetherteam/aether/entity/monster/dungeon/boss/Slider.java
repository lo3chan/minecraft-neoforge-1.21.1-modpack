package com.aetherteam.aether.entity.monster.dungeon.boss;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.AetherBossMob;
import com.aetherteam.aether.entity.ai.controller.BlankMoveControl;
import com.aetherteam.aether.entity.ai.goal.MostDamageTargetGoal;
import com.aetherteam.aether.entity.monster.dungeon.boss.goal.AvoidObstaclesGoal;
import com.aetherteam.aether.entity.monster.dungeon.boss.goal.BackOffAfterAttackGoal;
import com.aetherteam.aether.entity.monster.dungeon.boss.goal.CollideGoal;
import com.aetherteam.aether.entity.monster.dungeon.boss.goal.CrushGoal;
import com.aetherteam.aether.entity.monster.dungeon.boss.goal.SetPathUpOrDownGoal;
import com.aetherteam.aether.entity.monster.dungeon.boss.goal.SliderMoveGoal;
import com.aetherteam.aether.entity.monster.dungeon.boss.goal.SliderNearestAttackableTargetGoal;
import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.network.packet.clientbound.BossInfoPacket;
import com.aetherteam.nitrogen.entity.BossRoomTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

public class Slider extends PathfinderMob implements AetherBossMob<Slider>, Enemy, IEntityWithComplexSpawn {
   private static final EntityDataAccessor<Boolean> DATA_AWAKE_ID = SynchedEntityData.defineId(Slider.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Component> DATA_BOSS_NAME_ID = SynchedEntityData.defineId(Slider.class, EntityDataSerializers.COMPONENT);
   private static final EntityDataAccessor<Float> DATA_HURT_ANGLE_ID = SynchedEntityData.defineId(Slider.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_HURT_ANGLE_X_ID = SynchedEntityData.defineId(Slider.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_HURT_ANGLE_Z_ID = SynchedEntityData.defineId(Slider.class, EntityDataSerializers.FLOAT);
   private static final Music SLIDER_MUSIC = new Music(AetherSoundEvents.MUSIC_BOSS_SLIDER, 0, 0, true);
   public static final Map<Block, Function<BlockState, BlockState>> DUNGEON_BLOCK_CONVERSIONS = new HashMap<>(
      Map.ofEntries(
         Map.entry((Block)AetherBlocks.LOCKED_CARVED_STONE.get(), blockState -> ((Block)AetherBlocks.CARVED_STONE.get()).defaultBlockState()),
         Map.entry((Block)AetherBlocks.LOCKED_SENTRY_STONE.get(), blockState -> ((Block)AetherBlocks.SENTRY_STONE.get()).defaultBlockState()),
         Map.entry((Block)AetherBlocks.BOSS_DOORWAY_CARVED_STONE.get(), blockState -> Blocks.AIR.defaultBlockState()),
         Map.entry(
            (Block)AetherBlocks.TREASURE_DOORWAY_CARVED_STONE.get(),
            blockState -> (BlockState)((TrapDoorBlock)AetherBlocks.SKYROOT_TRAPDOOR.get())
               .defaultBlockState()
               .setValue(HorizontalDirectionalBlock.FACING, (Direction)blockState.getValue(HorizontalDirectionalBlock.FACING))
         )
      )
   );
   private MostDamageTargetGoal mostDamageTargetGoal;
   private final ServerBossEvent bossFight;
   @Nullable
   private BossRoomTracker<Slider> bronzeDungeon;
   private int chatCooldown;
   private Direction moveDirection = null;
   private int moveDelay = this.calculateMoveDelay();
   private Vec3 targetPoint = null;
   private int attackCooldown = 0;

   public Slider(EntityType<? extends Slider> type, Level level) {
      super(type, level);
      this.moveControl = new BlankMoveControl(this);
      this.bossFight = (ServerBossEvent)new ServerBossEvent(this.getBossName(), BossBarColor.RED, BossBarOverlay.PROGRESS).setPlayBossMusic(true);
      this.setBossFight(false);
      this.xpReward = 50;
      this.setRot(0.0F, 0.0F);
      this.setPersistenceRequired();
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      this.setBossName(BossNameGenerator.generateSliderName(this.getRandom()));
      this.moveTo(Mth.floor(this.getX()), this.getY(), Mth.floor(this.getZ()));
      return spawnData;
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 400.0).add(Attributes.FOLLOW_RANGE, 64.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new CollideGoal(this));
      this.goalSelector.addGoal(2, new CrushGoal(this));
      this.goalSelector.addGoal(3, new BackOffAfterAttackGoal(this));
      this.goalSelector.addGoal(4, new SetPathUpOrDownGoal(this));
      this.goalSelector.addGoal(5, new AvoidObstaclesGoal(this));
      this.goalSelector.addGoal(6, new SliderMoveGoal(this));
      this.mostDamageTargetGoal = new MostDamageTargetGoal(this);
      this.targetSelector.addGoal(1, this.mostDamageTargetGoal);
      this.targetSelector.addGoal(2, new SliderNearestAttackableTargetGoal(this, Player.class, false));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_AWAKE_ID, false);
      builder.define(DATA_BOSS_NAME_ID, Component.literal("Slider"));
      builder.define(DATA_HURT_ANGLE_ID, 0.0F);
      builder.define(DATA_HURT_ANGLE_X_ID, 0.0F);
      builder.define(DATA_HURT_ANGLE_Z_ID, 0.0F);
   }

   public void tick() {
      super.tick();
      if (!this.isAwake() || this.getTarget() instanceof Player player && (player.isCreative() || player.isSpectator())) {
         this.setTarget(null);
      }

      this.evaporate();
      if (this.getChatCooldown() > 0) {
         this.chatCooldown--;
      }
   }

   private void evaporate() {
      Pair<BlockPos, BlockPos> minMax = this.getDefaultBounds(this);
      AetherBossMob.super.evaporate(this, (BlockPos)minMax.getLeft(), (BlockPos)minMax.getRight(), blockState -> true);
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      this.bossFight.setProgress(this.getHealth() / this.getMaxHealth());
      this.trackDungeon();
      if (this.moveDelay > 0) {
         this.moveDelay--;
      }

      if (this.attackCooldown > 0) {
         this.attackCooldown--;
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      Optional<LivingEntity> damageResult = this.canDamageSlider(source);
      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         super.hurt(source, amount);
         if (!this.level().isClientSide() && source.getEntity() instanceof LivingEntity living) {
            this.mostDamageTargetGoal.addAggro(living, amount);
         }
      } else if (damageResult.isPresent()) {
         LivingEntity attacker = damageResult.get();
         if (super.hurt(source, amount) && this.getHealth() > 0.0F) {
            if (!this.isBossFight()) {
               this.start();
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.75));
            double a = Math.abs(this.position().x() - attacker.position().x());
            double c = Math.abs(this.position().z() - attacker.position().z());
            if (a > c) {
               this.setHurtAngleZ(1.0F);
               this.setHurtAngleX(0.0F);
               if (this.position().x() > attacker.position().x()) {
                  this.setHurtAngleZ(-1.0F);
               }
            } else {
               this.setHurtAngleX(1.0F);
               this.setHurtAngleZ(0.0F);
               if (this.position().z() > attacker.position().z()) {
                  this.setHurtAngleX(-1.0F);
               }
            }

            this.setHurtAngle(0.7F - this.getHealth() / 875.0F);
            if (!this.level().isClientSide() && source.getEntity() instanceof LivingEntity living) {
               this.mostDamageTargetGoal.addAggro(living, amount);
            }

            return true;
         }
      }

      return false;
   }

   private Optional<LivingEntity> canDamageSlider(DamageSource source) {
      if (this.level().getDifficulty() != Difficulty.PEACEFUL) {
         if (source.getDirectEntity() instanceof LivingEntity attacker) {
            if (this.getDungeon() == null || this.getDungeon().isPlayerWithinRoomInterior(attacker)) {
               if (!attacker.getMainHandItem().canPerformAction(ItemAbilities.PICKAXE_DIG)
                  && !attacker.getMainHandItem().is(AetherTags.Items.SLIDER_DAMAGING_ITEMS)
                  && !attacker.getMainHandItem().isCorrectToolForDrops(((Block)AetherBlocks.CARVED_STONE.get()).defaultBlockState())) {
                  return this.sendInvalidToolMessage(attacker);
               }

               return Optional.of(attacker);
            }

            this.sendTooFarMessage(attacker);
         } else if (source.getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity attacker) {
            if (this.getDungeon() != null && !this.getDungeon().isPlayerWithinRoomInterior(attacker)) {
               return this.sendTooFarMessage(attacker);
            }

            if (projectile.getType().is(AetherTags.Entities.SLIDER_DAMAGING_PROJECTILES)) {
               return Optional.of(attacker);
            }

            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0));
            return this.sendInvalidToolMessage(attacker);
         }
      }

      return Optional.empty();
   }

   private Optional<LivingEntity> sendInvalidToolMessage(LivingEntity attacker) {
      if (!this.level().isClientSide() && attacker instanceof Player player && this.getChatCooldown() <= 0) {
         if ((Boolean)AetherConfig.COMMON.reposition_slider_message.get()) {
            player.displayClientMessage(Component.translatable("gui.aether.slider.message.attack.invalid"), true);
         } else {
            player.sendSystemMessage(Component.translatable("gui.aether.slider.message.attack.invalid"));
         }

         this.setChatCooldown(15);
      }

      return Optional.empty();
   }

   private Optional<LivingEntity> sendTooFarMessage(LivingEntity attacker) {
      if (!this.level().isClientSide() && attacker instanceof Player player && this.getChatCooldown() <= 0) {
         this.displayTooFarMessage(player);
         this.setChatCooldown(15);
      }

      return Optional.empty();
   }

   private void start() {
      if (this.getAwakenSound() != null) {
         this.playSound(this.getAwakenSound(), 2.5F, 1.0F / (this.getRandom().nextFloat() * 0.2F + 0.9F));
      }

      this.setHealth(this.getMaxHealth());
      this.setAwake(true);
      this.setBossFight(true);
      if (this.getDungeon() != null) {
         this.closeRoom();
      }

      AetherEventDispatch.onBossFightStart(this, this.getDungeon());
   }

   public void reset() {
      this.setDeltaMovement(Vec3.ZERO);
      this.setAwake(false);
      this.setBossFight(false);
      this.setTarget(null);
      if (this.getDungeon() != null) {
         this.setPos(this.getDungeon().originCoordinates());
         this.openRoom();
      }

      AetherEventDispatch.onBossFightStop(this, this.getDungeon());
   }

   public void die(DamageSource source) {
      this.setDeltaMovement(Vec3.ZERO);
      this.explode();
      if (this.level() instanceof ServerLevel) {
         this.bossFight.setProgress(this.getHealth() / this.getMaxHealth());
         if (this.getDungeon() != null) {
            this.getDungeon().grantAdvancements(source);
            this.tearDownRoom();
         }
      }

      super.die(source);
   }

   private void explode() {
      for (int i = 0; i < (this.getHealth() <= 0.0F ? 16 : 48); i++) {
         double x = this.position().x() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 1.5;
         double y = this.getBoundingBox().minY + 1.75 + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 1.5;
         double z = this.position().z() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 1.5;
         this.level().addParticle(ParticleTypes.POOF, x, y, z, 0.0, 0.0, 0.0);
      }
   }

   public void knockback(double strength, double x, double z) {
   }

   public void push(double x, double y, double z) {
   }

   public void checkDespawn() {
   }

   @Nullable
   public BlockState convertBlock(BlockState state) {
      return DUNGEON_BLOCK_CONVERSIONS.getOrDefault(state.getBlock(), blockState -> null).apply(state);
   }

   public void startSeenByPlayer(ServerPlayer player) {
      super.startSeenByPlayer(player);
      PacketDistributor.sendToPlayer(player, new BossInfoPacket.Display(this.bossFight.getId(), this.getId()), new CustomPacketPayload[0]);
      if (this.getDungeon() == null || this.getDungeon().isPlayerTracked(player)) {
         this.bossFight.addPlayer(player);
         AetherEventDispatch.onBossFightPlayerAdd(this, this.getDungeon(), player);
      }
   }

   public void stopSeenByPlayer(ServerPlayer player) {
      super.stopSeenByPlayer(player);
      PacketDistributor.sendToPlayer(player, new BossInfoPacket.Remove(this.bossFight.getId(), this.getId()), new CustomPacketPayload[0]);
      this.bossFight.removePlayer(player);
      AetherEventDispatch.onBossFightPlayerRemove(this, this.getDungeon(), player);
   }

   public void onDungeonPlayerAdded(@Nullable Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         this.bossFight.addPlayer(serverPlayer);
         AetherEventDispatch.onBossFightPlayerAdd(this, this.getDungeon(), serverPlayer);
      }
   }

   public void onDungeonPlayerRemoved(@Nullable Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         this.bossFight.removePlayer(serverPlayer);
         AetherEventDispatch.onBossFightPlayerRemove(this, this.getDungeon(), serverPlayer);
      }
   }

   public boolean isAwake() {
      return (Boolean)this.getEntityData().get(DATA_AWAKE_ID);
   }

   public void setAwake(boolean awake) {
      this.getEntityData().set(DATA_AWAKE_ID, awake);
   }

   public Component getBossName() {
      return (Component)this.getEntityData().get(DATA_BOSS_NAME_ID);
   }

   public void setBossName(Component component) {
      this.getEntityData().set(DATA_BOSS_NAME_ID, component);
      this.bossFight.setName(component);
   }

   public float getHurtAngleX() {
      return (Float)this.getEntityData().get(DATA_HURT_ANGLE_X_ID);
   }

   public void setHurtAngleX(float hurtAngleX) {
      this.getEntityData().set(DATA_HURT_ANGLE_X_ID, hurtAngleX);
   }

   public float getHurtAngleZ() {
      return (Float)this.getEntityData().get(DATA_HURT_ANGLE_Z_ID);
   }

   public void setHurtAngleZ(float hurtAngleZ) {
      this.getEntityData().set(DATA_HURT_ANGLE_Z_ID, hurtAngleZ);
   }

   public float getHurtAngle() {
      return (Float)this.getEntityData().get(DATA_HURT_ANGLE_ID);
   }

   public void setHurtAngle(float hurtAngle) {
      this.getEntityData().set(DATA_HURT_ANGLE_ID, hurtAngle);
   }

   @Nullable
   public BossRoomTracker<Slider> getDungeon() {
      return this.bronzeDungeon;
   }

   public void setDungeon(@Nullable BossRoomTracker<Slider> dungeon) {
      this.bronzeDungeon = dungeon;
   }

   public boolean isBossFight() {
      return this.bossFight.isVisible();
   }

   public void setBossFight(boolean isFighting) {
      this.bossFight.setVisible(isFighting);
   }

   @Nullable
   @Override
   public ResourceLocation getBossBarTexture() {
      return ResourceLocation.fromNamespaceAndPath("aether", "boss_bar/slider");
   }

   @Nullable
   @Override
   public ResourceLocation getBossBarBackgroundTexture() {
      return ResourceLocation.fromNamespaceAndPath("aether", "boss_bar/slider_background");
   }

   @Nullable
   @Override
   public Music getBossMusic() {
      return SLIDER_MUSIC;
   }

   public int getChatCooldown() {
      return this.chatCooldown;
   }

   public void setChatCooldown(int cooldown) {
      this.chatCooldown = cooldown;
   }

   public int getDeathScore() {
      return this.deathScore;
   }

   @Nullable
   public Direction getMoveDirection() {
      return this.moveDirection;
   }

   public void setMoveDirection(@Nullable Direction moveDirection) {
      this.moveDirection = moveDirection;
   }

   public int getMoveDelay() {
      return this.moveDelay;
   }

   public void setMoveDelay(int moveDelay) {
      this.moveDelay = moveDelay;
   }

   @Nullable
   public Vec3 findTargetPoint() {
      Vec3 pos = this.targetPoint;
      if (pos != null) {
         return pos;
      } else {
         LivingEntity target = this.getTarget();
         return target == null ? null : target.position();
      }
   }

   @Nullable
   public Vec3 getTargetPoint() {
      return this.targetPoint;
   }

   public void setTargetPoint(@Nullable Vec3 targetPoint) {
      this.targetPoint = targetPoint;
   }

   public int attackCooldown() {
      return this.attackCooldown;
   }

   public void setAttackCooldown(int attackCooldown) {
      this.attackCooldown = attackCooldown;
   }

   public int calculateMoveDelay() {
      return this.isCritical() ? 1 + this.getRandom().nextInt(10) : 2 + this.getRandom().nextInt(14);
   }

   public static Direction calculateDirection(double x, double y, double z) {
      double absX = Math.abs(x);
      double absY = Math.abs(y);
      double absZ = Math.abs(z);
      if (absY > absX && absY > absZ) {
         return y > 0.0 ? Direction.UP : Direction.DOWN;
      } else if (absX > absZ) {
         return x > 0.0 ? Direction.EAST : Direction.WEST;
      } else {
         return z > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   public static AABB calculateAdjacentBox(AABB box, Direction direction) {
      double minX = box.minX;
      double minY = box.minY;
      double minZ = box.minZ;
      double maxX = box.maxX;
      double maxY = box.maxY;
      double maxZ = box.maxZ;
      if (direction == Direction.UP) {
         minY = maxY++;
      } else if (direction == Direction.DOWN) {
         maxY = minY--;
      } else if (direction == Direction.NORTH) {
         maxZ = minZ--;
      } else if (direction == Direction.SOUTH) {
         minZ = maxZ++;
      } else if (direction == Direction.EAST) {
         minX = maxX++;
      } else {
         maxX = minX--;
      }

      return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public float getVelocityIncrease() {
      return Math.max(this.isCritical() ? 0.045F - this.getHealth() / 10000.0F : 0.035F - this.getHealth() / 30000.0F, 0.013333334F);
   }

   public boolean isCritical() {
      return this.getHealth() <= this.getMaxHealth() / 4.0F;
   }

   public float getMaxVelocity() {
      return 2.5F;
   }

   public void setCustomName(@Nullable Component name) {
      super.setCustomName(name);
      this.setBossName(name);
   }

   protected SoundEvent getAwakenSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SLIDER_AWAKEN.get();
   }

   public SoundEvent getCollideSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SLIDER_COLLIDE.get();
   }

   public SoundEvent getMoveSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SLIDER_MOVE.get();
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SLIDER_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_SLIDER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SLIDER_DEATH.get();
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   public boolean canAttack(LivingEntity target) {
      return target.canBeSeenAsEnemy();
   }

   public boolean ignoreExplosion(Explosion explosion) {
      return true;
   }

   public float getYRot() {
      return 0.0F;
   }

   protected boolean canRide(Entity vehicle) {
      return false;
   }

   public boolean canBeCollidedWith() {
      return !this.isAwake();
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean shouldDiscardFriction() {
      return true;
   }

   protected boolean isAffectedByFluids() {
      return false;
   }

   public boolean displayFireAnimation() {
      return false;
   }

   public boolean isFullyFrozen() {
      return false;
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.EVENTS;
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      this.addBossSaveData(tag, this.registryAccess());
      tag.putBoolean("Awake", this.isAwake());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.readBossSaveData(tag, this.registryAccess());
      if (tag.contains("Awake")) {
         this.setAwake(tag.getBoolean("Awake"));
      }
   }

   public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
      CompoundTag tag = new CompoundTag();
      this.addBossSaveData(tag, this.registryAccess());
      buffer.writeNbt(tag);
   }

   public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
      CompoundTag tag = additionalData.readNbt();
      if (tag != null) {
         this.readBossSaveData(tag, this.registryAccess());
      }
   }
}
