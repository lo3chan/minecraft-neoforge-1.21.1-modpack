package io.wispforest.owo.registration.reflect;

import io.wispforest.owo.registration.annotations.RegistryNamespace;
import io.wispforest.owo.util.ReflectionUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class FieldRegistrationHandler {
   private FieldRegistrationHandler() {
   }

   public static <T> void process(Class<? extends FieldProcessingSubject<T>> clazz, ReflectionUtils.FieldConsumer<T> processor, boolean recurseIntoInnerClasses) {
      FieldProcessingSubject<T> handler = ReflectionUtils.tryInstantiateWithNoArgs((Class<FieldProcessingSubject<T>>)clazz);
      ReflectionUtils.iterateAccessibleStaticFields(clazz, handler.getTargetFieldType(), createProcessor(processor, handler));
      if (recurseIntoInnerClasses) {
         ReflectionUtils.forApplicableSubclasses(
            clazz, FieldProcessingSubject.class, subclass -> process((Class<? extends FieldProcessingSubject<T>>)subclass, processor, true)
         );
      }

      handler.afterFieldProcessing();
   }

   public static <T> void processSimple(Class<? extends SimpleFieldProcessingSubject<T>> clazz, boolean recurseIntoInnerClasses) {
      SimpleFieldProcessingSubject<T> handler = ReflectionUtils.tryInstantiateWithNoArgs((Class<SimpleFieldProcessingSubject<T>>)clazz);
      ReflectionUtils.iterateAccessibleStaticFields(clazz, handler.getTargetFieldType(), createProcessor(handler::processField, handler));
      if (recurseIntoInnerClasses) {
         ReflectionUtils.forApplicableSubclasses(
            clazz, SimpleFieldProcessingSubject.class, subclass -> processSimple((Class<? extends SimpleFieldProcessingSubject<T>>)subclass, true)
         );
      }

      handler.afterFieldProcessing();
   }

   public static <T> void register(Class<? extends AutoRegistryContainer<T>> clazz, String namespace, boolean recurseIntoInnerClasses) {
      AutoRegistryContainer<T> container = ReflectionUtils.tryInstantiateWithNoArgs((Class<AutoRegistryContainer<T>>)clazz);
      ReflectionUtils.iterateAccessibleStaticFields(clazz, container.getTargetFieldType(), createProcessor((fieldValue, identifier, field) -> {
         Registry.register(container.getRegistry(), ResourceLocation.fromNamespaceAndPath(namespace, identifier), fieldValue);
         container.postProcessField(namespace, fieldValue, identifier, field);
      }, container));
      if (recurseIntoInnerClasses) {
         ReflectionUtils.forApplicableSubclasses(clazz, AutoRegistryContainer.class, subclass -> {
            String classModId = namespace;
            if (subclass.isAnnotationPresent(RegistryNamespace.class)) {
               classModId = subclass.getAnnotation(RegistryNamespace.class).value();
            }

            register((Class<? extends AutoRegistryContainer<T>>)subclass, classModId, true);
         });
      }

      container.afterFieldProcessing();
   }

   private static <T> ReflectionUtils.FieldConsumer<T> createProcessor(ReflectionUtils.FieldConsumer<T> delegate, FieldProcessingSubject<T> handler) {
      return (value, name, field) -> {
         if (handler.shouldProcessField((T)value, name, field)) {
            delegate.accept(value, name, field);
         }
      };
   }
}
