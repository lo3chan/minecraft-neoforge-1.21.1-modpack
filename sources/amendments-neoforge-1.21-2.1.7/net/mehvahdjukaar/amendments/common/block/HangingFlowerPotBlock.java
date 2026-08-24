package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.List;
import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.common.FlowerPotHandler;
import net.mehvahdjukaar.amendments.common.tile.HangingFlowerPotBlockTile;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HangingFlowerPotBlock extends Block implements EntityBlock {
   public static final MapCodec<HangingFlowerPotBlock> CODEC = simpleCodec(HangingFlowerPotBlock::new);
   protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
   public static final IntegerProperty LIGHT_LEVEL = ModBlockProperties.LIGHT_LEVEL;

   public HangingFlowerPotBlock(Properties properties) {
      super(properties.lightLevel(state -> (Integer)state.getValue(LIGHT_LEVEL)));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIGHT_LEVEL, 0));
   }

   protected MapCodec<? extends HangingFlowerPotBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LIGHT_LEVEL});
   }

   public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
      Item i = stack.getItem();
      if (world.getBlockEntity(pos) instanceof HangingFlowerPotBlockTile tile && i instanceof BlockItem blockItem) {
         BlockState mimic = blockItem.getBlock().defaultBlockState();
         tile.setHeldBlock(mimic);
      }
   }

   public MutableComponent getName() {
      return Component.translatable("block.minecraft.flower_pot");
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return context.getClickedFace() == Direction.DOWN ? super.getStateForPlacement(context) : null;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.getBlockEntity(pos) instanceof HangingFlowerPotBlockTile tile) {
         Block pot = tile.getHeldBlock().getBlock();
         if (pot instanceof FlowerPotBlock flowerPot) {
            ItemStack itemstack = player.getItemInHand(hand);
            Block newPot = itemstack.getItem() instanceof BlockItem bi ? FlowerPotHandler.getFullPot(flowerPot, bi.getBlock()) : Blocks.AIR;
            boolean isEmptyFlower = newPot == Blocks.AIR;
            boolean isPotEmpty = FlowerPotHandler.isEmptyPot(pot);
            if (isEmptyFlower != isPotEmpty) {
               if (isPotEmpty) {
                  if (!level.isClientSide) {
                     tile.setHeldBlock(newPot.defaultBlockState());
                     level.sendBlockUpdated(pos, state, state, 2);
                     tile.setChanged();
                  }

                  playPlantSound(level, pos, player);
                  player.awardStat(Stats.POT_FLOWER);
                  itemstack.consume(1, player);
               } else {
                  ItemStack flowerItem = pot.getCloneItemStack(level, pos, state);
                  if (!flowerItem.equals(new ItemStack(this))) {
                     if (itemstack.isEmpty()) {
                        player.setItemInHand(hand, flowerItem);
                     } else if (!player.addItem(flowerItem)) {
                        player.drop(flowerItem, false);
                     }
                  }

                  if (!level.isClientSide) {
                     tile.setHeldBlock(FlowerPotHandler.getEmptyPot(flowerPot).defaultBlockState());
                     level.sendBlockUpdated(pos, state, state, 2);
                     tile.setChanged();
                  }
               }

               level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
               return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            return ItemInteractionResult.CONSUME;
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public static void playPlantSound(Level level, BlockPos pos, Player player) {
      level.playSound(player, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.95F);
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new HangingFlowerPotBlockTile(pPos, pState);
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      if (level.getBlockEntity(pos) instanceof HangingFlowerPotBlockTile te && te.getHeldBlock().getBlock() instanceof FlowerPotBlock b) {
         Block flower = b.getPotted();
         return flower == Blocks.AIR ? new ItemStack(Blocks.FLOWER_POT, 1) : new ItemStack(flower);
      } else {
         return new ItemStack(Blocks.FLOWER_POT, 1);
      }
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof HangingFlowerPotBlockTile tile
            && tile.getHeldBlock().getBlock() instanceof FlowerPotBlock flowerPotBlock
         ? Arrays.asList(new ItemStack(flowerPotBlock.getPotted()), new ItemStack(Items.FLOWER_POT))
         : super.getDrops(state, builder);
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return Shapes.block();
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == Direction.UP && !this.canSurvive(stateIn, worldIn, currentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      return Amendments.isSupportingCeiling(pos.relative(Direction.UP), worldIn);
   }
}
