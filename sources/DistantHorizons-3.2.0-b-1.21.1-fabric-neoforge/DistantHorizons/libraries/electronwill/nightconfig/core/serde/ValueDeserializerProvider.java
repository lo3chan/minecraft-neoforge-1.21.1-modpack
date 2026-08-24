package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

@FunctionalInterface
public interface ValueDeserializerProvider<T, R> {
   ValueDeserializer<T, R> provide(Class<?> class_, TypeConstraint typeConstraint);
}
