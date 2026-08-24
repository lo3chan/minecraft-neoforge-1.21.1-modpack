package de.cristelknight.cristellib.neoforge.extraapiutil;

import com.mojang.datafixers.util.Pair;
import de.cristelknight.cristellib.Constants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import org.objectweb.asm.Type;

public class APIFinder {
   public static <T> List<Pair<List<String>, T>> scanForAPIs(Class<?> annotationClazz, Class<T> returnClazz) {
      List<Pair<List<String>, T>> instances = new ArrayList<>();

      label40:
      for (ModFileScanData data : ModList.get().getAllScanData()) {
         List<AnnotationData> ebsTargets = data.getAnnotations()
            .stream()
            .filter(annotationData -> Type.getType(annotationClazz).equals(annotationData.annotationType()))
            .toList();
         List<String> modIds = data.getIModInfoData().stream().flatMap(info -> info.getMods().stream()).<String>map(IModInfo::getModId).toList();
         Iterator var7 = ebsTargets.iterator();

         while (true) {
            AnnotationData ad;
            Class<T> clazz;
            while (true) {
               if (!var7.hasNext()) {
                  continue label40;
               }

               ad = (AnnotationData)var7.next();

               try {
                  Class<?> clazz2 = Class.forName(ad.memberName());
                  if (returnClazz.isAssignableFrom(clazz2)) {
                     clazz = (Class<T>)clazz2;
                     break;
                  }

                  Constants.LOG.error("Failed to load api class {} for @{} annotation", ad.clazz().getClassName(), annotationClazz.getSimpleName());
               } catch (ClassNotFoundException var12) {
                  Constants.LOG.error("Failed to load api class {} for @{} annotation", ad.clazz().getClassName(), annotationClazz.getSimpleName(), var12);
               }
            }

            try {
               instances.add(new Pair(modIds, clazz.getDeclaredConstructor().newInstance()));
            } catch (Throwable var11) {
               Constants.LOG.error("Failed to load api: {}", ad.memberName(), var11);
            }
         }
      }

      return instances;
   }
}
