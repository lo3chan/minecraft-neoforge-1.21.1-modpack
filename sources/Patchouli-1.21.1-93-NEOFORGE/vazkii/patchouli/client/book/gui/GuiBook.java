package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.EntryDisplayState;
import vazkii.patchouli.client.book.gui.button.GuiButtonBook;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookArrow;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookBookmark;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookMarkRead;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.jei.PatchouliJeiPlugin;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.mixin.client.AccessorScreen;
import vazkii.patchouli.xplat.IXplatAbstractions;

public abstract class GuiBook extends Screen {
   public static final int FULL_WIDTH = 272;
   public static final int FULL_HEIGHT = 180;
   public static final int PAGE_WIDTH = 116;
   public static final int PAGE_HEIGHT = 156;
   public static final int TOP_PADDING = 18;
   public static final int LEFT_PAGE_X = 15;
   public static final int RIGHT_PAGE_X = 141;
   public static final int TEXT_LINE_HEIGHT = 9;
   public static final int MAX_BOOKMARKS = 10;
   public final Book book;
   private static long lastSound;
   public int bookLeft;
   public int bookTop;
   private float scaleFactor;
   @Nullable
   private List<Component> tooltip;
   @Nullable
   private ItemStack tooltipStack;
   @Nullable
   private Pair<BookEntry, Integer> targetPage;
   protected int spread = 0;
   protected int maxSpreads = 0;
   public int ticksInBook;
   public int maxScale;
   protected boolean needsBookmarkUpdate = false;

   public GuiBook(Book book, Component title) {
      super(title);
      this.book = book;
   }

   public void init() {
      Window res = this.minecraft.getWindow();
      double oldGuiScale = res.calculateScale((Integer)this.minecraft.options.guiScale().get(), this.minecraft.isEnforceUnicode());
      this.maxScale = this.getMaxAllowedScale();
      int persistentScale = Math.min(PersistentData.data.bookGuiScale, this.maxScale);
      double newGuiScale = res.calculateScale(persistentScale, this.minecraft.isEnforceUnicode());
      if (persistentScale > 0 && newGuiScale != oldGuiScale) {
         this.scaleFactor = (float)newGuiScale / (float)res.getGuiScale();
         res.setGuiScale(newGuiScale);
         this.width = res.getGuiScaledWidth();
         this.height = res.getGuiScaledHeight();
         res.setGuiScale(oldGuiScale);
      } else {
         this.scaleFactor = 1.0F;
      }

      this.bookLeft = this.width / 2 - 136;
      this.bookTop = this.height / 2 - 90;
      this.book.getContents().currentGui = this;
      this.addRenderableWidget(
         new GuiButtonBook(
            this,
            this.width / 2 - 9,
            this.bookTop + 180 - 5,
            308,
            0,
            18,
            9,
            this::canSeeBackButton,
            this::handleButtonBack,
            Component.translatable("patchouli.gui.lexicon.button.back"),
            Component.translatable("patchouli.gui.lexicon.button.back.info").withStyle(ChatFormatting.GRAY)
         )
      );
      this.addRenderableWidget(new GuiButtonBookArrow(this, this.bookLeft - 4, this.bookTop + 180 - 6, true));
      this.addRenderableWidget(new GuiButtonBookArrow(this, this.bookLeft + 272 - 14, this.bookTop + 180 - 6, false));
      this.addBookmarkButtons();
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.pose().pushPose();
      if (this.scaleFactor != 1.0F) {
         graphics.pose().scale(this.scaleFactor, this.scaleFactor, this.scaleFactor);
         mouseX = (int)(mouseX / this.scaleFactor);
         mouseY = (int)(mouseY / this.scaleFactor);
      }

      this.drawScreenAfterScale(graphics, mouseX, mouseY, partialTicks);
      graphics.pose().popPose();
   }

   private void drawScreenAfterScale(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      this.resetTooltip();
      graphics.pose().pushPose();
      graphics.pose().translate(this.bookLeft, this.bookTop, 0.0F);
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
      this.drawForegroundElements(graphics, mouseX, mouseY, partialTicks);
      graphics.pose().popPose();
      super.render(graphics, mouseX, mouseY, partialTicks);
      IXplatAbstractions.INSTANCE.fireDrawBookScreen(this.book.id, this, mouseX, mouseY, partialTicks, graphics);
      this.drawTooltip(graphics, mouseX, mouseY);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
   }

   public void addBookmarkButtons() {
      this.removeDrawablesIf(b -> b instanceof GuiButtonBookBookmark);
      int y = 0;
      List<PersistentData.Bookmark> bookmarks = PersistentData.data.getBookData(this.book).bookmarks;

      for (PersistentData.Bookmark bookmark : bookmarks) {
         this.addRenderableWidget(new GuiButtonBookBookmark(this, this.bookLeft + 272, this.bookTop + 18 + y, bookmark));
         y += 12;
      }

      if (this.shouldAddAddBookmarkButton() && bookmarks.size() <= 10) {
         this.addRenderableWidget(new GuiButtonBookBookmark(this, this.bookLeft + 272, this.bookTop + 18 + y, null));
      }

      if (MultiblockVisualizationHandler.hasMultiblock && MultiblockVisualizationHandler.bookmark != null) {
         this.addRenderableWidget(
            new GuiButtonBookBookmark(this, this.bookLeft + 272, this.bookTop + 18 + 156 - 20, MultiblockVisualizationHandler.bookmark, true)
         );
      }

      if (this.shouldAddMarkReadButton()) {
         this.addRenderableWidget(new GuiButtonBookMarkRead(this, this.bookLeft + 272, this.bookTop + 18 + 156 - 10));
      }
   }

   public final void removeDrawablesIf(Predicate<Renderable> pred) {
      ((AccessorScreen)this).getRenderables().removeIf(pred);
      this.children().removeIf(listener -> listener instanceof Renderable w && pred.test(w));
      ((AccessorScreen)this).getNarratables().removeIf(listener -> listener instanceof Renderable w && pred.test(w));
   }

   public final void removeDrawablesIn(Collection<?> coll) {
      this.removeDrawablesIf(coll::contains);
   }

   public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T drawableElement) {
      return (T)super.addRenderableWidget(drawableElement);
   }

   protected boolean shouldAddAddBookmarkButton() {
      return false;
   }

   protected boolean shouldAddMarkReadButton() {
      return this instanceof GuiBookIndex
         ? false
         : this.book.getContents().entries.values().stream().anyMatch(v -> !v.isLocked() && v.getReadState().equals(EntryDisplayState.UNREAD));
   }

   public void bookmarkThis() {
   }

   public void onFirstOpened() {
   }

   public void tick() {
      if (!hasShiftDown()) {
         this.ticksInBook++;
      }

      if (this.needsBookmarkUpdate) {
         this.needsBookmarkUpdate = false;
         this.addBookmarkButtons();
      }
   }

   final void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      drawFromTexture(graphics, this.book, 0, 0, 0, 0, 272, 180);
   }

   void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
   }

   final void drawTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
      if (this.tooltipStack != null) {
         List<Component> tooltip = Screen.getTooltipFromItem(this.minecraft, this.tooltipStack);
         Pair<BookEntry, Integer> provider = this.book.getContents().getEntryForStack(this.tooltipStack);
         if (provider != null && (!(this instanceof GuiBookEntry) || ((GuiBookEntry)this).entry != provider.getFirst())) {
            Component t = Component.literal("(")
               .append(Component.translatable("patchouli.gui.lexicon.shift_for_recipe"))
               .append(")")
               .withStyle(ChatFormatting.GOLD);
            tooltip.add(t);
            this.targetPage = provider;
         }

         graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
      } else if (this.tooltip != null && !this.tooltip.isEmpty()) {
         graphics.renderComponentTooltip(this.font, this.tooltip, mouseX, mouseY);
      }
   }

   final void resetTooltip() {
      this.tooltipStack = null;
      this.tooltip = null;
      this.targetPage = null;
   }

   public static void drawFromTexture(GuiGraphics graphics, Book book, int x, int y, int u, int v, int w, int h) {
      graphics.blit(book.bookTexture, x, y, u, v, w, h, 512, 256);
   }

   public boolean isPauseScreen() {
      return this.book.pauseGame;
   }

   private void handleButtonBack(Button button) {
      this.back(false);
   }

   public void handleButtonArrow(Button button) {
      this.changePage(((GuiButtonBookArrow)button).left, false);
   }

   public void handleButtonBookmark(Button button) {
      GuiButtonBookBookmark bookmarkButton = (GuiButtonBookBookmark)button;
      PersistentData.Bookmark bookmark = bookmarkButton.bookmark;
      if (bookmark == null || bookmark.getEntry(this.book) == null) {
         this.bookmarkThis();
      } else if (hasShiftDown() && !bookmarkButton.multiblock) {
         List<PersistentData.Bookmark> bookmarks = PersistentData.data.getBookData(this.book).bookmarks;
         bookmarks.remove(bookmark);
         PersistentData.save();
         this.needsBookmarkUpdate = true;
      } else {
         this.displayLexiconGui(new GuiBookEntry(this.book, bookmark.getEntry(this.book), bookmark.spread), true);
      }
   }

   public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      return this.mouseClickedScaled(mouseX / this.scaleFactor, mouseY / this.scaleFactor, mouseButton);
   }

   public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
      switch (mouseButton) {
         case 0:
            if (this.targetPage != null && hasShiftDown()) {
               this.displayLexiconGui(new GuiBookEntry(this.book, (BookEntry)this.targetPage.getFirst(), (Integer)this.targetPage.getSecond()), true);
               playBookFlipSound(this.book);
               return true;
            }
         case 2:
         default:
            for (GuiEventListener listener : this.children()) {
               if (listener.mouseClicked(mouseX, mouseY, mouseButton)) {
                  if (mouseButton == 0) {
                     this.setDragging(true);
                  }

                  return true;
               }
            }

            return false;
         case 1:
            this.back(true);
            return true;
         case 3:
            this.changePage(true, true);
            return true;
         case 4:
            this.changePage(false, true);
            return true;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode) && !this.canSeeBackButton()) {
         this.onClose();
         return true;
      } else if (keyCode == 259) {
         this.back(true);
         return true;
      } else if (this.tooltipStack != null && IXplatAbstractions.INSTANCE.handleRecipeKeybind(keyCode, scanCode, this.tooltipStack)) {
         return true;
      } else {
         return this.tooltipStack != null
               && IXplatAbstractions.INSTANCE.isModLoaded("jei")
               && PatchouliJeiPlugin.handleRecipeKeybind(keyCode, scanCode, this.tooltipStack)
            ? true
            : super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (scrollX < 0.0) {
         this.changePage(false, true);
      } else if (scrollX > 0.0) {
         this.changePage(true, true);
      }

      return true;
   }

   void back(boolean sfx) {
      if (!this.book.getContents().guiStack.isEmpty()) {
         if (hasShiftDown()) {
            this.displayLexiconGui(new GuiBookLanding(this.book), false);
            this.book.getContents().guiStack.clear();
         } else {
            this.displayLexiconGui(this.book.getContents().guiStack.pop(), false);
         }

         if (sfx) {
            playBookFlipSound(this.book);
         }
      }
   }

   void changePage(boolean left, boolean sfx) {
      if (this.canSeePageButton(left)) {
         if (left) {
            this.spread--;
         } else {
            this.spread++;
         }

         this.onPageChanged();
         if (sfx) {
            playBookFlipSound(this.book);
         }
      }
   }

   void onPageChanged() {
   }

   public boolean canBeOpened() {
      return true;
   }

   public boolean canSeePageButton(boolean left) {
      return left ? this.spread > 0 : this.spread + 1 < this.maxSpreads;
   }

   public boolean canSeeBackButton() {
      return !this.book.getContents().guiStack.isEmpty();
   }

   public void setTooltip(Component... strings) {
      this.setTooltip(Arrays.asList(strings));
   }

   public void setTooltip(List<Component> strings) {
      this.tooltip = strings;
   }

   public void setTooltipStack(ItemStack stack) {
      this.setTooltip(Collections.emptyList());
      this.tooltipStack = stack;
   }

   public boolean isMouseInRelativeRange(double absMx, double absMy, int x, int y, int w, int h) {
      double mx = this.getRelativeX(absMx);
      double my = this.getRelativeY(absMy);
      return mx > x && my > y && mx <= x + w && my <= y + h;
   }

   public double getRelativeX(double absX) {
      return absX - this.bookLeft;
   }

   public double getRelativeY(double absY) {
      return absY - this.bookTop;
   }

   public void drawProgressBar(GuiGraphics graphics, Book book, int mouseX, int mouseY, Predicate<BookEntry> filter) {
      if (book.showProgress && book.advancementsEnabled()) {
         int barLeft = 19;
         int barTop = 144;
         int barWidth = 106;
         int barHeight = 12;
         int totalEntries = 0;
         int unlockedEntries = 0;
         int unlockedSecretEntries = 0;

         for (BookEntry entry : book.getContents().entries.values()) {
            if (filter.test(entry)) {
               if (entry.isSecret()) {
                  if (!entry.isLocked()) {
                     unlockedSecretEntries++;
                  }
               } else {
                  BookCategory category = entry.getCategory();
                  if (!category.isSecret() || category.isLocked()) {
                     totalEntries++;
                     if (!entry.isLocked()) {
                        unlockedEntries++;
                     }
                  }
               }
            }
         }

         float unlockFract = unlockedEntries / Math.max(1.0F, (float)totalEntries);
         int progressWidth = (int)((barWidth - 1.0F) * unlockFract);
         graphics.fill(barLeft, barTop, barLeft + barWidth, barTop + barHeight, book.headerColor);
         this.drawGradient(graphics, barLeft + 1, barTop + 1, barLeft + barWidth - 1, barTop + barHeight - 1, book.progressBarBackground);
         this.drawGradient(graphics, barLeft + 1, barTop + 1, barLeft + progressWidth, barTop + barHeight - 1, book.progressBarColor);
         graphics.drawString(this.font, Component.translatable("patchouli.gui.lexicon.progress_meter"), barLeft, barTop - 9, book.headerColor, false);
         if (this.isMouseInRelativeRange(mouseX, mouseY, barLeft, barTop, barWidth, barHeight)) {
            List<Component> tooltip = new ArrayList<>();
            Component progressStr = Component.translatable("patchouli.gui.lexicon.progress_tooltip", new Object[]{unlockedEntries, totalEntries});
            tooltip.add(progressStr);
            if (unlockedSecretEntries > 0) {
               if (unlockedSecretEntries == 1) {
                  tooltip.add(Component.translatable("patchouli.gui.lexicon.progress_tooltip.secret1").withStyle(ChatFormatting.GRAY));
               } else {
                  tooltip.add(
                     Component.translatable("patchouli.gui.lexicon.progress_tooltip.secret", new Object[]{unlockedSecretEntries})
                        .withStyle(ChatFormatting.GRAY)
                  );
               }
            }

            if (unlockedEntries != totalEntries) {
               tooltip.add(Component.translatable("patchouli.gui.lexicon.progress_tooltip.info").withStyle(ChatFormatting.GRAY));
            }

            this.setTooltip(tooltip);
         }
      }
   }

   private void drawGradient(GuiGraphics graphics, int x, int y, int w, int h, int color) {
      int darkerColor = new Color(color).darker().getRGB();
      graphics.fillGradient(x, y, w, h, color, darkerColor);
   }

   public void drawCenteredStringNoShadow(GuiGraphics graphics, FormattedCharSequence s, int x, int y, int color) {
      graphics.drawString(this.font, s, x - this.font.width(s) / 2, y, color, false);
   }

   public void drawCenteredStringNoShadow(GuiGraphics graphics, String s, int x, int y, int color) {
      graphics.drawString(this.font, s, x - this.font.width(s) / 2, y, color, false);
   }

   private int getMaxAllowedScale() {
      return this.minecraft.getWindow().calculateScale(0, this.minecraft.isEnforceUnicode());
   }

   public int getSpread() {
      return this.spread;
   }

   public static void drawSeparator(GuiGraphics graphics, Book book, int x, int y) {
      int w = 110;
      int h = 3;
      int rx = x + 58 - w / 2;
      RenderSystem.enableBlend();
      graphics.setColor(1.0F, 1.0F, 1.0F, 0.8F);
      drawFromTexture(graphics, book, rx, y, 140, 180, w, h);
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawLock(GuiGraphics graphics, Book book, int x, int y) {
      drawFromTexture(graphics, book, x, y, 250, 180, 16, 16);
   }

   public static void drawMarking(GuiGraphics graphics, Book book, int x, int y, int rand, EntryDisplayState state) {
      if (state.hasIcon) {
         RenderSystem.enableBlend();
         float alpha = state.hasAnimation ? (float)Math.sin(ClientTicker.total * 0.2F) * 0.3F + 0.7F : 1.0F;
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
         drawFromTexture(graphics, book, x, y, state.u, 197, 8, 8);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public static void drawPageFiller(GuiGraphics graphics, Book book) {
      drawPageFiller(graphics, book, 141, 18);
   }

   public static void drawPageFiller(GuiGraphics graphics, Book book, int x, int y) {
      RenderSystem.enableBlend();
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      graphics.blit(book.fillerTexture, x + 58 - 64, y + 78 - 74, 0.0F, 0.0F, 128, 128, 128, 128);
   }

   public static void playBookFlipSound(Book book) {
      if (ClientTicker.ticksInGame - lastSound > 6L) {
         SoundEvent sfx = PatchouliSounds.getSound(book.flipSound, PatchouliSounds.BOOK_FLIP);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sfx, (float)(0.7 + Math.random() * 0.3)));
         lastSound = ClientTicker.ticksInGame;
      }
   }

   public static void openWebLink(Screen prevScreen, String address) {
      Minecraft mc = Minecraft.getInstance();
      mc.setScreen(new ConfirmLinkScreen(yes -> {
         if (yes) {
            Util.getPlatform().openUri(address);
         }

         mc.setScreen(prevScreen);
      }, address, false));
   }

   public void displayLexiconGui(GuiBook gui, boolean push) {
      this.book.getContents().openLexiconGui(gui, push);
   }
}
