package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

@FunctionalInterface
public interface ValueSerializerProvider<V, R> {
   ValueSerializer<V, R> provide(Class<?> class_, SerializerContext serializerContext);
}
