/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.common.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Optional;
import mezz.jei.common.config.GiveMode;
import mezz.jei.common.config.IServerConfig;
import mezz.jei.common.network.IConnectionToClient;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PacketCheatPermission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ServerCommandUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    private ServerCommandUtil() {
    }

    public static boolean hasPermissionForCheatMode(Player sender, IServerConfig serverConfig) {
        MinecraftServer minecraftServer;
        if (serverConfig.isCheatModeEnabledForCreative() && sender.isCreative()) {
            return true;
        }
        CommandSourceStack commandSource = sender.createCommandSourceStack();
        if (serverConfig.isCheatModeEnabledForOp() && (minecraftServer = sender.getServer()) != null) {
            int opPermissionLevel = minecraftServer.getOperatorUserPermissionLevel();
            return commandSource.hasPermission(opPermissionLevel);
        }
        if (serverConfig.isCheatModeEnabledForGive()) {
            return ServerCommandUtil.getGiveCommand(sender).map(giveCommand -> giveCommand.canUse((Object)commandSource)).orElse(false);
        }
        return false;
    }

    public static void executeGive(ServerPacketContext context, ItemStack itemStack, GiveMode giveMode) {
        IServerConfig serverConfig;
        ServerPlayer sender = context.player();
        if (ServerCommandUtil.hasPermissionForCheatMode((Player)sender, serverConfig = context.serverConfig())) {
            if (itemStack.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Player '{} ({})' tried to give an empty ItemStack.", (Object)sender.getName(), (Object)sender.getUUID());
                }
                return;
            }
            if (giveMode == GiveMode.INVENTORY) {
                ServerCommandUtil.giveToInventory((Player)sender, itemStack);
            } else if (giveMode == GiveMode.MOUSE_PICKUP) {
                ServerCommandUtil.mousePickupItemStack((Player)sender, itemStack);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Player '{} ({})' tried to cheat an ItemStack '{}' but does not have permission.", (Object)sender.getName(), (Object)sender.getUUID(), (Object)itemStack.getDisplayName());
            }
            IConnectionToClient connection = context.connection();
            connection.sendPacketToClient(new PacketCheatPermission(false, serverConfig), sender);
        }
    }

    public static void setHotbarSlot(ServerPacketContext context, ItemStack itemStack, int hotbarSlot) {
        IServerConfig serverConfig;
        ServerPlayer sender = context.player();
        if (ServerCommandUtil.hasPermissionForCheatMode((Player)sender, serverConfig = context.serverConfig())) {
            if (itemStack.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Player '{} ({})' tried to set an empty ItemStack to the hotbar slot: {}", (Object)sender.getName(), (Object)sender.getUUID(), (Object)hotbarSlot);
                }
                return;
            }
            if (!Inventory.isHotbarSlot((int)hotbarSlot)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Player '{} ({})' tried to set slot that is not in the hotbar: {}", (Object)sender.getName(), (Object)sender.getUUID(), (Object)hotbarSlot);
                }
                return;
            }
            ItemStack stackInSlot = sender.getInventory().getItem(hotbarSlot);
            if (ItemStack.matches((ItemStack)stackInSlot, (ItemStack)itemStack)) {
                return;
            }
            ItemStack itemStackCopy = itemStack.copy();
            sender.getInventory().setItem(hotbarSlot, itemStack);
            sender.level().playSound(null, sender.getX(), sender.getY(), sender.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, ((sender.getRandom().nextFloat() - sender.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            sender.inventoryMenu.broadcastChanges();
            ServerCommandUtil.notifyGive((Player)sender, itemStackCopy);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Player '{} ({})' tried to cheat an item '{}' to their hotbar but does not have permission.", (Object)sender.getName(), (Object)sender.getUUID(), (Object)itemStack.getDisplayName());
            }
            IConnectionToClient connection = context.connection();
            connection.sendPacketToClient(new PacketCheatPermission(false, serverConfig), sender);
        }
    }

    public static void mousePickupItemStack(Player sender, ItemStack itemStack) {
        int giveCount;
        AbstractContainerMenu containerMenu = sender.containerMenu;
        ItemStack itemStackCopy = itemStack.copy();
        ItemStack existingStack = containerMenu.getCarried();
        if (ServerCommandUtil.canStack(existingStack, itemStack)) {
            int newCount = Math.min(existingStack.getMaxStackSize(), existingStack.getCount() + itemStack.getCount());
            giveCount = newCount - existingStack.getCount();
            if (giveCount > 0) {
                existingStack.setCount(newCount);
            }
        } else {
            containerMenu.setCarried(itemStack);
            giveCount = itemStack.getCount();
        }
        if (giveCount > 0) {
            itemStackCopy.setCount(giveCount);
            ServerCommandUtil.notifyGive(sender, itemStackCopy);
            if (sender instanceof ServerPlayer) {
                containerMenu.broadcastChanges();
            }
        }
    }

    public static boolean canStack(ItemStack a, ItemStack b) {
        return !a.isEmpty() && !b.isEmpty() && ItemStack.isSameItemSameComponents((ItemStack)a, (ItemStack)b);
    }

    private static void giveToInventory(Player entityplayermp, ItemStack itemStack) {
        ItemStack itemStackCopy = itemStack.copy();
        boolean flag = entityplayermp.getInventory().add(itemStack);
        if (flag && itemStack.isEmpty()) {
            itemStack.setCount(1);
            ItemEntity entityitem = entityplayermp.drop(itemStack, false);
            if (entityitem != null) {
                entityitem.makeFakeItem();
            }
            entityplayermp.level().playSound(null, entityplayermp.getX(), entityplayermp.getY(), entityplayermp.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, ((entityplayermp.getRandom().nextFloat() - entityplayermp.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            entityplayermp.inventoryMenu.broadcastChanges();
        } else {
            ItemEntity entityitem = entityplayermp.drop(itemStack, false);
            if (entityitem != null) {
                entityitem.setNoPickUpDelay();
                entityitem.setTarget(entityplayermp.getUUID());
            }
        }
        ServerCommandUtil.notifyGive(entityplayermp, itemStackCopy);
    }

    private static void notifyGive(Player player, ItemStack stack) {
        if (player.getServer() == null) {
            return;
        }
        CommandSourceStack commandSource = player.createCommandSourceStack();
        int count = stack.getCount();
        Component stackTextComponent = stack.getDisplayName();
        Component displayName = player.getDisplayName();
        MutableComponent message = Component.translatable((String)"commands.give.success.single", (Object[])new Object[]{count, stackTextComponent, displayName});
        commandSource.sendSuccess(() -> ServerCommandUtil.lambda$notifyGive$1((Component)message), true);
    }

    private static Optional<CommandNode<CommandSourceStack>> getGiveCommand(Player sender) {
        return Optional.ofNullable(sender.getServer()).map(minecraftServer -> {
            Commands commandManager = minecraftServer.getCommands();
            CommandDispatcher dispatcher = commandManager.getDispatcher();
            RootCommandNode root = dispatcher.getRoot();
            return root.getChild("give");
        });
    }

    private static /* synthetic */ Component lambda$notifyGive$1(Component message) {
        return message;
    }
}

