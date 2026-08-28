/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractScrollWidget
 *  net.minecraft.client.gui.components.MultiLineTextWidget
 *  net.minecraft.client.gui.layouts.GridLayout
 *  net.minecraft.client.gui.layouts.GridLayout$RowHelper
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.layouts.LayoutSettings
 *  net.minecraft.client.gui.layouts.SpacerElement
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package net.irisshaders.iris.gui.debug;

import java.util.Objects;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class DebugTextWidget
extends AbstractScrollWidget {
    private final Font font;
    private final Content content;

    public DebugTextWidget(int i, int j, int k, int l, Font arg, Exception exception) {
        super(i, j, k, l, (Component)Component.empty());
        this.font = arg;
        this.content = this.buildContent(exception);
    }

    private Content buildContent(Exception exception) {
        if (exception instanceof ShaderCompileException) {
            ShaderCompileException sce = (ShaderCompileException)exception;
            return this.buildContentShader(sce);
        }
        ContentBuilder lv = new ContentBuilder(this.containerWidth());
        StackTraceElement[] elements = exception.getStackTrace();
        lv.addHeader(this.font, (Component)Component.literal((String)"Error: "));
        Objects.requireNonNull(this.font);
        lv.addSpacer(9);
        if (exception.getMessage() != null) {
            lv.addLine(this.font, (Component)Component.literal((String)exception.getMessage()));
        }
        Objects.requireNonNull(this.font);
        lv.addSpacer(9);
        lv.addHeader(this.font, (Component)Component.literal((String)"Stack trace: "));
        Objects.requireNonNull(this.font);
        lv.addSpacer(9);
        for (int i = 0; i < elements.length; ++i) {
            StackTraceElement element = elements[i];
            if (element == null) continue;
            lv.addLine(this.font, (Component)Component.literal((String)element.toString()));
            if (i >= elements.length - 1) continue;
            Objects.requireNonNull(this.font);
            lv.addSpacer(9);
        }
        return lv.build();
    }

    private Content buildContentShader(ShaderCompileException sce) {
        ContentBuilder lv = new ContentBuilder(this.containerWidth());
        lv.addHeader(this.font, (Component)Component.literal((String)("Shader compile error in " + sce.getFilename() + ": ")));
        Objects.requireNonNull(this.font);
        lv.addSpacer(9);
        lv.addLine(this.font, (Component)Component.literal((String)sce.getError()));
        return lv.build();
    }

    protected int getInnerHeight() {
        return this.content.container().getHeight();
    }

    protected boolean scrollbarVisible() {
        return this.getInnerHeight() > this.height;
    }

    protected double scrollRate() {
        Objects.requireNonNull(this.font);
        return 9.0;
    }

    protected void renderContents(GuiGraphics arg, int i, int j, float f) {
        int k = this.getY() + this.innerPadding();
        int l = this.getX() + this.innerPadding();
        arg.pose().pushPose();
        arg.pose().translate((double)l, (double)k, 0.0);
        this.content.container().visitWidgets(element -> element.render(arg, i, j, f));
        arg.pose().popPose();
    }

    protected void updateWidgetNarration(NarrationElementOutput arg) {
        arg.add(NarratedElementType.TITLE, this.content.narration());
    }

    private int containerWidth() {
        return this.width - this.totalInnerPadding();
    }

    record Content(GridLayout container, Component narration) {
    }

    static class ContentBuilder {
        private final int width;
        private final GridLayout grid;
        private final GridLayout.RowHelper helper;
        private final LayoutSettings alignHeader;
        private final MutableComponent narration = Component.empty();

        public ContentBuilder(int i) {
            this.width = i;
            this.grid = new GridLayout();
            this.grid.defaultCellSetting().alignHorizontallyLeft();
            this.helper = this.grid.createRowHelper(1);
            this.helper.addChild((LayoutElement)SpacerElement.width((int)i));
            this.alignHeader = this.helper.newCellSettings().alignHorizontallyCenter().paddingHorizontal(32);
        }

        public void addLine(Font arg, Component arg2) {
            this.addLine(arg, arg2, 0);
        }

        public void addLine(Font arg, Component arg2, int i) {
            this.helper.addChild((LayoutElement)new MultiLineTextWidget(this.width, 1, arg2, arg), this.helper.newCellSettings().paddingBottom(i));
            this.narration.append(arg2).append("\n");
        }

        public void addHeader(Font arg, Component arg2) {
            this.helper.addChild((LayoutElement)new MultiLineTextWidget(this.width - 64, 1, arg2, arg).setCentered(true), this.alignHeader);
            this.narration.append(arg2).append("\n");
        }

        public void addSpacer(int i) {
            this.helper.addChild((LayoutElement)SpacerElement.height((int)i));
        }

        public Content build() {
            this.grid.arrangeElements();
            return new Content(this.grid, (Component)this.narration);
        }
    }
}

