package net.mehvahdjukaar.amendments.common.block;

import java.util.List;
import net.mehvahdjukaar.amendments.common.tile.WaterloggedLilyBlockTile;
import net.mehvahdjukaar.moonlight.api.block.IBlockHolder;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WaterloggedLilyBlock extends WaterlilyBlock implements LiquidBlockContainer, EntityBlock {
   protected static final VoxelShape AABB = Block.box(1.0, 15.0, 1.0, 15.0, 16.0, 15.0);
   protected static final VoxelShape AABB_EXTENDED = Block.box(1.0, 15.0, 1.0, 15.0, 17.5, 15.0);
   protected static final VoxelShape AABB_FAKE = Block.box(1.0, 16.0, 1.0, 15.0, 17.5, 15.0);
   protected static final VoxelShape AABB_SUPPORT = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
   public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;

   public WaterloggedLilyBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(EXTENDED, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{EXTENDED});
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
      return state.getValue(EXTENDED) ? AABB_FAKE : AABB;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return state.getValue(EXTENDED) ? AABB_EXTENDED : AABB;
   }

   public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
      return AABB_SUPPORT;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public FluidState getFluidState(BlockState state) {
      return Fluids.WATER.getSource(false);
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
      return stateIn;
   }

   public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
      super.neighborChanged(state, world, pos, neighborBlock, fromPos, moving);
      if (pos.above().equals(fromPos)) {
         this.maybeConvertToVanilla(state, world, pos);
      }
   }

   public void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
      if (!this.maybeConvertToVanilla(state, serverLevel, pos)) {
      }

      super.tick(state, serverLevel, pos, random);
   }

   private boolean maybeConvertToVanilla(BlockState state, LevelAccessor serverLevel, BlockPos pos) {
      if (serverLevel.getBlockState(pos.above()).isAir() && serverLevel.getBlockEntity(pos) instanceof WaterloggedLilyBlockTile te) {
         serverLevel.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
         serverLevel.setBlock(pos.above(), te.getHeldBlock(), 3);

         for (Entity e : serverLevel.getEntitiesOfClass(Entity.class, AABB_SUPPORT.bounds().move(pos).move(0.0, 0.0625, 0.0))) {
            if (e.getPistonPushReaction() != PushReaction.IGNORE) {
               e.move(MoverType.SHULKER_BOX, new Vec3(0.0, 0.09375, 0.0));
            }
         }

         return true;
      } else {
         if ((Boolean)state.getValue(EXTENDED)) {
            serverLevel.setBlock(pos, (BlockState)state.setValue(EXTENDED, false), 3);
         }

         return false;
      }
   }

   public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
      return false;
   }

   public long getSeed(BlockState pState, BlockPos pPos) {
      return Mth.getSeed(pPos.above());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new WaterloggedLilyBlockTile(pos, state);
   }

   public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
      if (worldIn.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock();
         if (!mimicState.isAir() && !(mimicState.getBlock() instanceof WaterloggedLilyBlock)) {
            return Math.min(super.getDestroyProgress(state, player, worldIn, pos), mimicState.getDestroyProgress(player, worldIn, pos));
         }
      }

      return super.getDestroyProgress(state, player, worldIn, pos);
   }

   public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
      if (world.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock();
         if (!mimicState.isAir()) {
            return mimicState.getSoundType();
         }
      }

      return super.getSoundType(state);
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      List<ItemStack> drops = super.getDrops(state, builder);
      if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IBlockHolder tile) {
         BlockState heldState = tile.getHeldBlock();
         if (builder.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player
            && !ForgeHelper.canHarvestBlock(
               heldState, builder.getLevel(), BlockPos.containing((Position)builder.getParameter(LootContextParams.ORIGIN)), player
            )) {
            return drops;
         }

         if (!heldState.isAir() && !(heldState.getBlock() instanceof WaterloggedLilyBlock)) {
            drops.addAll(heldState.getDrops(builder));
         }
      }

      return drops;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
      if (world.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock();
         if (!mimicState.isAir() && !(mimicState.getBlock() instanceof WaterloggedLilyBlock)) {
            return Math.max(ForgeHelper.getExplosionResistance(mimicState, (Level)world, pos, explosion), state.getBlock().getExplosionResistance());
         }
      }

      return 2.0F;
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      if (level.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimic = tile.getHeldBlock();
         if (!mimic.isAir() && !(mimic.getBlock() instanceof WaterloggedLilyBlock)) {
            return mimic.getBlock().getCloneItemStack(level, pos, mimic);
         }
      }

      return super.getCloneItemStack(level, pos, state);
   }

   public String getDescriptionId() {
      return Blocks.LILY_PAD.getDescriptionId();
   }
}
