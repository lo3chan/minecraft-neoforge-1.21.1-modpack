package me.lucko.spark.lib.protobuf;

import java.util.Map;

public interface StructOrBuilder extends MessageLiteOrBuilder {
   int getFieldsCount();

   boolean containsFields(String key);

   @Deprecated
   Map<String, Value> getFields();

   Map<String, Value> getFieldsMap();

   Value getFieldsOrDefault(String key, Value defaultValue);

   Value getFieldsOrThrow(String key);
}
