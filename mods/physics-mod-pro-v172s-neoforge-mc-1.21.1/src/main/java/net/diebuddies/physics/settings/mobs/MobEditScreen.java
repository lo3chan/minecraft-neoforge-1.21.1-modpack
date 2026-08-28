/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.EntityType
 */
package net.diebuddies.physics.settings.mobs;

import java.util.List;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

public class MobEditScreen
extends LegacyOptionsSubScreen {
    private LegacyOptionsList list;
    private String mob;
    private MobSetting setting;

    public MobEditScreen(Screen parent, Options options, String mob) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.mobs.edit.title", (Object[])new Object[]{mob}));
        this.mob = mob;
        this.setting = ConfigMobs.getMobSetting((EntityType)EntityType.byString((String)mob).get()).copy();
    }

    protected void init() {
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 105, this.height - 27, 100, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.lastScreen)));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> {
            this.minecraft.setScreen(this.lastScreen);
            ConfigMobs.customizedMobs.put(this.mob, this.setting);
            ConfigMobs.save();
        }));
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.children.add(this.list);
        List<LegacyOption> options = AdjustableUtil.generateOptions(this, this.setting);
        this.list.addSmall(options.toArray(new LegacyOption[options.size()]));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }
}

