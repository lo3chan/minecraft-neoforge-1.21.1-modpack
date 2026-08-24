package codx.codxlib.api.ui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;

public final class CodxMenuButton {
   final Supplier<ItemStack> icon;
   final Consumer<CodxMenuClick> onClick;

   private CodxMenuButton(Supplier<ItemStack> icon, Consumer<CodxMenuClick> onClick) {
      this.icon = icon;
      this.onClick = onClick;
   }

   public static CodxMenuButton of(Supplier<ItemStack> icon, Consumer<CodxMenuClick> onClick) {
      return new CodxMenuButton(icon, onClick);
   }

   public static CodxMenuButton info(Item item, String name, String... lore) {
      return new CodxMenuButton(() -> makeInfo(item, name, lore), null);
   }

   public static CodxMenuButton toggle(Item item, String label, BooleanSupplier get, Consumer<Boolean> set, String... lore) {
      return new CodxMenuButton(() -> makeToggle(item, label, get.getAsBoolean(), lore), click -> {
         set.accept(!get.getAsBoolean());
         click.markChanged();
      });
   }

   public static CodxMenuButton adjustInt(Item item, String label, IntSupplier get, IntConsumer set, CodxMenuButton.Step step, String... lore) {
      return adjust(item, label, get, set, step, false, lore);
   }

   public static CodxMenuButton slider(Item item, String label, IntSupplier get, IntConsumer set, CodxMenuButton.Step step, String... lore) {
      return adjust(item, label, get, set, step, true, lore);
   }

   public static CodxMenuButton adjustDouble(Item item, String label, DoubleSupplier get, DoubleConsumer set, CodxMenuButton.DoubleStep step, String... lore) {
      return new CodxMenuButton(() -> makeAdjustDouble(item, label, get.getAsDouble(), step, lore), click -> {
         double delta = step.delta(click.rightClick(), click.shift());
         set.accept(step.clamp(get.getAsDouble() + delta));
         click.markChanged();
      });
   }

   public static CodxMenuButton cycle(Item item, String label, Supplier<String> get, Runnable next, String... lore) {
      return new CodxMenuButton(() -> makeCycle(item, label, get.get(), lore), click -> {
         next.run();
         click.markChanged();
      });
   }

   public static CodxMenuButton action(Item item, String name, Consumer<CodxMenuClick> onClick, String... lore) {
      return new CodxMenuButton(() -> makeInfo(item, name, lore), onClick);
   }

   private static CodxMenuButton adjust(Item item, String label, IntSupplier get, IntConsumer set, CodxMenuButton.Step step, boolean asSlider, String... lore) {
      return new CodxMenuButton(() -> makeAdjust(item, label, get.getAsInt(), step, asSlider, lore), click -> {
         int delta = step.delta(click.rightClick(), click.shift());
         set.accept(step.clamp(get.getAsInt() + delta));
         click.markChanged();
      });
   }

   private static ItemStack makeInfo(Item item, String name, String[] lore) {
      ItemStack stack = new ItemStack(item);
      setName(stack, lit(name));
      if (lore.length > 0) {
         setLore(stack, lines(lore));
      }

      hideAttrs(stack);
      return stack;
   }

   private static ItemStack makeToggle(Item item, String label, boolean on, String[] lore) {
      ItemStack stack = new ItemStack(item);
      setName(stack, lit((on ? "§a" : "§c") + label + ": " + (on ? "ON" : "OFF")));
      List<Component> l = new ArrayList<>();
      l.add(lit(on ? "§7Status: §aEnabled" : "§7Status: §cDisabled"));
      if (lore.length > 0) {
         l.add(lit(""));
      }

      for (String line : lore) {
         l.add(lit(line));
      }

      l.add(lit(""));
      l.add(lit("§7Click to toggle"));
      setLore(stack, l);
      hideAttrs(stack);
      if (on) {
         setGlint(stack);
      }

      return stack;
   }

   private static ItemStack makeAdjust(Item item, String label, int value, CodxMenuButton.Step step, boolean asSlider, String[] lore) {
      ItemStack stack = new ItemStack(item);
      setName(stack, lit("§b" + label));
      List<Component> l = new ArrayList<>();
      l.add(lit("§7Current: §e" + step.format.apply(value)));
      if (lore.length > 0) {
         l.add(lit(""));
      }

      for (String line : lore) {
         l.add(lit(line));
      }

      l.add(lit(""));
      l.add(lit("§7Left: §c-" + step.step + " §7| Right: §a+" + step.step));
      if (step.shiftStep != step.step) {
         l.add(lit("§7Shift+Left: §c-" + step.shiftStep + " §7| Shift+Right: §a+" + step.shiftStep));
      }

      setLore(stack, l);
      hideAttrs(stack);
      if (asSlider) {
         stack.setCount(Math.max(1, Math.min(64, value)));
      }

      return stack;
   }

   private static ItemStack makeAdjustDouble(Item item, String label, double value, CodxMenuButton.DoubleStep step, String[] lore) {
      ItemStack stack = new ItemStack(item);
      setName(stack, lit("§b" + label));
      List<Component> l = new ArrayList<>();
      l.add(lit("§7Current: §e" + step.format.apply(value)));
      if (lore.length > 0) {
         l.add(lit(""));
      }

      for (String line : lore) {
         l.add(lit(line));
      }

      l.add(lit(""));
      l.add(lit("§7Left: §c-" + step.text(step.step) + " §7| Right: §a+" + step.text(step.step)));
      if (step.shiftStep != step.step) {
         l.add(lit("§7Shift+Left: §c-" + step.text(step.shiftStep) + " §7| Shift+Right: §a+" + step.text(step.shiftStep)));
      }

      setLore(stack, l);
      hideAttrs(stack);
      return stack;
   }

   private static ItemStack makeCycle(Item item, String label, String current, String[] lore) {
      ItemStack stack = new ItemStack(item);
      setName(stack, lit("§b" + label + ": §e" + current));
      List<Component> l = new ArrayList<>();

      for (String line : lore) {
         l.add(lit(line));
      }

      if (lore.length > 0) {
         l.add(lit(""));
      }

      l.add(lit("§7Click to change"));
      setLore(stack, l);
      hideAttrs(stack);
      return stack;
   }

   private static List<Component> lines(String[] lore) {
      List<Component> l = new ArrayList<>(lore.length);

      for (String line : lore) {
         l.add(lit(line));
      }

      return l;
   }

   private static Component lit(String text) {
      return Component.literal(text);
   }

   private static void setName(ItemStack s, Component name) {
      s.set(DataComponents.CUSTOM_NAME, name);
   }

   private static void setLore(ItemStack s, List<Component> lore) {
      s.set(DataComponents.LORE, new ItemLore(lore));
   }

   private static void hideAttrs(ItemStack s) {
      s.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
   }

   private static void setGlint(ItemStack s) {
      s.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
   }

   public static final class DoubleStep {
      final double step;
      final double shiftStep;
      double min = -1.7976931348623157E308;
      double max = 1.7976931348623157E308;
      DoubleFunction<String> format = CodxMenuButton.DoubleStep::plain;

      private DoubleStep(double step, double shiftStep) {
         this.step = step;
         this.shiftStep = shiftStep;
      }

      public static CodxMenuButton.DoubleStep of(double step, double shiftStep) {
         return new CodxMenuButton.DoubleStep(step, shiftStep);
      }

      public static CodxMenuButton.DoubleStep of(double step) {
         return new CodxMenuButton.DoubleStep(step, step);
      }

      public CodxMenuButton.DoubleStep min(double min) {
         this.min = min;
         return this;
      }

      public CodxMenuButton.DoubleStep max(double max) {
         this.max = max;
         return this;
      }

      public CodxMenuButton.DoubleStep range(double lo, double hi) {
         this.min = lo;
         this.max = hi;
         return this;
      }

      public CodxMenuButton.DoubleStep format(DoubleFunction<String> format) {
         this.format = format;
         return this;
      }

      double delta(boolean right, boolean shift) {
         double mag = shift ? this.shiftStep : this.step;
         return right ? mag : -mag;
      }

      double clamp(double value) {
         return Math.max(this.min, Math.min(this.max, value));
      }

      String text(double value) {
         return plain(value);
      }

      private static String plain(double value) {
         if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return String.valueOf((long)value);
         } else {
            String formatted = String.format(Locale.ROOT, "%.4f", value);
            formatted = formatted.replaceAll("0+$", "");
            return formatted.endsWith(".") ? formatted.substring(0, formatted.length() - 1) : formatted;
         }
      }
   }

   public static final class Step {
      final int step;
      final int shiftStep;
      int min = -2147483648;
      int max = 2147483647;
      IntFunction<String> format = Integer::toString;

      private Step(int step, int shiftStep) {
         this.step = step;
         this.shiftStep = shiftStep;
      }

      public static CodxMenuButton.Step of(int step, int shiftStep) {
         return new CodxMenuButton.Step(step, shiftStep);
      }

      public static CodxMenuButton.Step of(int step) {
         return new CodxMenuButton.Step(step, step);
      }

      public CodxMenuButton.Step min(int min) {
         this.min = min;
         return this;
      }

      public CodxMenuButton.Step max(int max) {
         this.max = max;
         return this;
      }

      public CodxMenuButton.Step range(int lo, int hi) {
         this.min = lo;
         this.max = hi;
         return this;
      }

      public CodxMenuButton.Step format(IntFunction<String> format) {
         this.format = format;
         return this;
      }

      int delta(boolean right, boolean shift) {
         int mag = shift ? this.shiftStep : this.step;
         return right ? mag : -mag;
      }

      int clamp(int value) {
         return Math.max(this.min, Math.min(this.max, value));
      }
   }
}
