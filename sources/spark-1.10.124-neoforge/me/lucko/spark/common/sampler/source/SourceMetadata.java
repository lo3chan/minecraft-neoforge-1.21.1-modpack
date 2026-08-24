package me.lucko.spark.common.sampler.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import me.lucko.spark.proto.SparkProtos;

public class SourceMetadata {
   private final String name;
   private final String version;
   private final String author;
   private final String description;

   public static <T> List<SourceMetadata> gather(
      Collection<T> sources,
      Function<? super T, String> name,
      Function<? super T, String> version,
      Function<? super T, String> author,
      Function<? super T, String> description
   ) {
      Builder<SourceMetadata> builder = ImmutableList.builder();

      for (T source : sources) {
         SourceMetadata metadata = new SourceMetadata(name.apply(source), version.apply(source), author.apply(source), description.apply(source));
         builder.add(metadata);
      }

      return builder.build();
   }

   public SourceMetadata(String name, String version, String author, String description) {
      this.name = name;
      this.version = version;
      this.author = author;
      this.description = description;
   }

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.version;
   }

   public String getAuthor() {
      return this.author;
   }

   public SparkProtos.PluginOrModMetadata toProto() {
      SparkProtos.PluginOrModMetadata.Builder builder = SparkProtos.PluginOrModMetadata.newBuilder().setName(this.name);
      if (this.version != null) {
         builder.setVersion(this.version);
      }

      if (this.author != null) {
         builder.setAuthor(this.author);
      }

      if (this.description != null) {
         builder.setDescription(this.description);
      }

      return builder.build();
   }
}
