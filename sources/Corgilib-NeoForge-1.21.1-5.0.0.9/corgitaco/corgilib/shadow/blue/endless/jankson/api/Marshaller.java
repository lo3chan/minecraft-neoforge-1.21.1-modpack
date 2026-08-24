package corgitaco.corgilib.shadow.blue.endless.jankson.api;

import corgitaco.corgilib.shadow.blue.endless.jankson.JsonElement;
import java.lang.reflect.Type;

public interface Marshaller {
   JsonElement serialize(Object var1);

   <E> E marshall(Class<E> var1, JsonElement var2);

   <E> E marshall(Type var1, JsonElement var2);

   <E> E marshallCarefully(Class<E> var1, JsonElement var2) throws DeserializationException;
}
