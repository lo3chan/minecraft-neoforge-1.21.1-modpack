package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityCatfish extends WaterAnimal implements FlyingAnimal, Bucketable, ContainerListener {
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> CATFISH_SIZE = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SPIT_TIME = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> HAS_SWALLOWED_ENTITY = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<String> SWALLOWED_ENTITY_TYPE = SynchedEntityData.defineId(EntityCatfish.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<CompoundTag> SWALLOWED_ENTITY_DATA = SynchedEntityData.defineId(
      EntityCatfish.class, EntityDataSerializers.COMPOUND_TAG
   );
   private static final EntityDimensions SMALL_SIZE = EntityDimensions.scalable(0.9F, 0.6F);
   private static final EntityDimensions MEDIUM_SIZE = EntityDimensions.scalable(1.25F, 0.9F);
   private static final EntityDimensions LARGE_SIZE = EntityDimensions.scalable(1.9F, 0.9F);
   public static final ResourceLocation MEDIUM_LOOT = AMCompat.rl("alexsmobs", "entities/catfish_medium");
   public static final ResourceLocation LARGE_LOOT = AMCompat.rl("alexsmobs", "entities/catfish_large");
   public SimpleContainer catfishInventory;
   private int eatCooldown = 0;

   protected EntityCatfish(EntityType<? extends WaterAnimal> type, Level level) {
      super(type, level);
      this.initCatfishInventory();
      this.moveControl = new AquaticMoveController(this, 1.0F, 15.0F);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public int getMaxSpawnClusterSize() {
      return 2;
   }

   public boolean isMaxGroupSizeReached(int sze) {
      return sze > 2;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(2, new PanicGoal(this, 1.0));
      this.goalSelector.addGoal(3, new EntityCatfish.TargetFoodGoal(this));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.0, AMCompat.ingredientOf(AMTagRegistry.CATFISH_ITEM_FASCINATIONS), false));
      this.goalSelector.addGoal(5, new EntityCatfish.FascinateLanternGoal(this));
      this.goalSelector.addGoal(6, new AnimalAISwimBottom(this, 1.0, 7));
   }

   public boolean removeWhenFarAway(double p_27492_) {
      return !this.fromBucket() && !this.requiresCustomPersistence() && !this.hasCustomName();
   }

   private void initCatfishInventory() {
      SimpleContainer animalchest = this.catfishInventory;
      int size = this.getCatfishSize() > 2 ? 1 : (this.getCatfishSize() == 1 ? 9 : 3);
      this.catfishInventory = new SimpleContainer(size) {
         public boolean stillValid(Player player) {
            return EntityCatfish.this.isAlive() && EntityCatfish.this.portalProcess == null;
         }
      };
      this.catfishInventory.addListener(this);
      if (animalchest != null) {
         int i = Math.min(animalchest.getContainerSize(), this.catfishInventory.getContainerSize());

         for (int j = 0; j < i; j++) {
            ItemStack itemstack = animalchest.getItem(j);
            if (!itemstack.isEmpty()) {
               this.catfishInventory.setItem(j, itemstack.copy());
            }
         }
      }
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.catfishInventory != null) {
         for (int i = 0; i < this.catfishInventory.getContainerSize(); i++) {
            AMCompat.spawnAtLocation(this, this.catfishInventory.getItem(i));
         }

         this.catfishInventory.clearContent();
      }

      if (this.getCatfishSize() == 2) {
         this.spit();
      }
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence()
         || this.hasCustomName()
         || this.fromBucket()
         || this.hasSwallowedEntity()
         || this.catfishInventory != null && !this.catfishInventory.isEmpty();
   }

   public static boolean canCatfishSpawn(
      EntityType<EntityCatfish> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER || iServerWorld.getBlockState(pos).getFluidState().is(Fluids.WATER) && random.nextInt(1) == 0;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.catfishSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FROM_BUCKET, false);
      builder.define(CATFISH_SIZE, 0);
      builder.define(SPIT_TIME, 0);
      builder.define(SWALLOWED_ENTITY_TYPE, "minecraft:pig");
      builder.define(SWALLOWED_ENTITY_DATA, new CompoundTag());
      builder.define(HAS_SWALLOWED_ENTITY, false);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.getSpitTime() > 0) {
            this.setSpitTime(this.getSpitTime() - 1);
         }

         if (this.eatCooldown > 0) {
            this.eatCooldown--;
         }
      }
   }

   public void aiStep() {
      super.aiStep();
      boolean inSeaPickle = false;
      int width = (int)Math.ceil(this.getBbWidth() / 2.0F);
      int height = (int)Math.ceil(this.getBbHeight() / 2.0F);
      MutableBlockPos pos = this.blockPosition().mutable();
      BlockPos vomitTo = null;

      for (int i = -width; i <= width; i++) {
         for (int j = -height; j <= height; j++) {
            for (int k = -width; k <= width; k++) {
               pos.set(this.getX() + i, this.getY() + j, this.getZ() + k);
               if (this.level().getBlockState(pos).is(Blocks.SEA_PICKLE)) {
                  inSeaPickle = true;
                  vomitTo = pos;
                  break;
               }
            }
         }
      }

      if (inSeaPickle && this.canSpit()) {
         if (this.getSpitTime() == 0) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.PLAYER_BURP, this.getSoundVolume(), this.getVoicePitch());
         }

         if (vomitTo != null) {
            Vec3 face = Vec3.atCenterOf(vomitTo).subtract(this.getMouthVec());
            double d0 = face.horizontalDistance();
            this.setXRot((float)(-Mth.atan2(face.y, d0) * 57.2957763671875));
            this.setYRot((float)Mth.atan2(face.z, face.x) * 57.295776F - 90.0F);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
         }

         this.spit();
      }
   }

   @Nullable
   protected ResourceKey<LootTable> getDefaultLootTable() {
      if (this.getCatfishSize() == 2) {
         return AMCompat.lootKey(LARGE_LOOT);
      } else {
         return this.getCatfishSize() == 1 ? AMCompat.lootKey(MEDIUM_LOOT) : super.getDefaultLootTable();
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
      if (CATFISH_SIZE.equals(accessor)) {
         this.refreshDimensions();
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10.0F * this.getCatfishSize() + 10.0F);
         this.heal(50.0F);
      }

      super.onSyncedDataUpdated(accessor);
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean bucketed) {
      this.entityData.set(FROM_BUCKET, bucketed);
   }

   public void saveToBucketTag(@Nonnull ItemStack bucket) {
      if (this.hasCustomName()) {
         AMCompat.setHoverName(bucket, this.getCustomName());
      }

      Bucketable.saveDefaultDataToBucketTag(this, bucket);
      CompoundTag compound = AMCompat.getOrCreateTag(bucket);
      AMCompat.saveAdditionalTo(this, compound);
      AMCompat.setTag(bucket, compound);
   }

   public void loadFromBucketTag(@Nonnull CompoundTag compound) {
      Bucketable.loadDefaultDataFromBucketTag(this, compound);
      AMCompat.readAdditionalFrom(this, compound);
   }

   public ItemStack getBucketItemStack() {
      int catfishSize = this.getCatfishSize();

      Item item = switch (catfishSize) {
         case 1 -> (Item)AMItemRegistry.MEDIUM_CATFISH_BUCKET.get();
         case 2 -> (Item)AMItemRegistry.LARGE_CATFISH_BUCKET.get();
         default -> (Item)AMItemRegistry.SMALL_CATFISH_BUCKET.get();
      };
      return new ItemStack(item);
   }

   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_FISH;
   }

   public int getCatfishSize() {
      return Mth.clamp((Integer)this.entityData.get(CATFISH_SIZE), 0, 2);
   }

   public void setCatfishSize(int catfishSize) {
      this.entityData.set(CATFISH_SIZE, catfishSize);
   }

   public int getSpitTime() {
      return (Integer)this.entityData.get(SPIT_TIME);
   }

   public void setSpitTime(int time) {
      this.entityData.set(SPIT_TIME, time);
   }

   public boolean isSpitting() {
      return this.getSpitTime() > 0;
   }

   public String getSwallowedEntityType() {
      return (String)this.entityData.get(SWALLOWED_ENTITY_TYPE);
   }

   public void setSwallowedEntityType(String containedEntityType) {
      this.entityData.set(SWALLOWED_ENTITY_TYPE, containedEntityType);
   }

   public CompoundTag getSwallowedData() {
      return (CompoundTag)this.entityData.get(SWALLOWED_ENTITY_DATA);
   }

   public void setSwallowedData(CompoundTag containedData) {
      this.entityData.set(SWALLOWED_ENTITY_DATA, containedData);
   }

   public boolean hasSwallowedEntity() {
      return (Boolean)this.entityData.get(HAS_SWALLOWED_ENTITY);
   }

   public void setHasSwallowedEntity(boolean swallowedEntity) {
      this.entityData.set(HAS_SWALLOWED_ENTITY, swallowedEntity);
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.getDimsForCatfish().scale(this.getScale());
   }

   public boolean hurt(DamageSource source, float f) {
      if (super.hurt(source, f)) {
         this.spit();
         return true;
      } else {
         return false;
      }
   }

   @Nonnull
   protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (stack.getItem() == Items.SEA_PICKLE) {
         this.spit();
         return AMCompat.sidedSuccess(this.level().isClientSide());
      } else {
         return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FromBucket", this.fromBucket());
      compound.putFloat("CatfishSize", this.getCatfishSize());
      if (this.catfishInventory != null) {
         ListTag nbttaglist = new ListTag();

         for (int i = 0; i < this.catfishInventory.getContainerSize(); i++) {
            ItemStack itemstack = this.catfishInventory.getItem(i);
            if (!itemstack.isEmpty()) {
               CompoundTag CompoundNBT = new CompoundTag();
               CompoundNBT.putByte("Slot", (byte)i);
               AMCompat.saveInto(this.level().registryAccess(), itemstack, CompoundNBT);
               nbttaglist.add(CompoundNBT);
            }
         }

         AMCompat.put(compound, "Items", nbttaglist);
      }

      compound.putString("ContainedEntityType", this.getSwallowedEntityType());
      AMCompat.put(compound, "ContainedData", this.getSwallowedData());
      compound.putBoolean("HasSwallowedEntity", this.hasSwallowedEntity());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFromBucket(AMCompat.getBoolean(compound, "FromBucket"));
      this.setCatfishSize(AMCompat.getInt(compound, "CatfishSize"));
      if (this.catfishInventory != null) {
         ListTag nbttaglist = AMCompat.getList(compound, "Items", 10);
         this.initCatfishInventory();

         for (int i = 0; i < nbttaglist.size(); i++) {
            CompoundTag CompoundNBT = AMCompat.getCompound(nbttaglist, i);
            int j = AMCompat.getByte(CompoundNBT, "Slot") & 255;
            this.catfishInventory.setItem(j, AMCompat.loadItem(this.level().registryAccess(), CompoundNBT));
         }
      }

      this.setSwallowedEntityType(AMCompat.getString(compound, "ContainedEntityType"));
      if (!AMCompat.getCompound(compound, "ContainedData").isEmpty()) {
         this.setSwallowedData(AMCompat.getCompound(compound, "ContainedData"));
      }

      this.setHasSwallowedEntity(AMCompat.getBoolean(compound, "HasSwallowedEntity"));
   }

   private EntityDimensions getDimsForCatfish() {
      return switch (this.getCatfishSize()) {
         case 1 -> MEDIUM_SIZE;
         case 2 -> LARGE_SIZE;
         default -> SMALL_SIZE;
      };
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setCatfishSize(this.random.nextFloat() < 0.35F ? 1 : 0);
      if (this.random.nextFloat() < 0.1F) {
         Holder<Biome> holder = worldIn.getBiome(this.blockPosition());
         if (holder.is(AMTagRegistry.SPAWNS_HUGE_CATFISH) || reason == MobSpawnType.SPAWN_EGG) {
            this.setCatfishSize(2);
         }
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public void travel(Vec3 travelVector) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), travelVector);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(travelVector);
      }
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
   }

   public void containerChanged(Container p_18983_) {
   }

   protected void pickUpItem(ItemEntity itemEntity) {
      ItemStack itemstack = itemEntity.getItem();
      if (this.getCatfishSize() != 2 && !this.isFull() && this.catfishInventory != null && this.catfishInventory.addItem(itemstack).isEmpty()) {
         this.onItemPickup(itemEntity);
         this.take(itemEntity, itemstack.getCount());
         itemEntity.discard();
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
      }
   }

   public boolean isFull() {
      if (this.getCatfishSize() != 2 && this.catfishInventory != null) {
         for (int i = 0; i < this.catfishInventory.getContainerSize(); i++) {
            if (this.catfishInventory.getItem(i).isEmpty()) {
               return false;
            }
         }

         return true;
      } else {
         return this.hasSwallowedEntity();
      }
   }

   public float getVoicePitch() {
      float f = (3 - this.getCatfishSize()) * 0.33F;
      return (float)(super.getVoicePitch() * Math.sqrt(f) * 1.2000000476837158);
   }

   public boolean swallowEntity(Entity entity) {
      if (this.getCatfishSize() == 2 && entity instanceof Mob mob) {
         this.setHasSwallowedEntity(true);
         ResourceLocation mobtype = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
         if (mobtype != null) {
            this.setSwallowedEntityType(mobtype.toString());
         }

         CompoundTag tag = new CompoundTag();
         AMCompat.saveAdditionalTo(mob, tag);
         this.setSwallowedData(tag);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
         return true;
      } else {
         if (this.getCatfishSize() < 2 && entity instanceof ItemEntity item) {
            this.pickUpItem(item);
         }

         return false;
      }
   }

   public boolean canSpit() {
      return this.getCatfishSize() == 2 ? this.hasSwallowedEntity() : this.catfishInventory != null && !this.catfishInventory.isEmpty();
   }

   public void spit() {
      this.setSpitTime(10);
      this.eatCooldown = 60 + this.random.nextInt(60);
      if (this.getCatfishSize() == 2) {
         if (this.hasSwallowedEntity()) {
            EntityType type = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(AMCompat.rl(this.getSwallowedEntityType()));
            if (type != null && AMCompat.create(type, this.level()) instanceof LivingEntity alive) {
               AMCompat.readAdditionalFrom(alive, this.getSwallowedData());
               alive.setHealth(Math.max(2.0F, alive.getMaxHealth() * 0.25F));
               alive.setYRot(this.random.nextFloat() * 360.0F - 180.0F);
               alive.setPos(this.getMouthVec());
               if (this.level().addFreshEntity(alive)) {
                  this.setHasSwallowedEntity(false);
                  this.setSwallowedEntityType("minecraft:pig");
                  this.setSwallowedData(new CompoundTag());
               }
            }
         }
      } else {
         ItemStack itemStack = ItemStack.EMPTY;
         int index = -1;
         if (this.catfishInventory != null) {
            for (int i = 0; i < this.catfishInventory.getContainerSize(); i++) {
               if (!this.catfishInventory.getItem(i).isEmpty()) {
                  itemStack = this.catfishInventory.getItem(i);
                  index = i;
                  break;
               }
            }
         }

         if (!itemStack.isEmpty()) {
            Vec3 vec3 = this.getMouthVec();
            Vec3 vec32 = vec3.subtract(this.position()).normalize().scale(0.14000000059604645);
            ItemEntity item = new ItemEntity(this.level(), vec3.x, vec3.y, vec3.z, itemStack, vec32.x, vec32.y, vec32.z);
            item.setDeltaMovement(Vec3.ZERO);
            item.setPickUpDelay(30);
            if (this.level().addFreshEntity(item) && this.catfishInventory != null) {
               this.catfishInventory.setItem(index, ItemStack.EMPTY);
            }
         }
      }
   }

   private Vec3 getMouthVec() {
      Vec3 vec3 = new Vec3(0.0, this.getBbHeight() * 0.25F, this.getBbWidth() * 0.8F).xRot(this.getXRot() * 0.017453292F).yRot(-this.getYRot() * 0.017453292F);
      return this.position().add(vec3);
   }

   private boolean isFood(Entity entity) {
      return this.getCatfishSize() == 2
         ? !entity.getType().builtInRegistryHolder().is(AMTagRegistry.CATFISH_IGNORE_EATING)
            && entity instanceof Mob
            && !(entity instanceof EntityCatfish)
            && entity.getBbHeight() <= 1.0F
         : entity instanceof ItemEntity && ((ItemEntity)entity).getAge() > 35;
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this));
      return result.getBlockPos().equals(destinationBlock);
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.COD_HURT;
   }

   public boolean isFlying() {
      return false;
   }

   private class FascinateLanternGoal extends Goal {
      private final int searchLength;
      private final int verticalSearchRange;
      protected BlockPos destinationBlock;
      private final EntityCatfish fish;
      private int runDelay = 70;
      private int chillTime = 0;
      private int maxChillTime = 200;

      private FascinateLanternGoal(EntityCatfish fish) {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.fish = fish;
         this.searchLength = 16;
         this.verticalSearchRange = 6;
      }

      public boolean canContinueToUse() {
         return this.destinationBlock != null
            && this.isSeaLantern(this.fish.level(), this.destinationBlock.mutable())
            && this.isCloseToLantern(16.0)
            && !this.fish.isFull();
      }

      public boolean isCloseToLantern(double dist) {
         return this.destinationBlock == null || this.fish.distanceToSqr(Vec3.atCenterOf(this.destinationBlock)) < dist * dist;
      }

      public boolean canUse() {
         if (!this.fish.isInWaterOrBubble()) {
            return false;
         } else if (this.runDelay > 0) {
            this.runDelay--;
            return false;
         } else {
            this.runDelay = 70 + this.fish.random.nextInt(70);
            return !this.fish.isFull() && this.searchForDestination();
         }
      }

      public void start() {
         this.chillTime = 0;
         this.maxChillTime = 10 + EntityCatfish.this.random.nextInt(20);
      }

      public void tick() {
         Vec3 vec = Vec3.atCenterOf(this.destinationBlock);
         this.fish.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.0);
         if (this.fish.distanceToSqr(vec) < 1.0F + this.fish.getBbWidth() * 0.6F) {
            Vec3 face = vec.subtract(this.fish.position());
            this.fish.setDeltaMovement(this.fish.getDeltaMovement().add(face.normalize().scale(0.10000000149011612)));
            if (this.chillTime++ > this.maxChillTime) {
               this.destinationBlock = null;
            }
         }
      }

      public void stop() {
         this.destinationBlock = null;
      }

      protected boolean searchForDestination() {
         int lvt_1_1_ = this.searchLength;
         BlockPos lvt_3_1_ = this.fish.blockPosition();
         MutableBlockPos lvt_4_1_ = new MutableBlockPos();

         for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; lvt_6_1_++) {
               for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                  for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0;
                     lvt_8_1_ <= lvt_6_1_;
                     lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_
                  ) {
                     lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                     if (this.isSeaLantern(this.fish.level(), lvt_4_1_) && this.fish.canSeeBlock(lvt_4_1_)) {
                        this.destinationBlock = lvt_4_1_;
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }

      private boolean isSeaLantern(Level world, MutableBlockPos pos) {
         return world.getBlockState(pos).is(AMTagRegistry.CATFISH_BLOCK_FASCINATIONS);
      }
   }

   private class TargetFoodGoal extends Goal {
      private final EntityCatfish catfish;
      private Entity food;
      private int executionCooldown = 50;

      public TargetFoodGoal(EntityCatfish catfish) {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.catfish = catfish;
      }

      public boolean canUse() {
         if (this.catfish.isInWaterOrBubble() && this.catfish.eatCooldown <= 0) {
            if (this.executionCooldown > 0) {
               this.executionCooldown--;
            } else {
               this.executionCooldown = 50 + EntityCatfish.this.random.nextInt(50);
               if (!this.catfish.isFull()) {
                  List<Entity> list = this.catfish
                     .level()
                     .getEntitiesOfClass(
                        Entity.class,
                        this.catfish.getBoundingBox().inflate(8.0, 8.0, 8.0),
                        EntitySelector.NO_SPECTATORS.and(entity -> entity != this.catfish && this.catfish.isFood(entity))
                     );
                  list.sort(Comparator.comparingDouble(this.catfish::distanceToSqr));
                  if (!list.isEmpty()) {
                     this.food = list.get(0);
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.food != null && this.food.isAlive() && !this.catfish.isFull();
      }

      public void stop() {
         this.executionCooldown = 5;
      }

      public void tick() {
         this.catfish.getNavigation().moveTo(this.food.getX(), this.food.getY(0.5), this.food.getZ(), 1.0);
         float eatDist = this.catfish.getBbWidth() * 0.65F + this.food.getBbWidth();
         if (this.catfish.distanceTo(this.food) < eatDist + 3.0F && this.catfish.hasLineOfSight(this.food)) {
            Vec3 delta = this.catfish.getMouthVec().subtract(this.food.position()).normalize().scale(0.10000000149011612);
            this.food.setDeltaMovement(this.food.getDeltaMovement().add(delta));
            if (this.catfish.distanceTo(this.food) < eatDist) {
               if (this.food instanceof Player) {
                  this.food.hurt(this.catfish.damageSources().mobAttack(this.catfish), 12000.0F);
               } else if (this.catfish.swallowEntity(this.food)) {
                  this.catfish.gameEvent(GameEvent.EAT);
                  this.catfish.playSound(SoundEvents.GENERIC_EAT, this.catfish.getSoundVolume(), this.catfish.getVoicePitch());
                  this.food.discard();
               }
            }
         }
      }
   }
}
