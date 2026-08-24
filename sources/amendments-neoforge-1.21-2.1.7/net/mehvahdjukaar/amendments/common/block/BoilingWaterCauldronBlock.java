package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.moonlight.api.fluids.MLBuiltinSoftFluids;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteraction.InteractionMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class BoilingWaterCauldronBlock extends LayeredCauldronBlock {
   public static final MapCodec<BoilingWaterCauldronBlock> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Precipitation.CODEC.fieldOf("precipitation").forGetter(c -> c.precipitationType),
            CauldronInteraction.CODEC.fieldOf("interactions").forGetter(c -> c.interactions),
            propertiesCodec()
         )
         .apply(i, BoilingWaterCauldronBlock::new)
   );
   public static final BooleanProperty BOILING = ModBlockProperties.BOILING;
   private final Precipitation precipitationType;

   public BoilingWaterCauldronBlock(Precipitation precipitationType, InteractionMap interactions, Properties properties) {
      super(precipitationType, interactions, properties);
      this.precipitationType = precipitationType;
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(BOILING, false));
   }

   public MapCodec codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{BOILING});
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      super.entityInside(state, level, pos, entity);
      CommonCauldronCode.entityInside(state, level, pos, entity, () -> this.getContentHeight(state));
   }

   public BlockState updateShape(
      BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos
   ) {
      BlockState newState = super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
      return CommonCauldronCode.updateBoilingState(direction, neighborState, level, neighborPos, newState, currentPos);
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
      if (!level.isClientSide) {
         BlockPos below = pos.below();
         boolean shouldBoil = CommonCauldronCode.shouldBoil(
            level.getBlockState(below), SoftFluidStack.of(MLBuiltinSoftFluids.WATER.getHolder(level), (Integer)state.getValue(LEVEL)), level, below
         );
         if ((Boolean)state.getValue(BOILING) != shouldBoil) {
            level.setBlock(pos, (BlockState)state.setValue(BOILING, shouldBoil), 3);
         }
      }
   }

   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
      if (this.isEntityInsideContent(state, pos, entity)) {
         CommonCauldronCode.onEntityFallOnContent(level, state, entity, this.getContentHeight(state));
         super.fallOn(level, state, pos, entity, 0.0F);
      } else {
         super.fallOn(level, state, pos, entity, fallDistance);
      }
   }

   public static int getWaterColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int i) {
      return i == 1 && level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      super.animateTick(state, level, pos, random);
      if ((Boolean)state.getValue(BOILING)) {
         CommonCauldronCode.playBubblingAnimation(level, pos, this.getContentHeight(state), random, getWaterColor(state, level, pos, 1), 0);
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      return CommonCauldronCode.attemptPlayerCrafting(
            state, level, pos, player, hand, 3, SoftFluidStack.of(MLBuiltinSoftFluids.WATER.getHolder(level), (Integer)state.getValue(LEVEL))
         )
         ? ItemInteractionResult.sidedSuccess(level.isClientSide)
         : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }
}
