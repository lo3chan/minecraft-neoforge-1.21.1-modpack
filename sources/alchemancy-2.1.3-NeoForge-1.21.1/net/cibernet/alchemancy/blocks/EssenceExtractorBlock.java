package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.blocks.blockentities.EssenceExtractorBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EssenceExtractorBlock extends BaseEntityBlock {
   public static final MapCodec<EssenceExtractorBlock> CODEC = simpleCodec(EssenceExtractorBlock::new);

   public EssenceExtractorBlock(Properties properties) {
      super(properties);
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new EssenceExtractorBlockEntity(pos, state);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      EssenceExtractorBlockEntity blockEntity = (EssenceExtractorBlockEntity)level.getBlockEntity(pos);
      player.displayClientMessage(
         Component.literal(
            "Item: " + blockEntity.getItem(0) + " Essence: [" + blockEntity.storedEssence.getEssence() + " x" + blockEntity.storedEssence.getAmount() + "]"
         ),
         true
      );
      return InteractionResult.SUCCESS;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return level.isClientSide
         ? null
         : createTickerHelper(blockEntityType, (BlockEntityType)AlchemancyBlockEntities.ESSENCE_EXTRACTOR.get(), EssenceExtractorBlockEntity::serverTick);
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }
}
