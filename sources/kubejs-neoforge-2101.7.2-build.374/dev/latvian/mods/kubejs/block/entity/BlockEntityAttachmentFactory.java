package dev.latvian.mods.kubejs.block.entity;

import java.util.List;
import net.neoforged.neoforge.capabilities.BlockCapability;

public interface BlockEntityAttachmentFactory {
   BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity);

   default List<BlockCapability<?, ?>> getCapabilities() {
      return List.of();
   }

   default boolean isTicking() {
      return false;
   }
}
