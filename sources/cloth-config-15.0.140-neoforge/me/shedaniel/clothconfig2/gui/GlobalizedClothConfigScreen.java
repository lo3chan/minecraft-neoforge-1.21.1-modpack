package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.Expandable;
import me.shedaniel.clothconfig2.api.LazyResettable;
import me.shedaniel.clothconfig2.api.ReferenceBuildingConfigScreen;
import me.shedaniel.clothconfig2.api.ReferenceProvider;
import me.shedaniel.clothconfig2.api.ScissorsHandler;
import me.shedaniel.clothconfig2.api.scroll.ScrollingContainer;
import me.shedaniel.clothconfig2.gui.entries.EmptyEntry;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import me.shedaniel.clothconfig2.gui.widget.SearchFieldEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;

public class GlobalizedClothConfigScreen extends AbstractConfigScreen implements ReferenceBuildingConfigScreen, Expandable {
   public ClothConfigScreen.ListWidget<AbstractConfigEntry<AbstractConfigEntry<?>>> listWidget;
   private AbstractWidget cancelButton;
   private AbstractWidget exitButton;
   private final LinkedHashMap<Component, List<AbstractConfigEntry<?>>> categorizedEntries = Maps.newLinkedHashMap();
   private final ScrollingContainer sideScroller = new ScrollingContainer() {
      @Override
      public Rectangle getBounds() {
         return new Rectangle(4, 4, GlobalizedClothConfigScreen.this.getSideSliderPosition() - 14 - 4, GlobalizedClothConfigScreen.this.height - 8);
      }

      @Override
      public int getMaxScrollHeight() {
         int i = 0;

         for (GlobalizedClothConfigScreen.Reference reference : GlobalizedClothConfigScreen.this.references) {
            if (i != 0) {
               i = (int)(i + 3.0F * reference.getScale());
            }

            i = (int)(i + 9.0F * reference.getScale());
         }

         return i;
      }
   };
   private GlobalizedClothConfigScreen.Reference lastHoveredReference = null;
   private SearchFieldEntry searchFieldEntry;
   private final ScrollingContainer sideSlider = new ScrollingContainer() {
      private final Rectangle empty = new Rectangle();

      @Override
      public Rectangle getBounds() {
         return this.empty;
      }

      @Override
      public int getMaxScrollHeight() {
         return 1;
      }
   };
   private final List<GlobalizedClothConfigScreen.Reference> references = Lists.newArrayList();
   private final LazyResettable<Integer> sideExpandLimit = new LazyResettable<>(() -> {
      int max = 0;

      for (GlobalizedClothConfigScreen.Reference reference : this.references) {
         Component referenceText = reference.getText();
         int width = this.font.width(Component.literal(StringUtils.repeat("  ", reference.getIndent()) + "- ").append(referenceText));
         if (width > max) {
            max = width;
         }
      }

      return Math.min(max + 8, this.width / 4);
   });
   private boolean requestingReferenceRebuilding = false;

   @Internal
   public GlobalizedClothConfigScreen(Screen parent, Component title, Map<String, ConfigCategory> categoryMap, ResourceLocation backgroundLocation) {
      super(parent, title, backgroundLocation);
      categoryMap.forEach((categoryName, category) -> {
         List<AbstractConfigEntry<?>> entries = Lists.newArrayList();

         for (Object object : category.getEntries()) {
            AbstractConfigListEntry<?> entry;
            if (object instanceof Tuple) {
               entry = (AbstractConfigListEntry<?>)((Tuple)object).getB();
            } else {
               entry = (AbstractConfigListEntry<?>)object;
            }

            entry.setScreen(this);
            entries.add(entry);
         }

         this.categorizedEntries.put(category.getCategoryKey(), entries);
      });
      this.sideSlider.scrollTo(0.0, false);
   }

   @Override
   public void requestReferenceRebuilding() {
      this.requestingReferenceRebuilding = true;
   }

   @Override
   public Map<Component, List<AbstractConfigEntry<?>>> getCategorizedEntries() {
      return this.categorizedEntries;
   }

   protected void init() {
      super.init();
      this.sideExpandLimit.reset();
      this.references.clear();
      this.buildReferences();
      this.addWidget(
         this.listWidget = new ClothConfigScreen.ListWidget<>(
            this, this.minecraft, this.width - 14, this.height, 30, this.height - 32, this.getBackgroundLocation()
         )
      );
      this.listWidget.setLeftPos(14);
      this.listWidget.children().add(new EmptyEntry(5));
      this.listWidget.children().add(this.searchFieldEntry = new SearchFieldEntry(this, this.listWidget));
      this.listWidget.children().add(new EmptyEntry(5));
      this.categorizedEntries.forEach((category, entries) -> {
         if (!this.listWidget.children().isEmpty()) {
            this.listWidget.children().add(new EmptyEntry(5));
         }

         this.listWidget.children().add(new EmptyEntry(4));
         this.listWidget.children().add(new GlobalizedClothConfigScreen.CategoryTextEntry(category, category.copy().withStyle(ChatFormatting.BOLD)));
         this.listWidget.children().add(new EmptyEntry(4));
         this.listWidget.children().addAll(entries);
      });
      int buttonWidths = Math.min(200, (this.width - 50 - 12) / 3);
      this.addRenderableWidget(
         this.cancelButton = Button.builder(
               this.isEdited() ? Component.translatable("text.cloth-config.cancel_discard") : Component.translatable("gui.cancel"), widget -> this.quit()
            )
            .bounds(0, this.height - 26, buttonWidths, 20)
            .build()
      );
      this.addRenderableWidget(
         this.exitButton = new Button(0, this.height - 26, buttonWidths, 20, Component.empty(), button -> this.saveAll(true), Supplier::get) {
            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
               boolean hasErrors = false;

               label35:
               for (List<AbstractConfigEntry<?>> entries : GlobalizedClothConfigScreen.this.categorizedEntries.values()) {
                  for (AbstractConfigEntry<?> entry : entries) {
                     if (entry.getConfigError().isPresent()) {
                        hasErrors = true;
                        break label35;
                     }
                  }
               }

               this.active = GlobalizedClothConfigScreen.this.isEdited() && !hasErrors;
               this.setMessage(
                  hasErrors ? Component.translatable("text.cloth-config.error_cannot_save") : Component.translatable("text.cloth-config.save_and_done")
               );
               super.renderWidget(graphics, mouseX, mouseY, delta);
            }
         }
      );
      Optional.ofNullable(this.afterInitConsumer).ifPresent(consumer -> consumer.accept(this));
   }

   @Override
   public boolean matchesSearch(Iterator<String> tags) {
      return this.searchFieldEntry.matchesSearch(tags);
   }

   private void buildReferences() {
      this.categorizedEntries.forEach((categoryText, entries) -> {
         this.references.add(new GlobalizedClothConfigScreen.CategoryReference(categoryText));

         for (AbstractConfigEntry<?> entry : entries) {
            this.buildReferenceFor(entry, 1);
         }
      });
   }

   private void buildReferenceFor(AbstractConfigEntry<?> entry, int layer) {
      List<ReferenceProvider<?>> referencableEntries = entry.getReferenceProviderEntries();
      if (referencableEntries != null) {
         this.references.add(new GlobalizedClothConfigScreen.ConfigEntryReference(entry, layer));

         for (ReferenceProvider<?> referencableEntry : referencableEntries) {
            this.buildReferenceFor(referencableEntry.provideReferenceEntry(), layer + 1);
         }
      }
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      this.lastHoveredReference = null;
      if (this.requestingReferenceRebuilding) {
         this.references.clear();
         this.buildReferences();
         this.requestingReferenceRebuilding = false;
      }

      int sliderPosition = this.getSideSliderPosition();
      if (!this.isTransparentBackground()) {
         ScissorsHandler.INSTANCE.scissor(new Rectangle(sliderPosition, 0, this.width - sliderPosition, this.height));
         this.renderMenuBackground(graphics);
         this.overlayBackground(graphics, new Rectangle(14, 0, this.width, this.height), 64, 64, 64, 255, 255);
      } else {
         if (this.minecraft.level == null) {
            this.renderPanorama(graphics, delta);
         }

         this.renderBlurredBackground(delta);
         this.renderMenuBackground(graphics);
         ScissorsHandler.INSTANCE.scissor(new Rectangle(sliderPosition, 0, this.width - sliderPosition, this.height));
      }

      this.listWidget.width = this.width - sliderPosition;
      this.listWidget.setLeftPos(sliderPosition);
      this.listWidget.render(graphics, mouseX, mouseY, delta);
      ScissorsHandler.INSTANCE
         .scissor(new Rectangle(this.listWidget.left, this.listWidget.top, this.listWidget.width, this.listWidget.bottom - this.listWidget.top));

      for (AbstractConfigEntry<?> child : this.listWidget.children()) {
         child.lateRender(graphics, mouseX, mouseY, delta);
      }

      ScissorsHandler.INSTANCE.removeLastScissor();
      graphics.drawString(
         this.font, this.title.getVisualOrderText(), (int)(sliderPosition + (this.width - sliderPosition) / 2.0F - this.font.width(this.title) / 2.0F), 12, -1
      );
      ScissorsHandler.INSTANCE.removeLastScissor();
      this.cancelButton.setX(sliderPosition + (this.width - sliderPosition) / 2 - this.cancelButton.getWidth() - 3);
      this.exitButton.setX(sliderPosition + (this.width - sliderPosition) / 2 + 3);
      super.render(graphics, mouseX, mouseY, delta);
      this.sideSlider.updatePosition(delta);
      this.sideScroller.updatePosition(delta);
      if (this.isTransparentBackground()) {
         RenderSystem.enableBlend();
         graphics.blit(
            ResourceLocation.withDefaultNamespace("textures/gui/menu_list_background.png"),
            0,
            0,
            sliderPosition,
            this.height,
            sliderPosition,
            this.height,
            32,
            32
         );
         graphics.blit(
            ResourceLocation.withDefaultNamespace("textures/gui/menu_list_background.png"),
            0,
            0,
            sliderPosition - 14,
            this.height,
            sliderPosition - 14,
            this.height,
            32,
            32
         );
         graphics.blit(DynamicEntryListWidget.VERTICAL_HEADER_SEPARATOR, sliderPosition - 1, 0, 0.0F, 0.0F, 1, this.height, 2, 32);
         if (sliderPosition - 14 - 1 > 0) {
            graphics.blit(DynamicEntryListWidget.VERTICAL_HEADER_SEPARATOR, sliderPosition - 14 - 1, 0, 0.0F, 0.0F, 1, this.height, 2, 32);
         }

         RenderSystem.disableBlend();
      } else {
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder buffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderTexture(0, this.getBackgroundLocation());
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         float f = 32.0F;
         buffer.addVertex(sliderPosition - 14, this.height, 0.0F).setUv(0.0F, this.height / 32.0F).setColor(68, 68, 68, 255);
         buffer.addVertex(sliderPosition, this.height, 0.0F).setUv(0.4375F, this.height / 32.0F).setColor(68, 68, 68, 255);
         buffer.addVertex(sliderPosition, 0.0F, 0.0F).setUv(0.4375F, 0.0F).setColor(68, 68, 68, 255);
         buffer.addVertex(sliderPosition - 14, 0.0F, 0.0F).setUv(0.0F, 0.0F).setColor(68, 68, 68, 255);
         buffer.addVertex(0.0F, this.height, 0.0F).setUv(0.0F, (this.height + this.sideScroller.scrollAmountInt()) / 32.0F).setColor(32, 32, 32, 255);
         buffer.addVertex(sliderPosition - 14, this.height, 0.0F)
            .setUv((sliderPosition - 14) / 32.0F, (this.height + this.sideScroller.scrollAmountInt()) / 32.0F)
            .setColor(32, 32, 32, 255);
         buffer.addVertex(sliderPosition - 14, 0.0F, 0.0F)
            .setUv((sliderPosition - 14) / 32.0F, this.sideScroller.scrollAmountInt() / 32.0F)
            .setColor(32, 32, 32, 255);
         buffer.addVertex(0.0F, 0.0F, 0.0F).setUv(0.0F, this.sideScroller.scrollAmountInt() / 32.0F).setColor(32, 32, 32, 255);
         Matrix4f matrix = graphics.pose().last().pose();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         int shadeColor = this.isTransparentBackground() ? 120 : 160;
         buffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         buffer.addVertex(matrix, sliderPosition + 4, 0.0F, 100.0F).setColor(0, 0, 0, 0);
         buffer.addVertex(matrix, sliderPosition, 0.0F, 100.0F).setColor(0, 0, 0, shadeColor);
         buffer.addVertex(matrix, sliderPosition, this.height, 100.0F).setColor(0, 0, 0, shadeColor);
         buffer.addVertex(matrix, sliderPosition + 4, this.height, 100.0F).setColor(0, 0, 0, 0);
         shadeColor /= 2;
         buffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         buffer.addVertex(matrix, sliderPosition - 14, 0.0F, 100.0F).setColor(0, 0, 0, shadeColor);
         buffer.addVertex(matrix, sliderPosition - 14 - 4, 0.0F, 100.0F).setColor(0, 0, 0, 0);
         buffer.addVertex(matrix, sliderPosition - 14 - 4, this.height, 100.0F).setColor(0, 0, 0, 0);
         buffer.addVertex(matrix, sliderPosition - 14, this.height, 100.0F).setColor(0, 0, 0, shadeColor);
         RenderSystem.disableBlend();
      }

      Rectangle slideArrowBounds = new Rectangle(sliderPosition - 14, 0, 14, this.height);
      BufferSource immediate = graphics.bufferSource();
      this.font
         .renderText(
            ">",
            sliderPosition - 7 - this.font.width(">") / 2.0F,
            this.height / 2,
            (slideArrowBounds.contains(mouseX, mouseY) ? 16777120 : 16777215)
               | Mth.clamp(Mth.ceil((1.0 - this.sideSlider.scrollAmount()) * 255.0), 0, 255) << 24,
            false,
            graphics.pose().last().pose(),
            immediate,
            DisplayMode.NORMAL,
            0,
            15728880
         );
      this.font
         .renderText(
            "<",
            sliderPosition - 7 - this.font.width("<") / 2.0F,
            this.height / 2,
            (slideArrowBounds.contains(mouseX, mouseY) ? 16777120 : 16777215) | Mth.clamp(Mth.ceil(this.sideSlider.scrollAmount() * 255.0), 0, 255) << 24,
            false,
            graphics.pose().last().pose(),
            immediate,
            DisplayMode.NORMAL,
            0,
            15728880
         );
      graphics.flush();
      Rectangle scrollerBounds = this.sideScroller.getBounds();
      if (!scrollerBounds.isEmpty()) {
         ScissorsHandler.INSTANCE.scissor(new Rectangle(0, 0, sliderPosition - 14, this.height));
         int scrollOffset = scrollerBounds.y - this.sideScroller.scrollAmountInt();

         for (GlobalizedClothConfigScreen.Reference reference : this.references) {
            graphics.pose().pushPose();
            graphics.pose().scale(reference.getScale(), reference.getScale(), reference.getScale());
            MutableComponent text = Component.literal(StringUtils.repeat("  ", reference.getIndent()) + "- ").append(reference.getText());
            if (this.lastHoveredReference == null
               && new Rectangle(
                     scrollerBounds.x,
                     (int)(scrollOffset - 4.0F * reference.getScale()),
                     (int)(this.font.width(text) * reference.getScale()),
                     (int)((9 + 4) * reference.getScale())
                  )
                  .contains(mouseX, mouseY)) {
               this.lastHoveredReference = reference;
            }

            graphics.drawString(
               this.font, text.getVisualOrderText(), scrollerBounds.x, scrollOffset, this.lastHoveredReference == reference ? 16769544 : 16777215, false
            );
            graphics.pose().popPose();
            scrollOffset = (int)(scrollOffset + (9 + 3) * reference.getScale());
         }

         ScissorsHandler.INSTANCE.removeLastScissor();
         this.sideScroller.renderScrollBar(graphics);
      }
   }

   public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      Rectangle slideBounds = new Rectangle(0, 0, this.getSideSliderPosition() - 14, this.height);
      if (button == 0 && slideBounds.contains(mouseX, mouseY) && this.lastHoveredReference != null) {
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.lastHoveredReference.go();
         return true;
      } else {
         Rectangle slideArrowBounds = new Rectangle(this.getSideSliderPosition() - 14, 0, 14, this.height);
         if (button == 0 && slideArrowBounds.contains(mouseX, mouseY)) {
            this.setExpanded(!this.isExpanded());
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }

   @Override
   public boolean isExpanded() {
      return this.sideSlider.scrollTarget() == 1.0;
   }

   @Override
   public void setExpanded(boolean expanded) {
      this.sideSlider.scrollTo(expanded ? 1.0 : 0.0, true, 2000L);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
      Rectangle slideBounds = new Rectangle(0, 0, this.getSideSliderPosition() - 14, this.height);
      if (amountY != 0.0 && slideBounds.contains(mouseX, mouseY)) {
         this.sideScroller.offset(ClothConfigInitializer.getScrollStep() * -amountY, true);
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, amountX, amountY);
      }
   }

   private int getSideSliderPosition() {
      return (int)(this.sideSlider.scrollAmount() * this.sideExpandLimit.get().intValue() + 14.0);
   }

   private class CategoryReference implements GlobalizedClothConfigScreen.Reference {
      private final Component category;

      public CategoryReference(Component category) {
         this.category = category;
      }

      @Override
      public Component getText() {
         return this.category;
      }

      @Override
      public float getScale() {
         return 1.0F;
      }

      @Override
      public void go() {
         int i = 0;

         for (AbstractConfigEntry<?> child : GlobalizedClothConfigScreen.this.listWidget.children()) {
            if (child instanceof GlobalizedClothConfigScreen.CategoryTextEntry
               && ((GlobalizedClothConfigScreen.CategoryTextEntry)child).category == this.category) {
               GlobalizedClothConfigScreen.this.listWidget.scrollTo(i, true);
               return;
            }

            i += child.getItemHeight();
         }
      }
   }

   private static class CategoryTextEntry extends AbstractConfigListEntry<Object> {
      private final Component category;
      private final Component text;

      public CategoryTextEntry(Component category, Component text) {
         super(Component.literal(UUID.randomUUID().toString()), false);
         this.category = category;
         this.text = text;
      }

      @Override
      public int getItemHeight() {
         List<FormattedCharSequence> strings = Minecraft.getInstance().font.split(this.text, this.getParent().getItemWidth());
         return strings.isEmpty() ? 0 : 4 + strings.size() * 10;
      }

      @Nullable
      @Override
      public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
         return null;
      }

      @Override
      public Object getValue() {
         return null;
      }

      @Override
      public Optional<Object> getDefaultValue() {
         return Optional.empty();
      }

      @Override
      public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
         return false;
      }

      @Override
      public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
         super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
         int yy = y + 2;

         for (FormattedCharSequence text : Minecraft.getInstance().font.split(this.text, this.getParent().getItemWidth())) {
            graphics.drawString(Minecraft.getInstance().font, text, x - 4 + entryWidth / 2 - Minecraft.getInstance().font.width(text) / 2, yy, -1);
            yy += 10;
         }
      }

      public List<? extends GuiEventListener> children() {
         return Collections.emptyList();
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return Collections.emptyList();
      }
   }

   private class ConfigEntryReference implements GlobalizedClothConfigScreen.Reference {
      private final AbstractConfigEntry<?> entry;
      private final int layer;

      public ConfigEntryReference(AbstractConfigEntry<?> entry, int layer) {
         this.entry = entry;
         this.layer = layer;
      }

      @Override
      public int getIndent() {
         return this.layer;
      }

      @Override
      public Component getText() {
         return this.entry.getFieldName();
      }

      @Override
      public float getScale() {
         return 1.0F;
      }

      @Override
      public void go() {
         int[] i = new int[]{0};

         for (AbstractConfigEntry<?> child : GlobalizedClothConfigScreen.this.listWidget.children()) {
            int i1 = i[0];
            if (this.goChild(i, null, child)) {
               return;
            }

            i[0] = i1 + child.getItemHeight();
         }
      }

      private boolean goChild(int[] i, Integer expandedParent, AbstractConfigEntry<?> root) {
         if (root == this.entry) {
            GlobalizedClothConfigScreen.this.listWidget.scrollTo(expandedParent == null ? i[0] : expandedParent.intValue(), true);
            return true;
         } else {
            int j = i[0];
            i[0] += root.getInitialReferenceOffset();
            boolean expanded = root instanceof Expandable && ((Expandable)root).isExpanded();
            if (root instanceof Expandable) {
               ((Expandable)root).setExpanded(true);
            }

            List<? extends GuiEventListener> children = root.children();
            if (root instanceof Expandable) {
               ((Expandable)root).setExpanded(expanded);
            }

            for (GuiEventListener child : children) {
               if (child instanceof ReferenceProvider) {
                  int i1 = i[0];
                  if (this.goChild(
                     i,
                     expandedParent != null ? expandedParent : (root instanceof Expandable && !expanded ? j : null),
                     ((ReferenceProvider)child).provideReferenceEntry()
                  )) {
                     return true;
                  }

                  i[0] = i1 + ((ReferenceProvider)child).provideReferenceEntry().getItemHeight();
               }
            }

            return false;
         }
      }
   }

   private interface Reference {
      default int getIndent() {
         return 0;
      }

      Component getText();

      float getScale();

      void go();
   }
}
