package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import java.util.List;
import java.util.function.Supplier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public record CustomCapabilityAttachment(BlockCapability<?, ?> capability, Object data) implements BlockEntityAttachment {
   public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("custom_capability"), CustomCapabilityAttachment.Factory.class);

   @Override
   public Object getWrappedObject() {
      return this.data;
   }

   @Nullable
   @Override
   public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> c) {
      return (CAP)(c == this.capability ? this.data : null);
   }

   public record Factory(BlockCapability<?, ?> type, Supplier<?> dataFactory) implements BlockEntityAttachmentFactory {
      @Override
      public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
         return new CustomCapabilityAttachment(this.type, this.dataFactory.get());
      }

      @Override
      public List<BlockCapability<?, ?>> getCapabilities() {
         return List.of(this.type);
      }
   }
}
