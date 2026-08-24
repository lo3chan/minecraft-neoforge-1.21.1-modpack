package dev.isxander.yacl3.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.ListOptionEntry;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.utils.WidgetUtils;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OptionListWidget extends YACLSelectionList<OptionListWidget.Entry> {
   private final YACLScreen yaclScreen;
   private final ConfigCategory category;
   private String searchQuery = "";
   private final Consumer<DescriptionWithName> hoverEvent;
   private DescriptionWithName lastHoveredOption;

   public OptionListWidget(
      YACLScreen screen, ConfigCategory category, Minecraft client, int x, int y, int width, int height, Consumer<DescriptionWithName> hoverEvent
   ) {
      super(client, width, height, y);
      this.yaclScreen = screen;
      this.category = category;
      this.hoverEvent = hoverEvent;
      this.refreshOptions();
      UnmodifiableIterator var9 = category.groups().iterator();

      while (var9.hasNext()) {
         OptionGroup group = (OptionGroup)var9.next();
         if (group instanceof ListOption<?> listOption) {
            listOption.addRefreshListener(() -> this.refreshListEntries(listOption, category));
         }
      }
   }

   public void refreshOptions() {
      this.clearEntries();
      UnmodifiableIterator var1 = this.category.groups().iterator();

      while (var1.hasNext()) {
         OptionGroup group = (OptionGroup)var1.next();
         OptionListWidget.GroupSeparatorEntry groupSeparatorEntry;
         if (!group.isRoot()) {
            groupSeparatorEntry = (OptionListWidget.GroupSeparatorEntry)(group instanceof ListOption<?> listOption
               ? new OptionListWidget.ListGroupSeparatorEntry(listOption, this.yaclScreen)
               : new OptionListWidget.GroupSeparatorEntry(group, this.yaclScreen));
            this.addEntry(groupSeparatorEntry);
         } else {
            groupSeparatorEntry = null;
         }

         List<OptionListWidget.Entry> optionEntries = new ArrayList<>();
         if (groupSeparatorEntry instanceof OptionListWidget.ListGroupSeparatorEntry listGroupSeparatorEntry
            && listGroupSeparatorEntry.listOption.options().isEmpty()) {
            OptionListWidget.EmptyListLabel emptyListLabel = new OptionListWidget.EmptyListLabel(listGroupSeparatorEntry, this.category);
            this.addEntry(emptyListLabel);
            optionEntries.add(emptyListLabel);
         }

         UnmodifiableIterator var9 = group.options().iterator();

         while (var9.hasNext()) {
            Option<?> option = (Option<?>)var9.next();
            OptionListWidget.OptionEntry entry = new OptionListWidget.OptionEntry(
               option, this.category, group, groupSeparatorEntry, option.controller().provideWidget(this.yaclScreen, this.getDefaultEntryDimension())
            );
            this.addEntry(entry);
            optionEntries.add(entry);
         }

         if (groupSeparatorEntry != null) {
            groupSeparatorEntry.setChildEntries(optionEntries);
         }
      }

      this.setScrollAmount(0.0);
      this.repositionEntries();
   }

   private void refreshListEntries(ListOption<?> listOption, ConfigCategory category) {
      OptionListWidget.ListGroupSeparatorEntry groupSeparator = this.children()
         .stream()
         .filter(e -> e instanceof OptionListWidget.ListGroupSeparatorEntry gs && gs.group == listOption)
         .map(OptionListWidget.ListGroupSeparatorEntry.class::cast)
         .findAny()
         .orElse(null);
      if (groupSeparator == null) {
         YACLConstants.LOGGER.warn("Can't find group seperator to refresh list option entries for list option " + listOption.name());
      } else {
         for (OptionListWidget.Entry entry : groupSeparator.childEntries) {
            this.removeEntry(entry);
         }

         groupSeparator.childEntries.clear();
         if (listOption.options().isEmpty()) {
            OptionListWidget.EmptyListLabel emptyListLabel;
            this.addEntryBelow(groupSeparator, emptyListLabel = new OptionListWidget.EmptyListLabel(groupSeparator, category));
            groupSeparator.childEntries.add(emptyListLabel);
         } else {
            OptionListWidget.Entry lastEntry = groupSeparator;
            UnmodifiableIterator var10 = listOption.options().iterator();

            while (var10.hasNext()) {
               ListOptionEntry<?> listOptionEntry = (ListOptionEntry<?>)var10.next();
               OptionListWidget.OptionEntry optionEntry = new OptionListWidget.OptionEntry(
                  listOptionEntry,
                  category,
                  listOption,
                  groupSeparator,
                  listOptionEntry.controller().provideWidget(this.yaclScreen, this.getDefaultEntryDimension())
               );
               this.addEntryBelow(lastEntry, optionEntry);
               groupSeparator.childEntries.add(optionEntry);
               lastEntry = optionEntry;
            }
         }
      }
   }

   public Dimension<Integer> getDefaultEntryDimension() {
      return Dimension.ofInt(this.getRowLeft(), 0, this.getRowWidth(), 20);
   }

   public void expandAllGroups() {
      for (OptionListWidget.Entry entry : super.children()) {
         if (entry instanceof OptionListWidget.GroupSeparatorEntry groupSeparatorEntry) {
            groupSeparatorEntry.setExpanded(true);
         }
      }
   }

   public int getRowLeft() {
      return super.getRowLeft() - 6;
   }

   public int getRowWidth() {
      return this.getWidth() - 6 - 20;
   }

   public void updateSearchQuery(String query) {
      this.searchQuery = query;

      for (OptionListWidget.Entry entry : this.children()) {
         entry.updateSearchQuery(query);
      }

      this.expandAllGroups();
      this.repositionEntries();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      for (OptionListWidget.Entry child : this.children()) {
         if (child != this.getEntryAtPosition(mouseX, mouseY) && child instanceof OptionListWidget.OptionEntry optionEntry) {
            optionEntry.widget.unfocus();
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
      super.mouseScrolled(mouseX, mouseY, horizontal, vertical);

      for (OptionListWidget.Entry child : this.children()) {
         if (child.mouseScrolled(mouseX, mouseY, horizontal, vertical)) {
            break;
         }
      }

      return true;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.getFocused() != null && this.isDragging() && this.isValidMouseClick(button)
         ? WidgetUtils.mouseDragged(this.getFocused(), mouseX, mouseY, button, deltaX, deltaY)
         : super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      for (OptionListWidget.Entry child : this.children()) {
         if (child.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
         }
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean charTyped(char chr, int modifiers) {
      for (OptionListWidget.Entry child : this.children()) {
         if (child.charTyped(chr, modifiers)) {
            return true;
         }
      }

      return super.charTyped(chr, modifiers);
   }

   private List<OptionListWidget.Entry> superModifiableChildren() {
      return this.children();
   }

   public void addEntryAtIndex(int index, OptionListWidget.Entry entry) {
      this.superModifiableChildren().add(index, entry);
      this.repositionEntries();
   }

   public void addEntryBelow(OptionListWidget.Entry below, OptionListWidget.Entry entry) {
      int idx = this.superModifiableChildren().indexOf(below) + 1;
      if (idx == 0) {
         throw new IllegalStateException("The entry to insert below does not exist!");
      } else {
         this.addEntryAtIndex(idx, entry);
      }
   }

   public void addEntryBelowWithoutScroll(OptionListWidget.Entry below, OptionListWidget.Entry entry) {
      double d = this.contentHeight() - this.scrollAmount();
      this.addEntryBelow(below, entry);
      this.setScrollAmount(this.contentHeight() - d);
   }

   private void setHoverDescription(DescriptionWithName description) {
      if (description != this.lastHoveredOption) {
         this.lastHoveredOption = description;
         this.hoverEvent.accept(description);
      }
   }

   protected void renderListBackground(GuiGraphics guiGraphics) {
   }

   protected boolean isValidMouseClick(int button) {
      return button == 0 || button == 1 || button == 2;
   }

   protected OptionListWidget.Entry nextEntry(
      @NotNull ScreenDirection direction, @NotNull Predicate<OptionListWidget.Entry> predicate, OptionListWidget.Entry selected
   ) {
      return (OptionListWidget.Entry)super.nextEntry(direction, entry -> entry.isViewable() && predicate.test(entry), selected);
   }

   public class EmptyListLabel extends OptionListWidget.Entry {
      private final OptionListWidget.ListGroupSeparatorEntry parent;
      private final String groupName;
      private final String categoryName;

      public EmptyListLabel(OptionListWidget.ListGroupSeparatorEntry parent, ConfigCategory category) {
         this.parent = parent;
         this.groupName = parent.group.name().getString().toLowerCase();
         this.categoryName = category.name().getString().toLowerCase();
         this.setHeight(11);
      }

      @Override
      public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         graphics.drawCenteredString(
            Minecraft.getInstance().font,
            Component.translatable("yacl.list.empty").withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC}),
            this.getX() + this.getWidth() / 2,
            this.getY(),
            -1
         );
      }

      @Override
      public boolean updateSearchQuery(String searchQuery) {
         return this.searchQueryMatches = searchQuery.isEmpty() || this.groupName.contains(searchQuery);
      }

      @Override
      public boolean isViewable() {
         return this.parent.isExpanded() && super.isViewable();
      }

      @Override
      protected void onBecameHidden() {
         super.onBecameHidden();
         this.setHeight(0);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of();
      }
   }

   public abstract class Entry extends YACLSelectionList.Entry<OptionListWidget.Entry> {
      protected boolean searchQueryMatches = true;

      public Entry() {
         super(OptionListWidget.this);
      }

      public boolean updateSearchQuery(String searchQuery) {
         boolean matches = searchQuery.isEmpty();
         if (this.searchQueryMatches != matches) {
            this.searchQueryMatches = matches;
            this.refreshVisibilityState();
         }

         return this.searchQueryMatches;
      }

      public boolean isViewable() {
         return this.searchQueryMatches;
      }

      @Override
      public int getHeight() {
         return !this.isViewable() ? 0 : super.getHeight();
      }

      protected void refreshVisibilityState() {
         if (this.isViewable()) {
            this.onBecameViewable();
         } else {
            this.onBecameHidden();
         }
      }

      protected void onBecameViewable() {
      }

      protected void onBecameHidden() {
         this.setHeight(0);
      }
   }

   public class GroupSeparatorEntry extends OptionListWidget.Entry {
      protected final OptionGroup group;
      protected final MultiLineLabel wrappedName;
      protected final MultiLineLabel wrappedTooltip;
      protected final LowProfileButtonWidget expandMinimizeButton;
      protected final Screen screen;
      protected final Font font;
      protected boolean groupExpanded;
      protected List<OptionListWidget.Entry> childEntries;

      private GroupSeparatorEntry(OptionGroup group, Screen screen) {
         this.font = Minecraft.getInstance().font;
         this.childEntries = new ArrayList<>();
         this.group = group;
         this.screen = screen;
         this.wrappedName = MultiLineLabel.create(this.font, group.name(), OptionListWidget.this.getRowWidth() - 45);
         this.wrappedTooltip = MultiLineLabel.create(this.font, group.tooltip(), screen.width / 3 * 2 - 10);
         this.groupExpanded = !group.collapsed();
         this.expandMinimizeButton = new LowProfileButtonWidget(0, 0, 20, 20, Component.empty(), btn -> this.onExpandButtonPress());
         this.updateExpandMinimizeText();
         this.updateHeight();
      }

      @Override
      public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         if (this.isViewable()) {
            this.updateHeight();
            int buttonY = this.getY() + this.getHeight() / 2 - this.expandMinimizeButton.getHeight() / 2 + 1;
            this.expandMinimizeButton.setY(buttonY);
            this.expandMinimizeButton.setX(this.getX());
            this.expandMinimizeButton.render(graphics, mouseX, mouseY, deltaTicks);
            this.wrappedName.renderCentered(graphics, this.getX() + this.getWidth() / 2, this.getY() + this.getYPadding());
            if (this.isMouseOver(mouseX, mouseY)) {
               OptionListWidget.this.setHoverDescription(DescriptionWithName.of(this.group.name(), this.group.description()));
            }
         }
      }

      public boolean isExpanded() {
         return this.groupExpanded;
      }

      public void setExpanded(boolean expanded) {
         if (this.groupExpanded != expanded) {
            this.groupExpanded = expanded;
            this.updateExpandMinimizeText();
            this.childEntries.forEach(OptionListWidget.Entry::refreshVisibilityState);
            OptionListWidget.this.repositionEntries();
         }
      }

      protected void onExpandButtonPress() {
         this.setExpanded(!this.isExpanded());
      }

      protected void updateExpandMinimizeText() {
         this.expandMinimizeButton.setMessage(Component.literal(this.isExpanded() ? "▼" : "▶"));
      }

      public void setChildEntries(List<? extends OptionListWidget.Entry> childEntries) {
         this.childEntries.clear();
         this.childEntries.addAll(childEntries);
      }

      @Override
      public boolean updateSearchQuery(String searchQuery) {
         return this.searchQueryMatches = searchQuery.isEmpty() || this.childEntries.stream().anyMatch(e -> e.updateSearchQuery(searchQuery));
      }

      private int getYPadding() {
         return 6;
      }

      public void setFocused(boolean focused) {
         super.setFocused(focused);
         if (focused) {
            OptionListWidget.this.setHoverDescription(DescriptionWithName.of(this.group.name(), this.group.description()));
         }
      }

      private void updateHeight() {
         this.setHeight(Math.max(this.wrappedName.getLineCount(), 1) * 9 + this.getYPadding() * 2);
      }

      @NotNull
      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            @NotNull
            public NarrationPriority narrationPriority() {
               return NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput builder) {
               builder.add(NarratedElementType.TITLE, GroupSeparatorEntry.this.group.name());
               builder.add(NarratedElementType.HINT, GroupSeparatorEntry.this.group.tooltip());
            }
         });
      }

      @NotNull
      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.expandMinimizeButton);
      }
   }

   public class ListGroupSeparatorEntry extends OptionListWidget.GroupSeparatorEntry {
      private final ListOption<?> listOption;
      private final TextScaledButtonWidget resetListButton;
      private final TooltipButtonWidget addListButton;

      private ListGroupSeparatorEntry(ListOption<?> group, Screen screen) {
         super(group, screen);
         this.listOption = group;
         this.resetListButton = new TextScaledButtonWidget(
            screen, OptionListWidget.this.getRowRight() - 20, -50, 20, 20, 1.0F, Component.literal("↻"), button -> group.requestSetDefault()
         );
         group.addListener((opt, val) -> this.resetListButton.active = !opt.isPendingValueDefault() && opt.available());
         this.resetListButton.active = !group.isPendingValueDefault() && group.available();
         this.addListButton = new TooltipButtonWidget(
            OptionListWidget.this.yaclScreen,
            this.resetListButton.getX() - 20,
            -50,
            20,
            20,
            Component.literal("+"),
            Component.translatable("yacl.list.add_top"),
            btn -> {
               group.insertNewEntry();
               this.setExpanded(true);
            }
         );
         this.updateExpandMinimizeText();
         this.minimizeIfUnavailable();
      }

      @Override
      public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         if (this.isViewable()) {
            this.updateExpandMinimizeText();
            super.renderContent(graphics, mouseX, mouseY, hovered, deltaTicks);
            int buttonY = this.expandMinimizeButton.getY();
            this.resetListButton.setY(buttonY);
            this.addListButton.setY(buttonY);
            this.resetListButton.render(graphics, mouseX, mouseY, deltaTicks);
            this.addListButton.render(graphics, mouseX, mouseY, deltaTicks);
         }
      }

      private void minimizeIfUnavailable() {
         if (!this.listOption.available() && this.isExpanded()) {
            this.setExpanded(false);
         }
      }

      @Override
      protected void updateExpandMinimizeText() {
         super.updateExpandMinimizeText();
         this.expandMinimizeButton.active = this.listOption == null || this.listOption.available();
         if (this.addListButton != null) {
            this.addListButton.active = this.expandMinimizeButton.active && this.listOption.numberOfEntries() < this.listOption.maximumNumberOfEntries();
         }
      }

      @Override
      public void setExpanded(boolean expanded) {
         super.setExpanded(this.listOption.available() && expanded);
      }

      @NotNull
      @Override
      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.expandMinimizeButton, this.addListButton, this.resetListButton);
      }
   }

   public class OptionEntry extends OptionListWidget.Entry {
      public final Option<?> option;
      public final ConfigCategory category;
      public final OptionGroup group;
      @Nullable
      public final OptionListWidget.GroupSeparatorEntry groupSeparatorEntry;
      public final AbstractWidget widget;
      private final TextScaledButtonWidget resetButton;
      private final String categoryName;
      private final String groupName;

      public OptionEntry(
         Option<?> option,
         ConfigCategory category,
         OptionGroup group,
         @Nullable OptionListWidget.GroupSeparatorEntry groupSeparatorEntry,
         AbstractWidget widget
      ) {
         this.option = option;
         this.category = category;
         this.group = group;
         this.groupSeparatorEntry = groupSeparatorEntry;
         this.widget = widget;
         this.categoryName = category.name().getString().toLowerCase();
         this.groupName = group.name().getString().toLowerCase();
         if (option.canResetToDefault() && this.widget.canReset()) {
            this.widget.setDimension(this.widget.getDimension().expanded(-20, 0));
            this.resetButton = new TextScaledButtonWidget(
               OptionListWidget.this.yaclScreen,
               widget.getDimension().xLimit(),
               -50,
               20,
               20,
               2.0F,
               Component.literal("↻"),
               button -> option.requestSetDefault()
            );
            option.addListener((opt, val) -> this.resetButton.active = !opt.isPendingValueDefault() && opt.available());
            this.resetButton.active = !option.isPendingValueDefault() && option.available();
         } else {
            this.resetButton = null;
         }

         this.updateHeight();
      }

      @Override
      public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         if (this.isViewable()) {
            this.updateHeight();
            this.widget.setDimension(this.widget.getDimension().withY(this.getY()));
            this.widget.render(graphics, mouseX, mouseY, deltaTicks);
            if (this.resetButton != null) {
               this.resetButton.setY(this.getY());
               this.resetButton.render(graphics, mouseX, mouseY, deltaTicks);
            }

            if (this.isMouseOver(mouseX, mouseY)) {
               OptionListWidget.this.setHoverDescription(DescriptionWithName.of(this.option.name(), this.option.description()));
            }
         }
      }

      public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
         return this.widget.mouseScrolled(mouseX, mouseY, horizontal, vertical);
      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         return WidgetUtils.keyPressed(this.widget, keyCode, scanCode, modifiers);
      }

      public boolean charTyped(char chr, int modifiers) {
         return WidgetUtils.charTyped(this.widget, chr, modifiers);
      }

      @Override
      public boolean updateSearchQuery(String searchQuery) {
         this.searchQueryMatches = searchQuery.isEmpty() || this.groupName.contains(searchQuery) || this.widget.matchesSearch(searchQuery);
         this.refreshVisibilityState();
         return this.searchQueryMatches;
      }

      @Override
      public boolean isViewable() {
         return super.isViewable() && (this.groupSeparatorEntry == null || this.groupSeparatorEntry.isExpanded());
      }

      @Override
      protected void onBecameViewable() {
         super.onBecameViewable();
         this.updateHeight();
      }

      private void updateHeight() {
         this.setHeight(Math.max(this.widget.getDimension().height(), this.resetButton != null ? this.resetButton.getHeight() : 0) + 2);
      }

      public void setFocused(boolean focused) {
         super.setFocused(focused);
         if (focused) {
            OptionListWidget.this.setHoverDescription(DescriptionWithName.of(this.option.name(), this.option.description()));
         }
      }

      public List<? extends NarratableEntry> narratables() {
         return this.resetButton == null ? ImmutableList.of(this.widget) : ImmutableList.of(this.widget, this.resetButton);
      }

      public List<? extends GuiEventListener> children() {
         return this.resetButton == null ? ImmutableList.of(this.widget) : ImmutableList.of(this.widget, this.resetButton);
      }
   }
}
