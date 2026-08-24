package codx.codxlib.api.ui.menu;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CodxMenu extends AbstractContainerMenu {
   private final ServerPlayer player;
   private final SimpleContainer container;
   private final int rows;
   private final List<CodxMenu.Page> pages;
   private final Runnable onChange;
   private final Predicate<ServerPlayer> canUse;
   private int page;

   CodxMenu(int syncId, Inventory playerInventory, ServerPlayer player, int rows, List<CodxMenu.Page> pages, Runnable onChange, Predicate<ServerPlayer> canUse) {
      super(typeForRows(rows), syncId);
      this.player = player;
      this.rows = rows;
      this.pages = pages;
      this.onChange = onChange;
      this.canUse = canUse;
      this.container = new SimpleContainer(rows * 9);

      for (int i = 0; i < rows * 9; i++) {
         int col = i % 9;
         int row = i / 9;
         this.addSlot(new Slot(this.container, i, 8 + col * 18, 18 + row * 18) {
            public boolean mayPlace(ItemStack stack) {
               return false;
            }
         });
      }

      int invY = 32 + rows * 18;

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, invY + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.addSlot(new Slot(playerInventory, col, 8 + col * 18, invY + 58));
      }

      this.render();
   }

   public static SimpleMenuBuilder simple(Component title) {
      return new SimpleMenuBuilder(title);
   }

   public static SimpleMenuBuilder simple(String title) {
      return new SimpleMenuBuilder(Component.literal(title));
   }

   public static PagedMenuBuilder paged(Component title) {
      return new PagedMenuBuilder(title);
   }

   public static PagedMenuBuilder paged(String title) {
      return new PagedMenuBuilder(Component.literal(title));
   }

   int currentPage() {
      return this.page;
   }

   void setPage(int index) {
      this.page = Math.max(0, Math.min(this.pages.size() - 1, index));
   }

   private void render() {
      this.container.clearContent();
      this.pages.get(this.page).buttons().forEach((slot, button) -> {
         if (slot >= 0 && slot < this.rows * 9) {
            this.container.setItem(slot, button.icon.get());
         }
      });
   }

   public void clicked(int slotIndex, int button, ClickType actionType, Player who) {
      if (slotIndex >= 0 && slotIndex < this.rows * 9 && who instanceof ServerPlayer serverPlayer) {
         CodxMenuButton btn = this.pages.get(this.page).buttons().get(slotIndex);
         if (btn != null && btn.onClick != null) {
            boolean shift = actionType == ClickType.QUICK_MOVE;
            boolean rightClick = button == 1;
            CodxMenuClick click = new CodxMenuClick(this, serverPlayer, shift, rightClick);
            btn.onClick.accept(click);
            if (click.closed) {
               serverPlayer.closeContainer();
            } else {
               if (click.changed && this.onChange != null) {
                  this.onChange.run();
               }

               this.render();
            }
         }
      } else {
         super.clicked(slotIndex, button, actionType, who);
      }
   }

   public ItemStack quickMoveStack(Player who, int slot) {
      return ItemStack.EMPTY;
   }

   public boolean stillValid(Player who) {
      return who instanceof ServerPlayer serverPlayer && this.canUse.test(serverPlayer);
   }

   public void removed(Player who) {
      super.removed(who);
      this.container.clearContent();
   }

   static MenuType<?> typeForRows(int rows) {
      return switch (rows) {
         case 1 -> MenuType.GENERIC_9x1;
         case 2 -> MenuType.GENERIC_9x2;
         case 3 -> MenuType.GENERIC_9x3;
         case 4 -> MenuType.GENERIC_9x4;
         case 5 -> MenuType.GENERIC_9x5;
         default -> MenuType.GENERIC_9x6;
      };
   }

   record Page(Component title, Map<Integer, CodxMenuButton> buttons) {
   }
}
