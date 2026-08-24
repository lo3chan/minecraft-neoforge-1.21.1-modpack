package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.SmallCauldronBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.FireStarterHelper;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final MapCodec<SmallCauldronBlock> CODEC = simpleCodec(SmallCauldronBlock::new);

   public SmallCauldronBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false)).setValue(LIT, false)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
            .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER))
         .setValue(LIT, false);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WATERLOGGED, LIT});
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SmallCauldronBlockEntity(pos, state);
   }

   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      if (state.is(newState.getBlock())) {
         super.onRemove(state, level, pos, newState, movedByPiston);
      } else {
         if (level.getBlockEntity(pos) instanceof SmallCauldronBlockEntity cauldron && level instanceof ServerLevel) {
            cauldron.dropAll(level);
         }

         super.onRemove(state, level, pos, newState, movedByPiston);
      }
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      return level.getBlockEntity(pos) instanceof SmallCauldronBlockEntity cauldron
         ? ItemInteractionHelper.tryExtractOneItem(level, pos, player, cauldron)
         : InteractionResult.PASS;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (level.getBlockEntity(pos) instanceof SmallCauldronBlockEntity cauldron) {
         ItemInteractionResult result = this.tryStirWithLadle(stack, state, level, pos, player, hand, cauldron);
         if (result != null) {
            return result;
         } else {
            result = this.tryIgniteWithFireStarter(stack, state, level, pos, player, hand);
            if (result != null) {
               return result;
            } else {
               result = this.tryRusticBottle(stack, level, player, hand, cauldron);
               if (result != null) {
                  return result;
               } else {
                  result = this.tryLotusCleanse(stack, level, player, hand, cauldron);
                  if (result != null) {
                     return result;
                  } else {
                     result = this.tryWaterContainer(stack, level, player, hand, cauldron);
                     return result != null ? result : ItemInteractionHelper.tryInsertOneItem(level, pos, player, hand, cauldron, item -> true);
                  }
               }
            }
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return !level.isClientSide() && type == ModBlockEntityTypes.SMALL_CAULDRON.get()
         ? (tickLevel, tickPos, tickState, blockEntity) -> ((SmallCauldronBlockEntity)blockEntity).tick(tickLevel, tickPos, tickState)
         : null;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (level.getBlockEntity(pos) instanceof SmallCauldronBlockEntity cauldron) {
         if (cauldron.isSpoiled()) {
            spawnSpoiledEffects(level, pos, random);
         } else if ((Boolean)state.getValue(LIT) && cauldron.isCooking()) {
            spawnCookingEffects(level, pos, random);
         } else if ((Boolean)state.getValue(LIT) && cauldron.hasMixture()) {
            spawnMixtureEffects(level, pos, random, cauldron);
         } else if ((Boolean)state.getValue(LIT) && cauldron.getLiquidFill01() > 0.0F) {
            spawnLitWaterEffects(level, pos, random);
         }
      }
   }

   @Nullable
   private ItemInteractionResult tryStirWithLadle(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, SmallCauldronBlockEntity cauldron
   ) {
      if (hand != InteractionHand.MAIN_HAND || !stack.is((Item)ModItems.LADLE.get())) {
         return null;
      } else if (level.isClientSide()) {
         return cauldron.canStir(state, player) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if (!cauldron.tryStir(state, player)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         level.playSound(null, pos, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.BLOCKS, 0.9F, 1.05F);
         ((ServerLevel)level).sendParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 1.02, pos.getZ() + 0.5, 6, 0.1, 0.02, 0.1, 0.0);
         return ItemInteractionResult.CONSUME;
      }
   }

   @Nullable
   private ItemInteractionResult tryIgniteWithFireStarter(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
      if (!FireStarterHelper.isFireStarter(stack)) {
         return null;
      } else if ((Boolean)state.getValue(WATERLOGGED) || (Boolean)state.getValue(LIT)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if (level.isClientSide()) {
         return ItemInteractionResult.SUCCESS;
      } else {
         level.setBlock(pos, (BlockState)state.setValue(LIT, true), 3);
         FireStarterHelper.consumeFireStarter((ServerLevel)level, player, hand, stack);
         level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
         return ItemInteractionResult.CONSUME;
      }
   }

   public static boolean tryLightFromDispenser(ServerLevel level, BlockPos pos, BlockState state, ItemStack stack) {
      if (!(Boolean)state.getValue(WATERLOGGED) && !(Boolean)state.getValue(LIT)) {
         level.setBlock(pos, (BlockState)state.setValue(LIT, true), 3);
         FireStarterHelper.consumeFireStarter(level, null, null, stack);
         level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private ItemInteractionResult tryRusticBottle(ItemStack stack, Level level, Player player, InteractionHand hand, SmallCauldronBlockEntity cauldron) {
      if (!cauldron.isRusticBottle(stack)) {
         return null;
      } else if (level.isClientSide()) {
         return cauldron.canScoopMixtureWithRusticBottle() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         return cauldron.tryScoopBottlePublic(player, hand, stack) ? ItemInteractionResult.CONSUME : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Nullable
   private ItemInteractionResult tryLotusCleanse(ItemStack stack, Level level, Player player, InteractionHand hand, SmallCauldronBlockEntity cauldron) {
      if (!cauldron.isLotusBlossom(stack)) {
         return null;
      } else if (level.isClientSide()) {
         return cauldron.canCleanseSpoiledWithLotus() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         return cauldron.tryCleanseSpoiledPublic(player, hand, stack) ? ItemInteractionResult.CONSUME : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Nullable
   private ItemInteractionResult tryWaterContainer(ItemStack stack, Level level, Player player, InteractionHand hand, SmallCauldronBlockEntity cauldron) {
      if (!cauldron.isWaterContainer(stack)) {
         return null;
      } else if (level.isClientSide()) {
         return cauldron.canUseWaterContainer(stack) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         return cauldron.tryFillWithWaterPublic(player, hand, stack) ? ItemInteractionResult.CONSUME : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   private static void spawnLitWaterEffects(Level level, BlockPos pos, RandomSource random) {
      if (random.nextInt(2) == 0) {
         level.addParticle(
            ParticleTypes.BUBBLE_POP,
            pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.18,
            pos.getY() + 1.01,
            pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.18,
            random.nextGaussian() * 0.01,
            random.nextGaussian() * 0.03 + 0.03,
            random.nextGaussian() * 0.01
         );
      }
   }

   private static void spawnCookingEffects(Level level, BlockPos pos, RandomSource random) {
      if (random.nextInt(2) == 0) {
         level.addParticle(
            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, random.nextFloat(), random.nextFloat(), random.nextFloat()),
            pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.24,
            pos.getY() + 1.08,
            pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.24,
            0.0,
            0.03,
            0.0
         );
      }
   }

   private static void spawnMixtureEffects(Level level, BlockPos pos, RandomSource random, SmallCauldronBlockEntity cauldron) {
      if (cauldron.isOvercooked()) {
         level.addParticle(
            ParticleTypes.SMOKE,
            pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.22,
            pos.getY() + 1.06,
            pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.22,
            0.0,
            0.06,
            0.0
         );
      } else if (random.nextInt(6) == 0) {
         int rgb = cauldron.getMixtureBaseColor();
         level.addParticle(
            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, (rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F),
            pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.22,
            pos.getY() + 1.08,
            pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.22,
            0.0,
            0.02,
            0.0
         );
      }
   }

   private static void spawnSpoiledEffects(Level level, BlockPos pos, RandomSource random) {
      level.addParticle(
         ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.0F, 1.0F, 0.0F),
         pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 3.0,
         pos.getY() + 1.0 + random.nextDouble() * 0.6,
         pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 3.0,
         0.0,
         0.03,
         0.0
      );
   }
}
