/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AnvilMenu
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.anvil;

import java.util.Objects;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class AnvilHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private static AnvilMenu ANVIL_MENU = null;

    public static int findLevelsCost(ItemStack leftStack, ItemStack rightStack) {
        AnvilMenu anvilMenu = AnvilHelper.getFakeAnvilMenu();
        AnvilMenu result = AnvilHelper.setAnvilMenu(anvilMenu, leftStack, rightStack);
        if (result == null) {
            return -1;
        }
        return result.getCost();
    }

    public static AnvilMenu getFakeAnvilMenu() {
        if (ANVIL_MENU == null) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = (Player)Objects.requireNonNull(minecraft.player);
            Inventory fakeInventory = new Inventory(player);
            ANVIL_MENU = new AnvilMenu(0, fakeInventory);
        }
        return ANVIL_MENU;
    }

    @Nullable
    public static AnvilMenu setAnvilMenu(AnvilMenu anvilMenu, ItemStack leftStack, ItemStack rightStack) {
        try {
            Slot leftSlot = (Slot)anvilMenu.slots.get(0);
            Slot rightSlot = (Slot)anvilMenu.slots.get(1);
            if (leftSlot.getItem() != leftStack) {
                leftSlot.set(leftStack);
            }
            if (rightSlot.getItem() != rightStack) {
                rightSlot.set(rightStack);
            }
            return anvilMenu;
        }
        catch (RuntimeException e) {
            String left = ErrorUtil.getItemStackInfo(leftStack);
            String right = ErrorUtil.getItemStackInfo(rightStack);
            LOGGER.error("Could not set anvil recipe for: ({} and {}).", (Object)left, (Object)right, (Object)e);
            return null;
        }
    }
}

