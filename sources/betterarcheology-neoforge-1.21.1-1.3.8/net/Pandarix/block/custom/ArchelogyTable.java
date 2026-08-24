package net.Pandarix.block.custom;

import com.mojang.serialization.MapCodec;
import net.Pandarix.block.entity.ArcheologyTableBlockEntity;
import net.Pandarix.block.entity.ModBlockEntities;
import net.Pandarix.item.BetterBrushItem;
import net.Pandarix.util.ServerPlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArchelogyTable extends BaseEntityBlock {
   public static final MapCodec<ArchelogyTable> CODEC = simpleCodec(ArchelogyTable::new);
   public static final BooleanProperty DUSTING = BooleanProperty.create("dusting");

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public ArchelogyTable(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(DUSTING, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{DUSTING});
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
      if (state.getBlock() != newState.getBlock() && level.getBlockEntity(pos) instanceof ArcheologyTableBlockEntity archeologyTableBlockEntity) {
         Containers.dropContents(level, pos, archeologyTableBlockEntity);
         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, moved);
   }

   @NotNull
   public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
      if (!pLevel.isClientSide()) {
         BlockEntity entity = pLevel.getBlockEntity(pPos);
         if (!(entity instanceof ArcheologyTableBlockEntity)) {
            throw new IllegalStateException("Container Provider Missing!");
         }

         ServerPlayerHelper.tryOpenScreen(pPlayer, (ArcheologyTableBlockEntity)entity);
      }

      return InteractionResult.sidedSuccess(pLevel.isClientSide());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ArcheologyTableBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return createTickerHelper(pBlockEntityType, (BlockEntityType)ModBlockEntities.ARCHEOLOGY_TABLE.get(), ArcheologyTableBlockEntity::tick);
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
      if (pLevel.isClientSide() && (Boolean)pState.getValue(DUSTING)) {
         this.addDustParticles(pLevel, pPos, pRandom);
      }

      super.animateTick(pState, pLevel, pPos, pRandom);
   }

   public void addDustParticles(Level pLevel, BlockPos pos, RandomSource random) {
      ArcheologyTableBlockEntity entity = (ArcheologyTableBlockEntity)pLevel.getBlockEntity(pos);
      if (entity != null) {
         ItemStack brush = (ItemStack)entity.getItems().getFirst();
         int brushSpeed = brush.getItem() instanceof BetterBrushItem brushItem ? brushItem.getBrushingSpeed() : 10;
         if (entity.getProgress() % brushSpeed == 0) {
            pLevel.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 0.25F, 1.0F);
         }

         int i = random.nextIntBetweenInclusive(1, 3);
         BlockParticleOption blockStateParticleEffect = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState());

         for (int j = 0; j < i; j++) {
            pLevel.addParticle(
               blockStateParticleEffect,
               pos.getX() + 0.5,
               pos.getY() + 1,
               pos.getZ() + 0.5,
               3.0 * random.nextDouble() * (random.nextBoolean() ? 1 : -1),
               0.0,
               3.0 * random.nextDouble() * (random.nextBoolean() ? 1 : -1)
            );
         }
      }
   }
}
