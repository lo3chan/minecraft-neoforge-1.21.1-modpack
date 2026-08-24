package tallestegg.guardvillagers.common.entities;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent.Open;
import org.jetbrains.annotations.NotNull;
import tallestegg.guardvillagers.GuardEntityType;
import tallestegg.guardvillagers.GuardVillagers;
import tallestegg.guardvillagers.ModCompat;
import tallestegg.guardvillagers.client.GuardSounds;
import tallestegg.guardvillagers.configuration.GuardConfig;
import tallestegg.guardvillagers.loot_tables.GuardLootTables;
import tallestegg.guardvillagers.networking.GuardOpenInventoryPacket;

public class Guard extends PathfinderMob implements CrossbowAttackMob, RangedAttackMob, NeutralMob, ContainerListener, ReputationEventHandler {
   protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(Guard.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final AttributeModifier USE_ITEM_SPEED_PENALTY = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("guardvillagers", "item_slow_down"), -0.25, Operation.ADD_VALUE
   );
   private static final EntityDataAccessor<Optional<BlockPos>> GUARD_POS = SynchedEntityData.defineId(Guard.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
   private static final EntityDataAccessor<Boolean> PATROLLING = SynchedEntityData.defineId(Guard.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<String> GUARD_VARIANT = SynchedEntityData.defineId(Guard.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Boolean> RUNNING_TO_EAT = SynchedEntityData.defineId(Guard.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_CHARGING_STATE = SynchedEntityData.defineId(Guard.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> KICKING = SynchedEntityData.defineId(Guard.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FOLLOWING = SynchedEntityData.defineId(Guard.class, EntityDataSerializers.BOOLEAN);
   private static final Map<Pose, EntityDimensions> SIZE_BY_POSE = ImmutableMap.builder()
      .put(Pose.SLEEPING, SLEEPING_DIMENSIONS)
      .put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
      .put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
      .put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
      .put(
         Pose.CROUCHING,
         EntityDimensions.scalable(0.6F, 1.5F)
            .withEyeHeight(1.27F)
            .withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, new Vec3(0.0, 0.6, 0.0)))
      )
      .put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F).withEyeHeight(1.62F))
      .build();
   private static final UniformInt angerTime = TimeUtil.rangeOfSeconds(20, 39);
   private final GossipContainer gossips = new GossipContainer();
   public long lastGossipTime;
   public long lastGossipDecayTime;
   public SimpleContainer guardInventory = new SimpleContainer(6);
   public int kickTicks;
   public int shieldCoolDown;
   public int kickCoolDown;
   public boolean interacting;
   protected boolean spawnWithArmor;
   private int remainingPersistentAngerTime;
   private UUID persistentAngerTarget;

   public Guard(EntityType<? extends Guard> type, Level world) {
      super(type, world);
      this.guardInventory.addListener(this);
      this.setPersistenceRequired();
      if ((Boolean)GuardConfig.COMMON.GuardsOpenDoors.get()) {
         ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      }

      this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_OTHER, -1.0F);
   }

   protected PathNavigation createNavigation(Level level) {
      return new Guard.GuardGroundPathNavigation(this, level);
   }

   public static int slotToInventoryIndex(EquipmentSlot slot) {
      return switch (slot) {
         case CHEST -> 1;
         case FEET -> 3;
         case LEGS -> 2;
         default -> 0;
      };
   }

   public static String getVariantFromBiome(LevelAccessor world, BlockPos pos) {
      VillagerType type = VillagerType.byBiome(world.getBiome(pos));
      return GuardVillagers.removeModIdFromVillagerType(type.toString());
   }

   public static Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, (Double)GuardConfig.STARTUP.healthModifier.get())
         .add(Attributes.MOVEMENT_SPEED, (Double)GuardConfig.STARTUP.speedModifier.get())
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.FOLLOW_RANGE, (Double)GuardConfig.STARTUP.followRangeModifier.get());
   }

   public SpawnGroupData finalizeSpawn(
      @NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn
   ) {
      this.setPersistenceRequired();
      String type = getVariantFromBiome(this.level(), this.blockPosition());
      this.setVariant(type);
      RandomSource randomsource = worldIn.getRandom();
      this.populateDefaultEquipmentSlots(randomsource, difficultyIn);
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   protected void doPush(@NotNull Entity entityIn) {
      if (entityIn instanceof PathfinderMob living) {
         boolean attackTargets = living.getTarget() instanceof Villager || living.getTarget() instanceof IronGolem || living.getTarget() instanceof Guard;
         if (attackTargets) {
            this.setTarget(living);
         }
      }

      super.doPush(entityIn);
   }

   @Nullable
   public BlockPos getPatrolPos() {
      return (BlockPos)((Optional)this.entityData.get(GUARD_POS)).orElse(null);
   }

   public void setPatrolPos(BlockPos position) {
      this.entityData.set(GUARD_POS, Optional.ofNullable(position));
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)GuardSounds.GUARD_AMBIENT.value();
   }

   protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
      return (SoundEvent)GuardSounds.GUARD_HURT.value();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)GuardSounds.GUARD_DEATH.value();
   }

   protected void dropCustomDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource source, boolean recentlyHitIn) {
      for (int i = 0; i < this.guardInventory.getContainerSize(); i++) {
         ItemStack itemstack = this.guardInventory.getItem(i);
         RandomSource randomsource = this.level().getRandom();
         if (!itemstack.isEmpty()
            && !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)
            && randomsource.nextFloat() < ((Double)GuardConfig.COMMON.chanceToDropEquipment.get()).floatValue()) {
            this.spawnAtLocation(itemstack);
         }
      }
   }

   public void readAdditionalSaveData(@NotNull CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("Type", 99)) {
         int variantint = compound.getInt("Type");
         if (variantint == 1) {
            compound.putString("Variant", "desert");
         } else if (variantint == 2) {
            compound.putString("Variant", "savanna");
         } else if (variantint == 3) {
            compound.putString("Variant", "swamp");
         } else if (variantint == 4) {
            compound.putString("Variant", "jungle");
         } else if (variantint == 5) {
            compound.putString("Variant", "taiga");
         } else if (variantint == 6) {
            compound.putString("Variant", "snow");
         } else if (variantint == 0) {
            compound.putString("Variant", "plains");
         }
      }

      UUID uuid = compound.hasUUID("Owner") ? compound.getUUID("Owner") : null;
      if (uuid != null) {
         try {
            this.setOwnerId(uuid);
         } catch (Throwable var9) {
            this.setOwnerId(null);
         }
      }

      this.kickTicks = compound.getInt("KickTicks");
      this.setFollowing(compound.getBoolean("Following"));
      this.interacting = compound.getBoolean("Interacting");
      this.setPatrolling(compound.getBoolean("Patrolling"));
      this.shieldCoolDown = compound.getInt("KickCooldown");
      this.kickCoolDown = compound.getInt("ShieldCooldown");
      this.lastGossipDecayTime = compound.getLong("LastGossipDecay");
      this.lastGossipTime = compound.getLong("LastGossipTime");
      this.spawnWithArmor = compound.getBoolean("SpawnWithArmor");
      if (compound.contains("Variant")) {
         this.setVariant(GuardVillagers.removeModIdFromVillagerType(compound.getString("Variant")));
      }

      if (compound.contains("PatrolPosX")) {
         int x = compound.getInt("PatrolPosX");
         int y = compound.getInt("PatrolPosY");
         int z = compound.getInt("PatrolPosZ");
         this.entityData.set(GUARD_POS, Optional.of(new BlockPos(x, y, z)));
      }

      ListTag listtag = compound.getList("Gossips", 10);
      this.gossips.update(new Dynamic(NbtOps.INSTANCE, listtag));
      ListTag listnbt = compound.getList("Inventory", 9);

      for (int i = 0; i < listnbt.size(); i++) {
         CompoundTag compoundnbt = listnbt.getCompound(i);
         int j = compoundnbt.getByte("Slot") & 255;
         ItemStack stack = ItemStack.parseOptional(this.registryAccess(), compoundnbt);
         if (!stack.isEmpty()) {
            this.guardInventory.setItem(j, stack);
         } else {
            listtag.add(new CompoundTag());
         }
      }

      if (compound.contains("ArmorItems", 9)) {
         ListTag armorItems = compound.getList("ArmorItems", 10);

         for (int ix = 0; ix < this.armorItems.size(); ix++) {
            ItemStack stack = ItemStack.parseOptional(this.registryAccess(), armorItems.getCompound(ix));
            if (!stack.isEmpty()) {
               int index = slotToInventoryIndex(
                  this.getEquipmentSlotForItem(ItemStack.parse(this.registryAccess(), armorItems.getCompound(ix)).orElse(ItemStack.EMPTY))
               );
               this.guardInventory.setItem(index, stack);
            } else {
               listtag.add(new CompoundTag());
            }
         }

         if (compound.contains("HandItems", 9)) {
            ListTag handItems = compound.getList("HandItems", 10);

            for (int ixx = 0; ixx < this.handItems.size(); ixx++) {
               int handSlot = ixx == 0 ? 5 : 4;
               if (!ItemStack.parseOptional(this.registryAccess(), handItems.getCompound(ixx)).isEmpty()) {
                  this.guardInventory.setItem(handSlot, ItemStack.parseOptional(this.registryAccess(), handItems.getCompound(ixx)));
               } else {
                  listtag.add(new CompoundTag());
               }
            }

            if (!this.level().isClientSide) {
               this.readPersistentAngerSaveData(this.level(), compound);
            }
         }
      }
   }

   public void addAdditionalSaveData(@NotNull CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putString("Variant", this.getVariant());
      compound.putInt("KickTicks", this.kickTicks);
      compound.putInt("ShieldCooldown", this.shieldCoolDown);
      compound.putInt("KickCooldown", this.kickCoolDown);
      compound.putBoolean("Following", this.isFollowing());
      compound.putBoolean("Interacting", this.interacting);
      compound.putBoolean("Patrolling", this.isPatrolling());
      compound.putBoolean("SpawnWithArmor", this.spawnWithArmor);
      compound.putLong("LastGossipTime", this.lastGossipTime);
      compound.putLong("LastGossipDecay", this.lastGossipDecayTime);
      if (this.getOwnerId() != null) {
         compound.putUUID("Owner", this.getOwnerId());
      }

      ListTag listnbt = new ListTag();

      for (int i = 0; i < this.guardInventory.getContainerSize(); i++) {
         ItemStack itemstack = this.guardInventory.getItem(i);
         if (!itemstack.isEmpty()) {
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.putByte("Slot", (byte)i);
            listnbt.add(itemstack.save(this.registryAccess(), compoundnbt));
         } else {
            listnbt.add(new CompoundTag());
         }
      }

      compound.put("Inventory", listnbt);
      if (this.getPatrolPos() != null) {
         compound.putInt("PatrolPosX", this.getPatrolPos().getX());
         compound.putInt("PatrolPosY", this.getPatrolPos().getY());
         compound.putInt("PatrolPosZ", this.getPatrolPos().getZ());
      }

      compound.put("Gossips", (Tag)this.gossips.store(NbtOps.INSTANCE));
      this.addPersistentAngerSaveData(compound);
   }

   private void maybeDecayGossip() {
      long i = this.level().getGameTime();
      if (this.lastGossipDecayTime == 0L) {
         this.lastGossipDecayTime = i;
      } else if (i >= this.lastGossipDecayTime + 24000L) {
         this.gossips.decay();
         this.lastGossipDecayTime = i;
      }
   }

   protected void completeUsingItem() {
      if (this.isUsingItem()) {
         InteractionHand interactionhand = this.getUsedItemHand();
         if (!this.useItem.equals(this.getItemInHand(interactionhand))) {
            this.releaseUsingItem();
         } else if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.triggerItemUseEffects(this.useItem, 16);
            ItemStack copy = this.useItem.copy();
            ItemStack itemstack = EventHooks.onItemUseFinish(this, copy, this.getUseItemRemainingTicks(), this.useItem.finishUsingItem(this.level(), this));
            if (itemstack != this.useItem) {
               this.setItemInHand(interactionhand, itemstack);
            }

            if (this.useItem.getUseAnimation() != UseAnim.EAT) {
               this.useItem.shrink(1);
            }

            this.stopUsingItem();
         }
      }
   }

   @NotNull
   public ItemStack getItemBySlot(EquipmentSlot pSlot) {
      return switch (pSlot) {
         case CHEST -> this.guardInventory.getItem(1);
         case FEET -> this.guardInventory.getItem(3);
         case LEGS -> this.guardInventory.getItem(2);
         case HEAD -> this.guardInventory.getItem(0);
         case OFFHAND -> this.guardInventory.getItem(4);
         case MAINHAND -> this.guardInventory.getItem(5);
         default -> ItemStack.EMPTY;
      };
   }

   public GossipContainer getGossips() {
      return this.gossips;
   }

   public int getPlayerReputation(Player player) {
      return this.gossips.getReputation(player.getUUID(), gossipType -> true);
   }

   @Nullable
   public LivingEntity getOwner() {
      try {
         UUID uuid = this.getOwnerId();
         boolean heroOfTheVillage = uuid != null
            && this.level().getPlayerByUUID(uuid) != null
            && Objects.requireNonNull(this.level().getPlayerByUUID(uuid)).hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
         return uuid != null
               && (this.level().getPlayerByUUID(uuid) == null || heroOfTheVillage || !GuardConfig.COMMON.followHero.get())
               && (GuardConfig.COMMON.followHero.get() || this.level().getPlayerByUUID(uuid) != null)
            ? this.level().getPlayerByUUID(uuid)
            : null;
      } catch (IllegalArgumentException var3) {
         return null;
      }
   }

   public boolean isOwner(LivingEntity entityIn) {
      return entityIn == this.getOwner();
   }

   @Nullable
   public UUID getOwnerId() {
      return (UUID)((Optional)this.entityData.get(OWNER_UNIQUE_ID)).orElse(null);
   }

   public void setOwnerId(@Nullable UUID p_184754_1_) {
      this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
   }

   public boolean doHurtTarget(@NotNull Entity entityIn) {
      if (this.isKicking()) {
         ((LivingEntity)entityIn).knockback(1.0, Mth.sin(this.getYRot() * 0.017453292F), -Mth.cos(this.getYRot() * 0.017453292F));
         this.kickTicks = 10;
         this.level().broadcastEntityEvent(this, (byte)4);
         this.lookAt(entityIn, 90.0F, 90.0F);
      }

      ItemStack hand = this.getMainHandItem();
      this.damageGuardItem(1, EquipmentSlot.MAINHAND, hand);
      return super.doHurtTarget(entityIn);
   }

   public void handleEntityEvent(byte id) {
      if (id == 4) {
         this.kickTicks = 10;
      } else {
         super.handleEntityEvent(id);
      }
   }

   public boolean isImmobile() {
      return this.interacting || super.isImmobile();
   }

   public void die(@NotNull DamageSource source) {
      if ((Boolean)GuardConfig.COMMON.convertGuardOnDeath.get()
         && (this.level().getDifficulty() == Difficulty.NORMAL || this.level().getDifficulty() == Difficulty.HARD)
         && source.getEntity() instanceof Zombie
         && EventHooks.canLivingConvert((LivingEntity)source.getEntity(), EntityType.ZOMBIE_VILLAGER, timer -> {})) {
         ZombieVillager zombieguard = (ZombieVillager)this.convertTo(EntityType.ZOMBIE_VILLAGER, true);
         if (this.level().getDifficulty() != Difficulty.HARD && this.random.nextBoolean() || zombieguard == null) {
            return;
         }

         if (!this.isSilent()) {
            this.level().levelEvent(null, 1026, this.blockPosition(), 0);
         }

         this.discard();
      }

      super.die(source);
      Component deathMessage = this.getCombatTracker().getDeathMessage();
      if (this.dead
         && !this.level().isClientSide
         && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)
         && this.getOwner() instanceof ServerPlayer) {
         this.getOwner().sendSystemMessage(deathMessage);
      }
   }

   @NotNull
   public ItemStack eat(@NotNull Level world, ItemStack stack, @NotNull FoodProperties foodProperties) {
      if (stack.getUseAnimation() == UseAnim.EAT) {
         this.heal(foodProperties.nutrition());
      }

      world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
      super.eat(world, stack, foodProperties);
      return stack;
   }

   public void aiStep() {
      if (this.kickTicks > 0) {
         this.kickTicks--;
      }

      if (this.kickCoolDown > 0) {
         this.kickCoolDown--;
      }

      if (this.shieldCoolDown > 0) {
         this.shieldCoolDown--;
      }

      if (this.getHealth() < this.getMaxHealth() && this.tickCount % 200 == 0) {
         this.heal(((Double)GuardConfig.COMMON.amountOfHealthRegenerated.get()).floatValue());
      }

      if (this.spawnWithArmor) {
         getItemsFromLootTable(this);
         this.spawnWithArmor = false;
      }

      if (!this.level().isClientSide) {
         this.updatePersistentAnger((ServerLevel)this.level(), true);
      }

      this.updateSwingTime();
      super.aiStep();
   }

   public void tick() {
      this.maybeDecayGossip();
      super.tick();
   }

   @NotNull
   public EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
      return SIZE_BY_POSE.getOrDefault(pose, EntityDimensions.scalable(0.6F, 1.95F));
   }

   protected void blockUsingShield(@NotNull LivingEntity entityIn) {
      super.blockUsingShield(entityIn);
      this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
      if (entityIn.getMainHandItem().canDisableShield(this.useItem, this, entityIn)) {
         this.disableShield();
      }
   }

   protected void hurtCurrentlyUsedShield(float damage) {
      if (this.useItem.canPerformAction(ItemAbilities.SHIELD_BLOCK) && damage >= 3.0F) {
         int i = 1 + Mth.floor(damage);
         InteractionHand hand = this.getUsedItemHand();
         this.damageGuardItem(i, LivingEntity.getSlotForHand(hand), this.useItem);
         if (this.useItem.isEmpty()) {
            if (hand == InteractionHand.MAIN_HAND) {
               this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else {
               this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }

            this.useItem = ItemStack.EMPTY;
            this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
         }
      }
   }

   public void startUsingItem(@NotNull InteractionHand hand) {
      super.startUsingItem(hand);
      ItemStack itemstack = this.getItemInHand(hand);
      if (itemstack.canPerformAction(ItemAbilities.SHIELD_BLOCK)) {
         AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

         assert modifiableattributeinstance != null;

         modifiableattributeinstance.removeModifier(USE_ITEM_SPEED_PENALTY);
         modifiableattributeinstance.addTransientModifier(USE_ITEM_SPEED_PENALTY);
      }
   }

   public void stopUsingItem() {
      super.stopUsingItem();
      if (this.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(USE_ITEM_SPEED_PENALTY.id())) {
         this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(USE_ITEM_SPEED_PENALTY);
      }
   }

   public void disableShield() {
      this.shieldCoolDown = 100;
      this.stopUsingItem();
      this.level().broadcastEntityEvent(this, (byte)30);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder data) {
      super.defineSynchedData(data);
      data.define(GUARD_VARIANT, VillagerType.PLAINS.toString());
      data.define(DATA_CHARGING_STATE, false);
      data.define(KICKING, false);
      data.define(OWNER_UNIQUE_ID, Optional.empty());
      data.define(FOLLOWING, false);
      data.define(GUARD_POS, Optional.empty());
      data.define(PATROLLING, false);
      data.define(RUNNING_TO_EAT, false);
   }

   public void setChargingCrossbow(boolean charging) {
      this.entityData.set(DATA_CHARGING_STATE, charging);
   }

   public boolean isKicking() {
      return (Boolean)this.entityData.get(KICKING);
   }

   public void setKicking(boolean kicking) {
      this.entityData.set(KICKING, kicking);
   }

   protected void populateDefaultEquipmentSlots(RandomSource source, DifficultyInstance instance) {
      this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 100.0F;
      this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 100.0F;
      this.spawnWithArmor = true;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Guard.KickGoal(this));
      this.goalSelector.addGoal(0, new Guard.GuardEatFoodGoal(this));
      this.goalSelector.addGoal(0, new Guard.RaiseShieldGoal(this));
      this.goalSelector.addGoal(1, new Guard.GuardRunToEatGoal(this));
      this.goalSelector
         .addGoal(3, new Guard.RangedCrossbowAttackPassiveGoal<>(this, 1.0, ((Double)GuardConfig.COMMON.guardCrossbowAttackRadius.get()).floatValue()));
      this.goalSelector.addGoal(3, new Guard.GuardBowAttack(this, 0.5, 20, 15.0F));
      if (ModList.get().isLoaded("musketmod")) {
         this.goalSelector.addGoal(3, new ModCompat.UseMusketGoal<>(this, 20, 15.0F));
      }

      this.goalSelector.addGoal(3, new Guard.GuardMeleeGoal(this, 0.8, true));
      this.goalSelector.addGoal(4, new Guard.FollowHeroGoal(this, 0.800000011920929, 10.0F, 4.0F));
      if ((Boolean)GuardConfig.COMMON.GuardsRunFromPolarBears.get()) {
         this.goalSelector.addGoal(4, new AvoidEntityGoal(this, PolarBear.class, 12.0F, 1.0, 1.2));
      }

      this.goalSelector.addGoal(4, new MoveBackToVillageGoal(this, 0.5, false));
      if ((Boolean)GuardConfig.COMMON.GuardsOpenDoors.get()) {
         this.goalSelector.addGoal(4, new Guard.GuardInteractDoorGoal(this, true));
      }

      if ((Boolean)GuardConfig.COMMON.GuardFormation.get()) {
         this.goalSelector.addGoal(6, new Guard.FollowShieldGuards(this));
      }

      this.goalSelector.addGoal(3, new Guard.WalkBackToCheckPointGoal(this, 0.5));
      if ((Boolean)GuardConfig.COMMON.guardPatrolAroundVillageWorkstations.get()) {
         this.goalSelector.addGoal(5, new GolemRandomStrollInVillageGoal(this, 0.5));
      }

      if ((Boolean)GuardConfig.COMMON.guardPatrolVillageAi.get()) {
         this.goalSelector.addGoal(5, new MoveThroughVillageGoal(this, 0.5, false, 4, () -> false));
      }

      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.5));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, AbstractVillager.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new Guard.GuardLookAtAndStopMovingWhenBeingTheInteractionTarget(this));
      if ((Boolean)GuardConfig.COMMON.guardSinkToFightUnderWater.get()) {
         this.goalSelector
            .addGoal(
               10,
               new FloatGoal(this) {
                  public boolean canUse() {
                     return super.canUse()
                        && (
                           Guard.this.getTarget() != null
                                 && Guard.this.getY() - Guard.this.getTarget().getY()
                                    >= ((Integer)GuardConfig.COMMON.depthGuardHuntUnderwater.get()).intValue()
                              || Guard.this.getMainHandItem().getItem() instanceof ProjectileWeaponItem
                              || Guard.this.getTarget() == null
                              || Guard.this.getAirSupply() <= 0
                        );
                  }
               }
            );
      } else {
         this.goalSelector.addGoal(10, new FloatGoal(this));
      }

      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[]{Guard.class, IronGolem.class}).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(3, new Guard.HeroHurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new Guard.HeroHurtTargetGoal(this));
      this.targetSelector.addGoal(5, new Guard.DefendVillageGuardGoal(this));
      if ((Boolean)GuardConfig.COMMON.AttackAllMobs.get()) {
         this.targetSelector
            .addGoal(
               5,
               new NearestAttackableTargetGoal(
                  this, Mob.class, 5, true, true, mob -> mob instanceof Enemy && !((List)GuardConfig.COMMON.MobBlackList.get()).contains(mob.getEncodeId())
               )
            );
      } else {
         this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Ravager.class, true));
         this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Witch.class, true));
         this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Raider.class, true));
         this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Zombie.class, true, mob -> !(mob instanceof ZombifiedPiglin)));
      }

      this.targetSelector
         .addGoal(
            5,
            new NearestAttackableTargetGoal(
               this, LivingEntity.class, 5, true, true, mob -> ((List)GuardConfig.COMMON.MobWhiteList.get()).contains(mob.getEncodeId())
            )
         );
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal(this, false));
   }

   public boolean mayBeLeashed() {
      return false;
   }

   public void performRangedAttack(LivingEntity target, float distanceFactor) {
      this.shieldCoolDown = 8;
      if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
         this.performCrossbowAttack(this, 1.6F);
      }

      if (this.getMainHandItem().getItem() instanceof BowItem) {
         ItemStack hand = this.getMainHandItem();
         ItemStack itemstack = this.getProjectile(hand);
         AbstractArrow abstractarrowentity = ProjectileUtil.getMobArrow(this, itemstack, distanceFactor, hand);
         abstractarrowentity = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity, itemstack, hand);
         double d0 = target.getX() - this.getX();
         double d1 = target.getY(0.3333333333333333) - abstractarrowentity.getY();
         double d2 = target.getZ() - this.getZ();
         double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
         abstractarrowentity.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, 0.0F);
         this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
         this.level().addFreshEntity(abstractarrowentity);
         this.damageGuardItem(1, EquipmentSlot.MAINHAND, hand);
      }

      if (ModList.get().isLoaded("musketmod")) {
         ModCompat.shootGun(this);
      }
   }

   public void performCrossbowAttack(LivingEntity p_32337_, float p_32338_) {
      InteractionHand interactionhand = ProjectileUtil.getWeaponHoldingHand(p_32337_, item -> item instanceof CrossbowItem);
      ItemStack itemstack = p_32337_.getItemInHand(interactionhand);
      if (itemstack.getItem() instanceof CrossbowItem crossbowitem) {
         crossbowitem.performShooting(p_32337_.level(), p_32337_, interactionhand, itemstack, p_32338_, 0.0F, null);
      }

      this.onCrossbowAttackPerformed();
   }

   public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
      super.setItemSlot(slotIn, stack);
      switch (slotIn) {
         case CHEST:
            this.guardInventory.setItem(1, (ItemStack)this.armorItems.get(slotIn.getIndex()));
            break;
         case FEET:
            this.guardInventory.setItem(3, (ItemStack)this.armorItems.get(slotIn.getIndex()));
            break;
         case LEGS:
            this.guardInventory.setItem(2, (ItemStack)this.armorItems.get(slotIn.getIndex()));
            break;
         case HEAD:
            this.guardInventory.setItem(0, (ItemStack)this.armorItems.get(slotIn.getIndex()));
            break;
         case OFFHAND:
            this.guardInventory.setItem(4, (ItemStack)this.handItems.get(slotIn.getIndex()));
            break;
         case MAINHAND:
            this.guardInventory.setItem(5, (ItemStack)this.handItems.get(slotIn.getIndex()));
      }
   }

   public ItemStack getProjectile(ItemStack shootable) {
      if (shootable.getItem() instanceof ProjectileWeaponItem) {
         Predicate<ItemStack> predicate = ((ProjectileWeaponItem)shootable.getItem()).getSupportedHeldProjectiles();
         ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
         return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public int getKickTicks() {
      return this.kickTicks;
   }

   public boolean isFollowing() {
      return (Boolean)this.entityData.get(FOLLOWING);
   }

   public void setFollowing(boolean following) {
      this.entityData.set(FOLLOWING, following);
   }

   public boolean canAttack(LivingEntity target) {
      return !((List)GuardConfig.COMMON.MobBlackList.get()).contains(target.getEncodeId())
         && !target.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
         && !this.isOwner(target)
         && super.canAttack(target);
   }

   public void rideTick() {
      super.rideTick();
      if (this.getVehicle() instanceof PathfinderMob creatureentity) {
         this.yBodyRot = creatureentity.yBodyRot;
      }
   }

   public void onCrossbowAttackPerformed() {
      this.noActionTime = 0;
   }

   public void setTarget(LivingEntity entity) {
      if (entity == null
         || !entity.isAlive()
         || (this.getTeam() == null || entity.getTeam() == null || !this.getTeam().isAlliedTo(entity.getTeam()))
            && !((List)GuardConfig.COMMON.MobBlackList.get()).contains(EntityType.getKey(entity.getType()).toString())
            && !entity.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
            && !this.isOwner(entity)
            && !(entity instanceof TamableAnimal tamed && tamed.getOwner() != null && tamed.getOwner().getUUID().equals(this.getOwnerId()))) {
         super.setTarget(entity);
      }
   }

   public void gossip(Villager villager, long gameTime) {
      if ((gameTime < this.lastGossipTime || gameTime >= this.lastGossipTime + 1200L)
         && (gameTime < villager.lastGossipTime || gameTime >= villager.lastGossipTime + 1200L)) {
         this.gossips.transferFrom(villager.getGossips(), this.random, 10);
         this.lastGossipTime = gameTime;
         villager.lastGossipTime = gameTime;
      }
   }

   protected void blockedByShield(LivingEntity entityIn) {
      if (this.isKicking()) {
         this.setKicking(false);
      }

      super.blockedByShield(this);
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      boolean configValues = player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE) && (Boolean)GuardConfig.COMMON.giveGuardStuffHOTV.get()
         || player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE) && (Boolean)GuardConfig.COMMON.setGuardPatrolHotv.get()
         || player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
            && (Boolean)GuardConfig.COMMON.giveGuardStuffHOTV.get()
            && (Boolean)GuardConfig.COMMON.setGuardPatrolHotv.get()
         || this.getPlayerReputation(player) >= (Integer)GuardConfig.COMMON.reputationRequirement.get()
         || player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
            && !(Boolean)GuardConfig.COMMON.giveGuardStuffHOTV.get()
            && !(Boolean)GuardConfig.COMMON.setGuardPatrolHotv.get()
         || this.getOwnerId() != null && this.getOwnerId().equals(player.getUUID());
      boolean inventoryRequirements = !player.isSecondaryUseActive();
      if (inventoryRequirements) {
         if (this.getTarget() != player && this.isEffectiveAi() && configValues && player instanceof ServerPlayer) {
            this.openGui((ServerPlayer)player);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.CONSUME;
         }
      } else {
         return super.mobInteract(player, hand);
      }
   }

   public void onReputationEventFrom(ReputationEventType reputationEventType, Entity entity) {
   }

   public void containerChanged(Container invBasic) {
   }

   protected void hurtArmor(DamageSource damageSource, float damage) {
      if (this.random.nextFloat() < ((Double)GuardConfig.COMMON.chanceToBreakEquipment.get()).floatValue()) {
         this.doHurtEquipment(damageSource, damage, new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD});
      }
   }

   public void thunderHit(ServerLevel p_241841_1_, LightningBolt p_241841_2_) {
      if (p_241841_1_.getDifficulty() != Difficulty.PEACEFUL && EventHooks.canLivingConvert(this, EntityType.WITCH, timer -> {})) {
         Witch witchentity = (Witch)EntityType.WITCH.create(p_241841_1_);
         if (witchentity == null) {
            return;
         }

         witchentity.copyPosition(this);
         witchentity.finalizeSpawn(p_241841_1_, p_241841_1_.getCurrentDifficultyAt(witchentity.blockPosition()), MobSpawnType.CONVERSION, null);
         witchentity.setNoAi(this.isNoAi());
         witchentity.setCustomName(this.getCustomName());
         witchentity.setCustomNameVisible(this.isCustomNameVisible());
         witchentity.setPersistenceRequired();
         p_241841_1_.addFreshEntityWithPassengers(witchentity);
         this.discard();
      } else {
         super.thunderHit(p_241841_1_, p_241841_2_);
      }
   }

   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(UUID arg0) {
      this.persistentAngerTarget = arg0;
   }

   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   public void setRemainingPersistentAngerTime(int arg0) {
      this.remainingPersistentAngerTime = arg0;
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(angerTime.sample(this.random));
   }

   public void openGui(ServerPlayer player) {
      this.setOwnerId(player.getUUID());
      if (player.containerMenu != player.inventoryMenu) {
         player.closeContainer();
      }

      this.interacting = true;
      player.nextContainerCounter();
      player.connection.send(new GuardOpenInventoryPacket(player.containerCounter, this.guardInventory.getContainerSize(), this.getId()));
      player.containerMenu = new GuardContainer(player.containerCounter, player.getInventory(), this.guardInventory, this);
      player.initMenu(player.containerMenu);
      NeoForge.EVENT_BUS.post(new Open(player, player.containerMenu));
   }

   public boolean isEating() {
      return isConsumable(this.getUseItem()) && this.isUsingItem();
   }

   public boolean isPatrolling() {
      return (Boolean)this.entityData.get(PATROLLING);
   }

   public void setPatrolling(boolean patrolling) {
      this.entityData.set(PATROLLING, patrolling);
   }

   public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
      return item instanceof BowItem || item instanceof CrossbowItem || super.canFireProjectileWeapon(item);
   }

   public static boolean isConsumable(ItemStack stack) {
      return stack.getUseAnimation() == UseAnim.EAT || stack.getUseAnimation() == UseAnim.DRINK && !(stack.getItem() instanceof SplashPotionItem);
   }

   public void tryToTeleportToOwner() {
      LivingEntity livingentity = this.getOwner();
      if (livingentity != null) {
         this.teleportToAroundBlockPos(livingentity.blockPosition());
      }
   }

   public boolean shouldTryTeleportToOwner() {
      LivingEntity livingentity = this.getOwner();
      return livingentity != null
         && this.distanceToSqr(this.getOwner()) >= 144.0
         && (Boolean)GuardConfig.COMMON.guardTeleport.get()
         && this.getTarget() == null;
   }

   private void teleportToAroundBlockPos(BlockPos pos) {
      for (int i = 0; i < 10; i++) {
         int j = this.random.nextIntBetweenInclusive(-4, 4);
         int k = this.random.nextIntBetweenInclusive(-4, 4);
         if (Math.abs(j) >= 3 || Math.abs(k) >= 3) {
            int l = this.random.nextIntBetweenInclusive(-1, 1);
            if (this.maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
               return;
            }
         }
      }
   }

   private boolean maybeTeleportTo(int x, int y, int z) {
      if (!this.canTeleportTo(new BlockPos(x, y, z))) {
         return false;
      } else {
         this.moveTo(x + 0.5, y, z + 0.5, this.getYRot(), this.getXRot());
         this.navigation.stop();
         return true;
      }
   }

   private boolean canTeleportTo(BlockPos pos) {
      PathType pathtype = WalkNodeEvaluator.getPathTypeStatic(this, pos);
      if (pathtype != PathType.WALKABLE) {
         return false;
      } else {
         BlockState blockstate = this.level().getBlockState(pos.below());
         if (blockstate.getBlock() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos blockpos = pos.subtract(this.blockPosition());
            return this.level().noCollision(this, this.getBoundingBox().move(blockpos));
         }
      }
   }

   public static List<ItemStack> getItemsFromLootTable(LivingEntity entity) {
      LootTable loot = entity.level().getServer().reloadableRegistries().getLootTable(getLootTableFromData());
      net.minecraft.world.level.storage.loot.LootParams.Builder lootcontext$builder = new net.minecraft.world.level.storage.loot.LootParams.Builder(
            (ServerLevel)entity.level()
         )
         .withParameter(LootContextParams.THIS_ENTITY, entity);
      return loot.getRandomItems(lootcontext$builder.create(GuardLootTables.SLOT));
   }

   public static ResourceKey<LootTable> getLootTableFromData() {
      ResourceLocation lootTable = ResourceLocation.fromNamespaceAndPath("guardvillagers", "entities/guard_armor");
      return ResourceKey.create(Registries.LOOT_TABLE, lootTable);
   }

   public void setVariant(String variant) {
      this.entityData.set(GUARD_VARIANT, variant);
   }

   public String getVariant() {
      String variant = (String)this.entityData.get(GUARD_VARIANT);
      return !variant.isEmpty() ? variant : "plains";
   }

   public void damageGuardItem(int damage, EquipmentSlot slotToDamage, ItemStack item) {
      if (this.random.nextFloat() < ((Double)GuardConfig.COMMON.chanceToBreakEquipment.get()).floatValue()) {
         item.hurtAndBreak(damage, this, slotToDamage);
      }
   }

   public static class DefendVillageGuardGoal extends TargetGoal {
      private final Guard guard;
      private LivingEntity villageAggressorTarget;

      public DefendVillageGuardGoal(Guard guardIn) {
         super(guardIn, true, true);
         this.guard = guardIn;
         this.setFlags(EnumSet.of(Flag.TARGET, Flag.MOVE));
      }

      public boolean canUse() {
         AABB axisalignedbb = this.guard.getBoundingBox().inflate(10.0, 8.0, 10.0);
         List<Villager> list = this.guard.level().getEntitiesOfClass(Villager.class, axisalignedbb);
         List<Player> list1 = this.guard.level().getEntitiesOfClass(Player.class, axisalignedbb);

         for (Villager villager : list) {
            for (Player player : list1) {
               int i = villager.getPlayerReputation(player);
               if (i <= (Integer)GuardConfig.COMMON.reputationRequirementToBeAttacked.get()) {
                  this.villageAggressorTarget = player;
                  if (this.villageAggressorTarget.getTeam() != null
                     && this.guard.getTeam() != null
                     && this.guard.getTeam().isAlliedTo(this.villageAggressorTarget.getTeam())) {
                     return false;
                  }
               }
            }
         }

         return this.villageAggressorTarget != null
            && !this.villageAggressorTarget.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
            && !this.villageAggressorTarget.isSpectator()
            && !((Player)this.villageAggressorTarget).isCreative();
      }

      public void start() {
         this.guard.setTarget(this.villageAggressorTarget);
         super.start();
      }
   }

   public static class FollowHeroGoal extends Goal {
      private final Guard guard;
      private LivingEntity owner;
      private final double speedModifier;
      private final PathNavigation navigation;
      private int timeToRecalcPath;
      private final float stopDistance;
      private final float startDistance;
      private float oldWaterCost;

      public FollowHeroGoal(Guard guard, double speedModifier, float startDistance, float stopDistance) {
         this.guard = guard;
         this.speedModifier = speedModifier;
         this.navigation = guard.getNavigation();
         this.startDistance = startDistance;
         this.stopDistance = stopDistance;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.guard.getOwner();
         if (livingentity == null) {
            return false;
         } else if (this.guard.distanceToSqr(livingentity) < this.startDistance * this.startDistance) {
            return false;
         } else {
            this.owner = livingentity;
            return this.guard.isFollowing();
         }
      }

      public boolean canContinueToUse() {
         return this.navigation.isDone() ? false : this.guard.distanceToSqr(this.owner) >= this.stopDistance * this.stopDistance && this.guard.isFollowing();
      }

      public void start() {
         this.timeToRecalcPath = 0;
         this.oldWaterCost = this.guard.getPathfindingMalus(PathType.WATER);
         this.guard.setPathfindingMalus(PathType.WATER, 0.0F);
      }

      public void stop() {
         this.owner = null;
         this.navigation.stop();
         this.guard.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
      }

      public void tick() {
         boolean shouldTryTeleportToOwner = this.guard.shouldTryTeleportToOwner();
         if (!shouldTryTeleportToOwner) {
            this.guard.getLookControl().setLookAt(this.owner, 10.0F, this.guard.getMaxHeadXRot());
         }

         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (shouldTryTeleportToOwner) {
               this.guard.tryToTeleportToOwner();
            } else {
               this.navigation.moveTo(this.owner, this.speedModifier);
            }
         }
      }
   }

   public static class FollowShieldGuards extends Goal {
      private static final TargetingConditions NEARBY_GUARDS = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight();
      private final Guard taskOwner;
      private Guard guardtofollow;
      private double x;
      private double y;
      private double z;

      public FollowShieldGuards(Guard taskOwnerIn) {
         this.taskOwner = taskOwnerIn;
      }

      public boolean canUse() {
         List<? extends Guard> list = this.taskOwner
            .level()
            .getEntitiesOfClass(this.taskOwner.getClass(), this.taskOwner.getBoundingBox().inflate(8.0, 8.0, 8.0));
         if (!list.isEmpty()) {
            for (Guard guard : list) {
               if (!guard.isInvisible()
                  && guard.getOffhandItem().canPerformAction(ItemAbilities.SHIELD_BLOCK)
                  && guard.isBlocking()
                  && this.taskOwner
                        .level()
                        .getNearbyEntities(Guard.class, NEARBY_GUARDS.range(3.0), guard, this.taskOwner.getBoundingBox().inflate(5.0))
                        .size()
                     < 5
                  && !(this.taskOwner.getMainHandItem().getItem() instanceof ProjectileWeaponItem)) {
                  this.guardtofollow = guard;
                  Vec3 vec3d = this.getPosition();
                  if (vec3d == null) {
                     return false;
                  }

                  this.x = vec3d.x;
                  this.y = vec3d.y;
                  this.z = vec3d.z;
                  return true;
               }
            }
         }

         return false;
      }

      @Nullable
      protected Vec3 getPosition() {
         return DefaultRandomPos.getPosTowards(this.taskOwner, 16, 7, this.guardtofollow.position(), 1.5707963705062866);
      }

      public boolean canContinueToUse() {
         return !this.taskOwner.getNavigation().isDone() && !this.taskOwner.isVehicle();
      }

      public void stop() {
         this.taskOwner.getNavigation().stop();
         super.stop();
      }

      public void start() {
         this.taskOwner.getNavigation().moveTo(this.x, this.y, this.z, 0.4);
      }
   }

   public static class GuardBowAttack extends RangedBowAttackGoal<Guard> {
      protected Guard guard;

      public GuardBowAttack(Guard mob, double speedModifier, int attackIntervalMin, float attackRadius) {
         super(mob, speedModifier, attackIntervalMin, attackRadius);
         this.guard = mob;
      }

      public boolean canUse() {
         return this.guard.getTarget() != null && this.isBowInMainhand() && !this.guard.isEating() && !this.guard.isBlocking();
      }

      protected boolean isBowInMainhand() {
         return this.guard.getMainHandItem().getItem() instanceof BowItem;
      }

      public void tick() {
         super.tick();
         LivingEntity attacker = this.guard.getTarget();
         if (attacker != null) {
            this.guard.getLookControl().setLookAt(attacker);
            this.guard.lookAt(attacker, 30.0F, 30.0F);
         }

         if (this.guard.isPatrolling()) {
            this.guard.getNavigation().stop();
            this.guard.getMoveControl().strafe(0.0F, 0.0F);
         }

         if (Guard.RangedCrossbowAttackPassiveGoal.friendlyInLineOfSight(this.guard)) {
            this.guard.stopUsingItem();
         }
      }

      public boolean canContinueToUse() {
         return (this.canUse() || !this.guard.getNavigation().isDone()) && this.isBowInMainhand();
      }
   }

   public static class GuardEatFoodGoal extends Goal {
      public final Guard guard;

      public GuardEatFoodGoal(Guard guard) {
         this.guard = guard;
      }

      public boolean canUse() {
         return this.guard.getHealth() < this.guard.getMaxHealth() && Guard.isConsumable(this.guard.getOffhandItem()) && this.guard.isEating()
            || this.guard.getHealth() < this.guard.getMaxHealth()
               && Guard.isConsumable(this.guard.getOffhandItem())
               && this.guard.getTarget() == null
               && !this.guard.isAggressive();
      }

      public boolean canContinueToUse() {
         List<LivingEntity> list = this.guard.level().getEntitiesOfClass(LivingEntity.class, this.guard.getBoundingBox().inflate(5.0, 3.0, 5.0));
         if (!list.isEmpty()) {
            for (LivingEntity mob : list) {
               if (mob != null && mob instanceof Mob && ((Mob)mob).getTarget() instanceof Guard) {
                  return false;
               }
            }
         }

         return this.guard.isUsingItem() && this.guard.getTarget() == null && this.guard.getHealth() < this.guard.getMaxHealth()
            || this.guard.getTarget() != null && this.guard.getHealth() < this.guard.getMaxHealth() / 2.0F + 2.0F && this.guard.isEating();
      }

      public void start() {
         this.guard.startUsingItem(InteractionHand.OFF_HAND);
      }
   }

   public static class GuardGroundPathNavigation extends GroundPathNavigation {
      private final Guard guard;

      public GuardGroundPathNavigation(Guard guard, Level level) {
         super(guard, level);
         this.guard = guard;
      }

      public boolean isDone() {
         return this.guard.isPatrolling() && this.guard.getTarget() == null && this.guard.blockPosition().equals(this.guard.getPatrolPos()) || super.isDone();
      }
   }

   public static class GuardInteractDoorGoal extends OpenDoorGoal {
      private final Guard guard;

      public GuardInteractDoorGoal(Guard pMob, boolean pCloseDoor) {
         super(pMob, pCloseDoor);
         this.guard = pMob;
      }

      public boolean canUse() {
         return super.canUse();
      }

      public void start() {
         if (this.areOtherMobsComingThroughDoor(this.guard)) {
            super.start();
            this.guard.swing(InteractionHand.MAIN_HAND);
         }
      }

      private boolean areOtherMobsComingThroughDoor(Guard pEntity) {
         List<? extends PathfinderMob> nearbyEntityList = pEntity.level().getEntitiesOfClass(PathfinderMob.class, pEntity.getBoundingBox().inflate(4.0));
         if (!nearbyEntityList.isEmpty()) {
            for (PathfinderMob mob : nearbyEntityList) {
               if (mob.blockPosition().closerToCenterThan(pEntity.position(), 2.0)) {
                  return this.isMobComingThroughDoor(mob);
               }
            }
         }

         return false;
      }

      private boolean isMobComingThroughDoor(PathfinderMob pEntity) {
         if (pEntity.getNavigation() == null) {
            return false;
         } else {
            Path path = pEntity.getNavigation().getPath();
            if (path != null && !path.isDone()) {
               Node node = path.getPreviousNode();
               if (node == null) {
                  return false;
               } else {
                  Node node1 = path.getNextNode();
                  return pEntity.blockPosition().equals(node.asBlockPos()) || pEntity.blockPosition().equals(node1.asBlockPos());
               }
            } else {
               return false;
            }
         }
      }
   }

   public static class GuardLookAtAndStopMovingWhenBeingTheInteractionTarget extends Goal {
      private final Guard guard;
      private Villager villager;

      public GuardLookAtAndStopMovingWhenBeingTheInteractionTarget(Guard guard) {
         this.guard = guard;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         List<Villager> list = this.guard.level().getEntitiesOfClass(Villager.class, this.guard.getBoundingBox().inflate(10.0));
         if (!list.isEmpty()) {
            for (Villager villager : list) {
               if (villager.getBrain().hasMemoryValue(MemoryModuleType.INTERACTION_TARGET)
                  && ((LivingEntity)villager.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get()).is(this.guard)) {
                  this.villager = villager;
                  return true;
               }
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         super.tick();
         this.guard.getNavigation().stop();
         this.guard.lookAt(this.villager, 30.0F, 30.0F);
         this.guard.getLookControl().setLookAt(this.villager);
      }
   }

   public static class GuardMeleeGoal extends MeleeAttackGoal {
      private static final double DEFAULT_ATTACK_REACH = Math.sqrt(2.0399999618530273) - 0.6000000238418579;
      public final Guard guard;

      public GuardMeleeGoal(Guard guard, double speedIn, boolean useLongMemory) {
         super(guard, speedIn, useLongMemory);
         this.guard = guard;
      }

      public boolean canUse() {
         return (!(this.mob.getMainHandItem().getItem() instanceof CrossbowItem) || !(this.mob.getMainHandItem().getItem() instanceof BowItem))
            && this.guard.getTarget() != null
            && !this.guard.isEating()
            && super.canUse();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.guard.getTarget() != null;
      }

      public void tick() {
         LivingEntity target = this.guard.getTarget();
         if (target != null) {
            if (target.distanceTo(this.guard) <= 3.0) {
               this.guard.getMoveControl().strafe(-2.0F, 0.0F);
               this.guard.lookAt(target, 30.0F, 30.0F);
            }

            if (this.path != null && target.distanceTo(this.guard) <= 2.5) {
               this.guard.getNavigation().stop();
            }

            super.tick();
         }
      }

      protected void checkAndPerformAttack(LivingEntity enemy) {
         if (this.canPerformAttack(enemy)) {
            this.resetAttackCooldown();
            this.guard.stopUsingItem();
            if (this.guard.shieldCoolDown == 0) {
               this.guard.shieldCoolDown = 8;
            }

            this.guard.swing(InteractionHand.MAIN_HAND);
            this.guard.doHurtTarget(enemy);
         }
      }

      protected boolean canPerformAttack(LivingEntity mob) {
         return this.isTimeToAttack() && this.mobHitBox(this.mob).inflate(0.65).intersects(this.mobHitBox(mob)) && this.mob.getSensing().hasLineOfSight(mob);
      }

      protected AABB mobHitBox(LivingEntity mob) {
         Entity entity = mob.getVehicle();
         AABB aabb;
         if (entity != null) {
            AABB aabb1 = entity.getBoundingBox();
            AABB aabb2 = mob.getBoundingBox();
            aabb = new AABB(
               Math.min(aabb2.minX, aabb1.minX),
               aabb2.minY,
               Math.min(aabb2.minZ, aabb1.minZ),
               Math.max(aabb2.maxX, aabb1.maxX),
               aabb2.maxY,
               Math.max(aabb2.maxZ, aabb1.maxZ)
            );
         } else {
            aabb = mob.getBoundingBox();
         }

         return aabb.inflate(DEFAULT_ATTACK_REACH, 0.0, DEFAULT_ATTACK_REACH);
      }
   }

   public static class GuardRunToEatGoal extends RandomStrollGoal {
      private final Guard guard;
      private int walkTimer;

      public GuardRunToEatGoal(Guard guard) {
         super(guard, 1.0);
         this.guard = guard;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.LOOK));
      }

      public boolean canUse() {
         return this.guard.getHealth() < this.guard.getMaxHealth() / 2.0F
            && Guard.isConsumable(this.guard.getOffhandItem())
            && !this.guard.isEating()
            && this.guard.getTarget() != null
            && this.getPosition() != null;
      }

      public void start() {
         super.start();
         this.guard.setTarget(null);
         if (this.walkTimer <= 0) {
            this.walkTimer = 20;
         }
      }

      public void tick() {
         this.walkTimer--;
         List<LivingEntity> list = this.guard.level().getEntitiesOfClass(LivingEntity.class, this.guard.getBoundingBox().inflate(5.0, 3.0, 5.0));
         if (!list.isEmpty()) {
            for (LivingEntity mob : list) {
               if (mob != null
                  && (mob.getLastHurtMob() instanceof Guard || mob instanceof Mob && ((Mob)mob).getTarget() instanceof Guard)
                  && this.walkTimer < 20) {
                  this.walkTimer += 5;
               }
            }
         }
      }

      protected Vec3 getPosition() {
         List<LivingEntity> list = this.guard.level().getEntitiesOfClass(LivingEntity.class, this.guard.getBoundingBox().inflate(5.0, 3.0, 5.0));
         if (!list.isEmpty()) {
            for (LivingEntity mob : list) {
               if (mob != null && (mob.getLastHurtMob() instanceof Guard || mob instanceof Mob && ((Mob)mob).getTarget() instanceof Guard)) {
                  return DefaultRandomPos.getPosAway(this.guard, 16, 7, mob.position());
               }
            }
         }

         return super.getPosition();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.walkTimer > 0 && !this.guard.isEating();
      }

      public void stop() {
         super.stop();
         this.guard.startUsingItem(InteractionHand.OFF_HAND);
         this.guard.getNavigation().stop();
      }
   }

   public static class HeroHurtByTargetGoal extends TargetGoal {
      private final Guard guard;
      private LivingEntity attacker;
      private int timestamp;

      public HeroHurtByTargetGoal(Guard guard) {
         super(guard, false);
         this.guard = guard;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.guard.getOwner();
         if (livingentity == null) {
            return false;
         } else {
            this.attacker = livingentity.getLastHurtByMob();
            int i = livingentity.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
         }
      }

      protected boolean canAttack(@Nullable LivingEntity potentialTarget, TargetingConditions targetPredicate) {
         return super.canAttack(potentialTarget, targetPredicate) && !(potentialTarget instanceof IronGolem) && !(potentialTarget instanceof Guard);
      }

      public void start() {
         this.mob.setTarget(this.attacker);
         LivingEntity livingentity = this.guard.getOwner();
         if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
         }

         super.start();
      }
   }

   public static class HeroHurtTargetGoal extends TargetGoal {
      private final Guard guard;
      private LivingEntity attacker;
      private int timestamp;

      public HeroHurtTargetGoal(Guard theEntityTameableIn) {
         super(theEntityTameableIn, false);
         this.guard = theEntityTameableIn;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.guard.getOwner();
         if (livingentity == null) {
            return false;
         } else {
            this.attacker = livingentity.getLastHurtMob();
            int i = livingentity.getLastHurtMobTimestamp();
            return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
         }
      }

      protected boolean canAttack(@Nullable LivingEntity potentialTarget, TargetingConditions targetPredicate) {
         return super.canAttack(potentialTarget, targetPredicate) && !(potentialTarget instanceof AbstractVillager) && !(potentialTarget instanceof Guard);
      }

      public void start() {
         this.mob.setTarget(this.attacker);
         LivingEntity livingentity = this.guard.getOwner();
         if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
         }

         super.start();
      }
   }

   public static class KickGoal extends Goal {
      public final Guard guard;

      public KickGoal(Guard guard) {
         this.guard = guard;
      }

      public boolean canUse() {
         return this.guard.getTarget() != null
            && this.guard.getTarget().distanceTo(this.guard) <= 2.5
            && this.guard.getMainHandItem().getItem().useOnRelease(this.guard.getMainHandItem())
            && !this.guard.isBlocking()
            && this.guard.kickCoolDown == 0;
      }

      public void start() {
         this.guard.setKicking(true);
         if (this.guard.kickTicks <= 0) {
            this.guard.kickTicks = 10;
         }

         this.guard.doHurtTarget(this.guard.getTarget());
      }

      public void stop() {
         this.guard.setKicking(false);
         this.guard.kickCoolDown = 50;
      }
   }

   public static class RaiseShieldGoal extends Goal {
      public final Guard guard;

      public RaiseShieldGoal(Guard guard) {
         this.guard = guard;
      }

      public boolean canUse() {
         return !CrossbowItem.isCharged(this.guard.getMainHandItem())
            && this.guard.getOffhandItem().getItem().canPerformAction(this.guard.getOffhandItem(), ItemAbilities.SHIELD_BLOCK)
            && this.raiseShield()
            && this.guard.shieldCoolDown == 0
            && !this.guard
               .getOffhandItem()
               .getItem()
               .equals(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("piglinproliferation", "buckler")));
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public void start() {
         if (this.guard.getOffhandItem().getItem().canPerformAction(this.guard.getOffhandItem(), ItemAbilities.SHIELD_BLOCK)) {
            this.guard.startUsingItem(InteractionHand.OFF_HAND);
         }
      }

      public void stop() {
         if (!(Boolean)GuardConfig.COMMON.GuardRaiseShield.get()) {
            this.guard.stopUsingItem();
         }
      }

      protected boolean raiseShield() {
         LivingEntity target = this.guard.getTarget();
         if (target != null && this.guard.shieldCoolDown == 0) {
            boolean ranged = this.guard.getMainHandItem().getItem() instanceof CrossbowItem || this.guard.getMainHandItem().getItem() instanceof BowItem;
            return this.guard.distanceTo(target) <= 4.0
               || target instanceof Creeper
               || target instanceof RangedAttackMob && target.distanceTo(this.guard) >= 5.0 && !ranged
               || target instanceof Ravager
               || (Boolean)GuardConfig.COMMON.GuardRaiseShield.get();
         } else {
            return false;
         }
      }
   }

   public static class RangedCrossbowAttackPassiveGoal<T extends Guard> extends Goal {
      public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
      private final T mob;
      private final double speedModifier;
      private final float attackRadiusSqr;
      protected double wantedX;
      protected double wantedY;
      protected double wantedZ;
      private Guard.RangedCrossbowAttackPassiveGoal.CrossbowState crossbowState = Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.UNCHARGED;
      private int seeTime;
      private int attackDelay;
      private int updatePathDelay;

      public RangedCrossbowAttackPassiveGoal(T pMob, double pSpeedModifier, float pAttackRadius) {
         this.mob = pMob;
         this.speedModifier = pSpeedModifier;
         this.attackRadiusSqr = pAttackRadius * pAttackRadius;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         return this.isValidTarget() && this.isHoldingCrossbow();
      }

      private boolean isHoldingCrossbow() {
         return this.mob.isHolding(is -> is.getItem() instanceof CrossbowItem);
      }

      public boolean canContinueToUse() {
         return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
      }

      private boolean isValidTarget() {
         return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
      }

      public void stop() {
         super.stop();
         this.mob.setAggressive(false);
         this.mob.setTarget(null);
         this.seeTime = 0;
         if (this.mob.isUsingItem()) {
            this.mob.stopUsingItem();
            this.mob.setChargingCrossbow(false);
         }

         this.mob.setPose(Pose.STANDING);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void start() {
         this.mob.setAggressive(true);
      }

      public void tick() {
         LivingEntity livingentity = this.mob.getTarget();
         if (livingentity != null) {
            boolean canSee = this.mob.getSensing().hasLineOfSight(livingentity);
            boolean hasSeenEntityRecently = this.seeTime > 0;
            if (canSee != hasSeenEntityRecently) {
               this.seeTime = 0;
            }

            if (canSee) {
               this.seeTime++;
            } else {
               this.seeTime--;
            }

            double d0 = this.mob.distanceToSqr(livingentity);
            double d1 = livingentity.distanceTo(this.mob);
            if (d1 <= 4.0 && !this.mob.isPatrolling()) {
               this.mob.getMoveControl().strafe(this.mob.isUsingItem() ? -0.5F : -3.0F, 0.0F);
               this.mob.lookAt(livingentity, 30.0F, 30.0F);
            }

            if (this.mob.getRandom().nextInt(50) == 0) {
               if (this.mob.hasPose(Pose.STANDING)) {
                  this.mob.setPose(Pose.CROUCHING);
               } else {
                  this.mob.setPose(Pose.STANDING);
               }
            }

            boolean canSee2 = (d0 > this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
            if (canSee2) {
               this.updatePathDelay--;
               if (this.updatePathDelay <= 0 && !this.mob.isPatrolling()) {
                  this.mob.getNavigation().moveTo(livingentity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5);
                  this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
               }
            } else {
               this.updatePathDelay = 0;
               this.mob.getNavigation().stop();
            }

            this.mob.lookAt(livingentity, 30.0F, 30.0F);
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            if (this.crossbowState == Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.FIND_NEW_POSITION) {
               this.mob.stopUsingItem();
               this.mob.setChargingCrossbow(false);
               if (this.findPosition()) {
                  this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.mob.isCrouching() ? 0.5 : 0.9);
               }

               this.crossbowState = Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.UNCHARGED;
            } else if (this.crossbowState == Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.UNCHARGED) {
               if (!canSee2 && !this.mob.isPatrolling() || this.mob.isPatrolling() && canSee && !friendlyInLineOfSight(this.mob)) {
                  this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                  this.crossbowState = Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.CHARGING;
                  this.mob.setChargingCrossbow(true);
               }
            } else if (this.crossbowState == Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.CHARGING) {
               if (!this.mob.isUsingItem()) {
                  this.crossbowState = Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.UNCHARGED;
               }

               int i = this.mob.getTicksUsingItem();
               ItemStack itemstack = this.mob.getUseItem();
               if (i >= CrossbowItem.getChargeDuration(itemstack, this.mob) || CrossbowItem.isCharged(itemstack)) {
                  this.mob.releaseUsingItem();
                  this.crossbowState = Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.CHARGED;
                  this.attackDelay = 10;
                  this.mob.setChargingCrossbow(false);
               }
            } else if (this.crossbowState == Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.CHARGED) {
               this.attackDelay--;
               if (this.attackDelay == 0) {
                  this.crossbowState = Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.READY_TO_ATTACK;
               }
            } else if (this.crossbowState == Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.READY_TO_ATTACK && canSee) {
               if (friendlyInLineOfSight(this.mob) && !this.mob.isPatrolling()) {
                  this.crossbowState = Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.FIND_NEW_POSITION;
               } else {
                  this.mob.performRangedAttack(livingentity, 1.0F);
                  this.crossbowState = Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.UNCHARGED;
               }
            }
         }
      }

      public static boolean friendlyInLineOfSight(Mob mob) {
         Vec3 lookAngle = mob.getViewVector(1.0F);
         AABB aabb = mob.getBoundingBox().expandTowards(lookAngle.scale(6.0)).inflate(1.0, 1.0, 1.0);

         for (Entity guard : mob.level().getEntities(mob, aabb)) {
            if (guard != mob.getTarget()) {
               boolean isVillager = ((Guard)mob).getOwner() == guard
                  || guard.getType() == EntityType.VILLAGER
                  || guard.getType() == GuardEntityType.GUARD.get()
                  || guard.getType() == EntityType.IRON_GOLEM;
               if (isVillager) {
                  Vec3 vector3d = mob.getLookAngle();
                  Vec3 vector3d1 = guard.position().vectorTo(mob.position()).normalize();
                  vector3d1 = new Vec3(vector3d1.x, vector3d1.y, vector3d1.z);
                  if (vector3d1.dot(vector3d) < (Double)GuardConfig.COMMON.friendlyFireCheckValue.get() && mob.hasLineOfSight(guard)) {
                     return (Boolean)GuardConfig.COMMON.FriendlyFire.get();
                  }
               }
            }
         }

         return false;
      }

      public boolean findPosition() {
         Vec3 vector3d = this.getPosition();
         if (vector3d == null) {
            return false;
         } else {
            this.wantedX = vector3d.x;
            this.wantedY = vector3d.y;
            this.wantedZ = vector3d.z;
            return true;
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         return DefaultRandomPos.getPos(this.mob, 16, 7);
      }

      private boolean canRun() {
         return this.crossbowState == Guard.RangedCrossbowAttackPassiveGoal.CrossbowState.UNCHARGED;
      }

      public static enum CrossbowState {
         UNCHARGED,
         CHARGING,
         CHARGED,
         READY_TO_ATTACK,
         FIND_NEW_POSITION;
      }
   }

   public static class WalkBackToCheckPointGoal extends Goal {
      private final Guard guard;
      private final double speed;
      private long delayTime = 0L;
      private int ticksRan = 0;
      private boolean stop = false;

      public WalkBackToCheckPointGoal(Guard guard, double speedIn) {
         this.guard = guard;
         this.speed = speedIn;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.guard.getTarget() == null
            && this.guard.getPatrolPos() != null
            && !this.guard.blockPosition().equals(this.guard.getPatrolPos())
            && !this.guard.isFollowing()
            && this.guard.isPatrolling()
            && this.guard.level().getGameTime() - this.delayTime > 200L;
      }

      public boolean canContinueToUse() {
         return this.canUse() && this.guard.getNavigation().isInProgress() && this.stop;
      }

      public void start() {
         if (this.ticksRan > 200) {
            this.ticksRan = 0;
         }

         BlockPos blockpos = this.guard.getPatrolPos();
         if (blockpos != null && !this.guard.blockPosition().equals(this.guard.getPatrolPos())) {
            Path path = this.guard.getNavigation().createPath(blockpos, 0);
            this.guard.getNavigation().moveTo(path, this.speed);
         }
      }

      public void tick() {
         if (this.guard.getNavigation().getPath() != null && !this.guard.getNavigation().getPath().canReach()) {
            this.ticksRan++;
         }

         if (this.guard.getNavigation().getPath() != null
            && !this.guard.getNavigation().getPath().canReach()
            && !this.guard.blockPosition().equals(this.guard.getPatrolPos())
            && this.ticksRan > 200) {
            this.stop = true;
         }
      }

      public void stop() {
         if (this.stop) {
            this.delayTime = this.guard.level().getGameTime();
         }

         this.guard.getNavigation().stop();
         this.stop = false;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }
   }
}
