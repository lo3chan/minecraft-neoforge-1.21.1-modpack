package net.astralya.hexalia.block.entity.custom;

import java.util.Optional;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.astralya.hexalia.recipe.CelestialInfusionRecipe;
import net.astralya.hexalia.recipe.CelestialInfusionRecipeInput;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RitualBrazierBlockEntity extends BlockEntity implements Container, Clearable, ItemInteractionHelper.SingleItemStorage {
   public static final int CHANNEL_DURATION = 120;
   private static final String TAG_ITEM = "Item";
   private static final String TAG_CHAN_LEFT = "ChanLeft";
   private static final String TAG_CHAN_TOTAL = "ChanTotal";
   private static final String TAG_PENDING_OUT = "PendingOut";
   private static final String TAG_BLOOM_A = "BloomA";
   private static final String TAG_BLOOM_B = "BloomB";
   private static final String TAG_BLOOM_C = "BloomC";
   private ItemStack item = ItemStack.EMPTY;
   private float rotation;
   private int channelTicksRemaining;
   private int channelTotalTicks;
   private ItemStack pendingOutput = ItemStack.EMPTY;
   private long bloomPosA;
   private long bloomPosB;
   private long bloomPosC;

   public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.RITUAL_BRAZIER.get(), pos, state);
   }

   public boolean isChanneling() {
      return this.channelTicksRemaining > 0;
   }

   public float getChannelProgress(float partialTick) {
      if (this.isChanneling() && this.channelTotalTicks > 0) {
         float remaining = this.channelTicksRemaining - partialTick;
         float elapsed = this.channelTotalTicks - remaining;
         return Mth.clamp(elapsed / this.channelTotalTicks, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public RitualBrazierBlockEntity.RitualResult tryStartCelestialInfusion() {
      if (this.level instanceof ServerLevel server) {
         if (this.isChanneling()) {
            return RitualBrazierBlockEntity.RitualResult.ALREADY_CHANNELING;
         } else if (this.item.isEmpty()) {
            return RitualBrazierBlockEntity.RitualResult.INVALID_ITEM;
         } else if (!SunlightCheck.hasOpenSky(server, this.worldPosition.above())) {
            return RitualBrazierBlockEntity.RitualResult.NO_SKY;
         } else if (!this.captureNearbyCelestialBlooms(3)) {
            return RitualBrazierBlockEntity.RitualResult.NO_CELESTIAL_BLOOMS;
         } else {
            Optional<RecipeHolder<CelestialInfusionRecipe>> match = server.getRecipeManager()
               .getRecipeFor((RecipeType)ModRecipeTypes.CELESTIAL_INFUSION.get(), new CelestialInfusionRecipeInput(this.item), server);
            if (match.isEmpty()) {
               this.clearCapturedBlooms();
               return RitualBrazierBlockEntity.RitualResult.INVALID_ITEM;
            } else {
               ItemStack output = ((CelestialInfusionRecipe)match.get().value()).getResultItem(server.registryAccess());
               if (output.isEmpty()) {
                  this.clearCapturedBlooms();
                  return RitualBrazierBlockEntity.RitualResult.INVALID_ITEM;
               } else {
                  this.pendingOutput = output.copy();
                  this.channelTotalTicks = 120;
                  this.channelTicksRemaining = 120;
                  this.sync(server, this.worldPosition);
                  return RitualBrazierBlockEntity.RitualResult.SUCCESS;
               }
            }
         }
      } else {
         return RitualBrazierBlockEntity.RitualResult.INVALID_ITEM;
      }
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, RitualBrazierBlockEntity brazier) {
      if (level instanceof ServerLevel server && brazier.isChanneling()) {
         if (brazier.isEmpty() || brazier.pendingOutput.isEmpty()) {
            brazier.cancelChannel(server, pos);
         } else if (!SunlightCheck.canSeeSun(server, pos.above())) {
            brazier.cancelChannel(server, pos);
         } else {
            BlockPos a = BlockPos.of(brazier.bloomPosA);
            BlockPos b = BlockPos.of(brazier.bloomPosB);
            BlockPos c = BlockPos.of(brazier.bloomPosC);
            if (brazier.isValidBloomPos(a) && brazier.isValidBloomPos(b) && brazier.isValidBloomPos(c)) {
               brazier.emitChannelParticles(server, pos, a, b, c);
               brazier.channelTicksRemaining--;
               if (brazier.channelTicksRemaining <= 0) {
                  brazier.completeChannel(server, pos);
               } else {
                  brazier.setChanged();
                  server.sendBlockUpdated(pos, state, state, 3);
               }
            } else {
               brazier.cancelChannel(server, pos);
            }
         }
      }
   }

   private boolean isValidBloomPos(BlockPos pos) {
      if (this.level == null) {
         return false;
      } else {
         BlockState state = this.level.getBlockState(pos);
         return state.is((Block)ModBlocks.CELESTIAL_BLOOM.get()) || state.is((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get());
      }
   }

   private void cancelChannel(ServerLevel level, BlockPos pos) {
      this.channelTicksRemaining = 0;
      this.channelTotalTicks = 0;
      this.pendingOutput = ItemStack.EMPTY;
      this.clearCapturedBlooms();
      level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.35F, 0.7F);
      this.sync(level, pos);
   }

   private void completeChannel(ServerLevel level, BlockPos pos) {
      this.item = this.pendingOutput.copyWithCount(1);
      this.pendingOutput = ItemStack.EMPTY;
      BlockPos a = BlockPos.of(this.bloomPosA);
      BlockPos b = BlockPos.of(this.bloomPosB);
      BlockPos c = BlockPos.of(this.bloomPosC);
      this.clearCapturedBlooms();
      this.degradeCelestialBloom(level, a);
      this.degradeCelestialBloom(level, b);
      this.degradeCelestialBloom(level, c);
      level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.45F, 1.15F);
      level.sendParticles((SimpleParticleType)ModParticleTypes.SPARKLE.get(), pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 18, 0.25, 0.2, 0.25, 0.0);
      this.channelTicksRemaining = 0;
      this.channelTotalTicks = 0;
      this.sync(level, pos);
   }

   private void emitChannelParticles(ServerLevel server, BlockPos brazierPos, BlockPos a, BlockPos b, BlockPos c) {
      double x = brazierPos.getX() + 0.5;
      double y = brazierPos.getY() + 0.85;
      double z = brazierPos.getZ() + 0.5;
      server.sendParticles((SimpleParticleType)ModParticleTypes.SPARKLE.get(), x, y, z, 2, 0.2, 0.1, 0.2, 0.0);
      this.emitBloomSparkles(server, a);
      this.emitBloomSparkles(server, b);
      this.emitBloomSparkles(server, c);
   }

   private void emitBloomSparkles(ServerLevel server, BlockPos bloomPos) {
      server.sendParticles(
         (SimpleParticleType)ModParticleTypes.SPARKLE.get(), bloomPos.getX() + 0.5, bloomPos.getY() + 0.75, bloomPos.getZ() + 0.5, 1, 0.15, 0.15, 0.15, 0.0
      );
   }

   private void degradeCelestialBloom(ServerLevel level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      BlockState next = null;
      if (state.is((Block)ModBlocks.CELESTIAL_BLOOM.get())) {
         next = ((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get()).defaultBlockState();
      } else if (state.is((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get())) {
         next = Blocks.DEAD_BUSH.defaultBlockState();
      }

      if (next != null) {
         level.setBlock(pos, next, 3);
         level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 14, 0.2, 0.25, 0.2, 0.0);
      }
   }

   private boolean captureNearbyCelestialBlooms(int radius) {
      if (this.level == null) {
         return false;
      } else {
         this.clearCapturedBlooms();

         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               if (dx != 0 || dz != 0) {
                  BlockPos check = this.worldPosition.offset(dx, 0, dz);
                  BlockState state = this.level.getBlockState(check);
                  if (state.is((Block)ModBlocks.CELESTIAL_BLOOM.get()) || state.is((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get())) {
                     if (this.bloomPosA == 0L) {
                        this.bloomPosA = check.asLong();
                     } else if (this.bloomPosB == 0L) {
                        this.bloomPosB = check.asLong();
                     } else if (this.bloomPosC == 0L) {
                        this.bloomPosC = check.asLong();
                        return true;
                     }
                  }
               }
            }
         }

         this.clearCapturedBlooms();
         return false;
      }
   }

   private void clearCapturedBlooms() {
      this.bloomPosA = 0L;
      this.bloomPosB = 0L;
      this.bloomPosC = 0L;
   }

   @Override
   public boolean addItem(ItemStack stack) {
      if (!this.isChanneling() && this.item.isEmpty() && !stack.isEmpty()) {
         this.item = stack.split(1);
         this.inventoryChanged();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public ItemStack removeItem() {
      if (!this.isChanneling() && !this.item.isEmpty()) {
         ItemStack removed = this.item.split(1);
         this.inventoryChanged();
         return removed;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public int getContainerSize() {
      return 1;
   }

   @Override
   public boolean isEmpty() {
      return this.item.isEmpty();
   }

   public ItemStack getItem(int slot) {
      return slot == 0 ? this.item : ItemStack.EMPTY;
   }

   public ItemStack removeItem(int slot, int amount) {
      if (slot == 0 && amount > 0 && !this.isChanneling()) {
         ItemStack removed = this.item.split(amount);
         if (!removed.isEmpty()) {
            this.inventoryChanged();
         }

         return removed;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack removeItemNoUpdate(int slot) {
      if (slot == 0 && !this.isChanneling()) {
         ItemStack removed = this.item;
         this.item = ItemStack.EMPTY;
         return removed;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setItem(int slot, ItemStack stack) {
      if (slot == 0) {
         this.item = stack.copyWithCount(Math.min(stack.getCount(), this.getMaxStackSize()));
         this.inventoryChanged();
      }
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      return slot == 0 && !this.isChanneling() && this.item.isEmpty() && !stack.isEmpty();
   }

   public boolean canTakeItem(Container target, int slot, ItemStack stack) {
      return slot == 0 && !this.isChanneling();
   }

   public boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   public void clearContent() {
      this.item = ItemStack.EMPTY;
      this.inventoryChanged();
   }

   public ItemStack getStoredItem() {
      return this.item;
   }

   public float getRenderingRotation() {
      if (this.level != null && this.level.isClientSide) {
         this.rotation = this.rotation + (this.isChanneling() ? 1.5F : 0.5F);
         if (this.rotation >= 360.0F) {
            this.rotation = 0.0F;
         }

         return this.rotation;
      } else {
         return this.rotation;
      }
   }

   private void sync(ServerLevel level, BlockPos pos) {
      this.setChanged();
      level.sendBlockUpdated(pos, this.getBlockState(), this.getBlockState(), 3);
   }

   private void inventoryChanged() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, provider);
      return tag;
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.item = tag.contains("Item") ? ItemStack.parseOptional(provider, tag.getCompound("Item")) : ItemStack.EMPTY;
      this.channelTicksRemaining = tag.getInt("ChanLeft");
      this.channelTotalTicks = tag.getInt("ChanTotal");
      this.pendingOutput = tag.contains("PendingOut") ? ItemStack.parseOptional(provider, tag.getCompound("PendingOut")) : ItemStack.EMPTY;
      this.bloomPosA = tag.getLong("BloomA");
      this.bloomPosB = tag.getLong("BloomB");
      this.bloomPosC = tag.getLong("BloomC");
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      if (!this.item.isEmpty()) {
         tag.put("Item", this.item.save(provider));
      }

      tag.putInt("ChanLeft", this.channelTicksRemaining);
      tag.putInt("ChanTotal", this.channelTotalTicks);
      if (!this.pendingOutput.isEmpty()) {
         tag.put("PendingOut", this.pendingOutput.save(provider));
      }

      tag.putLong("BloomA", this.bloomPosA);
      tag.putLong("BloomB", this.bloomPosB);
      tag.putLong("BloomC", this.bloomPosC);
   }

   public static enum RitualResult {
      SUCCESS,
      NO_CELESTIAL_BLOOMS,
      NO_SKY,
      INVALID_ITEM,
      ALREADY_CHANNELING;
   }
}
