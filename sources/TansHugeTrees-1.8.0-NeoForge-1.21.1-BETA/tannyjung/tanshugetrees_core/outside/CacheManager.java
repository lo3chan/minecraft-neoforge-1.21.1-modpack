package tannyjung.tanshugetrees_core.outside;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import tannyjung.tanshugetrees_core.Core;

public class CacheManager {
   public static String clear() {
      int size = 0;
      size += CacheManager.DataLogic.clear();
      size += CacheManager.DataText.clear();
      size += CacheManager.DataShort.clear();
      size += CacheManager.DataInt.clear();
      if (size < 1024) {
         return size + " B";
      } else {
         return size < 1048576
            ? OutsideUtils.Mathematics.shorterDouble(size / 1024.0, 2) + " KB"
            : OutsideUtils.Mathematics.shorterDouble(size / 1048576.0, 2) + " MB";
      }
   }

   public static String[] getFunction(String path) {
      String[] data = CacheManager.DataText.getArray("functions").get(path);
      if (data == null) {
         data = FileManager.readTXT(Core.path_config + "/dev/temporary/" + path + ".txt");
         CacheManager.DataText.setArray("functions", path, data);
      }

      return data;
   }

   public static String getDictionary(String key, boolean is_number) {
      String get = CacheManager.DataText.getNormal("dictionary").get(key);
      if (get == null) {
         String value_id = "";
         String value_text = "";
         String path = Core.path_world_mod + "/dictionary.txt";
         String[] data = FileManager.readTXT(path);

         for (String scan : data) {
            if (is_number) {
               if (scan.startsWith(key + "|")) {
                  value_id = key;
                  value_text = scan.substring(scan.indexOf("|") + 1);
                  break;
               }
            } else if (scan.endsWith("|" + key)) {
               value_id = scan.substring(0, scan.indexOf("|"));
               value_text = key;
               break;
            }
         }

         if (value_id.isEmpty() && value_text.isEmpty()) {
            if (!is_number) {
               value_text = key;
            }

            if (!value_text.isEmpty()) {
               value_id = String.valueOf(data.length + 1);
               FileManager.writeTXT(path, value_id + "|" + value_text + "\n", true);
            }
         }

         CacheManager.DataText.setNormal("dictionary", value_id, value_text);
         CacheManager.DataText.setNormal("dictionary", value_text, value_id);
         if (is_number) {
            get = value_text;
         } else {
            get = value_id;
         }
      }

      return get;
   }

   public static class DataInt {
      private static final Map<String, Map<String, Integer>> normal = new HashMap<>();
      private static final Map<String, Map<String, int[]>> array = new HashMap<>();
      private static final Map<String, Map<String, Set<Integer>>> set = new HashMap<>();
      private static final Map<String, Map<String, List<Integer>>> list = new HashMap<>();
      private static final Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
      private static final Object lock_normal = new Object();
      private static final Object lock_array = new Object();
      private static final Object lock_set = new Object();
      private static final Object lock_list = new Object();
      private static final Object lock_map = new Object();

      private static int clear() {
         int size = 0;
         synchronized (lock_normal) {
            for (Entry<String, Map<String, Integer>> entry : normal.entrySet()) {
               size += entry.getValue().size() * 4;
            }

            normal.clear();
         }

         synchronized (lock_array) {
            for (Entry<String, Map<String, int[]>> entry1 : array.entrySet()) {
               for (Entry<String, int[]> entry2 : entry1.getValue().entrySet()) {
                  size += ((int[])entry2.getValue()).length * 4;
               }
            }

            array.clear();
         }

         synchronized (lock_set) {
            for (Entry<String, Map<String, Set<Integer>>> entry1 : set.entrySet()) {
               for (Entry<String, Set<Integer>> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().size() * 4;
               }
            }

            set.clear();
         }

         synchronized (lock_list) {
            for (Entry<String, Map<String, List<Integer>>> entry1 : list.entrySet()) {
               for (Entry<String, List<Integer>> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().size() * 4;
               }
            }

            list.clear();
         }

         synchronized (lock_map) {
            for (Entry<String, Map<String, Map<String, Integer>>> entry1 : map.entrySet()) {
               for (Entry<String, Map<String, Integer>> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().size() * 4;
               }
            }

            map.clear();
            return size;
         }
      }

      public static boolean existNormal(String name) {
         synchronized (lock_normal) {
            return normal.containsKey(name);
         }
      }

      public static Map<String, Integer> getNormal(String name) {
         synchronized (lock_normal) {
            return normal.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setNormal(String name, String key, int value) {
         synchronized (lock_normal) {
            if (key == null) {
               normal.put(name, new HashMap<>());
            } else {
               normal.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, int[]> getArray(String name) {
         synchronized (lock_array) {
            return array.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setArray(String name, String key, int[] value) {
         synchronized (lock_array) {
            if (key == null) {
               array.put(name, new HashMap<>());
            } else {
               array.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, Set<Integer>> getSet(String name) {
         synchronized (lock_set) {
            return set.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setSet(String name, String key, Set<Integer> value) {
         synchronized (lock_set) {
            if (key == null) {
               set.put(name, new HashMap<>());
            } else {
               set.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, List<Integer>> getList(String name) {
         synchronized (lock_list) {
            return list.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setList(String name, String key, List<Integer> value) {
         synchronized (lock_list) {
            if (key == null) {
               list.put(name, new HashMap<>());
            } else {
               list.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, Map<String, Integer>> getMap(String name) {
         synchronized (lock_map) {
            return map.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setMap(String name, String key, Map<String, Integer> value) {
         synchronized (lock_map) {
            if (key == null) {
               map.put(name, new HashMap<>());
            } else {
               map.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }
   }

   public static class DataLogic {
      private static final Map<String, Map<String, Boolean>> normal = new HashMap<>();
      private static final Map<String, Map<String, boolean[]>> array = new HashMap<>();
      private static final Map<String, Map<String, List<Boolean>>> list = new HashMap<>();
      private static final Map<String, Map<String, Map<String, Boolean>>> map = new HashMap<>();
      private static final Object lock_normal = new Object();
      private static final Object lock_array = new Object();
      private static final Object lock_list = new Object();
      private static final Object lock_map = new Object();

      private static int clear() {
         int size = 0;
         synchronized (lock_normal) {
            for (Entry<String, Map<String, Boolean>> entry : normal.entrySet()) {
               size += entry.getValue().size();
            }

            normal.clear();
         }

         synchronized (lock_array) {
            for (Entry<String, Map<String, boolean[]>> entry1 : array.entrySet()) {
               for (Entry<String, boolean[]> entry2 : entry1.getValue().entrySet()) {
                  size += ((boolean[])entry2.getValue()).length;
               }
            }

            array.clear();
         }

         synchronized (lock_list) {
            for (Entry<String, Map<String, List<Boolean>>> entry1 : list.entrySet()) {
               for (Entry<String, List<Boolean>> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().size();
               }
            }

            list.clear();
         }

         synchronized (lock_map) {
            for (Entry<String, Map<String, Map<String, Boolean>>> entry1 : map.entrySet()) {
               for (Entry<String, Map<String, Boolean>> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().size();
               }
            }

            map.clear();
            return size;
         }
      }

      public static boolean existNormal(String name, String key) {
         synchronized (lock_normal) {
            return normal.getOrDefault(name, new HashMap<>()).containsKey(key);
         }
      }

      public static Map<String, Boolean> getNormal(String name) {
         synchronized (lock_normal) {
            return normal.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setNormal(String name, String key, boolean value) {
         synchronized (lock_normal) {
            if (key == null) {
               normal.put(name, new HashMap<>());
            } else {
               normal.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, boolean[]> getArray(String name) {
         synchronized (lock_array) {
            return array.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setArray(String name, String key, boolean[] value) {
         synchronized (lock_array) {
            if (key == null) {
               array.put(name, new HashMap<>());
            } else {
               array.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, List<Boolean>> getList(String name) {
         synchronized (lock_list) {
            return list.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setList(String name, String key, List<Boolean> value) {
         synchronized (lock_list) {
            if (key == null) {
               list.put(name, new HashMap<>());
            } else {
               list.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, Map<String, Boolean>> getMap(String name) {
         synchronized (lock_map) {
            return map.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setMap(String name, String key, Map<String, Boolean> value) {
         synchronized (lock_map) {
            if (key == null) {
               map.put(name, new HashMap<>());
            } else {
               map.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }
   }

   public static class DataShort {
      private static final Map<String, Map<String, Short>> normal = new HashMap<>();
      private static final Map<String, Map<String, short[]>> array = new HashMap<>();
      private static final Map<String, Map<String, Set<Short>>> set = new HashMap<>();
      private static final Map<String, Map<String, List<Short>>> list = new HashMap<>();
      private static final Map<String, Map<String, Map<String, Short>>> map = new HashMap<>();
      private static final Object lock_normal = new Object();
      private static final Object lock_array = new Object();
      private static final Object lock_set = new Object();
      private static final Object lock_list = new Object();
      private static final Object lock_map = new Object();

      private static int clear() {
         int size = 0;
         synchronized (lock_normal) {
            for (Entry<String, Map<String, Short>> entry : normal.entrySet()) {
               size += entry.getValue().size() * 2;
            }

            normal.clear();
         }

         synchronized (lock_array) {
            for (Entry<String, Map<String, short[]>> entry1 : array.entrySet()) {
               for (Entry<String, short[]> entry2 : entry1.getValue().entrySet()) {
                  size += ((short[])entry2.getValue()).length * 2;
               }
            }

            array.clear();
         }

         synchronized (lock_set) {
            for (Entry<String, Map<String, Set<Short>>> entry1 : set.entrySet()) {
               for (Entry<String, Set<Short>> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().size() * 2;
               }
            }

            set.clear();
         }

         synchronized (lock_list) {
            for (Entry<String, Map<String, List<Short>>> entry1 : list.entrySet()) {
               for (Entry<String, List<Short>> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().size() * 2;
               }
            }

            list.clear();
         }

         synchronized (lock_map) {
            for (Entry<String, Map<String, Map<String, Short>>> entry1 : map.entrySet()) {
               for (Entry<String, Map<String, Short>> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().size() * 2;
               }
            }

            map.clear();
            return size;
         }
      }

      public static boolean existNormal(String name) {
         synchronized (lock_normal) {
            return normal.containsKey(name);
         }
      }

      public static Map<String, Short> getNormal(String name) {
         synchronized (lock_normal) {
            return normal.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setNormal(String name, String key, short value) {
         synchronized (lock_normal) {
            if (key == null) {
               normal.put(name, new HashMap<>());
            } else {
               normal.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, short[]> getArray(String name) {
         synchronized (lock_array) {
            return array.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setArray(String name, String key, short[] value) {
         synchronized (lock_array) {
            if (key == null) {
               array.put(name, new HashMap<>());
            } else {
               array.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, Set<Short>> getSet(String name) {
         synchronized (lock_set) {
            return set.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setSet(String name, String key, Set<Short> value) {
         synchronized (lock_set) {
            if (key == null) {
               set.put(name, new HashMap<>());
            } else {
               set.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, List<Short>> getList(String name) {
         synchronized (lock_list) {
            return list.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setList(String name, String key, List<Short> value) {
         synchronized (lock_list) {
            if (key == null) {
               list.put(name, new HashMap<>());
            } else {
               list.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, Map<String, Short>> getMap(String name) {
         synchronized (lock_map) {
            return map.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setMap(String name, String key, Map<String, Short> value) {
         synchronized (lock_map) {
            if (key == null) {
               map.put(name, new HashMap<>());
            } else {
               map.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }
   }

   public static class DataText {
      private static final Map<String, Map<String, String>> normal = new HashMap<>();
      private static final Map<String, Map<String, String[]>> array = new HashMap<>();
      private static final Map<String, Map<String, Set<String>>> set = new HashMap<>();
      private static final Map<String, Map<String, List<String>>> list = new HashMap<>();
      private static final Map<String, Map<String, Map<String, String>>> map = new HashMap<>();
      private static final Object lock_normal = new Object();
      private static final Object lock_array = new Object();
      private static final Object lock_set = new Object();
      private static final Object lock_list = new Object();
      private static final Object lock_map = new Object();

      private static int clear() {
         int size = 0;
         synchronized (lock_normal) {
            for (Entry<String, Map<String, String>> entry1 : normal.entrySet()) {
               for (Entry<String, String> entry2 : entry1.getValue().entrySet()) {
                  size += entry2.getValue().length() * 2;
               }
            }

            normal.clear();
         }

         synchronized (lock_array) {
            for (Entry<String, Map<String, String[]>> entry1 : array.entrySet()) {
               for (Entry<String, String[]> entry2 : entry1.getValue().entrySet()) {
                  for (String scan : entry2.getValue()) {
                     size += scan.length() * 2;
                  }
               }
            }

            array.clear();
         }

         synchronized (lock_set) {
            for (Entry<String, Map<String, Set<String>>> entry1 : set.entrySet()) {
               for (Entry<String, Set<String>> entry2 : entry1.getValue().entrySet()) {
                  for (String scan : entry2.getValue()) {
                     size += scan.length() * 2;
                  }
               }
            }

            set.clear();
         }

         synchronized (lock_list) {
            for (Entry<String, Map<String, List<String>>> entry1 : list.entrySet()) {
               for (Entry<String, List<String>> entry2 : entry1.getValue().entrySet()) {
                  for (String scan : entry2.getValue()) {
                     size += scan.length() * 2;
                  }
               }
            }

            list.clear();
         }

         synchronized (lock_map) {
            for (Entry<String, Map<String, Map<String, String>>> entry1 : map.entrySet()) {
               for (Entry<String, Map<String, String>> entry2 : entry1.getValue().entrySet()) {
                  for (Entry<String, String> entry3 : entry2.getValue().entrySet()) {
                     size += entry3.getValue().length() * 2;
                  }
               }
            }

            map.clear();
            return size;
         }
      }

      public static boolean existNormal(String name, String key) {
         synchronized (lock_normal) {
            return normal.getOrDefault(name, new HashMap<>()).containsKey(key);
         }
      }

      public static Map<String, String> getNormal(String name) {
         synchronized (lock_normal) {
            return normal.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setNormal(String name, String key, String value) {
         synchronized (lock_normal) {
            if (key == null) {
               normal.put(name, new HashMap<>());
            } else {
               normal.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, String[]> getArray(String name) {
         synchronized (lock_array) {
            return array.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setArray(String name, String key, String[] value) {
         synchronized (lock_array) {
            if (key == null) {
               array.put(name, new HashMap<>());
            } else {
               array.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, Set<String>> getSet(String name) {
         synchronized (lock_set) {
            return set.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setSet(String name, String key, Set<String> value) {
         synchronized (lock_set) {
            if (key == null) {
               set.put(name, new HashMap<>());
            } else {
               set.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, List<String>> getList(String name) {
         synchronized (lock_list) {
            return list.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setList(String name, String key, List<String> value) {
         synchronized (lock_list) {
            if (key == null) {
               list.put(name, new HashMap<>());
            } else {
               list.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }

      public static Map<String, Map<String, String>> getMap(String name) {
         synchronized (lock_map) {
            return map.getOrDefault(name, new HashMap<>());
         }
      }

      public static void setMap(String name, String key, Map<String, String> value) {
         synchronized (lock_map) {
            if (key == null) {
               map.put(name, new HashMap<>());
            } else {
               map.computeIfAbsent(name, create -> new HashMap<>()).put(key, value);
            }
         }
      }
   }
}
