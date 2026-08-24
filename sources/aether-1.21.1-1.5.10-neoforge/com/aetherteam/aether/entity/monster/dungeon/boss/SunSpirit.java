package com.aetherteam.aether.entity.monster.dungeon.boss;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.data.resources.registries.AetherDamageTypes;
import com.aetherteam.aether.entity.AetherBossMob;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.ai.controller.BlankMoveControl;
import com.aetherteam.aether.entity.monster.dungeon.FireMinion;
import com.aetherteam.aether.entity.projectile.crystal.AbstractCrystal;
import com.aetherteam.aether.entity.projectile.crystal.FireCrystal;
import com.aetherteam.aether.entity.projectile.crystal.IceCrystal;
import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.mixin.mixins.common.accessor.LookAtPlayerGoalAccessor;
import com.aetherteam.aether.network.packet.clientbound.BossInfoPacket;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import com.aetherteam.nitrogen.entity.BossRoomTracker;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;

public class SunSpirit extends PathfinderMob implements AetherBossMob<SunSpirit>, Enemy, IEntityWithComplexSpawn {
   private static final double DEFAULT_SPEED_MODIFIER = 1.0;
   private static final double FROZEN_SPEED_MODIFIER = 0.3;
   private static final float INCINERATION_DAMAGE = 10.0F;
   private static final int INCINERATION_FIRE_DURATION = 8;
   private static final int SUN_SPIRIT_FROZEN_DURATION = 175;
   private static final int ICE_CRYSTAL_SHOOT_COUNT_INTERVAL = 5;
   private static final int SHOOT_CRYSTAL_INTERVAL = 50;
   private static final int SPAWN_FIRE_INTERVAL = 35;
   private static final EntityDataAccessor<Boolean> DATA_IS_FROZEN = SynchedEntityData.defineId(SunSpirit.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_FROZEN_DURATION = SynchedEntityData.defineId(SunSpirit.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Component> DATA_BOSS_NAME = SynchedEntityData.defineId(SunSpirit.class, EntityDataSerializers.COMPONENT);
   private static final EntityDataAccessor<Boolean> DATA_DISPLAY_WEAK_MESSAGE = SynchedEntityData.defineId(SunSpirit.class, EntityDataSerializers.BOOLEAN);
   private static final Music SUN_SPIRIT_MUSIC = new Music(AetherSoundEvents.MUSIC_BOSS_SUN_SPIRIT, 0, 0, true);
   public static final Map<Block, Function<BlockState, BlockState>> DUNGEON_BLOCK_CONVERSIONS = new HashMap<>(
      Map.ofEntries(
         Map.entry((Block)AetherBlocks.LOCKED_HELLFIRE_STONE.get(), blockState -> ((Block)AetherBlocks.HELLFIRE_STONE.get()).defaultBlockState()),
         Map.entry((Block)AetherBlocks.LOCKED_LIGHT_HELLFIRE_STONE.get(), blockState -> ((Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get()).defaultBlockState()),
         Map.entry((Block)AetherBlocks.BOSS_DOORWAY_HELLFIRE_STONE.get(), blockState -> Blocks.AIR.defaultBlockState()),
         Map.entry((Block)AetherBlocks.TREASURE_DOORWAY_HELLFIRE_STONE.get(), blockState -> Blocks.AIR.defaultBlockState())
      )
   );
   private final ServerBossEvent bossFight;
   @Nullable
   private BossRoomTracker<SunSpirit> dungeon;
   private Vec3 origin;
   private int xMax = 9;
   private int zMax = 9;
   private int chatLine;
   private int chatCooldown;
   protected double speedModifier;

   public SunSpirit(EntityType<? extends SunSpirit> type, Level level) {
      super(type, level);
      this.moveControl = new BlankMoveControl(this);
      this.bossFight = (ServerBossEvent)new ServerBossEvent(this.getBossName(), BossBarColor.RED, BossBarOverlay.PROGRESS).setPlayBossMusic(true);
      this.setBossFight(false);
      this.origin = this.position();
      this.xpReward = 50;
      this.setNoGravity(true);
      this.speedModifier = 1.0;
      this.setPersistenceRequired();
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      this.setBossName(BossNameGenerator.generateSunSpiritName(this.getRandom()));
      this.origin = this.position();
      return spawnData;
   }

   public void registerGoals() {
      this.goalSelector.addGoal(0, new SunSpirit.DoNothingGoal(this));
      this.goalSelector.addGoal(1, new SunSpirit.SunSpiritLookGoal(this, Player.class, 40.0F, 1.0F));
      this.goalSelector.addGoal(2, new SunSpirit.ShootFireballGoal(this));
      this.goalSelector.addGoal(3, new SunSpirit.SummonFireGoal(this));
      this.goalSelector.addGoal(4, new SunSpirit.FlyAroundGoal(this));
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 500.0).add(Attributes.MOVEMENT_SPEED, 0.35);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_IS_FROZEN, false);
      builder.define(DATA_FROZEN_DURATION, 0);
      builder.define(DATA_BOSS_NAME, Component.literal("Sun Spirit"));
      builder.define(DATA_DISPLAY_WEAK_MESSAGE, true);
   }

   public void tick() {
      super.tick();
      this.breakBlocks();
      this.evaporate();
      if (this.getChatCooldown() > 0) {
         this.chatCooldown--;
      }

      if (this.getHealth() > 0.0F && !this.isFrozen()) {
         double x = this.getX() + (this.getRandom().nextFloat() - 0.5F) * this.getRandom().nextFloat();
         double y = this.getBoundingBox().minY + this.getRandom().nextFloat() - 0.5;
         double z = this.getZ() + (this.getRandom().nextFloat() - 0.5F) * this.getRandom().nextFloat();
         this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0.0, -0.075, 0.0);
         this.burnEntities();
      }

      this.setYRot(Mth.rotateIfNecessary(this.getYRot(), this.getYHeadRot(), 20.0F));
      this.speedModifier = this.isFrozen() ? 0.3 : 1.0;
   }

   private void breakBlocks() {
      if (this.level() instanceof ServerLevel serverLevel && EventHooks.canEntityGrief(this.level(), this)) {
         BlockPos.betweenClosedStream(this.getBoundingBox().inflate(1.0, 0.0, 1.0))
            .forEach(
               pos -> {
                  BlockState state = this.level().getBlockState(pos);
                  if (this.isBreakable(state)
                     && (state.getShape(this.level(), pos).equals(Shapes.block()) || !state.getCollisionShape(this.level(), pos).isEmpty())
                     && (this.getDungeon() == null || this.getDungeon().roomBounds().contains(pos.getCenter()))) {
                     this.level().destroyBlock(pos, true, this);
                     serverLevel.sendParticles(
                        ParticleTypes.FLAME,
                        pos.getCenter().x(),
                        pos.getCenter().y(),
                        pos.getCenter().z(),
                        5,
                        this.random.nextDouble() / 2.0 - this.random.nextDouble(),
                        this.random.nextDouble() / 2.0 - this.random.nextDouble(),
                        this.random.nextDouble() / 2.0 - this.random.nextDouble(),
                        0.1
                     );
                  }
               }
            );
      }
   }

   private boolean isBreakable(BlockState blockState) {
      return !blockState.isAir()
         && !blockState.is(AetherTags.Blocks.SUN_SPIRIT_UNBREAKABLE)
         && blockState.getBlock().defaultDestroyTime() >= 0.0F
         && blockState.getBlock().defaultDestroyTime() < 100.0F;
   }

   private void evaporate() {
      AABB boundingBox = this.getBoundingBox();
      BlockPos min = BlockPos.containing(boundingBox.minX - this.xMax, boundingBox.minY - 3.0, boundingBox.minZ - this.zMax);
      BlockPos max = BlockPos.containing(
         Math.ceil(boundingBox.maxX - 1.0) + this.xMax, Math.ceil(boundingBox.maxY - 1.0) + 4.0, Math.ceil(boundingBox.maxZ - 1.0) + this.zMax
      );
      AetherBossMob.super.evaporate(this, min, max, blockState -> true);
   }

   public void burnEntities() {
      for (Entity target : this.level()
         .getEntities(this, this.getBoundingBox().expandTowards(0.0, -2.0, 0.0).contract(-0.75, 0.0, -0.75).contract(0.75, 0.0, 0.75))) {
         if (target instanceof LivingEntity) {
            target.hurt(AetherDamageTypes.entityDamageSource(this.level(), AetherDamageTypes.INCINERATION, this), 10.0F);
            target.igniteForSeconds(8.0F);
         }
      }
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      this.bossFight.setProgress(this.getHealth() / this.getMaxHealth());
      this.trackDungeon();
      this.checkIceCrystals();
      if (this.getFrozenDuration() > 0) {
         this.setFrozenDuration(this.getFrozenDuration() - 1);
      } else {
         this.setFrozen(false);
      }
   }

   private void checkIceCrystals() {
      for (IceCrystal iceCrystal : this.level().getEntitiesOfClass(IceCrystal.class, this.getBoundingBox().inflate(0.1))) {
         iceCrystal.doDamage(this);
      }
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (!this.level().isClientSide() && !this.isBossFight() && this.getChatCooldown() <= 0) {
         this.setChatCooldown(14);
         if (this.getDungeon() != null && !this.getDungeon().isPlayerWithinRoomInterior(player)) {
            this.displayTooFarMessage(player);
         } else if (this.level().getDifficulty() != Difficulty.PEACEFUL) {
            if (!(Boolean)AetherConfig.COMMON.repeat_sun_spirit_dialogue.get()
               && ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).hasSeenSunSpiritDialogue()
               && this.chatLine == 0) {
               this.chatLine = 10;
            }

            if (this.chatLine < 9) {
               this.playSound(this.getInteractSound(), 1.0F, this.getVoicePitch());
            }

            switch (this.chatLine++) {
               case 0:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line0").withStyle(ChatFormatting.RED));
                  break;
               case 1:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line1").withStyle(ChatFormatting.RED));
                  break;
               case 2:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line2").withStyle(ChatFormatting.RED));
                  break;
               case 3:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line3").withStyle(ChatFormatting.RED));
                  break;
               case 4:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line4").withStyle(ChatFormatting.RED));
                  break;
               case 5:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line5.1").withStyle(ChatFormatting.RED));
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line5.2").withStyle(ChatFormatting.RED));
                  break;
               case 6:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line6.1").withStyle(ChatFormatting.RED));
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line6.2").withStyle(ChatFormatting.RED));
                  break;
               case 7:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line7.1").withStyle(ChatFormatting.RED));
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line7.2").withStyle(ChatFormatting.RED));
                  break;
               case 8:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line8").withStyle(ChatFormatting.RED));
                  break;
               case 9:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line9").withStyle(ChatFormatting.GOLD));
                  this.setHealth(this.getMaxHealth());
                  this.setBossFight(true);
                  if (this.getDungeon() != null) {
                     this.closeRoom();
                  }

                  this.playSound(this.getActivateSound(), 1.0F, this.getVoicePitch());
                  AetherEventDispatch.onBossFightStart(this, this.getDungeon());
                  ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).setSeenSunSpiritDialogue(true);
                  break;
               default:
                  this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line10").withStyle(ChatFormatting.RED));
                  this.chatLine = 9;
            }
         } else {
            this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.line1").withStyle(ChatFormatting.RED));
         }
      }

      return super.mobInteract(player, hand);
   }

   protected void chatWithNearby(Component message) {
      AABB room = this.getDungeon() == null ? this.getBoundingBox().inflate(16.0) : this.getDungeon().roomBounds();
      this.level().getNearbyPlayers(NON_COMBAT, this, room).forEach(player -> player.sendSystemMessage(message));
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean flag = super.hurt(source, amount);
      if (!this.level().isClientSide()
         && flag
         && this.getHealth() > 0.0F
         && source.getEntity() instanceof LivingEntity entity
         && source.getDirectEntity() instanceof IceCrystal) {
         if (this.getDisplayWeakMessage()) {
            this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.message.attack.weakened"));
            this.setDisplayWeakMessage(false);
         }

         this.setFrozen(true);
         this.setFrozenDuration(175);
         FireMinion minion = new FireMinion((EntityType<? extends FireMinion>)AetherEntityTypes.FIRE_MINION.get(), this.level());
         minion.setPos(this.position());
         minion.setTarget(entity);
         this.level().addFreshEntity(minion);
      }

      return flag;
   }

   public void reset() {
      this.setBossFight(false);
      this.setTarget(null);
      if (this.dungeon != null) {
         this.openRoom();
      }

      AetherEventDispatch.onBossFightStop(this, this.getDungeon());
   }

   public void die(DamageSource source) {
      if (!this.level().isClientSide()) {
         this.setFrozen(true);
         this.bossFight.setProgress(this.getHealth() / this.getMaxHealth());
         this.chatWithNearby(Component.translatable("gui.aether.sun_spirit.dead").withStyle(ChatFormatting.AQUA));
         if (this.getDungeon() != null) {
            this.getDungeon().grantAdvancements(source);
            this.tearDownRoom();
         }

         if (this.level().hasData(AetherDataAttachments.AETHER_TIME)) {
            AetherTimeAttachment data = (AetherTimeAttachment)this.level().getData(AetherDataAttachments.AETHER_TIME);
            data.setEternalDay(false);
            data.updateEternalDay(this.level());
            if ((Boolean)AetherConfig.SERVER.sync_aether_time.get()) {
               data.setSynched(-1, Direction.DIMENSION, "setShouldWait", true, this.level());
            }
         }
      }

      super.die(source);
   }

   public void knockback(double strength, double x, double z) {
   }

   public void push(double x, double y, double z) {
   }

   public boolean canBeAffected(MobEffectInstance effectInstance) {
      return false;
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
         if (!serverPlayer.isAlive()) {
            serverPlayer.sendSystemMessage(Component.translatable("gui.aether.sun_spirit.playerdeath").withStyle(ChatFormatting.RED));
         }

         AetherEventDispatch.onBossFightPlayerRemove(this, this.getDungeon(), serverPlayer);
      }
   }

   public boolean isFrozen() {
      return (Boolean)this.getEntityData().get(DATA_IS_FROZEN);
   }

   public void setFrozen(boolean frozen) {
      this.getEntityData().set(DATA_IS_FROZEN, frozen);
   }

   public int getFrozenDuration() {
      return (Integer)this.getEntityData().get(DATA_FROZEN_DURATION);
   }

   public void setFrozenDuration(int duration) {
      this.getEntityData().set(DATA_FROZEN_DURATION, duration);
   }

   public Component getBossName() {
      return (Component)this.getEntityData().get(DATA_BOSS_NAME);
   }

   public void setBossName(Component component) {
      this.getEntityData().set(DATA_BOSS_NAME, component);
      this.bossFight.setName(component);
   }

   public boolean getDisplayWeakMessage() {
      return (Boolean)this.getEntityData().get(DATA_DISPLAY_WEAK_MESSAGE);
   }

   public void setDisplayWeakMessage(boolean display) {
      this.getEntityData().set(DATA_DISPLAY_WEAK_MESSAGE, display);
   }

   @Nullable
   public BossRoomTracker<SunSpirit> getDungeon() {
      return this.dungeon;
   }

   public void setDungeon(@Nullable BossRoomTracker<SunSpirit> dungeon) {
      this.dungeon = dungeon;
      if (dungeon != null) {
         this.origin = dungeon.originCoordinates();
         this.xMax = this.zMax = Mth.floor(dungeon.roomBounds().getXsize() / 2.0 - 5.0);
      } else {
         this.origin = this.position();
         this.xMax = 9;
         this.zMax = 9;
      }
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
      return ResourceLocation.fromNamespaceAndPath("aether", "boss_bar/sun_spirit");
   }

   @Nullable
   @Override
   public ResourceLocation getBossBarBackgroundTexture() {
      return ResourceLocation.fromNamespaceAndPath("aether", "boss_bar/sun_spirit_background");
   }

   @Nullable
   @Override
   public Music getBossMusic() {
      return SUN_SPIRIT_MUSIC;
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

   public boolean isInvulnerableTo(DamageSource source) {
      if (this.isRemoved()) {
         return true;
      } else {
         return this.isFrozen()
            ? !(source.getEntity() instanceof LivingEntity) || source.getEntity() instanceof SunSpirit
            : !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.is(AetherTags.DamageTypes.IS_COLD);
      }
   }

   protected SoundEvent getInteractSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SUN_SPIRIT_INTERACT.get();
   }

   protected SoundEvent getActivateSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SUN_SPIRIT_ACTIVATE.get();
   }

   protected SoundEvent getShootFireSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SUN_SPIRIT_SHOOT_FIRE.get();
   }

   protected SoundEvent getShootIceSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SUN_SPIRIT_SHOOT_ICE.get();
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)AetherSoundEvents.ENTITY_SUN_SPIRIT_HURT.get();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SUN_SPIRIT_DEATH.get();
   }

   protected float getSoundVolume() {
      return 3.0F;
   }

   public boolean ignoreExplosion(Explosion explosion) {
      return true;
   }

   public boolean isNoGravity() {
      return true;
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      this.addBossSaveData(tag, this.registryAccess());
      tag.putInt("ChatLine", this.chatLine);
      tag.putDouble("OffsetX", this.origin.x() - this.getX());
      tag.putDouble("OffsetY", this.origin.y() - this.getY());
      tag.putDouble("OffsetZ", this.origin.z() - this.getZ());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.readBossSaveData(tag, this.registryAccess());
      if (tag.contains("ChatLine")) {
         this.chatLine = tag.getInt("ChatLine");
      }

      if (tag.contains("OffsetX")) {
         double offsetX = this.getX() + tag.getDouble("OffsetX");
         double offsetY = this.getY() + tag.getDouble("OffsetY");
         double offsetZ = this.getZ() + tag.getDouble("OffsetZ");
         this.origin = new Vec3(offsetX, offsetY, offsetZ);
      } else {
         this.origin = this.position();
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

   public static class DoNothingGoal extends Goal {
      private final SunSpirit sunSpirit;

      public DoNothingGoal(SunSpirit sunSpirit) {
         this.sunSpirit = sunSpirit;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
      }

      public boolean canUse() {
         return !this.sunSpirit.isBossFight();
      }

      public void start() {
         this.sunSpirit.setDeltaMovement(Vec3.ZERO);
         this.sunSpirit.setPos(this.sunSpirit.origin.x(), this.sunSpirit.origin.y(), this.sunSpirit.origin.z());
      }
   }

   public static class FlyAroundGoal extends Goal {
      private final SunSpirit sunSpirit;
      private float rotation;
      private int courseChangeTimer;

      public FlyAroundGoal(SunSpirit sunSpirit) {
         this.sunSpirit = sunSpirit;
         this.rotation = sunSpirit.random.nextFloat() * 360.0F;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public void tick() {
         boolean changedCourse = this.outOfBounds();
         double x = Mth.sin(this.rotation * 0.017453292F) * this.sunSpirit.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.sunSpirit.speedModifier;
         double z = -Mth.cos(this.rotation * 0.017453292F) * this.sunSpirit.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.sunSpirit.speedModifier;
         this.sunSpirit.setDeltaMovement(x, 0.0, z);
         if (changedCourse || ++this.courseChangeTimer >= 20) {
            if (this.sunSpirit.getRandom().nextInt(3) == 0) {
               this.rotation = this.rotation + (this.sunSpirit.getRandom().nextFloat() - this.sunSpirit.getRandom().nextFloat() * 60.0F);
            }

            this.rotation = Mth.wrapDegrees(this.rotation);
            this.courseChangeTimer = 0;
         }
      }

      protected boolean outOfBounds() {
         boolean flag = false;
         if (this.sunSpirit.getDeltaMovement().x() >= 0.0 && this.sunSpirit.getX() >= this.sunSpirit.origin.x() + this.sunSpirit.xMax
            || this.sunSpirit.getDeltaMovement().x() <= 0.0 && this.sunSpirit.getX() <= this.sunSpirit.origin.x() - this.sunSpirit.xMax) {
            this.rotation = 360.0F - this.rotation;
            flag = true;
         }

         if (this.sunSpirit.getDeltaMovement().z() >= 0.0 && this.sunSpirit.getZ() >= this.sunSpirit.origin.z() + this.sunSpirit.zMax
            || this.sunSpirit.getDeltaMovement().z() <= 0.0 && this.sunSpirit.getZ() <= this.sunSpirit.origin.z() - this.sunSpirit.zMax) {
            this.rotation = 180.0F - this.rotation;
            flag = true;
         }

         return flag;
      }

      public boolean canUse() {
         return this.sunSpirit.isBossFight();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }
   }

   public static class ShootFireballGoal extends Goal {
      private final SunSpirit sunSpirit;
      private int shootCrystalInterval;
      private int crystalCount;

      public ShootFireballGoal(SunSpirit sunSpirit) {
         this.sunSpirit = sunSpirit;
         this.shootCrystalInterval = 50;
         this.crystalCount = 5;
      }

      public boolean canUse() {
         return this.sunSpirit.isBossFight() && --this.shootCrystalInterval <= 0;
      }

      public void start() {
         AbstractCrystal crystal;
         if (--this.crystalCount <= 0) {
            crystal = new IceCrystal(this.sunSpirit.level(), this.sunSpirit);
            this.crystalCount = 5;
            this.sunSpirit
               .playSound(
                  this.sunSpirit.getShootIceSound(),
                  3.0F,
                  this.sunSpirit.level().getRandom().nextFloat() - this.sunSpirit.level().getRandom().nextFloat() * 0.2F + 1.2F
               );
         } else {
            crystal = new FireCrystal(this.sunSpirit.level(), this.sunSpirit);
            this.sunSpirit
               .playSound(
                  this.sunSpirit.getShootFireSound(),
                  3.0F,
                  this.sunSpirit.level().getRandom().nextFloat() - this.sunSpirit.level().getRandom().nextFloat() * 0.2F + 1.2F
               );
         }

         this.sunSpirit.level().addFreshEntity(crystal);
         this.shootCrystalInterval = 50;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }
   }

   public static class SummonFireGoal extends Goal {
      private final SunSpirit sunSpirit;
      private int summonFireInterval;

      public SummonFireGoal(SunSpirit sunSpirit) {
         this.sunSpirit = sunSpirit;
         this.summonFireInterval = 35;
      }

      public boolean canUse() {
         return this.sunSpirit.isBossFight() && --this.summonFireInterval <= 0;
      }

      public void start() {
         BlockPos pos = BlockPos.containing(this.sunSpirit.getX(), this.sunSpirit.getY(), this.sunSpirit.getZ());

         for (int i = 0; i <= 3; i++) {
            if (this.sunSpirit.level().isEmptyBlock(pos) && !this.sunSpirit.level().isEmptyBlock(pos.below())) {
               this.sunSpirit.level().setBlock(pos, Blocks.FIRE.defaultBlockState(), 11);
               break;
            }

            pos = pos.below();
         }

         this.summonFireInterval = 35;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }
   }

   public static class SunSpiritLookGoal extends LookAtPlayerGoal {
      public SunSpiritLookGoal(Mob mob, Class<? extends LivingEntity> lookAtType, float lookDistance, float probability) {
         this(mob, lookAtType, lookDistance, probability, false);
      }

      public SunSpiritLookGoal(Mob mob, Class<? extends LivingEntity> lookAtType, float lookDistance, float probability, boolean onlyHorizontal) {
         super(mob, lookAtType, lookDistance, probability, onlyHorizontal);
         TargetingConditions conditions;
         if (lookAtType == Player.class) {
            conditions = TargetingConditions.forNonCombat()
               .ignoreInvisibilityTesting()
               .range(lookDistance)
               .selector(entity -> EntitySelector.notRiding(mob).test(entity));
         } else {
            conditions = TargetingConditions.forNonCombat().ignoreInvisibilityTesting().range(lookDistance);
         }

         ((LookAtPlayerGoalAccessor)this).aether$setLookAtContext(conditions);
      }
   }
}
