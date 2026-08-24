package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import com.github.alexthe666.alexsmobs.message.MessageUpdateTransmutablesToDisplay;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockTransmutationTable extends BaseEntityBlock implements AMSpecialRenderBlock {
   private static final Component CONTAINER_TITLE = Component.translatable("alexsmobs.container.transmutation_table");
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape BASE_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 5.0, 15.0);
   private static final VoxelShape ARMS_NS = Block.box(1.0, 5.0, 5.5, 15.0, 16.0, 10.5);
   private static final VoxelShape ARMS_EW = Block.box(5.5, 5.0, 1.0, 10.5, 16.0, 15.0);
   private static final VoxelShape NS_AABB = Shapes.or(BASE_AABB, ARMS_NS);
   private static final VoxelShape EW_AABB = Shapes.or(BASE_AABB, ARMS_EW);

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return AMPlatform.unsupportedBlockCodec();
   }

   public BlockTransmutationTable() {
      super(
         Properties.of()
            .pushReaction(PushReaction.BLOCK)
            .mapColor(DyeColor.BLACK)
            .noOcclusion()
            .lightLevel(block -> 2)
            .emissiveRendering((block, world, pos) -> true)
            .sound(SoundType.STONE)
            .strength(1.0F)
            .requiresCorrectToolForDrops()
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return ((Direction)state.getValue(FACING)).getAxis() == Axis.Z ? NS_AABB : EW_AABB;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TileEntityTransmutationTable(pos, state);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   protected ItemInteractionResult useItemOn(
      ItemStack amStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result
   ) {
      return AMCompat.itemResult(this.amUse(state, level, pos, player, hand, result));
   }

   private InteractionResult amUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         player.openMenu(state.getMenuProvider(level, pos));
         player.awardStat(Stats.INTERACT_WITH_LOOM);
         if (level.getBlockEntity(pos) instanceof TileEntityTransmutationTable table) {
            AlexsMobs.sendMSGToAll(
               new MessageUpdateTransmutablesToDisplay(player.getId(), table.getPossibility(0), table.getPossibility(1), table.getPossibility(2))
            );
         }

         return InteractionResult.CONSUME;
      }
   }

   public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
      BlockEntity te = level.getBlockEntity(pos);
      return new SimpleMenuProvider(
         (i, inv, player) -> new MenuTransmutationTable(
            i, inv, ContainerLevelAccess.create(level, pos), player, te instanceof TileEntityTransmutationTable ? (TileEntityTransmutationTable)te : null
         ),
         CONTAINER_TITLE
      );
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return createTickerHelper(p_152182_, AMTileEntityRegistry.TRANSMUTATION_TABLE.get(), TileEntityTransmutationTable::commonTick);
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      explodeOnDestroy(level, pos);
      return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
   }

   private static void explodeOnDestroy(Level level, BlockPos pos) {
      if (AMConfig.transmutingTableExplodes) {
         level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0F, false, ExplosionInteraction.BLOCK);
      }
   }
}
