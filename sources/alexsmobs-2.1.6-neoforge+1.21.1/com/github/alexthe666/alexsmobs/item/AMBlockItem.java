package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AMBlockItem extends BlockItem implements CustomTabBehavior {
   private final Supplier<Block> blockSupplier;

   public AMBlockItem(Supplier<Block> blockSupplier, Properties props) {
      super((Block)null, props);
      this.blockSupplier = blockSupplier;
   }

   public Block getBlock() {
      return this.blockSupplier.get();
   }

   protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
      boolean applied = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
      CompoundTag stashed = AMCompat.getTagElement(stack, "BlockEntityTag");
      if (stashed != null && !level.isClientSide()) {
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity == null) {
            return applied;
         } else {
            Provider provider = level.registryAccess();
            CompoundTag merged = blockEntity.saveCustomOnly(provider);
            merged.merge(stashed);
            AMCompat.loadCustomOnly(blockEntity, merged, provider);
            blockEntity.setChanged();
            return true;
         }
      } else {
         return applied;
      }
   }

   public boolean canFitInsideCraftingRemainingItems() {
      return !(this.blockSupplier.get() instanceof ShulkerBoxBlock);
   }

   public void onDestroyed(ItemEntity p_150700_) {
      if (this.blockSupplier.get() instanceof ShulkerBoxBlock) {
         ItemStack itemstack = p_150700_.getItem();
         CompoundTag compoundtag = AMCompat.getBlockEntityData(itemstack);
         if (compoundtag != null && AMCompat.contains(compoundtag, "Items", 9)) {
            ListTag listtag = AMCompat.getList(compoundtag, "Items", 10);
            Provider provider = p_150700_.level().registryAccess();
            Stream<ItemStack> contents = listtag.stream().map(CompoundTag.class::cast).map(tag -> AMCompat.loadItem(provider, tag));
            ItemUtils.onContainerDestroyed(p_150700_, contents::iterator);
         }
      }
   }

   @Override
   public void fillItemCategory(Output contents) {
      if (!this.blockSupplier.equals(AMBlockRegistry.SAND_CIRCLE) && !this.blockSupplier.equals(AMBlockRegistry.RED_SAND_CIRCLE)) {
         contents.accept(this);
      }
   }

   public InteractionResult useOn(UseOnContext context) {
      return this.blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS) ? InteractionResult.PASS : super.useOn(context);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      if (this.blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS)) {
         BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, Fluid.SOURCE_ONLY);
         BlockHitResult blockhitresult1 = blockhitresult.withPosition(blockhitresult.getBlockPos().above());
         InteractionResult interactionresult = super.useOn(new UseOnContext(player, hand, blockhitresult1));
         return AMCompat.holder(interactionresult, player.getItemInHand(hand));
      } else {
         return super.use(level, player, hand);
      }
   }
}
