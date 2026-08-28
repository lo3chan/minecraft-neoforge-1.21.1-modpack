/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ProjectileSettingsScreen
extends LegacyOptionsSubScreen {
    private static final CycleOption<Boolean> PHYSICS_SNOWBALL_SHADE = CycleOption.createOnOff("physicsmod.menu.items.snowballshade", gameOptions -> ConfigClient.snowballShade, (gameOptions, option, value) -> {
        ConfigClient.snowballShade = value;
        ConfigClient.save();
    });
    private static final CycleOption<Model> PHYSICS_SNOWBALL_MODEL = CycleOption.create("physicsmod.menu.items.snowballmodel", (Object[])Model.values(), model -> Component.translatable((String)((Model)((Object)((Object)model))).toString()), gameOptions -> {
        int val = ConfigClient.snowballModel;
        if (val >= Model.values().length) {
            return Model.values()[0];
        }
        return Model.values()[val];
    }, (gameOptions, option, model) -> {
        Model type = (Model)((Object)((Object)model));
        ConfigClient.snowballModel = type.ordinal();
        ConfigClient.save();
    });
    private static final CycleOption<Impact> PHYSICS_SNOWBALL_IMPACT = CycleOption.create("physicsmod.menu.items.snowballimpact", (Object[])Impact.values(), model -> Component.translatable((String)((Impact)((Object)((Object)model))).toString()), gameOptions -> {
        int val = ConfigClient.snowballImpact;
        if (val >= Impact.values().length) {
            return Impact.values()[0];
        }
        return Impact.values()[val];
    }, (gameOptions, option, model) -> {
        Impact type = (Impact)((Object)((Object)model));
        ConfigClient.snowballImpact = type.ordinal();
        ConfigClient.save();
    });
    private static final CycleOption<Boolean> PHYSICS_ENDERPEARL_SHADE = CycleOption.createOnOff("physicsmod.menu.items.enderpearlshade", gameOptions -> ConfigClient.enderpearlShade, (gameOptions, option, value) -> {
        ConfigClient.enderpearlShade = value;
        ConfigClient.save();
    });
    private static final CycleOption<Model> PHYSICS_ENDERPEARL_MODEL = CycleOption.create("physicsmod.menu.items.enderpearlmodel", (Object[])Model.values(), model -> Component.translatable((String)((Model)((Object)((Object)model))).toString()), gameOptions -> {
        int val = ConfigClient.enderpearlModel;
        if (val >= Model.values().length) {
            return Model.values()[0];
        }
        return Model.values()[val];
    }, (gameOptions, option, model) -> {
        Model type = (Model)((Object)((Object)model));
        ConfigClient.enderpearlModel = type.ordinal();
        ConfigClient.save();
    });
    private static final CycleOption<Impact> PHYSICS_ENDERPEARL_IMPACT = CycleOption.create("physicsmod.menu.items.enderpearlimpact", (Object[])Impact.values(), model -> Component.translatable((String)((Impact)((Object)((Object)model))).toString()), gameOptions -> {
        int val = ConfigClient.enderpearlImpact;
        if (val >= Impact.values().length) {
            return Impact.values()[0];
        }
        return Impact.values()[val];
    }, (gameOptions, option, model) -> {
        Impact type = (Impact)((Object)((Object)model));
        ConfigClient.enderpearlImpact = type.ordinal();
        ConfigClient.save();
    });
    private static final CycleOption<Boolean> PHYSICS_EGG_SHADE = CycleOption.createOnOff("physicsmod.menu.items.eggshade", gameOptions -> ConfigClient.eggShade, (gameOptions, option, value) -> {
        ConfigClient.eggShade = value;
        ConfigClient.save();
    });
    private static final CycleOption<Model> PHYSICS_EGG_MODEL = CycleOption.create("physicsmod.menu.items.eggmodel", (Object[])Model.values(), model -> Component.translatable((String)((Model)((Object)((Object)model))).toString()), gameOptions -> {
        int val = ConfigClient.eggModel;
        if (val >= Model.values().length) {
            return Model.values()[0];
        }
        return Model.values()[val];
    }, (gameOptions, option, model) -> {
        Model type = (Model)((Object)((Object)model));
        ConfigClient.eggModel = type.ordinal();
        ConfigClient.save();
    });
    private static final CycleOption<Impact> PHYSICS_EGG_IMPACT = CycleOption.create("physicsmod.menu.items.eggimpact", (Object[])Impact.values(), model -> Component.translatable((String)((Impact)((Object)((Object)model))).toString()), gameOptions -> {
        int val = ConfigClient.eggImpact;
        if (val >= Impact.values().length) {
            return Impact.values()[0];
        }
        return Impact.values()[val];
    }, (gameOptions, option, model) -> {
        Impact type = (Impact)((Object)((Object)model));
        ConfigClient.eggImpact = type.ordinal();
        ConfigClient.save();
    });
    private static final ProgressOption PHYSICS_LIFETIME_ITEMS = new ProgressOption("physicsmod.menu.items.particlelifetimeitems", 0.0, 100.0, 0.1f, gameOptions -> ConfigClient.particleLifetimeItems, (gameOptions, value) -> {
        ConfigClient.particleLifetimeItems = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.items.particlelifetimeitems", String.format("%.2f", option.get((Options)gameOptions))));
    private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_ITEMS = new ProgressOption("physicsmod.menu.items.particlelifetimevarianceitems", 0.0, 30.0, 0.1f, gameOptions -> ConfigClient.particleLifetimeVarianceItems, (gameOptions, value) -> {
        ConfigClient.particleLifetimeVarianceItems = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.items.particlelifetimevarianceitems", String.format("%.2f", option.get((Options)gameOptions))));
    private LegacyOptionsList list;

    public ProjectileSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.items.title.pro"));
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.children.add(this.list);
        this.list.addSmall(PHYSICS_LIFETIME_ITEMS, PHYSICS_LIFETIME_VARIANCE_ITEMS);
        this.list.addSmall(PHYSICS_SNOWBALL_MODEL, PHYSICS_SNOWBALL_IMPACT);
        this.list.addBig(PHYSICS_SNOWBALL_SHADE);
        this.list.addSmall(PHYSICS_ENDERPEARL_MODEL, PHYSICS_ENDERPEARL_IMPACT);
        this.list.addBig(PHYSICS_ENDERPEARL_SHADE);
        this.list.addSmall(PHYSICS_EGG_MODEL, PHYSICS_EGG_IMPACT);
        this.list.addBig(PHYSICS_EGG_SHADE);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 50, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    private /* synthetic */ void lambda$init$31(Button button) {
        this.minecraft.setScreen(this.lastScreen);
    }

    private static /* synthetic */ void lambda$init$30(Button button) {
        Util.getPlatform().openUri("https://minecraftphysicsmod.com/pro");
    }

    public static enum Impact {
        Shatter("physicsmod.enum.impact.shatter"),
        Bounce("physicsmod.enum.impact.bounce"),
        Disappear("physicsmod.enum.impact.disappear");

        private String translationId;

        private Impact(String translationId) {
            this.translationId = translationId;
        }

        public String toString() {
            return this.translationId;
        }
    }

    public static enum Model {
        Voxel("physicsmod.enum.model.voxel"),
        Round("physicsmod.enum.model.round"),
        Classic("physicsmod.enum.model.classic");

        private String translationId;

        private Model(String translationId) {
            this.translationId = translationId;
        }

        public String toString() {
            return this.translationId;
        }
    }
}

