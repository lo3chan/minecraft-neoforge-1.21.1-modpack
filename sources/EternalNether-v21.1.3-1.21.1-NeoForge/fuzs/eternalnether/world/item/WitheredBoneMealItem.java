package fuzs.eternalnether.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class WitheredBoneMealItem extends Item {
   private static final int GROWTH_BONUS_COUNT = 3;

   public WitheredBoneMealItem(Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      BlockPos blockPos = context.getClickedPos();
      BlockPos blockPos2 = blockPos.relative(context.getClickedFace());
      if (growCrop(context.getItemInHand(), level, blockPos)) {
         if (!level.isClientSide) {
            context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            level.levelEvent(1505, blockPos, 15);
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         BlockState blockState = level.getBlockState(blockPos);
         if (blockState.isFaceSturdy(level, blockPos, context.getClickedFace())
            && growWaterPlant(context.getItemInHand(), level, blockPos2, context.getClickedFace())) {
            if (!level.isClientSide) {
               context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
               level.levelEvent(1505, blockPos2, 15);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public static boolean growCrop(ItemStack itemStack, Level level, BlockPos blockPos) {
      boolean wasPlantGrown = false;

      for (int i = 0; i < 3; i++) {
         if (!BoneMealItem.growCrop(itemStack, level, blockPos)) {
            return wasPlantGrown;
         }

         wasPlantGrown = true;
      }

      return wasPlantGrown;
   }

   public static boolean growWaterPlant(ItemStack itemStack, Level level, BlockPos blockPos, @Nullable Direction direction) {
      boolean wasPlantGrown = false;

      for (int i = 0; i < 3; i++) {
         if (!BoneMealItem.growWaterPlant(itemStack, level, blockPos, direction)) {
            return wasPlantGrown;
         }

         wasPlantGrown = true;
      }

      return wasPlantGrown;
   }
}
