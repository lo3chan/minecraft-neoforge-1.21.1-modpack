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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BookmarkButtonController implements IIconButtonController {
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
         tooltip.add(Component.translatable("jei.tooltip.bookmarks.disable"));
      } else {
         tooltip.add(Component.translatable("jei.tooltip.bookmarks.enable"));
      }

      IJeiKeyMapping bookmarkKey = this.keyBindings.getBookmark();
      if (bookmarkKey.isUnbound()) {
         MutableComponent noKey = Component.translatable("jei.tooltip.bookmarks.usage.nokey");
         tooltip.add(noKey.withStyle(ChatFormatting.RED));
      } else if (!this.bookmarkOverlay.hasRoom()) {
         MutableComponent notEnoughSpace = Component.translatable("jei.tooltip.bookmarks.not.enough.space");
         tooltip.add(notEnoughSpace.withStyle(ChatFormatting.GOLD));
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
      } else {
         return false;
      }
   }
}
