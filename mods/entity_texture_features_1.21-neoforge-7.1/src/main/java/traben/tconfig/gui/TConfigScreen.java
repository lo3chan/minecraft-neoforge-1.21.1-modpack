/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;

public class TConfigScreen
extends Screen {
    private final boolean showBackButton;
    protected Screen parent;
    protected Runnable resetDefaultValuesRunnable = null;
    protected Runnable undoChangesRunnable = null;

    protected TConfigScreen(@Translatable String title, Screen parent, boolean showBackButton) {
        super((Component)Component.translatable((String)title));
        this.parent = parent;
        this.showBackButton = showBackButton;
    }

    protected Component getBackButtonText() {
        return CommonComponents.GUI_BACK;
    }

    protected void init() {
        if (this.showBackButton) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)this.getBackButtonText(), button -> this.onClose()).bounds((int)((double)this.width * 0.7), (int)((double)this.height * 0.9), (int)((double)this.width * 0.2), 20).build());
        }
        if (this.resetDefaultValuesRunnable != null) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)ETF.getTextFromTranslation("dataPack.validation.reset"), button -> {
                this.resetDefaultValuesRunnable.run();
                this.rebuildWidgets();
            }).bounds((int)((double)this.width * 0.4), (int)((double)this.height * 0.9), (int)((double)this.width * 0.22), 20).build());
        }
        if (this.undoChangesRunnable != null) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)ETF.getTextFromTranslation("config.entity_features.undo"), button -> {
                this.undoChangesRunnable.run();
                this.rebuildWidgets();
            }).bounds((int)((double)this.width * 0.1), (int)((double)this.height * 0.9), (int)((double)this.width * 0.2), 20).build());
        }
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 15, -1);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }
}

