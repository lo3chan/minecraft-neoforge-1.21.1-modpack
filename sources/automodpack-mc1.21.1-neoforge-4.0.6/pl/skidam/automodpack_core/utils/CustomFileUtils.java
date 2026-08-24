package pl.skidam.automodpack_core.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.stream.Stream;
import pl.skidam.automodpack_core.GlobalVariables;

public class CustomFileUtils {
   private static final Path CWD = Path.of(System.getProperty("user.dir"));

   public static void executeOrder66(Path file) {
      executeOrder66(file, true);
   }

   public static void executeOrder66(Path file, boolean saveDummyFiles) {
      try {
         Files.deleteIfExists(file);
      } catch (IOException var3) {
      }

      if (Files.isRegularFile(file)) {
         ClientCacheUtils.dummyIT(file);
         if (saveDummyFiles) {
            ClientCacheUtils.saveDummyFiles();
         }
      }
   }

   public static Path getPathFromCWD(String path) {
      return getPath(CWD, path);
   }

   public static Path getPath(Path origin, String path) {
      if (origin == null) {
         throw new IllegalArgumentException("Origin path must not be null");
      } else if (path != null && !path.isBlank()) {
         path = path.replace('\\', '/');
         if (path.startsWith("/")) {
            path = path.substring(1);
         }

         return origin.resolve(path).normalize();
      } else {
         return origin;
      }
   }

   public static boolean isFilePhysical(Path path) {
      return path.getFileSystem() == FileSystems.getDefault();
   }

   public static void copyFile(Path source, Path destination) throws IOException {
      setupFilePaths(destination);

      try (
         InputStream is = new LockFreeInputStream(source);
         OutputStream os = Files.newOutputStream(destination, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
      ) {
         is.transferTo(os);
      } catch (IOException var10) {
         GlobalVariables.LOGGER.error("Failed to copy a file from {} to {}", source, destination);
      }
   }

   public static void setupFilePaths(Path file) throws IOException {
      if (!Files.exists(file)) {
         if (!Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
         }

         file.toFile().createNewFile();
      }
   }

   public static boolean compareFilesByteByByte(Path path, byte[] referenceBytes) {
      try {
         if (Files.size(path) != referenceBytes.length) {
            return false;
         } else {
            boolean var9;
            try (InputStream is = new BufferedInputStream(new LockFreeInputStream(path))) {
               int i = 0;

               int b;
               while ((b = is.read()) != -1) {
                  if (b != (referenceBytes[i++] & 255)) {
                     return false;
                  }
               }

               var9 = true;
            }

            return var9;
         }
      } catch (Exception var8) {
         GlobalVariables.LOGGER.error("Error comparing file byte by byte: {}", path, var8);
         return false;
      }
   }

   public static String formatPath(Path modpackFile, Path modpackPath) {
      if (modpackPath != null && modpackFile != null) {
         String modpackFileStr = modpackFile.normalize().toString();
         String modpackFileStrAbs = modpackFile.toAbsolutePath().normalize().toString();
         String modpackPathStrAbs = modpackPath.toAbsolutePath().normalize().toString();
         String cwdStrAbs = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize().toString();
         String formattedFile = modpackFileStr;
         if (modpackFileStrAbs.startsWith(modpackPathStrAbs)) {
            formattedFile = modpackFileStrAbs.substring(modpackPathStrAbs.length());
         } else if (modpackFileStrAbs.startsWith(cwdStrAbs)) {
            formattedFile = modpackFileStrAbs.substring(cwdStrAbs.length());
         } else if (!modpackFileStrAbs.equals(modpackFileStr)) {
            GlobalVariables.LOGGER
               .error(
                  "File: {} ({}) is not in modpack directory: {} ({}) or current working directory: {}",
                  modpackFileStr,
                  modpackFileStrAbs,
                  modpackPath,
                  modpackPathStrAbs,
                  cwdStrAbs
               );
         }

         formattedFile = formattedFile.replace(File.separator, "/");
         return prefixSlash(formattedFile);
      } else {
         throw new IllegalArgumentException("Arguments are null - modpackPath: " + modpackPath + ", modpackFile: " + modpackFile);
      }
   }

   public static String prefixSlash(String path) {
      return !path.isEmpty() && path.charAt(0) == '/' ? path : "/" + path;
   }

   public static String getHash(Path path) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-1");

         try (
            InputStream is = new BufferedInputStream(new LockFreeInputStream(path));
            DigestInputStream dis = new DigestInputStream(is, digest);
         ) {
            dis.transferTo(OutputStream.nullOutputStream());
         }

         byte[] var12 = digest.digest();
         return HexFormat.of().formatHex(var12);
      } catch (IOException var10) {
      } catch (Exception var11) {
         GlobalVariables.LOGGER.error("Failed to get hash for path: {}", path, var11);
      }

      return null;
   }

   public static String getCurseforgeMurmurHash(Path file) throws IOException {
      if (!Files.exists(file)) {
         return null;
      } else {
         long length = 0L;
         ByteArrayOutputStream filteredStream = new ByteArrayOutputStream();

         int b;
         try (InputStream is = new BufferedInputStream(new LockFreeInputStream(file))) {
            while ((b = is.read()) != -1) {
               if (b != 9 && b != 10 && b != 13 && b != 32) {
                  filteredStream.write(b);
                  length++;
               }
            }
         }

         int var20 = 1540483477;
         byte var21 = 24;
         long k = 0L;
         byte seed = 1;
         byte shift = 0;
         long h = seed ^ length;
         byte[] filteredBytes = filteredStream.toByteArray();

         for (byte byteVal : filteredBytes) {
            char bx = (char)(byteVal & 255);
            k |= (long)bx << shift;
            shift += 8;
            if (shift == 32) {
               h = 4294967295L & h;
               k *= 1540483477L;
               k = 4294967295L & k;
               k ^= k >> 24;
               k = 4294967295L & k;
               k *= 1540483477L;
               k = 4294967295L & k;
               h *= 1540483477L;
               h = 4294967295L & h;
               h ^= k;
               h = 4294967295L & h;
               k = 0L;
               shift = 0;
            }
         }

         if (shift > 0) {
            h ^= k;
            h = 4294967295L & h;
            h *= 1540483477L;
            h = 4294967295L & h;
         }

         h ^= h >> 13;
         h = 4294967295L & h;
         h *= 1540483477L;
         h = 4294967295L & h;
         h ^= h >> 15;
         h = 4294967295L & h;
         return String.valueOf(h);
      }
   }

   public static boolean isEmptyDirectory(Path parentPath) throws IOException {
      if (!Files.isDirectory(parentPath)) {
         return false;
      } else {
         boolean var2;
         try (Stream<Path> pathStream = Files.list(parentPath)) {
            var2 = pathStream.findAny().isEmpty();
         }

         return var2;
      }
   }
}
