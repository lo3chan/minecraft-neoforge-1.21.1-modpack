package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILootChests;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.ILootsChests;
import com.github.alexthe666.alexsmobs.entity.ai.RaccoonAIBeg;
import com.github.alexthe666.alexsmobs.entity.ai.RaccoonAIWash;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIDestroyTurtleEggs;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidType;

public class EntityRaccoon extends TamableAnimal implements IAnimatedEntity, IFollower, ITargetsDroppedItems, ILootsChests {
   private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityRaccoon.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityRaccoon.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BEGGING = SynchedEntityData.defineId(EntityRaccoon.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> WASHING = SynchedEntityData.defineId(EntityRaccoon.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<BlockPos>> WASH_POS = SynchedEntityData.defineId(
      EntityRaccoon.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityRaccoon.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> CARPET_COLOR = SynchedEntityData.defineId(EntityRaccoon.class, EntityDataSerializers.INT);
   public float prevStandProgress;
   public float standProgress;
   public float prevBegProgress;
   public float begProgress;
   public float prevWashProgress;
   public float washProgress;
   public float prevSitProgress;
   public float sitProgress;
   public int maxStandTime = 75;
   private int standingTime = 0;
   private int stealCooldown = 0;
   public int lookForWaterBeforeEatingTimer = 0;
   private int animationTick;
   private Animation currentAnimation;
   private int pickupItemCooldown = 0;
   @Nullable
   private UUID eggThrowerUUID = null;
   public boolean forcedSit = false;
   public static final Animation ANIMATION_ATTACK = Animation.create(12);
   private static final TargetingConditions VILLAGER_STEAL_PREDICATE = TargetingConditions.forCombat().range(20.0).ignoreLineOfSight();
   private static final TargetingConditions IRON_GOLEM_PREDICATE = TargetingConditions.forCombat().range(20.0).ignoreLineOfSight();

   protected EntityRaccoon(EntityType type, Level world) {
      super(type, world);
      this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.RACCOON_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.RACCOON_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.RACCOON_HURT.get();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.raccoonSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(2, new RaccoonAIWash(this));
      this.goalSelector.addGoal(3, new TameableAIFollowOwner(this, 1.3, 10.0F, 2.0F, false));
      this.goalSelector.addGoal(4, new FloatGoal(this));
      this.goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.1, true));
      this.goalSelector.addGoal(7, new AnimalAILootChests(this, 16));
      this.goalSelector.addGoal(8, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(9, new RaccoonAIBeg(this, 0.65));
      this.goalSelector.addGoal(10, new AnimalAIPanicBaby(this, 1.25));
      this.goalSelector.addGoal(11, new EntityRaccoon.AIStealFromVillagers(this));
      this.goalSelector.addGoal(12, new EntityRaccoon.StrollGoal(200));
      this.goalSelector.addGoal(13, new TameableAIDestroyTurtleEggs(this, 1.0, 3));
      this.goalSelector.addGoal(14, new AnimalAIWanderRanged(this, 120, 1.0, 14, 7));
      this.goalSelector.addGoal(15, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.goalSelector.addGoal(15, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new AnimalAIHurtByTargetNotBaby(this));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
      this.targetSelector.addGoal(3, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(4, new OwnerHurtTargetGoal(this));
   }

   public boolean isAlliedTo(Entity entityIn) {
      if (!(entityIn instanceof EntityBlueJay jay)) {
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
      } else {
         return jay.getRaccoonUUID() != null && jay.getRaccoonUUID().equals(this.getUUID());
      }
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.getColor() != null) {
         if (!this.level().isClientSide()) {
            AMCompat.spawnAtLocation(this, this.getCarpetItemBeingWorn());
         }

         this.setColor(null);
      }
   }

   @Nullable
   public DyeColor getColor() {
      int lvt_1_1_ = (Integer)this.entityData.get(CARPET_COLOR);
      return lvt_1_1_ == -1 ? null : DyeColor.byId(lvt_1_1_);
   }

   public void setColor(@Nullable DyeColor color) {
      this.entityData.set(CARPET_COLOR, color == null ? -1 : color.getId());
   }

   public Item getCarpetItemBeingWorn() {
      return this.getColor() != null ? EntityElephant.DYE_COLOR_ITEM_MAP.get(this.getColor()) : Items.AIR;
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.RACCOON_BREEDABLES);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      boolean owner = this.isTame() && this.isOwnedBy(player);
      if (itemstack.is(AMTagRegistry.RACCOON_TEAMING_FOODS) && this.bondWithBlueJays(player.getUUID())) {
         this.usePlayerItem(player, hand, itemstack);
         this.level().broadcastEntityEvent(this, (byte)93);
         return InteractionResult.SUCCESS;
      } else if (this.isTame() && !this.getMainHandItem().isEmpty()) {
         if (!this.level().isClientSide()) {
            AMCompat.spawnAtLocation(this, this.getMainHandItem().copy());
         }

         this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
         this.pickupItemCooldown = 60;
         return InteractionResult.SUCCESS;
      } else if (owner && itemstack.is(ItemTags.WOOL_CARPETS)) {
         DyeColor color = EntityElephant.getCarpetColor(itemstack);
         if (color != this.getColor()) {
            if (this.getColor() != null) {
               AMCompat.spawnAtLocation(this, this.getCarpetItemBeingWorn());
            }

            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound((SoundEvent)SoundEvents.LLAMA_SWAG.value(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            itemstack.shrink(1);
            this.setColor(color);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else if (owner && this.getColor() != null && itemstack.is(net.neoforged.neoforge.common.Tags.Items.TOOLS_SHEAR)) {
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         if (this.getColor() != null) {
            AMCompat.spawnAtLocation(this, this.getCarpetItemBeingWorn());
         }

         this.setColor(null);
         return InteractionResult.SUCCESS;
      } else if (this.isTame() && isRaccoonFood(itemstack) && !this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
         if (this.getMainHandItem().isEmpty()) {
            ItemStack copy = itemstack.copy();
            copy.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, copy);
            this.onEatItem();
            if (AMCompat.hasCraftingRemainder(itemstack)) {
               AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(itemstack));
            }

            if (!player.isCreative()) {
               itemstack.shrink(1);
            }

            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
         } else {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5.0F);
         }

         this.usePlayerItem(player, hand, itemstack);
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS
            && type != InteractionResult.SUCCESS
            && this.isTame()
            && this.isOwnedBy(player)
            && !isRaccoonFood(itemstack)
            && !player.isShiftKeyDown()) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
               this.setCommand(0);
            }

            player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
               this.forcedSit = true;
               this.setOrderedToSit(true);
               return InteractionResult.SUCCESS;
            } else {
               this.forcedSit = false;
               this.setOrderedToSit(false);
               return InteractionResult.SUCCESS;
            }
         } else {
            return type;
         }
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("RacSitting", this.isSitting());
      compound.putBoolean("ForcedToSit", this.forcedSit);
      compound.putInt("RacCommand", this.getCommand());
      compound.putInt("Carpet", (Integer)this.entityData.get(CARPET_COLOR));
      compound.putInt("StealCooldown", this.stealCooldown);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setOrderedToSit(AMCompat.getBoolean(compound, "RacSitting"));
      this.forcedSit = AMCompat.getBoolean(compound, "ForcedToSit");
      this.setCommand(AMCompat.getInt(compound, "RacCommand"));
      this.entityData.set(CARPET_COLOR, AMCompat.getInt(compound, "Carpet"));
      this.stealCooldown = AMCompat.getInt(compound, "StealCooldown");
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public static boolean isRaccoonFood(ItemStack stack) {
      return AMCompat.isEdible(stack) || stack.is(AMTagRegistry.RACCOON_FOODSTUFFS);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 9.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         Entity entity = source.getEntity();
         this.setOrderedToSit(false);
         if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 4.0F;
         }

         return super.hurt(source, amount);
      }
   }

   protected void updateControlFlags() {
      boolean flag = !(this.getControllingPassenger() instanceof Mob);
      boolean flag1 = !(this.getVehicle() instanceof Boat);
      boolean flag2 = this.getFirstPassenger() instanceof EntityBlueJay;
      this.goalSelector.setControlFlag(Flag.MOVE, flag || flag2);
      this.goalSelector.setControlFlag(Flag.JUMP, flag && flag1 || flag2);
      this.goalSelector.setControlFlag(Flag.LOOK, flag || flag2);
   }

   public void tick() {
      super.tick();
      this.prevStandProgress = this.standProgress;
      this.prevBegProgress = this.begProgress;
      this.prevWashProgress = this.washProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.isStanding()) {
         if (this.standProgress < 5.0F) {
            this.standProgress++;
         }
      } else if (this.standProgress > 0.0F) {
         this.standProgress--;
      }

      if (this.isBegging()) {
         if (this.begProgress < 5.0F) {
            this.begProgress++;
         }
      } else if (this.begProgress > 0.0F) {
         this.begProgress--;
      }

      if (this.isWashing()) {
         if (this.washProgress < 5.0F) {
            this.washProgress++;
         }
      } else if (this.washProgress > 0.0F) {
         this.washProgress--;
      }

      if (this.isSitting()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
         this.setStanding(false);
         this.standingTime = 0;
         this.maxStandTime = 75 + this.random.nextInt(50);
      }

      if (!this.level().isClientSide()) {
         if (this.lookForWaterBeforeEatingTimer > 0) {
            this.lookForWaterBeforeEatingTimer--;
         } else if (!this.isWashing() && this.canTargetItem(this.getMainHandItem())) {
            this.onEatItem();
            if (AMCompat.hasCraftingRemainder(this.getMainHandItem())) {
               AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(this.getMainHandItem()));
            }

            this.getMainHandItem().shrink(1);
         }
      }

      if (this.isWashing() && this.getWashPos() != null) {
         BlockPos washingPos = this.getWashPos();
         if (this.distanceToSqr(washingPos.getX() + 0.5, washingPos.getY() + 0.5, washingPos.getZ() + 0.5) < 3.0) {
            for (int j = 0; j < 4.0F; j++) {
               double d2 = this.random.nextDouble();
               double d3 = this.random.nextDouble();
               Vec3 vector3d = this.getDeltaMovement();
               this.level()
                  .addParticle(
                     ParticleTypes.SPLASH, washingPos.getX() + d2, washingPos.getY() + 0.8F, washingPos.getZ() + d3, vector3d.x, vector3d.y, vector3d.z
                  );
            }
         } else {
            this.setWashing(false);
         }
      }

      if (!this.level().isClientSide()
         && this.getTarget() != null
         && this.hasLineOfSight(this.getTarget())
         && this.distanceTo(this.getTarget()) < 4.0F
         && this.getAnimation() == ANIMATION_ATTACK
         && this.getAnimationTick() == 5) {
         float f1 = this.getYRot() * 0.017453292F;
         this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * -0.06F, 0.0, Mth.cos(f1) * -0.06F));
         AMCompat.knockback(this.getTarget(), 0.3499999940395355, this.getTarget().getX() - this.getX(), this.getTarget().getZ() - this.getZ());
         this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
      }

      if (this.stealCooldown > 0) {
         this.stealCooldown--;
      }

      if (this.pickupItemCooldown > 0) {
         this.pickupItemCooldown--;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void onEatItem() {
      this.heal(10.0F);
      this.level().broadcastEntityEvent(this, (byte)92);
      this.gameEvent(GameEvent.EAT);
      this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
   }

   public void postWashItem(ItemStack stack) {
      if (stack.is(AMTagRegistry.RACCOON_TAMEABLES) && this.eggThrowerUUID != null && !this.isTame()) {
         if (this.getRandom().nextFloat() < 0.3F) {
            AMCompat.setTame(this, true);
            AMCompat.setOwnerUUID(this, this.eggThrowerUUID);
            Player player = this.level().getPlayerByUUID(this.eggThrowerUUID);
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
            }

            this.level().broadcastEntityEvent(this, (byte)7);
         } else {
            this.level().broadcastEntityEvent(this, (byte)6);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 92) {
         for (int i = 0; i < 6 + this.random.nextInt(3); i++) {
            double d2 = this.random.nextGaussian() * 0.02;
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            this.level()
               .addParticle(
                  new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.MAIN_HAND)),
                  this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                  this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                  this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }
      } else if (id == 93) {
         for (int i = 0; i < 6 + this.random.nextInt(3); i++) {
            double d2 = this.random.nextGaussian() * 0.02;
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            this.level()
               .addParticle(
                  new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.GLOW_BERRIES)),
                  this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                  this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                  this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
      return true;
   }

   public boolean isStanding() {
      return (Boolean)this.entityData.get(STANDING);
   }

   public void setStanding(boolean standing) {
      this.entityData.set(STANDING, standing);
   }

   public boolean isBegging() {
      return (Boolean)this.entityData.get(BEGGING);
   }

   public void setBegging(boolean begging) {
      this.entityData.set(BEGGING, begging);
   }

   public boolean isWashing() {
      return (Boolean)this.entityData.get(WASHING);
   }

   public void setWashing(boolean washing) {
      this.entityData.set(WASHING, washing);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STANDING, false);
      builder.define(SITTING, false);
      builder.define(BEGGING, false);
      builder.define(WASHING, false);
      builder.define(CARPET_COLOR, -1);
      builder.define(COMMAND, 0);
      builder.define(WASH_POS, Optional.empty());
   }

   public BlockPos getWashPos() {
      return (BlockPos)((Optional)this.entityData.get(WASH_POS)).orElse(null);
   }

   public void setWashPos(BlockPos washingPos) {
      this.entityData.set(WASH_POS, Optional.ofNullable(washingPos));
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
      if (animation == ANIMATION_ATTACK) {
         this.maxStandTime = 15;
         this.setStanding(true);
      }
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_ATTACK};
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.RACCOON.get(), serverWorld);
   }

   public void travel(Vec3 vec3d) {
      if (this.isSitting() || this.isWashing()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return isRaccoonFood(stack) && this.pickupItemCooldown == 0;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.lookForWaterBeforeEatingTimer = 100;
      ItemStack duplicate = e.getItem().copy();
      duplicate.setCount(1);
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
      }

      Entity thrower = e.getOwner();
      if (e.getItem().is(AMTagRegistry.RACCOON_TEAMING_FOODS) && thrower != null && this.bondWithBlueJays(thrower.getUUID())) {
         this.level().broadcastEntityEvent(this, (byte)93);
      } else {
         this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
      }

      if (e.getItem().is(AMTagRegistry.RACCOON_TAMEABLES) && thrower != null) {
         this.eggThrowerUUID = thrower.getUUID();
      } else {
         this.eggThrowerUUID = null;
      }
   }

   public Vec3 getPassengerRidingPosition(Entity passenger) {
      return new Vec3(this.getX(), this.getY() + this.getPassengersRidingOffset(), this.getZ());
   }

   public double getPassengersRidingOffset() {
      return this.getBbHeight() * 0.45;
   }

   private boolean bondWithBlueJays(UUID uuid) {
      AABB allyBox = this.getBoundingBox().inflate(48.0);
      boolean any = false;

      for (EntityBlueJay entity : this.level().getEntitiesOfClass(EntityBlueJay.class, allyBox)) {
         if (entity.getFeedTime() > 0 && entity.getLastFeederUUID() != null && entity.getLastFeederUUID().equals(uuid)) {
            entity.setRaccoon(this);
            entity.setFeedTime(0);
            any = true;
         }
      }

      return any;
   }

   @Override
   public boolean isLootable(Container inventory) {
      for (int i = 0; i < inventory.getContainerSize(); i++) {
         if (this.shouldLootItem(inventory.getItem(i))) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean shouldLootItem(ItemStack stack) {
      return isRaccoonFood(stack);
   }

   public boolean isHoldingSugar() {
      return this.getMainHandItem().is(AMTagRegistry.RACOON_DISSOLVES);
   }

   public BlockPos getLightPosition() {
      BlockPos pos = AMBlockPos.fromVec3(this.position());
      return !this.level().getBlockState(pos).canOcclude() ? pos.above() : pos;
   }

   public boolean isRigby() {
      String name = ChatFormatting.stripFormatting(this.getName().getString());
      if (name == null) {
         return false;
      } else {
         String lowercaseName = name.toLowerCase(Locale.ROOT);
         return lowercaseName.contains("rigby");
      }
   }

   private class AIStealFromVillagers extends Goal {
      EntityRaccoon raccoon;
      AbstractVillager target;
      int golemCheckTime = 0;
      int cooldown = 0;
      int fleeTime = 0;

      private AIStealFromVillagers(EntityRaccoon raccoon) {
         this.raccoon = raccoon;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.cooldown > 0) {
            this.cooldown--;
            return false;
         } else if (this.raccoon != null
            && this.raccoon.stealCooldown == 0
            && this.raccoon.getMainHandItem() != null
            && this.raccoon.getMainHandItem().isEmpty()) {
            AbstractVillager villager = this.getNearbyVillagers();
            if (!this.isGolemNearby() && villager != null) {
               this.target = villager;
            }

            this.cooldown = 150;
            return this.target != null;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.target != null && this.raccoon != null;
      }

      public void stop() {
         this.target = null;
         this.cooldown = 200 + EntityRaccoon.this.random.nextInt(200);
         this.golemCheckTime = 0;
         this.fleeTime = 0;
      }

      public void tick() {
         if (this.target != null) {
            this.golemCheckTime++;
            if (this.fleeTime > 0) {
               this.fleeTime--;
               if (this.raccoon.getNavigation().isDone()) {
                  Vec3 fleevec = DefaultRandomPos.getPosAway(this.raccoon, 16, 7, this.raccoon.position());
                  if (fleevec != null) {
                     this.raccoon.getNavigation().moveTo(fleevec.x, fleevec.y, fleevec.z, 1.2999999523162842);
                  }
               }

               if (this.fleeTime == 0) {
                  this.stop();
               }
            } else {
               this.raccoon.getNavigation().moveTo(this.target, 1.0);
               if (this.raccoon.distanceTo(this.target) < 1.7F) {
                  this.raccoon.setStanding(true);
                  this.raccoon.maxStandTime = 15;
                  MerchantOffers offers = this.target.getOffers();
                  if (offers != null && !offers.isEmpty() && offers.size() >= 1) {
                     MerchantOffer offer = (MerchantOffer)offers.get(offers.size() <= 1 ? 0 : this.raccoon.getRandom().nextInt(offers.size() - 1));
                     if (offer != null) {
                        ItemStack stealStack = offer.getResult().getItem() == Items.EMERALD ? offer.getBaseCostA() : offer.getResult();
                        if (stealStack.isEmpty()) {
                           this.stop();
                        } else {
                           offer.increaseUses();
                           ItemStack copy = stealStack.copy();
                           copy.setCount(1);
                           this.raccoon.setItemInHand(InteractionHand.MAIN_HAND, copy);
                           this.fleeTime = 60 + EntityRaccoon.this.random.nextInt(60);
                           this.raccoon.getNavigation().stop();
                           EntityRaccoon.this.lookForWaterBeforeEatingTimer = 120 + EntityRaccoon.this.random.nextInt(60);
                           this.target.hurt(EntityRaccoon.this.damageSources().generic(), 0.0F);
                           this.raccoon.stealCooldown = 24000 + EntityRaccoon.this.random.nextInt(48000);
                        }
                     }
                  } else {
                     this.stop();
                  }
               }

               if (this.golemCheckTime % 30 == 0 && EntityRaccoon.this.random.nextBoolean() && this.isGolemNearby()) {
                  this.stop();
               }
            }
         }
      }

      @Nullable
      private boolean isGolemNearby() {
         List<IronGolem> lvt_1_1_ = AMCompat.getNearbyEntities(
            this.raccoon.level(), IronGolem.class, EntityRaccoon.IRON_GOLEM_PREDICATE, this.raccoon, this.raccoon.getBoundingBox().inflate(25.0)
         );
         return !lvt_1_1_.isEmpty();
      }

      @Nullable
      private AbstractVillager getNearbyVillagers() {
         List<AbstractVillager> lvt_1_1_ = AMCompat.getNearbyEntities(
            this.raccoon.level(), AbstractVillager.class, EntityRaccoon.VILLAGER_STEAL_PREDICATE, this.raccoon, this.raccoon.getBoundingBox().inflate(20.0)
         );
         double lvt_2_1_ = 10000.0;
         AbstractVillager lvt_4_1_ = null;

         for (AbstractVillager lvt_6_1_ : lvt_1_1_) {
            if (lvt_6_1_.getHealth() > 2.0F && !lvt_6_1_.getOffers().isEmpty() && this.raccoon.distanceToSqr(lvt_6_1_) < lvt_2_1_) {
               lvt_4_1_ = lvt_6_1_;
               lvt_2_1_ = this.raccoon.distanceToSqr(lvt_6_1_);
            }
         }

         return lvt_4_1_;
      }
   }

   class StrollGoal extends MoveThroughVillageGoal {
      public StrollGoal(int p_i50726_3_) {
         super(EntityRaccoon.this, 1.0, true, p_i50726_3_, () -> false);
      }

      public void start() {
         super.start();
      }

      public boolean canUse() {
         return super.canUse() && this.canFoxMove();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.canFoxMove();
      }

      private boolean canFoxMove() {
         return !EntityRaccoon.this.isWashing() && !EntityRaccoon.this.isSitting() && EntityRaccoon.this.getTarget() == null;
      }
   }
}
