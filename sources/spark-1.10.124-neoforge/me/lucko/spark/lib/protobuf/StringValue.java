package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class StringValue extends GeneratedMessageLite<StringValue, StringValue.Builder> implements StringValueOrBuilder {
   public static final int VALUE_FIELD_NUMBER = 1;
   private String value_ = "";
   private static final StringValue DEFAULT_INSTANCE;
   private static volatile Parser<StringValue> PARSER;

   private StringValue() {
   }

   @Override
   public String getValue() {
      return this.value_;
   }

   @Override
   public ByteString getValueBytes() {
      return ByteString.copyFromUtf8(this.value_);
   }

   private void setValue(String value) {
      Class<?> valueClass = value.getClass();
      this.value_ = value;
   }

   private void clearValue() {
      this.value_ = getDefaultInstance().getValue();
   }

   private void setValueBytes(ByteString value) {
      checkByteStringIsUtf8(value);
      this.value_ = value.toStringUtf8();
   }

   public static StringValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static StringValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static StringValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static StringValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static StringValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
   }

   public static StringValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
   }

   public static StringValue parseFrom(InputStream input) throws IOException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static StringValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static StringValue parseDelimitedFrom(InputStream input) throws IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input);
   }

   public static StringValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static StringValue parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
   }

   public static StringValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
   }

   public static StringValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.createBuilder();
   }

   public static StringValue.Builder newBuilder(StringValue prototype) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
      //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:283)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 0: getstatic me/lucko/spark/lib/protobuf/StringValue.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/StringValue;
      // 3: aload 0
      // 4: invokevirtual me/lucko/spark/lib/protobuf/StringValue.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
      // 7: checkcast me/lucko/spark/lib/protobuf/StringValue$Builder
      // a: areturn
   }

   @Override
   protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
      switch (method) {
         case NEW_MUTABLE_INSTANCE:
            return new StringValue();
         case NEW_BUILDER:
            return new StringValue.Builder();
         case BUILD_MESSAGE_INFO:
            Object[] objects = new Object[]{"value_"};
            String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001Ȉ";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
         case GET_DEFAULT_INSTANCE:
            return DEFAULT_INSTANCE;
         case GET_PARSER:
            Parser<StringValue> parser = PARSER;
            if (parser == null) {
               synchronized (StringValue.class) {
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

   public static StringValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static StringValue of(String value) {
      return newBuilder().setValue(value).build();
   }

   public static Parser<StringValue> parser() {
      return DEFAULT_INSTANCE.getParserForType();
   }

   static {
      StringValue defaultInstance = new StringValue();
      DEFAULT_INSTANCE = defaultInstance;
      GeneratedMessageLite.registerDefaultInstance(StringValue.class, defaultInstance);
   }

   public static final class Builder extends GeneratedMessageLite.Builder<StringValue, StringValue.Builder> implements StringValueOrBuilder {
      private Builder() {
         super(StringValue.DEFAULT_INSTANCE);
      }

      @Override
      public String getValue() {
         return this.instance.getValue();
      }

      @Override
      public ByteString getValueBytes() {
         return this.instance.getValueBytes();
      }

      public StringValue.Builder setValue(String value) {
         this.copyOnWrite();
         this.instance.setValue(value);
         return this;
      }

      public StringValue.Builder clearValue() {
         this.copyOnWrite();
         this.instance.clearValue();
         return this;
      }

      public StringValue.Builder setValueBytes(ByteString value) {
         this.copyOnWrite();
         this.instance.setValueBytes(value);
         return this;
      }
   }
}
