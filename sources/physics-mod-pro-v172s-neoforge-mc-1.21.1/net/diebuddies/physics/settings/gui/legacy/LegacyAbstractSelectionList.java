package net.diebuddies.physics.settings.gui.legacy;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.diebuddies.physics.settings.ux.BaseColors;
import net.diebuddies.physics.settings.ux.GUIResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

public abstract class LegacyAbstractSelectionList<E extends LegacyAbstractSelectionList.LegacyEntry<E>>
   extends AbstractContainerEventHandler
   implements Renderable,
   NarratableEntry {
   protected final Minecraft minecraft;
   public boolean renderBackgroundWhenIngame = true;
   protected final int itemHeight;
   private final List<E> children = new LegacyAbstractSelectionList.TrackedList();
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected int xOffset;
   protected boolean centerListVertically = true;
   private double scrollAmount;
   private boolean renderSelection = true;
   private boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   @Nullable
   private E selected;
   private boolean renderBackground = true;
   private boolean renderTopAndBottom = true;
   @Nullable
   private E hovered;
   private static float currentMouseX = 3.4028235E38F;
   private static float currentMouseY = 3.4028235E38F;
   private static float oldMouseX;
   private static float oldMouseY;
   private static float nextMouseX = 3.4028235E38F;
   private static float nextMouseY = 3.4028235E38F;
   private float mouseSmoothness = 0.15F;
   private float time = 0.0F;

   public LegacyAbstractSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m) {
      this.minecraft = minecraft;
      this.width = i;
      this.height = j;
      this.y0 = k;
      this.y1 = l;
      this.itemHeight = m;
      this.x0 = 0;
      this.x1 = i;
      this.xOffset = this.x0;
      this.init();
   }

   public void tick() {
      this.updatePositions();
      if (nextMouseX != 3.4028235E38F) {
         currentMouseX = Math.lerp(currentMouseX, nextMouseX, this.mouseSmoothness);
         currentMouseY = Math.lerp(currentMouseY, nextMouseY, this.mouseSmoothness);
      }
   }

   public void init() {
      if (currentMouseX == 3.4028235E38F) {
         currentMouseX = this.width * 0.5F + this.x0;
         currentMouseY = this.height * 0.5F + this.y0;
         this.updatePositions();
      }
   }

   private void updatePositions() {
      oldMouseX = currentMouseX;
      oldMouseY = currentMouseY;
   }

   public void setRenderSelection(boolean bl) {
      this.renderSelection = bl;
   }

   protected void setRenderHeader(boolean bl, int i) {
      this.renderHeader = bl;
      this.headerHeight = i;
      if (!bl) {
         this.headerHeight = 0;
      }
   }

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E entry) {
      this.selected = entry;
   }

   public void setRenderBackground(boolean bl) {
      this.renderBackground = bl;
   }

   public void setRenderTopAndBottom(boolean bl) {
      this.renderTopAndBottom = bl;
   }

   @Nullable
   public E getFocused() {
      return (E)super.getFocused();
   }

   public final List<E> children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
   }

   protected void replaceEntries(Collection<E> collection) {
      this.children.clear();
      this.children.addAll(collection);
   }

   protected E getEntry(int i) {
      return i >= this.children().size() ? null : this.children().get(i);
   }

   protected int addEntry(E entry) {
      this.children.add(entry);
      return this.children.size() - 1;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int i) {
      return Objects.equals(this.getSelected(), this.children().get(i));
   }

   @Nullable
   protected final E getEntryAtPosition(double d, double e) {
      int i = this.getRowWidth() / 2;
      int j = this.x0 + this.width / 2;
      int k = j - i;
      int l = j + i;
      int m = Mth.floor(e - this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int n = m / this.itemHeight;
      return d < this.getScrollbarPosition() && d >= k && d <= l && n >= 0 && m >= 0 && n < this.getItemCount() ? this.children().get(n) : null;
   }

   public void updateSize(int i, int j, int k, int l) {
      this.width = i;
      this.height = j;
      this.y0 = k;
      this.y1 = l;
      this.x0 = 0;
      this.x1 = i;
   }

   public void setLeftPos(int i) {
      this.x0 = i;
      this.x1 = i + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int i, int j) {
   }

   protected void renderHeader(GuiGraphics guiGraphics, int i, int j, Tesselator tesselator) {
   }

   protected void renderBackground(GuiGraphics guiGraphics) {
   }

   protected void renderDecorations(GuiGraphics guiGraphics, int i, int j) {
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      if (nextMouseX == 3.4028235E38F) {
         currentMouseX = net.diebuddies.math.Math.clamp((float)mouseX, 0.0F, (float)this.width);
         currentMouseY = net.diebuddies.math.Math.clamp((float)mouseY, 0.0F, (float)this.height);
      }

      this.time += delta;

      while (this.time >= 1.0F) {
         this.time--;
         this.tick();
      }

      nextMouseX = net.diebuddies.math.Math.clamp((float)mouseX, 0.0F, (float)this.width);
      nextMouseY = net.diebuddies.math.Math.clamp((float)mouseY, 0.0F, (float)this.height);
      float lMouseX = Math.lerp(oldMouseX, currentMouseX, this.time);
      float lMouseY = Math.lerp(oldMouseY, currentMouseY, this.time);
      this.renderBackground(guiGraphics);
      int scrollPosition = this.getScrollbarPosition();
      Tesselator tesselator = Tesselator.getInstance();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      this.hovered = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
      if (this.renderBackground && (this.renderBackgroundWhenIngame || !this.renderBackgroundWhenIngame && this.minecraft.level == null)) {
         RenderSystem.setShaderTexture(0, GUIResources.BACKGROUND_TEXTURE);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         BufferBuilder bufferBuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         int color = ARGB32.color(255, 64, 64, 64);
         int depth = -130;
         float offsetMultiplier = 0.001F;
         float uOffset = lMouseX * offsetMultiplier;
         float vOffset = lMouseY * offsetMultiplier;
         bufferBuilder.addVertex(this.xOffset, this.y1, depth)
            .setUv(this.xOffset / 32.0F + uOffset, (this.y1 + (int)this.getScrollAmount()) / 32.0F + vOffset)
            .setColor(color);
         bufferBuilder.addVertex(this.x1, this.y1, depth)
            .setUv(this.x1 / 32.0F + uOffset, (this.y1 + (int)this.getScrollAmount()) / 32.0F + vOffset)
            .setColor(color);
         bufferBuilder.addVertex(this.x1, this.y0, depth)
            .setUv(this.x1 / 32.0F + uOffset, (this.y0 + (int)this.getScrollAmount()) / 32.0F + vOffset)
            .setColor(color);
         bufferBuilder.addVertex(this.xOffset, this.y0, depth)
            .setUv(this.xOffset / 32.0F + uOffset, (this.y0 + (int)this.getScrollAmount()) / 32.0F + vOffset)
            .setColor(color);
         BufferUploader.drawWithShader(bufferBuilder.build());
      }

      int xStart = this.getRowLeft();
      int yStart = this.y0 + 4 - (int)this.getScrollAmount();
      if (this.renderHeader) {
         this.renderHeader(guiGraphics, xStart, yStart, tesselator);
      }

      this.renderList(guiGraphics, xStart, yStart, mouseX, mouseY, delta);
      if (this.renderTopAndBottom) {
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderTexture(0, GUIResources.BACKGROUND_TEXTURE);
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(519);
         int color = ARGB32.color(255, 96, 96, 96);
         int depth = -120;
         float offsetMultiplier = 0.0F;
         float uOffset = lMouseX * offsetMultiplier;
         float vOffset = lMouseY * offsetMultiplier;
         BufferBuilder bufferBuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         bufferBuilder.addVertex(this.xOffset, this.y0, depth).setUv(uOffset, this.y0 / 32.0F + vOffset).setColor(color);
         bufferBuilder.addVertex(this.xOffset + this.width, this.y0, depth).setUv(this.width / 32.0F + uOffset, this.y0 / 32.0F + vOffset).setColor(color);
         bufferBuilder.addVertex(this.xOffset + this.width, 0.0F, depth).setUv(this.width / 32.0F + uOffset, vOffset).setColor(color);
         bufferBuilder.addVertex(this.xOffset, 0.0F, depth).setUv(uOffset, vOffset).setColor(color);
         bufferBuilder.addVertex(this.xOffset, this.height, depth).setUv(uOffset, this.height / 32.0F + vOffset).setColor(color);
         bufferBuilder.addVertex(this.xOffset + this.width, this.height, depth)
            .setUv(this.width / 32.0F + uOffset, this.height / 32.0F + vOffset)
            .setColor(color);
         bufferBuilder.addVertex(this.xOffset + this.width, this.y1, depth).setUv(this.width / 32.0F + uOffset, this.y1 / 32.0F + vOffset).setColor(color);
         bufferBuilder.addVertex(this.xOffset, this.y1, depth).setUv(uOffset, this.y1 / 32.0F + vOffset).setColor(color);
         BufferUploader.drawWithShader(bufferBuilder.build());
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         int gradientSize = 4;
         bufferBuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         bufferBuilder.addVertex(this.xOffset, this.y0 + gradientSize, depth).setColor(0, 0, 0, 0);
         bufferBuilder.addVertex(this.x1, this.y0 + gradientSize, depth).setColor(0, 0, 0, 0);
         bufferBuilder.addVertex(this.x1, this.y0, depth).setColor(0, 0, 0, 255);
         bufferBuilder.addVertex(this.xOffset, this.y0, depth).setColor(0, 0, 0, 255);
         bufferBuilder.addVertex(this.xOffset, this.y1, depth).setColor(0, 0, 0, 255);
         bufferBuilder.addVertex(this.x1, this.y1, depth).setColor(0, 0, 0, 255);
         bufferBuilder.addVertex(this.x1, this.y1 - gradientSize, depth).setColor(0, 0, 0, 0);
         bufferBuilder.addVertex(this.xOffset, this.y1 - gradientSize, depth).setColor(0, 0, 0, 0);
         BufferUploader.drawWithShader(bufferBuilder.build());
      }

      int maxScroll = this.getMaxScroll();
      if (maxScroll > 0) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         int barHeight = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / this.getMaxPosition());
         barHeight = Mth.clamp(barHeight, 32, this.y1 - this.y0 - 8);
         int yPos = (int)this.getScrollAmount() * (this.y1 - this.y0 - barHeight) / maxScroll + this.y0;
         if (yPos < this.y0) {
            yPos = this.y0;
         }

         int barBackground = ARGB32.color(255, 14, 14, 14);
         int barWidth = 4;
         int depth = -120;
         scrollPosition += 2;
         int xScrollOffset = scrollPosition + barWidth;
         BufferBuilder bufferBuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         bufferBuilder.addVertex(scrollPosition, this.y1, depth).setColor(barBackground);
         bufferBuilder.addVertex(xScrollOffset, this.y1, depth).setColor(barBackground);
         bufferBuilder.addVertex(xScrollOffset, this.y0, depth).setColor(barBackground);
         bufferBuilder.addVertex(scrollPosition, this.y0, depth).setColor(barBackground);
         barBackground = ARGB32.color(255, 22, 22, 22);
         bufferBuilder.addVertex(scrollPosition + 1, this.y1, depth).setColor(barBackground);
         bufferBuilder.addVertex(xScrollOffset, this.y1, depth).setColor(barBackground);
         bufferBuilder.addVertex(xScrollOffset, this.y0, depth).setColor(barBackground);
         bufferBuilder.addVertex(scrollPosition + 1, this.y0, depth).setColor(barBackground);
         int highlightColor = BaseColors.BAR_COLOR;
         if (mouseX >= scrollPosition - 2 && mouseX < scrollPosition + 4) {
            highlightColor = BaseColors.HIGHLIGHT_COLOR;
         }

         int darkenFactor = 198;
         int darkerColor = ARGB32.multiply(highlightColor, ARGB32.color(255, darkenFactor, darkenFactor, darkenFactor));
         bufferBuilder.addVertex(scrollPosition, yPos + barHeight, depth).setColor(darkerColor);
         bufferBuilder.addVertex(xScrollOffset, yPos + barHeight, depth).setColor(darkerColor);
         bufferBuilder.addVertex(xScrollOffset, yPos, depth).setColor(darkerColor);
         bufferBuilder.addVertex(scrollPosition, yPos, depth).setColor(darkerColor);
         bufferBuilder.addVertex(scrollPosition + 1, yPos + barHeight, depth).setColor(highlightColor);
         bufferBuilder.addVertex(xScrollOffset, yPos + barHeight, depth).setColor(highlightColor);
         bufferBuilder.addVertex(xScrollOffset, yPos + 1, depth).setColor(highlightColor);
         bufferBuilder.addVertex(scrollPosition + 1, yPos + 1, depth).setColor(highlightColor);
         BufferUploader.drawWithShader(bufferBuilder.build());
      }

      this.renderDecorations(guiGraphics, mouseX, mouseY);
      RenderSystem.disableBlend();
   }

   protected void centerScrollOn(E entry) {
      this.setScrollAmount(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2);
   }

   protected void ensureVisible(E entry) {
      int i = this.getRowTop(this.children().indexOf(entry));
      int j = i - this.y0 - 4 - this.itemHeight;
      if (j < 0) {
         this.scroll(j);
      }

      int k;
      if ((k = this.y1 - i - this.itemHeight - this.itemHeight) < 0) {
         this.scroll(-k);
      }
   }

   private void scroll(int i) {
      this.setScrollAmount(this.getScrollAmount() + i);
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double d) {
      this.scrollAmount = Mth.clamp(d, 0.0, this.getMaxScroll());
   }

   public int getMaxScroll() {
      return java.lang.Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   public int getScrollBottom() {
      return (int)this.getScrollAmount() - this.height - this.headerHeight;
   }

   protected void updateScrollingState(double d, double e, int i) {
      this.scrolling = i == 0 && d >= this.getScrollbarPosition() && d < this.getScrollbarPosition() + 6;
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   public boolean mouseClicked(double d, double e, int i) {
      this.updateScrollingState(d, e, i);
      if (!this.isMouseOver(d, e)) {
         return false;
      } else {
         E entry = this.getEntryAtPosition(d, e);
         if (entry != null) {
            if (entry.mouseClicked(d, e, i)) {
               this.setFocused(entry);
               this.setDragging(true);
               return true;
            }
         } else if (i == 0) {
            this.clickedHeader((int)(d - (this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(e - this.y0) + (int)this.getScrollAmount() - 4);
            return true;
         }

         return this.scrolling;
      }
   }

   public boolean mouseReleased(double d, double e, int i) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(d, e, i);
      }

      return false;
   }

   public boolean mouseDragged(double d, double e, int i, double f, double g) {
      if (super.mouseDragged(d, e, i, f, g)) {
         return true;
      } else if (i == 0 && this.scrolling) {
         if (e < this.y0) {
            this.setScrollAmount(0.0);
         } else if (e > this.y1) {
            this.setScrollAmount(this.getMaxScroll());
         } else {
            double h = java.lang.Math.max(1, this.getMaxScroll());
            int j = this.y1 - this.y0;
            int k = Mth.clamp((int)((float)(j * j) / this.getMaxPosition()), 32, j - 8);
            double l = java.lang.Math.max(1.0, h / (j - k));
            this.setScrollAmount(this.getScrollAmount() + g * l);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double d, double e, double f, double g) {
      this.setScrollAmount(this.getScrollAmount() - g * this.itemHeight / 2.0);
      return true;
   }

   public boolean keyPressed(int i, int j, int k) {
      if (super.keyPressed(i, j, k)) {
         return true;
      } else if (i == 264) {
         this.moveSelection(LegacyAbstractSelectionList.SelectionDirection.DOWN);
         return true;
      } else if (i == 265) {
         this.moveSelection(LegacyAbstractSelectionList.SelectionDirection.UP);
         return true;
      } else {
         return false;
      }
   }

   protected void moveSelection(LegacyAbstractSelectionList.SelectionDirection selectionDirection) {
      this.moveSelection(selectionDirection, entry -> true);
   }

   protected void refreshSelection() {
      E entry = this.getSelected();
      if (entry != null) {
         this.setSelected(entry);
         this.ensureVisible(entry);
      }
   }

   protected void moveSelection(LegacyAbstractSelectionList.SelectionDirection selectionDirection, Predicate<E> predicate) {
      int i = selectionDirection == LegacyAbstractSelectionList.SelectionDirection.UP ? -1 : 1;
      if (!this.children().isEmpty()) {
         int j = this.children().indexOf(this.getSelected());

         int k;
         while (j != (k = Mth.clamp(j + i, 0, this.getItemCount() - 1))) {
            E entry = this.children().get(k);
            if (predicate.test(entry)) {
               this.setSelected(entry);
               this.ensureVisible(entry);
               break;
            }

            j = k;
         }
      }
   }

   public boolean isMouseOver(double d, double e) {
      return e >= this.y0 && e <= this.y1 && d >= this.x0 && d <= this.x1;
   }

   protected void renderList(GuiGraphics guiGraphics, int i, int j, int k, int l, float f) {
      int m = this.getItemCount();
      Tesselator tesselator = Tesselator.getInstance();

      for (int n = 0; n < m; n++) {
         int o = this.getRowTop(n);
         int p = this.getRowBottom(n);
         if (p >= this.y0 && o <= this.y1) {
            int q = j + n * this.itemHeight + this.headerHeight;
            int r = this.itemHeight - 4;
            E entry = this.getEntry(n);
            int s = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(n)) {
               int t = this.x0 + this.width / 2 - s / 2;
               int u = this.x0 + this.width / 2 + s / 2;
               RenderSystem.setShader(GameRenderer::getPositionShader);
               float g = this.isFocused() ? 1.0F : 0.5F;
               RenderSystem.setShaderColor(g, g, g, 1.0F);
               BufferBuilder bufferBuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
               bufferBuilder.addVertex(t, q + r + 2, 0.0F);
               bufferBuilder.addVertex(u, q + r + 2, 0.0F);
               bufferBuilder.addVertex(u, q - 2, 0.0F);
               bufferBuilder.addVertex(t, q - 2, 0.0F);
               BufferUploader.drawWithShader(bufferBuilder.build());
               RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
               bufferBuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
               bufferBuilder.addVertex(t + 1, q + r + 1, 0.0F);
               bufferBuilder.addVertex(u - 1, q + r + 1, 0.0F);
               bufferBuilder.addVertex(u - 1, q - 1, 0.0F);
               bufferBuilder.addVertex(t + 1, q - 1, 0.0F);
               BufferUploader.drawWithShader(bufferBuilder.build());
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            int t = this.getRowLeft();
            entry.render(guiGraphics, n, o, t, s, r, k, l, Objects.equals(this.hovered, entry), f);
         }
      }
   }

   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   protected int getRowTop(int i) {
      return this.y0 + 4 - (int)this.getScrollAmount() + i * this.itemHeight + this.headerHeight;
   }

   private int getRowBottom(int i) {
      return this.getRowTop(i) + this.itemHeight;
   }

   public boolean isFocused() {
      return false;
   }

   public NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarrationPriority.FOCUSED;
      } else {
         return this.hovered != null ? NarrationPriority.HOVERED : NarrationPriority.NONE;
      }
   }

   @Nullable
   protected E remove(int i) {
      E entry = this.children.get(i);
      return this.removeEntry(this.children.get(i)) ? entry : null;
   }

   protected boolean removeEntry(E entry) {
      boolean bl = this.children.remove(entry);
      if (bl && entry == this.getSelected()) {
         this.setSelected(null);
      }

      return bl;
   }

   @Nullable
   public E getHovered() {
      return this.hovered;
   }

   void bindEntryToSelf(LegacyAbstractSelectionList.LegacyEntry<E> entry) {
      entry.list = this;
   }

   protected void narrateListElementPosition(NarrationElementOutput narrationElementOutput, E entry) {
      List<E> list = this.children();
      int i;
      if (list.size() > 1 && (i = list.indexOf(entry)) != -1) {
         narrationElementOutput.add(NarratedElementType.POSITION, Component.translatable("narrator.position.list", new Object[]{i + 1, list.size()}));
      }
   }

   public abstract static class LegacyEntry<E extends LegacyAbstractSelectionList.LegacyEntry<E>> implements GuiEventListener {
      @Deprecated
      LegacyAbstractSelectionList<E> list;

      public abstract void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

      public boolean isMouseOver(double d, double e) {
         return Objects.equals(this.list.getEntryAtPosition(d, e), this);
      }

      public void setFocused(boolean var1) {
      }

      public boolean isFocused() {
         return false;
      }
   }

   protected static enum SelectionDirection {
      UP,
      DOWN;
   }

   class TrackedList extends AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      public E get(int i) {
         return this.delegate.get(i);
      }

      @Override
      public int size() {
         return this.delegate.size();
      }

      public E set(int i, E entry) {
         E entry2 = this.delegate.set(i, entry);
         LegacyAbstractSelectionList.this.bindEntryToSelf(entry);
         return entry2;
      }

      public void add(int i, E entry) {
         this.delegate.add(i, entry);
         LegacyAbstractSelectionList.this.bindEntryToSelf(entry);
      }

      public E remove(int i) {
         return this.delegate.remove(i);
      }
   }
}
