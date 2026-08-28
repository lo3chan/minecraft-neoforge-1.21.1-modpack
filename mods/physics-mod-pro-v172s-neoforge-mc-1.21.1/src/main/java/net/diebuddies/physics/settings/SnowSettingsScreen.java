/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package net.diebuddies.physics.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class SnowSettingsScreen
extends LegacyOptionsSubScreen {
    private boolean thicknessChanged = false;
    private final CycleOption<Boolean> PHYSICS_SNOW = CycleOption.createOnOff("physicsmod.menu.snow.snowphysics", gameOptions -> ConfigClient.snowPhysics, (gameOptions, option, value) -> {
        ConfigClient.snowPhysics = value;
        ConfigClient.save();
        Minecraft.getInstance().levelRenderer.allChanged();
    });
    private final ProgressOption PHYSICS_SNOW_THICKNESS = new ProgressOption("physicsmod.menu.snow.snowthickness", 0.0, 0.5, 0.01f, gameOptions -> ConfigClient.snowThickness, (gameOptions, value) -> {
        ConfigClient.snowThickness = value.floatValue();
        ConfigClient.save();
        this.thicknessChanged = true;
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.snow.snowthickness", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.snow.snowthickness.info"));
    private final CycleOption<Boolean> PHYSICS_SNOW_TRACKS = CycleOption.createOnOff("physicsmod.menu.snow.snowtracks", gameOptions -> ConfigClient.snowTracks, (gameOptions, option, value) -> {
        ConfigClient.snowTracks = value;
        ConfigClient.save();
    });
    private final ProgressOption PHYSICS_SNOW_TRACK_DISTANCE = new ProgressOption("physicsmod.menu.snow.snowtrackdistance", 4.0, 240.0, 0.1f, gameOptions -> ConfigClient.snowTrackDistance, (gameOptions, value) -> {
        ConfigClient.snowTrackDistance = value;
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.snow.snowtrackdistance", String.format("%.0f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.snow.snowtrackdistance.info"));
    private final ProgressOption PHYSICS_SNOW_TRACK_ENTITIES = new ProgressOption("physicsmod.menu.snow.snowtrackentities", 1.0, 60.0, 1.0f, gameOptions -> ConfigClient.snowTrackEntities, (gameOptions, value) -> {
        ConfigClient.snowTrackEntities = value.intValue();
        ConfigClient.save();
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.snow.snowtrackentities", String.format("%.0f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.snow.snowtrackentities.info"));
    private final CycleOption<Boolean> PHYSICS_GRASSY_SNOW = CycleOption.createOnOff("physicsmod.menu.snow.grasssnowy", gameOptions -> ConfigClient.grassSnowy, (gameOptions, option, value) -> {
        ConfigClient.grassSnowy = value;
        ConfigClient.save();
        Minecraft.getInstance().levelRenderer.allChanged();
    }).setTooltip(minecraft -> graphicsStatus -> Component.translatable((String)"physicsmod.menu.snow.grasssnowy.info"));
    private final CycleOption<Boolean> PHYSICS_SNOW_SMOOTH_SHADING = CycleOption.createOnOff("physicsmod.menu.snow.snowsmoothshading", gameOptions -> ConfigClient.snowSmoothShading, (gameOptions, option, value) -> {
        ConfigClient.snowSmoothShading = value;
        ConfigClient.save();
        Minecraft.getInstance().levelRenderer.allChanged();
    }).setTooltip(minecraft -> graphicsStatus -> Component.translatable((String)"physicsmod.menu.snow.snowsmoothshading.info"));
    private final CycleOption<SnowType> PHYSICS_SNOW_TYPE = CycleOption.create("physicsmod.menu.snow.snowtype", (Object[])SnowType.values(), model -> Component.translatable((String)((SnowType)((Object)((Object)model))).toString()), gameOptions -> {
        int val = ConfigClient.snowType;
        if (val >= SnowType.values().length) {
            return SnowType.values()[0];
        }
        return SnowType.values()[val];
    }, (gameOptions, option, model) -> {
        SnowType type = (SnowType)((Object)((Object)model));
        ConfigClient.snowType = type.ordinal();
        ConfigClient.save();
        Minecraft.getInstance().levelRenderer.allChanged();
    });
    private final CycleOption<SnowQuality> PHYSICS_SNOW_QUALITY = CycleOption.create("physicsmod.menu.snow.snowquality", (Object[])SnowQuality.values(), model -> Component.translatable((String)((SnowQuality)((Object)((Object)model))).toString()), gameOptions -> {
        int val = ConfigClient.snowQuality;
        if (val >= SnowQuality.values().length) {
            return SnowQuality.values()[0];
        }
        return SnowQuality.values()[val];
    }, (gameOptions, option, model) -> {
        SnowQuality type = (SnowQuality)((Object)((Object)model));
        ConfigClient.snowQuality = type.ordinal();
        ConfigClient.save();
        Minecraft.getInstance().levelRenderer.allChanged();
    });
    private final ProgressOption PHYSICS_SNOW_LOD = new ProgressOption("physicsmod.menu.snow.levelofdetail", 0.5, 10.0, 0.01f, gameOptions -> ConfigClient.snowLOD, (gameOptions, value) -> {
        ConfigClient.snowLOD = value.floatValue();
        ConfigClient.save();
        this.thicknessChanged = true;
    }, (gameOptions, option) -> option.customFormat("physicsmod.menu.snow.levelofdetail", String.format("%.2f", option.get((Options)gameOptions))), minecraft -> Component.translatable((String)"physicsmod.menu.snow.levelofdetail.info"));
    private static final int MAX_INFO_WIDTH = 300;
    private LegacyOptionsList list;
    private List<FormattedCharSequence> info;

    public SnowSettingsScreen(Screen parent, Options options) {
        super(parent, options, (Component)Component.translatable((String)"physicsmod.menu.snow.title.pro"));
        this.info = Minecraft.getInstance().font.split((FormattedText)Component.translatable((String)"physicsmod.menu.snow.warning"), 300);
        this.PHYSICS_SNOW_QUALITY.setTooltip(minecraft -> graphicsStatus -> Component.translatable((String)"physicsmod.menu.snow.snowquality.info"));
    }

    protected void init() {
        this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSmall(this.PHYSICS_SNOW, this.PHYSICS_SNOW_TRACKS);
        this.list.addBig(this.PHYSICS_SNOW_THICKNESS);
        this.list.addBig(this.PHYSICS_SNOW_LOD);
        this.list.addSmall(this.PHYSICS_SNOW_TRACK_ENTITIES, this.PHYSICS_SNOW_TRACK_DISTANCE);
        this.list.addSmall(this.PHYSICS_SNOW_TYPE, this.PHYSICS_SNOW_SMOOTH_SHADING);
        this.list.addSmall(this.PHYSICS_SNOW_QUALITY, this.PHYSICS_GRASSY_SNOW);
        this.children.add(this.list);
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> {
            this.onClose();
            this.minecraft.setScreen(this.lastScreen);
        }));
        this.addRenderableWidget((GuiEventListener)ButtonSettings.builder(this.width / 2 - 80, this.height - 27, 75, 20, (Component)Component.translatable((String)"physicsmod.gui.reset"), button -> PopupWidget.create(Language.getInstance().getOrDefault("physicsmod.menu.snow.reset"), this, widget -> this.addRenderableWidget((GuiEventListener)widget), widget -> this.removeWidget((GuiEventListener)widget), response -> {
            if (response == PopupWidget.PopupResponse.YES) {
                ConfigClient.resetSnowSettings();
                this.list.children().clear();
                this.minecraft.setScreen((Screen)new SnowSettingsScreen(this.lastScreen, this.options));
                Minecraft.getInstance().levelRenderer.allChanged();
            } else {
                this.list.children().clear();
                this.minecraft.setScreen((Screen)new SnowSettingsScreen(this.lastScreen, this.options));
            }
        })));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.list.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        PoseStack matrices = guiGraphics.pose();
        matrices.pushPose();
        matrices.translate(0.0f, 0.0f, -75.0f);
        int lineY = 0;
        for (FormattedCharSequence sequence : this.info) {
            guiGraphics.drawString(this.font, sequence, (this.width - 300) / 2, 187 + lineY, 0xFFFF55);
            lineY += 10;
        }
        matrices.popPose();
        super.render(guiGraphics, mouseX, mouseY, delta);
        BaseRenderer.renderSettingsTooltip(this.list, guiGraphics, mouseX, mouseY, this.width, this.height);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.thicknessChanged) {
            Minecraft.getInstance().levelRenderer.allChanged();
        }
    }

    public static enum SnowType {
        Round("physicsmod.enum.snowtype.round"),
        Cube("physicsmod.enum.snowtype.cube");

        private String translationId;

        private SnowType(String translationId) {
            this.translationId = translationId;
        }

        public String toString() {
            return this.translationId;
        }
    }

    public static enum SnowQuality {
        Medium("physicsmod.enum.snowquality.medium"),
        High("physicsmod.enum.snowquality.high");

        private String translationId;

        private SnowQuality(String translationId) {
            this.translationId = translationId;
        }

        public String toString() {
            return this.translationId;
        }
    }
}

