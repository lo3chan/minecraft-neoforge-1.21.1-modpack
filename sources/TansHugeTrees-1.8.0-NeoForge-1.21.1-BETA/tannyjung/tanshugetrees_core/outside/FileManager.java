package tannyjung.tanshugetrees_core.outside;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileManager {
   public static void createEmptyFile(String path, boolean is_directory) {
      File file = new File(path);
      if (!file.exists()) {
         try {
            if (is_directory) {
               file.mkdirs();
            } else {
               file.getParentFile().mkdirs();
               file.createNewFile();
            }
         } catch (Exception var4) {
            OutsideUtils.exception(new Exception(), var4, "");
         }
      }
   }

   public static List<File> getAllFiles(String path) {
      List<File> files = new ArrayList<>();

      try {
         Files.walk(Path.of(path)).forEach(source -> {
            if (!source.toFile().isDirectory()) {
               files.add(source.toFile());
            }
         });
      } catch (Exception var3) {
         OutsideUtils.exception(new Exception(), var3, "");
      }

      return files;
   }

   public static void rename(String path, String new_name) {
      File file = new File(path);
      file.renameTo(new File(file.getParentFile().getPath() + "/" + new_name));
   }

   public static void copy(String from, String to, boolean is_directory) {
      Path path_from = Path.of(from);
      Path path_to = Path.of(to);
      if (!is_directory) {
         createEmptyFile(path_to.getParent().toString(), true);

         try {
            Files.copy(path_from, path_to, StandardCopyOption.REPLACE_EXISTING);
         } catch (Exception var7) {
            OutsideUtils.exception(new Exception(), var7, "");
         }
      } else {
         try {
            Files.walk(path_from).forEach(source -> {
               if (!source.toFile().isDirectory()) {
                  Path path_new = path_to.resolve(path_from.relativize(source));
                  createEmptyFile(path_new.getParent().toString(), true);

                  try {
                     Files.copy(source, path_new, StandardCopyOption.REPLACE_EXISTING);
                  } catch (Exception var5) {
                     OutsideUtils.exception(new Exception(), var5, "");
                  }
               }
            });
         } catch (Exception var6) {
            OutsideUtils.exception(new Exception(), var6, "");
         }
      }
   }

   public static void delete(String path) {
      File file = new File(path);
      if (file.exists()) {
         try {
            Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).forEach(source -> source.toFile().delete());
         } catch (Exception var3) {
            OutsideUtils.exception(new Exception(), var3, "");
         }
      }
   }

   public static void writeTXT(String path, String write, boolean append) {
      File file = new File(path);
      createEmptyFile(file.getPath(), false);

      try {
         Writer writer = new FileWriter(file, append);
         BufferedWriter buffered_writer = new BufferedWriter(writer);
         buffered_writer.write(write);
         buffered_writer.close();
         writer.close();
      } catch (Exception var6) {
         OutsideUtils.exception(new Exception(), var6, "");
      }
   }

   public static String[] readTXT(String path) {
      File file = new File(path);
      if (file.exists()) {
         try {
            return Files.readAllLines(file.toPath()).toArray(new String[0]);
         } catch (Exception var3) {
            OutsideUtils.exception(new Exception(), var3, "");
         }
      }

      return new String[0];
   }

   public static void writeBIN(String path, List<String> write, boolean append) {
      createEmptyFile(path, false);
      if (write.size() > 0) {
         try {
            DataOutputStream file_bin = new DataOutputStream(new FileOutputStream(path, append));
            String type = "";
            String value = "";

            for (String scan : write) {
               type = scan.substring(0, 1);
               value = scan.substring(1);
               if (type.equals("b")) {
                  file_bin.writeByte(Byte.parseByte(value));
               } else if (type.equals("s")) {
                  file_bin.writeShort(Short.parseShort(value));
               } else if (type.equals("i")) {
                  file_bin.writeInt(Integer.parseInt(value));
               } else if (type.equals("l")) {
                  file_bin.writeBoolean(Boolean.parseBoolean(value));
               }
            }

            file_bin.close();
         } catch (Exception var8) {
            OutsideUtils.exception(new Exception(), var8, "");
         }
      }
   }

   public static ByteBuffer readBIN(String path) {
      File file = new File(path);
      if (file.exists()) {
         try {
            return ByteBuffer.wrap(Files.readAllBytes(file.toPath())).order(ByteOrder.BIG_ENDIAN);
         } catch (Exception var3) {
            OutsideUtils.exception(new Exception(), var3, "");
         }
      }

      return ByteBuffer.allocate(0);
   }

   public static void extractZIP(String path_zip, String path_folder, boolean skip_first_folder, String filter) {
      createEmptyFile(path_folder, true);

      try {
         ZipInputStream stream_input = new ZipInputStream(new FileInputStream(path_zip));
         ZipEntry entry = stream_input.getNextEntry();
         FileOutputStream stream_output = null;
         byte[] bytes = new byte[1024];
         int length = 0;
         String entry_path = "";

         for (int first_folder = 0; entry != null; entry = stream_input.getNextEntry()) {
            if (skip_first_folder && first_folder == 0) {
               first_folder = entry.getName().length();
            }

            entry_path = entry.getName().substring(first_folder);
            if (entry_path.contains(filter)) {
               if (entry.isDirectory()) {
                  createEmptyFile(path_folder + "/" + entry_path, true);
               } else {
                  createEmptyFile(path_folder + "/" + entry_path, false);
                  stream_output = new FileOutputStream(path_folder + "/" + entry_path);

                  while ((length = stream_input.read(bytes)) > 0) {
                     stream_output.write(bytes, 0, length);
                  }

                  stream_output.close();
               }
            }
         }

         stream_input.closeEntry();
         stream_input.close();
      } catch (Exception var11) {
         OutsideUtils.exception(new Exception(), var11, "");
      }
   }

   public static void compressZIP(String path_zip, File file) {
      createEmptyFile(path_zip, false);

      try {
         ZipOutputStream stream_output = new ZipOutputStream(new FileOutputStream(path_zip));
         byte[] bytes = new byte[1024];
         if (file.isDirectory()) {
            Files.walk(file.toPath()).forEach(source -> {
               if (!source.toFile().isDirectory()) {
                  try {
                     FileInputStream stream_inputx = new FileInputStream(source.toFile());
                     stream_output.putNextEntry(new ZipEntry(file.toPath().relativize(source).toString()));
                     int lengthx = 0;

                     while ((lengthx = stream_inputx.read(bytes)) >= 0) {
                        stream_output.write(bytes, 0, lengthx);
                     }

                     stream_inputx.close();
                  } catch (Exception var6x) {
                     OutsideUtils.exception(new Exception(), var6x, "");
                  }
               }
            });
         } else {
            FileInputStream stream_input = new FileInputStream(file);
            stream_output.putNextEntry(new ZipEntry(file.getName()));
            int length = 0;

            while ((length = stream_input.read(bytes)) >= 0) {
               stream_output.write(bytes, 0, length);
            }

            stream_input.close();
         }

         stream_output.close();
      } catch (Exception var6) {
         OutsideUtils.exception(new Exception(), var6, "");
      }
   }

   public static void mergeTXT(File file_from, File file_to) {
      if (!file_to.exists()) {
         copy(file_from.getPath(), file_to.getPath(), false);
      } else {
         String[] data_new = readTXT(file_from.getPath());
         if (data_new[0].equals("# REPLACE")) {
            StringBuilder write = new StringBuilder();
            boolean skip = true;

            for (String scan : data_new) {
               if (skip) {
                  skip = false;
               } else {
                  write.append(scan).append("\n");
               }
            }

            writeTXT(file_to.getPath(), write.toString(), false);
         } else {
            String[] data_old = readTXT(file_to.getPath());
            Map<String, String> data = new HashMap<>();
            String[] split = null;
            String[] split2 = null;

            for (String scanx : data_old) {
               if (!scanx.isEmpty() && !scanx.startsWith("#") && scanx.contains(" = ")) {
                  split = scanx.split(" = ");
                  data.put(split[0], split[1]);
               }
            }

            List<String> write_add = new ArrayList<>();
            StringBuilder merge = new StringBuilder();

            for (String scanxx : data_new) {
               if (!scanxx.isEmpty() && scanxx.contains(" = ")) {
                  if (!scanxx.startsWith("# MERGE")) {
                     if (scanxx.startsWith("# REPLACE")) {
                        split = scanxx.substring(scanxx.indexOf(" -> ") + 4).split(" = ");
                        if (data.containsKey(split[0])) {
                           split2 = data.get(split[1]).split(" >>> ");
                           write_add.add(split[0] + " = " + data.get(split[0]).replace(split2[0], split2[1]));
                        }
                     } else {
                        split = scanxx.split(" = ");
                        if (data.containsKey(split[0])) {
                           data.replace(split[0], split[1]);
                        } else {
                           write_add.add(scanxx);
                        }
                     }
                  } else {
                     split = scanxx.substring(scanxx.indexOf(" -> ") + 4).split(" = ");
                     if (!data.containsKey(split[0])) {
                        write_add.add(split[0] + " = " + split[1]);
                     } else {
                        merge.setLength(0);
                        merge.append("|").append(data.get(split[0]).replace(" / ", "| / |")).append("|");

                        for (String scan_list : split[1].split(" / ")) {
                           if (!merge.toString().contains("|" + scan_list + "|")) {
                              merge.append(" / ").append(scan_list);
                           }
                        }

                        data.put(split[0], merge.toString().replace("|", ""));
                     }
                  }
               }
            }

            List<String> write = new ArrayList<>();

            for (String scanxxx : data_old) {
               if (!scanxxx.contains(" = ")) {
                  write.add(scanxxx);
               } else {
                  if (scanxxx.startsWith("# MERGE")) {
                     scanxxx = scanxxx.substring(scanxxx.indexOf(" -> ") + 4);
                  }

                  split = scanxxx.split(" = ");
                  write.add(split[0] + " = " + data.get(split[0]));
               }
            }

            write.addAll(write_add);
            writeTXT(file_to.getPath(), String.join("\n", write), false);
         }
      }
   }
}
