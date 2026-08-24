package com.aetherteam.aether.entity.monster.dungeon.boss;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.gui.screen.ValkyrieQueenDialogueScreen;
import com.aetherteam.aether.data.resources.registries.AetherStructures;
import com.aetherteam.aether.entity.AetherBossMob;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.NpcDialogue;
import com.aetherteam.aether.entity.ai.goal.NpcDialogueGoal;
import com.aetherteam.aether.entity.monster.dungeon.AbstractValkyrie;
import com.aetherteam.aether.entity.projectile.crystal.ThunderCrystal;
import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.network.packet.clientbound.BossInfoPacket;
import com.aetherteam.aether.network.packet.clientbound.QueenDialoguePacket;
import com.aetherteam.nitrogen.entity.BossRoomTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

public class ValkyrieQueen extends AbstractValkyrie implements AetherBossMob<ValkyrieQueen>, NpcDialogue, IEntityWithComplexSpawn {
   private static final EntityDataAccessor<Boolean> DATA_IS_READY = SynchedEntityData.defineId(ValkyrieQueen.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Component> DATA_BOSS_NAME = SynchedEntityData.defineId(ValkyrieQueen.class, EntityDataSerializers.COMPONENT);
   private static final Music VALKYRIE_QUEEN_MUSIC = new Music(AetherSoundEvents.MUSIC_BOSS_VALKYRIE_QUEEN, 0, 0, true);
   public static final Map<Block, Function<BlockState, BlockState>> DUNGEON_BLOCK_CONVERSIONS = new HashMap<>(
      Map.ofEntries(
         Map.entry((Block)AetherBlocks.LOCKED_ANGELIC_STONE.get(), blockState -> ((Block)AetherBlocks.ANGELIC_STONE.get()).defaultBlockState()),
         Map.entry((Block)AetherBlocks.TRAPPED_ANGELIC_STONE.get(), blockState -> ((Block)AetherBlocks.ANGELIC_STONE.get()).defaultBlockState()),
         Map.entry((Block)AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.get(), blockState -> ((Block)AetherBlocks.LIGHT_ANGELIC_STONE.get()).defaultBlockState()),
         Map.entry((Block)AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE.get(), blockState -> ((Block)AetherBlocks.LIGHT_ANGELIC_STONE.get()).defaultBlockState()),
         Map.entry((Block)AetherBlocks.BOSS_DOORWAY_ANGELIC_STONE.get(), blockState -> Blocks.AIR.defaultBlockState()),
         Map.entry(
            (Block)AetherBlocks.TREASURE_DOORWAY_ANGELIC_STONE.get(),
            blockState -> (BlockState)((TrapDoorBlock)AetherBlocks.SKYROOT_TRAPDOOR.get())
               .defaultBlockState()
               .setValue(HorizontalDirectionalBlock.FACING, (Direction)blockState.getValue(HorizontalDirectionalBlock.FACING))
         )
      )
   );
   private final ServerBossEvent bossFight = (ServerBossEvent)new ServerBossEvent(this.getBossName(), BossBarColor.RED, BossBarOverlay.PROGRESS)
      .setPlayBossMusic(true);
   @Nullable
   private BossRoomTracker<ValkyrieQueen> dungeon;
   @Nullable
   private AABB dungeonBounds;
   @Nullable
   private Player conversingPlayer;

   public ValkyrieQueen(EntityType<? extends ValkyrieQueen> type, Level level) {
      super(type, level);
      this.setBossFight(false);
      this.xpReward = 50;
      this.setPersistenceRequired();
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      this.setBossName(BossNameGenerator.generateValkyrieName(this.getRandom()));
      if (reason == MobSpawnType.STRUCTURE) {
         StructureManager manager = level.getLevel().structureManager();
         manager.registryAccess().registry(Registries.STRUCTURE).ifPresent(registry -> {
            Structure temple = (Structure)registry.get(AetherStructures.SILVER_DUNGEON);
            if (temple != null) {
               StructureStart start = manager.getStructureAt(this.blockPosition(), temple);
               if (start != StructureStart.INVALID_START) {
                  BoundingBox box = start.getBoundingBox();
                  AABB dungeonBounds = new AABB(box.minX(), box.minY(), box.minZ(), box.maxX() + 1, box.maxY() + 1, box.maxZ() + 1);
                  this.setDungeonBounds(dungeonBounds);
               }
            }
         });
      }

      return spawnData;
   }

   @Override
   public void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new NpcDialogueGoal(this));
      this.goalSelector.addGoal(1, new ValkyrieQueen.GetUnstuckGoal(this));
      this.goalSelector.addGoal(2, new ValkyrieQueen.ThunderCrystalAttackGoal(this, 450, 28.0F));
      this.goalSelector.addGoal(3, new AbstractValkyrie.LungeGoal(this, 0.65, 0));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, livingEntity -> this.isBossFight()));
   }

   public static Builder createMobAttributes() {
      return AbstractValkyrie.createAttributes().add(Attributes.FOLLOW_RANGE, 28.0).add(Attributes.ATTACK_DAMAGE, 13.5).add(Attributes.MAX_HEALTH, 500.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_IS_READY, false);
      builder.define(DATA_BOSS_NAME, Component.literal("Valkyrie Queen"));
   }

   @Override
   public void tick() {
      super.tick();
      this.breakBlocks();
      this.evaporate();
      double motionY = this.getDeltaMovement().y();
      if (!this.onGround() && Math.abs(motionY - this.lastMotionY) > 0.07 && Math.abs(motionY - this.lastMotionY) < 0.09) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.055, 0.0));
      }
   }

   private void breakBlocks() {
      LivingEntity target = this.getTarget();
      if (!this.level().isClientSide() && target != null && EventHooks.canEntityGrief(this.level(), this)) {
         for (int i = 0; i < 2; i++) {
            Vec3i vector = i == 0 ? this.getMotionDirection().getNormal() : Vec3i.ZERO;
            BlockPos upperPosition = BlockPos.containing(this.getEyePosition()).offset(vector);
            BlockPos lowerPosition = this.blockPosition().offset(vector);
            BlockState upperState = this.level().getBlockState(upperPosition);
            BlockState lowerState = this.level().getBlockState(lowerPosition);
            if (!this.isBreakable(upperState)
               || !upperState.getShape(this.level(), upperPosition).equals(Shapes.block())
                  && upperState.getCollisionShape(this.level(), upperPosition).isEmpty()
               || this.getDungeon() != null && !this.getDungeon().roomBounds().contains(upperPosition.getCenter())) {
               if (this.isBreakable(lowerState)
                  && (
                     lowerState.getShape(this.level(), lowerPosition).equals(Shapes.block())
                        || !lowerState.getCollisionShape(this.level(), lowerPosition).isEmpty()
                  )
                  && (this.getDungeon() == null || this.getDungeon().roomBounds().contains(lowerPosition.getCenter()))) {
                  this.level().destroyBlock(lowerPosition, true, this);
                  this.swing(InteractionHand.MAIN_HAND);
               }
            } else {
               this.level().destroyBlock(upperPosition, true, this);
               this.swing(InteractionHand.MAIN_HAND);
            }
         }
      }
   }

   private boolean isBreakable(BlockState blockState) {
      return !blockState.isAir()
         && !blockState.is(AetherTags.Blocks.VALKYRIE_QUEEN_UNBREAKABLE)
         && blockState.getBlock().defaultDestroyTime() >= 0.0F
         && blockState.getBlock().defaultDestroyTime() < 100.0F;
   }

   private void evaporate() {
      Pair<BlockPos, BlockPos> minMax = this.getDefaultBounds(this);
      AetherBossMob.super.evaporate(
         this, (BlockPos)minMax.getLeft(), (BlockPos)minMax.getRight(), blockState -> !blockState.is(AetherTags.Blocks.VALKYRIE_QUEEN_UNBREAKABLE)
      );
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      this.bossFight.setProgress(this.getHealth() / this.getMaxHealth());
      this.trackDungeon();
   }

   @Override
   protected boolean teleportAroundTarget(Entity target) {
      Vec2 targetVec = new Vec2(this.getRandom().nextFloat() - 0.5F, this.getRandom().nextFloat() - 0.5F).normalized();
      double x = target.getX() + targetVec.x * 7.0F;
      double y = target.getY();
      double z = target.getZ() + targetVec.y * 7.0F;
      if (this.getDungeon() != null) {
         AABB room = this.getDungeon().roomBounds();
         x = Mth.clamp(x, room.minX + 1.0, room.maxX - 1.0);
         y = Mth.clamp(y, room.minY + 1.0, room.maxY - 1.0);
         z = Mth.clamp(z, room.minZ + 1.0, room.maxZ - 1.0);
      }

      return this.teleport(x, y, z);
   }

   protected void teleportUnstuck(Entity target) {
      this.teleport(target.getX(), target.getY(), target.getZ());
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (hand == InteractionHand.MAIN_HAND && !this.isBossFight() && !this.level().isClientSide()) {
         if (!this.isReady()) {
            this.lookAt(player, 180.0F, 180.0F);
            if (player instanceof ServerPlayer serverPlayer && this.getConversingPlayer() == null) {
               this.playSound(this.getInteractSound(), 1.0F, this.getVoicePitch());
               PacketDistributor.sendToPlayer(serverPlayer, new QueenDialoguePacket(this.getId()), new CustomPacketPayload[0]);
               this.setConversingPlayer(serverPlayer);
            }
         } else {
            this.chatWithNearby(Component.translatable("gui.aether.queen.dialog.ready"), true);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void openDialogueScreen() {
      Minecraft.getInstance().setScreen(new ValkyrieQueenDialogueScreen(this));
   }

   @Override
   public void handleNpcInteraction(Player player, byte interactionID) {
      label35:
      switch (interactionID) {
         case 0:
            this.chat(player, Component.translatable("gui.aether.queen.dialog.answer"), true);
            break;
         case 1:
            if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
               this.chat(player, Component.translatable("gui.aether.queen.dialog.peaceful"), true);
            } else if (player.getInventory().countItem((Item)AetherItems.VICTORY_MEDAL.get()) >= 10) {
               this.readyUp();
               int count = 10;

               for (ItemStack item : player.inventoryMenu.getItems()) {
                  if (item.is((Item)AetherItems.VICTORY_MEDAL.get())) {
                     if (item.getCount() > count) {
                        item.shrink(count);
                        break label35;
                     }

                     count -= item.getCount();
                     item.setCount(0);
                  }

                  if (count <= 0) {
                     break label35;
                  }
               }
            } else {
               this.chat(player, Component.translatable("gui.aether.queen.dialog.no_medals"), true);
            }
            break;
         case 2:
            this.chat(player, Component.translatable("gui.aether.queen.dialog.deny_fight"), true);
            break;
         case 3:
         default:
            this.chat(player, Component.translatable("gui.aether.queen.dialog.goodbye"), true);
      }

      this.setConversingPlayer(null);
   }

   public void readyUp() {
      MutableComponent message = Component.translatable("gui.aether.queen.dialog.begin");
      this.chatWithNearby(message, true);
      this.setReady(true);
   }

   protected void chatWithNearby(Component message, boolean sound) {
      AABB room = this.dungeon == null ? this.getBoundingBox().inflate(16.0) : this.dungeon.roomBounds();
      this.level().getNearbyPlayers(NON_COMBAT, this, room).forEach(player -> this.chat(player, message, sound));
   }

   @Override
   protected void chat(Player player, Component message, boolean sound) {
      player.sendSystemMessage(Component.literal("[").append(this.getBossName().copy().withStyle(ChatFormatting.YELLOW)).append("]: ").append(message));
      this.playSound(this.getInteractSound(), 1.0F, this.getVoicePitch());
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return super.hurt(source, amount);
      } else {
         if (this.isReady() && source.getEntity() instanceof LivingEntity attacker && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            if (this.getDungeon() != null && !this.getDungeon().isPlayerWithinRoomInterior(attacker)) {
               if (!this.level().isClientSide() && attacker instanceof Player player) {
                  this.displayTooFarMessage(player);
                  return false;
               }
            } else if (super.hurt(source, amount) && this.getHealth() > 0.0F) {
               if (!this.level().isClientSide() && !this.isBossFight()) {
                  this.chatWithNearby(Component.translatable("gui.aether.queen.dialog.fight"), false);
                  this.setHealth(this.getMaxHealth());
                  this.setBossFight(true);
                  if (this.getDungeon() != null) {
                     this.closeRoom();
                  }

                  AetherEventDispatch.onBossFightStart(this, this.getDungeon());
               }

               return true;
            }
         }

         return false;
      }
   }

   public boolean doHurtTarget(Entity entity) {
      boolean result = super.doHurtTarget(entity);
      if (entity instanceof ServerPlayer player && player.getHealth() <= 0.0F) {
         this.chat(player, Component.translatable("gui.aether.queen.dialog.playerdeath"), true);
      }

      return result;
   }

   public void reset() {
      this.setBossFight(false);
      this.setTarget(null);
      if (this.getDungeon() != null) {
         this.openRoom();
      }

      AetherEventDispatch.onBossFightStop(this, this.getDungeon());
   }

   public void die(DamageSource source) {
      if (!this.level().isClientSide) {
         this.bossFight.setProgress(this.getHealth() / this.getMaxHealth());
         this.chatWithNearby(Component.translatable("gui.aether.queen.dialog.defeated"), false);
         this.spawnExplosionParticles();
         if (this.getDungeon() != null) {
            this.getDungeon().grantAdvancements(source);
            this.tearDownRoom();
         }
      }

      super.die(source);
   }

   public void tearDownRoom() {
      for (BlockPos pos : BlockPos.betweenClosed(
         (int)this.dungeonBounds.minX,
         (int)this.dungeonBounds.minY,
         (int)this.dungeonBounds.minZ,
         (int)this.dungeonBounds.maxX,
         (int)this.dungeonBounds.maxY,
         (int)this.dungeonBounds.maxZ
      )) {
         BlockState state = this.level().getBlockState(pos);
         BlockState newState = this.convertBlock(state);
         if (newState != null) {
            this.level().setBlock(pos, newState, 3);
         }
      }
   }

   public void checkDespawn() {
   }

   @Nullable
   public BlockState convertBlock(BlockState state) {
      return DUNGEON_BLOCK_CONVERSIONS.getOrDefault(state.getBlock(), blockState -> null).apply(state);
   }

   public void startSeenByPlayer(ServerPlayer player) {
      super.startSeenByPlayer(player);
      PacketDistributor.sendToPlayer(player, new BossInfoPacket.Display(this.bossFight.getId(), this.getId()), new CustomPacketPayload[0]);
      if (this.getDungeon() == null || this.getDungeon().isPlayerTracked(player)) {
         this.bossFight.addPlayer(player);
         AetherEventDispatch.onBossFightPlayerAdd(this, this.getDungeon(), player);
      }
   }

   public void stopSeenByPlayer(ServerPlayer player) {
      super.stopSeenByPlayer(player);
      PacketDistributor.sendToPlayer(player, new BossInfoPacket.Remove(this.bossFight.getId(), this.getId()), new CustomPacketPayload[0]);
      this.bossFight.removePlayer(player);
      AetherEventDispatch.onBossFightPlayerRemove(this, this.getDungeon(), player);
   }

   public void onDungeonPlayerAdded(@Nullable Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         this.bossFight.addPlayer(serverPlayer);
         AetherEventDispatch.onBossFightPlayerAdd(this, this.getDungeon(), serverPlayer);
      }
   }

   public void onDungeonPlayerRemoved(@Nullable Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         this.bossFight.removePlayer(serverPlayer);
         AetherEventDispatch.onBossFightPlayerRemove(this, this.getDungeon(), serverPlayer);
      }
   }

   public boolean isReady() {
      return (Boolean)this.getEntityData().get(DATA_IS_READY);
   }

   public void setReady(boolean ready) {
      this.getEntityData().set(DATA_IS_READY, ready);
   }

   public Component getBossName() {
      return (Component)this.getEntityData().get(DATA_BOSS_NAME);
   }

   public void setBossName(Component component) {
      this.getEntityData().set(DATA_BOSS_NAME, component);
      this.bossFight.setName(component);
   }

   @Nullable
   public BossRoomTracker<ValkyrieQueen> getDungeon() {
      return this.dungeon;
   }

   public void setDungeon(@Nullable BossRoomTracker<ValkyrieQueen> dungeon) {
      this.dungeon = dungeon;
      if (this.dungeonBounds == null) {
         this.dungeonBounds = dungeon.roomBounds();
      }
   }

   public boolean isBossFight() {
      return this.bossFight.isVisible();
   }

   public void setBossFight(boolean isFighting) {
      this.bossFight.setVisible(isFighting);
   }

   @Nullable
   @Override
   public ResourceLocation getBossBarTexture() {
      return ResourceLocation.fromNamespaceAndPath("aether", "boss_bar/valkyrie_queen");
   }

   @Nullable
   @Override
   public ResourceLocation getBossBarBackgroundTexture() {
      return ResourceLocation.fromNamespaceAndPath("aether", "boss_bar/valkyrie_queen_background");
   }

   @Nullable
   @Override
   public Music getBossMusic() {
      return VALKYRIE_QUEEN_MUSIC;
   }

   public void setDungeonBounds(@Nullable AABB dungeonBounds) {
      this.dungeonBounds = dungeonBounds;
   }

   @Nullable
   @Override
   public Player getConversingPlayer() {
      return this.conversingPlayer;
   }

   @Override
   public void setConversingPlayer(@Nullable Player player) {
      this.conversingPlayer = player;
   }

   public int getDeathScore() {
      return this.deathScore;
   }

   public void setCustomName(@Nullable Component pName) {
      super.setCustomName(pName);
      this.setBossName(pName);
   }

   protected SoundEvent getInteractSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_VALKYRIE_QUEEN_INTERACT.get();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)AetherSoundEvents.ENTITY_VALKYRIE_QUEEN_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_VALKYRIE_QUEEN_DEATH.get();
   }

   protected boolean isAffectedByFluids() {
      return this.jumping;
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      this.addBossSaveData(tag, this.registryAccess());
      if (this.dungeonBounds != null) {
         tag.putDouble("DungeonBoundsMinX", this.dungeonBounds.minX);
         tag.putDouble("DungeonBoundsMinY", this.dungeonBounds.minY);
         tag.putDouble("DungeonBoundsMinZ", this.dungeonBounds.minZ);
         tag.putDouble("DungeonBoundsMaxX", this.dungeonBounds.maxX);
         tag.putDouble("DungeonBoundsMaxY", this.dungeonBounds.maxY);
         tag.putDouble("DungeonBoundsMaxZ", this.dungeonBounds.maxZ);
      }

      tag.putBoolean("Ready", this.isReady());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.readBossSaveData(tag, this.registryAccess());
      if (tag.contains("DungeonBoundsMinX")) {
         double minX = tag.getDouble("DungeonBoundsMinX");
         double minY = tag.getDouble("DungeonBoundsMinY");
         double minZ = tag.getDouble("DungeonBoundsMinZ");
         double maxX = tag.getDouble("DungeonBoundsMaxX");
         double maxY = tag.getDouble("DungeonBoundsMaxY");
         double maxZ = tag.getDouble("DungeonBoundsMaxZ");
         this.dungeonBounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
      }

      if (tag.contains("Ready")) {
         this.setReady(tag.getBoolean("Ready"));
      }
   }

   public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
      CompoundTag tag = new CompoundTag();
      this.addBossSaveData(tag, this.registryAccess());
      buffer.writeNbt(tag);
   }

   public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
      CompoundTag tag = additionalData.readNbt();
      if (tag != null) {
         this.readBossSaveData(tag, this.registryAccess());
      }
   }

   public static class GetUnstuckGoal extends Goal {
      private final ValkyrieQueen valkyrie;
      protected int stuckTimer;

      public GetUnstuckGoal(ValkyrieQueen valkyrie) {
         this.valkyrie = valkyrie;
      }

      public boolean canUse() {
         Entity target = this.valkyrie.getTarget();
         if (target == null) {
            return false;
         } else {
            if (target.getY() > this.valkyrie.getY()) {
               if (this.stuckTimer++ >= 75) {
                  this.stuckTimer = 0;
                  return true;
               }
            } else {
               this.stuckTimer = 0;
            }

            return false;
         }
      }

      public void start() {
         if (this.valkyrie.getTarget() != null) {
            this.valkyrie.teleportUnstuck(this.valkyrie.getTarget());
         }
      }
   }

   public static class ThunderCrystalAttackGoal extends Goal {
      private final Mob mob;
      private final int attackInterval;
      private final float attackRadius;
      private int attackTime = 0;

      public ThunderCrystalAttackGoal(Mob mob, int attackInterval, float attackRadius) {
         this.mob = mob;
         this.attackInterval = attackInterval;
         this.attackRadius = attackRadius;
      }

      public boolean canUse() {
         LivingEntity target = this.mob.getTarget();
         return target != null && target.isAlive() ? this.mob.level().getDifficulty() != Difficulty.PEACEFUL : false;
      }

      public void tick() {
         if (this.mob.getTarget() != null) {
            double distance = this.mob.distanceTo(this.mob.getTarget());
            if (distance < this.attackRadius && ++this.attackTime >= this.attackInterval) {
               ThunderCrystal thunderCrystal = new ThunderCrystal(
                  (EntityType<? extends ThunderCrystal>)AetherEntityTypes.THUNDER_CRYSTAL.get(), this.mob.level(), this.mob, this.mob.getTarget()
               );
               this.mob.level().addFreshEntity(thunderCrystal);
               this.attackTime = this.mob.getRandom().nextInt(40);
            }
         }
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }
   }
}
