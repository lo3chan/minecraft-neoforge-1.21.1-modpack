package codx.codxlib.api.ui.menu;

import net.minecraft.server.level.ServerPlayer;

public final class CodxMenuClick {
   private final CodxMenu menu;
   private final ServerPlayer player;
   private final boolean shift;
   private final boolean rightClick;
   boolean changed;
   boolean closed;

   CodxMenuClick(CodxMenu menu, ServerPlayer player, boolean shift, boolean rightClick) {
      this.menu = menu;
      this.player = player;
      this.shift = shift;
      this.rightClick = rightClick;
   }

   public ServerPlayer player() {
      return this.player;
   }

   public boolean shift() {
      return this.shift;
   }

   public boolean rightClick() {
      return this.rightClick;
   }

   public boolean leftClick() {
      return !this.rightClick;
   }

   public int page() {
      return this.menu.currentPage();
   }

   public void markChanged() {
      this.changed = true;
   }

   public void close() {
      this.closed = true;
   }

   public void gotoPage(int index) {
      this.menu.setPage(index);
   }

   public void nextPage() {
      this.menu.setPage(this.menu.currentPage() + 1);
   }

   public void prevPage() {
      this.menu.setPage(this.menu.currentPage() - 1);
   }
}
