package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CapuchinAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.CapuchinAIRangedAttack;
import com.github.alexthe666.alexsmobs.entity.ai.CapuchinAITargetBalloons;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags.Items;

public class EntityCapuchinMonkey extends TamableAnimal implements IAnimatedEntity, IFollower, ITargetsDroppedItems {
   public static final Animation ANIMATION_THROW = Animation.create(12);
   public static final Animation ANIMATION_HEADTILT = Animation.create(15);
   public static final Animation ANIMATION_SCRATCH = Animation.create(20);
   protected static final EntityDataAccessor<Boolean> DART = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DART_TARGET = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.INT);
   public float prevSitProgress;
   public float sitProgress;
   public boolean forcedSit = false;
   public boolean attackDecision = false;
   private int animationTick;
   private Animation currentAnimation;
   private int sittingTime = 0;
   private int maxSitTime = 75;
   private boolean hasSlowed = false;
   private int rideCooldown = 0;
   private Ingredient temptItems = null;

   protected EntityCapuchinMonkey(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.setPathfindingMalus(PathType.LEAVES, 0.0F);
   }

   public static boolean isTameableFood(ItemStack stack) {
      return stack.is(AMTagRegistry.CAPUCHIN_MONKEY_TAMEABLES);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.4000000059604645);
   }

   public static <T extends Mob> boolean canCapuchinSpawn(
      EntityType<EntityCapuchinMonkey> gorilla, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random
   ) {
      BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
      return (blockstate.is(AMTagRegistry.CAPUCHIN_MONKEY_SPAWNS) || blockstate.is(Blocks.AIR)) && worldIn.getRawBrightness(p_223317_3_, 0) > 8;
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public boolean isMaxGroupSizeReached(int sizeIn) {
      return false;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.capuchinMonkeySpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public Ingredient getAllFoods() {
      if (this.temptItems == null) {
         this.temptItems = AMCompat.ingredientOfTags(AMTagRegistry.CAPUCHIN_MONKEY_BREEDABLES, AMTagRegistry.CAPUCHIN_MONKEY_FOODSTUFFS);
      }

      return this.temptItems;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         Entity entity = source.getEntity();
         if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 4.0F;
         }

         return super.hurt(source, amount);
      }
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(3, new CapuchinAIMelee(this, 1.0, true));
      this.goalSelector.addGoal(3, new CapuchinAIRangedAttack(this, 1.0, 20, 15.0F));
      this.goalSelector.addGoal(6, new TameableAIFollowOwner(this, 1.0, 10.0F, 2.0F, false));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.1, AMCompat.ingredientOf(AMTagRegistry.CAPUCHIN_MONKEY_TAMEABLES), true) {
         public void tick() {
            super.tick();
            if (this.mob.distanceToSqr(this.player) < 6.25 && this.mob.getRandom().nextInt(14) == 0) {
               ((EntityCapuchinMonkey)this.mob).setAnimation(EntityCapuchinMonkey.ANIMATION_HEADTILT);
            }
         }
      });
      this.goalSelector.addGoal(7, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
      this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(4, new HurtByTargetGoal(this, new Class[]{EntityCapuchinMonkey.class, EntityTossedItem.class}).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(5, new CapuchinAITargetBalloons(this, true));
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.CAPUCHIN_MONKEY_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.CAPUCHIN_MONKEY_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.CAPUCHIN_MONKEY_HURT.get();
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

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("MonkeySitting", this.isSitting());
      compound.putBoolean("HasDart", this.hasDart());
      compound.putBoolean("ForcedToSit", this.forcedSit);
      compound.putInt("Command", this.getCommand());
      compound.putInt("Variant", this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setOrderedToSit(AMCompat.getBoolean(compound, "MonkeySitting"));
      this.forcedSit = AMCompat.getBoolean(compound, "ForcedToSit");
      this.setCommand(AMCompat.getInt(compound, "Command"));
      this.setDart(AMCompat.getBoolean(compound, "HasDart"));
      this.setVariant(AMCompat.getInt(compound, "Variant"));
   }

   public void tick() {
      super.tick();
      this.prevSitProgress = this.sitProgress;
      if (this.isSitting()) {
         if (this.sitProgress < 10.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (!this.forcedSit && this.isSitting() && ++this.sittingTime > this.maxSitTime) {
         this.setOrderedToSit(false);
         this.sittingTime = 0;
         this.maxSitTime = 75 + this.random.nextInt(50);
      }

      if (!this.level().isClientSide() && this.getAnimation() == NO_ANIMATION && !this.isSitting() && this.getCommand() != 1 && this.random.nextInt(1500) == 0) {
         this.maxSitTime = 300 + this.random.nextInt(250);
         this.setOrderedToSit(true);
      }

      AMCompat.setMaxUpStep(this, 2.0F);
      if (!this.forcedSit && this.isSitting() && (this.getDartTarget() != null || this.getCommand() == 1)) {
         this.setOrderedToSit(false);
      }

      if (!this.level().isClientSide()) {
         if (this.getTarget() != null && this.getAnimation() == ANIMATION_SCRATCH && this.getAnimationTick() == 10) {
            float f1 = this.getYRot() * 0.017453292F;
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.3F, 0.0, Mth.cos(f1) * 0.3F));
            AMCompat.knockback(this.getTarget(), 1.0, this.getTarget().getX() - this.getX(), this.getTarget().getZ() - this.getZ());
            this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            this.setAttackDecision(this.getTarget());
         }

         if (this.getDartTarget() != null && this.getDartTarget().isAlive() && this.getAnimation() == ANIMATION_THROW && this.getAnimationTick() == 5) {
            Vec3 vector3d = this.getDartTarget().getDeltaMovement();
            double d0 = this.getDartTarget().getX() + vector3d.x - this.getX();
            double d1 = this.getDartTarget().getEyeY() - 1.100000023841858 - this.getY();
            double d2 = this.getDartTarget().getZ() + vector3d.z - this.getZ();
            float f = Mth.sqrt((float)(d0 * d0 + d2 * d2));
            EntityTossedItem tossedItem = new EntityTossedItem(this.level(), this);
            tossedItem.setDart(this.hasDart());
            tossedItem.setXRot(tossedItem.getXRot() - 20.0F);
            tossedItem.shoot(d0, d1 + f * 0.2F, d2, this.hasDart() ? 1.15F : 0.75F, 8.0F);
            if (!this.isSilent()) {
               this.level()
                  .playSound(
                     null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F
                  );
               this.gameEvent(GameEvent.PROJECTILE_SHOOT);
            }

            this.level().addFreshEntity(tossedItem);
            this.setAttackDecision(this.getDartTarget());
         }
      }

      if (this.rideCooldown > 0) {
         this.rideCooldown--;
      }

      if (!this.level().isClientSide() && this.getAnimation() == NO_ANIMATION && this.getRandom().nextInt(300) == 0) {
         this.setAnimation(ANIMATION_HEADTILT);
      }

      if (!this.level().isClientSide() && this.isSitting()) {
         this.getNavigation().stop();
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_SCRATCH);
      }

      return true;
   }

   public void travel(Vec3 vec3d) {
      if (this.isSitting()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.hasDart()) {
         AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.ANCIENT_DART.get());
      }
   }

   public void rideTick() {
      Entity entity = this.getVehicle();
      if (this.isPassenger() && !entity.isAlive()) {
         this.stopRiding();
      } else if (this.isTame() && entity instanceof LivingEntity && this.isOwnedBy((LivingEntity)entity)) {
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.tick();
         if (this.isPassenger()) {
            Entity mount = this.getVehicle();
            if (mount instanceof Player player) {
               this.yBodyRot = player.yBodyRot;
               this.setYRot(player.getYRot());
               this.yHeadRot = player.yHeadRot;
               this.yRotO = player.yHeadRot;
               float radius = 0.0F;
               float angle = 0.017453292F * (((LivingEntity)mount).yBodyRot - 180.0F);
               double extraX = 0.0F * Mth.sin(3.1415927F + angle);
               double extraZ = 0.0F * Mth.cos(angle);
               this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getBbHeight() + 0.1, mount.getY()), mount.getZ() + extraZ);
               this.attackDecision = true;
               if (!mount.isAlive() || this.rideCooldown == 0 && mount.isShiftKeyDown()) {
                  this.removeVehicle();
                  this.attackDecision = false;
               }
            }
         }
      } else {
         super.rideTick();
      }
   }

   public void setAttackDecision(Entity target) {
      if (!(target instanceof Monster) && !this.hasDart()) {
         this.attackDecision = !this.attackDecision;
      } else {
         this.attackDecision = true;
      }
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public boolean hasDartTarget() {
      return (Integer)this.entityData.get(DART_TARGET) != -1 && this.hasDart();
   }

   public void setDartTarget(Entity entity) {
      this.entityData.set(DART_TARGET, entity == null ? -1 : entity.getId());
      if (entity instanceof LivingEntity target) {
         this.setTarget(target);
      }
   }

   @Nullable
   public Entity getDartTarget() {
      if (!this.hasDartTarget()) {
         return this.getTarget();
      } else {
         Entity entity = this.level().getEntity((Integer)this.entityData.get(DART_TARGET));
         return (Entity)(entity != null && entity.isAlive() ? entity : this.getTarget());
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(COMMAND, 0);
      builder.define(DART_TARGET, -1);
      builder.define(SITTING, false);
      builder.define(DART, false);
      builder.define(VARIANT, 0);
   }

   public boolean hasDart() {
      return (Boolean)this.entityData.get(DART);
   }

   public void setDart(boolean dart) {
      this.entityData.set(DART, dart);
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      EntityCapuchinMonkey monkey = AMCompat.create(AMEntityRegistry.CAPUCHIN_MONKEY.get(), p_241840_1_);
      monkey.setVariant(this.getVariant());
      return monkey;
   }

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (isTameableFood(itemstack)) {
         if (!this.isTame()) {
            this.usePlayerItem(player, hand, itemstack);
            if (this.getRandom().nextInt(5) == 0) {
               this.tame(player);
               this.level().broadcastEntityEvent(this, (byte)7);
            } else {
               this.level().broadcastEntityEvent(this, (byte)6);
            }

            return InteractionResult.SUCCESS;
         }

         if (this.isTame() && this.getAllFoods().test(itemstack) && !this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5.0F);
            return InteractionResult.SUCCESS;
         }
      }

      InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (interactionresult == InteractionResult.SUCCESS
         || type == InteractionResult.SUCCESS
         || !this.isTame()
         || !this.isOwnedBy(player)
         || this.isFood(itemstack)
         || isTameableFood(itemstack)
         || this.getAllFoods().test(itemstack)) {
         return type;
      } else if (!this.hasDart() && itemstack.getItem() == AMItemRegistry.ANCIENT_DART.get()) {
         this.setDart(true);
         this.usePlayerItem(player, hand, itemstack);
         return InteractionResult.CONSUME;
      } else if (this.hasDart() && itemstack.is(Items.TOOLS_SHEAR)) {
         this.setDart(false);
         AMCompat.hurtAndBreak(itemstack, 1, this, EquipmentSlot.MAINHAND);
         return InteractionResult.SUCCESS;
      } else if (player.isShiftKeyDown() && player.getPassengers().isEmpty()) {
         this.startRiding(player);
         this.rideCooldown = 20;
         return InteractionResult.SUCCESS;
      } else {
         this.setCommand(this.getCommand() + 1);
         if (this.getCommand() == 3) {
            this.setCommand(0);
         }

         player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
         boolean sit = this.getCommand() == 2;
         if (sit) {
            this.forcedSit = true;
            this.setOrderedToSit(true);
         } else {
            this.forcedSit = false;
            this.setOrderedToSit(false);
         }

         return InteractionResult.SUCCESS;
      }
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_THROW, ANIMATION_SCRATCH};
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return this.getAllFoods().test(stack) || isTameableFood(stack);
   }

   public boolean isFood(ItemStack stack) {
      Item item = stack.getItem();
      return this.isTame() && stack.is(AMTagRegistry.CAPUCHIN_MONKEY_BREEDABLES);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.heal(5.0F);
      this.gameEvent(GameEvent.EAT);
      this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
      if (e.getItem().is(AMTagRegistry.BANANAS) && this.getRandom().nextInt(4) == 0) {
         AMCompat.spawnAtLocation(this, new ItemStack((ItemLike)AMBlockRegistry.BANANA_PEEL.get()));
      }

      Entity itemThrower = e.getOwner();
      if (e.getItem().is(AMTagRegistry.CAPUCHIN_MONKEY_TAMEABLES) && itemThrower != null && !this.isTame()) {
         if (this.getRandom().nextInt(5) == 0) {
            AMCompat.setTame(this, true);
            AMCompat.setOwnerUUID(this, itemThrower.getUUID());
            this.level().broadcastEntityEvent(this, (byte)7);
         } else {
            this.level().broadcastEntityEvent(this, (byte)6);
         }
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance diff, MobSpawnType spawnType, @Nullable SpawnGroupData data) {
      int i;
      if (data instanceof EntityCapuchinMonkey.CapuchinGroupData) {
         i = ((EntityCapuchinMonkey.CapuchinGroupData)data).variant;
      } else {
         i = this.random.nextInt(4);
         data = new EntityCapuchinMonkey.CapuchinGroupData(i);
      }

      this.setVariant(i);
      return super.finalizeSpawn(world, diff, spawnType, data);
   }

   public static class CapuchinGroupData extends AgeableMobGroupData {
      public final int variant;

      CapuchinGroupData(int variant) {
         super(true);
         this.variant = variant;
      }
   }
}
