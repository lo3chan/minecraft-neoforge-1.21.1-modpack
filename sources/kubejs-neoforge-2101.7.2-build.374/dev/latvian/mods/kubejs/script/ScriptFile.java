package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class ScriptFile implements Comparable<ScriptFile> {
   private static final Pattern PROPERTY_PATTERN = Pattern.compile("^(\\w+)\\s*[:=]?\\s*(-?\\w+)$");
   public final ScriptPack pack;
   public final ScriptFileInfo info;
   private final Map<String, List<String>> properties;
   private int priority;
   private boolean ignored;
   private String packMode;
   private final Set<String> requiredMods;
   private boolean requiredClient;
   public String[] lines;
   public long lastModified;

   public ScriptFile(ScriptPack pack, ScriptFileInfo info) throws Exception {
      this.pack = pack;
      this.info = info;
      this.properties = new HashMap<>();
      this.priority = 0;
      this.ignored = false;
      this.packMode = "";
      this.requiredMods = new HashSet<>(0);
      this.requiredClient = false;
      this.lines = Files.readAllLines(info.path).toArray(StringUtilsWrapper.EMPTY_STRING_ARRAY);

      try {
         this.lastModified = Files.getLastModifiedTime(this.info.path).toMillis();
      } catch (Exception var6) {
         this.lastModified = 0L;
      }

      for (int i = 0; i < this.lines.length; i++) {
         String tline = this.lines[i].trim();
         if (tline.isEmpty() || tline.startsWith("import ")) {
            this.lines[i] = "";
         } else if (tline.startsWith("//")) {
            Matcher matcher = PROPERTY_PATTERN.matcher(tline.substring(2).trim());
            if (matcher.find()) {
               this.properties.computeIfAbsent(matcher.group(1).trim(), k -> new ArrayList<>()).add(matcher.group(2).trim());
            }

            this.lines[i] = "";
         }
      }

      this.priority = Integer.parseInt(this.getProperty("priority", "0"));
      this.ignored = this.getProperty("ignored", "false").equals("true") || this.getProperty("ignore", "false").equals("true");
      this.packMode = this.getProperty("packmode", "");
      this.requiredMods.addAll(this.getProperties("requires"));
      this.requiredClient = this.requiredMods.remove("client");
   }

   public void load(KubeJSContext cx) throws Throwable {
      cx.evaluateString(cx.topLevelScope, String.join("\n", this.lines), this.info.location, 1, null);
      this.lines = StringUtilsWrapper.EMPTY_STRING_ARRAY;
   }

   public List<String> getProperties(String s) {
      return this.properties.getOrDefault(s, List.of());
   }

   public String getProperty(String s, String def) {
      List<String> l = this.getProperties(s);
      return l.isEmpty() ? def : (String)l.getLast();
   }

   public int getPriority() {
      return this.priority;
   }

   public String skipLoading() {
      if (this.ignored) {
         return "Ignored";
      } else if (this.requiredClient && !FMLLoader.getDist().isClient()) {
         return "Client only";
      } else if (!this.packMode.isEmpty() && !this.packMode.equals(CommonProperties.get().packMode)) {
         return "Pack mode mismatch";
      } else {
         if (!this.requiredMods.isEmpty()) {
            for (String mod : this.requiredMods) {
               if (!ModList.get().isLoaded(mod)) {
                  return "Mod " + mod + " is not loaded";
               }
            }
         }

         return "";
      }
   }

   public int compareTo(ScriptFile o) {
      int i = Integer.compare(o.priority, this.priority);
      return i == 0 ? this.info.locationPath.compareToIgnoreCase(o.info.locationPath) : i;
   }
}
