package net.mehvahdjukaar.amendments.client.gui;

import java.util.Optional;
import net.mehvahdjukaar.amendments.common.LecternEditMenu;
import net.mehvahdjukaar.amendments.common.network.ServerBoundSyncLecternBookMessage;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;

public class LecternBookEditScreen extends BookEditScreen implements MenuAccess<LecternEditMenu> {
   private final LecternEditMenu menu;
   private final ContainerListener listener = new ContainerListener() {
      public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
         LecternBookEditScreen.this.book = stack;
         LecternBookEditScreen.this.pages.clear();
         WritableBookContent writableBookContent = (WritableBookContent)LecternBookEditScreen.this.book.get(DataComponents.WRITABLE_BOOK_CONTENT);
         if (writableBookContent != null) {
            writableBookContent.getPages(Minecraft.getInstance().isTextFilteringEnabled()).forEach(LecternBookEditScreen.this.pages::add);
            LecternBookEditScreen.this.clearDisplayCache();
         }

         if (LecternBookEditScreen.this.pages.isEmpty()) {
            LecternBookEditScreen.this.pages.add("");
         }
      }

      public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
         if (dataSlotIndex == 0) {
            LecternBookEditScreen.this.setPage(value);
         }
      }
   };
   private int lastPage;
   private Button takeBookButton;
   private QuillButton quill;
   private InkButton ink;
   private StyledTextFieldHelper page;

   public LecternBookEditScreen(LecternEditMenu lecternMenu, Inventory inventory, Component component) {
      super(inventory.player, lecternMenu.getBook(), InteractionHand.MAIN_HAND);
      this.menu = lecternMenu;
      this.lastPage = this.menu.getPage();
   }

   public void saveChanges(boolean publish) {
      this.saveChanges(publish, false);
   }

   public void saveChanges(boolean publish, boolean takeBook) {
      if (this.isModified) {
         this.eraseEmptyTrailingPages();
         this.updateLocalCopy();
         NetworkHelper.sendToServer(
            new ServerBoundSyncLecternBookMessage(this.menu.getPos(), this.pages, publish ? Optional.of(this.title.trim()) : Optional.empty(), takeBook)
         );
      }
   }

   public LecternEditMenu getMenu() {
      return this.menu;
   }

   protected void init() {
      this.page = new StyledTextFieldHelper(
         this::getCurrentPageText,
         this::setCurrentPageText,
         this::getClipboard,
         this::setClipboard,
         string -> string.length() < 1024 && this.font.wordWrapHeight(string, 114) <= 128
      );
      this.pageEdit = this.page;
      this.menu.addSlotListener(this.listener);
      this.quill = (QuillButton)this.addRenderableWidget(new QuillButton(this));
      this.ink = (InkButton)this.addRenderableWidget(new InkButton(this));
      int width = 76;
      this.takeBookButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"), button -> {
         this.saveChanges(false, true);
         if (!this.isModified) {
            this.sendButtonClick(3);
         }
      }).bounds(this.width / 2 - width / 2 - 6, 196, width, 20).build());
      super.init();
      int separation = 80;
      this.signButton.setX((this.width - width) / 2 - separation - 6);
      this.signButton.setWidth(width);
      this.removeWidget(this.doneButton);
      this.doneButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
         this.saveChanges(false);
         this.onClose();
      }).bounds((this.width - width) / 2 + separation - 6, 196, width, 20).build());
      this.removeWidget(this.finalizeButton);
      this.finalizeButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("book.finalizeButton"), button -> {
         if (this.isSigning) {
            this.saveChanges(true);
            this.onClose();
         }
      }).bounds(this.width / 2 - 100, 196, 98, 20).build());
      this.updateButtonVisibility();
   }

   protected void updateButtonVisibility() {
      super.updateButtonVisibility();
      this.takeBookButton.visible = !this.isSigning;
      this.ink.visible = !this.isSigning;
      this.quill.visible = !this.isSigning;
   }

   public void onClose() {
      this.minecraft.player.closeContainer();
      super.onClose();
   }

   public void pageBack() {
      this.sendButtonClick(1);
   }

   public void pageForward() {
      this.sendButtonClick(2);
   }

   private void setPage(int value) {
      while (this.lastPage != value) {
         if (value <= this.lastPage) {
            this.lastPage--;
            super.pageBack();
         } else {
            this.lastPage++;
            super.pageForward();
            if (this.lastPage > this.pages.size()) {
               for (int i = 0; i < value - this.pages.size(); i++) {
                  this.pages.add("");
               }

               this.isModified = true;
            }
         }
      }
   }

   private void sendButtonClick(int pageData) {
      this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, pageData);
   }

   public boolean titleKeyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 335) {
         if (!this.title.isEmpty()) {
            this.saveChanges(true);
            this.onClose();
         }

         return true;
      } else {
         return super.titleKeyPressed(keyCode, scanCode, modifiers);
      }
   }

   public void removed() {
      super.removed();
      this.menu.removeSlotListener(this.listener);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean charTyped(char codePoint, int modifiers) {
      if (!this.isSigning && StringUtil.isAllowedChatCharacter(codePoint)) {
         this.page.insertStyledText(Character.toString(codePoint), this.ink.getChatFormatting(), this.quill.getChatFormatting());
         this.clearDisplayCache();
         return true;
      } else {
         return super.charTyped(codePoint, modifiers);
      }
   }

   public void onInkClicked() {
      if (this.page.isSelecting()) {
         this.page.formatSelected(this.ink.getChatFormatting(), null);
      }
   }

   public void onQuillClicked() {
      if (this.page.isSelecting()) {
         this.page.formatSelected(null, this.quill.getChatFormatting());
      }
   }
}
