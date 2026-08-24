package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.MungusAIAlertBunfungus;
import com.github.alexthe666.alexsmobs.entity.ai.MungusAITemptMushroom;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMungusBiomeChange;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IShearable;

public class EntityMungus extends Animal implements ITargetsDroppedItems, Shearable, IShearable {
   protected static final EntityDataAccessor<Optional<BlockPos>> TARGETED_BLOCK_POS = SynchedEntityData.defineId(
      EntityMungus.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private static final EntityDataAccessor<Boolean> ALT_ORDER_MUSHROOMS = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> REVERTING = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> MUSHROOM_COUNT = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SACK_SWELL = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> EXPLOSION_DISABLED = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<BlockState>> MUSHROOM_STATE = SynchedEntityData.defineId(
      EntityMungus.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE
   );
   private static final int WIDTH_BITS = Mth.ceillog2(16) - 2;
   public static final int MAX_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + DimensionType.BITS_FOR_Y - 2;
   private static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
   private static final HashMap<String, String> MUSHROOM_TO_BIOME = new HashMap<>();
   private static final HashMap<String, String> MUSHROOM_TO_BLOCK = new HashMap<>();
   private static boolean initBiomeData = false;
   public float prevSwellProgress = 0.0F;
   public float swellProgress = 0.0F;
   private int beamCounter = 0;
   private int mosquitoAttackCooldown = 0;
   private boolean hasExploded;
   public int timeUntilNextEgg = this.random.nextInt(24000) + 24000;

   protected EntityMungus(EntityType<? extends Animal> type, Level worldIn) {
      super(type, worldIn);
      initBiomeData();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public static boolean canMungusSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return worldIn.getBlockState(pos.below()).canOcclude();
   }

   public static BlockState getMushroomBlockstate(Item item) {
      if (item instanceof BlockItem) {
         ResourceLocation name = BuiltInRegistries.ITEM.getKey(item);
         if (name != null && MUSHROOM_TO_BIOME.containsKey(name.toString())) {
            return ((BlockItem)item).getBlock().defaultBlockState();
         }
      }

      return null;
   }

   private static void initBiomeData() {
      if (!initBiomeData || MUSHROOM_TO_BIOME.isEmpty()) {
         initBiomeData = true;

         for (String str : AMConfig.mungusBiomeMatches) {
            String[] split = str.split("\\|");
            if (split.length >= 2) {
               MUSHROOM_TO_BIOME.put(split[0], split[1]);
               MUSHROOM_TO_BLOCK.put(split[0], split[2]);
            }
         }
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.MUNGUS_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.MUNGUS_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.MUNGUS_HURT.get();
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mungusSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new PanicGoal(this, 1.25));
      this.goalSelector.addGoal(3, new MungusAITemptMushroom(this, 1.0));
      this.goalSelector.addGoal(5, new EntityMungus.AITargetMushrooms());
      this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 60, 1.0, 14, 7));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, 10));
      this.targetSelector.addGoal(2, new MungusAIAlertBunfungus(this, EntityBunfungus.class));
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && this.isAlive() && !this.isBaby() && --this.timeUntilNextEgg <= 0) {
         ItemEntity dropped = AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.MUNGAL_SPORES.get());
         dropped.setDefaultPickUpDelay();
         this.timeUntilNextEgg = this.random.nextInt(24000) + 24000;
      }
   }

   public void baseTick() {
      super.baseTick();
      this.prevSwellProgress = this.swellProgress;
      if (this.isReverting() && AMConfig.mungusBiomeTransformationType == 2) {
         this.swellProgress += 0.5F;
         if (this.swellProgress >= 10.0F) {
            try {
               this.explode();
            } catch (Exception var2) {
               var2.printStackTrace();
            }

            this.swellProgress = 0.0F;
            this.entityData.set(REVERTING, false);
         }
      } else if (this.isAlive() && this.swellProgress > 0.0F) {
         this.swellProgress--;
      }

      if ((Boolean)this.entityData.get(EXPLOSION_DISABLED)) {
         if (this.mosquitoAttackCooldown < 0) {
            this.mosquitoAttackCooldown++;
         }

         if (this.mosquitoAttackCooldown > 200) {
            this.mosquitoAttackCooldown = 0;
            this.entityData.set(EXPLOSION_DISABLED, false);
         }
      }
   }

   protected void tickDeath() {
      super.tickDeath();
      if (this.getMushroomCount() >= 5 && AMConfig.mungusBiomeTransformationType > 0 && !this.isBaby() && !(Boolean)this.entityData.get(EXPLOSION_DISABLED)) {
         this.swellProgress++;
         if (this.deathTime == 19 && !this.hasExploded) {
            this.hasExploded = true;

            try {
               this.explode();
            } catch (Exception var2) {
               var2.printStackTrace();
            }
         }
      }
   }

   private void explode() {
      for (int i = 0; i < 5; i++) {
         float r1 = 6.0F * (this.random.nextFloat() - 0.5F);
         float r2 = 2.0F * (this.random.nextFloat() - 0.5F);
         float r3 = 6.0F * (this.random.nextFloat() - 0.5F);
         this.level().addParticle(ParticleTypes.EXPLOSION, this.getX() + r1, this.getY() + 0.5 + r2, this.getZ() + r3, r1 * 4.0F, r2 * 4.0F, r3 * 4.0F);
      }

      if (!this.level().isClientSide()) {
         ServerLevel serverLevel = (ServerLevel)this.level();
         int radius = 3;
         int j = 3 + this.level().getRandom().nextInt(1);
         int k = 3 + this.level().getRandom().nextInt(1);
         int l = 3 + this.level().getRandom().nextInt(1);
         float f = (j + k + l) * 0.333F + 0.5F;
         float ff = f * f;
         double ffDouble = ff;
         BlockPos center = this.blockPosition();
         BlockState transformState = Blocks.MYCELIUM.defaultBlockState();
         Registry<Biome> registry = serverLevel.getServer().registryAccess().registryOrThrow(Registries.BIOME);
         Holder<Biome> biome = (Holder<Biome>)registry.getHolder(Biomes.MUSHROOM_FIELDS).get();
         TagKey<Block> transformMatches = AMTagRegistry.MUNGUS_REPLACE_MUSHROOM;
         if (this.getMushroomState() != null) {
            String mushroomKey = BuiltInRegistries.BLOCK.getKey(this.getMushroomState().getBlock()).toString();
            if (MUSHROOM_TO_BLOCK.containsKey(mushroomKey)) {
               Block block = (Block)BuiltInRegistries.BLOCK.get(AMCompat.rl(MUSHROOM_TO_BLOCK.get(mushroomKey)));
               if (block != null) {
                  transformState = block.defaultBlockState();
                  if (block == Blocks.WARPED_NYLIUM) {
                     transformMatches = AMTagRegistry.MUNGUS_REPLACE_NETHER;
                  }

                  if (block == Blocks.CRIMSON_NYLIUM) {
                     transformMatches = AMTagRegistry.MUNGUS_REPLACE_NETHER;
                  }
               }
            }

            Holder<Biome> gottenFrom = this.getBiomeKeyFromShroom();
            if (gottenFrom != null) {
               biome = gottenFrom;
            }
         }

         BlockState finalTransformState = transformState;
         TagKey<Block> finalTransformReplace = transformMatches;
         if (AMConfig.mungusBiomeTransformationType == 2 && !this.level().isClientSide()) {
            this.transformBiome(center, biome);
         }

         this.gameEvent(GameEvent.EXPLODE);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), this.getSoundVolume(), this.getVoicePitch());
         if (!this.isReverting()) {
            BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l))
               .forEach(
                  blockpos -> {
                     if (blockpos.distSqr(center) <= ffDouble && this.level().getRandom().nextFloat() > (float)blockpos.distSqr(center) / ff) {
                        if (this.level().getBlockState(blockpos).is(finalTransformReplace) && !this.level().getBlockState(blockpos.above()).canOcclude()) {
                           this.level().setBlockAndUpdate(blockpos, finalTransformState);
                        }

                        if (this.level().getRandom().nextInt(4) == 0
                           && this.level().getBlockState(blockpos).isSolid()
                           && this.level().getFluidState(blockpos.above()).isEmpty()
                           && !this.level().getBlockState(blockpos.above()).canOcclude()) {
                           this.level().setBlockAndUpdate(blockpos.above(), this.getMushroomState());
                        }
                     }
                  }
               );
         }
      }
   }

   public void disableExplosion() {
      this.entityData.set(EXPLOSION_DISABLED, true);
   }

   private Holder<Biome> getBiomeKeyFromShroom() {
      Registry<Biome> registry = this.level().registryAccess().registryOrThrow(Registries.BIOME);
      BlockState state = this.getMushroomState();
      if (state == null) {
         return null;
      } else {
         ResourceLocation blockRegName = BuiltInRegistries.BLOCK.getKey(state.getBlock());
         if (blockRegName != null && MUSHROOM_TO_BIOME.containsKey(blockRegName.toString())) {
            String str = MUSHROOM_TO_BIOME.get(blockRegName.toString());
            Biome biome = (Biome)registry.getOptional(AMCompat.rl(str)).orElse(null);
            ResourceKey<Biome> resourceKey = (ResourceKey<Biome>)registry.getResourceKey(biome).orElse(null);
            return (Holder<Biome>)registry.getHolder(resourceKey).orElse(null);
         } else {
            return null;
         }
      }
   }

   private PalettedContainerRO<Holder<Biome>> getChunkBiomes(LevelChunk chunk) {
      int i = QuartPos.fromBlock(AMCompat.minBuildHeight(chunk));
      int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
      int l = Mth.clamp(QuartPos.fromBlock((int)this.getY()), i, k);
      int j = chunk.getSectionIndex(QuartPos.toBlock(l));
      LevelChunkSection section = chunk.getSection(j);
      return section == null ? null : section.getBiomes();
   }

   private void setChunkBiomes(LevelChunk chunk, PalettedContainer<Holder<Biome>> container) {
      int i = QuartPos.fromBlock(AMCompat.minBuildHeight(chunk));
      int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
      int l = Mth.clamp(QuartPos.fromBlock((int)this.getY()), i, k);
      int j = chunk.getSectionIndex(QuartPos.toBlock(l));
      LevelChunkSection section = chunk.getSection(j);
      if (section != null) {
         section.biomes = container;
      }
   }

   private void transformBiome(BlockPos pos, Holder<Biome> biome) {
      LevelChunk chunk = this.level().getChunkAt(pos);
      PalettedContainer<Holder<Biome>> container = this.getChunkBiomes(chunk).recreate();
      if ((Boolean)this.entityData.get(REVERTING)) {
         int lvt_4_1_ = chunk.getPos().getMinBlockX() >> 2;
         int yChunk = (int)this.getY() >> 2;
         int lvt_5_1_ = chunk.getPos().getMinBlockZ() >> 2;
         ChunkGenerator chunkgenerator = ((ServerLevel)this.level()).getChunkSource().getGenerator();

         for (int k = 0; k < 4; k++) {
            for (int l = 0; l < 4; l++) {
               for (int i1 = 0; i1 < 4; i1++) {
                  container.getAndSetUnchecked(k, l, i1, ((ServerLevel)this.level()).getUncachedNoiseBiome(lvt_4_1_ + k, yChunk + l, lvt_5_1_ + i1));
               }
            }
         }

         this.setChunkBiomes(chunk, container);
         if (!this.level().isClientSide()) {
         }
      } else {
         if (biome == null) {
            return;
         }

         if (container != null && !this.level().isClientSide()) {
            for (int biomeX = 0; biomeX < 4; biomeX++) {
               for (int biomeY = 0; biomeY < 4; biomeY++) {
                  for (int biomeZ = 0; biomeZ < 4; biomeZ++) {
                     container.getAndSetUnchecked(biomeX, biomeY, biomeZ, biome);
                  }
               }
            }

            this.setChunkBiomes(chunk, container);
            ResourceLocation biomeKey = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
            if (biomeKey != null) {
               AlexsMobs.sendMSGToAll(new MessageMungusBiomeChange(this.getId(), pos.getX(), pos.getZ(), biomeKey.toString()));
            }
         }
      }
   }

   public boolean shouldFollowMushroom(ItemStack stack) {
      BlockState state = getMushroomBlockstate(stack.getItem());
      if (state == null || state.isAir()) {
         return false;
      } else {
         return this.getMushroomCount() == 0 ? true : this.getMushroomState().getBlock() == state.getBlock();
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (itemstack.getItem() == Items.POISONOUS_POTATO && !this.isBaby()) {
         this.entityData.set(REVERTING, true);
         this.usePlayerItem(player, hand, itemstack);
         return InteractionResult.SUCCESS;
      } else if (this.shouldFollowMushroom(itemstack) && this.getMushroomCount() < 5) {
         this.entityData.set(REVERTING, false);
         BlockState state = getMushroomBlockstate(itemstack.getItem());
         this.gameEvent(GameEvent.BLOCK_PLACE);
         this.playSound(SoundEvents.FUNGUS_PLACE, this.getSoundVolume(), this.getVoicePitch());
         if (this.getMushroomState() != null && state != null && state.getBlock() != this.getMushroomState().getBlock()) {
            this.setMushroomCount(0);
         }

         this.setMushroomState(state);
         this.usePlayerItem(player, hand, itemstack);
         this.setMushroomCount(this.getMushroomCount() + 1);
         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean prev = super.hurt(source, amount);
      if (prev) {
         this.setBeamTarget(null);
         this.beamCounter = Math.min(this.beamCounter, -1200);
      }

      return prev;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(MUSHROOM_STATE, Optional.empty());
      builder.define(TARGETED_BLOCK_POS, Optional.empty());
      builder.define(ALT_ORDER_MUSHROOMS, false);
      builder.define(REVERTING, false);
      builder.define(EXPLOSION_DISABLED, false);
      builder.define(MUSHROOM_COUNT, 0);
      builder.define(SACK_SWELL, 0);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      BlockState blockstate = this.getMushroomState();
      if (blockstate != null) {
         AMCompat.put(compound, "MushroomState", NbtUtils.writeBlockState(blockstate));
      }

      compound.putInt("MushroomCount", this.getMushroomCount());
      compound.putInt("Sack", this.getSackSwell());
      compound.putInt("BeamCounter", this.beamCounter);
      compound.putBoolean("AltMush", (Boolean)this.entityData.get(ALT_ORDER_MUSHROOMS));
      if (this.getBeamTarget() != null) {
         AMCompat.put(compound, "BeamTarget", AMCompat.writeBlockPos(this.getBeamTarget()));
      }

      compound.putInt("EggTime", this.timeUntilNextEgg);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      BlockState blockstate = null;
      if (AMCompat.contains(compound, "MushroomState", 10)) {
         blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), AMCompat.getCompound(compound, "MushroomState"));
         if (blockstate.isAir()) {
            blockstate = null;
         }
      }

      if (AMCompat.contains(compound, "BeamTarget", 10)) {
         this.setBeamTarget(AMCompat.readBlockPos(compound, "BeamTarget"));
      }

      this.setMushroomState(blockstate);
      this.setMushroomCount(AMCompat.getInt(compound, "MushroomCount"));
      this.setSackSwell(AMCompat.getInt(compound, "Sack"));
      this.beamCounter = AMCompat.getInt(compound, "BeamCounter");
      this.entityData.set(ALT_ORDER_MUSHROOMS, AMCompat.getBoolean(compound, "AltMush"));
      if (AMCompat.contains(compound, "EggTime")) {
         this.timeUntilNextEgg = AMCompat.getInt(compound, "EggTime");
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.getBeamTarget() != null) {
         BlockPos t = this.getBeamTarget();
         if (this.isMushroomTarget(t) && this.hasLineOfSightMushroom(t)) {
            this.getLookControl().setLookAt(t.getX() + 0.5F, t.getY() + 0.15F, t.getZ() + 0.5F, 90.0F, 90.0F);
            this.getLookControl().tick();
            double d5 = 1.0;
            double eyeHeight = this.getY() + 1.0;
            if (this.beamCounter % 20 == 0) {
               this.playSound(AMSoundRegistry.MUNGUS_LASER_LOOP.get(), this.getVoicePitch(), this.getSoundVolume());
            }

            this.beamCounter++;
            double d0 = t.getX() + 0.5F - this.getX();
            double d1 = t.getY() + 0.5F - eyeHeight;
            double d2 = t.getZ() + 0.5F - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d0 /= d3;
            d1 /= d3;
            d2 /= d3;
            double d4 = this.random.nextDouble();

            while (d4 < d3 - 0.5) {
               d4 += 1.0 - d5 + this.random.nextDouble();
               if (this.random.nextFloat() < 0.1F) {
                  float r1 = 0.3F * (this.random.nextFloat() - 0.5F);
                  float r2 = 0.3F * (this.random.nextFloat() - 0.5F);
                  float r3 = 0.3F * (this.random.nextFloat() - 0.5F);
                  this.level()
                     .addParticle(
                        ParticleTypes.MYCELIUM,
                        this.getX() + d0 * d4 + r1,
                        eyeHeight + d1 * d4 + r2,
                        this.getZ() + d2 * d4 + r3,
                        r1 * 4.0F,
                        r2 * 4.0F,
                        r3 * 4.0F
                     );
               }
            }

            if (this.beamCounter > 200) {
               BlockState state = this.level().getBlockState(t);
               if (state.getBlock() instanceof BonemealableBlock) {
                  BonemealableBlock igrowable = (BonemealableBlock)state.getBlock();
                  boolean flag = false;
                  if (igrowable.isValidBonemealTarget(this.level(), t, state)) {
                     for (int i = 0; i < 5; i++) {
                        float r1 = 3.0F * (this.random.nextFloat() - 0.5F);
                        float r2 = 2.0F * (this.random.nextFloat() - 0.5F);
                        float r3 = 3.0F * (this.random.nextFloat() - 0.5F);
                        this.level()
                           .addParticle(
                              ParticleTypes.EXPLOSION, t.getX() + 0.5F + r1, t.getY() + 0.5F + r2, t.getZ() + 0.5F + r3, r1 * 4.0F, r2 * 4.0F, r3 * 4.0F
                           );
                     }

                     if (!this.level().isClientSide()) {
                        this.level().levelEvent(2005, t, 0);
                        igrowable.performBonemeal((ServerLevel)this.level(), this.level().getRandom(), t, state);
                        flag = this.level().getBlockState(t).getBlock() != state.getBlock();
                     }
                  }

                  if (!flag) {
                     int grown = 0;
                     int maxGrow = 2 + this.random.nextInt(3);

                     for (int i = 0; i < 15; i++) {
                        BlockPos pos = t.offset(this.random.nextInt(10) - 5, this.random.nextInt(4) - 2, this.random.nextInt(10) - 5);
                        if (grown < maxGrow && this.level().getBlockState(pos).isAir() && this.level().getBlockState(pos.below()).canOcclude()) {
                           this.level().setBlockAndUpdate(pos, state);
                           grown++;
                        }
                     }
                  }

                  this.playSound(AMSoundRegistry.MUNGUS_LASER_END.get(), this.getVoicePitch(), this.getSoundVolume());
                  if (flag) {
                     this.playSound(AMSoundRegistry.MUNGUS_LASER_GROW.get(), this.getVoicePitch(), this.getSoundVolume());
                  }

                  this.setBeamTarget(null);
                  this.beamCounter = -1200;
                  if (this.getMushroomCount() > 0) {
                     this.setMushroomCount(this.getMushroomCount() - 1);
                  }
               }
            }
         } else {
            this.setBeamTarget(null);
         }
      }

      if (this.beamCounter < 0) {
         this.beamCounter++;
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.MUNGUS_BREEDABLES);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.entityData.set(ALT_ORDER_MUSHROOMS, this.random.nextBoolean());
      this.setMushroomCount(this.random.nextInt(2));
      this.setMushroomState(this.random.nextBoolean() ? Blocks.BROWN_MUSHROOM.defaultBlockState() : Blocks.RED_MUSHROOM.defaultBlockState());
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public int getMushroomCount() {
      return (Integer)this.entityData.get(MUSHROOM_COUNT);
   }

   public void setMushroomCount(int command) {
      this.entityData.set(MUSHROOM_COUNT, command);
   }

   public int getSackSwell() {
      return (Integer)this.entityData.get(SACK_SWELL);
   }

   public void setSackSwell(int command) {
      this.entityData.set(SACK_SWELL, command);
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return (BlockPos)((Optional)this.getEntityData().get(TARGETED_BLOCK_POS)).orElse(null);
   }

   public void setBeamTarget(@Nullable BlockPos beamTarget) {
      this.getEntityData().set(TARGETED_BLOCK_POS, Optional.ofNullable(beamTarget));
   }

   public boolean isAltOrderMushroom() {
      return (Boolean)this.entityData.get(ALT_ORDER_MUSHROOMS);
   }

   @Nullable
   public BlockState getMushroomState() {
      return (BlockState)((Optional)this.entityData.get(MUSHROOM_STATE)).orElse(null);
   }

   public void setMushroomState(@Nullable BlockState state) {
      this.entityData.set(MUSHROOM_STATE, Optional.ofNullable(state));
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.MUNGUS.get(), p_241840_1_);
   }

   public boolean isMushroomTarget(BlockPos pos) {
      return this.getMushroomState() != null ? this.level().getBlockState(pos).getBlock() == this.getMushroomState().getBlock() : false;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return this.shouldFollowMushroom(stack) && this.getMushroomCount() < 5;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      if (this.shouldFollowMushroom(e.getItem())) {
         BlockState state = getMushroomBlockstate(e.getItem().getItem());
         if (this.getMushroomState() != null && state != null && state.getBlock() != this.getMushroomState().getBlock()) {
            this.setMushroomCount(0);
         }

         this.gameEvent(GameEvent.BLOCK_PLACE);
         this.playSound(SoundEvents.FUNGUS_PLACE, this.getSoundVolume(), this.getVoicePitch());
         this.setMushroomState(state);
         this.setMushroomCount(this.getMushroomCount() + 1);
      }
   }

   private boolean hasLineOfSightMushroom(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, this));
      return result.getBlockPos().equals(destinationBlock);
   }

   public boolean readyForShearing() {
      return this.isAlive() && this.getMushroomState() != null && this.getMushroomCount() > 0;
   }

   public boolean isShearable(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos) {
      return this.readyForShearing();
   }

   public void shear(SoundSource category) {
      this.gameEvent(GameEvent.ENTITY_INTERACT);
      this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, category, 1.0F, 1.0F);
      if (!this.level().isClientSide() && this.getMushroomState() != null && this.getMushroomCount() > 0) {
         this.setMushroomCount(this.getMushroomCount() - 1);
         if (this.getMushroomCount() <= 0) {
            this.setMushroomState(null);
            this.setBeamTarget(null);
            this.beamCounter = Math.min(-1200, this.beamCounter);
         }
      }
   }

   @Nonnull
   public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos) {
      world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
      if (!world.isClientSide() && this.getMushroomState() != null && this.getMushroomCount() > 0) {
         this.setMushroomCount(this.getMushroomCount() - 1);
         if (this.getMushroomCount() <= 0) {
            this.setMushroomState(null);
            this.setBeamTarget(null);
            this.beamCounter = Math.min(-1200, this.beamCounter);
         }
      }

      return Collections.emptyList();
   }

   public boolean isReverting() {
      return (Boolean)this.entityData.get(REVERTING);
   }

   public boolean isWarpedMoscoReady() {
      return this.getMushroomState() == Blocks.WARPED_FUNGUS.defaultBlockState() && this.getMushroomCount() >= 5;
   }

   class AITargetMushrooms extends Goal {
      private final int searchLength;
      protected BlockPos destinationBlock;
      protected int runDelay = 70;

      private AITargetMushrooms() {
         this.searchLength = 20;
      }

      public boolean canContinueToUse() {
         return this.destinationBlock != null && EntityMungus.this.isMushroomTarget(this.destinationBlock.mutable()) && this.isCloseToShroom(32.0);
      }

      public boolean isCloseToShroom(double dist) {
         return this.destinationBlock == null || EntityMungus.this.distanceToSqr(Vec3.atCenterOf(this.destinationBlock)) < dist * dist;
      }

      public boolean canUse() {
         if (EntityMungus.this.getBeamTarget() != null || EntityMungus.this.beamCounter < 0 || EntityMungus.this.getMushroomCount() <= 0) {
            return false;
         } else if (this.runDelay > 0) {
            this.runDelay--;
            return false;
         } else {
            this.runDelay = 70 + EntityMungus.this.random.nextInt(150);
            return this.searchForDestination();
         }
      }

      public void start() {
      }

      public void tick() {
         if (this.destinationBlock == null || !EntityMungus.this.isMushroomTarget(this.destinationBlock) || EntityMungus.this.beamCounter < 0) {
            this.stop();
         } else if (!EntityMungus.this.hasLineOfSightMushroom(this.destinationBlock)) {
            EntityMungus.this.getNavigation().moveTo(this.destinationBlock.getX(), this.destinationBlock.getY(), this.destinationBlock.getZ(), 1.0);
         } else {
            EntityMungus.this.setBeamTarget(this.destinationBlock);
            if (!EntityMungus.this.isInLove()) {
               EntityMungus.this.getNavigation().stop();
            }
         }
      }

      public void stop() {
         EntityMungus.this.setBeamTarget(null);
      }

      protected boolean searchForDestination() {
         int lvt_1_1_ = this.searchLength;
         BlockPos lvt_3_1_ = EntityMungus.this.blockPosition();
         MutableBlockPos lvt_4_1_ = new MutableBlockPos();

         for (int lvt_5_1_ = -5; lvt_5_1_ <= 5; lvt_5_1_++) {
            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; lvt_6_1_++) {
               for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                  for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0;
                     lvt_8_1_ <= lvt_6_1_;
                     lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_
                  ) {
                     lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                     if (this.isMushroom(EntityMungus.this.level(), lvt_4_1_)) {
                        this.destinationBlock = lvt_4_1_;
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }

      private boolean isMushroom(Level world, MutableBlockPos lvt_4_1_) {
         return EntityMungus.this.isMushroomTarget(lvt_4_1_);
      }
   }
}
