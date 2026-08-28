/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.cloth;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.ClothDisplayScreen;
import net.diebuddies.physics.settings.cloth.ClothEntitySelectionList;
import net.diebuddies.physics.settings.gui.TitleWidget;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class ClothEntitySelectionScreen
extends LegacyOptionsSubScreen {
    private ClothEntitySelectionList list;
    private Button select;

    public ClothEntitySelectionScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.mobs.customize.title"));
    }

    protected void init() {
        this.list = new ClothEntitySelectionList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.addRenderableWidget((GuiEventListener)this.list);
        EditBox search = new EditBox(Minecraft.getInstance().font, this.width / 2 - 160, this.height - 27, 100, 20, (Component)Component.literal((String)""));
        search.setValue("");
        search.setSuggestion(Language.getInstance().getOrDefault("physicsmod.gui.customplayer"));
        search.setResponder(changedText -> this.checkSearchText((String)changedText, search));
        this.addRenderableWidget((GuiEventListener)search);
        this.select = ButtonSettings.builder(this.width / 2 - 50, this.height - 27, 100, 20, (Component)Component.translatable((String)"physicsmod.gui.select"), button -> {
            ClothDisplayScreen clothDisplay = (ClothDisplayScreen)this.lastScreen;
            if (search.getValue().isEmpty()) {
                if (this.list.getSelected() != null) {
                    String selected = (String)((BaseEntry)this.list.getSelected()).getUserData();
                    clothDisplay.setSelectedEntity(selected);
                }
            } else {
                clothDisplay.setSelectedEntity("physicsmod:player:" + search.getValue());
            }
            this.minecraft.setScreen(this.lastScreen);
        });
        this.addRenderableWidget((GuiEventListener)this.select);
        this.addRenderableWidget((GuiEventListener)new TitleWidget(this));
    }

    private void checkSearchText(String searchText, EditBox search) {
        if (searchText.isEmpty()) {
            search.setSuggestion(Language.getInstance().getOrDefault("physicsmod.gui.customplayer"));
            this.select.setMessage((Component)Component.translatable((String)"physicsmod.gui.select"));
        } else {
            search.setSuggestion("");
            this.select.setMessage((Component)Component.translatable((String)"physicsmod.gui.selectx", (Object[])new Object[]{searchText}));
        }
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }
}

