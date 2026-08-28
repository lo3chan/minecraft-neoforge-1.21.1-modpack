/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui;

import com.google.common.base.Preconditions;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jetbrains.annotations.Nullable;

public record GuiProperties(Class<? extends Screen> screenClass, int guiLeft, int guiTop, int guiXSize, int guiYSize, int screenWidth, int screenHeight) implements IGuiProperties
{
    public GuiProperties {
        Preconditions.checkArgument((guiXSize > 0 ? 1 : 0) != 0, (Object)"guiXSize must be > 0");
        Preconditions.checkArgument((guiYSize > 0 ? 1 : 0) != 0, (Object)"guiYSize must be > 0");
        Preconditions.checkArgument((screenWidth > 0 ? 1 : 0) != 0, (Object)"screenWidth must be > 0");
        Preconditions.checkArgument((screenHeight > 0 ? 1 : 0) != 0, (Object)"screenHeight must be > 0");
    }

    @Nullable
    public static GuiProperties create(AbstractContainerScreen<?> containerScreen) {
        if (containerScreen.width <= 0 || containerScreen.height <= 0) {
            return null;
        }
        IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
        int x = screenHelper.getGuiLeft(containerScreen);
        int y = screenHelper.getGuiTop(containerScreen);
        int width = screenHelper.getXSize(containerScreen);
        int height = screenHelper.getYSize(containerScreen);
        if (x < 0) {
            width -= x;
            x = 0;
        }
        if (y < 0) {
            height -= y;
            y = 0;
        }
        if (width <= 0 || height <= 0) {
            return null;
        }
        return new GuiProperties(containerScreen.getClass(), x, y, width, height, containerScreen.width, containerScreen.height);
    }

    public static boolean areEqual(@Nullable IGuiProperties a, @Nullable IGuiProperties b) {
        if (a == b) {
            return true;
        }
        return a != null && b != null && a.screenClass().equals(b.screenClass()) && a.guiLeft() == b.guiLeft() && a.guiXSize() == b.guiXSize() && a.screenWidth() == b.screenWidth() && a.screenHeight() == b.screenHeight();
    }
}

