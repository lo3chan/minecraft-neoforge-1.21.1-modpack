/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.mobs;

import net.diebuddies.config.ConfigMobs;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.TitleWidget;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.mobs.MobEditScreen;
import net.diebuddies.physics.settings.mobs.MobSelectionList;
import net.diebuddies.physics.settings.mobs.MobSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MobCustomizeScreen
extends LegacyOptionsSubScreen {
    private static String searchText = "";
    private MobSelectionList list;

    public MobCustomizeScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.mobs.customize.title"));
    }

    protected void init() {
        this.list = new MobSelectionList(this.minecraft, this.width, this.height, 32, this.height - 32, 25, block -> this.minecraft.setScreen((Screen)new MobEditScreen((Screen)this, this.options, (String)block)));
        this.addRenderableWidget((GuiEventListener)this.list);
        EditBox search = new EditBox(Minecraft.getInstance().font, this.width / 2 - 160, this.height - 27, 100, 20, (Component)Component.literal((String)""));
        search.setValue(searchText);
        this.checkSearchText(searchText, search);
        search.setResponder(changedText -> this.checkSearchText((String)changedText, search));
        this.addRenderableWidget((GuiEventListener)search);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 50, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 60, this.height - 27, 100, 20, (Component)Component.translatable((String)"physicsmod.gui.reset"), button -> PopupWidget.create(Language.getInstance().getOrDefault("physicsmod.menu.mobs.customize.reset"), this, widget -> this.addRenderableWidget((GuiEventListener)widget), widget -> this.removeWidget((GuiEventListener)widget), response -> {
            if (response == PopupWidget.PopupResponse.YES) {
                ConfigMobs.resetMobs();
                this.list.children().clear();
                this.minecraft.setScreen((Screen)new MobSettingsScreen(this.lastScreen, this.options));
            }
        })));
        this.addRenderableWidget((GuiEventListener)new TitleWidget(this));
    }

    private void checkSearchText(String searchText, EditBox search) {
        MobCustomizeScreen.searchText = searchText;
        if (searchText.isEmpty()) {
            search.setSuggestion(Language.getInstance().getOrDefault("physicsmod.gui.search"));
        } else {
            search.setSuggestion("");
        }
        this.list.filter = searchText;
        this.list.refreshEntries();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }
}

