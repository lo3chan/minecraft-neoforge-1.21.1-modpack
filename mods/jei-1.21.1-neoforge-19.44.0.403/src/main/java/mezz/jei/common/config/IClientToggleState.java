/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

public interface IClientToggleState {
    public boolean isOverlayEnabled();

    public void toggleOverlayEnabled();

    public boolean isEditModeEnabled();

    public void toggleEditModeEnabled();

    public boolean isCheatItemsEnabled();

    public void toggleCheatItemsEnabled();

    public void setCheatItemsEnabled(boolean var1);

    public boolean isBookmarkOverlayEnabled();

    public void toggleBookmarkEnabled();

    public void setBookmarkEnabled(boolean var1);

    public void addEditModeToggleListener(IEditModeListener var1);

    public void clearListeners();

    public static interface IEditModeListener {
        public void onEditModeChanged();
    }
}

