package codx.codxlib.api.ui.menu;

import codx.codxlib.api.settings.CodxSettings;
import codx.codxlib.api.settings.CodxSettingsCommand;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class CodxSettingsMenu {
   private static final int PAGE_CAPACITY = 28;
   private static final int SLOT_BACK = 45;

   private CodxSettingsMenu() {
   }

   public static void open(ServerPlayer player, CodxSettings spec) {
      builder(spec).open(player);
   }

   public static void open(ServerPlayer player, CodxSettings spec, CodxSettingsCommand.ChangeNote note) {
      builder(spec).note(note).open(player);
   }

   public static CodxSettingsMenu.Builder builder(CodxSettings spec) {
      return new CodxSettingsMenu.Builder(spec);
   }

   private static void openRoot(ServerPlayer player, CodxSettingsMenu.Builder cfg) {
      List<String> categories = new ArrayList<>(cfg.spec.categories());
      PagedMenuBuilder menu = CodxMenu.paged(cfg.title()).onChange(cfg.spec::apply).resetButton(click -> {
         cfg.spec.resetAll();
         cfg.spec.apply();
         openRoot(click.player(), cfg);
      });
      paginate(
         menu,
         "Categories",
         categories,
         (layout, slot, category) -> {
            int size = cfg.spec.category(category).size();
            int changed = (int)cfg.spec.category(category).stream().filter(v -> !v.isDefault()).count();
            layout.action(
               slot,
               cfg.iconOr(category, Items.BOOK),
               "§e§l" + category,
               click -> openCategory(click.player(), cfg, category),
               "§7" + size + " setting(s)",
               changed == 0 ? "§8all at their defaults" : "§a" + changed + " changed",
               "",
               "§7Click to open"
            );
         }
      );
      menu.open(player);
   }

   private static void openCategory(ServerPlayer player, CodxSettingsMenu.Builder cfg, String category) {
      List<CodxSettings.ConfigValue<?>> values = cfg.spec.category(category);
      Function<CodxSettings.ConfigValue<?>, String> grouper = cfg.groupers.get(category);
      PagedMenuBuilder menu = CodxMenu.paged(cfg.title() + " §7— " + category).onChange(cfg.spec::apply);
      if (grouper == null) {
         paginate(menu, category, values, (layout, slot, valuex) -> widget(layout, slot, cfg, valuex));
         back(menu, click -> openRoot(click.player(), cfg));
         menu.open(player);
      } else {
         Map<String, List<CodxSettings.ConfigValue<?>>> groups = new LinkedHashMap<>();
         List<CodxSettings.ConfigValue<?>> loose = new ArrayList<>();

         for (CodxSettings.ConfigValue<?> value : values) {
            String group = grouper.apply(value);
            if (group != null && !group.isEmpty()) {
               groups.computeIfAbsent(group, k -> new ArrayList<>()).add(value);
            } else {
               loose.add(value);
            }
         }

         List<Object> entries = new ArrayList<>(groups.keySet());
         entries.addAll(loose);
         paginate(
            menu,
            category,
            entries,
            (layout, slot, entry) -> {
               if (entry instanceof CodxSettings.ConfigValue<?> valuex) {
                  widget(layout, slot, cfg, valuex);
               } else {
                  String groupx = (String)entry;
                  List<CodxSettings.ConfigValue<?>> members = groups.get(groupx);
                  int changed = (int)members.stream().filter(v -> !v.isDefault()).count();
                  layout.action(
                     slot,
                     cfg.iconOr(groupx, Items.CHEST),
                     "§e" + prettify(groupx),
                     click -> openGroup(click.player(), cfg, category, groupx, members),
                     "§7" + members.size() + " setting(s)",
                     changed == 0 ? "§8all at their defaults" : "§a" + changed + " changed",
                     "",
                     "§7Click to open"
                  );
               }
            }
         );
         back(menu, click -> openRoot(click.player(), cfg));
         menu.open(player);
      }
   }

   private static void openGroup(ServerPlayer player, CodxSettingsMenu.Builder cfg, String category, String group, List<CodxSettings.ConfigValue<?>> members) {
      PagedMenuBuilder menu = CodxMenu.paged(cfg.title() + " §7— " + prettify(group)).onChange(cfg.spec::apply);
      paginate(menu, group, members, (layout, slot, value) -> widget(layout, slot, cfg, value));
      back(menu, click -> openCategory(click.player(), cfg, category));
      menu.open(player);
   }

   private static <T> void paginate(PagedMenuBuilder menu, String title, Collection<T> entries, CodxSettingsMenu.Placer<T> placer) {
      List<T> list = new ArrayList<>(entries);
      int pages = Math.max(1, (list.size() + 28 - 1) / 28);

      for (int page = 0; page < pages; page++) {
         int from = page * 28;
         List<T> slice = list.subList(from, Math.min(list.size(), from + 28));
         menu.page(prettify(title), layout -> {
            for (int i = 0; i < slice.size(); i++) {
               placer.place(layout, slotFor(i), slice.get(i));
            }
         });
      }
   }

   private static int slotFor(int index) {
      return 10 + index / 7 * 9 + index % 7;
   }

   private static void back(PagedMenuBuilder menu, Consumer<CodxMenuClick> onBack) {
      menu.decorate(layout -> layout.action(45, Items.ARROW, "§a§l← Back", onBack, "§7Go back"));
   }

   private static void widget(CodxMenuLayout layout, int slot, CodxSettingsMenu.Builder cfg, CodxSettings.ConfigValue<?> value) {
      String label = prettify(value.name());
      String[] lore = lore(cfg, value);
      if (value instanceof CodxSettings.BooleanValue bool) {
         layout.toggle(slot, Items.LEVER, label, bool::get, on -> bool.toggle(), lore);
      } else if (value instanceof CodxSettings.IntValue number) {
         layout.adjustInt(slot, Items.REPEATER, label, number::get, number::add, intStep(number), lore);
      } else if (value instanceof CodxSettings.DoubleValue number) {
         layout.adjustDouble(slot, Items.COMPARATOR, label, number::get, number::add, doubleStep(number), lore);
      } else if (value instanceof CodxSettings.EnumValue<?> option) {
         layout.cycle(slot, Items.HOPPER, label, option::asString, option::cycle, lore);
      } else {
         Item item = value instanceof CodxSettings.ListValue ? Items.WRITABLE_BOOK : Items.NAME_TAG;
         layout.info(slot, item, "§b" + label + ": §e" + shorten(value.asString()), lore);
      }
   }

   private static CodxMenuButton.Step intStep(CodxSettings.IntValue value) {
      long span = (long)value.max() - value.min();
      int step = span > 5000L ? 100 : (span > 500L ? 10 : 1);
      return CodxMenuButton.Step.of(step, step * 10).range(value.min(), value.max());
   }

   private static CodxMenuButton.DoubleStep doubleStep(CodxSettings.DoubleValue value) {
      double span = value.max() - value.min();
      double step = span > 100.0 ? 1.0 : (span > 10.0 ? 0.5 : (span > 2.0 ? 0.1 : 0.05));
      return CodxMenuButton.DoubleStep.of(step, step * 10.0).range(value.min(), value.max());
   }

   private static String[] lore(CodxSettingsMenu.Builder cfg, CodxSettings.ConfigValue<?> value) {
      List<String> lore = new ArrayList<>(wrap(value.comment()));
      if (!lore.isEmpty()) {
         lore.add("");
      }

      lore.add("§8" + value.name());
      if (!value.rangeText().isEmpty()) {
         lore.add("§7Range: §f" + value.rangeText());
      }

      lore.add("§7Default: §f" + shorten(value.defaultAsString()));
      boolean readOnly = value instanceof CodxSettings.ListValue || value instanceof CodxSettings.StringValue;
      if (readOnly) {
         lore.add("");
         lore.add(cfg.command != null ? "§eSet this with " + cfg.command + " set " + value.name() : "§eEdit this in " + cfg.spec.fileName());
      }

      String note = cfg.note == null ? null : cfg.note.noteFor(value);
      if (note != null && !note.isEmpty()) {
         lore.add("");
         lore.addAll(wrap("§6" + note));
      }

      return lore.toArray(new String[0]);
   }

   private static String prettify(String name) {
      StringBuilder out = new StringBuilder(name.length() + 8);

      for (int i = 0; i < name.length(); i++) {
         char c = name.charAt(i);
         if (c == '_' || c == '.') {
            out.append(' ');
         } else if (i > 0 && Character.isUpperCase(c) && !Character.isUpperCase(name.charAt(i - 1))) {
            out.append(' ').append(c);
         } else {
            out.append(i == 0 ? Character.toUpperCase(c) : c);
         }
      }

      return out.toString();
   }

   private static List<String> wrap(String text) {
      List<String> lines = new ArrayList<>();
      if (text != null && !text.isEmpty()) {
         StringBuilder line = new StringBuilder();

         for (String word : text.split("\\s+")) {
            if (line.length() > 0 && line.length() + word.length() + 1 > 44) {
               lines.add("§7" + line);
               line.setLength(0);
            }

            if (line.length() > 0) {
               line.append(' ');
            }

            line.append(word);
         }

         if (line.length() > 0) {
            lines.add("§7" + line);
         }

         return lines;
      } else {
         return lines;
      }
   }

   private static String shorten(String text) {
      String flat = text.replace('\n', ' ');
      return flat.length() <= 40 ? flat : flat.substring(0, 37) + "...";
   }

   public static final class Builder {
      private final CodxSettings spec;
      private final Map<String, Item> icons = new LinkedHashMap<>();
      private final Map<String, Function<CodxSettings.ConfigValue<?>, String>> groupers = new LinkedHashMap<>();
      private String title;
      private String command;
      private CodxSettingsCommand.ChangeNote note;

      private Builder(CodxSettings spec) {
         this.spec = spec;
      }

      public CodxSettingsMenu.Builder title(String title) {
         this.title = title;
         return this;
      }

      public CodxSettingsMenu.Builder command(String command) {
         this.command = command;
         return this;
      }

      public CodxSettingsMenu.Builder note(CodxSettingsCommand.ChangeNote note) {
         this.note = note;
         return this;
      }

      public CodxSettingsMenu.Builder icon(String key, Item item) {
         this.icons.put(key, item);
         return this;
      }

      public CodxSettingsMenu.Builder group(String category, Function<CodxSettings.ConfigValue<?>, String> grouper) {
         this.groupers.put(category, grouper);
         return this;
      }

      public void open(ServerPlayer player) {
         CodxSettingsMenu.openRoot(player, this);
      }

      private String title() {
         return this.title != null ? this.title : "§6§l" + this.spec.fileName();
      }

      private Item iconOr(String key, Item fallback) {
         return this.icons.getOrDefault(key, fallback);
      }
   }

   @FunctionalInterface
   private interface Placer<T> {
      void place(CodxMenuLayout var1, int var2, T var3);
   }
}
