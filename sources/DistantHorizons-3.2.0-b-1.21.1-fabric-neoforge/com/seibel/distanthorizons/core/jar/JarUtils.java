package com.seibel.distanthorizons.core.jar;

import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModChecker;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class JarUtils {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   @Nullable
   public static File jarFile = null;

   public static URI accessFileURI(String resource) throws URISyntaxException {
      return Objects.requireNonNull(JarUtils.class.getResource(resource)).toURI();
   }

   public static InputStream accessFile(String resource) {
      ClassLoader loader = JarUtils.class.getClassLoader();
      InputStream input = loader.getResourceAsStream(resource);
      if (input == null) {
         input = loader.getResourceAsStream(resource);
      }

      return input;
   }

   public static String convertInputStreamToString(InputStream inputStream) {
      char[] buffer = new char[8192];
      StringBuilder result = new StringBuilder();

      try {
         Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

         int charsRead;
         try {
            while ((charsRead = reader.read(buffer, 0, buffer.length)) > 0) {
               result.append(buffer, 0, charsRead);
            }
         } catch (Throwable var7) {
            try {
               reader.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         reader.close();
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      return result.toString();
   }

   public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
      FileInputStream fis = new FileInputStream(file);
      byte[] byteArray = new byte[1024];
      int bytesCount = 0;

      while ((bytesCount = fis.read(byteArray)) != -1) {
         digest.update(byteArray, 0, bytesCount);
      }

      fis.close();
      byte[] bytes = digest.digest();
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < bytes.length; i++) {
         sb.append(Integer.toString((bytes[i] & 255) + 256, 16).substring(1));
      }

      return sb.toString();
   }

   static {
      try {
         URI jarUri = JarUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI();
         jarFile = new File(jarUri);
      } catch (Exception var3) {
         try {
            jarFile = SingletonInjector.INSTANCE.get(IModChecker.class).modLocation("distanthorizons");
         } catch (Exception var2) {
            LOGGER.warn("Unable to get jar file via URI or Mod Checker Location.");
            LOGGER.warn("URI Error: [" + var3.getMessage() + "]", var3);
            LOGGER.warn("Mod Location Error: [" + var2.getMessage() + "]", var2);
         }
      }
   }
}
