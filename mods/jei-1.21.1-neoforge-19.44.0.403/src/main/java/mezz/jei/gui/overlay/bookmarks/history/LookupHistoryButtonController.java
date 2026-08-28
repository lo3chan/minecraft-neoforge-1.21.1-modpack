/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package mezz.jei.gui.overlay.bookmarks.history;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.gui.textures.Textures;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class LookupHistoryButtonController
implements IIconButtonController {
    private final IDrawable offIcon;
    private final IDrawable onIcon;
    private final IClientConfig clientConfig;

    public LookupHistoryButtonController(IClientConfig clientConfig) {
        Textures textures = Internal.getTextures();
        this.offIcon = textures.getHistoryButtonDisabledIcon();
        this.onIcon = textures.getHistoryButtonEnabledIcon();
        this.clientConfig = clientConfig;
    }

    @Override
    public void getTooltips(ITooltipBuilder tooltip) {
        if (this.clientConfig.lookupHistoryEnabled().getValue().booleanValue()) {
            tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.lookupHistory.disable"));
        } else {
            tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.lookupHistory.enable"));
        }
        tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.lookupHistory.usage").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void updateState(IButtonState state) {
        state.setForcePressed(this.clientConfig.lookupHistoryEnabled().getValue());
        if (this.clientConfig.lookupHistoryEnabled().getValue().booleanValue()) {
            state.setIcon(this.onIcon);
        } else {
            state.setIcon(this.offIcon);
        }
    }

    @Override
    public boolean onPress(IJeiUserInput input) {
        if (!input.isSimulate()) {
            this.clientConfig.lookupHistoryEnabled().set(this.clientConfig.lookupHistoryEnabled().getValue() == false);
        }
        return true;
    }
}

