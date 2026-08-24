package me.lucko.spark.common.sampler.node.exporter;

import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.proto.SparkSamplerProtos;

public interface NodeExporter {
   SparkSamplerProtos.ThreadNode export(ThreadNode var1);
}
