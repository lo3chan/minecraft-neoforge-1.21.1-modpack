package net.joefoxe.hexerei.block.custom;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;

public class Candelabra extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final VoxelShape GROUNDED_SHAPE = Stream.of(
         Block.box(6.5, 11.0, 6.5, 9.5, 12.0, 9.5),
         Block.box(1.0, 5.5, 7.0, 3.0, 7.5, 9.0),
         Block.box(13.0, 8.5, 7.0, 15.0, 12.5, 9.0),
         Block.box(6.5, 1.0, 6.5, 9.5, 2.0, 9.5),
         Block.box(1.0, 8.5, 7.0, 3.0, 12.5, 9.0),
         Block.box(3.0, 5.5, 7.01, 13.0, 6.5, 9.01),
         Block.box(12.5, 7.5, 6.5, 15.5, 8.5, 9.5),
         Block.box(7.0, 2.0, 7.0, 9.0, 11.0, 9.0),
         Block.box(13.0, 5.5, 7.0, 15.0, 7.5, 9.0),
         Block.box(6.5, 9.5, 12.5, 9.5, 10.5, 15.5),
         Block.box(0.5, 7.5, 6.5, 3.5, 8.5, 9.5),
         Block.box(7.0, 10.5, 13.0, 9.0, 14.5, 15.0),
         Block.box(5.5, 0.0, 5.5, 10.5, 1.0, 10.5),
         Block.box(7.0, 10.5, 1.0, 9.0, 14.5, 3.0),
         Block.box(7.0, 12.0, 7.0, 9.0, 16.0, 9.0),
         Block.box(7.0, 7.5, 13.0, 9.0, 9.5, 15.0),
         Block.box(7.01, 7.5, 3.0, 9.01, 8.5, 13.0),
         Block.box(7.0, 7.5, 1.0, 9.0, 9.5, 3.0),
         Block.box(6.5, 9.5, 0.5, 9.5, 10.5, 3.5)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   public static final VoxelShape HANGING_SHAPES = Stream.of(
         Block.box(6.5, 11.0, 6.5, 9.5, 12.0, 9.5),
         Block.box(1.0, 5.5, 7.0, 3.0, 7.5, 9.0),
         Block.box(13.0, 8.5, 7.0, 15.0, 12.5, 9.0),
         Block.box(1.0, 8.5, 7.0, 3.0, 12.5, 9.0),
         Block.box(3.0, 5.5, 7.01, 13.0, 6.5, 9.01),
         Block.box(12.5, 7.5, 6.5, 15.5, 8.5, 9.5),
         Block.box(7.0, 2.0, 7.0, 9.0, 11.0, 9.0),
         Block.box(7.0, 12.0, 7.0, 9.0, 16.0, 9.0),
         Block.box(13.0, 5.5, 7.0, 15.0, 7.5, 9.0),
         Block.box(6.5, 9.5, 12.5, 9.5, 10.5, 15.5),
         Block.box(0.5, 7.5, 6.5, 3.5, 8.5, 9.5),
         Block.box(7.0, 10.5, 13.0, 9.0, 14.5, 15.0),
         Block.box(7.0, 10.5, 1.0, 9.0, 14.5, 3.0),
         Block.box(7.0, 7.5, 13.0, 9.0, 9.5, 15.0),
         Block.box(7.01, 7.5, 3.0, 9.01, 8.5, 13.0),
         Block.box(7.0, 7.5, 1.0, 9.0, 9.5, 3.0),
         Block.box(6.5, 9.5, 0.5, 9.5, 10.5, 3.5),
         Block.box(7.5, 0.0, 7.5, 8.5, 2.0, 8.5)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   public static final VoxelShape GROUNDED_SHAPE_TURNED = Stream.of(
         Block.box(6.5, 11.0, 6.5, 9.5, 12.0, 9.5),
         Block.box(7.0, 5.5, 13.0, 9.0, 7.5, 15.0),
         Block.box(7.0, 8.5, 1.0, 9.0, 12.5, 3.0),
         Block.box(6.5, 1.0, 6.5, 9.5, 2.0, 9.5),
         Block.box(7.0, 8.5, 13.0, 9.0, 12.5, 15.0),
         Block.box(7.0, 5.5, 3.0, 9.01, 6.5, 13.0),
         Block.box(6.5, 7.5, 0.5, 9.5, 8.5, 3.5),
         Block.box(7.0, 2.0, 7.0, 9.0, 11.0, 9.0),
         Block.box(7.0, 5.5, 1.0, 9.0, 7.5, 3.0),
         Block.box(12.5, 9.5, 6.5, 15.5, 10.5, 9.5),
         Block.box(6.5, 7.5, 12.5, 9.5, 8.5, 15.5),
         Block.box(13.0, 10.5, 7.0, 15.0, 14.5, 9.0),
         Block.box(5.5, 0.0, 5.5, 10.5, 1.0, 10.5),
         Block.box(1.0, 10.5, 7.0, 3.0, 14.5, 9.0),
         Block.box(7.0, 12.0, 7.0, 9.0, 16.0, 9.0),
         Block.box(13.0, 7.5, 7.0, 15.0, 9.5, 9.0),
         Block.box(3.0, 7.5, 7.0, 13.0, 8.5, 9.0),
         Block.box(1.0, 7.5, 7.0, 3.0, 9.5, 9.0),
         Block.box(0.5, 9.5, 6.5, 3.5, 10.5, 9.5)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   public static final VoxelShape HANGING_SHAPES_TURNED = Stream.of(
         Block.box(6.5, 11.0, 6.5, 9.5, 12.0, 9.5),
         Block.box(7.0, 5.5, 13.0, 9.0, 7.5, 15.0),
         Block.box(7.0, 8.5, 1.03, 9.0, 12.5, 3.0),
         Block.box(7.0, 8.5, 13.0, 9.0, 12.5, 15.0),
         Block.box(7.01, 5.5, 3.0, 9.0, 6.5, 13.0),
         Block.box(6.5, 7.5, 0.5, 9.5, 8.5, 3.5),
         Block.box(7.0, 2.0, 7.0, 9.0, 11.0, 9.0),
         Block.box(7.0, 12.0, 7.0, 9.0, 16.0, 9.0),
         Block.box(7.0, 5.5, 1.03, 9.0, 7.5, 3.0),
         Block.box(12.5, 9.5, 6.5, 15.5, 10.5, 9.5),
         Block.box(6.5, 7.5, 12.5, 9.5, 8.5, 15.5),
         Block.box(13.0, 10.5, 7.0, 15.0, 14.5, 9.0),
         Block.box(1.0, 10.5, 7.0, 3.0, 14.5, 9.0),
         Block.box(13.0, 7.5, 7.0, 15.0, 9.5, 9.0),
         Block.box(3.0, 7.5, 7.0, 13.0, 8.5, 9.0),
         Block.box(1.0, 7.5, 7.0, 3.0, 9.5, 9.0),
         Block.box(0.5, 9.5, 6.5, 3.5, 10.5, 9.5),
         Block.box(7.5, 0.0, 7.5, 8.5, 2.0, 8.5)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

      for (Direction direction : context.getNearestLookingDirections()) {
         if (direction.getAxis() == Axis.Y) {
            BlockState blockstate = (BlockState)this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
            if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
               return (BlockState)((BlockState)((BlockState)blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER))
                     .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
                  .setValue(LIT, Boolean.FALSE);
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.getValue(HorizontalDirectionalBlock.FACING) != Direction.NORTH && state.getValue(HorizontalDirectionalBlock.FACING) != Direction.SOUTH
         ? (state.getValue(HANGING) ? HANGING_SHAPES_TURNED : GROUNDED_SHAPE_TURNED)
         : (state.getValue(HANGING) ? HANGING_SHAPES : GROUNDED_SHAPE);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      ItemStack itemstack = player.getItemInHand(hand);
      Random random = new Random();
      if (itemstack.getItem() == Items.FLINT_AND_STEEL && canBeLit(state)) {
         level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
         level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
         itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
         return ItemInteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   public static boolean canBeLit(BlockState state) {
      return !(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && !(Boolean)state.getValue(BlockStateProperties.LIT);
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   public Candelabra(Properties properties) {
      super(properties.noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HANGING, Boolean.FALSE)).setValue(WATERLOGGED, Boolean.FALSE))
            .setValue(LIT, Boolean.FALSE)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, HANGING, WATERLOGGED, LIT});
   }

   public static void extinguish(LevelAccessor world, BlockPos pos, BlockState state) {
      if (world.isClientSide()) {
         for (int i = 0; i < 20; i++) {
            spawnSmokeParticles((Level)world, pos, true);
         }
      }
   }

   public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
      if (!(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {
         boolean flag = (Boolean)state.getValue(LIT);
         if (flag) {
            if (!worldIn.isClientSide()) {
               worldIn.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            extinguish(worldIn, pos, state);
         }

         worldIn.setBlock(pos, (BlockState)((BlockState)state.setValue(WATERLOGGED, Boolean.TRUE)).setValue(LIT, Boolean.FALSE), 3);
         worldIn.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
         return true;
      } else {
         return false;
      }
   }

   protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
      if (!level.isClientSide && projectile.isOnFire()) {
         Entity entity = projectile.getOwner();
         boolean flag = entity == null || entity instanceof Player || EventHooks.canEntityGrief(level, entity);
         if (flag && !(Boolean)state.getValue(LIT) && !(Boolean)state.getValue(WATERLOGGED)) {
            BlockPos blockpos = hit.getBlockPos();
            level.setBlock(blockpos, (BlockState)state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
         }
      }

      super.onProjectileHit(level, state, hit, projectile);
   }

   public static void spawnSmokeParticles(Level worldIn, BlockPos pos, boolean spawnExtraSmoke) {
      RandomSource random = worldIn.getRandom();
      SimpleParticleType basicparticletype = ParticleTypes.CAMPFIRE_COSY_SMOKE;
      worldIn.addParticle(
         basicparticletype,
         true,
         pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
         pos.getY() + random.nextDouble() + random.nextDouble(),
         pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
         0.0,
         0.07,
         0.0
      );
      if (spawnExtraSmoke) {
         worldIn.addParticle(
            ParticleTypes.SMOKE,
            pos.getX() + 0.25 + random.nextDouble() / 2.0 * (random.nextBoolean() ? 1 : -1),
            pos.getY() + 0.4,
            pos.getZ() + 0.25 + random.nextDouble() / 2.0 * (random.nextBoolean() ? 1 : -1),
            0.0,
            0.005,
            0.0
         );
      }
   }

   public static boolean isLit(BlockState state) {
      return state.hasProperty(LIT) && (Boolean)state.getValue(LIT);
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      return !stateIn.canSurvive(worldIn, currentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      Direction direction = getBlockConnected(state).getOpposite();
      return Block.canSupportCenter(worldIn, pos.relative(direction), direction.getOpposite());
   }

   protected static Direction getBlockConnected(BlockState state) {
      return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return !(Boolean)state.getValue(WATERLOGGED);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.candelabra_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
      if ((Boolean)state.getValue(LIT)) {
         if (rand.nextInt(10) == 0) {
            world.playSound(
               null,
               pos.getX() + 0.5,
               pos.getY() + 0.5,
               pos.getZ() + 0.5,
               SoundEvents.FURNACE_FIRE_CRACKLE,
               SoundSource.BLOCKS,
               0.5F + rand.nextFloat() / 2.0F,
               rand.nextFloat() * 0.7F + 0.6F
            );
         }

         if (!(Boolean)state.getValue(HANGING)) {
            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F,
                  pos.getY() + 1.0625F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F,
                  pos.getY() + 1.0625F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }
         }

         if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST || state.getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F + 0.375F,
                  pos.getY() + 1.0F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F + 0.375F,
                  pos.getY() + 1.0F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F - 0.375F,
                  pos.getY() + 1.0F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F - 0.375F,
                  pos.getY() + 1.0F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F,
                  pos.getY() + 0.875F,
                  pos.getZ() + 0.5F + 0.375F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F,
                  pos.getY() + 0.875F,
                  pos.getZ() + 0.5F + 0.375F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F,
                  pos.getY() + 0.875F,
                  pos.getZ() + 0.5F - 0.375F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F,
                  pos.getY() + 0.875F,
                  pos.getZ() + 0.5F - 0.375F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }
         }

         if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH || state.getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F,
                  pos.getY() + 1.0F,
                  pos.getZ() + 0.5F + 0.375F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F,
                  pos.getY() + 1.0F,
                  pos.getZ() + 0.5F + 0.375F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F,
                  pos.getY() + 1.0F,
                  pos.getZ() + 0.5F - 0.375F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F,
                  pos.getY() + 1.0F,
                  pos.getZ() + 0.5F - 0.375F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F + 0.375F,
                  pos.getY() + 0.875F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F + 0.375F,
                  pos.getY() + 0.875F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.FLAME,
                  pos.getX() + 0.5F - 0.375F,
                  pos.getY() + 0.875F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.01,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }

            if (rand.nextInt(3) == 0) {
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5F - 0.375F,
                  pos.getY() + 0.875F,
                  pos.getZ() + 0.5F,
                  (rand.nextDouble() - 0.5) / 100.0,
                  (rand.nextDouble() + 0.5) * 0.035,
                  (rand.nextDouble() - 0.5) / 100.0
               );
            }
         }
      }
   }
}
