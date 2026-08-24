package mezz.jei.neoforge.startup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public final class ForgePluginFinder {
   private static final Logger LOGGER = LogManager.getLogger();

   private ForgePluginFinder() {
   }

   public static List<IModPlugin> getModPlugins() {
      return getInstances(JeiPlugin.class, IModPlugin.class);
   }

   private static <T> List<T> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
      Type annotationType = Type.getType(annotationClass);
      List<ModFileScanData> allScanData = ModList.get().getAllScanData();
      Set<String> pluginClassNames = new LinkedHashSet<>();

      for (ModFileScanData scanData : allScanData) {
         for (AnnotationData a : scanData.getAnnotations()) {
            if (Objects.equals(a.annotationType(), annotationType)) {
               String memberName = a.memberName();
               pluginClassNames.add(memberName);
            }
         }
      }

      return getInstances(pluginClassNames, instanceClass);
   }

   static <T> List<T> getInstances(Iterable<String> pluginClassNames, Class<T> instanceClass) {
      List<T> instances = new ArrayList<>();

      for (String className : pluginClassNames) {
         try {
            Class<?> asmClass = Class.forName(className);
            Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
            Constructor<? extends T> constructor = asmInstanceClass.getDeclaredConstructor();
            T instance = (T)constructor.newInstance();
            instances.add(instance);
         } catch (RuntimeException | LinkageError | ReflectiveOperationException var9) {
            LOGGER.error("Failed to load: {}", className, var9);
         }
      }

      return instances;
   }
}
