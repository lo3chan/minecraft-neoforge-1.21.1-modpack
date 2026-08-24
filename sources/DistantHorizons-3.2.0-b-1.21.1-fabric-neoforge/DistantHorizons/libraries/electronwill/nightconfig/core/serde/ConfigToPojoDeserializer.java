package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Optional;

final class ConfigToPojoDeserializer implements ValueDeserializer<UnmodifiableConfig, Object> {
   public Object deserialize(UnmodifiableConfig value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
      if (!resultType.isPresent()) {
         return value;
      } else {
         TypeConstraint t = resultType.get();
         Class<?> cls = t.getSatisfyingRawType().orElseThrow(() -> new SerdeException("Could not find a concrete type that can satisfy the constraint " + t));

         Object instance;
         try {
            Constructor<?> constructor = cls.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers())) {
               constructor.setAccessible(true);
            }

            instance = constructor.newInstance();
         } catch (Exception var8) {
            throw new SerdeException("Failed to create an instance of " + cls, var8);
         }

         ctx.deserializeFields(value, instance);
         return instance;
      }
   }
}
