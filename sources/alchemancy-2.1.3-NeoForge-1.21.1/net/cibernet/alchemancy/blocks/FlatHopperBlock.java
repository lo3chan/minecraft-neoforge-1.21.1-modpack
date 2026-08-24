package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import java.util.TreeMap;
import net.cibernet.alchemancy.events.handler.GeneralEventHandler;
import net.cibernet.alchemancy.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class FlatHopperBlock extends DirectionalBlock {
   private static final TreeMap<Direction, VoxelShape> SHAPES = VoxelShapeUtils.createDirectionMap(
      Shapes.or(
         Block.box(0.0, 0.0, 0.0, 3.0, 1.0, 16.0),
         new VoxelShape[]{Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 3.0), Block.box(13.0, 0.0, 0.0, 16.0, 1.0, 16.0), Block.box(0.0, 0.0, 13.0, 16.0, 1.0, 16.0)}
      )
   );
   public static final MapCodec<FlatHopperBlock> CODEC = simpleCodec(FlatHopperBlock::new);

   public FlatHopperBlock(Properties properties) {
      super(properties);
      GeneralEventHandler.registerTickingBlockFunction(this, FlatHopperBlock::tick);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPES.get(state.getValue(FACING));
   }

   public static void tick(ServerLevel level, BlockPos pos) {
      Direction facing = ((Direction)level.getBlockState(pos).getValue(FACING)).getOpposite();
      BlockPos connectedPos = pos.relative(facing);
      BlockState connectedState = level.getBlockState(connectedPos);
      IItemHandler cap = (IItemHandler)level.getCapability(ItemHandler.BLOCK, connectedPos, connectedState, level.getBlockEntity(pos), facing.getOpposite());
      if (cap != null) {
         for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos))) {
            if ((!item.hasPickUpDelay() || item.getPersistentData().getBoolean("alchemancy:from_pedestal"))
               && !item.getPersistentData().getBoolean("alchemancy:from_pedestal_click")) {
               if (ItemHandlerHelper.insertItem(cap, item.getItem(), false).isEmpty()) {
                  item.discard();
               }

               return;
            }
         }
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getClickedFace());
   }

   protected MapCodec<? extends DirectionalBlock> codec() {
      return CODEC;
   }
}
