package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroups;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NativeEventWrapper;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

public enum ScriptType implements ScriptTypePredicate, ScriptTypeHolder {
   STARTUP("startup", "KubeJS Startup", KubeJSPaths.STARTUP_SCRIPTS),
   SERVER("server", "KubeJS Server", KubeJSPaths.SERVER_SCRIPTS),
   CLIENT("client", "KubeJS Client", KubeJSPaths.CLIENT_SCRIPTS);

   public static final ScriptType[] VALUES = values();
   public final String name;
   public final ConsoleJS console;
   public final Path path;
   public final String nameStrip;
   public transient Executor executor;
   public final Lazy<ClassFilter> classFilter;
   public final Map<NativeEventWrapper.Listeners.Key, NativeEventWrapper.Listeners> nativeEventListeners;
   public KubeJSFileWatcherThread fileWatcherThread;

   private ScriptType(String n, String cname, Path path) {
      this.name = n;
      this.console = new ConsoleJS(this, LoggerFactory.getLogger(cname));
      this.path = path;
      this.nameStrip = this.name + "_scripts:";
      this.executor = Runnable::run;
      this.classFilter = Lazy.of(() -> KubeJSPlugins.createClassFilter(this));
      this.nativeEventListeners = new HashMap<>(0);
   }

   public Path getLogFile() {
      Path dir = FMLPaths.GAMEDIR.get().resolve("logs/kubejs");
      Path file = dir.resolve(this.name + ".log");

      try {
         if (!Files.exists(dir)) {
            Files.createDirectories(dir);
         }

         if (!Files.exists(file)) {
            Path oldFile = dir.resolve(this.name + ".txt");
            if (Files.exists(oldFile)) {
               Files.move(oldFile, file);
            } else {
               Files.createFile(file);
            }
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return file;
   }

   public boolean isClient() {
      return this == CLIENT;
   }

   public boolean isServer() {
      return this == SERVER;
   }

   public boolean isStartup() {
      return this == STARTUP;
   }

   @HideFromJS
   public void unload() {
      this.console.resetFile();

      for (EventGroup group : EventGroups.ALL.get().map().values()) {
         for (EventHandler handler : group.getHandlers().values()) {
            handler.clear(this);
         }
      }

      for (NativeEventWrapper.Listeners listener : this.nativeEventListeners.values()) {
         listener.listeners().clear();
      }

      this.fileWatcherThread = null;
   }

   @Override
   public boolean test(ScriptType type) {
      return type == this;
   }

   @Override
   public List<ScriptType> getValidTypes() {
      return List.of(this);
   }

   @NotNull
   public ScriptTypePredicate negate() {
      return switch (this) {
         case STARTUP -> ScriptTypePredicate.COMMON;
         case SERVER -> ScriptTypePredicate.STARTUP_OR_CLIENT;
         case CLIENT -> ScriptTypePredicate.STARTUP_OR_SERVER;
      };
   }

   @Override
   public ScriptType kjs$getScriptType() {
      return this;
   }

   static {
      ConsoleJS.STARTUP = STARTUP.console;
      ConsoleJS.SERVER = SERVER.console;
      ConsoleJS.CLIENT = CLIENT.console;
   }
}
