package tannyjung.tanshugetrees_handcode.systems.tree_generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import tannyjung.tanshugetrees_core.Core;
import tannyjung.tanshugetrees_core.game.GameUtils;
import tannyjung.tanshugetrees_core.outside.FileManager;
import tannyjung.tanshugetrees_core.outside.OutsideUtils;

public class PresetFixer {
   public static void start(ServerLevel level_server) {
      File template = new File(Core.path_config + "/dev/temporary/#dev/preset_template.txt");
      if (template.exists() && !template.isDirectory()) {
         File[] packs = new File(Core.path_config + "/custom_packs").listFiles();
         if (packs == null) {
            GameUtils.Misc.sendChatMessage(level_server, "Error not found any pack installed / red");
            return;
         }

         GameUtils.Misc.sendChatMessage(level_server, "Start fixing all tree presets from all extracted packs / gray");
         boolean fix_at_least_one = false;
         File[] presets = null;

         for (File pack : packs) {
            if (!pack.getName().endsWith(".zip")) {
               presets = new File(pack.getPath() + "/presets").listFiles();
               if (presets != null) {
                  for (File preset : presets) {
                     preset = new File(preset.getPath() + "/" + preset.getName() + ".txt");
                     if (preset.exists() && !preset.isDirectory() && fix(level_server, template, preset)) {
                        fix_at_least_one = true;
                     }
                  }
               }
            }
         }

         if (fix_at_least_one) {
            GameUtils.Misc.sendChatMessage(level_server, "Completed! / gray");
         } else {
            GameUtils.Misc.sendChatMessage(level_server, "There's nothing changed / gray");
         }
      } else {
         GameUtils.Misc.sendChatMessage(level_server, "Template not found / red");
      }
   }

   private static boolean fix(ServerLevel level_server, File template, File file) {
      boolean fix_at_least_one = false;
      String id = Path.of(Core.path_config + "/custom_packs").relativize(file.toPath()).toString().replace("\\", "/").replace("/presets/", "/");
      Map<String, String> data_unlock = new HashMap<>();
      Map<String, String> data_lock = new HashMap<>();
      int index = 0;

      try {
         BufferedReader buffered_reader = new BufferedReader(new FileReader(file), 65536);
         String scan = "";

         while ((scan = buffered_reader.readLine()) != null) {
            if (!scan.isEmpty() && !scan.startsWith("-")) {
               index = scan.indexOf(" = ");
               if (index > 0) {
                  if (scan.startsWith("    ")) {
                     data_lock.put(scan.substring("    ".length(), index), scan);
                  } else {
                     data_unlock.put(scan.substring(0, index), scan);
                  }
               }
            }
         }

         buffered_reader.close();
      } catch (Exception var15) {
         OutsideUtils.exception(new Exception(), var15, "");
      }

      StringBuilder write = new StringBuilder();
      String name = "";
      String value_old = "";
      String value_new = "";

      try {
         BufferedReader buffered_reader = new BufferedReader(new FileReader(template), 65536);
         String scan = "";

         while ((scan = buffered_reader.readLine()) != null) {
            if (!scan.isEmpty() && !scan.startsWith("-")) {
               index = scan.indexOf(" = ");
               if (index > 0) {
                  name = scan.substring(0, index);
                  if (data_lock.containsKey(name)) {
                     write.append(data_lock.get(name)).append("\n");
                  } else if (data_unlock.containsKey(name)) {
                     if (!data_unlock.get(name).equals(scan)) {
                        fix_at_least_one = true;
                        value_old = data_unlock.get(name).substring(index + " = ".length()).replace("\"", "\\\"");
                        value_new = scan.substring(index + " = ".length()).replace("\"", "\\\"");
                        GameUtils.Misc.sendChatMessage(level_server, "Updated " + id + " > " + name + " > " + value_old + " > " + value_new + " / dark_gray");
                     }

                     write.append(scan).append("\n");
                  } else {
                     fix_at_least_one = true;
                     write.append(scan).append("\n");
                     GameUtils.Misc.sendChatMessage(level_server, "Added " + id + " > " + name + " / dark_gray");
                  }
               }
            } else {
               write.append(scan).append("\n");
            }
         }

         buffered_reader.close();
      } catch (Exception var14) {
         OutsideUtils.exception(new Exception(), var14, "");
      }

      FileManager.writeTXT(file.getPath(), write.toString(), false);
      return fix_at_least_one;
   }
}
