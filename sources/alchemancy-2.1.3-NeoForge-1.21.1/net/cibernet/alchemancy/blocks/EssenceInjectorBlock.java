package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.blocks.blockentities.EssenceInjectorBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.IEssenceHolder;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EssenceInjectorBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final MapCodec<EssenceInjectorBlock> CODEC = simpleCodec(EssenceInjectorBlock::new);

   public EssenceInjectorBlock(Properties properties) {
      super(properties);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState()
         .setValue(
            FACING,
            context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
               ? context.getHorizontalDirection().getOpposite()
               : context.getHorizontalDirection()
         );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new EssenceInjectorBlockEntity(pos, state);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      IEssenceHolder blockEntity = (IEssenceHolder)level.getBlockEntity(pos);
      player.displayClientMessage(
         Component.literal("Essence: [" + blockEntity.getEssenceContainer().getEssence() + " x" + blockEntity.getEssenceContainer().getAmount() + "]"), true
      );
      return InteractionResult.SUCCESS;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return level.isClientSide
         ? null
         : createTickerHelper(blockEntityType, (BlockEntityType)AlchemancyBlockEntities.ESSENCE_INJECTOR.get(), EssenceInjectorBlockEntity::serverTick);
   }

   protected BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }
}
