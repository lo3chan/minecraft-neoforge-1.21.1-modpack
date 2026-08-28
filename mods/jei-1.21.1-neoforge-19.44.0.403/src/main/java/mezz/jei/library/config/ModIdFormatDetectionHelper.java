/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.TooltipFlag$Default
 *  net.minecraft.world.level.ItemLike
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.config;

import java.util.List;
import mezz.jei.library.config.ModIdFormatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

final class ModIdFormatDetectionHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    private ModIdFormatDetectionHelper() {
    }

    public static Component detectModNameTooltipFormatting() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        List<Component> tooltip = ModIdFormatDetectionHelper.getTestTooltip((Player)player, new ItemStack((ItemLike)Items.APPLE));
        return ModIdFormatConfig.detectModNameTooltipFormatting(tooltip);
    }

    private static List<Component> getTestTooltip(@Nullable Player player, ItemStack itemStack) {
        try {
            return itemStack.getTooltipLines(Item.TooltipContext.EMPTY, player, (TooltipFlag)TooltipFlag.Default.NORMAL);
        }
        catch (LinkageError | RuntimeException e) {
            LOGGER.error("Error while Testing for mod name formatting", e);
            return List.of();
        }
    }
}

