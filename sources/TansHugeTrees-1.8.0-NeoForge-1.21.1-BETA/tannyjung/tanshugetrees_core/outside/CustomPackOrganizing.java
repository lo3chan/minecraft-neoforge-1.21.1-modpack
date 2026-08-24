package tannyjung.tanshugetrees_core.outside;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.server.level.ServerLevel;
import tannyjung.tanshugetrees_core.Core;
import tannyjung.tanshugetrees_core.game.GameUtils;

public class CustomPackOrganizing {
   private static final Map<String, String> cache_pack_ids = new HashMap<>();
   private static final Set<String> cache_error_files = new HashSet<>();
   private static final Map<String, Map<String, Set<String>>> errors = new HashMap<>();

   public static void start(ServerLevel level_server, String pack_separate_multiple, String folder_settings, String folder_functions) {
      errors.clear();
      FileManager.delete(Core.path_config + "/dev/temporary");
      FileManager.createEmptyFile(Core.path_config + "/dev/temporary", true);
      File[] packs = new File(Core.path_config + "/custom_packs").listFiles();
      if (packs != null) {
         for (File pack : packs) {
            FileManager.rename(pack.getPath(), pack.getName().replace("[INCOMPATIBLE] ", ""));
         }

         packs = new File(Core.path_config + "/custom_packs").listFiles();
         if (packs != null) {
            for (File pack : packs) {
               if (pack.getName().endsWith(".zip")) {
                  FileManager.extractZIP(pack.getPath(), Core.path_config + "/dev/temporary/pack_zip/" + pack.getName(), false, "");
               }
            }

            organizeInfo();
            packs = new File(Core.path_config + "/custom_packs").listFiles();
            if (packs != null) {
               pack_separate_multiple = " / " + pack_separate_multiple + " / ";
               File main_pack = TannyPackManager.getCurrentFile();
               if (main_pack.exists()) {
                  organize(main_pack, pack_separate_multiple);
               }

               for (File packx : packs) {
                  if (!packx.getName().equals(main_pack.getName())) {
                     organize(packx, pack_separate_multiple);
                  }
               }

               File file = new File(Core.path_config + "/dev/temporary/edit");
               if (file.exists()) {
                  for (File scan : FileManager.getAllFiles(file.getPath())) {
                     Path path_to = file.toPath().relativize(scan.toPath());
                     path_to = Path.of(Core.path_config + "/dev/temporary").resolve(path_to);
                     if (path_to.toFile().exists()) {
                        if (scan.getName().endsWith(".txt")) {
                           FileManager.mergeTXT(scan, path_to.toFile());
                        } else {
                           FileManager.copy(scan.getPath(), path_to.toString(), false);
                        }
                     }
                  }
               }

               test(level_server, folder_settings, false);
               test(level_server, folder_functions, true);
               FileManager.delete(Core.path_config + "/dev/temporary/pack_zip");

               for (String scanx : cache_error_files) {
                  FileManager.rename(scanx, "[INCOMPATIBLE] " + new File(scanx).getName());
               }

               cache_pack_ids.clear();
               cache_error_files.clear();
            }
         }
      }
   }

   private static void organizeInfo() {
      File[] packs = new File(Core.path_config + "/custom_packs").listFiles();
      if (packs != null) {
         Map<String, String> locations = new HashMap<>();
         File file = null;

         for (File pack : packs) {
            if (pack.getName().endsWith(".zip")) {
               file = new File(Core.path_config + "/dev/temporary/pack_zip/" + pack.getName() + "/info.txt");
            } else {
               file = new File(pack.getPath() + "/info.txt");
            }

            if (file.exists()) {
               FileManager.copy(file.getPath(), Core.path_config + "/dev/temporary/info/" + pack.getName() + ".txt", false);
               locations.put(pack.getName(), file.getPath());
            }
         }

         for (Entry<String, String> entry : locations.entrySet()) {
            cache_pack_ids.put(entry.getKey(), OutsideUtils.convertFileToDataMap(entry.getValue()).get("pack_id"));
         }

         boolean pass = false;
         Map<String, String> data = null;
         String location = "";
         String pack_id = "";
         String data_structure_version = "";
         String required_packs = "";
         String required_mods = "";
         Map<String, List<Integer>> test_duplicated_pack = new HashMap<>();

         for (File pack : packs) {
            boolean var21 = true;
            location = locations.get(pack.getName());
            if (location == null) {
               CustomPackOrganizing.Error.add(
                  "pack",
                  "packs / info file not found. This will results skipping these packs. Make sure you use the version that includes info file.",
                  pack.getPath(),
                  pack.getName()
               );
               var21 = false;
            }

            data = OutsideUtils.convertFileToDataMap(location);
            pack_id = cache_pack_ids.get(pack.getName());
            if (pack_id == null) {
               CustomPackOrganizing.Error.add(
                  "pack",
                  "packs / pack ID not found. This will results skipping these packs. Make sure you use the version that includes pack ID.",
                  pack.getPath(),
                  pack.getName()
               );
               var21 = false;
            } else {
               test_duplicated_pack.computeIfAbsent(pack_id, create -> new ArrayList<>()).add(1);
               if (test_duplicated_pack.get(pack_id).size() > 1) {
                  CustomPackOrganizing.Error.add(
                     "pack",
                     "packs / duplicated pack IDs. This will results skipping these packs. You can report this to the pack authors to help them fix it.",
                     pack.getPath(),
                     pack.getName() + " > " + pack_id
                  );
                  var21 = false;
                  cache_pack_ids.replace(pack.getName(), pack_id + "_" + test_duplicated_pack.get(pack_id).size());
               }
            }

            data_structure_version = data.get("data_structure_version");
            if (data_structure_version == null) {
               CustomPackOrganizing.Error.add("pack", "packs / missing data_structure_version", pack.getPath(), pack.getName() + " > " + data_structure_version);
               var21 = false;
            } else if (!Core.data_structure_version_pack.equals(data_structure_version)) {
               CustomPackOrganizing.Error.add(
                  "pack",
                  "packs / unsupported data structure version. This will results skipping these packs. Your version is "
                     + Core.data_structure_version_pack
                     + " but these packs require a different version.",
                  pack.getPath(),
                  pack.getName() + " > " + data_structure_version
               );
               var21 = false;
            }

            required_packs = data.get("required_packs");
            if (required_packs == null) {
               CustomPackOrganizing.Error.add("pack", "packs / missing required_packs", pack.getPath(), pack.getName() + " > " + data_structure_version);
               var21 = false;
            } else if (!required_packs.isEmpty()) {
               for (String scan : required_packs.split(" / ")) {
                  if (!cache_pack_ids.containsValue(scan)) {
                     CustomPackOrganizing.Error.add(
                        "pack",
                        "packs / required packs not found. This will results skipping these packs. Make sure you install required packs to allow these packs to work.",
                        pack.getPath(),
                        pack.getName() + " > " + scan
                     );
                     var21 = false;
                  }
               }
            }

            required_mods = data.get("required_mods");
            if (required_mods == null) {
               CustomPackOrganizing.Error.add("pack", "packs / missing required_mods", pack.getPath(), pack.getName() + " > " + data_structure_version);
               var21 = false;
            } else if (!required_mods.isEmpty()) {
               for (String scanx : required_mods.split(" / ")) {
                  if (!GameUtils.Misc.isModLoaded(scanx)) {
                     CustomPackOrganizing.Error.add(
                        "pack",
                        "packs / required mods not found. This will results skipping these packs. Make sure you install required mods to allow these packs to work.",
                        pack.getPath(),
                        pack.getName() + " > " + scanx
                     );
                     var21 = false;
                  }
               }
            }

            if (!var21) {
               FileManager.rename(pack.getPath(), "[INCOMPATIBLE] " + pack.getName());
               cache_pack_ids.remove(pack.getName());
               cache_pack_ids.put("[INCOMPATIBLE] " + pack.getName(), pack_id);
            }
         }
      }
   }

   private static void test(ServerLevel level_server, String folders, boolean is_function) {
      for (String folder : folders.split(" / ")) {
         String suffix = "";
         if (folder.contains(" < ")) {
            String[] split = folder.split(" < ");
            folder = split[0];
            suffix = split[1];
         }

         File file = new File(Core.path_config + "/dev/temporary/" + folder);
         if (file.exists()) {
            for (File scan : FileManager.getAllFiles(file.getPath())) {
               if (!scan.getName().startsWith("[INCOMPATIBLE] ") && scan.getName().endsWith(suffix + ".txt")) {
                  CustomPackOrganizing.Option.testParse(level_server, scan, is_function);
               }
            }
         }
      }
   }

   private static void organize(File pack, String pack_separate_multiple) {
      boolean incompatible = pack.getName().startsWith("[INCOMPATIBLE] ");
      if (pack.getName().endsWith(".zip")) {
         pack = new File(Core.path_config + "/dev/temporary/pack_zip/" + pack.getName());
      }

      File[] files = pack.listFiles();
      if (files != null) {
         boolean is_separate_multiple = false;

         for (File file : files) {
            if (file.isDirectory()) {
               is_separate_multiple = pack_separate_multiple.contains(" / " + file.getName() + " / ");
               boolean is_separate_multiple_final = is_separate_multiple;

               for (File scan : FileManager.getAllFiles(file.getPath())) {
                  Path path_copy_to = Path.of(Core.path_config + "/dev/temporary/" + file.getName());
                  if (is_separate_multiple_final) {
                     path_copy_to = path_copy_to.resolve(cache_pack_ids.get(pack.getName()));
                  }

                  path_copy_to = path_copy_to.resolve(pack.toPath().resolve(file.getName()).relativize(scan.toPath()));
                  if (incompatible) {
                     path_copy_to = path_copy_to.getParent().resolve("[INCOMPATIBLE] " + path_copy_to.toFile().getName());
                  }

                  FileManager.copy(scan.getPath(), path_copy_to.toString(), false);
               }
            }
         }
      }
   }

   public static class Error {
      private static void add(String type, String error, String path, String troublemaker) {
         CustomPackOrganizing.errors.computeIfAbsent(type, create -> new HashMap<>()).computeIfAbsent(error, create -> new HashSet<>()).add(troublemaker);
         CustomPackOrganizing.cache_error_files.add(path);
      }

      public static void sendMessage(ServerLevel level_server) {
         boolean to_chat = false;
         String message = "";
         String[] split = null;
         boolean first = true;

         for (Entry<String, Map<String, Set<String>>> entry1 : CustomPackOrganizing.errors.entrySet()) {
            to_chat = level_server != null && (entry1.getKey().equals("pack") || Core.developer_mode);
            if (first) {
               first = false;
               if (!to_chat) {
                  Core.logger.error("----------------------------------------------------------------------------------------------------");
               }
            }

            for (Entry<String, Set<String>> entry2 : entry1.getValue().entrySet()) {
               split = entry2.getKey().split(" / ");
               message = "Detected incompatible " + split[0] + ", caused by " + split[1];
               if (to_chat) {
                  GameUtils.Misc.sendChatMessage(level_server, message + " / red");
               } else {
                  Core.logger.error(message);
               }

               for (String get : entry2.getValue()) {
                  if (to_chat) {
                     GameUtils.Misc.sendChatMessage(level_server, get + " / dark_gray");
                  } else {
                     Core.logger.error("- {}", get);
                  }
               }

               if (!to_chat) {
                  Core.logger.error("----------------------------------------------------------------------------------------------------");
               }
            }
         }
      }
   }

   public static class Option {
      private static boolean testParse(ServerLevel level_server, File file, boolean is_function) {
         boolean pass = true;
         String id = Path.of(Core.path_config + "/dev/temporary/").relativize(file.toPath()).toString().replace("\\", "/");
         String[] split = null;
         String option = "";
         String value = "";
         File file_test = null;

         for (String scan : FileManager.readTXT(file.getPath())) {
            if (scan.contains(" = ")) {
               split = scan.split(" = ");

               try {
                  option = split[0];
                  value = split[1];
               } catch (Exception var14) {
                  OutsideUtils.exception(new Exception(), var14, "");
                  CustomPackOrganizing.Error.add(
                     "file",
                     "setting files / option parsing error. This will results skipping them in mod systems.",
                     file.getPath(),
                     id + " > " + Arrays.toString((Object[])split)
                  );
                  pass = false;
               }

               if (!is_function && !value.equals("none")) {
                  if (option.startsWith("path_")) {
                     option = option.substring("path_".length()).replace("_", " ");
                     file_test = new File(Core.path_config + "/dev/temporary/" + value);
                     if (file_test.exists()) {
                        if (file_test.listFiles() == null) {
                           CustomPackOrganizing.Error.add(
                              "file", "settings files / empty " + option + " folder. This will results skipping them in mod systems.", file.getPath(), id
                           );
                           pass = false;
                        }
                     } else {
                        file_test = new File(file_test.getPath() + ".txt");
                        if (!file_test.exists()) {
                           CustomPackOrganizing.Error.add(
                              "file",
                              "world gen files / " + option + " path not found. This will results skipping them in mod systems.",
                              file.getPath(),
                              id + " > " + value
                           );
                           pass = false;
                        }
                     }
                  } else if (option.startsWith("Block ")) {
                     if (level_server != null) {
                        value = value.replace(" keep", "");
                        if (GameUtils.Tile.fromText(level_server, value).isAir()) {
                           CustomPackOrganizing.Error.add(
                              "file", "settings file / unknown block IDs. This will results skipping them in mod systems.", file.getPath(), id + " > " + value
                           );
                           pass = false;
                        }
                     }
                  } else if (option.startsWith("Function ")) {
                     file_test = new File(Core.path_config + "/dev/temporary/" + value + ".txt");
                     if (!file_test.exists()) {
                        CustomPackOrganizing.Error.add(
                           "file", "settings file / unknown functions. This will results skipping them in mod systems.", file.getPath(), id + " > " + value
                        );
                        pass = false;
                     } else if (!testParse(level_server, file_test, true)) {
                        CustomPackOrganizing.Error.add(
                           "file", "settings files / functions is mark as incompatible. This will results skipping them in mod systems.", file.getPath(), id
                        );
                        pass = false;
                     }
                  }
               }
            }
         }

         return pass;
      }
   }
}
