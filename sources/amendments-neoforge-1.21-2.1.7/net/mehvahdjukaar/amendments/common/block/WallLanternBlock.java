package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.mehvahdjukaar.amendments.common.LanternRegistry;
import net.mehvahdjukaar.amendments.common.network.ClientBoundEntityHitSwayingBlockMessage;
import net.mehvahdjukaar.amendments.common.tile.SwayingBlockTile;
import net.mehvahdjukaar.amendments.common.tile.WallLanternBlockTile;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.ThinAirCompat;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.WaterBlock;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WallLanternBlock extends WaterBlock implements EntityBlock {
   public static final MapCodec<WallLanternBlock> CODEC = simpleCodec(WallLanternBlock::new);
   public static final VoxelShape SHAPE_NORTH = Block.box(5.0, 2.0, 6.0, 11.0, 15.99, 16.0);
   public static final VoxelShape SHAPE_SOUTH = MthUtils.rotateVoxelShape(SHAPE_NORTH, Direction.SOUTH);
   public static final VoxelShape SHAPE_WEST = MthUtils.rotateVoxelShape(SHAPE_NORTH, Direction.WEST);
   public static final VoxelShape SHAPE_EAST = MthUtils.rotateVoxelShape(SHAPE_NORTH, Direction.EAST);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<ModBlockProperties.BlockAttachment> ATTACHMENT = ModBlockProperties.BLOCK_ATTACHMENT;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final IntegerProperty LIGHT_LEVEL = ModBlockProperties.LIGHT_LEVEL;
   public final LanternRegistry.LanternType type;

   public WallLanternBlock(Properties properties, LanternRegistry.LanternType type) {
      super(properties.lightLevel(s -> s.getValue(LIT) ? (Integer)s.getValue(LIGHT_LEVEL) : 0));
      this.type = type;
      int light = Math.max(ForgeHelper.getLightEmission(type.lantern.defaultBlockState(), null, BlockPos.ZERO), 5);
      boolean lit = !type.getId().toString().equals("charm:redstone_lantern");
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                  .setValue(LIGHT_LEVEL, Math.max(light, 5)))
               .setValue(WATERLOGGED, false))
            .setValue(LIT, lit)
      );
   }

   public BlockState getLanternState(BlockState wallState) {
      BlockState lantern = this.type.lantern.defaultBlockState();
      if (lantern.hasProperty(LanternBlock.HANGING)) {
         lantern = (BlockState)lantern.setValue(LanternBlock.HANGING, false);
      }

      if (lantern.hasProperty(LIT) && wallState.hasProperty(LIT)) {
         lantern = (BlockState)lantern.setValue(LIT, (Boolean)wallState.getValue(LIT));
      }

      return lantern;
   }

   @Deprecated
   public WallLanternBlock(Properties properties) {
      this(properties, LanternRegistry.VANILLA);
   }

   protected MapCodec<? extends WallLanternBlock> codec() {
      return CODEC;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      BlockState lantern = this.getLanternState(state);
      ItemInteractionResult blockRes = lantern.useItemOn(stack, level, player, hand, hitResult);
      if (blockRes.consumesAction() && lantern.hasProperty(LIT)) {
         BlockState updated = this.getLanternState(state);
         if (updated.getValue(LIT) != state.getValue(LIT)) {
            level.setBlock(pos, (BlockState)state.setValue(LIT, (Boolean)updated.getValue(LIT)), 2);
         }
      }

      return blockRes;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      BlockState lantern = this.getLanternState(state);
      InteractionResult useResult = lantern.useWithoutItem(level, player, hitResult);
      if (useResult.consumesAction() && lantern.hasProperty(LIT)) {
         BlockState updated = this.getLanternState(state);
         if (updated.getValue(LIT) != state.getValue(LIT)) {
            level.setBlock(pos, (BlockState)state.setValue(LIT, (Boolean)updated.getValue(LIT)), 2);
         }
      }

      return useResult;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      if (context.getClickedFace().getAxis() == Axis.Y) {
         return null;
      } else {
         BlockState state = super.getStateForPlacement(context);
         BlockPos blockpos = context.getClickedPos();
         Level world = context.getLevel();
         Direction dir = context.getClickedFace();
         BlockPos relative = blockpos.relative(dir.getOpposite());
         BlockState facingState = world.getBlockState(relative);
         return (BlockState)getConnectedState(state, facingState, world, relative, dir).setValue(FACING, context.getClickedFace());
      }
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      return facing == ((Direction)stateIn.getValue(FACING)).getOpposite()
         ? (!stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : getConnectedState(stateIn, facingState, worldIn, facingPos, facing))
         : stateIn;
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction direction = (Direction)state.getValue(FACING);
      BlockPos blockpos = pos.relative(direction.getOpposite());
      BlockState blockstate = level.getBlockState(blockpos);
      return ModBlockProperties.BlockAttachment.get(blockstate, blockpos, level, direction) != null;
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   public static BlockState getConnectedState(BlockState state, BlockState facingState, LevelAccessor world, BlockPos pos, Direction dir) {
      ModBlockProperties.BlockAttachment attachment = ModBlockProperties.BlockAttachment.get(facingState, pos, world, dir);
      return attachment == null ? state : (BlockState)state.setValue(ATTACHMENT, attachment);
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case NORTH -> SHAPE_NORTH;
         case WEST -> SHAPE_WEST;
         case EAST -> SHAPE_EAST;
         default -> SHAPE_SOUTH;
      };
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return new ItemStack(this.type.lantern);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LIGHT_LEVEL, LIT, FACING, ATTACHMENT});
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
      super.tick(state, level, pos, rand);
      if (state.getBlock() instanceof WallLanternBlock wall) {
         if (wall.type.getId().toString().equals("charm:redstone_lantern") && (Boolean)state.getValue(LIT) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, (BlockState)state.cycle(LIT), 2);
         }

         if (CompatHandler.THIN_AIR && level.getBlockEntity(pos) instanceof WallLanternBlockTile te) {
            BlockState lantern = wall.getLanternState(state);
            if (ThinAirCompat.isAirLantern(lantern)) {
               te.updateThinAir(lantern);
               level.sendBlockUpdated(pos, state, state, 3);
            }
         }
      }
   }

   public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
      if (!world.isClientSide && state.getBlock() instanceof WallLanternBlock wall && wall.type.getId().toString().equals("charm:redstone_lantern")) {
         boolean flag = (Boolean)state.getValue(LIT);
         if (flag != world.hasNeighborSignal(pos)) {
            if (flag) {
               world.scheduleTick(pos, this, 4);
            } else {
               world.setBlock(pos, (BlockState)state.cycle(LIT), 2);
            }
         }
      }
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return state.getBlock() instanceof WallLanternBlock wall ? List.of(new ItemStack(wall.type.lantern)) : super.getDrops(state, builder);
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (state.getBlock() instanceof WallLanternBlock wall) {
         BlockState s = wall.getLanternState(state);
         s.getBlock().animateTick(s, level, pos, random);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState pState) {
      return new WallLanternBlockTile(pos, pState);
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      super.entityInside(state, level, pos, entity);
      if (level.isClientSide) {
         if (level.getBlockEntity(pos) instanceof WallLanternBlockTile tile) {
            tile.amendments$getAnimation().hitByEntity(entity, state, pos);
         }
      } else {
         if (entity.xo != entity.getX() || entity.zo != entity.getZ() || entity.yo != entity.getY()) {
            level.gameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
         }

         NetworkHelper.sendToAllClientPlayersTrackingEntity(entity, new ClientBoundEntityHitSwayingBlockMessage(pos, entity.getId()));
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return Utils.getTicker(pBlockEntityType, ModRegistry.WALL_LANTERN_TILE.get(), pLevel.isClientSide ? SwayingBlockTile::clientTick : null);
   }

   public static void placeOn(LanternRegistry.LanternType lanternType, BlockPos onPos, Direction face, Level world) {
      WallLanternBlock wallBlock = ModRegistry.WALL_LANTERNS.get(lanternType);
      if (wallBlock != null) {
         BlockState state = (BlockState)getConnectedState(wallBlock.defaultBlockState(), world.getBlockState(onPos), world, onPos, face).setValue(FACING, face);
         world.setBlock(onPos.relative(face), state, 3);
      }
   }
}
