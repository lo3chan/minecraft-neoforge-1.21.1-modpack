/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.AbstractContainerEventHandler
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.narration.NarratableEntry$NarrationPriority
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Math
 */
package net.diebuddies.physics.settings.gui.legacy;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

public abstract class LegacyAbstractSelectionList<E extends LegacyEntry<E>>
extends AbstractContainerEventHandler
implements Renderable,
NarratableEntry {
    protected final Minecraft minecraft;
    public boolean renderBackgroundWhenIngame = true;
    protected final int itemHeight;
    private final List<E> children = new TrackedList();
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
    private static float currentMouseX = Float.MAX_VALUE;
    private static float currentMouseY = Float.MAX_VALUE;
    private static float oldMouseX;
    private static float oldMouseY;
    private static float nextMouseX;
    private static float nextMouseY;
    private float mouseSmoothness = 0.15f;
    private float time = 0.0f;

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
        if (nextMouseX != Float.MAX_VALUE) {
            currentMouseX = Math.lerp((float)currentMouseX, (float)nextMouseX, (float)this.mouseSmoothness);
            currentMouseY = Math.lerp((float)currentMouseY, (float)nextMouseY, (float)this.mouseSmoothness);
        }
    }

    public void init() {
        if (currentMouseX == Float.MAX_VALUE) {
            currentMouseX = (float)this.width * 0.5f + (float)this.x0;
            currentMouseY = (float)this.height * 0.5f + (float)this.y0;
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
        return (E)((LegacyEntry)super.getFocused());
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
        if (i >= this.children().size()) {
            return null;
        }
        return (E)((LegacyEntry)this.children().get(i));
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
        int m = Mth.floor((double)(e - (double)this.y0)) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int n = m / this.itemHeight;
        if (d < (double)this.getScrollbarPosition() && d >= (double)k && d <= (double)l && n >= 0 && m >= 0 && n < this.getItemCount()) {
            return (E)((LegacyEntry)this.children().get(n));
        }
        return null;
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
        int maxScroll;
        if (nextMouseX == Float.MAX_VALUE) {
            currentMouseX = net.diebuddies.math.Math.clamp((float)mouseX, 0.0f, (float)this.width);
            currentMouseY = net.diebuddies.math.Math.clamp((float)mouseY, 0.0f, (float)this.height);
        }
        this.time += delta;
        while (this.time >= 1.0f) {
            this.time -= 1.0f;
            this.tick();
        }
        nextMouseX = net.diebuddies.math.Math.clamp((float)mouseX, 0.0f, (float)this.width);
        nextMouseY = net.diebuddies.math.Math.clamp((float)mouseY, 0.0f, (float)this.height);
        float lMouseX = Math.lerp((float)oldMouseX, (float)currentMouseX, (float)this.time);
        float lMouseY = Math.lerp((float)oldMouseY, (float)currentMouseY, (float)this.time);
        this.renderBackground(guiGraphics);
        int scrollPosition = this.getScrollbarPosition();
        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        this.hovered = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        Object v0 = this.hovered;
        if (this.renderBackground && (this.renderBackgroundWhenIngame || !this.renderBackgroundWhenIngame && this.minecraft.level == null)) {
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)GUIResources.BACKGROUND_TEXTURE);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            int color = FastColor.ARGB32.color((int)255, (int)64, (int)64, (int)64);
            int depth = -130;
            float offsetMultiplier = 0.001f;
            float uOffset = lMouseX * offsetMultiplier;
            float vOffset = lMouseY * offsetMultiplier;
            bufferBuilder.addVertex((float)this.xOffset, (float)this.y1, (float)depth).setUv((float)this.xOffset / 32.0f + uOffset, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0f + vOffset).setColor(color);
            bufferBuilder.addVertex((float)this.x1, (float)this.y1, (float)depth).setUv((float)this.x1 / 32.0f + uOffset, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0f + vOffset).setColor(color);
            bufferBuilder.addVertex((float)this.x1, (float)this.y0, (float)depth).setUv((float)this.x1 / 32.0f + uOffset, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0f + vOffset).setColor(color);
            bufferBuilder.addVertex((float)this.xOffset, (float)this.y0, (float)depth).setUv((float)this.xOffset / 32.0f + uOffset, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0f + vOffset).setColor(color);
            BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        }
        int xStart = this.getRowLeft();
        int yStart = this.y0 + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(guiGraphics, xStart, yStart, tesselator);
        }
        this.renderList(guiGraphics, xStart, yStart, mouseX, mouseY, delta);
        if (this.renderTopAndBottom) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)GUIResources.BACKGROUND_TEXTURE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc((int)519);
            int color = FastColor.ARGB32.color((int)255, (int)96, (int)96, (int)96);
            int depth = -120;
            float offsetMultiplier = 0.0f;
            float uOffset = lMouseX * offsetMultiplier;
            float vOffset = lMouseY * offsetMultiplier;
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.addVertex((float)this.xOffset, (float)this.y0, (float)depth).setUv(uOffset, (float)this.y0 / 32.0f + vOffset).setColor(color);
            bufferBuilder.addVertex((float)(this.xOffset + this.width), (float)this.y0, (float)depth).setUv((float)this.width / 32.0f + uOffset, (float)this.y0 / 32.0f + vOffset).setColor(color);
            bufferBuilder.addVertex((float)(this.xOffset + this.width), 0.0f, (float)depth).setUv((float)this.width / 32.0f + uOffset, vOffset).setColor(color);
            bufferBuilder.addVertex((float)this.xOffset, 0.0f, (float)depth).setUv(uOffset, vOffset).setColor(color);
            bufferBuilder.addVertex((float)this.xOffset, (float)this.height, (float)depth).setUv(uOffset, (float)this.height / 32.0f + vOffset).setColor(color);
            bufferBuilder.addVertex((float)(this.xOffset + this.width), (float)this.height, (float)depth).setUv((float)this.width / 32.0f + uOffset, (float)this.height / 32.0f + vOffset).setColor(color);
            bufferBuilder.addVertex((float)(this.xOffset + this.width), (float)this.y1, (float)depth).setUv((float)this.width / 32.0f + uOffset, (float)this.y1 / 32.0f + vOffset).setColor(color);
            bufferBuilder.addVertex((float)this.xOffset, (float)this.y1, (float)depth).setUv(uOffset, (float)this.y1 / 32.0f + vOffset).setColor(color);
            BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
            RenderSystem.depthFunc((int)515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ZERO, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int gradientSize = 4;
            bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.addVertex((float)this.xOffset, (float)(this.y0 + gradientSize), (float)depth).setColor(0, 0, 0, 0);
            bufferBuilder.addVertex((float)this.x1, (float)(this.y0 + gradientSize), (float)depth).setColor(0, 0, 0, 0);
            bufferBuilder.addVertex((float)this.x1, (float)this.y0, (float)depth).setColor(0, 0, 0, 255);
            bufferBuilder.addVertex((float)this.xOffset, (float)this.y0, (float)depth).setColor(0, 0, 0, 255);
            bufferBuilder.addVertex((float)this.xOffset, (float)this.y1, (float)depth).setColor(0, 0, 0, 255);
            bufferBuilder.addVertex((float)this.x1, (float)this.y1, (float)depth).setColor(0, 0, 0, 255);
            bufferBuilder.addVertex((float)this.x1, (float)(this.y1 - gradientSize), (float)depth).setColor(0, 0, 0, 0);
            bufferBuilder.addVertex((float)this.xOffset, (float)(this.y1 - gradientSize), (float)depth).setColor(0, 0, 0, 0);
            BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        }
        if ((maxScroll = this.getMaxScroll()) > 0) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int barHeight = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            barHeight = Mth.clamp((int)barHeight, (int)32, (int)(this.y1 - this.y0 - 8));
            int yPos = (int)this.getScrollAmount() * (this.y1 - this.y0 - barHeight) / maxScroll + this.y0;
            if (yPos < this.y0) {
                yPos = this.y0;
            }
            int barBackground = FastColor.ARGB32.color((int)255, (int)14, (int)14, (int)14);
            int barWidth = 4;
            int depth = -120;
            int xScrollOffset = (scrollPosition += 2) + barWidth;
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.addVertex((float)scrollPosition, (float)this.y1, (float)depth).setColor(barBackground);
            bufferBuilder.addVertex((float)xScrollOffset, (float)this.y1, (float)depth).setColor(barBackground);
            bufferBuilder.addVertex((float)xScrollOffset, (float)this.y0, (float)depth).setColor(barBackground);
            bufferBuilder.addVertex((float)scrollPosition, (float)this.y0, (float)depth).setColor(barBackground);
            barBackground = FastColor.ARGB32.color((int)255, (int)22, (int)22, (int)22);
            bufferBuilder.addVertex((float)(scrollPosition + 1), (float)this.y1, (float)depth).setColor(barBackground);
            bufferBuilder.addVertex((float)xScrollOffset, (float)this.y1, (float)depth).setColor(barBackground);
            bufferBuilder.addVertex((float)xScrollOffset, (float)this.y0, (float)depth).setColor(barBackground);
            bufferBuilder.addVertex((float)(scrollPosition + 1), (float)this.y0, (float)depth).setColor(barBackground);
            int highlightColor = BaseColors.BAR_COLOR;
            if (mouseX >= scrollPosition - 2 && mouseX < scrollPosition + 4) {
                highlightColor = BaseColors.HIGHLIGHT_COLOR;
            }
            int darkenFactor = 198;
            int darkerColor = FastColor.ARGB32.multiply((int)highlightColor, (int)FastColor.ARGB32.color((int)255, (int)darkenFactor, (int)darkenFactor, (int)darkenFactor));
            bufferBuilder.addVertex((float)scrollPosition, (float)(yPos + barHeight), (float)depth).setColor(darkerColor);
            bufferBuilder.addVertex((float)xScrollOffset, (float)(yPos + barHeight), (float)depth).setColor(darkerColor);
            bufferBuilder.addVertex((float)xScrollOffset, (float)yPos, (float)depth).setColor(darkerColor);
            bufferBuilder.addVertex((float)scrollPosition, (float)yPos, (float)depth).setColor(darkerColor);
            bufferBuilder.addVertex((float)(scrollPosition + 1), (float)(yPos + barHeight), (float)depth).setColor(highlightColor);
            bufferBuilder.addVertex((float)xScrollOffset, (float)(yPos + barHeight), (float)depth).setColor(highlightColor);
            bufferBuilder.addVertex((float)xScrollOffset, (float)(yPos + 1), (float)depth).setColor(highlightColor);
            bufferBuilder.addVertex((float)(scrollPosition + 1), (float)(yPos + 1), (float)depth).setColor(highlightColor);
            BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
        }
        this.renderDecorations(guiGraphics, mouseX, mouseY);
        RenderSystem.disableBlend();
    }

    protected void centerScrollOn(E entry) {
        this.setScrollAmount(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2);
    }

    protected void ensureVisible(E entry) {
        int k;
        int i = this.getRowTop(this.children().indexOf(entry));
        int j = i - this.y0 - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }
        if ((k = this.y1 - i - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-k);
        }
    }

    private void scroll(int i) {
        this.setScrollAmount(this.getScrollAmount() + (double)i);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double d) {
        this.scrollAmount = Mth.clamp((double)d, (double)0.0, (double)this.getMaxScroll());
    }

    public int getMaxScroll() {
        return java.lang.Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }

    public int getScrollBottom() {
        return (int)this.getScrollAmount() - this.height - this.headerHeight;
    }

    protected void updateScrollingState(double d, double e, int i) {
        this.scrolling = i == 0 && d >= (double)this.getScrollbarPosition() && d < (double)(this.getScrollbarPosition() + 6);
    }

    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }

    public boolean mouseClicked(double d, double e, int i) {
        this.updateScrollingState(d, e, i);
        if (!this.isMouseOver(d, e)) {
            return false;
        }
        E entry = this.getEntryAtPosition(d, e);
        if (entry != null) {
            if (entry.mouseClicked(d, e, i)) {
                this.setFocused((GuiEventListener)entry);
                this.setDragging(true);
                return true;
            }
        } else if (i == 0) {
            this.clickedHeader((int)(d - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
        }
        return this.scrolling;
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
        }
        if (i != 0 || !this.scrolling) {
            return false;
        }
        if (e < (double)this.y0) {
            this.setScrollAmount(0.0);
        } else if (e > (double)this.y1) {
            this.setScrollAmount(this.getMaxScroll());
        } else {
            double h = java.lang.Math.max(1, this.getMaxScroll());
            int j = this.y1 - this.y0;
            int k = Mth.clamp((int)((int)((float)(j * j) / (float)this.getMaxPosition())), (int)32, (int)(j - 8));
            double l = java.lang.Math.max(1.0, h / (double)(j - k));
            this.setScrollAmount(this.getScrollAmount() + g * l);
        }
        return true;
    }

    public boolean mouseScrolled(double d, double e, double f, double g) {
        this.setScrollAmount(this.getScrollAmount() - g * (double)this.itemHeight / 2.0);
        return true;
    }

    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (i == 264) {
            this.moveSelection(SelectionDirection.DOWN);
            return true;
        }
        if (i == 265) {
            this.moveSelection(SelectionDirection.UP);
            return true;
        }
        return false;
    }

    protected void moveSelection(SelectionDirection selectionDirection) {
        this.moveSelection(selectionDirection, entry -> true);
    }

    protected void refreshSelection() {
        E entry = this.getSelected();
        if (entry != null) {
            this.setSelected(entry);
            this.ensureVisible(entry);
        }
    }

    protected void moveSelection(SelectionDirection selectionDirection, Predicate<E> predicate) {
        int i;
        int n = i = selectionDirection == SelectionDirection.UP ? -1 : 1;
        if (!this.children().isEmpty()) {
            int k;
            int j = this.children().indexOf(this.getSelected());
            while (j != (k = Mth.clamp((int)(j + i), (int)0, (int)(this.getItemCount() - 1)))) {
                LegacyEntry entry = (LegacyEntry)this.children().get(k);
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
        return e >= (double)this.y0 && e <= (double)this.y1 && d >= (double)this.x0 && d <= (double)this.x1;
    }

    protected void renderList(GuiGraphics guiGraphics, int i, int j, int k, int l, float f) {
        int m = this.getItemCount();
        Tesselator tesselator = Tesselator.getInstance();
        for (int n = 0; n < m; ++n) {
            int t;
            int o = this.getRowTop(n);
            int p = this.getRowBottom(n);
            if (p < this.y0 || o > this.y1) continue;
            int q = j + n * this.itemHeight + this.headerHeight;
            int r = this.itemHeight - 4;
            E entry = this.getEntry(n);
            int s = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(n)) {
                t = this.x0 + this.width / 2 - s / 2;
                int u = this.x0 + this.width / 2 + s / 2;
                RenderSystem.setShader(GameRenderer::getPositionShader);
                float g = this.isFocused() ? 1.0f : 0.5f;
                RenderSystem.setShaderColor((float)g, (float)g, (float)g, (float)1.0f);
                BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                bufferBuilder.addVertex((float)t, (float)(q + r + 2), 0.0f);
                bufferBuilder.addVertex((float)u, (float)(q + r + 2), 0.0f);
                bufferBuilder.addVertex((float)u, (float)(q - 2), 0.0f);
                bufferBuilder.addVertex((float)t, (float)(q - 2), 0.0f);
                BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
                RenderSystem.setShaderColor((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                bufferBuilder.addVertex((float)(t + 1), (float)(q + r + 1), 0.0f);
                bufferBuilder.addVertex((float)(u - 1), (float)(q + r + 1), 0.0f);
                bufferBuilder.addVertex((float)(u - 1), (float)(q - 1), 0.0f);
                bufferBuilder.addVertex((float)(t + 1), (float)(q - 1), 0.0f);
                BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            }
            t = this.getRowLeft();
            ((LegacyEntry)entry).render(guiGraphics, n, o, t, s, r, k, l, Objects.equals(this.hovered, entry), f);
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

    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        if (this.hovered != null) {
            return NarratableEntry.NarrationPriority.HOVERED;
        }
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Nullable
    protected E remove(int i) {
        LegacyEntry entry = (LegacyEntry)this.children.get(i);
        if (this.removeEntry((LegacyEntry)this.children.get(i))) {
            return (E)entry;
        }
        return null;
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

    void bindEntryToSelf(LegacyEntry<E> entry) {
        entry.list = this;
    }

    protected void narrateListElementPosition(NarrationElementOutput narrationElementOutput, E entry) {
        int i;
        List<E> list = this.children();
        if (list.size() > 1 && (i = list.indexOf(entry)) != -1) {
            narrationElementOutput.add(NarratedElementType.POSITION, (Component)Component.translatable((String)"narrator.position.list", (Object[])new Object[]{i + 1, list.size()}));
        }
    }

    static {
        nextMouseX = Float.MAX_VALUE;
        nextMouseY = Float.MAX_VALUE;
    }

    class TrackedList
    extends AbstractList<E> {
        private final List<E> delegate = Lists.newArrayList();

        TrackedList() {
        }

        @Override
        public E get(int i) {
            return (LegacyEntry)this.delegate.get(i);
        }

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public E set(int i, E entry) {
            LegacyEntry entry2 = (LegacyEntry)this.delegate.set(i, entry);
            LegacyAbstractSelectionList.this.bindEntryToSelf(entry);
            return entry2;
        }

        @Override
        public void add(int i, E entry) {
            this.delegate.add(i, entry);
            LegacyAbstractSelectionList.this.bindEntryToSelf(entry);
        }

        @Override
        public E remove(int i) {
            return (LegacyEntry)this.delegate.remove(i);
        }
    }

    public static abstract class LegacyEntry<E extends LegacyEntry<E>>
    implements GuiEventListener {
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
}

