/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.GrindstoneMenu
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.grindstone;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import org.jetbrains.annotations.Nullable;

final class GrindstoneHelper {
    @Nullable
    private static GrindstoneMenu GRINDSTONE_MENU;

    private GrindstoneHelper() {
    }

    @Nullable
    public static GrindstoneMenu getFakeGrindstoneMenu() {
        if (GRINDSTONE_MENU == null) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return null;
            }
            Inventory fakeInventory = new Inventory((Player)player);
            GRINDSTONE_MENU = new GrindstoneMenu(0, fakeInventory);
        }
        return GRINDSTONE_MENU;
    }
}

