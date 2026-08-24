package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

public class DataExport {
   @HideFromJS
   public static DataExport export = null;
   public CommandSourceStack source;
   private final Map<String, Callable<byte[]>> exportedFiles = new ConcurrentHashMap<>();

   public static void exportData() {
      if (export != null) {
         try {
            export.exportData0();
         } catch (Exception var1) {
            var1.printStackTrace();
         }

         export = null;
      }
   }

   public void add(String path, Callable<byte[]> data) {
      try {
         this.exportedFiles.put(path, data);
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   public void addString(String path, String data) {
      this.add(path, () -> data.getBytes(StandardCharsets.UTF_8));
   }

   public void addJson(String path, JsonElement json) {
      this.add(path, () -> JsonUtils.toPrettyString(json).getBytes(StandardCharsets.UTF_8));
   }

   private void appendLine(StringBuilder sb, Calendar calendar, ConsoleLine line) {
      calendar.setTimeInMillis(line.timestamp);
      sb.append('[');
      TimeJS.appendTimestamp(sb, calendar);
      sb.append(']');
      sb.append(' ');
      sb.append('[');
      sb.append(line.type);
      sb.append(']');
      sb.append(' ');
      if (line.type == LogType.ERROR) {
         sb.append('!');
         sb.append(' ');
      }

      sb.append(line.getText());
      sb.append('\n');
   }

   private void exportData0() throws Exception {
      this.source.registryAccess().registries().forEach(reg -> {
         ResourceKey<? extends Registry<?>> key = reg.key();
         Registry<?> registry = reg.value();
         JsonObject j = new JsonObject();

         for (Entry<? extends ResourceKey<?>, ?> entryx : registry.entrySet()) {
            j.addProperty(entryx.getKey().location().toString(), entryx.getValue() == null ? "null" : entryx.getValue().getClass().getName());
         }

         this.addJson("registries/" + key.location().getPath() + ".json", j);
      });
      StringBuilder logStringBuilder = new StringBuilder();
      Calendar calendar = Calendar.getInstance();

      for (ConsoleLine line : ConsoleJS.SERVER.errors) {
         this.appendLine(logStringBuilder, calendar, line);
      }

      if (!logStringBuilder.isEmpty()) {
         logStringBuilder.setLength(logStringBuilder.length() - 1);
         this.addString("errors.log", logStringBuilder.toString());
      }

      logStringBuilder.setLength(0);

      for (ConsoleLine line : ConsoleJS.SERVER.warnings) {
         this.appendLine(logStringBuilder, calendar, line);
      }

      if (!logStringBuilder.isEmpty()) {
         logStringBuilder.setLength(logStringBuilder.length() - 1);
         this.addString("warnings.log", logStringBuilder.toString());
      }

      JsonArray modArr = new JsonArray();

      for (IModInfo mod : ModList.get().getMods()) {
         JsonObject o = new JsonObject();
         o.addProperty("id", mod.getModId().trim());
         o.addProperty("name", mod.getDisplayName().trim());
         o.addProperty("version", mod.getVersion().toString().trim());
         o.addProperty("description", mod.getDescription().trim());
         o.entrySet().removeIf(e -> e.getValue() instanceof JsonPrimitive p && p.isString() && p.getAsString().isEmpty());
         modArr.add(o);
      }

      this.addJson("mods.json", modArr);
      KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::exportServerData);
      JsonArray index = new JsonArray();
      this.exportedFiles.keySet().stream().sorted(String.CASE_INSENSITIVE_ORDER).forEach(index::add);
      this.addJson("index.json", index);
      HashSet<String> exportedFilePaths = new HashSet<>();

      for (String file : this.exportedFiles.keySet()) {
         exportedFilePaths.add(file.replace(':', '/'));
      }

      Files.walk(KubeJSPaths.EXPORT)
         .sorted(Comparator.reverseOrder())
         .filter(path -> Files.isDirectory(path) ? true : !exportedFilePaths.contains(KubeJSPaths.EXPORT.relativize(path).toString().replace('\\', '/')))
         .map(Path::toFile)
         .forEach(file -> {
            if (file.isFile()) {
               file.delete();
               KubeJS.LOGGER.info("Deleted old file {}", file.getPath());
            } else if (file.isDirectory() && file.list().length == 0) {
               file.delete();
               KubeJS.LOGGER.info("Deleted empty directory {}", file.getPath());
            }
         });
      if (Files.notExists(KubeJSPaths.EXPORT)) {
         Files.createDirectory(KubeJSPaths.EXPORT);
      }

      CompletableFuture[] arr = new CompletableFuture[this.exportedFiles.size()];
      int i = 0;

      for (Entry<String, Callable<byte[]>> entry : this.exportedFiles.entrySet()) {
         arr[i++] = CompletableFuture.runAsync(() -> {
            try {
               Path path = KubeJSPaths.EXPORT.resolve(entry.getKey().replace(':', '/'));
               Path parent = path.getParent();
               if (Files.notExists(parent)) {
                  Files.createDirectories(parent);
               }

               if (Files.notExists(path)) {
                  Files.createFile(path);
               }

               Files.write(path, entry.getValue().call());
            } catch (Exception var3) {
               var3.printStackTrace();
            }
         }, Util.backgroundExecutor());
      }

      CompletableFuture.allOf(arr).join();
      if (this.source.getServer().isSingleplayer()) {
         this.source
            .sendSuccess(
               () -> Component.literal("Done! Export in local/kubejs/export").kjs$clickOpenFile(KubeJSPaths.EXPORT.toAbsolutePath().toString()), false
            );
      } else {
         this.source.sendSuccess(() -> Component.literal("Done! Export in local/kubejs/export"), false);
      }
   }
}
