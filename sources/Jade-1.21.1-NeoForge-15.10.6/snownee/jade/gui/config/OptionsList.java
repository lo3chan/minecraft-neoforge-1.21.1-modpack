package snownee.jade.gui.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.InputType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.CycleButton.Builder;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import snownee.jade.gui.BaseOptionsScreen;
import snownee.jade.gui.PreviewOptionsScreen;
import snownee.jade.gui.config.value.CycleOptionValue;
import snownee.jade.gui.config.value.InputOptionValue;
import snownee.jade.gui.config.value.OptionValue;
import snownee.jade.gui.config.value.SliderOptionValue;
import snownee.jade.util.ClientProxy;
import snownee.jade.util.SmoothChasingValue;

public class OptionsList extends ContainerObjectSelectionList<OptionsList.Entry> {
   public static final Component OPTION_ON = CommonComponents.OPTION_ON.copy().withStyle(style -> style.withColor(-4589878));
   public static final Component OPTION_OFF = CommonComponents.OPTION_OFF.copy().withStyle(style -> style.withColor(-30080));
   public final Set<OptionsList.Entry> forcePreview = Sets.newIdentityHashSet();
   protected final List<OptionsList.Entry> entries = Lists.newArrayList();
   private final Runnable diskWriter;
   public OptionsList.Title currentTitle;
   public OptionValue<?> invalidEntry;
   public KeyMapping selectedKey;
   private BaseOptionsScreen owner;
   private final SmoothChasingValue smoothScroll;
   private OptionsList.Entry defaultParent;

   public OptionsList(BaseOptionsScreen owner, Minecraft client, int width, int height, int y0, int entryHeight, Runnable diskWriter) {
      super(client, width, height, y0, entryHeight);
      this.owner = owner;
      this.diskWriter = diskWriter;
      this.smoothScroll = new SmoothChasingValue().withSpeed(0.6F);
   }

   public OptionsList(BaseOptionsScreen owner, Minecraft client, int width, int height, int y0, int entryHeight) {
      this(owner, client, width, height, y0, entryHeight, null);
   }

   private static void walkChildren(OptionsList.Entry entry, Consumer<OptionsList.Entry> consumer) {
      consumer.accept(entry);

      for (OptionsList.Entry child : entry.children) {
         walkChildren(child, consumer);
      }
   }

   public int getRowWidth() {
      return Math.min(this.width, 300);
   }

   protected int getScrollbarPosition() {
      return this.owner.width - 6;
   }

   public void setScrollAmount(double scroll) {
      this.smoothScroll.target(Mth.clamp((float)scroll, 0.0F, this.getMaxScroll()));
   }

   public void forceSetScrollAmount(double scroll) {
      this.smoothScroll.start((float)scroll);
      super.setScrollAmount(scroll);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
      double speed = !ClientProxy.hasFastScroll && Screen.hasControlDown() ? 4.5 : 1.5;
      this.setScrollAmount(this.smoothScroll.getTarget() - deltaY * this.itemHeight * speed);
      return true;
   }

   public boolean mouseDragged(double d, double e, int i, double f, double g) {
      this.smoothScroll.value = this.smoothScroll.getTarget();
      super.setScrollAmount(this.smoothScroll.value);
      return super.mouseDragged(d, e, i, f, g);
   }

   public boolean isFocused() {
      return this.owner.getFocused() == this;
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent event) {
      OptionsNav.Entry navEntry = (OptionsNav.Entry)this.owner.getOptionsNav().getFocused();
      if (navEntry != null && event instanceof ArrowNavigation nav && nav.direction() == ScreenDirection.RIGHT) {
         OptionsList.Title title = navEntry.getTitle();
         this.setFocused(title);
         ComponentPath path = super.nextFocusPath(new ArrowNavigation(ScreenDirection.DOWN));
         this.setFocused(null);
         return path;
      } else {
         return super.nextFocusPath(event);
      }
   }

   public void ensureVisible(OptionsList.Entry entry) {
      super.ensureVisible(entry);
   }

   protected boolean isSelectedItem(int i) {
      return PreviewOptionsScreen.isAdjustingPosition() ? false : Objects.equals(this.getSelected(), this.children().get(i));
   }

   protected void renderListSeparators(GuiGraphics guiGraphics) {
      RenderSystem.enableBlend();
      ResourceLocation resourceLocation2 = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
      guiGraphics.blit(resourceLocation2, 0, this.getBottom(), 0.0F, 0.0F, this.owner.width, 2, 32, 2);
      RenderSystem.disableBlend();
   }

   protected void renderSelection(GuiGraphics guiGraphics, int i, int j, int k, int l, int m) {
      guiGraphics.fill(this.getX(), i - 2, this.getRight(), i + k + 2, 872415231);
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      float deltaTicks = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
      this.smoothScroll.tick(deltaTicks);
      super.setScrollAmount(this.smoothScroll.value);
      this.hovered = null;
      if (!PreviewOptionsScreen.isAdjustingPosition()) {
         InputType lastInputType = this.minecraft.getLastInputType();
         mouseY = Math.min(mouseY, this.getRowRight());
         if (lastInputType.isMouse() && this.isMouseOver(mouseX, mouseY)) {
            this.hovered = this.getEntryAtPosition(mouseX, mouseY);
         } else if (lastInputType.isKeyboard() && this.getFocused() != null) {
            this.hovered = this.getFocused();
         }

         if (this.hovered instanceof OptionsList.Title title) {
            this.setSelected(null);
            this.currentTitle = title;
         } else {
            this.setSelected((OptionsList.Entry)this.hovered);
            if (this.hovered != null && ((OptionsList.Entry)this.hovered).root() instanceof OptionsList.Title title) {
               this.currentTitle = title;
            }
         }
      }

      this.enableScissor(guiGraphics);
      this.renderListItems(guiGraphics, mouseX, mouseY, partialTicks);
      guiGraphics.disableScissor();
      this.renderListSeparators(guiGraphics);
      if (this.scrollbarVisible()) {
         int k = this.getScrollbarPosition();
         int l = (int)((float)(this.height * this.height) / this.getMaxPosition());
         l = Mth.clamp(l, 32, this.height - 8);
         int m = (int)this.getScrollAmount() * (this.height - l) / this.getMaxScroll() + this.getY();
         if (m < this.getY()) {
            m = this.getY();
         }

         RenderSystem.enableBlend();
         guiGraphics.blitSprite(SCROLLER_BACKGROUND_SPRITE, k, this.getY(), 6, this.getHeight());
         guiGraphics.blitSprite(SCROLLER_SPRITE, k, m, 6, l);
         RenderSystem.disableBlend();
      }

      this.renderDecorations(guiGraphics, mouseX, mouseY);
      RenderSystem.disableBlend();
   }

   public void save() {
      this.children().stream().filter(e -> e instanceof OptionValue).map(e -> (OptionValue)e).forEach(OptionValue::save);
      if (this.diskWriter != null) {
         this.diskWriter.run();
      }
   }

   public <T extends OptionsList.Entry> T add(T entry) {
      this.entries.add(entry);
      if (entry instanceof OptionsList.Title) {
         this.setDefaultParent(entry);
      } else if (this.defaultParent != null) {
         entry.parent(this.defaultParent);
      }

      return entry;
   }

   @Nullable
   public OptionsList.Entry getEntryAt(double x, double y) {
      return (OptionsList.Entry)this.getEntryAtPosition(x, y);
   }

   public int getRowTop(int i) {
      return super.getRowTop(i);
   }

   public int getRowBottom(int i) {
      return super.getRowBottom(i);
   }

   public void setDefaultParent(OptionsList.Entry defaultParent) {
      this.defaultParent = defaultParent;
   }

   public MutableComponent title(String string) {
      return this.add(new OptionsList.Title(string)).getTitle();
   }

   public OptionValue<Float> slider(String optionName, Supplier<Float> getter, Consumer<Float> setter) {
      return this.slider(optionName, getter, setter, 0.0F, 1.0F, FloatUnaryOperator.identity());
   }

   public OptionValue<Float> slider(String optionName, Supplier<Float> getter, Consumer<Float> setter, float min, float max, FloatUnaryOperator aligner) {
      return this.add(new SliderOptionValue(optionName, getter, setter, min, max, aligner));
   }

   public <T> OptionValue<T> input(String optionName, Supplier<T> getter, Consumer<T> setter, Predicate<String> validator) {
      return this.add(new InputOptionValue<>(this::updateSaveState, optionName, getter, setter, validator));
   }

   public <T> OptionValue<T> input(String optionName, Supplier<T> getter, Consumer<T> setter) {
      return this.input(optionName, getter, setter, Predicates.alwaysTrue());
   }

   public OptionValue<Boolean> choices(String optionName, Supplier<Boolean> getter, BooleanConsumer setter) {
      return this.choices(optionName, getter, setter, null);
   }

   public OptionValue<Boolean> choices(
      String optionName, Supplier<Boolean> getter, BooleanConsumer setter, @Nullable Consumer<Builder<Boolean>> builderConsumer
   ) {
      Builder<Boolean> builder = CycleButton.booleanBuilder(OPTION_ON, OPTION_OFF);
      if (builderConsumer != null) {
         builderConsumer.accept(builder);
      }

      return this.add(new CycleOptionValue<>(optionName, builder, getter, setter));
   }

   public <T extends Enum<T>> OptionValue<T> choices(String optionName, Supplier<T> getter, Consumer<T> setter) {
      return this.choices(optionName, getter, setter, null);
   }

   public <T extends Enum<T>> OptionValue<T> choices(String optionName, Supplier<T> getter, Consumer<T> setter, @Nullable Consumer<Builder<T>> builderConsumer) {
      List<T> values = Arrays.asList(getter.get().getDeclaringClass().getEnumConstants());
      Builder<T> builder = CycleButton.builder(v -> {
         String name = v.name().toLowerCase(Locale.ENGLISH);

         return (Component)(switch (name) {
            case "on" -> OPTION_ON;
            case "off" -> OPTION_OFF;
            default -> OptionsList.Entry.makeTitle(optionName + "_" + name);
         });
      }).withValues(values);
      if (builderConsumer != null) {
         builderConsumer.accept(builder);
      }

      return this.add(new CycleOptionValue<>(optionName, builder, getter, setter));
   }

   public <T> OptionValue<T> choices(String optionName, Supplier<T> getter, List<T> values, Consumer<T> setter, Function<T, Component> nameProvider) {
      return this.add(new CycleOptionValue<>(optionName, CycleButton.builder(nameProvider).withValues(values), getter, setter));
   }

   public void keybind(KeyMapping keybind) {
      this.add(new KeybindOptionButton(this, keybind));
   }

   public void removed() {
      this.forcePreview.clear();

      for (OptionsList.Entry entry : this.entries) {
         entry.parent = null;
         if (!entry.children.isEmpty()) {
            entry.children.clear();
         }
      }

      this.clearEntries();
      this.owner = null;
   }

   public void updateSearch(String search) {
      this.clearEntries();
      if (search.isBlank()) {
         this.entries.forEach(x$0 -> this.addEntry(x$0));
      } else {
         Set<OptionsList.Entry> matches = Sets.newLinkedHashSet();
         String[] keywords = search.toLowerCase(Locale.ENGLISH).split("\\s+");

         for (OptionsList.Entry entry : this.entries) {
            int bingo = 0;
            List<String> messages = entry.getMessages();

            for (String keyword : keywords) {
               for (String message : messages) {
                  if (message.contains(keyword)) {
                     bingo++;
                     break;
                  }
               }
            }

            if (bingo == keywords.length) {
               walkChildren(entry, matches::add);

               while (entry.parent() != null) {
                  entry = entry.parent();
                  matches.add(entry);
               }
            }
         }

         for (OptionsList.Entry entry : this.entries) {
            if (matches.contains(entry)) {
               this.addEntry(entry);
            }
         }

         if (matches.isEmpty()) {
            this.addEntry(new OptionsList.Title(Component.translatable("gui.jade.no_results").withStyle(ChatFormatting.GRAY)));
         }
      }
   }

   public void updateSaveState() {
      this.invalidEntry = null;

      for (OptionsList.Entry entry : this.entries) {
         if (entry instanceof OptionValue<?> value && !value.isValidValue()) {
            this.invalidEntry = value;
            break;
         }
      }

      if (this.invalidEntry == null) {
         this.owner.saveButton.setTooltip(null);
      } else {
         this.owner.saveButton.setTooltip(Tooltip.create(Component.translatable("gui.jade.invalid_value_cant_save")));
      }
   }

   public void updateOptionValue(@Nullable ResourceLocation key) {
      for (OptionsList.Entry entry : this.entries) {
         if (entry instanceof OptionValue<?> value && (key == null || key.equals(value.getId()))) {
            value.updateValue();
         }
      }
   }

   public void showOnTop(OptionsList.Entry entry) {
      this.setScrollAmount(this.itemHeight * this.children().indexOf(entry) + 1);
      if (entry instanceof OptionsList.Title title) {
         this.currentTitle = title;
      }
   }

   public void resetMappingAndUpdateButtons() {
      for (OptionsList.Entry entry : this.entries) {
         if (entry instanceof KeybindOptionButton button) {
            button.refresh(this.selectedKey);
         }
      }
   }

   public boolean keyPressed(int i, int j, int k) {
      if (this.selectedKey != null) {
         Options options = Minecraft.getInstance().options;
         if (i == 256) {
            options.setKey(this.selectedKey, InputConstants.UNKNOWN);
         } else {
            options.setKey(this.selectedKey, InputConstants.getKey(i, j));
         }

         this.selectedKey = null;
         this.resetMappingAndUpdateButtons();
         return true;
      } else {
         return super.keyPressed(i, j, k);
      }
   }

   public boolean mouseClicked(double d, double e, int i) {
      if (this.selectedKey != null) {
         Options options = Minecraft.getInstance().options;
         options.setKey(this.selectedKey, Type.MOUSE.getOrCreate(i));
         this.selectedKey = null;
         this.resetMappingAndUpdateButtons();
      }

      return super.mouseClicked(d, e, i);
   }

   public static class Entry extends net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry<OptionsList.Entry> {
      protected final Minecraft client;
      protected final List<String> messages = Lists.newArrayList();
      protected final List<AbstractWidget> widgets = Lists.newArrayList();
      protected final List<Vector2i> widgetOffsets = Lists.newArrayList();
      protected List<Component> description = List.of();
      private OptionsList.Entry parent;
      private List<OptionsList.Entry> children = List.of();

      public Entry() {
         this.client = Minecraft.getInstance();
      }

      public static MutableComponent makeTitle(String key) {
         return Component.translatable(makeKey(key));
      }

      public static String makeKey(String key) {
         return Util.makeDescriptionId("config", ResourceLocation.fromNamespaceAndPath("jade", key));
      }

      public AbstractWidget getFirstWidget() {
         return this.widgets.isEmpty() ? null : (AbstractWidget)this.widgets.getFirst();
      }

      public void addWidget(AbstractWidget widget, int offsetX) {
         this.addWidget(widget, offsetX, -widget.getHeight() / 2);
      }

      public void addWidget(AbstractWidget widget, int offsetX, int offsetY) {
         this.widgets.add(widget);
         this.widgetOffsets.add(new Vector2i(offsetX, offsetY));
      }

      public List<? extends AbstractWidget> children() {
         return this.widgets;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children();
      }

      public void render(
         GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime
      ) {
         for (AbstractWidget widget : this.widgets) {
            Vector2i offset = this.widgetOffsets.get(this.widgets.indexOf(widget));
            widget.setX(rowLeft + width - 110 + offset.x);
            widget.setY(rowTop + height / 2 + offset.y);
            widget.render(guiGraphics, mouseX, mouseY, deltaTime);
         }
      }

      public void setDisabled(boolean b) {
         for (AbstractWidget widget : this.widgets) {
            widget.active = !b;
            if (widget instanceof EditBox box) {
               box.setEditable(!b);
            }
         }
      }

      public List<Component> getDescription() {
         return this.description;
      }

      public List<Component> getDescriptionOnShift() {
         return List.of();
      }

      public int getTextX(int width) {
         return 0;
      }

      public int getTextWidth() {
         return 0;
      }

      public OptionsList.Entry parent(OptionsList.Entry parent) {
         this.parent = parent;
         if (parent.children.isEmpty()) {
            parent.children = Lists.newArrayList();
         }

         parent.children.add(this);
         return this;
      }

      public OptionsList.Entry parent() {
         return this.parent;
      }

      public OptionsList.Entry root() {
         OptionsList.Entry entry = this;

         while (entry.parent() != null) {
            entry = entry.parent();
         }

         return entry;
      }

      public final List<String> getMessages() {
         return this.messages;
      }

      public void addMessage(String message) {
         this.messages.add(StringUtil.stripColor(message).toLowerCase(Locale.ENGLISH));
      }

      public void addMessageKey(String key) {
         key = makeKey(key + "_extra_msg");
         if (I18n.exists(key)) {
            this.addMessage(I18n.get(key, new Object[0]));
         }
      }
   }

   public static class Title extends OptionsList.Entry {
      public Component narration;
      private final MutableComponent title;
      private int x;

      public Title(String key) {
         this.title = makeTitle(key);
         this.addMessageKey(key);
         this.addMessage(this.title.getString());
         key = makeKey(key + "_desc");
         if (I18n.exists(key)) {
            this.description = List.of(Component.translatable(key));
            this.addMessage(((Component)this.description.getFirst()).getString());
         }

         this.narration = Component.translatable("narration.jade.category", new Object[]{this.title});
      }

      public Title(MutableComponent title) {
         this.title = title;
         this.narration = title;
      }

      public MutableComponent getTitle() {
         return this.title;
      }

      @Override
      public void render(
         GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime
      ) {
         this.x = rowLeft;
         guiGraphics.drawString(this.client.font, this.title, this.getTextX(width), rowTop + height - 9, 16777215);
      }

      @Override
      public int getTextX(int width) {
         return this.x + (width - this.client.font.width(this.title)) / 2;
      }

      @Override
      public int getTextWidth() {
         return this.client.font.width(this.title);
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return List.of(new NarratableEntry() {
            public NarrationPriority narrationPriority() {
               return NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput narrationElementOutput) {
               narrationElementOutput.add(NarratedElementType.TITLE, Title.this.narration);
            }
         });
      }
   }
}
