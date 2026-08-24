package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public final class ObjectDeserializer extends AbstractObjectDeserializer {
   public static ObjectDeserializerBuilder builder() {
      return new ObjectDeserializerBuilder(true);
   }

   public static ObjectDeserializerBuilder blankBuilder() {
      return new ObjectDeserializerBuilder(false);
   }

   public static ObjectDeserializer standard() {
      return builder().build();
   }

   ObjectDeserializer(ObjectDeserializerBuilder builder) {
      super(builder);
   }

   @Override
   public <C extends Collection<V>, V> C deserializeToCollection(Object configValue, Class<C> collectionClass, Class<V> valueClass) {
      return super.deserializeToCollection(configValue, collectionClass, valueClass);
   }

   @Override
   public <M extends Map<String, V>, V> M deserializeToMap(Object configValue, Class<M> mapClass, Class<V> valueClass) {
      return super.deserializeToMap(configValue, mapClass, valueClass);
   }

   @Override
   public void deserializeFields(UnmodifiableConfig source, Object destination) {
      super.deserializeFields(source, destination);
   }

   @Override
   public <R> R deserializeFields(UnmodifiableConfig source, Supplier<? extends R> destinationSupplier) {
      return super.deserializeFields(source, destinationSupplier);
   }
}
