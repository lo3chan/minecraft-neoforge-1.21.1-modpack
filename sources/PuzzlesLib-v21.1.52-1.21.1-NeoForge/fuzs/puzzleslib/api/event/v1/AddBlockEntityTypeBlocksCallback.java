package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.function.BiConsumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@FunctionalInterface
public interface AddBlockEntityTypeBlocksCallback {
   EventInvoker<AddBlockEntityTypeBlocksCallback> EVENT = EventInvoker.lookup(AddBlockEntityTypeBlocksCallback.class);

   void onAddBlockEntityTypeBlocks(BiConsumer<BlockEntityType<?>, Block> var1);
}
