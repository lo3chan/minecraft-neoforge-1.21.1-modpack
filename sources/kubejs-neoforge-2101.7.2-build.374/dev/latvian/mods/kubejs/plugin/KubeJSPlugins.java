package dev.latvian.mods.kubejs.plugin;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ModResourceBindings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IModFile;

public class KubeJSPlugins {
   private static final List<KubeJSPlugin> LIST = new ArrayList<>();
   private static final List<String> GLOBAL_CLASS_FILTER = new ArrayList<>();
   private static final ModResourceBindings BINDINGS = new ModResourceBindings();

   public static void load(List<IModFile> modFiles, boolean loadClientPlugins) {
      try {
         for (IModFile file : modFiles) {
            if (!file.getModInfos().isEmpty()) {
               loadMod(((IModInfo)file.getModInfos().getFirst()).getModId(), file, loadClientPlugins);
            }
         }
      } catch (Exception var4) {
         throw new RuntimeException("Failed to load KubeJS plugin", var4);
      }
   }

   private static void loadMod(String modId, IModFile mod, boolean loadClientPlugins) throws IOException {
      Path pp = mod.findResource(new String[]{"kubejs.plugins.txt"});
      if (Files.exists(pp)) {
         loadFromFile(Files.lines(pp), modId, loadClientPlugins);
      }

      Path pc = mod.findResource(new String[]{"kubejs.classfilter.txt"});
      if (Files.exists(pc)) {
         GLOBAL_CLASS_FILTER.addAll(Files.readAllLines(pc));
      }

      BINDINGS.readBindings(modId, mod);
   }

   private static void loadFromFile(Stream<String> contents, String source, boolean loadClientPlugins) {
      KubeJS.LOGGER.info("Found plugin source {}", source);
      contents.<String>map(s -> s.split("#", 2)[0].trim()).filter(s -> !s.isBlank()).flatMap(s -> {
         String[] line = s.split(" ");

         for (int i = 1; i < line.length; i++) {
            if (line[i].equalsIgnoreCase("client")) {
               if (!loadClientPlugins) {
                  if (DevProperties.get().logSkippedPlugins) {
                     KubeJS.LOGGER.warn("Plugin {} does not load on server side, skipping", line[0]);
                  }

                  return Stream.empty();
               }
            } else if (!ModList.get().isLoaded(line[i])) {
               if (DevProperties.get().logSkippedPlugins) {
                  KubeJS.LOGGER.warn("Plugin {} does not have required mod '{}' loaded, skipping", line[0], line[i]);
               }

               return Stream.empty();
            }
         }

         try {
            return Stream.of(Class.forName(line[0]));
         } catch (Throwable var5) {
            KubeJS.LOGGER.error("Failed to load plugin {} from source {}: {}", new Object[]{s, source, var5});
            var5.printStackTrace();
            return Stream.empty();
         }
      }).filter(KubeJSPlugin.class::isAssignableFrom).forEach(c -> {
         try {
            LIST.add((KubeJSPlugin)c.getDeclaredConstructor().newInstance());
         } catch (Throwable var3) {
            KubeJS.LOGGER.error("Failed to init KubeJS plugin {} from source {}: {}", new Object[]{c.getName(), source, var3});
         }
      });
   }

   public static ClassFilter createClassFilter(ScriptType type) {
      ClassFilter filter = new ClassFilter(type);
      forEachPlugin(filter, KubeJSPlugin::registerClasses);

      for (String s : GLOBAL_CLASS_FILTER) {
         if (s.length() >= 2) {
            if (s.startsWith("+")) {
               filter.allow(s.substring(1).trim());
            } else if (s.startsWith("-")) {
               filter.deny(s.substring(1).trim());
            }
         }
      }

      return filter;
   }

   public static void forEachPlugin(Consumer<KubeJSPlugin> callback) {
      LIST.forEach(callback);
   }

   public static <T> void forEachPlugin(T instance, BiConsumer<KubeJSPlugin, T> callback) {
      for (KubeJSPlugin item : LIST) {
         callback.accept(item, instance);
      }
   }

   public static List<KubeJSPlugin> getAll() {
      return Collections.unmodifiableList(LIST);
   }

   public static void addSidedBindings(BindingRegistry event) {
      BINDINGS.addBindings(event);
   }
}
