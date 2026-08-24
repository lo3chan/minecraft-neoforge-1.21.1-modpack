package net.mehvahdjukaar.amendments.common.tile;

import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnhancedSkullBlockTile extends BlockEntity {
   @Nullable
   protected SkullBlockEntity innerTile = null;

   public EnhancedSkullBlockTile(BlockEntityType type, BlockPos pWorldPosition, BlockState pBlockState) {
      super(type, pWorldPosition, pBlockState);
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.saveInnerTile("Skull", this.innerTile, tag, registries);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.innerTile = this.loadInnerTile("Skull", this.innerTile, tag, registries);
   }

   protected void saveInnerTile(String tagName, @Nullable SkullBlockEntity tile, CompoundTag tag, Provider registries) {
      if (tile != null) {
         tag.put(tagName + "State", NbtUtils.writeBlockState(tile.getBlockState()));
         tag.put(tagName, tile.saveWithFullMetadata(registries));
      }
   }

   @Nullable
   protected SkullBlockEntity loadInnerTile(String tagName, @Nullable SkullBlockEntity tile, CompoundTag tag, Provider registries) {
      if (tag.contains(tagName)) {
         BlockState state = Utils.readBlockState(tag.getCompound(tagName + "State"), this.level);
         CompoundTag tileTag = tag.getCompound(tagName);
         if (tile != null) {
            tile.loadWithComponents(tileTag, registries);
            return tile;
         }

         if (BlockEntity.loadStatic(this.getBlockPos(), state, tileTag, registries) instanceof SkullBlockEntity skullTile) {
            return skullTile;
         }
      }

      return null;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public ItemStack getSkullItem() {
      return this.innerTile != null ? new ItemStack(this.innerTile.getBlockState().getBlock()) : ItemStack.EMPTY;
   }

   public void initialize(SkullBlockEntity oldTile, ItemStack stack, Player player, InteractionHand hand) {
      RegistryAccess registries = player.level().registryAccess();
      this.innerTile = (SkullBlockEntity)oldTile.getType().create(this.getBlockPos(), oldTile.getBlockState());
      if (this.innerTile != null) {
         this.innerTile.loadWithComponents(oldTile.saveWithoutMetadata(registries), registries);
      }
   }

   @Nullable
   public BlockState getSkull() {
      return this.innerTile != null ? this.innerTile.getBlockState() : null;
   }

   @Nullable
   public BlockEntity getSkullTile() {
      return this.innerTile;
   }

   protected void tick(Level level, BlockPos pos, BlockState state) {
      tickInner(level, pos, this.innerTile);
   }

   protected static void tickInner(Level level, BlockPos pos, @Nullable SkullBlockEntity inner) {
      if (inner != null) {
         BlockState b = inner.getBlockState();
         if (b.getBlock() instanceof EntityBlock eb) {
            BlockEntityTicker ticker = eb.getTicker(level, b, inner.getType());
            if (ticker != null) {
               ticker.tick(level, pos, b, inner);
            }
         }
      }
   }
}
