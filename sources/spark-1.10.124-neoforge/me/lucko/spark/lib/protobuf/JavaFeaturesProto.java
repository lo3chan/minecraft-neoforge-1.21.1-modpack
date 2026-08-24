package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class JavaFeaturesProto {
   public static final int JAVA_FIELD_NUMBER = 1001;
   public static final GeneratedMessageLite.GeneratedExtension<DescriptorProtos.FeatureSet, JavaFeaturesProto.JavaFeatures> java_ = GeneratedMessageLite.newSingularGeneratedExtension(
      DescriptorProtos.FeatureSet.getDefaultInstance(),
      JavaFeaturesProto.JavaFeatures.getDefaultInstance(),
      JavaFeaturesProto.JavaFeatures.getDefaultInstance(),
      null,
      1001,
      WireFormat.FieldType.MESSAGE,
      JavaFeaturesProto.JavaFeatures.class
   );

   private JavaFeaturesProto() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
      registry.add(java_);
   }

   public static final class JavaFeatures
      extends GeneratedMessageLite<JavaFeaturesProto.JavaFeatures, JavaFeaturesProto.JavaFeatures.Builder>
      implements JavaFeaturesProto.JavaFeaturesOrBuilder {
      private int bitField0_;
      public static final int LEGACY_CLOSED_ENUM_FIELD_NUMBER = 1;
      private boolean legacyClosedEnum_;
      public static final int UTF8_VALIDATION_FIELD_NUMBER = 2;
      private int utf8Validation_;
      private static final JavaFeaturesProto.JavaFeatures DEFAULT_INSTANCE;
      private static volatile Parser<JavaFeaturesProto.JavaFeatures> PARSER;

      private JavaFeatures() {
      }

      @Override
      public boolean hasLegacyClosedEnum() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public boolean getLegacyClosedEnum() {
         return this.legacyClosedEnum_;
      }

      private void setLegacyClosedEnum(boolean value) {
         this.bitField0_ |= 1;
         this.legacyClosedEnum_ = value;
      }

      private void clearLegacyClosedEnum() {
         this.bitField0_ &= -2;
         this.legacyClosedEnum_ = false;
      }

      @Override
      public boolean hasUtf8Validation() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public JavaFeaturesProto.JavaFeatures.Utf8Validation getUtf8Validation() {
         JavaFeaturesProto.JavaFeatures.Utf8Validation result = JavaFeaturesProto.JavaFeatures.Utf8Validation.forNumber(this.utf8Validation_);
         return result == null ? JavaFeaturesProto.JavaFeatures.Utf8Validation.UTF8_VALIDATION_UNKNOWN : result;
      }

      private void setUtf8Validation(JavaFeaturesProto.JavaFeatures.Utf8Validation value) {
         this.utf8Validation_ = value.getNumber();
         this.bitField0_ |= 2;
      }

      private void clearUtf8Validation() {
         this.bitField0_ &= -3;
         this.utf8Validation_ = 0;
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static JavaFeaturesProto.JavaFeatures parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static JavaFeaturesProto.JavaFeatures.Builder newBuilder(JavaFeaturesProto.JavaFeatures prototype) {
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
         // 0: getstatic me/lucko/spark/lib/protobuf/JavaFeaturesProto$JavaFeatures.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/JavaFeaturesProto$JavaFeatures;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/JavaFeaturesProto$JavaFeatures.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/JavaFeaturesProto$JavaFeatures$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new JavaFeaturesProto.JavaFeatures();
            case NEW_BUILDER:
               return new JavaFeaturesProto.JavaFeatures.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_", "legacyClosedEnum_", "utf8Validation_", JavaFeaturesProto.JavaFeatures.Utf8Validation.internalGetVerifier()
               };
               String info = "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001ဇ\u0000\u0002᠌\u0001";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<JavaFeaturesProto.JavaFeatures> parser = PARSER;
               if (parser == null) {
                  synchronized (JavaFeaturesProto.JavaFeatures.class) {
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

      public static JavaFeaturesProto.JavaFeatures getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<JavaFeaturesProto.JavaFeatures> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         JavaFeaturesProto.JavaFeatures defaultInstance = new JavaFeaturesProto.JavaFeatures();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(JavaFeaturesProto.JavaFeatures.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<JavaFeaturesProto.JavaFeatures, JavaFeaturesProto.JavaFeatures.Builder>
         implements JavaFeaturesProto.JavaFeaturesOrBuilder {
         private Builder() {
            super(JavaFeaturesProto.JavaFeatures.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasLegacyClosedEnum() {
            return this.instance.hasLegacyClosedEnum();
         }

         @Override
         public boolean getLegacyClosedEnum() {
            return this.instance.getLegacyClosedEnum();
         }

         public JavaFeaturesProto.JavaFeatures.Builder setLegacyClosedEnum(boolean value) {
            this.copyOnWrite();
            this.instance.setLegacyClosedEnum(value);
            return this;
         }

         public JavaFeaturesProto.JavaFeatures.Builder clearLegacyClosedEnum() {
            this.copyOnWrite();
            this.instance.clearLegacyClosedEnum();
            return this;
         }

         @Override
         public boolean hasUtf8Validation() {
            return this.instance.hasUtf8Validation();
         }

         @Override
         public JavaFeaturesProto.JavaFeatures.Utf8Validation getUtf8Validation() {
            return this.instance.getUtf8Validation();
         }

         public JavaFeaturesProto.JavaFeatures.Builder setUtf8Validation(JavaFeaturesProto.JavaFeatures.Utf8Validation value) {
            this.copyOnWrite();
            this.instance.setUtf8Validation(value);
            return this;
         }

         public JavaFeaturesProto.JavaFeatures.Builder clearUtf8Validation() {
            this.copyOnWrite();
            this.instance.clearUtf8Validation();
            return this;
         }
      }

      public static enum Utf8Validation implements Internal.EnumLite {
         UTF8_VALIDATION_UNKNOWN(0),
         DEFAULT(1),
         VERIFY(2);

         public static final int UTF8_VALIDATION_UNKNOWN_VALUE = 0;
         public static final int DEFAULT_VALUE = 1;
         public static final int VERIFY_VALUE = 2;
         private static final Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.Utf8Validation> internalValueMap = new Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.Utf8Validation>() {
            public JavaFeaturesProto.JavaFeatures.Utf8Validation findValueByNumber(int number) {
               return JavaFeaturesProto.JavaFeatures.Utf8Validation.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static JavaFeaturesProto.JavaFeatures.Utf8Validation valueOf(int value) {
            return forNumber(value);
         }

         public static JavaFeaturesProto.JavaFeatures.Utf8Validation forNumber(int value) {
            switch (value) {
               case 0:
                  return UTF8_VALIDATION_UNKNOWN;
               case 1:
                  return DEFAULT;
               case 2:
                  return VERIFY;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.Utf8Validation> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return JavaFeaturesProto.JavaFeatures.Utf8Validation.Utf8ValidationVerifier.INSTANCE;
         }

         private Utf8Validation(int value) {
            this.value = value;
         }

         private static final class Utf8ValidationVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new JavaFeaturesProto.JavaFeatures.Utf8Validation.Utf8ValidationVerifier();

            @Override
            public boolean isInRange(int number) {
               return JavaFeaturesProto.JavaFeatures.Utf8Validation.forNumber(number) != null;
            }
         }
      }
   }

   public interface JavaFeaturesOrBuilder extends MessageLiteOrBuilder {
      boolean hasLegacyClosedEnum();

      boolean getLegacyClosedEnum();

      boolean hasUtf8Validation();

      JavaFeaturesProto.JavaFeatures.Utf8Validation getUtf8Validation();
   }
}
