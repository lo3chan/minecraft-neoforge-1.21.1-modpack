package codx.codxlib.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public final class CodxLibCommands {
   private static final String SUPPORT_URL = "https://discord.com/channels/1187428541072158860/1506642708532432986";

   private CodxLibCommands() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("codxlib")
                  .then(Commands.literal("versions").executes(ctx -> runVersions((CommandSourceStack)ctx.getSource()))))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("help").requires(CodxLibCommands::canRequestHelp))
                     .executes(ctx -> runHelp((CommandSourceStack)ctx.getSource()))
               ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("pack").requires(CodxLibCommands::canRequestHelp))
                  .executes(ctx -> runPack((CommandSourceStack)ctx.getSource()))
            )
      );
   }

   private static boolean canRequestHelp(CommandSourceStack source) {
      MinecraftServer server = source.getServer();
      return server != null && server.isSingleplayer() || source.hasPermission(2);
   }

   private static int runHelp(CommandSourceStack source) {
      try {
         Path file = CodxDebugReport.write(source.getServer());
         source.sendSuccess(() -> Component.literal("§7[CodxLib] Debug report written to §6" + file), false);
         source.sendSuccess(() -> Component.literal("§7[CodxLib] Lists all installed mods, codx mods + their settings, and online players."), false);
         source.sendSuccess(
            () -> Component.literal("§7[CodxLib] Need help? Open a support request: ")
               .append(CodxNotify.link("https://discord.com/channels/1187428541072158860/1506642708532432986")),
            false
         );
         startInstancePack(source);
         return 1;
      } catch (IOException var2) {
         source.sendFailure(Component.literal("§c[CodxLib] Failed to write debug report: " + var2.getMessage()));
         return 0;
      }
   }

   private static int runPack(CommandSourceStack source) {
      return startInstancePack(source) ? 1 : 0;
   }

   private static boolean startInstancePack(CommandSourceStack source) {
      MinecraftServer server = source.getServer();
      if (server == null) {
         source.sendFailure(Component.literal("§c[CodxLib] Cannot export an instance pack without a running world."));
         return false;
      } else {
         source.sendSuccess(() -> Component.literal("§7[CodxLib] Building a Modrinth instance pack — this takes a few seconds..."), false);
         CodxInstancePack.writeAsync(server)
            .whenComplete(
               (result, error) -> server.execute(
                  () -> {
                     if (error == null && result != null) {
                        source.sendSuccess(() -> Component.literal("§7[CodxLib] Instance pack written to §6" + result.file()), false);
                        source.sendSuccess(
                           () -> Component.literal(
                              "§7[CodxLib] Open it in the Modrinth App (Add instance -> From file) to rebuild this setup: §6"
                                 + result.resolved()
                                 + "§7 of §6"
                                 + result.total()
                                 + "§7 mods"
                                 + (result.seed() == null ? "" : ", world seed §6" + result.seed())
                           ),
                           false
                        );
                        if (!result.unresolved().isEmpty()) {
                           source.sendSuccess(
                              () -> Component.literal(
                                 "§e[CodxLib] §7"
                                    + result.unresolved().size()
                                    + " mod(s) are not on Modrinth and must be added by hand — they are listed in the pack's codxlib-debug/README.txt."
                              ),
                              false
                           );
                        }
                     } else {
                        String reason = error == null ? "unknown error" : rootMessage(error);
                        source.sendFailure(Component.literal("§c[CodxLib] Failed to write the instance pack: " + reason));
                     }
                  }
               )
            );
         return true;
      }
   }

   private static String rootMessage(Throwable error) {
      Throwable cause = error;

      while (cause.getCause() != null && cause.getCause() != cause) {
         cause = cause.getCause();
      }

      String message = cause.getMessage();
      return message != null && !message.isBlank() ? message : cause.getClass().getSimpleName();
   }

   private static int runVersions(CommandSourceStack source) {
      List<ModInfo> mods = UpdateChecker.registered();
      if (mods.isEmpty()) {
         source.sendSuccess(() -> Component.literal("§7[CodxLib] No mods are registered for update checks."), false);
         return 0;
      } else {
         source.sendSuccess(() -> Component.literal("§7[CodxLib] Checking versions for §6" + mods.size() + "§7 mod(s)..."), false);

         for (ModInfo mod : mods) {
            String current = UpdateChecker.currentVersionOf(mod);
            UpdateChecker.checkVersionAsync(
               source.getServer(),
               mod,
               (hasUpdate, latest) -> {
                  Component line = (Component)(hasUpdate && latest != null
                     ? UpdateChecker.updateAvailableMessage(mod, latest)
                     : Component.literal("§7" + mod.chatPrefix() + " up to date (§a" + current + "§7)"));
                  source.sendSuccess(() -> line, false);
               }
            );
         }

         return mods.size();
      }
   }
}
