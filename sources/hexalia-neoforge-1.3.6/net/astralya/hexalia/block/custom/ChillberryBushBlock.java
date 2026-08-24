package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChillberryBushBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<ChillberryBushBlock> CODEC = simpleCodec(ChillberryBushBlock::new);
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
   private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);

   public ChillberryBushBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected MapCodec<? extends BushBlock> codec() {
      return CODEC;
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE && (Integer)state.getValue(AGE) >= 2) {
         entity.makeStuckInBlock(state, new Vec3(0.800000011920929, 0.75, 0.800000011920929));
         entity.setIsInPowderSnow(true);
         if (level.isClientSide()) {
            RandomSource random = level.getRandom();
            if (random.nextBoolean()) {
               level.addParticle(
                  ParticleTypes.SNOWFLAKE,
                  entity.getX(),
                  pos.getY() + 1,
                  entity.getZ(),
                  Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F,
                  0.05,
                  Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F
               );
            }
         }
      }
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      int age = (Integer)state.getValue(AGE);
      boolean mature = age == 3;
      if (age > 1) {
         int count = 1 + level.random.nextInt(2) + (mature ? 1 : 0);
         popResource(level, pos, new ItemStack((ItemLike)ModItems.CHILLBERRIES.get(), count));
         level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
         BlockState harvested = (BlockState)state.setValue(AGE, 1);
         level.setBlock(pos, harvested, 2);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, harvested));
         return InteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      return state.getValue(AGE) != 3 && stack.is(Items.BONE_MEAL)
         ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
         : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return (Integer)state.getValue(AGE) < 3;
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      level.setBlock(pos, (BlockState)state.setValue(AGE, Math.min(3, (Integer)state.getValue(AGE) + 1)), 2);
   }

   protected boolean isRandomlyTicking(BlockState state) {
      return (Integer)state.getValue(AGE) < 3;
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if ((Integer)state.getValue(AGE) < 3 && random.nextInt(5) == 0 && level.getRawBrightness(pos.above(), 0) >= 9) {
         BlockState grown = (BlockState)state.setValue(AGE, (Integer)state.getValue(AGE) + 1);
         level.setBlock(pos, grown, 2);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(grown));
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }
}
