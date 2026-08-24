package dev.latvian.mods.kubejs.script;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScriptManager {
   public final ScriptType scriptType;
   public final Map<String, ScriptPack> packs;
   private final ClassFilter classFilter;
   public KubeJSContextFactory contextFactory;
   public boolean canListenEvents;

   public ScriptManager(ScriptType t) {
      this.scriptType = t;
      this.packs = new LinkedHashMap<>();
      this.classFilter = KubeJSPlugins.createClassFilter(this.scriptType);
   }

   public RegistryAccessContainer getRegistries() {
      return RegistryAccessContainer.current;
   }

   public void unload() {
      this.packs.clear();
      this.scriptType.unload();
   }

   public void reload() {
      KubeJSPlugins.forEachPlugin(KubeJSPlugin::clearCaches);
      long start = System.currentTimeMillis();
      KubeJSWeb.broadcastUpdate("before_scripts_loaded", "", () -> {
         JsonObject broadcast = new JsonObject();
         broadcast.addProperty("type", this.scriptType.name);
         broadcast.addProperty("time", start);
         return broadcast;
      });
      this.unload();
      this.scriptType.console.writeToFile(LogType.INIT, KubeJS.DISPLAY_NAME + "; MC 2101 NeoForge");
      this.scriptType.console.writeToFile(LogType.INIT, "Loaded plugins:");

      for (KubeJSPlugin plugin : KubeJSPlugins.getAll()) {
         this.scriptType.console.writeToFile(LogType.INIT, "- " + plugin.getClass().getName());
      }

      KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::beforeScriptsLoaded);
      this.loadFromDirectory();
      this.load(start);
      KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::afterScriptsLoaded);
   }

   public void collectScripts(ScriptPack pack, Path dir, String path) {
      if (!path.isEmpty() && !path.endsWith("/")) {
         path = path + "/";
      }

      String pathPrefix = path;

      try {
         for (Path file : Files.walk(dir, 10, FileVisitOption.FOLLOW_LINKS).filter(x$0 -> Files.isRegularFile(x$0)).toList()) {
            String fileName = dir.relativize(file).toString().replace(File.separatorChar, '/');
            if (fileName.endsWith(".js") || fileName.endsWith(".ts") && !fileName.endsWith(".d.ts")) {
               pack.info.scripts.add(new ScriptFileInfo(pack.info, file, pathPrefix + fileName));
            }
         }
      } catch (IOException var8) {
         var8.printStackTrace();
      }
   }

   public void loadPackFromDirectory(Path path, String name, boolean exampleFile) {
      if (Files.notExists(path)) {
         if (!exampleFile) {
            return;
         }

         try {
            Files.createDirectories(path);
         } catch (Exception var8) {
            this.scriptType.console.error("Failed to create script directory", var8);
         }

         try (OutputStream out = Files.newOutputStream(path.resolve("main.js"))) {
            out.write(
               ("// Visit the wiki for more info - https://kubejs.com/\nconsole.info('Hello, World! (Loaded " + name + " example script)')\n\n")
                  .getBytes(StandardCharsets.UTF_8)
            );
         } catch (Exception var10) {
            this.scriptType.console.error("Failed to write main.js", var10);
         }
      }

      ScriptPack pack = new ScriptPack(this, new ScriptPackInfo(path.getFileName().toString(), ""));
      if (Files.exists(path)) {
         this.collectScripts(pack, path, "");

         for (ScriptFileInfo fileInfo : pack.info.scripts) {
            this.loadFile(pack, fileInfo);
         }
      }

      this.packs.put(pack.info.namespace, pack);
   }

   private void loadFile(ScriptPack pack, ScriptFileInfo fileInfo) {
      try {
         ScriptFile file = new ScriptFile(pack, fileInfo);
         String skip = file.skipLoading();
         if (skip.isEmpty()) {
            pack.scripts.add(file);
         } else {
            this.scriptType.console.info("Skipped " + fileInfo.location + ": " + skip);
         }
      } catch (Throwable var5) {
         this.scriptType.console.error("Failed to pre-load script file '" + fileInfo.location + "'", var5);
      }
   }

   public void loadFromDirectory() {
      this.loadPackFromDirectory(this.scriptType.path, this.scriptType.name, true);
   }

   public boolean isClassAllowed(String name) {
      return this.classFilter.isAllowed(name);
   }

   private void load(long startAll) {
      this.contextFactory = new KubeJSContextFactory(this);
      this.scriptType.console.contextFactory = new WeakReference<>(this.contextFactory);
      if (PlatformWrapper.isGeneratingData()) {
         this.scriptType.console.info("Skipping KubeJS script loading (DataGen)");
      } else {
         this.canListenEvents = true;
         TypeWrapperRegistry typeWrappers = new TypeWrapperRegistry(this.scriptType, this.contextFactory.getTypeWrappers());
         RecordDefaultsRegistry recordDefaults = this.contextFactory::registerDefaultRecordProperties;

         for (KubeJSPlugin plugin : KubeJSPlugins.getAll()) {
            plugin.registerTypeWrappers(typeWrappers);
            plugin.registerRecordDefaults(recordDefaults);
         }

         int i = 0;
         int t = 0;
         KubeJSContext cx = (KubeJSContext)this.contextFactory.enter();
         ArrayList<ScriptFile> watchingFiles = new ArrayList<>();

         for (ScriptPack pack : this.packs.values()) {
            try {
               pack.scripts.sort(null);

               for (ScriptFile file : pack.scripts) {
                  t++;
                  long start = System.currentTimeMillis();

                  try {
                     file.load(cx);
                     i++;
                     this.scriptType.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000.0 + " s");
                     watchingFiles.add(file);
                  } catch (Throwable var16) {
                     this.scriptType.console.error("", var16);
                  }
               }
            } catch (Throwable var17) {
               this.scriptType.console.error("Failed to read script pack " + pack.info.namespace, var17);
            }
         }

         this.loadAdditional();
         long end = System.currentTimeMillis();
         long ms = end - startAll;
         this.scriptType
            .console
            .info(
               "Loaded "
                  + i
                  + "/"
                  + t
                  + " KubeJS "
                  + this.scriptType.name
                  + " scripts in "
                  + ms / 1000.0
                  + " s with "
                  + this.scriptType.console.errors.size()
                  + " errors and "
                  + this.scriptType.console.warnings.size()
                  + " warnings"
            );
         this.canListenEvents = false;
         if (!watchingFiles.isEmpty() && DevProperties.get().reloadOnFileSave) {
            this.scriptType.fileWatcherThread = new KubeJSFileWatcherThread(this.scriptType, watchingFiles.toArray(new ScriptFile[0]), this::fullReload);
            this.scriptType.fileWatcherThread.start();
         }

         int t1 = t;
         int i1 = i;
         KubeJSWeb.broadcastUpdate("after_scripts_loaded", "", () -> {
            JsonObject broadcast = new JsonObject();
            broadcast.addProperty("type", this.scriptType.name);
            broadcast.addProperty("total", t1);
            broadcast.addProperty("successful", i1);
            broadcast.addProperty("errors", this.scriptType.console.errors.size());
            broadcast.addProperty("warnings", this.scriptType.console.warnings.size());
            broadcast.addProperty("time", end);
            broadcast.addProperty("duration", ms);
            return broadcast;
         });
      }
   }

   public void loadAdditional() {
   }

   protected void fullReload() {
      KubeJS.PROXY.runInMainThread(this::reload);
   }
}
