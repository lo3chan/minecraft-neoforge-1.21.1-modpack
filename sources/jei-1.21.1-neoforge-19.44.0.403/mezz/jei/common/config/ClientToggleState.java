package mezz.jei.common.config;

import java.util.ArrayList;
import java.util.List;

public class ClientToggleState implements IClientToggleState {
   private final List<IClientToggleState.IEditModeListener> editModeListeners = new ArrayList<>();
   private boolean overlayEnabled = true;
   private boolean cheatItemsEnabled = false;
   private boolean editModeEnabled = false;
   private boolean bookmarkOverlayEnabled = true;

   @Override
   public boolean isOverlayEnabled() {
      return this.overlayEnabled;
   }

   @Override
   public void toggleOverlayEnabled() {
      this.overlayEnabled = !this.overlayEnabled;
   }

   @Override
   public boolean isBookmarkOverlayEnabled() {
      return this.isOverlayEnabled() && this.bookmarkOverlayEnabled;
   }

   @Override
   public void toggleBookmarkEnabled() {
      this.setBookmarkEnabled(!this.bookmarkOverlayEnabled);
   }

   @Override
   public void setBookmarkEnabled(boolean value) {
      if (this.bookmarkOverlayEnabled != value) {
         this.bookmarkOverlayEnabled = value;
      }
   }

   @Override
   public boolean isCheatItemsEnabled() {
      return this.cheatItemsEnabled;
   }

   @Override
   public void toggleCheatItemsEnabled() {
      this.setCheatItemsEnabled(!this.cheatItemsEnabled);
   }

   @Override
   public void setCheatItemsEnabled(boolean value) {
      this.cheatItemsEnabled = value;
   }

   @Override
   public boolean isEditModeEnabled() {
      return this.editModeEnabled;
   }

   @Override
   public void toggleEditModeEnabled() {
      this.editModeEnabled = !this.editModeEnabled;
      this.editModeListeners.forEach(IClientToggleState.IEditModeListener::onEditModeChanged);
   }

   @Override
   public void addEditModeToggleListener(IClientToggleState.IEditModeListener listener) {
      this.editModeListeners.add(listener);
   }

   @Override
   public void clearListeners() {
      this.editModeListeners.clear();
   }
}
