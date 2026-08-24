package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import java.util.Optional;

public interface ValueDeserializer<T, R> {
   R deserialize(T object, Optional<TypeConstraint> optional, DeserializerContext deserializerContext);
}
