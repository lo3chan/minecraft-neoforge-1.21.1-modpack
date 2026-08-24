package me.lucko.spark.common.platform;

import me.lucko.spark.proto.SparkProtos;

public interface PlatformInfo {
   int DATA_VERSION = 2;

   PlatformInfo.Type getType();

   String getName();

   String getBrand();

   String getVersion();

   String getMinecraftVersion();

   default int getSparkVersion() {
      return 2;
   }

   default PlatformInfo.Data toData() {
      return new PlatformInfo.Data(this.getType(), this.getName(), this.getBrand(), this.getVersion(), this.getMinecraftVersion(), this.getSparkVersion());
   }

   public static final class Data {
      private final PlatformInfo.Type type;
      private final String name;
      private final String brand;
      private final String version;
      private final String minecraftVersion;
      private final int sparkVersion;

      public Data(PlatformInfo.Type type, String name, String brand, String version, String minecraftVersion, int sparkVersion) {
         this.type = type;
         this.name = name;
         this.brand = brand;
         this.version = version;
         this.minecraftVersion = minecraftVersion;
         this.sparkVersion = sparkVersion;
      }

      public PlatformInfo.Type getType() {
         return this.type;
      }

      public String getName() {
         return this.name;
      }

      public String getBrand() {
         return this.brand;
      }

      public String getVersion() {
         return this.version;
      }

      public String getMinecraftVersion() {
         return this.minecraftVersion;
      }

      public int getSparkVersion() {
         return this.sparkVersion;
      }

      public SparkProtos.PlatformMetadata toProto() {
         SparkProtos.PlatformMetadata.Builder proto = SparkProtos.PlatformMetadata.newBuilder()
            .setType(this.type.toProto())
            .setName(this.name)
            .setBrand(this.brand)
            .setVersion(this.version)
            .setSparkVersion(this.sparkVersion);
         if (this.minecraftVersion != null) {
            proto.setMinecraftVersion(this.minecraftVersion);
         }

         return proto.build();
      }
   }

   public static enum Type {
      SERVER(SparkProtos.PlatformMetadata.Type.SERVER),
      CLIENT(SparkProtos.PlatformMetadata.Type.CLIENT),
      PROXY(SparkProtos.PlatformMetadata.Type.PROXY),
      APPLICATION(SparkProtos.PlatformMetadata.Type.APPLICATION);

      private final SparkProtos.PlatformMetadata.Type type;

      private Type(SparkProtos.PlatformMetadata.Type type) {
         this.type = type;
      }

      public SparkProtos.PlatformMetadata.Type toProto() {
         return this.type;
      }
   }
}
