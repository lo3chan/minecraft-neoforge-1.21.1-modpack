/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.ModList
 *  net.neoforged.neoforgespi.language.ModFileScanData
 *  net.neoforged.neoforgespi.language.ModFileScanData$AnnotationData
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.objectweb.asm.Type
 */
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public final class ForgePluginFinder {
    private static final Logger LOGGER = LogManager.getLogger();

    private ForgePluginFinder() {
    }

    public static List<IModPlugin> getModPlugins() {
        return ForgePluginFinder.getInstances(JeiPlugin.class, IModPlugin.class);
    }

    private static <T> List<T> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
        Type annotationType = Type.getType(annotationClass);
        List allScanData = ModList.get().getAllScanData();
        LinkedHashSet<String> pluginClassNames = new LinkedHashSet<String>();
        for (ModFileScanData scanData : allScanData) {
            Set annotations = scanData.getAnnotations();
            for (ModFileScanData.AnnotationData a : annotations) {
                if (!Objects.equals(a.annotationType(), annotationType)) continue;
                String memberName = a.memberName();
                pluginClassNames.add(memberName);
            }
        }
        return ForgePluginFinder.getInstances(pluginClassNames, instanceClass);
    }

    static <T> List<T> getInstances(Iterable<String> pluginClassNames, Class<T> instanceClass) {
        ArrayList<T> instances = new ArrayList<T>();
        for (String className : pluginClassNames) {
            try {
                Class<?> asmClass = Class.forName(className);
                Class<T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                Constructor<T> constructor = asmInstanceClass.getDeclaredConstructor(new Class[0]);
                T instance = constructor.newInstance(new Object[0]);
                instances.add(instance);
            }
            catch (LinkageError | ReflectiveOperationException | RuntimeException e) {
                LOGGER.error("Failed to load: {}", (Object)className, (Object)e);
            }
        }
        return instances;
    }
}

