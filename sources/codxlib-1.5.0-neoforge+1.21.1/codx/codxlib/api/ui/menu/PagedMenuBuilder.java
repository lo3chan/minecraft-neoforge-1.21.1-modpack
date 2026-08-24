package codx.codxlib.api.ui.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Items;

public final class PagedMenuBuilder {
   private static final int SLOT_RESET = 45;
   private static final int SLOT_PREV = 47;
   private static final int SLOT_TITLE = 49;
   private static final int SLOT_NEXT = 51;
   private static final int SLOT_CLOSE = 53;
   private final Component title;
   private final List<CodxMenu.Page> pages = new ArrayList<>();
   private final List<Consumer<CodxMenuLayout>> decorations = new ArrayList<>();
   private Runnable onChange;
   private Consumer<CodxMenuClick> reset;
   private Predicate<ServerPlayer> canUse = player -> true;

   PagedMenuBuilder(Component title) {
      this.title = title;
   }

   public PagedMenuBuilder onChange(Runnable onChange) {
      this.onChange = onChange;
      return this;
   }

   public PagedMenuBuilder canUse(Predicate<ServerPlayer> canUse) {
      this.canUse = canUse;
      return this;
   }

   public PagedMenuBuilder resetButton(Consumer<CodxMenuClick> reset) {
      this.reset = reset;
      return this;
   }

   public PagedMenuBuilder decorate(Consumer<CodxMenuLayout> decoration) {
      this.decorations.add(decoration);
      return this;
   }

   public PagedMenuBuilder page(String pageTitle, Consumer<CodxMenuLayout> populate) {
      CodxMenuLayout layout = new CodxMenuLayout();
      populate.accept(layout);
      this.pages.add(new CodxMenu.Page(Component.literal(pageTitle), layout.buttons()));
      return this;
   }

   public void open(ServerPlayer player) {
      Map<Integer, CodxMenuButton> decorated = new LinkedHashMap<>();
      if (!this.decorations.isEmpty()) {
         CodxMenuLayout layout = new CodxMenuLayout();

         for (Consumer<CodxMenuLayout> decoration : this.decorations) {
            decoration.accept(layout);
         }

         decorated.putAll(layout.buttons());
      }

      List<CodxMenu.Page> finalPages = new ArrayList<>(this.pages.size());

      for (int i = 0; i < this.pages.size(); i++) {
         CodxMenu.Page src = this.pages.get(i);
         Map<Integer, CodxMenuButton> buttons = new LinkedHashMap<>(decorated);
         buttons.putAll(src.buttons());
         this.addNav(buttons, i, this.pages.size(), src.title());
         finalPages.add(new CodxMenu.Page(src.title(), buttons));
      }

      player.openMenu(
         new SimpleMenuProvider((syncId, inventory, who) -> new CodxMenu(syncId, inventory, player, 6, finalPages, this.onChange, this.canUse), this.title)
      );
   }

   private void addNav(Map<Integer, CodxMenuButton> buttons, int index, int total, Component pageTitle) {
      if (this.reset != null) {
         buttons.put(45, CodxMenuButton.action(Items.BARRIER, "§c§lReset to Defaults", click -> {
            this.reset.accept(click);
            click.markChanged();
         }, "§7Reset all settings to defaults"));
      }

      if (index > 0) {
         buttons.put(47, CodxMenuButton.action(Items.ARROW, "§a§l← Previous", CodxMenuClick::prevPage, "§7Go to the previous page"));
      }

      buttons.put(49, CodxMenuButton.info(Items.MAP, "§e§l" + pageTitle.getString() + " §7(" + (index + 1) + "/" + total + ")"));
      if (index < total - 1) {
         buttons.put(51, CodxMenuButton.action(Items.ARROW, "§a§lNext →", CodxMenuClick::nextPage, "§7Go to the next page"));
      }

      buttons.put(53, CodxMenuButton.action(Items.OAK_DOOR, "§c§lClose", CodxMenuClick::close, "§7Close the menu"));
   }
}
