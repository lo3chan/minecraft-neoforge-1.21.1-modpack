/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 */
package mezz.jei.gui.overlay.bookmarks;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.runtime.IJeiKeyMapping;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public class BookmarkButtonController
implements IIconButtonController {
    private final IDrawable offIcon;
    private final IDrawable onIcon;
    private final BookmarkOverlay bookmarkOverlay;
    private final BookmarkList bookmarkList;
    private final IClientToggleState toggleState;
    private final IInternalKeyMappings keyBindings;

    public BookmarkButtonController(BookmarkOverlay bookmarkOverlay, BookmarkList bookmarkList, IClientToggleState toggleState, IInternalKeyMappings keyBindings) {
        Textures textures = Internal.getTextures();
        this.offIcon = textures.getBookmarkButtonDisabledIcon();
        this.onIcon = textures.getBookmarkButtonEnabledIcon();
        this.bookmarkOverlay = bookmarkOverlay;
        this.bookmarkList = bookmarkList;
        this.toggleState = toggleState;
        this.keyBindings = keyBindings;
    }

    @Override
    public void getTooltips(ITooltipBuilder tooltip) {
        if (this.toggleState.isBookmarkOverlayEnabled()) {
            tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.bookmarks.disable"));
        } else {
            tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.bookmarks.enable"));
        }
        IJeiKeyMapping bookmarkKey = this.keyBindings.getBookmark();
        if (bookmarkKey.isUnbound()) {
            MutableComponent noKey = Component.translatable((String)"jei.tooltip.bookmarks.usage.nokey");
            tooltip.add((FormattedText)noKey.withStyle(ChatFormatting.RED));
        } else if (!this.bookmarkOverlay.hasRoom()) {
            MutableComponent notEnoughSpace = Component.translatable((String)"jei.tooltip.bookmarks.not.enough.space");
            tooltip.add((FormattedText)notEnoughSpace.withStyle(ChatFormatting.GOLD));
        } else {
            tooltip.addKeyUsageComponent("jei.tooltip.bookmarks.usage.key", bookmarkKey);
        }
    }

    @Override
    public void updateState(IButtonState state) {
        if (this.toggleState.isBookmarkOverlayEnabled()) {
            state.setIcon(this.onIcon);
            state.setForcePressed(true);
        } else {
            state.setIcon(this.offIcon);
            state.setForcePressed(false);
        }
    }

    @Override
    public boolean onPress(IJeiUserInput input) {
        if (!this.bookmarkList.isEmpty() && this.bookmarkOverlay.hasRoom()) {
            if (!input.isSimulate()) {
                this.toggleState.toggleBookmarkEnabled();
            }
            return true;
        }
        return false;
    }
}

