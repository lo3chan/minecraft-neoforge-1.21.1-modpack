package tannyjung.tanshugetrees_core.outside;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tannyjung.tanshugetrees_core.Core;

public class ConfigClassic {
   public static void repair(String path, String original) {
      Map<String, Boolean> should_keep = new HashMap<>();
      Map<String, String> old_values = new HashMap<>();
      List<String> options = new ArrayList<>();
      List<String> values = new ArrayList<>();
      List<String> defaults = new ArrayList<>();
      String[] split = null;

      for (String scan : FileManager.readTXT(path)) {
         if (scan.contains(" = ")) {
            split = scan.split(" = ");
            options.add(split[0]);
            values.add(split[1]);
            old_values.put(split[0], split[1]);
         } else if (scan.startsWith("| Default is ")) {
            scan = scan.substring("| Default is ".length());
            scan = scan.substring(2, scan.length() - 2);
            defaults.addAll(Arrays.asList(scan.split(" ] \\[ ")));

            while (defaults.size() > values.size()) {
               options.add("");
               values.add("");
            }
         }
      }

      boolean test = false;

      for (int loop = 0; loop < options.size(); loop++) {
         boolean var22;
         if (defaults.get(loop).isEmpty()) {
            var22 = false;
         } else {
            var22 = !values.get(loop).equals(defaults.get(loop));
         }

         should_keep.put(options.get(loop), var22);
      }

      StringBuilder write = new StringBuilder();
      write.append("Important Notes");
      write.append("\n");
      write.append("\n");
      write.append("- To apply this config and repair missing values, run this command [ /").append(Core.mod_id_big).append("restart ] or restart the world.");
      write.append("\n");
      write.append(
         "- This config can be automatic repair itself to keep your unchanged values up to date, so don't panic when you see option values change by themselves!"
      );
      write.append("\n");
      String option = "";
      String value = "";
      split = null;
      List<String> written_values = new ArrayList<>();

      for (String scanx : original.split("\\n")) {
         if (!scanx.startsWith("|") && scanx.contains(" = ")) {
            split = scanx.split(" = ");
            option = split[0];
            value = split[1];
            if (old_values.containsKey(option)) {
               if (should_keep.get(option)) {
                  write.append(option).append(" = ").append(old_values.get(option));
               } else {
                  write.append(scanx);
               }
            } else {
               write.append(scanx);
            }

            written_values.add(value);
         } else if (scanx.isEmpty() && written_values.size() > 0) {
            write.append(scanx).append("| Default is");

            for (String get : written_values) {
               write.append(" [ ").append(get).append(" ]");
            }

            write.append("\n");
            written_values.clear();
         } else {
            write.append(scanx);
         }

         write.append("\n");
      }

      FileManager.writeTXT(path, write.toString(), false);
   }

   public static Map<String, String> getValues(String path) {
      Map<String, String> data = new HashMap<>();
      int index = 0;

      for (String scan : FileManager.readTXT(path)) {
         if (!scan.isEmpty() && scan.contains(" = ")) {
            index = scan.indexOf(" = ");
            data.put(scan.substring(0, index), scan.substring(index + 3));
         }
      }

      return data;
   }
}
