package com.aetherteam.aether.client.gui.screen.inventory;

import com.aetherteam.aether.client.gui.component.inventory.LorePageButton;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LoreBookScreen extends AbstractContainerScreen<LoreBookMenu> {
   private static final ResourceLocation TEXTURE_LORE_BACKING = ResourceLocation.fromNamespaceAndPath("aether", "textures/gui/menu/lore_backing.png");
   private static final ResourceLocation TEXTURE_LORE_BOOK = ResourceLocation.fromNamespaceAndPath("aether", "textures/gui/menu/lore_book.png");
   private final Map<Integer, List<FormattedCharSequence>> pages = new HashMap<>();
   private LorePageButton previousButton;
   private LorePageButton nextButton;
   private int currentPageNumber;
   private ItemStack lastStack;

   public LoreBookScreen(LoreBookMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.imageWidth = 256;
      this.imageHeight = 199;
   }

   protected void init() {
      super.init();
      int xPos = (this.width - this.getXSize()) / 2;
      int yPos = (this.height - this.getYSize()) / 2;
      this.previousButton = (LorePageButton)this.addRenderableWidget(new LorePageButton(new Builder(Component.literal("<"), button -> {
         if (this.currentPageNumber > 0) {
            this.currentPageNumber--;
         }
      }).bounds(xPos + 14, yPos + 169, 20, 20)));
      this.nextButton = (LorePageButton)this.addRenderableWidget(new LorePageButton(new Builder(Component.literal(">"), button -> {
         if (this.currentPageNumber < this.pages.size() - 1) {
            this.currentPageNumber++;
         }
      }).bounds(xPos + 221, yPos + 169, 20, 20)));
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
      Component previous = Component.translatable("gui.aether.book_of_lore.previous");
      Component next = Component.translatable("gui.aether.book_of_lore.next");
      this.drawNormalBookText(guiGraphics, this.font, previous, 13, 158);
      this.drawNormalBookText(guiGraphics, this.font, next, 221, 158);
      Component book = Component.translatable("gui.aether.book_of_lore.book");
      Component ofLore = Component.translatable("gui.aether.book_of_lore.of_lore");
      this.drawCenteredBookText(guiGraphics, this.font, book, 75, 20);
      this.drawCenteredBookText(guiGraphics, this.font, ofLore, 75, 30);
      Component item = Component.translatable("gui.aether.book_of_lore.item");
      this.drawRightBookText(guiGraphics, this.font, item, 78, 67);
      ItemStack itemStack = ((Slot)((LoreBookMenu)this.getMenu()).slots.getFirst()).getItem();
      if (!itemStack.isEmpty()) {
         String entryKey = ((LoreBookMenu)this.getMenu()).getLoreEntryKey(itemStack);
         if (I18n.exists(entryKey)) {
            Component entry = Component.translatable(entryKey);
            this.createPages(entry);
            if (this.currentPageNumber == 0) {
               Component title = itemStack.getHoverName().plainCopy();
               this.createText(guiGraphics, this.font.split(title, 98), 136, 10);
               this.createText(guiGraphics, this.pages.get(0), 136, 32);
            } else {
               this.createText(guiGraphics, this.pages.get(this.currentPageNumber), 136, 10);
            }
         }
      }

      if (itemStack.isEmpty() || !itemStack.is(this.lastStack.getItem())) {
         this.pages.clear();
         this.currentPageNumber = 0;
      }

      this.previousButton.active = this.currentPageNumber > 0;
      this.nextButton.active = this.currentPageNumber < this.pages.size() - 1;
      this.lastStack = itemStack;
   }

   private void createPages(Component loreEntry) {
      List<FormattedCharSequence> formattedText = new ArrayList<>(this.font.split(loreEntry, 98));
      if (formattedText.size() < 6) {
         List<FormattedCharSequence> firstPage = formattedText.subList(0, formattedText.size());
         this.pages.put(0, firstPage);
      } else {
         List<FormattedCharSequence> firstPage = formattedText.subList(0, 6);
         this.pages.put(0, firstPage);
         List<FormattedCharSequence> remainingPages = formattedText.subList(6, formattedText.size());
         List<List<FormattedCharSequence>> list = Lists.partition(remainingPages, 8);

         for (int i = 1; i < list.size() + 1; i++) {
            this.pages.put(i, list.get(i - 1));
         }
      }
   }

   private void createText(GuiGraphics guiGraphics, List<FormattedCharSequence> reorderingProcessors, int x, int y) {
      int length = 0;

      for (FormattedCharSequence line : reorderingProcessors) {
         this.drawBookText(guiGraphics, this.font, line, x, y + length * 10);
         length++;
      }
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
      int xPos = (this.width - this.getXSize()) / 2;
      int yPos = (this.height - this.getYSize()) / 2;
      guiGraphics.blit(TEXTURE_LORE_BACKING, xPos, yPos - 4, 0.0F, 0.0F, this.getXSize(), this.getYSize() + 56, 256, 256);
      guiGraphics.blit(TEXTURE_LORE_BOOK, xPos + 12, yPos + 2, 0.0F, 0.0F, this.getXSize(), this.getYSize() + 56, 256, 256);
   }

   private void drawNormalBookText(GuiGraphics guiGraphics, Font fontRenderer, Component component, int x, int y) {
      FormattedCharSequence sequence = component.getVisualOrderText();
      this.drawBookText(guiGraphics, fontRenderer, sequence, x, y);
   }

   private void drawRightBookText(GuiGraphics guiGraphics, Font fontRenderer, Component component, int x, int y) {
      FormattedCharSequence sequence = component.getVisualOrderText();
      this.drawBookText(guiGraphics, fontRenderer, sequence, x - fontRenderer.width(sequence), y);
   }

   private void drawCenteredBookText(GuiGraphics guiGraphics, Font fontRenderer, Component component, int x, int y) {
      FormattedCharSequence sequence = component.getVisualOrderText();
      this.drawBookText(guiGraphics, fontRenderer, sequence, x - fontRenderer.width(sequence) / 2, y);
   }

   private void drawBookText(GuiGraphics guiGraphics, Font fontRenderer, FormattedCharSequence sequence, int x, int y) {
      guiGraphics.drawString(fontRenderer, sequence, x, y, 4210752, false);
   }
}
