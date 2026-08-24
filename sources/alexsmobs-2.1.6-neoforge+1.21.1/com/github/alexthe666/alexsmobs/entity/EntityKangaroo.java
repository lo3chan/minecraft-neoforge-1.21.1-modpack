package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRideParent;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.KangarooAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.message.MessageKangarooEat;
import com.github.alexthe666.alexsmobs.message.MessageKangarooInventorySync;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

public class EntityKangaroo extends TamableAnimal implements ContainerListener, IAnimatedEntity, IFollower {
   public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
   public static final Animation ANIMATION_KICK = Animation.create(15);
   public static final Animation ANIMATION_PUNCH_R = Animation.create(13);
   public static final Animation ANIMATION_PUNCH_L = Animation.create(13);
   public static final Animation ANIMATION_STAB_R = Animation.create(22);
   public static final Animation ANIMATION_STAB_L = Animation.create(22);
   public static final int STAB_HIT_TICK = 13;
   private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> VISUAL_FLAG = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> POUCH_TICK = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HELMET_INDEX = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SWORD_INDEX = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> CHEST_INDEX = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> FORCED_SIT = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.BOOLEAN);
   public float prevPouchProgress;
   public float pouchProgress;
   public float sitProgress;
   public float prevSitProgress;
   public float standProgress;
   public float prevStandProgress;
   public float totalMovingProgress;
   public float prevTotalMovingProgress;
   public int maxStandTime = 75;
   public SimpleContainer kangarooInventory;
   private int animationTick;
   private Animation currentAnimation;
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int currentMoveTypeDuration;
   private int standingTime = 0;
   private int sittingTime = 0;
   private int maxSitTime = 75;
   private int eatCooldown = 0;
   private int carrotFeedings = 0;
   private int clientArmorCooldown = 0;

   protected EntityKangaroo(EntityType type, Level world) {
      super(type, world);
      this.initKangarooInventory();
      this.jumpControl = new EntityKangaroo.JumpHelperController(this);
      this.moveControl = new EntityKangaroo.MoveHelperController(this);
   }

   public static <T extends Mob> boolean canKangarooSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.KANGAROO_SPAWNS);
      return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8;
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 22.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 0.5)
         .add(Attributes.ATTACK_DAMAGE, 4.0);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return null;
   }

   public boolean handleLeashAtDistance(Entity holder, float distance) {
      this.amTickLeash();
      return false;
   }

   private void amTickLeash() {
      Entity lvt_1_1_ = this.getLeashHolder();
      if (lvt_1_1_ != null && lvt_1_1_.level() == this.level()) {
         this.restrictTo(lvt_1_1_.blockPosition(), 5);
         float lvt_2_1_ = this.distanceTo(lvt_1_1_);
         if (this.isSitting()) {
            if (lvt_2_1_ > 10.0F) {
               this.dropLeash(true, true);
            }

            return;
         }

         if (lvt_2_1_ > 10.0F) {
            this.dropLeash(true, true);
            this.goalSelector.disableControlFlag(Flag.MOVE);
         } else if (lvt_2_1_ > 6.0F) {
            double lvt_3_1_ = (lvt_1_1_.getX() - this.getX()) / lvt_2_1_;
            double lvt_5_1_ = (lvt_1_1_.getY() - this.getY()) / lvt_2_1_;
            double lvt_7_1_ = (lvt_1_1_.getZ() - this.getZ()) / lvt_2_1_;
            this.setDeltaMovement(
               this.getDeltaMovement()
                  .add(
                     Math.copySign(lvt_3_1_ * lvt_3_1_ * 0.4, lvt_3_1_),
                     Math.copySign(lvt_5_1_ * lvt_5_1_ * 0.4, lvt_5_1_),
                     Math.copySign(lvt_7_1_ * lvt_7_1_ * 0.4, lvt_7_1_)
                  )
            );
         } else {
            this.goalSelector.enableControlFlag(Flag.MOVE);
            float lvt_3_2_ = 2.0F;

            try {
               Vec3 lvt_4_1_ = new Vec3(lvt_1_1_.getX() - this.getX(), lvt_1_1_.getY() - this.getY(), lvt_1_1_.getZ() - this.getZ())
                  .normalize()
                  .scale(Math.max(lvt_2_1_ - 2.0F, 0.0F));
               this.getNavigation().moveTo(this.getX() + lvt_4_1_.x, this.getY() + lvt_4_1_.y, this.getZ() + lvt_4_1_.z, this.followLeashSpeed());
            } catch (Exception var9) {
            }
         }
      }
   }

   public boolean forcedSit() {
      return (Boolean)this.entityData.get(FORCED_SIT);
   }

   public boolean isRoger() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && s.equalsIgnoreCase("roger");
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.kangarooSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.KANGAROO_IDLE.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.KANGAROO_IDLE.get();
   }

   private void initKangarooInventory() {
      SimpleContainer animalchest = this.kangarooInventory;
      this.kangarooInventory = new SimpleContainer(9) {
         public void stopOpen(Player player) {
            EntityKangaroo.this.entityData.set(EntityKangaroo.POUCH_TICK, 10);
            EntityKangaroo.this.resetKangarooSlots();
         }

         public boolean stillValid(Player player) {
            return EntityKangaroo.this.isAlive() && EntityKangaroo.this.portalProcess == null;
         }
      };
      this.kangarooInventory.addListener(this);
      if (animalchest != null) {
         int i = Math.min(animalchest.getContainerSize(), this.kangarooInventory.getContainerSize());

         for (int j = 0; j < i; j++) {
            ItemStack itemstack = animalchest.getItem(j);
            if (!itemstack.isEmpty()) {
               this.kangarooInventory.setItem(j, itemstack.copy());
            }
         }

         this.resetKangarooSlots();
      }
   }

   protected void dropEquipment() {
      super.dropEquipment();

      for (int i = 0; i < this.kangarooInventory.getContainerSize(); i++) {
         AMCompat.spawnAtLocation(this, this.kangarooInventory.getItem(i));
      }

      this.kangarooInventory.clearContent();
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.isTame() && itemstack.is(AMTagRegistry.KANGAROO_TAMEABLES)) {
         this.usePlayerItem(player, hand, itemstack);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.HORSE_EAT, this.getSoundVolume(), this.getVoicePitch());
         this.carrotFeedings++;
         if ((this.carrotFeedings <= 10 || this.getRandom().nextInt(2) != 0) && this.carrotFeedings <= 15) {
            this.level().broadcastEntityEvent(this, (byte)6);
         } else {
            this.tame(player);
            this.level().broadcastEntityEvent(this, (byte)7);
         }

         return InteractionResult.SUCCESS;
      } else if (this.isTame()
         && this.getHealth() < this.getMaxHealth()
         && AMCompat.isEdible(item)
         && AMCompat.getFoodProperties(item) != null
         && !AMCompat.isMeat(item)) {
         this.usePlayerItem(player, hand, itemstack);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.HORSE_EAT, this.getSoundVolume(), this.getVoicePitch());
         this.heal(AMCompat.nutrition(AMCompat.getFoodProperties(item)));
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult == InteractionResult.SUCCESS
            || type == InteractionResult.SUCCESS
            || !this.isTame()
            || !this.isOwnedBy(player)
            || this.isFood(itemstack)) {
            return type;
         } else if (player.isShiftKeyDown()) {
            if (!this.isBaby()) {
               this.openGUI(player);
               this.ejectPassengers();
               this.entityData.set(POUCH_TICK, -1);
            }

            return InteractionResult.SUCCESS;
         } else {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
               this.setCommand(0);
            }

            player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
               this.entityData.set(FORCED_SIT, true);
               this.setOrderedToSit(true);
               return InteractionResult.SUCCESS;
            } else {
               this.entityData.set(FORCED_SIT, false);
               this.maxSitTime = 0;
               this.setOrderedToSit(false);
               return InteractionResult.SUCCESS;
            }
         }
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("KangarooSitting", this.isSitting());
      compound.putBoolean("KangarooSittingForced", this.forcedSit());
      compound.putBoolean("Standing", this.isStanding());
      compound.putInt("Command", this.getCommand());
      compound.putInt("HelmetInvIndex", (Integer)this.entityData.get(HELMET_INDEX));
      compound.putInt("SwordInvIndex", (Integer)this.entityData.get(SWORD_INDEX));
      compound.putInt("ChestInvIndex", (Integer)this.entityData.get(CHEST_INDEX));
      if (this.kangarooInventory != null) {
         ListTag nbttaglist = new ListTag();

         for (int i = 0; i < this.kangarooInventory.getContainerSize(); i++) {
            ItemStack itemstack = this.kangarooInventory.getItem(i);
            if (!itemstack.isEmpty()) {
               CompoundTag CompoundNBT = new CompoundTag();
               CompoundNBT.putByte("Slot", (byte)i);
               AMCompat.saveInto(this.level().registryAccess(), itemstack, CompoundNBT);
               nbttaglist.add(CompoundNBT);
            }
         }

         AMCompat.put(compound, "Items", nbttaglist);
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setOrderedToSit(AMCompat.getBoolean(compound, "KangarooSitting"));
      this.entityData.set(FORCED_SIT, AMCompat.getBoolean(compound, "KangarooSittingForced"));
      this.setStanding(AMCompat.getBoolean(compound, "Standing"));
      this.setCommand(AMCompat.getInt(compound, "Command"));
      this.entityData.set(HELMET_INDEX, AMCompat.getInt(compound, "HelmetInvIndex"));
      this.entityData.set(SWORD_INDEX, AMCompat.getInt(compound, "SwordInvIndex"));
      this.entityData.set(CHEST_INDEX, AMCompat.getInt(compound, "ChestInvIndex"));
      if (this.kangarooInventory != null) {
         ListTag nbttaglist = AMCompat.getList(compound, "Items", 10);
         this.initKangarooInventory();

         for (int i = 0; i < nbttaglist.size(); i++) {
            CompoundTag CompoundNBT = AMCompat.getCompound(nbttaglist, i);
            int j = AMCompat.getByte(CompoundNBT, "Slot") & 255;
            this.kangarooInventory.setItem(j, AMCompat.loadItem(this.level().registryAccess(), CompoundNBT));
         }
      } else {
         ListTag nbttaglist = AMCompat.getList(compound, "Items", 10);
         this.initKangarooInventory();

         for (int i = 0; i < nbttaglist.size(); i++) {
            CompoundTag CompoundNBT = AMCompat.getCompound(nbttaglist, i);
            int j = AMCompat.getByte(CompoundNBT, "Slot") & 255;
            this.initKangarooInventory();
            this.kangarooInventory.setItem(j, AMCompat.loadItem(this.level().registryAccess(), CompoundNBT));
         }
      }

      this.resetKangarooSlots();
   }

   public void openGUI(Player playerEntity) {
      if (!this.level().isClientSide() && !this.hasPassenger(playerEntity)) {
         ((ServerPlayer)playerEntity).openMenu(new MenuProvider() {
            public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
               return new DispenserMenu(p_createMenu_1_, p_createMenu_2_, EntityKangaroo.this.kangarooInventory);
            }

            public Component getDisplayName() {
               return Component.translatable("entity.alexsmobs.kangaroo.pouch");
            }
         });
      }
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean isStanding() {
      return (Boolean)this.entityData.get(STANDING);
   }

   public void setStanding(boolean standing) {
      this.entityData.set(STANDING, standing);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public int getVisualFlag() {
      return (Integer)this.entityData.get(VISUAL_FLAG);
   }

   public void setVisualFlag(int visualFlag) {
      this.entityData.set(VISUAL_FLAG, visualFlag);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STANDING, false);
      builder.define(SITTING, false);
      builder.define(FORCED_SIT, false);
      builder.define(COMMAND, 0);
      builder.define(VISUAL_FLAG, 0);
      builder.define(POUCH_TICK, 0);
      builder.define(CHEST_INDEX, -1);
      builder.define(HELMET_INDEX, -1);
      builder.define(SWORD_INDEX, -1);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new GroundPathNavigatorWide(this, worldIn, 2.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(1, new KangarooAIMelee(this, 1.2, false));
      this.goalSelector.addGoal(2, new FloatGoal(this));
      this.goalSelector.addGoal(2, new TameableAIFollowOwner(this, 1.2, 5.0F, 2.0F, false));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new AnimalAIRideParent(this, 1.25));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, AMCompat.ingredientOf(AMTagRegistry.KANGAROO_TAMEABLES), false));
      this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 110, 1.2, 10, 7));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, new AnimalAIHurtByTargetNotBaby(this));
   }

   protected boolean canAddPassenger(Entity passenger) {
      return super.canAddPassenger(passenger) && (Integer)this.entityData.get(POUCH_TICK) == 0;
   }

   public Vec3 getPassengerRidingPosition(Entity passenger) {
      return new Vec3(this.getX(), this.getY() + this.getPassengersRidingOffset(), this.getZ());
   }

   public double getPassengersRidingOffset() {
      return this.getBbHeight() * 0.3499999940395355;
   }

   public void onAddedToLevel() {
      super.onAddedToLevel();
      this.updateClientInventory();
   }

   public void tick() {
      super.tick();
      boolean moving = this.getDeltaMovement().lengthSqr() > 0.03;
      int pouchTick = (Integer)this.entityData.get(POUCH_TICK);
      this.prevTotalMovingProgress = this.totalMovingProgress;
      this.prevPouchProgress = this.pouchProgress;
      this.prevSitProgress = this.sitProgress;
      this.prevStandProgress = this.standProgress;
      if (this.isSitting()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.eatCooldown > 0) {
         this.eatCooldown--;
      }

      if (this.isStanding()) {
         if (this.standProgress < 5.0F) {
            this.standProgress++;
         }
      } else if (this.standProgress > 0.0F) {
         this.standProgress--;
      }

      if (moving) {
         if (this.totalMovingProgress < 5.0F) {
            this.totalMovingProgress++;
         }
      } else if (this.totalMovingProgress > 0.0F) {
         this.totalMovingProgress--;
      }

      if (pouchTick != 0 && this.pouchProgress < 5.0F) {
         this.pouchProgress++;
      }

      if (pouchTick == 0 && this.pouchProgress > 0.0F) {
         this.pouchProgress--;
      }

      if (pouchTick > 0) {
         this.entityData.set(POUCH_TICK, pouchTick - 1);
      }

      if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
         this.setStanding(false);
         this.standingTime = 0;
         this.maxStandTime = 75 + this.random.nextInt(50);
      }

      if (this.isSitting() && !this.forcedSit() && ++this.sittingTime > this.maxSitTime) {
         this.setOrderedToSit(false);
         this.sittingTime = 0;
         this.maxSitTime = 75 + this.random.nextInt(50);
      }

      if (!this.level().isClientSide()
         && this.getAnimation() == NO_ANIMATION
         && this.getCommand() != 1
         && !this.isStanding()
         && !this.isSitting()
         && this.random.nextInt(1500) == 0) {
         this.maxSitTime = 500 + this.random.nextInt(350);
         this.setOrderedToSit(true);
      }

      if (!this.forcedSit() && this.isSitting() && (this.getTarget() != null || this.isStanding())) {
         this.setOrderedToSit(false);
      }

      if (this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && this.random.nextInt(1500) == 0) {
         this.maxStandTime = 75 + this.random.nextInt(50);
         this.setStanding(true);
      }

      if (this.forcedSit() && !this.isVehicle() && this.isTame()) {
         this.setOrderedToSit(true);
      }

      if (!this.level().isClientSide()) {
         if (this.tickCount == 1) {
            this.updateClientInventory();
         }

         if (!moving
            && this.getAnimation() == NO_ANIMATION
            && !this.isSitting()
            && !this.isStanding()
            && (this.getRandom().nextInt(180) == 0 || this.getHealth() < this.getMaxHealth() && this.getRandom().nextInt(40) == 0)
            && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
            this.setAnimation(ANIMATION_EAT_GRASS);
         }

         if (this.getAnimation() == ANIMATION_EAT_GRASS
            && this.getAnimationTick() == 20
            && this.getHealth() < this.getMaxHealth()
            && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
            this.heal(6.0F);
            this.level().levelEvent(2001, this.blockPosition().below(), Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
            this.level().setBlock(this.blockPosition().below(), Blocks.DIRT.defaultBlockState(), 2);
         }

         if (this.getHealth() < this.getMaxHealth() && this.isTame() && this.eatCooldown == 0) {
            this.eatCooldown = 20 + this.random.nextInt(40);
            if (!this.kangarooInventory.isEmpty()) {
               ItemStack foodStack = ItemStack.EMPTY;

               for (int i = 0; i < this.kangarooInventory.getContainerSize(); i++) {
                  ItemStack stack = this.kangarooInventory.getItem(i);
                  if (AMCompat.isEdible(stack.getItem()) && AMCompat.getFoodProperties(stack.getItem()) != null && !AMCompat.isMeat(stack.getItem())) {
                     foodStack = stack;
                  }
               }

               if (!foodStack.isEmpty() && AMCompat.getFoodProperties(foodStack.getItem()) != null) {
                  AlexsMobs.sendMSGToAll(new MessageKangarooEat(this.getId(), foodStack));
                  this.heal(AMCompat.nutrition(AMCompat.getFoodProperties(foodStack.getItem())) * 2);
                  foodStack.shrink(1);
                  this.gameEvent(GameEvent.EAT);
                  this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
               }
            }
         }
      }

      if (this.jumpTicks < this.jumpDuration) {
         this.jumpTicks++;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

      LivingEntity attackTarget = this.getTarget();
      if (attackTarget != null && this.hasLineOfSight(attackTarget)) {
         if (this.distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 1.0F) {
            if (this.getAnimation() == ANIMATION_KICK && this.getAnimationTick() == 8) {
               AMCompat.knockback(attackTarget, 1.2999999523162842, Mth.sin(this.getYRot() * 0.017453292F), -Mth.cos(this.getYRot() * 0.017453292F));
               AMCompat.doHurtTarget(this, this.getTarget());
            }

            if (this.getAnimation() == ANIMATION_PUNCH_L && this.getAnimationTick() == 6) {
               float rot = this.getYRot() + 90.0F;
               AMCompat.knockback(attackTarget, 0.8500000238418579, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
               AMCompat.doHurtTarget(this, this.getTarget());
            }

            if (this.getAnimation() == ANIMATION_PUNCH_R && this.getAnimationTick() == 6) {
               float rot = this.getYRot() - 90.0F;
               AMCompat.knockback(attackTarget, 0.8500000238418579, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
               AMCompat.doHurtTarget(this, this.getTarget());
            }

            if ((this.getAnimation() == ANIMATION_STAB_R || this.getAnimation() == ANIMATION_STAB_L) && this.getAnimationTick() == 13) {
               float rot = this.getYRot();
               AMCompat.knockback(attackTarget, 1.0, Mth.sin(rot * 0.017453292F), -Mth.cos(rot * 0.017453292F));
               AMCompat.doHurtTarget(this, this.getTarget());
            }
         }

         this.lookAt(attackTarget, 360.0F, 360.0F);
      }

      if (this.isBaby() && attackTarget != null) {
         this.setTarget(null);
      }

      if (this.isVehicle()) {
         this.entityData.set(POUCH_TICK, 10);
         this.setStanding(true);
         this.maxStandTime = 25;
      }

      if (this.isPassenger()) {
         if (this.isBaby() && this.getVehicle() instanceof EntityKangaroo) {
            EntityKangaroo mount = (EntityKangaroo)this.getVehicle();
            this.setYRot(mount.yBodyRot);
            this.yHeadRot = mount.yBodyRot;
            this.yBodyRot = mount.yBodyRot;
         }

         if (this.getVehicle() instanceof EntityKangaroo && !this.isBaby()) {
            this.removeVehicle();
         }
      }

      if (this.clientArmorCooldown > 0) {
         this.clientArmorCooldown--;
      }

      if (this.tickCount > 5 && !this.level().isClientSide() && this.clientArmorCooldown == 0 && this.isTame()) {
         this.updateClientInventory();
         this.clientArmorCooldown = 20;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean doHurtTarget(Entity entityIn) {
      boolean prev = super.doHurtTarget(entityIn);
      if (prev && !this.getMainHandItem().isEmpty()) {
         this.damageItem(this.getMainHandItem(), EquipmentSlot.MAINHAND);
      }

      return prev;
   }

   public boolean hurt(DamageSource src, float amount) {
      boolean prev = super.hurt(src, amount);
      if (prev) {
         if (!this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.damageItem(this.getItemBySlot(EquipmentSlot.HEAD), EquipmentSlot.HEAD);
         }

         if (!this.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            this.damageItem(this.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST);
         }
      }

      return prev;
   }

   private void damageItem(ItemStack stack, EquipmentSlot slot) {
      if (stack != null && !stack.isEmpty()) {
         AMCompat.hurtAndBreak(stack, 1, this, slot);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || source.is(DamageTypes.IN_WALL);
   }

   public boolean isAlliedTo(Entity entityIn) {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (entityIn == livingentity) {
            return true;
         }

         if (entityIn instanceof TamableAnimal) {
            return ((TamableAnimal)entityIn).isOwnedBy(livingentity);
         }

         if (livingentity != null) {
            return livingentity.isAlliedTo(entityIn);
         }
      }

      return super.isAlliedTo(entityIn);
   }

   public MoveControl getMoveControl() {
      return this.moveControl;
   }

   public void travel(Vec3 vec3d) {
      if (this.isSitting() || this.getAnimation() == ANIMATION_EAT_GRASS) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   private void checkLandingDelay() {
      this.updateMoveTypeDuration();
      this.disableJumpControl();
   }

   public PathNavigation getNavigation() {
      return this.navigation;
   }

   @Nullable
   public Entity getControlledVehicle() {
      return this.getVehicle() instanceof EntityKangaroo ? null : super.getControlledVehicle();
   }

   private void enableJumpControl() {
      if (this.jumpControl instanceof EntityKangaroo.JumpHelperController) {
         ((EntityKangaroo.JumpHelperController)this.jumpControl).setCanJump(true);
      }
   }

   private void disableJumpControl() {
      if (this.jumpControl instanceof EntityKangaroo.JumpHelperController) {
         ((EntityKangaroo.JumpHelperController)this.jumpControl).setCanJump(false);
      }
   }

   private void updateMoveTypeDuration() {
      if (this.moveControl.getSpeedModifier() < 2.0) {
         this.currentMoveTypeDuration = 2;
      } else {
         this.currentMoveTypeDuration = 1;
      }
   }

   private void calculateRotationYaw(double x, double z) {
      this.setYRot((float)(Mth.atan2(z - this.getZ(), x - this.getX()) * 57.2957763671875) - 90.0F);
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      if (this.currentMoveTypeDuration > 0) {
         this.currentMoveTypeDuration--;
      }

      if (this.onGround()) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.currentMoveTypeDuration == 0) {
            LivingEntity livingentity = this.getTarget();
            if (livingentity != null && this.distanceToSqr(livingentity) < 16.0) {
               this.calculateRotationYaw(livingentity.getX(), livingentity.getZ());
               this.moveControl.setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), this.moveControl.getSpeedModifier());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         if (this.jumpControl instanceof EntityKangaroo.JumpHelperController rabbitController) {
            if (!rabbitController.getIsJumping()) {
               if (this.moveControl.hasWanted() && this.currentMoveTypeDuration == 0) {
                  Path path = this.navigation.getPath();
                  new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                  if (path != null && !path.isDone()) {
                     Vec3 vector3d = path.getNextEntityPos(this);
                  }

                  this.startJumping();
               }
            } else if (!rabbitController.canJump()) {
               this.enableJumpControl();
            }
         }
      }

      this.wasOnGround = this.onGround();
   }

   public float getJumpCompletion(float partialTicks) {
      return this.jumpDuration == 0 ? 0.0F : (this.jumpTicks + partialTicks) / this.jumpDuration;
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int i) {
      this.animationTick = i;
   }

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
      if (animation == ANIMATION_KICK) {
         this.setStanding(true);
         this.maxStandTime = 30;
      } else if (animation == ANIMATION_PUNCH_R) {
         this.setStanding(true);
         this.maxStandTime = 15;
      } else if (animation == ANIMATION_PUNCH_L) {
         this.setStanding(true);
         this.maxStandTime = 15;
      } else if (animation == ANIMATION_STAB_R || animation == ANIMATION_STAB_L) {
         this.setStanding(true);
         this.maxStandTime = 25;
      }
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_EAT_GRASS, ANIMATION_KICK, ANIMATION_PUNCH_L, ANIMATION_PUNCH_R, ANIMATION_STAB_L, ANIMATION_STAB_R};
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.KANGAROO.get(), serverWorld);
   }

   public void setMovementSpeed(double newSpeed) {
      this.getNavigation().setSpeedModifier(newSpeed);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), newSpeed);
   }

   protected float getJumpPower() {
      return 0.5F;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean isFood(ItemStack stack) {
      Item item = stack.getItem();
      return stack.is(AMTagRegistry.KANGAROO_BREEDABLES);
   }

   public void resetKangarooSlots() {
      if (!this.level().isClientSide()) {
         int swordIndex = -1;
         double swordDamage = 0.0;
         int helmetIndex = -1;
         double helmetArmor = 0.0;
         int chestplateIndex = -1;
         double chestplateArmor = 0.0;

         for (int i = 0; i < this.kangarooInventory.getContainerSize(); i++) {
            ItemStack stack = this.kangarooInventory.getItem(i);
            if (!stack.isEmpty()) {
               double dmg = this.getDamageForItem(stack);
               if (dmg > 0.0 && dmg > swordDamage) {
                  swordDamage = dmg;
                  swordIndex = i;
               }

               if (stack.getItem().canEquip(stack, EquipmentSlot.HEAD, this) && !this.isBaby() && helmetIndex == -1) {
                  helmetIndex = i;
               }

               if (AMCompat.isArmor(stack) && !this.isBaby()) {
                  if (AMCompat.equipmentSlotFor(stack) == EquipmentSlot.HEAD) {
                     double prot = this.getProtectionForItem(stack, EquipmentSlot.HEAD);
                     if (prot > 0.0 && prot > helmetArmor) {
                        helmetArmor = prot;
                        helmetIndex = i;
                     }
                  }

                  if (AMCompat.equipmentSlotFor(stack) == EquipmentSlot.CHEST) {
                     double prot = this.getProtectionForItem(stack, EquipmentSlot.CHEST);
                     if (prot > 0.0 && prot > chestplateArmor) {
                        chestplateArmor = prot;
                        chestplateIndex = i;
                     }
                  }
               }
            }
         }

         this.entityData.set(SWORD_INDEX, swordIndex);
         this.entityData.set(CHEST_INDEX, chestplateIndex);
         this.entityData.set(HELMET_INDEX, helmetIndex);
         this.updateClientInventory();
      }
   }

   private void updateClientInventory() {
      if (!this.level().isClientSide()) {
         for (int i = 0; i < 9; i++) {
            AlexsMobs.sendMSGToAll(new MessageKangarooInventorySync(this.getId(), i, this.kangarooInventory.getItem(i)));
         }
      }
   }

   @Nullable
   private Map<EquipmentSlot, ItemStack> collectEquipmentChanges() {
      Map<EquipmentSlot, ItemStack> map = null;

      for (EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
         ItemStack itemstack;
         switch (equipmentslottype.getType()) {
            case HAND:
               itemstack = this.getItemInHand(equipmentslottype);
               break;
            case HUMANOID_ARMOR:
               itemstack = this.getArmorInSlot(equipmentslottype);
               break;
            default:
               continue;
         }

         ItemStack itemstack1 = this.getItemBySlot(equipmentslottype);
         if (!ItemStack.matches(itemstack1, itemstack)) {
            NeoForge.EVENT_BUS.post(new LivingEquipmentChangeEvent(this, equipmentslottype, itemstack, itemstack1));
            if (map == null) {
               map = Maps.newEnumMap(EquipmentSlot.class);
            }

            map.put(equipmentslottype, itemstack1);
            if (!itemstack.isEmpty()) {
               AMCompat.removeItemModifiers(this, itemstack, equipmentslottype);
            }

            if (!itemstack1.isEmpty()) {
               AMCompat.addItemModifiers(this, itemstack1, equipmentslottype);
            }
         }
      }

      return map;
   }

   public ItemStack getItemBySlot(EquipmentSlot slotIn) {
      return switch (slotIn.getType()) {
         case HAND -> this.getItemInHand(slotIn);
         case HUMANOID_ARMOR -> this.getArmorInSlot(slotIn);
         default -> ItemStack.EMPTY;
      };
   }

   private ItemStack getArmorInSlot(EquipmentSlot slot) {
      int helmIndex = (Integer)this.entityData.get(HELMET_INDEX);
      int chestIndex = (Integer)this.entityData.get(CHEST_INDEX);
      return slot == EquipmentSlot.HEAD && helmIndex >= 0
         ? this.kangarooInventory.getItem(helmIndex)
         : (slot == EquipmentSlot.CHEST && chestIndex >= 0 ? this.kangarooInventory.getItem(chestIndex) : ItemStack.EMPTY);
   }

   private ItemStack getItemInHand(EquipmentSlot slot) {
      int index = (Integer)this.entityData.get(SWORD_INDEX);
      return slot == EquipmentSlot.MAINHAND && index >= 0 ? this.kangarooInventory.getItem(index) : ItemStack.EMPTY;
   }

   public double getDamageForItem(ItemStack itemStack) {
      return AMCompat.attackDamageOf(itemStack, EquipmentSlot.MAINHAND);
   }

   public double getProtectionForItem(ItemStack itemStack, EquipmentSlot type) {
      return AMCompat.armorOf(itemStack, type);
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      double d0 = this.moveControl.getSpeedModifier();
      if (d0 > 0.0) {
         double d1 = this.getDeltaMovement().horizontalDistanceSqr();
         if (d1 < 0.01) {
         }
      }

      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)1);
      }
   }

   public boolean hasJumper() {
      return this.jumpControl instanceof EntityKangaroo.JumpHelperController;
   }

   public void startJumping() {
      if (!this.isSitting() || this.isInWater()) {
         this.setJumping(true);
         this.jumpDuration = 16;
         this.jumpTicks = 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 1) {
         this.spawnSprintParticle();
         this.jumpDuration = 16;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(id);
      }
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   public void containerChanged(Container iInventory) {
      this.resetKangarooSlots();
   }

   public static class JumpHelperController extends JumpControl {
      private final EntityKangaroo kangaroo;
      private boolean canJump;

      public JumpHelperController(EntityKangaroo kangaroo) {
         super(kangaroo);
         this.kangaroo = kangaroo;
      }

      public boolean getIsJumping() {
         return this.jump;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean canJumpIn) {
         this.canJump = canJumpIn;
      }

      public void tick() {
         if (this.jump) {
            this.kangaroo.startJumping();
            this.jump = false;
         }
      }
   }

   static class MoveHelperController extends MoveControl {
      private final EntityKangaroo kangaroo;
      private double nextJumpSpeed;

      public MoveHelperController(EntityKangaroo kangaroo) {
         super(kangaroo);
         this.kangaroo = kangaroo;
      }

      public void tick() {
         if (this.kangaroo.hasJumper()
            && this.kangaroo.onGround()
            && !this.kangaroo.jumping
            && !((EntityKangaroo.JumpHelperController)this.kangaroo.jumpControl).getIsJumping()) {
            this.kangaroo.setMovementSpeed(0.0);
         } else if (this.hasWanted()) {
            this.kangaroo.setMovementSpeed(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setWantedPosition(double x, double y, double z, double speedIn) {
         if (this.kangaroo.isInWater()) {
            speedIn = 1.5;
         }

         super.setWantedPosition(x, y, z, speedIn);
         if (speedIn > 0.0) {
            this.nextJumpSpeed = speedIn;
         }
      }
   }
}
