package me.lucko.spark.proto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.lucko.spark.lib.protobuf.AbstractMessageLite;
import me.lucko.spark.lib.protobuf.ByteString;
import me.lucko.spark.lib.protobuf.CodedInputStream;
import me.lucko.spark.lib.protobuf.ExtensionRegistryLite;
import me.lucko.spark.lib.protobuf.GeneratedMessageLite;
import me.lucko.spark.lib.protobuf.Internal;
import me.lucko.spark.lib.protobuf.InvalidProtocolBufferException;
import me.lucko.spark.lib.protobuf.MapEntryLite;
import me.lucko.spark.lib.protobuf.MapFieldLite;
import me.lucko.spark.lib.protobuf.MessageLiteOrBuilder;
import me.lucko.spark.lib.protobuf.Parser;
import me.lucko.spark.lib.protobuf.WireFormat;

public final class SparkHeapProtos {
   private SparkHeapProtos() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static final class HeapData
      extends GeneratedMessageLite<SparkHeapProtos.HeapData, SparkHeapProtos.HeapData.Builder>
      implements SparkHeapProtos.HeapDataOrBuilder {
      private int bitField0_;
      public static final int METADATA_FIELD_NUMBER = 1;
      private SparkHeapProtos.HeapMetadata metadata_;
      public static final int ENTRIES_FIELD_NUMBER = 2;
      private Internal.ProtobufList<SparkHeapProtos.HeapEntry> entries_ = emptyProtobufList();
      private static final SparkHeapProtos.HeapData DEFAULT_INSTANCE;
      private static volatile Parser<SparkHeapProtos.HeapData> PARSER;

      private HeapData() {
      }

      @Override
      public boolean hasMetadata() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public SparkHeapProtos.HeapMetadata getMetadata() {
         return this.metadata_ == null ? SparkHeapProtos.HeapMetadata.getDefaultInstance() : this.metadata_;
      }

      private void setMetadata(SparkHeapProtos.HeapMetadata value) {
         value.getClass();
         this.metadata_ = value;
         this.bitField0_ |= 1;
      }

      private void mergeMetadata(SparkHeapProtos.HeapMetadata value) {
         value.getClass();
         if (this.metadata_ != null && this.metadata_ != SparkHeapProtos.HeapMetadata.getDefaultInstance()) {
            this.metadata_ = SparkHeapProtos.HeapMetadata.newBuilder(this.metadata_).mergeFrom(value).buildPartial();
         } else {
            this.metadata_ = value;
         }

         this.bitField0_ |= 1;
      }

      private void clearMetadata() {
         this.metadata_ = null;
         this.bitField0_ &= -2;
      }

      @Override
      public List<SparkHeapProtos.HeapEntry> getEntriesList() {
         return this.entries_;
      }

      public List<? extends SparkHeapProtos.HeapEntryOrBuilder> getEntriesOrBuilderList() {
         return this.entries_;
      }

      @Override
      public int getEntriesCount() {
         return this.entries_.size();
      }

      @Override
      public SparkHeapProtos.HeapEntry getEntries(int index) {
         return this.entries_.get(index);
      }

      public SparkHeapProtos.HeapEntryOrBuilder getEntriesOrBuilder(int index) {
         return this.entries_.get(index);
      }

      private void ensureEntriesIsMutable() {
         Internal.ProtobufList<SparkHeapProtos.HeapEntry> tmp = this.entries_;
         if (!tmp.isModifiable()) {
            this.entries_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setEntries(int index, SparkHeapProtos.HeapEntry value) {
         value.getClass();
         this.ensureEntriesIsMutable();
         this.entries_.set(index, value);
      }

      private void addEntries(SparkHeapProtos.HeapEntry value) {
         value.getClass();
         this.ensureEntriesIsMutable();
         this.entries_.add(value);
      }

      private void addEntries(int index, SparkHeapProtos.HeapEntry value) {
         value.getClass();
         this.ensureEntriesIsMutable();
         this.entries_.add(index, value);
      }

      private void addAllEntries(Iterable<? extends SparkHeapProtos.HeapEntry> values) {
         this.ensureEntriesIsMutable();
         AbstractMessageLite.addAll(values, this.entries_);
      }

      private void clearEntries() {
         this.entries_ = emptyProtobufList();
      }

      private void removeEntries(int index) {
         this.ensureEntriesIsMutable();
         this.entries_.remove(index);
      }

      public static SparkHeapProtos.HeapData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapData parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapData parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapData parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapData parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapData parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapData.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkHeapProtos.HeapData.Builder newBuilder(SparkHeapProtos.HeapData prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.Collections$2.tryAdvance(Collections.java:4851)
         //   at java.base/java.util.stream.ReferencePipeline.forEachWithCancel(ReferencePipeline.java:129)
         //   at java.base/java.util.stream.AbstractPipeline.copyIntoWithCancel(AbstractPipeline.java:527)
         //   at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:513)
         //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
         //   at java.base/java.util.stream.MatchOps$MatchOp.evaluateSequential(MatchOps.java:230)
         //   at java.base/java.util.stream.MatchOps$MatchOp.evaluateSequential(MatchOps.java:196)
         //   at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
         //   at java.base/java.util.stream.ReferencePipeline.anyMatch(ReferencePipeline.java:632)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$21(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/proto/SparkHeapProtos$HeapData.DEFAULT_INSTANCE Lme/lucko/spark/proto/SparkHeapProtos$HeapData;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/proto/SparkHeapProtos$HeapData.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/proto/SparkHeapProtos$HeapData$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new SparkHeapProtos.HeapData();
            case NEW_BUILDER:
               return new SparkHeapProtos.HeapData.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"bitField0_", "metadata_", "entries_", SparkHeapProtos.HeapEntry.class};
               String info = "\u0000\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0001\u0000\u0001ဉ\u0000\u0002\u001b";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<SparkHeapProtos.HeapData> parser = PARSER;
               if (parser == null) {
                  synchronized (SparkHeapProtos.HeapData.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return (byte)1;
            case SET_MEMOIZED_IS_INITIALIZED:
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static SparkHeapProtos.HeapData getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkHeapProtos.HeapData> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkHeapProtos.HeapData defaultInstance = new SparkHeapProtos.HeapData();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkHeapProtos.HeapData.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<SparkHeapProtos.HeapData, SparkHeapProtos.HeapData.Builder>
         implements SparkHeapProtos.HeapDataOrBuilder {
         private Builder() {
            super(SparkHeapProtos.HeapData.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasMetadata() {
            return this.instance.hasMetadata();
         }

         @Override
         public SparkHeapProtos.HeapMetadata getMetadata() {
            return this.instance.getMetadata();
         }

         public SparkHeapProtos.HeapData.Builder setMetadata(SparkHeapProtos.HeapMetadata value) {
            this.copyOnWrite();
            this.instance.setMetadata(value);
            return this;
         }

         public SparkHeapProtos.HeapData.Builder setMetadata(SparkHeapProtos.HeapMetadata.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setMetadata(builderForValue.build());
            return this;
         }

         public SparkHeapProtos.HeapData.Builder mergeMetadata(SparkHeapProtos.HeapMetadata value) {
            this.copyOnWrite();
            this.instance.mergeMetadata(value);
            return this;
         }

         public SparkHeapProtos.HeapData.Builder clearMetadata() {
            this.copyOnWrite();
            this.instance.clearMetadata();
            return this;
         }

         @Override
         public List<SparkHeapProtos.HeapEntry> getEntriesList() {
            return Collections.unmodifiableList(this.instance.getEntriesList());
         }

         @Override
         public int getEntriesCount() {
            return this.instance.getEntriesCount();
         }

         @Override
         public SparkHeapProtos.HeapEntry getEntries(int index) {
            return this.instance.getEntries(index);
         }

         public SparkHeapProtos.HeapData.Builder setEntries(int index, SparkHeapProtos.HeapEntry value) {
            this.copyOnWrite();
            this.instance.setEntries(index, value);
            return this;
         }

         public SparkHeapProtos.HeapData.Builder setEntries(int index, SparkHeapProtos.HeapEntry.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setEntries(index, builderForValue.build());
            return this;
         }

         public SparkHeapProtos.HeapData.Builder addEntries(SparkHeapProtos.HeapEntry value) {
            this.copyOnWrite();
            this.instance.addEntries(value);
            return this;
         }

         public SparkHeapProtos.HeapData.Builder addEntries(int index, SparkHeapProtos.HeapEntry value) {
            this.copyOnWrite();
            this.instance.addEntries(index, value);
            return this;
         }

         public SparkHeapProtos.HeapData.Builder addEntries(SparkHeapProtos.HeapEntry.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addEntries(builderForValue.build());
            return this;
         }

         public SparkHeapProtos.HeapData.Builder addEntries(int index, SparkHeapProtos.HeapEntry.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addEntries(index, builderForValue.build());
            return this;
         }

         public SparkHeapProtos.HeapData.Builder addAllEntries(Iterable<? extends SparkHeapProtos.HeapEntry> values) {
            this.copyOnWrite();
            this.instance.addAllEntries(values);
            return this;
         }

         public SparkHeapProtos.HeapData.Builder clearEntries() {
            this.copyOnWrite();
            this.instance.clearEntries();
            return this;
         }

         public SparkHeapProtos.HeapData.Builder removeEntries(int index) {
            this.copyOnWrite();
            this.instance.removeEntries(index);
            return this;
         }
      }
   }

   public interface HeapDataOrBuilder extends MessageLiteOrBuilder {
      boolean hasMetadata();

      SparkHeapProtos.HeapMetadata getMetadata();

      List<SparkHeapProtos.HeapEntry> getEntriesList();

      SparkHeapProtos.HeapEntry getEntries(int var1);

      int getEntriesCount();
   }

   public static final class HeapEntry
      extends GeneratedMessageLite<SparkHeapProtos.HeapEntry, SparkHeapProtos.HeapEntry.Builder>
      implements SparkHeapProtos.HeapEntryOrBuilder {
      public static final int ORDER_FIELD_NUMBER = 1;
      private int order_;
      public static final int INSTANCES_FIELD_NUMBER = 2;
      private int instances_;
      public static final int SIZE_FIELD_NUMBER = 3;
      private long size_;
      public static final int TYPE_FIELD_NUMBER = 4;
      private String type_ = "";
      private static final SparkHeapProtos.HeapEntry DEFAULT_INSTANCE;
      private static volatile Parser<SparkHeapProtos.HeapEntry> PARSER;

      private HeapEntry() {
      }

      @Override
      public int getOrder() {
         return this.order_;
      }

      private void setOrder(int value) {
         this.order_ = value;
      }

      private void clearOrder() {
         this.order_ = 0;
      }

      @Override
      public int getInstances() {
         return this.instances_;
      }

      private void setInstances(int value) {
         this.instances_ = value;
      }

      private void clearInstances() {
         this.instances_ = 0;
      }

      @Override
      public long getSize() {
         return this.size_;
      }

      private void setSize(long value) {
         this.size_ = value;
      }

      private void clearSize() {
         this.size_ = 0L;
      }

      @Override
      public String getType() {
         return this.type_;
      }

      @Override
      public ByteString getTypeBytes() {
         return ByteString.copyFromUtf8(this.type_);
      }

      private void setType(String value) {
         Class<?> valueClass = value.getClass();
         this.type_ = value;
      }

      private void clearType() {
         this.type_ = getDefaultInstance().getType();
      }

      private void setTypeBytes(ByteString value) {
         checkByteStringIsUtf8(value);
         this.type_ = value.toStringUtf8();
      }

      public static SparkHeapProtos.HeapEntry parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapEntry parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapEntry parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapEntry parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapEntry.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkHeapProtos.HeapEntry.Builder newBuilder(SparkHeapProtos.HeapEntry prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:283)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/proto/SparkHeapProtos$HeapEntry.DEFAULT_INSTANCE Lme/lucko/spark/proto/SparkHeapProtos$HeapEntry;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/proto/SparkHeapProtos$HeapEntry.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/proto/SparkHeapProtos$HeapEntry$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new SparkHeapProtos.HeapEntry();
            case NEW_BUILDER:
               return new SparkHeapProtos.HeapEntry.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"order_", "instances_", "size_", "type_"};
               String info = "\u0000\u0004\u0000\u0000\u0001\u0004\u0004\u0000\u0000\u0000\u0001\u0004\u0002\u0004\u0003\u0002\u0004Ȉ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<SparkHeapProtos.HeapEntry> parser = PARSER;
               if (parser == null) {
                  synchronized (SparkHeapProtos.HeapEntry.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return (byte)1;
            case SET_MEMOIZED_IS_INITIALIZED:
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static SparkHeapProtos.HeapEntry getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkHeapProtos.HeapEntry> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkHeapProtos.HeapEntry defaultInstance = new SparkHeapProtos.HeapEntry();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkHeapProtos.HeapEntry.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<SparkHeapProtos.HeapEntry, SparkHeapProtos.HeapEntry.Builder>
         implements SparkHeapProtos.HeapEntryOrBuilder {
         private Builder() {
            super(SparkHeapProtos.HeapEntry.DEFAULT_INSTANCE);
         }

         @Override
         public int getOrder() {
            return this.instance.getOrder();
         }

         public SparkHeapProtos.HeapEntry.Builder setOrder(int value) {
            this.copyOnWrite();
            this.instance.setOrder(value);
            return this;
         }

         public SparkHeapProtos.HeapEntry.Builder clearOrder() {
            this.copyOnWrite();
            this.instance.clearOrder();
            return this;
         }

         @Override
         public int getInstances() {
            return this.instance.getInstances();
         }

         public SparkHeapProtos.HeapEntry.Builder setInstances(int value) {
            this.copyOnWrite();
            this.instance.setInstances(value);
            return this;
         }

         public SparkHeapProtos.HeapEntry.Builder clearInstances() {
            this.copyOnWrite();
            this.instance.clearInstances();
            return this;
         }

         @Override
         public long getSize() {
            return this.instance.getSize();
         }

         public SparkHeapProtos.HeapEntry.Builder setSize(long value) {
            this.copyOnWrite();
            this.instance.setSize(value);
            return this;
         }

         public SparkHeapProtos.HeapEntry.Builder clearSize() {
            this.copyOnWrite();
            this.instance.clearSize();
            return this;
         }

         @Override
         public String getType() {
            return this.instance.getType();
         }

         @Override
         public ByteString getTypeBytes() {
            return this.instance.getTypeBytes();
         }

         public SparkHeapProtos.HeapEntry.Builder setType(String value) {
            this.copyOnWrite();
            this.instance.setType(value);
            return this;
         }

         public SparkHeapProtos.HeapEntry.Builder clearType() {
            this.copyOnWrite();
            this.instance.clearType();
            return this;
         }

         public SparkHeapProtos.HeapEntry.Builder setTypeBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setTypeBytes(value);
            return this;
         }
      }
   }

   public interface HeapEntryOrBuilder extends MessageLiteOrBuilder {
      int getOrder();

      int getInstances();

      long getSize();

      String getType();

      ByteString getTypeBytes();
   }

   public static final class HeapMetadata
      extends GeneratedMessageLite<SparkHeapProtos.HeapMetadata, SparkHeapProtos.HeapMetadata.Builder>
      implements SparkHeapProtos.HeapMetadataOrBuilder {
      private int bitField0_;
      public static final int CREATOR_FIELD_NUMBER = 1;
      private SparkProtos.CommandSenderMetadata creator_;
      public static final int PLATFORM_METADATA_FIELD_NUMBER = 2;
      private SparkProtos.PlatformMetadata platformMetadata_;
      public static final int PLATFORM_STATISTICS_FIELD_NUMBER = 3;
      private SparkProtos.PlatformStatistics platformStatistics_;
      public static final int SYSTEM_STATISTICS_FIELD_NUMBER = 4;
      private SparkProtos.SystemStatistics systemStatistics_;
      public static final int GENERATED_TIME_FIELD_NUMBER = 5;
      private long generatedTime_;
      public static final int SERVER_CONFIGURATIONS_FIELD_NUMBER = 6;
      private MapFieldLite<String, String> serverConfigurations_ = MapFieldLite.emptyMapField();
      public static final int SOURCES_FIELD_NUMBER = 7;
      private MapFieldLite<String, SparkProtos.PluginOrModMetadata> sources_ = MapFieldLite.emptyMapField();
      public static final int EXTRA_PLATFORM_METADATA_FIELD_NUMBER = 8;
      private MapFieldLite<String, String> extraPlatformMetadata_ = MapFieldLite.emptyMapField();
      private static final SparkHeapProtos.HeapMetadata DEFAULT_INSTANCE;
      private static volatile Parser<SparkHeapProtos.HeapMetadata> PARSER;

      private HeapMetadata() {
      }

      @Override
      public boolean hasCreator() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public SparkProtos.CommandSenderMetadata getCreator() {
         return this.creator_ == null ? SparkProtos.CommandSenderMetadata.getDefaultInstance() : this.creator_;
      }

      private void setCreator(SparkProtos.CommandSenderMetadata value) {
         value.getClass();
         this.creator_ = value;
         this.bitField0_ |= 1;
      }

      private void mergeCreator(SparkProtos.CommandSenderMetadata value) {
         value.getClass();
         if (this.creator_ != null && this.creator_ != SparkProtos.CommandSenderMetadata.getDefaultInstance()) {
            this.creator_ = SparkProtos.CommandSenderMetadata.newBuilder(this.creator_).mergeFrom(value).buildPartial();
         } else {
            this.creator_ = value;
         }

         this.bitField0_ |= 1;
      }

      private void clearCreator() {
         this.creator_ = null;
         this.bitField0_ &= -2;
      }

      @Override
      public boolean hasPlatformMetadata() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public SparkProtos.PlatformMetadata getPlatformMetadata() {
         return this.platformMetadata_ == null ? SparkProtos.PlatformMetadata.getDefaultInstance() : this.platformMetadata_;
      }

      private void setPlatformMetadata(SparkProtos.PlatformMetadata value) {
         value.getClass();
         this.platformMetadata_ = value;
         this.bitField0_ |= 2;
      }

      private void mergePlatformMetadata(SparkProtos.PlatformMetadata value) {
         value.getClass();
         if (this.platformMetadata_ != null && this.platformMetadata_ != SparkProtos.PlatformMetadata.getDefaultInstance()) {
            this.platformMetadata_ = SparkProtos.PlatformMetadata.newBuilder(this.platformMetadata_).mergeFrom(value).buildPartial();
         } else {
            this.platformMetadata_ = value;
         }

         this.bitField0_ |= 2;
      }

      private void clearPlatformMetadata() {
         this.platformMetadata_ = null;
         this.bitField0_ &= -3;
      }

      @Override
      public boolean hasPlatformStatistics() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public SparkProtos.PlatformStatistics getPlatformStatistics() {
         return this.platformStatistics_ == null ? SparkProtos.PlatformStatistics.getDefaultInstance() : this.platformStatistics_;
      }

      private void setPlatformStatistics(SparkProtos.PlatformStatistics value) {
         value.getClass();
         this.platformStatistics_ = value;
         this.bitField0_ |= 4;
      }

      private void mergePlatformStatistics(SparkProtos.PlatformStatistics value) {
         value.getClass();
         if (this.platformStatistics_ != null && this.platformStatistics_ != SparkProtos.PlatformStatistics.getDefaultInstance()) {
            this.platformStatistics_ = SparkProtos.PlatformStatistics.newBuilder(this.platformStatistics_).mergeFrom(value).buildPartial();
         } else {
            this.platformStatistics_ = value;
         }

         this.bitField0_ |= 4;
      }

      private void clearPlatformStatistics() {
         this.platformStatistics_ = null;
         this.bitField0_ &= -5;
      }

      @Override
      public boolean hasSystemStatistics() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public SparkProtos.SystemStatistics getSystemStatistics() {
         return this.systemStatistics_ == null ? SparkProtos.SystemStatistics.getDefaultInstance() : this.systemStatistics_;
      }

      private void setSystemStatistics(SparkProtos.SystemStatistics value) {
         value.getClass();
         this.systemStatistics_ = value;
         this.bitField0_ |= 8;
      }

      private void mergeSystemStatistics(SparkProtos.SystemStatistics value) {
         value.getClass();
         if (this.systemStatistics_ != null && this.systemStatistics_ != SparkProtos.SystemStatistics.getDefaultInstance()) {
            this.systemStatistics_ = SparkProtos.SystemStatistics.newBuilder(this.systemStatistics_).mergeFrom(value).buildPartial();
         } else {
            this.systemStatistics_ = value;
         }

         this.bitField0_ |= 8;
      }

      private void clearSystemStatistics() {
         this.systemStatistics_ = null;
         this.bitField0_ &= -9;
      }

      @Override
      public long getGeneratedTime() {
         return this.generatedTime_;
      }

      private void setGeneratedTime(long value) {
         this.generatedTime_ = value;
      }

      private void clearGeneratedTime() {
         this.generatedTime_ = 0L;
      }

      private MapFieldLite<String, String> internalGetServerConfigurations() {
         return this.serverConfigurations_;
      }

      private MapFieldLite<String, String> internalGetMutableServerConfigurations() {
         if (!this.serverConfigurations_.isMutable()) {
            this.serverConfigurations_ = this.serverConfigurations_.mutableCopy();
         }

         return this.serverConfigurations_;
      }

      @Override
      public int getServerConfigurationsCount() {
         return this.internalGetServerConfigurations().size();
      }

      @Override
      public boolean containsServerConfigurations(String key) {
         Class<?> keyClass = key.getClass();
         return this.internalGetServerConfigurations().containsKey(key);
      }

      @Deprecated
      @Override
      public Map<String, String> getServerConfigurations() {
         return this.getServerConfigurationsMap();
      }

      @Override
      public Map<String, String> getServerConfigurationsMap() {
         return Collections.unmodifiableMap(this.internalGetServerConfigurations());
      }

      @Override
      public String getServerConfigurationsOrDefault(String key, String defaultValue) {
         Class<?> keyClass = key.getClass();
         Map<String, String> map = this.internalGetServerConfigurations();
         return map.containsKey(key) ? map.get(key) : defaultValue;
      }

      @Override
      public String getServerConfigurationsOrThrow(String key) {
         Class<?> keyClass = key.getClass();
         Map<String, String> map = this.internalGetServerConfigurations();
         if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
         } else {
            return map.get(key);
         }
      }

      private Map<String, String> getMutableServerConfigurationsMap() {
         return this.internalGetMutableServerConfigurations();
      }

      private MapFieldLite<String, SparkProtos.PluginOrModMetadata> internalGetSources() {
         return this.sources_;
      }

      private MapFieldLite<String, SparkProtos.PluginOrModMetadata> internalGetMutableSources() {
         if (!this.sources_.isMutable()) {
            this.sources_ = this.sources_.mutableCopy();
         }

         return this.sources_;
      }

      @Override
      public int getSourcesCount() {
         return this.internalGetSources().size();
      }

      @Override
      public boolean containsSources(String key) {
         Class<?> keyClass = key.getClass();
         return this.internalGetSources().containsKey(key);
      }

      @Deprecated
      @Override
      public Map<String, SparkProtos.PluginOrModMetadata> getSources() {
         return this.getSourcesMap();
      }

      @Override
      public Map<String, SparkProtos.PluginOrModMetadata> getSourcesMap() {
         return Collections.unmodifiableMap(this.internalGetSources());
      }

      @Override
      public SparkProtos.PluginOrModMetadata getSourcesOrDefault(String key, SparkProtos.PluginOrModMetadata defaultValue) {
         Class<?> keyClass = key.getClass();
         Map<String, SparkProtos.PluginOrModMetadata> map = this.internalGetSources();
         return map.containsKey(key) ? map.get(key) : defaultValue;
      }

      @Override
      public SparkProtos.PluginOrModMetadata getSourcesOrThrow(String key) {
         Class<?> keyClass = key.getClass();
         Map<String, SparkProtos.PluginOrModMetadata> map = this.internalGetSources();
         if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
         } else {
            return map.get(key);
         }
      }

      private Map<String, SparkProtos.PluginOrModMetadata> getMutableSourcesMap() {
         return this.internalGetMutableSources();
      }

      private MapFieldLite<String, String> internalGetExtraPlatformMetadata() {
         return this.extraPlatformMetadata_;
      }

      private MapFieldLite<String, String> internalGetMutableExtraPlatformMetadata() {
         if (!this.extraPlatformMetadata_.isMutable()) {
            this.extraPlatformMetadata_ = this.extraPlatformMetadata_.mutableCopy();
         }

         return this.extraPlatformMetadata_;
      }

      @Override
      public int getExtraPlatformMetadataCount() {
         return this.internalGetExtraPlatformMetadata().size();
      }

      @Override
      public boolean containsExtraPlatformMetadata(String key) {
         Class<?> keyClass = key.getClass();
         return this.internalGetExtraPlatformMetadata().containsKey(key);
      }

      @Deprecated
      @Override
      public Map<String, String> getExtraPlatformMetadata() {
         return this.getExtraPlatformMetadataMap();
      }

      @Override
      public Map<String, String> getExtraPlatformMetadataMap() {
         return Collections.unmodifiableMap(this.internalGetExtraPlatformMetadata());
      }

      @Override
      public String getExtraPlatformMetadataOrDefault(String key, String defaultValue) {
         Class<?> keyClass = key.getClass();
         Map<String, String> map = this.internalGetExtraPlatformMetadata();
         return map.containsKey(key) ? map.get(key) : defaultValue;
      }

      @Override
      public String getExtraPlatformMetadataOrThrow(String key) {
         Class<?> keyClass = key.getClass();
         Map<String, String> map = this.internalGetExtraPlatformMetadata();
         if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
         } else {
            return map.get(key);
         }
      }

      private Map<String, String> getMutableExtraPlatformMetadataMap() {
         return this.internalGetMutableExtraPlatformMetadata();
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapMetadata parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapMetadata parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static SparkHeapProtos.HeapMetadata parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static SparkHeapProtos.HeapMetadata.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static SparkHeapProtos.HeapMetadata.Builder newBuilder(SparkHeapProtos.HeapMetadata prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at org.jetbrains.java.decompiler.main.DecompilerContext.getCurrentContext(DecompilerContext.java:67)
         //   at org.jetbrains.java.decompiler.main.DecompilerContext.getStructContext(DecompilerContext.java:137)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/proto/SparkHeapProtos$HeapMetadata.DEFAULT_INSTANCE Lme/lucko/spark/proto/SparkHeapProtos$HeapMetadata;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/proto/SparkHeapProtos$HeapMetadata.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/proto/SparkHeapProtos$HeapMetadata$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new SparkHeapProtos.HeapMetadata();
            case NEW_BUILDER:
               return new SparkHeapProtos.HeapMetadata.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "creator_",
                  "platformMetadata_",
                  "platformStatistics_",
                  "systemStatistics_",
                  "generatedTime_",
                  "serverConfigurations_",
                  SparkHeapProtos.HeapMetadata.ServerConfigurationsDefaultEntryHolder.defaultEntry,
                  "sources_",
                  SparkHeapProtos.HeapMetadata.SourcesDefaultEntryHolder.defaultEntry,
                  "extraPlatformMetadata_",
                  SparkHeapProtos.HeapMetadata.ExtraPlatformMetadataDefaultEntryHolder.defaultEntry
               };
               String info = "\u0000\b\u0000\u0001\u0001\b\b\u0003\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004ဉ\u0003\u0005\u0002\u00062\u00072\b2";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<SparkHeapProtos.HeapMetadata> parser = PARSER;
               if (parser == null) {
                  synchronized (SparkHeapProtos.HeapMetadata.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return (byte)1;
            case SET_MEMOIZED_IS_INITIALIZED:
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static SparkHeapProtos.HeapMetadata getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<SparkHeapProtos.HeapMetadata> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         SparkHeapProtos.HeapMetadata defaultInstance = new SparkHeapProtos.HeapMetadata();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(SparkHeapProtos.HeapMetadata.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<SparkHeapProtos.HeapMetadata, SparkHeapProtos.HeapMetadata.Builder>
         implements SparkHeapProtos.HeapMetadataOrBuilder {
         private Builder() {
            super(SparkHeapProtos.HeapMetadata.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasCreator() {
            return this.instance.hasCreator();
         }

         @Override
         public SparkProtos.CommandSenderMetadata getCreator() {
            return this.instance.getCreator();
         }

         public SparkHeapProtos.HeapMetadata.Builder setCreator(SparkProtos.CommandSenderMetadata value) {
            this.copyOnWrite();
            this.instance.setCreator(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder setCreator(SparkProtos.CommandSenderMetadata.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setCreator(builderForValue.build());
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder mergeCreator(SparkProtos.CommandSenderMetadata value) {
            this.copyOnWrite();
            this.instance.mergeCreator(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder clearCreator() {
            this.copyOnWrite();
            this.instance.clearCreator();
            return this;
         }

         @Override
         public boolean hasPlatformMetadata() {
            return this.instance.hasPlatformMetadata();
         }

         @Override
         public SparkProtos.PlatformMetadata getPlatformMetadata() {
            return this.instance.getPlatformMetadata();
         }

         public SparkHeapProtos.HeapMetadata.Builder setPlatformMetadata(SparkProtos.PlatformMetadata value) {
            this.copyOnWrite();
            this.instance.setPlatformMetadata(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder setPlatformMetadata(SparkProtos.PlatformMetadata.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setPlatformMetadata(builderForValue.build());
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder mergePlatformMetadata(SparkProtos.PlatformMetadata value) {
            this.copyOnWrite();
            this.instance.mergePlatformMetadata(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder clearPlatformMetadata() {
            this.copyOnWrite();
            this.instance.clearPlatformMetadata();
            return this;
         }

         @Override
         public boolean hasPlatformStatistics() {
            return this.instance.hasPlatformStatistics();
         }

         @Override
         public SparkProtos.PlatformStatistics getPlatformStatistics() {
            return this.instance.getPlatformStatistics();
         }

         public SparkHeapProtos.HeapMetadata.Builder setPlatformStatistics(SparkProtos.PlatformStatistics value) {
            this.copyOnWrite();
            this.instance.setPlatformStatistics(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder setPlatformStatistics(SparkProtos.PlatformStatistics.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setPlatformStatistics(builderForValue.build());
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder mergePlatformStatistics(SparkProtos.PlatformStatistics value) {
            this.copyOnWrite();
            this.instance.mergePlatformStatistics(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder clearPlatformStatistics() {
            this.copyOnWrite();
            this.instance.clearPlatformStatistics();
            return this;
         }

         @Override
         public boolean hasSystemStatistics() {
            return this.instance.hasSystemStatistics();
         }

         @Override
         public SparkProtos.SystemStatistics getSystemStatistics() {
            return this.instance.getSystemStatistics();
         }

         public SparkHeapProtos.HeapMetadata.Builder setSystemStatistics(SparkProtos.SystemStatistics value) {
            this.copyOnWrite();
            this.instance.setSystemStatistics(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder setSystemStatistics(SparkProtos.SystemStatistics.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setSystemStatistics(builderForValue.build());
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder mergeSystemStatistics(SparkProtos.SystemStatistics value) {
            this.copyOnWrite();
            this.instance.mergeSystemStatistics(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder clearSystemStatistics() {
            this.copyOnWrite();
            this.instance.clearSystemStatistics();
            return this;
         }

         @Override
         public long getGeneratedTime() {
            return this.instance.getGeneratedTime();
         }

         public SparkHeapProtos.HeapMetadata.Builder setGeneratedTime(long value) {
            this.copyOnWrite();
            this.instance.setGeneratedTime(value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder clearGeneratedTime() {
            this.copyOnWrite();
            this.instance.clearGeneratedTime();
            return this;
         }

         @Override
         public int getServerConfigurationsCount() {
            return this.instance.getServerConfigurationsMap().size();
         }

         @Override
         public boolean containsServerConfigurations(String key) {
            Class<?> keyClass = key.getClass();
            return this.instance.getServerConfigurationsMap().containsKey(key);
         }

         public SparkHeapProtos.HeapMetadata.Builder clearServerConfigurations() {
            this.copyOnWrite();
            this.instance.getMutableServerConfigurationsMap().clear();
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder removeServerConfigurations(String key) {
            Class<?> keyClass = key.getClass();
            this.copyOnWrite();
            this.instance.getMutableServerConfigurationsMap().remove(key);
            return this;
         }

         @Deprecated
         @Override
         public Map<String, String> getServerConfigurations() {
            return this.getServerConfigurationsMap();
         }

         @Override
         public Map<String, String> getServerConfigurationsMap() {
            return Collections.unmodifiableMap(this.instance.getServerConfigurationsMap());
         }

         @Override
         public String getServerConfigurationsOrDefault(String key, String defaultValue) {
            Class<?> keyClass = key.getClass();
            Map<String, String> map = this.instance.getServerConfigurationsMap();
            return map.containsKey(key) ? map.get(key) : defaultValue;
         }

         @Override
         public String getServerConfigurationsOrThrow(String key) {
            Class<?> keyClass = key.getClass();
            Map<String, String> map = this.instance.getServerConfigurationsMap();
            if (!map.containsKey(key)) {
               throw new IllegalArgumentException();
            } else {
               return map.get(key);
            }
         }

         public SparkHeapProtos.HeapMetadata.Builder putServerConfigurations(String key, String value) {
            Class<?> keyClass = key.getClass();
            Class<?> valueClass = value.getClass();
            this.copyOnWrite();
            this.instance.getMutableServerConfigurationsMap().put(key, value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder putAllServerConfigurations(Map<String, String> values) {
            this.copyOnWrite();
            this.instance.getMutableServerConfigurationsMap().putAll(values);
            return this;
         }

         @Override
         public int getSourcesCount() {
            return this.instance.getSourcesMap().size();
         }

         @Override
         public boolean containsSources(String key) {
            Class<?> keyClass = key.getClass();
            return this.instance.getSourcesMap().containsKey(key);
         }

         public SparkHeapProtos.HeapMetadata.Builder clearSources() {
            this.copyOnWrite();
            this.instance.getMutableSourcesMap().clear();
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder removeSources(String key) {
            Class<?> keyClass = key.getClass();
            this.copyOnWrite();
            this.instance.getMutableSourcesMap().remove(key);
            return this;
         }

         @Deprecated
         @Override
         public Map<String, SparkProtos.PluginOrModMetadata> getSources() {
            return this.getSourcesMap();
         }

         @Override
         public Map<String, SparkProtos.PluginOrModMetadata> getSourcesMap() {
            return Collections.unmodifiableMap(this.instance.getSourcesMap());
         }

         @Override
         public SparkProtos.PluginOrModMetadata getSourcesOrDefault(String key, SparkProtos.PluginOrModMetadata defaultValue) {
            Class<?> keyClass = key.getClass();
            Map<String, SparkProtos.PluginOrModMetadata> map = this.instance.getSourcesMap();
            return map.containsKey(key) ? map.get(key) : defaultValue;
         }

         @Override
         public SparkProtos.PluginOrModMetadata getSourcesOrThrow(String key) {
            Class<?> keyClass = key.getClass();
            Map<String, SparkProtos.PluginOrModMetadata> map = this.instance.getSourcesMap();
            if (!map.containsKey(key)) {
               throw new IllegalArgumentException();
            } else {
               return map.get(key);
            }
         }

         public SparkHeapProtos.HeapMetadata.Builder putSources(String key, SparkProtos.PluginOrModMetadata value) {
            Class<?> keyClass = key.getClass();
            Class<?> valueClass = value.getClass();
            this.copyOnWrite();
            this.instance.getMutableSourcesMap().put(key, value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder putAllSources(Map<String, SparkProtos.PluginOrModMetadata> values) {
            this.copyOnWrite();
            this.instance.getMutableSourcesMap().putAll(values);
            return this;
         }

         @Override
         public int getExtraPlatformMetadataCount() {
            return this.instance.getExtraPlatformMetadataMap().size();
         }

         @Override
         public boolean containsExtraPlatformMetadata(String key) {
            Class<?> keyClass = key.getClass();
            return this.instance.getExtraPlatformMetadataMap().containsKey(key);
         }

         public SparkHeapProtos.HeapMetadata.Builder clearExtraPlatformMetadata() {
            this.copyOnWrite();
            this.instance.getMutableExtraPlatformMetadataMap().clear();
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder removeExtraPlatformMetadata(String key) {
            Class<?> keyClass = key.getClass();
            this.copyOnWrite();
            this.instance.getMutableExtraPlatformMetadataMap().remove(key);
            return this;
         }

         @Deprecated
         @Override
         public Map<String, String> getExtraPlatformMetadata() {
            return this.getExtraPlatformMetadataMap();
         }

         @Override
         public Map<String, String> getExtraPlatformMetadataMap() {
            return Collections.unmodifiableMap(this.instance.getExtraPlatformMetadataMap());
         }

         @Override
         public String getExtraPlatformMetadataOrDefault(String key, String defaultValue) {
            Class<?> keyClass = key.getClass();
            Map<String, String> map = this.instance.getExtraPlatformMetadataMap();
            return map.containsKey(key) ? map.get(key) : defaultValue;
         }

         @Override
         public String getExtraPlatformMetadataOrThrow(String key) {
            Class<?> keyClass = key.getClass();
            Map<String, String> map = this.instance.getExtraPlatformMetadataMap();
            if (!map.containsKey(key)) {
               throw new IllegalArgumentException();
            } else {
               return map.get(key);
            }
         }

         public SparkHeapProtos.HeapMetadata.Builder putExtraPlatformMetadata(String key, String value) {
            Class<?> keyClass = key.getClass();
            Class<?> valueClass = value.getClass();
            this.copyOnWrite();
            this.instance.getMutableExtraPlatformMetadataMap().put(key, value);
            return this;
         }

         public SparkHeapProtos.HeapMetadata.Builder putAllExtraPlatformMetadata(Map<String, String> values) {
            this.copyOnWrite();
            this.instance.getMutableExtraPlatformMetadataMap().putAll(values);
            return this;
         }
      }

      private static final class ExtraPlatformMetadataDefaultEntryHolder {
         static final MapEntryLite<String, String> defaultEntry = MapEntryLite.newDefaultInstance(
            WireFormat.FieldType.STRING, "", WireFormat.FieldType.STRING, ""
         );
      }

      private static final class ServerConfigurationsDefaultEntryHolder {
         static final MapEntryLite<String, String> defaultEntry = MapEntryLite.newDefaultInstance(
            WireFormat.FieldType.STRING, "", WireFormat.FieldType.STRING, ""
         );
      }

      private static final class SourcesDefaultEntryHolder {
         static final MapEntryLite<String, SparkProtos.PluginOrModMetadata> defaultEntry = MapEntryLite.newDefaultInstance(
            WireFormat.FieldType.STRING, "", WireFormat.FieldType.MESSAGE, SparkProtos.PluginOrModMetadata.getDefaultInstance()
         );
      }
   }

   public interface HeapMetadataOrBuilder extends MessageLiteOrBuilder {
      boolean hasCreator();

      SparkProtos.CommandSenderMetadata getCreator();

      boolean hasPlatformMetadata();

      SparkProtos.PlatformMetadata getPlatformMetadata();

      boolean hasPlatformStatistics();

      SparkProtos.PlatformStatistics getPlatformStatistics();

      boolean hasSystemStatistics();

      SparkProtos.SystemStatistics getSystemStatistics();

      long getGeneratedTime();

      int getServerConfigurationsCount();

      boolean containsServerConfigurations(String var1);

      @Deprecated
      Map<String, String> getServerConfigurations();

      Map<String, String> getServerConfigurationsMap();

      String getServerConfigurationsOrDefault(String var1, String var2);

      String getServerConfigurationsOrThrow(String var1);

      int getSourcesCount();

      boolean containsSources(String var1);

      @Deprecated
      Map<String, SparkProtos.PluginOrModMetadata> getSources();

      Map<String, SparkProtos.PluginOrModMetadata> getSourcesMap();

      SparkProtos.PluginOrModMetadata getSourcesOrDefault(String var1, SparkProtos.PluginOrModMetadata var2);

      SparkProtos.PluginOrModMetadata getSourcesOrThrow(String var1);

      int getExtraPlatformMetadataCount();

      boolean containsExtraPlatformMetadata(String var1);

      @Deprecated
      Map<String, String> getExtraPlatformMetadata();

      Map<String, String> getExtraPlatformMetadataMap();

      String getExtraPlatformMetadataOrDefault(String var1, String var2);

      String getExtraPlatformMetadataOrThrow(String var1);
   }
}
