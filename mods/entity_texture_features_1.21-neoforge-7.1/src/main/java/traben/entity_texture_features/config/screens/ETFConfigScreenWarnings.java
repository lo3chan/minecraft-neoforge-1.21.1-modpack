/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package traben.entity_texture_features.config.screens;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.tconfig.TConfig;
import traben.tconfig.gui.TConfigScreen;

public class ETFConfigScreenWarnings
extends TConfigScreen {
    final Set<ETFConfigWarning> warningsFound;

    public ETFConfigScreenWarnings(Screen parent, Set<ETFConfigWarning> warningsFound) {
        super("config.entity_texture_features.warnings.title", parent, true);
        this.warningsFound = warningsFound;
    }

    public static Set<String> getIgnoredWarnings() {
        return ETF.warningConfigHandler.getConfig().ignoredConfigIds;
    }

    @Override
    public void onClose() {
        ETF.warningConfigHandler.saveToFile();
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)ETF.getTextFromTranslation("config.entity_texture_features.ignore_all"), button -> {
            for (ETFConfigWarning warn : ETFConfigWarnings.getRegisteredWarnings()) {
                ETFConfigScreenWarnings.getIgnoredWarnings().add(warn.getID());
            }
            this.rebuildWidgets();
        }).bounds((int)((double)this.width * 0.25), (int)((double)this.height * 0.9), (int)((double)this.width * 0.2), 20).build());
        double offset = 0.0;
        for (ETFConfigWarning warning : this.warningsFound) {
            if (warning.doesShowDisableButton()) {
                Button butt = Button.builder((Component)Component.nullToEmpty((String)(ETF.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() + (ETFConfigScreenWarnings.getIgnoredWarnings().contains(warning.getID()) ? CommonComponents.GUI_YES : CommonComponents.GUI_NO).getString())), button -> {
                    if (ETFConfigScreenWarnings.getIgnoredWarnings().contains(warning.getID())) {
                        ETFConfigScreenWarnings.getIgnoredWarnings().remove(warning.getID());
                    } else {
                        ETFConfigScreenWarnings.getIgnoredWarnings().add(warning.getID());
                    }
                    button.setMessage(Component.nullToEmpty((String)(ETF.getTextFromTranslation("config.entity_texture_features.warn.ignore").getString() + (ETFConfigScreenWarnings.getIgnoredWarnings().contains(warning.getID()) ? CommonComponents.GUI_YES : CommonComponents.GUI_NO).getString())));
                }).bounds((int)((double)this.width * 0.75), (int)((double)this.height * (0.25 + offset)), (int)((double)this.width * 0.17), 20).tooltip(Tooltip.create((Component)ETF.getTextFromTranslation("config.entity_texture_features.ignore_description"))).build();
                this.addRenderableWidget((GuiEventListener)butt);
            }
            offset += 0.1;
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, ETF.getTextFromTranslation("config.entity_texture_features.warn_instruction"), (int)((double)this.width * 0.5), (int)((double)this.height * 0.18), 0xFFFFFF);
        double offset = 0.0;
        for (ETFConfigWarning warning : this.warningsFound) {
            context.drawString(this.font, ETF.getTextFromTranslation(warning.getTitle()), (int)((double)this.width * 0.05), (int)((double)this.height * (0.25 + offset)), 0xFFFFFF);
            context.drawString(this.font, ETF.getTextFromTranslation(warning.getSubTitle()), (int)((double)this.width * 0.05), (int)((double)this.height * (0.29 + offset)), 0x888888);
            offset += 0.1;
        }
    }

    public static class WarningConfig
    extends TConfig.NoGUI {
        public Set<String> ignoredConfigIds = new HashSet<String>();
    }
}

