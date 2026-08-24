package me.flashyreese.mods.reeses_sodium_options.client.gui;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.BasicFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action.OptionUndoAction;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab.Tab;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab.TabFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.search.SearchTextFieldWidget;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionsScreenUiState;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.FlatButtonWidget;
import net.caffeinemc.mods.sodium.client.SodiumClientMod;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.ExternalPage;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.caffeinemc.mods.sodium.client.data.fingerprint.HashedFingerprint;
import net.caffeinemc.mods.sodium.client.gui.SodiumOptions;
import net.caffeinemc.mods.sodium.client.gui.prompt.ScreenPrompt;
import net.caffeinemc.mods.sodium.client.gui.prompt.ScreenPromptable;
import net.caffeinemc.mods.sodium.client.gui.prompt.ScreenPrompt.Action;
import net.caffeinemc.mods.sodium.client.services.PlatformRuntimeInformation;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.Util;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ComponentPath.Path;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.Screen.NarratableSearchResult;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SodiumVideoOptionsScreen extends Screen implements ScreenPromptable, PreviousScreenHolder {
   private static final OptionsScreenUiState SHARED_UI_STATE = new OptionsScreenUiState();
   private static final Set<String> RSO_CONFIG_IDS = Set.of("reeses-sodium-options", "reeses_sodium_options");
   private static final double ASPECT_RATIO_16_9 = 1.7777777777777777;
   private static final int TOOLBAR_BUTTON_WIDTH = 65;
   private static final int TOOLBAR_BUTTON_GAP = 4;
   private static final int TOOLBAR_BUTTON_Y_GAP = 5;
   private static final int TOP_ROW_HEIGHT = 20;
   private static final int TOP_ROW_Y_GAP = 26;
   private static final List<FormattedText> DONATION_PROMPT_MESSAGE = List.of(
      FormattedText.composite(new FormattedText[]{Component.literal("Hello!")}),
      FormattedText.composite(
         new FormattedText[]{
            Component.literal("It seems that you've been enjoying "),
            Component.literal("Sodium").withColor(2616210),
            Component.literal(", the free and open-source optimization mod for Minecraft.")
         }
      ),
      FormattedText.composite(
         new FormattedText[]{
            Component.literal("Mods like these are complex. They require "),
            Component.literal("thousands of hours").withColor(16739840),
            Component.literal(" of development, debugging, and tuning to create the experience that players have come to expect.")
         }
      ),
      FormattedText.composite(
         new FormattedText[]{
            Component.literal("If you'd like to show your token of appreciation, and support the development of our mod in the process, then consider "),
            Component.literal("buying us a coffee").withColor(15550926),
            Component.literal(".")
         }
      ),
      FormattedText.composite(new FormattedText[]{Component.literal("And thanks again for using our mod! We hope it helps you (and your computer.)")})
   );
   private final Screen prevScreen;
   private final OptionsScreenUiState uiState;
   private FlatButtonWidget applyButton;
   private FlatButtonWidget closeButton;
   private FlatButtonWidget undoButton;
   private FlatButtonWidget donateButton;
   private FlatButtonWidget hideDonateButton;
   private boolean hasPendingChanges;
   private SearchTextFieldWidget searchTextField;
   private AbstractFrame rootFrame;
   private TabFrame tabFrame;
   @Nullable
   private ScreenPrompt prompt;
   @Nullable
   private ComponentPath previousArrowFocusPath;
   @Nullable
   private GuiEventListener currentArrowFocusLeaf;
   @Nullable
   private ScreenDirection lastArrowDirection;
   @Nullable
   private NarratableEntry lastNarratable;

   public SodiumVideoOptionsScreen(Screen prev) {
      super(Component.literal("Reese's Sodium Menu"));
      this.prevScreen = prev;
      this.uiState = SHARED_UI_STATE;
      this.checkPromptTimers();
      ConfigManager.CONFIG.resetAllOptionsFromBindings();
   }

   @Override
   public Screen rso$previousScreen() {
      return this.prevScreen;
   }

   @Nullable
   public FlatButtonWidget rso$getApplyButton() {
      return this.applyButton;
   }

   @Nullable
   public FlatButtonWidget rso$getCloseButton() {
      return this.closeButton;
   }

   @Nullable
   public FlatButtonWidget rso$getUndoButton() {
      return this.undoButton;
   }

   @Nullable
   public SearchTextFieldWidget rso$getSearchTextField() {
      return this.searchTextField;
   }

   @Nullable
   public TabFrame rso$getTabFrame() {
      return this.tabFrame;
   }

   private void checkPromptTimers() {
      if (!PlatformRuntimeInformation.getInstance().isDevelopmentEnvironment()) {
         SodiumOptions options = SodiumClientMod.options();
         if (!options.notifications.hasSeenDonationPrompt) {
            HashedFingerprint fingerprint = null;

            try {
               fingerprint = HashedFingerprint.loadFromDisk();
            } catch (Throwable var5) {
               SodiumClientMod.logger().error("Failed to read the fingerprint from disk", var5);
            }

            if (fingerprint != null) {
               Instant now = Instant.now();
               Instant threshold = Instant.ofEpochSecond(fingerprint.timestamp()).plus(3L, ChronoUnit.DAYS);
               if (now.isAfter(threshold)) {
                  this.openDonationPrompt(options);
               }
            }
         }
      }
   }

   private void openDonationPrompt(SodiumOptions options) {
      ScreenPrompt prompt = new ScreenPrompt(this, DONATION_PROMPT_MESSAGE, 320, 190, new Action(Component.literal("Buy us a coffee"), this::openDonationPage));
      prompt.setFocused(true);
      options.notifications.hasSeenDonationPrompt = true;

      try {
         SodiumOptions.writeToDisk(options);
      } catch (IOException var4) {
         SodiumClientMod.logger().error("Failed to update config file", var4);
      }
   }

   public void rebuildUI() {
      boolean wasSearchBarFocused = this.searchTextField.isFocused();
      this.clearArrowNavigationMemory();
      this.rebuildWidgets();
      if (wasSearchBarFocused) {
         this.focusSearchTextField();
      }
   }

   private void refreshSearchResults() {
      this.clearArrowNavigationMemory();
      if (this.tabFrame != null) {
         this.tabFrame.refreshFromState();
      }
   }

   protected void init() {
      super.init();
      BaseWidget.setKeyboardFocusVisible(this.minecraft.getLastInputType().isKeyboard());
      ConfigManager.CONFIG.invalidateGlobalRebuildDependents();
      this.rootFrame = this.parentFrameBuilder().build();
      this.addRenderableWidget(this.rootFrame);
      if (this.searchTextField.isFocused()) {
         this.focusSearchTextField();
      } else if (this.restoreFocusedOptionForSelectedTab()) {
         this.rememberCurrentOptionFocus();
      } else {
         this.setFocused(this.rootFrame);
      }

      if (this.prompt != null) {
         this.prompt.init();
      }
   }

   protected void updateNarratedWidget(NarrationElementOutput narrationElementOutput) {
      List<NarratableEntry> narratables = this.rootFrame == null
         ? List.of()
         : this.rootFrame
            .collectNarratables()
            .stream()
            .filter(NarratableEntry::isActive)
            .sorted(Comparator.comparingInt(TabOrderedElement::getTabOrderGroup))
            .toList();
      NarratableSearchResult result = Screen.findNarratableWidget(narratables, this.lastNarratable);
      if (result != null) {
         if (result.priority.isTerminal()) {
            this.lastNarratable = result.entry;
         }

         if (narratables.size() > 1) {
            narrationElementOutput.add(
               NarratedElementType.POSITION, Component.translatable("narrator.position.screen", new Object[]{result.index + 1, narratables.size()})
            );
            if (result.priority == NarrationPriority.FOCUSED) {
               narrationElementOutput.add(NarratedElementType.USAGE, this.getUsageNarration());
            }
         }

         result.entry.updateNarration(narrationElementOutput.nest());
      }
   }

   private void focusSearchTextField() {
      this.searchTextField.setFocused(true);
      if (this.rootFrame != null) {
         this.rootFrame.setFocused(this.searchTextField);
         this.setFocused(this.rootFrame);
      } else {
         this.setFocused(this.searchTextField);
      }
   }

   private BasicFrame.Builder parentFrameBuilder() {
      boolean donationCleared = SodiumClientMod.options().notifications.hasClearedDonationButton;
      int newWidth = this.width;
      if ((float)this.width / this.height > 1.7777777777777777) {
         newWidth = (int)(this.height * 1.7777777777777777);
      }

      LayoutBounds basicFrameDim = new LayoutBounds((this.width - newWidth) / 2, 0, newWidth, this.height);
      LayoutBounds tabFrameDim = new LayoutBounds(
         basicFrameDim.x() + basicFrameDim.width() / 20 / 2,
         basicFrameDim.y() + basicFrameDim.height() / 4 / 2,
         basicFrameDim.width() - basicFrameDim.width() / 20,
         basicFrameDim.height() / 4 * 3
      );
      int toolbarY = tabFrameDim.getLimitY() + 5;
      LayoutBounds closeButtonDim = new LayoutBounds(toolbarButtonX(tabFrameDim, 0), toolbarY, 65, 20);
      LayoutBounds applyButtonDim = new LayoutBounds(toolbarButtonX(tabFrameDim, 1), toolbarY, 65, 20);
      LayoutBounds undoButtonDim = new LayoutBounds(toolbarButtonX(tabFrameDim, 2), toolbarY, 65, 20);
      Component donationText = Component.translatable("sodium.options.buttons.donate");
      int donationTextWidth = this.minecraft.font.width(donationText);
      int topRowY = tabFrameDim.y() - 26;
      LayoutBounds donateButtonDim = new LayoutBounds(tabFrameDim.getLimitX() - 32 - donationTextWidth, topRowY, 10 + donationTextWidth, 20);
      LayoutBounds hideDonateButtonDim = new LayoutBounds(tabFrameDim.getLimitX() - 20, topRowY, 20, 20);
      this.undoButton = new FlatButtonWidget(
         undoButtonDim, Component.translatable("sodium.options.buttons.undo"), ConfigManager.CONFIG::resetAllOptionsFromBindings, true, false
      );
      this.applyButton = new FlatButtonWidget(
         applyButtonDim, Component.translatable("sodium.options.buttons.apply"), ConfigManager.CONFIG::applyAllOptions, true, false
      );
      this.closeButton = new FlatButtonWidget(closeButtonDim, Component.translatable("gui.done"), this::onClose, true, false);
      this.donateButton = new FlatButtonWidget(donateButtonDim, donationText, this::openDonationPage, true, false);
      this.hideDonateButton = new FlatButtonWidget(hideDonateButtonDim, Component.literal("x"), this::hideDonationButton, true, false);
      if (donationCleared) {
         this.setDonationButtonVisibility(false);
      }

      BasicFrame.Builder basicFrameBuilder = this.parentBasicFrameBuilder(basicFrameDim, tabFrameDim);
      LayoutBounds searchTextFieldDim;
      if (donationCleared) {
         searchTextFieldDim = new LayoutBounds(tabFrameDim.x(), topRowY, tabFrameDim.width(), 20);
      } else {
         searchTextFieldDim = new LayoutBounds(tabFrameDim.x(), topRowY, tabFrameDim.width() - (tabFrameDim.getLimitX() - donateButtonDim.x()) - 2, 20);
         basicFrameBuilder.addChild(() -> this.donateButton).addChild(() -> this.hideDonateButton);
      }

      this.searchTextField = new SearchTextFieldWidget(
         searchTextFieldDim, getOrderedModOptions(), this.uiState, tabFrameDim.height(), this::refreshSearchResults
      );
      basicFrameBuilder.addChild(() -> this.searchTextField);
      return basicFrameBuilder;
   }

   private static int toolbarButtonX(LayoutBounds tabFrameDim, int slotFromRight) {
      return tabFrameDim.getLimitX() - (slotFromRight + 1) * 65 - slotFromRight * 4;
   }

   private BasicFrame.Builder parentBasicFrameBuilder(LayoutBounds parentBasicFrameDim, LayoutBounds tabFrameDim) {
      return BasicFrame.builder()
         .withDimension(parentBasicFrameDim)
         .withRenderOutline(false)
         .withScreen(this)
         .addChild(
            () -> {
               this.tabFrame = TabFrame.createBuilder()
                  .setDimension(tabFrameDim)
                  .withScreen(this)
                  .shouldRenderOutline(false)
                  .setTabRailScrollBarOffset(this.uiState.tabFrameScrollBarOffset())
                  .setScrollSelectedTabIntoView(this.uiState.scrollSelectedTabIntoView())
                  .setTabRailSelectedTab(this.uiState.tabFrameSelectedTab())
                  .setTabRailSelectedGroup(this.uiState.tabFrameSelectedGroup())
                  .setManuallyCollapsedTabGroups(this.uiState.manuallyCollapsedTabGroups())
                  .setOptionPageScrollBarOffset(this.uiState.optionPageScrollBarOffset())
                  .setOptionStateStore(this.uiState)
                  .addTabs(
                     tabs -> getOrderedModOptions()
                        .forEach(
                           config -> config.pages()
                              .forEach(page -> tabs.add(Tab.builder().from(this, config, page, this.uiState.optionPageScrollBarOffset(), this.uiState)))
                        )
                  )
                  .onSetTab(() -> this.uiState.optionPageScrollBarOffset().set(0))
                  .build();
               return this.tabFrame;
            }
         )
         .addChild(() -> this.undoButton)
         .addChild(() -> this.applyButton)
         .addChild(() -> this.closeButton);
   }

   private static List<ModOptions> getOrderedModOptions() {
      return ConfigManager.CONFIG.getModOptions().stream().sorted((left, right) -> Boolean.compare(isOwnConfig(left), isOwnConfig(right))).toList();
   }

   private static boolean isOwnConfig(ModOptions modOptions) {
      return RSO_CONFIG_IDS.contains(modOptions.configId());
   }

   public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.updateControls();
      super.render(guiGraphics, this.prompt != null ? -1 : mouseX, this.prompt != null ? -1 : mouseY, delta);
      if (this.prompt != null) {
         this.prompt.render(guiGraphics, mouseX, mouseY, delta);
      }
   }

   private void updateControls() {
      boolean hasChanges = ConfigManager.CONFIG.anyOptionChanged();
      this.applyButton.setEnabled(hasChanges);
      this.undoButton.setVisible(hasChanges);
      this.closeButton.setEnabled(!hasChanges);
      this.hasPendingChanges = hasChanges;
   }

   private void setDonationButtonVisibility(boolean value) {
      this.donateButton.setVisible(value);
      this.hideDonateButton.setVisible(value);
   }

   private void hideDonationButton() {
      SodiumOptions options = SodiumClientMod.options();
      options.notifications.hasClearedDonationButton = true;

      try {
         SodiumOptions.writeToDisk(options);
      } catch (IOException var3) {
         throw new RuntimeException("Failed to save configuration", var3);
      }

      this.setDonationButtonVisibility(false);
      this.rebuildUI();
   }

   private void openDonationPage() {
      Util.getPlatform().openUri("https://caffeinemc.net/donate");
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.clearArrowNavigationMemory();
      String previousTabKey = this.getSelectedTabKey();
      if (this.prompt != null) {
         return this.prompt.mouseClicked(mouseX, mouseY, button);
      } else {
         boolean handled = super.mouseClicked(mouseX, mouseY, button);
         this.afterInput(previousTabKey);
         return handled;
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      boolean handled = super.mouseReleased(mouseX, mouseY, button);
      if (button == 0 && this.rootFrame != null) {
         this.rootFrame.releaseActionButtonLayoutHolds();
      }

      return handled;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      BaseWidget.setKeyboardFocusVisible(true);
      if (this.prompt != null) {
         return this.prompt.keyPressed(keyCode, scanCode, modifiers);
      } else {
         String previousTabKey = this.getSelectedTabKey();
         if (this.isSearchShortcut(keyCode)) {
            this.focusSearchTextField();
            this.searchTextField.selectAllText();
            this.clearArrowNavigationMemory();
            return true;
         } else if (keyCode == 256 && this.handleFocusedOptionBackNavigation()) {
            this.clearArrowNavigationMemory();
            this.afterInput(previousTabKey);
            return true;
         } else if (keyCode == 256 && this.clearSearchText()) {
            this.clearArrowNavigationMemory();
            return true;
         } else if (keyCode == 80 && (modifiers & 1) != 0 && !this.isSearchTextFieldFocused()) {
            this.minecraft.setScreen(new VideoSettingsScreen(this.prevScreen, this.minecraft, this.minecraft.options));
            return true;
         } else {
            if (!this.isSearchTextFieldFocused()) {
               if (this.isUndoShortcut(keyCode) && this.undoFocusedOption()) {
                  this.clearArrowNavigationMemory();
                  this.afterInput(previousTabKey);
                  return true;
               }

               if (this.keyPressedOptionListNavigation(keyCode)) {
                  this.clearArrowNavigationMemory();
                  this.afterInput(previousTabKey);
                  return true;
               }

               if (this.isApplyShortcut(keyCode)) {
                  GuiEventListener focused = this.getFocused();
                  if (focused != null && focused.keyPressed(keyCode, scanCode, modifiers)) {
                     this.clearArrowNavigationMemory();
                     this.afterInput(previousTabKey);
                     return true;
                  }

                  if (ConfigManager.CONFIG.anyOptionChanged()) {
                     ConfigManager.CONFIG.applyAllOptions();
                     this.updateControls();
                     this.clearArrowNavigationMemory();
                     this.afterInput(previousTabKey);
                     return true;
                  }
               }
            }

            ScreenDirection arrowDirection = getArrowDirection(keyCode);
            if (arrowDirection != null) {
               return this.keyPressedArrow(keyCode, scanCode, modifiers, arrowDirection);
            } else {
               this.clearArrowNavigationMemory();
               boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
               if (handled) {
                  this.afterInput(previousTabKey);
               }

               return handled;
            }
         }
      }
   }

   private boolean isSearchShortcut(int keyCode) {
      return keyCode == 70 && Screen.hasControlDown();
   }

   private boolean isUndoShortcut(int keyCode) {
      return keyCode == 90 && Screen.hasControlDown();
   }

   private boolean isApplyShortcut(int keyCode) {
      return keyCode == 257 || keyCode == 335;
   }

   private boolean isSearchTextFieldFocused() {
      return this.searchTextField != null && this.searchTextField.isFocused();
   }

   private boolean clearSearchText() {
      if (this.searchTextField != null && this.searchTextField.hasText()) {
         this.searchTextField.clearText();
         this.focusSearchTextField();
         return true;
      } else {
         return false;
      }
   }

   private boolean keyPressedOptionListNavigation(int keyCode) {
      if (this.tabFrame != null && this.rootFrame != null) {
         boolean handled = true;
         OptionRow target;
         switch (keyCode) {
            case 266:
               handled = this.tabFrame.scrollSelectedPage(-1);
               target = this.tabFrame.findFirstVisibleSelectedOptionRow();
               break;
            case 267:
               handled = this.tabFrame.scrollSelectedPage(1);
               target = this.tabFrame.findLastVisibleSelectedOptionRow();
               break;
            case 268:
               this.tabFrame.scrollSelectedPageToStart();
               target = this.tabFrame.findFirstSelectedOptionRow();
               break;
            case 269:
               this.tabFrame.scrollSelectedPageToEnd();
               target = this.tabFrame.findLastSelectedOptionRow();
               break;
            default:
               return false;
         }

         return this.focusOptionRow(target) || handled;
      } else {
         return false;
      }
   }

   private boolean undoFocusedOption() {
      OptionRow focusedOptionRow = this.getFocusedOptionRow();
      if (!(focusedOptionRow != null && focusedOptionRow.getOption() instanceof StatefulOption<?> option)) {
         return false;
      } else if (focusedOptionRow.undoFocusedActionButton()) {
         this.updateControls();
         return true;
      } else if (!OptionUndoAction.canUndo(option)) {
         return false;
      } else {
         OptionUndoAction.undoChanges(option);
         focusedOptionRow.clearActionButtonFocus();
         this.updateControls();
         return true;
      }
   }

   private boolean restoreFocusedOptionForSelectedTab() {
      if (this.tabFrame == null) {
         return false;
      } else {
         String tabKey = this.getSelectedTabKey();
         if (tabKey == null) {
            return false;
         } else {
            ResourceLocation optionId = this.uiState.focusedOptionIdsByTab().get(tabKey);
            return optionId != null && this.focusOptionRow(this.tabFrame.findSelectedOptionRow(optionId));
         }
      }
   }

   private boolean focusOptionRow(@Nullable OptionRow optionRow) {
      if (optionRow != null && this.rootFrame != null && this.rootFrame.focusOptionRow(optionRow)) {
         this.setFocused(this.rootFrame);
         return true;
      } else {
         return false;
      }
   }

   private void afterInput(@Nullable String previousTabKey) {
      if (!this.isSearchTextFieldFocused()) {
         if (Objects.equals(previousTabKey, this.getSelectedTabKey()) || !this.restoreFocusedOptionForSelectedTab()) {
            this.rememberCurrentOptionFocus();
         }
      }
   }

   private void rememberCurrentOptionFocus() {
      OptionRow focusedOptionRow = this.getFocusedOptionRow();
      String tabKey = this.getSelectedTabKey();
      if (tabKey != null && focusedOptionRow != null && focusedOptionRow.getOption() instanceof OptionExtended optionExtended) {
         this.uiState.focusedOptionIdsByTab().put(tabKey, optionExtended.rso$getId());
      }
   }

   @Nullable
   private OptionRow getFocusedOptionRow() {
      return this.rootFrame == null ? null : findFocusedOptionRow(this.rootFrame);
   }

   private boolean handleFocusedOptionBackNavigation() {
      OptionRow focusedOptionRow = this.getFocusedOptionRow();
      return focusedOptionRow != null && focusedOptionRow.handleBackNavigation();
   }

   @Nullable
   private static OptionRow findFocusedOptionRow(GuiEventListener listener) {
      if (listener instanceof OptionRow optionRow && optionRow.isFocused()) {
         return optionRow;
      } else {
         if (listener instanceof ContainerEventHandler container) {
            GuiEventListener focused = container.getFocused();
            if (focused != null) {
               return findFocusedOptionRow(focused);
            }
         }

         return null;
      }
   }

   @Nullable
   private String getSelectedTabKey() {
      return this.tabFrame == null ? null : this.tabFrame.getSelectedTabKey().orElse(null);
   }

   @Nullable
   public String rso$getSelectedTabKey() {
      return this.getSelectedTabKey();
   }

   public boolean rso$cycleTab(int direction) {
      BaseWidget.setKeyboardFocusVisible(true);
      if (this.tabFrame != null && direction != 0) {
         List<Tab<?>> tabs = this.tabFrame.getTabs();
         if (tabs.isEmpty()) {
            return false;
         } else {
            int currentIndex = this.tabFrame.getSelectedTab().map(tabs::indexOf).filter(index -> index >= 0).orElse(0);

            for (int offset = 1; offset <= tabs.size(); offset++) {
               int nextIndex = Math.floorMod(currentIndex + direction * offset, tabs.size());
               Tab<?> nextTab = tabs.get(nextIndex);
               if (!(nextTab.getPage() instanceof ExternalPage)) {
                  this.clearArrowNavigationMemory();
                  this.uiState.scrollSelectedTabIntoView().set(true);
                  this.tabFrame.setTab(Optional.of(nextTab));
                  this.focusFirstOptionInSelectedTab();
                  this.rememberCurrentOptionFocus();
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public boolean rso$focusFirstOptionInSelectedTab() {
      BaseWidget.setKeyboardFocusVisible(true);
      return this.focusFirstOptionInSelectedTab();
   }

   private boolean focusFirstOptionInSelectedTab() {
      return this.tabFrame == null ? false : this.focusOptionRow(this.tabFrame.findFirstSelectedOptionRow());
   }

   public boolean rso$navigateController(ScreenDirection direction) {
      int keyCode = keyForDirection(direction);
      BaseWidget.setKeyboardFocusVisible(true);
      return this.prompt != null ? this.keyPressed(keyCode, 0, 0) : this.keyPressedArrow(keyCode, 0, 0, direction);
   }

   public boolean rso$handleControllerBack() {
      BaseWidget.setKeyboardFocusVisible(true);
      return this.keyPressed(256, 0, 0);
   }

   public boolean rso$handleControllerPress() {
      BaseWidget.setKeyboardFocusVisible(true);
      return this.keyPressed(257, 0, 0);
   }

   public void rso$afterControllerInput(@Nullable String previousTabKey) {
      this.afterInput(previousTabKey);
   }

   @Nullable
   public GuiEventListener rso$getFocusedLeaf() {
      return focusedLeaf(this.getFocused());
   }

   @Nullable
   private static GuiEventListener focusedLeaf(@Nullable GuiEventListener listener) {
      if (listener instanceof ContainerEventHandler container) {
         GuiEventListener focused = container.getFocused();
         if (focused != null) {
            return focusedLeaf(focused);
         }
      }

      return listener;
   }

   private boolean keyPressedArrow(int keyCode, int scanCode, int modifiers, ScreenDirection direction) {
      GuiEventListener focused = this.getFocused();
      if (focused != null && focused.keyPressed(keyCode, scanCode, modifiers)) {
         this.clearArrowNavigationMemory();
         this.rememberCurrentOptionFocus();
         return true;
      } else {
         ComponentPath currentFocusPath = this.getCurrentFocusPath();
         if (this.restorePreviousArrowFocus(direction, currentFocusPath)) {
            this.rememberCurrentOptionFocus();
            return true;
         } else {
            ComponentPath nextFocusPath = this.nextFocusPath(new ArrowNavigation(direction));
            if (nextFocusPath == null) {
               this.clearArrowNavigationMemory();
               return false;
            } else {
               this.changeFocus(nextFocusPath);
               this.rememberArrowNavigation(direction, currentFocusPath, nextFocusPath);
               this.rememberCurrentOptionFocus();
               return true;
            }
         }
      }
   }

   private boolean restorePreviousArrowFocus(ScreenDirection direction, @Nullable ComponentPath currentFocusPath) {
      if (this.previousArrowFocusPath != null
         && this.lastArrowDirection != null
         && currentFocusPath != null
         && direction == this.lastArrowDirection.getOpposite()
         && leafComponent(currentFocusPath) == this.currentArrowFocusLeaf
         && this.containsFocusLeaf(leafComponent(this.previousArrowFocusPath))) {
         ComponentPath previousPath = this.previousArrowFocusPath;
         this.changeFocus(previousPath);
         this.previousArrowFocusPath = currentFocusPath;
         this.currentArrowFocusLeaf = leafComponent(previousPath);
         this.lastArrowDirection = direction;
         return true;
      } else {
         return false;
      }
   }

   private void rememberArrowNavigation(ScreenDirection direction, @Nullable ComponentPath previousPath, ComponentPath currentPath) {
      if (previousPath != null && leafComponent(previousPath) != leafComponent(currentPath)) {
         this.previousArrowFocusPath = previousPath;
         this.currentArrowFocusLeaf = leafComponent(currentPath);
         this.lastArrowDirection = direction;
      } else {
         this.clearArrowNavigationMemory();
      }
   }

   private static GuiEventListener leafComponent(ComponentPath path) {
      return path instanceof Path parentPath ? leafComponent(parentPath.childPath()) : path.component();
   }

   private void clearArrowNavigationMemory() {
      this.previousArrowFocusPath = null;
      this.currentArrowFocusLeaf = null;
      this.lastArrowDirection = null;
   }

   private boolean containsFocusLeaf(GuiEventListener leaf) {
      for (GuiEventListener child : this.children()) {
         if (containsFocusLeaf(child, leaf)) {
            return true;
         }
      }

      return false;
   }

   private static boolean containsFocusLeaf(GuiEventListener component, GuiEventListener leaf) {
      if (component == leaf) {
         return true;
      } else {
         if (component instanceof ContainerEventHandler container) {
            for (GuiEventListener child : container.children()) {
               if (containsFocusLeaf(child, leaf)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Nullable
   private static ScreenDirection getArrowDirection(int keyCode) {
      return switch (keyCode) {
         case 262 -> ScreenDirection.RIGHT;
         case 263 -> ScreenDirection.LEFT;
         case 264 -> ScreenDirection.DOWN;
         case 265 -> ScreenDirection.UP;
         default -> null;
      };
   }

   private static int keyForDirection(ScreenDirection direction) {
      return switch (direction) {
         case LEFT -> 263;
         case RIGHT -> 262;
         case UP -> 265;
         case DOWN -> 264;
         default -> throw new MatchException(null, null);
      };
   }

   public boolean shouldCloseOnEsc() {
      return !this.hasPendingChanges;
   }

   public void onClose() {
      this.uiState.lastSearch().set("");
      this.uiState.lastSearchIndex().set(0);
      this.uiState.updateSearchResults(false, List.of());
      this.uiState.clearOptionUiStates();
      this.uiState.focusedOptionIdsByTab().clear();
      this.minecraft.setScreen(this.prevScreen);
   }

   @Nullable
   public ScreenPrompt getPrompt() {
      return this.prompt;
   }

   public void setPrompt(@Nullable ScreenPrompt prompt) {
      this.prompt = prompt;
   }

   public Dim2i getDimensions() {
      return new Dim2i(0, 0, this.width, this.height);
   }
}
