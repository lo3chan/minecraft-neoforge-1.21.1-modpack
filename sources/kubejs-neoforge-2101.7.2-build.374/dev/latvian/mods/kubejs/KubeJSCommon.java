package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.net.KubeServerData;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.web.WebServerProperties;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class KubeJSCommon {
   public void handleDataFromServerPacket(String channel, @Nullable CompoundTag data) {
   }

   @Nullable
   public Player getClientPlayer() {
      return null;
   }

   public void generateTypings(CommandSourceStack source) {
   }

   public void reloadConfig() {
      CommonProperties.reload();
      DevProperties.reload();
      WebServerProperties.reload();
   }

   public void reloadStartupScripts(boolean dedicated) {
   }

   public void export(List<ExportablePackResources> packs) {
   }

   public void openErrors(ScriptType type) {
   }

   public void openErrors(ScriptType type, List<ConsoleLine> errors, List<ConsoleLine> warnings) {
   }

   public void runInMainThread(Runnable runnable) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         server.execute(runnable);
      } else {
         runnable.run();
      }
   }

   public void updateServerData(KubeServerData data) {
   }

   public String getWebServerWindowTitle() {
      return "Dedicated Server";
   }
}
