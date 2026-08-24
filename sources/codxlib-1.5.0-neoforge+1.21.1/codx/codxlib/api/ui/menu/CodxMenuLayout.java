package codx.codxlib.api.ui.menu;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;

public final class CodxMenuLayout {
   private final Map<Integer, CodxMenuButton> buttons = new LinkedHashMap<>();

   Map<Integer, CodxMenuButton> buttons() {
      return this.buttons;
   }

   int maxSlot() {
      int max = -1;

      for (int slot : this.buttons.keySet()) {
         max = Math.max(max, slot);
      }

      return max;
   }

   public CodxMenuLayout put(int slot, CodxMenuButton button) {
      this.buttons.put(slot, button);
      return this;
   }

   public CodxMenuLayout info(int slot, Item item, String name, String... lore) {
      return this.put(slot, CodxMenuButton.info(item, name, lore));
   }

   public CodxMenuLayout toggle(int slot, Item item, String label, BooleanSupplier get, Consumer<Boolean> set, String... lore) {
      return this.put(slot, CodxMenuButton.toggle(item, label, get, set, lore));
   }

   public CodxMenuLayout adjustInt(int slot, Item item, String label, IntSupplier get, IntConsumer set, CodxMenuButton.Step step, String... lore) {
      return this.put(slot, CodxMenuButton.adjustInt(item, label, get, set, step, lore));
   }

   public CodxMenuLayout slider(int slot, Item item, String label, IntSupplier get, IntConsumer set, CodxMenuButton.Step step, String... lore) {
      return this.put(slot, CodxMenuButton.slider(item, label, get, set, step, lore));
   }

   public CodxMenuLayout adjustDouble(int slot, Item item, String label, DoubleSupplier get, DoubleConsumer set, CodxMenuButton.DoubleStep step, String... lore) {
      return this.put(slot, CodxMenuButton.adjustDouble(item, label, get, set, step, lore));
   }

   public CodxMenuLayout cycle(int slot, Item item, String label, Supplier<String> get, Runnable next, String... lore) {
      return this.put(slot, CodxMenuButton.cycle(item, label, get, next, lore));
   }

   public CodxMenuLayout action(int slot, Item item, String name, Consumer<CodxMenuClick> onClick, String... lore) {
      return this.put(slot, CodxMenuButton.action(item, name, onClick, lore));
   }
}
