package me.lucko.spark.lib.adventure.text.event;

import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.nbt.api.BinaryTagHolder;
import org.jetbrains.annotations.NotNull;

public interface DataComponentValue extends Examinable {
   @NotNull
   static DataComponentValue.Removed removed() {
      return RemovedDataComponentValueImpl.REMOVED;
   }

   public interface Removed extends DataComponentValue {
   }

   public interface TagSerializable extends DataComponentValue {
      @NotNull
      BinaryTagHolder asBinaryTag();
   }
}
