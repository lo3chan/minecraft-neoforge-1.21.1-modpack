package net.joefoxe.hexerei.client.renderer.entity.custom;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.block.custom.PickablePlant;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.ITargetsDroppedItems;
import net.joefoxe.hexerei.client.renderer.entity.render.CrowVariant;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.CrowContainer;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.AskForSyncPacket;
import net.joefoxe.hexerei.util.message.CrowCawPacket;
import net.joefoxe.hexerei.util.message.CrowSyncCommandToServer;
import net.joefoxe.hexerei.util.message.CrowSyncHelpCommandToServer;
import net.joefoxe.hexerei.util.message.EatParticlesPacket;
import net.joefoxe.hexerei.util.message.EntitySyncAdditionalDataPacket;
import net.joefoxe.hexerei.util.message.EntitySyncPacket;
import net.joefoxe.hexerei.util.message.PeckPacket;
import net.joefoxe.hexerei.util.message.StartRidingPacket;
import net.joefoxe.hexerei.util.message.TailFanPacket;
import net.joefoxe.hexerei.util.message.TailWagPacket;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.EntitySelector;
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
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Vector3f;

public class CrowEntity extends TamableAnimal implements ContainerListener, FlyingAnimal, ITargetsDroppedItems, Container, MenuProvider, PowerableMob {
   private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SEED_MIXTURE.get()});
   public boolean headTilt;
   public int headTiltTimer;
   public float headZTiltAngle;
   public float headZTiltAngleActual;
   public float headXTiltAngle;
   public float headXTiltAngleActual;
   public boolean caw;
   public int cawTimer;
   public float cawTiltAngle;
   public float cawTiltAngleActual;
   public boolean tailWag;
   public int tailWagTimer;
   public float tailWagTiltAngle;
   public float tailWagTiltAngleActual;
   public boolean tailFan;
   public int tailFanTimer;
   public float tailFanTiltAngle;
   public float tailFanTiltAngleActual;
   public boolean peck;
   public int peckTimer;
   public float peckTiltAngle;
   public float peckTiltAngleActual;
   public float rightWingAngle;
   public float rightWingAngleActual;
   public float leftWingAngle;
   public float leftWingAngleActual;
   public boolean dance;
   public int animationCounter;
   public int pickpocketTimer;
   private BlockPos jukebox;
   private int rideCooldownCounter;
   public int cofferHerbJarListResetTimer;
   public List<BlockPos> cofferHerbJarList;
   public List<Villager> villagerList;
   protected final Predicate<Villager> targetEntitySelector;
   private UUID seedThrowerID;
   private int heldItemTime = 0;
   public boolean aiItemFlag = false;
   public boolean aiCofferTileFlag = false;
   public boolean doingTask;
   public boolean searchForNewCropTarget;
   public int searchForNewCropTargetTimer = 0;
   public boolean depositItemBeforePerch;
   public boolean breedNuggetGivenByPlayer = false;
   public int breedNuggetGivenByCrowTimer = 0;
   public UUID breedNuggetGivenByPlayerUUID;
   public int waitToGiveTime = 0;
   public int stuckTimer = 0;
   public List<Block> harvestWhitelist = new ArrayList<>();
   public Vec3 lastStuckCheckPos = new Vec3(0.0, 0.0, 0.0);
   public boolean sync;
   public final ItemStackHandler itemHandler = this.createHandler();
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HELP_COMMAND = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(
      CrowEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private static final EntityDataAccessor<Integer> CROW_DYE_COLOR = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(CrowEntity.class, EntityDataSerializers.BOOLEAN);
   public int playingDead;
   public int interactionRange;
   public boolean canAttack;
   private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
   public static final int TOTAL_PLAYDEAD_TIME = 200;
   protected FlyingPathNavigation flyingNav;
   protected GroundPathNavigation groundNav;
   private boolean bringItemHome = false;
   private boolean bringItemHomeActive = false;
   private Goal bringItemHomeGoal;
   private int lastSwappedNavigator = 0;

   public CrowEntity(EntityType<CrowEntity> type, Level worldIn) {
      super(type, worldIn);
      this.registerGoals();
      this.flyingNav = (FlyingPathNavigation)this.createFlyingNavigation(worldIn);
      this.groundNav = (GroundPathNavigation)this.createGroundNavigation(worldIn);
      this.moveControl = new CrowEntity.CrowMoveController(this, 10) {
         @Override
         public void tick() {
            if (!CrowEntity.this.isPlayingDead()) {
               super.tick();
            }
         }
      };
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.LEAVES, 4.0F);
      this.headTilt = false;
      this.headTiltTimer = new Random().nextInt(100);
      this.headZTiltAngle = 0.0F;
      this.headZTiltAngleActual = 0.0F;
      this.headXTiltAngle = 0.0F;
      this.headXTiltAngleActual = 0.0F;
      this.cofferHerbJarListResetTimer = 80;
      this.animationCounter = 0;
      this.pickpocketTimer = 0;
      this.targetEntitySelector = input -> true;
      this.doingTask = false;
      this.sync = false;
      this.caw = false;
      this.cawTimer = new Random().nextInt(360) + 360;
      this.cawTiltAngle = 0.0F;
      this.cawTiltAngleActual = 0.0F;
      this.tailWag = false;
      this.tailWagTimer = new Random().nextInt(100);
      this.tailWagTiltAngle = 0.0F;
      this.tailWagTiltAngleActual = 0.0F;
      this.tailFan = false;
      this.tailFanTimer = new Random().nextInt(100);
      this.tailFanTiltAngle = 0.0F;
      this.tailFanTiltAngleActual = 0.0F;
      this.peck = false;
      this.peckTiltAngle = 0.0F;
      this.peckTiltAngleActual = 0.0F;
      this.rightWingAngle = 0.0F;
      this.rightWingAngleActual = 0.0F;
      this.leftWingAngle = 0.0F;
      this.leftWingAngleActual = 0.0F;
      this.playingDead = 0;
      this.interactionRange = 24;
      this.canAttack = true;
   }

   protected float getJumpPower() {
      return super.getJumpPower() * 1.1F;
   }

   public void switchNavigator(boolean shouldFly, boolean force) {
      if (Math.abs(this.tickCount - this.lastSwappedNavigator) > 40 || force) {
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

   protected void registerGoals() {
      this.bringItemHomeGoal = new CrowEntity.BringItemHome(this);
      this.goalSelector.addGoal(0, new CrowEntity.FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
      this.goalSelector.addGoal(2, new CrowEntity.FlyBackToPerchGoal(this));
      this.goalSelector.addGoal(2, new CrowEntity.SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new CrowEntity.FollowOwnerGoal(this, 1.0, 5.0F, 1.0F, true));
      this.goalSelector.addGoal(2, new CrowEntity.WanderAroundPlayerGoal(this, 1.0));
      this.goalSelector.addGoal(1, new CrowEntity.BreedGoal(this, 1.5));
      this.goalSelector.addGoal(1, new CrowEntity.TemptGoal(this, 1.0, TEMPTATION_ITEMS, false));
      this.goalSelector.addGoal(4, new CrowEntity.FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(4, new CrowEntity.WaterAvoidingRandomFlyingGoal(this, 1.0));
      this.goalSelector.addGoal(5, new CrowEntity.WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(4, new CrowEntity.LandOnOwnersShoulderGoal(this));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F) {
         public void tick() {
            if (!CrowEntity.this.isPlayingDead()) {
               super.tick();
            }
         }
      });
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this) {
         public void tick() {
            if (!CrowEntity.this.isPlayingDead()) {
               super.tick();
            }
         }
      });
      this.targetSelector.addGoal(2, new CrowEntity.CrowGatherItems(this, false, false, 40, this.interactionRange));
      this.goalSelector.addGoal(2, new CrowEntity.CrowDepositCoffer(this));
      this.goalSelector.addGoal(2, new CrowEntity.CrowHarvestGoal(1.5, this.interactionRange, 6));
      this.goalSelector.addGoal(2, new CrowEntity.CrowPickpocketVillager(this));
      this.targetSelector.addGoal(1, new CrowEntity.OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(1, new CrowEntity.OwnerHurtTargetGoal(this));
      this.goalSelector.addGoal(1, new CrowEntity.MeleeAttackGoal(this, 2.0, true));
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(COMMAND, 0);
      builder.define(HELP_COMMAND, 0);
      builder.define(PERCH_POS, Optional.empty());
      builder.define(CROW_DYE_COLOR, -1);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(DATA_PLAYING_DEAD, false);
      builder.define(DATA_FLYING, !this.onGround());
   }

   public void sync() {
      this.setChanged();
      if (!this.level().isClientSide) {
         HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EntitySyncPacket(this, this.saveWithoutId(new CompoundTag())));
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

   public int getInteractionRange() {
      return this.interactionRange;
   }

   protected int getInventorySize() {
      return 3;
   }

   public void die(DamageSource pCause) {
      if (!this.checkTotemDeathProtection(pCause)) {
         super.die(pCause);
      }
   }

   public void reviveCrow() {
      this.revive();
      this.dead = false;
      this.setHealth(this.getMaxHealth());
   }

   public boolean isDead() {
      return this.dead;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      float f = this.getHealth();
      if (!this.level().isClientSide
         && !this.isNoAi()
         && (this.random.nextInt(3) < pAmount || f / this.getMaxHealth() < 0.5F)
         && pAmount < f
         && (pSource.getEntity() != null || pSource.getDirectEntity() != null)
         && !this.isPlayingDead()) {
         this.playingDead = 200;
      }

      return pSource.is(DamageTypes.SWEET_BERRY_BUSH) ? false : super.hurt(pSource, pAmount);
   }

   public boolean isInWall() {
      return this.isPassenger() ? false : super.isInWall();
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(3) {
         protected void onContentsChanged(int slot) {
            CrowEntity.this.syncAdditionalData();
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
      this.peck = true;
      this.peckTiltAngle = 80.0F;
      this.peckTimer = 10;
      this.caw = false;
      if (!this.level().isClientSide) {
         this.cawTimer = new Random().nextInt(360) + 360;
      }

      this.cawTiltAngle = 0.0F;
      this.cawTiltAngleActual = 0.0F;
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

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public CrowVariant getVariant() {
      return CrowVariant.byId(this.getTypeVariant() & 0xFF);
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

         ItemStack itemstack1 = this.itemHandler.getStackInSlot(2);
         if (!triggered && itemstack1.is((Item)ModItems.CROW_ANKH_AMULET.get())) {
            itemstack = itemstack1.copy();
            itemstack1.shrink(1);
            triggered = true;
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

      if (!this.level().isClientSide) {
         if (this.itemHandler.getStackInSlot(2).is((Item)ModItems.CROW_BLANK_AMULET.get())) {
            this.itemHandler.getStackInSlot(2).inventoryTick(this.level(), this, 2, true);
         }

         if (this.itemHandler.getStackInSlot(2).getItem() instanceof MapItem mapItem) {
            MapItemSavedData mapitemsaveddata = MapItem.getSavedData(this.itemHandler.getStackInSlot(2), this.level());
            if (mapitemsaveddata != null && !mapitemsaveddata.locked) {
               mapItem.update(this.level(), this, mapitemsaveddata);
            }
         }
      }

      if (this.bringItemHome) {
         this.bringItemHome();
      }

      if (this.playingDead > 0) {
         if (!this.isPlayingDead()) {
            this.setPlayingDead(true);
         }

         this.playingDead--;
      } else if (this.isPlayingDead()) {
         this.setPlayingDead(false);
      }

      if (!this.isPlayingDead()) {
         if (this.breedNuggetGivenByCrowTimer > 0 && !this.level().isClientSide) {
            this.breedNuggetGivenByCrowTimer--;
            if (this.breedNuggetGivenByCrowTimer == 10) {
               this.peck();
               HexereiPacketHandler.sendToNearbyClient(this.level(), this, new PeckPacket(this));
            }

            if (this.breedNuggetGivenByCrowTimer == 0 && this.level().getPlayerByUUID(this.breedNuggetGivenByPlayerUUID) != null) {
               this.setInLove(this.level().getPlayerByUUID(this.breedNuggetGivenByPlayerUUID));
            }
         }

         if (this.pickpocketTimer > 0) {
            this.pickpocketTimer--;
         }

         if (this.searchForNewCropTargetTimer > 0) {
            this.searchForNewCropTargetTimer--;
            if (this.searchForNewCropTargetTimer <= 0) {
               this.searchForNewCropTarget = false;
            }
         }

         this.animationCounter++;
         this.rideCooldownCounter++;
         if (!this.level().isClientSide && this.isTame() && this.cofferHerbJarListResetTimer-- <= 0) {
            this.cofferHerbJarList = HexereiUtil.getAllToggledCofferAndHerbJarPositionsNearby(this.interactionRange, this.level(), this);
            this.cofferHerbJarListResetTimer = 200;
            this.villagerList = this.level().getEntitiesOfClass(Villager.class, this.getTargetableArea(this.interactionRange), this.targetEntitySelector);
         }

         if (!this.level().isClientSide) {
            if (this.onGround() && this.isFlying()) {
               this.entityData.set(DATA_FLYING, false);
            }

            if (this.isFlyingNav() && !this.isFlying()) {
               this.entityData.set(DATA_FLYING, true);
            }
         }

         if (this.getDeltaMovement().y < -0.0075) {
            this.rightWingAngle = Mth.sin(ClientEvents.getClientTicksWithoutPartial() / 5.0F) * 0.05F;
            this.leftWingAngle = -Mth.sin(0.97F + ClientEvents.getClientTicksWithoutPartial() / 5.0F) * 0.05F;
         } else {
            this.rightWingAngle = (float)Math.sin(ClientEvents.getClientTicksWithoutPartial() / 5.0F) * 0.8F;
            this.leftWingAngle = -((float)Math.sin(ClientEvents.getClientTicksWithoutPartial() / 5.0F)) * 0.8F;
         }

         this.rightWingAngleActual = this.moveTo(this.rightWingAngleActual, this.rightWingAngle, 0.1F);
         this.leftWingAngleActual = this.moveTo(this.leftWingAngleActual, this.leftWingAngle, 0.1F);
         if (this.headTiltTimer-- <= 0) {
            this.headTilt = !this.headTilt;
            if (this.headTilt) {
               this.headTiltTimer = this.random.nextInt(80) + 20;
               this.headZTiltAngle = this.random.nextInt(180) - 90;
               this.headXTiltAngle = this.random.nextInt(180) - 90;
            } else {
               this.headTiltTimer = this.random.nextInt(80) + 20;
               this.headZTiltAngle = 0.0F;
               this.headXTiltAngle = 0.0F;
            }
         }

         if (this.level().isClientSide) {
            this.headZTiltAngleActual = this.moveTo(this.headZTiltAngleActual, this.headZTiltAngle, 15.0F);
            this.headXTiltAngleActual = this.moveTo(this.headXTiltAngleActual, this.headXTiltAngle, 15.0F);
         }

         if (this.peckTimer > 0) {
            this.peckTimer--;
         }

         if (this.peckTimer <= 0 && this.peck) {
            this.peck = false;
            this.peckTiltAngle = 0.0F;
         }

         this.peckTiltAngleActual = this.moveTo(this.peckTiltAngleActual, this.peckTiltAngle, 15.0F);
         if (!this.level().isClientSide && !this.itemHandler.getStackInSlot(1).isEmpty()) {
            this.cawTimer = 360;
         }

         if (this.cawTimer > 0) {
            this.cawTimer--;
            if (this.cawTimer <= 0) {
               this.caw = !this.caw;
               if (this.caw) {
                  if (!this.level().isClientSide) {
                     HexereiPacketHandler.sendToNearbyClient(this.level(), this, new CrowCawPacket(this));
                     this.playSound((SoundEvent)ModSounds.CROW_CAW.get(), this.getSoundVolume(), this.getVoicePitch());
                     this.cawTimer = 15;
                  }
               } else if (!this.level().isClientSide) {
                  this.cawTimer = this.random.nextInt(360) + 360;
               } else {
                  this.cawTiltAngle = 0.0F;
               }
            }
         }

         if (this.level().isClientSide) {
            this.cawTiltAngleActual = this.moveTo(this.cawTiltAngleActual, this.cawTiltAngle, 30.0F);
         }

         if (this.tailWagTimer > 0) {
            this.tailWagTimer--;
            if (this.tailWagTimer <= 0) {
               this.tailWag = !this.tailWag;
               if (this.tailWag) {
                  if (!this.level().isClientSide) {
                     this.tailWagTimer = 15;
                     HexereiPacketHandler.sendToNearbyClient(this.level(), this, new TailWagPacket(this, this.tailWagTimer));
                  }
               } else if (!this.level().isClientSide) {
                  this.tailWagTimer = this.random.nextInt(80) + 80;
               } else {
                  this.tailWagTiltAngle = 0.0F;
               }
            }
         }

         if (this.level().isClientSide) {
            if (this.tailWag) {
               this.tailWagTiltAngle = Mth.sin(ClientEvents.getClientTicks()) * 100.0F;
            }

            this.tailWagTiltAngleActual = this.moveTo(this.tailWagTiltAngleActual, this.tailWagTiltAngle, 30.0F);
         }

         if (this.tailFanTimer > 0) {
            this.tailFanTimer--;
            if (this.tailFanTimer <= 0) {
               this.tailFan = !this.tailFan;
               if (this.tailFan) {
                  if (!this.level().isClientSide) {
                     this.tailFanTimer = 15;
                     if (!this.tailWag) {
                        HexereiPacketHandler.sendToNearbyClient(this.level(), this, new TailFanPacket(this, this.tailFanTimer));
                     } else {
                        this.tailFan = false;
                     }
                  }
               } else if (!this.level().isClientSide) {
                  this.tailFanTimer = this.random.nextInt(80) + 20;
               } else {
                  this.tailFanTiltAngle = 0.0F;
               }
            }
         }

         if (this.level().isClientSide) {
            this.tailFanTiltAngleActual = this.moveTo(this.tailFanTiltAngleActual, this.tailFanTiltAngle, 20.0F);
         }

         if (!this.itemHandler.getStackInSlot(1).isEmpty()) {
            this.heldItemTime++;
            if (this.heldItemTime > 60 && this.isCrowEdible(this.itemHandler.getStackInSlot(1)) && (!this.isTame() || this.getHealth() < this.getMaxHealth())) {
               this.heldItemTime = 0;
               this.heal(4.0F);
               if (!this.level().isClientSide) {
                  HexereiPacketHandler.sendToNearbyClient(this.level(), this, new EatParticlesPacket(this, this.itemHandler.getStackInSlot(1)));
               }

               this.playSound(SoundEvents.PARROT_EAT, this.getSoundVolume(), this.getVoicePitch());
               if (this.itemHandler.getStackInSlot(1).getItem() == ModItems.SEED_MIXTURE.get() && this.seedThrowerID != null && !this.isTame()) {
                  if (this.getRandom().nextFloat() < 0.3F) {
                     this.setTame(true, true);
                     this.setCommand(1);
                     this.setOwnerUUID(this.seedThrowerID);
                     Player player = this.level().getPlayerByUUID(this.seedThrowerID);
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
            }
         } else {
            this.heldItemTime = 0;
         }
      }
   }

   public void eatParticles(ItemStack stack) {
      float scale = 3.0F;
      if (this.isBaby()) {
         scale = 4.0F;
      }

      Vec3 vec3 = this.calculateViewVector(0.0F, this.yBodyRot);

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

   public int getHelpCommand() {
      return (Integer)this.entityData.get(HELP_COMMAND);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public boolean getCommandFollow() {
      return (Integer)this.entityData.get(COMMAND) == 0;
   }

   public boolean getCommandSit() {
      return (Integer)this.entityData.get(COMMAND) == 1;
   }

   public boolean getCommandWander() {
      return (Integer)this.entityData.get(COMMAND) == 2;
   }

   public boolean getCommandHelp() {
      return (Integer)this.entityData.get(COMMAND) == 3;
   }

   public void setCommandLoad(int command) {
      this.entityData.set(COMMAND, command);
      this.setOrderedToSit(command == 1);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
      if (this.level().isClientSide) {
         HexereiPacketHandler.sendToServer(new CrowSyncCommandToServer(this, command));
      } else {
         this.setOrderedToSit(command == 1);
      }
   }

   public void setHelpCommandLoad(int command) {
      this.entityData.set(HELP_COMMAND, command);
   }

   public void setHelpCommand(int command) {
      this.entityData.set(HELP_COMMAND, command);
      if (this.level().isClientSide) {
         HexereiPacketHandler.sendToServer(new CrowSyncHelpCommandToServer(this, command));
      }
   }

   public void setCommandFollow() {
      this.entityData.set(COMMAND, 0);
      this.setOrderedToSit(false);
   }

   public void setCommandSit() {
      this.entityData.set(COMMAND, 1);
      this.setOrderedToSit(true);
   }

   public void setCommandWander() {
      this.entityData.set(COMMAND, 2);
      this.setOrderedToSit(false);
   }

   public void setCommandHelp() {
      this.entityData.set(COMMAND, 3);
      this.setOrderedToSit(false);
   }

   public void setPerchPos(BlockPos pos) {
      this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
   }

   public BlockPos getPerchPos() {
      return (BlockPos)((Optional)this.entityData.get(PERCH_POS)).orElse(null);
   }

   public DyeColor getDyeColor() {
      DyeColor color = HexereiUtil.getDyeColorNamed(this.getName().getString(), 0);
      return color == null ? DyeColor.byId((Integer)this.entityData.get(CROW_DYE_COLOR)) : color;
   }

   public int getDyeColorId() {
      return (Integer)this.entityData.get(CROW_DYE_COLOR);
   }

   public void setDyeColor(int color) {
      this.entityData.set(CROW_DYE_COLOR, color);
   }

   public void setDyeColor(DyeColor color) {
      this.entityData.set(CROW_DYE_COLOR, color.getId());
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
         case ANIMAL_ARMOR -> ItemStack.EMPTY.copy();
         default -> throw new MatchException(null, null);
      };
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setCommandLoad(compound.getInt("Command"));
      this.setHelpCommandLoad(compound.getInt("HelpCommand"));
      this.pickpocketTimer = compound.getInt("PickpocketTimer");
      this.setTypeVariant(compound.getInt("Variant"));
      this.playingDead = compound.getInt("PlayingDeadTimer");
      if (compound.contains("InteractionRange")) {
         this.interactionRange = compound.getInt("InteractionRange");
      }

      if (compound.contains("CanAttack")) {
         this.canAttack = compound.getBoolean("CanAttack");
      }

      this.itemHandler.deserializeNBT(this.level().registryAccess(), compound.getCompound("inv"));
      List<Block> list = new ArrayList<>();
      if (compound.contains("HarvestWhitelistSize")) {
         int size = compound.getInt("HarvestWhitelistSize");

         for (int i = 0; i < size; i++) {
            list.add((Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(compound.getString("HarvestWhitelist_" + i))));
         }
      }

      this.harvestWhitelist = list;
      if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
         this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
      }

      if (compound.contains("DyeColor")) {
         this.setDyeColor(compound.getInt("DyeColor"));
      }
   }

   public void readAdditionalSaveDataNoSuper(CompoundTag compound) {
      this.setCommandLoad(compound.getInt("Command"));
      this.setHelpCommandLoad(compound.getInt("HelpCommand"));
      this.pickpocketTimer = compound.getInt("PickpocketTimer");
      this.setTypeVariant(compound.getInt("Variant"));
      this.playingDead = compound.getInt("PlayingDeadTimer");
      if (compound.contains("InteractionRange")) {
         this.interactionRange = compound.getInt("InteractionRange");
      }

      if (compound.contains("CanAttack")) {
         this.canAttack = compound.getBoolean("CanAttack");
      }

      this.itemHandler.deserializeNBT(this.level().registryAccess(), compound.getCompound("inv"));
      List<Block> list = new ArrayList<>();
      if (compound.contains("HarvestWhitelistSize")) {
         int size = compound.getInt("HarvestWhitelistSize");

         for (int i = 0; i < size; i++) {
            list.add((Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(compound.getString("HarvestWhitelist_" + i))));
         }
      }

      this.harvestWhitelist = list;
      if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
         this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
      }

      if (compound.contains("DyeColor")) {
         this.setDyeColor(compound.getInt("DyeColor"));
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Command", this.getCommand());
      compound.putInt("HelpCommand", this.getHelpCommand());
      compound.putInt("PickpocketTimer", this.pickpocketTimer);
      compound.putInt("Variant", this.getTypeVariant());
      compound.putInt("PlayingDeadTimer", this.playingDead);
      compound.putInt("InteractionRange", this.interactionRange);
      compound.putBoolean("CanAttack", this.canAttack);
      compound.put("inv", this.itemHandler.serializeNBT(this.level().registryAccess()));
      compound.putInt("HarvestWhitelistSize", this.harvestWhitelist.size());

      for (int i = 0; i < this.harvestWhitelist.size(); i++) {
         if (BuiltInRegistries.BLOCK.containsValue(this.harvestWhitelist.get(i))) {
            compound.putString("HarvestWhitelist_" + i, BuiltInRegistries.BLOCK.getKey(this.harvestWhitelist.get(i)).toString());
         }
      }

      if (this.getPerchPos() != null) {
         compound.putInt("PerchX", this.getPerchPos().getX());
         compound.putInt("PerchY", this.getPerchPos().getY());
         compound.putInt("PerchZ", this.getPerchPos().getZ());
      }

      compound.putInt("DyeColor", this.getDyeColorId());
   }

   public void addAdditionalSaveDataNoSuper(CompoundTag compound) {
      compound.putInt("Command", this.getCommand());
      compound.putInt("HelpCommand", this.getHelpCommand());
      compound.putInt("PickpocketTimer", this.pickpocketTimer);
      compound.putInt("Variant", this.getTypeVariant());
      compound.putInt("PlayingDeadTimer", this.playingDead);
      compound.putInt("InteractionRange", this.interactionRange);
      compound.putBoolean("CanAttack", this.canAttack);
      compound.put("inv", this.itemHandler.serializeNBT(this.level().registryAccess()));
      compound.putInt("HarvestWhitelistSize", this.harvestWhitelist.size());

      for (int i = 0; i < this.harvestWhitelist.size(); i++) {
         ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(this.harvestWhitelist.get(i));
         if (BuiltInRegistries.BLOCK.containsValue(this.harvestWhitelist.get(i))) {
            compound.putString("HarvestWhitelist_" + i, BuiltInRegistries.BLOCK.getKey(this.harvestWhitelist.get(i)).toString());
         }
      }

      if (this.getPerchPos() != null) {
         compound.putInt("PerchX", this.getPerchPos().getX());
         compound.putInt("PerchY", this.getPerchPos().getY());
         compound.putInt("PerchZ", this.getPerchPos().getZ());
      }

      compound.putInt("DyeColor", this.getDyeColorId());
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomsource = level.getRandom();
      CrowVariant variant;
      if (spawnGroupData instanceof CrowEntity.CrowGroupData) {
         variant = ((CrowEntity.CrowGroupData)spawnGroupData).variant;
      } else {
         boolean isVariant = randomsource.nextInt(5) == 0;
         variant = (CrowVariant)Util.getRandom(CrowVariant.values(), randomsource);
         if (!isVariant) {
            variant = CrowVariant.BLACK;
         }

         spawnGroupData = new CrowEntity.CrowGroupData(variant);
      }

      this.setTypeVariant(variant.getId() & 0xFF);
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
         if (this.isOrderedToSit() || this.isInSittingPose()) {
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
            && (this.isOrderedToSit() || this.isInSittingPose())) {
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
      return stack.getItem() == ModItems.SEED_MIXTURE.get();
   }

   public boolean canBeSeenAsEnemy() {
      return !this.isPlayingDead() && super.canBeSeenAsEnemy();
   }

   public void setPlayingDead(boolean pPlayingDead) {
      if (this.getItem(2).is((Item)ModItems.CROW_ANKH_AMULET.get())) {
         ItemStack stack = this.getItem(2).copy();
         CompoundTag tag = ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag();
         tag.putBoolean("Active", pPlayingDead);
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         this.setItem(2, stack);
         if (pPlayingDead) {
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1));
         }
      }

      if (pPlayingDead != this.isPlayingDead()) {
         this.setNoGravity(false);
         this.setSpeed(0.0F);
         this.navigation.stop();
         this.caw = false;
         this.cawTimer = this.random.nextInt(360) + 360;
         this.tailFan = false;
         this.tailFanTimer = this.random.nextInt(80) + 20;
         this.tailWag = false;
         this.tailWagTimer = this.random.nextInt(80) + 80;
         this.sync();
      }

      this.entityData.set(DATA_PLAYING_DEAD, pPlayingDead);
   }

   public boolean isPlayingDead() {
      return (Boolean)this.entityData.get(DATA_PLAYING_DEAD);
   }

   public Map<String, Vector3f> getModelRotationValues() {
      return this.modelRotationValues;
   }

   public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
      CrowEntity crow = (CrowEntity)((EntityType)ModEntityTypes.CROW.get()).create(world);
      if (crow != null) {
         CrowVariant crowVariant = this.random.nextBoolean() ? this.getVariant() : ((CrowEntity)entity).getVariant();
         crow.setTypeVariant(crowVariant.getId() & 0xFF);
         crow.setPersistenceRequired();
      }

      return crow;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (!player.isSecondaryUseActive() && this.isTame() && this.isOwnedBy(player) && player.getItemInHand(hand).is((Item)ModItems.CROW_FLUTE.get())) {
         ItemStack stack = player.getItemInHand(hand).copy();
         FluteData fluteData = (FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
         if (fluteData.commandMode() == 1) {
            List<FluteData.CrowIds> crowList = new ArrayList<>(fluteData.crowList());
            boolean flag = false;

            for (int i = 0; i < crowList.size(); i++) {
               UUID thisuuid = this.getUUID();
               UUID tempuuid = crowList.get(i).uuid();
               if (tempuuid != null && thisuuid.toString().equals(tempuuid.toString())) {
                  flag = true;
                  player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_deselect_message", new Object[]{this.getName()}), true);
                  this.playSound((SoundEvent)ModSounds.CROW_FLUTE_DESELECT.get(), 1.0F, 0.75F);
                  crowList.remove(i);
                  FluteData newFluteData = new FluteData(
                     fluteData.commandSelected(),
                     fluteData.helpCommandSelected(),
                     fluteData.commandMode(),
                     crowList,
                     fluteData.dyeColor1(),
                     fluteData.dyeColor2()
                  );
                  stack.set(ModDataComponents.FLUTE, newFluteData);
                  player.setItemInHand(hand, stack);
                  break;
               }
            }

            if (!flag) {
               if (crowList.size() < 9) {
                  crowList.add(new FluteData.CrowIds(this.getUUID(), this.getId()));
                  player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_selected_message", new Object[]{this.getName()}), true);
                  this.playSound((SoundEvent)ModSounds.CROW_FLUTE_SELECT.get(), 1.0F, 0.75F);
                  FluteData newFluteData = new FluteData(
                     fluteData.commandSelected(),
                     fluteData.helpCommandSelected(),
                     fluteData.commandMode(),
                     crowList,
                     fluteData.dyeColor1(),
                     fluteData.dyeColor2()
                  );
                  stack.set(ModDataComponents.FLUTE, newFluteData);
                  player.setItemInHand(hand, stack);
               } else {
                  player.playSound((SoundEvent)ModSounds.CROW_FLUTE_DESELECT.get(), 1.0F, 0.1F);
                  player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 10);
               }
            }

            return InteractionResult.SUCCESS;
         }
      }

      if (player.isSecondaryUseActive() && this.isOwnedBy(player)) {
         if (!this.level().isClientSide()) {
            MenuProvider containerProvider = this.createContainerProvider(this.level(), this.blockPosition());
            player.openMenu(containerProvider, b -> b.writeInt(this.getId()));
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else if (this.itemHandler.getStackInSlot(1).isEmpty()) {
         if (!this.isTame() && itemstack.getItem() == ModItems.SEED_MIXTURE.get()) {
            if (!player.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            if (!this.level().isClientSide) {
               if (this.random.nextInt(10) == 0 && !EventHooks.onAnimalTame(this, player)) {
                  this.tame(player);
                  this.level().broadcastEntityEvent(this, (byte)7);
               } else {
                  this.level().broadcastEntityEvent(this, (byte)6);
               }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         } else if (this.isTame() && !this.isBaby() && itemstack.getItem() == Items.GOLD_NUGGET && !this.isInLove()) {
            this.breedNuggetGivenByPlayer = true;
            this.breedNuggetGivenByPlayerUUID = player.getUUID();
            if (!player.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            if (!this.level().isClientSide) {
               this.setInLove(player);
               this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
               this.itemHandler.setStackInSlot(1, new ItemStack(Items.GOLD_NUGGET));
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         } else if (!this.isTame() || !this.isOwnedBy(player)) {
            return super.mobInteract(player, hand);
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

               this.setCommand(this.getCommand() + 1);
               if (this.getCommand() == 4) {
                  this.setCommand(0);
               }

               if (this.getCommand() == 3) {
                  if (this.getHelpCommand() == 0) {
                     player.displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_0", new Object[]{this.getName()}), true);
                  }

                  if (this.getHelpCommand() == 1) {
                     player.displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_1", new Object[]{this.getName()}), true);
                  }

                  if (this.getHelpCommand() == 2) {
                     player.displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_2", new Object[]{this.getName()}), true);
                  }
               } else {
                  player.displayClientMessage(Component.translatable("entity.hexerei.crow_command_" + this.getCommand(), new Object[]{this.getName()}), true);
               }

               this.setOrderedToSit(this.getCommand() == 1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }
      } else {
         if (itemstack.getItem() != Items.GOLD_NUGGET || !this.breedNuggetGivenByPlayer && this.breedNuggetGivenByCrowTimer <= 0) {
            this.spawnAtLocation(this.itemHandler.getStackInSlot(1).copy());
            this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      }
   }

   protected void dropAllDeathLoot(ServerLevel level, DamageSource damageSource) {
      ItemStack hat = this.itemHandler.getStackInSlot(0);
      ItemStack itemstack = this.itemHandler.getStackInSlot(1);
      ItemStack misc = this.itemHandler.getStackInSlot(2);
      if (!itemstack.isEmpty()) {
         this.spawnAtLocation(itemstack);
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }

      if (!hat.isEmpty()) {
         this.spawnAtLocation(hat);
      }

      if (!misc.isEmpty()) {
         this.spawnAtLocation(misc);
      }

      super.dropAllDeathLoot(level, damageSource);
   }

   private boolean isCrowEdible(ItemStack stack) {
      return stack.has(DataComponents.FOOD) || TEMPTATION_ITEMS.test(stack);
   }

   private boolean isCrowTemptItem(ItemStack stack) {
      return TEMPTATION_ITEMS.test(stack);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      if (this.isTame() && this.getCommandHelp() && this.cofferHerbJarList != null && !this.cofferHerbJarList.isEmpty()) {
         int k = 0;
         boolean flag = false;
         if (this.getPerchPos() != null) {
            BlockEntity perchEntity = this.level().getBlockEntity(this.getPerchPos());
            if (perchEntity instanceof HerbJarTile herbJarTile && herbJarTile.buttonToggled != 0) {
               ItemStack herbJarItem = herbJarTile.itemHandler.getStackInSlot(0);
               if ((Boolean)HexConfig.JARS_ONLY_HOLD_HERBS.get()) {
                  if (stack.is(HexereiTags.Items.HERB_ITEM) && (herbJarItem.isEmpty() || ItemStack.isSameItemSameComponents(herbJarItem, stack))) {
                     return true;
                  }
               } else if (herbJarItem.isEmpty() || ItemStack.isSameItemSameComponents(herbJarItem, stack)) {
                  return true;
               }
            }

            if (perchEntity instanceof CofferTile cofferTile && cofferTile.buttonToggled != 0) {
               switch (cofferTile.mode) {
                  case WHITELIST_INV:
                     if (cofferTile.hasWhitelistItem(stack) || cofferTile.hasItem(stack.getItem())) {
                        flag = true;
                     }
                     break;
                  case WHITELIST:
                     if (cofferTile.hasWhitelistItem(stack)) {
                        flag = true;
                     }
                     break;
                  case BLACKLIST_INV:
                     if (!cofferTile.hasWhitelistItem(stack) && cofferTile.hasItem(stack.getItem())) {
                        flag = true;
                     }
               }

               if (cofferTile.isEmpty() || cofferTile.hasNonMaxStackItemStack(stack) || cofferTile.hasItem(Items.AIR)) {
                  return true;
               }
            }
         }

         for (BlockPos blockPos : this.cofferHerbJarList) {
            BlockEntity blockEntity = this.level().getBlockEntity(this.cofferHerbJarList.get(k++));
            if (blockEntity instanceof CofferTile cofferTile) {
               switch (cofferTile.mode) {
                  case WHITELIST_INV:
                     if (cofferTile.hasWhitelistItem(stack) || cofferTile.hasItem(stack.getItem())) {
                        flag = true;
                     }
                     break;
                  case WHITELIST:
                     if (cofferTile.hasWhitelistItem(stack)) {
                        flag = true;
                     }
                     break;
                  case BLACKLIST_INV:
                     if (!cofferTile.hasWhitelistItem(stack) && cofferTile.hasItem(stack.getItem())) {
                        continue;
                     }
               }

               if (flag) {
                  break;
               }
            }

            if (blockEntity instanceof HerbJarTile herbJarTilex && herbJarTilex.itemHandler.getStackInSlot(0).is(stack.getItem())) {
               flag = true;
               break;
            }
         }

         return flag;
      } else {
         return this.isTame() && this.getHealth() < this.getMaxHealth()
            ? this.isCrowEdible(stack)
            : !this.isTame() && this.isCrowEdible(stack) && !this.isMaxHealth() || this.isCrowTemptItem(stack);
      }
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
      if (e.getItem().getItem() == ModItems.SEED_MIXTURE.get() && !this.isTame()) {
         this.seedThrowerID = itemThrower.getUUID();
      } else {
         this.seedThrowerID = null;
      }
   }

   @Override
   public void onFindTarget(ItemEntity e) {
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

   public int getContainerSize() {
      return 3;
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
      return new CrowContainer(id, this, inv, player);
   }

   private MenuProvider createContainerProvider(Level worldIn, BlockPos pos) {
      return new MenuProvider() {
         @org.jetbrains.annotations.Nullable
         public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return new CrowContainer(i, CrowEntity.this, playerInventory, playerEntity);
         }

         public Component getDisplayName() {
            return Component.translatable("");
         }
      };
   }

   public boolean isPowered() {
      return this.itemHandler.getStackInSlot(1).is((Item)ModItems.WARHAMMER.get()) && this.getDisplayName().getString().equals("Thor");
   }

   public void containerChanged(Container p_18983_) {
      ItemStack stack = p_18983_.getItem(2);
      stack.setEntityRepresentation(this);
   }

   private void flyOrWalkTo(Vec3 pos) {
      Path path1 = this.flyingNav.createPath(BlockPos.containing(pos), 0);
      Path path2 = this.groundNav.createPath(BlockPos.containing(pos), 0);
      if (path1 != null) {
         if (path2 == null) {
            this.switchNavigator(true);
         } else if (path2.getDistToTarget() > path1.getDistToTarget()) {
            this.switchNavigator(true);
         } else {
            this.switchNavigator(!this.random.nextBoolean());
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

   protected void addBringItemHomeGoal() {
      if (this.bringItemHomeGoal == null) {
         this.bringItemHomeGoal = new CrowEntity.BringItemHome(this);
      }

      this.goalSelector.removeGoal(this.bringItemHomeGoal);
      if (this.isTame()) {
         this.goalSelector.addGoal(1, this.bringItemHomeGoal);
      }
   }

   protected void bringItemHome() {
      this.bringItemHome = false;
      this.bringItemHomeActive = true;
      this.addBringItemHomeGoal();
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
         if (!this.animal.isInLove()) {
            return false;
         } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
         }
      }

      public boolean canContinueToUse() {
         return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 120;
      }

      public void start() {
         CrowEntity.this.doingTask = true;
         CrowEntity.this.setCommandHelp();
         if (CrowEntity.this.isInSittingPose()) {
            CrowEntity.this.setInSittingPose(false);
            CrowEntity.this.setOrderedToSit(false);
         }

         super.start();
      }

      public void stop() {
         CrowEntity.this.doingTask = false;
         this.partner = null;
         this.loveTime = 0;
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            if (this.partner != null) {
               this.animal.getLookControl().setLookAt(this.partner, 10.0F, this.animal.getMaxHeadXRot());
               CrowEntity.this.walkToIfNotFlyTo(this.partner.position());
               this.animal.getNavigation().moveTo(this.partner, CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
               this.loveTime++;
               if (this.animal.distanceToSqr(this.partner) < 4.0 && CrowEntity.this.breedNuggetGivenByPlayer) {
                  if (!((CrowEntity)this.partner).getCommandSit()) {
                     ((CrowEntity)this.partner).setCommandSit();
                  }

                  this.partner.getLookControl().setLookAt(this.animal, 10.0F, this.animal.getMaxHeadXRot());
                  CrowEntity.this.waitToGiveTime++;
                  if (CrowEntity.this.waitToGiveTime > 20 && CrowEntity.this.onGround() && CrowEntity.this.itemHandler.getStackInSlot(1).is(Items.GOLD_NUGGET)) {
                     ((CrowEntity)this.partner).setCommandFollow();
                     CrowEntity.this.waitToGiveTime = 0;
                     CrowEntity.this.breedNuggetGivenByPlayer = false;
                     CrowEntity.this.peck();
                     HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
                     ItemStack stack = ((CrowEntity)this.partner).itemHandler.getStackInSlot(1).copy();
                     ItemStack stack2 = CrowEntity.this.itemHandler.getStackInSlot(1).copy();
                     ((CrowEntity)this.partner).itemHandler.setStackInSlot(1, stack2);
                     CrowEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
                     ItemEntity itemEntity = new ItemEntity(
                        this.partner.level(), this.partner.position().x, this.partner.position().y, this.partner.position().z, stack
                     );
                     this.partner.level().addFreshEntity(itemEntity);
                     if (CrowEntity.this.breedNuggetGivenByPlayerUUID != null
                        && this.level.getPlayerByUUID(CrowEntity.this.breedNuggetGivenByPlayerUUID) != null) {
                        ((CrowEntity)this.partner).breedNuggetGivenByPlayerUUID = CrowEntity.this.breedNuggetGivenByPlayerUUID;
                        ((CrowEntity)this.partner).breedNuggetGivenByCrowTimer = 20;
                     } else if (CrowEntity.this.getOwner() instanceof Player) {
                        this.partner.setInLove((Player)CrowEntity.this.getOwner());
                     }
                  }
               }

               if (this.loveTime >= this.adjustedTickDelay(60)
                  && this.animal.distanceToSqr(this.partner) < 9.0
                  && CrowEntity.this.itemHandler.getStackInSlot(1).is(Items.GOLD_NUGGET)) {
                  this.breed();
               }
            }
         }
      }

      public boolean canMateCrowBringNugget(Animal animal) {
         if (animal.isBaby()) {
            return false;
         } else if (animal == CrowEntity.this) {
            return false;
         } else {
            return animal.getClass() != CrowEntity.this.getClass() ? false : CrowEntity.this.isInLove();
         }
      }

      public boolean canMateCrowReceiveNugget(Animal animal) {
         if (animal.isBaby()) {
            return false;
         } else if (animal == CrowEntity.this) {
            return false;
         } else {
            return animal.getClass() != CrowEntity.this.getClass() ? false : CrowEntity.this.isInLove() && animal.isInLove();
         }
      }

      @Nullable
      private Animal getFreePartner() {
         List<? extends Animal> list = this.level
            .getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(16.0));
         double d0 = 1.7976931348623157E308;
         Animal animal = null;
         if (CrowEntity.this.breedNuggetGivenByPlayer) {
            for (Animal animal1 : list) {
               if (this.canMateCrowBringNugget(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                  animal = animal1;
                  d0 = this.animal.distanceToSqr(animal1);
               }
            }
         } else {
            for (Animal animal1x : list) {
               if (this.canMateCrowReceiveNugget(animal1x) && this.animal.distanceToSqr(animal1x) < d0) {
                  animal = animal1x;
                  d0 = this.animal.distanceToSqr(animal1x);
               }
            }
         }

         return animal;
      }

      protected void breed() {
         CrowEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
         this.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
      }

      public void spawnChildFromBreeding(ServerLevel p_27564_, Animal p_27565_) {
         AgeableMob ageablemob = CrowEntity.this.getBreedOffspring(p_27564_, p_27565_);
         BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(CrowEntity.this, p_27565_, ageablemob);
         BabyEntitySpawnEvent spawnevent = (BabyEntitySpawnEvent)NeoForge.EVENT_BUS.post(event);
         ageablemob = event.getChild();
         if (spawnevent.isCanceled()) {
            CrowEntity.this.setAge(6000);
            p_27565_.setAge(6000);
            CrowEntity.this.resetLove();
            p_27565_.resetLove();
         } else {
            if (ageablemob != null) {
               ServerPlayer serverplayer = CrowEntity.this.getLoveCause();
               if (serverplayer == null && p_27565_.getLoveCause() != null) {
                  serverplayer = p_27565_.getLoveCause();
               }

               if (serverplayer != null) {
                  serverplayer.awardStat(Stats.ANIMALS_BRED);
                  CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, CrowEntity.this, p_27565_, ageablemob);
               }

               CrowEntity.this.setAge(6000);
               p_27565_.setAge(6000);
               CrowEntity.this.resetLove();
               p_27565_.resetLove();
               ageablemob.setBaby(true);
               ageablemob.moveTo(CrowEntity.this.getX(), CrowEntity.this.getY(), CrowEntity.this.getZ(), 0.0F, 0.0F);
               p_27564_.addFreshEntityWithPassengers(ageablemob);
               ((CrowEntity)ageablemob).setOwnerUUID(CrowEntity.this.breedNuggetGivenByPlayerUUID);
               ((CrowEntity)ageablemob).setTame(true, true);
               ((CrowEntity)ageablemob).setCommandSit();
               p_27564_.broadcastEntityEvent(CrowEntity.this, (byte)18);
               if (p_27564_.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                  p_27564_.addFreshEntity(
                     new ExperienceOrb(
                        p_27564_, CrowEntity.this.getX(), CrowEntity.this.getY(), CrowEntity.this.getZ(), CrowEntity.this.getRandom().nextInt(7) + 1
                     )
                  );
               }
            }
         }
      }
   }

   public static class BringItemHome extends Goal {
      private final CrowEntity crow;

      public BringItemHome(CrowEntity crow) {
         this.crow = crow;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.crow.bringItemHomeActive && !this.crow.getCommandSit();
      }

      public boolean canContinueToUse() {
         return this.crow.bringItemHomeActive && !this.crow.getCommandSit();
      }

      public void tick() {
         if (!this.crow.isPlayingDead()) {
            float topOffset = 0.0F;
            if (this.crow.itemHandler.getStackInSlot(1).isEmpty()) {
               this.crow.bringItemHomeActive = false;
               this.stop();
            }

            BlockPos returnLoc = BlockPos.ZERO;
            boolean toOwner = false;
            if (this.crow.getPerchPos() != null) {
               returnLoc = this.crow.getPerchPos();
               topOffset = (float)this.crow
                  .level()
                  .getBlockState(this.crow.getPerchPos())
                  .getOcclusionShape(this.crow.level(), this.crow.getPerchPos())
                  .max(Axis.Y);
            } else if (this.crow.getOwner() != null) {
               returnLoc = this.crow.getOwner().blockPosition();
               toOwner = true;
            } else {
               this.stop();
               this.crow.bringItemHomeActive = false;
            }

            Vec3 crowPos = this.crow.position();
            float crowPosY = (float)crowPos.y();
            float crowDistTo = (float)this.crow.distanceTo(returnLoc.getX(), returnLoc.getZ());
            float heightBelow = returnLoc.getY() + topOffset - 0.25F;
            float heightAbove = returnLoc.getY() + topOffset + 0.25F;
            if (crowDistTo < 1.0F && crowPosY >= heightBelow && crowPosY < heightAbove) {
               if (!toOwner && this.crow.level().getBlockEntity(returnLoc) instanceof Container container) {
                  try {
                     BlockEntity entity = this.crow.level().getBlockEntity(returnLoc);
                     IItemHandler handler = (IItemHandler)this.crow.level().getCapability(ItemHandler.BLOCK, returnLoc, Direction.DOWN);
                     if (handler != null) {
                        ItemStack duplicate = this.crow.itemHandler.getStackInSlot(1).copy();
                        ItemStack insertSimulate = ItemHandlerHelper.insertItem(handler, duplicate, true);
                        if (!insertSimulate.equals(duplicate)) {
                           ItemStack shrunkenStack = ItemHandlerHelper.insertItem(handler, duplicate, false);
                           if (shrunkenStack.isEmpty()) {
                              this.crow.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
                           } else {
                              this.crow.itemHandler.setStackInSlot(1, shrunkenStack);
                           }
                        } else {
                           ItemEntity ie = this.crow.spawnAtLocation(this.crow.itemHandler.getStackInSlot(1).copy());
                           if (ie != null) {
                              ie.setDeltaMovement(this.crow.getDeltaMovement());
                           }
                        }
                     }
                  } catch (Exception var15) {
                  }
               } else if (toOwner) {
                  if (this.crow.getOwner() instanceof Player player) {
                     player.getInventory().placeItemBackInInventory(this.crow.itemHandler.getStackInSlot(1).copy());
                  } else {
                     this.crow.spawnAtLocation(this.crow.itemHandler.getStackInSlot(1).copy());
                  }
               } else {
                  this.crow.spawnAtLocation(this.crow.itemHandler.getStackInSlot(1).copy());
               }

               this.crow.peck();
               HexereiPacketHandler.sendToNearbyClient(this.crow.level(), this.crow, new PeckPacket(this.crow));
               this.crow.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
               this.crow.bringItemHome = false;
               this.crow.bringItemHomeActive = false;
               this.crow.goalSelector.removeGoal(this.crow.bringItemHomeGoal);
               this.stop();
            } else {
               if (toOwner) {
                  returnLoc = returnLoc.above();
               }

               BlockPos pos = BlockPos.containing(Vec3.atBottomCenterOf(returnLoc).add(0.0, topOffset, 0.0));
               this.crow.flyOrWalkTo(Vec3.atBottomCenterOf(pos));
               this.crow.navigation.moveTo(this.crow.getNavigation().createPath(pos, 0), this.crow.isFlyingNav() ? 2.0 : 1.5);
            }

            super.tick();
         }
      }

      public void start() {
         this.crow.lastStuckCheckPos = this.crow.position();
         Vec3 returnLoc = Vec3.ZERO;
         if (this.crow.getPerchPos() != null) {
            returnLoc = this.crow.getPerchPos().above().getCenter();
         } else if (this.crow.getOwner() != null) {
            returnLoc = this.crow.getOwner().position();
         } else {
            this.stop();
         }

         if (this.crow.getPerchPos() != null) {
            this.crow.flyOrWalkTo(this.crow.getPerchPos().above().getCenter());
            this.crow.navigation.moveTo(this.crow.getNavigation().createPath(BlockPos.containing(returnLoc), 0), this.crow.isFlyingNav() ? 2.0 : 1.5);
         }
      }

      public void stop() {
         this.crow.doingTask = false;
      }
   }

   private class CrowDepositCoffer extends Goal {
      private final CrowEntity entity;
      protected final CrowEntity.CrowDepositCoffer.Sorter theNearestAttackableTargetSorter;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private BlockEntity targetEntity;
      private BlockPos flightTarget = null;
      private int cooldown = 0;
      private int tryTicks = 0;
      private Tag tag;

      CrowDepositCoffer(CrowEntity entity) {
         this.entity = entity;
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new CrowEntity.CrowDepositCoffer.Sorter(CrowEntity.this);
      }

      public boolean canUse() {
         if (CrowEntity.this.doingTask) {
            return false;
         } else if (CrowEntity.this.isPassenger()
            || CrowEntity.this.aiItemFlag
            || CrowEntity.this.isVehicle()
            || CrowEntity.this.isInSittingPose()
            || !CrowEntity.this.isTame()) {
            return false;
         } else if (CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()) {
            return false;
         } else {
            if (!this.mustUpdate) {
               long worldTime = CrowEntity.this.level().getGameTime() % 10L;
               if (CrowEntity.this.getNoActionTime() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (CrowEntity.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            if (this.entity.cofferHerbJarList != null && !this.entity.cofferHerbJarList.isEmpty()) {
               if (CrowEntity.this.getHelpCommand() == 0 && CrowEntity.this.getCommand() == 3) {
                  if (CrowEntity.this.isInSittingPose()) {
                     CrowEntity.this.setInSittingPose(false);
                     CrowEntity.this.setOrderedToSit(false);
                  }

                  this.entity.cofferHerbJarList.sort(this.theNearestAttackableTargetSorter);
                  boolean flag = false;

                  for (BlockPos pos : this.entity.cofferHerbJarList) {
                     this.targetEntity = CrowEntity.this.level().getBlockEntity(pos);
                     if (this.targetEntity != null) {
                        ItemStack crowItem = CrowEntity.this.itemHandler.getStackInSlot(1);
                        BlockPos perchPos = CrowEntity.this.getPerchPos();
                        if (this.targetEntity.getBlockPos().equals(perchPos)) {
                           if (this.targetEntity instanceof CofferTile cofferTile) {
                              switch (cofferTile.mode) {
                                 case WHITELIST_INV:
                                    if (cofferTile.isEmpty() || cofferTile.hasNonMaxStackItemStack(crowItem) || cofferTile.hasItem(Items.AIR)) {
                                       flag = true;
                                    }
                                    break;
                                 case WHITELIST:
                                    if (cofferTile.hasWhitelistItem(crowItem)
                                       && (cofferTile.isEmpty() || cofferTile.hasNonMaxStackItemStack(crowItem) || cofferTile.hasItem(Items.AIR))) {
                                       flag = true;
                                    }
                                    break;
                                 case BLACKLIST_INV:
                                    if (!cofferTile.hasWhitelistItem(crowItem)
                                       && (cofferTile.isEmpty() || cofferTile.hasNonMaxStackItemStack(crowItem) || cofferTile.hasItem(Items.AIR))) {
                                       flag = true;
                                    }
                              }

                              if (flag) {
                                 break;
                              }
                           } else if (this.targetEntity instanceof HerbJarTile herbJarTile) {
                              ItemStack herbJarItem = herbJarTile.itemHandler.getStackInSlot(0);
                              if (herbJarItem.isEmpty() || ItemStack.isSameItemSameComponents(herbJarItem, crowItem)) {
                                 flag = true;
                                 break;
                              }
                           }
                        }

                        if (!(this.targetEntity instanceof CofferTile cofferTile)) {
                           if (this.targetEntity instanceof HerbJarTile
                              && ((HerbJarTile)this.targetEntity).itemHandler.getStackInSlot(0).getItem()
                                 == CrowEntity.this.itemHandler.getStackInSlot(1).getItem()) {
                              flag = true;
                              break;
                           }
                        } else {
                           switch (cofferTile.mode) {
                              case WHITELIST_INV:
                                 if (cofferTile.hasWhitelistItem(crowItem) || cofferTile.hasItem(crowItem.getItem())) {
                                    flag = true;
                                 }
                                 break;
                              case WHITELIST:
                                 if (cofferTile.hasWhitelistItem(crowItem)) {
                                    flag = true;
                                 }
                                 break;
                              case BLACKLIST_INV:
                                 if (!cofferTile.hasWhitelistItem(crowItem) && cofferTile.hasItem(crowItem.getItem())) {
                                    flag = true;
                                 }
                           }

                           if (flag) {
                              break;
                           }
                        }
                     }
                  }

                  if (!flag) {
                     return false;
                  } else {
                     this.mustUpdate = false;
                     CrowEntity.this.aiCofferTileFlag = true;
                     return true;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public boolean canContinueToUse() {
         if (CrowEntity.this.getHelpCommand() == 0 && CrowEntity.this.getCommand() == 3 && CrowEntity.this.isInSittingPose()) {
            CrowEntity.this.setInSittingPose(false);
            CrowEntity.this.setOrderedToSit(false);
         }

         return this.targetEntity != null
            && !CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()
            && CrowEntity.this.getCommand() == 3
            && CrowEntity.this.getHelpCommand() == 0;
      }

      public void start() {
         CrowEntity.this.doingTask = true;
         CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
      }

      public void stop() {
         CrowEntity.this.doingTask = false;
         this.flightTarget = null;
         this.targetEntity = null;
         CrowEntity.this.aiCofferTileFlag = false;
         CrowEntity.this.depositItemBeforePerch = false;
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 20 == 0;
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            if (this.cooldown > 0) {
               this.cooldown--;
            }

            boolean isStuck = false;
            if (CrowEntity.this.stuckTimer++ > 80) {
               if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 2.25) {
                  isStuck = true;
               }

               CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
            }

            if (isStuck) {
               Vec3 randomPos = DefaultRandomPos.getPos(CrowEntity.this, 10, 7);
               if (randomPos == null) {
                  randomPos = LandRandomPos.getPos(CrowEntity.this, 10, 7);
               }

               BlockPos pos;
               if (randomPos != null) {
                  pos = new BlockPos((int)randomPos.x, (int)randomPos.y, (int)randomPos.z);
               } else if (CrowEntity.this.getPerchPos() != null) {
                  pos = CrowEntity.this.getPerchPos().above().above();
               } else {
                  pos = CrowEntity.this.blockPosition().above().above();
               }

               CrowEntity.this.navigation.stop();
               if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 0.1 && CrowEntity.this.onGround()) {
                  CrowEntity.this.push(
                     Math.min(0.20000000298023224, (pos.getX() - CrowEntity.this.position().x) / 20.0),
                     0.15000000596046448,
                     Math.min(0.20000000298023224, (pos.getZ() - CrowEntity.this.position().z) / 20.0)
                  );
                  CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), CrowEntity.this.isFlyingNav() ? 1.5 : 1.0);
               } else {
                  CrowEntity.this.flyOrWalkTo(CrowEntity.this.blockPosition().above().above().getCenter());
                  CrowEntity.this.navigation
                     .moveTo(
                        CrowEntity.this.getNavigation().createPath(CrowEntity.this.blockPosition().above().above(), 0),
                        CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                     );
               }

               CrowEntity.this.stuckTimer = 0;
            }

            this.tryTicks++;
            if (this.shouldRecalculatePath() && this.targetEntity != null) {
               CrowEntity.this.flyOrWalkTo(this.targetEntity.getBlockPos().getCenter());
               this.entity
                  .getNavigation()
                  .moveTo(
                     this.entity
                        .getNavigation()
                        .createPath(
                           this.targetEntity.getBlockPos().getX() + 0.5,
                           this.targetEntity.getBlockPos().getY() + 0.5F,
                           this.targetEntity.getBlockPos().getZ() + 0.5,
                           0
                        ),
                     CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                  );
               if (this.targetEntity
                     .getBlockPos()
                     .closerThan(new Vec3i((int)CrowEntity.this.position().x, (int)CrowEntity.this.position().y, (int)CrowEntity.this.position().z), 3.0)
                  && CrowEntity.this.position().y < this.targetEntity.getBlockPos().getY()) {
                  CrowEntity.this.setNoGravity(false);
                  CrowEntity.this.push(
                     (this.targetEntity.getBlockPos().getX() - CrowEntity.this.position().x) / 50.0,
                     (this.targetEntity.getBlockPos().getY() - CrowEntity.this.position().y) / 50.0 + 0.10000000149011612,
                     (this.targetEntity.getBlockPos().getZ() - CrowEntity.this.position().z) / 50.0
                  );
               }
            }

            if (this.flightTarget != null) {
               if (CrowEntity.this.horizontalCollision) {
                  CrowEntity.this.getMoveControl()
                     .setWantedPosition(
                        this.flightTarget.getX() + 0.5F,
                        CrowEntity.this.getY() + 1.0,
                        this.flightTarget.getZ() + 0.5F,
                        CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                     );
               } else {
                  CrowEntity.this.flyOrWalkTo(this.flightTarget.getCenter().add(0.0, 0.5, 0.0));
                  CrowEntity.this.getNavigation()
                     .moveTo(
                        this.entity
                           .getNavigation()
                           .createPath(this.flightTarget.getX() + 0.5F, this.flightTarget.getY() + 1.0F, this.flightTarget.getZ() + 0.5F, 0),
                        CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                     );
               }
            }

            if (this.targetEntity != null) {
               this.flightTarget = this.targetEntity.getBlockPos();
               if (CrowEntity.this.distanceToSqr(
                     this.targetEntity.getBlockPos().getX() + 0.5F, this.targetEntity.getBlockPos().getY(), this.targetEntity.getBlockPos().getZ() + 0.5F
                  )
                  < this.entity.getMaxDistToItem() * 1.25) {
                  try {
                     BlockEntity entity = this.targetEntity;
                     IItemHandler handler = (IItemHandler)CrowEntity.this.level().getCapability(ItemHandler.BLOCK, entity.getBlockPos(), null);
                     if (handler != null && this.cooldown == 0) {
                        ItemStack duplicate = CrowEntity.this.itemHandler.getStackInSlot(1).copy();
                        ItemStack insertSimulate = ItemHandlerHelper.insertItem(handler, duplicate, true);
                        if (!insertSimulate.equals(duplicate)) {
                           ItemStack shrunkenStack = ItemHandlerHelper.insertItem(handler, duplicate, false);
                           if (shrunkenStack.isEmpty()) {
                              CrowEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
                           } else {
                              CrowEntity.this.itemHandler.setStackInSlot(1, shrunkenStack);
                           }

                           CrowEntity.this.peck();
                           HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
                        } else {
                           this.cooldown = 20;
                        }
                     }
                  } catch (Exception var7) {
                  }

                  this.stop();
               }
            }
         }
      }

      public static ItemStack insertItem(IItemHandler dest, ItemStack stack, boolean simulate) {
         if (dest != null && !stack.isEmpty()) {
            for (int i = 0; i < dest.getSlots(); i++) {
               stack = dest.insertItem(i, stack, simulate);
               if (stack.isEmpty()) {
                  return ItemStack.EMPTY;
               }
            }

            return stack;
         } else {
            return stack;
         }
      }

      protected double getTargetDistance() {
         return 4.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(CrowEntity.this.getX(), CrowEntity.this.getY(), CrowEntity.this.getZ());
         AABB aabb = new AABB(-16.0, -16.0, -16.0, 16.0, 16.0, 16.0);
         return aabb.move(renderCenter);
      }

      public class Sorter implements Comparator<BlockPos> {
         private final Entity theEntity;

         public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
         }

         public int compare(BlockPos p_compare_1_, BlockPos p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(Vec3.atCenterOf(p_compare_1_));
            double d1 = this.theEntity.distanceToSqr(Vec3.atCenterOf(p_compare_2_));
            return Double.compare(d0, d1);
         }
      }
   }

   public class CrowGatherItems<T extends ItemEntity> extends TargetGoal {
      protected final CrowEntity.CrowGatherItems.Sorter theNearestAttackableTargetSorter;
      protected final Predicate<? super ItemEntity> targetEntitySelector;
      protected int executionChance;
      protected boolean mustUpdate;
      protected ItemEntity targetEntity;
      protected ITargetsDroppedItems hunter;
      private int tickThreshold;
      private int walkCooldown = 0;
      protected int tryTicks = 0;

      public CrowGatherItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
         this(creature, 1, checkSight, onlyNearby, null, tickThreshold);
      }

      public CrowGatherItems(
         PathfinderMob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector, int ticksExisted
      ) {
         super(creature, checkSight, onlyNearby);
         this.executionChance = chance;
         this.tickThreshold = ticksExisted;
         this.hunter = (ITargetsDroppedItems)creature;
         this.theNearestAttackableTargetSorter = new CrowEntity.CrowGatherItems.Sorter(creature);
         this.targetEntitySelector = item -> {
            ItemStack stack = item.getItem();
            return !stack.isEmpty() && this.hunter.canTargetItem(stack) && item.tickCount > this.tickThreshold;
         };
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (CrowEntity.this.doingTask) {
            return false;
         } else if (!CrowEntity.this.isTame()
            || !CrowEntity.this.isMaxHealth()
            || (CrowEntity.this.getCommand() != 3 || CrowEntity.this.getHelpCommand() == 0) && CrowEntity.this.getCommand() == 3) {
            if (!CrowEntity.this.isPassenger() && (!CrowEntity.this.isVehicle() || CrowEntity.this.getControllingPassenger() == null)) {
               if (!CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()) {
                  return false;
               } else {
                  if (!this.mustUpdate) {
                     long worldTime = CrowEntity.this.level().getGameTime() % 10L;
                     if (this.mob.getNoActionTime() >= 100 && worldTime != 0L) {
                        return false;
                     }

                     if (this.mob.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                        return false;
                     }
                  }

                  List<ItemEntity> list = this.mob
                     .level()
                     .getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(CrowEntity.this.interactionRange + 1), this.targetEntitySelector);
                  if (list.isEmpty()) {
                     return false;
                  } else {
                     if (CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                     }

                     list.sort(this.theNearestAttackableTargetSorter);
                     this.targetEntity = list.get(0);
                     this.mustUpdate = false;
                     if (this.targetEntity == null) {
                        return false;
                     } else {
                        this.hunter.onFindTarget(this.targetEntity);
                        return !((CrowEntity)this.mob).isInSittingPose() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
                     }
                  }
               }
            } else {
               return false;
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
         CrowEntity.this.doingTask = true;
         CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
         this.moveTo();
         super.start();
      }

      protected void moveTo() {
         if (this.walkCooldown > 0) {
            this.walkCooldown--;
         } else {
            CrowEntity.this.flyOrWalkTo(this.targetEntity.position().add(0.5, 0.25, 0.5));
            this.mob
               .getNavigation()
               .moveTo(
                  CrowEntity.this.getNavigation()
                     .createPath(this.targetEntity.getX() + 0.5, this.targetEntity.getY() + 0.25, this.targetEntity.getZ() + 0.5, 0),
                  CrowEntity.this.isFlyingNav() ? 1.5 : 1.25
               );
            this.walkCooldown = 30 + this.mob.getRandom().nextInt(40);
         }
      }

      public void stop() {
         CrowEntity.this.doingTask = false;
         super.stop();
         this.mob.getNavigation().stop();
         this.targetEntity = null;
         ((CrowEntity)this.mob).aiItemFlag = false;
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            super.tick();
            boolean isStuck = false;
            if (CrowEntity.this.stuckTimer++ > 80) {
               if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 2.25) {
                  isStuck = true;
               }

               CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
            }

            if (isStuck) {
               Vec3 randomPos = DefaultRandomPos.getPos(CrowEntity.this, 10, 7);
               if (randomPos == null) {
                  randomPos = LandRandomPos.getPos(CrowEntity.this, 10, 7);
               }

               BlockPos pos;
               if (randomPos != null) {
                  pos = new BlockPos((int)randomPos.x, (int)randomPos.y, (int)randomPos.z);
               } else if (CrowEntity.this.getPerchPos() != null) {
                  pos = CrowEntity.this.getPerchPos().above().above();
               } else {
                  pos = CrowEntity.this.blockPosition().above().above();
               }

               CrowEntity.this.navigation.stop();
               if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 0.1 && CrowEntity.this.onGround()) {
                  CrowEntity.this.push(
                     Math.min(0.20000000298023224, (pos.getX() - CrowEntity.this.position().x) / 20.0),
                     0.15000000596046448,
                     Math.min(0.20000000298023224, (pos.getZ() - CrowEntity.this.position().z) / 20.0)
                  );
                  CrowEntity.this.flyOrWalkTo(pos.getCenter());
                  CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), CrowEntity.this.isFlyingNav() ? 1.5 : 1.0);
               } else {
                  CrowEntity.this.flyOrWalkTo(CrowEntity.this.blockPosition().above().above().getCenter());
                  CrowEntity.this.navigation
                     .moveTo(
                        CrowEntity.this.getNavigation().createPath(CrowEntity.this.blockPosition().above().above(), 0),
                        CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                     );
               }

               CrowEntity.this.stuckTimer = 0;
            }

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
               && CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()) {
               if (this.hunter instanceof CrowEntity crow && !crow.peck) {
                  crow.peck();
                  HexereiPacketHandler.sendToNearbyClient(crow.level(), crow, new PeckPacket(crow));
               }

               if (((CrowEntity)this.hunter).peckTiltAngleActual > 40.0F) {
                  this.hunter.onGetItem(this.targetEntity);
                  CrowEntity.this.depositItemBeforePerch = true;
                  this.targetEntity.getItem().shrink(1);
                  this.stop();
               }
            }

            CrowEntity crow = (CrowEntity)this.mob;
            if (this.targetEntity != null) {
               crow.aiItemFlag = true;
               if (this.mob.distanceTo(this.targetEntity) <= CrowEntity.this.getMaxDistToItem()) {
                  crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY() + 0.5, this.targetEntity.getZ(), 1.5);
               }

               if (!crow.isInSittingPose()) {
                  CrowEntity.this.flyOrWalkTo(this.targetEntity.position().add(0.0, 0.5, 0.0));
                  this.mob
                     .getNavigation()
                     .moveTo(
                        CrowEntity.this.getNavigation().createPath(this.targetEntity.getX(), this.targetEntity.getY() + 0.5, this.targetEntity.getZ(), 0),
                        CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                     );
               }

               this.tryTicks++;
               if (this.shouldRecalculatePath()) {
                  if (this.targetEntity.position().distanceTo(CrowEntity.this.position()) < 3.0
                     && CrowEntity.this.position().y < this.targetEntity.position().y()) {
                     CrowEntity.this.setNoGravity(false);
                     CrowEntity.this.push(
                        (this.targetEntity.position().x - CrowEntity.this.position().x) / 50.0,
                        (this.targetEntity.position().y - CrowEntity.this.position().y) / 50.0 + 0.10000000149011612,
                        (this.targetEntity.position().z - CrowEntity.this.position().z) / 50.0
                     );
                  }

                  CrowEntity.this.flyOrWalkTo(this.targetEntity.position().add(0.0, 3.0, 0.0));
                  this.mob
                     .getNavigation()
                     .moveTo(
                        CrowEntity.this.getNavigation().createPath(this.targetEntity.getX(), this.targetEntity.getY() + 3.0, this.targetEntity.getZ(), 0),
                        CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                     );
               }
            }
         }
      }

      public void makeUpdate() {
         this.mustUpdate = true;
      }

      public boolean canContinueToUse() {
         if (!(CrowEntity.this.getHealth() >= CrowEntity.this.getMaxHealth())
            || !CrowEntity.this.isTame()
            || ((CrowEntity)this.mob).getCommand() == 3 && ((CrowEntity)this.mob).getHelpCommand() == 0) {
            boolean path = this.mob.getBbWidth() > 2.0 || !this.mob.getNavigation().isDone();
            if (CrowEntity.this.isInSittingPose()) {
               CrowEntity.this.setInSittingPose(false);
               CrowEntity.this.setOrderedToSit(false);
            }

            return path
               && this.targetEntity != null
               && this.targetEntity.isAlive()
               && !((CrowEntity)this.mob).isInSittingPose()
               && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
         } else {
            return false;
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

   public static class CrowGroupData extends AgeableMobGroupData {
      public final CrowVariant variant;

      public CrowGroupData(CrowVariant pVariant) {
         super(true);
         this.variant = pVariant;
      }
   }

   public class CrowHarvestGoal extends MoveToBlockGoal {
      private static final int WAIT_TICKS = 40;
      protected int ticksWaited;
      protected int tryTicks = 0;

      public CrowHarvestGoal(double p_28675_, int p_28676_, int p_28677_) {
         super(CrowEntity.this, p_28675_, p_28676_, p_28677_);
         this.searchRange = CrowEntity.this.interactionRange + 1;
      }

      protected int nextStartTick(PathfinderMob p_25618_) {
         return reducedTickDelay(20 + p_25618_.getRandom().nextInt(20));
      }

      public double acceptedDistance() {
         return 2.0;
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 100 == 0;
      }

      protected boolean isValidTarget(LevelReader p_28680_, BlockPos pos) {
         if (CrowEntity.this.getPerchPos() != null) {
            double dist = pos.distToCenterSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ());
            if (dist > CrowEntity.this.interactionRange * CrowEntity.this.interactionRange) {
               return false;
            }
         }

         BlockState blockstate = p_28680_.getBlockState(pos);
         if (CrowEntity.this.harvestWhitelist.isEmpty() || CrowEntity.this.harvestWhitelist.contains(blockstate.getBlock())) {
            if (blockstate.is(HexereiTags.Blocks.CROW_HARVESTABLE)) {
               if (blockstate.getBlock() instanceof StemBlock) {
                  return false;
               }

               if (blockstate.hasProperty(BlockStateProperties.AGE_1)) {
                  return (Integer)blockstate.getValue(BlockStateProperties.AGE_1) >= 1;
               }

               if (blockstate.hasProperty(BlockStateProperties.AGE_2)) {
                  return (Integer)blockstate.getValue(BlockStateProperties.AGE_2) >= 2;
               }

               if (blockstate.hasProperty(BlockStateProperties.AGE_3)) {
                  return (Integer)blockstate.getValue(BlockStateProperties.AGE_3) >= 3;
               }

               if (blockstate.hasProperty(BlockStateProperties.AGE_4)) {
                  return (Integer)blockstate.getValue(BlockStateProperties.AGE_4) >= 4;
               }

               if (blockstate.hasProperty(BlockStateProperties.AGE_5)) {
                  return (Integer)blockstate.getValue(BlockStateProperties.AGE_5) >= 5;
               }

               if (blockstate.hasProperty(BlockStateProperties.AGE_7)) {
                  return (Integer)blockstate.getValue(BlockStateProperties.AGE_7) >= 7;
               }

               return CaveVines.hasGlowBerries(blockstate);
            }

            if (blockstate.is(HexereiTags.Blocks.CROW_BLOCK_HARVESTABLE)) {
               if (blockstate.getBlock() instanceof StemBlock) {
                  if (CrowEntity.this.level().getBlockState(pos.north()).getBlock() instanceof AttachedStemBlock stemBlock
                     && CrowEntity.this.level().getBlockState(pos).is((Block)BuiltInRegistries.BLOCK.get(stemBlock.fruit.location()))) {
                     return true;
                  }

                  if (CrowEntity.this.level().getBlockState(pos.south()).getBlock() instanceof AttachedStemBlock stemBlock
                     && CrowEntity.this.level().getBlockState(pos).is((Block)BuiltInRegistries.BLOCK.get(stemBlock.fruit.location()))) {
                     return true;
                  }

                  if (CrowEntity.this.level().getBlockState(pos.east()).getBlock() instanceof AttachedStemBlock stemBlock
                     && CrowEntity.this.level().getBlockState(pos).is((Block)BuiltInRegistries.BLOCK.get(stemBlock.fruit.location()))) {
                     return true;
                  }

                  if (CrowEntity.this.level().getBlockState(pos.west()).getBlock() instanceof AttachedStemBlock stemBlock
                     && CrowEntity.this.level().getBlockState(pos).is((Block)BuiltInRegistries.BLOCK.get(stemBlock.fruit.location()))) {
                     return true;
                  }

                  return false;
               }

               if (BuiltInRegistries.BLOCK.getKey(blockstate.getBlock()).toString().equals("immersiveengineering:hemp")) {
                  return CrowEntity.this.level().getBlockState(pos.below()).is(blockstate.getBlock());
               }

               if (!(blockstate.getBlock() instanceof SugarCaneBlock)) {
                  return true;
               }

               return CrowEntity.this.level().getBlockState(pos.below()).is(blockstate.getBlock())
                  && !CrowEntity.this.level().getBlockState(pos.below().below()).is(blockstate.getBlock());
            }
         }

         return false;
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            BlockPos blockpos = this.getMoveToTarget();
            if (this.isReachedTarget()) {
               if (this.ticksWaited >= 10) {
                  this.onReachedTarget();
               } else {
                  this.ticksWaited++;
               }
            }

            this.tryTicks++;
            if (this.shouldRecalculatePath()) {
               if (blockpos.closerThan(new Vec3i((int)CrowEntity.this.position().x, (int)CrowEntity.this.position().y, (int)CrowEntity.this.position().z), 3.0)
                  && CrowEntity.this.position().y < blockpos.getY()) {
                  CrowEntity.this.setNoGravity(false);
                  if (CrowEntity.this.onGround()) {
                     CrowEntity.this.push(
                        (blockpos.getX() - CrowEntity.this.position().x) / 50.0,
                        (blockpos.getY() - CrowEntity.this.position().y) / 50.0 + 0.25,
                        (blockpos.getZ() - CrowEntity.this.position().z) / 50.0
                     );
                  } else {
                     CrowEntity.this.push(
                        (blockpos.getX() - CrowEntity.this.position().x) / 50.0,
                        (blockpos.getY() - CrowEntity.this.position().y) / 50.0 + 0.10000000149011612,
                        (blockpos.getZ() - CrowEntity.this.position().z) / 50.0
                     );
                  }
               }

               CrowEntity.this.flyOrWalkTo(blockpos.getCenter().add(0.0, 0.5, 0.0));
               this.mob
                  .getNavigation()
                  .moveTo(
                     CrowEntity.this.getNavigation().createPath(blockpos.getX() + 0.5, blockpos.getY() + 1.0, blockpos.getZ() + 0.5, 0),
                     CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier
                  );
            }

            super.tick();
         }
      }

      protected void onReachedTarget() {
         if (EventHooks.canEntityGrief(CrowEntity.this.level(), CrowEntity.this)) {
            BlockState blockstate = CrowEntity.this.level().getBlockState(this.blockPos);
            if (blockstate.is(HexereiTags.Blocks.CROW_HARVESTABLE)) {
               if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
                  this.pickSweetBerries(blockstate);
                  CrowEntity.this.peck();
                  HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
               } else if (CaveVines.hasGlowBerries(blockstate)) {
                  this.pickGlowBerry(blockstate);
                  CrowEntity.this.peck();
                  HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
               } else if (blockstate.getBlock() instanceof PickableDoublePlant && !CrowEntity.this.level().isClientSide) {
                  ItemStack firstOutput = new ItemStack(
                     (ItemLike)DataFixUtils.orElse(
                        this.mob
                           .level()
                           .registryAccess()
                           .registryOrThrow(Registries.ITEM)
                           .getOptional(((PickableDoublePlant)blockstate.getBlock()).firstOutput),
                        blockstate.getBlock()
                     ),
                     ((PickableDoublePlant)blockstate.getBlock()).maxFirstOutput
                  );
                  ItemStack secondOutput = ItemStack.EMPTY;
                  if (((PickableDoublePlant)blockstate.getBlock()).secondOutput != null) {
                     secondOutput = new ItemStack(
                        (ItemLike)DataFixUtils.orElse(
                           this.mob
                              .level()
                              .registryAccess()
                              .registryOrThrow(Registries.ITEM)
                              .getOptional(((PickableDoublePlant)blockstate.getBlock()).secondOutput),
                           blockstate.getBlock()
                        ),
                        ((PickableDoublePlant)blockstate.getBlock()).maxSecondOutput
                     );
                  }

                  int j = Math.max(1, CrowEntity.this.random.nextInt(firstOutput.getCount()));
                  int k = 0;
                  if (((PickableDoublePlant)blockstate.getBlock()).secondOutput != null) {
                     k = Math.max(1, CrowEntity.this.random.nextInt(secondOutput.getCount()));
                  }

                  Block.popResource(CrowEntity.this.level(), this.blockPos, new ItemStack(firstOutput.getItem(), Math.max(1, (int)Math.floor(j))));
                  if (CrowEntity.this.random.nextInt(2) == 0 && ((PickableDoublePlant)blockstate.getBlock()).secondOutput != null) {
                     Block.popResource(CrowEntity.this.level(), this.blockPos, new ItemStack(secondOutput.getItem(), Math.max(1, (int)Math.floor(k))));
                  }

                  if (blockstate.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                     CrowEntity.this.level()
                        .setBlock(this.blockPos, (BlockState)CrowEntity.this.level().getBlockState(this.blockPos).setValue(BlockStateProperties.AGE_3, 0), 2);
                     CrowEntity.this.level()
                        .setBlock(
                           this.blockPos.above(),
                           (BlockState)CrowEntity.this.level().getBlockState(this.blockPos.above()).setValue(BlockStateProperties.AGE_3, 0),
                           2
                        );
                  }

                  if (blockstate.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                     CrowEntity.this.level()
                        .setBlock(
                           this.blockPos.below(),
                           (BlockState)CrowEntity.this.level().getBlockState(this.blockPos.below()).setValue(BlockStateProperties.AGE_3, 0),
                           2
                        );
                     CrowEntity.this.level()
                        .setBlock(this.blockPos, (BlockState)CrowEntity.this.level().getBlockState(this.blockPos).setValue(BlockStateProperties.AGE_3, 0), 2);
                  }

                  CrowEntity.this.peck();
                  CrowEntity.this.playSound(SoundEvents.CAVE_VINES_PICK_BERRIES, 1.0F, 1.0F);
                  HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
               } else if (blockstate.getBlock() instanceof PickablePlant && !CrowEntity.this.level().isClientSide) {
                  ItemStack firstOutputx = new ItemStack(
                     (ItemLike)DataFixUtils.orElse(
                        this.mob.level().registryAccess().registryOrThrow(Registries.ITEM).getOptional(((PickablePlant)blockstate.getBlock()).firstOutput),
                        blockstate.getBlock()
                     ),
                     ((PickablePlant)blockstate.getBlock()).maxFirstOutput
                  );
                  ItemStack secondOutputx = ItemStack.EMPTY;
                  if (((PickablePlant)blockstate.getBlock()).secondOutput != null) {
                     secondOutputx = new ItemStack(
                        (ItemLike)DataFixUtils.orElse(
                           this.mob.level().registryAccess().registryOrThrow(Registries.ITEM).getOptional(((PickablePlant)blockstate.getBlock()).secondOutput),
                           blockstate.getBlock()
                        ),
                        ((PickablePlant)blockstate.getBlock()).maxSecondOutput
                     );
                  }

                  int jx = Math.max(1, CrowEntity.this.random.nextInt(firstOutputx.getCount()));
                  int kx = 0;
                  if (((PickablePlant)blockstate.getBlock()).secondOutput != null) {
                     kx = Math.max(1, CrowEntity.this.random.nextInt(secondOutputx.getCount()));
                  }

                  PickablePlant.popResource(CrowEntity.this.level(), this.blockPos, new ItemStack(firstOutputx.getItem(), Math.max(1, (int)Math.floor(jx))));
                  if (CrowEntity.this.random.nextInt(2) == 0 && ((PickablePlant)blockstate.getBlock()).secondOutput != null) {
                     PickablePlant.popResource(CrowEntity.this.level(), this.blockPos, new ItemStack(secondOutputx.getItem(), Math.max(1, (int)Math.floor(kx))));
                  }

                  CrowEntity.this.level().setBlock(this.blockPos, (BlockState)blockstate.setValue(BlockStateProperties.AGE_3, 0), 2);
                  CrowEntity.this.peck();
                  CrowEntity.this.playSound(SoundEvents.CAVE_VINES_PICK_BERRIES, 1.0F, 1.0F);
                  HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
               } else if (!CrowEntity.this.level().isClientSide) {
                  for (ItemStack drop : Block.getDrops(
                     blockstate, (ServerLevel)CrowEntity.this.level(), this.blockPos, CrowEntity.this.level().getBlockEntity(this.blockPos)
                  )) {
                     if (blockstate.hasProperty(BlockStateProperties.AGE_3)) {
                        CrowEntity.this.level()
                           .addFreshEntity(
                              new ItemEntity(
                                 CrowEntity.this.level(), this.blockPos.getX() + 0.5F, this.blockPos.getY() + 0.25F, this.blockPos.getZ() + 0.5F, drop
                              )
                           );
                        CrowEntity.this.level().setBlock(this.blockPos, (BlockState)blockstate.setValue(BlockStateProperties.AGE_3, 0), 2);
                     }

                     if (blockstate.hasProperty(BlockStateProperties.AGE_7)) {
                        CrowEntity.this.level()
                           .addFreshEntity(
                              new ItemEntity(
                                 CrowEntity.this.level(), this.blockPos.getX() + 0.5F, this.blockPos.getY() + 0.25F, this.blockPos.getZ() + 0.5F, drop
                              )
                           );
                        CrowEntity.this.level().setBlock(this.blockPos, (BlockState)blockstate.setValue(BlockStateProperties.AGE_7, 0), 2);
                     }

                     CrowEntity.this.peck();
                     CrowEntity.this.playSound(SoundEvents.CROP_BREAK, 1.0F, 1.0F);
                     HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
                  }
               }
            } else if (blockstate.is(HexereiTags.Blocks.CROW_BLOCK_HARVESTABLE)) {
               net.minecraft.world.level.storage.loot.LootParams.Builder lootparams = new net.minecraft.world.level.storage.loot.LootParams.Builder(
                     (ServerLevel)CrowEntity.this.level()
                  )
                  .withParameter(LootContextParams.ORIGIN, CrowEntity.this.position())
                  .withParameter(LootContextParams.TOOL, new ItemStack(Items.IRON_HOE));

               for (ItemStack stack : blockstate.getDrops(lootparams)) {
                  Block.popResource(CrowEntity.this.level(), this.blockPos, stack);
                  CrowEntity.this.playSound(blockstate.getSoundType().getBreakSound(), 1.0F, 1.0F);
                  HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
               }

               CrowEntity.this.level().setBlock(this.blockPos, Blocks.AIR.defaultBlockState(), 3);
            }
         }
      }

      private void pickGlowBerry(BlockState p_148927_) {
         CaveVines.use(CrowEntity.this, p_148927_, CrowEntity.this.level(), this.blockPos);
      }

      private void pickSweetBerries(BlockState p_148929_) {
         int i = (Integer)p_148929_.getValue(SweetBerryBushBlock.AGE);
         p_148929_.setValue(SweetBerryBushBlock.AGE, 1);
         int j = 1 + CrowEntity.this.random.nextInt(2) + (i == 3 ? 1 : 0);
         ItemStack itemstack = CrowEntity.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (itemstack.isEmpty()) {
            CrowEntity.this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
            j--;
         }

         if (j > 0) {
            Block.popResource(CrowEntity.this.level(), this.blockPos, new ItemStack(Items.SWEET_BERRIES, j));
         }

         CrowEntity.this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
         CrowEntity.this.level().setBlock(this.blockPos, (BlockState)p_148929_.setValue(SweetBerryBushBlock.AGE, 1), 2);
      }

      public boolean canUse() {
         this.searchRange = CrowEntity.this.interactionRange + 1;
         if (CrowEntity.this.doingTask) {
            return false;
         } else {
            if (CrowEntity.this.getPerchPos() != null) {
               double dist = CrowEntity.this.blockPosition()
                  .distToCenterSqr(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ());
               if (dist > CrowEntity.this.interactionRange * CrowEntity.this.interactionRange) {
                  return false;
               }
            }

            if (CrowEntity.this.getHelpCommand() == 1 && CrowEntity.this.getCommand() == 3 && CrowEntity.this.isInSittingPose()) {
               CrowEntity.this.setInSittingPose(false);
               CrowEntity.this.setOrderedToSit(false);
            }

            return !CrowEntity.this.isSleeping() && CrowEntity.this.getHelpCommand() == 1 && CrowEntity.this.getCommand() == 3 && super.canUse();
         }
      }

      public boolean canContinueToUse() {
         if (CrowEntity.this.getHelpCommand() == 1 && CrowEntity.this.getCommand() == 3 && CrowEntity.this.isInSittingPose()) {
            CrowEntity.this.setInSittingPose(false);
            CrowEntity.this.setOrderedToSit(false);
         }

         return super.canContinueToUse() && CrowEntity.this.getCommand() == 3 && CrowEntity.this.getHelpCommand() == 1;
      }

      public void start() {
         CrowEntity.this.doingTask = true;
         this.ticksWaited = 0;
         super.start();
      }

      public void stop() {
         if (CrowEntity.this.getCommandHelp() && CrowEntity.this.getHelpCommand() == 1) {
            CrowEntity.this.searchForNewCropTarget = true;
            CrowEntity.this.searchForNewCropTargetTimer = 40;
         }

         CrowEntity.this.doingTask = false;
      }
   }

   private static class CrowMoveController extends MoveControl {
      private final int maxTurn;

      public CrowMoveController(CrowEntity crow, int pMaxTurn) {
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
            return nodeevaluator.getPathType(
                  new PathfindingContext(this.mob.level(), this.mob),
                  Mth.floor(this.mob.getX() + pRelativeX),
                  this.mob.getBlockY(),
                  Mth.floor(this.mob.getZ() + pRelativeZ)
               )
               == PathType.WALKABLE;
         } else {
            return true;
         }
      }
   }

   private class CrowPickpocketVillager extends Goal {
      private final CrowEntity entity;
      protected final CrowEntity.CrowPickpocketVillager.Sorter theNearestAttackableTargetSorter;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private Villager targetEntity;
      private Vec3 flightTarget = null;
      private int tryTicks = 0;
      private int fleeTicks = 0;
      private static final int FLEE_MAX = 20;
      private boolean success = false;

      CrowPickpocketVillager(CrowEntity entity) {
         this.entity = entity;
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new CrowEntity.CrowPickpocketVillager.Sorter(CrowEntity.this);
      }

      public boolean canUse() {
         if (CrowEntity.this.doingTask) {
            return false;
         } else if (CrowEntity.this.pickpocketTimer > 0) {
            return false;
         } else if (!CrowEntity.this.isTame()) {
            return false;
         } else if (CrowEntity.this.isVehicle()) {
            return false;
         } else if (CrowEntity.this.isPassenger()) {
            return false;
         } else if (CrowEntity.this.getCommand() != 3) {
            return false;
         } else if (CrowEntity.this.getHelpCommand() != 2) {
            return false;
         } else {
            if (!this.mustUpdate) {
               long worldTime = CrowEntity.this.level().getGameTime() % 10L;
               if (CrowEntity.this.getNoActionTime() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (CrowEntity.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            if (this.entity.villagerList != null && !this.entity.villagerList.isEmpty()) {
               if (CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.getCommand() == 3 && CrowEntity.this.isInSittingPose()) {
                  CrowEntity.this.setInSittingPose(false);
                  CrowEntity.this.setOrderedToSit(false);
               }

               this.entity.villagerList.sort(this.theNearestAttackableTargetSorter);
               this.targetEntity = this.entity.villagerList.get(0);
               this.mustUpdate = false;
               if (!CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty()) {
                  CrowEntity.this.spawnAtLocation(CrowEntity.this.itemHandler.getStackInSlot(1).copy());
                  CrowEntity.this.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
               }

               return true;
            } else {
               return false;
            }
         }
      }

      public boolean canContinueToUse() {
         if (CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.getCommand() == 3 && CrowEntity.this.isInSittingPose()) {
            CrowEntity.this.setInSittingPose(false);
            CrowEntity.this.setOrderedToSit(false);
         }

         return !CrowEntity.this.itemHandler.getStackInSlot(1).isEmpty() && CrowEntity.this.pickpocketTimer > 0
            ? true
            : this.targetEntity != null && CrowEntity.this.getCommand() == 3 && CrowEntity.this.getHelpCommand() == 2;
      }

      public void start() {
         CrowEntity.this.doingTask = true;
         this.success = false;
      }

      public void stop() {
         CrowEntity.this.doingTask = false;
         this.flightTarget = null;
         this.targetEntity = null;
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            if (CrowEntity.this.pickpocketTimer <= 0) {
               if (this.targetEntity != null) {
                  if (CrowEntity.this.distanceToSqr(this.targetEntity.position().x, this.targetEntity.position().y + 0.75, this.targetEntity.position().z)
                     < this.entity.getMaxDistToItem() * 8.0) {
                     CrowEntity.this.flyOrWalkTo(this.targetEntity.position().add(0.0, 0.75, 0.0));
                     this.entity
                        .getNavigation()
                        .moveTo(
                           this.entity
                              .getNavigation()
                              .createPath(this.targetEntity.position().x, this.targetEntity.position().y + 0.75, this.targetEntity.position().z, 0),
                           CrowEntity.this.isFlyingNav() ? 2.5 : 2.0
                        );
                  } else {
                     CrowEntity.this.flyOrWalkTo(this.targetEntity.position().add(0.0, 1.75, 0.0));
                     this.entity
                        .getNavigation()
                        .moveTo(
                           this.entity
                              .getNavigation()
                              .createPath(this.targetEntity.position().x, this.targetEntity.position().y + 1.75, this.targetEntity.position().z, 0),
                           CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                        );
                  }

                  this.flightTarget = this.targetEntity.position();
                  this.tryTicks++;
                  if (this.shouldRecalculatePath()) {
                     if (this.targetEntity.position().distanceTo(CrowEntity.this.position()) < 3.0
                        && CrowEntity.this.position().y < this.targetEntity.position().y()) {
                        CrowEntity.this.setNoGravity(false);
                        CrowEntity.this.push(
                           (this.targetEntity.position().x - CrowEntity.this.position().x) / 50.0,
                           (this.targetEntity.position().y - CrowEntity.this.position().y) / 50.0 + 0.10000000149011612,
                           (this.targetEntity.position().z - CrowEntity.this.position().z) / 50.0
                        );
                     }

                     CrowEntity.this.walkToIfNotFlyTo(this.targetEntity.position().add(new Vec3(0.0, 3.0, 0.0)));
                     CrowEntity.this.getNavigation()
                        .moveTo(
                           this.entity.getNavigation().createPath(this.targetEntity.getX(), this.targetEntity.getY() + 3.0, this.targetEntity.getZ(), 0),
                           CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                        );
                  }

                  if (CrowEntity.this.distanceToSqr(this.targetEntity.position().x, this.targetEntity.position().y + 0.75, this.targetEntity.position().z)
                     < this.entity.getMaxDistToItem() * 2.0) {
                     this.entity.lookAt(this.targetEntity, 10.0F, this.entity.getMaxHeadXRot());
                     CrowEntity.this.peck();
                     HexereiPacketHandler.sendToNearbyClient(CrowEntity.this.level(), CrowEntity.this, new PeckPacket(CrowEntity.this));
                     CrowEntity.this.pickpocketTimer = (Integer)HexConfig.CROW_PICKPOCKET_COOLDOWN.get();
                     BlockPos pos = RandomPos.generateRandomDirection(this.entity.getRandom(), 4, 1);
                     this.flightTarget = new Vec3(
                        pos.getX() + CrowEntity.this.position().x, pos.getY() + CrowEntity.this.position().y + 2.0, pos.getZ() + CrowEntity.this.position().z
                     );
                     CrowEntity.this.walkToIfNotFlyTo(this.flightTarget);
                     this.entity
                        .getNavigation()
                        .moveTo(
                           this.entity.getNavigation().createPath(this.flightTarget.x, this.flightTarget.y + 0.75, this.flightTarget.z, 0),
                           CrowEntity.this.isFlyingNav() ? 1.5 : 1.0
                        );
                     net.minecraft.world.level.storage.loot.LootParams.Builder builder = new net.minecraft.world.level.storage.loot.LootParams.Builder(
                           (ServerLevel)CrowEntity.this.level()
                        )
                        .withParameter(LootContextParams.THIS_ENTITY, this.targetEntity);
                     LootParams ctx = builder.create(LootContextParamSet.builder().build());
                     NonNullList<ItemStack> stacks = NonNullList.create();
                     stacks.addAll(
                        CrowEntity.this.level()
                           .getServer()
                           .reloadableRegistries()
                           .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse("hexerei:entities/crow_pickpocket_villager")))
                           .getRandomItems(ctx)
                     );
                     if (!stacks.isEmpty()) {
                        CrowEntity.this.itemHandler.setStackInSlot(1, (ItemStack)stacks.get(0));
                     }

                     this.targetEntity.getBrain().setActiveActivityIfPossible(Activity.PANIC);
                     this.fleeTicks = 0;
                     this.success = true;
                     if (this.targetEntity.position().distanceTo(CrowEntity.this.position()) < 3.0
                        && CrowEntity.this.position().y < this.targetEntity.position().y()) {
                        CrowEntity.this.setNoGravity(false);
                        CrowEntity.this.push(0.0, 1.0, 0.0);
                     }
                  }
               }
            } else if (!this.success) {
            }

            if (this.success && this.fleeTicks < 20) {
               if (this.flightTarget != null) {
                  CrowEntity.this.walkToIfNotFlyTo(this.flightTarget);
                  this.entity
                     .getNavigation()
                     .moveTo(
                        this.entity.getNavigation().createPath(this.flightTarget.x, this.flightTarget.y + 0.75, this.flightTarget.z, 0),
                        CrowEntity.this.isFlyingNav() ? 2.5 : 2.0
                     );
                  if (CrowEntity.this.distanceToSqr(this.flightTarget.x, this.flightTarget.y + 0.75, this.flightTarget.z)
                     < this.entity.getMaxDistToItem() * 4.0) {
                     BlockPos posx = RandomPos.generateRandomDirection(this.entity.getRandom(), 4, 4);
                     this.flightTarget = new Vec3(
                        posx.getX() + CrowEntity.this.position().x, CrowEntity.this.position().y, posx.getZ() + CrowEntity.this.position().z
                     );
                  }
               }

               this.fleeTicks++;
               if (this.fleeTicks >= 20) {
                  CrowEntity.this.bringItemHome = true;
                  this.stop();
               }
            }
         }
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 20 == 0;
      }

      public class Sorter implements Comparator<Villager> {
         private final Entity crow;

         public Sorter(Entity theEntityIn) {
            this.crow = theEntityIn;
         }

         public int compare(Villager p_compare_1_, Villager p_compare_2_) {
            double d0 = this.crow.distanceToSqr(new Vec3(p_compare_1_.position().x, p_compare_1_.position().y, p_compare_1_.position().z));
            double d1 = this.crow.distanceToSqr(new Vec3(p_compare_2_.position().x, p_compare_2_.position().y, p_compare_2_.position().z));
            return Double.compare(d0, d1);
         }
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
         return this.mob.isInWater() && this.mob.getFluidTypeHeight((FluidType)NeoForgeMod.WATER_TYPE.value()) > this.mob.getFluidJumpThreshold()
            || this.mob.isInLava();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (this.mob.getRandom().nextFloat() < 0.8F) {
            this.mob.getJumpControl().jump();
            Vec3 randomPos = DefaultRandomPos.getPos(CrowEntity.this, 10, 7);
            if (randomPos == null) {
               randomPos = LandRandomPos.getPos(CrowEntity.this, 10, 7);
            }

            BlockPos pos;
            if (randomPos != null) {
               pos = new BlockPos((int)randomPos.x, (int)randomPos.y, (int)randomPos.z);
            } else if (CrowEntity.this.getPerchPos() != null) {
               pos = CrowEntity.this.getPerchPos().above().above();
            } else {
               pos = CrowEntity.this.blockPosition().above().above();
            }

            if (!CrowEntity.this.isInSittingPose() && !CrowEntity.this.getCommandSit()) {
               this.mob.push(0.0, 0.1, 0.0);
            }

            CrowEntity.this.flyOrWalkTo(pos.getCenter());
            CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), CrowEntity.this.isFlyingNav() ? 1.5 : 1.0);
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
         if (CrowEntity.this.getPerchPos() == null) {
            return false;
         } else if (CrowEntity.this.aiItemFlag) {
            return false;
         } else if (CrowEntity.this.depositItemBeforePerch) {
            return false;
         } else if (CrowEntity.this.getCommand() == 0) {
            return false;
         } else if (CrowEntity.this.getCommand() == 2) {
            return false;
         } else {
            if (CrowEntity.this.getCommand() == 3) {
               if (CrowEntity.this.distanceToSqr(
                     CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()
                  )
                  > 288.0) {
                  if (CrowEntity.this.navigation.getPath() != null) {
                     if (CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                     }

                     return true;
                  }

                  if (CrowEntity.this.navigation.isStuck()) {
                     if (CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                     }

                     return true;
                  }
               } else if (CrowEntity.this.navigation.getPath() == null) {
                  return true;
               }
            }

            double topOffset = 0.0;
            if (CrowEntity.this.getPerchPos() != null) {
               topOffset = CrowEntity.this.level()
                  .getBlockState(CrowEntity.this.getPerchPos())
                  .getOcclusionShape(CrowEntity.this.level(), CrowEntity.this.getPerchPos())
                  .max(Axis.Y);
            }

            if (this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1.0
               && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset
               && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset) {
               return false;
            } else {
               if (CrowEntity.this.isInSittingPose()) {
                  CrowEntity.this.setInSittingPose(false);
                  CrowEntity.this.setOrderedToSit(false);
               }

               return true;
            }
         }
      }

      public double distanceTo(double p_20276_, double p_20278_) {
         double d0 = CrowEntity.this.getX() - p_20276_ - 0.5;
         double d1 = CrowEntity.this.getZ() - p_20278_ - 0.5;
         return Mth.sqrt((float)(d0 * d0 + d1 * d1));
      }

      public boolean canUse() {
         if (CrowEntity.this.searchForNewCropTarget) {
            return false;
         } else if (CrowEntity.this.aiItemFlag) {
            return false;
         } else if (CrowEntity.this.doingTask) {
            return false;
         } else {
            if (CrowEntity.this.isInSittingPose()) {
               if (CrowEntity.this.getPerchPos() == null) {
                  return false;
               }

               if (CrowEntity.this.getPerchPos().distToCenterSqr(CrowEntity.this.position().x, CrowEntity.this.position().y, CrowEntity.this.position().z)
                  < 1.0) {
                  return false;
               }
            }

            if (CrowEntity.this.depositItemBeforePerch) {
               if (!CrowEntity.this.getItem(1).isEmpty()) {
                  return false;
               }

               CrowEntity.this.depositItemBeforePerch = false;
            }

            if (!this.mob.isTame()) {
               return false;
            } else if (this.mob.isInWaterOrBubble()) {
               return false;
            } else {
               LivingEntity livingentity = this.mob.getOwner();
               if (livingentity == null) {
                  return true;
               } else if (CrowEntity.this.getPerchPos() == null) {
                  return false;
               } else if (CrowEntity.this.getCommand() == 0) {
                  return false;
               } else if (CrowEntity.this.getCommand() == 2) {
                  return false;
               } else {
                  if (CrowEntity.this.getCommand() == 3) {
                     if (CrowEntity.this.distanceToSqr(
                           CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getY(), CrowEntity.this.getPerchPos().getZ()
                        )
                        > 288.0) {
                        if (CrowEntity.this.navigation.getPath() != null) {
                           if (CrowEntity.this.isInSittingPose()) {
                              CrowEntity.this.setInSittingPose(false);
                              CrowEntity.this.setOrderedToSit(false);
                           }

                           return true;
                        }

                        if (CrowEntity.this.navigation.isStuck()) {
                           if (CrowEntity.this.isInSittingPose()) {
                              CrowEntity.this.setInSittingPose(false);
                              CrowEntity.this.setOrderedToSit(false);
                           }

                           return true;
                        }
                     } else if (CrowEntity.this.navigation.getPath() == null) {
                        return true;
                     }
                  }

                  double topOffset = CrowEntity.this.level()
                     .getBlockState(CrowEntity.this.getPerchPos())
                     .getOcclusionShape(CrowEntity.this.level(), CrowEntity.this.getPerchPos())
                     .max(Axis.Y);
                  if (this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1.0
                     && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset
                     && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset) {
                     return CrowEntity.this.isInSittingPose()
                        ? false
                        : !(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1.0)
                           || !(this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset)
                           || !(this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset);
                  } else {
                     if (CrowEntity.this.isInSittingPose()) {
                        CrowEntity.this.setInSittingPose(false);
                        CrowEntity.this.setOrderedToSit(false);
                     }

                     return true;
                  }
               }
            }
         }
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            double topOffset = 0.0;
            if (CrowEntity.this.getPerchPos() != null) {
               topOffset = CrowEntity.this.level()
                  .getBlockState(CrowEntity.this.getPerchPos())
                  .getOcclusionShape(CrowEntity.this.level(), CrowEntity.this.getPerchPos())
                  .max(Axis.Y);
            }

            boolean isStuck = false;
            if (CrowEntity.this.stuckTimer++ > 80) {
               if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 2.25) {
                  isStuck = true;
                  CrowEntity.this.stuckTimer = 0;
               }

               CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
            }

            if (isStuck) {
               Vec3 randomPos = DefaultRandomPos.getPos(this.mob, 10, 7);
               BlockPos pos = null;
               if (CrowEntity.this.getPerchPos() != null) {
                  pos = CrowEntity.this.getPerchPos().above().above();
               } else {
                  pos = CrowEntity.this.blockPosition().above().above();
               }

               CrowEntity.this.navigation.stop();
               if (CrowEntity.this.distanceToSqr(CrowEntity.this.lastStuckCheckPos) < 0.1 && CrowEntity.this.onGround()) {
                  CrowEntity.this.push(
                     Math.min(0.20000000298023224, (pos.getX() - CrowEntity.this.position().x) / 20.0),
                     0.15000000596046448,
                     Math.min(0.20000000298023224, (pos.getZ() - CrowEntity.this.position().z) / 20.0)
                  );
                  CrowEntity.this.flyOrWalkTo(pos.getCenter());
                  CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(pos, 0), CrowEntity.this.isFlyingNav() ? 1.5 : 1.0);
               } else {
                  CrowEntity.this.flyOrWalkTo(CrowEntity.this.blockPosition().above().above().getCenter());
                  CrowEntity.this.navigation.moveTo(CrowEntity.this.getNavigation().createPath(CrowEntity.this.blockPosition().above().above(), 0), 1.5);
               }

               CrowEntity.this.stuckTimer = 0;
            } else if (CrowEntity.this.getPerchPos() != null
               && (
                  !(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1.0)
                     || !(this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset)
                     || !(this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset)
               )) {
               CrowEntity.this.flyOrWalkTo(CrowEntity.this.getPerchPos().above().getCenter());
               CrowEntity.this.navigation
                  .moveTo(this.mob.getNavigation().createPath(CrowEntity.this.getPerchPos().above(), -1), CrowEntity.this.isFlyingNav() ? 1.5 : 1.0);
            }

            super.tick();
         }
      }

      public void start() {
         CrowEntity.this.lastStuckCheckPos = CrowEntity.this.position();
         if (CrowEntity.this.getPerchPos() != null) {
            CrowEntity.this.flyOrWalkTo(CrowEntity.this.getPerchPos().above().getCenter());
            CrowEntity.this.navigation
               .moveTo(this.mob.getNavigation().createPath(CrowEntity.this.getPerchPos().above(), 0), CrowEntity.this.isFlyingNav() ? 1.5 : 1.0);
         }
      }

      public void stop() {
         CrowEntity.this.aiItemFlag = false;
         CrowEntity.this.doingTask = false;
         if (CrowEntity.this.getPerchPos() != null) {
            double topOffset = CrowEntity.this.level()
               .getBlockState(CrowEntity.this.getPerchPos())
               .getOcclusionShape(CrowEntity.this.level(), CrowEntity.this.getPerchPos())
               .max(Axis.Y);
            if (this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1.0
               && this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset
               && this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset) {
               CrowEntity.this.setInSittingPose(true);
            }
         }
      }
   }

   public class FollowOwnerGoal extends Goal {
      private final TamableAnimal tamable;
      private LivingEntity owner;
      private final LevelReader level;
      private final double speedModifier;
      private final PathNavigation navigation;
      private int timeToRecalcPath;
      private final float stopDistance;
      private final float startDistance;
      private float oldWaterCost;
      private final boolean canFly;

      public FollowOwnerGoal(TamableAnimal p_25294_, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_) {
         this.tamable = p_25294_;
         this.level = p_25294_.level();
         this.speedModifier = p_25295_;
         this.navigation = p_25294_.getNavigation();
         this.startDistance = p_25296_;
         this.stopDistance = p_25297_;
         this.canFly = p_25298_;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
         if (!(p_25294_.getNavigation() instanceof GroundPathNavigation) && !(p_25294_.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
         }
      }

      public boolean canUse() {
         LivingEntity livingentity = this.tamable.getOwner();
         if (livingentity == null) {
            return false;
         } else if (this.tamable.isPassenger()) {
            return false;
         } else if (livingentity.isSpectator()) {
            return false;
         } else if (this.tamable.isOrderedToSit() || CrowEntity.this.isInSittingPose()) {
            return false;
         } else if (((CrowEntity)this.tamable).getCommand() != 0) {
            return false;
         } else if (this.tamable.distanceToSqr(livingentity) < this.startDistance * this.startDistance) {
            return false;
         } else {
            if (CrowEntity.this.getCommand() == 0 && CrowEntity.this.isInSittingPose()) {
               CrowEntity.this.setInSittingPose(false);
               CrowEntity.this.setOrderedToSit(false);
            }

            this.owner = livingentity;
            return true;
         }
      }

      public boolean canContinueToUse() {
         if (this.navigation.isDone()) {
            return false;
         } else if (this.tamable.isOrderedToSit() || CrowEntity.this.isInSittingPose()) {
            return false;
         } else if (this.tamable.isPassenger()) {
            return false;
         } else {
            if (CrowEntity.this.getCommand() == 0 && CrowEntity.this.isInSittingPose()) {
               CrowEntity.this.setInSittingPose(false);
               CrowEntity.this.setOrderedToSit(false);
            }

            return !(this.tamable.distanceToSqr(this.owner) <= this.stopDistance * this.stopDistance);
         }
      }

      public void start() {
         this.timeToRecalcPath = 0;
         this.oldWaterCost = this.tamable.getPathfindingMalus(PathType.WATER);
         this.tamable.setPathfindingMalus(PathType.WATER, 0.0F);
      }

      public void stop() {
         CrowEntity.this.doingTask = false;
         this.owner = null;
         this.navigation.stop();
         this.tamable.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, this.tamable.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
               this.timeToRecalcPath = this.adjustedTickDelay(4);
               if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                  if (this.tamable.distanceToSqr(this.owner) >= 144.0) {
                     this.teleportToOwner();
                  } else {
                     CrowEntity.this.flyOrWalkTo(this.owner.position());
                     CrowEntity.this.getNavigation()
                        .moveTo(
                           this.owner.position().x,
                           this.owner.position().y,
                           this.owner.position().z,
                           CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier
                        );
                  }
               }
            }
         }
      }

      private void teleportToOwner() {
         BlockPos blockpos = this.owner.blockPosition();

         for (int i = 0; i < 10; i++) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
               return;
            }
         }
      }

      private boolean maybeTeleportTo(int p_25304_, int p_25305_, int p_25306_) {
         if (Math.abs(p_25304_ - this.owner.getX()) < 2.0 && Math.abs(p_25306_ - this.owner.getZ()) < 2.0) {
            return false;
         } else if (!this.canTeleportTo(new BlockPos(p_25304_, p_25305_, p_25306_))) {
            return false;
         } else {
            CrowEntity.this.flyOrWalkTo(new Vec3(p_25304_ + 0.5, p_25305_, p_25306_ + 0.5));
            this.tamable.moveTo(p_25304_ + 0.5, p_25305_, p_25306_ + 0.5, this.tamable.getYRot(), this.tamable.getXRot());
            this.navigation.stop();
            return true;
         }
      }

      private boolean canTeleportTo(BlockPos p_25308_) {
         PathType blockpathtypes = WalkNodeEvaluator.getPathTypeStatic(this.tamable, p_25308_.mutable());
         BlockState blockstate = this.level.getBlockState(p_25308_.below());
         if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos blockpos = p_25308_.subtract(this.tamable.blockPosition());
            return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockpos));
         }
      }

      private int randomIntInclusive(int p_25301_, int p_25302_) {
         return this.tamable.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
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
         } else if (!CrowEntity.this.isOrderedToSit() && !CrowEntity.this.isInSittingPose()) {
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
         } else if (!CrowEntity.this.isOrderedToSit() && !CrowEntity.this.isInSittingPose()) {
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
         if (!CrowEntity.this.isPlayingDead()) {
            if (--this.timeToRecalcPath <= 0) {
               this.timeToRecalcPath = this.adjustedTickDelay(10);
               if (this.parent != null) {
                  CrowEntity.this.walkToIfNotFlyTo(this.parent.position());
               }

               this.animal.getNavigation().moveTo(this.parent, CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
            }
         }
      }
   }

   public class LandOnOwnersShoulderGoal extends Goal {
      private final CrowEntity entity;
      private ServerPlayer owner;
      private boolean isSittingOnShoulder;

      public LandOnOwnersShoulderGoal(CrowEntity p_25483_) {
         this.entity = p_25483_;
      }

      public boolean canContinueToUse() {
         if (CrowEntity.this.isPassenger()) {
            return false;
         } else {
            if (CrowEntity.this.getCommand() == 0 && CrowEntity.this.isInSittingPose()) {
               CrowEntity.this.setInSittingPose(false);
               CrowEntity.this.setOrderedToSit(false);
            }

            return super.canContinueToUse();
         }
      }

      public boolean canUse() {
         if (CrowEntity.this.isBaby()) {
            return false;
         } else if (CrowEntity.this.isPassenger()) {
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
               && this.entity.getCommand() == 0
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
         if (!CrowEntity.this.isPlayingDead()) {
            if (!this.isSittingOnShoulder
               && !this.entity.isInSittingPose()
               && !this.entity.isLeashed()
               && this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
               this.isSittingOnShoulder = this.entity.startRiding(this.owner, true);
               if (!CrowEntity.this.level().isClientSide) {
                  HexereiPacketHandler.sendToNearbyClient(this.entity.level(), this.entity, new StartRidingPacket(this.entity, this.owner));
               }
            }

            if (this.isSittingOnShoulder) {
               this.entity.rideCooldownCounter = 0;
            }
         }
      }
   }

   public class MeleeAttackGoal extends Goal {
      protected final PathfinderMob mob;
      private final double speedModifier;
      private final boolean followingTargetEvenIfNotSeen;
      private Path path;
      private double pathedTargetX;
      private double pathedTargetY;
      private double pathedTargetZ;
      private int ticksUntilNextPathRecalculation;
      private int ticksUntilNextAttack;
      private final int attackInterval = 20;
      private long lastCanUseCheck;
      private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
      private int failedPathFindingPenalty = 0;
      private boolean canPenalize = false;

      public MeleeAttackGoal(PathfinderMob p_25552_, double p_25553_, boolean p_25554_) {
         this.mob = p_25552_;
         this.speedModifier = p_25553_;
         this.followingTargetEvenIfNotSeen = p_25554_;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (!CrowEntity.this.canAttack) {
            return false;
         } else {
            long i = this.mob.level().getGameTime();
            if (i - this.lastCanUseCheck < 20L) {
               return false;
            } else {
               this.lastCanUseCheck = i;
               LivingEntity livingentity = this.mob.getTarget();
               if (livingentity == null) {
                  return false;
               } else if (!livingentity.isAlive()) {
                  return false;
               } else if (this.canPenalize) {
                  if (--this.ticksUntilNextPathRecalculation <= 0) {
                     this.path = this.mob.getNavigation().createPath(livingentity, 0);
                     this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                     return this.path != null;
                  } else {
                     return true;
                  }
               } else {
                  if (CrowEntity.this.getCommand() != 1 && CrowEntity.this.isInSittingPose()) {
                     CrowEntity.this.setInSittingPose(false);
                     CrowEntity.this.setOrderedToSit(false);
                  }

                  this.path = this.mob.getNavigation().createPath(livingentity, 0);
                  return this.path != null
                     ? true
                     : this.getAttackReachSqr(livingentity) >= this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
               }
            }
         }
      }

      public boolean canContinueToUse() {
         LivingEntity livingentity = this.mob.getTarget();
         if (livingentity == null) {
            return false;
         } else if (!livingentity.isAlive()) {
            return false;
         } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
         } else if (!this.mob.isWithinRestriction(livingentity.blockPosition())) {
            return false;
         } else {
            if (CrowEntity.this.getCommand() != 1 && CrowEntity.this.isInSittingPose()) {
               CrowEntity.this.setInSittingPose(false);
               CrowEntity.this.setOrderedToSit(false);
            }

            return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player)livingentity).isCreative();
         }
      }

      public void start() {
         CrowEntity.this.switchNavigator(true);
         this.mob.getNavigation().moveTo(this.path, CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
         this.mob.setAggressive(true);
         this.ticksUntilNextPathRecalculation = 0;
         this.ticksUntilNextAttack = 0;
      }

      public void stop() {
         LivingEntity livingentity = this.mob.getTarget();
         if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
            this.mob.setTarget(null);
         }

         this.mob.setAggressive(false);
         this.mob.getNavigation().stop();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null) {
               this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
               double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
               double d1 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY() + livingentity.getBbHeight(), livingentity.getZ());
               this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
               if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingentity))
                  && this.ticksUntilNextPathRecalculation <= 0
                  && (
                     this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0
                        || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0
                        || this.mob.getRandom().nextFloat() < 0.05F
                  )) {
                  this.pathedTargetX = livingentity.getX();
                  this.pathedTargetY = livingentity.getY();
                  this.pathedTargetZ = livingentity.getZ();
                  this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                  if (this.canPenalize) {
                     this.ticksUntilNextPathRecalculation = this.ticksUntilNextPathRecalculation + this.failedPathFindingPenalty;
                     if (this.mob.getNavigation().getPath() != null) {
                        Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1.0) {
                           this.failedPathFindingPenalty = 0;
                        } else {
                           this.failedPathFindingPenalty += 10;
                        }
                     } else {
                        this.failedPathFindingPenalty += 10;
                     }
                  }

                  if (d0 > 1024.0) {
                     this.ticksUntilNextPathRecalculation += 10;
                  } else if (d0 > 256.0) {
                     this.ticksUntilNextPathRecalculation += 5;
                  }

                  CrowEntity.this.switchNavigator(true);
                  if (!this.mob
                     .getNavigation()
                     .moveTo(
                        CrowEntity.this.getNavigation().createPath(livingentity, 0),
                        CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier
                     )) {
                     this.ticksUntilNextPathRecalculation += 15;
                  }

                  this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
               }

               this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
               this.checkAndPerformAttack(livingentity, d0, d1);
            }
         }
      }

      protected void checkAndPerformAttack(LivingEntity p_25557_, double p_25558_, double p_25559_) {
         double d0 = this.getAttackReachSqr(p_25557_);
         if ((p_25558_ <= d0 || p_25559_ <= d0) && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.doHurtTarget(p_25557_);
            CrowEntity.this.peck();
            HexereiPacketHandler.sendToNearbyClient(this.mob.level(), this.mob, new PeckPacket(this.mob));
         }
      }

      public boolean doHurtTarget(Entity entity) {
         float f = (float)CrowEntity.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
         float f1 = (float)CrowEntity.this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
         DamageSource damagesource = this.mob.damageSources().mobAttack(this.mob);
         if (this.mob.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, CrowEntity.this.itemHandler.getStackInSlot(1), entity, damagesource, f);
         }

         boolean flag = entity.hurt(damagesource, f);
         if (flag) {
            if (f1 > 0.0F && entity instanceof LivingEntity) {
               ((LivingEntity)entity)
                  .knockback(f1 * 0.5F, Mth.sin(CrowEntity.this.getYRot() * 0.017453292F), -Mth.cos(CrowEntity.this.getYRot() * 0.017453292F));
               CrowEntity.this.setDeltaMovement(CrowEntity.this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (this.mob.level() instanceof ServerLevel serverlevel1) {
               EnchantmentHelper.doPostAttackEffects(serverlevel1, entity, damagesource);
            }

            CrowEntity.this.setLastHurtMob(entity);
            CrowEntity.this.playAttackSound();
         }

         return flag;
      }

      protected void resetAttackCooldown() {
         this.ticksUntilNextAttack = this.adjustedTickDelay(20);
      }

      protected boolean isTimeToAttack() {
         return this.ticksUntilNextAttack <= 0;
      }

      protected int getTicksUntilNextAttack() {
         return this.ticksUntilNextAttack;
      }

      protected int getAttackInterval() {
         return this.adjustedTickDelay(20);
      }

      protected double getAttackReachSqr(LivingEntity p_25556_) {
         return this.mob.getBbWidth() * 2.5F * this.mob.getBbWidth() * 2.5F + p_25556_.getBbWidth();
      }
   }

   public class OwnerHurtByTargetGoal extends TargetGoal {
      private final TamableAnimal tameAnimal;
      private LivingEntity ownerLastHurtBy;
      private int timestamp;

      public OwnerHurtByTargetGoal(TamableAnimal p_26107_) {
         super(p_26107_, false);
         this.tameAnimal = p_26107_;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      protected boolean canAttack(@org.jetbrains.annotations.Nullable LivingEntity entity, TargetingConditions p_26152_) {
         return entity instanceof TamableAnimal && CrowEntity.this.getOwner() != null && ((TamableAnimal)entity).isOwnedBy(CrowEntity.this.getOwner())
            ? false
            : super.canAttack(entity, p_26152_);
      }

      public boolean canUse() {
         if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit() && !CrowEntity.this.getCommandSit()) {
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity == null) {
               return false;
            } else {
               if (CrowEntity.this.isInSittingPose()) {
                  CrowEntity.this.setInSittingPose(false);
                  CrowEntity.this.setOrderedToSit(false);
               }

               this.ownerLastHurtBy = livingentity.getLastHurtByMob();
               int i = livingentity.getLastHurtByMobTimestamp();
               return i != this.timestamp
                  && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT)
                  && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingentity);
            }
         } else {
            return false;
         }
      }

      public void start() {
         this.mob.setTarget(this.ownerLastHurtBy);
         LivingEntity livingentity = this.tameAnimal.getOwner();
         if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
         }

         super.start();
      }
   }

   public class OwnerHurtTargetGoal extends TargetGoal {
      private final TamableAnimal tameAnimal;
      private LivingEntity ownerLastHurt;
      private int timestamp;

      public OwnerHurtTargetGoal(TamableAnimal p_26114_) {
         super(p_26114_, false);
         this.tameAnimal = p_26114_;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      protected boolean canAttack(@org.jetbrains.annotations.Nullable LivingEntity entity, TargetingConditions p_26152_) {
         return entity instanceof TamableAnimal && CrowEntity.this.getOwner() != null && ((TamableAnimal)entity).isOwnedBy(CrowEntity.this.getOwner())
            ? false
            : super.canAttack(entity, p_26152_);
      }

      public boolean canUse() {
         if (this.tameAnimal.isTame() && !this.tameAnimal.isOrderedToSit() && !CrowEntity.this.getCommandSit()) {
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity == null) {
               return false;
            } else {
               if (CrowEntity.this.isInSittingPose()) {
                  CrowEntity.this.setInSittingPose(false);
                  CrowEntity.this.setOrderedToSit(false);
               }

               this.ownerLastHurt = livingentity.getLastHurtMob();
               int i = livingentity.getLastHurtMobTimestamp();
               return i != this.timestamp
                  && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT)
                  && this.tameAnimal.wantsToAttack(this.ownerLastHurt, livingentity);
            }
         } else {
            return false;
         }
      }

      public void start() {
         this.mob.setTarget(this.ownerLastHurt);
         LivingEntity livingentity = this.tameAnimal.getOwner();
         if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
         }

         super.start();
      }
   }

   public class SitWhenOrderedToGoal extends Goal {
      private final TamableAnimal mob;

      public SitWhenOrderedToGoal(TamableAnimal p_25898_) {
         this.mob = p_25898_;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public double distanceTo(double p_20276_, double p_20278_) {
         double d0 = CrowEntity.this.getX() - p_20276_ - 0.5;
         double d1 = CrowEntity.this.getZ() - p_20278_ - 0.5;
         return Mth.sqrt((float)(d0 * d0 + d1 * d1));
      }

      public boolean canContinueToUse() {
         if (CrowEntity.this.getPerchPos() != null) {
            double topOffset = CrowEntity.this.level()
               .getBlockState(CrowEntity.this.getPerchPos())
               .getOcclusionShape(CrowEntity.this.level(), CrowEntity.this.getPerchPos())
               .max(Axis.Y);
            if (!(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1.0)
               || !(this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset)
               || !(this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset)) {
               CrowEntity.this.setOrderedToSit(false);
               return false;
            }
         }

         return this.mob.isOrderedToSit();
      }

      public boolean canUse() {
         if (CrowEntity.this.doingTask) {
            return false;
         } else if (!this.mob.isTame()) {
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
               if (CrowEntity.this.getPerchPos() != null) {
                  double topOffset = CrowEntity.this.level()
                     .getBlockState(CrowEntity.this.getPerchPos())
                     .getOcclusionShape(CrowEntity.this.level(), CrowEntity.this.getPerchPos())
                     .max(Axis.Y);
                  if (!(this.distanceTo(CrowEntity.this.getPerchPos().getX(), CrowEntity.this.getPerchPos().getZ()) < 1.0)
                     || !(this.mob.position().y() >= CrowEntity.this.getPerchPos().getY() + topOffset)
                     || !(this.mob.position().y() < CrowEntity.this.getPerchPos().above().getY() + topOffset)) {
                     return false;
                  }
               }

               return (!(this.mob.distanceToSqr(livingentity) < 288.0) || livingentity.getLastHurtByMob() == null) && this.mob.isOrderedToSit();
            }
         }
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            super.tick();
         }
      }

      public void start() {
         this.mob.getNavigation().stop();
         this.mob.setInSittingPose(true);
      }

      public void stop() {
         this.mob.setInSittingPose(false);
      }
   }

   public class TemptGoal extends Goal {
      private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();
      private final TargetingConditions targetingConditions;
      protected final PathfinderMob mob;
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

      public TemptGoal(PathfinderMob p_25939_, double p_25940_, Ingredient p_25941_, boolean p_25942_) {
         this.mob = p_25939_;
         this.speedModifier = p_25940_;
         this.items = p_25941_;
         this.canScare = p_25942_;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
         this.targetingConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
      }

      public boolean canUse() {
         if (CrowEntity.this.getCommandSit() && CrowEntity.this.isInSittingPose()) {
            return false;
         } else if (this.calmDown > 0) {
            this.calmDown--;
            return false;
         } else {
            if (CrowEntity.this.isInSittingPose()) {
               CrowEntity.this.setInSittingPose(false);
               CrowEntity.this.setOrderedToSit(false);
            }

            this.player = this.mob.level().getNearestPlayer(this.targetingConditions, this.mob);
            return this.player != null;
         }
      }

      private boolean shouldFollow(LivingEntity p_148139_) {
         return this.items.test(p_148139_.getMainHandItem()) || this.items.test(p_148139_.getOffhandItem());
      }

      public boolean canContinueToUse() {
         if (!CrowEntity.this.getCommandSit() && !CrowEntity.this.isInSittingPose()) {
            if (this.canScare()) {
               if (this.mob.distanceToSqr(this.player) < 36.0) {
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
         CrowEntity.this.doingTask = false;
         this.player = null;
         this.mob.getNavigation().stop();
         this.calmDown = reducedTickDelay(100);
         this.isRunning = false;
      }

      public void tick() {
         if (!CrowEntity.this.isPlayingDead()) {
            this.mob.getLookControl().setLookAt(this.player, this.mob.getMaxHeadYRot() + 20, this.mob.getMaxHeadXRot());
            if (this.mob.distanceToSqr(this.player) < 6.25) {
               this.mob.getNavigation().stop();
            } else {
               if (CrowEntity.this.random.nextInt(reducedTickDelay(2)) == 0) {
                  CrowEntity.this.walkToIfNotFlyTo(this.player.position());
               }

               this.mob.getNavigation().moveTo(this.player, CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
            }
         }
      }

      public boolean isRunning() {
         return this.isRunning;
      }
   }

   public class WanderAroundPlayerGoal extends RandomStrollGoal {
      public WanderAroundPlayerGoal(PathfinderMob pMob, double pSpeedModifier) {
         super(pMob, pSpeedModifier, 5);
      }

      public void start() {
         CrowEntity.this.flyOrWalkTo(new Vec3(this.wantedX, this.wantedY, this.wantedZ));
         this.mob
            .getNavigation()
            .moveTo(this.wantedX, this.wantedY, this.wantedZ, CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
      }

      public boolean canUse() {
         if (CrowEntity.this.doingTask) {
            return false;
         } else if (CrowEntity.this.getCommand() != 2) {
            return false;
         } else if (this.mob.isVehicle()) {
            return false;
         } else {
            if (!this.forceTrigger) {
               int rtd = reducedTickDelay(this.interval);
               int next = this.mob.getRandom().nextInt(rtd);
               if (next != 0) {
                  return false;
               }
            }

            Vec3 vec3 = this.getPosition();
            if (vec3 == null) {
               return false;
            } else {
               this.wantedX = vec3.x;
               this.wantedY = vec3.y;
               this.wantedZ = vec3.z;
               this.forceTrigger = false;
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         if (CrowEntity.this.doingTask) {
            return false;
         } else {
            return CrowEntity.this.getCommand() != 2 ? false : super.canContinueToUse();
         }
      }

      protected Vec3 getPosition() {
         int pRadius = 5;
         int pVerticalDistance = 7;
         if (CrowEntity.this.getOwner() != null) {
            boolean flag = GoalUtils.mobRestricted(this.mob, pRadius);
            double d0 = -1.0 / 0.0;
            BlockPos blockpos = null;

            for (int i = 0; i < 10; i++) {
               BlockPos pos = RandomPos.generateRandomDirection(CrowEntity.this.getRandom(), pRadius, pVerticalDistance);
               BlockPos blockpos1;
               if (CrowEntity.this.getPerchPos() != null) {
                  blockpos1 = generateRandomPosTowardDirection(Vec3.atLowerCornerOf(CrowEntity.this.getPerchPos()), this.mob, flag, pos);
               } else if (CrowEntity.this.getOwner() != null) {
                  blockpos1 = generateRandomPosTowardDirection(CrowEntity.this.getOwner().position(), this.mob, flag, pos);
               } else {
                  blockpos1 = CrowEntity.this.blockPosition();
               }

               if (blockpos1 != null) {
                  double d1 = CrowEntity.this.getWalkTargetValue(blockpos1);
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

   public class WaterAvoidingRandomFlyingGoal extends CrowEntity.WaterAvoidingRandomStrollGoal {
      public WaterAvoidingRandomFlyingGoal(PathfinderMob p_25981_, double p_25982_) {
         super(p_25981_, p_25982_);
      }

      @Override
      public void start() {
         CrowEntity.this.switchNavigator(true);
         this.mob
            .getNavigation()
            .moveTo(this.wantedX, this.wantedY, this.wantedZ, CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
      }

      @Override
      public boolean canUse() {
         if (CrowEntity.this.doingTask) {
            return false;
         } else if (CrowEntity.this.isInSittingPose() || CrowEntity.this.isOrderedToSit()) {
            return false;
         } else if (CrowEntity.this.getCommandWander()) {
            return false;
         } else {
            return CrowEntity.this.getCommand() != 3 || CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.pickpocketTimer < 0 ? super.canUse() : false;
         }
      }

      @Override
      public boolean canContinueToUse() {
         if (!CrowEntity.this.isInSittingPose() && !CrowEntity.this.isOrderedToSit()) {
            return CrowEntity.this.getCommand() != 3 || CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.pickpocketTimer < 0
               ? super.canContinueToUse()
               : false;
         } else {
            return false;
         }
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
         CrowEntity.this.walkToIfNotFlyTo(new Vec3(this.wantedX, this.wantedY, this.wantedZ));
         this.mob
            .getNavigation()
            .moveTo(this.wantedX, this.wantedY, this.wantedZ, CrowEntity.this.isFlyingNav() ? 1.25 * this.speedModifier : 0.75 * this.speedModifier);
      }

      public boolean canUse() {
         if (CrowEntity.this.doingTask) {
            return false;
         } else if (!CrowEntity.this.isInSittingPose() && !CrowEntity.this.isOrderedToSit()) {
            if (CrowEntity.this.getCommand() != 3 || CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.pickpocketTimer < 0) {
               return CrowEntity.this.getCommandWander() ? false : super.canUse();
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         if (!CrowEntity.this.isInSittingPose() && !CrowEntity.this.isOrderedToSit()) {
            return CrowEntity.this.getCommand() != 3 || CrowEntity.this.getHelpCommand() == 2 && CrowEntity.this.pickpocketTimer < 0
               ? super.canContinueToUse()
               : false;
         } else {
            return false;
         }
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
