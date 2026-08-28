/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.mc.ComponentProvider
 *  dev.tr7zw.transition.mc.GeneralUtil
 *  dev.tr7zw.trender.gui.GuiDescription
 *  dev.tr7zw.trender.gui.client.AbstractConfigScreen
 *  dev.tr7zw.trender.gui.client.AbstractConfigScreen$OptionInstance
 *  dev.tr7zw.trender.gui.client.BackgroundPainter
 *  dev.tr7zw.trender.gui.widget.WButton
 *  dev.tr7zw.trender.gui.widget.WGridPanel
 *  dev.tr7zw.trender.gui.widget.WListPanel
 *  dev.tr7zw.trender.gui.widget.WPanel
 *  dev.tr7zw.trender.gui.widget.WPlayerPreview
 *  dev.tr7zw.trender.gui.widget.WWidget
 *  dev.tr7zw.trender.gui.widget.data.Insets
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package dev.tr7zw.waveycapes;

import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.trender.gui.GuiDescription;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WListPanel;
import dev.tr7zw.trender.gui.widget.WPanel;
import dev.tr7zw.trender.gui.widget.WPlayerPreview;
import dev.tr7zw.trender.gui.widget.WWidget;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.config.Config;
import java.util.ArrayList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class WaveyCapesConfigScreen {
    public static Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent).createScreen();
    }

    private static class CustomConfigScreen
    extends AbstractConfigScreen {
        public CustomConfigScreen(Screen previous) {
            super((Component)ComponentProvider.translatable((String)"text.wc.title"), previous);
            WGridPanel root = new WGridPanel(8);
            root.setInsets(Insets.ROOT_PANEL);
            this.setRootPanel((WPanel)root);
            ArrayList<AbstractConfigScreen.OptionInstance> options = new ArrayList<AbstractConfigScreen.OptionInstance>();
            options.add(this.getEnumOption("text.wc.setting.capestyle", CapeStyle.class, () -> ModBase.config.capeStyle, v -> {
                ModBase.config.capeStyle = v;
            }));
            options.add(this.getEnumOption("text.wc.setting.windmode", WindMode.class, () -> ModBase.config.windMode, v -> {
                ModBase.config.windMode = v;
            }));
            options.add(this.getEnumOption("text.wc.setting.capemovement", CapeMovement.class, () -> ModBase.config.capeMovement, v -> {
                ModBase.config.capeMovement = v;
            }));
            options.add(this.getIntOption("text.wc.setting.gravity", 5, 32, () -> ModBase.config.gravity, v -> {
                ModBase.config.gravity = v;
            }));
            options.add(this.getIntOption("text.wc.setting.heightMultiplier", 4, 16, () -> ModBase.config.heightMultiplier, v -> {
                ModBase.config.heightMultiplier = v;
            }));
            WListPanel optionList = this.createOptionList(options);
            optionList.setGap(-1);
            optionList.setSize(280, 180);
            root.add((WWidget)optionList, 0, 1, 29, 25);
            WButton doneButton = new WButton(CommonComponents.GUI_DONE);
            doneButton.setOnClick(() -> {
                this.save();
                GeneralUtil.setScreen((Screen)previous);
            });
            root.add((WWidget)doneButton, 0, 26, 6, 2);
            WPlayerPreview playerPreview = new WPlayerPreview();
            playerPreview.setRotationX(164);
            playerPreview.setRotationY(5);
            playerPreview.setShowBackground(true);
            root.add((WWidget)playerPreview, 10, 14);
            WButton resetButton = new WButton((Component)ComponentProvider.translatable((String)"controls.reset"));
            resetButton.setOnClick(() -> {
                this.reset();
                root.layout();
            });
            root.add((WWidget)resetButton, 23, 26, 6, 2);
            root.setBackgroundPainter(BackgroundPainter.VANILLA);
            root.validate((GuiDescription)this);
            root.setHost((GuiDescription)this);
        }

        public void reset() {
            ModBase.config = new Config();
        }

        public void save() {
            WaveyCapesBase.INSTANCE.writeConfig();
        }
    }
}

