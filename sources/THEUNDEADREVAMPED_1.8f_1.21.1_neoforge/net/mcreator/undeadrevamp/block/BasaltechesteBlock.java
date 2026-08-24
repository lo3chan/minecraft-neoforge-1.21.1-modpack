package net.mcreator.undeadrevamp.block;

import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlockEntities;
import net.mcreator.undeadrevamp.procedures.CoffinBlockAddedProcedure;
import net.mcreator.undeadrevamp.procedures.CoffinOnBlockRightClickedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BasaltechesteBlock extends BaseEntityBlock implements EntityBlock {
   public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final MapCodec<BasaltechesteBlock> CODEC = simpleCodec(properties -> new BasaltechesteBlock());

   public MapCodec<BasaltechesteBlock> codec() {
      return CODEC;
   }

   public BasaltechesteBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.BASALT)
            .strength(1.0F, 20.0F)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return ((BlockEntityType)UndeadRevamp2ModBlockEntities.BASALTECHESTE.get()).create(blockPos, blockState);
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return true;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case NORTH -> Shapes.or(box(0.0, 0.0, 5.0, 16.0, 32.0, 13.0), box(0.0, 0.0, 1.0, 16.0, 32.0, 4.0));
         case EAST -> Shapes.or(box(3.0, 0.0, 0.0, 11.0, 32.0, 16.0), box(12.0, 0.0, 0.0, 15.0, 32.0, 16.0));
         case WEST -> Shapes.or(box(5.0, 0.0, 0.0, 13.0, 32.0, 16.0), box(1.0, 0.0, 0.0, 4.0, 32.0, 16.0));
         default -> Shapes.or(box(0.0, 0.0, 3.0, 16.0, 32.0, 11.0), box(0.0, 0.0, 12.0, 16.0, 32.0, 15.0));
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{ANIMATION, FACING});
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

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      List<ItemStack> dropsOriginal = super.getDrops(state, builder);
      return !dropsOriginal.isEmpty() ? dropsOriginal : Collections.singletonList(new ItemStack(Blocks.BASALT, 3));
   }

   public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
      super.onPlace(blockstate, world, pos, oldState, moving);
      CoffinBlockAddedProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }

   public InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
      super.useWithoutItem(blockstate, world, pos, entity, hit);
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      double hitX = hit.getLocation().x;
      double hitY = hit.getLocation().y;
      double hitZ = hit.getLocation().z;
      Direction direction = hit.getDirection();
      CoffinOnBlockRightClickedProcedure.execute(world, x, y, z);
      return InteractionResult.SUCCESS;
   }
}
