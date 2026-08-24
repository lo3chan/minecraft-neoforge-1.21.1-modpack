package net.mehvahdjukaar.moonlight.core.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundOpenConfigScreenMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

public class ConfigCommand {
   private static final String MOD_ARG = "mod_id";
   private static final Set<String> NOT_MODS = Set.of("minecraft", "java", "fabric", "fabricloader", "forge", "neoforge", "mixinextras");

   public static ArgumentBuilder<CommandSourceStack, ?> register() {
      return ((LiteralArgumentBuilder)Commands.literal("config").executes(ctx -> open(ctx, "")))
         .then(
            Commands.argument("mod_id", StringArgumentType.word())
               .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(suggestedMods(), builder))
               .executes(ctx -> open(ctx, StringArgumentType.getString(ctx, "mod_id")))
         );
   }

   private static int open(CommandContext<CommandSourceStack> ctx, String modId) {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayer();
      if (player == null) {
         return 0;
      } else {
         NetworkHelper.sendToClientPlayer(player, new ClientBoundOpenConfigScreenMessage(modId));
         return 1;
      }
   }

   private static List<String> suggestedMods() {
      return PlatHelper.getInstalledMods().stream().filter(id -> !NOT_MODS.contains(id) && !id.startsWith("fabric-")).toList();
   }
}
