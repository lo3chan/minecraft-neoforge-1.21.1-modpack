package net.astralya.hexalia.block.entity.custom;

import java.util.List;
import net.astralya.hexalia.block.custom.SmallCauldronBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.smallcauldron.SmallCauldronContents;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronBlockEntity extends BlockEntity implements Container, Clearable, ItemInteractionHelper.ItemStorage {
   public static final int STIR_ANIM_TICKS = 20;
   private static final String TAG_STIR_ANIM_TICK = "StirAnimTick";
   private static final int SPOILED_AURA_INTERVAL_TICKS = 20;
   private final SmallCauldronContents contents = new SmallCauldronContents();
   private int stirAnimTick;
   private long clientStirStartGameTime;
   private int clientStirStartTick;
   private boolean stirAnimDirty;

   public SmallCauldronBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.SMALL_CAULDRON.get(), pos, state);
   }

   public void tick(Level level, BlockPos pos, BlockState state) {
      if (level instanceof ServerLevel server) {
         if (this.stirAnimTick > 0) {
            this.stirAnimTick--;
            this.stirAnimDirty = true;
         }

         this.contents.tickServer(server, (Boolean)state.getValue(SmallCauldronBlock.LIT));
         if (this.contents.isSpoiled()) {
            this.applySpoiledAura(server, pos);
         }

         this.syncIfNeeded();
      }
   }

   public boolean canStir(BlockState state, Player player) {
      return this.contents.canStir((Boolean)state.getValue(SmallCauldronBlock.LIT));
   }

   public boolean tryStir(BlockState state, Player player) {
      if (this.level instanceof ServerLevel server && this.contents.canStir((Boolean)state.getValue(SmallCauldronBlock.LIT))) {
         this.contents.stir(server);
         this.stirAnimTick = 20;
         this.stirAnimDirty = true;
         this.syncIfNeeded();
         return true;
      } else {
         return false;
      }
   }

   public int getStirAnimTick() {
      return this.stirAnimTick;
   }

   public float getStirProgress(float partialTick) {
      Level level = this.getLevel();
      if (level == null) {
         return 0.0F;
      } else if (level.isClientSide()) {
         if (this.clientStirStartTick <= 0) {
            return 0.0F;
         } else {
            float elapsed = (float)(level.getGameTime() - this.clientStirStartGameTime) + partialTick;
            float progress = elapsed / this.clientStirStartTick;
            return progress >= 1.0F ? 0.0F : Mth.clamp(progress, 0.0F, 1.0F);
         }
      } else if (this.stirAnimTick <= 0) {
         return 0.0F;
      } else {
         float progress = (20.0F - (this.stirAnimTick - partialTick)) / 20.0F;
         return Mth.clamp(progress, 0.0F, 1.0F);
      }
   }

   public List<ItemStack> getIngredientsForRender() {
      return this.contents.getIngredientsForRender();
   }

   public int getContainerSize() {
      return 4;
   }

   public boolean isEmpty() {
      for (int slot = 0; slot < this.getContainerSize(); slot++) {
         if (!this.contents.getIngredient(slot).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int slot) {
      return this.contents.getIngredient(slot);
   }

   public ItemStack removeItem(int slot, int amount) {
      if (slot >= 0
         && slot < this.getContainerSize()
         && amount > 0
         && this.contents.canExtractOneIngredient((Boolean)this.getBlockState().getValue(SmallCauldronBlock.LIT))) {
         ItemStack existing = this.contents.getIngredient(slot);
         if (existing.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            this.contents.setIngredient(slot, ItemStack.EMPTY);
            this.syncIfNeeded();
            return existing.copyWithCount(1);
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack removeItemNoUpdate(int slot) {
      if (slot >= 0 && slot < this.getContainerSize()) {
         ItemStack existing = this.contents.getIngredient(slot);
         this.contents.setIngredient(slot, ItemStack.EMPTY);
         return existing;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setItem(int slot, ItemStack stack) {
      if (slot >= 0 && slot < this.getContainerSize()) {
         this.contents.setIngredient(slot, stack);
         this.syncIfNeeded();
      }
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      return slot >= 0 && slot < this.getContainerSize() && this.contents.getIngredient(slot).isEmpty() && this.contents.canInsertOne(stack);
   }

   public boolean canTakeItem(Container target, int slot, ItemStack stack) {
      return slot >= 0
         && slot < this.getContainerSize()
         && this.contents.canExtractOneIngredient((Boolean)this.getBlockState().getValue(SmallCauldronBlock.LIT));
   }

   public boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   public void clearContent() {
      this.contents.resetToEmpty();
      this.contents.clearDirty();
      this.syncIfNeeded();
   }

   public float getLiquidFill01() {
      return this.contents.getLiquidFill01();
   }

   public float getVisualLiquidFill01() {
      return this.contents.getVisualLiquidFill01();
   }

   public int getVisualLiquidColor() {
      return this.contents.getVisualLiquidColor();
   }

   public boolean isSpoiled() {
      return this.contents.isSpoiled();
   }

   public boolean isCooking() {
      return this.contents.isCooking();
   }

   public boolean hasMixture() {
      return this.contents.hasMixture();
   }

   public boolean isOvercooked() {
      return this.contents.isOvercooked();
   }

   public int getMixtureBaseColor() {
      return this.contents.getMixtureBaseColor();
   }

   public boolean canExtractOneIngredient() {
      return this.contents.canExtractOneIngredient((Boolean)this.getBlockState().getValue(SmallCauldronBlock.LIT));
   }

   @Override
   public boolean canExtractItem() {
      return this.canExtractOneIngredient();
   }

   public ItemStack extractOneIngredient() {
      if (!(this.level instanceof ServerLevel)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack out = this.contents.extractOneIngredient();
         this.syncIfNeeded();
         return out;
      }
   }

   @Override
   public ItemStack removeItem() {
      return this.extractOneIngredient();
   }

   public boolean canInsertOne(ItemStack stack) {
      return this.contents.canInsertOne(stack);
   }

   @Override
   public boolean canInsertItem(ItemStack stack) {
      return this.canInsertOne(stack);
   }

   @Override
   public boolean addItem(ItemStack stack) {
      return !stack.isEmpty() && this.canInsertOne(stack) ? this.insertOneIntoCauldron(stack.split(1)) : false;
   }

   public boolean insertOneIntoCauldron(ItemStack held) {
      if (!(this.level instanceof ServerLevel)) {
         return false;
      } else {
         boolean inserted = this.contents.insertOne(held);
         this.syncIfNeeded();
         return inserted;
      }
   }

   public boolean canScoopMixtureWithRusticBottle() {
      return this.contents.canScoopMixtureWithRusticBottle();
   }

   public boolean tryScoopBottlePublic(Player player, InteractionHand hand, ItemStack held) {
      if (this.level instanceof ServerLevel server) {
         boolean var6 = this.contents.tryScoopBottle(server, this.centerX(), this.topY(), this.centerZ(), player, hand, held);
         this.syncIfNeeded();
         return var6;
      } else {
         return false;
      }
   }

   public boolean isRusticBottle(ItemStack stack) {
      return this.contents.isRusticBottle(stack);
   }

   public boolean isLotusBlossom(ItemStack stack) {
      return this.contents.isLotusBlossom(stack);
   }

   public boolean isWaterContainer(ItemStack stack) {
      return this.contents.isWaterContainer(stack);
   }

   public boolean canCleanseSpoiledWithLotus() {
      return this.contents.canCleanseSpoiledWithLotus();
   }

   public boolean tryCleanseSpoiledPublic(Player player, InteractionHand hand, ItemStack held) {
      if (!(this.level instanceof ServerLevel)) {
         return false;
      } else {
         boolean cleansed = this.contents.tryCleanseSpoiled(player, hand, held);
         this.syncIfNeeded();
         return cleansed;
      }
   }

   public boolean canUseWaterContainer(ItemStack stack) {
      return this.contents.canUseWaterContainer(stack);
   }

   public boolean tryFillWithWaterPublic(Player player, InteractionHand hand, ItemStack held) {
      if (this.level instanceof ServerLevel server) {
         boolean var6 = this.contents.tryUseWaterContainer(server, this.centerX(), this.centerY(), this.centerZ(), player, hand, held);
         this.syncIfNeeded();
         return var6;
      } else {
         return false;
      }
   }

   public void dropAll(Level level) {
      if (level != null && !level.isClientSide()) {
         this.contents.dropAll(level, this.centerX(), this.centerY(), this.centerZ());
         this.contents.clearDirty();
         this.stirAnimDirty = false;
         this.setChanged();
      }
   }

   private void applySpoiledAura(ServerLevel server, BlockPos pos) {
      if (server.getGameTime() % 20L == 0L) {
         for (Player player : server.getEntitiesOfClass(Player.class, new AABB(pos).inflate(1.0))) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, false, true, true));
         }
      }
   }

   private void syncIfNeeded() {
      if (this.stirAnimDirty || this.contents.isDirty()) {
         this.contents.clearDirty();
         this.stirAnimDirty = false;
         this.inventoryChanged();
      }
   }

   private void inventoryChanged() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   private double centerX() {
      return this.worldPosition.getX() + 0.5;
   }

   private double centerY() {
      return this.worldPosition.getY() + 0.5;
   }

   private double centerZ() {
      return this.worldPosition.getZ() + 0.5;
   }

   private double topY() {
      return this.worldPosition.getY() + 1.0;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("StirAnimTick", this.stirAnimTick);
      this.contents.save(tag, registries);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      int previousStirTick = this.stirAnimTick;
      this.stirAnimTick = Mth.clamp(tag.getInt("StirAnimTick"), 0, 20);
      this.contents.load(tag, registries);
      Level level = this.getLevel();
      if (level != null && level.isClientSide()) {
         if (this.stirAnimTick > previousStirTick) {
            this.clientStirStartGameTime = level.getGameTime();
            this.clientStirStartTick = this.stirAnimTick;
         } else if (this.stirAnimTick <= 0) {
            this.clientStirStartTick = 0;
         }
      }

      this.stirAnimDirty = false;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }
}
