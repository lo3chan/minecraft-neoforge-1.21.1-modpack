package net.nycto_team.overpacked.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.bobophones.bobolib.util.BU;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.nycto_team.overpacked.item.GiantBackpackItem;
import net.nycto_team.overpacked.menu.GiantBackpackMenu;
import net.nycto_team.overpacked.registry.ModItems;
import net.nycto_team.overpacked.util.Utils;

public class GiantBackpack extends Entity {
   public SimpleContainer[] inv;
   private final int[] openers = new int[3];
   public static final EntityDataAccessor<Integer> color = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<String> name = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<CompoundTag> inv_data = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.COMPOUND_TAG);
   private static final EntityDataAccessor<Integer> sleeping_bag_color = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Boolean> big_cell = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<Byte> right_cell = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.BYTE);
   public static final EntityDataAccessor<Byte> left_cell = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Integer> hurt_time = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> hurt_dir = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> damage = SynchedEntityData.defineId(GiantBackpack.class, EntityDataSerializers.FLOAT);
   public AnimationState big_cell_open_anim_state = new AnimationState();
   public AnimationState big_cell_close_anim_state = new AnimationState();
   private int big_cell_timeout = 0;
   public AnimationState right_cell_open_anim_state = new AnimationState();
   public AnimationState right_cell_close_anim_state = new AnimationState();
   private int right_cell_timeout = 0;
   public AnimationState left_cell_open_anim_state = new AnimationState();
   public AnimationState left_cell_close_anim_state = new AnimationState();
   private int left_cell_timeout = 0;

   public GiantBackpack(EntityType<?> entity_type, Level level) {
      super(entity_type, level);
      this.blocksBuilding = true;
      this.CreateInventory();
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   public double getEyeY() {
      return 0.5;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.EVENTS;
   }

   public Direction getMotionDirection() {
      return this.getDirection().getClockWise();
   }

   public ItemStack getPickResult() {
      return this.get_stack();
   }

   public boolean canCollideWith(Entity entity) {
      return true;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (!this.level().isClientSide() && !this.isRemoved()) {
         this.SetHurtDir(-this.get_hurt_dir());
         this.SetHurtTime(10);
         this.SetDamage(this.get_damage() + amount * 10.0F);
         this.markHurt();
         this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
         boolean flag = source.isCreativePlayer();
         if (flag || this.get_damage() > 40.0F) {
            ItemStack stack = this.get_stack();
            if ((!flag || stack.has(DataComponents.CUSTOM_DATA)) && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
               this.spawnAtLocation(stack);
            }

            this.discard();
         }

         return true;
      } else {
         return true;
      }
   }

   public void tick() {
      super.tick();
      this.setDeltaMovement(this.getDeltaMovement().add(new Vec3(0.0, this.isNoGravity() ? 0.0 : -0.04, 0.0)).multiply(0.5, 1.0, 0.5));
      this.move(MoverType.SELF, this.getDeltaMovement());
      if (this.get_hurt_time() > 0) {
         this.SetHurtTime(this.get_hurt_time() - 1);
      }

      if (this.get_damage() > 0.0F) {
         this.SetDamage(this.get_damage() - 1.0F);
      }

      if (!this.isRemoved()) {
         this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(e -> e.push(this));
      }

      this.checkInsideBlocks();
      if (this.level().isClientSide()) {
         this.SetupAnim();
      }
   }

   public InteractionResult interactAt(Player player, Vec3 pos, InteractionHand hand) {
      if (!this.level().isClientSide()) {
         ItemStack stack = player.getItemInHand(hand);
         if (player.isSecondaryUseActive()) {
            ItemStack sleeping_bag = this.get_sleeping_bag();
            int color = Utils.get_sleeping_bag_color(stack);
            if (!sleeping_bag.isEmpty()) {
               if (!player.getInventory().add(sleeping_bag)) {
                  player.drop(sleeping_bag, false);
               }

               this.SetSleepingBag(ItemStack.EMPTY);
               this.SetSleepingBagColor(-1);
               return InteractionResult.SUCCESS;
            }

            if (color != -1) {
               this.SetSleepingBag(player.getAbilities().instabuild ? stack.copy().split(1) : stack.split(1));
               this.SetSleepingBagColor(color);
               return InteractionResult.SUCCESS;
            }
         }

         float yaw = -Utils.rad(this.getYRot());
         double x = pos.x * Math.cos(yaw) - pos.z * Math.sin(yaw);
         boolean no_right_cell = this.get_right_cell() == 0;
         boolean no_left_cell = this.get_left_cell() == 0;
         int inv_id = x > 0.4 ? 1 : (x < -0.4 ? 2 : 0);
         int open_id = inv_id;
         if (inv_id == 1 && no_right_cell || inv_id == 2 && no_left_cell) {
            open_id = 0;
         }

         if (stack.is((Item)ModItems.backpack_pocket.get())) {
            if (no_right_cell && inv_id == 1) {
               this.SetRightCell(1);
               BU.ShrinkCreative(player, stack);
               return InteractionResult.SUCCESS;
            }

            if (no_left_cell && inv_id == 2) {
               this.SetLeftCell(1);
               BU.ShrinkCreative(player, stack);
               return InteractionResult.SUCCESS;
            }
         }

         this.OpenMenu(player, open_id);
      }

      return InteractionResult.SUCCESS;
   }

   public void animateHurt(float yaw) {
      this.SetHurtDir(-this.get_hurt_dir());
      this.SetHurtTime(10);
      this.SetDamage(this.get_damage() * 11.0F);
   }

   private void OpenMenu(Player player, int inv_id) {
      if (player instanceof ServerPlayer s_player) {
         s_player.openMenu(
            new SimpleMenuProvider(
               (id, inv, p) -> new GiantBackpackMenu(id, inv, this, inv_id),
               (Component)(this.get_name().isEmpty() ? this.getDisplayName() : Component.literal(this.get_name()))
            ),
            buf -> buf.writeInt(this.getId()).writeByte(inv_id)
         );
      }
   }

   private void CreateInventory() {
      this.inv = new SimpleContainer[3];

      for (int i = 0; i < 3; i++) {
         SimpleContainer container = this.inv[i];
         this.inv[i] = new SimpleContainer(i == 0 ? 55 : 28);
         if (container != null) {
            for (int ii = 0; ii < Math.min(container.getContainerSize(), this.inv[i].getContainerSize()); ii++) {
               ItemStack stack = container.getItem(ii);
               if (!stack.isEmpty()) {
                  this.inv[i].setItem(ii, stack.copy());
               }
            }
         }

         this.inv[i].addListener(c -> this.SyncInventory());
      }
   }

   public void SyncInventory() {
      CompoundTag tag = new CompoundTag();
      this.SaveInventory(tag);
      this.SetInvData(tag);
   }

   public boolean has_inv_changed(int i, Container new_inv) {
      return this.inv[i] != new_inv;
   }

   public int get_items_count() {
      int count = 0;
      Map<Item, Integer> map = new HashMap<>();

      for (int i = 0; i < 3; i++) {
         for (int ii = 0; ii < this.inv[i].getContainerSize(); ii++) {
            ItemStack stack = this.inv[i].getItem(ii);
            if (!stack.isEmpty()) {
               if (map.containsKey(stack.getItem())) {
                  int cur_count = map.get(stack.getItem()) + stack.getCount();
                  map.put(stack.getItem(), cur_count);
               } else {
                  map.put(stack.getItem(), stack.getCount());
               }
            }
         }
      }

      for (Entry<Item, Integer> entry : map.entrySet()) {
         count += Mth.ceil((float)entry.getValue().intValue() / entry.getKey().getDefaultMaxStackSize());
      }

      return count;
   }

   public void SetSleepingBag(ItemStack value) {
      this.inv[0].setItem(this.inv[0].getContainerSize() - 1, value);
   }

   public ItemStack get_sleeping_bag() {
      return this.inv[0].getItem(this.inv[0].getContainerSize() - 1);
   }

   public ItemStack get_stack() {
      ItemStack stack = GiantBackpackItem.get_colored_stack(this.get_color());
      int count = this.get_items_count();
      if (count > 0 || this.get_sleeping_bag_color() != -1 || this.get_right_cell() != 0 || this.get_left_cell() != 0) {
         CompoundTag tag = new CompoundTag();
         this.Save(tag);
         if (count > 0) {
            tag.putInt("Count", count);
         }

         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }

      if (!this.get_name().isEmpty()) {
         stack.set(DataComponents.CUSTOM_NAME, Component.literal(this.get_name()));
      }

      return stack;
   }

   public void Save(CompoundTag tag) {
      if (this.get_sleeping_bag_color() != -1) {
         tag.putInt("SleepingBagColor", this.get_sleeping_bag_color());
      }

      if (this.get_right_cell() != 0) {
         tag.putByte("RightCell", (byte)0);
      }

      if (this.get_left_cell() != 0) {
         tag.putByte("LeftCell", (byte)0);
      }

      if (!this.get_inv_data().isEmpty()) {
         tag.put("Items", this.get_inv_data());
      }
   }

   public void Load(CompoundTag tag) {
      if (tag.contains("SleepingBagColor")) {
         this.SetSleepingBagColor(tag.getInt("SleepingBagColor"));
      }

      if (tag.contains("RightCell")) {
         this.SetRightCell(1);
      }

      if (tag.contains("LeftCell")) {
         this.SetLeftCell(1);
      }

      this.LoadInventory(tag.getCompound("Items"));
   }

   private void SaveInventory(CompoundTag tag) {
      for (int i = 0; i < 3; i++) {
         ListTag list = new ListTag();

         for (int ii = 0; ii < this.inv[i].getContainerSize(); ii++) {
            ItemStack stack = this.inv[i].getItem(ii);
            if (!stack.isEmpty()) {
               CompoundTag slot_tag = new CompoundTag();
               slot_tag.putByte("Slot", (byte)ii);
               list.add(stack.save(this.registryAccess(), slot_tag));
            }
         }

         tag.put("Inv" + i, list);
      }
   }

   public void LoadInventory(CompoundTag tag) {
      this.CreateInventory();

      for (int i = 0; i < 3; i++) {
         if (tag.contains("Inv" + i)) {
            ListTag list = tag.getList("Inv" + i, 10);

            for (int ii = 0; ii < list.size(); ii++) {
               CompoundTag slot_tag = list.getCompound(ii);
               int slot = slot_tag.getByte("Slot") & 255;
               if (slot < this.inv[i].getContainerSize()) {
                  this.inv[i].setItem(slot, ItemStack.parse(this.registryAccess(), slot_tag).orElse(ItemStack.EMPTY));
               }
            }
         }
      }

      this.SetInvData(tag);
   }

   public void UpdateAnim(int inv_id, int viewer) {
      this.openers[inv_id] = this.openers[inv_id] + viewer;
      boolean flag = this.openers[inv_id] > 0;
      int i = flag ? 2 : 1;
      switch (inv_id) {
         case 1:
            this.SetRightCell(i);
            break;
         case 2:
            this.SetLeftCell(i);
            break;
         default:
            this.SetBigCell(flag);
      }
   }

   public void SetColor(int value) {
      this.entityData.set(color, value);
   }

   public int get_color() {
      return (Integer)this.entityData.get(color);
   }

   public void SetName(String value) {
      this.entityData.set(name, value);
   }

   public String get_name() {
      return (String)this.entityData.get(name);
   }

   public void SetInvData(CompoundTag value) {
      this.entityData.set(inv_data, value);
   }

   public CompoundTag get_inv_data() {
      return (CompoundTag)this.entityData.get(inv_data);
   }

   public void SetSleepingBagColor(int value) {
      this.entityData.set(sleeping_bag_color, value);
   }

   public int get_sleeping_bag_color() {
      return (Integer)this.entityData.get(sleeping_bag_color);
   }

   public void SetBigCell(boolean value) {
      this.entityData.set(big_cell, value);
   }

   public boolean get_big_cell() {
      return (Boolean)this.entityData.get(big_cell);
   }

   public void SetRightCell(int value) {
      this.SetRightCell((byte)value);
   }

   public void SetRightCell(byte value) {
      this.entityData.set(right_cell, value);
   }

   public byte get_right_cell() {
      return (Byte)this.entityData.get(right_cell);
   }

   public void SetLeftCell(int value) {
      this.SetLeftCell((byte)value);
   }

   public void SetLeftCell(byte value) {
      this.entityData.set(left_cell, value);
   }

   public byte get_left_cell() {
      return (Byte)this.entityData.get(left_cell);
   }

   public void SetDamage(float value) {
      this.entityData.set(damage, value);
   }

   public float get_damage() {
      return (Float)this.entityData.get(damage);
   }

   public void SetHurtTime(int value) {
      this.entityData.set(hurt_time, value);
   }

   public int get_hurt_time() {
      return (Integer)this.entityData.get(hurt_time);
   }

   public void SetHurtDir(int value) {
      this.entityData.set(hurt_dir, value);
   }

   public int get_hurt_dir() {
      return (Integer)this.entityData.get(hurt_dir);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(color, 0);
      builder.define(name, "");
      builder.define(inv_data, new CompoundTag());
      builder.define(sleeping_bag_color, -1);
      builder.define(big_cell, false);
      builder.define(right_cell, (byte)0);
      builder.define(left_cell, (byte)0);
      builder.define(hurt_time, 0);
      builder.define(hurt_dir, 1);
      builder.define(damage, 0.0F);
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Color", this.get_color());
      if (!this.get_name().isEmpty()) {
         tag.putString("Name", this.get_name());
      }

      this.Save(tag);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      if (tag.contains("Color")) {
         this.SetColor(tag.getInt("Color"));
      }

      if (tag.contains("Name")) {
         this.SetName(tag.getString("Name"));
      }

      this.Load(tag);
   }

   private void SetupAnim() {
      if (this.get_big_cell() && this.big_cell_timeout >= 5) {
         this.big_cell_open_anim_state.startIfStopped(this.tickCount);
         this.big_cell_close_anim_state.stop();
         this.big_cell_timeout = 0;
      } else if (this.big_cell_open_anim_state.isStarted() && this.big_cell_timeout >= 9) {
         this.big_cell_close_anim_state.startIfStopped(this.tickCount);
         this.big_cell_open_anim_state.stop();
         this.big_cell_timeout = 0;
      }

      if (this.get_right_cell() == 2 && this.right_cell_timeout >= 5) {
         this.right_cell_open_anim_state.startIfStopped(this.tickCount);
         this.right_cell_close_anim_state.stop();
         this.right_cell_timeout = 0;
      } else if (this.right_cell_open_anim_state.isStarted() && this.right_cell_timeout >= 9) {
         this.right_cell_close_anim_state.startIfStopped(this.tickCount);
         this.right_cell_open_anim_state.stop();
         this.right_cell_timeout = 0;
      }

      if (this.get_left_cell() == 2 && this.left_cell_timeout >= 5) {
         this.left_cell_open_anim_state.startIfStopped(this.tickCount);
         this.left_cell_close_anim_state.stop();
         this.left_cell_timeout = 0;
      } else if (this.left_cell_open_anim_state.isStarted() && this.left_cell_timeout >= 9) {
         this.left_cell_close_anim_state.startIfStopped(this.tickCount);
         this.left_cell_open_anim_state.stop();
         this.left_cell_timeout = 0;
      }

      this.big_cell_timeout++;
      this.right_cell_timeout++;
      this.left_cell_timeout++;
   }
}
