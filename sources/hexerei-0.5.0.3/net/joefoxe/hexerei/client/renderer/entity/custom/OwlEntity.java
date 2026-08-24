package net.joefoxe.hexerei.client.renderer.entity.custom;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.ITargetsDroppedItems;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.QuirkController;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.quirks.FavoriteBlockQuirk;
import net.joefoxe.hexerei.client.renderer.entity.render.OwlVariant;
import net.joefoxe.hexerei.container.OwlContainer;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.data.owl.OwlLoadedChunksSavedData;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CourierLetterItem;
import net.joefoxe.hexerei.item.custom.CourierPackageItem;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.AskForSyncPacket;
import net.joefoxe.hexerei.util.message.BrowAnimPacket;
import net.joefoxe.hexerei.util.message.BrowPositioningPacket;
import net.joefoxe.hexerei.util.message.ClientboundOpenOwlCourierSendScreenPacket;
import net.joefoxe.hexerei.util.message.EatParticlesPacket;
import net.joefoxe.hexerei.util.message.EmotionPacket;
import net.joefoxe.hexerei.util.message.EntitySyncAdditionalDataPacket;
import net.joefoxe.hexerei.util.message.EntitySyncPacket;
import net.joefoxe.hexerei.util.message.HeadShakePacket;
import net.joefoxe.hexerei.util.message.HeadTiltPacket;
import net.joefoxe.hexerei.util.message.OwlHootPacket;
import net.joefoxe.hexerei.util.message.OwlSyncInvPacket;
import net.joefoxe.hexerei.util.message.OwlTeleportParticlePacket;
import net.joefoxe.hexerei.util.message.PeckPacket;
import net.joefoxe.hexerei.util.message.StartRidingPacket;
import net.joefoxe.hexerei.util.message.TailFanPacket;
import net.joefoxe.hexerei.util.message.TailWagPacket;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class OwlEntity extends TamableAnimal implements ContainerListener, FlyingAnimal, ITargetsDroppedItems, Container, MenuProvider, PowerableMob {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(new ItemLike[]{Items.SALMON, Items.COD});
   public OwlEntity.BrowPositioning browPositioning = OwlEntity.BrowPositioning.NORMAL;
   public OwlEntity.BrowAnimation browAnimation;
   public OwlEntity.BrowHappyAnimation browHappyAnimation;
   public OwlEntity.TailWagAnimation tailWagAnimation;
   public OwlEntity.TailFanAnimation tailFanAnimation;
   public OwlEntity.HootAnimation hootAnimation;
   public OwlEntity.PeckAnimation peckAnimation;
   public OwlEntity.HeadTiltAnimation headTiltAnimation;
   public OwlEntity.HeadShakeAnimation headShakeAnimation;
   public OwlEntity.AnimationController animationController;
   public OwlEntity.MessagingController messagingController;
   public float bodyXRot;
   public float bodyXRotLast;
   public float bodyYOffset;
   public float bodyYOffsetLast;
   public float rightWingAngle;
   public float rightWingAngleLast;
   public float rightWingFoldAngle;
   public float rightWingMiddleAngle;
   public float rightWingMiddleAngleLast;
   public float rightWingMiddleFoldAngle;
   public float rightWingTipAngle;
   public float leftWingAngle;
   public float leftWingAngleLast;
   public float leftWingFoldAngle;
   public float leftWingMiddleAngle;
   public float leftWingMiddleAngleLast;
   public float leftWingMiddleFoldAngle;
   public float leftWingTipAngle;
   public boolean dance;
   public int animationCounter;
   public float itemHeldSwing = 0.0F;
   public float itemHeldSwingLast = 0.0F;
   private BlockPos jukebox;
   private int rideCooldownCounter;
   private UUID fishThrowerID;
   private int heldItemTime = 0;
   public OwlEntity.OwlTask currentTask;
   public ItemEntity targetingItem;
   public boolean sync;
   public final ItemStackHandler itemHandler = this.createHandler();
   private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(OwlEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
   private static final EntityDataAccessor<Integer> OWL_DYE_COLOR = SynchedEntityData.defineId(OwlEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(OwlEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(OwlEntity.class, EntityDataSerializers.BOOLEAN);
   public int interactionRange;
   public boolean canAttack;
   private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
   protected FlyingPathNavigation flyingNav;
   protected GroundPathNavigation groundNav;
   private int lastSwappedNavigator = -40;
   public OwlEntity.Emotions emotions;
   public OwlEntity.EmotionState emotionState;
   private int emotionTicks = 0;
   public QuirkController quirkController;
   public int lowHealthDistressIncreaseTickLast = 0;
   public boolean breedGiftGivenByPlayer = false;
   public int breedGiftGivenByPartnerTimer = 0;
   public UUID breedGiftGivenByPlayerUUID;
   public int waitToGiveTime = 0;

   public OwlEntity(EntityType<OwlEntity> type, Level worldIn) {
      super(type, worldIn);
      this.registerGoals();
      this.flyingNav = (FlyingPathNavigation)this.createFlyingNavigation(worldIn);
      this.groundNav = (GroundPathNavigation)this.createGroundNavigation(worldIn);
      this.moveControl = new OwlEntity.OwlMoveController(this, 10);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
      this.animationCounter = 0;
      this.currentTask = OwlEntity.OwlTask.NONE;
      this.targetingItem = null;
      this.sync = false;
      this.animationController = new OwlEntity.AnimationController();
      this.browAnimation = new OwlEntity.BrowAnimation(this);
      this.animationController.addAnimation(this.browAnimation);
      this.browHappyAnimation = new OwlEntity.BrowHappyAnimation(this);
      this.animationController.addAnimation(this.browHappyAnimation);
      this.tailWagAnimation = new OwlEntity.TailWagAnimation(this);
      this.animationController.addAnimation(this.tailWagAnimation);
      this.tailFanAnimation = new OwlEntity.TailFanAnimation(this);
      this.animationController.addAnimation(this.tailFanAnimation);
      this.hootAnimation = new OwlEntity.HootAnimation(this);
      this.animationController.addAnimation(this.hootAnimation);
      this.peckAnimation = new OwlEntity.PeckAnimation(this);
      this.animationController.addAnimation(this.peckAnimation);
      this.headTiltAnimation = new OwlEntity.HeadTiltAnimation(this);
      this.animationController.addAnimation(this.headTiltAnimation);
      this.headShakeAnimation = new OwlEntity.HeadShakeAnimation(this);
      this.animationController.addAnimation(this.headShakeAnimation);
      this.messagingController = new OwlEntity.MessagingController(this);
      this.bodyXRot = 0.0F;
      this.bodyYOffset = 0.0F;
      this.rightWingAngle = -((float)Math.toRadians(85.0));
      this.leftWingAngle = (float)Math.toRadians(85.0);
      this.rightWingMiddleAngle = -((float)Math.toRadians(10.0));
      this.leftWingMiddleAngle = (float)Math.toRadians(10.0);
      this.rightWingMiddleFoldAngle = (float)Math.toRadians(30.0);
      this.leftWingMiddleFoldAngle = -((float)Math.toRadians(30.0));
      this.rightWingFoldAngle = (float)Math.toRadians(0.0);
      this.leftWingFoldAngle = -((float)Math.toRadians(0.0));
      this.rightWingTipAngle = (float)Math.toRadians(60.0);
      this.leftWingTipAngle = -((float)Math.toRadians(60.0));
      this.bodyYOffsetLast = this.bodyYOffset;
      this.rightWingAngleLast = this.rightWingAngle;
      this.leftWingAngleLast = this.leftWingAngle;
      this.rightWingMiddleAngleLast = this.rightWingMiddleAngle;
      this.leftWingMiddleAngleLast = this.leftWingMiddleAngle;
      this.interactionRange = 24;
      this.canAttack = true;
      this.emotions = new OwlEntity.Emotions(0, 0, 0);
      this.determineEmotionState();
      this.quirkController = new QuirkController();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new OwlEntity.FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.4) {
         public boolean canUse() {
            return this.mob instanceof OwlEntity owl && owl.isInSittingPose() ? false : super.canUse();
         }
      });
      this.goalSelector.addGoal(2, new OwlEntity.FlyBackToPerchGoal(this));
      this.goalSelector.addGoal(2, new OwlEntity.SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new OwlEntity.DeliverMessageGoal(this));
      this.goalSelector.addGoal(3, new OwlEntity.OwlFavoriteBlockGoal(this, 1.5));
      this.goalSelector.addGoal(2, new OwlEntity.FollowOwnerGoal(this, 1.25, 5.0F, 1.0F, true));
      this.goalSelector.addGoal(1, new OwlEntity.BreedGoal(this, 1.5));
      this.goalSelector.addGoal(1, new OwlEntity.TemptGoal(this, 1.0, TEMPTATION_ITEMS, false));
      this.goalSelector.addGoal(4, new OwlEntity.FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(4, new OwlEntity.WaterAvoidingRandomFlyingGoal(this, 1.0));
      this.goalSelector.addGoal(4, new OwlEntity.WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(10, new OwlEntity.OwlLookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(2, new OwlEntity.OwlGatherItems(this, false, false, 40, this.interactionRange));
   }

   protected float nextStep() {
      return this.moveDist + 0.25F;
   }

   protected void playStepSound(BlockPos pPos, BlockState pBlock) {
      this.playSound(SoundEvents.CHICKEN_STEP, 0.03F, 0.75F);
   }

   public BlockPos getBlockPosBelowThatAffectsMyMovement() {
      return this.getOnPos(0.500001F);
   }

   protected float getJumpPower() {
      return super.getJumpPower() * 1.1F;
   }

   public int getMaxHeadYRot() {
      return (this.onGround() || !this.isFlying()) && this.navigation.isDone() ? 180 : 90;
   }

   public void switchNavigator(boolean shouldFly, boolean force) {
      if (Math.abs(this.tickCount - this.lastSwappedNavigator) > 40 || force) {
         if (this.lastSwappedNavigator == -40) {
            this.entityData.set(DATA_FLYING, shouldFly);
         }

         this.lastSwappedNavigator = this.tickCount;
         this.navigation = (PathNavigation)(shouldFly ? this.flyingNav : this.groundNav);
      }
   }

   public void switchNavigator(boolean shouldFly) {
      this.switchNavigator(shouldFly, false);
   }

   public boolean isFlyingNav() {
      return this.navigation == this.flyingNav;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PERCH_POS, Optional.empty());
      builder.define(OWL_DYE_COLOR, -1);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(DATA_FLYING, true);
   }

   public void syncInv() {
      if (!this.level().isClientSide) {
         HexereiPacketHandler.sendToNearbyClient(this.level(), this, new OwlSyncInvPacket(this, this.itemHandler.serializeNBT(this.level().registryAccess())));
      }
   }

   public void sync() {
      this.setChanged();
      if (!this.level().isClientSide) {
         HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EntitySyncPacket(this, this.saveWithoutId(new CompoundTag())));
         this.syncAdditionalData();
      }
   }

   public void syncAdditionalData() {
      this.setChanged();
      if (!this.level().isClientSide) {
         CompoundTag tag = new CompoundTag();
         this.addAdditionalSaveDataNoSuper(tag);
         HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EntitySyncAdditionalDataPacket(this, tag));
      }
   }

   public boolean isMaxHealth() {
      return this.getHealth() >= this.getMaxHealth();
   }

   public void die(DamageSource pCause) {
      if (!this.checkTotemDeathProtection(pCause)) {
         this.messagingController.stopForceloadingChunks();
         super.die(pCause);
      }
   }

   public void remove(RemovalReason pReason) {
      if (pReason.shouldDestroy()) {
         this.messagingController.stopForceloadingChunks();
      }

      super.remove(pReason);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      float f = this.getHealth();
      if (pSource.is(DamageTypes.SWEET_BERRY_BUSH)) {
         return false;
      } else {
         if (!this.level().isClientSide) {
            this.emotions.setAnger(this.emotions.getAnger() + (int)Mth.clamp(pAmount * 20.0F, 10.0F, 30.0F));
            this.emotionChanged();
         }

         return super.hurt(pSource, pAmount);
      }
   }

   public boolean isInWall() {
      return this.isPassenger() ? false : super.isInWall();
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(2) {
         protected void onContentsChanged(int slot) {
            OwlEntity.this.syncAdditionalData();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot != 0 || stack.getItem() instanceof ArmorItem armorItem && armorItem.getType() == Type.HELMET;
         }

         public int getSlotLimit(int slot) {
            return slot != 0 && slot != 1 && slot != 2 ? 64 : 1;
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !this.isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
         }
      };
   }

   @Override
   public void peck() {
      this.peckAnimation.start();
      this.hootAnimation.start();
   }

   public void rideTick() {
      Entity entity = this.getVehicle();
      if (this.isPassenger() && !entity.isAlive()) {
         this.stopRiding();
      } else if (this.isTame() && entity instanceof LivingEntity && this.isOwnedBy((LivingEntity)entity)) {
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.tick();
         Entity player = this.getVehicle();
         if (this.isPassenger()) {
            int i = player.getPassengers().indexOf(this);
            float radius = 0.38F;
            float angle = 0.017453292F * (((Player)player).yBodyRot + (i == 0 ? -90 : 90));
            this.yHeadRot = ((Player)player).yHeadRot;
            this.yRotO = ((Player)player).yHeadRot;
            this.setPos(
               player.getX() + radius * Mth.sin((float)(3.141592653589793 + angle)),
               player.getY() + (!player.isShiftKeyDown() ? 1.4 : 1.2),
               player.getZ() + radius * Mth.cos(angle)
            );
            if (!player.isAlive() || player.isShiftKeyDown() || ((Player)player).isFallFlying() || this.getTarget() != null && this.getTarget().isAlive()) {
               this.removeVehicle();
            }
         }
      } else {
         super.rideTick();
      }
   }

   private void setTypeVariant(int pTypeVariant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, pTypeVariant);
   }

   private void setFlying(boolean flying) {
      this.entityData.set(DATA_FLYING, flying);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public OwlVariant getVariant() {
      return OwlVariant.byId(this.getTypeVariant() & 0xFF);
   }

   public void setRecordPlayingNearby(BlockPos p_21082_, boolean p_21083_) {
      this.jukebox = p_21082_;
      this.dance = p_21083_;
   }

   protected AABB getTargetableArea(double targetDistance) {
      Vec3 renderCenter = new Vec3(this.getX(), this.getY(), this.getZ());
      AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
      return aabb.move(renderCenter);
   }

   public void setBrowPos(OwlEntity.BrowPositioning browPositioning) {
      this.browPositioning = browPositioning;
      if (!this.level().isClientSide) {
         HexereiPacketHandler.sendToNearbyClient(this.level(), this, new BrowPositioningPacket(this, this.browPositioning));
      }
   }

   public void determineEmotionState() {
      OwlEntity.EmotionState closestState = null;
      double closestDistance = 1.7976931348623157E308;

      for (OwlEntity.EmotionState state : OwlEntity.EmotionState.values()) {
         double distance = this.calculateStateDistance(this.emotions, state.getScales());
         if (distance < closestDistance) {
            closestDistance = distance;
            closestState = state;
         }
      }

      this.emotionState = closestState;
   }

   private double calculateStateDistance(OwlEntity.Emotions scales1, OwlEntity.Emotions scales2) {
      int anger = scales1.getAnger() - scales2.getAnger();
      int plead = scales1.getDistress() - scales2.getDistress();
      int happiness = scales1.getHappiness() - scales2.getHappiness();
      return Math.sqrt(anger * anger + plead * plead + happiness * happiness);
   }

   private void adjustEmotion() {
      if (this.random.nextInt(1) == 0) {
         int adjustment = (int)Math.round((this.easeInOutCubic(this.emotions.getAnger() / 100.0F) * 0.5 + 0.25) * 20.0 + 1.0);
         this.emotions.setAnger(this.emotions.getAnger() - adjustment);
      }

      if (this.random.nextInt(1) == 0) {
         int adjustment = (int)Math.round((this.easeInOutCubic(this.emotions.getDistress() / 100.0F) * 0.5 + 0.25) * 20.0 + 1.0);
         this.emotions.setDistress(this.emotions.getDistress() - adjustment);
      }

      if (this.random.nextInt(1) == 0) {
         int adjustment = (int)Math.round((this.easeInOutCubic(this.emotions.getDistress() / 100.0F) * 0.5 + 0.25) * 20.0 + 1.0);
         this.emotions.setHappiness(this.emotions.getHappiness() - adjustment);
      }

      this.emotionChanged();
   }

   public double easeInOutCubic(float x) {
      return x < 0.5 ? 4.0F * x * x * x : 1.0 - Math.pow(-2.0F * x + 2.0F, 3.0) / 2.0;
   }

   public void tame(Player pPlayer) {
      this.emotions.setDistress(this.emotions.getDistress() - 70);
      this.emotions.setHappiness(this.emotions.getHappiness() + 60);
      super.tame(pPlayer);
   }

   public void emotionChanged() {
      if (!this.level().isClientSide) {
         int packedEmotion = this.emotions.getHappiness() << 16 | this.emotions.getDistress() << 8 | this.emotions.getAnger();
         HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EmotionPacket(this, packedEmotion));
      }
   }

   private int adjustTowardsRestingPoint(int currentValue, int adjustmentFactor) {
      int restingPoint = 0;
      int difference = currentValue - restingPoint;
      int adjustment = Math.round((float)difference / adjustmentFactor);
      adjustment += this.random.nextInt(5) - 2;
      return Math.max(0, Math.min(100, currentValue - adjustment));
   }

   public boolean checkTotemDeathProtection(DamageSource pDamageSource) {
      if (pDamageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return false;
      } else {
         ItemStack itemstack = null;
         boolean triggered = false;

         for (InteractionHand interactionhand : InteractionHand.values()) {
            ItemStack itemstack1 = this.getItemInHand(interactionhand);
            if (itemstack1.is((Item)ModItems.CROW_ANKH_AMULET.get())) {
               itemstack = itemstack1.copy();
               itemstack1.shrink(1);
               triggered = true;
               break;
            }
         }

         if (triggered) {
            this.setHealth(1.0F);
            this.removeAllEffects();
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            this.level().broadcastEntityEvent(this, (byte)35);
            this.sync();
         }

         return triggered;
      }
   }

   public void tick() {
      super.tick();
      if (!this.sync && this.level() instanceof ServerLevel) {
         this.sync();
         this.sync = true;
      }

      if (!this.sync && this.level() instanceof ClientLevel) {
         if (this.level().isClientSide) {
            HexereiPacketHandler.sendToServer(new AskForSyncPacket(this));
         }

         this.sync = true;
      }

      if (this.breedGiftGivenByPartnerTimer > 0 && !this.level().isClientSide) {
         this.breedGiftGivenByPartnerTimer--;
         if (this.breedGiftGivenByPartnerTimer == 16) {
            this.peck();
            HexereiPacketHandler.sendToNearbyClient(this.level(), this, new PeckPacket(this));
         }

         if (this.breedGiftGivenByPartnerTimer == 0 && this.level().getPlayerByUUID(this.breedGiftGivenByPlayerUUID) != null) {
            this.setInLove(this.level().getPlayerByUUID(this.breedGiftGivenByPlayerUUID));
            this.heal(4.0F);
            if (!this.level().isClientSide) {
               HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, this.itemHandler.getStackInSlot(1)));
               this.emotions.setDistress(this.emotions.getDistress() - 25);
               this.emotions.setAnger(this.emotions.getAnger() - 15 - this.random.nextInt(5));
               this.emotionChanged();
            }

            if (this.itemHandler.getStackInSlot(1).hasCraftingRemainingItem()) {
               this.spawnAtLocation(this.itemHandler.getStackInSlot(1).getCraftingRemainingItem());
            }

            this.itemHandler.getStackInSlot(1).shrink(1);
            this.playSound(SoundEvents.PARROT_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.syncInv();
         }
      }

      this.quirkController.tick(this);
      if (!this.level().isClientSide) {
         float health = this.getHealth();
         float threshold = this.getMaxHealth() / 3.0F;
         if (health < threshold && this.tickCount - this.lowHealthDistressIncreaseTickLast > 20 && this.random.nextInt(10) == 0) {
            this.lowHealthDistressIncreaseTickLast = this.tickCount;
            this.emotions.setDistress(this.emotions.getDistress() + 5 + this.random.nextInt(15));
            this.emotions.setHappiness(this.emotions.getHappiness() - (this.random.nextInt(5) + 1));
            this.emotionChanged();
         }

         if (this.tickCount % 100 == 0) {
            if (this.isTame() && this.getOwner() instanceof Player owner && owner.distanceTo(this) < 5.0F) {
               this.emotions.setHappiness(this.emotions.getHappiness() + this.random.nextInt(5) + 1);
            }

            if (this.emotions.isHappy() && this.getHealth() < this.getMaxHealth()) {
               this.heal(1.0F);
            }
         }
      }

      float deltaDist = (float)Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z);
      float deltaYDist = (float)Math.sqrt(this.getDeltaMovement().y * this.getDeltaMovement().y);
      this.itemHeldSwingLast = this.itemHeldSwing;
      this.itemHeldSwing = this.moveTo(
         this.itemHeldSwing,
         Mth.clamp((deltaDist * 455.0F - deltaYDist * 300.0F) / 4.0F, 0.0F, 40.0F),
         3.0F + 10.0F * Mth.abs(Mth.clamp((deltaDist * 455.0F - deltaYDist * 300.0F) / 4.0F, 0.0F, 40.0F) - this.itemHeldSwing) / 40.0F
      );
      this.animationCounter++;
      this.rideCooldownCounter++;
      if (!this.level().isClientSide) {
         if (this.onGround() && this.isFlying()) {
            this.entityData.set(DATA_FLYING, false);
         }

         if (this.isFlyingNav() && !this.isFlying()) {
            this.entityData.set(DATA_FLYING, true);
         }

         this.emotionTicks++;
         if (this.emotionTicks >= 600) {
            this.emotionTicks = 0;
            this.adjustEmotion();
         }
      }

      if (this.level() instanceof ServerLevel serverLevel && this.targetingItem == null && this.currentTask == OwlEntity.OwlTask.PICKUP_ITEM) {
         this.currentTask = OwlEntity.OwlTask.NONE;
      }

      this.bodyYOffsetLast = this.bodyYOffset;
      this.rightWingAngleLast = this.rightWingAngle;
      this.leftWingAngleLast = this.leftWingAngle;
      this.rightWingMiddleAngleLast = this.rightWingMiddleAngle;
      this.leftWingMiddleAngleLast = this.leftWingMiddleAngle;
      this.bodyXRotLast = this.bodyXRot;
      if (this.isFlying() && !this.onGround()) {
         if (this.getDeltaMovement().y < -0.0075) {
            this.bodyXRot = (float)Mth.lerp(0.15, this.bodyXRot, 1.0471975803375244);
            this.rightWingMiddleFoldAngle = (float)Mth.lerp(0.45, this.rightWingMiddleFoldAngle, -((float)Math.toRadians(-15.0)));
            this.leftWingMiddleFoldAngle = (float)Mth.lerp(0.45, this.leftWingMiddleFoldAngle, (float)Math.toRadians(-15.0));
            this.rightWingFoldAngle = (float)Mth.lerp(0.45, this.rightWingFoldAngle, (float)Math.toRadians(25.0));
            this.leftWingFoldAngle = (float)Mth.lerp(0.45, this.leftWingFoldAngle, -((float)Math.toRadians(25.0)));
            this.bodyYOffset = (float)Mth.lerp(0.45, this.bodyYOffset, Math.sin((ClientEvents.getClientTicksWithoutPartial() + 2.0F) / 8.0F));
            this.rightWingAngle = (float)Mth.lerp(0.75, this.rightWingAngle, Math.sin(ClientEvents.getClientTicksWithoutPartial() / 8.0F) * 0.10000000149011612);
            this.leftWingAngle = (float)Mth.lerp(0.75, this.leftWingAngle, -Math.sin(ClientEvents.getClientTicksWithoutPartial() / 8.0F) * 0.10000000149011612);
            this.rightWingMiddleAngle = (float)Mth.lerp(
               0.75, this.rightWingMiddleAngle, Mth.sin((ClientEvents.getClientTicksWithoutPartial() - 8.0F) / 8.0F) * 0.25F - 0.125
            );
            this.leftWingMiddleAngle = (float)Mth.lerp(
               0.75, this.leftWingMiddleAngle, -Mth.sin((ClientEvents.getClientTicksWithoutPartial() - 8.0F) / 8.0F) * 0.25F + 0.125
            );
         } else {
            this.bodyXRot = (float)Mth.lerp(0.15, this.bodyXRot, 0.7853981852531433);
            this.rightWingMiddleFoldAngle = (float)Mth.lerp(0.45, this.rightWingMiddleFoldAngle, -((float)Math.toRadians(5.0)));
            this.leftWingMiddleFoldAngle = (float)Mth.lerp(0.45, this.leftWingMiddleFoldAngle, (float)Math.toRadians(5.0));
            this.rightWingFoldAngle = (float)Mth.lerp(0.45, this.rightWingFoldAngle, (float)Math.toRadians(0.0));
            this.leftWingFoldAngle = (float)Mth.lerp(0.45, this.leftWingFoldAngle, -((float)Math.toRadians(0.0)));
            this.bodyYOffset = (float)Mth.lerp(0.45, this.bodyYOffset, Math.sin((ClientEvents.getClientTicksWithoutPartial() + 1.0F) / 4.0F));
            this.rightWingAngle = (float)Mth.lerp(0.75, this.rightWingAngle, Math.sin(ClientEvents.getClientTicksWithoutPartial() / 4.0F) * 1.0);
            this.leftWingAngle = (float)Mth.lerp(0.75, this.leftWingAngle, -Math.sin(ClientEvents.getClientTicksWithoutPartial() / 4.0F) * 1.0);
            this.rightWingMiddleAngle = (float)Mth.lerp(
               0.75, this.rightWingMiddleAngle, Mth.sin((ClientEvents.getClientTicksWithoutPartial() - 4.0F) / 4.0F) * 0.5F - 0.25
            );
            this.leftWingMiddleAngle = (float)Mth.lerp(
               0.75, this.leftWingMiddleAngle, -Mth.sin((ClientEvents.getClientTicksWithoutPartial() - 4.0F) / 4.0F) * 0.5F + 0.25
            );
         }

         this.rightWingTipAngle = (float)Mth.lerp(0.45, this.rightWingTipAngle, (float)Math.toRadians(15.0));
         this.leftWingTipAngle = (float)Mth.lerp(0.45, this.leftWingTipAngle, -((float)Math.toRadians(15.0)));
      } else {
         this.bodyXRot = (float)Mth.lerp(0.25, this.bodyXRot, 0.0);
         this.bodyYOffset = (float)Mth.lerp(0.25, this.bodyYOffset, 0.0);
         this.rightWingAngle = (float)Mth.lerp(0.45, this.rightWingAngle, -((float)Math.toRadians(85.0)));
         this.leftWingAngle = (float)Mth.lerp(0.45, this.leftWingAngle, (float)Math.toRadians(85.0));
         this.rightWingMiddleAngle = (float)Mth.lerp(0.45, this.rightWingMiddleAngle, -((float)Math.toRadians(10.0)));
         this.leftWingMiddleAngle = (float)Mth.lerp(0.45, this.leftWingMiddleAngle, (float)Math.toRadians(10.0));
         this.rightWingMiddleFoldAngle = (float)Mth.lerp(0.45, this.rightWingMiddleFoldAngle, (float)Math.toRadians(30.0));
         this.leftWingMiddleFoldAngle = (float)Mth.lerp(0.45, this.leftWingMiddleFoldAngle, -((float)Math.toRadians(30.0)));
         this.rightWingFoldAngle = (float)Mth.lerp(0.45, this.rightWingFoldAngle, (float)Math.toRadians(0.0));
         this.leftWingFoldAngle = (float)Mth.lerp(0.45, this.leftWingFoldAngle, -((float)Math.toRadians(0.0)));
         this.rightWingTipAngle = (float)Mth.lerp(0.45, this.rightWingTipAngle, (float)Math.toRadians(60.0));
         this.leftWingTipAngle = (float)Mth.lerp(0.45, this.leftWingTipAngle, -((float)Math.toRadians(60.0)));
      }

      this.animationController.tick();
      this.messagingController.tick();
      if (!this.itemHandler.getStackInSlot(1).isEmpty()) {
         this.heldItemTime++;
         if (this.heldItemTime > 60
            && this.isOwlEdible(this.itemHandler.getStackInSlot(1))
            && (
               !this.isTame()
                  || this.getHealth() < this.getMaxHealth()
                  || this.emotions.getDistress() > 50
                  || this.emotionState == OwlEntity.EmotionState.DISTRESSED
            )) {
            this.heldItemTime = 0;
            this.heal(4.0F);
            if (!this.level().isClientSide) {
               HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, this.itemHandler.getStackInSlot(1)));
               this.emotions.setDistress(this.emotions.getDistress() - 25);
               this.emotions.setAnger(this.emotions.getAnger() - 15 - this.random.nextInt(5));
               this.emotionChanged();
            }

            this.playSound(SoundEvents.PARROT_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (TEMPTATION_ITEMS.test(this.itemHandler.getStackInSlot(1)) && this.fishThrowerID != null && !this.isTame()) {
               if (this.getRandom().nextFloat() < 0.5F
                  && this.level().getPlayerByUUID(this.fishThrowerID) != null
                  && !EventHooks.onAnimalTame(this, this.level().getPlayerByUUID(this.fishThrowerID))) {
                  this.setTame(true, true);
                  this.setOwnerUUID(this.fishThrowerID);
                  Player player = this.level().getPlayerByUUID(this.fishThrowerID);
                  if (player instanceof ServerPlayer) {
                     CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
                  }

                  this.level().broadcastEntityEvent(this, (byte)7);
               } else {
                  this.level().broadcastEntityEvent(this, (byte)6);
               }
            }

            if (this.itemHandler.getStackInSlot(1).hasCraftingRemainingItem()) {
               this.spawnAtLocation(this.itemHandler.getStackInSlot(1).getCraftingRemainingItem());
            }

            this.itemHandler.getStackInSlot(1).shrink(1);
            this.syncInv();
         }
      } else {
         this.heldItemTime = 0;
      }

      this.determineEmotionState();
   }

   public void eatParticles(ItemStack stack) {
      float scale = 3.0F;
      if (this.isBaby()) {
         scale = 4.0F;
      }

      Vec3 vec3 = this.calculateViewVector(0.0F, this.yHeadRot);

      for (int i = 0; i < 6; i++) {
         this.level()
            .addParticle(
               new ItemParticleOption(ParticleTypes.ITEM, stack),
               this.getRandomX(0.125) + vec3.x / scale,
               this.random.nextDouble() / 4.0 - 0.125 + this.getEyeY(),
               this.getRandomZ(0.125) + vec3.z / scale,
               (this.random.nextDouble() - 0.5) / 15.0,
               (this.random.nextDouble() + 0.5) * 0.15,
               (this.random.nextDouble() - 0.5) / 15.0
            );
      }
   }

   private float moveTo(float input, float moveTo, float speed) {
      float distance = moveTo - input;
      if (Math.abs(distance) <= speed) {
         return moveTo;
      } else {
         if (distance > 0.0F) {
            input += speed;
         } else {
            input -= speed;
         }

         return input;
      }
   }

   public void setPerchPos(BlockPos pos) {
      this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
   }

   public BlockPos getPerchPos() {
      return (BlockPos)((Optional)this.entityData.get(PERCH_POS)).orElse(null);
   }

   public DyeColor getDyeColor() {
      DyeColor color = HexereiUtil.getDyeColorNamed(this.getName().getString(), 0);
      return color == null ? DyeColor.byId((Integer)this.entityData.get(OWL_DYE_COLOR)) : color;
   }

   public int getDyeColorId() {
      return (Integer)this.entityData.get(OWL_DYE_COLOR);
   }

   public void setDyeColor(int color) {
      this.entityData.set(OWL_DYE_COLOR, color);
   }

   public void setDyeColor(DyeColor color) {
      this.entityData.set(OWL_DYE_COLOR, color.getId());
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return this.createFlyingNavigation(worldIn);
   }

   protected PathNavigation createFlyingNavigation(Level worldIn) {
      FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
      flyingpathnavigator.setCanOpenDoors(false);
      flyingpathnavigator.setCanFloat(true);
      flyingpathnavigator.setCanPassDoors(true);
      return flyingpathnavigator;
   }

   protected PathNavigation createGroundNavigation(Level worldIn) {
      GroundPathNavigation groundpathnavigator = new GroundPathNavigation(this, worldIn);
      groundpathnavigator.setCanOpenDoors(false);
      groundpathnavigator.setCanFloat(true);
      groundpathnavigator.setCanPassDoors(true);
      return groundpathnavigator;
   }

   public static AttributeSupplier createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 12.0)
         .add(Attributes.FLYING_SPEED, 0.6)
         .add(Attributes.MOVEMENT_SPEED, 0.2)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .build();
   }

   public ItemStack getItemBySlot(EquipmentSlot slot) {
      return switch (slot.getType()) {
         case HAND -> this.itemHandler.getStackInSlot(1);
         case HUMANOID_ARMOR -> this.itemHandler.getStackInSlot(0);
         case ANIMAL_ARMOR -> ItemStack.EMPTY;
         default -> throw new MatchException(null, null);
      };
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.readAdditionalSaveDataNoSuper(compound);
   }

   public void readAdditionalSaveDataNoSuper(CompoundTag compound) {
      this.setTypeVariant(compound.getInt("Variant"));
      if (compound.contains("IsFlyingNav")) {
         this.switchNavigator(compound.getBoolean("IsFlyingNav"), true);
      }

      if (compound.contains("IsFlying")) {
         this.setFlying(compound.getBoolean("IsFlying"));
      } else {
         this.entityData.set(DATA_FLYING, false);
      }

      if (compound.contains("InteractionRange")) {
         this.interactionRange = compound.getInt("InteractionRange");
      }

      if (compound.contains("CanAttack")) {
         this.canAttack = compound.getBoolean("CanAttack");
      }

      this.itemHandler.deserializeNBT(this.registryAccess(), compound.getCompound("inv"));
      if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
         this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
      }

      if (compound.contains("DyeColor")) {
         this.setDyeColor(compound.getInt("DyeColor"));
      }

      if (compound.contains("EmotionScales")) {
         int packedEmotionScales = compound.getInt("EmotionScales");
         int happiness = packedEmotionScales >> 16 & 0xFF;
         int distress = packedEmotionScales >> 8 & 0xFF;
         int anger = packedEmotionScales & 0xFF;
         this.emotions = new OwlEntity.Emotions(anger, distress, happiness);
      }

      this.quirkController.read(compound);
      this.messagingController.read(compound);
      if (compound.contains("task")) {
         this.currentTask = OwlEntity.OwlTask.byId(compound.getInt("task"));
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      this.addAdditionalSaveDataNoSuper(compound);
   }

   public void addAdditionalSaveDataNoSuper(CompoundTag compound) {
      compound.putInt("Variant", this.getTypeVariant());
      compound.putInt("InteractionRange", this.interactionRange);
      compound.putBoolean("CanAttack", this.canAttack);
      compound.putBoolean("IsFlying", this.isFlying());
      compound.putBoolean("IsFlyingNav", this.isFlyingNav());
      compound.put("inv", this.itemHandler.serializeNBT(this.registryAccess()));
      if (this.getPerchPos() != null) {
         compound.putInt("PerchX", this.getPerchPos().getX());
         compound.putInt("PerchY", this.getPerchPos().getY());
         compound.putInt("PerchZ", this.getPerchPos().getZ());
      }

      compound.putInt("DyeColor", this.getDyeColorId());
      int packedEmotionScales = this.emotions.getHappiness() << 16 | this.emotions.getDistress() << 8 | this.emotions.getAnger();
      compound.putInt("EmotionScales", packedEmotionScales);
      this.quirkController.write(compound);
      this.messagingController.write(compound);
      compound.putInt("task", this.currentTask.ordinal());
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomsource = level.getRandom();
      OwlVariant variant;
      if (spawnGroupData instanceof OwlEntity.CrowGroupData) {
         variant = ((OwlEntity.CrowGroupData)spawnGroupData).variant;
      } else {
         boolean isVariant = randomsource.nextInt(5) == 0;
         variant = (OwlVariant)Util.getRandom(OwlVariant.values(), randomsource);
         if (!isVariant) {
            variant = OwlVariant.GREAT_HORNED;
         }

         spawnGroupData = new OwlEntity.CrowGroupData(variant);
      }

      this.setTypeVariant(variant.getId() & 0xFF);
      List<Block> col = BuiltInRegistries.BLOCK.stream().toList();

      for (int i = 0; i < 25; i++) {
         if (col.toArray()[(int)(col.size() * new Random().nextFloat())] instanceof Block block) {
            try {
               if (Block.isFaceFull(block.defaultBlockState().getShape(level, this.blockPosition(), CollisionContext.empty()), Direction.UP)
                  && block.asItem() != Items.AIR) {
                  this.quirkController.addQuirk(new FavoriteBlockQuirk(block, 20));
                  break;
               }
            } catch (Exception var11) {
               LOGGER.error("Error trying to set block as favorite: {}", block, var11);
            }
         }
      }

      return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
   }

   public void aiStep() {
      super.aiStep();
      Vec3 motion = this.getDeltaMovement();
      if (this.isFlying() && motion.y < 0.0) {
         this.setDeltaMovement(motion.multiply(1.0, 0.7, 1.0));
      }
   }

   public void travel(Vec3 vec3d) {
      if (this.getPerchPos() == null) {
         if ((this.isOrderedToSit() || this.isInSittingPose()) && this.currentTask.isNone()) {
            if (this.getNavigation().getPath() != null) {
               this.getNavigation().stop();
            }

            vec3d = Vec3.ZERO;
         }
      } else {
         double topOffset = this.level().getBlockState(this.getPerchPos()).getOcclusionShape(this.level(), this.getPerchPos()).max(Axis.Y);
         if (this.distanceTo(this.getPerchPos().getX(), this.getPerchPos().getZ()) < 1.0
            && this.position().y() >= this.getPerchPos().getY() + topOffset
            && this.position().y() < this.getPerchPos().above().getY() + topOffset - 0.75
            && (this.isOrderedToSit() || this.isInSittingPose())
            && this.currentTask.isNone()) {
            if (this.getNavigation().getPath() != null) {
               this.getNavigation().stop();
            }

            vec3d = Vec3.ZERO;
         }
      }

      super.travel(vec3d);
   }

   public double distanceTo(double p_20276_, double p_20278_) {
      double d0 = this.getX() - p_20276_ - 0.5;
      double d1 = this.getZ() - p_20278_ - 0.5;
      return Mth.sqrt((float)(d0 * d0 + d1 * d1));
   }

   protected int calculateFallDamage(float p_21237_, float p_21238_) {
      return 0;
   }

   protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
   }

   public boolean isFood(ItemStack stack) {
      return TEMPTATION_ITEMS.test(stack);
   }

   public Map<String, Vector3f> getModelRotationValues() {
      return this.modelRotationValues;
   }

   public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
      OwlEntity owl = (OwlEntity)((EntityType)ModEntityTypes.OWL.get()).create(world);
      if (owl != null) {
         OwlVariant owlVariant = ((OwlEntity)entity).getVariant();
         if (this.random.nextBoolean()) {
            owlVariant = this.getVariant();
         }

         owl.setTypeVariant(owlVariant.getId() & 0xFF);
         owl.setPersistenceRequired();
      }

      return owl;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (this.itemHandler.getStackInSlot(1).isEmpty()) {
         if (!this.isTame() && this.isOwlTemptItem(itemstack)) {
            ItemStack particleCopy = itemstack.copy();
            if (!player.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            if (!this.level().isClientSide) {
               this.emotions.setDistress(this.emotions.getDistress() - 25);
               this.emotions.setAnger(this.emotions.getAnger() - 15 - this.random.nextInt(5));
               this.emotionChanged();
               this.heal(4.0F);
               if (!this.level().isClientSide) {
                  HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, particleCopy));
               }

               if (this.random.nextInt(5) == 0 && !EventHooks.onAnimalTame(this, player)) {
                  this.tame(player);
                  this.level().broadcastEntityEvent(this, (byte)7);
               } else {
                  this.level().broadcastEntityEvent(this, (byte)6);
               }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }

         if (this.isTame() && this.isOwlTemptItem(itemstack) && (this.emotions.getDistress() > 15 || !this.isMaxHealth())) {
            ItemStack particleCopyx = itemstack.copy();
            if (!player.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            if (!this.level().isClientSide) {
               this.emotions.setDistress(this.emotions.getDistress() - 25);
               this.emotions.setAnger(this.emotions.getAnger() - 15 - this.random.nextInt(5));
               this.emotionChanged();
               this.heal(4.0F);
               if (!this.level().isClientSide) {
                  HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, particleCopyx));
               }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }

         if (!player.isSecondaryUseActive()
            && this.isTame()
            && this.isOwnedBy(player)
            && !this.isBaby()
            && itemstack.getItem() == Items.RABBIT
            && !this.isInLove()) {
            if (this.getAge() == 0) {
               if (!player.getAbilities().instabuild) {
                  itemstack.shrink(1);
               }

               if (!this.level().isClientSide) {
                  this.setInLove(player);
                  this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
                  this.itemHandler.setStackInSlot(1, new ItemStack(Items.RABBIT));
                  this.breedGiftGivenByPlayer = true;
                  this.breedGiftGivenByPlayerUUID = player.getUUID();
                  this.currentTask = OwlEntity.OwlTask.BREEDING;
               }
            } else if (!this.headShakeAnimation.active) {
               this.headShakeAnimation.start();
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }
      }

      if (!this.isTame() || !this.isOwnedBy(player)) {
         return super.mobInteract(player, hand);
      } else if (player.isSecondaryUseActive() && this.isOwnedBy(player)) {
         if (!this.level().isClientSide()) {
            MenuProvider containerProvider = this.createContainerProvider(this.level(), this.blockPosition());
            player.openMenu(containerProvider, b -> b.writeInt(this.getId()));
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else if (itemstack.getItem() == ModItems.COURIER_LETTER.get()) {
         if (this.getOwner() == player) {
            if (this.level().isClientSide) {
               if (!CourierLetterItem.isSealed(itemstack)) {
                  player.sendSystemMessage(Component.translatable("hexerei.letter.empty"));
               }
            } else if (player instanceof ServerPlayer serverPlayer && CourierLetterItem.isSealed(itemstack)) {
               HexereiPacketHandler.sendToPlayerClient(
                  new ClientboundOpenOwlCourierSendScreenPacket(this.getId(), hand, player.getInventory().selected), serverPlayer
               );
            }
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else if (itemstack.getItem() == ModItems.COURIER_PACKAGE.get()) {
         CourierPackageItem.PackageInvWrapper wrapper = new CourierPackageItem.PackageInvWrapper(itemstack);
         boolean empty = wrapper.isEmpty();
         if (this.getOwner() == player) {
            if (this.level().isClientSide) {
               if (empty || !wrapper.getSealed()) {
                  player.sendSystemMessage(Component.translatable("hexerei.package.empty"));
               }
            } else if (player instanceof ServerPlayer serverPlayer && !empty && wrapper.getSealed()) {
               HexereiPacketHandler.sendToPlayerClient(
                  new ClientboundOpenOwlCourierSendScreenPacket(this.getId(), hand, player.getInventory().selected), serverPlayer
               );
            }
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         if (!this.level().isClientSide) {
            if (itemstack.getItem() instanceof DyeItem) {
               DyeColor dyecolor = ((DyeItem)itemstack.getItem()).getDyeColor();
               if (dyecolor != this.getDyeColor() || this.getDyeColorId() == -1) {
                  this.setDyeColor(dyecolor);
                  if (!player.getAbilities().instabuild) {
                     itemstack.shrink(1);
                  }

                  return InteractionResult.SUCCESS;
               }
            }

            this.setOrderedToSit(!this.isOrderedToSit());
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      }
   }

   protected void dropAllDeathLoot(ServerLevel p_level, DamageSource damageSource) {
      ItemStack hat = this.itemHandler.getStackInSlot(0);
      ItemStack itemstack = this.itemHandler.getStackInSlot(1);
      ItemStack messageStack = this.messagingController.getMessageStack();
      if (!itemstack.isEmpty()) {
         this.spawnAtLocation(itemstack);
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }

      if (!hat.isEmpty()) {
         this.spawnAtLocation(hat.copy());
         this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
      }

      if (!messageStack.isEmpty()) {
         this.spawnAtLocation(messageStack.copy());
         this.messagingController.messageStack = ItemStack.EMPTY;
      }

      super.dropAllDeathLoot(p_level, damageSource);
   }

   private boolean isOwlEdible(ItemStack stack) {
      return stack.has(DataComponents.FOOD) || this.isOwlTemptItem(stack);
   }

   private boolean isOwlTemptItem(ItemStack stack) {
      return TEMPTATION_ITEMS.test(stack);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      if (this.isTame()) {
         if (this.getHealth() < this.getMaxHealth()) {
            return this.isOwlEdible(stack);
         }

         if (this.emotions.getDistress() > 50 || this.emotionState == OwlEntity.EmotionState.DISTRESSED) {
            return this.isOwlTemptItem(stack);
         }
      }

      return !this.isTame() && this.isOwlEdible(stack) && !this.isMaxHealth() || this.isOwlTemptItem(stack);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack duplicate = e.getItem().copy();
      duplicate.setCount(1);
      if (!this.itemHandler.getStackInSlot(1).isEmpty() && !this.level().isClientSide) {
         this.spawnAtLocation(this.itemHandler.getStackInSlot(1), 0.0F);
      }

      Entity itemThrower = e.getOwner();
      this.itemHandler.setStackInSlot(1, duplicate);
      if (TEMPTATION_ITEMS.test(e.getItem()) && !this.isTame()) {
         this.fishThrowerID = itemThrower == null ? null : itemThrower.getUUID();
      } else {
         this.fishThrowerID = null;
      }

      if (this.currentTask == OwlEntity.OwlTask.PICKUP_ITEM) {
         this.currentTask = OwlEntity.OwlTask.NONE;
      }
   }

   @Override
   public void onFindTarget(ItemEntity e) {
      if (this.currentTask.isNone()) {
         this.currentTask = OwlEntity.OwlTask.PICKUP_ITEM;
      }

      ITargetsDroppedItems.super.onFindTarget(e);
   }

   @Override
   public double getMaxDistToItem() {
      return 1.0;
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(DATA_FLYING);
   }

   public boolean canSitOnShoulder() {
      return this.rideCooldownCounter > 100;
   }

   protected void doPush(Entity entityIn) {
      if (!(entityIn instanceof Player)) {
         super.doPush(entityIn);
      }
   }

   public boolean onGround() {
      return this.isPassenger() || super.onGround();
   }

   public int getContainerSize() {
      return 2;
   }

   public boolean canPlaceItem(int pIndex, ItemStack pStack) {
      return this.itemHandler.isItemValid(pIndex, pStack);
   }

   public boolean canTakeItem(Container pTarget, int pIndex, ItemStack pStack) {
      return false;
   }

   public boolean isEmpty() {
      return false;
   }

   public ItemStack getItem(int index) {
      return this.itemHandler.getStackInSlot(index);
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack stack = this.itemHandler.getStackInSlot(index).copy();
      if (count >= stack.getCount()) {
         this.itemHandler.setStackInSlot(index, ItemStack.EMPTY);
      } else {
         this.itemHandler.getStackInSlot(index).setCount(stack.getCount() - count);
         stack.setCount(count);
      }

      return stack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      ItemStack stack = this.itemHandler.getStackInSlot(index).copy();
      this.itemHandler.setStackInSlot(index, ItemStack.EMPTY);
      return stack;
   }

   public void setItem(int index, ItemStack stack) {
      if (index >= 0 && index < 3) {
         this.itemHandler.setStackInSlot(index, stack);
      }

      this.syncAdditionalData();
   }

   public void setChanged() {
   }

   public boolean stillValid(Player player) {
      return this.isRemoved() ? false : !(player.distanceToSqr(this) > 144.0);
   }

   public void clearContent() {
      for (int i = 0; i < 3; i++) {
         this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
      }
   }

   @org.jetbrains.annotations.Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
      return new OwlContainer(id, this, inv, player);
   }

   private MenuProvider createContainerProvider(Level worldIn, BlockPos pos) {
      return new MenuProvider() {
         @org.jetbrains.annotations.Nullable
         public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return new OwlContainer(i, OwlEntity.this, playerInventory, playerEntity);
         }

         public Component getDisplayName() {
            return Component.translatable("");
         }
      };
   }

   public boolean isPowered() {
      return false;
   }

   public void containerChanged(Container p_18983_) {
      ItemStack stack = p_18983_.getItem(0);
      stack.setEntityRepresentation(this);
   }

   public static void teleportParticles(Level level, Vec3 pos, OwlVariant owlVariant) {
      if (level.isClientSide()) {
         for (int i = 0; i < 10; i++) {
            RandomSource random = level.getRandom();
            SimpleParticleType particleType = getParticle(owlVariant);
            Vec3 offset = new Vec3(
               random.nextDouble() / 2.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 2.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               particleType,
               true,
               pos.x() + 0.5 + offset.x,
               pos.y() + random.nextDouble() * 0.15000000596046448,
               pos.z() + 0.5 + offset.z,
               offset.x / 4.0,
               random.nextDouble() * -0.05 - 0.05,
               offset.z / 4.0
            );
         }

         RandomSource random = level.getRandom();
         SimpleParticleType particleType = (SimpleParticleType)ModParticleTypes.STAR_BRUSH.get();
         float radius = 3.0F;

         for (int i = 0; i < 10; i++) {
            float rotation = random.nextFloat() * 18.0F + 36.0F * i;
            float rad = radius * random.nextFloat() * 0.5F;
            Vec3 offset = new Vec3(rad * Math.cos(rotation), 0.0, rad * Math.sin(rotation));
            level.addParticle(
               particleType,
               true,
               pos.x() + offset.x,
               pos.y() + random.nextDouble() * 0.15000000596046448,
               pos.z() + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.025,
               offset.z / 20.0
            );
         }
      }
   }

   public static SimpleParticleType getParticle(OwlVariant owlVariant) {
      return switch (owlVariant) {
         case GREAT_HORNED -> (SimpleParticleType)ModParticleTypes.OWL_TELEPORT.get();
         case BARN -> (SimpleParticleType)ModParticleTypes.OWL_TELEPORT_BARN.get();
         case BARRED -> (SimpleParticleType)ModParticleTypes.OWL_TELEPORT_BARRED.get();
         case SNOWY -> (SimpleParticleType)ModParticleTypes.OWL_TELEPORT_SNOWY.get();
      };
   }

   private void flyOrWalkTo(Vec3 pos) {
      Path path1 = this.flyingNav.createPath(BlockPos.containing(pos), 0);
      Path path2 = this.groundNav.createPath(BlockPos.containing(pos), 0);
      if (path1 != null) {
         if (path2 != null && path2.canReach()) {
            if (path2.getDistToTarget() > path1.getDistToTarget()) {
               this.switchNavigator(true);
            } else {
               this.switchNavigator(!this.random.nextBoolean());
            }
         } else {
            this.switchNavigator(true);
         }
      }
   }

   private void walkToIfNotFlyTo(Vec3 pos) {
      Path path1 = this.flyingNav.createPath(BlockPos.containing(pos), 0);
      Path path2 = this.groundNav.createPath(BlockPos.containing(pos), 0);
      if (path1 != null) {
         if (path2 == null) {
            this.switchNavigator(true);
         } else if (Math.max(0.0F, path2.getDistToTarget() - 2.0F) > path1.getDistToTarget()) {
            this.switchNavigator(true);
         } else {
            this.switchNavigator(false);
         }
      }
   }

   private void teleportToOwner() {
      if (this.getOwner() instanceof Player owner) {
         BlockPos blockpos = owner.blockPosition();

         for (int i = 0; i < 30; i++) {
            int j = this.randomIntInclusive(-5, 5);
            int k = this.randomIntInclusive(-1, 5);
            int l = this.randomIntInclusive(-5, 5);
            boolean flag = this.teleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
               return;
            }
         }
      }
   }

   private boolean teleportToDest(ResourceKey<Level> dim, BlockPos blockpos) {
      for (int i = 0; i < 30; i++) {
         int j = this.randomInt(6, 12);
         int k = this.randomInt(6, 12);
         int l = this.randomInt(6, 12);
         boolean flag = this.teleportTo(dim, blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
         if (flag) {
            return true;
         }
      }

      return false;
   }

   private int randomInt(int min, int max) {
      return (this.getRandom().nextInt(max - min + 1) + min) * (this.getRandom().nextBoolean() ? -1 : 1);
   }

   private boolean teleportTo(int x, int y, int z) {
      if (!this.canTeleportTo(new BlockPos(x, y, z))) {
         return false;
      } else {
         this.switchNavigator(true, true);
         this.setPos(x, y, z);
         this.moveTo(x + 0.5, y, z + 0.5, this.getYRot(), this.getXRot());
         return true;
      }
   }

   private boolean teleportTo(ResourceKey<Level> dim, int x, int y, int z) {
      if (!this.canTeleportTo(dim, new BlockPos(x, y, z))) {
         return false;
      } else {
         this.switchNavigator(true, true);
         this.setPos(x, y, z);
         this.moveTo(x + 0.5, y, z + 0.5, this.getYRot(), this.getXRot());
         return true;
      }
   }

   private boolean canTeleportTo(BlockPos pos) {
      if (pos.getY() <= -64) {
         return false;
      } else {
         BlockPos offset = pos.subtract(this.blockPosition());
         FluidState state = this.level().getFluidState(pos);
         return this.level().noCollision(this, this.getBoundingBox().move(offset)) && state.isEmpty();
      }
   }

   private boolean canTeleportTo(ResourceKey<Level> dim, BlockPos pos) {
      if (pos.getY() <= -64) {
         return false;
      } else {
         BlockPos offset = pos.subtract(this.blockPosition());
         if (this.getServer().getLevel(dim) == null) {
            return false;
         } else {
            FluidState state = this.getServer().getLevel(dim).getFluidState(pos);
            return this.getServer().getLevel(dim).noCollision(this, this.getBoundingBox().move(offset)) && state.isEmpty();
         }
      }
   }

   private int randomIntInclusive(int p_25301_, int p_25302_) {
      return this.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
   }

   public interface Animation {
      void tick();

      void start();

      void stop();

      boolean isActive();
   }

   public class AnimationBase implements OwlEntity.Animation {
      public boolean active;
      public int cooldownTimer;
      public int activeTimer;
      public boolean useCooldown = true;

      @Override
      public void tick() {
         this.preTick();
         if (this.active) {
            this.activeTick();
            this.activeTimer--;
            if (this.activeTimer <= 0) {
               this.stop();
            }
         } else if (!OwlEntity.this.level().isClientSide && this.useCooldown) {
            this.cooldownTimer--;
            if (this.cooldownTimer <= 0) {
               this.start();
            }
         }

         this.postTick();
      }

      public void preTick() {
      }

      public void activeTick() {
      }

      public void postTick() {
      }

      @Override
      public void start() {
         this.active = true;
      }

      @Override
      public void stop() {
         this.active = false;
      }

      @Override
      public boolean isActive() {
         return this.active;
      }
   }

   public class AnimationController {
      private List<OwlEntity.Animation> animations = new ArrayList<>();

      public void addAnimation(OwlEntity.Animation animation) {
         this.animations.add(animation);
      }

      public void tick() {
         for (OwlEntity.Animation animation : this.animations) {
            animation.tick();
         }
      }
   }

   public class BreedGoal extends Goal {
      private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight();
      protected final Animal animal;
      private final Class<? extends Animal> partnerClass;
      protected final Level level;
      @Nullable
      protected Animal partner;
      private int loveTime;
      private final double speedModifier;

      public BreedGoal(Animal p_25122_, double p_25123_) {
         this(p_25122_, p_25123_, (Class<? extends Animal>)p_25122_.getClass());
      }

      public BreedGoal(Animal p_25125_, double p_25126_, Class<? extends Animal> p_25127_) {
         this.animal = p_25125_;
         this.level = p_25125_.level();
         this.partnerClass = p_25127_;
         this.speedModifier = p_25126_;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (OwlEntity.this.currentTask.is(OwlEntity.OwlTask.BREEDING)) {
            this.partner = this.getFreePartner();
            return this.partner != null;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.partner != null && this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 120;
      }

      public void start() {
         if (OwlEntity.this.isInSittingPose()) {
            OwlEntity.this.setInSittingPose(false);
            OwlEntity.this.setOrderedToSit(false);
         }

         super.start();
      }

      public void stop() {
      }

      public void tick() {
         if (this.partner != null) {
            this.animal.getLookControl().setLookAt(this.partner, 10.0F, this.animal.getMaxHeadXRot());
            OwlEntity.this.walkToIfNotFlyTo(this.partner.position());
            this.animal.getNavigation().moveTo(this.partner, OwlEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
            this.loveTime++;
            if (this.animal.distanceToSqr(this.partner) < 4.0 && OwlEntity.this.breedGiftGivenByPlayer) {
               if (!((OwlEntity)this.partner).isOrderedToSit()) {
                  ((OwlEntity)this.partner).setOrderedToSit(true);
               }

               if (!this.partner.getLookControl().isLookingAtTarget()) {
                  this.partner.getLookControl().setLookAt(this.animal);
               }

               OwlEntity.this.waitToGiveTime++;
               if (OwlEntity.this.waitToGiveTime > 20 && OwlEntity.this.onGround() && OwlEntity.this.itemHandler.getStackInSlot(1).is(Items.RABBIT)) {
                  ((OwlEntity)this.partner).setOrderedToSit(false);
                  OwlEntity.this.waitToGiveTime = 0;
                  OwlEntity.this.breedGiftGivenByPlayer = false;
                  OwlEntity.this.peck();
                  HexereiPacketHandler.sendToNearbyClient(OwlEntity.this.level(), OwlEntity.this, new PeckPacket(OwlEntity.this));
                  ItemStack stack = ((OwlEntity)this.partner).itemHandler.getStackInSlot(1).copy();
                  ItemStack stack2 = OwlEntity.this.itemHandler.getStackInSlot(1).copy();
                  ((OwlEntity)this.partner).itemHandler.setStackInSlot(1, stack2);
                  OwlEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
                  ItemEntity itemEntity = new ItemEntity(
                     this.partner.level(), this.partner.position().x, this.partner.position().y, this.partner.position().z, stack
                  );
                  this.partner.level().addFreshEntity(itemEntity);
                  if (OwlEntity.this.breedGiftGivenByPlayerUUID != null && this.level.getPlayerByUUID(OwlEntity.this.breedGiftGivenByPlayerUUID) != null) {
                     ((OwlEntity)this.partner).breedGiftGivenByPlayerUUID = OwlEntity.this.breedGiftGivenByPlayerUUID;
                     ((OwlEntity)this.partner).breedGiftGivenByPartnerTimer = 20;
                  } else if (OwlEntity.this.getOwner() instanceof Player) {
                     this.partner.setInLove((Player)OwlEntity.this.getOwner());
                  }
               }
            }

            if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < 9.0) {
               this.breed();
               OwlEntity.this.currentTask = OwlEntity.OwlTask.NONE;
               this.partner = null;
               this.loveTime = 0;
            }
         }
      }

      public boolean canMateOwlBringGift(Animal animal) {
         if (animal.isBaby()) {
            return false;
         } else if (animal == OwlEntity.this) {
            return false;
         } else {
            return animal.getClass() != OwlEntity.this.getClass()
               ? false
               : OwlEntity.this.itemHandler.getStackInSlot(1).is(Items.RABBIT) && animal.getAge() == 0;
         }
      }

      public boolean canMateOwlReceiveGift(Animal animal) {
         if (animal.isBaby()) {
            return false;
         } else if (animal == OwlEntity.this) {
            return false;
         } else {
            return animal.getClass() != OwlEntity.this.getClass() ? false : OwlEntity.this.isInLove() && animal.isInLove();
         }
      }

      @Nullable
      private Animal getFreePartner() {
         List<? extends Animal> list = this.level
            .getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(16.0));
         double d0 = 1.7976931348623157E308;
         Animal animal = null;
         if (OwlEntity.this.breedGiftGivenByPlayer) {
            for (Animal animal1 : list) {
               if (this.canMateOwlBringGift(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                  animal = animal1;
                  d0 = this.animal.distanceToSqr(animal1);
               }
            }
         } else {
            for (Animal animal1x : list) {
               if (this.canMateOwlReceiveGift(animal1x) && this.animal.distanceToSqr(animal1x) < d0) {
                  animal = animal1x;
                  d0 = this.animal.distanceToSqr(animal1x);
               }
            }
         }

         return animal;
      }

      protected void breed() {
         OwlEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
         this.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
      }

      public void spawnChildFromBreeding(ServerLevel p_27564_, Animal p_27565_) {
         AgeableMob ageablemob = OwlEntity.this.getBreedOffspring(p_27564_, p_27565_);
         BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(OwlEntity.this, p_27565_, ageablemob);
         boolean cancelled = ((BabyEntitySpawnEvent)NeoForge.EVENT_BUS.post(event)).isCanceled();
         ageablemob = event.getChild();
         if (cancelled) {
            OwlEntity.this.setAge(6000);
            p_27565_.setAge(6000);
            OwlEntity.this.resetLove();
            p_27565_.resetLove();
         } else {
            if (ageablemob != null) {
               ServerPlayer serverplayer = OwlEntity.this.getLoveCause();
               if (serverplayer == null && p_27565_.getLoveCause() != null) {
                  serverplayer = p_27565_.getLoveCause();
               }

               if (serverplayer != null) {
                  serverplayer.awardStat(Stats.ANIMALS_BRED);
                  CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, OwlEntity.this, p_27565_, ageablemob);
               }

               OwlEntity.this.setAge(6000);
               p_27565_.setAge(6000);
               OwlEntity.this.resetLove();
               p_27565_.resetLove();
               ageablemob.setBaby(true);
               ageablemob.moveTo(this.partner.getX(), this.partner.getY(), this.partner.getZ(), 0.0F, 0.0F);
               p_27564_.addFreshEntityWithPassengers(ageablemob);
               ((OwlEntity)ageablemob).setOwnerUUID(OwlEntity.this.getOwnerUUID());
               ((OwlEntity)ageablemob).setTame(true, false);
               ((OwlEntity)ageablemob).setOrderedToSit(true);
               p_27564_.broadcastEntityEvent(OwlEntity.this, (byte)18);
               if (p_27564_.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                  p_27564_.addFreshEntity(
                     new ExperienceOrb(p_27564_, OwlEntity.this.getX(), OwlEntity.this.getY(), OwlEntity.this.getZ(), OwlEntity.this.getRandom().nextInt(7) + 1)
                  );
               }
            }
         }
      }
   }

   public static enum BrowAnim {
      LEFT,
      RIGHT,
      BOTH;
   }

   public class BrowAnimation extends OwlEntity.AnimationBase {
      private OwlEntity.BrowAnim browAnim = OwlEntity.BrowAnim.BOTH;
      private float browRotTarget;
      private float browRot = 0.0F;
      private OwlEntity owl;

      public float getBrowRot() {
         return this.browRot;
      }

      public void setBrowAnim(OwlEntity.BrowAnim browAnim) {
         this.browAnim = browAnim;
      }

      public OwlEntity.BrowAnim getBrowAnim() {
         return this.browAnim;
      }

      public BrowAnimation(OwlEntity owl) {
         this.browRotTarget = 0.0F;
         this.owl = owl;
      }

      @Override
      public void activeTick() {
         if (this.owl.level().isClientSide) {
            this.browRotTarget = Mth.sin(this.owl.tickCount + this.owl.getId() * 342) * 16.0F;
         }
      }

      @Override
      public void postTick() {
         this.browRot = OwlEntity.this.moveTo(this.browRot, this.browRotTarget, 30.0F);
      }

      @Override
      public void start() {
         super.start();
         if (!this.owl.level().isClientSide) {
            this.activeTimer = 5 + this.owl.getRandom().nextInt(10);
            HexereiPacketHandler.sendToNearbyClient(
               this.owl.level(),
               this.owl,
               new BrowAnimPacket(this.owl, OwlEntity.BrowAnim.values()[this.owl.getRandom().nextInt(OwlEntity.BrowAnim.values().length)], this.activeTimer)
            );
         }
      }

      @Override
      public void stop() {
         super.stop();
         this.cooldownTimer = OwlEntity.this.random.nextInt(160) + 60;
         this.browRotTarget = 0.0F;
      }
   }

   public class BrowHappyAnimation extends OwlEntity.AnimationBase {
      private OwlEntity.BrowAnim browAnim = OwlEntity.BrowAnim.BOTH;
      private float browRotTarget;
      private float browRot = 0.0F;
      private float browRotLast;
      private OwlEntity owl;

      public float getBrowRot() {
         return this.browRot;
      }

      public float getBrowRotLast() {
         return this.browRotLast;
      }

      public void setBrowAnim(OwlEntity.BrowAnim browAnim) {
         this.browAnim = browAnim;
      }

      public OwlEntity.BrowAnim getBrowAnim() {
         return this.browAnim;
      }

      public BrowHappyAnimation(OwlEntity owl) {
         this.browRotTarget = 0.0F;
         this.owl = owl;
      }

      @Override
      public void activeTick() {
         if (this.owl.level().isClientSide) {
            float val = this.owl.tickCount + this.owl.getId() * 342;
            this.browRotTarget = Mth.sin(val) * 26.0F;
         }
      }

      @Override
      public void postTick() {
         this.browRotLast = this.browRot;
         this.browRot = OwlEntity.this.moveTo(this.browRot, this.browRotTarget, 30.0F);
      }

      @Override
      public void start() {
         super.start();
         if (!this.owl.level().isClientSide) {
            this.activeTimer = 15 + this.owl.getRandom().nextInt(10);
            HexereiPacketHandler.sendToNearbyClient(
               this.owl.level(),
               this.owl,
               new BrowAnimPacket(
                  this.owl, OwlEntity.BrowAnim.values()[this.owl.getRandom().nextInt(OwlEntity.BrowAnim.values().length)], this.activeTimer, true
               )
            );
         }
      }

      @Override
      public void stop() {
         super.stop();
         this.cooldownTimer = OwlEntity.this.random.nextInt(160) + 60;
         this.browRotTarget = 0.0F;
      }
   }

   public static enum BrowPositioning {
      NORMAL(0.0F, 0.0F, 0.0F),
      PLEAD(0.75F, 0.05F, 40.0F),
      ANGRY(0.25F, 0.5F, -15.0F);

      final float xOffset;
      final float yOffset;
      final float zRot;

      private BrowPositioning(float xOffset, float yOffset, float zRot) {
         this.xOffset = xOffset;
         this.yOffset = yOffset;
         this.zRot = zRot;
      }

      public float getxOffset() {
         return this.xOffset;
      }

      public float getyOffset() {
         return this.yOffset;
      }

      public float getzRot() {
         return this.zRot;
      }
   }

   public static class CrowGroupData extends AgeableMobGroupData {
      public final OwlVariant variant;

      public CrowGroupData(OwlVariant pVariant) {
         super(true);
         this.variant = pVariant;
      }
   }

   public class DeliverMessageGoal extends Goal {
      private final OwlEntity owl;
      private Vec3 wantedPos = null;
      private static final int REFRESH_MAX = 5;
      private int refresh = 5;
      private int stuck = 0;
      private int stuckStageTotal = 0;
      private int checkOldPos = 0;
      private Vec3 oldPos = null;
      private Path stuckPath = null;
      private BlockPos stuckPathDest = null;

      public DeliverMessageGoal(OwlEntity owl) {
         this.owl = owl;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public boolean canContinueToUse() {
         return !OwlEntity.this.currentTask.is(OwlEntity.OwlTask.DELIVER_MESSAGE) ? false : this.owl.messagingController.isDelivering();
      }

      public boolean canUse() {
         if (!OwlEntity.this.currentTask.is(OwlEntity.OwlTask.DELIVER_MESSAGE)) {
            return false;
         } else {
            return !this.owl.messagingController.isDelivering() ? false : this.owl.isTame();
         }
      }

      public void tick() {
         OwlEntity.MessagingController controller = this.owl.messagingController;
         this.refresh++;
         this.stuck++;
         this.stuckStageTotal++;
         this.checkOldPos++;
         if (this.checkOldPos > 40) {
            if (this.oldPos != null && this.oldPos.distanceTo(this.owl.position()) < 1.0) {
               this.stuck = 200;
            }

            this.oldPos = this.owl.position();
            this.checkOldPos = 0;
         }

         this.owl.setInSittingPose(false);
         if (this.stuckStageTotal >= 400) {
            switch (controller.stage) {
               case FIND_FLY_OFF_LOCATION:
               case FLY_OFF_AND_TELEPORT:
               case FLY_TO_DESTINATION:
                  if (controller.getDestination() != null) {
                     this.wantedPos = controller.getDestination().pos().getCenter();
                     controller.stage = OwlEntity.MessagingController.Stage.FLY_TO_DESTINATION;
                  }
                  break;
               case FIND_FLY_BACK_LOCATION:
               case FLY_BACK_AND_TELEPORT:
               case RETURN_TO_START:
                  if (controller.startPos != null) {
                     this.wantedPos = controller.startPos.pos().getCenter();
                     controller.stage = OwlEntity.MessagingController.Stage.RETURN_TO_START;
                  }
            }

            Vec3 oldPos = this.owl.position();
            ResourceKey<Level> oldDim = this.owl.level().dimension();
            OwlEntity.this.teleportTo(this.wantedPos.x(), this.wantedPos.y(), this.wantedPos.z());
            HexereiPacketHandler.sendToNearbyClient(
               this.owl.level(), this.owl, new OwlTeleportParticlePacket(this.owl.level().dimension(), this.owl.position(), this.owl.getVariant())
            );
            HexereiPacketHandler.sendToNearbyClient(
               this.owl.level().getServer().getLevel(oldDim),
               this.owl,
               new OwlTeleportParticlePacket(this.owl.level().dimension(), oldPos, this.owl.getVariant())
            );
            this.stuckStageTotal = 0;
            this.stuck = 0;
            this.stuckPath = null;
         } else if (this.stuck < 160 && this.stuckPath == null) {
            switch (controller.stage) {
               case FIND_FLY_OFF_LOCATION:
                  for (int ixx = 0; ixx < 10; ixx++) {
                     Vec3 vec3 = this.owl.getViewVector(0.0F);
                     Vec3 vec = getPos(this.owl, 14, 4, 6, vec3.x, vec3.z, 1.5707963705062866);
                     if (vec != null && vec.distanceTo(this.owl.position()) > 8.0) {
                        OwlEntity.this.switchNavigator(true, true);
                        Path pathx = this.owl.getNavigation().createPath(BlockPos.containing(vec), 0);
                        if (pathx != null && pathx.canReach()) {
                           this.wantedPos = vec;
                           if (controller.forceLoadChunks()) {
                              controller.stage = OwlEntity.MessagingController.Stage.FLY_OFF_AND_TELEPORT;
                              this.refresh = 5;
                              this.stuck = 0;
                              this.stuckStageTotal = 0;
                              break;
                           }
                        }
                     }
                  }
                  break;
               case FLY_OFF_AND_TELEPORT:
                  if (controller.hasDestination()) {
                     if (this.wantedPos == null) {
                        controller.stage = OwlEntity.MessagingController.Stage.FIND_FLY_OFF_LOCATION;
                     } else {
                        BlockPos posxx = controller.getDestination().pos();
                        Path pathx = this.owl.getNavigation().createPath(posxx, 0);
                        double dist = this.owl.distanceToSqr(Vec3.atBottomCenterOf(posxx));
                        if (pathx != null && pathx.getDistToTarget() < 2.0F && dist < 1.0) {
                           controller.stage = OwlEntity.MessagingController.Stage.FLY_TO_DESTINATION;
                        } else {
                           pathx = this.owl.getNavigation().createPath(BlockPos.containing(this.wantedPos), 0);
                           if (this.refresh > 5) {
                              this.owl.getNavigation().moveTo(pathx, 2.0);
                              this.refresh = 0;
                           }

                           if (controller.forceLoadChunks() && OwlEntity.this.distanceToSqr(this.wantedPos.x, this.wantedPos.y, this.wantedPos.z) < 4.0) {
                              ResourceKey<Level> oldDim = this.owl.level().dimension();
                              Vec3 oldPos = new Vec3(this.owl.position().toVector3f());
                              if (OwlEntity.this.teleportToDest(controller.getDestination().dimension(), posxx)) {
                                 ServerLevel dimChange = this.owl.getServer().getLevel(controller.getDestination().dimension());
                                 if (dimChange != null && !dimChange.equals(this.owl.level())) {
                                    this.owl
                                       .changeDimension(
                                          new DimensionTransition(
                                             dimChange,
                                             posxx.getCenter(),
                                             this.owl.getDeltaMovement(),
                                             this.owl.getYRot(),
                                             this.owl.getXRot(),
                                             DimensionTransition.DO_NOTHING
                                          )
                                       );
                                 }

                                 new Vec3(this.owl.position().toVector3f());
                                 HexereiPacketHandler.sendToNearbyClient(
                                    this.owl.level(),
                                    this.owl,
                                    new OwlTeleportParticlePacket(this.owl.level().dimension(), this.owl.position(), this.owl.getVariant())
                                 );
                                 HexereiPacketHandler.sendToNearbyClient(
                                    this.owl.level().getServer().getLevel(oldDim),
                                    this.owl,
                                    new OwlTeleportParticlePacket(this.owl.level().dimension(), oldPos, this.owl.getVariant())
                                 );
                                 this.owl.hootAnimation.start();
                                 controller.stage = OwlEntity.MessagingController.Stage.FLY_TO_DESTINATION;
                                 this.refresh = 5;
                                 this.stuck = 0;
                                 this.stuckStageTotal = 0;
                              }
                           }
                        }
                     }
                  }
                  break;
               case FLY_TO_DESTINATION:
                  if (controller.hasDestination()) {
                     BlockPos posx = controller.getDestination().pos();
                     if (this.refresh > 5) {
                        Path pathx = this.owl.getNavigation().createPath(posx, 0);
                        this.owl.getNavigation().moveTo(pathx, 2.0);
                        this.refresh = 0;
                     }

                     if (controller.forceLoadChunks() && OwlEntity.this.distanceToSqr(posx.getX(), posx.getY(), posx.getZ()) < 3.0) {
                        controller.stage = OwlEntity.MessagingController.Stage.FIND_FLY_BACK_LOCATION;
                        this.refresh = 5;
                        this.stuck = 0;
                        this.stuckStageTotal = 0;
                        Map<GlobalPos, OwlCourierDepotData> depots = OwlCourierDepotSavedData.get().getDepots();
                        if (controller.destinationPos != null) {
                           if (!this.owl.messagingController.messageStack.isEmpty() && depots.containsKey(controller.getDestination())) {
                              for (int ix = 0; ix < depots.get(controller.getDestination()).items.size(); ix++) {
                                 if (((ItemStack)depots.get(controller.getDestination()).items.get(ix)).isEmpty()) {
                                    depots.get(controller.getDestination()).items.set(ix, this.owl.messagingController.messageStack.copy());
                                    this.owl.messagingController.messageStack = ItemStack.EMPTY;
                                    OwlCourierDepotSavedData.get().setDirty();
                                    OwlCourierDepotSavedData.get().syncInvToClient(controller.getDestination());
                                    this.owl.sync();
                                    this.owl.peck();
                                    break;
                                 }
                              }
                           }
                        } else if (controller.destinationPlayer != null
                           && this.owl.level().getServer().getPlayerList().getPlayer(controller.destinationPlayer.getUUID()) != null
                           && controller.destinationPlayer.isAlive()
                           && !this.owl.messagingController.messageStack.isEmpty()) {
                           controller.destinationPlayer.getInventory().placeItemBackInInventory(this.owl.messagingController.messageStack.copy());
                           this.owl.messagingController.messageStack = ItemStack.EMPTY;
                           this.owl.sync();
                           this.owl.peck();
                        }
                     }
                  }
                  break;
               case FIND_FLY_BACK_LOCATION:
                  for (int i = 0; i < 10; i++) {
                     Vec3 vec3 = this.owl.getViewVector(0.0F);
                     Vec3 vec = getPos(this.owl, 14, 4, 6, vec3.x, vec3.z, 1.5707963705062866);
                     if (vec != null && vec.distanceTo(this.owl.position()) > 8.0) {
                        OwlEntity.this.switchNavigator(true, true);
                        Path pathx = this.owl.getNavigation().createPath(BlockPos.containing(vec), 0);
                        if (pathx != null && pathx.canReach()) {
                           this.wantedPos = vec;
                           controller.stage = OwlEntity.MessagingController.Stage.FLY_BACK_AND_TELEPORT;
                           this.refresh = 5;
                           break;
                        }
                     }
                  }
                  break;
               case FLY_BACK_AND_TELEPORT:
                  if (controller.startPos != null) {
                     Path pathx = this.owl.getNavigation().createPath(controller.startPos.pos(), 0);
                     if (pathx != null && pathx.getDistToTarget() < 2.0F && this.owl.distanceToSqr(Vec3.atBottomCenterOf(controller.startPos.pos())) < 4.0) {
                        controller.stage = OwlEntity.MessagingController.Stage.RETURN_TO_START;
                     } else if (this.wantedPos == null) {
                        controller.stage = OwlEntity.MessagingController.Stage.FIND_FLY_BACK_LOCATION;
                     } else {
                        Path path2 = this.owl.getNavigation().createPath(BlockPos.containing(this.wantedPos), 0);
                        if (this.refresh > 5) {
                           this.owl.getNavigation().moveTo(path2, 3.0);
                           this.refresh = 0;
                        }

                        if (OwlEntity.this.distanceToSqr(this.wantedPos.x, this.wantedPos.y, this.wantedPos.z) < 2.0) {
                           BlockPos posx = controller.startPos.pos();
                           Vec3 oldPos = new Vec3(this.owl.position().toVector3f());
                           ServerLevel dimChange = this.owl.getServer().getLevel(controller.startPos.dimension());
                           ResourceKey<Level> dim1 = controller.startPos.dimension();
                           ResourceKey<Level> dim2 = OwlEntity.this.level().dimension();
                           if (OwlEntity.this.teleportToDest(controller.startPos.dimension(), posx)) {
                              Vec3 newPos = new Vec3(this.owl.position().toVector3f());
                              if (dimChange != null && !dimChange.equals(this.owl.level())) {
                                 this.owl
                                    .changeDimension(
                                       new DimensionTransition(
                                          dimChange,
                                          posx.getCenter(),
                                          this.owl.getDeltaMovement(),
                                          this.owl.getYRot(),
                                          this.owl.getXRot(),
                                          DimensionTransition.DO_NOTHING
                                       )
                                    );
                              }

                              HexereiPacketHandler.sendToNearbyClient(
                                 this.owl.level().getServer().getLevel(dim1),
                                 this.owl,
                                 new OwlTeleportParticlePacket(this.owl.level().dimension(), newPos, this.owl.getVariant())
                              );
                              HexereiPacketHandler.sendToNearbyClient(
                                 this.owl.level().getServer().getLevel(dim2),
                                 this.owl,
                                 new OwlTeleportParticlePacket(this.owl.level().dimension(), oldPos, this.owl.getVariant())
                              );
                              controller.stage = OwlEntity.MessagingController.Stage.RETURN_TO_START;
                              this.wantedPos = controller.startPos.pos().getCenter();
                              this.refresh = 5;
                              this.stuck = 0;
                              this.stuckStageTotal = 0;
                              this.owl.hootAnimation.start();
                           }
                        }
                     }
                  }
                  break;
               case RETURN_TO_START:
                  if (controller.startPos == null) {
                     this.end();
                  } else {
                     BlockPos pos = controller.startPos.pos();
                     Path path = this.owl.getNavigation().createPath(pos, 0);
                     if (this.refresh > 5 && path != null) {
                        this.owl.getNavigation().moveTo(path, 2.0);
                        this.refresh = 0;
                     }

                     if (OwlEntity.this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 1.5) {
                        this.refresh = 5;
                        this.stuck = 0;
                        this.stuckStageTotal = 0;
                        controller.stage = OwlEntity.MessagingController.Stage.DONE;
                        if (!controller.messageStack.isEmpty()) {
                           ItemEntity item = this.owl.spawnAtLocation(controller.getMessageStack().copy(), 0.0F);
                           if (item != null) {
                              item.setUnlimitedLifetime();
                           }

                           if (this.owl.getOwner() instanceof ServerPlayer owner) {
                              owner.sendSystemMessage(
                                 Component.translatable(
                                    "message.hexerei.owl_could_not_deliver",
                                    new Object[]{this.owl.getName().getString(), controller.getMessageStack().getHoverName().getString()}
                                 )
                              );
                           }

                           controller.setMessageStack(ItemStack.EMPTY);
                           this.owl.sync();
                           this.owl.peck();
                        }

                        this.end();
                     }
                  }
            }
         } else if (this.stuckPath == null) {
            Vec3 vec3 = this.owl.getViewVector(0.0F);
            Vec3 vec = getPos(this.owl, 7, 2, 1, vec3.x, vec3.z, 1.5707963705062866);
            OwlEntity.this.switchNavigator(true, true);
            if (vec != null) {
               Path pathx = this.owl.getNavigation().createPath(BlockPos.containing(vec), 0);
               if (pathx != null && pathx.canReach()) {
                  this.stuckPath = pathx;
                  this.stuckPathDest = BlockPos.containing(vec);
                  this.owl.getNavigation().moveTo(this.stuckPath, 2.0);
               }
            }
         } else {
            this.owl.getNavigation().moveTo(this.stuckPath, 2.0);
            if (this.owl.distanceToSqr(this.stuckPathDest.getX(), this.stuckPathDest.getY(), this.stuckPathDest.getZ()) < 3.0) {
               this.stuckPath = null;
               this.stuckPathDest = null;
               this.stuck = 0;
            }
         }

         super.tick();
      }

      public void end() {
         OwlEntity.MessagingController controller = this.owl.messagingController;
         if (this.owl.level() instanceof ServerLevel serverLevel && controller.stage == OwlEntity.MessagingController.Stage.DONE) {
            OwlLoadedChunksSavedData.get(serverLevel).clearOwl(serverLevel, this.owl);
         }

         controller.startPos = null;
         controller.destinationPlayer = null;
         controller.destinationPos = null;
         controller.stage = OwlEntity.MessagingController.Stage.FIND_FLY_OFF_LOCATION;
         this.owl.currentTask = OwlEntity.OwlTask.NONE;
         controller.clearLastCheckedChunks();
         this.stop();
      }

      public void start() {
         this.owl.setInSittingPose(false);
      }

      public void stop() {
      }

      @Nullable
      public static Vec3 getPos(PathfinderMob pMob, int pMaxDistance, int pYRange, int pY, double pX, double pZ, double pAmplifier) {
         boolean flag = GoalUtils.mobRestricted(pMob, pMaxDistance);
         return RandomPos.generateRandomPos(pMob, () -> {
            BlockPos blockpos = generateRandomPos(pMob, pMaxDistance, pYRange, pY, pX, pZ, pAmplifier, flag);
            return blockpos != null && pMob.level().getFluidState(blockpos).isEmpty() ? blockpos : null;
         });
      }

      @Nullable
      public static BlockPos generateRandomPos(
         PathfinderMob pMob, int pMaxDistance, int pYRange, int pY, double pX, double pZ, double pAmplifier, boolean pShortCircuit
      ) {
         BlockPos blockpos = RandomPos.generateRandomDirectionWithinRadians(pMob.getRandom(), pMaxDistance, pYRange, pY, pX, pZ, pAmplifier);
         if (blockpos == null) {
            return null;
         } else {
            BlockPos blockpos1 = RandomPos.generateRandomPosTowardDirection(pMob, pMaxDistance, pMob.getRandom(), blockpos);
            if (!GoalUtils.isOutsideLimits(blockpos1, pMob) && !GoalUtils.isRestricted(pShortCircuit, pMob, blockpos1)) {
               blockpos1 = RandomPos.moveUpOutOfSolid(blockpos1, pMob.level().getMaxBuildHeight(), p_148376_ -> GoalUtils.isSolid(pMob, p_148376_));
               return GoalUtils.hasMalus(pMob, blockpos1) ? null : blockpos1;
            } else {
               return null;
            }
         }
      }
   }

   public static enum EmotionState {
      CONTEMPT(0.0F, 0.0F, 0.0F, new OwlEntity.Emotions(0, 0, 0)),
      HAPPY(0.0F, 0.0F, 0.0F, new OwlEntity.Emotions(0, 0, 100)),
      DISTRESSED(0.75F, 0.05F, 40.0F, new OwlEntity.Emotions(0, 100, 0)),
      ANGRY(0.25F, 0.5F, -15.0F, new OwlEntity.Emotions(100, 0, 0));

      private final OwlEntity.Emotions scales;
      final float browXOffset;
      final float browYOffset;
      final float browZRot;

      private EmotionState(float xOffset, float yOffset, float zRot, OwlEntity.Emotions scales) {
         this.browXOffset = xOffset;
         this.browYOffset = yOffset;
         this.browZRot = zRot;
         this.scales = scales;
      }

      public OwlEntity.Emotions getScales() {
         return this.scales;
      }

      public float getxOffset() {
         return this.browXOffset;
      }

      public float getyOffset() {
         return this.browYOffset;
      }

      public float getzRot() {
         return this.browZRot;
      }
   }

   public static class Emotions {
      private int anger;
      private int distress;
      private int happiness;

      public Emotions(int anger, int distress, int happiness) {
         this.anger = anger;
         this.distress = distress;
         this.happiness = happiness;
      }

      public void setAnger(int anger) {
         this.anger = Mth.clamp(anger, 0, 100);
      }

      public void setDistress(int distress) {
         this.distress = Mth.clamp(distress, 0, 100);
      }

      public void setHappiness(int happiness) {
         this.happiness = Mth.clamp(happiness, 0, 100);
      }

      public int getAnger() {
         return this.anger;
      }

      public int getDistress() {
         return this.distress;
      }

      public int getHappiness() {
         return this.happiness;
      }

      public boolean isHappy() {
         return this.getAnger() < 30 && this.getHappiness() > 50;
      }
   }

   public class FloatGoal extends Goal {
      private final Mob mob;

      public FloatGoal(Mob p_25230_) {
         this.mob = p_25230_;
         this.setFlags(EnumSet.of(Flag.JUMP));
         p_25230_.getNavigation().setCanFloat(true);
      }

      public boolean canUse() {
         return this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold() || this.mob.isInLava();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (this.mob.getRandom().nextFloat() < 0.8F) {
            this.mob.getJumpControl().jump();
            Vec3 randomPos = DefaultRandomPos.getPos(OwlEntity.this, 10, 7);
            if (randomPos == null) {
               randomPos = LandRandomPos.getPos(OwlEntity.this, 10, 7);
            }

            BlockPos pos;
            if (randomPos != null) {
               pos = new BlockPos((int)randomPos.x, (int)randomPos.y, (int)randomPos.z);
            } else if (OwlEntity.this.getPerchPos() != null) {
               pos = OwlEntity.this.getPerchPos().above().above();
            } else {
               pos = OwlEntity.this.blockPosition().above().above();
            }

            if (!OwlEntity.this.isInSittingPose()) {
               this.mob.push(0.0, 0.1, 0.0);
            }

            OwlEntity.this.flyOrWalkTo(pos.getCenter());
            OwlEntity.this.navigation.moveTo(OwlEntity.this.getNavigation().createPath(pos, 0), OwlEntity.this.isFlyingNav() ? 1.5 : 1.0);
         }
      }
   }

   public class FlyBackToPerchGoal extends Goal {
      private final TamableAnimal mob;

      public FlyBackToPerchGoal(TamableAnimal p_25898_) {
         this.mob = p_25898_;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public boolean canContinueToUse() {
         if (OwlEntity.this.getPerchPos() == null) {
            return false;
         } else {
            double topOffset = 0.0;
            if (OwlEntity.this.getPerchPos() != null) {
               topOffset = OwlEntity.this.level()
                  .getBlockState(OwlEntity.this.getPerchPos())
                  .getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos())
                  .max(Axis.Y);
            }

            if (this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1.0
               && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset
               && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset) {
               return false;
            } else {
               if (OwlEntity.this.isInSittingPose()) {
                  OwlEntity.this.setInSittingPose(false);
                  OwlEntity.this.setOrderedToSit(false);
               }

               return true;
            }
         }
      }

      public double distanceTo(double p_20276_, double p_20278_) {
         double d0 = OwlEntity.this.getX() - p_20276_ - 0.5;
         double d1 = OwlEntity.this.getZ() - p_20278_ - 0.5;
         return Mth.sqrt((float)(d0 * d0 + d1 * d1));
      }

      public boolean canUse() {
         if (!OwlEntity.this.currentTask.isNone()) {
            return false;
         } else {
            if (OwlEntity.this.isInSittingPose()) {
               if (OwlEntity.this.getPerchPos() == null) {
                  return false;
               }

               if (OwlEntity.this.getPerchPos().distToCenterSqr(OwlEntity.this.position().x, OwlEntity.this.position().y, OwlEntity.this.position().z) < 1.0) {
                  return false;
               }
            }

            if (!this.mob.isTame()) {
               return false;
            } else if (this.mob.isInWaterOrBubble()) {
               return false;
            } else {
               LivingEntity livingentity = this.mob.getOwner();
               if (livingentity == null) {
                  return true;
               } else if (OwlEntity.this.getPerchPos() == null) {
                  return false;
               } else {
                  double topOffset = OwlEntity.this.level()
                     .getBlockState(OwlEntity.this.getPerchPos())
                     .getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos())
                     .max(Axis.Y);
                  if (this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1.0
                     && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset
                     && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset) {
                     return OwlEntity.this.isInSittingPose()
                        ? false
                        : !(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1.0)
                           || !(this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset)
                           || !(this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset);
                  } else {
                     if (OwlEntity.this.isInSittingPose()) {
                        OwlEntity.this.setInSittingPose(false);
                        OwlEntity.this.setOrderedToSit(false);
                     }

                     return true;
                  }
               }
            }
         }
      }

      public void tick() {
         double topOffset = 0.0;
         if (OwlEntity.this.getPerchPos() != null) {
            topOffset = OwlEntity.this.level()
               .getBlockState(OwlEntity.this.getPerchPos())
               .getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos())
               .max(Axis.Y);
         }

         boolean isStuck = false;
         if (OwlEntity.this.getPerchPos() != null
            && (
               !(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1.0)
                  || !(this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset)
                  || !(this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset)
            )) {
            OwlEntity.this.flyOrWalkTo(OwlEntity.this.getPerchPos().above().getCenter());
            OwlEntity.this.navigation
               .moveTo(this.mob.getNavigation().createPath(OwlEntity.this.getPerchPos().above(), -1), OwlEntity.this.isFlyingNav() ? 1.5 : 1.0);
         }

         super.tick();
      }

      public void start() {
         if (OwlEntity.this.getPerchPos() != null) {
            OwlEntity.this.flyOrWalkTo(OwlEntity.this.getPerchPos().above().getCenter());
            OwlEntity.this.navigation
               .moveTo(this.mob.getNavigation().createPath(OwlEntity.this.getPerchPos().above(), 0), OwlEntity.this.isFlyingNav() ? 1.5 : 1.0);
         }
      }

      public void stop() {
         OwlEntity.this.currentTask = OwlEntity.OwlTask.NONE;
         if (OwlEntity.this.getPerchPos() != null) {
            double topOffset = OwlEntity.this.level()
               .getBlockState(OwlEntity.this.getPerchPos())
               .getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos())
               .max(Axis.Y);
            if (this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1.0
               && this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset
               && this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset) {
               OwlEntity.this.setInSittingPose(true);
            }
         }
      }
   }

   public class FollowOwnerGoal extends Goal {
      private final OwlEntity owl;
      private LivingEntity owner;
      private final LevelReader level;
      private final double speedModifier;
      private final PathNavigation navigation;
      private int timeToRecalcPath;
      private final float stopDistance;
      private final float startDistance;
      private float oldWaterCost;
      private final boolean canFly;

      public FollowOwnerGoal(OwlEntity owl, double speedModifier, float startDistance, float stopDistance, boolean canFly) {
         this.owl = owl;
         this.level = owl.level();
         this.speedModifier = speedModifier;
         this.navigation = owl.getNavigation();
         this.startDistance = startDistance;
         this.stopDistance = stopDistance;
         this.canFly = canFly;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
         if (!(owl.getNavigation() instanceof GroundPathNavigation) && !(owl.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
         }
      }

      public boolean canUse() {
         if (!this.owl.currentTask.isNone()) {
            return false;
         } else {
            LivingEntity livingentity = this.owl.getOwner();
            if (livingentity == null) {
               return false;
            } else if (livingentity.isSpectator()) {
               return false;
            } else if (!this.owl.isOrderedToSit() && !OwlEntity.this.isInSittingPose()) {
               if (this.owl.distanceToSqr(livingentity) < this.startDistance * this.startDistance) {
                  return false;
               } else {
                  this.owner = livingentity;
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public boolean canContinueToUse() {
         if (!this.owl.currentTask.isNone()) {
            return false;
         } else if (this.navigation.isDone()) {
            return false;
         } else {
            return !this.owl.isOrderedToSit() && !OwlEntity.this.isInSittingPose()
               ? !(this.owl.distanceToSqr(this.owner) <= this.stopDistance * this.stopDistance)
               : false;
         }
      }

      public void start() {
         this.timeToRecalcPath = 0;
         this.oldWaterCost = this.owl.getPathfindingMalus(PathType.WATER);
         this.owl.setPathfindingMalus(PathType.WATER, 0.0F);
      }

      public void stop() {
         OwlEntity.this.currentTask = OwlEntity.OwlTask.NONE;
         this.owner = null;
         this.navigation.stop();
         this.owl.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
      }

      public void tick() {
         this.owl.getLookControl().setLookAt(this.owner, 10.0F, this.owl.getMaxHeadXRot());
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(4);
            if (!this.owl.isLeashed() && !this.owl.isPassenger()) {
               if (this.owl.distanceToSqr(this.owner) >= 144.0) {
                  if (this.teleportToOwner()) {
                     OwlEntity.this.getNavigation().stop();
                     OwlEntity.this.flyOrWalkTo(this.owner.position());
                     OwlEntity.this.getNavigation().moveTo(this.owner.position().x, this.owner.position().y, this.owner.position().z, this.speedModifier);
                  }
               } else {
                  OwlEntity.this.flyOrWalkTo(this.owner.position());
                  OwlEntity.this.getNavigation().moveTo(this.owner.position().x, this.owner.position().y, this.owner.position().z, this.speedModifier);
               }
            }
         }
      }

      private boolean teleportToOwner() {
         BlockPos blockpos = this.owner.blockPosition();

         for (int i = 0; i < 10; i++) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
               return true;
            }
         }

         return false;
      }

      private boolean maybeTeleportTo(int x, int y, int z) {
         if (Math.abs(x - this.owner.getX()) < 2.0 && Math.abs(z - this.owner.getZ()) < 2.0) {
            return false;
         } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
         } else {
            OwlEntity.this.flyOrWalkTo(new Vec3(x + 0.5, y, z + 0.5));
            this.owl.moveTo(x + 0.5, y, z + 0.5, this.owl.getYRot(), this.owl.getXRot());
            this.navigation.stop();
            return true;
         }
      }

      private boolean canTeleportTo(BlockPos pos) {
         if (pos.getY() <= -64) {
            return false;
         } else {
            BlockState blockstate = this.level.getBlockState(pos.below());
            if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
               return false;
            } else {
               BlockPos blockpos = pos.subtract(this.owl.blockPosition());
               return this.level.noCollision(this.owl, this.owl.getBoundingBox().move(blockpos));
            }
         }
      }

      private int randomIntInclusive(int p_25301_, int p_25302_) {
         return this.owl.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
      }
   }

   public class FollowParentGoal extends Goal {
      public static final int HORIZONTAL_SCAN_RANGE = 8;
      public static final int VERTICAL_SCAN_RANGE = 4;
      public static final int DONT_FOLLOW_IF_CLOSER_THAN = 3;
      private final Animal animal;
      @Nullable
      private Animal parent;
      private final double speedModifier;
      private int timeToRecalcPath;

      public FollowParentGoal(Animal p_25319_, double p_25320_) {
         this.animal = p_25319_;
         this.speedModifier = p_25320_;
      }

      public boolean canUse() {
         if (this.animal.getAge() >= 0) {
            return false;
         } else if (!OwlEntity.this.isOrderedToSit() && !OwlEntity.this.isInSittingPose()) {
            List<? extends Animal> list = this.animal.level().getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0, 4.0, 8.0));
            Animal animal = null;
            double d0 = 1.7976931348623157E308;

            for (Animal animal1 : list) {
               if (animal1.getAge() >= 0) {
                  double d1 = this.animal.distanceToSqr(animal1);
                  if (!(d1 > d0)) {
                     d0 = d1;
                     animal = animal1;
                  }
               }
            }

            if (animal == null) {
               return false;
            } else if (d0 < 9.0) {
               return false;
            } else {
               this.parent = animal;
               return true;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         if (this.animal.getAge() >= 0) {
            return false;
         } else if (!this.parent.isAlive()) {
            return false;
         } else if (!OwlEntity.this.isOrderedToSit() && !OwlEntity.this.isInSittingPose()) {
            double d0 = this.animal.distanceToSqr(this.parent);
            return !(d0 < 9.0) && !(d0 > 256.0);
         } else {
            return false;
         }
      }

      public void start() {
         this.timeToRecalcPath = 0;
      }

      public void stop() {
         this.parent = null;
      }

      public void tick() {
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.parent != null) {
               OwlEntity.this.walkToIfNotFlyTo(this.parent.position());
            }

            this.animal.getNavigation().moveTo(this.parent, OwlEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
         }
      }
   }

   public class HeadShakeAnimation extends OwlEntity.AnimationBase {
      public float zTiltTarget = 0.0F;
      private float zTilt = 0.0F;
      private float zTiltLast = this.zTilt;
      private OwlEntity owl;

      public float getzTilt() {
         return this.zTilt;
      }

      public float getzTiltLast() {
         return this.zTiltLast;
      }

      public HeadShakeAnimation(OwlEntity owl) {
         this.owl = owl;
         this.useCooldown = false;
      }

      @Override
      public void start() {
         super.start();
         if (!this.owl.level().isClientSide) {
            this.activeTimer = 15;
            HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new HeadShakePacket(this.owl, this.activeTimer));
         }
      }

      @Override
      public void stop() {
         super.stop();
         this.zTiltTarget = 0.0F;
      }

      @Override
      public void preTick() {
         this.zTiltLast = this.zTilt;
      }

      @Override
      public void activeTick() {
         this.zTiltTarget = Mth.sin(this.owl.tickCount + this.owl.getId() * 642) * 100.0F;
      }

      @Override
      public void postTick() {
         this.zTilt = OwlEntity.this.moveTo(this.zTilt, this.zTiltTarget, 15.0F);
      }
   }

   public class HeadTiltAnimation extends OwlEntity.AnimationBase {
      public float zTiltTarget = 0.0F;
      private float zTilt;
      public float xTiltTarget = 0.0F;
      private float xTilt;
      private OwlEntity owl;

      public float getzTilt() {
         return this.zTilt;
      }

      public float getxTilt() {
         return this.xTilt;
      }

      public HeadTiltAnimation(OwlEntity owl) {
         this.zTilt = 0.0F;
         this.xTilt = 0.0F;
         this.owl = owl;
         this.cooldownTimer = OwlEntity.this.random.nextInt(100);
      }

      @Override
      public void activeTick() {
      }

      @Override
      public void postTick() {
         this.zTilt = OwlEntity.this.moveTo(this.zTilt, this.zTiltTarget, 15.0F);
         this.xTilt = OwlEntity.this.moveTo(this.xTilt, this.xTiltTarget, 15.0F);
      }

      @Override
      public void start() {
         super.start();
         if (!this.owl.level().isClientSide) {
            this.activeTimer = OwlEntity.this.random.nextInt(20) + 10;
            this.xTiltTarget = OwlEntity.this.random.nextInt(100) - 50;
            this.zTiltTarget = OwlEntity.this.random.nextInt(100) - 50;
            HexereiPacketHandler.sendToNearbyClient(
               this.owl.level(), this.owl, new HeadTiltPacket(this.owl, this.activeTimer, this.xTiltTarget, this.zTiltTarget)
            );
         }
      }

      @Override
      public void stop() {
         super.stop();
         this.zTiltTarget = 0.0F;
         this.xTiltTarget = 0.0F;
         this.cooldownTimer = OwlEntity.this.random.nextInt(80) + 20;
      }
   }

   public class HootAnimation extends OwlEntity.AnimationBase {
      private float hootRotTarget = 0.0F;
      private float hootRot = 0.0F;
      private OwlEntity owl;

      public float getHootRot() {
         return this.hootRot;
      }

      public HootAnimation(OwlEntity owl) {
         this.owl = owl;
      }

      @Override
      public void activeTick() {
         if (this.owl.level().isClientSide) {
            this.hootRotTarget = 80.0F;
         }
      }

      @Override
      public void postTick() {
         this.hootRot = OwlEntity.this.moveTo(this.hootRot, this.hootRotTarget, 30.0F);
      }

      @Override
      public void start() {
         super.start();
         if (!this.owl.level().isClientSide) {
            this.activeTimer = 15;
            HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new OwlHootPacket(this.owl, this.activeTimer));
            this.owl.playSound((SoundEvent)ModSounds.OWL_HOOT.get(), this.owl.getSoundVolume(), this.owl.getVoicePitch());
         }
      }

      @Override
      public void stop() {
         super.stop();
         this.cooldownTimer = OwlEntity.this.random.nextInt(560) + 160;
         this.hootRotTarget = 0.0F;
      }
   }

   public class LandOnOwnersShoulderGoal extends Goal {
      private final OwlEntity entity;
      private ServerPlayer owner;
      private boolean isSittingOnShoulder;

      public LandOnOwnersShoulderGoal(OwlEntity p_25483_) {
         this.entity = p_25483_;
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse();
      }

      public boolean canUse() {
         if (OwlEntity.this.isBaby()) {
            return false;
         } else {
            ServerPlayer serverplayer = (ServerPlayer)this.entity.getOwner();
            boolean flag = serverplayer != null
               && !serverplayer.isSpectator()
               && !serverplayer.getAbilities().flying
               && !serverplayer.isInWater()
               && !serverplayer.isInPowderSnow;
            return !this.entity.isOrderedToSit()
               && !this.entity.isInSittingPose()
               && flag
               && this.entity.canSitOnShoulder()
               && serverplayer.getPassengers().size() < 2;
         }
      }

      public boolean isInterruptable() {
         return !this.isSittingOnShoulder;
      }

      public void start() {
         this.owner = (ServerPlayer)this.entity.getOwner();
         this.isSittingOnShoulder = false;
      }

      public void tick() {
         if (!this.isSittingOnShoulder
            && !this.entity.isInSittingPose()
            && !this.entity.isLeashed()
            && this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
            this.isSittingOnShoulder = this.entity.startRiding(this.owner, true);
            if (!OwlEntity.this.level().isClientSide) {
               HexereiPacketHandler.sendToNearbyClient(this.entity.level(), this.entity, new StartRidingPacket(this.entity, this.owner));
            }
         }

         if (this.isSittingOnShoulder) {
            this.entity.rideCooldownCounter = 0;
         }
      }
   }

   public static class MessageText {
      public static final int LINES = 12;
      private static final Codec<Component[]> LINES_CODEC = ComponentSerialization.FLAT_CODEC
         .listOf()
         .comapFlatMap(
            p_337999_ -> Util.fixedSize(p_337999_, 12).map(components -> components.toArray(new Component[0])),
            components -> Arrays.stream(components).toList()
         );
      public static final Codec<OwlEntity.MessageText> DIRECT_CODEC = RecordCodecBuilder.create(
         p_338000_ -> p_338000_.group(LINES_CODEC.fieldOf("messages").forGetter(text -> text.messages)).apply(p_338000_, OwlEntity.MessageText::load)
      );
      private final Component[] messages;

      public MessageText() {
         this(emptyMessages(12));
      }

      public MessageText(Component[] pMessages) {
         this.messages = pMessages;
      }

      private static Component[] emptyMessages(int size) {
         Component[] messages = new Component[size];
         Arrays.fill(messages, CommonComponents.EMPTY);
         return messages;
      }

      private static OwlEntity.MessageText load(Component[] component) {
         return new OwlEntity.MessageText(component);
      }

      public Component getMessage(int pIndex) {
         return this.getMessages()[pIndex];
      }

      public OwlEntity.MessageText setMessage(int pIndex, Component pText) {
         Component[] acomponent = Arrays.copyOf(this.messages, this.messages.length);
         acomponent[pIndex] = pText;
         return new OwlEntity.MessageText(acomponent);
      }

      public Component[] getMessages() {
         return this.messages;
      }
   }

   public static class MessagingController {
      private OwlEntity owl;
      private GlobalPos startPos = null;
      private GlobalPos destinationPos = null;
      private Player destinationPlayer = null;
      private Map<ResourceKey<Level>, Set<ChunkPos>> lastCheckedChunks = new HashMap<>();
      private OwlEntity.MessagingController.Stage stage = OwlEntity.MessagingController.Stage.DONE;
      private ItemStack messageStack = ItemStack.EMPTY;

      public MessagingController(OwlEntity owl) {
         this.owl = owl;
      }

      public boolean hasDelivery() {
         return !this.getMessageStack().isEmpty();
      }

      public boolean isDelivering() {
         return this.stage != OwlEntity.MessagingController.Stage.DONE;
      }

      public ItemStack getMessageStack() {
         return this.messageStack;
      }

      public void setMessageStack(ItemStack messageStack) {
         this.messageStack = messageStack;
      }

      public void tick() {
         if (this.owl.currentTask == OwlEntity.OwlTask.DELIVER_MESSAGE) {
            this.handleActiveState();
         } else {
            this.handleInactiveState();
         }
      }

      public Map<ResourceKey<Level>, Set<ChunkPos>> getLastCheckedChunks() {
         return this.lastCheckedChunks;
      }

      public void clearLastCheckedChunks() {
         this.lastCheckedChunks.clear();
      }

      private OwlEntity.MessageText loadLines(OwlEntity.MessageText pText) {
         for (int i = 0; i < 4; i++) {
            Component component = pText.getMessage(i);
            pText = pText.setMessage(i, component);
         }

         return pText;
      }

      public boolean forceLoadChunks() {
         if (this.owl.isTame() && !this.owl.isDeadOrDying() && this.owl.level() instanceof ServerLevel serverLevel && this.hasDestination()) {
            GlobalPos dest = this.getDestination();
            GlobalPos start = this.startPos;
            ChunkPos startChunk = new ChunkPos(start.pos());
            ChunkPos targetChunk = new ChunkPos(dest.pos());
            Set<ChunkPos> newChunks = new HashSet<>();

            for (int dx = -1; dx <= 1; dx++) {
               for (int dz = -1; dz <= 1; dz++) {
                  newChunks.add(new ChunkPos(startChunk.x + dx, startChunk.z + dz));
               }
            }

            if (!newChunks.equals(this.lastCheckedChunks.get(start.dimension()))) {
               ServerLevel level = serverLevel.getServer().getLevel(start.dimension());
               if (level != null) {
                  OwlLoadedChunksSavedData.get().addOwlLoading(level, this.owl, newChunks);
                  if (this.lastCheckedChunks.containsKey(start.dimension())) {
                     this.lastCheckedChunks.get(start.dimension()).clear();
                  }

                  this.lastCheckedChunks.put(start.dimension(), newChunks);
               }
            }

            newChunks = new HashSet<>();

            for (int dx = -1; dx <= 1; dx++) {
               for (int dz = -1; dz <= 1; dz++) {
                  newChunks.add(new ChunkPos(targetChunk.x + dx, targetChunk.z + dz));
               }
            }

            if (!newChunks.equals(this.lastCheckedChunks.get(dest.dimension()))) {
               ServerLevel level = serverLevel.getServer().getLevel(dest.dimension());
               if (level != null) {
                  OwlLoadedChunksSavedData.get().addOwlLoading(level, this.owl, newChunks);
                  if (this.lastCheckedChunks.containsKey(dest.dimension())) {
                     this.lastCheckedChunks.get(dest.dimension()).clear();
                  }

                  this.lastCheckedChunks.put(dest.dimension(), newChunks);
               }
            }

            return true;
         } else {
            return false;
         }
      }

      public void stopForceloadingChunks() {
         if (this.owl.level() instanceof ServerLevel serverLevel) {
            OwlLoadedChunksSavedData.get(serverLevel).clearOwl(serverLevel, this.owl);
            this.owl.messagingController.clearLastCheckedChunks();
         }
      }

      private void handleActiveState() {
         if (!this.owl.level().isClientSide
            && !this.hasDestination()
            && this.owl.currentTask == OwlEntity.OwlTask.DELIVER_MESSAGE
            && (
               this.stage == OwlEntity.MessagingController.Stage.FIND_FLY_OFF_LOCATION
                  || this.stage == OwlEntity.MessagingController.Stage.FLY_OFF_AND_TELEPORT
                  || this.stage == OwlEntity.MessagingController.Stage.FLY_TO_DESTINATION
            )) {
            this.stage = OwlEntity.MessagingController.Stage.FIND_FLY_BACK_LOCATION;
         }
      }

      private void handleInactiveState() {
         if (!this.owl.level().isClientSide) {
            if (this.hasDelivery() && !this.hasDestination()) {
               this.owl.spawnAtLocation(this.getMessageStack().copy());
               this.setMessageStack(ItemStack.EMPTY);
            } else if (this.hasDelivery()) {
               this.owl.currentTask = OwlEntity.OwlTask.DELIVER_MESSAGE;
            }
         }
      }

      public void write(CompoundTag nbt) {
         if (this.startPos != null) {
            Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, this.startPos).result();
            tag.ifPresent(value -> nbt.put("startPos", value));
         }

         if (this.destinationPos != null) {
            Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, this.destinationPos).result();
            tag.ifPresent(value -> nbt.put("destinationPos", value));
         }

         if (this.destinationPlayer != null) {
            nbt.putUUID("destinationEntity", this.destinationPlayer.getUUID());
         }

         if (this.stage != null) {
            nbt.putInt("stage", this.stage.ordinal());
         }

         if (!this.messageStack.isEmpty()) {
            nbt.put("messageStack", this.messageStack.save(this.owl.registryAccess(), new CompoundTag()));
         }
      }

      public void read(CompoundTag nbt) {
         if (nbt.contains("startPos")) {
            Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("startPos")).result();
            pos.ifPresent(globalPos -> this.startPos = globalPos);
         }

         if (nbt.contains("destinationPos")) {
            Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("destinationPos")).result();
            pos.ifPresent(globalPos -> this.destinationPos = globalPos);
         }

         if (nbt.contains("destinationEntity")) {
            this.destinationPlayer = this.owl.level().getPlayerByUUID(nbt.getUUID("destinationEntity"));
         }

         if (nbt.contains("stage")) {
            this.stage = OwlEntity.MessagingController.Stage.byId(nbt.getInt("stage"));
         }

         if (nbt.contains("messageStack")) {
            this.messageStack = ItemStack.parseOptional(this.owl.registryAccess(), nbt.getCompound("messageStack"));
         } else {
            this.messageStack = ItemStack.EMPTY.copy();
         }
      }

      public void setDestination(Player entity) {
         this.destinationPlayer = entity;
      }

      public void setDestination(GlobalPos pos) {
         this.destinationPos = pos;
      }

      public void setStartPos(GlobalPos startPos) {
         this.startPos = startPos;
      }

      public boolean hasDestination() {
         return this.destinationPos != null || this.destinationPlayer != null;
      }

      public GlobalPos getDestination() {
         return this.destinationPlayer != null
            ? GlobalPos.of(this.destinationPlayer.level().dimension(), this.destinationPlayer.blockPosition())
            : this.destinationPos;
      }

      public void start(GlobalPos startPos) {
         this.setStartPos(startPos);
         this.stage = OwlEntity.MessagingController.Stage.byId(0);
      }

      public static enum Stage {
         FIND_FLY_OFF_LOCATION,
         FLY_OFF_AND_TELEPORT,
         FLY_TO_DESTINATION,
         FIND_FLY_BACK_LOCATION,
         FLY_BACK_AND_TELEPORT,
         RETURN_TO_START,
         DONE;

         public static OwlEntity.MessagingController.Stage byId(int id) {
            return values()[id >= 0 && id < values().length ? id : 0];
         }
      }
   }

   public class OwlFavoriteBlockGoal extends MoveToBlockGoal {
      private final OwlEntity owl;
      private int ticks;
      private int sinceLastOnBlock;
      private int cooldownTicks = 0;
      private boolean useCooldown = false;

      public OwlFavoriteBlockGoal(OwlEntity pOwl, double pSpeedModifier) {
         super(pOwl, pSpeedModifier, 12, 3);
         this.owl = pOwl;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public double acceptedDistance() {
         return 0.25;
      }

      public boolean canUse() {
         if (this.useCooldown && this.cooldownTicks < 500) {
            this.cooldownTicks++;
            if (this.cooldownTicks >= 500) {
               this.cooldownTicks = 0;
               this.useCooldown = false;
            }

            if (this.owl.currentTask == OwlEntity.OwlTask.GO_TO_FAVORITE_BLOCK) {
               this.owl.currentTask = OwlEntity.OwlTask.NONE;
            }

            return false;
         } else if (this.sinceLastOnBlock < 20) {
            this.sinceLastOnBlock++;
            return false;
         } else if (this.owl.isTame()
            && this.owl.currentTask.isNoneOr(OwlEntity.OwlTask.GO_TO_FAVORITE_BLOCK)
            && FavoriteBlockQuirk.fromController(this.owl.quirkController).size() > 0) {
            if (this.owl.currentTask == OwlEntity.OwlTask.GO_TO_FAVORITE_BLOCK) {
               this.ticks++;
               if (this.ticks > 500 || this.owl.isOrderedToSit() || this.owl.isInSittingPose()) {
                  this.owl.currentTask = OwlEntity.OwlTask.NONE;
                  this.owl.getNavigation().stop();
                  return false;
               }

               for (FavoriteBlockQuirk quirk : FavoriteBlockQuirk.fromController(this.owl.quirkController)) {
                  if (this.owl.getBlockStateOn().is(quirk.getFavoriteBlock())) {
                     this.ticks++;
                     if (this.ticks > 100) {
                        this.owl.currentTask = OwlEntity.OwlTask.NONE;
                        this.owl.getNavigation().stop();
                        this.useCooldown = true;
                     } else if (this.owl.getOwner() instanceof Player owner && this.owl.distanceToSqr(owner) >= 144.0) {
                        if (!this.owl.isOrderedToSit()) {
                           this.owl.teleportToOwner();
                        }

                        this.owl.currentTask = OwlEntity.OwlTask.NONE;
                        this.owl.getNavigation().stop();
                        this.useCooldown = true;
                     }

                     return false;
                  }
               }
            }

            return !this.owl.isOrderedToSit() && !this.owl.isInSittingPose() ? super.canUse() : false;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         boolean canContinue = super.canContinueToUse();
         return !this.useCooldown && canContinue && !this.owl.isOrderedToSit();
      }

      public void start() {
         super.start();
      }

      public void stop() {
         super.stop();
      }

      public void tick() {
         BlockPos blockpos = this.getMoveToTarget();
         float dist = (float)blockpos.distToCenterSqr(this.mob.position().add(0.0, 0.5, 0.0));
         if (!(dist < this.acceptedDistance())) {
            this.tryTicks++;
            if (this.shouldRecalculatePath()) {
               this.owl.walkToIfNotFlyTo(new Vec3(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5));
               this.mob.getNavigation().moveTo(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5, this.speedModifier);
            } else if (dist < 3.0F) {
               this.mob.getNavigation().stop();
               this.mob.getMoveControl().setWantedPosition(blockpos.getX() + 0.5F, blockpos.getY(), blockpos.getZ() + 0.5F, this.speedModifier * 1.25);
            }
         } else {
            if (this.ticks < 100) {
               this.ticks++;
            } else {
               this.owl.currentTask = OwlEntity.OwlTask.NONE;
               this.ticks = 0;
               this.useCooldown = true;
               this.stop();
            }

            this.sinceLastOnBlock = 0;
            this.tryTicks--;
         }

         if (this.owl.getOwner() instanceof Player owner && this.owl.distanceToSqr(owner) >= 144.0) {
            this.owl.teleportToOwner();
            this.owl.currentTask = OwlEntity.OwlTask.NONE;
            this.owl.getNavigation().stop();
            this.useCooldown = true;
            this.stop();
         }
      }

      protected int nextStartTick(PathfinderMob pCreature) {
         return reducedTickDelay(200 + pCreature.getRandom().nextInt(200));
      }

      public boolean posEqual(BlockPos pos1, BlockPos pos2) {
         return pos1 != null && pos2 != null ? pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ() : false;
      }

      protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
         if (pLevel.isEmptyBlock(pPos.above())) {
            BlockState blockstate = pLevel.getBlockState(pPos);

            for (FavoriteBlockQuirk quirk : FavoriteBlockQuirk.fromController(this.owl.quirkController)) {
               if (blockstate.is(quirk.getFavoriteBlock())) {
                  boolean collision = !quirk.getFavoriteBlock()
                     .defaultBlockState()
                     .getCollisionShape(pLevel, BlockPos.ZERO, CollisionContext.empty())
                     .isEmpty();
                  boolean collisionBelow = !pLevel.getBlockState(pPos.below())
                     .getBlock()
                     .defaultBlockState()
                     .getCollisionShape(pLevel, BlockPos.ZERO, CollisionContext.empty())
                     .isEmpty();
                  if (!collision && !collisionBelow) {
                     return false;
                  }

                  if (this.ticks > 100) {
                     if (this.posEqual(this.owl.getOnPos(0.5001F), pPos)) {
                        if (this.owl.currentTask == OwlEntity.OwlTask.GO_TO_FAVORITE_BLOCK) {
                           this.owl.currentTask = OwlEntity.OwlTask.NONE;
                        }

                        return false;
                     }

                     if (this.posEqual(this.owl.getOnPos(), pPos)) {
                        if (this.owl.currentTask == OwlEntity.OwlTask.GO_TO_FAVORITE_BLOCK) {
                           this.owl.currentTask = OwlEntity.OwlTask.NONE;
                        }

                        return false;
                     }
                  }

                  this.owl.currentTask = OwlEntity.OwlTask.GO_TO_FAVORITE_BLOCK;
                  return true;
               }
            }
         }

         return false;
      }
   }

   public class OwlGatherItems<T extends ItemEntity> extends TargetGoal {
      protected final OwlEntity.OwlGatherItems.Sorter theNearestAttackableTargetSorter;
      protected final Predicate<? super ItemEntity> targetEntitySelector;
      protected int executionChance;
      protected boolean mustUpdate;
      protected ItemEntity targetEntity;
      protected ITargetsDroppedItems hunter;
      private int tickThreshold;
      private int walkCooldown = 0;
      protected int tryTicks = 0;

      public OwlGatherItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
         this(creature, 1, checkSight, onlyNearby, null, tickThreshold);
      }

      public OwlGatherItems(
         PathfinderMob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector, int ticksExisted
      ) {
         super(creature, checkSight, onlyNearby);
         this.executionChance = chance;
         this.tickThreshold = ticksExisted;
         this.hunter = (ITargetsDroppedItems)creature;
         this.theNearestAttackableTargetSorter = new OwlEntity.OwlGatherItems.Sorter(creature);
         this.targetEntitySelector = item -> {
            ItemStack stack = item.getItem();
            return !stack.isEmpty() && this.hunter.canTargetItem(stack) && item.tickCount > this.tickThreshold;
         };
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (!OwlEntity.this.currentTask.isNoneOr(OwlEntity.OwlTask.PICKUP_ITEM)) {
            return false;
         } else if (!OwlEntity.this.isPassenger() && (!OwlEntity.this.isVehicle() || OwlEntity.this.getControllingPassenger() == null)) {
            if (!OwlEntity.this.itemHandler.getStackInSlot(1).isEmpty()) {
               return false;
            } else {
               if (!this.mustUpdate) {
                  long worldTime = OwlEntity.this.level().getGameTime() % 10L;
                  if (this.mob.getNoActionTime() >= 100 && worldTime != 0L) {
                     return false;
                  }

                  if (this.mob.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                     return false;
                  }
               }

               List<ItemEntity> list = this.mob
                  .level()
                  .getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(OwlEntity.this.interactionRange + 1), this.targetEntitySelector);
               if (list.isEmpty()) {
                  return false;
               } else {
                  if (OwlEntity.this.isInSittingPose()) {
                     OwlEntity.this.setInSittingPose(false);
                     OwlEntity.this.setOrderedToSit(false);
                  }

                  list.sort(this.theNearestAttackableTargetSorter);
                  this.targetEntity = list.get(0);
                  this.mustUpdate = false;
                  if (this.targetEntity == null) {
                     return false;
                  } else {
                     this.hunter.onFindTarget(this.targetEntity);
                     return !((OwlEntity)this.mob).isInSittingPose() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
                  }
               }
            }
         } else {
            return false;
         }
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 10 == 0;
      }

      protected double getFollowDistance() {
         return 16.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(this.mob.getX() + 0.5, this.mob.getY() + 0.5, this.mob.getZ() + 0.5);
         AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
         return aabb.move(renderCenter);
      }

      public void start() {
         this.moveTo();
         super.start();
      }

      protected void moveTo() {
         if (this.walkCooldown > 0) {
            this.walkCooldown--;
         } else {
            OwlEntity.this.walkToIfNotFlyTo(this.targetEntity.position().add(0.5, 0.25, 0.5));
            this.mob
               .getNavigation()
               .moveTo(
                  OwlEntity.this.getNavigation().createPath(this.targetEntity.getX() + 0.5, this.targetEntity.getY() + 0.25, this.targetEntity.getZ() + 0.5, 0),
                  OwlEntity.this.isFlyingNav() ? 1.5 : 1.25
               );
            this.walkCooldown = 30 + this.mob.getRandom().nextInt(40);
         }
      }

      public void stop() {
         super.stop();
         this.mob.getNavigation().stop();
         this.targetEntity = null;
      }

      public void tick() {
         super.tick();
         if (this.targetEntity != null && (this.targetEntity == null || this.targetEntity.isAlive())) {
            this.moveTo();
         } else {
            this.stop();
            this.mob.getNavigation().stop();
         }

         if (this.targetEntity != null
            && this.mob.hasLineOfSight(this.targetEntity)
            && this.mob.getBbWidth() > this.hunter.getMaxDistToItem()
            && this.mob.onGround()) {
            this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY() + 0.5, this.targetEntity.getZ(), 1.5);
         }

         if (this.targetEntity != null
            && this.targetEntity.isAlive()
            && this.mob.distanceToSqr(this.targetEntity) < this.hunter.getMaxDistToItem()
            && OwlEntity.this.itemHandler.getStackInSlot(1).isEmpty()) {
            if (!((OwlEntity)this.hunter).peckAnimation.active) {
               this.hunter.peck();
               HexereiPacketHandler.sendToNearbyClient(this.mob.level(), this.mob, new PeckPacket(this.mob));
            }

            if (((OwlEntity)this.hunter).peckAnimation.peckRot > 40.0F) {
               this.hunter.onGetItem(this.targetEntity);
               this.targetEntity.getItem().shrink(1);
               this.stop();
            }
         }

         OwlEntity crow = (OwlEntity)this.mob;
         if (this.targetEntity != null) {
            if (this.mob.distanceTo(this.targetEntity) <= OwlEntity.this.getMaxDistToItem()) {
               crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY() + 0.5, this.targetEntity.getZ(), 1.5);
            }

            if (!crow.isInSittingPose()) {
               OwlEntity.this.flyOrWalkTo(this.targetEntity.position().add(0.0, 0.5, 0.0));
               this.mob
                  .getNavigation()
                  .moveTo(
                     OwlEntity.this.getNavigation().createPath(this.targetEntity.getX(), this.targetEntity.getY() + 0.5, this.targetEntity.getZ(), 0),
                     OwlEntity.this.isFlyingNav() ? 1.5 : 1.0
                  );
            }

            this.tryTicks++;
            if (this.shouldRecalculatePath()) {
               if (this.targetEntity.position().distanceTo(OwlEntity.this.position()) < 3.0 && OwlEntity.this.position().y < this.targetEntity.position().y()) {
                  OwlEntity.this.setNoGravity(false);
                  OwlEntity.this.push(
                     (this.targetEntity.position().x - OwlEntity.this.position().x) / 50.0,
                     (this.targetEntity.position().y - OwlEntity.this.position().y) / 50.0 + 0.10000000149011612,
                     (this.targetEntity.position().z - OwlEntity.this.position().z) / 50.0
                  );
               }

               OwlEntity.this.flyOrWalkTo(this.targetEntity.position().add(0.0, 3.0, 0.0));
               this.mob
                  .getNavigation()
                  .moveTo(
                     OwlEntity.this.getNavigation().createPath(this.targetEntity.getX(), this.targetEntity.getY() + 3.0, this.targetEntity.getZ(), 0),
                     OwlEntity.this.isFlyingNav() ? 1.5 : 1.0
                  );
            }
         }
      }

      public void makeUpdate() {
         this.mustUpdate = true;
      }

      public boolean canContinueToUse() {
         if (OwlEntity.this.getHealth() >= OwlEntity.this.getMaxHealth() && OwlEntity.this.isTame()) {
            return false;
         } else {
            boolean path = this.mob.getBbWidth() > 2.0 || !this.mob.getNavigation().isDone();
            if (OwlEntity.this.isInSittingPose()) {
               OwlEntity.this.setInSittingPose(false);
               OwlEntity.this.setOrderedToSit(false);
            }

            return path
               && this.targetEntity != null
               && this.targetEntity.isAlive()
               && !((OwlEntity)this.mob).isInSittingPose()
               && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
         }
      }

      public static class Sorter implements Comparator<Entity> {
         private final Entity theEntity;

         public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
         }

         public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return Double.compare(d0, d1);
         }
      }
   }

   public class OwlLookAtPlayerGoal extends LookAtPlayerGoal {
      public OwlLookAtPlayerGoal(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
         super(pMob, pLookAtType, pLookDistance);
      }

      public boolean canUse() {
         return !OwlEntity.this.currentTask.is(OwlEntity.OwlTask.BREEDING) && OwlEntity.this.breedGiftGivenByPartnerTimer <= 0 ? super.canUse() : false;
      }

      public boolean canContinueToUse() {
         return !OwlEntity.this.currentTask.is(OwlEntity.OwlTask.BREEDING) && OwlEntity.this.breedGiftGivenByPartnerTimer <= 0
            ? super.canContinueToUse()
            : false;
      }
   }

   private static class OwlMoveController extends MoveControl {
      private final int maxTurn;

      public OwlMoveController(OwlEntity crow, int pMaxTurn) {
         super(crow);
         this.maxTurn = pMaxTurn;
      }

      public void tick() {
         if (this.mob.getNavigation() instanceof FlyingPathNavigation) {
            if (this.operation == Operation.MOVE_TO) {
               this.operation = Operation.WAIT;
               this.mob.setNoGravity(true);
               double d0 = this.wantedX - this.mob.getX();
               double d1 = this.wantedY - this.mob.getY();
               double d2 = this.wantedZ - this.mob.getZ();
               double d3 = d0 * d0 + d1 * d1 + d2 * d2;
               if (d3 < 2.500000277905201E-7) {
                  this.mob.setYya(0.0F);
                  this.mob.setZza(0.0F);
                  return;
               }

               float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
               this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
               float f1;
               if (this.mob.onGround()) {
                  f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
               } else {
                  f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
               }

               this.mob.setSpeed(f1);
               double d4 = Math.sqrt(d0 * d0 + d2 * d2);
               if (Math.abs(d1) > 9.999999747378752E-6 || Math.abs(d4) > 9.999999747378752E-6) {
                  float f2 = (float)(-(Mth.atan2(d1, d4) * 57.2957763671875));
                  this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, this.maxTurn));
                  this.mob.setYya(d1 > 0.0 ? f1 : -f1);
               }
            } else {
               this.mob.setNoGravity(false);
               this.mob.setYya(0.0F);
               this.mob.setZza(0.0F);
            }
         } else if (this.operation == Operation.STRAFE) {
            float fx = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            float f1x = (float)this.speedModifier * fx;
            float f2 = this.strafeForwards;
            float f3 = this.strafeRight;
            float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
            if (f4 < 1.0F) {
               f4 = 1.0F;
            }

            f4 = f1x / f4;
            f2 *= f4;
            f3 *= f4;
            float f5 = Mth.sin(this.mob.getYRot() * 0.017453292F);
            float f6 = Mth.cos(this.mob.getYRot() * 0.017453292F);
            float f7 = f2 * f6 - f3 * f5;
            float f8 = f3 * f6 + f2 * f5;
            if (!this.isWalkable(f7, f8)) {
               this.strafeForwards = 1.0F;
               this.strafeRight = 0.0F;
            }

            this.mob.setSpeed(f1x);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(this.strafeRight);
            this.operation = Operation.WAIT;
         } else if (this.operation == Operation.MOVE_TO) {
            this.operation = Operation.WAIT;
            double d0x = this.wantedX - this.mob.getX();
            double d1x = this.wantedZ - this.mob.getZ();
            double d2x = this.wantedY - this.mob.getY();
            double d3x = d0x * d0x + d2x * d2x + d1x * d1x;
            if (d3x < 2.500000277905201E-7) {
               this.mob.setZza(0.0F);
               return;
            }

            float f9 = (float)(Mth.atan2(d1x, d0x) * 57.2957763671875) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            BlockPos blockpos = this.mob.blockPosition();
            BlockState blockstate = this.mob.level().getBlockState(blockpos);
            VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos);
            if (d2x > this.mob.maxUpStep() && d0x * d0x + d1x * d1x < Math.max(1.0F, this.mob.getBbWidth())
               || !voxelshape.isEmpty()
                  && this.mob.getY() < voxelshape.max(Axis.Y) + blockpos.getY()
                  && !blockstate.is(BlockTags.DOORS)
                  && !blockstate.is(BlockTags.FENCES)) {
               this.mob.getJumpControl().jump();
               this.operation = Operation.JUMPING;
            }
         } else if (this.operation == Operation.JUMPING) {
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (this.mob.onGround()) {
               this.operation = Operation.WAIT;
            }
         } else {
            this.mob.setZza(0.0F);
         }
      }

      private boolean isWalkable(float pRelativeX, float pRelativeZ) {
         PathNavigation pathnavigation = this.mob.getNavigation();
         if (pathnavigation != null) {
            NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
            if (nodeevaluator != null
               && nodeevaluator.getPathType(
                     new PathfindingContext(this.mob.level(), this.mob),
                     Mth.floor(this.mob.getX() + pRelativeX),
                     this.mob.getBlockY(),
                     Mth.floor(this.mob.getZ() + pRelativeZ)
                  )
                  != PathType.WALKABLE) {
               return false;
            }
         }

         return true;
      }
   }

   public static enum OwlTask {
      NONE,
      DELIVER_MESSAGE,
      GO_TO_FAVORITE_BLOCK,
      PICKUP_ITEM,
      BREEDING;

      public boolean isNoneOr(OwlEntity.OwlTask owlTask) {
         return this == owlTask || this == NONE;
      }

      public boolean is(OwlEntity.OwlTask owlTask) {
         return this == owlTask;
      }

      public boolean isNone() {
         return this == NONE;
      }

      public static OwlEntity.OwlTask byId(int id) {
         OwlEntity.OwlTask[] type = values();
         return type[id >= 0 && id < type.length ? id : 0];
      }
   }

   public class PeckAnimation extends OwlEntity.AnimationBase {
      private float peckRotTarget = 0.0F;
      private float peckRot = 0.0F;
      private OwlEntity owl;

      public float getPeckRot() {
         return this.peckRot;
      }

      public PeckAnimation(OwlEntity owl) {
         this.owl = owl;
         this.useCooldown = false;
      }

      @Override
      public void activeTick() {
         this.peckRotTarget = 80.0F;
      }

      @Override
      public void postTick() {
         this.peckRot = OwlEntity.this.moveTo(this.peckRot, this.peckRotTarget, 15.0F);
      }

      @Override
      public void start() {
         super.start();
         if (!this.owl.level().isClientSide) {
            this.activeTimer = 10;
            HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new PeckPacket(this.owl));
         }
      }

      @Override
      public void stop() {
         super.stop();
         this.peckRotTarget = 0.0F;
      }
   }

   public class SitWhenOrderedToGoal extends Goal {
      private final TamableAnimal mob;

      public SitWhenOrderedToGoal(TamableAnimal p_25898_) {
         this.mob = p_25898_;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public double distanceTo(double p_20276_, double p_20278_) {
         double d0 = OwlEntity.this.getX() - p_20276_ - 0.5;
         double d1 = OwlEntity.this.getZ() - p_20278_ - 0.5;
         return Mth.sqrt((float)(d0 * d0 + d1 * d1));
      }

      public boolean canContinueToUse() {
         if (!OwlEntity.this.currentTask.isNone()) {
            return false;
         } else {
            if (OwlEntity.this.getPerchPos() != null) {
               double topOffset = OwlEntity.this.level()
                  .getBlockState(OwlEntity.this.getPerchPos())
                  .getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos())
                  .max(Axis.Y);
               if (!(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1.0)
                  || !(this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset)
                  || !(this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset)) {
                  OwlEntity.this.setOrderedToSit(false);
                  return false;
               }
            }

            return this.mob.isOrderedToSit();
         }
      }

      public boolean canUse() {
         if (OwlEntity.this.currentTask == OwlEntity.OwlTask.GO_TO_FAVORITE_BLOCK) {
            List<FavoriteBlockQuirk> quirks = FavoriteBlockQuirk.fromController(OwlEntity.this.quirkController);
            boolean flag = false;

            for (FavoriteBlockQuirk quirk : quirks) {
               if (OwlEntity.this.getBlockStateOn().is(quirk.getFavoriteBlock())) {
                  flag = true;
               }
            }

            if (!flag) {
               return false;
            }
         } else if (!OwlEntity.this.currentTask.isNone()) {
            return false;
         }

         if (!this.mob.isTame()) {
            return false;
         } else if (this.mob.isInWaterOrBubble()) {
            return false;
         } else if (!this.mob.onGround()) {
            return false;
         } else {
            LivingEntity livingentity = this.mob.getOwner();
            if (livingentity == null) {
               return true;
            } else {
               if (OwlEntity.this.getPerchPos() != null) {
                  double topOffset = OwlEntity.this.level()
                     .getBlockState(OwlEntity.this.getPerchPos())
                     .getOcclusionShape(OwlEntity.this.level(), OwlEntity.this.getPerchPos())
                     .max(Axis.Y);
                  if (!(this.distanceTo(OwlEntity.this.getPerchPos().getX(), OwlEntity.this.getPerchPos().getZ()) < 1.0)
                     || !(this.mob.position().y() >= OwlEntity.this.getPerchPos().getY() + topOffset)
                     || !(this.mob.position().y() < OwlEntity.this.getPerchPos().above().getY() + topOffset)) {
                     return false;
                  }
               }

               return this.mob.distanceToSqr(livingentity) < 288.0 && livingentity.getLastHurtByMob() != null ? false : this.mob.isOrderedToSit();
            }
         }
      }

      public void tick() {
         super.tick();
      }

      public void start() {
         this.mob.getNavigation().stop();
         this.mob.setInSittingPose(true);
      }

      public void stop() {
         this.mob.setInSittingPose(false);
      }
   }

   public class TailFanAnimation extends OwlEntity.AnimationBase {
      private float fanRotTarget = 0.0F;
      private float fanRot = 0.0F;
      private OwlEntity owl;

      public float getFanRot() {
         return this.fanRot;
      }

      public TailFanAnimation(OwlEntity owl) {
         this.owl = owl;
      }

      @Override
      public void activeTick() {
         if (this.owl.level().isClientSide) {
            this.fanRotTarget = Mth.sin(this.owl.tickCount + this.owl.getId() * 342) * 100.0F;
         }
      }

      @Override
      public void postTick() {
         this.fanRot = OwlEntity.this.moveTo(this.fanRot, this.fanRotTarget, 30.0F);
      }

      @Override
      public void start() {
         super.start();
         if (!this.owl.level().isClientSide) {
            this.activeTimer = 5 + this.owl.getRandom().nextInt(10);
            HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new TailFanPacket(this.owl, this.activeTimer));
         }
      }

      @Override
      public void stop() {
         super.stop();
         this.cooldownTimer = OwlEntity.this.random.nextInt(160) + 20;
         this.fanRotTarget = 0.0F;
      }
   }

   public class TailWagAnimation extends OwlEntity.AnimationBase {
      private float wagRotTarget = 0.0F;
      private float wagRot = 0.0F;
      private OwlEntity owl;

      public float getWagRot() {
         return this.wagRot;
      }

      public TailWagAnimation(OwlEntity owl) {
         this.owl = owl;
      }

      @Override
      public void activeTick() {
         if (this.owl.level().isClientSide) {
            this.wagRotTarget = Mth.sin(this.owl.tickCount + this.owl.getId() * 342) * 100.0F;
         }
      }

      @Override
      public void postTick() {
         this.wagRot = OwlEntity.this.moveTo(this.wagRot, this.wagRotTarget, 30.0F);
      }

      @Override
      public void start() {
         super.start();
         if (!this.owl.level().isClientSide) {
            this.activeTimer = 5 + this.owl.getRandom().nextInt(10);
            HexereiPacketHandler.sendToNearbyClient(this.owl.level(), this.owl, new TailWagPacket(this.owl, this.activeTimer));
         }
      }

      @Override
      public void stop() {
         super.stop();
         this.cooldownTimer = OwlEntity.this.random.nextInt(160) + 20;
         this.wagRotTarget = 0.0F;
      }
   }

   public class TemptGoal extends Goal {
      private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();
      private final TargetingConditions targetingConditions;
      protected final OwlEntity owl;
      private final double speedModifier;
      private double px;
      private double py;
      private double pz;
      private double pRotX;
      private double pRotY;
      @Nullable
      protected Player player;
      private int calmDown;
      private boolean isRunning;
      private final Ingredient items;
      private final boolean canScare;

      public TemptGoal(OwlEntity p_25939_, double p_25940_, Ingredient p_25941_, boolean p_25942_) {
         this.owl = p_25939_;
         this.speedModifier = p_25940_;
         this.items = p_25941_;
         this.canScare = p_25942_;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
         this.targetingConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
      }

      public boolean canUse() {
         if (this.owl.isInSittingPose() || this.owl.isOrderedToSit()) {
            return false;
         } else if (this.owl.currentTask.is(OwlEntity.OwlTask.PICKUP_ITEM) || this.owl.currentTask.is(OwlEntity.OwlTask.BREEDING)) {
            return false;
         } else if (this.calmDown > 0) {
            this.calmDown--;
            return false;
         } else {
            this.player = this.owl.level().getNearestPlayer(this.targetingConditions, this.owl);
            return this.player != null;
         }
      }

      private boolean shouldFollow(LivingEntity player) {
         return this.player != null && OwlEntity.this.isTame() && OwlEntity.this.isOwnedBy(player) && !OwlEntity.this.isBaby() && OwlEntity.this.getAge() == 0
            ? player.getMainHandItem().is(HexereiTags.Items.OWL_BREEDING_FOOD) || player.getOffhandItem().is(HexereiTags.Items.OWL_BREEDING_FOOD)
            : player.getMainHandItem().is(HexereiTags.Items.OWL_TAMING_FOOD) || player.getOffhandItem().is(HexereiTags.Items.OWL_TAMING_FOOD);
      }

      public boolean canContinueToUse() {
         if (!this.owl.isInSittingPose() && !this.owl.isOrderedToSit()) {
            if (this.canScare()) {
               if (this.owl.distanceToSqr(this.player) < 36.0) {
                  if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002) {
                     return false;
                  }

                  if (Math.abs(this.player.getXRot() - this.pRotX) > 5.0 || Math.abs(this.player.getYRot() - this.pRotY) > 5.0) {
                     return false;
                  }
               } else {
                  this.px = this.player.getX();
                  this.py = this.player.getY();
                  this.pz = this.player.getZ();
               }

               this.pRotX = this.player.getXRot();
               this.pRotY = this.player.getYRot();
            }

            return this.canUse();
         } else {
            return false;
         }
      }

      protected boolean canScare() {
         return this.canScare;
      }

      public void start() {
         this.px = this.player.getX();
         this.py = this.player.getY();
         this.pz = this.player.getZ();
         this.isRunning = true;
      }

      public void stop() {
         OwlEntity.this.currentTask = OwlEntity.OwlTask.NONE;
         this.player = null;
         this.owl.getNavigation().stop();
         this.calmDown = reducedTickDelay(100);
         this.isRunning = false;
         OwlEntity.this.setBrowPos(OwlEntity.BrowPositioning.NORMAL);
      }

      public void tick() {
         this.owl.getLookControl().setLookAt(this.player, this.owl.getMaxHeadYRot() + 20, this.owl.getMaxHeadXRot());
         if (!OwlEntity.this.isInSittingPose()) {
            if (!OwlEntity.this.isTame()) {
               if (this.owl.distanceToSqr(this.player) < 14.25 && OwlEntity.this.random.nextInt(20) == 0) {
                  OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() + 5 + OwlEntity.this.random.nextInt(5));
                  OwlEntity.this.emotionChanged();
                  OwlEntity.this.determineEmotionState();
               }
            } else if (this.owl.distanceToSqr(this.player) < 10.25 && OwlEntity.this.random.nextInt(20) == 0) {
               OwlEntity.this.emotions.setDistress(OwlEntity.this.emotions.getDistress() + 5 + OwlEntity.this.random.nextInt(5));
               OwlEntity.this.emotionChanged();
               OwlEntity.this.determineEmotionState();
            }

            if (this.owl.distanceToSqr(this.player) < 6.25) {
               this.owl.getNavigation().stop();
            } else {
               if (OwlEntity.this.random.nextInt(reducedTickDelay(2)) == 0) {
                  OwlEntity.this.walkToIfNotFlyTo(this.player.position());
               }

               this.owl.getNavigation().moveTo(this.player, 1.25 * this.speedModifier);
            }
         }
      }

      public boolean isRunning() {
         return this.isRunning;
      }
   }

   public class WanderAroundPlayerGoal extends RandomStrollGoal {
      public WanderAroundPlayerGoal(PathfinderMob pMob, double pSpeedModifier) {
         super(pMob, pSpeedModifier, 85);
      }

      public void start() {
         OwlEntity.this.flyOrWalkTo(new Vec3(this.wantedX, this.wantedY, this.wantedZ));
         this.mob
            .getNavigation()
            .moveTo(this.wantedX, this.wantedY, this.wantedZ, OwlEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
         this.mob.setNoActionTime(0);
      }

      public boolean canUse() {
         return !OwlEntity.this.currentTask.isNone() ? false : super.canUse();
      }

      public boolean canContinueToUse() {
         return !OwlEntity.this.currentTask.isNone() ? false : super.canContinueToUse();
      }

      protected Vec3 getPosition() {
         int pRadius = 5;
         int pVerticalDistance = 7;
         if (OwlEntity.this.getOwner() != null) {
            boolean flag = GoalUtils.mobRestricted(this.mob, pRadius);
            double d0 = -1.0 / 0.0;
            BlockPos blockpos = null;

            for (int i = 0; i < 10; i++) {
               BlockPos pos = RandomPos.generateRandomDirection(OwlEntity.this.getRandom(), pRadius, pVerticalDistance);
               BlockPos blockpos1;
               if (OwlEntity.this.getPerchPos() != null) {
                  blockpos1 = generateRandomPosTowardDirection(Vec3.atLowerCornerOf(OwlEntity.this.getPerchPos()), this.mob, flag, pos);
               } else if (OwlEntity.this.getOwner() != null) {
                  blockpos1 = generateRandomPosTowardDirection(OwlEntity.this.getOwner().position(), this.mob, flag, pos);
               } else {
                  blockpos1 = OwlEntity.this.blockPosition();
               }

               if (blockpos1 != null) {
                  double d1 = OwlEntity.this.getWalkTargetValue(blockpos1);
                  if (d1 > d0) {
                     d0 = d1;
                     blockpos = blockpos1;
                  }
               }
            }

            return blockpos != null ? Vec3.atBottomCenterOf(blockpos) : null;
         } else {
            return DefaultRandomPos.getPos(this.mob, 10, 7);
         }
      }

      private static BlockPos generateRandomPosTowardDirection(Position pos, PathfinderMob pMob, boolean pShortCircuit, BlockPos pPos) {
         BlockPos blockpos = generateRandomPosTowardDirection(pos, pPos);
         boolean outsidelimits = !GoalUtils.isOutsideLimits(blockpos, pMob);
         boolean restricted = !GoalUtils.isRestricted(pShortCircuit, pMob, blockpos);
         boolean notstable = !GoalUtils.isNotStable(pMob.getNavigation(), blockpos);
         boolean malus = !GoalUtils.hasMalus(pMob, blockpos);
         return outsidelimits && restricted && malus ? blockpos : null;
      }

      public static BlockPos generateRandomPosTowardDirection(Position pos, BlockPos pPos) {
         int i = pPos.getX();
         int j = pPos.getZ();
         return BlockPos.containing(i + pos.x(), pPos.getY() + pos.y(), j + pos.z());
      }
   }

   public class WaterAvoidingRandomFlyingGoal extends OwlEntity.WaterAvoidingRandomStrollGoal {
      public WaterAvoidingRandomFlyingGoal(PathfinderMob p_25981_, double p_25982_) {
         super(p_25981_, p_25982_);
      }

      @Override
      public void start() {
         OwlEntity.this.switchNavigator(true);
         this.mob
            .getNavigation()
            .moveTo(this.wantedX, this.wantedY, this.wantedZ, OwlEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
      }

      @Override
      public boolean canUse() {
         if (OwlEntity.this.isTame()) {
            return false;
         } else if (!OwlEntity.this.currentTask.isNone()) {
            return false;
         } else {
            return OwlEntity.this.isInSittingPose() ? false : super.canUse();
         }
      }

      @Override
      public boolean canContinueToUse() {
         return OwlEntity.this.isInSittingPose() ? false : super.canContinueToUse();
      }

      @Nullable
      @Override
      protected Vec3 getPosition() {
         Vec3 vec3 = this.mob.getViewVector(0.0F);
         int i = 8;
         Vec3 vec31 = HoverRandomPos.getPos(this.mob, 8, 7, vec3.x, vec3.z, 1.5707964F, 3, 1);
         return vec31 != null ? vec31 : AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, vec3.x, vec3.z, 1.5707963705062866);
      }
   }

   public class WaterAvoidingRandomStrollGoal extends RandomStrollGoal {
      public static final float PROBABILITY = 0.001F;
      protected final float probability;

      public WaterAvoidingRandomStrollGoal(PathfinderMob p_25987_, double p_25988_) {
         this(p_25987_, p_25988_, 0.001F);
      }

      public WaterAvoidingRandomStrollGoal(PathfinderMob p_25990_, double p_25991_, float p_25992_) {
         super(p_25990_, p_25991_);
         this.probability = p_25992_;
      }

      public void start() {
         OwlEntity.this.walkToIfNotFlyTo(new Vec3(this.wantedX, this.wantedY, this.wantedZ));
         this.mob
            .getNavigation()
            .moveTo(this.wantedX, this.wantedY, this.wantedZ, OwlEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
      }

      public boolean canUse() {
         if (OwlEntity.this.isTame()) {
            return false;
         } else if (!OwlEntity.this.currentTask.isNone()) {
            return false;
         } else {
            return OwlEntity.this.isInSittingPose() ? false : super.canUse();
         }
      }

      public boolean canContinueToUse() {
         return OwlEntity.this.isInSittingPose() ? false : super.canContinueToUse();
      }

      @Nullable
      protected Vec3 getPosition() {
         if (this.mob.isInWaterOrBubble()) {
            Vec3 vec3 = LandRandomPos.getPos(this.mob, 15, 7);
            return vec3 == null ? super.getPosition() : vec3;
         } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
         }
      }
   }
}
