package vectorwing.farmersdelight.common.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.tag.ModTags;

public class TomatoBlock extends CropBlock {
   public static final IntegerProperty VINE_AGE = BlockStateProperties.AGE_3;
   public static final BooleanProperty ROPELOGGED = BooleanProperty.create("ropelogged");
   private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

   public TomatoBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(this.getAgeProperty(), 0)).setValue(ROPELOGGED, false)
      );
   }

   protected TomatoBlock(Properties properties, boolean dummy) {
      super(properties);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      int age = (Integer)state.getValue(this.getAgeProperty());
      boolean isMature = age == this.getMaxAge();
      return !isMature && stack.is(Items.BONE_MEAL)
         ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
         : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      int age = (Integer)state.getValue(this.getAgeProperty());
      boolean isMature = age == this.getMaxAge();
      if (isMature) {
         int quantity = 1 + level.random.nextInt(2);
         popResource(level, pos, new ItemStack((ItemLike)ModItems.TOMATO.get(), quantity));
         if (level.random.nextFloat() < 0.05) {
            popResource(level, pos, new ItemStack((ItemLike)ModItems.ROTTEN_TOMATO.get()));
         }

         level.playSound(null, pos, ModSounds.BLOCK_TOMATOES_PICK_TOMATOES.get(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
         level.setBlock(pos, (BlockState)state.setValue(this.getAgeProperty(), 0), 2);
         return InteractionResult.SUCCESS;
      } else {
         return super.useWithoutItem(state, level, pos, player, hit);
      }
   }

   public boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         level.destroyBlock(pos, true);
         if ((Boolean)state.getValue(ROPELOGGED)) {
            destroyAndPlaceRope(level, pos);
         }
      }
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (level.isAreaLoaded(pos, 1)) {
         if (state.is((Block)ModBlocks.TOMATO_CROP.get()) && (Boolean)state.getValue(ROPELOGGED)) {
            level.setBlockAndUpdate(
               pos, (BlockState)((HangingTomatoBlock)ModBlocks.TOMATO_CROP_ON_ROPE.get()).defaultBlockState().setValue(VINE_AGE, this.getAge(state))
            );
         } else {
            if (level.getRawBrightness(pos, 0) >= 9) {
               int age = this.getAge(state);
               if (age < this.getMaxAge()) {
                  float speed = 5.0F;
                  if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / speed) + 1) == 0)) {
                     level.setBlock(pos, (BlockState)state.setValue(this.getAgeProperty(), age + 1), 2);
                     CommonHooks.fireCropGrowPost(level, pos, state);
                  }
               }

               this.climbRopeAbove(level, pos);
            }
         }
      }
   }

   public BlockState getStateForAge(int age) {
      return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), age);
   }

   public IntegerProperty getAgeProperty() {
      return VINE_AGE;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected ItemLike getBaseSeedId() {
      return (ItemLike)ModItems.TOMATO_SEEDS.get();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{VINE_AGE, ROPELOGGED});
   }

   public boolean canClimbBlock(BlockState stateAbove) {
      return Configuration.ENABLE_TOMATO_VINE_CLIMBING_TAGGED_ROPES.get() ? stateAbove.is(ModTags.Blocks.ROPES) : stateAbove.is(ModBlocks.ROPE.get());
   }

   @Nullable
   public BlockState getClimbingState(BlockState stateAbove) {
      return this.canClimbBlock(stateAbove) ? ((HangingTomatoBlock)ModBlocks.TOMATO_CROP_ON_ROPE.get()).defaultBlockState() : null;
   }

   public void climbRopeAbove(ServerLevel level, BlockPos pos) {
      BlockPos posAbove = pos.above();
      BlockState stateAbove = level.getBlockState(posAbove);
      BlockState climbingState = this.getClimbingState(stateAbove);
      if (climbingState != null) {
         int vineHeight = 1;

         while (level.getBlockState(pos.below(vineHeight)).is(this)) {
            vineHeight++;
         }

         if (vineHeight < 3) {
            level.setBlockAndUpdate(posAbove, climbingState);
         }
      }
   }

   protected int getBonemealAgeIncrease(Level level) {
      return super.getBonemealAgeIncrease(level) / 2;
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      if (!this.isMaxAge(state)) {
         return true;
      } else {
         MutableBlockPos mutablePos = pos.mutable();

         for (int height = 0; height < 2; height++) {
            mutablePos.move(Direction.UP);
            BlockState nextState = level.getBlockState(mutablePos);
            if (this.canClimbBlock(nextState)) {
               return true;
            }

            if (!(nextState.getBlock() instanceof HangingTomatoBlock)) {
               return false;
            }

            if (!this.isMaxAge(nextState)) {
               return true;
            }
         }

         return false;
      }
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      if (state.is((Block)ModBlocks.TOMATO_CROP.get()) && (Boolean)state.getValue(ROPELOGGED)) {
         level.setBlockAndUpdate(
            pos, (BlockState)((HangingTomatoBlock)ModBlocks.TOMATO_CROP_ON_ROPE.get()).defaultBlockState().setValue(VINE_AGE, this.getAge(state))
         );
      } else {
         int newAge = this.getAge(state) + this.getBonemealAgeIncrease(level);
         if (newAge <= this.getMaxAge()) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(this.getAgeProperty(), newAge));
            if (random.nextFloat() < 0.3F) {
               this.climbRopeAbove(level, pos);
            }
         } else {
            BlockState aboveState = level.getBlockState(pos.above());
            if (this.canClimbBlock(level.getBlockState(pos.above()))) {
               this.climbRopeAbove(level, pos);
            } else if (aboveState.is((Block)ModBlocks.TOMATO_CROP_ON_ROPE.get()) && this.isValidBonemealTarget(level, pos, aboveState)) {
               this.performBonemeal(level, random, pos.above(), aboveState);
            }
         }
      }
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos belowPos = pos.below();
      BlockState belowState = level.getBlockState(belowPos);
      return belowState.getBlock() instanceof TomatoBlock ? this.hasGoodCropConditions(level, pos) : super.canSurvive(state, level, pos);
   }

   public boolean hasGoodCropConditions(LevelReader level, BlockPos pos) {
      return level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if (!state.canSurvive(level, currentPos)) {
         level.scheduleTick(currentPos, this, 1);
      }

      return state;
   }

   @Deprecated(
      forRemoval = true
   )
   public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
      super.playerDestroy(level, player, pos, state, blockEntity, stack);
      if (state.hasProperty(ROPELOGGED) && (Boolean)state.getValue(ROPELOGGED)) {
         destroyAndPlaceRope(level, pos);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static void destroyAndPlaceRope(Level level, BlockPos pos) {
      Block configuredRopeBlock = (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(Configuration.DEFAULT_TOMATO_VINE_ROPE.get()));
      Block finalRopeBlock = configuredRopeBlock != null ? configuredRopeBlock : ModBlocks.ROPE.get();
      level.setBlockAndUpdate(pos, finalRopeBlock.defaultBlockState());
   }
}
