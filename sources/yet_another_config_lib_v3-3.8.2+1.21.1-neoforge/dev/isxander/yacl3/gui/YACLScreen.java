package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.CustomTabProvider;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.PlaceholderCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.controllers.ControllerPopupWidget;
import dev.isxander.yacl3.gui.controllers.PopupControllerScreen;
import dev.isxander.yacl3.gui.tab.ScrollableNavigationBar;
import dev.isxander.yacl3.gui.tab.TabExt;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class YACLScreen extends Screen {
   public final YetAnotherConfigLib config;
   private final Screen parent;
   public final TabManager tabManager = new TabManager(x$0 -> {
      net.minecraft.client.gui.components.AbstractWidget var10000 = (net.minecraft.client.gui.components.AbstractWidget)this.addRenderableWidget(x$0);
   }, x$0 -> this.removeWidget(x$0));
   public ScrollableNavigationBar tabNavigationBar;
   public ScreenRectangle tabArea;
   public Component saveButtonMessage;
   public Tooltip saveButtonTooltipMessage;
   private int saveButtonMessageTime;
   private boolean pendingChanges;
   public ControllerPopupWidget<?> currentPopupController = null;
   public boolean popupControllerVisible = false;

   public YACLScreen(YetAnotherConfigLib config, Screen parent) {
      super(config.title());
      this.config = config;
      this.parent = parent;
      OptionUtils.forEachOptions(config, option -> option.addEventListener((opt, event) -> {
         if (event != OptionEventListener.Event.INITIAL) {
            this.onOptionChanged(opt);
         }
      }));
   }

   protected void init() {
      this.tabArea = new ScreenRectangle(0, 23, this.width, this.height - 24 + 1);
      int currentTab = this.tabNavigationBar != null ? this.tabNavigationBar.getTabs().indexOf(this.tabManager.getCurrentTab()) : 0;
      if (currentTab == -1) {
         currentTab = 0;
      }

      this.tabNavigationBar = new ScrollableNavigationBar(
         this.width,
         this.tabManager,
         this.config
            .categories()
            .stream()
            .map(
               category -> {
                  if (category instanceof CustomTabProvider tabProvider) {
                     return tabProvider.createTab(this, this.tabArea);
                  } else {
                     return (Tab)(category instanceof PlaceholderCategory placeholder
                        ? new YACLScreen.PlaceholderTab(placeholder, this)
                        : new YACLScreen.CategoryTab(this, category, this.tabArea));
                  }
               }
            )
            .toList()
      );
      this.tabNavigationBar.selectTab(currentTab, false);
      this.tabNavigationBar.arrangeElements();
      this.tabManager.setTabArea(this.tabArea);
      this.addRenderableWidget(this.tabNavigationBar);
      this.config.initConsumer().accept(this);
   }

   public void addPopupControllerWidget(ControllerPopupWidget<?> controllerPopupWidget) {
      if (this.currentPopupController != null) {
         this.clearPopupControllerWidget();
      }

      this.currentPopupController = controllerPopupWidget;
      this.popupControllerVisible = true;
      OptionListWidget optionListWidget = null;
      if (this.tabNavigationBar.getTabManager().getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab) {
         optionListWidget = categoryTab.optionList.getType();
      }

      if (optionListWidget != null) {
         this.minecraft.setScreen(new PopupControllerScreen(this, controllerPopupWidget));
      }
   }

   public void clearPopupControllerWidget() {
      if (Minecraft.getInstance().screen instanceof PopupControllerScreen popupControllerScreen) {
         popupControllerScreen.onClose();
      }

      this.popupControllerVisible = false;
      this.currentPopupController = null;
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      if (this.tabManager.getCurrentTab() instanceof TabExt tab) {
         tab.renderBackground(guiGraphics);
      }
   }

   public void finishOrSave() {
      this.saveButtonMessage = null;
      if (this.pendingChanges()) {
         Set<OptionFlag> flags = new HashSet<>();
         OptionUtils.forEachOptions(this.config, option -> {
            if (option.applyValue()) {
               flags.addAll(option.flags());
            }
         });
         OptionUtils.forEachOptions(this.config, option -> {
            if (option.changed()) {
               option.forgetPendingValue();
               YACLConstants.LOGGER.error("Option '{}' value mismatch after applying! Reset to binding's getter.", option.name().getString());
            }
         });
         this.config.saveFunction().run();
         flags.forEach(flag -> flag.accept(this.minecraft));
         this.pendingChanges = false;
         if (this.tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab) {
            categoryTab.updateButtons();
         }
      } else {
         this.onClose();
      }
   }

   public void cancelOrReset() {
      if (this.pendingChanges()) {
         OptionUtils.forEachOptions(this.config, Option::forgetPendingValue);
         this.onClose();
      } else {
         OptionUtils.forEachOptions(this.config, Option::requestSetDefault);
      }
   }

   public void undo() {
      OptionUtils.forEachOptions(this.config, Option::forgetPendingValue);
   }

   public void tick() {
      if (this.tabManager.getCurrentTab() instanceof TabExt tabExt) {
         tabExt.tick();
      }

      if (this.tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab && this.saveButtonMessage != null) {
         if (this.saveButtonMessageTime > 140) {
            this.saveButtonMessage = null;
            this.saveButtonTooltipMessage = null;
            this.saveButtonMessageTime = 0;
         } else {
            this.saveButtonMessageTime++;
            categoryTab.saveFinishedButton.setMessage(this.saveButtonMessage);
            if (this.saveButtonTooltipMessage != null) {
               categoryTab.saveFinishedButton.setTooltip(this.saveButtonTooltipMessage);
            }
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (super.mouseClicked(mouseX, mouseY, button)) {
         this.setDragging(true);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.getFocused() != null
         && this.isDragging()
         && (button == 0 || button == 1)
         && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   public void setSaveButtonMessage(Component message, Component tooltip) {
      this.saveButtonMessage = message;
      this.saveButtonTooltipMessage = Tooltip.create(tooltip);
      this.saveButtonMessageTime = 0;
   }

   public boolean pendingChanges() {
      return this.pendingChanges;
   }

   private void onOptionChanged(Option<?> option) {
      this.pendingChanges = false;
      OptionUtils.consumeOptions(this.config, opt -> {
         this.pendingChanges = this.pendingChanges | opt.changed();
         return this.pendingChanges;
      });
      if (this.tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab) {
         categoryTab.updateButtons();
      }
   }

   public boolean shouldCloseOnEsc() {
      if (this.pendingChanges()) {
         this.setSaveButtonMessage(
            Component.translatable("yacl.gui.save_before_exit").withStyle(ChatFormatting.RED), Component.translatable("yacl.gui.save_before_exit.tooltip")
         );
         return false;
      } else {
         return true;
      }
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public static void renderMultilineTooltip(
      GuiGraphics graphics, Font font, MultiLineLabel text, int centerX, int yAbove, int yBelow, int screenWidth, int screenHeight
   ) {
      if (text.getLineCount() > 0) {
         int maxWidth = text.getWidth();
         int lineHeight = 9 + 1;
         int height = text.getLineCount() * lineHeight - 1;
         int belowY = yBelow + 12;
         int aboveY = yAbove - height + 12;
         int maxBelow = screenHeight - (belowY + height);
         int minAbove = aboveY - height;
         int y = aboveY;
         if (minAbove < 8) {
            y = maxBelow > minAbove ? belowY : aboveY;
         }

         int x = Math.max(centerX - text.getWidth() / 2 - 12, -6);
         int drawX = x + 12;
         int drawY = y - 12;
         GuiUtils.pushPose(graphics);
         TooltipRenderUtil.renderTooltipBackground(graphics, drawX, drawY, maxWidth, height, 400);
         GuiUtils.translateZ(graphics, 400.0F);
         text.renderLeftAligned(graphics, drawX, drawY, lineHeight, -1);
         GuiUtils.popPose(graphics);
      }
   }

   public static class CategoryTab implements TabExt {
      private static final ResourceLocation DARKER_BG = YACLPlatform.mcRl("textures/gui/menu_list_background.png");
      private final YACLScreen screen;
      private final ConfigCategory category;
      private final Tooltip tooltip;
      private WidgetAndType<OptionListWidget> optionList;
      public final Button saveFinishedButton;
      public final Button cancelResetButton;
      public final Button undoButton;
      private final SearchFieldWidget searchField;
      private OptionDescriptionWidget descriptionWidget;
      private final ScreenRectangle rightPaneDim;

      public CategoryTab(YACLScreen screen, ConfigCategory category, ScreenRectangle tabArea) {
         this.screen = screen;
         this.category = category;
         this.tooltip = Tooltip.create(category.tooltip());
         int columnWidth = screen.width / 3;
         int padding = columnWidth / 20;
         columnWidth = Math.min(columnWidth, 400);
         int paddedWidth = columnWidth - padding * 2;
         this.rightPaneDim = new ScreenRectangle(screen.width / 3 * 2, tabArea.top() + 1, screen.width / 3, tabArea.height());
         MutableDimension<Integer> actionDim = Dimension.ofInt(screen.width / 3 * 2 + screen.width / 6, screen.height - padding - 20, paddedWidth, 20);
         this.saveFinishedButton = Button.builder(Component.literal("Done"), btn -> screen.finishOrSave())
            .pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
            .size(actionDim.width(), actionDim.height())
            .build();
         actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
         this.cancelResetButton = Button.builder(Component.literal("Cancel"), btn -> screen.cancelOrReset())
            .pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
            .size(actionDim.width(), actionDim.height())
            .build();
         actionDim.move(actionDim.width() + 4, 0);
         this.undoButton = Button.builder(Component.translatable("yacl.gui.undo"), btn -> screen.undo())
            .pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
            .size(actionDim.width(), actionDim.height())
            .tooltip(Tooltip.create(Component.translatable("yacl.gui.undo.tooltip")))
            .build();
         this.searchField = new SearchFieldWidget(
            screen,
            screen.font,
            screen.width / 3 * 2 + screen.width / 6 - paddedWidth / 2 + 1,
            this.undoButton.getY() - 22,
            paddedWidth - 2,
            18,
            Component.translatable("gui.recipebook.search_hint"),
            Component.translatable("gui.recipebook.search_hint"),
            searchQuery -> this.optionList.getType().updateSearchQuery(searchQuery)
         );
         this.optionList = YACLSelectionList.asWidget(
            new OptionListWidget(
               screen, category, screen.minecraft, 0, 0, screen.width / 3 * 2 + 1, screen.height, desc -> this.descriptionWidget.setOptionDescription(desc)
            )
         );
         this.descriptionWidget = new OptionDescriptionWidget(
            () -> new ScreenRectangle(
               screen.width / 3 * 2 + padding, tabArea.top() + padding, paddedWidth, this.searchField.getY() - 1 - tabArea.top() - padding * 2
            ),
            null
         );
         this.updateButtons();
      }

      public Component getTabTitle() {
         return this.category.name();
      }

      public void visitChildren(Consumer<net.minecraft.client.gui.components.AbstractWidget> consumer) {
         consumer.accept(this.optionList.getWidget());
         consumer.accept(this.saveFinishedButton);
         consumer.accept(this.cancelResetButton);
         consumer.accept(this.undoButton);
         consumer.accept(this.searchField);
         consumer.accept(this.descriptionWidget);
      }

      @Override
      public void renderBackground(GuiGraphics graphics) {
         RenderSystem.enableBlend();
         GuiUtils.blitGuiTex(
            graphics,
            DARKER_BG,
            this.rightPaneDim.left(),
            this.rightPaneDim.top(),
            this.rightPaneDim.right() + 2,
            this.rightPaneDim.bottom() + 2,
            this.rightPaneDim.width() + 2,
            this.rightPaneDim.height() + 2,
            32,
            32
         );
         RenderSystem.disableBlend();
         GuiUtils.pushPose(graphics);
         GuiUtils.translateZ(graphics, 10.0F);
         GuiUtils.blitGuiTex(
            graphics,
            CreateWorldScreen.HEADER_SEPARATOR,
            this.rightPaneDim.left() - 1,
            this.rightPaneDim.top() - 2,
            0.0F,
            0.0F,
            this.rightPaneDim.width() + 1,
            2,
            32,
            2
         );
         GuiUtils.popPose(graphics);
         GuiUtils.pushPose(graphics);
         GuiUtils.translate2D(graphics, this.rightPaneDim.left(), this.rightPaneDim.top() - 1);
         GuiUtils.rotate2D(graphics, 90.0F);
         GuiUtils.blitGuiTex(graphics, CreateWorldScreen.FOOTER_SEPARATOR, 0, 0, 0.0F, 0.0F, this.rightPaneDim.height() + 1, 2, 32, 2);
         GuiUtils.popPose(graphics);
      }

      public void doLayout(ScreenRectangle tabArea) {
         ScreenRectangle rect = new ScreenRectangle(tabArea.position(), tabArea.width() / 3 * 2, tabArea.height());
         this.optionList.getType().setX(rect.left());
         this.optionList.getType().setY(rect.top());
         this.optionList.getType().setWidth(rect.width());
         this.optionList.getType().setHeight(rect.height());
      }

      @Override
      public void tick() {
         this.descriptionWidget.tick();
      }

      @Nullable
      @Override
      public Tooltip getTooltip() {
         return this.tooltip;
      }

      public void updateButtons() {
         boolean pendingChanges = this.screen.pendingChanges();
         this.undoButton.active = pendingChanges;
         this.saveFinishedButton
            .setMessage(pendingChanges ? Component.translatable("yacl.gui.save") : GuiUtils.translatableFallback("yacl.gui.done", CommonComponents.GUI_DONE));
         this.saveFinishedButton
            .setTooltip(Tooltip.create(pendingChanges ? Component.translatable("yacl.gui.save.tooltip") : Component.translatable("yacl.gui.finished.tooltip")));
         this.cancelResetButton
            .setMessage(
               pendingChanges ? GuiUtils.translatableFallback("yacl.gui.cancel", CommonComponents.GUI_CANCEL) : Component.translatable("controls.reset")
            );
         this.cancelResetButton
            .setTooltip(Tooltip.create(pendingChanges ? Component.translatable("yacl.gui.cancel.tooltip") : Component.translatable("yacl.gui.reset.tooltip")));
      }
   }

   public static class PlaceholderTab implements TabExt {
      private final YACLScreen screen;
      private final PlaceholderCategory category;
      private final Tooltip tooltip;

      public PlaceholderTab(PlaceholderCategory category, YACLScreen screen) {
         this.screen = screen;
         this.category = category;
         this.tooltip = Tooltip.create(category.tooltip());
      }

      public Component getTabTitle() {
         return this.category.name();
      }

      public void visitChildren(Consumer<net.minecraft.client.gui.components.AbstractWidget> consumer) {
      }

      public void doLayout(ScreenRectangle screenRectangle) {
         this.screen.minecraft.setScreen(this.category.screen().apply(this.screen.minecraft, this.screen));
      }

      @Nullable
      @Override
      public Tooltip getTooltip() {
         return this.tooltip;
      }
   }
}
