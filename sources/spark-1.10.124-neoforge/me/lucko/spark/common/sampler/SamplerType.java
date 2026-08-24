package me.lucko.spark.common.sampler;

import me.lucko.spark.common.sampler.async.AsyncSampler;
import me.lucko.spark.common.sampler.java.JavaSampler;
import me.lucko.spark.proto.SparkSamplerProtos;

public enum SamplerType {
   JAVA(JavaSampler.class, SparkSamplerProtos.SamplerMetadata.SamplerEngine.JAVA),
   ASYNC(AsyncSampler.class, SparkSamplerProtos.SamplerMetadata.SamplerEngine.ASYNC);

   private final Class<? extends Sampler> expectedClass;
   private final SparkSamplerProtos.SamplerMetadata.SamplerEngine proto;

   private SamplerType(Class<? extends Sampler> expectedClass, SparkSamplerProtos.SamplerMetadata.SamplerEngine proto) {
      this.expectedClass = expectedClass;
      this.proto = proto;
   }

   public Class<? extends Sampler> implClass() {
      return this.expectedClass;
   }

   public SparkSamplerProtos.SamplerMetadata.SamplerEngine asProto() {
      return this.proto;
   }
}
