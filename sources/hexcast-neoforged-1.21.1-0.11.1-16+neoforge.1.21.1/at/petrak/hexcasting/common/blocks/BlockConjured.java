package at.petrak.hexcasting.common.blocks;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.blocks.entity.BlockEntityConjured;
import at.petrak.hexcasting.xplat.IForgeLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockConjured extends Block implements EntityBlock, IForgeLikeBlock {
   public BlockConjured(Properties properties) {
      super(properties);
   }

   public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
      BlockState state = super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
      pPlayer.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 1.0F, 1.0F);
      return state;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return pLevel.isClientSide() ? BlockConjured::tick : null;
   }

   private static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
      if (t instanceof BlockEntityConjured conjured) {
         conjured.particleEffect();
      }
   }

   public void stepOn(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Entity pEntity) {
      if (pLevel.getBlockEntity(pPos) instanceof BlockEntityConjured bec) {
         bec.walkParticle(pEntity);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return new BlockEntityConjured(pPos, pState);
   }

   public static void setColor(LevelAccessor pLevel, BlockPos pPos, FrozenPigment colorizer) {
      if (pLevel.getBlockEntity(pPos) instanceof BlockEntityConjured tile) {
         tile.setColorizer(colorizer);
      }
   }

   public void onPlace(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pOldState, boolean pIsMoving) {
      pLevel.sendBlockUpdated(pPos, pState, pState, 2);
      super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
   }

   public boolean propagatesSkylightDown(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
      return true;
   }

   @NotNull
   public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return Shapes.empty();
   }

   public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return 1.0F;
   }

   @NotNull
   public RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.INVISIBLE;
   }

   protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
   }

   public boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
      return this.addLandingEffects(state1, worldserver, pos, entity, numberOfParticles);
   }

   @Override
   public boolean addLandingEffects(BlockState state, ServerLevel worldserver, BlockPos pos, LivingEntity entity, int numberOfParticles) {
      if (worldserver.getBlockEntity(pos) instanceof BlockEntityConjured bec) {
         bec.landParticle(entity, numberOfParticles);
      }

      return true;
   }
}
