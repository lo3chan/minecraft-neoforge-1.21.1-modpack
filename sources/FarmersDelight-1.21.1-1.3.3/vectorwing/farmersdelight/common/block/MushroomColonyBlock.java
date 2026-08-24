package vectorwing.farmersdelight.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.util.TriState;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;

public class MushroomColonyBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<MushroomColonyBlock> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("mushroom").forGetter(block -> block.mushroomType), propertiesCodec())
         .apply(builder, MushroomColonyBlock::new)
   );
   public static final int PLACING_LIGHT_LEVEL = 13;
   public final Holder<Item> mushroomType;
   public static final IntegerProperty COLONY_AGE = BlockStateProperties.AGE_3;
   protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
      Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0),
      Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0),
      Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)
   };

   public MushroomColonyBlock(Holder<Item> mushroomType, Properties properties) {
      super(properties);
      this.mushroomType = mushroomType;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(COLONY_AGE, 0));
   }

   public ItemInteractionResult useItemOn(
      ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      int age = (Integer)state.getValue(COLONY_AGE);
      if (age > 0) {
         ItemStack mushroomStack = this.getCloneItemStack(level, pos, state);
         if (ItemUtils.isValidTool(heldStack, ItemAbilities.SHEARS_HARVEST, Items.TOOLS_SHEAR)) {
            level.setBlock(pos, (BlockState)state.setValue(COLONY_AGE, age - 1), 2);
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            popResource(level, pos, mushroomStack);
            if (!level.isClientSide) {
               heldStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
               ((ServerLevel)level)
                  .sendParticles(
                     new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.001
                  );
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }

         if (ItemUtils.isKnife(heldStack)) {
            int colonyAge = (Integer)state.getValue(COLONY_AGE);
            mushroomStack.setCount(colonyAge);
            level.setBlock(pos, (BlockState)state.setValue(COLONY_AGE, 0), 2);
            level.playSound(null, pos, this.soundType.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            popResource(level, pos, mushroomStack);
            if (!level.isClientSide) {
               heldStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
               ((ServerLevel)level)
                  .sendParticles(
                     new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.1
                  );
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
   }

   public IntegerProperty getAgeProperty() {
      return COLONY_AGE;
   }

   protected MapCodec<? extends BushBlock> codec() {
      return CODEC;
   }

   protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
      return state.isSolidRender(level, pos);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos floorPos = pos.below();
      BlockState floorState = level.getBlockState(floorPos);
      TriState soilDecision = floorState.canSustainPlant(level, floorPos, Direction.UP, state);
      return floorState.is(BlockTags.MUSHROOM_GROW_BLOCK)
         || (soilDecision.isDefault() ? level.getRawBrightness(pos, 0) < 13 && this.mayPlaceOn(floorState, level, floorPos) : soilDecision.isTrue());
   }

   public int getMaxAge() {
      return 3;
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      int age = (Integer)state.getValue(COLONY_AGE);
      BlockState groundState = level.getBlockState(pos.below());
      if (age < this.getMaxAge()
         && groundState.is(ModTags.Blocks.MUSHROOM_COLONY_GROWABLE_ON)
         && CommonHooks.canCropGrow(level, pos, state, random.nextInt(4) == 0)) {
         level.setBlock(pos, (BlockState)state.setValue(COLONY_AGE, age + 1), 2);
         CommonHooks.fireCropGrowPost(level, pos, state);
      }
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return new ItemStack((ItemLike)this.mushroomType.value());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{COLONY_AGE});
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return (Integer)state.getValue(this.getAgeProperty()) < this.getMaxAge();
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   protected int getBonemealAgeIncrease(Level level) {
      return Mth.nextInt(level.random, 1, 2);
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      int age = Math.min(this.getMaxAge(), (Integer)state.getValue(COLONY_AGE) + this.getBonemealAgeIncrease(level));
      level.setBlock(pos, (BlockState)state.setValue(COLONY_AGE, age), 2);
   }
}
