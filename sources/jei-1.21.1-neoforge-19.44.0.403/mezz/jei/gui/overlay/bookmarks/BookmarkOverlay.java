package mezz.jei.gui.overlay.bookmarks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IBookmarkOverlay;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.IPaged;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.MouseUtil;
import mezz.jei.gui.input.handlers.CombinedDragHandler;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import mezz.jei.gui.input.handlers.NullDragHandler;
import mezz.jei.gui.input.handlers.ProxyDragHandler;
import mezz.jei.gui.input.handlers.ProxyInputHandler;
import mezz.jei.gui.overlay.GuiPropertiesCache;
import mezz.jei.gui.overlay.IScreenPropertiesUpdater;
import mezz.jei.gui.overlay.bookmarks.history.LookupHistoryButtonController;
import mezz.jei.gui.overlay.bookmarks.history.LookupHistoryOverlay;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.ingredients.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.ingredients.IngredientListSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class BookmarkOverlay implements IRecipeFocusSource, IBookmarkOverlay {
   private static final int BORDER_MARGIN = 6;
   private static final int INNER_PADDING = 2;
   private static final int BUTTON_SIZE = 20;
   private static final int LOOKUP_HISTORY_BOTTOM_PADDING = 6;
   private static final int LOOKUP_HISTORY_PADDING_EXTRA = 4;
   private final BookmarkDragManager bookmarkDragManager;
   private final GuiPropertiesCache<Screen> guiPropertiesCache;
   private final IngredientGridWithNavigation contents;
   private final LookupHistoryOverlay lookupHistoryOverlay;
   private final IconButton bookmarkButton;
   private final IconButton historyButton;
   private final BookmarkList bookmarkList;
   private final IClientToggleState toggleState;
   private final IClientConfig clientConfig;
   private boolean screenPropertiesDirty;

   public BookmarkOverlay(
      BookmarkList bookmarkList,
      IngredientGridWithNavigation contents,
      LookupHistoryOverlay lookupHistoryOverlay,
      IClientToggleState toggleState,
      IClientConfig clientConfig,
      IIngredientGridConfig bookmarkListConfig,
      IScreenHelper screenHelper,
      IInternalKeyMappings keyBindings
   ) {
      this.bookmarkList = bookmarkList;
      this.toggleState = toggleState;
      this.clientConfig = clientConfig;
      this.bookmarkButton = new IconButton(new BookmarkButtonController(this, bookmarkList, toggleState, keyBindings));
      this.historyButton = new IconButton(new LookupHistoryButtonController(clientConfig));
      this.contents = contents;
      this.lookupHistoryOverlay = lookupHistoryOverlay;
      this.guiPropertiesCache = new GuiPropertiesCache<>(screen -> screenHelper.getGuiProperties(screen).orElse(null));
      this.bookmarkDragManager = new BookmarkDragManager(this);
      bookmarkList.addSourceListChangedListener(() -> {
         toggleState.setBookmarkEnabled(!bookmarkList.isEmpty());
         this.markScreenPropertiesDirty();
      });
      lookupHistoryOverlay.getLookupHistory().addSourceListChangedListener(this::markScreenPropertiesDirty);
      clientConfig.lookupHistoryEnabled().addListener(v -> this.markScreenPropertiesDirty());
      clientConfig.maxLookupHistoryRows().addListener(v -> this.markScreenPropertiesDirty());
      clientConfig.lookupHistoryDisplaySide().addListener(v -> this.markScreenPropertiesDirty());
      this.addGridConfigListeners(bookmarkListConfig);
   }

   public boolean isListDisplayed() {
      this.updateScreenPropertiesIfDirty();
      return this.toggleState.isBookmarkOverlayEnabled() && this.guiPropertiesCache.hasValidScreen() && this.contents.hasRoom() && !this.bookmarkList.isEmpty();
   }

   public boolean hasRoom() {
      this.updateScreenPropertiesIfDirty();
      return this.contents.hasRoom();
   }

   private void markScreenPropertiesDirty() {
      this.screenPropertiesDirty = true;
   }

   private void addGridConfigListeners(IIngredientGridConfig gridConfig) {
      gridConfig.maxColumns().addListener(v -> this.markScreenPropertiesDirty());
      gridConfig.maxRows().addListener(v -> this.markScreenPropertiesDirty());
      gridConfig.drawBackground().addListener(v -> this.markScreenPropertiesDirty());
      gridConfig.horizontalAlignment().addListener(v -> this.markScreenPropertiesDirty());
      gridConfig.verticalAlignment().addListener(v -> this.markScreenPropertiesDirty());
      gridConfig.navigationVisibility().addListener(v -> this.markScreenPropertiesDirty());
   }

   private void updateScreenPropertiesIfDirty() {
      if (this.screenPropertiesDirty) {
         this.screenPropertiesDirty = false;
         Minecraft minecraft = Minecraft.getInstance();
         this.getScreenPropertiesUpdater().updateScreen(minecraft.screen).forceUpdate();
      }
   }

   public IScreenPropertiesUpdater getScreenPropertiesUpdater() {
      return this.guiPropertiesCache.createUpdater(this::onGuiPropertiesChanged);
   }

   private void onGuiPropertiesChanged() {
      IGuiProperties guiProperties = this.guiPropertiesCache.getGuiProperties();
      if (guiProperties == null) {
         this.contents.close();
         this.lookupHistoryOverlay.close();
      } else {
         this.updateBounds(guiProperties, this.guiPropertiesCache.getGuiExclusionAreas());
      }
   }

   private void updateBounds(IGuiProperties guiProperties, Set<ImmutableRect2i> guiExclusionAreas) {
      ImmutableRect2i displayArea = getDisplayArea(guiProperties);
      ImmutablePoint2i mouseExclusionArea = this.guiPropertiesCache.getMouseExclusionArea();
      ImmutableRect2i availableContentsArea = displayArea.cropBottom(22);
      if (this.clientConfig.lookupHistoryEnabled().getValue() && this.lookupHistoryOverlay.isDisplayedOnThisSide()) {
         int lookupHistoryDisplayHeight = this.lookupHistoryOverlay.getDisplayHeight();
         if (lookupHistoryDisplayHeight > 0) {
            ImmutableRect2i historyArea = displayArea.insetBy(6).cropBottom(26).keepBottom(lookupHistoryDisplayHeight);
            availableContentsArea = cropBottomTo(availableContentsArea, historyArea.y() - 4);
            this.lookupHistoryOverlay.updateBounds(historyArea, guiExclusionAreas, mouseExclusionArea);
            this.lookupHistoryOverlay.updateLayout();
         }
      }

      IElement<?> pageAnchorElement = this.contents.getPageAnchorElement();
      this.contents.updateBounds(availableContentsArea, guiExclusionAreas, mouseExclusionArea);
      this.contents.updateLayoutKeepingPageAnchorVisible(pageAnchorElement);
      if (this.contents.hasRoom()) {
         ImmutableRect2i contentsArea = this.contents.getBackgroundArea();
         ImmutableRect2i bookmarkButtonArea = displayArea.insetBy(6).matchWidthAndX(contentsArea).keepBottom(20).keepLeft(20);
         this.bookmarkButton.updateBounds(bookmarkButtonArea);
         ImmutableRect2i historyButtonArea = bookmarkButtonArea.moveRight(22);
         this.historyButton.updateBounds(historyButtonArea);
      } else {
         ImmutableRect2i bookmarkButtonArea = displayArea.insetBy(6).keepBottom(20).keepLeft(20);
         this.bookmarkButton.updateBounds(bookmarkButtonArea);
         ImmutableRect2i historyButtonArea = bookmarkButtonArea.moveRight(22);
         this.historyButton.updateBounds(historyButtonArea);
      }
   }

   private static ImmutableRect2i getDisplayArea(IGuiProperties guiProperties) {
      int width = guiProperties.guiLeft();
      if (width <= 0) {
         width = 0;
      }

      int screenHeight = guiProperties.screenHeight();
      return new ImmutableRect2i(0, 0, width, screenHeight);
   }

   private static ImmutableRect2i cropBottomTo(ImmutableRect2i area, int bottomY) {
      int cropAmount = getBottom(area) - bottomY;
      return cropAmount <= 0 ? area : area.cropBottom(cropAmount);
   }

   private static int getBottom(ImmutableRect2i area) {
      return area.y() + area.height();
   }

   public void drawScreen(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(guiGraphics);
      this.drawForeground(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
   }

   public void drawBackground(GuiGraphics guiGraphics) {
      if (this.isListDisplayed()) {
         this.contents.drawBackground(guiGraphics);
      }

      if (this.guiPropertiesCache.hasValidScreen() && this.toggleState.isOverlayEnabled()) {
         this.lookupHistoryOverlay.drawBackground(guiGraphics);
      }
   }

   public void drawForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      if (this.isListDisplayed()) {
         this.bookmarkDragManager.updateDrag(mouseX, mouseY);
         this.contents.drawForeground(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
      }

      if (this.guiPropertiesCache.hasValidScreen() && this.toggleState.isOverlayEnabled()) {
         this.lookupHistoryOverlay.draw(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
      }

      if (this.guiPropertiesCache.hasValidScreen()) {
         this.bookmarkButton.draw(guiGraphics, mouseX, mouseY, partialTicks);
         this.historyButton.draw(guiGraphics, mouseX, mouseY, partialTicks);
      }
   }

   public void drawTooltips(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY) {
      this.updateScreenPropertiesIfDirty();
      if (!this.bookmarkDragManager.drawDraggedItem(guiGraphics, mouseX, mouseY)) {
         if (this.isListDisplayed()) {
            this.contents.drawTooltips(minecraft, guiGraphics, mouseX, mouseY);
         }

         if (this.guiPropertiesCache.hasValidScreen() && this.toggleState.isOverlayEnabled()) {
            this.lookupHistoryOverlay.drawTooltips(minecraft, guiGraphics, mouseX, mouseY);
         }
      }

      if (this.guiPropertiesCache.hasValidScreen()) {
         this.bookmarkButton.drawTooltips(guiGraphics, mouseX, mouseY);
         this.historyButton.drawTooltips(guiGraphics, mouseX, mouseY);
      }
   }

   public void tick() {
      if (this.isListDisplayed()) {
         this.contents.tick();
      }

      if (this.guiPropertiesCache.hasValidScreen() && this.toggleState.isOverlayEnabled()) {
         this.lookupHistoryOverlay.tick();
      }
   }

   @Override
   public Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double mouseX, double mouseY) {
      this.updateScreenPropertiesIfDirty();
      if (this.isListDisplayed()) {
         return Stream.concat(this.contents.getIngredientUnderMouse(mouseX, mouseY), this.lookupHistoryOverlay.getIngredientUnderMouse(mouseX, mouseY));
      } else {
         return this.lookupHistoryOverlay.isListDisplayed() ? this.lookupHistoryOverlay.getIngredientUnderMouse(mouseX, mouseY) : Stream.empty();
      }
   }

   @Override
   public Stream<IDraggableIngredientInternal<?>> getDraggableIngredientUnderMouse(double mouseX, double mouseY) {
      this.updateScreenPropertiesIfDirty();
      if (this.isListDisplayed()) {
         return Stream.concat(
            this.contents.getDraggableIngredientUnderMouse(mouseX, mouseY), this.lookupHistoryOverlay.getDraggableIngredientUnderMouse(mouseX, mouseY)
         );
      } else {
         return this.lookupHistoryOverlay.isListDisplayed() ? this.lookupHistoryOverlay.getDraggableIngredientUnderMouse(mouseX, mouseY) : Stream.empty();
      }
   }

   @Override
   public Optional<ITypedIngredient<?>> getIngredientUnderMouse() {
      double mouseX = MouseUtil.getX();
      double mouseY = MouseUtil.getY();
      return this.getIngredientUnderMouse(mouseX, mouseY).map(IClickableIngredientInternal::getTypedIngredient).findFirst();
   }

   @Nullable
   @Override
   public <T> T getIngredientUnderMouse(IIngredientType<T> ingredientType) {
      double mouseX = MouseUtil.getX();
      double mouseY = MouseUtil.getY();
      return this.getIngredientUnderMouse(mouseX, mouseY)
         .map(IClickableIngredientInternal::getTypedIngredient)
         .map(i -> i.getIngredient(ingredientType))
         .flatMap(Optional::stream)
         .findFirst()
         .orElse(null);
   }

   public IUserInputHandler createInputHandler() {
      IUserInputHandler bookmarkButtonInputHandler = this.bookmarkButton.createInputHandler();
      IUserInputHandler historyButtonInputHandler = this.historyButton.createInputHandler();
      IUserInputHandler buttonInputHandler = new CombinedInputHandler("BookmarkOverlayButton", bookmarkButtonInputHandler, historyButtonInputHandler);
      IUserInputHandler displayedInputHandler = new CombinedInputHandler("BookmarkOverlay", this.contents.createInputHandler(), buttonInputHandler);
      return new ProxyInputHandler(() -> this.isListDisplayed() ? displayedInputHandler : buttonInputHandler);
   }

   public IDragHandler createDragHandler() {
      IDragHandler lookupHistoryDragHandler = this.lookupHistoryOverlay.createDragHandler();
      IDragHandler combinedDragHandlers = new CombinedDragHandler(
         this.contents.createDragHandler(), lookupHistoryDragHandler, this.bookmarkDragManager.createDragHandler()
      );
      return new ProxyDragHandler(() -> {
         if (this.isListDisplayed()) {
            return combinedDragHandlers;
         } else {
            return (IDragHandler)(this.lookupHistoryOverlay.isListDisplayed() ? lookupHistoryDragHandler : NullDragHandler.INSTANCE);
         }
      });
   }

   public void drawOnForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      this.updateScreenPropertiesIfDirty();
      if (this.isListDisplayed()) {
         this.contents.drawOnForeground(guiGraphics, mouseX, mouseY);
      }

      this.lookupHistoryOverlay.drawOnForeground(guiGraphics, mouseX, mouseY);
   }

   public List<IBookmarkDragTarget> createBookmarkDragTargets() {
      this.updateScreenPropertiesIfDirty();
      List<BookmarkOverlay.DragTarget> slotTargets = this.contents
         .getSlots()
         .map(this::createDragTarget)
         .filter(Optional::isPresent)
         .map(Optional::get)
         .toList();
      IBookmark firstBookmark = ((BookmarkOverlay.DragTarget)slotTargets.getFirst()).bookmark;
      IBookmark lastBookmark = ((BookmarkOverlay.DragTarget)slotTargets.getLast()).bookmark;
      List<IBookmarkDragTarget> bookmarkDragTargets = new ArrayList<>(slotTargets);
      IPaged pageDelegate = this.contents.getPageDelegate();
      if (pageDelegate.getPageCount() > 1) {
         bookmarkDragTargets.add(
            new BookmarkOverlay.ActionDragTarget(this.contents.getNextPageButtonArea(), lastBookmark, this.bookmarkList, 1, pageDelegate::nextPage)
         );
         bookmarkDragTargets.add(
            new BookmarkOverlay.ActionDragTarget(this.contents.getBackButtonArea(), firstBookmark, this.bookmarkList, -1, pageDelegate::previousPage)
         );
      }

      bookmarkDragTargets.add(new BookmarkOverlay.DragTarget(this.contents.getSlotBackgroundArea(), lastBookmark, this.bookmarkList, 0));
      return bookmarkDragTargets;
   }

   private Optional<BookmarkOverlay.DragTarget> createDragTarget(IngredientListSlot ingredientListSlot) {
      return ingredientListSlot.getOptionalElement()
         .flatMap(IElement::getBookmark)
         .map(bookmark -> new BookmarkOverlay.DragTarget(ingredientListSlot.getArea(), bookmark, this.bookmarkList, 0));
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.contents.isMouseOver(mouseX, mouseY);
   }

   public static class ActionDragTarget extends BookmarkOverlay.DragTarget {
      private final Runnable action;

      public ActionDragTarget(ImmutableRect2i area, IBookmark bookmark, BookmarkList bookmarkList, int offset, Runnable action) {
         super(area, bookmark, bookmarkList, offset);
         this.action = action;
      }

      @Override
      public void accept(IBookmark bookmark) {
         super.accept(bookmark);
         this.action.run();
      }
   }

   public static class DragTarget implements IBookmarkDragTarget {
      private final ImmutableRect2i area;
      private final IBookmark bookmark;
      private final BookmarkList bookmarkList;
      private final int offset;

      public DragTarget(ImmutableRect2i area, IBookmark bookmark, BookmarkList bookmarkList, int offset) {
         this.area = area;
         this.bookmark = bookmark;
         this.bookmarkList = bookmarkList;
         this.offset = offset;
      }

      @Override
      public ImmutableRect2i getArea() {
         return this.area;
      }

      @Override
      public void accept(IBookmark bookmark) {
         this.bookmarkList.moveBookmark(this.bookmark, bookmark, this.offset);
      }
   }
}
