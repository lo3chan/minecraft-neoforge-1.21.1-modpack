package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ColorProvidersContext;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.BlockColorsNeoForgeAccessor;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public record BlockColorProvidersContextNeoForgeImpl(BiConsumer<BlockColor, Block> consumer, BlockColors blockColors)
   implements ColorProvidersContext<Block, BlockColor> {
   public void registerColorProvider(BlockColor provider, Block... blocks) {
      Objects.requireNonNull(provider, "provider is null");
      Objects.requireNonNull(blocks, "blocks is null");
      Preconditions.checkState(blocks.length > 0, "blocks is empty");

      for (Block block : blocks) {
         Objects.requireNonNull(block, "block is null");
         this.consumer.accept(provider, block);
      }
   }

   @Nullable
   public BlockColor getProvider(Block block) {
      return ((BlockColorsNeoForgeAccessor)this.blockColors).puzzleslib$getBlockColors().get(block);
   }
}
