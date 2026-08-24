package net.joefoxe.hexerei.block.custom;

import java.util.List;
import java.util.Random;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.SageBurningPlateTile;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SageBurningPlate extends Block implements ITileEntity<SageBurningPlateTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final IntegerProperty MODE = IntegerProperty.create("mode", 0, 3);
   public static final VoxelShape SHAPE = Block.box(2.0, 0.0, 5.0, 14.0, 1.0, 11.0);
   public static final VoxelShape SHAPE_TURNED = Block.box(5.0, 0.0, 2.0, 11.0, 1.0, 14.0);

   public RenderShape getRenderShape(BlockState p_60550_) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState()
                  .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
               .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER))
            .setValue(LIT, false))
         .setValue(MODE, 0);
   }

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.EAST
            && p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.WEST
         ? SHAPE
         : SHAPE_TURNED;
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      BlockEntity tileEntity = level.getBlockEntity(pos);
      if (stack.is((Item)ModItems.CROW_FLUTE.get()) && ((FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
         stack.useOn(new UseOnContext(player, hand, hitResult));
         return ItemInteractionResult.SUCCESS;
      } else {
         ItemStack itemstack = player.getItemInHand(hand);
         Random random = new Random();
         if (tileEntity instanceof SageBurningPlateTile sageBurningPlateTile) {
            if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
               if (((ItemStack)sageBurningPlateTile.getItems().get(0)).is((Item)ModItems.DRIED_SAGE_BUNDLE.get()) && !(Boolean)state.getValue(LIT)) {
                  level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.LIT, true), 11);
                  level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                  itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                  return ItemInteractionResult.sidedSuccess(level.isClientSide());
               } else {
                  return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
               }
            } else {
               if (itemstack.isEmpty() && !player.isShiftKeyDown()) {
                  level.setBlock(pos, (BlockState)state.setValue(MODE, state.getValue(MODE) + 1 > 3 ? 0 : (Integer)state.getValue(MODE) + 1), 11);
                  state = level.getBlockState(pos);
                  String s = "display.hexerei.sage_plate_toggle_" + state.getValue(MODE);
                  player.displayClientMessage(Component.translatable(s), true);
               } else {
                  ((SageBurningPlateTile)tileEntity).interactSageBurningPlate(player, hitResult);
               }

               return ItemInteractionResult.SUCCESS;
            }
         } else {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
         }
      }
   }

   public SageBurningPlate(Properties properties) {
      super(properties.noOcclusion());
      this.withPropertiesOf((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE)).setValue(LIT, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, WATERLOGGED, LIT, MODE});
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         MutableComponent string = Component.translatable(HexConfig.SAGE_BURNING_PLATE_RANGE.get() + "")
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)));
         MutableComponent itemText = Component.translatable(((Item)ModItems.DRIED_SAGE_BUNDLE.get()).getDescriptionId())
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10061824)));
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.sage_burning_plate_shift_1", new Object[]{itemText})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.sage_burning_plate_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.sage_burning_plate_shift_3", new Object[]{string})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.sage_burning_plate_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.sage_burning_plate_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.sage_burning_plate_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         BlockEntity tileentity = level.getBlockEntity(pos);
         if (tileentity != null) {
            SageBurningPlateTile te = (SageBurningPlateTile)level.getBlockEntity(pos);
            if (!((ItemStack)te.getItems().get(0)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(0)));
            }
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   @Override
   public Class<SageBurningPlateTile> getTileEntityClass() {
      return SageBurningPlateTile.class;
   }

   @javax.annotation.Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SageBurningPlateTile((BlockEntityType<?>)ModTileEntities.SAGE_BURNING_PLATE_TILE.get(), pos, state);
   }

   @javax.annotation.Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.SAGE_BURNING_PLATE_TILE.get() ? (world2, pos, state2, entity) -> ((SageBurningPlateTile)entity).tick() : null;
   }
}
