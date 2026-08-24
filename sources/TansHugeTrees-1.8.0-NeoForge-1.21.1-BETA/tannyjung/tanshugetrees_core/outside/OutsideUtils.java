package tannyjung.tanshugetrees_core.outside;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import tannyjung.tanshugetrees_core.Core;

public class OutsideUtils {
   public static void exception(Exception from, Exception exception, String details) {
      Core.logger.error("----------------------------------------------------------------------------------------------------");
      StackTraceElement from_get = from.getStackTrace()[0];
      Core.logger.error("Found an error reported from {} -> {} -> {}", from_get.getClassName(), from_get.getMethodName(), from_get.getLineNumber());
      if (!details.isEmpty()) {
         for (String get : details.split(" / ")) {
            Core.logger.error(get);
         }
      }

      Core.logger.error(exception.getMessage());

      for (StackTraceElement get : exception.getStackTrace()) {
         if (get.toString().contains("tannyjung")) {
            Core.logger.error(get);
         }
      }

      Core.logger.error("----------------------------------------------------------------------------------------------------");
   }

   public static String testVersion(String version_main, String version) {
      if (version_main.equals(version)) {
         return "same";
      } else {
         String[] main_split = version_main.split("\\.");
         String[] split = version.split("\\.");
         int main_split1 = 0;
         int main_split2 = 0;
         int main_split3 = 0;
         int split1 = 0;
         int split2 = 0;
         int split3 = 0;

         try {
            main_split1 = Integer.parseInt(main_split[0]);
            main_split2 = Integer.parseInt(main_split[1]);
            main_split3 = Integer.parseInt(main_split[2]);
            split1 = Integer.parseInt(split[0]);
            split2 = Integer.parseInt(split[1]);
            split3 = Integer.parseInt(split[2]);
         } catch (Exception var11) {
            return "outdated";
         }

         return main_split1 >= split1 && main_split2 >= split2 && main_split3 >= split3 ? "early" : "outdated";
      }
   }

   public static boolean isURLAvailable(String url) {
      try {
         HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
         connection.setRequestMethod("HEAD");
         connection.setConnectTimeout(5000);
         connection.setReadTimeout(5000);
         connection.connect();
         return true;
      } catch (Exception var2) {
         exception(new Exception(), var2, "");
         return false;
      }
   }

   public static boolean download(String url, String to) {
      FileManager.createEmptyFile(to, false);

      try {
         boolean var6;
         try (FileOutputStream output = new FileOutputStream(to)) {
            BufferedInputStream input = new BufferedInputStream(new URI(url).toURL().openStream());
            byte[] buffer = new byte[1024];

            int bytesRead;
            while ((bytesRead = input.read(buffer, 0, 1024)) != -1) {
               output.write(buffer, 0, bytesRead);
            }

            FileManager.delete(to);
            var6 = true;
         }

         return var6;
      } catch (Exception var9) {
         exception(new Exception(), var9, "");
         return false;
      }
   }

   public static BlockPos convertPosRotationMirrored(BlockPos pos, int[] rotation_mirrored) {
      int posX = pos.getX();
      int posZ = pos.getZ();
      int save_posX = posX;
      if (rotation_mirrored[0] == 2) {
         posX = posZ;
         posZ = -save_posX;
      } else if (rotation_mirrored[0] == 3) {
         posX = -posX;
         posZ = -posZ;
      } else if (rotation_mirrored[0] == 4) {
         posX = -posZ;
         posZ = save_posX;
      }

      if (rotation_mirrored[1] == 1) {
         posX = -posX;
      } else if (rotation_mirrored[1] == 2) {
         posZ = -posZ;
      }

      return new BlockPos(posX, pos.getY(), posZ);
   }

   public static BlockPos convertPosFallen(BlockPos pos, int fallen_direction) {
      int posX = pos.getX();
      int posY = pos.getY();
      int posZ = pos.getZ();
      int posY_save = posY;
      if (fallen_direction == 1) {
         posY = posX;
         posX = posY_save;
      } else if (fallen_direction == 2) {
         posY = posZ;
         posZ = posY_save;
      } else if (fallen_direction == 3) {
         posY = posX;
         posX = -posY_save;
      } else if (fallen_direction == 4) {
         posY = posZ;
         posZ = -posY_save;
      }

      return new BlockPos(posX, posY, posZ);
   }

   public static int[] convertSizeRotationMirrored(int[] rotation_mirrored, int sizeX, int sizeZ, int center_sizeX, int center_sizeZ) {
      int save_sizeX = sizeX;
      int save_sizeZ = sizeZ;
      int save_center_sizeX = center_sizeX;
      if (rotation_mirrored[0] == 2) {
         sizeX = sizeZ;
         sizeZ = save_sizeX;
         center_sizeX = center_sizeZ;
         center_sizeZ = save_sizeX - save_center_sizeX;
      } else if (rotation_mirrored[0] == 3) {
         center_sizeX = sizeX - center_sizeX;
         center_sizeZ = sizeZ - center_sizeZ;
      } else if (rotation_mirrored[0] == 4) {
         sizeX = sizeZ;
         sizeZ = save_sizeX;
         center_sizeX = save_sizeZ - center_sizeZ;
         center_sizeZ = save_center_sizeX;
      }

      if (rotation_mirrored[1] == 1) {
         center_sizeX = sizeX - center_sizeX;
      } else if (rotation_mirrored[1] == 2) {
         center_sizeZ = sizeZ - center_sizeZ;
      }

      return new int[]{sizeX, sizeZ, center_sizeX, center_sizeZ};
   }

   public static int[] convertSizeFallen(int fallen_direction, int sizeX, int sizeY, int sizeZ, int center_sizeX, int center_sizeY, int center_sizeZ) {
      int save_sizeX = sizeX;
      int save_sizeY = sizeY;
      int save_sizeZ = sizeZ;
      int save_center_sizeY = center_sizeY;
      if (fallen_direction == 1) {
         sizeY = sizeX;
         sizeX = save_sizeY;
         center_sizeY = save_sizeX - center_sizeX;
         center_sizeX = save_center_sizeY;
      } else if (fallen_direction == 2) {
         sizeY = sizeZ;
         sizeZ = save_sizeY;
         center_sizeY = save_sizeZ - center_sizeZ;
         center_sizeZ = save_center_sizeY;
      } else if (fallen_direction == 3) {
         sizeY = sizeX;
         sizeX = save_sizeY;
         center_sizeY = center_sizeX;
         center_sizeX = save_sizeY - save_center_sizeY;
      } else if (fallen_direction == 4) {
         sizeY = sizeZ;
         sizeZ = save_sizeY;
         center_sizeY = center_sizeZ;
         center_sizeZ = save_sizeY - save_center_sizeY;
      }

      return new int[]{sizeX, sizeY, sizeZ, center_sizeX, center_sizeY, center_sizeZ};
   }

   public static String convertRegionQuadtree(ChunkPos chunk_pos, int level) {
      StringBuilder write = new StringBuilder();
      int localX = chunk_pos.x & 31;
      int localZ = chunk_pos.z & 31;

      for (int step = 1; step <= level; step++) {
         int size = 32 >> step;
         int posX = localX / size % 2;
         int posZ = localZ / size % 2;
         if (posX == 0 && posZ == 0) {
            write.append("/NW");
         } else if (posX == 1 && posZ == 0) {
            write.append("/NE");
         } else if (posX == 0) {
            write.append("/SW");
         } else {
            write.append("/SE");
         }
      }

      return write.substring(1);
   }

   public static String[] readOnlineTXT(String url) {
      if (isURLAvailable(url)) {
         try {
            List<String> data = new ArrayList<>();
            BufferedReader buffered_reader = new BufferedReader(new InputStreamReader(new URI(url).toURL().openStream()), 65536);
            String scan = "";

            while ((scan = buffered_reader.readLine()) != null) {
               data.add(scan);
            }

            buffered_reader.close();
            return data.toArray(new String[0]);
         } catch (Exception var4) {
            exception(new Exception(), var4, "");
         }
      }

      return new String[0];
   }

   public static Map<String, String> convertFileToDataMap(String path) {
      Map<String, String> data = new HashMap<>();
      String[] split = null;

      for (String scan : FileManager.readTXT(path)) {
         if (!scan.isEmpty() && scan.contains(" = ")) {
            split = scan.split(" = ");
            if (split[1].equals("none")) {
               split[1] = "";
            }

            data.put(split[0], split[1]);
         }
      }

      return data;
   }

   public static class Data {
      public static byte[] convertShortToArrayByte(short value) {
         return ByteBuffer.allocate(2).putShort(value).array();
      }

      public static byte[] convertIntToArrayByte(int value) {
         return ByteBuffer.allocate(4).putInt(value).array();
      }

      public static byte[] convertDoubleToArrayByte(double value) {
         return ByteBuffer.allocate(8).putDouble(value).array();
      }

      public static short[] convertListShortToArrayShort(List<Short> data) {
         short[] convert = new short[data.size()];

         for (int scan = 0; scan < convert.length; scan++) {
            convert[scan] = data.get(scan);
         }

         return convert;
      }

      public static int[] convertListIntToArrayInt(List<Integer> data) {
         int[] convert = new int[data.size()];

         for (int scan = 0; scan < convert.length; scan++) {
            convert[scan] = data.get(scan);
         }

         return convert;
      }
   }

   public static class Mathematics {
      public static boolean isNumberStartWith(int number, int test) {
         int base = 1;

         while (base <= test) {
            base *= 10;
         }

         while (number >= base) {
            number /= 10;
         }

         return number == test;
      }

      public static boolean isNumberEndWith(int number, int test) {
         int base = 1;

         while (base * 10 <= test) {
            base *= 10;
         }

         return number % base == test;
      }

      public static double shorterDouble(double number, int decimal) {
         double test = Math.pow(10.0, decimal);
         return Math.ceil(number * test) / test;
      }
   }
}
