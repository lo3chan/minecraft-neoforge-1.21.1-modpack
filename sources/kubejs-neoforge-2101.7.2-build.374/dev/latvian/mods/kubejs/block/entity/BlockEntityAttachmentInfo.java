package dev.latvian.mods.kubejs.block.entity;

import java.util.EnumSet;
import net.minecraft.core.Direction;

public record BlockEntityAttachmentInfo(
   String id, BlockEntityAttachmentType type, int index, EnumSet<Direction> directions, BlockEntityAttachmentFactory factory
) {
   @Override
   public String toString() {
      return this.id;
   }
}
