package io.github.razordevs.deep_aether.mixin.entity;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.passive.Aerwhale;
import com.aetherteam.aether.item.AetherItems;
import io.github.razordevs.deep_aether.entity.AerwhaleSaddleable;
import io.github.razordevs.deep_aether.init.DAItems;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Aerwhale.class})
public abstract class AerwhaleMixin extends FlyingMob implements AerwhaleSaddleable, ContainerEntity, HasCustomInventoryScreen {
   @Unique
   private static final EntityDataAccessor<Boolean> DATA_STILL_ID = SynchedEntityData.defineId(Aerwhale.class, EntityDataSerializers.BOOLEAN);
   @Unique
   private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(Aerwhale.class, EntityDataSerializers.BOOLEAN);
   @Unique
   private NonNullList<ItemStack> deep_Aether$itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
   @Unique
   @Nullable
   private ResourceKey<LootTable> deep_Aether$lootTable;
   @Unique
   private long deep_Aether$lootTableSeed;

   @Shadow(
      remap = false
   )
   public abstract void setYRotData(float var1);

   protected AerwhaleMixin(EntityType<? extends FlyingMob> p_20806_, Level p_20807_) {
      super(p_20806_, p_20807_);
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"defineSynchedData"}
   )
   protected void defineSynchedData(Builder builder, CallbackInfo ci) {
      builder.define(DATA_SADDLE_ID, false);
      builder.define(DATA_STILL_ID, false);
   }

   @Unique
   public boolean deep_Aether$isStill() {
      return (Boolean)this.getEntityData().get(DATA_STILL_ID);
   }

   @Unique
   public void deep_Aether$setStill(boolean isStill) {
      this.getEntityData().set(DATA_STILL_ID, isStill);
   }

   @Overwrite
   public void travel(@NotNull Vec3 vector) {
      if (this.deep_Aether$isStill()) {
         this.setDeltaMovement(new Vec3(0.0, 0.0, 0.0));
      } else if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
         List<Entity> passengers = this.getPassengers();
         if (!passengers.isEmpty()) {
            Entity entity = (Entity)passengers.getFirst();
            if (entity instanceof Player player) {
               this.setYRot(player.getYRot() + 90.0F);
               this.yRotO = player.getYHeadRot();
               this.setXRot(-player.getXRot());
               this.xRotO = player.getXRot() * 0.5F;
               this.setYHeadRot(player.getYHeadRot());
               this.yBodyRotO = player.getYHeadRot();
               this.setYRotData(this.getYRot() - 90.0F);
               float yRot = Mth.wrapDegrees(this.getYRot() + 90.0F);
               yRot = Mth.approachDegrees(yRot, player.getYRot(), 1.0E-5F);
               this.setYBodyRot(yRot);
               vector = new Vec3(player.xxa, 0.0, player.zza <= 0.0F ? player.zza * 0.25F : player.zza);
               if (((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isJumping()) {
                  this.setDeltaMovement(new Vec3(0.0, 0.0, 0.0));
               } else {
                  double d0 = Math.toRadians(this.getYRot());
                  double d1 = Math.toRadians(-player.getXRot());
                  double d2 = Math.cos(d1);
                  this.setDeltaMovement(
                     0.98 * (this.getDeltaMovement().x() + 0.05 * Math.cos(d0) * d2),
                     0.98 * (this.getDeltaMovement().y() + 0.02 * Math.sin(d1)),
                     0.98 * (this.getDeltaMovement().z() + 0.05 * Math.sin(d0) * d2)
                  );
               }

               if (!this.level().isClientSide()) {
                  super.travel(vector);
               }

               double d0 = this.getX() - this.xo;
               double d1 = this.getZ() - this.zo;
               float f4 = 4.0F * Mth.sqrt((float)(d0 * d0 + d1 * d1));
               if (f4 > 1.0F) {
                  f4 = 1.0F;
               }

               this.walkAnimation.update(f4, 0.4F);
            }
         } else {
            super.travel(vector);
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      cancellable = true,
      method = {"mobInteract"}
   )
   protected void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (this.isSaddleable() && itemStack.is((Item)AetherItems.NATURE_STAFF.get())) {
         itemStack.hurtAndBreak(1, player, Objects.requireNonNull(itemStack.getEquipmentSlot()));
         this.deep_Aether$setStill(!this.deep_Aether$isStill());

         for (int i = 0; i < 20; i++) {
            EntityUtil.spawnMovementExplosionParticles(this);
         }

         cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide()));
      } else if (this.getPassengers().size() > 1) {
         cir.setReturnValue(this.chestInteract(player));
      } else if (player.isSecondaryUseActive()) {
         cir.setReturnValue(this.chestInteract(player));
      } else if (this.isSaddled()) {
         player.startRiding(this);
         cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide()));
      }
   }

   @Unique
   private InteractionResult chestInteract(Player player) {
      InteractionResult interactionresult = this.interactWithContainerVehicle(player);
      if (interactionresult.consumesAction()) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, player);
         PiglinAi.angerNearbyPiglins(player, true);
      }

      return interactionresult;
   }

   protected boolean canAddPassenger(Entity entity) {
      return this.getPassengers().size() < 2;
   }

   protected void positionRider(Entity entity, MoveFunction moveFunction) {
      int i = this.getPassengers().indexOf(entity);
      if (i >= 0) {
         boolean flag = i == 0;
         float f = 0.7F;
         float f1 = (float)(this.isRemoved() ? 0.009999999776482582 : this.getBbHeight() * 0.75);
         float f2 = 0.0F;
         if (this.level().isClientSide && (Boolean)AetherConfig.CLIENT.legacy_models.get()) {
            f1++;
            f--;
            f2 = 0.1F;
         }

         if (this.getPassengers().size() > 1 && !flag) {
            f = -1.2F;
         }

         if (i == 0) {
            f1 += 0.3F;
            f -= 0.1F;
         } else {
            f = -2.1F;
         }

         Vec3 vec3 = new Vec3(0.0, 0.0, f).yRot(-this.yBodyRot * 0.017453292F);
         moveFunction.accept(entity, this.getX() + vec3.x, this.getY() + f1, this.getZ() + vec3.z + f2);
         this.deep_Aether$clampRotation(entity);
      }
   }

   @Unique
   private void deep_Aether$clampRotation(Entity p_252070_) {
      p_252070_.setYBodyRot(this.getYRot());
      float f = p_252070_.getYRot();
      float f1 = Mth.wrapDegrees(f - this.getYRot());
      float f2 = Mth.clamp(f1, -160.0F, 160.0F);
      p_252070_.yRotO += f2 - f1;
      float f3 = f + f2 - f1;
      p_252070_.setYRot(f3);
      p_252070_.setYHeadRot(f3);
   }

   @Override
   public boolean isSaddleable() {
      return this.isAlive();
   }

   @Override
   public void equipSaddle(@Nullable SoundSource source) {
      this.deep_Aether$setSaddled(true);
   }

   @Override
   public boolean isSaddled() {
      return (Boolean)this.getEntityData().get(DATA_SADDLE_ID);
   }

   @Unique
   public void deep_Aether$setSaddled(boolean isSaddled) {
      this.getEntityData().set(DATA_SADDLE_ID, isSaddled);
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      this.addChestVehicleSaveData(tag, this.registryAccess());
      tag.putBoolean("isSaddled", this.isSaddled());
      tag.putBoolean("isStill", this.deep_Aether$isStill());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.readChestVehicleSaveData(tag, this.level().registryAccess());
      if (tag.contains("isSaddled")) {
         this.deep_Aether$setSaddled(tag.getBoolean("isSaddled"));
      }

      if (tag.contains("isStill")) {
         this.deep_Aether$setStill(tag.getBoolean("isStill"));
      }
   }

   public void die(@NotNull DamageSource damageSource) {
      super.die(damageSource);
      this.chestVehicleDestroyed(damageSource, this.level(), this);
   }

   public void remove(@NotNull RemovalReason reason) {
      if (!this.level().isClientSide && reason.shouldDestroy()) {
         Containers.dropContents(this.level(), this, this);
      }

      super.remove(reason);
   }

   public void openCustomInventoryScreen(Player player) {
      player.openMenu(this);
      if (!player.level().isClientSide) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, player);
         PiglinAi.angerNearbyPiglins(player, true);
      }
   }

   public NonNullList<ItemStack> getItemStacks() {
      return this.deep_Aether$itemStacks;
   }

   public void clearItemStacks() {
      this.deep_Aether$itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
   }

   public int getContainerSize() {
      return 27;
   }

   @NotNull
   public ItemStack getItem(int a) {
      return this.getChestVehicleItem(a);
   }

   @NotNull
   public ItemStack removeItem(int a, int b) {
      return this.removeChestVehicleItem(a, b);
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int a) {
      return this.removeChestVehicleItemNoUpdate(a);
   }

   public void setItem(int a, @NotNull ItemStack b) {
      this.setChestVehicleItem(a, b);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player player) {
      return this.isChestVehicleStillValid(player);
   }

   public void clearContent() {
      this.clearChestVehicleContent();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int a, Inventory inventory, Player player) {
      if (this.deep_Aether$lootTable != null && player.isSpectator()) {
         return null;
      } else {
         this.deep_Aether$unpackLootTable(inventory.player);
         return ChestMenu.threeRows(a, inventory, this);
      }
   }

   @Unique
   public void deep_Aether$unpackLootTable(@Nullable Player player) {
      this.unpackChestVehicleLootTable(player);
   }

   @NotNull
   public SlotAccess getSlot(int a) {
      return this.getChestVehicleSlot(a);
   }

   public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
      this.deep_Aether$lootTable = lootTable;
   }

   public long getLootTableSeed() {
      return this.deep_Aether$lootTableSeed;
   }

   public void setLootTableSeed(long seed) {
      this.deep_Aether$lootTableSeed = seed;
   }

   public ResourceKey<LootTable> getLootTable() {
      return this.deep_Aether$lootTable;
   }

   protected void dropFromLootTable(DamageSource p_21021_, boolean p_21022_) {
      ResourceKey<LootTable> resourcekey = this.getLootTable();
      if (resourcekey != null) {
         LootTable loottable = this.level().getServer().reloadableRegistries().getLootTable(resourcekey);
         net.minecraft.world.level.storage.loot.LootParams.Builder lootparams$builder = new net.minecraft.world.level.storage.loot.LootParams.Builder(
               (ServerLevel)this.level()
            )
            .withParameter(LootContextParams.THIS_ENTITY, this)
            .withParameter(LootContextParams.ORIGIN, this.position())
            .withParameter(LootContextParams.DAMAGE_SOURCE, p_21021_)
            .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, p_21021_.getEntity())
            .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, p_21021_.getDirectEntity());
         if (p_21022_ && this.lastHurtByPlayer != null) {
            lootparams$builder = lootparams$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer)
               .withLuck(this.lastHurtByPlayer.getLuck());
         }

         LootParams lootparams = lootparams$builder.create(LootContextParamSets.ENTITY);
         loottable.getRandomItems(lootparams, this.getLootTableSeed(), this::spawnAtLocation);
      }
   }

   protected void dropEquipment() {
      if (this.isSaddled()) {
         this.spawnAtLocation((ItemLike)DAItems.AERWHALE_SADDLE.get());
         this.deep_Aether$setSaddled(false);
      }
   }

   public void stopOpen(Player player) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), Context.of(player));
   }
}
