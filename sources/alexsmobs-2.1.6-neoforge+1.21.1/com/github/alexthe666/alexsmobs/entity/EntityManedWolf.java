package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.message.MessageStartDancing;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.google.common.base.Predicates;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityManedWolf extends Animal implements ITargetsDroppedItems, IDancingMob {
   private static final EntityDataAccessor<Float> EAR_PITCH = SynchedEntityData.defineId(EntityManedWolf.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> EAR_YAW = SynchedEntityData.defineId(EntityManedWolf.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(EntityManedWolf.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> SHAKING_TIME = SynchedEntityData.defineId(EntityManedWolf.class, EntityDataSerializers.INT);
   private static final Supplier<Ingredient> allFoods = AMCompat.lazyIngredient(
      () -> AMCompat.ingredientOfTags(AMTagRegistry.MANED_WOLF_BREEDABLES, AMTagRegistry.MANED_WOLF_STENCH_FOODS)
   );
   public float prevEarPitch;
   public float prevEarYaw;
   public float prevDanceProgress;
   public float danceProgress;
   public float prevShakeProgress;
   public float shakeProgress;
   private int earCooldown = 0;
   private float targetPitch;
   private float targetYaw;
   private boolean isJukeboxing;
   private BlockPos jukeboxPosition;
   private BlockPos nearestAnthill;

   protected EntityManedWolf(EntityType<? extends Animal> animal, Level level) {
      super(animal, level);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 16.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.manedWolfSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, (Predicate)allFoods.get(), false));
      this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, 30));
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(EAR_PITCH, 0.0F);
      builder.define(EAR_YAW, 0.0F);
      builder.define(SHAKING_TIME, 0);
      builder.define(DANCING, false);
   }

   public float getEarYaw() {
      return (Float)this.entityData.get(EAR_YAW);
   }

   public void setEarYaw(float yaw) {
      this.entityData.set(EAR_YAW, yaw);
   }

   public float getEarPitch() {
      return (Float)this.entityData.get(EAR_PITCH);
   }

   public void setEarPitch(float pitch) {
      this.entityData.set(EAR_PITCH, pitch);
   }

   public boolean isDancing() {
      return (Boolean)this.entityData.get(DANCING);
   }

   @Override
   public void setDancing(boolean dancing) {
      this.entityData.set(DANCING, dancing);
      this.isJukeboxing = dancing;
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.MANED_WOLF_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.MANED_WOLF_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.MANED_WOLF_HURT.get();
   }

   private void attractAnimals() {
      if (this.getShakingTime() % 5 == 0) {
         for (Animal e : this.level().getEntitiesOfClass(Animal.class, this.getBoundingBox().inflate(16.0, 8.0, 16.0))) {
            if (!(e instanceof EntityManedWolf) && !(e instanceof TamableAnimal tamedMob && tamedMob.isInSittingPose())) {
               e.setTarget(null);
               e.setLastHurtByMob(null);
               Vec3 vec = LandRandomPos.getPosTowards(e, 20, 7, this.position());
               if (vec != null) {
                  e.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.5);
               }
            }
         }
      }
   }

   private void pollinateAnthill() {
      if (this.nearestAnthill != null && this.level().getBlockEntity(this.nearestAnthill) instanceof TileEntityLeafcutterAnthill) {
         if (this.getShakingTime() % 5 == 0) {
            this.getNavigation().moveTo(this.nearestAnthill.getX() + 0.5F, this.nearestAnthill.getY() + 1.0F, this.nearestAnthill.getZ() + 0.5F, 1.0);
         }

         if (this.nearestAnthill.closerToCenterThan(this.position(), 6.0) && this.getShakingTime() % 20 == 0) {
            ((TileEntityLeafcutterAnthill)this.level().getBlockEntity(this.nearestAnthill)).growFungus();
         }
      }
   }

   private void findAnthill() {
      if (this.nearestAnthill == null || !(this.level().getBlockEntity(this.nearestAnthill) instanceof TileEntityLeafcutterAnthill)) {
         PoiManager pointofinterestmanager = ((ServerLevel)this.level()).getPoiManager();
         Stream<BlockPos> stream = pointofinterestmanager.findAll(
            poiTypeHolder -> poiTypeHolder.is(AMPointOfInterestRegistry.LEAFCUTTER_ANT_HILL_KEY),
            Predicates.alwaysTrue(),
            this.blockPosition(),
            10,
            Occupancy.ANY
         );
         List<BlockPos> listOfHives = stream.collect(Collectors.toList());
         BlockPos nearest = null;

         for (BlockPos pos : listOfHives) {
            if (nearest == null || pos.distSqr(this.blockPosition()) < nearest.distSqr(this.blockPosition())) {
               nearest = pos;
            }
         }

         this.nearestAnthill = nearest;
      }
   }

   @Override
   public void setJukeboxPos(BlockPos pos) {
      this.jukeboxPosition = pos;
   }

   public boolean isShaking() {
      return this.getShakingTime() > 0;
   }

   public int getShakingTime() {
      return (Integer)this.entityData.get(SHAKING_TIME);
   }

   public void setShakingTime(int shaking) {
      this.entityData.set(SHAKING_TIME, shaking);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (itemstack.is(AMTagRegistry.MANED_WOLF_STENCH_FOODS) && !this.isShaking() && this.getMainHandItem().isEmpty()) {
         this.usePlayerItem(player, hand, itemstack);
         this.eatItemEffect(itemstack);
         this.setShakingTime(100 + this.random.nextInt(30));
         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   private void eatItemEffect(ItemStack heldItemMainhand) {
      for (int i = 0; i < 2 + this.random.nextInt(2); i++) {
         double d2 = this.random.nextGaussian() * 0.02;
         double d0 = this.random.nextGaussian() * 0.02;
         double d1 = this.random.nextGaussian() * 0.02;
         float radius = this.getBbWidth() * 0.65F;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
         if (heldItemMainhand.getItem() instanceof BlockItem) {
            data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem)heldItemMainhand.getItem()).getBlock().defaultBlockState());
         }

         this.level().addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6F, this.getZ() + extraZ, d0, d1, d2);
      }
   }

   public void tick() {
      super.tick();
      this.prevEarPitch = this.getEarPitch();
      this.prevEarYaw = this.getEarYaw();
      this.prevDanceProgress = this.danceProgress;
      this.prevShakeProgress = this.shakeProgress;
      if (!this.level().isClientSide()) {
         this.updateEars();
      }

      boolean dance = this.isDancing();
      if (this.jukeboxPosition == null
         || !this.jukeboxPosition.closerToCenterThan(this.position(), 15.0)
         || !this.level().getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
         this.isJukeboxing = false;
         this.setDancing(false);
         this.jukeboxPosition = null;
      }

      if (dance && this.danceProgress < 5.0F) {
         this.danceProgress++;
      }

      if (!dance && this.danceProgress > 0.0F) {
         this.danceProgress--;
      }

      if (this.isShaking() && this.shakeProgress < 5.0F) {
         this.shakeProgress++;
      }

      if (!this.isShaking() && this.shakeProgress > 0.0F) {
         this.shakeProgress--;
      }

      if (this.isShaking()) {
         this.setShakingTime(this.getShakingTime() - 1);
         if (this.level().isClientSide()) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = 0.05000000074505806 + this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.SMELLY.get(),
                  this.getRandomX(0.699999988079071),
                  this.getY(0.6000000238418579),
                  this.getRandomZ(0.699999988079071),
                  d0,
                  d1,
                  d2
               );
         } else {
            this.attractAnimals();
            this.findAnthill();
            if (this.nearestAnthill != null) {
               this.pollinateAnthill();
            }
         }
      }
   }

   private void updateEars() {
      float pitchDist = Math.abs(this.targetPitch - this.getEarPitch());
      float yawDist = Math.abs(this.targetYaw - this.getEarYaw());
      if (this.earCooldown <= 0 && this.random.nextInt(30) == 0 && pitchDist <= 0.1F && yawDist <= 0.1F) {
         this.targetPitch = Mth.clamp(this.random.nextFloat() * 60.0F - 30.0F, -30.0F, 30.0F);
         this.targetYaw = Mth.clamp(this.random.nextFloat() * 60.0F - 30.0F, -30.0F, 30.0F);
         this.earCooldown = 8 + this.random.nextInt(15);
      }

      if (pitchDist > 0.1F) {
         if (this.getEarPitch() < this.targetPitch) {
            this.setEarPitch(this.getEarPitch() + Math.min(pitchDist, 4.0F));
         }

         if (this.getEarPitch() > this.targetPitch) {
            this.setEarPitch(this.getEarPitch() - Math.min(pitchDist, 4.0F));
         }
      }

      if (yawDist > 0.1F) {
         if (this.getEarYaw() < this.targetYaw) {
            this.setEarYaw(this.getEarYaw() + Math.min(yawDist, 4.0F));
         }

         if (this.getEarYaw() > this.targetYaw) {
            this.setEarYaw(this.getEarYaw() - Math.min(yawDist, 4.0F));
         }
      }

      if (this.earCooldown > 0) {
         this.earCooldown--;
      }
   }

   public boolean isFood(ItemStack stack) {
      return !stack.is(AMTagRegistry.MANED_WOLF_STENCH_FOODS) && allFoods.get().test(stack);
   }

   public void travel(Vec3 vec3d) {
      if (this.isDancing() || this.danceProgress > 0.0F) {
         if (this.getNavigation().getPath() != null) {
            this.getNavigation().stop();
         }

         vec3d = Vec3.ZERO;
      }

      super.travel(vec3d);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return allFoods.get().test(stack) && !this.isShaking();
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.eatItemEffect(e.getItem());
      if (e.getItem().is(AMTagRegistry.MANED_WOLF_STENCH_FOODS)) {
         this.setShakingTime(100 + this.random.nextInt(30));
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return AMCompat.create(AMEntityRegistry.MANED_WOLF.get(), serverWorld);
   }

   @OnlyIn(Dist.CLIENT)
   public void setRecordPlayingNearby(BlockPos pos, boolean isPartying) {
      AlexsMobs.sendMSGToServer(new MessageStartDancing(this.getId(), isPartying, pos));
      this.setDancing(isPartying);
      if (isPartying) {
         this.setJukeboxPos(pos);
      } else {
         this.setJukeboxPos(null);
      }
   }

   public boolean isEnder() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && (s.toLowerCase().contains("plummet") || s.toLowerCase().contains("ender"));
   }
}
