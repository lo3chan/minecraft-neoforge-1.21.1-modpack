package net.joefoxe.hexerei.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public class FloweringLilyPadItem extends BlockItem {
   public FloweringLilyPadItem(Block p_43436_, Properties p_43437_) {
      super(p_43436_, p_43437_);
   }

   public InteractionResult useOn(UseOnContext p_43439_) {
      return InteractionResult.PASS;
   }

   public InteractionResultHolder<ItemStack> use(Level p_43441_, Player p_43442_, InteractionHand p_43443_) {
      BlockHitResult blockhitresult = getPlayerPOVHitResult(p_43441_, p_43442_, Fluid.SOURCE_ONLY);
      BlockHitResult blockhitresult1 = blockhitresult.withPosition(blockhitresult.getBlockPos().above());
      InteractionResult interactionresult = super.useOn(new UseOnContext(p_43442_, p_43443_, blockhitresult1));
      return new InteractionResultHolder(interactionresult, p_43442_.getItemInHand(p_43443_));
   }
}
