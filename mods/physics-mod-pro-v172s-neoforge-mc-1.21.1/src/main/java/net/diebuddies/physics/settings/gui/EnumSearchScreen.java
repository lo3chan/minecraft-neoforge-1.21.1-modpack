/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.EnumOption;
import net.diebuddies.physics.settings.gui.EnumSelectionList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class EnumSearchScreen
extends LegacyOptionsSubScreen {
    private static String searchText = "";
    private EnumSelectionList list;
    private EnumOption option;

    public EnumSearchScreen(Screen parent, EnumOption option, String translatableTitle) {
        super(parent, null, (Component)Component.translatable((String)translatableTitle));
        this.option = option;
    }

    protected void init() {
        this.list = new EnumSelectionList(this.minecraft, this.width, this.height, 32, this.height - 32, 25, this.option.selectedEnum);
        this.addRenderableWidget((GuiEventListener)this.list);
        int offset = 45;
        EditBox search = new EditBox(Minecraft.getInstance().font, this.width / 2 - 175 + offset, this.height - 27, 80, 20, (Component)Component.literal((String)""));
        search.setValue(searchText);
        this.checkSearchText(searchText, search);
        search.setResponder(changedText -> this.checkSearchText((String)changedText, search));
        this.addRenderableWidget((GuiEventListener)search);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 85 + offset, this.height - 27, 80, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.lastScreen)));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 5 + offset, this.height - 27, 80, 20, (Component)Component.translatable((String)"physicsmod.gui.select"), button -> {
            if (this.list.getSelected() != null) {
                this.option.setEnum(((BaseEntry)this.list.getSelected()).getUserData());
                this.minecraft.setScreen(this.lastScreen);
            }
        }));
    }

    private void checkSearchText(String searchText, EditBox search) {
        EnumSearchScreen.searchText = searchText;
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}

