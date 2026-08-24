package me.lucko.spark.lib.protobuf;

@CheckReturnValue
interface MessageInfoFactory {
   boolean isSupported(Class<?> clazz);

   MessageInfo messageInfoFor(Class<?> clazz);
}
