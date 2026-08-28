/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.layouts.FrameLayout
 *  net.minecraft.client.gui.layouts.GridLayout
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.layouts.LayoutSettings
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.apache.commons.lang3.exception.ExceptionUtils
 */
package net.irisshaders.iris.gui.debug;

import java.io.IOException;
import java.util.Objects;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.debug.DebugTextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class DebugLoadFailedGridScreen
extends Screen {
    private final Exception exception;
    private final Screen parent;

    public DebugLoadFailedGridScreen(Screen parent, Component arg, Exception exception) {
        super(arg);
        this.parent = parent;
        this.exception = exception;
    }

    protected void init() {
        super.init();
        GridLayout widget = new GridLayout();
        LayoutSettings layoutSettings = widget.newCellSettings().alignVerticallyTop().alignHorizontallyCenter();
        LayoutSettings layoutSettings4 = widget.newCellSettings().alignVerticallyTop().paddingTop(30).alignHorizontallyCenter();
        LayoutSettings layoutSettings2 = widget.newCellSettings().alignVerticallyTop().paddingTop(30).alignHorizontallyLeft();
        LayoutSettings layoutSettings3 = widget.newCellSettings().alignVerticallyTop().paddingTop(30).alignHorizontallyRight();
        int numWidgets = 0;
        Objects.requireNonNull(this.font);
        widget.addChild((LayoutElement)new DebugTextWidget(0, 0, this.width - 80, 9 * 15, this.font, this.exception), ++numWidgets, 0, 1, 2, layoutSettings);
        widget.addChild((LayoutElement)Button.builder((Component)Component.translatable((String)"menu.returnToGame"), arg2 -> this.minecraft.setScreen(this.parent)).width(100).build(), ++numWidgets, 0, 1, 2, layoutSettings2);
        widget.addChild((LayoutElement)Button.builder((Component)Component.literal((String)"Reload pack"), arg2 -> {
            Minecraft.getInstance().setScreen(this.parent);
            try {
                Iris.reload();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).width(100).build(), numWidgets, 0, 1, 2, layoutSettings3);
        widget.addChild((LayoutElement)Button.builder((Component)Component.literal((String)"Copy error"), arg2 -> this.minecraft.keyboardHandler.setClipboard(ExceptionUtils.getStackTrace((Throwable)this.exception))).width(100).build(), numWidgets, 0, 1, 2, layoutSettings4);
        widget.arrangeElements();
        FrameLayout.centerInRectangle((LayoutElement)widget, (int)0, (int)0, (int)this.width, (int)this.height);
        widget.visitWidgets(x$0 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget((GuiEventListener)x$0);
        });
    }
}

