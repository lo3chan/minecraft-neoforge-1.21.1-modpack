package com.alonie.brbe.generic;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.layout.GridSpec;
import com.alonie.brbe.util.BRBTextures;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class GenericRecipePage<M extends AbstractContainerMenu, C extends GenericRecipeBookCollection<R, M>, R extends GenericRecipe> {
   protected final RegistryAccess registryAccess;
   protected M menu;
   protected Minecraft minecraft;
   protected int parentLeft;
   protected int parentTop;
   protected int bookWidth = 147;
   protected StateSwitchingButton forwardButton;
   protected StateSwitchingButton backButton;
   protected List<C> recipeCollections = ImmutableList.of();
   protected C lastClickedRecipeCollection;
   protected R lastClickedRecipe;
   protected BRBBookCategories.Category category;
   protected int totalPages;
   protected int currentPage;
   private final List<GenericRecipeButton<C, R, M>> buttons = Lists.newArrayListWithCapacity(80);
   protected GenericRecipeButton<C, R, M> hoveredButton;

   public List<GenericRecipeButton<C, R, M>> getButtons() {
      return this.buttons;
   }

   public GenericRecipePage(RegistryAccess registryAccess, Supplier<GenericRecipeButton<C, R, M>> recipeButtonSupplier) {
      this.registryAccess = registryAccess;

      for (int i = 0; i < 20; i++) {
         this.buttons.add(recipeButtonSupplier.get());
      }
   }

   public int getColumns() {
      if (!BetterRecipeBook.ctx().config().expandedRecipeBook) {
         return 5;
      } else {
         int availableWidth = this.bookWidth - 22;
         return Math.max(5, availableWidth / 25);
      }
   }

   public int getButtonsPerPage() {
      return !BetterRecipeBook.ctx().config().expandedRecipeBook ? GridSpec.standard().totalButtons() : this.getColumns() * 4;
   }

   protected void initialize(Minecraft client, int parentLeft, int parentTop, M menu, int bookWidth) {
      this.minecraft = client;
      this.menu = menu;
      this.parentLeft = parentLeft;
      this.parentTop = parentTop;
      this.bookWidth = bookWidth;
      int cols;
      int gridLeft;
      int forwardX;
      int backX;
      if (BetterRecipeBook.ctx().config().expandedRecipeBook) {
         cols = this.getColumns();
         int gridWidth = cols * 25;
         gridLeft = parentLeft + (bookWidth - gridWidth) / 2;
         int pageCenterX = parentLeft + bookWidth / 2;
         forwardX = pageCenterX + 3;
         backX = pageCenterX - 15;
      } else {
         cols = 5;
         gridLeft = parentLeft + 11;
         forwardX = parentLeft + 93;
         backX = parentLeft + 38;
      }

      this.forwardButton = new StateSwitchingButton(forwardX, parentTop + 137, 12, 17, false);
      this.forwardButton.initTextureValues(BRBTextures.RECIPE_BOOK_PAGE_FORWARD_SPRITES);
      this.backButton = new StateSwitchingButton(backX, parentTop + 137, 12, 17, true);
      this.backButton.initTextureValues(BRBTextures.RECIPE_BOOK_PAGE_BACKWARD_SPRITES);

      for (int k = 0; k < this.buttons.size(); k++) {
         this.buttons.get(k).setPosition(gridLeft + 25 * (k % cols), parentTop + 31 + 25 * (k / cols));
         this.buttons.get(k).visible = false;
      }
   }

   protected boolean overlayMouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
      return false;
   }

   protected void initOverlay(C recipeCollection, int x, int y, RegistryAccess registryAccess) {
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button, int j, int k, int l, int m) {
      this.lastClickedRecipe = null;
      this.lastClickedRecipeCollection = null;
      if (this.overlayIsVisible() && this.overlayMouseClicked(mouseX, mouseY, button, j, k, l, m)) {
         return true;
      } else if (this.forwardButton.mouseClicked(mouseX, mouseY, button)) {
         if (++this.currentPage >= this.totalPages) {
            this.currentPage = BetterRecipeBook.config.scrollAround ? 0 : this.totalPages - 1;
         }

         this.updateButtonsForPage();
         return true;
      } else if (this.backButton.mouseClicked(mouseX, mouseY, button)) {
         if (--this.currentPage < 0) {
            this.currentPage = BetterRecipeBook.config.scrollAround ? this.totalPages - 1 : 0;
         }

         this.updateButtonsForPage();
         return true;
      } else {
         for (GenericRecipeButton<C, R, M> recipeButton : this.buttons) {
            if (recipeButton.mouseClicked(mouseX, mouseY, button)) {
               if (button == 0) {
                  this.lastClickedRecipe = recipeButton.getCurrentDisplayedRecipe();
                  this.lastClickedRecipeCollection = recipeButton.getCollection();
               } else if (button == 1 && !this.overlayIsVisible() && !recipeButton.isOnlyOption()) {
                  this.initOverlay(recipeButton.getCollection(), this.parentLeft, this.parentTop, this.registryAccess);
               }

               return true;
            }
         }

         return false;
      }
   }

   public void updateButtonsForPage() {
      int bpp = this.getButtonsPerPage();
      int i = bpp * this.currentPage;

      for (int j = 0; j < this.buttons.size(); j++) {
         GenericRecipeButton<C, R, M> button = this.buttons.get(j);
         if (i + j < this.recipeCollections.size()) {
            C output = this.recipeCollections.get(i + j);
            button.showCollection(output, this.menu, this.category);
            button.visible = true;
         } else {
            button.visible = false;
         }
      }

      this.updateArrowButtons();
   }

   protected boolean overlayIsVisible() {
      return false;
   }

   protected void render(GuiGraphics gui, int blitX, int blitY, int mouseX, int mouseY, float delta) {
      if (this.backButton != null && this.forwardButton != null && this.buttons != null) {
         if (BetterRecipeBook.getQueuedScroll() != 0) {
            if (this.isMouseOverRecipeBookPage(mouseX, mouseY, blitX, blitY) && this.totalPages > 1) {
               this.currentPage = this.currentPage + BetterRecipeBook.getQueuedScroll();
               if (this.currentPage >= this.totalPages) {
                  this.currentPage = BetterRecipeBook.config.scrollAround ? this.currentPage % this.totalPages : this.totalPages - 1;
               } else if (this.currentPage < 0) {
                  this.currentPage = BetterRecipeBook.config.scrollAround ? this.currentPage % this.totalPages + this.totalPages : 0;
               }

               this.updateButtonsForPage();
            }

            BetterRecipeBook.setQueuedScroll(0);
         }

         if (this.totalPages > 1) {
            String string = this.currentPage + 1 + "/" + this.totalPages;
            int stringWidth = this.minecraft.font.width(string);
            gui.drawString(this.minecraft.font, string, blitX + this.bookWidth / 2 - stringWidth / 2, blitY + 141, -1, false);
         }

         this.hoveredButton = null;

         for (GenericRecipeButton<C, R, M> button : this.buttons) {
            button.render(gui, mouseX, mouseY, delta);
            if (button.visible && button.isHoveredOrFocused()) {
               this.hoveredButton = button;
            }
         }

         if (this.backButton != null) {
            this.backButton.render(gui, mouseX, mouseY, delta);
         }

         if (this.forwardButton != null) {
            this.forwardButton.render(gui, mouseX, mouseY, delta);
         }
      }
   }

   private boolean isMouseOverRecipeBookPage(int mouseX, int mouseY, int left, int top) {
      return mouseX >= left && mouseX < left + this.bookWidth && mouseY >= top && mouseY < top + 166;
   }

   public void setResults(List<C> recipeCollection, boolean resetCurrentPage, BRBBookCategories.Category category) {
      this.recipeCollections = recipeCollection;
      this.category = category;
      int bpp = this.getButtonsPerPage();
      this.totalPages = (int)Math.ceil((double)recipeCollection.size() / bpp);
      if (this.totalPages <= this.currentPage || resetCurrentPage) {
         this.currentPage = 0;
      }

      this.updateButtonsForPage();
   }

   @Nullable
   public R getCurrentClickedRecipe() {
      return this.lastClickedRecipe;
   }

   @Nullable
   public C getLastClickedRecipeCollection() {
      return this.lastClickedRecipeCollection;
   }

   protected void updateArrowButtons() {
      if (this.forwardButton != null && this.backButton != null) {
         if (BetterRecipeBook.config.scrollAround && this.totalPages > 1) {
            this.forwardButton.visible = true;
            this.backButton.visible = true;
         } else {
            this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
            this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
         }
      }
   }

   public void drawTooltip(GuiGraphics gui, int x, int y) {
      if (this.minecraft != null && this.minecraft.screen != null && this.hoveredButton != null) {
         gui.renderComponentTooltip(Minecraft.getInstance().font, this.hoveredButton.getTooltipText(), x, y);
      }
   }
}
