package mezz.jei.common.transfer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public record TransferOperation(int inventorySlotId, int craftingSlotId, int count) {
   public static final StreamCodec<ByteBuf, TransferOperation> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, p -> p.inventorySlotId, ByteBufCodecs.VAR_INT, p -> p.craftingSlotId, TransferOperation::new
   );
   public static final StreamCodec<ByteBuf, TransferOperation> COUNTED_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, p -> p.inventorySlotId, ByteBufCodecs.VAR_INT, p -> p.craftingSlotId, ByteBufCodecs.VAR_INT, p -> p.count, TransferOperation::new
   );

   public TransferOperation(int inventorySlotId, int craftingSlotId) {
      this(inventorySlotId, craftingSlotId, 1);
   }

   public TransferOperation(int inventorySlotId, int craftingSlotId, int count) {
      if (count < 1) {
         throw new IllegalArgumentException("Transfer operation count must be positive");
      } else {
         this.inventorySlotId = inventorySlotId;
         this.craftingSlotId = craftingSlotId;
         this.count = count;
      }
   }

   public Slot inventorySlot(AbstractContainerMenu container) {
      return container.getSlot(this.inventorySlotId);
   }

   public Slot craftingSlot(AbstractContainerMenu container) {
      return container.getSlot(this.craftingSlotId);
   }
}
