package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

public interface ValueSerializer<T, R> {
   R serialize(T object, SerializerContext serializerContext);
}
