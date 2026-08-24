package net.Pandarix.block.custom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SheepFossilBlock extends FossilBaseBlock {
   public static final BooleanProperty PLAYING = BooleanProperty.create("playing");
   private static final int playCooldown = 80;
   public static final IntegerProperty HORN_SOUND = IntegerProperty.create("horn_sound", 0, 7);
   private static final Map<Direction, VoxelShape> SHEEP_SHAPES_FOR_DIRECTION = ImmutableMap.of(
      Direction.NORTH,
      Shapes.or(
         Block.box(4.0, 0.0, 4.0, 12.0, 17.75, 19.0),
         new VoxelShape[]{Block.box(4.0, 9.0, 0.0, 12.0, 17.75, 4.0), Block.box(3.75, 14.0, -7.5, 12.0, 25.0, 5.0)}
      ),
      Direction.SOUTH,
      Shapes.or(
         Block.box(4.0, 0.0, -3.0, 12.0, 17.75, 12.0),
         new VoxelShape[]{Block.box(4.0, 9.0, 12.0, 12.0, 17.75, 16.0), Block.box(4.0, 14.0, 11.0, 12.25, 25.0, 23.5)}
      ),
      Direction.EAST,
      Shapes.or(
         Block.box(-3.0, 0.0, 4.0, 12.0, 17.75, 12.0),
         new VoxelShape[]{Block.box(12.0, 9.0, 4.0, 16.0, 17.75, 12.0), Block.box(11.0, 14.0, 3.75, 23.5, 25.0, 12.0)}
      ),
      Direction.WEST,
      Shapes.or(
         Block.box(4.0, 0.0, 4.0, 19.0, 17.75, 12.0),
         new VoxelShape[]{Block.box(0.0, 9.0, 4.0, 4.0, 17.75, 12.0), Block.box(-7.5, 14.0, 4.0, 5.0, 25.0, 12.25)}
      )
   );

   public SheepFossilBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(HORN_SOUND, 0)).setValue(PLAYING, false));
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHEEP_SHAPES_FOR_DIRECTION.get(pState.getValue(FACING));
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
      boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
      boolean playing = (Boolean)state.getValue(PLAYING);
      if (powered && !playing) {
         if (!level.isClientSide()) {
            level.playSound(
               null, pos, (SoundEvent)((Reference)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get((Integer)state.getValue(HORN_SOUND))).value(), SoundSource.BLOCKS
            );
         }

         level.setBlock(pos, (BlockState)state.setValue(PLAYING, true), 3);
         level.scheduleTick(pos, this, 80);
      }
   }

   @NotNull
   public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
      if ((Boolean)pState.getValue(PLAYING)) {
         return InteractionResult.FAIL;
      } else {
         if (!pLevel.isClientSide()) {
            if ((Integer)pState.getValue(HORN_SOUND) + 1 <= 7) {
               pLevel.setBlock(pPos, (BlockState)((BlockState)pState.setValue(HORN_SOUND, (Integer)pState.getValue(HORN_SOUND) + 1)).setValue(PLAYING, true), 3);
            } else {
               pLevel.setBlock(pPos, (BlockState)((BlockState)pState.setValue(HORN_SOUND, 0)).setValue(PLAYING, true), 3);
            }

            pLevel.playSound(
               null,
               pPos,
               (SoundEvent)((Reference)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get((Integer)pLevel.getBlockState(pPos).getValue(HORN_SOUND))).value(),
               SoundSource.BLOCKS
            );
            pLevel.scheduleTick(pPos, this, 80);
         } else {
            pLevel.addParticle(ParticleTypes.NOTE, pPos.getX() + 0.5, pPos.getY() + 1.5, pPos.getZ() + 0.5, 0.0, 0.2, 0.0);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      super.tick(pState, pLevel, pPos, pRandom);
      pLevel.setBlock(pPos, (BlockState)pState.setValue(PLAYING, false), 3);
   }

   @Override
   protected void createBlockStateDefinition(@NotNull Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{HORN_SOUND, PLAYING});
   }

   public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
      pTooltipComponents.add(Component.translatable("block.betterarcheology.sheep_fossil_tooltip").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
   }
}
