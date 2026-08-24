package at.petrak.paucal.xplat.common.command;

import at.petrak.paucal.xplat.common.ContributorsManifest;
import at.petrak.paucal.xplat.common.msg.MsgReloadContributorsS2C;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.networking.NetworkManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandReloadContributors {
   public static void add(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(((LiteralArgumentBuilder)Commands.literal("reload").requires(css -> css.hasPermission(1))).executes(ctx -> {
         ContributorsManifest.loadContributors();
         NetworkManager.sendToPlayers(((CommandSourceStack)ctx.getSource()).getLevel().players(), new MsgReloadContributorsS2C());
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable("command.paucal.reload"), true);
         return 1;
      }));
   }
}
