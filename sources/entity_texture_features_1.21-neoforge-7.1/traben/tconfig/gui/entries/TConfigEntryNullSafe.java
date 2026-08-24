package traben.tconfig.gui.entries;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class TConfigEntryNullSafe<E extends Enum<E>> extends TConfigEntryValue<E> {
   public TConfigEntryNullSafe(String translationKey, String tooltip, Supplier<E> getter, Consumer<E> setter, E defaultValue) {
      super(translationKey, tooltip, getter, setter, defaultValue);
   }

   @Override
   boolean hasChangedFromInitial() {
      return this.getValueFromWidget() != this.getter.get();
   }

   public abstract TConfigEntryNullSafe<E> allowNullValue();
}
