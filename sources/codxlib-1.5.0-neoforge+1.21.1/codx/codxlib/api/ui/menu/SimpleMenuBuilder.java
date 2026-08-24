package codx.codxlib.api.ui.menu;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public final class SimpleMenuBuilder {
   private final Component title;
   private final CodxMenuLayout layout = new CodxMenuLayout();
   private int rows = -1;
   private Runnable onChange;
   private Predicate<ServerPlayer> canUse = player -> true;

   SimpleMenuBuilder(Component title) {
      this.title = title;
   }

   public SimpleMenuBuilder rows(int rows) {
      this.rows = Math.max(1, Math.min(6, rows));
      return this;
   }

   public SimpleMenuBuilder onChange(Runnable onChange) {
      this.onChange = onChange;
      return this;
   }

   public SimpleMenuBuilder canUse(Predicate<ServerPlayer> canUse) {
      this.canUse = canUse;
      return this;
   }

   public SimpleMenuBuilder layout(Consumer<CodxMenuLayout> populate) {
      populate.accept(this.layout);
      return this;
   }

   public void open(ServerPlayer player) {
      int resolvedRows = this.rows > 0 ? this.rows : autoRows(this.layout.maxSlot());
      List<CodxMenu.Page> pages = List.of(new CodxMenu.Page(this.title, this.layout.buttons()));
      player.openMenu(
         new SimpleMenuProvider(
            (syncId, inventory, who) -> new CodxMenu(syncId, inventory, player, resolvedRows, pages, this.onChange, this.canUse), this.title
         )
      );
   }

   private static int autoRows(int maxSlot) {
      return maxSlot < 0 ? 1 : Math.max(1, Math.min(6, maxSlot / 9 + 1));
   }
}
