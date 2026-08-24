package de.cristelknight.cristellib.config.client.extension;

import de.cristelknight.cristellib.config.client.extension.extensions.SimpleConfigExtension;
import de.cristelknight.cristellib.config.client.extension.extensions.StructureConfigExtension;
import de.cristelknight.cristellib.config.client.simple.ClientConfigRegistry;
import java.util.HashMap;
import java.util.Map;

public class ExtensionRegistry {
   private static final Map<ExtensionRegistry.ExtensionFactory<?>, ExtensionRegistry.LoadPredicate> EXTENSIONS = new HashMap<>();

   public static Map<ExtensionRegistry.ExtensionFactory<?>, ExtensionRegistry.LoadPredicate> getExtensions() {
      return EXTENSIONS;
   }

   public static void registerConfigScreenExtension(ExtensionRegistry.ExtensionFactory<?> extension) {
      registerConfigScreenExtension(extension, modId -> true);
   }

   public static void registerConfigScreenExtension(ExtensionRegistry.ExtensionFactory<?> extensionFactory, ExtensionRegistry.LoadPredicate predicate) {
      EXTENSIONS.put(extensionFactory, predicate);
   }

   static {
      registerConfigScreenExtension(StructureConfigExtension::new, StructureConfigExtension.SHOULD_LOAD);
      registerConfigScreenExtension(SimpleConfigExtension::new, ClientConfigRegistry::hasScreens);
   }

   @FunctionalInterface
   public interface ExtensionFactory<T extends ConfigScreenExtension> {
      T create(String var1);
   }

   @FunctionalInterface
   public interface LoadPredicate {
      boolean test(String var1);
   }
}
