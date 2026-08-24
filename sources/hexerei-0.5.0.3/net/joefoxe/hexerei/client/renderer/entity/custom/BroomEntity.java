package net.joefoxe.hexerei.client.renderer.entity.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.BroomType;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.container.BroomContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomAttachmentItem;
import net.joefoxe.hexerei.item.custom.BroomBrushItem;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.BroomTickableAttachmentItem;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.BroomActivateToServer;
import net.joefoxe.hexerei.util.message.BroomAskForSyncPacket;
import net.joefoxe.hexerei.util.message.BroomDamageBrushToServer;
import net.joefoxe.hexerei.util.message.BroomDamageMiscToServer;
import net.joefoxe.hexerei.util.message.BroomSyncFloatModeToServer;
import net.joefoxe.hexerei.util.message.BroomSyncPacket;
import net.joefoxe.hexerei.util.message.BroomSyncRotation;
import net.joefoxe.hexerei.util.message.BroomSyncRotationToServer;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BroomEntity extends Entity implements Container, MenuProvider, HasCustomInventoryScreen {
   private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> FORWARD_DIRECTION = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DAMAGE_TAKEN = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> LEFT_PADDLE = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> RIGHT_PADDLE = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ROCKING_TICKS = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<String> BROOM_TYPE = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Integer> FIRST_SLOT = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SECOND_SLOT = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> THIRD_SLOT = SynchedEntityData.defineId(BroomEntity.class, EntityDataSerializers.INT);
   public float speedMultiplier = 0.75F;
   private final float[] paddlePositions = new float[2];
   private float outOfControlTicks;
   public float deltaRotation;
   public float deltaRotationLerp;
   public float deltaRotationOld;
   public float floatingOffset;
   public float floatingOffsetOld;
   public Vec3 deltaMovementLerp;
   public Vec3 deltaMovementOld;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYRot;
   private double lerpXRot;
   public boolean leftInputDown;
   public boolean rightInputDown;
   public boolean forwardInputDown;
   public boolean backInputDown;
   public boolean jumpInputDown;
   public boolean sneakingInputDown;
   public boolean activateInputDown;
   private double waterLevel;
   private float boatGlide;
   private BroomEntity.Status status;
   private BroomEntity.Status previousStatus;
   private double lastYd;
   private boolean rocking;
   private boolean downwards;
   private float rockingIntensity;
   private float rockingAngle;
   private float prevRockingAngle;
   private final int drainTimeMax = 200;
   private int drainTime = 200;
   private final int miscDrainTimeMax = 20;
   private int miscDrainTime = 20;
   private boolean broomSync = false;
   public boolean floatMode = false;
   public boolean isItem = false;
   public boolean broomCalled = false;
   public int broomCalledDelay = 40;
   public ItemStack selfItem = null;
   public float age = 0.0F;
   public List<Entity> prevPassengers = null;
   public UUID broomUUID;
   public final ItemStackHandler itemHandler = this.createHandler();
   public NonNullList<ItemStack> items = NonNullList.withSize(30, ItemStack.EMPTY);

   public BroomEntity(Level worldIn, double x, double y, double z) {
      this((EntityType<BroomEntity>)ModEntityTypes.BROOM.get(), worldIn);
      this.setPos(x, y, z);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = x;
      this.yo = y;
      this.zo = z;
      this.speedMultiplier = this.getBroomType().speedMultiplier();
   }

   public BroomEntity(EntityType<BroomEntity> broomEntityEntityType, Level world) {
      super(broomEntityEntityType, world);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(TIME_SINCE_HIT, 0);
      builder.define(FORWARD_DIRECTION, 1);
      builder.define(DAMAGE_TAKEN, 0.0F);
      builder.define(LEFT_PADDLE, false);
      builder.define(RIGHT_PADDLE, false);
      builder.define(ROCKING_TICKS, 0);
      builder.define(FIRST_SLOT, 0);
      builder.define(SECOND_SLOT, 0);
      builder.define(THIRD_SLOT, 0);
      builder.define(BROOM_TYPE, "willow");
   }

   public boolean canCollideWith(Entity entity) {
      return func_242378_a(this, entity);
   }

   public static boolean func_242378_a(Entity p_242378_0_, Entity entity) {
      return (entity.canBeCollidedWith() || entity.isPushable()) && !p_242378_0_.isPassengerOfSameVehicle(entity);
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean isPushable() {
      return !this.broomCalled;
   }

   public Vec3 getRelativePortalPosition(Axis axis, FoundRectangle portal) {
      return PortalShape.getRelativePosition(portal, axis, this.position(), this.getDimensions(this.getPose()));
   }

   protected int getMaxPassengers() {
      return this.getModule(BroomEntity.BroomSlot.SATCHEL).is((Item)ModItems.BROOM_SEAT.get()) ? 2 : 1;
   }

   public double getPassengersRidingOffset(Entity passenger) {
      float height = 2.25F;
      if (passenger != null) {
         height = passenger.getBbHeight();
      }

      return this.floatingOffset - height;
   }

   public Vec3 getVehicleAttachmentPoint(Entity entity) {
      return super.getVehicleAttachmentPoint(entity);
   }

   public Vec3 getPassengerRidingPosition(Entity passenger) {
      return super.getPassengerRidingPosition(passenger).add(0.0, this.getPassengersRidingOffset(passenger), 0.0);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (!this.level().isClientSide && !this.isRemoved()) {
         this.setForwardDirection(-this.getForwardDirection());
         this.setTimeSinceHit(10);
         this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
         this.markHurt();
         boolean flag = source.getDirectEntity() instanceof Player player && player.getAbilities().instabuild;
         if (flag || this.getDamageTaken() > 50.0F) {
            if (!flag) {
               this.level()
                  .addFreshEntity(
                     new ItemEntity(
                        this.level(),
                        this.blockPosition().getX() + 0.5F,
                        this.blockPosition().getY() + 0.5F,
                        this.blockPosition().getZ() + 0.5F,
                        this.getPickResult()
                     )
                  );
            }

            this.remove(RemovalReason.DISCARDED);
         }

         return true;
      } else {
         return true;
      }
   }

   public static DyeColor getDyeColorNamed(BroomEntity broom) {
      if (broom.getCustomName() == null) {
         return null;
      } else if (broom.getCustomName().getString().equals("Thunderbolt VII")) {
         return DyeColor.byId(4);
      } else {
         return broom.getCustomName().getString().equals("Firebolt") ? DyeColor.byId(14) : HexereiUtil.getDyeColorNamed(broom.getCustomName().getString(), 0);
      }
   }

   @Nullable
   public ItemStack getPickedResult(HitResult target) {
      return super.getPickedResult(target);
   }

   @NotNull
   public ItemStack getPickResult() {
      return this.getPickResult(true);
   }

   public ItemStack getPickResult(boolean getNewUUID) {
      ItemStack item = this.getBroomItem().getDefaultInstance();
      CompoundTag tag = ((CustomData)item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      CompoundTag inv = this.itemHandler.serializeNBT(this.level().registryAccess());
      boolean flag = false;

      for (int i = 0; i < 30; i++) {
         if (!this.itemHandler.getStackInSlot(i).isEmpty()) {
            flag = true;
            break;
         }
      }

      if (flag) {
         tag.put("Inventory", inv);
      }

      tag.putBoolean("floatMode", this.floatMode);
      if (this.broomUUID != null && !getNewUUID) {
         tag.putUUID("broomUUID", this.broomUUID);
      } else {
         UUID newUUID = UUID.randomUUID();
         tag.putUUID("broomUUID", newUUID);
      }

      item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      Component name = this.getCustomName();
      if (name != null && !name.getString().isEmpty()) {
         item.set(DataComponents.CUSTOM_NAME, name);
      }

      return item;
   }

   public void onAboveBubbleCol(boolean downwards) {
      if (!this.level().isClientSide) {
         this.rocking = true;
         this.downwards = downwards;
         if (this.getRockingTicks() == 0) {
            this.setRockingTicks(60);
         }
      }

      this.level()
         .addParticle(ParticleTypes.SPLASH, this.getX() + this.random.nextFloat(), this.getY() + 0.7, this.getZ() + this.random.nextFloat(), 0.0, 0.0, 0.0);
      if (this.random.nextInt(20) == 0) {
         this.level()
            .playSound(
               null, this.getX(), this.getY(), this.getZ(), this.getSwimSplashSound(), this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat()
            );
      }
   }

   public void push(Entity entityIn) {
      if (entityIn instanceof BroomEntity) {
         if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
            super.push(entityIn);
         }
      } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
         super.push(entityIn);
      }
   }

   public Item getBroomItem() {
      return this.getBroomType().item();
   }

   public void animateHurt(float pYaw) {
      this.setForwardDirection(-this.getForwardDirection());
      this.setTimeSinceHit(10);
      this.setDamageTaken(this.getDamageTaken() * 11.0F);
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
      this.lerpX = x;
      this.lerpY = y;
      this.lerpZ = z;
      this.lerpYRot = yRot;
      this.lerpXRot = xRot;
      this.lerpSteps = 10;
   }

   public double lerpTargetX() {
      return this.lerpSteps > 0 ? this.lerpX : this.getX();
   }

   public double lerpTargetY() {
      return this.lerpSteps > 0 ? this.lerpY : this.getY();
   }

   public double lerpTargetZ() {
      return this.lerpSteps > 0 ? this.lerpZ : this.getZ();
   }

   public float lerpTargetXRot() {
      return this.lerpSteps > 0 ? (float)this.lerpXRot : this.getXRot();
   }

   public float lerpTargetYRot() {
      return this.lerpSteps > 0 ? (float)this.lerpYRot : this.getYRot();
   }

   public Direction getMotionDirection() {
      return this.getDirection().getClockWise();
   }

   public int getExtraBrush() {
      for (int i = 3; i < 30; i++) {
         if (this.itemHandler.getStackInSlot(i).is(HexereiTags.Items.BROOM_BRUSH)) {
            return i;
         }
      }

      return -1;
   }

   public void damageBrush() {
      this.getModule(BroomEntity.BroomSlot.BRUSH).setDamageValue(this.getModule(BroomEntity.BroomSlot.BRUSH).getDamageValue() + 1);
      if (this.getModule(BroomEntity.BroomSlot.BRUSH).getDamageValue() >= this.getModule(BroomEntity.BroomSlot.BRUSH).getMaxDamage()) {
         this.setModule(BroomEntity.BroomSlot.BRUSH, ItemStack.EMPTY);
         this.level().playSound(null, this, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.5F, this.random.nextFloat() * 0.4F + 1.0F);
         this.sync();
      }

      for (BroomEntity.BroomSlot slot : BroomEntity.BroomSlot.values()) {
         if (this.getModule(slot).getItem() instanceof BroomAttachmentItem broomAttachment) {
            broomAttachment.onBrushDamage(this, this.random);
         }
      }
   }

   public void transferBrushParticles() {
      for (int i = 0; i < 20; i++) {
         int j = this.random.nextInt(2) * 2 - 1;
         int k = this.random.nextInt(2) * 2 - 1;
         double d3 = this.random.nextFloat() * j;
         double d4 = (this.random.nextFloat() - 0.5) * 0.125;
         double d5 = this.random.nextFloat() * k;
         float rotOffset = this.random.nextFloat() * 10.0F - 5.0F;
         float rot = this.random.nextFloat() * 360.0F;
         if (this.random.nextInt(5) == 0) {
            this.level()
               .addParticle(
                  ParticleTypes.DRAGON_BREATH,
                  this.getX()
                     - Math.sin((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + this.getDeltaMovement().length() / 4.0)
                     + Math.cos(rot) * (this.random.nextDouble() * 0.05 + 0.5),
                  this.getY() + this.floatingOffset + 0.1F * this.random.nextFloat() - this.getDeltaMovement().y(),
                  this.getZ()
                     + Math.cos((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + this.getDeltaMovement().length() / 4.0)
                     + Math.sin(rot) * (this.random.nextDouble() * 0.05 + 0.5),
                  -Math.cos(rot) * (this.random.nextDouble() * 0.005 + 0.025),
                  (this.random.nextDouble() - 0.5) * 0.005,
                  -Math.sin(rot) * (this.random.nextDouble() * 0.005 + 0.025)
               );
         }

         this.level()
            .addParticle(
               ParticleTypes.PORTAL,
               this.getX()
                  - Math.sin((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                     * (1.25 + this.getDeltaMovement().length() / 4.0),
               this.getY() + this.floatingOffset + 0.1F * this.random.nextFloat() - this.getDeltaMovement().y(),
               this.getZ()
                  + Math.cos((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                     * (1.25 + this.getDeltaMovement().length() / 4.0),
               d3,
               d4,
               d5
            );
         if (this.getModule(BroomEntity.BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem && brushItem.list != null) {
            ParticleEngine pe = Minecraft.getInstance().particleEngine;
            Random random = new Random();

            for (Tuple<ParticleOptions, Integer> tuple : brushItem.list) {
               ParticleOptions option = (ParticleOptions)tuple.getA();
               int delay = (Integer)tuple.getB();
               if (random.nextInt(delay) == 0) {
                  this.level()
                     .addParticle(
                        option,
                        this.getX()
                           - Math.sin((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                              * (1.25 + this.getDeltaMovement().length() / 4.0),
                        this.getY() + this.floatingOffset + 0.25F * random.nextFloat() - this.getDeltaMovement().y(),
                        this.getZ()
                           + Math.cos((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                              * (1.25 + this.getDeltaMovement().length() / 4.0),
                        (random.nextDouble() - 0.5) * 0.05,
                        (random.nextDouble() - 0.5) * 0.05,
                        (random.nextDouble() - 0.5) * 0.05
                     );
               }
            }
         }
      }

      for (int i = 0; i < 20; i++) {
         float rotOffsetx = this.random.nextFloat() * 10.0F - 5.0F;
         float rotx = 18 * i;
         if (this.getModule(BroomEntity.BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem && brushItem.list != null) {
            ParticleEngine pe = Minecraft.getInstance().particleEngine;
            Random random = new Random();

            for (Tuple<ParticleOptions, Integer> tuplex : brushItem.list) {
               ParticleOptions option = (ParticleOptions)tuplex.getA();
               int delay = (Integer)tuplex.getB();
               if (random.nextInt(delay) == 0) {
                  this.level()
                     .addParticle(
                        option,
                        this.getX()
                           - Math.sin((this.getYRot() - 90.0F + this.deltaRotation + rotOffsetx) / 180.0F * 3.141592653589793)
                              * (1.25 + this.getDeltaMovement().length() / 4.0),
                        this.getY() + this.floatingOffset + 0.1F * random.nextFloat() - this.getDeltaMovement().y(),
                        this.getZ()
                           + Math.cos((this.getYRot() - 90.0F + this.deltaRotation + rotOffsetx) / 180.0F * 3.141592653589793)
                              * (1.25 + this.getDeltaMovement().length() / 4.0),
                        Math.cos(rotx) * (random.nextDouble() * 0.005 + 0.15),
                        (random.nextDouble() - 0.5) * 0.005,
                        Math.sin(rotx) * (random.nextDouble() * 0.005 + 0.15)
                     );
               }
            }
         }
      }
   }

   public void damageMisc() {
      this.getModule(BroomEntity.BroomSlot.MISC).setDamageValue(this.getModule(BroomEntity.BroomSlot.MISC).getDamageValue() + 1);
      if (this.getModule(BroomEntity.BroomSlot.MISC).getDamageValue() >= this.getModule(BroomEntity.BroomSlot.MISC).getMaxDamage()) {
         this.setModule(BroomEntity.BroomSlot.MISC, ItemStack.EMPTY);
         this.level().playSound(null, this, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, this.random.nextFloat() * 0.4F + 1.0F);
         this.sync();
      }
   }

   public void startOpen(Player pPlayer) {
      super.startOpen(pPlayer);
      SoundEvent sound = (SoundEvent)SoundEvents.ARMOR_EQUIP_LEATHER.value();
      float volume = 0.75F;
      if (this.isEnder()) {
         sound = SoundEvents.ENDER_CHEST_OPEN;
         volume = 0.5F;
      }

      this.level().playSound(null, this.getX(), this.getY() + 0.5, this.getZ(), sound, SoundSource.BLOCKS, volume, this.random.nextFloat() * 0.1F + 0.9F);
   }

   public void stopOpen(Player p_18954_) {
      super.stopOpen(p_18954_);
      SoundEvent sound = (SoundEvent)SoundEvents.ARMOR_EQUIP_LEATHER.value();
      float pitch = 0.4F;
      float volume = 0.75F;
      if (this.isEnder()) {
         sound = SoundEvents.ENDER_CHEST_CLOSE;
         pitch = 0.9F;
         volume = 0.5F;
      }

      this.level().playSound(null, this.getX(), this.getY() + 0.5, this.getZ(), sound, SoundSource.BLOCKS, volume, this.random.nextFloat() * 0.1F + pitch);
   }

   public void tick() {
      this.age++;
      this.floatingOffsetOld = this.floatingOffset;
      this.deltaMovementOld = this.getDeltaMovement();
      this.deltaRotationOld = this.deltaRotation;
      if (!this.level().isClientSide) {
         if (this.prevPassengers != null && this.prevPassengers.size() != this.getPassengers().size()) {
            List<Entity> dismounted = new ArrayList<>(this.prevPassengers);
            dismounted.removeAll(this.getPassengers());

            for (Entity passenger : dismounted) {
               for (BroomEntity.BroomSlot slot : BroomEntity.BroomSlot.values()) {
                  if (this.getModule(slot).getItem() instanceof BroomAttachmentItem broomAttachment) {
                     broomAttachment.onDismount(this, passenger, this.random);
                  }
               }
            }
         }

         this.prevPassengers = this.getPassengers();
      }

      if (!this.broomSync && this.level() instanceof ServerLevel) {
         this.sync();
         this.broomSync = true;
      }

      if (!this.broomSync && this.level() instanceof ClientLevel) {
         if (this.level().isClientSide) {
            HexereiPacketHandler.sendToServer(new BroomAskForSyncPacket(this.getId()));
         }

         this.broomSync = true;
      }

      this.previousStatus = this.status;
      this.status = this.getBoatStatus();
      if (this.status != BroomEntity.Status.UNDER_WATER && this.status != BroomEntity.Status.UNDER_FLOWING_WATER) {
         this.outOfControlTicks = 0.0F;
      } else {
         this.outOfControlTicks++;
         if (this.getModule(BroomEntity.BroomSlot.MISC).is((Item)ModItems.BROOM_WATERPROOF_TIP.get())) {
            this.outOfControlTicks = 0.0F;
         }
      }

      if (!this.level().isClientSide && this.outOfControlTicks >= 60.0F) {
         this.ejectPassengers();
      }

      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.getDamageTaken() > 0.0F) {
         this.setDamageTaken(this.getDamageTaken() - 1.0F);
      }

      Entity entityPassenger = this.getControllingPassenger();
      if (this.level().isClientSide()) {
         if (entityPassenger instanceof LivingEntity && entityPassenger.equals(Hexerei.proxy.getPlayer())) {
            LocalPlayer player = (LocalPlayer)Hexerei.proxy.getPlayer();
            this.setNoGravity(true);
            this.updateInputs(player.input.left, player.input.right, player.input.up, player.input.down, player.input.jumping, player.input.shiftKeyDown);
         } else if (entityPassenger == null) {
            this.setNoGravity(this.floatMode);
         }
      } else if (entityPassenger instanceof Player) {
         this.setNoGravity(true);
      } else if (entityPassenger == null) {
         this.setNoGravity(this.floatMode);
      }

      if (!this.getModule(BroomEntity.BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH)) {
         this.setNoGravity(false);
         this.floatMode = false;
      } else if (this.level().isClientSide()) {
         if (entityPassenger instanceof Player && (this.getPaddleState(0) || this.getPaddleState(1))) {
            this.drainTime--;
         }

         if (this.drainTime <= 0) {
            HexereiPacketHandler.sendToServer(new BroomDamageBrushToServer(this.getId()));
            this.drainTime = 200;
         }
      }

      for (BroomEntity.BroomSlot slotx : BroomEntity.BroomSlot.values()) {
         if (this.getModule(slotx).getItem() instanceof BroomTickableAttachmentItem toTick) {
            toTick.tick(this, this.getModule(slotx));
         }
      }

      super.tick();
      this.tickLerp();
      if (this.isEnder() && this.random.nextInt(5) == 0) {
         RandomSource pRandom = this.random;
         int j = pRandom.nextInt(2) * 2 - 1;
         int k = pRandom.nextInt(2) * 2 - 1;
         double d3 = pRandom.nextFloat() * j;
         double d4 = (pRandom.nextFloat() - 0.5) * 0.125;
         double d5 = pRandom.nextFloat() * k;
         float rotOffset = this.random.nextFloat() * 10.0F - 5.0F;
         float rot = this.random.nextFloat() * 360.0F;
         if (this.random.nextInt(5) == 0) {
            this.level()
               .addParticle(
                  ParticleTypes.DRAGON_BREATH,
                  this.getX()
                     - Math.sin((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (0.25 + this.getDeltaMovement().length() / 4.0)
                     + Math.cos(rot) * (this.random.nextDouble() * 0.05 + 0.5),
                  this.getY() + this.floatingOffset + 0.1F * this.random.nextFloat() - this.getDeltaMovement().y(),
                  this.getZ()
                     + Math.cos((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (0.25 + this.getDeltaMovement().length() / 4.0)
                     + Math.sin(rot) * (this.random.nextDouble() * 0.05 + 0.5),
                  -Math.cos(rot) * (this.random.nextDouble() * 0.005 + 0.025),
                  (this.random.nextDouble() - 0.5) * 0.005,
                  -Math.sin(rot) * (this.random.nextDouble() * 0.005 + 0.025)
               );
         }

         this.level()
            .addParticle(
               ParticleTypes.PORTAL,
               this.getX()
                  - Math.sin((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                     * (0.25 + this.getDeltaMovement().length() / 4.0),
               this.getY() + this.floatingOffset + 0.1F * this.random.nextFloat() - this.getDeltaMovement().y(),
               this.getZ()
                  + Math.cos((this.getYRot() - 90.0F + this.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                     * (0.25 + this.getDeltaMovement().length() / 4.0),
               d3,
               d4,
               d5
            );
      }

      if (this.isControlledByLocalInstance()) {
         if (this.getPassengers().isEmpty() || !(this.getPassengers().getFirst() instanceof Player)) {
            this.setPaddleState(false, false);
         }

         if (this.getModule(BroomEntity.BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH) && this.level().isClientSide) {
            this.floatingOffset = HexereiUtil.moveTo(
               this.floatingOffset, 0.05F + (float)Math.sin((this.age * 2.0F + this.getId() * 1000) / 30.0F) * 0.15F, 0.0075F
            );
         }

         this.updateMotion();
         if (this.level().isClientSide) {
            this.controlBoat();
            this.level().sendPacketToServer(new ServerboundPaddleBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
            HexereiPacketHandler.sendToServer(new BroomSyncRotationToServer(this));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         if (this.getModule(BroomEntity.BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH)
            && this.getModule(BroomEntity.BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem) {
            brushItem.renderParticles(this, this.level(), this.status, this.random);
         }
      } else {
         if (this.getPassengers().isEmpty()) {
            this.updateMotion();
            this.move(MoverType.SELF, this.getDeltaMovement());
         }

         if (this.floatMode) {
            if (this.level().isClientSide) {
               this.floatingOffset = HexereiUtil.moveTo(
                  this.floatingOffset, 0.05F + (float)Math.sin((this.age * 2.0F + this.getId() * 1000) / 30.0F) * 0.15F, 0.01F
               );
            }

            if (this.getModule(BroomEntity.BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem) {
               brushItem.renderParticles(this, this.level(), this.status, this.random);
            }
         } else {
            this.floatingOffset = HexereiUtil.moveTo(this.floatingOffset, 0.0F, 0.04F);
         }
      }

      this.updateRocking();

      for (int i = 0; i <= 1; i++) {
         if (this.getPaddleState(i)) {
            if (!this.isSilent()
               && this.paddlePositions[i] % 6.2831855F <= 0.7853981852531433
               && (this.paddlePositions[i] + 0.39269909262657166) % 6.2831854820251465 >= 0.7853981852531433) {
               SoundEvent soundevent = this.getPaddleSound();
               if (soundevent != null) {
                  Vec3 vector3d = this.getViewVector(1.0F);
                  double d0 = i == 1 ? -vector3d.z : vector3d.z;
                  double d1 = i == 1 ? vector3d.x : -vector3d.x;
                  this.level()
                     .playSound(
                        null, this.getX() + d0, this.getY(), this.getZ() + d1, soundevent, this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat()
                     );
               }
            }

            this.paddlePositions[i] = (float)(this.paddlePositions[i] + 0.39269909262657166);
         } else {
            this.paddlePositions[i] = 0.0F;
         }
      }

      if (this.broomCalledDelay > 0) {
         this.broomCalledDelay--;
         if (this.broomCalledDelay <= 0) {
            this.broomCalled = false;
         }
      }

      this.checkInsideBlocks();
      List<Entity> list = this.level()
         .getEntities(this, this.getBoundingBox().inflate(0.20000000298023224, 0.009999999776482582, 0.20000000298023224), EntitySelector.pushableBy(this));
      if (!list.isEmpty()) {
         boolean flag = !this.level().isClientSide && this.getControllingPassenger() instanceof Player;
         List<Entity> pushableList = list.stream().filter(entity -> entity instanceof Player).toList();

         for (Entity entity : list.stream()
            .filter(
               entityx -> entityx != this.getControllingPassenger()
                  && entityx.getType().is(HexereiTags.Entity.CAN_RIDE_BROOM)
                  && !(entityx instanceof Player)
                  && !entityx.isPassenger()
                  && !(
                     entityx instanceof TamableAnimal tamableAnimal
                        && (!tamableAnimal.isTame() || this.getControllingPassenger() == null || !tamableAnimal.isOwnedBy(this.getControllingPassenger()))
                  )
            )
            .toList()) {
            if (!entity.hasPassenger(this) && flag && this.getPassengers().size() == 1) {
               entity.startRiding(this, true);
            }
         }

         for (Entity entityx : pushableList) {
            if (!entityx.hasPassenger(this)) {
               this.push(entityx);
            }
         }
      }

      if (this.level().isClientSide()
         && this.getDeltaMovement().length() >= 0.01
         && this.getModule(BroomEntity.BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH)) {
         ItemStack misc = this.getModule(BroomEntity.BroomSlot.MISC);
         if (misc.getItem() instanceof BroomAttachmentItem attachmentItem && attachmentItem.shouldRenderParticles(this, this.level(), this.status)) {
            attachmentItem.renderParticles(this, this.level(), this.status, this.random);
         } else if (this.getModule(BroomEntity.BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem) {
            brushItem.renderParticles(this, this.level(), this.status, this.random);
         }
      }
   }

   private void updateRocking() {
      if (this.level().isClientSide) {
         int i = this.getRockingTicks();
         if (i > 0) {
            this.rockingIntensity += 0.05F;
         } else {
            this.rockingIntensity -= 0.1F;
         }

         this.rockingIntensity = Mth.clamp(this.rockingIntensity, 0.0F, 1.0F);
         this.prevRockingAngle = this.rockingAngle;
         this.rockingAngle = 10.0F * (float)Math.sin(0.5F * (float)this.level().getGameTime()) * this.rockingIntensity;
      } else {
         if (!this.rocking) {
            this.setRockingTicks(0);
         }

         int k = this.getRockingTicks();
         if (k > 0) {
            this.setRockingTicks(--k);
            int j = 60 - k - 1;
            if (j > 0 && k == 0) {
               this.setRockingTicks(0);
               Vec3 vector3d = this.getDeltaMovement();
               if (this.downwards) {
                  this.setDeltaMovement(vector3d.add(0.0, -0.7, 0.0));
                  this.ejectPassengers();
               } else {
                  this.setDeltaMovement(vector3d.x, this.hasPassenger(Player.class::isInstance) ? 2.7 : 0.6, vector3d.z);
               }
            }

            this.rocking = false;
         }
      }
   }

   @javax.annotation.Nullable
   protected SoundEvent getPaddleSound() {
      return switch (this.getBoatStatus()) {
         case IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER -> SoundEvents.BOAT_PADDLE_WATER;
         case UNDER_LAVA, UNDER_FLOWING_LAVA, IN_AIR -> null;
         case ON_LAND -> SoundEvents.BOAT_PADDLE_LAND;
      };
   }

   protected void playStepSound(BlockPos pPos, BlockState pState) {
   }

   private void tickLerp() {
      if (this.isControlledByLocalInstance()) {
         this.lerpSteps = 0;
         this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
      }

      if (this.lerpSteps > 0 && this.deltaMovementLerp != null) {
         this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
         this.deltaRotation = Mth.lerp(1.0F / this.lerpSteps, this.deltaRotation, this.deltaRotationLerp);
         this.setDeltaMovement(
            new Vec3(
               Mth.lerp(1.0F / this.lerpSteps, this.getDeltaMovement().x, this.deltaMovementLerp.x),
               Mth.lerp(1.0F / this.lerpSteps, this.getDeltaMovement().y, this.deltaMovementLerp.y),
               Mth.lerp(1.0F / this.lerpSteps, this.getDeltaMovement().z, this.deltaMovementLerp.z)
            )
         );
         this.lerpSteps--;
      }
   }

   public void setPaddleState(boolean left, boolean right) {
      this.entityData.set(LEFT_PADDLE, left);
      this.entityData.set(RIGHT_PADDLE, right);
   }

   private BroomEntity.Status getBoatStatus() {
      BroomEntity.Status boatentity$status = this.getUnderwaterStatus();
      if (boatentity$status != null) {
         this.waterLevel = this.getBoundingBox().maxY;
         return boatentity$status;
      } else if (this.checkInWater()) {
         return BroomEntity.Status.IN_WATER;
      } else {
         float f = this.getBoatGlide();
         if (f > 0.0F) {
            this.boatGlide = f;
            return BroomEntity.Status.ON_LAND;
         } else {
            return BroomEntity.Status.IN_AIR;
         }
      }
   }

   public float getWaterLevelAbove() {
      AABB axisalignedbb = this.getBoundingBox();
      int i = Mth.floor(axisalignedbb.minX);
      int j = Mth.ceil(axisalignedbb.maxX);
      int k = Mth.floor(axisalignedbb.maxY);
      int l = Mth.ceil(axisalignedbb.maxY - this.lastYd);
      int i1 = Mth.floor(axisalignedbb.minZ);
      int j1 = Mth.ceil(axisalignedbb.maxZ);
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      label39:
      for (int k1 = k; k1 < l; k1++) {
         float f = 0.0F;

         for (int l1 = i; l1 < j; l1++) {
            for (int i2 = i1; i2 < j1; i2++) {
               blockpos$mutable.set(l1, k1, i2);
               FluidState fluidstate = this.level().getFluidState(blockpos$mutable);
               if (fluidstate.is(FluidTags.WATER)) {
                  f = Math.max(f, fluidstate.getHeight(this.level(), blockpos$mutable));
               }

               if (f >= 1.0F) {
                  continue label39;
               }
            }
         }

         if (f < 1.0F) {
            return blockpos$mutable.getY() + f;
         }
      }

      return l + 1;
   }

   public float getBoatGlide() {
      AABB axisalignedbb = this.getBoundingBox();
      AABB axisalignedbb1 = new AABB(
         axisalignedbb.minX, axisalignedbb.minY - 0.001, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ
      );
      int i = Mth.floor(axisalignedbb1.minX) - 1;
      int j = Mth.ceil(axisalignedbb1.maxX) + 1;
      int k = Mth.floor(axisalignedbb1.minY) - 1;
      int l = Mth.ceil(axisalignedbb1.maxY) + 1;
      int i1 = Mth.floor(axisalignedbb1.minZ) - 1;
      int j1 = Mth.ceil(axisalignedbb1.maxZ) + 1;
      VoxelShape voxelshape = Shapes.create(axisalignedbb1);
      float f = 0.0F;
      int k1 = 0;
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int l1 = i; l1 < j; l1++) {
         for (int i2 = i1; i2 < j1; i2++) {
            int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
            if (j2 != 2) {
               for (int k2 = k; k2 < l; k2++) {
                  if (j2 <= 0 || k2 != k && k2 != l - 1) {
                     blockpos$mutable.set(l1, k2, i2);
                     BlockState blockstate = this.level().getBlockState(blockpos$mutable);
                     if (!(blockstate.getBlock() instanceof WaterlilyBlock)
                        && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), blockpos$mutable).move(l1, k2, i2), voxelshape, BooleanOp.AND)) {
                        f += blockstate.getFriction(this.level(), blockpos$mutable, this);
                        k1++;
                     }
                  }
               }
            }
         }
      }

      return f / k1;
   }

   public boolean fireImmune() {
      return this.getModule(BroomEntity.BroomSlot.MISC).is((Item)ModItems.BROOM_NETHERITE_TIP.get());
   }

   private boolean checkInWater() {
      AABB axisalignedbb = this.getBoundingBox();
      int i = Mth.floor(axisalignedbb.minX);
      int j = Mth.ceil(axisalignedbb.maxX);
      int k = Mth.floor(axisalignedbb.minY);
      int l = Mth.ceil(axisalignedbb.minY + 0.001);
      int i1 = Mth.floor(axisalignedbb.minZ);
      int j1 = Mth.ceil(axisalignedbb.maxZ);
      boolean flag = false;
      this.waterLevel = 5.0E-324;
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int k1 = i; k1 < j; k1++) {
         for (int l1 = k; l1 < l; l1++) {
            for (int i2 = i1; i2 < j1; i2++) {
               blockpos$mutable.set(k1, l1, i2);
               FluidState fluidstate = this.level().getFluidState(blockpos$mutable);
               if (fluidstate.is(FluidTags.WATER)) {
                  float f = l1 + fluidstate.getHeight(this.level(), blockpos$mutable);
                  this.waterLevel = Math.max((double)f, this.waterLevel);
                  flag |= axisalignedbb.minY < f;
               }
            }
         }
      }

      return flag;
   }

   @javax.annotation.Nullable
   private BroomEntity.Status getUnderwaterStatus() {
      AABB axisalignedbb = this.getBoundingBox();
      double d0 = axisalignedbb.maxY + 0.001;
      int i = Mth.floor(axisalignedbb.minX);
      int j = Mth.ceil(axisalignedbb.maxX);
      int k = Mth.floor(axisalignedbb.maxY);
      int l = Mth.ceil(d0);
      int i1 = Mth.floor(axisalignedbb.minZ);
      int j1 = Mth.ceil(axisalignedbb.maxZ);
      boolean flag = false;
      boolean lavaFlag = false;
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int k1 = i; k1 < j; k1++) {
         for (int l1 = k; l1 < l; l1++) {
            for (int i2 = i1; i2 < j1; i2++) {
               blockpos$mutable.set(k1, l1, i2);
               FluidState fluidstate = this.level().getFluidState(blockpos$mutable);
               if (fluidstate.is(FluidTags.WATER) && d0 < blockpos$mutable.getY() + fluidstate.getHeight(this.level(), blockpos$mutable)) {
                  if (!fluidstate.isSource()) {
                     return BroomEntity.Status.UNDER_FLOWING_WATER;
                  }

                  flag = true;
               } else if (fluidstate.is(FluidTags.LAVA) && d0 < blockpos$mutable.getY() + fluidstate.getHeight(this.level(), blockpos$mutable)) {
                  if (!fluidstate.isSource()) {
                     return BroomEntity.Status.UNDER_FLOWING_LAVA;
                  }

                  lavaFlag = true;
               }
            }
         }
      }

      if (lavaFlag) {
         return BroomEntity.Status.UNDER_LAVA;
      } else {
         return flag ? BroomEntity.Status.UNDER_WATER : null;
      }
   }

   private void updateMotion() {
      double d0 = -0.03999999910593033;
      double d1 = this.isNoGravity() ? 0.0 : -0.03999999910593033;
      double d2 = 0.0;
      float momentum = 0.05F;
      this.speedMultiplier = this.getBroomType().speedMultiplier();
      if (this.getModule(BroomEntity.BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brush) {
         this.speedMultiplier = this.speedMultiplier + brush.getSpeedModifier(this);
      }

      if (this.previousStatus == BroomEntity.Status.IN_AIR && this.status != BroomEntity.Status.IN_AIR && this.status != BroomEntity.Status.ON_LAND) {
         this.lastYd = 0.0;
         this.status = BroomEntity.Status.IN_WATER;
      } else {
         Vec3 vector3d = this.getDeltaMovement();
         if (this.status == BroomEntity.Status.IN_WATER) {
            d2 = (this.waterLevel - this.getY()) / this.getBbHeight();
            momentum = 0.9F;
         } else if (this.status == BroomEntity.Status.UNDER_FLOWING_WATER || this.status == BroomEntity.Status.UNDER_WATER) {
            d2 = (this.waterLevel - this.getY()) / this.getBbHeight();
            momentum = 0.8F;
            if (this.level().isClientSide() && this.getModule(BroomEntity.BroomSlot.MISC).is((Item)ModItems.BROOM_WATERPROOF_TIP.get())) {
               this.miscDrainTime--;
               if (this.miscDrainTime <= 0) {
                  HexereiPacketHandler.sendToServer(new BroomDamageMiscToServer(this.getId()));
                  this.miscDrainTime = 20;
               }
            }
         } else if (this.status == BroomEntity.Status.UNDER_FLOWING_LAVA || this.status == BroomEntity.Status.UNDER_LAVA) {
            d2 = (this.waterLevel - this.getY()) / this.getBbHeight();
            momentum = 0.7F;
            if (this.level().isClientSide()) {
               this.miscDrainTime--;
               if (this.miscDrainTime <= 0) {
                  HexereiPacketHandler.sendToServer(new BroomDamageMiscToServer(this.getId()));
                  this.miscDrainTime = 20;
               }
            }
         } else if (this.status == BroomEntity.Status.IN_AIR) {
            momentum = 0.9F;
         } else if (this.status == BroomEntity.Status.ON_LAND) {
            momentum = this.boatGlide;
            if (this.getControllingPassenger() instanceof Player) {
               this.boatGlide /= 2.0F;
            }
         }

         this.setDeltaMovement(vector3d.x * momentum, vector3d.y + d1, vector3d.z * momentum);
         this.deltaRotation *= momentum;
         if (d2 > 0.0) {
            Vec3 vector3d1 = this.getDeltaMovement();
            this.setDeltaMovement(vector3d1.x, (vector3d1.y + d2 * 0.06153846016296973) * 0.75, vector3d1.z);
         }
      }

      if (this.isNoGravity() && Mth.abs((float)(this.getDeltaMovement().y() / (1.15F + Mth.abs((float)this.getDeltaMovement().y()) / 6.0F))) > 0.0F) {
         this.setDeltaMovement(
            this.getDeltaMovement().x(),
            Mth.abs((float)(this.getDeltaMovement().y() / (1.15F + Mth.abs((float)this.getDeltaMovement().y()) / 6.0F))) < 0.1F
               ? 0.0
               : this.getDeltaMovement().y() / (1.15F + Mth.abs((float)this.getDeltaMovement().y()) / 6.0F),
            this.getDeltaMovement().z()
         );
      }

      if (this.getDeltaMovement().y() > this.speedMultiplier / 4.0F) {
         this.setDeltaMovement(this.getDeltaMovement().x(), this.speedMultiplier / 4.0F, this.getDeltaMovement().z());
      }

      if (this.getDeltaMovement().y() < -this.speedMultiplier / 4.0F) {
         this.setDeltaMovement(this.getDeltaMovement().x(), -this.speedMultiplier / 4.0F, this.getDeltaMovement().z());
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void controlBoat() {
      Options settings = Minecraft.getInstance().options;
      boolean down = ModKeyBindings.broomDown.isDown();
      boolean up = ModKeyBindings.broomUp.isDown();
      boolean activate = ModKeyBindings.broomActivate.isDown();
      if (this.isVehicle()) {
         if (activate && !this.activateInputDown) {
            HexereiPacketHandler.sendToServer(new BroomActivateToServer(this.getId()));
         }

         float f = 0.0F;
         if (this.leftInputDown) {
            this.deltaRotation--;
         }

         if (this.rightInputDown) {
            this.deltaRotation++;
         }

         if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown) {
            f += 0.02F;
         }

         this.setYRot(this.getYRot() + this.deltaRotation);
         if (this.forwardInputDown) {
            f += 0.1F;
         }

         if (this.backInputDown) {
            f -= 0.02F;
         }

         if (up) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.1275F + 0.01F * this.speedMultiplier, 0.0));
         }

         if (down) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.1275F - 0.01F * this.speedMultiplier, 0.0));
         }

         this.setDeltaMovement(
            this.getDeltaMovement()
               .add(
                  (double)(Mth.sin(-(this.getYRot() + 90.0F) * 0.017453292F) * f) * this.speedMultiplier,
                  0.0,
                  (double)(Mth.cos((this.getYRot() + 90.0F) * 0.017453292F) * f) * this.speedMultiplier
               )
         );
         this.setPaddleState(
            this.rightInputDown && !this.leftInputDown || this.forwardInputDown, this.leftInputDown && !this.rightInputDown || this.forwardInputDown
         );
      }

      this.activateInputDown = activate;
   }

   public void activate() {
      for (BroomEntity.BroomSlot slot : BroomEntity.BroomSlot.values()) {
         if (this.getModule(slot).getItem() instanceof BroomAttachmentItem broomAttachment) {
            broomAttachment.onActivate(this, this.random);
         }
      }

      if (this.getBroomType().item() instanceof BroomItem broomItem) {
         broomItem.onActivate(this, this.random);
      }
   }

   public void positionRider(Entity passenger, MoveFunction pCallback) {
      if (this.hasPassenger(passenger)) {
         Vec3 vec3 = this.getPassengerOffset(passenger);
         pCallback.accept(passenger, this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
         passenger.setYRot(passenger.getYRot() + this.deltaRotation);
         passenger.setYHeadRot(passenger.getYHeadRot() + this.deltaRotation);
         this.applyYawToEntity(passenger);
      }
   }

   public Vec3 getPassengerOffset(Entity passenger) {
      float f = 0.0F;
      float f1 = this.floatingOffset - 0.4F;
      boolean hasSeat = this.getModule(BroomEntity.BroomSlot.SATCHEL).is((Item)ModItems.BROOM_SEAT.get());
      Vec3 offset = Vec3.ZERO;
      if (this.getBroomType().item() instanceof BroomItem broomItem) {
         offset = broomItem.getTipOffset();
      }

      if (this.getPassengers().size() > 1 || passenger instanceof Animal) {
         int i = this.getPassengers().indexOf(passenger);
         if (i != 1 && !(passenger instanceof Animal)) {
            f = 0.4F;
            Vec3 vec3 = new Vec3(f, 0.0, 0.0).zRot((float)this.getDeltaMovement().y() * 25.0F * 0.017453292F);
            f1 += (float)vec3.y + 0.1F;
         } else {
            f = -0.6F;
            if (passenger instanceof Animal) {
               f = -1.0F;
            }

            Vec3 vec3 = new Vec3(f, 0.0, 0.0).add(offset).zRot((float)this.getDeltaMovement().y() * 25.0F * 0.017453292F);
            f1 += (float)vec3.y;
         }
      } else if (hasSeat) {
         f = 0.4F;
         Vec3 vec3 = new Vec3(f, 0.0, 0.0).zRot((float)this.getDeltaMovement().y() * 25.0F * 0.017453292F);
         f1 += (float)vec3.y + 0.1F;
      }

      float offsetZ = 0.0F;
      if (passenger instanceof Animal) {
         f1 += 0.575F;
         offsetZ = 0.05F;
      }

      Vec3 vec3 = new Vec3(f, 0.0, offsetZ)
         .yRot(
            (-this.getYRot() - Math.clamp(this.deltaRotation, -13.0F + this.deltaRotation / 22.5F, 13.0F + this.deltaRotation / 22.5F) * 2.0F + 90.0F)
                  * 0.017453292F
               - 1.5707964F
         );
      return vec3.add(0.0, f1, 0.0);
   }

   protected void applyYawToEntity(Entity entityToUpdate) {
      int i = -1;
      float rotation = 0.0F;
      boolean hasSeat = this.getModule(BroomEntity.BroomSlot.SATCHEL).is((Item)ModItems.BROOM_SEAT.get());
      if (this.getPassengers().size() > 1) {
         i = this.getPassengers().indexOf(entityToUpdate);
      }

      if (i == 1) {
         rotation = 0.0F;
      } else if (hasSeat && (i == 0 || i == -1)) {
         rotation = 90.0F;
      }

      if (entityToUpdate.getType().is(HexereiTags.Entity.CAN_RIDE_BROOM)) {
         rotation = 80.0F;
      }

      if (entityToUpdate instanceof CrowEntity) {
         rotation = 60.0F;
      }

      if (entityToUpdate instanceof OwlEntity) {
         rotation = 40.0F;
      }

      if (entityToUpdate instanceof Cat) {
         rotation = 90.0F;
      }

      entityToUpdate.setYBodyRot(this.getYRot() + rotation);
      float f = Mth.wrapDegrees(entityToUpdate.getYRot() - this.getYRot() - rotation);
      float f1 = Mth.clamp(f, -105.0F, 105.0F);
      entityToUpdate.yRotO += f1 - f;
      entityToUpdate.setYRot(entityToUpdate.getYRot() + f1 - f);
      if (!(entityToUpdate instanceof Cat)) {
         entityToUpdate.setYHeadRot(entityToUpdate.getYRot());
      }

      if (entityToUpdate instanceof TamableAnimal animal) {
         if (animal instanceof Cat cat) {
            cat.setInSittingPose(false);
            cat.setOrderedToSit(false);
            cat.setLying(true);
         } else {
            animal.setInSittingPose(true);
            animal.setOrderedToSit(true);
            entityToUpdate.setYHeadRot(this.getYRot() + 90.0F);
            animal.yHeadRotO = this.getYRot() + 90.0F;
         }
      }
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
      return super.getDismountLocationForPassenger(passenger)
         .add(0.0, passenger.getBbHeight() / 2.0F, 0.0)
         .add(this.getPassengerOffset(passenger).multiply(0.8500000238418579, 0.8500000238418579, 0.8500000238418579));
   }

   public void onPassengerTurned(Entity entityToUpdate) {
      this.applyYawToEntity(entityToUpdate);
   }

   public void recreateFromPacket(ClientboundAddEntityPacket packet) {
      super.recreateFromPacket(packet);
      if (this.level().isClientSide) {
         HexereiPacketHandler.sendToServer(new BroomAskForSyncPacket(this.getId()));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putString("BroomType", this.getBroomType().name());
      compound.put("inv", this.itemHandler.serializeNBT(this.level().registryAccess()));
      compound.putBoolean("floatMode", this.floatMode);
      if (this.broomUUID != null) {
         compound.putUUID("broomUUID", this.broomUUID);
      }
   }

   public boolean save(CompoundTag compound) {
      compound.putString("BroomType", this.getBroomType().name());
      compound.put("inv", this.itemHandler.serializeNBT(this.level().registryAccess()));
      compound.putBoolean("floatMode", this.floatMode);
      if (this.broomUUID != null) {
         compound.putUUID("broomUUID", this.broomUUID);
      }

      return super.save(compound);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      if (compound.contains("Type", 8)) {
         this.setBroomType(compound.getString("Type"));
      }

      if (compound.contains("BroomType", 8)) {
         this.setBroomType(compound.getString("BroomType"));
      }

      this.itemHandler.deserializeNBT(this.level().registryAccess(), compound.getCompound("inv"));
      this.floatMode = compound.getBoolean("floatMode");
      if (compound.contains("broomUUID")) {
         this.broomUUID = compound.getUUID("broomUUID");
      }
   }

   public void load(CompoundTag compound) {
      super.load(compound);
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(30) {
         protected void onContentsChanged(int slot) {
            BroomEntity.this.sync();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch (slot) {
               case 0 -> stack.is(HexereiTags.Items.BROOM_MISC);
               case 1 -> stack.is(HexereiTags.Items.SMALL_SATCHELS)
                  || stack.is(HexereiTags.Items.MEDIUM_SATCHELS)
                  || stack.is(HexereiTags.Items.LARGE_SATCHELS);
               case 2 -> stack.is(HexereiTags.Items.BROOM_BRUSH);
               default -> true;
            };
         }

         public int getSlotLimit(int slot) {
            return slot != 0 && slot != 1 && slot != 2 ? 64 : 1;
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !this.isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
         }
      };
   }

   private MenuProvider createContainerProvider(Level worldIn, BlockPos pos, final boolean isEnder) {
      return new MenuProvider() {
         @Nullable
         public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return new BroomContainer(i, BroomEntity.this, playerInventory, playerEntity, isEnder);
         }

         public Component getDisplayName() {
            return Component.translatable("");
         }
      };
   }

   public InteractionResult interact(Player player, InteractionHand hand) {
      if (player.isSecondaryUseActive()) {
         if (!this.level().isClientSide()) {
            MenuProvider containerProvider = this.createContainerProvider(
               this.level(), this.blockPosition(), this.getModule(BroomEntity.BroomSlot.SATCHEL).is((Item)ModItems.ENDER_SATCHEL.get())
            );
            player.openMenu(containerProvider, b -> b.writeInt(this.getId()).writeBoolean(this.isEnder()));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else if (this.outOfControlTicks < 60.0F) {
         if (!this.level().isClientSide) {
            if (player.startRiding(this)) {
               for (BroomEntity.BroomSlot slot : BroomEntity.BroomSlot.values()) {
                  if (this.getModule(slot).getItem() instanceof BroomAttachmentItem broomAttachment) {
                     broomAttachment.onMount(this, player, this.random);
                  }
               }

               if (this.getModule(BroomEntity.BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH)) {
                  this.push(0.0, 0.25, 0.0);
               }

               return InteractionResult.CONSUME;
            } else {
               return InteractionResult.PASS;
            }
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean getPaddleState(int side) {
      return (Boolean)this.entityData.get(side == 0 ? LEFT_PADDLE : RIGHT_PADDLE) && this.getControllingPassenger() != null;
   }

   public void setFloatMode(boolean floatMode) {
      this.floatMode = floatMode;
      if (this.level().isClientSide) {
         HexereiPacketHandler.sendToServer(new BroomSyncFloatModeToServer(this.getId(), this.getFloatMode()));
      }

      this.sync();
   }

   public boolean getFloatMode() {
      return this.floatMode;
   }

   public void setRotation(float rotation) {
      this.setYRot(rotation);
      this.yRotO = rotation;
   }

   public void syncDeltaRotation() {
      HexereiPacketHandler.sendToNearbyClient(this.level(), this, new BroomSyncRotation(this));
   }

   public void setDamageTaken(float damageTaken) {
      this.entityData.set(DAMAGE_TAKEN, damageTaken);
   }

   public float getDamageTaken() {
      return (Float)this.entityData.get(DAMAGE_TAKEN);
   }

   public void setTimeSinceHit(int timeSinceHit) {
      this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
   }

   public int getTimeSinceHit() {
      return (Integer)this.entityData.get(TIME_SINCE_HIT);
   }

   private void setRockingTicks(int ticks) {
      this.entityData.set(ROCKING_TICKS, ticks);
   }

   private int getRockingTicks() {
      return (Integer)this.entityData.get(ROCKING_TICKS);
   }

   public float getRockingAngle(float partialTicks) {
      return Mth.lerp(partialTicks, this.prevRockingAngle, this.rockingAngle);
   }

   public void setForwardDirection(int forwardDirection) {
      this.entityData.set(FORWARD_DIRECTION, forwardDirection);
   }

   public int getForwardDirection() {
      return (Integer)this.entityData.get(FORWARD_DIRECTION);
   }

   public void setBroomType(String broomType) {
      this.entityData.set(BROOM_TYPE, BroomType.byName(broomType).name());
   }

   public BroomType getBroomType() {
      return BroomType.byName((String)this.entityData.get(BROOM_TYPE));
   }

   protected boolean canAddPassenger(Entity passenger) {
      return this.getPassengers().size() == 1 && this.getPassengers().get(0) instanceof CrowEntity && passenger instanceof Player
         ? true
         : this.getPassengers().size() < this.getMaxPassengers();
   }

   @javax.annotation.Nullable
   public LivingEntity getControllingPassenger() {
      List<Entity> list = this.getPassengers();
      return !list.isEmpty() && this.getFirstPassenger() instanceof Player ? (LivingEntity)this.getFirstPassenger() : null;
   }

   public void updateInputs(
      boolean leftInputDown, boolean rightInputDown, boolean forwardInputDown, boolean backInputDown, boolean jumpInputDown, boolean sneakingInputDown
   ) {
      this.leftInputDown = leftInputDown;
      this.rightInputDown = rightInputDown;
      this.forwardInputDown = forwardInputDown;
      this.backInputDown = backInputDown;
      this.jumpInputDown = jumpInputDown;
      this.sneakingInputDown = sneakingInputDown;
      if (!this.getModule(BroomEntity.BroomSlot.BRUSH).is(HexereiTags.Items.BROOM_BRUSH)) {
         this.leftInputDown = false;
         this.rightInputDown = false;
         this.forwardInputDown = false;
         this.backInputDown = false;
         this.jumpInputDown = false;
         this.sneakingInputDown = false;
      }
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
      return super.getAddEntityPacket(entity);
   }

   public boolean canSwim() {
      return this.status == BroomEntity.Status.UNDER_WATER || this.status == BroomEntity.Status.UNDER_FLOWING_WATER;
   }

   protected void addPassenger(Entity passenger) {
      super.addPassenger(passenger);
      if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
         this.lerpSteps = 0;
         this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, this.getYRot(), (float)this.lerpXRot);
      }
   }

   public int getContainerSize() {
      return 30;
   }

   public boolean isEmpty() {
      return false;
   }

   public ItemStack getItem(int index) {
      return index >= 0 && index < this.items.size() ? (ItemStack)this.items.get(index) : ItemStack.EMPTY;
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return index == 1 && !stack.is(HexereiTags.Items.SMALL_SATCHELS) ? false : ((ItemStack)this.items.get(index)).isEmpty();
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack itemStack = ContainerHelper.removeItem(this.items, index, count);
      if (itemStack.getCount() < 1) {
         itemStack.setCount(1);
      }

      return itemStack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      return ContainerHelper.takeItem(this.items, index);
   }

   public void setItem(int index, ItemStack stack) {
      if (index >= 0 && index < this.items.size()) {
         ItemStack itemStack = stack.copy();
         this.items.set(index, itemStack);
      }

      this.sync();
   }

   public void setChanged() {
   }

   public void sync() {
      this.setChanged();
      if (!this.level().isClientSide) {
         HexereiPacketHandler.sendToNearbyClient(this.level(), this, new BroomSyncPacket(this.getId(), this.saveWithoutId(new CompoundTag())));
      }
   }

   public boolean stillValid(Player player) {
      return this.isRemoved() ? false : !(player.distanceToSqr(this) > 64.0);
   }

   public void clearContent() {
      this.items.clear();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
      return new BroomContainer(id, this, inv, player, this.isEnder());
   }

   public boolean isEnder() {
      return this.getModule(BroomEntity.BroomSlot.SATCHEL).is((Item)ModItems.ENDER_SATCHEL.get());
   }

   public boolean isReplacer() {
      return this.getModule(BroomEntity.BroomSlot.SATCHEL).is((Item)ModItems.REPLACER_SATCHEL.get());
   }

   public void openCustomInventoryScreen(Player pPlayer) {
      if (!this.level().isClientSide && (!this.isVehicle() || this.hasPassenger(pPlayer))) {
         MenuProvider containerProvider = this.createContainerProvider(
            this.level(), this.blockPosition(), this.getModule(BroomEntity.BroomSlot.SATCHEL).is((Item)ModItems.ENDER_SATCHEL.get())
         );
         pPlayer.openMenu(containerProvider, b -> b.writeInt(this.getId()).writeBoolean(this.isEnder()));
      }
   }

   public ItemStack getModule(BroomEntity.BroomSlot slot) {
      return this.itemHandler.getStackInSlot(slot.ordinal());
   }

   public void setModule(BroomEntity.BroomSlot slot, ItemStack module) {
      this.itemHandler.setStackInSlot(slot.ordinal(), module);
   }

   public List<ItemStack> getSatchelSlots(int satchelSize) {
      List<ItemStack> content = new ArrayList<>();

      for (int i = 3; i < satchelSize; i++) {
         content.add(this.itemHandler.getStackInSlot(i));
      }

      return content;
   }

   public static enum AccelerationDirection {
      FORWARD,
      NONE,
      REVERSE,
      CHARGING;

      public static BroomEntity.AccelerationDirection fromEntity(LivingEntity entity) {
         if (entity.yya > 0.0F) {
            return FORWARD;
         } else {
            return entity.yya < 0.0F ? REVERSE : NONE;
         }
      }
   }

   public static enum BroomSlot {
      MISC,
      SATCHEL,
      BRUSH;
   }

   public static enum Status {
      IN_WATER,
      UNDER_WATER,
      UNDER_FLOWING_WATER,
      UNDER_LAVA,
      UNDER_FLOWING_LAVA,
      ON_LAND,
      IN_AIR;
   }
}
