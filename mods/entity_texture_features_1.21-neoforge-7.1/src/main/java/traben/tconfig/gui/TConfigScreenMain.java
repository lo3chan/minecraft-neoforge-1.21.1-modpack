/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package traben.tconfig.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import traben.tconfig.TConfig;
import traben.tconfig.TConfigHandler;
import traben.tconfig.gui.TConfigEntryListWidget;
import traben.tconfig.gui.TConfigScreen;
import traben.tconfig.gui.entries.TConfigEntry;
import traben.tconfig.gui.entries.TConfigEntryCategory;

public abstract class TConfigScreenMain
extends TConfigScreen {
    protected final TConfigEntryCategory entries;
    protected final List<ResourceLocation> modIcons;
    protected final Set<TConfigHandler<?>> configHandlers;
    boolean haveInitConfigs = false;

    public TConfigScreenMain(String title, Screen parent, Set<TConfigHandler<?>> inputHandlers, List<TConfigEntry> defaultEntries) {
        super(title, parent, true);
        this.entries = new TConfigEntryCategory.Empty().addAll(defaultEntries);
        this.modIcons = new ArrayList<ResourceLocation>();
        this.configHandlers = inputHandlers;
        this.resetDefaultValuesRunnable = this.entries::setValuesToDefault;
        this.undoChangesRunnable = this.entries::resetValuesToInitial;
    }

    @Override
    protected Component getBackButtonText() {
        return CommonComponents.GUI_DONE;
    }

    private void initConfigs() {
        if (this.haveInitConfigs) {
            return;
        }
        this.haveInitConfigs = true;
        this.configHandlers.stream().filter(TConfigHandler::doesGUI).forEach(configHandler -> {
            Object config = configHandler.getConfig();
            this.entries.addAll(((TConfig)config).getGUIOptions().getOptions().values());
            ResourceLocation icon = ((TConfig)config).getModIcon();
            if (icon != null) {
                this.modIcons.add(icon);
            }
        });
    }

    @Override
    public void onClose() {
        if (this.entries.saveValuesToConfig()) {
            this.configHandlers.forEach(TConfigHandler::saveToFile);
            Minecraft.getInstance().reloadResourcePacks();
        }
        super.onClose();
    }

    @Override
    protected void init() {
        this.initConfigs();
        super.init();
        TConfigEntryListWidget child = new TConfigEntryListWidget((int)((double)this.width * 0.3), (int)((double)this.height * 0.7), (int)((double)this.height * 0.15), (int)((double)this.width * 0.6), 24, this.entries.getOptions().values().toArray(new TConfigEntry[0]));
        child.setWidgetBackgroundToFullWidth();
        this.addRenderableWidget((GuiEventListener)child);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (!this.modIcons.isEmpty()) {
            int ix = this.width - this.modIcons.size() * 34;
            for (ResourceLocation modIcon : this.modIcons) {
                context.blit(modIcon, ix, 2, 0.0f, 0.0f, 32, 32, 32, 32);
                ix += 34;
            }
        }
    }
}

