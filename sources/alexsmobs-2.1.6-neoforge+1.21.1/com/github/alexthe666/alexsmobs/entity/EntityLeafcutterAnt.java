package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.LeafcutterAntAIFollowCaravan;
import com.github.alexthe666.alexsmobs.entity.ai.LeafcutterAntAIForageLeaves;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAITempt;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.google.common.base.Predicates;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityLeafcutterAnt extends Animal implements NeutralMob, IAnimatedEntity {
   public static final Animation ANIMATION_BITE = Animation.create(13);
   protected static final EntityDimensions QUEEN_SIZE = EntityDimensions.fixed(1.25F, 0.98F);
   public static final ResourceLocation QUEEN_LOOT = AMCompat.rl("alexsmobs", "entities/leafcutter_ant_queen");
   private static final EntityDataAccessor<Optional<BlockPos>> LEAF_HARVESTED_POS = SynchedEntityData.defineId(
      EntityLeafcutterAnt.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private static final EntityDataAccessor<Optional<BlockState>> LEAF_HARVESTED_STATE = SynchedEntityData.defineId(
      EntityLeafcutterAnt.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE
   );
   private static final EntityDataAccessor<Boolean> HAS_LEAF = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> ANT_SCALE = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.DIRECTION);
   private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Boolean> QUEEN = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ANGER_TIME = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.INT);
   private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private static final UniformInt ANGRY_TIMER = TimeUtil.rangeOfSeconds(10, 20);
   public float attachChangeProgress = 0.0F;
   public float prevAttachChangeProgress = 0.0F;
   private Direction prevAttachDir = Direction.DOWN;
   @Nullable
   private EntityLeafcutterAnt caravanHead;
   @Nullable
   private EntityLeafcutterAnt caravanTail;
   private UUID lastHurtBy;
   @Nullable
   private BlockPos hivePos = null;
   private int stayOutOfHiveCountdown;
   private int animationTick;
   private Animation currentAnimation;
   private boolean isUpsideDownNavigator;
   private static final Supplier<Ingredient> TEMPTATION_ITEMS = AMCompat.lazyIngredient(() -> AMCompat.ingredientOf(AMTagRegistry.LEAFCUTTER_ANT_FOODSTUFFS));
   private int haveBabyCooldown = 0;

   public boolean isFood(ItemStack stack) {
      return stack.is(Items.WHEAT);
   }

   public EntityLeafcutterAnt(EntityType type, Level world) {
      super(type, world);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.switchNavigator(true);
   }

   public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
      if (!(entitylivingbaseIn instanceof Player) || !((Player)entitylivingbaseIn).isCreative()) {
         super.setTarget(entitylivingbaseIn);
      }
   }

   @Nullable
   protected ResourceKey<LootTable> getDefaultLootTable() {
      return this.isQueen() ? AMCompat.lootKey(QUEEN_LOOT) : super.getDefaultLootTable();
   }

   private void switchNavigator(boolean rightsideUp) {
      if (rightsideUp) {
         this.moveControl = new MoveControl(this);
         this.navigation = new AdvancedPathNavigateNoTeleport(this, this.level(), AdvancedPathNavigate.MovementType.WALKING, true, false);
         this.isUpsideDownNavigator = false;
      } else {
         this.moveControl = new FlightMoveController(this, 0.6F, false);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isUpsideDownNavigator = true;
      }
   }

   public boolean canCollideWith(Entity entity) {
      return !(entity instanceof EntityAnteater) && super.canCollideWith(entity);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 8.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   private static boolean isSideSolid(BlockGetter reader, BlockPos pos, Entity entityIn, Direction direction) {
      return Block.isFaceFull(reader.getBlockState(pos).getCollisionShape(reader, pos, CollisionContext.of(entityIn)), direction);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EntityLeafcutterAnt.ReturnToHiveGoal());
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(3, new TameableAITempt(this, 1.1, TEMPTATION_ITEMS.get(), false));
      this.goalSelector.addGoal(4, new LeafcutterAntAIFollowCaravan(this, 1.0));
      this.goalSelector.addGoal(5, new LeafcutterAntAIForageLeaves(this));
      this.goalSelector.addGoal(6, new AnimalAIWanderRanged(this, 30, 1.0, 25, 7));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new EntityLeafcutterAnt.AngerGoal(this).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal(this, true));
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.isQueen() && !this.isBaby() ? QUEEN_SIZE : super.getDefaultDimensions(poseIn);
   }

   public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
      return false;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.entityData.get(ATTACHED_FACE);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new WallClimberNavigation(this, worldIn);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isQueen() ? AMSoundRegistry.LEAFCUTTER_ANT_QUEEN_HURT.get() : AMSoundRegistry.LEAFCUTTER_ANT_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return this.isQueen() ? AMSoundRegistry.LEAFCUTTER_ANT_QUEEN_HURT.get() : AMSoundRegistry.LEAFCUTTER_ANT_HURT.get();
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   public void push(Entity entity) {
      if (!(entity instanceof EntityAnteater)) {
         super.push(entity);
      }
   }

   private void pacifyAllNearby() {
      this.stopBeingAngry();

      for (EntityLeafcutterAnt ant : this.level().getEntitiesOfClass(EntityLeafcutterAnt.class, this.getBoundingBox().inflate(20.0, 6.0, 20.0))) {
         ant.stopBeingAngry();
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (type != InteractionResult.SUCCESS && itemstack.is(AMTagRegistry.LEAFCUTTER_ANT_FOODSTUFFS)) {
         if (this.isQueen() && this.haveBabyCooldown == 0) {
            int babies = 1 + this.random.nextInt(1);
            this.pacifyAllNearby();

            for (int i = 0; i < babies; i++) {
               EntityLeafcutterAnt leafcutterAnt = AMCompat.create(AMEntityRegistry.LEAFCUTTER_ANT.get(), this.level());
               leafcutterAnt.copyPosition(this);
               leafcutterAnt.setAge(-24000);
               if (!this.level().isClientSide()) {
                  this.level().broadcastEntityEvent(this, (byte)18);
                  this.level().addFreshEntity(leafcutterAnt);
               }
            }

            if (!player.isCreative()) {
               itemstack.shrink(1);
            }

            this.haveBabyCooldown = 24000;
            this.setBaby(false);
         } else {
            this.pacifyAllNearby();
            if (!player.isCreative()) {
               itemstack.shrink(1);
            }

            this.level().broadcastEntityEvent(this, (byte)48);
            this.heal(3.0F);
         }

         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      if (id == 48) {
         for (int i = 0; i < 3; i++) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   public void tick() {
      this.prevAttachChangeProgress = this.attachChangeProgress;
      super.tick();
      if (this.isQueen() && this.getBbWidth() < AMCompat.width(QUEEN_SIZE)) {
         this.refreshDimensions();
      }

      if (this.attachChangeProgress > 0.0F) {
         this.attachChangeProgress -= 0.25F;
      }

      AMCompat.setMaxUpStep(this, this.isQueen() ? 1.0F : 0.5F);
      Vec3 vector3d = this.getDeltaMovement();
      if (!this.level().isClientSide() && !this.isQueen()) {
         this.setBesideClimbableBlock(this.horizontalCollision || this.verticalCollision && !this.onGround());
         if (this.onGround() || this.isInWaterOrBubble() || this.isInLava()) {
            this.entityData.set(ATTACHED_FACE, Direction.DOWN);
         } else if (this.verticalCollision) {
            this.entityData.set(ATTACHED_FACE, Direction.UP);
         } else {
            Direction closestDirection = Direction.DOWN;
            double closestDistance = 100.0;

            for (Direction dir : HORIZONTALS) {
               BlockPos antPos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
               BlockPos offsetPos = antPos.relative(dir);
               Vec3 offset = Vec3.atCenterOf(offsetPos);
               if (closestDistance > this.position().distanceTo(offset) && this.level().loadedAndEntityCanStandOnFace(offsetPos, this, dir.getOpposite())) {
                  closestDistance = this.position().distanceTo(offset);
                  closestDirection = dir;
               }
            }

            this.entityData.set(ATTACHED_FACE, closestDirection);
         }
      }

      boolean flag = false;
      Direction attachmentFacing = this.getAttachmentFacing();
      if (attachmentFacing != Direction.DOWN) {
         if (attachmentFacing == Direction.UP) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 1.0, 0.0));
         } else {
            if (!this.horizontalCollision && attachmentFacing != Direction.UP) {
               Vec3 vec = Vec3.atLowerCornerOf(attachmentFacing.getNormal());
               this.setDeltaMovement(this.getDeltaMovement().add(vec.normalize().multiply(0.10000000149011612, 0.10000000149011612, 0.10000000149011612)));
            }

            if (!this.onGround() && vector3d.y < 0.0) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.5, 1.0));
               flag = true;
            }
         }
      }

      if (attachmentFacing == Direction.UP) {
         this.setNoGravity(true);
         this.setDeltaMovement(vector3d.multiply(0.7, 1.0, 0.7));
      } else {
         this.setNoGravity(false);
      }

      if (!flag && this.onClimbable()) {
         this.setDeltaMovement(vector3d.multiply(1.0, 0.4, 1.0));
      }

      if (this.prevAttachDir != attachmentFacing) {
         this.attachChangeProgress = 1.0F;
      }

      this.prevAttachDir = attachmentFacing;
      if (!this.level().isClientSide()) {
         if (attachmentFacing == Direction.UP && !this.isUpsideDownNavigator) {
            this.switchNavigator(false);
         }

         if (attachmentFacing != Direction.UP && this.isUpsideDownNavigator) {
            this.switchNavigator(true);
         }

         if (this.stayOutOfHiveCountdown > 0) {
            this.stayOutOfHiveCountdown--;
         }

         if (this.tickCount % 20 == 0 && !this.isHiveValid()) {
            this.hivePos = null;
         }

         LivingEntity attackTarget = this.getTarget();
         if (attackTarget != null
            && this.distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 1.0F
            && this.hasLineOfSight(attackTarget)
            && this.getAnimation() == ANIMATION_BITE
            && this.getAnimationTick() == 6) {
            float damage = (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            attackTarget.hurt(this.damageSources().mobAttack(this), damage);
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private boolean isClimeableFromSide(BlockPos offsetPos, Direction opposite) {
      return false;
   }

   private boolean isHiveValid() {
      if (!this.hasHive()) {
         return false;
      } else {
         BlockEntity tileentity = this.level().getBlockEntity(this.hivePos);
         return tileentity instanceof TileEntityLeafcutterAnthill;
      }
   }

   protected void onInsideBlock(BlockState state) {
   }

   public boolean onClimbable() {
      return this.isBesideClimbableBlock();
   }

   public boolean isBesideClimbableBlock() {
      return ((Byte)this.entityData.get(CLIMBING) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = (Byte)this.entityData.get(CLIMBING);
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.entityData.set(CLIMBING, b0);
   }

   public int getRemainingPersistentAngerTime() {
      return (Integer)this.entityData.get(ANGER_TIME);
   }

   public void setRemainingPersistentAngerTime(int time) {
      this.entityData.set(ANGER_TIME, time);
   }

   public UUID getPersistentAngerTarget() {
      return this.lastHurtBy;
   }

   public void setPersistentAngerTarget(@Nullable UUID target) {
      this.lastHurtBy = target;
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(ANGRY_TIMER.sample(this.random));
   }

   protected void customServerAiStep() {
      if (!this.level().isClientSide()) {
         this.updatePersistentAnger((ServerLevel)this.level(), false);
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CLIMBING, (byte)0);
      builder.define(LEAF_HARVESTED_POS, Optional.empty());
      builder.define(LEAF_HARVESTED_STATE, Optional.empty());
      builder.define(HAS_LEAF, false);
      builder.define(QUEEN, false);
      builder.define(ATTACHED_FACE, Direction.DOWN);
      builder.define(ANT_SCALE, 1.0F);
      builder.define(ANGER_TIME, 0);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setAntScale(0.75F + this.random.nextFloat() * 0.3F);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public float getAntScale() {
      return (Float)this.entityData.get(ANT_SCALE);
   }

   public void setAntScale(float scale) {
      this.entityData.set(ANT_SCALE, scale);
   }

   public BlockPos getHarvestedPos() {
      return (BlockPos)((Optional)this.entityData.get(LEAF_HARVESTED_POS)).orElse(null);
   }

   public void setLeafHarvestedPos(BlockPos harvestedPos) {
      this.entityData.set(LEAF_HARVESTED_POS, Optional.ofNullable(harvestedPos));
   }

   public BlockState getHarvestedState() {
      return (BlockState)((Optional)this.entityData.get(LEAF_HARVESTED_STATE)).orElse(null);
   }

   public void setLeafHarvestedState(BlockState state) {
      this.entityData.set(LEAF_HARVESTED_STATE, Optional.ofNullable(state));
   }

   public boolean hasLeaf() {
      return (Boolean)this.entityData.get(HAS_LEAF);
   }

   public void setLeaf(boolean leaf) {
      this.entityData.set(HAS_LEAF, leaf);
   }

   public boolean isQueen() {
      return (Boolean)this.entityData.get(QUEEN);
   }

   public void setQueen(boolean queen) {
      boolean prev = this.isQueen();
      if (!prev && queen) {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(36.0);
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0);
         this.setHealth(36.0F);
      } else {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(6.0);
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0);
      }

      this.entityData.set(QUEEN, queen);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(ATTACHED_FACE, Direction.from3DDataValue(AMCompat.getByte(compound, "AttachFace")));
      this.setLeaf(AMCompat.getBoolean(compound, "Leaf"));
      this.setQueen(AMCompat.getBoolean(compound, "Queen"));
      this.setAntScale(AMCompat.getFloat(compound, "AntScale"));
      BlockState blockstate = null;
      if (AMCompat.contains(compound, "HarvestedLeafState", 10)) {
         blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), AMCompat.getCompound(compound, "HarvestedLeafState"));
         if (blockstate.isAir()) {
            blockstate = null;
         }
      }

      this.stayOutOfHiveCountdown = AMCompat.getInt(compound, "CannotEnterHiveTicks");
      this.haveBabyCooldown = AMCompat.getInt(compound, "BabyCooldown");
      this.hivePos = null;
      if (AMCompat.contains(compound, "HivePos")) {
         this.hivePos = AMCompat.readBlockPos(compound, "HivePos");
      }

      this.setLeafHarvestedState(blockstate);
      if (AMCompat.contains(compound, "HLPX")) {
         int i = AMCompat.getInt(compound, "HLPX");
         int j = AMCompat.getInt(compound, "HLPY");
         int k = AMCompat.getInt(compound, "HLPZ");
         this.entityData.set(LEAF_HARVESTED_POS, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.entityData.set(LEAF_HARVESTED_POS, Optional.empty());
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putByte("AttachFace", (byte)((Direction)this.entityData.get(ATTACHED_FACE)).get3DDataValue());
      compound.putBoolean("Leaf", this.hasLeaf());
      compound.putBoolean("Queen", this.isQueen());
      compound.putFloat("AntScale", this.getAntScale());
      BlockState blockstate = this.getHarvestedState();
      if (blockstate != null) {
         AMCompat.put(compound, "HarvestedLeafState", NbtUtils.writeBlockState(blockstate));
      }

      if (this.hasHive()) {
         AMCompat.put(compound, "HivePos", AMCompat.writeBlockPos(this.getHivePos()));
      }

      compound.putInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
      compound.putInt("BabyCooldown", this.haveBabyCooldown);
      BlockPos blockpos = this.getHarvestedPos();
      if (blockpos != null) {
         compound.putInt("HLPX", blockpos.getX());
         compound.putInt("HLPY", blockpos.getY());
         compound.putInt("HLPZ", blockpos.getZ());
      }
   }

   public void setStayOutOfHiveCountdown(int p_226450_1_) {
      this.stayOutOfHiveCountdown = p_226450_1_;
   }

   private boolean isHiveNearFire() {
      if (this.hivePos == null) {
         return false;
      } else {
         BlockEntity tileentity = this.level().getBlockEntity(this.hivePos);
         return tileentity instanceof TileEntityLeafcutterAnthill && ((TileEntityLeafcutterAnthill)tileentity).isNearFire();
      }
   }

   private boolean doesHiveHaveSpace(BlockPos pos) {
      BlockEntity tileentity = this.level().getBlockEntity(pos);
      return tileentity instanceof TileEntityLeafcutterAnthill ? !((TileEntityLeafcutterAnthill)tileentity).isFullOfAnts() : false;
   }

   public boolean hasHive() {
      return this.hivePos != null;
   }

   @Nullable
   public BlockPos getHivePos() {
      return this.hivePos;
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(EntityLeafcutterAnt caravanHeadIn) {
      this.caravanHead = caravanHeadIn;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTrail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public EntityLeafcutterAnt getCaravanHead() {
      return this.caravanHead;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return null;
   }

   public boolean shouldLeadCaravan() {
      return !this.hasLeaf();
   }

   public void calculateEntityAnimation(boolean flying) {
      float f1 = (float)Mth.length(this.getX() - this.xo, 2.0 * (this.getY() - this.yo), this.getZ() - this.zo);
      float f2 = Math.min(f1 * 4.0F, 1.0F);
      this.walkAnimation.update(f2, 0.4F);
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
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_BITE};
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public boolean doHurtTarget(Entity entityIn) {
      this.setAnimation(ANIMATION_BITE);
      return true;
   }

   class AngerGoal extends HurtByTargetGoal {
      AngerGoal(EntityLeafcutterAnt beeIn) {
         super(beeIn, new Class[0]);
         this.setAlertOthers(new Class[]{EntityLeafcutterAnt.class});
      }

      public boolean canContinueToUse() {
         return EntityLeafcutterAnt.this.isAngry() && super.canContinueToUse();
      }

      protected void alertOther(Mob mobIn, LivingEntity targetIn) {
         if (mobIn instanceof EntityLeafcutterAnt && this.mob.hasLineOfSight(targetIn)) {
            mobIn.setTarget(targetIn);
         }
      }
   }

   private class ReturnToHiveGoal extends Goal {
      private int searchCooldown = 1;
      private BlockPos hivePos;
      private int approachTime = 0;
      private int moveToCooldown = 0;

      public ReturnToHiveGoal() {
      }

      public boolean canUse() {
         if (EntityLeafcutterAnt.this.stayOutOfHiveCountdown > 0) {
            return false;
         } else {
            if (EntityLeafcutterAnt.this.hasLeaf() || EntityLeafcutterAnt.this.isQueen()) {
               this.searchCooldown--;
               BlockPos hive = EntityLeafcutterAnt.this.hivePos;
               if (hive != null && EntityLeafcutterAnt.this.level().getBlockEntity(hive) instanceof TileEntityLeafcutterAnthill) {
                  this.hivePos = hive;
                  return true;
               }

               if (this.searchCooldown <= 0) {
                  this.searchCooldown = 400;
                  PoiManager pointofinterestmanager = ((ServerLevel)EntityLeafcutterAnt.this.level()).getPoiManager();
                  Stream<BlockPos> stream = pointofinterestmanager.findAll(
                     poiTypeHolder -> poiTypeHolder.is(AMPointOfInterestRegistry.LEAFCUTTER_ANT_HILL_KEY),
                     Predicates.alwaysTrue(),
                     EntityLeafcutterAnt.this.blockPosition(),
                     100,
                     Occupancy.ANY
                  );
                  List<BlockPos> listOfHives = stream.collect(Collectors.toList());
                  BlockPos ret = null;

                  for (BlockPos pos : listOfHives) {
                     if (ret == null || pos.distSqr(EntityLeafcutterAnt.this.blockPosition()) < ret.distSqr(EntityLeafcutterAnt.this.blockPosition())) {
                        ret = pos;
                     }
                  }

                  this.hivePos = ret;
                  EntityLeafcutterAnt.this.hivePos = ret;
                  return this.hivePos != null;
               }
            }

            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.hivePos != null && EntityLeafcutterAnt.this.distanceToSqr(Vec3.upFromBottomCenterOf(this.hivePos, 1.0)) > 1.0;
      }

      public void stop() {
         this.hivePos = null;
         this.searchCooldown = 20;
         this.approachTime = 0;
      }

      public void start() {
         this.searchCooldown = 20;
         this.approachTime = 0;
         this.moveToCooldown = 10 + EntityLeafcutterAnt.this.random.nextInt(10);
      }

      public void tick() {
         if (this.moveToCooldown > 0) {
            this.moveToCooldown--;
         }

         if (this.hivePos != null) {
            double dist = EntityLeafcutterAnt.this.distanceToSqr(Vec3.upFromBottomCenterOf(this.hivePos, 1.0));
            if (dist < 1.2000000476837158
               && EntityLeafcutterAnt.this.getBlockPosBelowThatAffectsMyMovement().equals(this.hivePos)
               && EntityLeafcutterAnt.this.level().getBlockEntity(this.hivePos) instanceof TileEntityLeafcutterAnthill beehivetileentity) {
               beehivetileentity.tryEnterHive(EntityLeafcutterAnt.this, EntityLeafcutterAnt.this.hasLeaf());
            }

            if (dist < 16.0) {
               this.approachTime++;
               if (dist < 4.0) {
                  Vec3 center = Vec3.upFromBottomCenterOf(this.hivePos, 1.100000023841858);
                  Vec3 add = center.subtract(EntityLeafcutterAnt.this.position());
                  if (add.length() > 1.0) {
                     add = add.normalize();
                  }

                  add = add.scale(0.20000000298023224);
                  EntityLeafcutterAnt.this.setDeltaMovement(EntityLeafcutterAnt.this.getDeltaMovement().add(add));
               }

               if (dist < (this.approachTime < 200 ? 2 : 10) && EntityLeafcutterAnt.this.getY() >= this.hivePos.getY()) {
                  if (EntityLeafcutterAnt.this.getAttachmentFacing() != Direction.DOWN) {
                     EntityLeafcutterAnt.this.setDeltaMovement(EntityLeafcutterAnt.this.getDeltaMovement().add(0.0, 0.1, 0.0));
                  }

                  EntityLeafcutterAnt.this.getMoveControl()
                     .setWantedPosition(this.hivePos.getX() + 0.5, this.hivePos.getY() + 1.5, this.hivePos.getZ() + 0.5, 1.0);
               }

               if (this.moveToCooldown <= 0) {
                  this.moveToCooldown = 50 + EntityLeafcutterAnt.this.random.nextInt(30);
                  EntityLeafcutterAnt.this.navigation.resetMaxVisitedNodesMultiplier();
                  EntityLeafcutterAnt.this.navigation
                     .moveTo(this.hivePos.getX() + 0.5, this.hivePos.getY() + 1.600000023841858, this.hivePos.getZ() + 0.5, 1.0);
               }
            } else {
               this.startMovingToFar(this.hivePos);
            }
         }
      }

      private boolean startMovingToFar(BlockPos pos) {
         if (this.moveToCooldown <= 0) {
            this.moveToCooldown = 50 + EntityLeafcutterAnt.this.random.nextInt(30);
            EntityLeafcutterAnt.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
            EntityLeafcutterAnt.this.navigation.moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
         }

         return EntityLeafcutterAnt.this.navigation.getPath() != null && EntityLeafcutterAnt.this.navigation.getPath().canReach();
      }
   }
}
