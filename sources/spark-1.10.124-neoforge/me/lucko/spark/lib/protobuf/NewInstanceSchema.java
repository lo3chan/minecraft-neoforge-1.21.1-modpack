package me.lucko.spark.lib.protobuf;

@CheckReturnValue
interface NewInstanceSchema {
   Object newInstance(Object defaultInstance);
}
