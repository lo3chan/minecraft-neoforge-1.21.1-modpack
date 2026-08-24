package vectorwing.farmersdelight.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.EventHooks;
import vectorwing.farmersdelight.common.registry.ModItems;

public class BuddingBushBlock extends BushBlock {
   public static final MapCodec<BuddingBushBlock> CODEC = simpleCodec(BuddingBushBlock::new);
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
      Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
   };

   public BuddingBushBlock(Properties properties) {
      super(properties);
   }

   protected MapCodec<? extends BushBlock> codec() {
      return CODEC;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
   }

   protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
      return state.getBlock() instanceof FarmBlock;
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected int getAge(BlockState state) {
      return (Integer)state.getValue(this.getAgeProperty());
   }

   public BlockState getStateForAge(int age) {
      return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), age);
   }

   public boolean isMaxAge(BlockState state) {
      return (Integer)state.getValue(this.getAgeProperty()) >= this.getMaxAge();
   }

   public boolean isRandomlyTicking(BlockState state) {
      return this.canGrowPastMaxAge() || !this.isMaxAge(state);
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (level.isAreaLoaded(pos, 1)) {
         if (level.getRawBrightness(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age <= this.getMaxAge()) {
               float growthSpeed = getGrowthSpeed(state, level, pos);
               if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / growthSpeed) + 1) == 0)) {
                  if (this.isMaxAge(state)) {
                     this.growPastMaxAge(state, level, pos, random);
                  } else {
                     level.setBlockAndUpdate(pos, this.getStateForAge(age + 1));
                  }

                  CommonHooks.fireCropGrowPost(level, pos, state);
               }
            }
         }
      }
   }

   public boolean canGrowPastMaxAge() {
      return false;
   }

   public void growPastMaxAge(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
   }

   protected static float getGrowthSpeed(BlockState state, BlockGetter level, BlockPos pos) {
      float speed = 1.0F;
      BlockPos posBelow = pos.below();

      for (int posX = -1; posX <= 1; posX++) {
         for (int posZ = -1; posZ <= 1; posZ++) {
            float speedBonus = 0.0F;
            BlockState stateBelow = level.getBlockState(posBelow.offset(posX, 0, posZ));
            TriState soilDecision = stateBelow.canSustainPlant(level, posBelow.offset(posX, 0, posZ), Direction.UP, state);
            if (soilDecision.isDefault()) {
               speedBonus = 1.0F;
               if (stateBelow.isFertile(level, pos.offset(posX, 0, posZ))) {
                  speedBonus = 3.0F;
               }
            }

            if (posX != 0 || posZ != 0) {
               speedBonus /= 4.0F;
            }

            speed += speedBonus;
         }
      }

      BlockPos posNorth = pos.north();
      BlockPos posSouth = pos.south();
      BlockPos posWest = pos.west();
      BlockPos posEast = pos.east();
      Block block = state.getBlock();
      boolean matchesEastWestRow = level.getBlockState(posWest).is(block) || level.getBlockState(posEast).is(block);
      boolean matchesNorthSouthRow = level.getBlockState(posNorth).is(block) || level.getBlockState(posSouth).is(block);
      if (matchesEastWestRow && matchesNorthSouthRow) {
         speed /= 2.0F;
      } else {
         boolean matchesDiagonalRows = level.getBlockState(posWest.north()).is(block)
            || level.getBlockState(posEast.north()).is(block)
            || level.getBlockState(posEast.south()).is(block)
            || level.getBlockState(posWest.south()).is(block);
         if (matchesDiagonalRows) {
            speed /= 2.0F;
         }
      }

      return speed;
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      TriState soilDecision = level.getBlockState(pos.below()).canSustainPlant(level, pos.below(), Direction.UP, state);
      return !soilDecision.isDefault() ? soilDecision.isTrue() : hasSufficientLight(level, pos) && super.canSurvive(state, level, pos);
   }

   public static boolean hasSufficientLight(LevelReader level, BlockPos pos) {
      return level.getRawBrightness(pos, 0) >= 8;
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (entity instanceof Ravager && EventHooks.canEntityGrief(level, entity)) {
         level.destroyBlock(pos, true, entity);
      }

      super.entityInside(state, level, pos, entity);
   }

   protected ItemLike getBaseSeedId() {
      return (ItemLike)ModItems.TOMATO_SEEDS.get();
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return new ItemStack(this.getBaseSeedId());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }
}
