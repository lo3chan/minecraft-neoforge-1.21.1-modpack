package net.raphimc.immediatelyfast.util;

import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

public class ServiceUtil {
   public static <T> T load(Class<T> service) {
      List<Provider<T>> providers = ServiceLoader.load(service).stream().toList();
      if (providers.isEmpty()) {
         throw new IllegalStateException("No implementation found for " + service.getName());
      } else if (providers.size() > 1) {
         throw new IllegalStateException(
            "Multiple implementations found for " + service.getName() + ": " + providers.stream().map(p -> p.type().getName()).toList()
         );
      } else {
         return (T)((Provider)providers.getFirst()).get();
      }
   }
}
