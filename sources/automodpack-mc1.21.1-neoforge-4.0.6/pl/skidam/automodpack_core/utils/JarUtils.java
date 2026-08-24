package pl.skidam.automodpack_core.utils;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.security.CodeSource;
import pl.skidam.automodpack_core.GlobalVariables;

public class JarUtils {
   public static Path getJarPath(Class<?> clazz) {
      try {
         CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
         if (codeSource != null && codeSource.getLocation() != null) {
            Path path = Path.of(codeSource.getLocation().toURI());
            return resolvePhysicalPath(path);
         } else {
            throw new IllegalStateException("CodeSource is null for " + clazz.getSimpleName());
         }
      } catch (Exception var3) {
         throw new RuntimeException("Failed to determine JAR path for " + clazz.getSimpleName(), var3);
      }
   }

   private static Path resolvePhysicalPath(Path path) {
      try {
         Object fs = path.getFileSystem();
         Method method = fs.getClass().getMethod("getPrimaryPath");
         Object result = method.invoke(fs);
         if (result instanceof Path) {
            return (Path)result;
         }
      } catch (NoSuchMethodException var4) {
      } catch (Exception var5) {
         GlobalVariables.LOGGER.error("Failed to resolve physical path for {}", path, var5);
      }

      return path;
   }
}
