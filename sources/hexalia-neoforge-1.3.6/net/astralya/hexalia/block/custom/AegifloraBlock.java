package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.AegifloraBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class AegifloraBlock extends EnchantedPlantBlock implements EntityBlock {
   public AegifloraBlock(Properties properties) {
      super(properties);
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new AegifloraBlockEntity(pos, state);
   }
}
