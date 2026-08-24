package net.mehvahdjukaar.amendments.common.block;

import java.util.Map;
import net.mehvahdjukaar.amendments.common.tile.LiquidCauldronBlockTile;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction.InteractionMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class ModCauldronBlock extends AbstractCauldronBlock implements EntityBlock {
   public static final BooleanProperty BOILING = ModBlockProperties.BOILING;
   private final int maxLevel;

   public ModCauldronBlock(Properties properties) {
      super(properties, new InteractionMap("amendments_empty", Map.of()));
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(BOILING, false));
      this.maxLevel = this.getLevelProperty().getPossibleValues().size();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{BOILING});
   }

   public boolean isFull(BlockState state) {
      return (Integer)state.getValue(this.getLevelProperty()) == this.maxLevel;
   }

   public BlockState updateShape(
      BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos
   ) {
      BlockState newState = super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
      return CommonCauldronCode.updateBoilingState(direction, neighborState, level, neighborPos, newState, currentPos);
   }

   public Item asItem() {
      return Items.CAULDRON;
   }

   public abstract IntegerProperty getLevelProperty();

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      return (Integer)state.getValue(this.getLevelProperty());
   }

   public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Precipitation precipitation) {
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new LiquidCauldronBlockTile(pos, state);
   }

   protected double getContentHeight(BlockState state) {
      double start = 0.5625;
      double end = 0.9375;
      IntegerProperty levelProperty = this.getLevelProperty();
      int level = (Integer)state.getValue(levelProperty);
      return this.maxLevel <= 1 ? start : start + (end - start) * (level - 1) / (this.maxLevel - 1);
   }

   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
      if (this.isEntityInsideContent(state, pos, entity)) {
         CommonCauldronCode.onEntityFallOnContent(level, state, entity, this.getContentHeight(state));
         super.fallOn(level, state, pos, entity, 0.0F);
      } else {
         super.fallOn(level, state, pos, entity, fallDistance);
      }
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (this.isEntityInsideContent(state, pos, entity)) {
         CommonCauldronCode.entityInside(state, level, pos, entity, () -> this.getContentHeight(state));
         this.handleEntityInsideFluidSpecial(state, level, pos, entity);
      }
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      super.animateTick(state, level, pos, random);
      if ((Boolean)state.getValue(BOILING) && level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
         SoftFluidTank tank = te.getSoftFluidTank();
         int color = tank.getCachedParticleColor(level, pos);
         int light = tank.getFluidValue().getEmissivity();
         CommonCauldronCode.playBubblingAnimation(level, pos, this.getContentHeight(state), random, color, light);
      }
   }

   protected abstract void handleEntityInsideFluidSpecial(BlockState var1, Level var2, BlockPos var3, Entity var4);

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
         if (te.interactWithPlayerItem(player, hand, stack)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }

         SoftFluidTank tank = te.getSoftFluidTank();
         int tankCapacity = tank.getCapacity();
         SoftFluidStack currentFluid = tank.getFluid();
         if (CommonCauldronCode.attemptPlayerCrafting(state, level, pos, player, hand, tankCapacity, currentFluid)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public BlockState updateStateOnFluidChange(BlockState state, Level level, BlockPos pos, SoftFluidStack fluid) {
      int height = fluid.getCount();
      if (fluid.isEmpty()) {
         state = Blocks.CAULDRON.defaultBlockState();
      } else {
         BlockPos below = pos.below();
         state = (BlockState)((BlockState)state.setValue(this.getLevelProperty(), height))
            .setValue(BOILING, CommonCauldronCode.shouldBoil(level.getBlockState(below), fluid, level, below));
      }

      return state;
   }
}
