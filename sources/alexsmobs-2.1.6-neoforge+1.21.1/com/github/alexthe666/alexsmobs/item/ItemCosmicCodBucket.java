package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class ItemCosmicCodBucket extends ItemModFishBucket {
   public ItemCosmicCodBucket(Properties builder) {
      super(AMEntityRegistry.COSMIC_COD, Fluids.EMPTY, builder.stacksTo(1));
   }

   @Nonnull
   public InteractionResultHolder<ItemStack> use(@Nonnull Level level, Player player, @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, Fluid.NONE);
      if (blockhitresult.getType() == Type.MISS) {
         return AMCompat.pass(itemstack);
      } else if (blockhitresult.getType() != Type.BLOCK) {
         return AMCompat.pass(itemstack);
      } else {
         BlockPos blockpos = blockhitresult.getBlockPos();
         Direction direction = blockhitresult.getDirection();
         BlockPos blockpos1 = blockpos.relative(direction);
         if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, itemstack)) {
            this.checkExtraContent(player, level, itemstack, blockpos1);
            player.awardStat(Stats.ITEM_USED.get(this));
            return AMCompat.sidedSuccess(getEmptySuccessItem(itemstack, player), level.isClientSide());
         } else {
            return super.use(level, player, hand);
         }
      }
   }
}
