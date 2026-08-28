/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.Commands
 *  net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
 */
package mezz.jei.neoforge.chat;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import mezz.jei.common.chat.JeiChatItemLinkRecipeLookup;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

public final class JeiInternalShowCommand {
    private JeiInternalShowCommand() {
    }

    public static void register(PermanentEventSubscriptions subscriptions) {
        subscriptions.register(RegisterClientCommandsEvent.class, JeiInternalShowCommand::onRegisterClientCommands);
    }

    private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder)Commands.literal((String)"jei_internal_show").then(Commands.argument((String)"link", (ArgumentType)StringArgumentType.greedyString()).executes(context -> {
            String link = StringArgumentType.getString((CommandContext)context, (String)"link");
            return JeiChatItemLinkRecipeLookup.executeShowRecipeCommand(link);
        })));
    }
}

