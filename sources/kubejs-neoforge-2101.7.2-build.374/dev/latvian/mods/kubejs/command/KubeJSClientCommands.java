package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.client.KubeJSClient;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;

public class KubeJSClientCommands {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> cmd = (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)Commands.literal("kubejs")
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload")
                        .then(
                           ((LiteralArgumentBuilder)Commands.literal("client-scripts").requires(source -> true))
                              .executes(context -> reloadClient((CommandSourceStack)context.getSource()))
                        ))
                     .then(
                        ((LiteralArgumentBuilder)Commands.literal("textures").requires(source -> true))
                           .executes(context -> reloadTextures((CommandSourceStack)context.getSource()))
                     ))
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("lang").requires(source -> true))
                        .executes(context -> reloadLang((CommandSourceStack)context.getSource()))
                  )
            ))
         .then(Commands.literal("browse").executes(source -> {
            Util.getPlatform().openPath(KubeJSPaths.DIRECTORY);
            return 1;
         }));
      LiteralCommandNode<CommandSourceStack> node = dispatcher.register(cmd);
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("kjs").redirect(node));
   }

   private static int reloadClient(CommandSourceStack source) {
      KubeJSClient.reloadClientScripts();
      source.sendSystemMessage(Component.literal("Done! To reload textures, models and other assets, press F3 + T"));
      return 1;
   }

   private static int reloadTextures(CommandSourceStack source) {
      reloadResources(Minecraft.getInstance().getTextureManager());
      return 1;
   }

   private static int reloadLang(CommandSourceStack source) {
      KubeJSClient.reloadClientScripts();
      reloadResources(Minecraft.getInstance().getLanguageManager());
      return 1;
   }

   private static void reloadResources(PreparableReloadListener listener) {
      long start = System.currentTimeMillis();
      Minecraft mc = Minecraft.getInstance();
      mc.getResourceManager().getResource(GeneratedData.INTERNAL_RELOAD.id());
      listener.reload(
            CompletableFuture::completedFuture, mc.getResourceManager(), InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Util.backgroundExecutor(), mc
         )
         .thenAccept(unused -> mc.player.sendSystemMessage(Component.literal("Done! You still may have to reload all assets with F3 + T")));
   }
}
