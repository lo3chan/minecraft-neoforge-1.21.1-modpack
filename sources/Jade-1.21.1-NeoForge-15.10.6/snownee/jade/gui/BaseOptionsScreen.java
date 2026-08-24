package snownee.jade.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation;
import net.minecraft.client.gui.navigation.FocusNavigationEvent.InitialFocus;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeClient;
import snownee.jade.gui.config.BelowOrAboveListEntryTooltipPositioner;
import snownee.jade.gui.config.NotUglyEditBox;
import snownee.jade.gui.config.OptionsList;
import snownee.jade.gui.config.OptionsNav;
import snownee.jade.gui.config.value.OptionValue;

public abstract class BaseOptionsScreen extends Screen {
   protected final Screen parent;
   private final Set<GuiEventListener> entryWidgets = Sets.newIdentityHashSet();
   public Button saveButton;
   protected Runnable saver;
   protected Runnable canceller;
   protected OptionsList options;
   protected OptionsNav optionsNav;
   private NotUglyEditBox searchBox;

   public BaseOptionsScreen(Screen parent, Component title) {
      super(title);
      this.parent = parent;
   }

   protected void init() {
      Objects.requireNonNull(this.minecraft);
      double scroll = this.options == null ? 0.0 : this.options.getScrollAmount();
      super.init();
      this.entryWidgets.clear();
      if (this.options != null) {
         this.options.removed();
      }

      this.options = this.createOptions();
      this.options.setX(120);
      this.optionsNav = new OptionsNav(this.options, 120, this.height - 32 - 18, 18, 18);
      this.searchBox = new NotUglyEditBox(this.font, 0, 0, 120, 18, this.searchBox, Component.translatable("gui.jade.search")) {
         @Nullable
         @Override
         public ComponentPath nextFocusPath(FocusNavigationEvent event) {
            if (event instanceof ArrowNavigation arrow && arrow.direction().getAxis() == ScreenAxis.HORIZONTAL) {
               return null;
            } else {
               return event instanceof InitialFocus ? null : super.nextFocusPath(event);
            }
         }
      };
      this.searchBox.setBordered(false);
      this.searchBox.setHint(Component.translatable("gui.jade.search.hint"));
      this.searchBox.responder = s -> {
         this.options.updateSearch(s);
         this.optionsNav.refresh();
      };
      this.searchBox.paddingLeft = 12;
      this.searchBox.paddingTop = 6;
      this.searchBox.paddingRight = 18;
      this.addRenderableWidget(this.optionsNav);
      this.addRenderableWidget(this.searchBox);
      this.addRenderableWidget(this.options);
      this.searchBox.responder.accept(this.searchBox.getValue());
      this.options.forceSetScrollAmount(scroll);
      this.saveButton = (Button)this.addRenderableWidget(
         Button.builder(
               Component.translatable("gui.jade.save_and_quit").withStyle(style -> style.withColor(-4589878)),
               w -> {
                  if (this.options.invalidEntry == null) {
                     this.options.save();
                     this.saver.run();
                     this.minecraft.setScreen(this.parent);
                  } else {
                     this.changeFocus(
                        ComponentPath.path(
                           this.options.invalidEntry.getFirstWidget(), new ContainerEventHandler[]{this.options.invalidEntry, this.options, this}
                        )
                     );
                     this.options.ensureVisible(this.options.invalidEntry);
                  }
               }
            )
            .bounds(this.width - 100, this.height - 25, 90, 20)
            .build()
      );
      if (this.canceller != null) {
         this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_CANCEL, w -> this.onClose()).bounds(this.saveButton.getX() - 95, this.height - 25, 90, 20).build()
         );
      }

      this.options.updateSaveState();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      OptionsList.Entry entry = this.options.isMouseOver(mouseX, mouseY) ? this.options.getEntryAt(mouseX, mouseY) : null;
      if (entry != null) {
         int valueX = entry.getTextX(this.options.getRowWidth());
         if (mouseX >= valueX && mouseX < valueX + entry.getTextWidth()) {
            List<Component> descs = Lists.newArrayListWithExpectedSize(3);
            descs.addAll(entry.getDescription());
            if (hasShiftDown()) {
               descs.addAll(entry.getDescriptionOnShift());
            }

            if (!descs.isEmpty()) {
               descs.replaceAll(BaseOptionsScreen::processBuiltInVariables);
               this.setTooltipForNextRenderPass(MultilineTooltip.create(descs), new BelowOrAboveListEntryTooltipPositioner(this.options, entry), false);
            }
         }

         if (entry instanceof OptionValue<?> optionValue && optionValue.serverFeature) {
            int x = entry.getTextX(this.options.getRowWidth()) + entry.getTextWidth() + 1;
            int y = this.options.getRowTop(this.options.children().indexOf(entry)) + 7;
            if (mouseX >= x && mouseX < x + 4 && mouseY >= y && mouseY < y + 4) {
               this.setTooltipForNextRenderPass(
                  Tooltip.create(Component.translatable("gui.jade.server_feature")), new BelowOrAboveListEntryTooltipPositioner(this.options, entry), false
               );
            }
         }
      }
   }

   public static Component processBuiltInVariables(Component component) {
      if (component.getString().contains("${SHOW_DETAILS}")) {
         List<Component> objects = Lists.newArrayListWithExpectedSize(3);
         objects.add(Component.translatable("key.jade.show_details"));
         if (JadeClient.showDetails.getName().contains("alternative")) {
            objects.add(InputConstants.getKey("key.keyboard.left.shift").getDisplayName().copy().withStyle(ChatFormatting.AQUA));
         }

         if (!JadeClient.showDetails.isUnbound()) {
            objects.add(JadeClient.showDetails.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.AQUA));
         }

         Component keyName = Component.translatable("config.jade.key_name_n_bind_" + (objects.size() - 1), objects.toArray());
         component = replaceVariables(component, "${SHOW_DETAILS}", keyName);
      }

      if (component.getString().contains("${SHOW_OVERLAY}")) {
         List<Component> objectsx = Lists.newArrayListWithExpectedSize(3);
         objectsx.add(Component.translatable(JadeClient.showOverlay.getName()));
         if (!JadeClient.showOverlay.isUnbound()) {
            objectsx.add(JadeClient.showOverlay.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.AQUA));
         }

         Component keyName = Component.translatable("config.jade.key_name_n_bind_" + (objectsx.size() - 1), objectsx.toArray());
         component = replaceVariables(component, "${SHOW_OVERLAY}", keyName);
      }

      return component;
   }

   private static Component replaceVariables(Component component, String source, Component replacement) {
      MutableComponent newComponent = Component.empty().withStyle(component.getStyle());

      for (Component part : component.toFlatList()) {
         String partString = part.getString();
         if (partString.contains(source)) {
            boolean first = true;

            for (String s : StringUtils.splitByWholeSeparatorPreserveAllTokens(partString, source)) {
               if (first) {
                  first = false;
               } else {
                  newComponent.append(replacement);
               }

               if (!s.isEmpty()) {
                  newComponent.append(Component.literal(s));
               }
            }
         } else {
            newComponent.append(part);
         }
      }

      return newComponent;
   }

   public abstract OptionsList createOptions();

   public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
      return this.optionsNav.isMouseOver(mouseX, mouseY)
         ? this.optionsNav.mouseScrolled(mouseX, mouseY, deltaX, deltaY)
         : this.options.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
   }

   public void onClose() {
      if (this.canceller != null) {
         this.canceller.run();
      }

      Objects.requireNonNull(this.minecraft).setScreen(this.parent);
   }

   public void removed() {
      this.options.removed();
   }

   public <T extends GuiEventListener & NarratableEntry> T addEntryWidget(T widget) {
      this.entryWidgets.add(widget);
      return (T)super.addWidget(widget);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int p_94697_) {
      boolean onList = this.options.isMouseOver(mouseX, mouseY);

      for (GuiEventListener guieventlistener : this.children()) {
         if ((onList || !this.entryWidgets.contains(guieventlistener)) && guieventlistener.mouseClicked(mouseX, mouseY, p_94697_)) {
            this.setFocused(guieventlistener);
            if (p_94697_ == 0) {
               this.setDragging(true);
            }

            return true;
         }
      }

      return false;
   }

   public boolean shouldCloseOnEsc() {
      return this.options.selectedKey == null;
   }

   public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
      boolean onList = this.options != null && this.options.isMouseOver(mouseX, mouseY);

      for (GuiEventListener guieventlistener : this.children()) {
         if ((onList || !this.entryWidgets.contains(guieventlistener)) && guieventlistener.isMouseOver(mouseX, mouseY)) {
            return Optional.of(guieventlistener);
         }
      }

      return Optional.empty();
   }

   public OptionsNav getOptionsNav() {
      return this.optionsNav;
   }
}
