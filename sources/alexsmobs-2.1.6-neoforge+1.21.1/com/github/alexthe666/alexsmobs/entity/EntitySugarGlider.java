package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.SmartClimbPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class EntitySugarGlider extends TamableAnimal implements IFollower {
   public static final ResourceLocation SUGAR_GLIDER_REWARD = AMCompat.rl("alexsmobs", "gameplay/sugar_glider_reward");
   public static final Map<Block, Item> LEAF_TO_SAPLING = (Map<Block, Item>)Util.make(Maps.newHashMap(), map -> {
      map.put(Blocks.OAK_LEAVES, Items.OAK_SAPLING);
      map.put(Blocks.BIRCH_LEAVES, Items.BIRCH_SAPLING);
      map.put(Blocks.SPRUCE_LEAVES, Items.SPRUCE_SAPLING);
      map.put(Blocks.JUNGLE_LEAVES, Items.JUNGLE_SAPLING);
      map.put(Blocks.ACACIA_LEAVES, Items.ACACIA_SAPLING);
      map.put(Blocks.DARK_OAK_LEAVES, Items.DARK_OAK_SAPLING);
      map.put(Blocks.MANGROVE_LEAVES, Items.MANGROVE_PROPAGULE);
   });
   public static final Map<Block, List<Item>> LEAF_TO_RARES = (Map<Block, List<Item>>)Util.make(Maps.newHashMap(), map -> {
      map.put(Blocks.OAK_LEAVES, List.of(Items.APPLE));
      map.put(Blocks.JUNGLE_LEAVES, List.of(AMItemRegistry.BANANA.get(), AMItemRegistry.LEAFCUTTER_ANT_PUPA.get(), Items.COCOA_BEANS));
      map.put(Blocks.ACACIA_LEAVES, List.of(AMItemRegistry.ACACIA_BLOSSOM.get()));
   });
   private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.DIRECTION);
   private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Boolean> GLIDING = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> FORAGING_TIME = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.BOOLEAN);
   private static final Direction[] POSSIBLE_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private final int attachChangeCooldown = 0;
   public float glideProgress;
   public float prevGlideProgress;
   public float forageProgress;
   public float prevForageProgress;
   public float sitProgress;
   public float prevSitProgress;
   public float attachChangeProgress = 0.0F;
   public float prevAttachChangeProgress = 0.0F;
   public Direction prevAttachDir = Direction.DOWN;
   private boolean isGlidingNavigator;
   private boolean stopClimbing = false;
   private int forageCooldown = 0;
   private int detachCooldown = 0;
   private int rideCooldown = 0;

   protected EntitySugarGlider(EntityType type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.switchNavigator(true);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new FlyingAIFollowOwner(this, 1.0, 5.0F, 2.0F, true));
      this.goalSelector
         .addGoal(3, new TemptGoal(this, 1.1, AMCompat.ingredientOfTags(AMTagRegistry.SUGAR_GLIDER_BREEDABLES, AMTagRegistry.SUGAR_GLIDER_TAMEABLES), false) {
            public void start() {
               super.start();
               EntitySugarGlider.this.entityData.set(EntitySugarGlider.ATTACHED_FACE, Direction.DOWN);
            }
         });
      this.goalSelector.addGoal(4, new BreedGoal(this, 0.8) {
         public void start() {
            super.start();
            EntitySugarGlider.this.entityData.set(EntitySugarGlider.ATTACHED_FACE, Direction.DOWN);
         }
      });
      this.goalSelector.addGoal(5, new EntitySugarGlider.GlideGoal());
      this.goalSelector.addGoal(6, new PanicGoal(this, 1.0));
      this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 100, 1.0, 10, 7));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CLIMBING, (byte)0);
      builder.define(ATTACHED_FACE, Direction.DOWN);
      builder.define(GLIDING, false);
      builder.define(FORAGING_TIME, 0);
      builder.define(COMMAND, 0);
      builder.define(SITTING, false);
   }

   private void switchNavigator(boolean onGround) {
      if (onGround) {
         this.moveControl = new MoveControl(this);
         this.navigation = new SmartClimbPathNavigator(this, this.level());
         this.isGlidingNavigator = false;
      } else {
         this.moveControl = new FlightMoveController(this, 0.6F, false);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isGlidingNavigator = true;
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.SUGAR_GLIDER_BREEDABLES);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.SUGAR_GLIDER_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.SUGAR_GLIDER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.SUGAR_GLIDER_HURT.get();
   }

   public static boolean canSugarGliderSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return isBrightEnoughToSpawn(worldIn, pos);
   }

   public boolean checkSpawnObstruction(LevelReader reader) {
      if (reader.isUnobstructed(this) && !reader.containsAnyLiquid(this.getBoundingBox())) {
         BlockPos blockpos = this.blockPosition();
         BlockState blockstate2 = reader.getBlockState(blockpos.below());
         return blockstate2.is(BlockTags.LEAVES) || blockstate2.is(BlockTags.LOGS) || blockstate2.is(Blocks.GRASS_BLOCK);
      } else {
         return false;
      }
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.sugarGliderSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public void tick() {
      super.tick();
      AMCompat.setMaxUpStep(this, 1.0F);
      this.prevGlideProgress = this.glideProgress;
      this.prevAttachChangeProgress = this.attachChangeProgress;
      this.prevForageProgress = this.forageProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.attachChangeProgress > 0.0F) {
         this.attachChangeProgress -= 0.25F;
      }

      if (this.glideProgress < 5.0F && this.isGliding()) {
         this.glideProgress += 2.5F;
      }

      if (this.glideProgress > 0.0F && !this.isGliding()) {
         this.glideProgress -= 2.5F;
      }

      if (this.forageProgress < 5.0F && this.getForagingTime() > 0) {
         this.forageProgress++;
      }

      if (this.forageProgress > 0.0F && this.getForagingTime() <= 0) {
         this.forageProgress--;
      }

      boolean sitVisual = this.isOrderedToSit() && !this.isInWater() && this.onGround();
      if (this.sitProgress < 5.0F && sitVisual) {
         this.sitProgress++;
      }

      if (this.sitProgress > 0.0F && !sitVisual) {
         this.sitProgress--;
      }

      if (this.isGliding()) {
         if (this.shouldStopGliding()) {
            this.setGliding(false);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.5, 0.99));
         }
      }

      Vec3 vector3d = this.getDeltaMovement();
      if (!this.level().isClientSide()) {
         this.setBesideClimbableBlock(this.horizontalCollision);
         if (!this.onGround() && !this.isOrderedToSit() && !this.isInWaterOrBubble() && !this.isInLava() && !this.isGliding() && !this.isPassenger()) {
            Direction closestDirection = Direction.DOWN;
            double closestDistance = 100.0;

            for (Direction dir : POSSIBLE_DIRECTIONS) {
               BlockPos antPos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
               BlockPos offsetPos = antPos.relative(dir);
               Vec3 offset = Vec3.atCenterOf(offsetPos);
               if (closestDistance > this.position().distanceTo(offset) && this.level().loadedAndEntityCanStandOnFace(offsetPos, this, dir.getOpposite())) {
                  closestDistance = this.position().distanceTo(offset);
                  closestDirection = dir;
               }
            }

            this.entityData.set(ATTACHED_FACE, closestDistance > this.getBbWidth() * 0.5F + 0.7F ? Direction.DOWN : closestDirection);
         } else {
            this.entityData.set(ATTACHED_FACE, Direction.DOWN);
         }
      }

      boolean flag = false;
      if (this.getAttachmentFacing() != Direction.DOWN) {
         if (!this.horizontalCollision && this.getAttachmentFacing() != Direction.UP) {
            Vec3 vec = Vec3.atLowerCornerOf(this.getAttachmentFacing().getNormal());
            this.setDeltaMovement(vector3d.add(vec.normalize().multiply(0.10000000149011612, 0.10000000149011612, 0.10000000149011612)));
         }

         if (!this.onGround() && vector3d.y < 0.0) {
            this.setDeltaMovement(vector3d.multiply(1.0, 0.5, 1.0));
            flag = true;
         }
      }

      if (this.getAttachmentFacing() != Direction.DOWN && !this.isGliding()) {
         this.setNoGravity(true);
         this.setDeltaMovement(vector3d.multiply(0.6, 0.4, 0.6));
      } else {
         this.setNoGravity(false);
      }

      if (this.prevAttachDir != this.getAttachmentFacing()) {
         this.attachChangeProgress = 1.0F;
      }

      this.prevAttachDir = this.getAttachmentFacing();
      if (!this.level().isClientSide()) {
         if ((this.getAttachmentFacing() == Direction.UP || this.isGliding()) && !this.isGlidingNavigator) {
            this.switchNavigator(false);
         }

         if (this.getAttachmentFacing() != Direction.UP && this.isGlidingNavigator) {
            this.switchNavigator(true);
         }
      }

      BlockPos on = this.blockPosition().relative(this.getAttachmentFacing());
      if (this.shouldForage() && this.level().getBlockState(on).is(BlockTags.LEAVES)) {
         BlockState state = this.level().getBlockState(on);
         if (this.getForagingTime() < 100) {
            if (this.random.nextInt(2) == 0) {
               for (int i = 0; i < 4 + this.random.nextInt(2); i++) {
                  double motX = this.random.nextGaussian() * 0.02;
                  double motY = this.random.nextGaussian() * 0.02;
                  double motZ = this.random.nextGaussian() * 0.02;
                  this.level()
                     .addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        on.getX() + this.random.nextFloat(),
                        on.getY() + this.random.nextFloat(),
                        on.getZ() + this.random.nextFloat(),
                        motX,
                        motY,
                        motZ
                     );
               }
            }

            this.setForagingTime(this.getForagingTime() + 1);
         } else {
            if (!this.level().isClientSide()) {
               List<ItemStack> lootList = this.getForageLoot(state);
               if (lootList.size() > 0) {
                  for (ItemStack stack : lootList) {
                     ItemEntity e = AMCompat.spawnAtLocation(this, stack.copy());
                     if (e != null) {
                        e.hasImpulse = true;
                        e.setDeltaMovement(e.getDeltaMovement().multiply(0.2, 0.2, 0.2));
                     }
                  }
               }
            }

            this.forageCooldown = 8000 + 8000 * this.random.nextInt(2);
            this.setForagingTime(0);
         }
      } else {
         this.setForagingTime(0);
      }

      if (this.detachCooldown > 0) {
         this.detachCooldown--;
      }

      if (this.rideCooldown > 0) {
         this.rideCooldown--;
      }
   }

   private void clearGrantedSlowFalling(Entity mount) {
      if (mount instanceof LivingEntity living) {
         MobEffectInstance granted = living.getEffect(MobEffects.SLOW_FALLING);
         if (granted != null && granted.isAmbient() && !granted.isVisible() && granted.getAmplifier() == 0) {
            living.removeEffect(MobEffects.SLOW_FALLING);
         }
      }
   }

   public void stopRiding() {
      this.clearGrantedSlowFalling(this.getVehicle());
      super.stopRiding();
   }

   public void rideTick() {
      Entity entity = this.getVehicle();
      if (this.isPassenger() && !entity.isAlive()) {
         this.clearGrantedSlowFalling(entity);
         this.stopRiding();
      } else if (this.isTame() && entity instanceof LivingEntity && this.isOwnedBy((LivingEntity)entity)) {
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.tick();
         if (this.isPassenger()) {
            Entity mount = this.getVehicle();
            if (mount instanceof Player) {
               ((LivingEntity)mount).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, true, false));
               this.yBodyRot = ((LivingEntity)mount).yBodyRot;
               this.setYRot(mount.getYRot());
               this.yHeadRot = ((LivingEntity)mount).yHeadRot;
               this.yRotO = ((LivingEntity)mount).yHeadRot;
               float radius = 0.0F;
               float angle = 0.017453292F * (((LivingEntity)mount).yBodyRot - 180.0F);
               double extraX = 0.0F * Mth.sin(3.1415927F + angle);
               double extraZ = 0.0F * Mth.cos(angle);
               this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getBbHeight() + 0.1, mount.getY()), mount.getZ() + extraZ);
               if (!mount.isAlive() || this.rideCooldown == 0 && mount.isShiftKeyDown()) {
                  this.clearGrantedSlowFalling(mount);
                  this.removeVehicle();
               }
            }
         }
      } else {
         super.rideTick();
      }
   }

   private List<ItemStack> getForageLoot(BlockState leafState) {
      Item sapling = LEAF_TO_SAPLING.get(leafState.getBlock());
      List<Item> rares = LEAF_TO_RARES.get(leafState.getBlock());
      float rng = this.getRandom().nextFloat();
      if (rng < 0.1F && rares != null) {
         Item item = rares.size() <= 1 ? rares.get(0) : rares.get(this.getRandom().nextInt(rares.size()));
         return List.of(new ItemStack(item));
      } else if (rng < 0.25F && sapling != null) {
         return List.of(new ItemStack(sapling));
      } else {
         LootTable loottable = AMCompat.lootTable(this.level().getServer(), SUGAR_GLIDER_REWARD);
         return loottable.getRandomItems(
            new net.minecraft.world.level.storage.loot.LootParams.Builder((ServerLevel)this.level())
               .withParameter(LootContextParams.THIS_ENTITY, this)
               .create(LootContextParamSets.PIGLIN_BARTER)
         );
      }
   }

   public void travel(Vec3 travelVector) {
      if (this.isOrderedToSit()) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         travelVector = Vec3.ZERO;
      }

      if (this.isInWater() && this.getDeltaMovement().y > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.5, 1.0));
      }

      super.travel(travelVector);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(ATTACHED_FACE, Direction.from3DDataValue(AMCompat.getByte(compound, "AttachFace")));
      this.setCommand(AMCompat.getInt(compound, "SugarGliderCommand"));
      this.setOrderedToSit(AMCompat.getBoolean(compound, "SugarGliderSitting"));
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putByte("AttachFace", (byte)((Direction)this.entityData.get(ATTACHED_FACE)).get3DDataValue());
      compound.putInt("SugarGliderCommand", this.getCommand());
      compound.putBoolean("SugarGliderSitting", this.isOrderedToSit());
   }

   public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
      return false;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void onInsideBlock(BlockState state) {
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public boolean onClimbable() {
      return this.isBesideClimbableBlock() && !this.isGliding() && !this.stopClimbing && !this.isOrderedToSit();
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

   public Direction getAttachmentFacing() {
      return (Direction)this.entityData.get(ATTACHED_FACE);
   }

   public boolean isGliding() {
      return (Boolean)this.entityData.get(GLIDING);
   }

   public void setGliding(boolean gliding) {
      this.entityData.set(GLIDING, gliding);
   }

   public int getForagingTime() {
      return (Integer)this.entityData.get(FORAGING_TIME);
   }

   public void setForagingTime(int feedingTime) {
      this.entityData.set(FORAGING_TIME, feedingTime);
   }

   public boolean isOrderedToSit() {
      return (Boolean)this.entityData.get(SITTING);
   }

   public void setOrderedToSit(boolean sit) {
      this.entityData.set(SITTING, sit);
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.isTame() && itemstack.is(AMTagRegistry.SUGAR_GLIDER_TAMEABLES)) {
         this.usePlayerItem(player, hand, itemstack);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.FOX_EAT, this.getSoundVolume(), this.getVoicePitch());
         if (this.getRandom().nextInt(2) == 0) {
            this.tame(player);
            this.level().broadcastEntityEvent(this, (byte)7);
         } else {
            this.level().broadcastEntityEvent(this, (byte)6);
         }

         return InteractionResult.SUCCESS;
      } else if (this.isTame() && itemstack.is(AMTagRegistry.INSECT_ITEMS)) {
         if (this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.FOX_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5.0F);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
         if (interactionresult == InteractionResult.SUCCESS
            || type == InteractionResult.SUCCESS
            || !this.isTame()
            || !this.isOwnedBy(player)
            || this.isFood(itemstack)) {
            return type;
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
               this.setOrderedToSit(true);
               return InteractionResult.SUCCESS;
            } else {
               this.setOrderedToSit(false);
               return InteractionResult.SUCCESS;
            }
         }
      }
   }

   public void calculateEntityAnimation(boolean b) {
      float f1 = (float)Mth.length(this.getX() - this.xo, (this.getY() - this.yo) * 2.0, this.getZ() - this.zo);
      float f2 = Math.min(f1 * 6.0F, 1.0F);
      this.walkAnimation.update(f2, 0.4F);
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new WallClimberNavigation(this, worldIn) {
         protected boolean canUpdatePath() {
            return super.canUpdatePath() || ((EntitySugarGlider)this.mob).isBesideClimbableBlock() || this.mob.jumping;
         }
      };
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
      return AMCompat.create(AMEntityRegistry.SUGAR_GLIDER.get(), serverLevel);
   }

   private boolean shouldStopGliding() {
      return this.onGround() || this.getAttachmentFacing() != Direction.DOWN;
   }

   private boolean shouldForage() {
      return this.isTame() && !this.isBaby() && this.forageCooldown == 0;
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   @Override
   public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
      if (!(this.distanceTo(owner) < 5.0F) && !this.isBaby()) {
         Vec3 fly = new Vec3(0.0, 0.0, 0.0);
         float f = 0.5F;
         if (this.onGround()) {
            fly = fly.add(0.0, 0.4, 0.0);
            f = 0.9F;
         }

         fly = fly.add(owner.getEyePosition().subtract(this.position()).normalize().scale(f));
         this.setDeltaMovement(fly);
         Vec3 move = this.getDeltaMovement();
         double d0 = move.horizontalDistance();
         this.setXRot((float)(-Mth.atan2(move.y, d0) * 57.2957763671875));
         this.setYRot((float)Mth.atan2(move.z, move.x) * 57.295776F - 90.0F);
         this.setGliding(true);
      } else {
         this.setGliding(!this.onGround());
         this.getNavigation().moveTo(owner, followSpeed);
      }
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, this));
      return result.getBlockPos().equals(destinationBlock);
   }

   private class GlideGoal extends Goal {
      private boolean climbing;
      private int climbTime = 0;
      private int leapSearchCooldown = 0;
      private int climbTimeout = 0;
      private BlockPos climb;
      private BlockPos glide;
      private boolean itsOver = false;
      private int airtime = 0;
      private Direction climbOffset = Direction.UP;

      private GlideGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (EntitySugarGlider.this.getForagingTime() <= 0
            && !EntitySugarGlider.this.isBaby()
            && !EntitySugarGlider.this.isOrderedToSit()
            && EntitySugarGlider.this.getRandom().nextInt(45) == 0) {
            if (EntitySugarGlider.this.getAttachmentFacing() != Direction.DOWN) {
               this.climb = EntitySugarGlider.this.blockPosition().relative(EntitySugarGlider.this.getAttachmentFacing());
            } else {
               this.climb = this.findClimbPos();
            }

            return this.climb != null;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.climb != null
            && !this.itsOver
            && this.climbTimeout < 30
            && (!this.climbing || !EntitySugarGlider.this.level().isEmptyBlock(this.climb) && !EntitySugarGlider.this.getNavigation().isStuck())
            && EntitySugarGlider.this.getForagingTime() <= 0
            && !EntitySugarGlider.this.isOrderedToSit();
      }

      public void start() {
         this.climbTimeout = 0;
         this.leapSearchCooldown = 0;
         this.airtime = 0;
         this.climbing = true;
         this.climbTime = 0;
         EntitySugarGlider.this.getNavigation().stop();
      }

      public void stop() {
         this.climbTimeout = 0;
         this.climb = null;
         this.glide = null;
         this.itsOver = false;
         EntitySugarGlider.this.stopClimbing = false;
         EntitySugarGlider.this.setGliding(false);
         EntitySugarGlider.this.getNavigation().stop();
      }

      public void tick() {
         if (this.leapSearchCooldown > 0) {
            this.leapSearchCooldown--;
         }

         if (this.climbing) {
            float inDir = EntitySugarGlider.this.getAttachmentFacing() == Direction.DOWN && EntitySugarGlider.this.getY() > this.climb.getY() + 0.3F
               ? 0.5F + EntitySugarGlider.this.getBbWidth() * 0.5F
               : 0.5F;
            Vec3 offset = Vec3.atCenterOf(this.climb)
               .subtract(0.0, 0.0, 0.0)
               .add(this.climbOffset.getStepX() * inDir, this.climbOffset.getStepY() * inDir, this.climbOffset.getStepZ() * inDir);
            double d0 = this.climb.getX() + 0.5F - EntitySugarGlider.this.getX();
            double d2 = this.climb.getZ() + 0.5F - EntitySugarGlider.this.getZ();
            double xzDistSqr = d0 * d0 + d2 * d2;
            if (EntitySugarGlider.this.getY() > offset.y - 0.30000001192092896 - EntitySugarGlider.this.getBbHeight()) {
               EntitySugarGlider.this.stopClimbing = true;
            }

            if (xzDistSqr < 3.0 && EntitySugarGlider.this.getAttachmentFacing() != Direction.DOWN) {
               Vec3 silly = new Vec3(d0, 0.0, d2).normalize().scale(0.1);
               EntitySugarGlider.this.setDeltaMovement(EntitySugarGlider.this.getDeltaMovement().add(silly));
            } else {
               EntitySugarGlider.this.getNavigation().moveTo(offset.x, offset.y, offset.z, 1.0);
            }

            if (EntitySugarGlider.this.getAttachmentFacing() == Direction.DOWN) {
               this.climbTimeout++;
               this.climbTime = 0;
            } else {
               this.climbTimeout = 0;
               this.climbTime++;
               if (this.climbTime > 40 && this.leapSearchCooldown == 0) {
                  BlockPos leapTo = this.findLeapPos(EntitySugarGlider.this.shouldForage() && EntitySugarGlider.this.random.nextInt(5) != 0);
                  this.leapSearchCooldown = 5 + EntitySugarGlider.this.getRandom().nextInt(10);
                  if (leapTo != null) {
                     EntitySugarGlider.this.stopClimbing = false;
                     EntitySugarGlider.this.setGliding(true);
                     EntitySugarGlider.this.getNavigation().stop();
                     EntitySugarGlider.this.entityData.set(EntitySugarGlider.ATTACHED_FACE, Direction.DOWN);
                     this.glide = leapTo;
                     this.climbing = false;
                  }
               }
            }
         } else if (this.glide != null) {
            EntitySugarGlider.this.stopClimbing = false;
            EntitySugarGlider.this.setGliding(true);
            if (this.airtime > 5
               && (
                  EntitySugarGlider.this.horizontalCollision
                     || EntitySugarGlider.this.onGround()
                     || Math.sqrt(EntitySugarGlider.this.distanceToSqr(Vec3.atCenterOf(this.glide))) < 1.100000023841858
               )) {
               EntitySugarGlider.this.setGliding(false);
               EntitySugarGlider.this.detachCooldown = 20 + EntitySugarGlider.this.random.nextInt(80);
               this.itsOver = true;
            }

            Vec3 fly = Vec3.atCenterOf(this.glide).subtract(EntitySugarGlider.this.position()).normalize().scale(0.30000001192092896);
            EntitySugarGlider.this.setDeltaMovement(fly);
            Vec3 move = EntitySugarGlider.this.getDeltaMovement();
            double d0x = move.horizontalDistance();
            EntitySugarGlider.this.setXRot((float)(-Mth.atan2(move.y, d0x) * 57.2957763671875));
            EntitySugarGlider.this.setYRot((float)Mth.atan2(move.z, move.x) * 57.295776F - 90.0F);
            this.airtime++;
         }
      }

      private BlockPos findClimbPos() {
         BlockPos mobPos = EntitySugarGlider.this.blockPosition();

         for (int i = 0; i < 15; i++) {
            BlockPos offset = mobPos.offset(
               EntitySugarGlider.this.random.nextInt(16) - 8, EntitySugarGlider.this.random.nextInt(4) + 1, EntitySugarGlider.this.random.nextInt(16) - 8
            );
            double d0 = offset.getX() + 0.5F - EntitySugarGlider.this.getX();
            double d2 = offset.getZ() + 0.5F - EntitySugarGlider.this.getZ();
            double xzDistSqr = d0 * d0 + d2 * d2;
            Vec3 blockVec = Vec3.atCenterOf(offset);
            BlockHitResult result = EntitySugarGlider.this.level()
               .clip(
                  new ClipContext(
                     EntitySugarGlider.this.getEyePosition(),
                     blockVec,
                     net.minecraft.world.level.ClipContext.Block.COLLIDER,
                     Fluid.NONE,
                     EntitySugarGlider.this
                  )
               );
            if (result.getType() != Type.MISS
               && xzDistSqr > 4.0
               && result.getDirection().getAxis() != Axis.Y
               && this.getDistanceOffGround(result.getBlockPos().relative(result.getDirection())) > 3
               && this.isPositionEasilyClimbable(result.getBlockPos())) {
               this.climbOffset = result.getDirection();
               return result.getBlockPos();
            }
         }

         return null;
      }

      private BlockPos findLeapPos(boolean leavesOnly) {
         BlockPos mobPos = EntitySugarGlider.this.blockPosition().relative(this.climbOffset.getOpposite());

         for (int i = 0; i < 15; i++) {
            BlockPos offset = mobPos.offset(
               EntitySugarGlider.this.random.nextInt(32) - 16, -1 - EntitySugarGlider.this.random.nextInt(4), EntitySugarGlider.this.random.nextInt(32) - 16
            );
            Vec3 blockVec = Vec3.atCenterOf(offset);
            BlockHitResult result = EntitySugarGlider.this.level()
               .clip(
                  new ClipContext(
                     EntitySugarGlider.this.getEyePosition(),
                     blockVec,
                     net.minecraft.world.level.ClipContext.Block.COLLIDER,
                     Fluid.NONE,
                     EntitySugarGlider.this
                  )
               );
            if (result.getType() != Type.MISS
               && result.getBlockPos().distSqr(mobPos) > 4.0
               && (!leavesOnly || EntitySugarGlider.this.level().getBlockState(result.getBlockPos()).is(BlockTags.LEAVES))) {
               return result.getBlockPos();
            }
         }

         return null;
      }

      private int getDistanceOffGround(BlockPos pos) {
         int dist;
         for (dist = 0; pos.getY() > -64 && EntitySugarGlider.this.level().isEmptyBlock(pos); dist++) {
            pos = pos.below();
         }

         return dist;
      }

      private boolean isPositionEasilyClimbable(BlockPos pos) {
         pos = pos.below();

         while (pos.getY() > EntitySugarGlider.this.getY() && !EntitySugarGlider.this.level().isEmptyBlock(pos)) {
            pos = pos.below();
         }

         return pos.getY() <= EntitySugarGlider.this.getY();
      }
   }
}
