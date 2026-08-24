package corgitaco.corgilib.shadow.blue.endless.jankson.impl.serializer;

import corgitaco.corgilib.shadow.blue.endless.jankson.api.DeserializationException;
import corgitaco.corgilib.shadow.blue.endless.jankson.api.Marshaller;

@FunctionalInterface
public interface InternalDeserializerFunction<B> {
   B deserialize(Object var1, Marshaller var2) throws DeserializationException;
}
