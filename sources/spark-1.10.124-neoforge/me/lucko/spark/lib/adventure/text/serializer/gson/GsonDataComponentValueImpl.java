package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.util.Objects;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.internal.Internals;
import me.lucko.spark.lib.adventure.text.event.DataComponentValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GsonDataComponentValueImpl implements GsonDataComponentValue {
   private final JsonElement element;

   GsonDataComponentValueImpl(@NotNull final JsonElement element) {
      this.element = element;
   }

   @NotNull
   @Override
   public JsonElement element() {
      return this.element;
   }

   @NotNull
   @Override
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("element", this.element));
   }

   @Override
   public String toString() {
      return Internals.toString(this);
   }

   @Override
   public boolean equals(@Nullable final Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         GsonDataComponentValueImpl that = (GsonDataComponentValueImpl)other;
         return Objects.equals(this.element, that.element);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.element);
   }

   static final class RemovedGsonComponentValueImpl extends GsonDataComponentValueImpl implements DataComponentValue.Removed {
      static final GsonDataComponentValueImpl.RemovedGsonComponentValueImpl INSTANCE = new GsonDataComponentValueImpl.RemovedGsonComponentValueImpl();

      private RemovedGsonComponentValueImpl() {
         super(JsonNull.INSTANCE);
      }
   }
}
