package vectorwing.farmersdelight.common.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.MathUtils;

public class CookingPotBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
   public static final MapCodec<CookingPotBlock> CODEC = simpleCodec(CookingPotBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<CookingPotSupport> SUPPORT = EnumProperty.create("support", CookingPotSupport.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0);
   protected static final VoxelShape SHAPE_WITH_TRAY = Shapes.or(SHAPE, Block.box(0.0, -1.0, 0.0, 16.0, 0.0, 16.0));

   public CookingPotBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
               .setValue(SUPPORT, CookingPotSupport.NONE))
            .setValue(WATERLOGGED, false)
      );
   }

   protected MapCodec<? extends Block> codec() {
      return CODEC;
   }

   public ItemInteractionResult useItemOn(
      ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result
   ) {
      if (heldStack.isEmpty() && player.isShiftKeyDown()) {
         level.setBlockAndUpdate(
            pos,
            (BlockState)state.setValue(
               SUPPORT,
               ((CookingPotSupport)state.getValue(SUPPORT)).equals(CookingPotSupport.HANDLE) ? this.getTrayState(level, pos) : CookingPotSupport.HANDLE
            )
         );
         level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
      } else if (!level.isClientSide) {
         if (level.getBlockEntity(pos) instanceof CookingPotBlockEntity cookingPot) {
            ItemStack servingStack = cookingPot.useHeldItemOnMeal(heldStack);
            if (servingStack != ItemStack.EMPTY) {
               if (!player.getInventory().add(servingStack)) {
                  player.drop(servingStack, false);
               }

               level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
               player.openMenu(cookingPot, pos);
            }
         }

         return ItemInteractionResult.SUCCESS;
      }

      return ItemInteractionResult.SUCCESS;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return ((CookingPotSupport)state.getValue(SUPPORT)).equals(CookingPotSupport.TRAY) ? SHAPE_WITH_TRAY : SHAPE;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      FluidState fluid = level.getFluidState(context.getClickedPos());
      BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
      return context.getClickedFace().equals(Direction.DOWN)
         ? (BlockState)state.setValue(SUPPORT, CookingPotSupport.HANDLE)
         : (BlockState)state.setValue(SUPPORT, this.getTrayState(level, pos));
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return facing.getAxis().equals(Axis.Y) && !((CookingPotSupport)state.getValue(SUPPORT)).equals(CookingPotSupport.HANDLE)
         ? (BlockState)state.setValue(SUPPORT, this.getTrayState(level, currentPos))
         : state;
   }

   private CookingPotSupport getTrayState(LevelAccessor level, BlockPos pos) {
      return level.getBlockState(pos.below()).is(ModTags.Blocks.TRAY_HEAT_SOURCES) ? CookingPotSupport.TRAY : CookingPotSupport.NONE;
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      ItemStack stack = super.getCloneItemStack(level, pos, state);
      Optional<CookingPotBlockEntity> cookingPot = level.getBlockEntity(pos, ModBlockEntityTypes.COOKING_POT.get());
      if (cookingPot.isPresent()) {
         stack = cookingPot.get().getAsItem();
      }

      return stack;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof CookingPotBlockEntity cookingPot) {
            Containers.dropContents(level, pos, cookingPot.getDroppableInventory());
            cookingPot.getUsedRecipesAndPopExperience(level, Vec3.atCenterOf(pos));
            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, SUPPORT, WATERLOGGED});
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (level.getBlockEntity(pos) instanceof CookingPotBlockEntity cookingPot && cookingPot.isHeated()) {
         SoundEvent boilSound = !cookingPot.getMeal().isEmpty() ? ModSounds.BLOCK_COOKING_POT_BOIL_SOUP.get() : ModSounds.BLOCK_COOKING_POT_BOIL.get();
         double x = pos.getX() + 0.5;
         double y = pos.getY();
         double z = pos.getZ() + 0.5;
         if (random.nextInt(10) == 0) {
            level.playLocalSound(x, y, z, boilSound, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.2F + 0.9F, false);
         }
      }
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof CookingPotBlockEntity cookingPot) {
         ItemStackHandler inventory = cookingPot.getInventory();
         return MathUtils.calcRedstoneFromItemHandler(inventory);
      } else {
         return 0;
      }
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlockEntityTypes.COOKING_POT.get().create(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
      return level.isClientSide
         ? createTickerHelper(blockEntity, ModBlockEntityTypes.COOKING_POT.get(), CookingPotBlockEntity::animationTick)
         : createTickerHelper(blockEntity, ModBlockEntityTypes.COOKING_POT.get(), CookingPotBlockEntity::cookingTick);
   }

   @Nullable
   protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
      BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker
   ) {
      return clientType == serverType ? ticker : null;
   }
}
