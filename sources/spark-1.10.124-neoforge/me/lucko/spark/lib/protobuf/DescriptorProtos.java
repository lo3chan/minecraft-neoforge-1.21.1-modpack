package me.lucko.spark.lib.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public final class DescriptorProtos {
   private DescriptorProtos() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static final class DescriptorProto
      extends GeneratedMessageLite<DescriptorProtos.DescriptorProto, DescriptorProtos.DescriptorProto.Builder>
      implements DescriptorProtos.DescriptorProtoOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_;
      public static final int FIELD_FIELD_NUMBER = 2;
      private Internal.ProtobufList<DescriptorProtos.FieldDescriptorProto> field_;
      public static final int EXTENSION_FIELD_NUMBER = 6;
      private Internal.ProtobufList<DescriptorProtos.FieldDescriptorProto> extension_;
      public static final int NESTED_TYPE_FIELD_NUMBER = 3;
      private Internal.ProtobufList<DescriptorProtos.DescriptorProto> nestedType_;
      public static final int ENUM_TYPE_FIELD_NUMBER = 4;
      private Internal.ProtobufList<DescriptorProtos.EnumDescriptorProto> enumType_;
      public static final int EXTENSION_RANGE_FIELD_NUMBER = 5;
      private Internal.ProtobufList<DescriptorProtos.DescriptorProto.ExtensionRange> extensionRange_;
      public static final int ONEOF_DECL_FIELD_NUMBER = 8;
      private Internal.ProtobufList<DescriptorProtos.OneofDescriptorProto> oneofDecl_;
      public static final int OPTIONS_FIELD_NUMBER = 7;
      private DescriptorProtos.MessageOptions options_;
      public static final int RESERVED_RANGE_FIELD_NUMBER = 9;
      private Internal.ProtobufList<DescriptorProtos.DescriptorProto.ReservedRange> reservedRange_;
      public static final int RESERVED_NAME_FIELD_NUMBER = 10;
      private Internal.ProtobufList<String> reservedName_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.DescriptorProto DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.DescriptorProto> PARSER;

      private DescriptorProto() {
         this.name_ = "";
         this.field_ = emptyProtobufList();
         this.extension_ = emptyProtobufList();
         this.nestedType_ = emptyProtobufList();
         this.enumType_ = emptyProtobufList();
         this.extensionRange_ = emptyProtobufList();
         this.oneofDecl_ = emptyProtobufList();
         this.reservedRange_ = emptyProtobufList();
         this.reservedName_ = GeneratedMessageLite.emptyProtobufList();
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         return this.name_;
      }

      @Override
      public ByteString getNameBytes() {
         return ByteString.copyFromUtf8(this.name_);
      }

      private void setName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.name_ = value;
      }

      private void clearName() {
         this.bitField0_ &= -2;
         this.name_ = getDefaultInstance().getName();
      }

      private void setNameBytes(ByteString value) {
         this.name_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public List<DescriptorProtos.FieldDescriptorProto> getFieldList() {
         return this.field_;
      }

      public List<? extends DescriptorProtos.FieldDescriptorProtoOrBuilder> getFieldOrBuilderList() {
         return this.field_;
      }

      @Override
      public int getFieldCount() {
         return this.field_.size();
      }

      @Override
      public DescriptorProtos.FieldDescriptorProto getField(int index) {
         return this.field_.get(index);
      }

      public DescriptorProtos.FieldDescriptorProtoOrBuilder getFieldOrBuilder(int index) {
         return this.field_.get(index);
      }

      private void ensureFieldIsMutable() {
         Internal.ProtobufList<DescriptorProtos.FieldDescriptorProto> tmp = this.field_;
         if (!tmp.isModifiable()) {
            this.field_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setField(int index, DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureFieldIsMutable();
         this.field_.set(index, value);
      }

      private void addField(DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureFieldIsMutable();
         this.field_.add(value);
      }

      private void addField(int index, DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureFieldIsMutable();
         this.field_.add(index, value);
      }

      private void addAllField(Iterable<? extends DescriptorProtos.FieldDescriptorProto> values) {
         this.ensureFieldIsMutable();
         AbstractMessageLite.addAll(values, this.field_);
      }

      private void clearField() {
         this.field_ = emptyProtobufList();
      }

      private void removeField(int index) {
         this.ensureFieldIsMutable();
         this.field_.remove(index);
      }

      @Override
      public List<DescriptorProtos.FieldDescriptorProto> getExtensionList() {
         return this.extension_;
      }

      public List<? extends DescriptorProtos.FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
         return this.extension_;
      }

      @Override
      public int getExtensionCount() {
         return this.extension_.size();
      }

      @Override
      public DescriptorProtos.FieldDescriptorProto getExtension(int index) {
         return this.extension_.get(index);
      }

      public DescriptorProtos.FieldDescriptorProtoOrBuilder getExtensionOrBuilder(int index) {
         return this.extension_.get(index);
      }

      private void ensureExtensionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.FieldDescriptorProto> tmp = this.extension_;
         if (!tmp.isModifiable()) {
            this.extension_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setExtension(int index, DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureExtensionIsMutable();
         this.extension_.set(index, value);
      }

      private void addExtension(DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureExtensionIsMutable();
         this.extension_.add(value);
      }

      private void addExtension(int index, DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureExtensionIsMutable();
         this.extension_.add(index, value);
      }

      private void addAllExtension(Iterable<? extends DescriptorProtos.FieldDescriptorProto> values) {
         this.ensureExtensionIsMutable();
         AbstractMessageLite.addAll(values, this.extension_);
      }

      private void clearExtension() {
         this.extension_ = emptyProtobufList();
      }

      private void removeExtension(int index) {
         this.ensureExtensionIsMutable();
         this.extension_.remove(index);
      }

      @Override
      public List<DescriptorProtos.DescriptorProto> getNestedTypeList() {
         return this.nestedType_;
      }

      public List<? extends DescriptorProtos.DescriptorProtoOrBuilder> getNestedTypeOrBuilderList() {
         return this.nestedType_;
      }

      @Override
      public int getNestedTypeCount() {
         return this.nestedType_.size();
      }

      @Override
      public DescriptorProtos.DescriptorProto getNestedType(int index) {
         return this.nestedType_.get(index);
      }

      public DescriptorProtos.DescriptorProtoOrBuilder getNestedTypeOrBuilder(int index) {
         return this.nestedType_.get(index);
      }

      private void ensureNestedTypeIsMutable() {
         Internal.ProtobufList<DescriptorProtos.DescriptorProto> tmp = this.nestedType_;
         if (!tmp.isModifiable()) {
            this.nestedType_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setNestedType(int index, DescriptorProtos.DescriptorProto value) {
         value.getClass();
         this.ensureNestedTypeIsMutable();
         this.nestedType_.set(index, value);
      }

      private void addNestedType(DescriptorProtos.DescriptorProto value) {
         value.getClass();
         this.ensureNestedTypeIsMutable();
         this.nestedType_.add(value);
      }

      private void addNestedType(int index, DescriptorProtos.DescriptorProto value) {
         value.getClass();
         this.ensureNestedTypeIsMutable();
         this.nestedType_.add(index, value);
      }

      private void addAllNestedType(Iterable<? extends DescriptorProtos.DescriptorProto> values) {
         this.ensureNestedTypeIsMutable();
         AbstractMessageLite.addAll(values, this.nestedType_);
      }

      private void clearNestedType() {
         this.nestedType_ = emptyProtobufList();
      }

      private void removeNestedType(int index) {
         this.ensureNestedTypeIsMutable();
         this.nestedType_.remove(index);
      }

      @Override
      public List<DescriptorProtos.EnumDescriptorProto> getEnumTypeList() {
         return this.enumType_;
      }

      public List<? extends DescriptorProtos.EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
         return this.enumType_;
      }

      @Override
      public int getEnumTypeCount() {
         return this.enumType_.size();
      }

      @Override
      public DescriptorProtos.EnumDescriptorProto getEnumType(int index) {
         return this.enumType_.get(index);
      }

      public DescriptorProtos.EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(int index) {
         return this.enumType_.get(index);
      }

      private void ensureEnumTypeIsMutable() {
         Internal.ProtobufList<DescriptorProtos.EnumDescriptorProto> tmp = this.enumType_;
         if (!tmp.isModifiable()) {
            this.enumType_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setEnumType(int index, DescriptorProtos.EnumDescriptorProto value) {
         value.getClass();
         this.ensureEnumTypeIsMutable();
         this.enumType_.set(index, value);
      }

      private void addEnumType(DescriptorProtos.EnumDescriptorProto value) {
         value.getClass();
         this.ensureEnumTypeIsMutable();
         this.enumType_.add(value);
      }

      private void addEnumType(int index, DescriptorProtos.EnumDescriptorProto value) {
         value.getClass();
         this.ensureEnumTypeIsMutable();
         this.enumType_.add(index, value);
      }

      private void addAllEnumType(Iterable<? extends DescriptorProtos.EnumDescriptorProto> values) {
         this.ensureEnumTypeIsMutable();
         AbstractMessageLite.addAll(values, this.enumType_);
      }

      private void clearEnumType() {
         this.enumType_ = emptyProtobufList();
      }

      private void removeEnumType(int index) {
         this.ensureEnumTypeIsMutable();
         this.enumType_.remove(index);
      }

      @Override
      public List<DescriptorProtos.DescriptorProto.ExtensionRange> getExtensionRangeList() {
         return this.extensionRange_;
      }

      public List<? extends DescriptorProtos.DescriptorProto.ExtensionRangeOrBuilder> getExtensionRangeOrBuilderList() {
         return this.extensionRange_;
      }

      @Override
      public int getExtensionRangeCount() {
         return this.extensionRange_.size();
      }

      @Override
      public DescriptorProtos.DescriptorProto.ExtensionRange getExtensionRange(int index) {
         return this.extensionRange_.get(index);
      }

      public DescriptorProtos.DescriptorProto.ExtensionRangeOrBuilder getExtensionRangeOrBuilder(int index) {
         return this.extensionRange_.get(index);
      }

      private void ensureExtensionRangeIsMutable() {
         Internal.ProtobufList<DescriptorProtos.DescriptorProto.ExtensionRange> tmp = this.extensionRange_;
         if (!tmp.isModifiable()) {
            this.extensionRange_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setExtensionRange(int index, DescriptorProtos.DescriptorProto.ExtensionRange value) {
         value.getClass();
         this.ensureExtensionRangeIsMutable();
         this.extensionRange_.set(index, value);
      }

      private void addExtensionRange(DescriptorProtos.DescriptorProto.ExtensionRange value) {
         value.getClass();
         this.ensureExtensionRangeIsMutable();
         this.extensionRange_.add(value);
      }

      private void addExtensionRange(int index, DescriptorProtos.DescriptorProto.ExtensionRange value) {
         value.getClass();
         this.ensureExtensionRangeIsMutable();
         this.extensionRange_.add(index, value);
      }

      private void addAllExtensionRange(Iterable<? extends DescriptorProtos.DescriptorProto.ExtensionRange> values) {
         this.ensureExtensionRangeIsMutable();
         AbstractMessageLite.addAll(values, this.extensionRange_);
      }

      private void clearExtensionRange() {
         this.extensionRange_ = emptyProtobufList();
      }

      private void removeExtensionRange(int index) {
         this.ensureExtensionRangeIsMutable();
         this.extensionRange_.remove(index);
      }

      @Override
      public List<DescriptorProtos.OneofDescriptorProto> getOneofDeclList() {
         return this.oneofDecl_;
      }

      public List<? extends DescriptorProtos.OneofDescriptorProtoOrBuilder> getOneofDeclOrBuilderList() {
         return this.oneofDecl_;
      }

      @Override
      public int getOneofDeclCount() {
         return this.oneofDecl_.size();
      }

      @Override
      public DescriptorProtos.OneofDescriptorProto getOneofDecl(int index) {
         return this.oneofDecl_.get(index);
      }

      public DescriptorProtos.OneofDescriptorProtoOrBuilder getOneofDeclOrBuilder(int index) {
         return this.oneofDecl_.get(index);
      }

      private void ensureOneofDeclIsMutable() {
         Internal.ProtobufList<DescriptorProtos.OneofDescriptorProto> tmp = this.oneofDecl_;
         if (!tmp.isModifiable()) {
            this.oneofDecl_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setOneofDecl(int index, DescriptorProtos.OneofDescriptorProto value) {
         value.getClass();
         this.ensureOneofDeclIsMutable();
         this.oneofDecl_.set(index, value);
      }

      private void addOneofDecl(DescriptorProtos.OneofDescriptorProto value) {
         value.getClass();
         this.ensureOneofDeclIsMutable();
         this.oneofDecl_.add(value);
      }

      private void addOneofDecl(int index, DescriptorProtos.OneofDescriptorProto value) {
         value.getClass();
         this.ensureOneofDeclIsMutable();
         this.oneofDecl_.add(index, value);
      }

      private void addAllOneofDecl(Iterable<? extends DescriptorProtos.OneofDescriptorProto> values) {
         this.ensureOneofDeclIsMutable();
         AbstractMessageLite.addAll(values, this.oneofDecl_);
      }

      private void clearOneofDecl() {
         this.oneofDecl_ = emptyProtobufList();
      }

      private void removeOneofDecl(int index) {
         this.ensureOneofDeclIsMutable();
         this.oneofDecl_.remove(index);
      }

      @Override
      public boolean hasOptions() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.MessageOptions getOptions() {
         return this.options_ == null ? DescriptorProtos.MessageOptions.getDefaultInstance() : this.options_;
      }

      private void setOptions(DescriptorProtos.MessageOptions value) {
         value.getClass();
         this.options_ = value;
         this.bitField0_ |= 2;
      }

      private void mergeOptions(DescriptorProtos.MessageOptions value) {
         value.getClass();
         if (this.options_ != null && this.options_ != DescriptorProtos.MessageOptions.getDefaultInstance()) {
            this.options_ = DescriptorProtos.MessageOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
         } else {
            this.options_ = value;
         }

         this.bitField0_ |= 2;
      }

      private void clearOptions() {
         this.options_ = null;
         this.bitField0_ &= -3;
      }

      @Override
      public List<DescriptorProtos.DescriptorProto.ReservedRange> getReservedRangeList() {
         return this.reservedRange_;
      }

      public List<? extends DescriptorProtos.DescriptorProto.ReservedRangeOrBuilder> getReservedRangeOrBuilderList() {
         return this.reservedRange_;
      }

      @Override
      public int getReservedRangeCount() {
         return this.reservedRange_.size();
      }

      @Override
      public DescriptorProtos.DescriptorProto.ReservedRange getReservedRange(int index) {
         return this.reservedRange_.get(index);
      }

      public DescriptorProtos.DescriptorProto.ReservedRangeOrBuilder getReservedRangeOrBuilder(int index) {
         return this.reservedRange_.get(index);
      }

      private void ensureReservedRangeIsMutable() {
         Internal.ProtobufList<DescriptorProtos.DescriptorProto.ReservedRange> tmp = this.reservedRange_;
         if (!tmp.isModifiable()) {
            this.reservedRange_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setReservedRange(int index, DescriptorProtos.DescriptorProto.ReservedRange value) {
         value.getClass();
         this.ensureReservedRangeIsMutable();
         this.reservedRange_.set(index, value);
      }

      private void addReservedRange(DescriptorProtos.DescriptorProto.ReservedRange value) {
         value.getClass();
         this.ensureReservedRangeIsMutable();
         this.reservedRange_.add(value);
      }

      private void addReservedRange(int index, DescriptorProtos.DescriptorProto.ReservedRange value) {
         value.getClass();
         this.ensureReservedRangeIsMutable();
         this.reservedRange_.add(index, value);
      }

      private void addAllReservedRange(Iterable<? extends DescriptorProtos.DescriptorProto.ReservedRange> values) {
         this.ensureReservedRangeIsMutable();
         AbstractMessageLite.addAll(values, this.reservedRange_);
      }

      private void clearReservedRange() {
         this.reservedRange_ = emptyProtobufList();
      }

      private void removeReservedRange(int index) {
         this.ensureReservedRangeIsMutable();
         this.reservedRange_.remove(index);
      }

      @Override
      public List<String> getReservedNameList() {
         return this.reservedName_;
      }

      @Override
      public int getReservedNameCount() {
         return this.reservedName_.size();
      }

      @Override
      public String getReservedName(int index) {
         return this.reservedName_.get(index);
      }

      @Override
      public ByteString getReservedNameBytes(int index) {
         return ByteString.copyFromUtf8(this.reservedName_.get(index));
      }

      private void ensureReservedNameIsMutable() {
         Internal.ProtobufList<String> tmp = this.reservedName_;
         if (!tmp.isModifiable()) {
            this.reservedName_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setReservedName(int index, String value) {
         Class<?> valueClass = value.getClass();
         this.ensureReservedNameIsMutable();
         this.reservedName_.set(index, value);
      }

      private void addReservedName(String value) {
         Class<?> valueClass = value.getClass();
         this.ensureReservedNameIsMutable();
         this.reservedName_.add(value);
      }

      private void addAllReservedName(Iterable<String> values) {
         this.ensureReservedNameIsMutable();
         AbstractMessageLite.addAll(values, this.reservedName_);
      }

      private void clearReservedName() {
         this.reservedName_ = GeneratedMessageLite.emptyProtobufList();
      }

      private void addReservedNameBytes(ByteString value) {
         this.ensureReservedNameIsMutable();
         this.reservedName_.add(value.toStringUtf8());
      }

      public static DescriptorProtos.DescriptorProto parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.DescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.DescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.DescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.DescriptorProto.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.DescriptorProto.Builder newBuilder(DescriptorProtos.DescriptorProto prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.DescriptorProto();
            case NEW_BUILDER:
               return new DescriptorProtos.DescriptorProto.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "name_",
                  "field_",
                  DescriptorProtos.FieldDescriptorProto.class,
                  "nestedType_",
                  DescriptorProtos.DescriptorProto.class,
                  "enumType_",
                  DescriptorProtos.EnumDescriptorProto.class,
                  "extensionRange_",
                  DescriptorProtos.DescriptorProto.ExtensionRange.class,
                  "extension_",
                  DescriptorProtos.FieldDescriptorProto.class,
                  "options_",
                  "oneofDecl_",
                  DescriptorProtos.OneofDescriptorProto.class,
                  "reservedRange_",
                  DescriptorProtos.DescriptorProto.ReservedRange.class,
                  "reservedName_"
               };
               String info = "\u0001\n\u0000\u0001\u0001\n\n\u0000\b\u0007\u0001ဈ\u0000\u0002Л\u0003Л\u0004Л\u0005Л\u0006Л\u0007ᐉ\u0001\bЛ\t\u001b\n\u001a";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.DescriptorProto> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.DescriptorProto.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.DescriptorProto getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.DescriptorProto> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.DescriptorProto defaultInstance = new DescriptorProtos.DescriptorProto();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.DescriptorProto.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.DescriptorProto, DescriptorProtos.DescriptorProto.Builder>
         implements DescriptorProtos.DescriptorProtoOrBuilder {
         private Builder() {
            super(DescriptorProtos.DescriptorProto.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasName() {
            return this.instance.hasName();
         }

         @Override
         public String getName() {
            return this.instance.getName();
         }

         @Override
         public ByteString getNameBytes() {
            return this.instance.getNameBytes();
         }

         public DescriptorProtos.DescriptorProto.Builder setName(String value) {
            this.copyOnWrite();
            this.instance.setName(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setNameBytes(value);
            return this;
         }

         @Override
         public List<DescriptorProtos.FieldDescriptorProto> getFieldList() {
            return Collections.unmodifiableList(this.instance.getFieldList());
         }

         @Override
         public int getFieldCount() {
            return this.instance.getFieldCount();
         }

         @Override
         public DescriptorProtos.FieldDescriptorProto getField(int index) {
            return this.instance.getField(index);
         }

         public DescriptorProtos.DescriptorProto.Builder setField(int index, DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setField(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setField(int index, DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setField(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addField(DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addField(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addField(int index, DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addField(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addField(DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addField(builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addField(int index, DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addField(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addAllField(Iterable<? extends DescriptorProtos.FieldDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllField(values);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearField() {
            this.copyOnWrite();
            this.instance.clearField();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder removeField(int index) {
            this.copyOnWrite();
            this.instance.removeField(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.FieldDescriptorProto> getExtensionList() {
            return Collections.unmodifiableList(this.instance.getExtensionList());
         }

         @Override
         public int getExtensionCount() {
            return this.instance.getExtensionCount();
         }

         @Override
         public DescriptorProtos.FieldDescriptorProto getExtension(int index) {
            return this.instance.getExtension(index);
         }

         public DescriptorProtos.DescriptorProto.Builder setExtension(int index, DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setExtension(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setExtension(int index, DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setExtension(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addExtension(DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addExtension(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addExtension(int index, DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addExtension(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addExtension(DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addExtension(builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addExtension(int index, DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addExtension(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addAllExtension(Iterable<? extends DescriptorProtos.FieldDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllExtension(values);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearExtension() {
            this.copyOnWrite();
            this.instance.clearExtension();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder removeExtension(int index) {
            this.copyOnWrite();
            this.instance.removeExtension(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.DescriptorProto> getNestedTypeList() {
            return Collections.unmodifiableList(this.instance.getNestedTypeList());
         }

         @Override
         public int getNestedTypeCount() {
            return this.instance.getNestedTypeCount();
         }

         @Override
         public DescriptorProtos.DescriptorProto getNestedType(int index) {
            return this.instance.getNestedType(index);
         }

         public DescriptorProtos.DescriptorProto.Builder setNestedType(int index, DescriptorProtos.DescriptorProto value) {
            this.copyOnWrite();
            this.instance.setNestedType(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setNestedType(int index, DescriptorProtos.DescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setNestedType(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addNestedType(DescriptorProtos.DescriptorProto value) {
            this.copyOnWrite();
            this.instance.addNestedType(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addNestedType(int index, DescriptorProtos.DescriptorProto value) {
            this.copyOnWrite();
            this.instance.addNestedType(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addNestedType(DescriptorProtos.DescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addNestedType(builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addNestedType(int index, DescriptorProtos.DescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addNestedType(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addAllNestedType(Iterable<? extends DescriptorProtos.DescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllNestedType(values);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearNestedType() {
            this.copyOnWrite();
            this.instance.clearNestedType();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder removeNestedType(int index) {
            this.copyOnWrite();
            this.instance.removeNestedType(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.EnumDescriptorProto> getEnumTypeList() {
            return Collections.unmodifiableList(this.instance.getEnumTypeList());
         }

         @Override
         public int getEnumTypeCount() {
            return this.instance.getEnumTypeCount();
         }

         @Override
         public DescriptorProtos.EnumDescriptorProto getEnumType(int index) {
            return this.instance.getEnumType(index);
         }

         public DescriptorProtos.DescriptorProto.Builder setEnumType(int index, DescriptorProtos.EnumDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setEnumType(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setEnumType(int index, DescriptorProtos.EnumDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setEnumType(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addEnumType(DescriptorProtos.EnumDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addEnumType(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addEnumType(int index, DescriptorProtos.EnumDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addEnumType(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addEnumType(DescriptorProtos.EnumDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addEnumType(builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addEnumType(int index, DescriptorProtos.EnumDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addEnumType(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addAllEnumType(Iterable<? extends DescriptorProtos.EnumDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllEnumType(values);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearEnumType() {
            this.copyOnWrite();
            this.instance.clearEnumType();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder removeEnumType(int index) {
            this.copyOnWrite();
            this.instance.removeEnumType(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.DescriptorProto.ExtensionRange> getExtensionRangeList() {
            return Collections.unmodifiableList(this.instance.getExtensionRangeList());
         }

         @Override
         public int getExtensionRangeCount() {
            return this.instance.getExtensionRangeCount();
         }

         @Override
         public DescriptorProtos.DescriptorProto.ExtensionRange getExtensionRange(int index) {
            return this.instance.getExtensionRange(index);
         }

         public DescriptorProtos.DescriptorProto.Builder setExtensionRange(int index, DescriptorProtos.DescriptorProto.ExtensionRange value) {
            this.copyOnWrite();
            this.instance.setExtensionRange(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setExtensionRange(int index, DescriptorProtos.DescriptorProto.ExtensionRange.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setExtensionRange(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addExtensionRange(DescriptorProtos.DescriptorProto.ExtensionRange value) {
            this.copyOnWrite();
            this.instance.addExtensionRange(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addExtensionRange(int index, DescriptorProtos.DescriptorProto.ExtensionRange value) {
            this.copyOnWrite();
            this.instance.addExtensionRange(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addExtensionRange(DescriptorProtos.DescriptorProto.ExtensionRange.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addExtensionRange(builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addExtensionRange(int index, DescriptorProtos.DescriptorProto.ExtensionRange.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addExtensionRange(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addAllExtensionRange(Iterable<? extends DescriptorProtos.DescriptorProto.ExtensionRange> values) {
            this.copyOnWrite();
            this.instance.addAllExtensionRange(values);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearExtensionRange() {
            this.copyOnWrite();
            this.instance.clearExtensionRange();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder removeExtensionRange(int index) {
            this.copyOnWrite();
            this.instance.removeExtensionRange(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.OneofDescriptorProto> getOneofDeclList() {
            return Collections.unmodifiableList(this.instance.getOneofDeclList());
         }

         @Override
         public int getOneofDeclCount() {
            return this.instance.getOneofDeclCount();
         }

         @Override
         public DescriptorProtos.OneofDescriptorProto getOneofDecl(int index) {
            return this.instance.getOneofDecl(index);
         }

         public DescriptorProtos.DescriptorProto.Builder setOneofDecl(int index, DescriptorProtos.OneofDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setOneofDecl(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setOneofDecl(int index, DescriptorProtos.OneofDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOneofDecl(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addOneofDecl(DescriptorProtos.OneofDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addOneofDecl(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addOneofDecl(int index, DescriptorProtos.OneofDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addOneofDecl(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addOneofDecl(DescriptorProtos.OneofDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addOneofDecl(builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addOneofDecl(int index, DescriptorProtos.OneofDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addOneofDecl(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addAllOneofDecl(Iterable<? extends DescriptorProtos.OneofDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllOneofDecl(values);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearOneofDecl() {
            this.copyOnWrite();
            this.instance.clearOneofDecl();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder removeOneofDecl(int index) {
            this.copyOnWrite();
            this.instance.removeOneofDecl(index);
            return this;
         }

         @Override
         public boolean hasOptions() {
            return this.instance.hasOptions();
         }

         @Override
         public DescriptorProtos.MessageOptions getOptions() {
            return this.instance.getOptions();
         }

         public DescriptorProtos.DescriptorProto.Builder setOptions(DescriptorProtos.MessageOptions value) {
            this.copyOnWrite();
            this.instance.setOptions(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setOptions(DescriptorProtos.MessageOptions.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOptions(builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder mergeOptions(DescriptorProtos.MessageOptions value) {
            this.copyOnWrite();
            this.instance.mergeOptions(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearOptions() {
            this.copyOnWrite();
            this.instance.clearOptions();
            return this;
         }

         @Override
         public List<DescriptorProtos.DescriptorProto.ReservedRange> getReservedRangeList() {
            return Collections.unmodifiableList(this.instance.getReservedRangeList());
         }

         @Override
         public int getReservedRangeCount() {
            return this.instance.getReservedRangeCount();
         }

         @Override
         public DescriptorProtos.DescriptorProto.ReservedRange getReservedRange(int index) {
            return this.instance.getReservedRange(index);
         }

         public DescriptorProtos.DescriptorProto.Builder setReservedRange(int index, DescriptorProtos.DescriptorProto.ReservedRange value) {
            this.copyOnWrite();
            this.instance.setReservedRange(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder setReservedRange(int index, DescriptorProtos.DescriptorProto.ReservedRange.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setReservedRange(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addReservedRange(DescriptorProtos.DescriptorProto.ReservedRange value) {
            this.copyOnWrite();
            this.instance.addReservedRange(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addReservedRange(int index, DescriptorProtos.DescriptorProto.ReservedRange value) {
            this.copyOnWrite();
            this.instance.addReservedRange(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addReservedRange(DescriptorProtos.DescriptorProto.ReservedRange.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addReservedRange(builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addReservedRange(int index, DescriptorProtos.DescriptorProto.ReservedRange.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addReservedRange(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addAllReservedRange(Iterable<? extends DescriptorProtos.DescriptorProto.ReservedRange> values) {
            this.copyOnWrite();
            this.instance.addAllReservedRange(values);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearReservedRange() {
            this.copyOnWrite();
            this.instance.clearReservedRange();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder removeReservedRange(int index) {
            this.copyOnWrite();
            this.instance.removeReservedRange(index);
            return this;
         }

         @Override
         public List<String> getReservedNameList() {
            return Collections.unmodifiableList(this.instance.getReservedNameList());
         }

         @Override
         public int getReservedNameCount() {
            return this.instance.getReservedNameCount();
         }

         @Override
         public String getReservedName(int index) {
            return this.instance.getReservedName(index);
         }

         @Override
         public ByteString getReservedNameBytes(int index) {
            return this.instance.getReservedNameBytes(index);
         }

         public DescriptorProtos.DescriptorProto.Builder setReservedName(int index, String value) {
            this.copyOnWrite();
            this.instance.setReservedName(index, value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addReservedName(String value) {
            this.copyOnWrite();
            this.instance.addReservedName(value);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addAllReservedName(Iterable<String> values) {
            this.copyOnWrite();
            this.instance.addAllReservedName(values);
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder clearReservedName() {
            this.copyOnWrite();
            this.instance.clearReservedName();
            return this;
         }

         public DescriptorProtos.DescriptorProto.Builder addReservedNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.addReservedNameBytes(value);
            return this;
         }
      }

      public static final class ExtensionRange
         extends GeneratedMessageLite<DescriptorProtos.DescriptorProto.ExtensionRange, DescriptorProtos.DescriptorProto.ExtensionRange.Builder>
         implements DescriptorProtos.DescriptorProto.ExtensionRangeOrBuilder {
         private int bitField0_;
         public static final int START_FIELD_NUMBER = 1;
         private int start_;
         public static final int END_FIELD_NUMBER = 2;
         private int end_;
         public static final int OPTIONS_FIELD_NUMBER = 3;
         private DescriptorProtos.ExtensionRangeOptions options_;
         private byte memoizedIsInitialized = 2;
         private static final DescriptorProtos.DescriptorProto.ExtensionRange DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.DescriptorProto.ExtensionRange> PARSER;

         private ExtensionRange() {
         }

         @Override
         public boolean hasStart() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getStart() {
            return this.start_;
         }

         private void setStart(int value) {
            this.bitField0_ |= 1;
            this.start_ = value;
         }

         private void clearStart() {
            this.bitField0_ &= -2;
            this.start_ = 0;
         }

         @Override
         public boolean hasEnd() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public int getEnd() {
            return this.end_;
         }

         private void setEnd(int value) {
            this.bitField0_ |= 2;
            this.end_ = value;
         }

         private void clearEnd() {
            this.bitField0_ &= -3;
            this.end_ = 0;
         }

         @Override
         public boolean hasOptions() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public DescriptorProtos.ExtensionRangeOptions getOptions() {
            return this.options_ == null ? DescriptorProtos.ExtensionRangeOptions.getDefaultInstance() : this.options_;
         }

         private void setOptions(DescriptorProtos.ExtensionRangeOptions value) {
            value.getClass();
            this.options_ = value;
            this.bitField0_ |= 4;
         }

         private void mergeOptions(DescriptorProtos.ExtensionRangeOptions value) {
            value.getClass();
            if (this.options_ != null && this.options_ != DescriptorProtos.ExtensionRangeOptions.getDefaultInstance()) {
               this.options_ = DescriptorProtos.ExtensionRangeOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
            } else {
               this.options_ = value;
            }

            this.bitField0_ |= 4;
         }

         private void clearOptions() {
            this.options_ = null;
            this.bitField0_ &= -5;
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange.Builder newBuilder(DescriptorProtos.DescriptorProto.ExtensionRange prototype) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.main.DecompilerContext.getCurrentContext(DecompilerContext.java:67)
            //   at org.jetbrains.java.decompiler.main.DecompilerContext.getStructContext(DecompilerContext.java:137)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$ExtensionRange.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$ExtensionRange;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$ExtensionRange.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$ExtensionRange$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.DescriptorProto.ExtensionRange();
               case NEW_BUILDER:
                  return new DescriptorProtos.DescriptorProto.ExtensionRange.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{"bitField0_", "start_", "end_", "options_"};
                  String info = "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0001\u0001င\u0000\u0002င\u0001\u0003ᐉ\u0002";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.DescriptorProto.ExtensionRange> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.DescriptorProto.ExtensionRange.class) {
                        parser = PARSER;
                        if (parser == null) {
                           parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                           PARSER = parser;
                        }
                     }
                  }

                  return parser;
               case GET_MEMOIZED_IS_INITIALIZED:
                  return this.memoizedIsInitialized;
               case SET_MEMOIZED_IS_INITIALIZED:
                  this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
                  return null;
               default:
                  throw new UnsupportedOperationException();
            }
         }

         public static DescriptorProtos.DescriptorProto.ExtensionRange getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.DescriptorProto.ExtensionRange> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.DescriptorProto.ExtensionRange defaultInstance = new DescriptorProtos.DescriptorProto.ExtensionRange();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.DescriptorProto.ExtensionRange.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.DescriptorProto.ExtensionRange, DescriptorProtos.DescriptorProto.ExtensionRange.Builder>
            implements DescriptorProtos.DescriptorProto.ExtensionRangeOrBuilder {
            private Builder() {
               super(DescriptorProtos.DescriptorProto.ExtensionRange.DEFAULT_INSTANCE);
            }

            @Override
            public boolean hasStart() {
               return this.instance.hasStart();
            }

            @Override
            public int getStart() {
               return this.instance.getStart();
            }

            public DescriptorProtos.DescriptorProto.ExtensionRange.Builder setStart(int value) {
               this.copyOnWrite();
               this.instance.setStart(value);
               return this;
            }

            public DescriptorProtos.DescriptorProto.ExtensionRange.Builder clearStart() {
               this.copyOnWrite();
               this.instance.clearStart();
               return this;
            }

            @Override
            public boolean hasEnd() {
               return this.instance.hasEnd();
            }

            @Override
            public int getEnd() {
               return this.instance.getEnd();
            }

            public DescriptorProtos.DescriptorProto.ExtensionRange.Builder setEnd(int value) {
               this.copyOnWrite();
               this.instance.setEnd(value);
               return this;
            }

            public DescriptorProtos.DescriptorProto.ExtensionRange.Builder clearEnd() {
               this.copyOnWrite();
               this.instance.clearEnd();
               return this;
            }

            @Override
            public boolean hasOptions() {
               return this.instance.hasOptions();
            }

            @Override
            public DescriptorProtos.ExtensionRangeOptions getOptions() {
               return this.instance.getOptions();
            }

            public DescriptorProtos.DescriptorProto.ExtensionRange.Builder setOptions(DescriptorProtos.ExtensionRangeOptions value) {
               this.copyOnWrite();
               this.instance.setOptions(value);
               return this;
            }

            public DescriptorProtos.DescriptorProto.ExtensionRange.Builder setOptions(DescriptorProtos.ExtensionRangeOptions.Builder builderForValue) {
               this.copyOnWrite();
               this.instance.setOptions(builderForValue.build());
               return this;
            }

            public DescriptorProtos.DescriptorProto.ExtensionRange.Builder mergeOptions(DescriptorProtos.ExtensionRangeOptions value) {
               this.copyOnWrite();
               this.instance.mergeOptions(value);
               return this;
            }

            public DescriptorProtos.DescriptorProto.ExtensionRange.Builder clearOptions() {
               this.copyOnWrite();
               this.instance.clearOptions();
               return this;
            }
         }
      }

      public interface ExtensionRangeOrBuilder extends MessageLiteOrBuilder {
         boolean hasStart();

         int getStart();

         boolean hasEnd();

         int getEnd();

         boolean hasOptions();

         DescriptorProtos.ExtensionRangeOptions getOptions();
      }

      public static final class ReservedRange
         extends GeneratedMessageLite<DescriptorProtos.DescriptorProto.ReservedRange, DescriptorProtos.DescriptorProto.ReservedRange.Builder>
         implements DescriptorProtos.DescriptorProto.ReservedRangeOrBuilder {
         private int bitField0_;
         public static final int START_FIELD_NUMBER = 1;
         private int start_;
         public static final int END_FIELD_NUMBER = 2;
         private int end_;
         private static final DescriptorProtos.DescriptorProto.ReservedRange DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.DescriptorProto.ReservedRange> PARSER;

         private ReservedRange() {
         }

         @Override
         public boolean hasStart() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getStart() {
            return this.start_;
         }

         private void setStart(int value) {
            this.bitField0_ |= 1;
            this.start_ = value;
         }

         private void clearStart() {
            this.bitField0_ &= -2;
            this.start_ = 0;
         }

         @Override
         public boolean hasEnd() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public int getEnd() {
            return this.end_;
         }

         private void setEnd(int value) {
            this.bitField0_ |= 2;
            this.end_ = value;
         }

         private void clearEnd() {
            this.bitField0_ &= -3;
            this.end_ = 0;
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.DescriptorProto.ReservedRange.Builder newBuilder(DescriptorProtos.DescriptorProto.ReservedRange prototype) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.main.DecompilerContext.getCurrentContext(DecompilerContext.java:67)
            //   at org.jetbrains.java.decompiler.main.DecompilerContext.getStructContext(DecompilerContext.java:137)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$ReservedRange.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$ReservedRange;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$ReservedRange.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$DescriptorProto$ReservedRange$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.DescriptorProto.ReservedRange();
               case NEW_BUILDER:
                  return new DescriptorProtos.DescriptorProto.ReservedRange.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{"bitField0_", "start_", "end_"};
                  String info = "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001င\u0000\u0002င\u0001";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.DescriptorProto.ReservedRange> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.DescriptorProto.ReservedRange.class) {
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

         public static DescriptorProtos.DescriptorProto.ReservedRange getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.DescriptorProto.ReservedRange> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.DescriptorProto.ReservedRange defaultInstance = new DescriptorProtos.DescriptorProto.ReservedRange();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.DescriptorProto.ReservedRange.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.DescriptorProto.ReservedRange, DescriptorProtos.DescriptorProto.ReservedRange.Builder>
            implements DescriptorProtos.DescriptorProto.ReservedRangeOrBuilder {
            private Builder() {
               super(DescriptorProtos.DescriptorProto.ReservedRange.DEFAULT_INSTANCE);
            }

            @Override
            public boolean hasStart() {
               return this.instance.hasStart();
            }

            @Override
            public int getStart() {
               return this.instance.getStart();
            }

            public DescriptorProtos.DescriptorProto.ReservedRange.Builder setStart(int value) {
               this.copyOnWrite();
               this.instance.setStart(value);
               return this;
            }

            public DescriptorProtos.DescriptorProto.ReservedRange.Builder clearStart() {
               this.copyOnWrite();
               this.instance.clearStart();
               return this;
            }

            @Override
            public boolean hasEnd() {
               return this.instance.hasEnd();
            }

            @Override
            public int getEnd() {
               return this.instance.getEnd();
            }

            public DescriptorProtos.DescriptorProto.ReservedRange.Builder setEnd(int value) {
               this.copyOnWrite();
               this.instance.setEnd(value);
               return this;
            }

            public DescriptorProtos.DescriptorProto.ReservedRange.Builder clearEnd() {
               this.copyOnWrite();
               this.instance.clearEnd();
               return this;
            }
         }
      }

      public interface ReservedRangeOrBuilder extends MessageLiteOrBuilder {
         boolean hasStart();

         int getStart();

         boolean hasEnd();

         int getEnd();
      }
   }

   public interface DescriptorProtoOrBuilder extends MessageLiteOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      List<DescriptorProtos.FieldDescriptorProto> getFieldList();

      DescriptorProtos.FieldDescriptorProto getField(int index);

      int getFieldCount();

      List<DescriptorProtos.FieldDescriptorProto> getExtensionList();

      DescriptorProtos.FieldDescriptorProto getExtension(int index);

      int getExtensionCount();

      List<DescriptorProtos.DescriptorProto> getNestedTypeList();

      DescriptorProtos.DescriptorProto getNestedType(int index);

      int getNestedTypeCount();

      List<DescriptorProtos.EnumDescriptorProto> getEnumTypeList();

      DescriptorProtos.EnumDescriptorProto getEnumType(int index);

      int getEnumTypeCount();

      List<DescriptorProtos.DescriptorProto.ExtensionRange> getExtensionRangeList();

      DescriptorProtos.DescriptorProto.ExtensionRange getExtensionRange(int index);

      int getExtensionRangeCount();

      List<DescriptorProtos.OneofDescriptorProto> getOneofDeclList();

      DescriptorProtos.OneofDescriptorProto getOneofDecl(int index);

      int getOneofDeclCount();

      boolean hasOptions();

      DescriptorProtos.MessageOptions getOptions();

      List<DescriptorProtos.DescriptorProto.ReservedRange> getReservedRangeList();

      DescriptorProtos.DescriptorProto.ReservedRange getReservedRange(int index);

      int getReservedRangeCount();

      List<String> getReservedNameList();

      int getReservedNameCount();

      String getReservedName(int index);

      ByteString getReservedNameBytes(int index);
   }

   public static enum Edition implements Internal.EnumLite {
      EDITION_UNKNOWN(0),
      EDITION_LEGACY(900),
      EDITION_PROTO2(998),
      EDITION_PROTO3(999),
      EDITION_2023(1000),
      EDITION_2024(1001),
      EDITION_1_TEST_ONLY(1),
      EDITION_2_TEST_ONLY(2),
      EDITION_99997_TEST_ONLY(99997),
      EDITION_99998_TEST_ONLY(99998),
      EDITION_99999_TEST_ONLY(99999),
      EDITION_MAX(2147483647);

      public static final int EDITION_UNKNOWN_VALUE = 0;
      public static final int EDITION_LEGACY_VALUE = 900;
      public static final int EDITION_PROTO2_VALUE = 998;
      public static final int EDITION_PROTO3_VALUE = 999;
      public static final int EDITION_2023_VALUE = 1000;
      public static final int EDITION_2024_VALUE = 1001;
      public static final int EDITION_1_TEST_ONLY_VALUE = 1;
      public static final int EDITION_2_TEST_ONLY_VALUE = 2;
      public static final int EDITION_99997_TEST_ONLY_VALUE = 99997;
      public static final int EDITION_99998_TEST_ONLY_VALUE = 99998;
      public static final int EDITION_99999_TEST_ONLY_VALUE = 99999;
      public static final int EDITION_MAX_VALUE = 2147483647;
      private static final Internal.EnumLiteMap<DescriptorProtos.Edition> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.Edition>() {
         public DescriptorProtos.Edition findValueByNumber(int number) {
            return DescriptorProtos.Edition.forNumber(number);
         }
      };
      private final int value;

      @Override
      public final int getNumber() {
         return this.value;
      }

      @Deprecated
      public static DescriptorProtos.Edition valueOf(int value) {
         return forNumber(value);
      }

      public static DescriptorProtos.Edition forNumber(int value) {
         switch (value) {
            case 0:
               return EDITION_UNKNOWN;
            case 1:
               return EDITION_1_TEST_ONLY;
            case 2:
               return EDITION_2_TEST_ONLY;
            case 900:
               return EDITION_LEGACY;
            case 998:
               return EDITION_PROTO2;
            case 999:
               return EDITION_PROTO3;
            case 1000:
               return EDITION_2023;
            case 1001:
               return EDITION_2024;
            case 99997:
               return EDITION_99997_TEST_ONLY;
            case 99998:
               return EDITION_99998_TEST_ONLY;
            case 99999:
               return EDITION_99999_TEST_ONLY;
            case 2147483647:
               return EDITION_MAX;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<DescriptorProtos.Edition> internalGetValueMap() {
         return internalValueMap;
      }

      public static Internal.EnumVerifier internalGetVerifier() {
         return DescriptorProtos.Edition.EditionVerifier.INSTANCE;
      }

      private Edition(int value) {
         this.value = value;
      }

      private static final class EditionVerifier implements Internal.EnumVerifier {
         static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.Edition.EditionVerifier();

         @Override
         public boolean isInRange(int number) {
            return DescriptorProtos.Edition.forNumber(number) != null;
         }
      }
   }

   public static final class EnumDescriptorProto
      extends GeneratedMessageLite<DescriptorProtos.EnumDescriptorProto, DescriptorProtos.EnumDescriptorProto.Builder>
      implements DescriptorProtos.EnumDescriptorProtoOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_;
      public static final int VALUE_FIELD_NUMBER = 2;
      private Internal.ProtobufList<DescriptorProtos.EnumValueDescriptorProto> value_;
      public static final int OPTIONS_FIELD_NUMBER = 3;
      private DescriptorProtos.EnumOptions options_;
      public static final int RESERVED_RANGE_FIELD_NUMBER = 4;
      private Internal.ProtobufList<DescriptorProtos.EnumDescriptorProto.EnumReservedRange> reservedRange_;
      public static final int RESERVED_NAME_FIELD_NUMBER = 5;
      private Internal.ProtobufList<String> reservedName_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.EnumDescriptorProto DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.EnumDescriptorProto> PARSER;

      private EnumDescriptorProto() {
         this.name_ = "";
         this.value_ = emptyProtobufList();
         this.reservedRange_ = emptyProtobufList();
         this.reservedName_ = GeneratedMessageLite.emptyProtobufList();
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         return this.name_;
      }

      @Override
      public ByteString getNameBytes() {
         return ByteString.copyFromUtf8(this.name_);
      }

      private void setName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.name_ = value;
      }

      private void clearName() {
         this.bitField0_ &= -2;
         this.name_ = getDefaultInstance().getName();
      }

      private void setNameBytes(ByteString value) {
         this.name_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public List<DescriptorProtos.EnumValueDescriptorProto> getValueList() {
         return this.value_;
      }

      public List<? extends DescriptorProtos.EnumValueDescriptorProtoOrBuilder> getValueOrBuilderList() {
         return this.value_;
      }

      @Override
      public int getValueCount() {
         return this.value_.size();
      }

      @Override
      public DescriptorProtos.EnumValueDescriptorProto getValue(int index) {
         return this.value_.get(index);
      }

      public DescriptorProtos.EnumValueDescriptorProtoOrBuilder getValueOrBuilder(int index) {
         return this.value_.get(index);
      }

      private void ensureValueIsMutable() {
         Internal.ProtobufList<DescriptorProtos.EnumValueDescriptorProto> tmp = this.value_;
         if (!tmp.isModifiable()) {
            this.value_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setValue(int index, DescriptorProtos.EnumValueDescriptorProto value) {
         value.getClass();
         this.ensureValueIsMutable();
         this.value_.set(index, value);
      }

      private void addValue(DescriptorProtos.EnumValueDescriptorProto value) {
         value.getClass();
         this.ensureValueIsMutable();
         this.value_.add(value);
      }

      private void addValue(int index, DescriptorProtos.EnumValueDescriptorProto value) {
         value.getClass();
         this.ensureValueIsMutable();
         this.value_.add(index, value);
      }

      private void addAllValue(Iterable<? extends DescriptorProtos.EnumValueDescriptorProto> values) {
         this.ensureValueIsMutable();
         AbstractMessageLite.addAll(values, this.value_);
      }

      private void clearValue() {
         this.value_ = emptyProtobufList();
      }

      private void removeValue(int index) {
         this.ensureValueIsMutable();
         this.value_.remove(index);
      }

      @Override
      public boolean hasOptions() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.EnumOptions getOptions() {
         return this.options_ == null ? DescriptorProtos.EnumOptions.getDefaultInstance() : this.options_;
      }

      private void setOptions(DescriptorProtos.EnumOptions value) {
         value.getClass();
         this.options_ = value;
         this.bitField0_ |= 2;
      }

      private void mergeOptions(DescriptorProtos.EnumOptions value) {
         value.getClass();
         if (this.options_ != null && this.options_ != DescriptorProtos.EnumOptions.getDefaultInstance()) {
            this.options_ = DescriptorProtos.EnumOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
         } else {
            this.options_ = value;
         }

         this.bitField0_ |= 2;
      }

      private void clearOptions() {
         this.options_ = null;
         this.bitField0_ &= -3;
      }

      @Override
      public List<DescriptorProtos.EnumDescriptorProto.EnumReservedRange> getReservedRangeList() {
         return this.reservedRange_;
      }

      public List<? extends DescriptorProtos.EnumDescriptorProto.EnumReservedRangeOrBuilder> getReservedRangeOrBuilderList() {
         return this.reservedRange_;
      }

      @Override
      public int getReservedRangeCount() {
         return this.reservedRange_.size();
      }

      @Override
      public DescriptorProtos.EnumDescriptorProto.EnumReservedRange getReservedRange(int index) {
         return this.reservedRange_.get(index);
      }

      public DescriptorProtos.EnumDescriptorProto.EnumReservedRangeOrBuilder getReservedRangeOrBuilder(int index) {
         return this.reservedRange_.get(index);
      }

      private void ensureReservedRangeIsMutable() {
         Internal.ProtobufList<DescriptorProtos.EnumDescriptorProto.EnumReservedRange> tmp = this.reservedRange_;
         if (!tmp.isModifiable()) {
            this.reservedRange_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setReservedRange(int index, DescriptorProtos.EnumDescriptorProto.EnumReservedRange value) {
         value.getClass();
         this.ensureReservedRangeIsMutable();
         this.reservedRange_.set(index, value);
      }

      private void addReservedRange(DescriptorProtos.EnumDescriptorProto.EnumReservedRange value) {
         value.getClass();
         this.ensureReservedRangeIsMutable();
         this.reservedRange_.add(value);
      }

      private void addReservedRange(int index, DescriptorProtos.EnumDescriptorProto.EnumReservedRange value) {
         value.getClass();
         this.ensureReservedRangeIsMutable();
         this.reservedRange_.add(index, value);
      }

      private void addAllReservedRange(Iterable<? extends DescriptorProtos.EnumDescriptorProto.EnumReservedRange> values) {
         this.ensureReservedRangeIsMutable();
         AbstractMessageLite.addAll(values, this.reservedRange_);
      }

      private void clearReservedRange() {
         this.reservedRange_ = emptyProtobufList();
      }

      private void removeReservedRange(int index) {
         this.ensureReservedRangeIsMutable();
         this.reservedRange_.remove(index);
      }

      @Override
      public List<String> getReservedNameList() {
         return this.reservedName_;
      }

      @Override
      public int getReservedNameCount() {
         return this.reservedName_.size();
      }

      @Override
      public String getReservedName(int index) {
         return this.reservedName_.get(index);
      }

      @Override
      public ByteString getReservedNameBytes(int index) {
         return ByteString.copyFromUtf8(this.reservedName_.get(index));
      }

      private void ensureReservedNameIsMutable() {
         Internal.ProtobufList<String> tmp = this.reservedName_;
         if (!tmp.isModifiable()) {
            this.reservedName_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setReservedName(int index, String value) {
         Class<?> valueClass = value.getClass();
         this.ensureReservedNameIsMutable();
         this.reservedName_.set(index, value);
      }

      private void addReservedName(String value) {
         Class<?> valueClass = value.getClass();
         this.ensureReservedNameIsMutable();
         this.reservedName_.add(value);
      }

      private void addAllReservedName(Iterable<String> values) {
         this.ensureReservedNameIsMutable();
         AbstractMessageLite.addAll(values, this.reservedName_);
      }

      private void clearReservedName() {
         this.reservedName_ = GeneratedMessageLite.emptyProtobufList();
      }

      private void addReservedNameBytes(ByteString value) {
         this.ensureReservedNameIsMutable();
         this.reservedName_.add(value.toStringUtf8());
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumDescriptorProto.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.EnumDescriptorProto.Builder newBuilder(DescriptorProtos.EnumDescriptorProto prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$EnumDescriptorProto.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$EnumDescriptorProto;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$EnumDescriptorProto.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$EnumDescriptorProto$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.EnumDescriptorProto();
            case NEW_BUILDER:
               return new DescriptorProtos.EnumDescriptorProto.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "name_",
                  "value_",
                  DescriptorProtos.EnumValueDescriptorProto.class,
                  "options_",
                  "reservedRange_",
                  DescriptorProtos.EnumDescriptorProto.EnumReservedRange.class,
                  "reservedName_"
               };
               String info = "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0003\u0002\u0001ဈ\u0000\u0002Л\u0003ᐉ\u0001\u0004\u001b\u0005\u001a";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.EnumDescriptorProto> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.EnumDescriptorProto.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.EnumDescriptorProto getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.EnumDescriptorProto> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.EnumDescriptorProto defaultInstance = new DescriptorProtos.EnumDescriptorProto();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.EnumDescriptorProto.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.EnumDescriptorProto, DescriptorProtos.EnumDescriptorProto.Builder>
         implements DescriptorProtos.EnumDescriptorProtoOrBuilder {
         private Builder() {
            super(DescriptorProtos.EnumDescriptorProto.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasName() {
            return this.instance.hasName();
         }

         @Override
         public String getName() {
            return this.instance.getName();
         }

         @Override
         public ByteString getNameBytes() {
            return this.instance.getNameBytes();
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setName(String value) {
            this.copyOnWrite();
            this.instance.setName(value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setNameBytes(value);
            return this;
         }

         @Override
         public List<DescriptorProtos.EnumValueDescriptorProto> getValueList() {
            return Collections.unmodifiableList(this.instance.getValueList());
         }

         @Override
         public int getValueCount() {
            return this.instance.getValueCount();
         }

         @Override
         public DescriptorProtos.EnumValueDescriptorProto getValue(int index) {
            return this.instance.getValue(index);
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setValue(int index, DescriptorProtos.EnumValueDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setValue(index, value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setValue(int index, DescriptorProtos.EnumValueDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setValue(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addValue(DescriptorProtos.EnumValueDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addValue(value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addValue(int index, DescriptorProtos.EnumValueDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addValue(index, value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addValue(DescriptorProtos.EnumValueDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addValue(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addValue(int index, DescriptorProtos.EnumValueDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addValue(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addAllValue(Iterable<? extends DescriptorProtos.EnumValueDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllValue(values);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder clearValue() {
            this.copyOnWrite();
            this.instance.clearValue();
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder removeValue(int index) {
            this.copyOnWrite();
            this.instance.removeValue(index);
            return this;
         }

         @Override
         public boolean hasOptions() {
            return this.instance.hasOptions();
         }

         @Override
         public DescriptorProtos.EnumOptions getOptions() {
            return this.instance.getOptions();
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setOptions(DescriptorProtos.EnumOptions value) {
            this.copyOnWrite();
            this.instance.setOptions(value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setOptions(DescriptorProtos.EnumOptions.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOptions(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder mergeOptions(DescriptorProtos.EnumOptions value) {
            this.copyOnWrite();
            this.instance.mergeOptions(value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder clearOptions() {
            this.copyOnWrite();
            this.instance.clearOptions();
            return this;
         }

         @Override
         public List<DescriptorProtos.EnumDescriptorProto.EnumReservedRange> getReservedRangeList() {
            return Collections.unmodifiableList(this.instance.getReservedRangeList());
         }

         @Override
         public int getReservedRangeCount() {
            return this.instance.getReservedRangeCount();
         }

         @Override
         public DescriptorProtos.EnumDescriptorProto.EnumReservedRange getReservedRange(int index) {
            return this.instance.getReservedRange(index);
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setReservedRange(int index, DescriptorProtos.EnumDescriptorProto.EnumReservedRange value) {
            this.copyOnWrite();
            this.instance.setReservedRange(index, value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setReservedRange(
            int index, DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder builderForValue
         ) {
            this.copyOnWrite();
            this.instance.setReservedRange(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addReservedRange(DescriptorProtos.EnumDescriptorProto.EnumReservedRange value) {
            this.copyOnWrite();
            this.instance.addReservedRange(value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addReservedRange(int index, DescriptorProtos.EnumDescriptorProto.EnumReservedRange value) {
            this.copyOnWrite();
            this.instance.addReservedRange(index, value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addReservedRange(DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addReservedRange(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addReservedRange(
            int index, DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder builderForValue
         ) {
            this.copyOnWrite();
            this.instance.addReservedRange(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addAllReservedRange(
            Iterable<? extends DescriptorProtos.EnumDescriptorProto.EnumReservedRange> values
         ) {
            this.copyOnWrite();
            this.instance.addAllReservedRange(values);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder clearReservedRange() {
            this.copyOnWrite();
            this.instance.clearReservedRange();
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder removeReservedRange(int index) {
            this.copyOnWrite();
            this.instance.removeReservedRange(index);
            return this;
         }

         @Override
         public List<String> getReservedNameList() {
            return Collections.unmodifiableList(this.instance.getReservedNameList());
         }

         @Override
         public int getReservedNameCount() {
            return this.instance.getReservedNameCount();
         }

         @Override
         public String getReservedName(int index) {
            return this.instance.getReservedName(index);
         }

         @Override
         public ByteString getReservedNameBytes(int index) {
            return this.instance.getReservedNameBytes(index);
         }

         public DescriptorProtos.EnumDescriptorProto.Builder setReservedName(int index, String value) {
            this.copyOnWrite();
            this.instance.setReservedName(index, value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addReservedName(String value) {
            this.copyOnWrite();
            this.instance.addReservedName(value);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addAllReservedName(Iterable<String> values) {
            this.copyOnWrite();
            this.instance.addAllReservedName(values);
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder clearReservedName() {
            this.copyOnWrite();
            this.instance.clearReservedName();
            return this;
         }

         public DescriptorProtos.EnumDescriptorProto.Builder addReservedNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.addReservedNameBytes(value);
            return this;
         }
      }

      public static final class EnumReservedRange
         extends GeneratedMessageLite<DescriptorProtos.EnumDescriptorProto.EnumReservedRange, DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder>
         implements DescriptorProtos.EnumDescriptorProto.EnumReservedRangeOrBuilder {
         private int bitField0_;
         public static final int START_FIELD_NUMBER = 1;
         private int start_;
         public static final int END_FIELD_NUMBER = 2;
         private int end_;
         private static final DescriptorProtos.EnumDescriptorProto.EnumReservedRange DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.EnumDescriptorProto.EnumReservedRange> PARSER;

         private EnumReservedRange() {
         }

         @Override
         public boolean hasStart() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getStart() {
            return this.start_;
         }

         private void setStart(int value) {
            this.bitField0_ |= 1;
            this.start_ = value;
         }

         private void clearStart() {
            this.bitField0_ &= -2;
            this.start_ = 0;
         }

         @Override
         public boolean hasEnd() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public int getEnd() {
            return this.end_;
         }

         private void setEnd(int value) {
            this.bitField0_ |= 2;
            this.end_ = value;
         }

         private void clearEnd() {
            this.bitField0_ &= -3;
            this.end_ = 0;
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder newBuilder(
            DescriptorProtos.EnumDescriptorProto.EnumReservedRange prototype
         ) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.main.DecompilerContext.getCurrentContext(DecompilerContext.java:67)
            //   at org.jetbrains.java.decompiler.main.DecompilerContext.getStructContext(DecompilerContext.java:137)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$EnumDescriptorProto$EnumReservedRange.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$EnumDescriptorProto$EnumReservedRange;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$EnumDescriptorProto$EnumReservedRange.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$EnumDescriptorProto$EnumReservedRange$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.EnumDescriptorProto.EnumReservedRange();
               case NEW_BUILDER:
                  return new DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{"bitField0_", "start_", "end_"};
                  String info = "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001င\u0000\u0002င\u0001";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.EnumDescriptorProto.EnumReservedRange> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.EnumDescriptorProto.EnumReservedRange.class) {
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

         public static DescriptorProtos.EnumDescriptorProto.EnumReservedRange getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.EnumDescriptorProto.EnumReservedRange> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.EnumDescriptorProto.EnumReservedRange defaultInstance = new DescriptorProtos.EnumDescriptorProto.EnumReservedRange();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.EnumDescriptorProto.EnumReservedRange.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.EnumDescriptorProto.EnumReservedRange, DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder>
            implements DescriptorProtos.EnumDescriptorProto.EnumReservedRangeOrBuilder {
            private Builder() {
               super(DescriptorProtos.EnumDescriptorProto.EnumReservedRange.DEFAULT_INSTANCE);
            }

            @Override
            public boolean hasStart() {
               return this.instance.hasStart();
            }

            @Override
            public int getStart() {
               return this.instance.getStart();
            }

            public DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder setStart(int value) {
               this.copyOnWrite();
               this.instance.setStart(value);
               return this;
            }

            public DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder clearStart() {
               this.copyOnWrite();
               this.instance.clearStart();
               return this;
            }

            @Override
            public boolean hasEnd() {
               return this.instance.hasEnd();
            }

            @Override
            public int getEnd() {
               return this.instance.getEnd();
            }

            public DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder setEnd(int value) {
               this.copyOnWrite();
               this.instance.setEnd(value);
               return this;
            }

            public DescriptorProtos.EnumDescriptorProto.EnumReservedRange.Builder clearEnd() {
               this.copyOnWrite();
               this.instance.clearEnd();
               return this;
            }
         }
      }

      public interface EnumReservedRangeOrBuilder extends MessageLiteOrBuilder {
         boolean hasStart();

         int getStart();

         boolean hasEnd();

         int getEnd();
      }
   }

   public interface EnumDescriptorProtoOrBuilder extends MessageLiteOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      List<DescriptorProtos.EnumValueDescriptorProto> getValueList();

      DescriptorProtos.EnumValueDescriptorProto getValue(int index);

      int getValueCount();

      boolean hasOptions();

      DescriptorProtos.EnumOptions getOptions();

      List<DescriptorProtos.EnumDescriptorProto.EnumReservedRange> getReservedRangeList();

      DescriptorProtos.EnumDescriptorProto.EnumReservedRange getReservedRange(int index);

      int getReservedRangeCount();

      List<String> getReservedNameList();

      int getReservedNameCount();

      String getReservedName(int index);

      ByteString getReservedNameBytes(int index);
   }

   public static final class EnumOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.EnumOptions, DescriptorProtos.EnumOptions.Builder>
      implements DescriptorProtos.EnumOptionsOrBuilder {
      private int bitField0_;
      public static final int ALLOW_ALIAS_FIELD_NUMBER = 2;
      private boolean allowAlias_;
      public static final int DEPRECATED_FIELD_NUMBER = 3;
      private boolean deprecated_;
      public static final int DEPRECATED_LEGACY_JSON_FIELD_CONFLICTS_FIELD_NUMBER = 6;
      private boolean deprecatedLegacyJsonFieldConflicts_;
      public static final int FEATURES_FIELD_NUMBER = 7;
      private DescriptorProtos.FeatureSet features_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.EnumOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.EnumOptions> PARSER;

      private EnumOptions() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      @Override
      public boolean hasAllowAlias() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public boolean getAllowAlias() {
         return this.allowAlias_;
      }

      private void setAllowAlias(boolean value) {
         this.bitField0_ |= 1;
         this.allowAlias_ = value;
      }

      private void clearAllowAlias() {
         this.bitField0_ &= -2;
         this.allowAlias_ = false;
      }

      @Override
      public boolean hasDeprecated() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public boolean getDeprecated() {
         return this.deprecated_;
      }

      private void setDeprecated(boolean value) {
         this.bitField0_ |= 2;
         this.deprecated_ = value;
      }

      private void clearDeprecated() {
         this.bitField0_ &= -3;
         this.deprecated_ = false;
      }

      @Deprecated
      @Override
      public boolean hasDeprecatedLegacyJsonFieldConflicts() {
         return (this.bitField0_ & 4) != 0;
      }

      @Deprecated
      @Override
      public boolean getDeprecatedLegacyJsonFieldConflicts() {
         return this.deprecatedLegacyJsonFieldConflicts_;
      }

      /** @deprecated */
      private void setDeprecatedLegacyJsonFieldConflicts(boolean value) {
         this.bitField0_ |= 4;
         this.deprecatedLegacyJsonFieldConflicts_ = value;
      }

      /** @deprecated */
      private void clearDeprecatedLegacyJsonFieldConflicts() {
         this.bitField0_ &= -5;
         this.deprecatedLegacyJsonFieldConflicts_ = false;
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 8;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 8;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -9;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      public static DescriptorProtos.EnumOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.EnumOptions.Builder newBuilder(DescriptorProtos.EnumOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$EnumOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$EnumOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$EnumOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$EnumOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.EnumOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.EnumOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "allowAlias_",
                  "deprecated_",
                  "deprecatedLegacyJsonFieldConflicts_",
                  "features_",
                  "uninterpretedOption_",
                  DescriptorProtos.UninterpretedOption.class
               };
               String info = "\u0001\u0005\u0000\u0001\u0002ϧ\u0005\u0000\u0001\u0002\u0002ဇ\u0000\u0003ဇ\u0001\u0006ဇ\u0002\u0007ᐉ\u0003ϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.EnumOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.EnumOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.EnumOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.EnumOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.EnumOptions defaultInstance = new DescriptorProtos.EnumOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.EnumOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.EnumOptions, DescriptorProtos.EnumOptions.Builder>
         implements DescriptorProtos.EnumOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.EnumOptions.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasAllowAlias() {
            return this.instance.hasAllowAlias();
         }

         @Override
         public boolean getAllowAlias() {
            return this.instance.getAllowAlias();
         }

         public DescriptorProtos.EnumOptions.Builder setAllowAlias(boolean value) {
            this.copyOnWrite();
            this.instance.setAllowAlias(value);
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder clearAllowAlias() {
            this.copyOnWrite();
            this.instance.clearAllowAlias();
            return this;
         }

         @Override
         public boolean hasDeprecated() {
            return this.instance.hasDeprecated();
         }

         @Override
         public boolean getDeprecated() {
            return this.instance.getDeprecated();
         }

         public DescriptorProtos.EnumOptions.Builder setDeprecated(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecated(value);
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder clearDeprecated() {
            this.copyOnWrite();
            this.instance.clearDeprecated();
            return this;
         }

         @Deprecated
         @Override
         public boolean hasDeprecatedLegacyJsonFieldConflicts() {
            return this.instance.hasDeprecatedLegacyJsonFieldConflicts();
         }

         @Deprecated
         @Override
         public boolean getDeprecatedLegacyJsonFieldConflicts() {
            return this.instance.getDeprecatedLegacyJsonFieldConflicts();
         }

         @Deprecated
         public DescriptorProtos.EnumOptions.Builder setDeprecatedLegacyJsonFieldConflicts(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecatedLegacyJsonFieldConflicts(value);
            return this;
         }

         @Deprecated
         public DescriptorProtos.EnumOptions.Builder clearDeprecatedLegacyJsonFieldConflicts() {
            this.copyOnWrite();
            this.instance.clearDeprecatedLegacyJsonFieldConflicts();
            return this;
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.EnumOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.EnumOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.EnumOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }
      }
   }

   public interface EnumOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.EnumOptions, DescriptorProtos.EnumOptions.Builder> {
      boolean hasAllowAlias();

      boolean getAllowAlias();

      boolean hasDeprecated();

      boolean getDeprecated();

      @Deprecated
      boolean hasDeprecatedLegacyJsonFieldConflicts();

      @Deprecated
      boolean getDeprecatedLegacyJsonFieldConflicts();

      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();
   }

   public static final class EnumValueDescriptorProto
      extends GeneratedMessageLite<DescriptorProtos.EnumValueDescriptorProto, DescriptorProtos.EnumValueDescriptorProto.Builder>
      implements DescriptorProtos.EnumValueDescriptorProtoOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_;
      public static final int NUMBER_FIELD_NUMBER = 2;
      private int number_;
      public static final int OPTIONS_FIELD_NUMBER = 3;
      private DescriptorProtos.EnumValueOptions options_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.EnumValueDescriptorProto DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.EnumValueDescriptorProto> PARSER;

      private EnumValueDescriptorProto() {
         this.name_ = "";
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         return this.name_;
      }

      @Override
      public ByteString getNameBytes() {
         return ByteString.copyFromUtf8(this.name_);
      }

      private void setName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.name_ = value;
      }

      private void clearName() {
         this.bitField0_ &= -2;
         this.name_ = getDefaultInstance().getName();
      }

      private void setNameBytes(ByteString value) {
         this.name_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public boolean hasNumber() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public int getNumber() {
         return this.number_;
      }

      private void setNumber(int value) {
         this.bitField0_ |= 2;
         this.number_ = value;
      }

      private void clearNumber() {
         this.bitField0_ &= -3;
         this.number_ = 0;
      }

      @Override
      public boolean hasOptions() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public DescriptorProtos.EnumValueOptions getOptions() {
         return this.options_ == null ? DescriptorProtos.EnumValueOptions.getDefaultInstance() : this.options_;
      }

      private void setOptions(DescriptorProtos.EnumValueOptions value) {
         value.getClass();
         this.options_ = value;
         this.bitField0_ |= 4;
      }

      private void mergeOptions(DescriptorProtos.EnumValueOptions value) {
         value.getClass();
         if (this.options_ != null && this.options_ != DescriptorProtos.EnumValueOptions.getDefaultInstance()) {
            this.options_ = DescriptorProtos.EnumValueOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
         } else {
            this.options_ = value;
         }

         this.bitField0_ |= 4;
      }

      private void clearOptions() {
         this.options_ = null;
         this.bitField0_ &= -5;
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumValueDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueDescriptorProto.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.EnumValueDescriptorProto.Builder newBuilder(DescriptorProtos.EnumValueDescriptorProto prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1734)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$EnumValueDescriptorProto.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$EnumValueDescriptorProto;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$EnumValueDescriptorProto.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$EnumValueDescriptorProto$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.EnumValueDescriptorProto();
            case NEW_BUILDER:
               return new DescriptorProtos.EnumValueDescriptorProto.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"bitField0_", "name_", "number_", "options_"};
               String info = "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0001\u0001ဈ\u0000\u0002င\u0001\u0003ᐉ\u0002";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.EnumValueDescriptorProto> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.EnumValueDescriptorProto.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.EnumValueDescriptorProto getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.EnumValueDescriptorProto> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.EnumValueDescriptorProto defaultInstance = new DescriptorProtos.EnumValueDescriptorProto();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.EnumValueDescriptorProto.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.EnumValueDescriptorProto, DescriptorProtos.EnumValueDescriptorProto.Builder>
         implements DescriptorProtos.EnumValueDescriptorProtoOrBuilder {
         private Builder() {
            super(DescriptorProtos.EnumValueDescriptorProto.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasName() {
            return this.instance.hasName();
         }

         @Override
         public String getName() {
            return this.instance.getName();
         }

         @Override
         public ByteString getNameBytes() {
            return this.instance.getNameBytes();
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder setName(String value) {
            this.copyOnWrite();
            this.instance.setName(value);
            return this;
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setNameBytes(value);
            return this;
         }

         @Override
         public boolean hasNumber() {
            return this.instance.hasNumber();
         }

         @Override
         public int getNumber() {
            return this.instance.getNumber();
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder setNumber(int value) {
            this.copyOnWrite();
            this.instance.setNumber(value);
            return this;
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder clearNumber() {
            this.copyOnWrite();
            this.instance.clearNumber();
            return this;
         }

         @Override
         public boolean hasOptions() {
            return this.instance.hasOptions();
         }

         @Override
         public DescriptorProtos.EnumValueOptions getOptions() {
            return this.instance.getOptions();
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder setOptions(DescriptorProtos.EnumValueOptions value) {
            this.copyOnWrite();
            this.instance.setOptions(value);
            return this;
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder setOptions(DescriptorProtos.EnumValueOptions.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOptions(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder mergeOptions(DescriptorProtos.EnumValueOptions value) {
            this.copyOnWrite();
            this.instance.mergeOptions(value);
            return this;
         }

         public DescriptorProtos.EnumValueDescriptorProto.Builder clearOptions() {
            this.copyOnWrite();
            this.instance.clearOptions();
            return this;
         }
      }
   }

   public interface EnumValueDescriptorProtoOrBuilder extends MessageLiteOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasNumber();

      int getNumber();

      boolean hasOptions();

      DescriptorProtos.EnumValueOptions getOptions();
   }

   public static final class EnumValueOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.EnumValueOptions, DescriptorProtos.EnumValueOptions.Builder>
      implements DescriptorProtos.EnumValueOptionsOrBuilder {
      private int bitField0_;
      public static final int DEPRECATED_FIELD_NUMBER = 1;
      private boolean deprecated_;
      public static final int FEATURES_FIELD_NUMBER = 2;
      private DescriptorProtos.FeatureSet features_;
      public static final int DEBUG_REDACT_FIELD_NUMBER = 3;
      private boolean debugRedact_;
      public static final int FEATURE_SUPPORT_FIELD_NUMBER = 4;
      private DescriptorProtos.FieldOptions.FeatureSupport featureSupport_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.EnumValueOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.EnumValueOptions> PARSER;

      private EnumValueOptions() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      @Override
      public boolean hasDeprecated() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public boolean getDeprecated() {
         return this.deprecated_;
      }

      private void setDeprecated(boolean value) {
         this.bitField0_ |= 1;
         this.deprecated_ = value;
      }

      private void clearDeprecated() {
         this.bitField0_ &= -2;
         this.deprecated_ = false;
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 2;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 2;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -3;
      }

      @Override
      public boolean hasDebugRedact() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public boolean getDebugRedact() {
         return this.debugRedact_;
      }

      private void setDebugRedact(boolean value) {
         this.bitField0_ |= 4;
         this.debugRedact_ = value;
      }

      private void clearDebugRedact() {
         this.bitField0_ &= -5;
         this.debugRedact_ = false;
      }

      @Override
      public boolean hasFeatureSupport() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public DescriptorProtos.FieldOptions.FeatureSupport getFeatureSupport() {
         return this.featureSupport_ == null ? DescriptorProtos.FieldOptions.FeatureSupport.getDefaultInstance() : this.featureSupport_;
      }

      private void setFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport value) {
         value.getClass();
         this.featureSupport_ = value;
         this.bitField0_ |= 8;
      }

      private void mergeFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport value) {
         value.getClass();
         if (this.featureSupport_ != null && this.featureSupport_ != DescriptorProtos.FieldOptions.FeatureSupport.getDefaultInstance()) {
            this.featureSupport_ = DescriptorProtos.FieldOptions.FeatureSupport.newBuilder(this.featureSupport_).mergeFrom(value).buildPartial();
         } else {
            this.featureSupport_ = value;
         }

         this.bitField0_ |= 8;
      }

      private void clearFeatureSupport() {
         this.featureSupport_ = null;
         this.bitField0_ &= -9;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumValueOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.EnumValueOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.EnumValueOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.EnumValueOptions.Builder newBuilder(DescriptorProtos.EnumValueOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$EnumValueOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$EnumValueOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$EnumValueOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$EnumValueOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.EnumValueOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.EnumValueOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "deprecated_",
                  "features_",
                  "debugRedact_",
                  "featureSupport_",
                  "uninterpretedOption_",
                  DescriptorProtos.UninterpretedOption.class
               };
               String info = "\u0001\u0005\u0000\u0001\u0001ϧ\u0005\u0000\u0001\u0002\u0001ဇ\u0000\u0002ᐉ\u0001\u0003ဇ\u0002\u0004ဉ\u0003ϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.EnumValueOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.EnumValueOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.EnumValueOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.EnumValueOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.EnumValueOptions defaultInstance = new DescriptorProtos.EnumValueOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.EnumValueOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.EnumValueOptions, DescriptorProtos.EnumValueOptions.Builder>
         implements DescriptorProtos.EnumValueOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.EnumValueOptions.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasDeprecated() {
            return this.instance.hasDeprecated();
         }

         @Override
         public boolean getDeprecated() {
            return this.instance.getDeprecated();
         }

         public DescriptorProtos.EnumValueOptions.Builder setDeprecated(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecated(value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder clearDeprecated() {
            this.copyOnWrite();
            this.instance.clearDeprecated();
            return this;
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.EnumValueOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public boolean hasDebugRedact() {
            return this.instance.hasDebugRedact();
         }

         @Override
         public boolean getDebugRedact() {
            return this.instance.getDebugRedact();
         }

         public DescriptorProtos.EnumValueOptions.Builder setDebugRedact(boolean value) {
            this.copyOnWrite();
            this.instance.setDebugRedact(value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder clearDebugRedact() {
            this.copyOnWrite();
            this.instance.clearDebugRedact();
            return this;
         }

         @Override
         public boolean hasFeatureSupport() {
            return this.instance.hasFeatureSupport();
         }

         @Override
         public DescriptorProtos.FieldOptions.FeatureSupport getFeatureSupport() {
            return this.instance.getFeatureSupport();
         }

         public DescriptorProtos.EnumValueOptions.Builder setFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport value) {
            this.copyOnWrite();
            this.instance.setFeatureSupport(value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder setFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatureSupport(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder mergeFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport value) {
            this.copyOnWrite();
            this.instance.mergeFeatureSupport(value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder clearFeatureSupport() {
            this.copyOnWrite();
            this.instance.clearFeatureSupport();
            return this;
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.EnumValueOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.EnumValueOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }
      }
   }

   public interface EnumValueOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.EnumValueOptions, DescriptorProtos.EnumValueOptions.Builder> {
      boolean hasDeprecated();

      boolean getDeprecated();

      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      boolean hasDebugRedact();

      boolean getDebugRedact();

      boolean hasFeatureSupport();

      DescriptorProtos.FieldOptions.FeatureSupport getFeatureSupport();

      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();
   }

   public static final class ExtensionRangeOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.ExtensionRangeOptions, DescriptorProtos.ExtensionRangeOptions.Builder>
      implements DescriptorProtos.ExtensionRangeOptionsOrBuilder {
      private int bitField0_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      public static final int DECLARATION_FIELD_NUMBER = 2;
      private Internal.ProtobufList<DescriptorProtos.ExtensionRangeOptions.Declaration> declaration_;
      public static final int FEATURES_FIELD_NUMBER = 50;
      private DescriptorProtos.FeatureSet features_;
      public static final int VERIFICATION_FIELD_NUMBER = 3;
      private int verification_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.ExtensionRangeOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.ExtensionRangeOptions> PARSER;

      private ExtensionRangeOptions() {
         this.uninterpretedOption_ = emptyProtobufList();
         this.declaration_ = emptyProtobufList();
         this.verification_ = 1;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      @Override
      public List<DescriptorProtos.ExtensionRangeOptions.Declaration> getDeclarationList() {
         return this.declaration_;
      }

      public List<? extends DescriptorProtos.ExtensionRangeOptions.DeclarationOrBuilder> getDeclarationOrBuilderList() {
         return this.declaration_;
      }

      @Override
      public int getDeclarationCount() {
         return this.declaration_.size();
      }

      @Override
      public DescriptorProtos.ExtensionRangeOptions.Declaration getDeclaration(int index) {
         return this.declaration_.get(index);
      }

      public DescriptorProtos.ExtensionRangeOptions.DeclarationOrBuilder getDeclarationOrBuilder(int index) {
         return this.declaration_.get(index);
      }

      private void ensureDeclarationIsMutable() {
         Internal.ProtobufList<DescriptorProtos.ExtensionRangeOptions.Declaration> tmp = this.declaration_;
         if (!tmp.isModifiable()) {
            this.declaration_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setDeclaration(int index, DescriptorProtos.ExtensionRangeOptions.Declaration value) {
         value.getClass();
         this.ensureDeclarationIsMutable();
         this.declaration_.set(index, value);
      }

      private void addDeclaration(DescriptorProtos.ExtensionRangeOptions.Declaration value) {
         value.getClass();
         this.ensureDeclarationIsMutable();
         this.declaration_.add(value);
      }

      private void addDeclaration(int index, DescriptorProtos.ExtensionRangeOptions.Declaration value) {
         value.getClass();
         this.ensureDeclarationIsMutable();
         this.declaration_.add(index, value);
      }

      private void addAllDeclaration(Iterable<? extends DescriptorProtos.ExtensionRangeOptions.Declaration> values) {
         this.ensureDeclarationIsMutable();
         AbstractMessageLite.addAll(values, this.declaration_);
      }

      private void clearDeclaration() {
         this.declaration_ = emptyProtobufList();
      }

      private void removeDeclaration(int index) {
         this.ensureDeclarationIsMutable();
         this.declaration_.remove(index);
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 1;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 1;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -2;
      }

      @Override
      public boolean hasVerification() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.ExtensionRangeOptions.VerificationState getVerification() {
         DescriptorProtos.ExtensionRangeOptions.VerificationState result = DescriptorProtos.ExtensionRangeOptions.VerificationState.forNumber(
            this.verification_
         );
         return result == null ? DescriptorProtos.ExtensionRangeOptions.VerificationState.UNVERIFIED : result;
      }

      private void setVerification(DescriptorProtos.ExtensionRangeOptions.VerificationState value) {
         this.verification_ = value.getNumber();
         this.bitField0_ |= 2;
      }

      private void clearVerification() {
         this.bitField0_ &= -3;
         this.verification_ = 1;
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ExtensionRangeOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ExtensionRangeOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.ExtensionRangeOptions.Builder newBuilder(DescriptorProtos.ExtensionRangeOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$ExtensionRangeOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$ExtensionRangeOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$ExtensionRangeOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$ExtensionRangeOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.ExtensionRangeOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.ExtensionRangeOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "declaration_",
                  DescriptorProtos.ExtensionRangeOptions.Declaration.class,
                  "verification_",
                  DescriptorProtos.ExtensionRangeOptions.VerificationState.internalGetVerifier(),
                  "features_",
                  "uninterpretedOption_",
                  DescriptorProtos.UninterpretedOption.class
               };
               String info = "\u0001\u0004\u0000\u0001\u0002ϧ\u0004\u0000\u0002\u0002\u0002\u001b\u0003᠌\u00012ᐉ\u0000ϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.ExtensionRangeOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.ExtensionRangeOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.ExtensionRangeOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.ExtensionRangeOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.ExtensionRangeOptions defaultInstance = new DescriptorProtos.ExtensionRangeOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.ExtensionRangeOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.ExtensionRangeOptions, DescriptorProtos.ExtensionRangeOptions.Builder>
         implements DescriptorProtos.ExtensionRangeOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.ExtensionRangeOptions.DEFAULT_INSTANCE);
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.ExtensionRangeOptions.Declaration> getDeclarationList() {
            return Collections.unmodifiableList(this.instance.getDeclarationList());
         }

         @Override
         public int getDeclarationCount() {
            return this.instance.getDeclarationCount();
         }

         @Override
         public DescriptorProtos.ExtensionRangeOptions.Declaration getDeclaration(int index) {
            return this.instance.getDeclaration(index);
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder setDeclaration(int index, DescriptorProtos.ExtensionRangeOptions.Declaration value) {
            this.copyOnWrite();
            this.instance.setDeclaration(index, value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder setDeclaration(
            int index, DescriptorProtos.ExtensionRangeOptions.Declaration.Builder builderForValue
         ) {
            this.copyOnWrite();
            this.instance.setDeclaration(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addDeclaration(DescriptorProtos.ExtensionRangeOptions.Declaration value) {
            this.copyOnWrite();
            this.instance.addDeclaration(value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addDeclaration(int index, DescriptorProtos.ExtensionRangeOptions.Declaration value) {
            this.copyOnWrite();
            this.instance.addDeclaration(index, value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addDeclaration(DescriptorProtos.ExtensionRangeOptions.Declaration.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addDeclaration(builderForValue.build());
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addDeclaration(
            int index, DescriptorProtos.ExtensionRangeOptions.Declaration.Builder builderForValue
         ) {
            this.copyOnWrite();
            this.instance.addDeclaration(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder addAllDeclaration(Iterable<? extends DescriptorProtos.ExtensionRangeOptions.Declaration> values) {
            this.copyOnWrite();
            this.instance.addAllDeclaration(values);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder clearDeclaration() {
            this.copyOnWrite();
            this.instance.clearDeclaration();
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder removeDeclaration(int index) {
            this.copyOnWrite();
            this.instance.removeDeclaration(index);
            return this;
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public boolean hasVerification() {
            return this.instance.hasVerification();
         }

         @Override
         public DescriptorProtos.ExtensionRangeOptions.VerificationState getVerification() {
            return this.instance.getVerification();
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder setVerification(DescriptorProtos.ExtensionRangeOptions.VerificationState value) {
            this.copyOnWrite();
            this.instance.setVerification(value);
            return this;
         }

         public DescriptorProtos.ExtensionRangeOptions.Builder clearVerification() {
            this.copyOnWrite();
            this.instance.clearVerification();
            return this;
         }
      }

      public static final class Declaration
         extends GeneratedMessageLite<DescriptorProtos.ExtensionRangeOptions.Declaration, DescriptorProtos.ExtensionRangeOptions.Declaration.Builder>
         implements DescriptorProtos.ExtensionRangeOptions.DeclarationOrBuilder {
         private int bitField0_;
         public static final int NUMBER_FIELD_NUMBER = 1;
         private int number_;
         public static final int FULL_NAME_FIELD_NUMBER = 2;
         private String fullName_ = "";
         public static final int TYPE_FIELD_NUMBER = 3;
         private String type_ = "";
         public static final int RESERVED_FIELD_NUMBER = 5;
         private boolean reserved_;
         public static final int REPEATED_FIELD_NUMBER = 6;
         private boolean repeated_;
         private static final DescriptorProtos.ExtensionRangeOptions.Declaration DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.ExtensionRangeOptions.Declaration> PARSER;

         private Declaration() {
         }

         @Override
         public boolean hasNumber() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getNumber() {
            return this.number_;
         }

         private void setNumber(int value) {
            this.bitField0_ |= 1;
            this.number_ = value;
         }

         private void clearNumber() {
            this.bitField0_ &= -2;
            this.number_ = 0;
         }

         @Override
         public boolean hasFullName() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getFullName() {
            return this.fullName_;
         }

         @Override
         public ByteString getFullNameBytes() {
            return ByteString.copyFromUtf8(this.fullName_);
         }

         private void setFullName(String value) {
            Class<?> valueClass = value.getClass();
            this.bitField0_ |= 2;
            this.fullName_ = value;
         }

         private void clearFullName() {
            this.bitField0_ &= -3;
            this.fullName_ = getDefaultInstance().getFullName();
         }

         private void setFullNameBytes(ByteString value) {
            this.fullName_ = value.toStringUtf8();
            this.bitField0_ |= 2;
         }

         @Override
         public boolean hasType() {
            return (this.bitField0_ & 4) != 0;
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
            this.bitField0_ |= 4;
            this.type_ = value;
         }

         private void clearType() {
            this.bitField0_ &= -5;
            this.type_ = getDefaultInstance().getType();
         }

         private void setTypeBytes(ByteString value) {
            this.type_ = value.toStringUtf8();
            this.bitField0_ |= 4;
         }

         @Override
         public boolean hasReserved() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public boolean getReserved() {
            return this.reserved_;
         }

         private void setReserved(boolean value) {
            this.bitField0_ |= 8;
            this.reserved_ = value;
         }

         private void clearReserved() {
            this.bitField0_ &= -9;
            this.reserved_ = false;
         }

         @Override
         public boolean hasRepeated() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public boolean getRepeated() {
            return this.repeated_;
         }

         private void setRepeated(boolean value) {
            this.bitField0_ |= 16;
            this.repeated_ = value;
         }

         private void clearRepeated() {
            this.bitField0_ &= -17;
            this.repeated_ = false;
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.ExtensionRangeOptions.Declaration.Builder newBuilder(DescriptorProtos.ExtensionRangeOptions.Declaration prototype) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$ExtensionRangeOptions$Declaration.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$ExtensionRangeOptions$Declaration;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$ExtensionRangeOptions$Declaration.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$ExtensionRangeOptions$Declaration$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.ExtensionRangeOptions.Declaration();
               case NEW_BUILDER:
                  return new DescriptorProtos.ExtensionRangeOptions.Declaration.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{"bitField0_", "number_", "fullName_", "type_", "reserved_", "repeated_"};
                  String info = "\u0001\u0005\u0000\u0001\u0001\u0006\u0005\u0000\u0000\u0000\u0001င\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0005ဇ\u0003\u0006ဇ\u0004";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.ExtensionRangeOptions.Declaration> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.ExtensionRangeOptions.Declaration.class) {
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

         public static DescriptorProtos.ExtensionRangeOptions.Declaration getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.ExtensionRangeOptions.Declaration> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.ExtensionRangeOptions.Declaration defaultInstance = new DescriptorProtos.ExtensionRangeOptions.Declaration();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.ExtensionRangeOptions.Declaration.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.ExtensionRangeOptions.Declaration, DescriptorProtos.ExtensionRangeOptions.Declaration.Builder>
            implements DescriptorProtos.ExtensionRangeOptions.DeclarationOrBuilder {
            private Builder() {
               super(DescriptorProtos.ExtensionRangeOptions.Declaration.DEFAULT_INSTANCE);
            }

            @Override
            public boolean hasNumber() {
               return this.instance.hasNumber();
            }

            @Override
            public int getNumber() {
               return this.instance.getNumber();
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder setNumber(int value) {
               this.copyOnWrite();
               this.instance.setNumber(value);
               return this;
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder clearNumber() {
               this.copyOnWrite();
               this.instance.clearNumber();
               return this;
            }

            @Override
            public boolean hasFullName() {
               return this.instance.hasFullName();
            }

            @Override
            public String getFullName() {
               return this.instance.getFullName();
            }

            @Override
            public ByteString getFullNameBytes() {
               return this.instance.getFullNameBytes();
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder setFullName(String value) {
               this.copyOnWrite();
               this.instance.setFullName(value);
               return this;
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder clearFullName() {
               this.copyOnWrite();
               this.instance.clearFullName();
               return this;
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder setFullNameBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.setFullNameBytes(value);
               return this;
            }

            @Override
            public boolean hasType() {
               return this.instance.hasType();
            }

            @Override
            public String getType() {
               return this.instance.getType();
            }

            @Override
            public ByteString getTypeBytes() {
               return this.instance.getTypeBytes();
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder setType(String value) {
               this.copyOnWrite();
               this.instance.setType(value);
               return this;
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder clearType() {
               this.copyOnWrite();
               this.instance.clearType();
               return this;
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder setTypeBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.setTypeBytes(value);
               return this;
            }

            @Override
            public boolean hasReserved() {
               return this.instance.hasReserved();
            }

            @Override
            public boolean getReserved() {
               return this.instance.getReserved();
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder setReserved(boolean value) {
               this.copyOnWrite();
               this.instance.setReserved(value);
               return this;
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder clearReserved() {
               this.copyOnWrite();
               this.instance.clearReserved();
               return this;
            }

            @Override
            public boolean hasRepeated() {
               return this.instance.hasRepeated();
            }

            @Override
            public boolean getRepeated() {
               return this.instance.getRepeated();
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder setRepeated(boolean value) {
               this.copyOnWrite();
               this.instance.setRepeated(value);
               return this;
            }

            public DescriptorProtos.ExtensionRangeOptions.Declaration.Builder clearRepeated() {
               this.copyOnWrite();
               this.instance.clearRepeated();
               return this;
            }
         }
      }

      public interface DeclarationOrBuilder extends MessageLiteOrBuilder {
         boolean hasNumber();

         int getNumber();

         boolean hasFullName();

         String getFullName();

         ByteString getFullNameBytes();

         boolean hasType();

         String getType();

         ByteString getTypeBytes();

         boolean hasReserved();

         boolean getReserved();

         boolean hasRepeated();

         boolean getRepeated();
      }

      public static enum VerificationState implements Internal.EnumLite {
         DECLARATION(0),
         UNVERIFIED(1);

         public static final int DECLARATION_VALUE = 0;
         public static final int UNVERIFIED_VALUE = 1;
         private static final Internal.EnumLiteMap<DescriptorProtos.ExtensionRangeOptions.VerificationState> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.ExtensionRangeOptions.VerificationState>() {
            public DescriptorProtos.ExtensionRangeOptions.VerificationState findValueByNumber(int number) {
               return DescriptorProtos.ExtensionRangeOptions.VerificationState.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.ExtensionRangeOptions.VerificationState valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.ExtensionRangeOptions.VerificationState forNumber(int value) {
            switch (value) {
               case 0:
                  return DECLARATION;
               case 1:
                  return UNVERIFIED;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.ExtensionRangeOptions.VerificationState> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.ExtensionRangeOptions.VerificationState.VerificationStateVerifier.INSTANCE;
         }

         private VerificationState(int value) {
            this.value = value;
         }

         private static final class VerificationStateVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.ExtensionRangeOptions.VerificationState.VerificationStateVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.ExtensionRangeOptions.VerificationState.forNumber(number) != null;
            }
         }
      }
   }

   public interface ExtensionRangeOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.ExtensionRangeOptions, DescriptorProtos.ExtensionRangeOptions.Builder> {
      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();

      List<DescriptorProtos.ExtensionRangeOptions.Declaration> getDeclarationList();

      DescriptorProtos.ExtensionRangeOptions.Declaration getDeclaration(int index);

      int getDeclarationCount();

      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      boolean hasVerification();

      DescriptorProtos.ExtensionRangeOptions.VerificationState getVerification();
   }

   public static final class FeatureSet
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.FeatureSet, DescriptorProtos.FeatureSet.Builder>
      implements DescriptorProtos.FeatureSetOrBuilder {
      private int bitField0_;
      public static final int FIELD_PRESENCE_FIELD_NUMBER = 1;
      private int fieldPresence_;
      public static final int ENUM_TYPE_FIELD_NUMBER = 2;
      private int enumType_;
      public static final int REPEATED_FIELD_ENCODING_FIELD_NUMBER = 3;
      private int repeatedFieldEncoding_;
      public static final int UTF8_VALIDATION_FIELD_NUMBER = 4;
      private int utf8Validation_;
      public static final int MESSAGE_ENCODING_FIELD_NUMBER = 5;
      private int messageEncoding_;
      public static final int JSON_FORMAT_FIELD_NUMBER = 6;
      private int jsonFormat_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.FeatureSet DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.FeatureSet> PARSER;

      private FeatureSet() {
      }

      @Override
      public boolean hasFieldPresence() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet.FieldPresence getFieldPresence() {
         DescriptorProtos.FeatureSet.FieldPresence result = DescriptorProtos.FeatureSet.FieldPresence.forNumber(this.fieldPresence_);
         return result == null ? DescriptorProtos.FeatureSet.FieldPresence.FIELD_PRESENCE_UNKNOWN : result;
      }

      private void setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence value) {
         this.fieldPresence_ = value.getNumber();
         this.bitField0_ |= 1;
      }

      private void clearFieldPresence() {
         this.bitField0_ &= -2;
         this.fieldPresence_ = 0;
      }

      @Override
      public boolean hasEnumType() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet.EnumType getEnumType() {
         DescriptorProtos.FeatureSet.EnumType result = DescriptorProtos.FeatureSet.EnumType.forNumber(this.enumType_);
         return result == null ? DescriptorProtos.FeatureSet.EnumType.ENUM_TYPE_UNKNOWN : result;
      }

      private void setEnumType(DescriptorProtos.FeatureSet.EnumType value) {
         this.enumType_ = value.getNumber();
         this.bitField0_ |= 2;
      }

      private void clearEnumType() {
         this.bitField0_ &= -3;
         this.enumType_ = 0;
      }

      @Override
      public boolean hasRepeatedFieldEncoding() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet.RepeatedFieldEncoding getRepeatedFieldEncoding() {
         DescriptorProtos.FeatureSet.RepeatedFieldEncoding result = DescriptorProtos.FeatureSet.RepeatedFieldEncoding.forNumber(this.repeatedFieldEncoding_);
         return result == null ? DescriptorProtos.FeatureSet.RepeatedFieldEncoding.REPEATED_FIELD_ENCODING_UNKNOWN : result;
      }

      private void setRepeatedFieldEncoding(DescriptorProtos.FeatureSet.RepeatedFieldEncoding value) {
         this.repeatedFieldEncoding_ = value.getNumber();
         this.bitField0_ |= 4;
      }

      private void clearRepeatedFieldEncoding() {
         this.bitField0_ &= -5;
         this.repeatedFieldEncoding_ = 0;
      }

      @Override
      public boolean hasUtf8Validation() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet.Utf8Validation getUtf8Validation() {
         DescriptorProtos.FeatureSet.Utf8Validation result = DescriptorProtos.FeatureSet.Utf8Validation.forNumber(this.utf8Validation_);
         return result == null ? DescriptorProtos.FeatureSet.Utf8Validation.UTF8_VALIDATION_UNKNOWN : result;
      }

      private void setUtf8Validation(DescriptorProtos.FeatureSet.Utf8Validation value) {
         this.utf8Validation_ = value.getNumber();
         this.bitField0_ |= 8;
      }

      private void clearUtf8Validation() {
         this.bitField0_ &= -9;
         this.utf8Validation_ = 0;
      }

      @Override
      public boolean hasMessageEncoding() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet.MessageEncoding getMessageEncoding() {
         DescriptorProtos.FeatureSet.MessageEncoding result = DescriptorProtos.FeatureSet.MessageEncoding.forNumber(this.messageEncoding_);
         return result == null ? DescriptorProtos.FeatureSet.MessageEncoding.MESSAGE_ENCODING_UNKNOWN : result;
      }

      private void setMessageEncoding(DescriptorProtos.FeatureSet.MessageEncoding value) {
         this.messageEncoding_ = value.getNumber();
         this.bitField0_ |= 16;
      }

      private void clearMessageEncoding() {
         this.bitField0_ &= -17;
         this.messageEncoding_ = 0;
      }

      @Override
      public boolean hasJsonFormat() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet.JsonFormat getJsonFormat() {
         DescriptorProtos.FeatureSet.JsonFormat result = DescriptorProtos.FeatureSet.JsonFormat.forNumber(this.jsonFormat_);
         return result == null ? DescriptorProtos.FeatureSet.JsonFormat.JSON_FORMAT_UNKNOWN : result;
      }

      private void setJsonFormat(DescriptorProtos.FeatureSet.JsonFormat value) {
         this.jsonFormat_ = value.getNumber();
         this.bitField0_ |= 32;
      }

      private void clearJsonFormat() {
         this.bitField0_ &= -33;
         this.jsonFormat_ = 0;
      }

      public static DescriptorProtos.FeatureSet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FeatureSet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FeatureSet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FeatureSet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSet parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FeatureSet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSet parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FeatureSet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSet parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FeatureSet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSet.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.FeatureSet.Builder newBuilder(DescriptorProtos.FeatureSet prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSet.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSet;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSet.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSet$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.FeatureSet();
            case NEW_BUILDER:
               return new DescriptorProtos.FeatureSet.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "fieldPresence_",
                  DescriptorProtos.FeatureSet.FieldPresence.internalGetVerifier(),
                  "enumType_",
                  DescriptorProtos.FeatureSet.EnumType.internalGetVerifier(),
                  "repeatedFieldEncoding_",
                  DescriptorProtos.FeatureSet.RepeatedFieldEncoding.internalGetVerifier(),
                  "utf8Validation_",
                  DescriptorProtos.FeatureSet.Utf8Validation.internalGetVerifier(),
                  "messageEncoding_",
                  DescriptorProtos.FeatureSet.MessageEncoding.internalGetVerifier(),
                  "jsonFormat_",
                  DescriptorProtos.FeatureSet.JsonFormat.internalGetVerifier()
               };
               String info = "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001᠌\u0000\u0002᠌\u0001\u0003᠌\u0002\u0004᠌\u0003\u0005᠌\u0004\u0006᠌\u0005";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.FeatureSet> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.FeatureSet.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.FeatureSet getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.FeatureSet> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.FeatureSet defaultInstance = new DescriptorProtos.FeatureSet();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FeatureSet.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.FeatureSet, DescriptorProtos.FeatureSet.Builder>
         implements DescriptorProtos.FeatureSetOrBuilder {
         private Builder() {
            super(DescriptorProtos.FeatureSet.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasFieldPresence() {
            return this.instance.hasFieldPresence();
         }

         @Override
         public DescriptorProtos.FeatureSet.FieldPresence getFieldPresence() {
            return this.instance.getFieldPresence();
         }

         public DescriptorProtos.FeatureSet.Builder setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence value) {
            this.copyOnWrite();
            this.instance.setFieldPresence(value);
            return this;
         }

         public DescriptorProtos.FeatureSet.Builder clearFieldPresence() {
            this.copyOnWrite();
            this.instance.clearFieldPresence();
            return this;
         }

         @Override
         public boolean hasEnumType() {
            return this.instance.hasEnumType();
         }

         @Override
         public DescriptorProtos.FeatureSet.EnumType getEnumType() {
            return this.instance.getEnumType();
         }

         public DescriptorProtos.FeatureSet.Builder setEnumType(DescriptorProtos.FeatureSet.EnumType value) {
            this.copyOnWrite();
            this.instance.setEnumType(value);
            return this;
         }

         public DescriptorProtos.FeatureSet.Builder clearEnumType() {
            this.copyOnWrite();
            this.instance.clearEnumType();
            return this;
         }

         @Override
         public boolean hasRepeatedFieldEncoding() {
            return this.instance.hasRepeatedFieldEncoding();
         }

         @Override
         public DescriptorProtos.FeatureSet.RepeatedFieldEncoding getRepeatedFieldEncoding() {
            return this.instance.getRepeatedFieldEncoding();
         }

         public DescriptorProtos.FeatureSet.Builder setRepeatedFieldEncoding(DescriptorProtos.FeatureSet.RepeatedFieldEncoding value) {
            this.copyOnWrite();
            this.instance.setRepeatedFieldEncoding(value);
            return this;
         }

         public DescriptorProtos.FeatureSet.Builder clearRepeatedFieldEncoding() {
            this.copyOnWrite();
            this.instance.clearRepeatedFieldEncoding();
            return this;
         }

         @Override
         public boolean hasUtf8Validation() {
            return this.instance.hasUtf8Validation();
         }

         @Override
         public DescriptorProtos.FeatureSet.Utf8Validation getUtf8Validation() {
            return this.instance.getUtf8Validation();
         }

         public DescriptorProtos.FeatureSet.Builder setUtf8Validation(DescriptorProtos.FeatureSet.Utf8Validation value) {
            this.copyOnWrite();
            this.instance.setUtf8Validation(value);
            return this;
         }

         public DescriptorProtos.FeatureSet.Builder clearUtf8Validation() {
            this.copyOnWrite();
            this.instance.clearUtf8Validation();
            return this;
         }

         @Override
         public boolean hasMessageEncoding() {
            return this.instance.hasMessageEncoding();
         }

         @Override
         public DescriptorProtos.FeatureSet.MessageEncoding getMessageEncoding() {
            return this.instance.getMessageEncoding();
         }

         public DescriptorProtos.FeatureSet.Builder setMessageEncoding(DescriptorProtos.FeatureSet.MessageEncoding value) {
            this.copyOnWrite();
            this.instance.setMessageEncoding(value);
            return this;
         }

         public DescriptorProtos.FeatureSet.Builder clearMessageEncoding() {
            this.copyOnWrite();
            this.instance.clearMessageEncoding();
            return this;
         }

         @Override
         public boolean hasJsonFormat() {
            return this.instance.hasJsonFormat();
         }

         @Override
         public DescriptorProtos.FeatureSet.JsonFormat getJsonFormat() {
            return this.instance.getJsonFormat();
         }

         public DescriptorProtos.FeatureSet.Builder setJsonFormat(DescriptorProtos.FeatureSet.JsonFormat value) {
            this.copyOnWrite();
            this.instance.setJsonFormat(value);
            return this;
         }

         public DescriptorProtos.FeatureSet.Builder clearJsonFormat() {
            this.copyOnWrite();
            this.instance.clearJsonFormat();
            return this;
         }
      }

      public static enum EnumType implements Internal.EnumLite {
         ENUM_TYPE_UNKNOWN(0),
         OPEN(1),
         CLOSED(2);

         public static final int ENUM_TYPE_UNKNOWN_VALUE = 0;
         public static final int OPEN_VALUE = 1;
         public static final int CLOSED_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.FeatureSet.EnumType> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FeatureSet.EnumType>() {
            public DescriptorProtos.FeatureSet.EnumType findValueByNumber(int number) {
               return DescriptorProtos.FeatureSet.EnumType.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FeatureSet.EnumType valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FeatureSet.EnumType forNumber(int value) {
            switch (value) {
               case 0:
                  return ENUM_TYPE_UNKNOWN;
               case 1:
                  return OPEN;
               case 2:
                  return CLOSED;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FeatureSet.EnumType> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FeatureSet.EnumType.EnumTypeVerifier.INSTANCE;
         }

         private EnumType(int value) {
            this.value = value;
         }

         private static final class EnumTypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FeatureSet.EnumType.EnumTypeVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FeatureSet.EnumType.forNumber(number) != null;
            }
         }
      }

      public static enum FieldPresence implements Internal.EnumLite {
         FIELD_PRESENCE_UNKNOWN(0),
         EXPLICIT(1),
         IMPLICIT(2),
         LEGACY_REQUIRED(3);

         public static final int FIELD_PRESENCE_UNKNOWN_VALUE = 0;
         public static final int EXPLICIT_VALUE = 1;
         public static final int IMPLICIT_VALUE = 2;
         public static final int LEGACY_REQUIRED_VALUE = 3;
         private static final Internal.EnumLiteMap<DescriptorProtos.FeatureSet.FieldPresence> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FeatureSet.FieldPresence>() {
            public DescriptorProtos.FeatureSet.FieldPresence findValueByNumber(int number) {
               return DescriptorProtos.FeatureSet.FieldPresence.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FeatureSet.FieldPresence valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FeatureSet.FieldPresence forNumber(int value) {
            switch (value) {
               case 0:
                  return FIELD_PRESENCE_UNKNOWN;
               case 1:
                  return EXPLICIT;
               case 2:
                  return IMPLICIT;
               case 3:
                  return LEGACY_REQUIRED;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FeatureSet.FieldPresence> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FeatureSet.FieldPresence.FieldPresenceVerifier.INSTANCE;
         }

         private FieldPresence(int value) {
            this.value = value;
         }

         private static final class FieldPresenceVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FeatureSet.FieldPresence.FieldPresenceVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FeatureSet.FieldPresence.forNumber(number) != null;
            }
         }
      }

      public static enum JsonFormat implements Internal.EnumLite {
         JSON_FORMAT_UNKNOWN(0),
         ALLOW(1),
         LEGACY_BEST_EFFORT(2);

         public static final int JSON_FORMAT_UNKNOWN_VALUE = 0;
         public static final int ALLOW_VALUE = 1;
         public static final int LEGACY_BEST_EFFORT_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.FeatureSet.JsonFormat> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FeatureSet.JsonFormat>() {
            public DescriptorProtos.FeatureSet.JsonFormat findValueByNumber(int number) {
               return DescriptorProtos.FeatureSet.JsonFormat.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FeatureSet.JsonFormat valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FeatureSet.JsonFormat forNumber(int value) {
            switch (value) {
               case 0:
                  return JSON_FORMAT_UNKNOWN;
               case 1:
                  return ALLOW;
               case 2:
                  return LEGACY_BEST_EFFORT;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FeatureSet.JsonFormat> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FeatureSet.JsonFormat.JsonFormatVerifier.INSTANCE;
         }

         private JsonFormat(int value) {
            this.value = value;
         }

         private static final class JsonFormatVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FeatureSet.JsonFormat.JsonFormatVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FeatureSet.JsonFormat.forNumber(number) != null;
            }
         }
      }

      public static enum MessageEncoding implements Internal.EnumLite {
         MESSAGE_ENCODING_UNKNOWN(0),
         LENGTH_PREFIXED(1),
         DELIMITED(2);

         public static final int MESSAGE_ENCODING_UNKNOWN_VALUE = 0;
         public static final int LENGTH_PREFIXED_VALUE = 1;
         public static final int DELIMITED_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.FeatureSet.MessageEncoding> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FeatureSet.MessageEncoding>() {
            public DescriptorProtos.FeatureSet.MessageEncoding findValueByNumber(int number) {
               return DescriptorProtos.FeatureSet.MessageEncoding.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FeatureSet.MessageEncoding valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FeatureSet.MessageEncoding forNumber(int value) {
            switch (value) {
               case 0:
                  return MESSAGE_ENCODING_UNKNOWN;
               case 1:
                  return LENGTH_PREFIXED;
               case 2:
                  return DELIMITED;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FeatureSet.MessageEncoding> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FeatureSet.MessageEncoding.MessageEncodingVerifier.INSTANCE;
         }

         private MessageEncoding(int value) {
            this.value = value;
         }

         private static final class MessageEncodingVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FeatureSet.MessageEncoding.MessageEncodingVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FeatureSet.MessageEncoding.forNumber(number) != null;
            }
         }
      }

      public static enum RepeatedFieldEncoding implements Internal.EnumLite {
         REPEATED_FIELD_ENCODING_UNKNOWN(0),
         PACKED(1),
         EXPANDED(2);

         public static final int REPEATED_FIELD_ENCODING_UNKNOWN_VALUE = 0;
         public static final int PACKED_VALUE = 1;
         public static final int EXPANDED_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.FeatureSet.RepeatedFieldEncoding> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FeatureSet.RepeatedFieldEncoding>() {
            public DescriptorProtos.FeatureSet.RepeatedFieldEncoding findValueByNumber(int number) {
               return DescriptorProtos.FeatureSet.RepeatedFieldEncoding.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FeatureSet.RepeatedFieldEncoding valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FeatureSet.RepeatedFieldEncoding forNumber(int value) {
            switch (value) {
               case 0:
                  return REPEATED_FIELD_ENCODING_UNKNOWN;
               case 1:
                  return PACKED;
               case 2:
                  return EXPANDED;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FeatureSet.RepeatedFieldEncoding> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FeatureSet.RepeatedFieldEncoding.RepeatedFieldEncodingVerifier.INSTANCE;
         }

         private RepeatedFieldEncoding(int value) {
            this.value = value;
         }

         private static final class RepeatedFieldEncodingVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FeatureSet.RepeatedFieldEncoding.RepeatedFieldEncodingVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FeatureSet.RepeatedFieldEncoding.forNumber(number) != null;
            }
         }
      }

      public static enum Utf8Validation implements Internal.EnumLite {
         UTF8_VALIDATION_UNKNOWN(0),
         VERIFY(2),
         NONE(3);

         public static final int UTF8_VALIDATION_UNKNOWN_VALUE = 0;
         public static final int VERIFY_VALUE = 2;
         public static final int NONE_VALUE = 3;
         private static final Internal.EnumLiteMap<DescriptorProtos.FeatureSet.Utf8Validation> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FeatureSet.Utf8Validation>() {
            public DescriptorProtos.FeatureSet.Utf8Validation findValueByNumber(int number) {
               return DescriptorProtos.FeatureSet.Utf8Validation.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FeatureSet.Utf8Validation valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FeatureSet.Utf8Validation forNumber(int value) {
            switch (value) {
               case 0:
                  return UTF8_VALIDATION_UNKNOWN;
               case 1:
               default:
                  return null;
               case 2:
                  return VERIFY;
               case 3:
                  return NONE;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FeatureSet.Utf8Validation> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FeatureSet.Utf8Validation.Utf8ValidationVerifier.INSTANCE;
         }

         private Utf8Validation(int value) {
            this.value = value;
         }

         private static final class Utf8ValidationVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FeatureSet.Utf8Validation.Utf8ValidationVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FeatureSet.Utf8Validation.forNumber(number) != null;
            }
         }
      }
   }

   public static final class FeatureSetDefaults
      extends GeneratedMessageLite<DescriptorProtos.FeatureSetDefaults, DescriptorProtos.FeatureSetDefaults.Builder>
      implements DescriptorProtos.FeatureSetDefaultsOrBuilder {
      private int bitField0_;
      public static final int DEFAULTS_FIELD_NUMBER = 1;
      private Internal.ProtobufList<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> defaults_;
      public static final int MINIMUM_EDITION_FIELD_NUMBER = 4;
      private int minimumEdition_;
      public static final int MAXIMUM_EDITION_FIELD_NUMBER = 5;
      private int maximumEdition_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.FeatureSetDefaults DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.FeatureSetDefaults> PARSER;

      private FeatureSetDefaults() {
         this.defaults_ = emptyProtobufList();
      }

      @Override
      public List<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> getDefaultsList() {
         return this.defaults_;
      }

      public List<? extends DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefaultOrBuilder> getDefaultsOrBuilderList() {
         return this.defaults_;
      }

      @Override
      public int getDefaultsCount() {
         return this.defaults_.size();
      }

      @Override
      public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault getDefaults(int index) {
         return this.defaults_.get(index);
      }

      public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefaultOrBuilder getDefaultsOrBuilder(int index) {
         return this.defaults_.get(index);
      }

      private void ensureDefaultsIsMutable() {
         Internal.ProtobufList<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> tmp = this.defaults_;
         if (!tmp.isModifiable()) {
            this.defaults_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setDefaults(int index, DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault value) {
         value.getClass();
         this.ensureDefaultsIsMutable();
         this.defaults_.set(index, value);
      }

      private void addDefaults(DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault value) {
         value.getClass();
         this.ensureDefaultsIsMutable();
         this.defaults_.add(value);
      }

      private void addDefaults(int index, DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault value) {
         value.getClass();
         this.ensureDefaultsIsMutable();
         this.defaults_.add(index, value);
      }

      private void addAllDefaults(Iterable<? extends DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> values) {
         this.ensureDefaultsIsMutable();
         AbstractMessageLite.addAll(values, this.defaults_);
      }

      private void clearDefaults() {
         this.defaults_ = emptyProtobufList();
      }

      private void removeDefaults(int index) {
         this.ensureDefaultsIsMutable();
         this.defaults_.remove(index);
      }

      @Override
      public boolean hasMinimumEdition() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public DescriptorProtos.Edition getMinimumEdition() {
         DescriptorProtos.Edition result = DescriptorProtos.Edition.forNumber(this.minimumEdition_);
         return result == null ? DescriptorProtos.Edition.EDITION_UNKNOWN : result;
      }

      private void setMinimumEdition(DescriptorProtos.Edition value) {
         this.minimumEdition_ = value.getNumber();
         this.bitField0_ |= 1;
      }

      private void clearMinimumEdition() {
         this.bitField0_ &= -2;
         this.minimumEdition_ = 0;
      }

      @Override
      public boolean hasMaximumEdition() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.Edition getMaximumEdition() {
         DescriptorProtos.Edition result = DescriptorProtos.Edition.forNumber(this.maximumEdition_);
         return result == null ? DescriptorProtos.Edition.EDITION_UNKNOWN : result;
      }

      private void setMaximumEdition(DescriptorProtos.Edition value) {
         this.maximumEdition_ = value.getNumber();
         this.bitField0_ |= 2;
      }

      private void clearMaximumEdition() {
         this.bitField0_ &= -3;
         this.maximumEdition_ = 0;
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSetDefaults parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FeatureSetDefaults parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FeatureSetDefaults parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FeatureSetDefaults.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.FeatureSetDefaults.Builder newBuilder(DescriptorProtos.FeatureSetDefaults prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSetDefaults.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSetDefaults;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSetDefaults.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSetDefaults$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.FeatureSetDefaults();
            case NEW_BUILDER:
               return new DescriptorProtos.FeatureSetDefaults.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "defaults_",
                  DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.class,
                  "minimumEdition_",
                  DescriptorProtos.Edition.internalGetVerifier(),
                  "maximumEdition_",
                  DescriptorProtos.Edition.internalGetVerifier()
               };
               String info = "\u0001\u0003\u0000\u0001\u0001\u0005\u0003\u0000\u0001\u0001\u0001Л\u0004᠌\u0000\u0005᠌\u0001";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.FeatureSetDefaults> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.FeatureSetDefaults.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.FeatureSetDefaults getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.FeatureSetDefaults> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.FeatureSetDefaults defaultInstance = new DescriptorProtos.FeatureSetDefaults();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FeatureSetDefaults.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.FeatureSetDefaults, DescriptorProtos.FeatureSetDefaults.Builder>
         implements DescriptorProtos.FeatureSetDefaultsOrBuilder {
         private Builder() {
            super(DescriptorProtos.FeatureSetDefaults.DEFAULT_INSTANCE);
         }

         @Override
         public List<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> getDefaultsList() {
            return Collections.unmodifiableList(this.instance.getDefaultsList());
         }

         @Override
         public int getDefaultsCount() {
            return this.instance.getDefaultsCount();
         }

         @Override
         public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault getDefaults(int index) {
            return this.instance.getDefaults(index);
         }

         public DescriptorProtos.FeatureSetDefaults.Builder setDefaults(int index, DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault value) {
            this.copyOnWrite();
            this.instance.setDefaults(index, value);
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder setDefaults(
            int index, DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder builderForValue
         ) {
            this.copyOnWrite();
            this.instance.setDefaults(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder addDefaults(DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault value) {
            this.copyOnWrite();
            this.instance.addDefaults(value);
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder addDefaults(int index, DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault value) {
            this.copyOnWrite();
            this.instance.addDefaults(index, value);
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder addDefaults(DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addDefaults(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder addDefaults(
            int index, DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder builderForValue
         ) {
            this.copyOnWrite();
            this.instance.addDefaults(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder addAllDefaults(
            Iterable<? extends DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> values
         ) {
            this.copyOnWrite();
            this.instance.addAllDefaults(values);
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder clearDefaults() {
            this.copyOnWrite();
            this.instance.clearDefaults();
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder removeDefaults(int index) {
            this.copyOnWrite();
            this.instance.removeDefaults(index);
            return this;
         }

         @Override
         public boolean hasMinimumEdition() {
            return this.instance.hasMinimumEdition();
         }

         @Override
         public DescriptorProtos.Edition getMinimumEdition() {
            return this.instance.getMinimumEdition();
         }

         public DescriptorProtos.FeatureSetDefaults.Builder setMinimumEdition(DescriptorProtos.Edition value) {
            this.copyOnWrite();
            this.instance.setMinimumEdition(value);
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder clearMinimumEdition() {
            this.copyOnWrite();
            this.instance.clearMinimumEdition();
            return this;
         }

         @Override
         public boolean hasMaximumEdition() {
            return this.instance.hasMaximumEdition();
         }

         @Override
         public DescriptorProtos.Edition getMaximumEdition() {
            return this.instance.getMaximumEdition();
         }

         public DescriptorProtos.FeatureSetDefaults.Builder setMaximumEdition(DescriptorProtos.Edition value) {
            this.copyOnWrite();
            this.instance.setMaximumEdition(value);
            return this;
         }

         public DescriptorProtos.FeatureSetDefaults.Builder clearMaximumEdition() {
            this.copyOnWrite();
            this.instance.clearMaximumEdition();
            return this;
         }
      }

      public static final class FeatureSetEditionDefault
         extends GeneratedMessageLite<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault, DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder>
         implements DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefaultOrBuilder {
         private int bitField0_;
         public static final int EDITION_FIELD_NUMBER = 3;
         private int edition_;
         public static final int OVERRIDABLE_FEATURES_FIELD_NUMBER = 4;
         private DescriptorProtos.FeatureSet overridableFeatures_;
         public static final int FIXED_FEATURES_FIELD_NUMBER = 5;
         private DescriptorProtos.FeatureSet fixedFeatures_;
         private byte memoizedIsInitialized = 2;
         private static final DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> PARSER;

         private FeatureSetEditionDefault() {
         }

         @Override
         public boolean hasEdition() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public DescriptorProtos.Edition getEdition() {
            DescriptorProtos.Edition result = DescriptorProtos.Edition.forNumber(this.edition_);
            return result == null ? DescriptorProtos.Edition.EDITION_UNKNOWN : result;
         }

         private void setEdition(DescriptorProtos.Edition value) {
            this.edition_ = value.getNumber();
            this.bitField0_ |= 1;
         }

         private void clearEdition() {
            this.bitField0_ &= -2;
            this.edition_ = 0;
         }

         @Override
         public boolean hasOverridableFeatures() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public DescriptorProtos.FeatureSet getOverridableFeatures() {
            return this.overridableFeatures_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.overridableFeatures_;
         }

         private void setOverridableFeatures(DescriptorProtos.FeatureSet value) {
            value.getClass();
            this.overridableFeatures_ = value;
            this.bitField0_ |= 2;
         }

         private void mergeOverridableFeatures(DescriptorProtos.FeatureSet value) {
            value.getClass();
            if (this.overridableFeatures_ != null && this.overridableFeatures_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
               this.overridableFeatures_ = DescriptorProtos.FeatureSet.newBuilder(this.overridableFeatures_).mergeFrom(value).buildPartial();
            } else {
               this.overridableFeatures_ = value;
            }

            this.bitField0_ |= 2;
         }

         private void clearOverridableFeatures() {
            this.overridableFeatures_ = null;
            this.bitField0_ &= -3;
         }

         @Override
         public boolean hasFixedFeatures() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public DescriptorProtos.FeatureSet getFixedFeatures() {
            return this.fixedFeatures_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.fixedFeatures_;
         }

         private void setFixedFeatures(DescriptorProtos.FeatureSet value) {
            value.getClass();
            this.fixedFeatures_ = value;
            this.bitField0_ |= 4;
         }

         private void mergeFixedFeatures(DescriptorProtos.FeatureSet value) {
            value.getClass();
            if (this.fixedFeatures_ != null && this.fixedFeatures_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
               this.fixedFeatures_ = DescriptorProtos.FeatureSet.newBuilder(this.fixedFeatures_).mergeFrom(value).buildPartial();
            } else {
               this.fixedFeatures_ = value;
            }

            this.bitField0_ |= 4;
         }

         private void clearFixedFeatures() {
            this.fixedFeatures_ = null;
            this.bitField0_ &= -5;
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseDelimitedFrom(
            InputStream input, ExtensionRegistryLite extensionRegistry
         ) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder newBuilder(
            DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault prototype
         ) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSetDefaults$FeatureSetEditionDefault.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSetDefaults$FeatureSetEditionDefault;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSetDefaults$FeatureSetEditionDefault.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FeatureSetDefaults$FeatureSetEditionDefault$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault();
               case NEW_BUILDER:
                  return new DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{
                     "bitField0_", "edition_", DescriptorProtos.Edition.internalGetVerifier(), "overridableFeatures_", "fixedFeatures_"
                  };
                  String info = "\u0001\u0003\u0000\u0001\u0003\u0005\u0003\u0000\u0000\u0002\u0003᠌\u0000\u0004ᐉ\u0001\u0005ᐉ\u0002";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.class) {
                        parser = PARSER;
                        if (parser == null) {
                           parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                           PARSER = parser;
                        }
                     }
                  }

                  return parser;
               case GET_MEMOIZED_IS_INITIALIZED:
                  return this.memoizedIsInitialized;
               case SET_MEMOIZED_IS_INITIALIZED:
                  this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
                  return null;
               default:
                  throw new UnsupportedOperationException();
            }
         }

         public static DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault defaultInstance = new DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault, DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder>
            implements DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefaultOrBuilder {
            private Builder() {
               super(DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.DEFAULT_INSTANCE);
            }

            @Override
            public boolean hasEdition() {
               return this.instance.hasEdition();
            }

            @Override
            public DescriptorProtos.Edition getEdition() {
               return this.instance.getEdition();
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder setEdition(DescriptorProtos.Edition value) {
               this.copyOnWrite();
               this.instance.setEdition(value);
               return this;
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder clearEdition() {
               this.copyOnWrite();
               this.instance.clearEdition();
               return this;
            }

            @Override
            public boolean hasOverridableFeatures() {
               return this.instance.hasOverridableFeatures();
            }

            @Override
            public DescriptorProtos.FeatureSet getOverridableFeatures() {
               return this.instance.getOverridableFeatures();
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder setOverridableFeatures(DescriptorProtos.FeatureSet value) {
               this.copyOnWrite();
               this.instance.setOverridableFeatures(value);
               return this;
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder setOverridableFeatures(
               DescriptorProtos.FeatureSet.Builder builderForValue
            ) {
               this.copyOnWrite();
               this.instance.setOverridableFeatures(builderForValue.build());
               return this;
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder mergeOverridableFeatures(DescriptorProtos.FeatureSet value) {
               this.copyOnWrite();
               this.instance.mergeOverridableFeatures(value);
               return this;
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder clearOverridableFeatures() {
               this.copyOnWrite();
               this.instance.clearOverridableFeatures();
               return this;
            }

            @Override
            public boolean hasFixedFeatures() {
               return this.instance.hasFixedFeatures();
            }

            @Override
            public DescriptorProtos.FeatureSet getFixedFeatures() {
               return this.instance.getFixedFeatures();
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder setFixedFeatures(DescriptorProtos.FeatureSet value) {
               this.copyOnWrite();
               this.instance.setFixedFeatures(value);
               return this;
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder setFixedFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
               this.copyOnWrite();
               this.instance.setFixedFeatures(builderForValue.build());
               return this;
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder mergeFixedFeatures(DescriptorProtos.FeatureSet value) {
               this.copyOnWrite();
               this.instance.mergeFixedFeatures(value);
               return this;
            }

            public DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault.Builder clearFixedFeatures() {
               this.copyOnWrite();
               this.instance.clearFixedFeatures();
               return this;
            }
         }
      }

      public interface FeatureSetEditionDefaultOrBuilder extends MessageLiteOrBuilder {
         boolean hasEdition();

         DescriptorProtos.Edition getEdition();

         boolean hasOverridableFeatures();

         DescriptorProtos.FeatureSet getOverridableFeatures();

         boolean hasFixedFeatures();

         DescriptorProtos.FeatureSet getFixedFeatures();
      }
   }

   public interface FeatureSetDefaultsOrBuilder extends MessageLiteOrBuilder {
      List<DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault> getDefaultsList();

      DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault getDefaults(int index);

      int getDefaultsCount();

      boolean hasMinimumEdition();

      DescriptorProtos.Edition getMinimumEdition();

      boolean hasMaximumEdition();

      DescriptorProtos.Edition getMaximumEdition();
   }

   public interface FeatureSetOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.FeatureSet, DescriptorProtos.FeatureSet.Builder> {
      boolean hasFieldPresence();

      DescriptorProtos.FeatureSet.FieldPresence getFieldPresence();

      boolean hasEnumType();

      DescriptorProtos.FeatureSet.EnumType getEnumType();

      boolean hasRepeatedFieldEncoding();

      DescriptorProtos.FeatureSet.RepeatedFieldEncoding getRepeatedFieldEncoding();

      boolean hasUtf8Validation();

      DescriptorProtos.FeatureSet.Utf8Validation getUtf8Validation();

      boolean hasMessageEncoding();

      DescriptorProtos.FeatureSet.MessageEncoding getMessageEncoding();

      boolean hasJsonFormat();

      DescriptorProtos.FeatureSet.JsonFormat getJsonFormat();
   }

   public static final class FieldDescriptorProto
      extends GeneratedMessageLite<DescriptorProtos.FieldDescriptorProto, DescriptorProtos.FieldDescriptorProto.Builder>
      implements DescriptorProtos.FieldDescriptorProtoOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_;
      public static final int NUMBER_FIELD_NUMBER = 3;
      private int number_;
      public static final int LABEL_FIELD_NUMBER = 4;
      private int label_;
      public static final int TYPE_FIELD_NUMBER = 5;
      private int type_;
      public static final int TYPE_NAME_FIELD_NUMBER = 6;
      private String typeName_;
      public static final int EXTENDEE_FIELD_NUMBER = 2;
      private String extendee_;
      public static final int DEFAULT_VALUE_FIELD_NUMBER = 7;
      private String defaultValue_;
      public static final int ONEOF_INDEX_FIELD_NUMBER = 9;
      private int oneofIndex_;
      public static final int JSON_NAME_FIELD_NUMBER = 10;
      private String jsonName_;
      public static final int OPTIONS_FIELD_NUMBER = 8;
      private DescriptorProtos.FieldOptions options_;
      public static final int PROTO3_OPTIONAL_FIELD_NUMBER = 17;
      private boolean proto3Optional_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.FieldDescriptorProto DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.FieldDescriptorProto> PARSER;

      private FieldDescriptorProto() {
         this.name_ = "";
         this.label_ = 1;
         this.type_ = 1;
         this.typeName_ = "";
         this.extendee_ = "";
         this.defaultValue_ = "";
         this.jsonName_ = "";
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         return this.name_;
      }

      @Override
      public ByteString getNameBytes() {
         return ByteString.copyFromUtf8(this.name_);
      }

      private void setName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.name_ = value;
      }

      private void clearName() {
         this.bitField0_ &= -2;
         this.name_ = getDefaultInstance().getName();
      }

      private void setNameBytes(ByteString value) {
         this.name_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public boolean hasNumber() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public int getNumber() {
         return this.number_;
      }

      private void setNumber(int value) {
         this.bitField0_ |= 2;
         this.number_ = value;
      }

      private void clearNumber() {
         this.bitField0_ &= -3;
         this.number_ = 0;
      }

      @Override
      public boolean hasLabel() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public DescriptorProtos.FieldDescriptorProto.Label getLabel() {
         DescriptorProtos.FieldDescriptorProto.Label result = DescriptorProtos.FieldDescriptorProto.Label.forNumber(this.label_);
         return result == null ? DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL : result;
      }

      private void setLabel(DescriptorProtos.FieldDescriptorProto.Label value) {
         this.label_ = value.getNumber();
         this.bitField0_ |= 4;
      }

      private void clearLabel() {
         this.bitField0_ &= -5;
         this.label_ = 1;
      }

      @Override
      public boolean hasType() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public DescriptorProtos.FieldDescriptorProto.Type getType() {
         DescriptorProtos.FieldDescriptorProto.Type result = DescriptorProtos.FieldDescriptorProto.Type.forNumber(this.type_);
         return result == null ? DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE : result;
      }

      private void setType(DescriptorProtos.FieldDescriptorProto.Type value) {
         this.type_ = value.getNumber();
         this.bitField0_ |= 8;
      }

      private void clearType() {
         this.bitField0_ &= -9;
         this.type_ = 1;
      }

      @Override
      public boolean hasTypeName() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public String getTypeName() {
         return this.typeName_;
      }

      @Override
      public ByteString getTypeNameBytes() {
         return ByteString.copyFromUtf8(this.typeName_);
      }

      private void setTypeName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 16;
         this.typeName_ = value;
      }

      private void clearTypeName() {
         this.bitField0_ &= -17;
         this.typeName_ = getDefaultInstance().getTypeName();
      }

      private void setTypeNameBytes(ByteString value) {
         this.typeName_ = value.toStringUtf8();
         this.bitField0_ |= 16;
      }

      @Override
      public boolean hasExtendee() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public String getExtendee() {
         return this.extendee_;
      }

      @Override
      public ByteString getExtendeeBytes() {
         return ByteString.copyFromUtf8(this.extendee_);
      }

      private void setExtendee(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 32;
         this.extendee_ = value;
      }

      private void clearExtendee() {
         this.bitField0_ &= -33;
         this.extendee_ = getDefaultInstance().getExtendee();
      }

      private void setExtendeeBytes(ByteString value) {
         this.extendee_ = value.toStringUtf8();
         this.bitField0_ |= 32;
      }

      @Override
      public boolean hasDefaultValue() {
         return (this.bitField0_ & 64) != 0;
      }

      @Override
      public String getDefaultValue() {
         return this.defaultValue_;
      }

      @Override
      public ByteString getDefaultValueBytes() {
         return ByteString.copyFromUtf8(this.defaultValue_);
      }

      private void setDefaultValue(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 64;
         this.defaultValue_ = value;
      }

      private void clearDefaultValue() {
         this.bitField0_ &= -65;
         this.defaultValue_ = getDefaultInstance().getDefaultValue();
      }

      private void setDefaultValueBytes(ByteString value) {
         this.defaultValue_ = value.toStringUtf8();
         this.bitField0_ |= 64;
      }

      @Override
      public boolean hasOneofIndex() {
         return (this.bitField0_ & 128) != 0;
      }

      @Override
      public int getOneofIndex() {
         return this.oneofIndex_;
      }

      private void setOneofIndex(int value) {
         this.bitField0_ |= 128;
         this.oneofIndex_ = value;
      }

      private void clearOneofIndex() {
         this.bitField0_ &= -129;
         this.oneofIndex_ = 0;
      }

      @Override
      public boolean hasJsonName() {
         return (this.bitField0_ & 256) != 0;
      }

      @Override
      public String getJsonName() {
         return this.jsonName_;
      }

      @Override
      public ByteString getJsonNameBytes() {
         return ByteString.copyFromUtf8(this.jsonName_);
      }

      private void setJsonName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 256;
         this.jsonName_ = value;
      }

      private void clearJsonName() {
         this.bitField0_ &= -257;
         this.jsonName_ = getDefaultInstance().getJsonName();
      }

      private void setJsonNameBytes(ByteString value) {
         this.jsonName_ = value.toStringUtf8();
         this.bitField0_ |= 256;
      }

      @Override
      public boolean hasOptions() {
         return (this.bitField0_ & 512) != 0;
      }

      @Override
      public DescriptorProtos.FieldOptions getOptions() {
         return this.options_ == null ? DescriptorProtos.FieldOptions.getDefaultInstance() : this.options_;
      }

      private void setOptions(DescriptorProtos.FieldOptions value) {
         value.getClass();
         this.options_ = value;
         this.bitField0_ |= 512;
      }

      private void mergeOptions(DescriptorProtos.FieldOptions value) {
         value.getClass();
         if (this.options_ != null && this.options_ != DescriptorProtos.FieldOptions.getDefaultInstance()) {
            this.options_ = DescriptorProtos.FieldOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
         } else {
            this.options_ = value;
         }

         this.bitField0_ |= 512;
      }

      private void clearOptions() {
         this.options_ = null;
         this.bitField0_ &= -513;
      }

      @Override
      public boolean hasProto3Optional() {
         return (this.bitField0_ & 1024) != 0;
      }

      @Override
      public boolean getProto3Optional() {
         return this.proto3Optional_;
      }

      private void setProto3Optional(boolean value) {
         this.bitField0_ |= 1024;
         this.proto3Optional_ = value;
      }

      private void clearProto3Optional() {
         this.bitField0_ &= -1025;
         this.proto3Optional_ = false;
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FieldDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FieldDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FieldDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FieldDescriptorProto.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.FieldDescriptorProto.Builder newBuilder(DescriptorProtos.FieldDescriptorProto prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FieldDescriptorProto.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FieldDescriptorProto;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FieldDescriptorProto.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FieldDescriptorProto$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.FieldDescriptorProto();
            case NEW_BUILDER:
               return new DescriptorProtos.FieldDescriptorProto.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "name_",
                  "extendee_",
                  "number_",
                  "label_",
                  DescriptorProtos.FieldDescriptorProto.Label.internalGetVerifier(),
                  "type_",
                  DescriptorProtos.FieldDescriptorProto.Type.internalGetVerifier(),
                  "typeName_",
                  "defaultValue_",
                  "options_",
                  "oneofIndex_",
                  "jsonName_",
                  "proto3Optional_"
               };
               String info = "\u0001\u000b\u0000\u0001\u0001\u0011\u000b\u0000\u0000\u0001\u0001ဈ\u0000\u0002ဈ\u0005\u0003င\u0001\u0004᠌\u0002\u0005᠌\u0003\u0006ဈ\u0004\u0007ဈ\u0006\bᐉ\t\tင\u0007\nဈ\b\u0011ဇ\n";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.FieldDescriptorProto> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.FieldDescriptorProto.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.FieldDescriptorProto getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.FieldDescriptorProto> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.FieldDescriptorProto defaultInstance = new DescriptorProtos.FieldDescriptorProto();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FieldDescriptorProto.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.FieldDescriptorProto, DescriptorProtos.FieldDescriptorProto.Builder>
         implements DescriptorProtos.FieldDescriptorProtoOrBuilder {
         private Builder() {
            super(DescriptorProtos.FieldDescriptorProto.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasName() {
            return this.instance.hasName();
         }

         @Override
         public String getName() {
            return this.instance.getName();
         }

         @Override
         public ByteString getNameBytes() {
            return this.instance.getNameBytes();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setName(String value) {
            this.copyOnWrite();
            this.instance.setName(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setNameBytes(value);
            return this;
         }

         @Override
         public boolean hasNumber() {
            return this.instance.hasNumber();
         }

         @Override
         public int getNumber() {
            return this.instance.getNumber();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setNumber(int value) {
            this.copyOnWrite();
            this.instance.setNumber(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearNumber() {
            this.copyOnWrite();
            this.instance.clearNumber();
            return this;
         }

         @Override
         public boolean hasLabel() {
            return this.instance.hasLabel();
         }

         @Override
         public DescriptorProtos.FieldDescriptorProto.Label getLabel() {
            return this.instance.getLabel();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setLabel(DescriptorProtos.FieldDescriptorProto.Label value) {
            this.copyOnWrite();
            this.instance.setLabel(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearLabel() {
            this.copyOnWrite();
            this.instance.clearLabel();
            return this;
         }

         @Override
         public boolean hasType() {
            return this.instance.hasType();
         }

         @Override
         public DescriptorProtos.FieldDescriptorProto.Type getType() {
            return this.instance.getType();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setType(DescriptorProtos.FieldDescriptorProto.Type value) {
            this.copyOnWrite();
            this.instance.setType(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearType() {
            this.copyOnWrite();
            this.instance.clearType();
            return this;
         }

         @Override
         public boolean hasTypeName() {
            return this.instance.hasTypeName();
         }

         @Override
         public String getTypeName() {
            return this.instance.getTypeName();
         }

         @Override
         public ByteString getTypeNameBytes() {
            return this.instance.getTypeNameBytes();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setTypeName(String value) {
            this.copyOnWrite();
            this.instance.setTypeName(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearTypeName() {
            this.copyOnWrite();
            this.instance.clearTypeName();
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setTypeNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setTypeNameBytes(value);
            return this;
         }

         @Override
         public boolean hasExtendee() {
            return this.instance.hasExtendee();
         }

         @Override
         public String getExtendee() {
            return this.instance.getExtendee();
         }

         @Override
         public ByteString getExtendeeBytes() {
            return this.instance.getExtendeeBytes();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setExtendee(String value) {
            this.copyOnWrite();
            this.instance.setExtendee(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearExtendee() {
            this.copyOnWrite();
            this.instance.clearExtendee();
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setExtendeeBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setExtendeeBytes(value);
            return this;
         }

         @Override
         public boolean hasDefaultValue() {
            return this.instance.hasDefaultValue();
         }

         @Override
         public String getDefaultValue() {
            return this.instance.getDefaultValue();
         }

         @Override
         public ByteString getDefaultValueBytes() {
            return this.instance.getDefaultValueBytes();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setDefaultValue(String value) {
            this.copyOnWrite();
            this.instance.setDefaultValue(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearDefaultValue() {
            this.copyOnWrite();
            this.instance.clearDefaultValue();
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setDefaultValueBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setDefaultValueBytes(value);
            return this;
         }

         @Override
         public boolean hasOneofIndex() {
            return this.instance.hasOneofIndex();
         }

         @Override
         public int getOneofIndex() {
            return this.instance.getOneofIndex();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setOneofIndex(int value) {
            this.copyOnWrite();
            this.instance.setOneofIndex(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearOneofIndex() {
            this.copyOnWrite();
            this.instance.clearOneofIndex();
            return this;
         }

         @Override
         public boolean hasJsonName() {
            return this.instance.hasJsonName();
         }

         @Override
         public String getJsonName() {
            return this.instance.getJsonName();
         }

         @Override
         public ByteString getJsonNameBytes() {
            return this.instance.getJsonNameBytes();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setJsonName(String value) {
            this.copyOnWrite();
            this.instance.setJsonName(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearJsonName() {
            this.copyOnWrite();
            this.instance.clearJsonName();
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setJsonNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setJsonNameBytes(value);
            return this;
         }

         @Override
         public boolean hasOptions() {
            return this.instance.hasOptions();
         }

         @Override
         public DescriptorProtos.FieldOptions getOptions() {
            return this.instance.getOptions();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setOptions(DescriptorProtos.FieldOptions value) {
            this.copyOnWrite();
            this.instance.setOptions(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setOptions(DescriptorProtos.FieldOptions.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOptions(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder mergeOptions(DescriptorProtos.FieldOptions value) {
            this.copyOnWrite();
            this.instance.mergeOptions(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearOptions() {
            this.copyOnWrite();
            this.instance.clearOptions();
            return this;
         }

         @Override
         public boolean hasProto3Optional() {
            return this.instance.hasProto3Optional();
         }

         @Override
         public boolean getProto3Optional() {
            return this.instance.getProto3Optional();
         }

         public DescriptorProtos.FieldDescriptorProto.Builder setProto3Optional(boolean value) {
            this.copyOnWrite();
            this.instance.setProto3Optional(value);
            return this;
         }

         public DescriptorProtos.FieldDescriptorProto.Builder clearProto3Optional() {
            this.copyOnWrite();
            this.instance.clearProto3Optional();
            return this;
         }
      }

      public static enum Label implements Internal.EnumLite {
         LABEL_OPTIONAL(1),
         LABEL_REPEATED(3),
         LABEL_REQUIRED(2);

         public static final int LABEL_OPTIONAL_VALUE = 1;
         public static final int LABEL_REPEATED_VALUE = 3;
         public static final int LABEL_REQUIRED_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.FieldDescriptorProto.Label> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FieldDescriptorProto.Label>() {
            public DescriptorProtos.FieldDescriptorProto.Label findValueByNumber(int number) {
               return DescriptorProtos.FieldDescriptorProto.Label.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FieldDescriptorProto.Label valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FieldDescriptorProto.Label forNumber(int value) {
            switch (value) {
               case 1:
                  return LABEL_OPTIONAL;
               case 2:
                  return LABEL_REQUIRED;
               case 3:
                  return LABEL_REPEATED;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FieldDescriptorProto.Label> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FieldDescriptorProto.Label.LabelVerifier.INSTANCE;
         }

         private Label(int value) {
            this.value = value;
         }

         private static final class LabelVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FieldDescriptorProto.Label.LabelVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FieldDescriptorProto.Label.forNumber(number) != null;
            }
         }
      }

      public static enum Type implements Internal.EnumLite {
         TYPE_DOUBLE(1),
         TYPE_FLOAT(2),
         TYPE_INT64(3),
         TYPE_UINT64(4),
         TYPE_INT32(5),
         TYPE_FIXED64(6),
         TYPE_FIXED32(7),
         TYPE_BOOL(8),
         TYPE_STRING(9),
         TYPE_GROUP(10),
         TYPE_MESSAGE(11),
         TYPE_BYTES(12),
         TYPE_UINT32(13),
         TYPE_ENUM(14),
         TYPE_SFIXED32(15),
         TYPE_SFIXED64(16),
         TYPE_SINT32(17),
         TYPE_SINT64(18);

         public static final int TYPE_DOUBLE_VALUE = 1;
         public static final int TYPE_FLOAT_VALUE = 2;
         public static final int TYPE_INT64_VALUE = 3;
         public static final int TYPE_UINT64_VALUE = 4;
         public static final int TYPE_INT32_VALUE = 5;
         public static final int TYPE_FIXED64_VALUE = 6;
         public static final int TYPE_FIXED32_VALUE = 7;
         public static final int TYPE_BOOL_VALUE = 8;
         public static final int TYPE_STRING_VALUE = 9;
         public static final int TYPE_GROUP_VALUE = 10;
         public static final int TYPE_MESSAGE_VALUE = 11;
         public static final int TYPE_BYTES_VALUE = 12;
         public static final int TYPE_UINT32_VALUE = 13;
         public static final int TYPE_ENUM_VALUE = 14;
         public static final int TYPE_SFIXED32_VALUE = 15;
         public static final int TYPE_SFIXED64_VALUE = 16;
         public static final int TYPE_SINT32_VALUE = 17;
         public static final int TYPE_SINT64_VALUE = 18;
         private static final Internal.EnumLiteMap<DescriptorProtos.FieldDescriptorProto.Type> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FieldDescriptorProto.Type>() {
            public DescriptorProtos.FieldDescriptorProto.Type findValueByNumber(int number) {
               return DescriptorProtos.FieldDescriptorProto.Type.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FieldDescriptorProto.Type valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FieldDescriptorProto.Type forNumber(int value) {
            switch (value) {
               case 1:
                  return TYPE_DOUBLE;
               case 2:
                  return TYPE_FLOAT;
               case 3:
                  return TYPE_INT64;
               case 4:
                  return TYPE_UINT64;
               case 5:
                  return TYPE_INT32;
               case 6:
                  return TYPE_FIXED64;
               case 7:
                  return TYPE_FIXED32;
               case 8:
                  return TYPE_BOOL;
               case 9:
                  return TYPE_STRING;
               case 10:
                  return TYPE_GROUP;
               case 11:
                  return TYPE_MESSAGE;
               case 12:
                  return TYPE_BYTES;
               case 13:
                  return TYPE_UINT32;
               case 14:
                  return TYPE_ENUM;
               case 15:
                  return TYPE_SFIXED32;
               case 16:
                  return TYPE_SFIXED64;
               case 17:
                  return TYPE_SINT32;
               case 18:
                  return TYPE_SINT64;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FieldDescriptorProto.Type> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FieldDescriptorProto.Type.TypeVerifier.INSTANCE;
         }

         private Type(int value) {
            this.value = value;
         }

         private static final class TypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FieldDescriptorProto.Type.TypeVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FieldDescriptorProto.Type.forNumber(number) != null;
            }
         }
      }
   }

   public interface FieldDescriptorProtoOrBuilder extends MessageLiteOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasNumber();

      int getNumber();

      boolean hasLabel();

      DescriptorProtos.FieldDescriptorProto.Label getLabel();

      boolean hasType();

      DescriptorProtos.FieldDescriptorProto.Type getType();

      boolean hasTypeName();

      String getTypeName();

      ByteString getTypeNameBytes();

      boolean hasExtendee();

      String getExtendee();

      ByteString getExtendeeBytes();

      boolean hasDefaultValue();

      String getDefaultValue();

      ByteString getDefaultValueBytes();

      boolean hasOneofIndex();

      int getOneofIndex();

      boolean hasJsonName();

      String getJsonName();

      ByteString getJsonNameBytes();

      boolean hasOptions();

      DescriptorProtos.FieldOptions getOptions();

      boolean hasProto3Optional();

      boolean getProto3Optional();
   }

   public static final class FieldOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.FieldOptions, DescriptorProtos.FieldOptions.Builder>
      implements DescriptorProtos.FieldOptionsOrBuilder {
      private int bitField0_;
      public static final int CTYPE_FIELD_NUMBER = 1;
      private int ctype_;
      public static final int PACKED_FIELD_NUMBER = 2;
      private boolean packed_;
      public static final int JSTYPE_FIELD_NUMBER = 6;
      private int jstype_;
      public static final int LAZY_FIELD_NUMBER = 5;
      private boolean lazy_;
      public static final int UNVERIFIED_LAZY_FIELD_NUMBER = 15;
      private boolean unverifiedLazy_;
      public static final int DEPRECATED_FIELD_NUMBER = 3;
      private boolean deprecated_;
      public static final int WEAK_FIELD_NUMBER = 10;
      private boolean weak_;
      public static final int DEBUG_REDACT_FIELD_NUMBER = 16;
      private boolean debugRedact_;
      public static final int RETENTION_FIELD_NUMBER = 17;
      private int retention_;
      public static final int TARGETS_FIELD_NUMBER = 19;
      private Internal.IntList targets_;
      private static final Internal.IntListAdapter.IntConverter<DescriptorProtos.FieldOptions.OptionTargetType> targets_converter_ = new Internal.IntListAdapter.IntConverter<DescriptorProtos.FieldOptions.OptionTargetType>() {
         public DescriptorProtos.FieldOptions.OptionTargetType convert(int from) {
            DescriptorProtos.FieldOptions.OptionTargetType result = DescriptorProtos.FieldOptions.OptionTargetType.forNumber(from);
            return result == null ? DescriptorProtos.FieldOptions.OptionTargetType.TARGET_TYPE_UNKNOWN : result;
         }
      };
      public static final int EDITION_DEFAULTS_FIELD_NUMBER = 20;
      private Internal.ProtobufList<DescriptorProtos.FieldOptions.EditionDefault> editionDefaults_;
      public static final int FEATURES_FIELD_NUMBER = 21;
      private DescriptorProtos.FeatureSet features_;
      public static final int FEATURE_SUPPORT_FIELD_NUMBER = 22;
      private DescriptorProtos.FieldOptions.FeatureSupport featureSupport_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.FieldOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.FieldOptions> PARSER;

      private FieldOptions() {
         this.targets_ = emptyIntList();
         this.editionDefaults_ = emptyProtobufList();
         this.uninterpretedOption_ = emptyProtobufList();
      }

      @Override
      public boolean hasCtype() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public DescriptorProtos.FieldOptions.CType getCtype() {
         DescriptorProtos.FieldOptions.CType result = DescriptorProtos.FieldOptions.CType.forNumber(this.ctype_);
         return result == null ? DescriptorProtos.FieldOptions.CType.STRING : result;
      }

      private void setCtype(DescriptorProtos.FieldOptions.CType value) {
         this.ctype_ = value.getNumber();
         this.bitField0_ |= 1;
      }

      private void clearCtype() {
         this.bitField0_ &= -2;
         this.ctype_ = 0;
      }

      @Override
      public boolean hasPacked() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public boolean getPacked() {
         return this.packed_;
      }

      private void setPacked(boolean value) {
         this.bitField0_ |= 2;
         this.packed_ = value;
      }

      private void clearPacked() {
         this.bitField0_ &= -3;
         this.packed_ = false;
      }

      @Override
      public boolean hasJstype() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public DescriptorProtos.FieldOptions.JSType getJstype() {
         DescriptorProtos.FieldOptions.JSType result = DescriptorProtos.FieldOptions.JSType.forNumber(this.jstype_);
         return result == null ? DescriptorProtos.FieldOptions.JSType.JS_NORMAL : result;
      }

      private void setJstype(DescriptorProtos.FieldOptions.JSType value) {
         this.jstype_ = value.getNumber();
         this.bitField0_ |= 4;
      }

      private void clearJstype() {
         this.bitField0_ &= -5;
         this.jstype_ = 0;
      }

      @Override
      public boolean hasLazy() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public boolean getLazy() {
         return this.lazy_;
      }

      private void setLazy(boolean value) {
         this.bitField0_ |= 8;
         this.lazy_ = value;
      }

      private void clearLazy() {
         this.bitField0_ &= -9;
         this.lazy_ = false;
      }

      @Override
      public boolean hasUnverifiedLazy() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public boolean getUnverifiedLazy() {
         return this.unverifiedLazy_;
      }

      private void setUnverifiedLazy(boolean value) {
         this.bitField0_ |= 16;
         this.unverifiedLazy_ = value;
      }

      private void clearUnverifiedLazy() {
         this.bitField0_ &= -17;
         this.unverifiedLazy_ = false;
      }

      @Override
      public boolean hasDeprecated() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public boolean getDeprecated() {
         return this.deprecated_;
      }

      private void setDeprecated(boolean value) {
         this.bitField0_ |= 32;
         this.deprecated_ = value;
      }

      private void clearDeprecated() {
         this.bitField0_ &= -33;
         this.deprecated_ = false;
      }

      @Override
      public boolean hasWeak() {
         return (this.bitField0_ & 64) != 0;
      }

      @Override
      public boolean getWeak() {
         return this.weak_;
      }

      private void setWeak(boolean value) {
         this.bitField0_ |= 64;
         this.weak_ = value;
      }

      private void clearWeak() {
         this.bitField0_ &= -65;
         this.weak_ = false;
      }

      @Override
      public boolean hasDebugRedact() {
         return (this.bitField0_ & 128) != 0;
      }

      @Override
      public boolean getDebugRedact() {
         return this.debugRedact_;
      }

      private void setDebugRedact(boolean value) {
         this.bitField0_ |= 128;
         this.debugRedact_ = value;
      }

      private void clearDebugRedact() {
         this.bitField0_ &= -129;
         this.debugRedact_ = false;
      }

      @Override
      public boolean hasRetention() {
         return (this.bitField0_ & 256) != 0;
      }

      @Override
      public DescriptorProtos.FieldOptions.OptionRetention getRetention() {
         DescriptorProtos.FieldOptions.OptionRetention result = DescriptorProtos.FieldOptions.OptionRetention.forNumber(this.retention_);
         return result == null ? DescriptorProtos.FieldOptions.OptionRetention.RETENTION_UNKNOWN : result;
      }

      private void setRetention(DescriptorProtos.FieldOptions.OptionRetention value) {
         this.retention_ = value.getNumber();
         this.bitField0_ |= 256;
      }

      private void clearRetention() {
         this.bitField0_ &= -257;
         this.retention_ = 0;
      }

      @Override
      public List<DescriptorProtos.FieldOptions.OptionTargetType> getTargetsList() {
         return new Internal.IntListAdapter<>(this.targets_, targets_converter_);
      }

      @Override
      public int getTargetsCount() {
         return this.targets_.size();
      }

      @Override
      public DescriptorProtos.FieldOptions.OptionTargetType getTargets(int index) {
         DescriptorProtos.FieldOptions.OptionTargetType result = DescriptorProtos.FieldOptions.OptionTargetType.forNumber(this.targets_.getInt(index));
         return result == null ? DescriptorProtos.FieldOptions.OptionTargetType.TARGET_TYPE_UNKNOWN : result;
      }

      private void ensureTargetsIsMutable() {
         Internal.IntList tmp = this.targets_;
         if (!tmp.isModifiable()) {
            this.targets_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setTargets(int index, DescriptorProtos.FieldOptions.OptionTargetType value) {
         value.getClass();
         this.ensureTargetsIsMutable();
         this.targets_.setInt(index, value.getNumber());
      }

      private void addTargets(DescriptorProtos.FieldOptions.OptionTargetType value) {
         value.getClass();
         this.ensureTargetsIsMutable();
         this.targets_.addInt(value.getNumber());
      }

      private void addAllTargets(Iterable<? extends DescriptorProtos.FieldOptions.OptionTargetType> values) {
         this.ensureTargetsIsMutable();

         for (DescriptorProtos.FieldOptions.OptionTargetType value : values) {
            this.targets_.addInt(value.getNumber());
         }
      }

      private void clearTargets() {
         this.targets_ = emptyIntList();
      }

      @Override
      public List<DescriptorProtos.FieldOptions.EditionDefault> getEditionDefaultsList() {
         return this.editionDefaults_;
      }

      public List<? extends DescriptorProtos.FieldOptions.EditionDefaultOrBuilder> getEditionDefaultsOrBuilderList() {
         return this.editionDefaults_;
      }

      @Override
      public int getEditionDefaultsCount() {
         return this.editionDefaults_.size();
      }

      @Override
      public DescriptorProtos.FieldOptions.EditionDefault getEditionDefaults(int index) {
         return this.editionDefaults_.get(index);
      }

      public DescriptorProtos.FieldOptions.EditionDefaultOrBuilder getEditionDefaultsOrBuilder(int index) {
         return this.editionDefaults_.get(index);
      }

      private void ensureEditionDefaultsIsMutable() {
         Internal.ProtobufList<DescriptorProtos.FieldOptions.EditionDefault> tmp = this.editionDefaults_;
         if (!tmp.isModifiable()) {
            this.editionDefaults_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setEditionDefaults(int index, DescriptorProtos.FieldOptions.EditionDefault value) {
         value.getClass();
         this.ensureEditionDefaultsIsMutable();
         this.editionDefaults_.set(index, value);
      }

      private void addEditionDefaults(DescriptorProtos.FieldOptions.EditionDefault value) {
         value.getClass();
         this.ensureEditionDefaultsIsMutable();
         this.editionDefaults_.add(value);
      }

      private void addEditionDefaults(int index, DescriptorProtos.FieldOptions.EditionDefault value) {
         value.getClass();
         this.ensureEditionDefaultsIsMutable();
         this.editionDefaults_.add(index, value);
      }

      private void addAllEditionDefaults(Iterable<? extends DescriptorProtos.FieldOptions.EditionDefault> values) {
         this.ensureEditionDefaultsIsMutable();
         AbstractMessageLite.addAll(values, this.editionDefaults_);
      }

      private void clearEditionDefaults() {
         this.editionDefaults_ = emptyProtobufList();
      }

      private void removeEditionDefaults(int index) {
         this.ensureEditionDefaultsIsMutable();
         this.editionDefaults_.remove(index);
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 512) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 512;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 512;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -513;
      }

      @Override
      public boolean hasFeatureSupport() {
         return (this.bitField0_ & 1024) != 0;
      }

      @Override
      public DescriptorProtos.FieldOptions.FeatureSupport getFeatureSupport() {
         return this.featureSupport_ == null ? DescriptorProtos.FieldOptions.FeatureSupport.getDefaultInstance() : this.featureSupport_;
      }

      private void setFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport value) {
         value.getClass();
         this.featureSupport_ = value;
         this.bitField0_ |= 1024;
      }

      private void mergeFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport value) {
         value.getClass();
         if (this.featureSupport_ != null && this.featureSupport_ != DescriptorProtos.FieldOptions.FeatureSupport.getDefaultInstance()) {
            this.featureSupport_ = DescriptorProtos.FieldOptions.FeatureSupport.newBuilder(this.featureSupport_).mergeFrom(value).buildPartial();
         } else {
            this.featureSupport_ = value;
         }

         this.bitField0_ |= 1024;
      }

      private void clearFeatureSupport() {
         this.featureSupport_ = null;
         this.bitField0_ &= -1025;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      public static DescriptorProtos.FieldOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FieldOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FieldOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FieldOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FieldOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FieldOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FieldOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FieldOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FieldOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FieldOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FieldOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FieldOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FieldOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.FieldOptions.Builder newBuilder(DescriptorProtos.FieldOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.FieldOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.FieldOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "ctype_",
                  DescriptorProtos.FieldOptions.CType.internalGetVerifier(),
                  "packed_",
                  "deprecated_",
                  "lazy_",
                  "jstype_",
                  DescriptorProtos.FieldOptions.JSType.internalGetVerifier(),
                  "weak_",
                  "unverifiedLazy_",
                  "debugRedact_",
                  "retention_",
                  DescriptorProtos.FieldOptions.OptionRetention.internalGetVerifier(),
                  "targets_",
                  DescriptorProtos.FieldOptions.OptionTargetType.internalGetVerifier(),
                  "editionDefaults_",
                  DescriptorProtos.FieldOptions.EditionDefault.class,
                  "features_",
                  "featureSupport_",
                  "uninterpretedOption_",
                  DescriptorProtos.UninterpretedOption.class
               };
               String info = "\u0001\u000e\u0000\u0001\u0001ϧ\u000e\u0000\u0003\u0002\u0001᠌\u0000\u0002ဇ\u0001\u0003ဇ\u0005\u0005ဇ\u0003\u0006᠌\u0002\nဇ\u0006\u000fဇ\u0004\u0010ဇ\u0007\u0011᠌\b\u0013ࠞ\u0014\u001b\u0015ᐉ\t\u0016ဉ\nϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.FieldOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.FieldOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.FieldOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.FieldOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.FieldOptions defaultInstance = new DescriptorProtos.FieldOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FieldOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.FieldOptions, DescriptorProtos.FieldOptions.Builder>
         implements DescriptorProtos.FieldOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.FieldOptions.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasCtype() {
            return this.instance.hasCtype();
         }

         @Override
         public DescriptorProtos.FieldOptions.CType getCtype() {
            return this.instance.getCtype();
         }

         public DescriptorProtos.FieldOptions.Builder setCtype(DescriptorProtos.FieldOptions.CType value) {
            this.copyOnWrite();
            this.instance.setCtype(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearCtype() {
            this.copyOnWrite();
            this.instance.clearCtype();
            return this;
         }

         @Override
         public boolean hasPacked() {
            return this.instance.hasPacked();
         }

         @Override
         public boolean getPacked() {
            return this.instance.getPacked();
         }

         public DescriptorProtos.FieldOptions.Builder setPacked(boolean value) {
            this.copyOnWrite();
            this.instance.setPacked(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearPacked() {
            this.copyOnWrite();
            this.instance.clearPacked();
            return this;
         }

         @Override
         public boolean hasJstype() {
            return this.instance.hasJstype();
         }

         @Override
         public DescriptorProtos.FieldOptions.JSType getJstype() {
            return this.instance.getJstype();
         }

         public DescriptorProtos.FieldOptions.Builder setJstype(DescriptorProtos.FieldOptions.JSType value) {
            this.copyOnWrite();
            this.instance.setJstype(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearJstype() {
            this.copyOnWrite();
            this.instance.clearJstype();
            return this;
         }

         @Override
         public boolean hasLazy() {
            return this.instance.hasLazy();
         }

         @Override
         public boolean getLazy() {
            return this.instance.getLazy();
         }

         public DescriptorProtos.FieldOptions.Builder setLazy(boolean value) {
            this.copyOnWrite();
            this.instance.setLazy(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearLazy() {
            this.copyOnWrite();
            this.instance.clearLazy();
            return this;
         }

         @Override
         public boolean hasUnverifiedLazy() {
            return this.instance.hasUnverifiedLazy();
         }

         @Override
         public boolean getUnverifiedLazy() {
            return this.instance.getUnverifiedLazy();
         }

         public DescriptorProtos.FieldOptions.Builder setUnverifiedLazy(boolean value) {
            this.copyOnWrite();
            this.instance.setUnverifiedLazy(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearUnverifiedLazy() {
            this.copyOnWrite();
            this.instance.clearUnverifiedLazy();
            return this;
         }

         @Override
         public boolean hasDeprecated() {
            return this.instance.hasDeprecated();
         }

         @Override
         public boolean getDeprecated() {
            return this.instance.getDeprecated();
         }

         public DescriptorProtos.FieldOptions.Builder setDeprecated(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecated(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearDeprecated() {
            this.copyOnWrite();
            this.instance.clearDeprecated();
            return this;
         }

         @Override
         public boolean hasWeak() {
            return this.instance.hasWeak();
         }

         @Override
         public boolean getWeak() {
            return this.instance.getWeak();
         }

         public DescriptorProtos.FieldOptions.Builder setWeak(boolean value) {
            this.copyOnWrite();
            this.instance.setWeak(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearWeak() {
            this.copyOnWrite();
            this.instance.clearWeak();
            return this;
         }

         @Override
         public boolean hasDebugRedact() {
            return this.instance.hasDebugRedact();
         }

         @Override
         public boolean getDebugRedact() {
            return this.instance.getDebugRedact();
         }

         public DescriptorProtos.FieldOptions.Builder setDebugRedact(boolean value) {
            this.copyOnWrite();
            this.instance.setDebugRedact(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearDebugRedact() {
            this.copyOnWrite();
            this.instance.clearDebugRedact();
            return this;
         }

         @Override
         public boolean hasRetention() {
            return this.instance.hasRetention();
         }

         @Override
         public DescriptorProtos.FieldOptions.OptionRetention getRetention() {
            return this.instance.getRetention();
         }

         public DescriptorProtos.FieldOptions.Builder setRetention(DescriptorProtos.FieldOptions.OptionRetention value) {
            this.copyOnWrite();
            this.instance.setRetention(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearRetention() {
            this.copyOnWrite();
            this.instance.clearRetention();
            return this;
         }

         @Override
         public List<DescriptorProtos.FieldOptions.OptionTargetType> getTargetsList() {
            return this.instance.getTargetsList();
         }

         @Override
         public int getTargetsCount() {
            return this.instance.getTargetsCount();
         }

         @Override
         public DescriptorProtos.FieldOptions.OptionTargetType getTargets(int index) {
            return this.instance.getTargets(index);
         }

         public DescriptorProtos.FieldOptions.Builder setTargets(int index, DescriptorProtos.FieldOptions.OptionTargetType value) {
            this.copyOnWrite();
            this.instance.setTargets(index, value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addTargets(DescriptorProtos.FieldOptions.OptionTargetType value) {
            this.copyOnWrite();
            this.instance.addTargets(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addAllTargets(Iterable<? extends DescriptorProtos.FieldOptions.OptionTargetType> values) {
            this.copyOnWrite();
            this.instance.addAllTargets(values);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearTargets() {
            this.copyOnWrite();
            this.instance.clearTargets();
            return this;
         }

         @Override
         public List<DescriptorProtos.FieldOptions.EditionDefault> getEditionDefaultsList() {
            return Collections.unmodifiableList(this.instance.getEditionDefaultsList());
         }

         @Override
         public int getEditionDefaultsCount() {
            return this.instance.getEditionDefaultsCount();
         }

         @Override
         public DescriptorProtos.FieldOptions.EditionDefault getEditionDefaults(int index) {
            return this.instance.getEditionDefaults(index);
         }

         public DescriptorProtos.FieldOptions.Builder setEditionDefaults(int index, DescriptorProtos.FieldOptions.EditionDefault value) {
            this.copyOnWrite();
            this.instance.setEditionDefaults(index, value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder setEditionDefaults(int index, DescriptorProtos.FieldOptions.EditionDefault.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setEditionDefaults(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addEditionDefaults(DescriptorProtos.FieldOptions.EditionDefault value) {
            this.copyOnWrite();
            this.instance.addEditionDefaults(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addEditionDefaults(int index, DescriptorProtos.FieldOptions.EditionDefault value) {
            this.copyOnWrite();
            this.instance.addEditionDefaults(index, value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addEditionDefaults(DescriptorProtos.FieldOptions.EditionDefault.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addEditionDefaults(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addEditionDefaults(int index, DescriptorProtos.FieldOptions.EditionDefault.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addEditionDefaults(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addAllEditionDefaults(Iterable<? extends DescriptorProtos.FieldOptions.EditionDefault> values) {
            this.copyOnWrite();
            this.instance.addAllEditionDefaults(values);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearEditionDefaults() {
            this.copyOnWrite();
            this.instance.clearEditionDefaults();
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder removeEditionDefaults(int index) {
            this.copyOnWrite();
            this.instance.removeEditionDefaults(index);
            return this;
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.FieldOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public boolean hasFeatureSupport() {
            return this.instance.hasFeatureSupport();
         }

         @Override
         public DescriptorProtos.FieldOptions.FeatureSupport getFeatureSupport() {
            return this.instance.getFeatureSupport();
         }

         public DescriptorProtos.FieldOptions.Builder setFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport value) {
            this.copyOnWrite();
            this.instance.setFeatureSupport(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder setFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatureSupport(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder mergeFeatureSupport(DescriptorProtos.FieldOptions.FeatureSupport value) {
            this.copyOnWrite();
            this.instance.mergeFeatureSupport(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearFeatureSupport() {
            this.copyOnWrite();
            this.instance.clearFeatureSupport();
            return this;
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.FieldOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.FieldOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }
      }

      public static enum CType implements Internal.EnumLite {
         STRING(0),
         CORD(1),
         STRING_PIECE(2);

         public static final int STRING_VALUE = 0;
         public static final int CORD_VALUE = 1;
         public static final int STRING_PIECE_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.FieldOptions.CType> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FieldOptions.CType>() {
            public DescriptorProtos.FieldOptions.CType findValueByNumber(int number) {
               return DescriptorProtos.FieldOptions.CType.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FieldOptions.CType valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FieldOptions.CType forNumber(int value) {
            switch (value) {
               case 0:
                  return STRING;
               case 1:
                  return CORD;
               case 2:
                  return STRING_PIECE;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FieldOptions.CType> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FieldOptions.CType.CTypeVerifier.INSTANCE;
         }

         private CType(int value) {
            this.value = value;
         }

         private static final class CTypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FieldOptions.CType.CTypeVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FieldOptions.CType.forNumber(number) != null;
            }
         }
      }

      public static final class EditionDefault
         extends GeneratedMessageLite<DescriptorProtos.FieldOptions.EditionDefault, DescriptorProtos.FieldOptions.EditionDefault.Builder>
         implements DescriptorProtos.FieldOptions.EditionDefaultOrBuilder {
         private int bitField0_;
         public static final int EDITION_FIELD_NUMBER = 3;
         private int edition_;
         public static final int VALUE_FIELD_NUMBER = 2;
         private String value_ = "";
         private static final DescriptorProtos.FieldOptions.EditionDefault DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.FieldOptions.EditionDefault> PARSER;

         private EditionDefault() {
         }

         @Override
         public boolean hasEdition() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public DescriptorProtos.Edition getEdition() {
            DescriptorProtos.Edition result = DescriptorProtos.Edition.forNumber(this.edition_);
            return result == null ? DescriptorProtos.Edition.EDITION_UNKNOWN : result;
         }

         private void setEdition(DescriptorProtos.Edition value) {
            this.edition_ = value.getNumber();
            this.bitField0_ |= 1;
         }

         private void clearEdition() {
            this.bitField0_ &= -2;
            this.edition_ = 0;
         }

         @Override
         public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
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
            this.bitField0_ |= 2;
            this.value_ = value;
         }

         private void clearValue() {
            this.bitField0_ &= -3;
            this.value_ = getDefaultInstance().getValue();
         }

         private void setValueBytes(ByteString value) {
            this.value_ = value.toStringUtf8();
            this.bitField0_ |= 2;
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.EditionDefault.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.FieldOptions.EditionDefault.Builder newBuilder(DescriptorProtos.FieldOptions.EditionDefault prototype) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$EditionDefault.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$EditionDefault;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$EditionDefault.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$EditionDefault$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.FieldOptions.EditionDefault();
               case NEW_BUILDER:
                  return new DescriptorProtos.FieldOptions.EditionDefault.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{"bitField0_", "value_", "edition_", DescriptorProtos.Edition.internalGetVerifier()};
                  String info = "\u0001\u0002\u0000\u0001\u0002\u0003\u0002\u0000\u0000\u0000\u0002ဈ\u0001\u0003᠌\u0000";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.FieldOptions.EditionDefault> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.FieldOptions.EditionDefault.class) {
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

         public static DescriptorProtos.FieldOptions.EditionDefault getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.FieldOptions.EditionDefault> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.FieldOptions.EditionDefault defaultInstance = new DescriptorProtos.FieldOptions.EditionDefault();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FieldOptions.EditionDefault.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.FieldOptions.EditionDefault, DescriptorProtos.FieldOptions.EditionDefault.Builder>
            implements DescriptorProtos.FieldOptions.EditionDefaultOrBuilder {
            private Builder() {
               super(DescriptorProtos.FieldOptions.EditionDefault.DEFAULT_INSTANCE);
            }

            @Override
            public boolean hasEdition() {
               return this.instance.hasEdition();
            }

            @Override
            public DescriptorProtos.Edition getEdition() {
               return this.instance.getEdition();
            }

            public DescriptorProtos.FieldOptions.EditionDefault.Builder setEdition(DescriptorProtos.Edition value) {
               this.copyOnWrite();
               this.instance.setEdition(value);
               return this;
            }

            public DescriptorProtos.FieldOptions.EditionDefault.Builder clearEdition() {
               this.copyOnWrite();
               this.instance.clearEdition();
               return this;
            }

            @Override
            public boolean hasValue() {
               return this.instance.hasValue();
            }

            @Override
            public String getValue() {
               return this.instance.getValue();
            }

            @Override
            public ByteString getValueBytes() {
               return this.instance.getValueBytes();
            }

            public DescriptorProtos.FieldOptions.EditionDefault.Builder setValue(String value) {
               this.copyOnWrite();
               this.instance.setValue(value);
               return this;
            }

            public DescriptorProtos.FieldOptions.EditionDefault.Builder clearValue() {
               this.copyOnWrite();
               this.instance.clearValue();
               return this;
            }

            public DescriptorProtos.FieldOptions.EditionDefault.Builder setValueBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.setValueBytes(value);
               return this;
            }
         }
      }

      public interface EditionDefaultOrBuilder extends MessageLiteOrBuilder {
         boolean hasEdition();

         DescriptorProtos.Edition getEdition();

         boolean hasValue();

         String getValue();

         ByteString getValueBytes();
      }

      public static final class FeatureSupport
         extends GeneratedMessageLite<DescriptorProtos.FieldOptions.FeatureSupport, DescriptorProtos.FieldOptions.FeatureSupport.Builder>
         implements DescriptorProtos.FieldOptions.FeatureSupportOrBuilder {
         private int bitField0_;
         public static final int EDITION_INTRODUCED_FIELD_NUMBER = 1;
         private int editionIntroduced_;
         public static final int EDITION_DEPRECATED_FIELD_NUMBER = 2;
         private int editionDeprecated_;
         public static final int DEPRECATION_WARNING_FIELD_NUMBER = 3;
         private String deprecationWarning_ = "";
         public static final int EDITION_REMOVED_FIELD_NUMBER = 4;
         private int editionRemoved_;
         private static final DescriptorProtos.FieldOptions.FeatureSupport DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.FieldOptions.FeatureSupport> PARSER;

         private FeatureSupport() {
         }

         @Override
         public boolean hasEditionIntroduced() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public DescriptorProtos.Edition getEditionIntroduced() {
            DescriptorProtos.Edition result = DescriptorProtos.Edition.forNumber(this.editionIntroduced_);
            return result == null ? DescriptorProtos.Edition.EDITION_UNKNOWN : result;
         }

         private void setEditionIntroduced(DescriptorProtos.Edition value) {
            this.editionIntroduced_ = value.getNumber();
            this.bitField0_ |= 1;
         }

         private void clearEditionIntroduced() {
            this.bitField0_ &= -2;
            this.editionIntroduced_ = 0;
         }

         @Override
         public boolean hasEditionDeprecated() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public DescriptorProtos.Edition getEditionDeprecated() {
            DescriptorProtos.Edition result = DescriptorProtos.Edition.forNumber(this.editionDeprecated_);
            return result == null ? DescriptorProtos.Edition.EDITION_UNKNOWN : result;
         }

         private void setEditionDeprecated(DescriptorProtos.Edition value) {
            this.editionDeprecated_ = value.getNumber();
            this.bitField0_ |= 2;
         }

         private void clearEditionDeprecated() {
            this.bitField0_ &= -3;
            this.editionDeprecated_ = 0;
         }

         @Override
         public boolean hasDeprecationWarning() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public String getDeprecationWarning() {
            return this.deprecationWarning_;
         }

         @Override
         public ByteString getDeprecationWarningBytes() {
            return ByteString.copyFromUtf8(this.deprecationWarning_);
         }

         private void setDeprecationWarning(String value) {
            Class<?> valueClass = value.getClass();
            this.bitField0_ |= 4;
            this.deprecationWarning_ = value;
         }

         private void clearDeprecationWarning() {
            this.bitField0_ &= -5;
            this.deprecationWarning_ = getDefaultInstance().getDeprecationWarning();
         }

         private void setDeprecationWarningBytes(ByteString value) {
            this.deprecationWarning_ = value.toStringUtf8();
            this.bitField0_ |= 4;
         }

         @Override
         public boolean hasEditionRemoved() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public DescriptorProtos.Edition getEditionRemoved() {
            DescriptorProtos.Edition result = DescriptorProtos.Edition.forNumber(this.editionRemoved_);
            return result == null ? DescriptorProtos.Edition.EDITION_UNKNOWN : result;
         }

         private void setEditionRemoved(DescriptorProtos.Edition value) {
            this.editionRemoved_ = value.getNumber();
            this.bitField0_ |= 8;
         }

         private void clearEditionRemoved() {
            this.bitField0_ &= -9;
            this.editionRemoved_ = 0;
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.FieldOptions.FeatureSupport.Builder newBuilder(DescriptorProtos.FieldOptions.FeatureSupport prototype) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$FeatureSupport.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$FeatureSupport;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$FeatureSupport.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FieldOptions$FeatureSupport$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.FieldOptions.FeatureSupport();
               case NEW_BUILDER:
                  return new DescriptorProtos.FieldOptions.FeatureSupport.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{
                     "bitField0_",
                     "editionIntroduced_",
                     DescriptorProtos.Edition.internalGetVerifier(),
                     "editionDeprecated_",
                     DescriptorProtos.Edition.internalGetVerifier(),
                     "deprecationWarning_",
                     "editionRemoved_",
                     DescriptorProtos.Edition.internalGetVerifier()
                  };
                  String info = "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001᠌\u0000\u0002᠌\u0001\u0003ဈ\u0002\u0004᠌\u0003";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.FieldOptions.FeatureSupport> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.FieldOptions.FeatureSupport.class) {
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

         public static DescriptorProtos.FieldOptions.FeatureSupport getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.FieldOptions.FeatureSupport> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.FieldOptions.FeatureSupport defaultInstance = new DescriptorProtos.FieldOptions.FeatureSupport();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FieldOptions.FeatureSupport.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.FieldOptions.FeatureSupport, DescriptorProtos.FieldOptions.FeatureSupport.Builder>
            implements DescriptorProtos.FieldOptions.FeatureSupportOrBuilder {
            private Builder() {
               super(DescriptorProtos.FieldOptions.FeatureSupport.DEFAULT_INSTANCE);
            }

            @Override
            public boolean hasEditionIntroduced() {
               return this.instance.hasEditionIntroduced();
            }

            @Override
            public DescriptorProtos.Edition getEditionIntroduced() {
               return this.instance.getEditionIntroduced();
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder setEditionIntroduced(DescriptorProtos.Edition value) {
               this.copyOnWrite();
               this.instance.setEditionIntroduced(value);
               return this;
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder clearEditionIntroduced() {
               this.copyOnWrite();
               this.instance.clearEditionIntroduced();
               return this;
            }

            @Override
            public boolean hasEditionDeprecated() {
               return this.instance.hasEditionDeprecated();
            }

            @Override
            public DescriptorProtos.Edition getEditionDeprecated() {
               return this.instance.getEditionDeprecated();
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder setEditionDeprecated(DescriptorProtos.Edition value) {
               this.copyOnWrite();
               this.instance.setEditionDeprecated(value);
               return this;
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder clearEditionDeprecated() {
               this.copyOnWrite();
               this.instance.clearEditionDeprecated();
               return this;
            }

            @Override
            public boolean hasDeprecationWarning() {
               return this.instance.hasDeprecationWarning();
            }

            @Override
            public String getDeprecationWarning() {
               return this.instance.getDeprecationWarning();
            }

            @Override
            public ByteString getDeprecationWarningBytes() {
               return this.instance.getDeprecationWarningBytes();
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder setDeprecationWarning(String value) {
               this.copyOnWrite();
               this.instance.setDeprecationWarning(value);
               return this;
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder clearDeprecationWarning() {
               this.copyOnWrite();
               this.instance.clearDeprecationWarning();
               return this;
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder setDeprecationWarningBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.setDeprecationWarningBytes(value);
               return this;
            }

            @Override
            public boolean hasEditionRemoved() {
               return this.instance.hasEditionRemoved();
            }

            @Override
            public DescriptorProtos.Edition getEditionRemoved() {
               return this.instance.getEditionRemoved();
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder setEditionRemoved(DescriptorProtos.Edition value) {
               this.copyOnWrite();
               this.instance.setEditionRemoved(value);
               return this;
            }

            public DescriptorProtos.FieldOptions.FeatureSupport.Builder clearEditionRemoved() {
               this.copyOnWrite();
               this.instance.clearEditionRemoved();
               return this;
            }
         }
      }

      public interface FeatureSupportOrBuilder extends MessageLiteOrBuilder {
         boolean hasEditionIntroduced();

         DescriptorProtos.Edition getEditionIntroduced();

         boolean hasEditionDeprecated();

         DescriptorProtos.Edition getEditionDeprecated();

         boolean hasDeprecationWarning();

         String getDeprecationWarning();

         ByteString getDeprecationWarningBytes();

         boolean hasEditionRemoved();

         DescriptorProtos.Edition getEditionRemoved();
      }

      public static enum JSType implements Internal.EnumLite {
         JS_NORMAL(0),
         JS_STRING(1),
         JS_NUMBER(2);

         public static final int JS_NORMAL_VALUE = 0;
         public static final int JS_STRING_VALUE = 1;
         public static final int JS_NUMBER_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.FieldOptions.JSType> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FieldOptions.JSType>() {
            public DescriptorProtos.FieldOptions.JSType findValueByNumber(int number) {
               return DescriptorProtos.FieldOptions.JSType.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FieldOptions.JSType valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FieldOptions.JSType forNumber(int value) {
            switch (value) {
               case 0:
                  return JS_NORMAL;
               case 1:
                  return JS_STRING;
               case 2:
                  return JS_NUMBER;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FieldOptions.JSType> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FieldOptions.JSType.JSTypeVerifier.INSTANCE;
         }

         private JSType(int value) {
            this.value = value;
         }

         private static final class JSTypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FieldOptions.JSType.JSTypeVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FieldOptions.JSType.forNumber(number) != null;
            }
         }
      }

      public static enum OptionRetention implements Internal.EnumLite {
         RETENTION_UNKNOWN(0),
         RETENTION_RUNTIME(1),
         RETENTION_SOURCE(2);

         public static final int RETENTION_UNKNOWN_VALUE = 0;
         public static final int RETENTION_RUNTIME_VALUE = 1;
         public static final int RETENTION_SOURCE_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.FieldOptions.OptionRetention> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FieldOptions.OptionRetention>() {
            public DescriptorProtos.FieldOptions.OptionRetention findValueByNumber(int number) {
               return DescriptorProtos.FieldOptions.OptionRetention.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FieldOptions.OptionRetention valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FieldOptions.OptionRetention forNumber(int value) {
            switch (value) {
               case 0:
                  return RETENTION_UNKNOWN;
               case 1:
                  return RETENTION_RUNTIME;
               case 2:
                  return RETENTION_SOURCE;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FieldOptions.OptionRetention> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FieldOptions.OptionRetention.OptionRetentionVerifier.INSTANCE;
         }

         private OptionRetention(int value) {
            this.value = value;
         }

         private static final class OptionRetentionVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FieldOptions.OptionRetention.OptionRetentionVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FieldOptions.OptionRetention.forNumber(number) != null;
            }
         }
      }

      public static enum OptionTargetType implements Internal.EnumLite {
         TARGET_TYPE_UNKNOWN(0),
         TARGET_TYPE_FILE(1),
         TARGET_TYPE_EXTENSION_RANGE(2),
         TARGET_TYPE_MESSAGE(3),
         TARGET_TYPE_FIELD(4),
         TARGET_TYPE_ONEOF(5),
         TARGET_TYPE_ENUM(6),
         TARGET_TYPE_ENUM_ENTRY(7),
         TARGET_TYPE_SERVICE(8),
         TARGET_TYPE_METHOD(9);

         public static final int TARGET_TYPE_UNKNOWN_VALUE = 0;
         public static final int TARGET_TYPE_FILE_VALUE = 1;
         public static final int TARGET_TYPE_EXTENSION_RANGE_VALUE = 2;
         public static final int TARGET_TYPE_MESSAGE_VALUE = 3;
         public static final int TARGET_TYPE_FIELD_VALUE = 4;
         public static final int TARGET_TYPE_ONEOF_VALUE = 5;
         public static final int TARGET_TYPE_ENUM_VALUE = 6;
         public static final int TARGET_TYPE_ENUM_ENTRY_VALUE = 7;
         public static final int TARGET_TYPE_SERVICE_VALUE = 8;
         public static final int TARGET_TYPE_METHOD_VALUE = 9;
         private static final Internal.EnumLiteMap<DescriptorProtos.FieldOptions.OptionTargetType> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FieldOptions.OptionTargetType>() {
            public DescriptorProtos.FieldOptions.OptionTargetType findValueByNumber(int number) {
               return DescriptorProtos.FieldOptions.OptionTargetType.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FieldOptions.OptionTargetType valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FieldOptions.OptionTargetType forNumber(int value) {
            switch (value) {
               case 0:
                  return TARGET_TYPE_UNKNOWN;
               case 1:
                  return TARGET_TYPE_FILE;
               case 2:
                  return TARGET_TYPE_EXTENSION_RANGE;
               case 3:
                  return TARGET_TYPE_MESSAGE;
               case 4:
                  return TARGET_TYPE_FIELD;
               case 5:
                  return TARGET_TYPE_ONEOF;
               case 6:
                  return TARGET_TYPE_ENUM;
               case 7:
                  return TARGET_TYPE_ENUM_ENTRY;
               case 8:
                  return TARGET_TYPE_SERVICE;
               case 9:
                  return TARGET_TYPE_METHOD;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FieldOptions.OptionTargetType> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FieldOptions.OptionTargetType.OptionTargetTypeVerifier.INSTANCE;
         }

         private OptionTargetType(int value) {
            this.value = value;
         }

         private static final class OptionTargetTypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FieldOptions.OptionTargetType.OptionTargetTypeVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FieldOptions.OptionTargetType.forNumber(number) != null;
            }
         }
      }
   }

   public interface FieldOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.FieldOptions, DescriptorProtos.FieldOptions.Builder> {
      boolean hasCtype();

      DescriptorProtos.FieldOptions.CType getCtype();

      boolean hasPacked();

      boolean getPacked();

      boolean hasJstype();

      DescriptorProtos.FieldOptions.JSType getJstype();

      boolean hasLazy();

      boolean getLazy();

      boolean hasUnverifiedLazy();

      boolean getUnverifiedLazy();

      boolean hasDeprecated();

      boolean getDeprecated();

      boolean hasWeak();

      boolean getWeak();

      boolean hasDebugRedact();

      boolean getDebugRedact();

      boolean hasRetention();

      DescriptorProtos.FieldOptions.OptionRetention getRetention();

      List<DescriptorProtos.FieldOptions.OptionTargetType> getTargetsList();

      int getTargetsCount();

      DescriptorProtos.FieldOptions.OptionTargetType getTargets(int index);

      List<DescriptorProtos.FieldOptions.EditionDefault> getEditionDefaultsList();

      DescriptorProtos.FieldOptions.EditionDefault getEditionDefaults(int index);

      int getEditionDefaultsCount();

      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      boolean hasFeatureSupport();

      DescriptorProtos.FieldOptions.FeatureSupport getFeatureSupport();

      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();
   }

   public static final class FileDescriptorProto
      extends GeneratedMessageLite<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder>
      implements DescriptorProtos.FileDescriptorProtoOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_;
      public static final int PACKAGE_FIELD_NUMBER = 2;
      private String package_;
      public static final int DEPENDENCY_FIELD_NUMBER = 3;
      private Internal.ProtobufList<String> dependency_;
      public static final int PUBLIC_DEPENDENCY_FIELD_NUMBER = 10;
      private Internal.IntList publicDependency_;
      public static final int WEAK_DEPENDENCY_FIELD_NUMBER = 11;
      private Internal.IntList weakDependency_;
      public static final int MESSAGE_TYPE_FIELD_NUMBER = 4;
      private Internal.ProtobufList<DescriptorProtos.DescriptorProto> messageType_;
      public static final int ENUM_TYPE_FIELD_NUMBER = 5;
      private Internal.ProtobufList<DescriptorProtos.EnumDescriptorProto> enumType_;
      public static final int SERVICE_FIELD_NUMBER = 6;
      private Internal.ProtobufList<DescriptorProtos.ServiceDescriptorProto> service_;
      public static final int EXTENSION_FIELD_NUMBER = 7;
      private Internal.ProtobufList<DescriptorProtos.FieldDescriptorProto> extension_;
      public static final int OPTIONS_FIELD_NUMBER = 8;
      private DescriptorProtos.FileOptions options_;
      public static final int SOURCE_CODE_INFO_FIELD_NUMBER = 9;
      private DescriptorProtos.SourceCodeInfo sourceCodeInfo_;
      public static final int SYNTAX_FIELD_NUMBER = 12;
      private String syntax_;
      public static final int EDITION_FIELD_NUMBER = 14;
      private int edition_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.FileDescriptorProto DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.FileDescriptorProto> PARSER;

      private FileDescriptorProto() {
         this.name_ = "";
         this.package_ = "";
         this.dependency_ = GeneratedMessageLite.emptyProtobufList();
         this.publicDependency_ = emptyIntList();
         this.weakDependency_ = emptyIntList();
         this.messageType_ = emptyProtobufList();
         this.enumType_ = emptyProtobufList();
         this.service_ = emptyProtobufList();
         this.extension_ = emptyProtobufList();
         this.syntax_ = "";
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         return this.name_;
      }

      @Override
      public ByteString getNameBytes() {
         return ByteString.copyFromUtf8(this.name_);
      }

      private void setName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.name_ = value;
      }

      private void clearName() {
         this.bitField0_ &= -2;
         this.name_ = getDefaultInstance().getName();
      }

      private void setNameBytes(ByteString value) {
         this.name_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public boolean hasPackage() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getPackage() {
         return this.package_;
      }

      @Override
      public ByteString getPackageBytes() {
         return ByteString.copyFromUtf8(this.package_);
      }

      private void setPackage(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 2;
         this.package_ = value;
      }

      private void clearPackage() {
         this.bitField0_ &= -3;
         this.package_ = getDefaultInstance().getPackage();
      }

      private void setPackageBytes(ByteString value) {
         this.package_ = value.toStringUtf8();
         this.bitField0_ |= 2;
      }

      @Override
      public List<String> getDependencyList() {
         return this.dependency_;
      }

      @Override
      public int getDependencyCount() {
         return this.dependency_.size();
      }

      @Override
      public String getDependency(int index) {
         return this.dependency_.get(index);
      }

      @Override
      public ByteString getDependencyBytes(int index) {
         return ByteString.copyFromUtf8(this.dependency_.get(index));
      }

      private void ensureDependencyIsMutable() {
         Internal.ProtobufList<String> tmp = this.dependency_;
         if (!tmp.isModifiable()) {
            this.dependency_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setDependency(int index, String value) {
         Class<?> valueClass = value.getClass();
         this.ensureDependencyIsMutable();
         this.dependency_.set(index, value);
      }

      private void addDependency(String value) {
         Class<?> valueClass = value.getClass();
         this.ensureDependencyIsMutable();
         this.dependency_.add(value);
      }

      private void addAllDependency(Iterable<String> values) {
         this.ensureDependencyIsMutable();
         AbstractMessageLite.addAll(values, this.dependency_);
      }

      private void clearDependency() {
         this.dependency_ = GeneratedMessageLite.emptyProtobufList();
      }

      private void addDependencyBytes(ByteString value) {
         this.ensureDependencyIsMutable();
         this.dependency_.add(value.toStringUtf8());
      }

      @Override
      public List<Integer> getPublicDependencyList() {
         return this.publicDependency_;
      }

      @Override
      public int getPublicDependencyCount() {
         return this.publicDependency_.size();
      }

      @Override
      public int getPublicDependency(int index) {
         return this.publicDependency_.getInt(index);
      }

      private void ensurePublicDependencyIsMutable() {
         Internal.IntList tmp = this.publicDependency_;
         if (!tmp.isModifiable()) {
            this.publicDependency_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setPublicDependency(int index, int value) {
         this.ensurePublicDependencyIsMutable();
         this.publicDependency_.setInt(index, value);
      }

      private void addPublicDependency(int value) {
         this.ensurePublicDependencyIsMutable();
         this.publicDependency_.addInt(value);
      }

      private void addAllPublicDependency(Iterable<? extends Integer> values) {
         this.ensurePublicDependencyIsMutable();
         AbstractMessageLite.addAll(values, this.publicDependency_);
      }

      private void clearPublicDependency() {
         this.publicDependency_ = emptyIntList();
      }

      @Override
      public List<Integer> getWeakDependencyList() {
         return this.weakDependency_;
      }

      @Override
      public int getWeakDependencyCount() {
         return this.weakDependency_.size();
      }

      @Override
      public int getWeakDependency(int index) {
         return this.weakDependency_.getInt(index);
      }

      private void ensureWeakDependencyIsMutable() {
         Internal.IntList tmp = this.weakDependency_;
         if (!tmp.isModifiable()) {
            this.weakDependency_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setWeakDependency(int index, int value) {
         this.ensureWeakDependencyIsMutable();
         this.weakDependency_.setInt(index, value);
      }

      private void addWeakDependency(int value) {
         this.ensureWeakDependencyIsMutable();
         this.weakDependency_.addInt(value);
      }

      private void addAllWeakDependency(Iterable<? extends Integer> values) {
         this.ensureWeakDependencyIsMutable();
         AbstractMessageLite.addAll(values, this.weakDependency_);
      }

      private void clearWeakDependency() {
         this.weakDependency_ = emptyIntList();
      }

      @Override
      public List<DescriptorProtos.DescriptorProto> getMessageTypeList() {
         return this.messageType_;
      }

      public List<? extends DescriptorProtos.DescriptorProtoOrBuilder> getMessageTypeOrBuilderList() {
         return this.messageType_;
      }

      @Override
      public int getMessageTypeCount() {
         return this.messageType_.size();
      }

      @Override
      public DescriptorProtos.DescriptorProto getMessageType(int index) {
         return this.messageType_.get(index);
      }

      public DescriptorProtos.DescriptorProtoOrBuilder getMessageTypeOrBuilder(int index) {
         return this.messageType_.get(index);
      }

      private void ensureMessageTypeIsMutable() {
         Internal.ProtobufList<DescriptorProtos.DescriptorProto> tmp = this.messageType_;
         if (!tmp.isModifiable()) {
            this.messageType_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setMessageType(int index, DescriptorProtos.DescriptorProto value) {
         value.getClass();
         this.ensureMessageTypeIsMutable();
         this.messageType_.set(index, value);
      }

      private void addMessageType(DescriptorProtos.DescriptorProto value) {
         value.getClass();
         this.ensureMessageTypeIsMutable();
         this.messageType_.add(value);
      }

      private void addMessageType(int index, DescriptorProtos.DescriptorProto value) {
         value.getClass();
         this.ensureMessageTypeIsMutable();
         this.messageType_.add(index, value);
      }

      private void addAllMessageType(Iterable<? extends DescriptorProtos.DescriptorProto> values) {
         this.ensureMessageTypeIsMutable();
         AbstractMessageLite.addAll(values, this.messageType_);
      }

      private void clearMessageType() {
         this.messageType_ = emptyProtobufList();
      }

      private void removeMessageType(int index) {
         this.ensureMessageTypeIsMutable();
         this.messageType_.remove(index);
      }

      @Override
      public List<DescriptorProtos.EnumDescriptorProto> getEnumTypeList() {
         return this.enumType_;
      }

      public List<? extends DescriptorProtos.EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
         return this.enumType_;
      }

      @Override
      public int getEnumTypeCount() {
         return this.enumType_.size();
      }

      @Override
      public DescriptorProtos.EnumDescriptorProto getEnumType(int index) {
         return this.enumType_.get(index);
      }

      public DescriptorProtos.EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(int index) {
         return this.enumType_.get(index);
      }

      private void ensureEnumTypeIsMutable() {
         Internal.ProtobufList<DescriptorProtos.EnumDescriptorProto> tmp = this.enumType_;
         if (!tmp.isModifiable()) {
            this.enumType_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setEnumType(int index, DescriptorProtos.EnumDescriptorProto value) {
         value.getClass();
         this.ensureEnumTypeIsMutable();
         this.enumType_.set(index, value);
      }

      private void addEnumType(DescriptorProtos.EnumDescriptorProto value) {
         value.getClass();
         this.ensureEnumTypeIsMutable();
         this.enumType_.add(value);
      }

      private void addEnumType(int index, DescriptorProtos.EnumDescriptorProto value) {
         value.getClass();
         this.ensureEnumTypeIsMutable();
         this.enumType_.add(index, value);
      }

      private void addAllEnumType(Iterable<? extends DescriptorProtos.EnumDescriptorProto> values) {
         this.ensureEnumTypeIsMutable();
         AbstractMessageLite.addAll(values, this.enumType_);
      }

      private void clearEnumType() {
         this.enumType_ = emptyProtobufList();
      }

      private void removeEnumType(int index) {
         this.ensureEnumTypeIsMutable();
         this.enumType_.remove(index);
      }

      @Override
      public List<DescriptorProtos.ServiceDescriptorProto> getServiceList() {
         return this.service_;
      }

      public List<? extends DescriptorProtos.ServiceDescriptorProtoOrBuilder> getServiceOrBuilderList() {
         return this.service_;
      }

      @Override
      public int getServiceCount() {
         return this.service_.size();
      }

      @Override
      public DescriptorProtos.ServiceDescriptorProto getService(int index) {
         return this.service_.get(index);
      }

      public DescriptorProtos.ServiceDescriptorProtoOrBuilder getServiceOrBuilder(int index) {
         return this.service_.get(index);
      }

      private void ensureServiceIsMutable() {
         Internal.ProtobufList<DescriptorProtos.ServiceDescriptorProto> tmp = this.service_;
         if (!tmp.isModifiable()) {
            this.service_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setService(int index, DescriptorProtos.ServiceDescriptorProto value) {
         value.getClass();
         this.ensureServiceIsMutable();
         this.service_.set(index, value);
      }

      private void addService(DescriptorProtos.ServiceDescriptorProto value) {
         value.getClass();
         this.ensureServiceIsMutable();
         this.service_.add(value);
      }

      private void addService(int index, DescriptorProtos.ServiceDescriptorProto value) {
         value.getClass();
         this.ensureServiceIsMutable();
         this.service_.add(index, value);
      }

      private void addAllService(Iterable<? extends DescriptorProtos.ServiceDescriptorProto> values) {
         this.ensureServiceIsMutable();
         AbstractMessageLite.addAll(values, this.service_);
      }

      private void clearService() {
         this.service_ = emptyProtobufList();
      }

      private void removeService(int index) {
         this.ensureServiceIsMutable();
         this.service_.remove(index);
      }

      @Override
      public List<DescriptorProtos.FieldDescriptorProto> getExtensionList() {
         return this.extension_;
      }

      public List<? extends DescriptorProtos.FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
         return this.extension_;
      }

      @Override
      public int getExtensionCount() {
         return this.extension_.size();
      }

      @Override
      public DescriptorProtos.FieldDescriptorProto getExtension(int index) {
         return this.extension_.get(index);
      }

      public DescriptorProtos.FieldDescriptorProtoOrBuilder getExtensionOrBuilder(int index) {
         return this.extension_.get(index);
      }

      private void ensureExtensionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.FieldDescriptorProto> tmp = this.extension_;
         if (!tmp.isModifiable()) {
            this.extension_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setExtension(int index, DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureExtensionIsMutable();
         this.extension_.set(index, value);
      }

      private void addExtension(DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureExtensionIsMutable();
         this.extension_.add(value);
      }

      private void addExtension(int index, DescriptorProtos.FieldDescriptorProto value) {
         value.getClass();
         this.ensureExtensionIsMutable();
         this.extension_.add(index, value);
      }

      private void addAllExtension(Iterable<? extends DescriptorProtos.FieldDescriptorProto> values) {
         this.ensureExtensionIsMutable();
         AbstractMessageLite.addAll(values, this.extension_);
      }

      private void clearExtension() {
         this.extension_ = emptyProtobufList();
      }

      private void removeExtension(int index) {
         this.ensureExtensionIsMutable();
         this.extension_.remove(index);
      }

      @Override
      public boolean hasOptions() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public DescriptorProtos.FileOptions getOptions() {
         return this.options_ == null ? DescriptorProtos.FileOptions.getDefaultInstance() : this.options_;
      }

      private void setOptions(DescriptorProtos.FileOptions value) {
         value.getClass();
         this.options_ = value;
         this.bitField0_ |= 4;
      }

      private void mergeOptions(DescriptorProtos.FileOptions value) {
         value.getClass();
         if (this.options_ != null && this.options_ != DescriptorProtos.FileOptions.getDefaultInstance()) {
            this.options_ = DescriptorProtos.FileOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
         } else {
            this.options_ = value;
         }

         this.bitField0_ |= 4;
      }

      private void clearOptions() {
         this.options_ = null;
         this.bitField0_ &= -5;
      }

      @Override
      public boolean hasSourceCodeInfo() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public DescriptorProtos.SourceCodeInfo getSourceCodeInfo() {
         return this.sourceCodeInfo_ == null ? DescriptorProtos.SourceCodeInfo.getDefaultInstance() : this.sourceCodeInfo_;
      }

      private void setSourceCodeInfo(DescriptorProtos.SourceCodeInfo value) {
         value.getClass();
         this.sourceCodeInfo_ = value;
         this.bitField0_ |= 8;
      }

      private void mergeSourceCodeInfo(DescriptorProtos.SourceCodeInfo value) {
         value.getClass();
         if (this.sourceCodeInfo_ != null && this.sourceCodeInfo_ != DescriptorProtos.SourceCodeInfo.getDefaultInstance()) {
            this.sourceCodeInfo_ = DescriptorProtos.SourceCodeInfo.newBuilder(this.sourceCodeInfo_).mergeFrom(value).buildPartial();
         } else {
            this.sourceCodeInfo_ = value;
         }

         this.bitField0_ |= 8;
      }

      private void clearSourceCodeInfo() {
         this.sourceCodeInfo_ = null;
         this.bitField0_ &= -9;
      }

      @Override
      public boolean hasSyntax() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public String getSyntax() {
         return this.syntax_;
      }

      @Override
      public ByteString getSyntaxBytes() {
         return ByteString.copyFromUtf8(this.syntax_);
      }

      private void setSyntax(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 16;
         this.syntax_ = value;
      }

      private void clearSyntax() {
         this.bitField0_ &= -17;
         this.syntax_ = getDefaultInstance().getSyntax();
      }

      private void setSyntaxBytes(ByteString value) {
         this.syntax_ = value.toStringUtf8();
         this.bitField0_ |= 16;
      }

      @Override
      public boolean hasEdition() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public DescriptorProtos.Edition getEdition() {
         DescriptorProtos.Edition result = DescriptorProtos.Edition.forNumber(this.edition_);
         return result == null ? DescriptorProtos.Edition.EDITION_UNKNOWN : result;
      }

      private void setEdition(DescriptorProtos.Edition value) {
         this.edition_ = value.getNumber();
         this.bitField0_ |= 32;
      }

      private void clearEdition() {
         this.bitField0_ &= -33;
         this.edition_ = 0;
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorProto.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.FileDescriptorProto.Builder newBuilder(DescriptorProtos.FileDescriptorProto prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FileDescriptorProto.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FileDescriptorProto;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FileDescriptorProto.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FileDescriptorProto$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.FileDescriptorProto();
            case NEW_BUILDER:
               return new DescriptorProtos.FileDescriptorProto.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "name_",
                  "package_",
                  "dependency_",
                  "messageType_",
                  DescriptorProtos.DescriptorProto.class,
                  "enumType_",
                  DescriptorProtos.EnumDescriptorProto.class,
                  "service_",
                  DescriptorProtos.ServiceDescriptorProto.class,
                  "extension_",
                  DescriptorProtos.FieldDescriptorProto.class,
                  "options_",
                  "sourceCodeInfo_",
                  "publicDependency_",
                  "weakDependency_",
                  "syntax_",
                  "edition_",
                  DescriptorProtos.Edition.internalGetVerifier()
               };
               String info = "\u0001\r\u0000\u0001\u0001\u000e\r\u0000\u0007\u0005\u0001ဈ\u0000\u0002ဈ\u0001\u0003\u001a\u0004Л\u0005Л\u0006Л\u0007Л\bᐉ\u0002\tဉ\u0003\n\u0016\u000b\u0016\fဈ\u0004\u000e᠌\u0005";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.FileDescriptorProto> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.FileDescriptorProto.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.FileDescriptorProto getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.FileDescriptorProto> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.FileDescriptorProto defaultInstance = new DescriptorProtos.FileDescriptorProto();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FileDescriptorProto.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder>
         implements DescriptorProtos.FileDescriptorProtoOrBuilder {
         private Builder() {
            super(DescriptorProtos.FileDescriptorProto.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasName() {
            return this.instance.hasName();
         }

         @Override
         public String getName() {
            return this.instance.getName();
         }

         @Override
         public ByteString getNameBytes() {
            return this.instance.getNameBytes();
         }

         public DescriptorProtos.FileDescriptorProto.Builder setName(String value) {
            this.copyOnWrite();
            this.instance.setName(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setNameBytes(value);
            return this;
         }

         @Override
         public boolean hasPackage() {
            return this.instance.hasPackage();
         }

         @Override
         public String getPackage() {
            return this.instance.getPackage();
         }

         @Override
         public ByteString getPackageBytes() {
            return this.instance.getPackageBytes();
         }

         public DescriptorProtos.FileDescriptorProto.Builder setPackage(String value) {
            this.copyOnWrite();
            this.instance.setPackage(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearPackage() {
            this.copyOnWrite();
            this.instance.clearPackage();
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setPackageBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setPackageBytes(value);
            return this;
         }

         @Override
         public List<String> getDependencyList() {
            return Collections.unmodifiableList(this.instance.getDependencyList());
         }

         @Override
         public int getDependencyCount() {
            return this.instance.getDependencyCount();
         }

         @Override
         public String getDependency(int index) {
            return this.instance.getDependency(index);
         }

         @Override
         public ByteString getDependencyBytes(int index) {
            return this.instance.getDependencyBytes(index);
         }

         public DescriptorProtos.FileDescriptorProto.Builder setDependency(int index, String value) {
            this.copyOnWrite();
            this.instance.setDependency(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addDependency(String value) {
            this.copyOnWrite();
            this.instance.addDependency(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addAllDependency(Iterable<String> values) {
            this.copyOnWrite();
            this.instance.addAllDependency(values);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearDependency() {
            this.copyOnWrite();
            this.instance.clearDependency();
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addDependencyBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.addDependencyBytes(value);
            return this;
         }

         @Override
         public List<Integer> getPublicDependencyList() {
            return Collections.unmodifiableList(this.instance.getPublicDependencyList());
         }

         @Override
         public int getPublicDependencyCount() {
            return this.instance.getPublicDependencyCount();
         }

         @Override
         public int getPublicDependency(int index) {
            return this.instance.getPublicDependency(index);
         }

         public DescriptorProtos.FileDescriptorProto.Builder setPublicDependency(int index, int value) {
            this.copyOnWrite();
            this.instance.setPublicDependency(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addPublicDependency(int value) {
            this.copyOnWrite();
            this.instance.addPublicDependency(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addAllPublicDependency(Iterable<? extends Integer> values) {
            this.copyOnWrite();
            this.instance.addAllPublicDependency(values);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearPublicDependency() {
            this.copyOnWrite();
            this.instance.clearPublicDependency();
            return this;
         }

         @Override
         public List<Integer> getWeakDependencyList() {
            return Collections.unmodifiableList(this.instance.getWeakDependencyList());
         }

         @Override
         public int getWeakDependencyCount() {
            return this.instance.getWeakDependencyCount();
         }

         @Override
         public int getWeakDependency(int index) {
            return this.instance.getWeakDependency(index);
         }

         public DescriptorProtos.FileDescriptorProto.Builder setWeakDependency(int index, int value) {
            this.copyOnWrite();
            this.instance.setWeakDependency(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addWeakDependency(int value) {
            this.copyOnWrite();
            this.instance.addWeakDependency(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addAllWeakDependency(Iterable<? extends Integer> values) {
            this.copyOnWrite();
            this.instance.addAllWeakDependency(values);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearWeakDependency() {
            this.copyOnWrite();
            this.instance.clearWeakDependency();
            return this;
         }

         @Override
         public List<DescriptorProtos.DescriptorProto> getMessageTypeList() {
            return Collections.unmodifiableList(this.instance.getMessageTypeList());
         }

         @Override
         public int getMessageTypeCount() {
            return this.instance.getMessageTypeCount();
         }

         @Override
         public DescriptorProtos.DescriptorProto getMessageType(int index) {
            return this.instance.getMessageType(index);
         }

         public DescriptorProtos.FileDescriptorProto.Builder setMessageType(int index, DescriptorProtos.DescriptorProto value) {
            this.copyOnWrite();
            this.instance.setMessageType(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setMessageType(int index, DescriptorProtos.DescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setMessageType(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addMessageType(DescriptorProtos.DescriptorProto value) {
            this.copyOnWrite();
            this.instance.addMessageType(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addMessageType(int index, DescriptorProtos.DescriptorProto value) {
            this.copyOnWrite();
            this.instance.addMessageType(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addMessageType(DescriptorProtos.DescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addMessageType(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addMessageType(int index, DescriptorProtos.DescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addMessageType(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addAllMessageType(Iterable<? extends DescriptorProtos.DescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllMessageType(values);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearMessageType() {
            this.copyOnWrite();
            this.instance.clearMessageType();
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder removeMessageType(int index) {
            this.copyOnWrite();
            this.instance.removeMessageType(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.EnumDescriptorProto> getEnumTypeList() {
            return Collections.unmodifiableList(this.instance.getEnumTypeList());
         }

         @Override
         public int getEnumTypeCount() {
            return this.instance.getEnumTypeCount();
         }

         @Override
         public DescriptorProtos.EnumDescriptorProto getEnumType(int index) {
            return this.instance.getEnumType(index);
         }

         public DescriptorProtos.FileDescriptorProto.Builder setEnumType(int index, DescriptorProtos.EnumDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setEnumType(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setEnumType(int index, DescriptorProtos.EnumDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setEnumType(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addEnumType(DescriptorProtos.EnumDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addEnumType(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addEnumType(int index, DescriptorProtos.EnumDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addEnumType(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addEnumType(DescriptorProtos.EnumDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addEnumType(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addEnumType(int index, DescriptorProtos.EnumDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addEnumType(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addAllEnumType(Iterable<? extends DescriptorProtos.EnumDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllEnumType(values);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearEnumType() {
            this.copyOnWrite();
            this.instance.clearEnumType();
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder removeEnumType(int index) {
            this.copyOnWrite();
            this.instance.removeEnumType(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.ServiceDescriptorProto> getServiceList() {
            return Collections.unmodifiableList(this.instance.getServiceList());
         }

         @Override
         public int getServiceCount() {
            return this.instance.getServiceCount();
         }

         @Override
         public DescriptorProtos.ServiceDescriptorProto getService(int index) {
            return this.instance.getService(index);
         }

         public DescriptorProtos.FileDescriptorProto.Builder setService(int index, DescriptorProtos.ServiceDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setService(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setService(int index, DescriptorProtos.ServiceDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setService(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addService(DescriptorProtos.ServiceDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addService(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addService(int index, DescriptorProtos.ServiceDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addService(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addService(DescriptorProtos.ServiceDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addService(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addService(int index, DescriptorProtos.ServiceDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addService(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addAllService(Iterable<? extends DescriptorProtos.ServiceDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllService(values);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearService() {
            this.copyOnWrite();
            this.instance.clearService();
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder removeService(int index) {
            this.copyOnWrite();
            this.instance.removeService(index);
            return this;
         }

         @Override
         public List<DescriptorProtos.FieldDescriptorProto> getExtensionList() {
            return Collections.unmodifiableList(this.instance.getExtensionList());
         }

         @Override
         public int getExtensionCount() {
            return this.instance.getExtensionCount();
         }

         @Override
         public DescriptorProtos.FieldDescriptorProto getExtension(int index) {
            return this.instance.getExtension(index);
         }

         public DescriptorProtos.FileDescriptorProto.Builder setExtension(int index, DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setExtension(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setExtension(int index, DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setExtension(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addExtension(DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addExtension(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addExtension(int index, DescriptorProtos.FieldDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addExtension(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addExtension(DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addExtension(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addExtension(int index, DescriptorProtos.FieldDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addExtension(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder addAllExtension(Iterable<? extends DescriptorProtos.FieldDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllExtension(values);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearExtension() {
            this.copyOnWrite();
            this.instance.clearExtension();
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder removeExtension(int index) {
            this.copyOnWrite();
            this.instance.removeExtension(index);
            return this;
         }

         @Override
         public boolean hasOptions() {
            return this.instance.hasOptions();
         }

         @Override
         public DescriptorProtos.FileOptions getOptions() {
            return this.instance.getOptions();
         }

         public DescriptorProtos.FileDescriptorProto.Builder setOptions(DescriptorProtos.FileOptions value) {
            this.copyOnWrite();
            this.instance.setOptions(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setOptions(DescriptorProtos.FileOptions.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOptions(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder mergeOptions(DescriptorProtos.FileOptions value) {
            this.copyOnWrite();
            this.instance.mergeOptions(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearOptions() {
            this.copyOnWrite();
            this.instance.clearOptions();
            return this;
         }

         @Override
         public boolean hasSourceCodeInfo() {
            return this.instance.hasSourceCodeInfo();
         }

         @Override
         public DescriptorProtos.SourceCodeInfo getSourceCodeInfo() {
            return this.instance.getSourceCodeInfo();
         }

         public DescriptorProtos.FileDescriptorProto.Builder setSourceCodeInfo(DescriptorProtos.SourceCodeInfo value) {
            this.copyOnWrite();
            this.instance.setSourceCodeInfo(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setSourceCodeInfo(DescriptorProtos.SourceCodeInfo.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setSourceCodeInfo(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder mergeSourceCodeInfo(DescriptorProtos.SourceCodeInfo value) {
            this.copyOnWrite();
            this.instance.mergeSourceCodeInfo(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearSourceCodeInfo() {
            this.copyOnWrite();
            this.instance.clearSourceCodeInfo();
            return this;
         }

         @Override
         public boolean hasSyntax() {
            return this.instance.hasSyntax();
         }

         @Override
         public String getSyntax() {
            return this.instance.getSyntax();
         }

         @Override
         public ByteString getSyntaxBytes() {
            return this.instance.getSyntaxBytes();
         }

         public DescriptorProtos.FileDescriptorProto.Builder setSyntax(String value) {
            this.copyOnWrite();
            this.instance.setSyntax(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearSyntax() {
            this.copyOnWrite();
            this.instance.clearSyntax();
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder setSyntaxBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setSyntaxBytes(value);
            return this;
         }

         @Override
         public boolean hasEdition() {
            return this.instance.hasEdition();
         }

         @Override
         public DescriptorProtos.Edition getEdition() {
            return this.instance.getEdition();
         }

         public DescriptorProtos.FileDescriptorProto.Builder setEdition(DescriptorProtos.Edition value) {
            this.copyOnWrite();
            this.instance.setEdition(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder clearEdition() {
            this.copyOnWrite();
            this.instance.clearEdition();
            return this;
         }
      }
   }

   public interface FileDescriptorProtoOrBuilder extends MessageLiteOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasPackage();

      String getPackage();

      ByteString getPackageBytes();

      List<String> getDependencyList();

      int getDependencyCount();

      String getDependency(int index);

      ByteString getDependencyBytes(int index);

      List<Integer> getPublicDependencyList();

      int getPublicDependencyCount();

      int getPublicDependency(int index);

      List<Integer> getWeakDependencyList();

      int getWeakDependencyCount();

      int getWeakDependency(int index);

      List<DescriptorProtos.DescriptorProto> getMessageTypeList();

      DescriptorProtos.DescriptorProto getMessageType(int index);

      int getMessageTypeCount();

      List<DescriptorProtos.EnumDescriptorProto> getEnumTypeList();

      DescriptorProtos.EnumDescriptorProto getEnumType(int index);

      int getEnumTypeCount();

      List<DescriptorProtos.ServiceDescriptorProto> getServiceList();

      DescriptorProtos.ServiceDescriptorProto getService(int index);

      int getServiceCount();

      List<DescriptorProtos.FieldDescriptorProto> getExtensionList();

      DescriptorProtos.FieldDescriptorProto getExtension(int index);

      int getExtensionCount();

      boolean hasOptions();

      DescriptorProtos.FileOptions getOptions();

      boolean hasSourceCodeInfo();

      DescriptorProtos.SourceCodeInfo getSourceCodeInfo();

      boolean hasSyntax();

      String getSyntax();

      ByteString getSyntaxBytes();

      boolean hasEdition();

      DescriptorProtos.Edition getEdition();
   }

   public static final class FileDescriptorSet
      extends GeneratedMessageLite<DescriptorProtos.FileDescriptorSet, DescriptorProtos.FileDescriptorSet.Builder>
      implements DescriptorProtos.FileDescriptorSetOrBuilder {
      public static final int FILE_FIELD_NUMBER = 1;
      private Internal.ProtobufList<DescriptorProtos.FileDescriptorProto> file_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.FileDescriptorSet DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.FileDescriptorSet> PARSER;

      private FileDescriptorSet() {
         this.file_ = emptyProtobufList();
      }

      @Override
      public List<DescriptorProtos.FileDescriptorProto> getFileList() {
         return this.file_;
      }

      public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getFileOrBuilderList() {
         return this.file_;
      }

      @Override
      public int getFileCount() {
         return this.file_.size();
      }

      @Override
      public DescriptorProtos.FileDescriptorProto getFile(int index) {
         return this.file_.get(index);
      }

      public DescriptorProtos.FileDescriptorProtoOrBuilder getFileOrBuilder(int index) {
         return this.file_.get(index);
      }

      private void ensureFileIsMutable() {
         Internal.ProtobufList<DescriptorProtos.FileDescriptorProto> tmp = this.file_;
         if (!tmp.isModifiable()) {
            this.file_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setFile(int index, DescriptorProtos.FileDescriptorProto value) {
         value.getClass();
         this.ensureFileIsMutable();
         this.file_.set(index, value);
      }

      private void addFile(DescriptorProtos.FileDescriptorProto value) {
         value.getClass();
         this.ensureFileIsMutable();
         this.file_.add(value);
      }

      private void addFile(int index, DescriptorProtos.FileDescriptorProto value) {
         value.getClass();
         this.ensureFileIsMutable();
         this.file_.add(index, value);
      }

      private void addAllFile(Iterable<? extends DescriptorProtos.FileDescriptorProto> values) {
         this.ensureFileIsMutable();
         AbstractMessageLite.addAll(values, this.file_);
      }

      private void clearFile() {
         this.file_ = emptyProtobufList();
      }

      private void removeFile(int index) {
         this.ensureFileIsMutable();
         this.file_.remove(index);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorSet parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileDescriptorSet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileDescriptorSet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileDescriptorSet.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.FileDescriptorSet.Builder newBuilder(DescriptorProtos.FileDescriptorSet prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FileDescriptorSet.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FileDescriptorSet;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FileDescriptorSet.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FileDescriptorSet$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.FileDescriptorSet();
            case NEW_BUILDER:
               return new DescriptorProtos.FileDescriptorSet.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"file_", DescriptorProtos.FileDescriptorProto.class};
               String info = "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0001Л";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.FileDescriptorSet> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.FileDescriptorSet.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.FileDescriptorSet getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.FileDescriptorSet> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.FileDescriptorSet defaultInstance = new DescriptorProtos.FileDescriptorSet();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FileDescriptorSet.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.FileDescriptorSet, DescriptorProtos.FileDescriptorSet.Builder>
         implements DescriptorProtos.FileDescriptorSetOrBuilder {
         private Builder() {
            super(DescriptorProtos.FileDescriptorSet.DEFAULT_INSTANCE);
         }

         @Override
         public List<DescriptorProtos.FileDescriptorProto> getFileList() {
            return Collections.unmodifiableList(this.instance.getFileList());
         }

         @Override
         public int getFileCount() {
            return this.instance.getFileCount();
         }

         @Override
         public DescriptorProtos.FileDescriptorProto getFile(int index) {
            return this.instance.getFile(index);
         }

         public DescriptorProtos.FileDescriptorSet.Builder setFile(int index, DescriptorProtos.FileDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setFile(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorSet.Builder setFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFile(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorSet.Builder addFile(DescriptorProtos.FileDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addFile(value);
            return this;
         }

         public DescriptorProtos.FileDescriptorSet.Builder addFile(int index, DescriptorProtos.FileDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addFile(index, value);
            return this;
         }

         public DescriptorProtos.FileDescriptorSet.Builder addFile(DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addFile(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorSet.Builder addFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addFile(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileDescriptorSet.Builder addAllFile(Iterable<? extends DescriptorProtos.FileDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllFile(values);
            return this;
         }

         public DescriptorProtos.FileDescriptorSet.Builder clearFile() {
            this.copyOnWrite();
            this.instance.clearFile();
            return this;
         }

         public DescriptorProtos.FileDescriptorSet.Builder removeFile(int index) {
            this.copyOnWrite();
            this.instance.removeFile(index);
            return this;
         }
      }
   }

   public interface FileDescriptorSetOrBuilder extends MessageLiteOrBuilder {
      List<DescriptorProtos.FileDescriptorProto> getFileList();

      DescriptorProtos.FileDescriptorProto getFile(int index);

      int getFileCount();
   }

   public static final class FileOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.FileOptions, DescriptorProtos.FileOptions.Builder>
      implements DescriptorProtos.FileOptionsOrBuilder {
      private int bitField0_;
      public static final int JAVA_PACKAGE_FIELD_NUMBER = 1;
      private String javaPackage_;
      public static final int JAVA_OUTER_CLASSNAME_FIELD_NUMBER = 8;
      private String javaOuterClassname_;
      public static final int JAVA_MULTIPLE_FILES_FIELD_NUMBER = 10;
      private boolean javaMultipleFiles_;
      public static final int JAVA_GENERATE_EQUALS_AND_HASH_FIELD_NUMBER = 20;
      private boolean javaGenerateEqualsAndHash_;
      public static final int JAVA_STRING_CHECK_UTF8_FIELD_NUMBER = 27;
      private boolean javaStringCheckUtf8_;
      public static final int OPTIMIZE_FOR_FIELD_NUMBER = 9;
      private int optimizeFor_;
      public static final int GO_PACKAGE_FIELD_NUMBER = 11;
      private String goPackage_;
      public static final int CC_GENERIC_SERVICES_FIELD_NUMBER = 16;
      private boolean ccGenericServices_;
      public static final int JAVA_GENERIC_SERVICES_FIELD_NUMBER = 17;
      private boolean javaGenericServices_;
      public static final int PY_GENERIC_SERVICES_FIELD_NUMBER = 18;
      private boolean pyGenericServices_;
      public static final int DEPRECATED_FIELD_NUMBER = 23;
      private boolean deprecated_;
      public static final int CC_ENABLE_ARENAS_FIELD_NUMBER = 31;
      private boolean ccEnableArenas_;
      public static final int OBJC_CLASS_PREFIX_FIELD_NUMBER = 36;
      private String objcClassPrefix_;
      public static final int CSHARP_NAMESPACE_FIELD_NUMBER = 37;
      private String csharpNamespace_;
      public static final int SWIFT_PREFIX_FIELD_NUMBER = 39;
      private String swiftPrefix_;
      public static final int PHP_CLASS_PREFIX_FIELD_NUMBER = 40;
      private String phpClassPrefix_;
      public static final int PHP_NAMESPACE_FIELD_NUMBER = 41;
      private String phpNamespace_;
      public static final int PHP_METADATA_NAMESPACE_FIELD_NUMBER = 44;
      private String phpMetadataNamespace_;
      public static final int RUBY_PACKAGE_FIELD_NUMBER = 45;
      private String rubyPackage_;
      public static final int FEATURES_FIELD_NUMBER = 50;
      private DescriptorProtos.FeatureSet features_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.FileOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.FileOptions> PARSER;

      private FileOptions() {
         this.javaPackage_ = "";
         this.javaOuterClassname_ = "";
         this.optimizeFor_ = 1;
         this.goPackage_ = "";
         this.ccEnableArenas_ = true;
         this.objcClassPrefix_ = "";
         this.csharpNamespace_ = "";
         this.swiftPrefix_ = "";
         this.phpClassPrefix_ = "";
         this.phpNamespace_ = "";
         this.phpMetadataNamespace_ = "";
         this.rubyPackage_ = "";
         this.uninterpretedOption_ = emptyProtobufList();
      }

      @Override
      public boolean hasJavaPackage() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getJavaPackage() {
         return this.javaPackage_;
      }

      @Override
      public ByteString getJavaPackageBytes() {
         return ByteString.copyFromUtf8(this.javaPackage_);
      }

      private void setJavaPackage(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.javaPackage_ = value;
      }

      private void clearJavaPackage() {
         this.bitField0_ &= -2;
         this.javaPackage_ = getDefaultInstance().getJavaPackage();
      }

      private void setJavaPackageBytes(ByteString value) {
         this.javaPackage_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public boolean hasJavaOuterClassname() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getJavaOuterClassname() {
         return this.javaOuterClassname_;
      }

      @Override
      public ByteString getJavaOuterClassnameBytes() {
         return ByteString.copyFromUtf8(this.javaOuterClassname_);
      }

      private void setJavaOuterClassname(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 2;
         this.javaOuterClassname_ = value;
      }

      private void clearJavaOuterClassname() {
         this.bitField0_ &= -3;
         this.javaOuterClassname_ = getDefaultInstance().getJavaOuterClassname();
      }

      private void setJavaOuterClassnameBytes(ByteString value) {
         this.javaOuterClassname_ = value.toStringUtf8();
         this.bitField0_ |= 2;
      }

      @Override
      public boolean hasJavaMultipleFiles() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public boolean getJavaMultipleFiles() {
         return this.javaMultipleFiles_;
      }

      private void setJavaMultipleFiles(boolean value) {
         this.bitField0_ |= 4;
         this.javaMultipleFiles_ = value;
      }

      private void clearJavaMultipleFiles() {
         this.bitField0_ &= -5;
         this.javaMultipleFiles_ = false;
      }

      @Deprecated
      @Override
      public boolean hasJavaGenerateEqualsAndHash() {
         return (this.bitField0_ & 8) != 0;
      }

      @Deprecated
      @Override
      public boolean getJavaGenerateEqualsAndHash() {
         return this.javaGenerateEqualsAndHash_;
      }

      /** @deprecated */
      private void setJavaGenerateEqualsAndHash(boolean value) {
         this.bitField0_ |= 8;
         this.javaGenerateEqualsAndHash_ = value;
      }

      /** @deprecated */
      private void clearJavaGenerateEqualsAndHash() {
         this.bitField0_ &= -9;
         this.javaGenerateEqualsAndHash_ = false;
      }

      @Override
      public boolean hasJavaStringCheckUtf8() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public boolean getJavaStringCheckUtf8() {
         return this.javaStringCheckUtf8_;
      }

      private void setJavaStringCheckUtf8(boolean value) {
         this.bitField0_ |= 16;
         this.javaStringCheckUtf8_ = value;
      }

      private void clearJavaStringCheckUtf8() {
         this.bitField0_ &= -17;
         this.javaStringCheckUtf8_ = false;
      }

      @Override
      public boolean hasOptimizeFor() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public DescriptorProtos.FileOptions.OptimizeMode getOptimizeFor() {
         DescriptorProtos.FileOptions.OptimizeMode result = DescriptorProtos.FileOptions.OptimizeMode.forNumber(this.optimizeFor_);
         return result == null ? DescriptorProtos.FileOptions.OptimizeMode.SPEED : result;
      }

      private void setOptimizeFor(DescriptorProtos.FileOptions.OptimizeMode value) {
         this.optimizeFor_ = value.getNumber();
         this.bitField0_ |= 32;
      }

      private void clearOptimizeFor() {
         this.bitField0_ &= -33;
         this.optimizeFor_ = 1;
      }

      @Override
      public boolean hasGoPackage() {
         return (this.bitField0_ & 64) != 0;
      }

      @Override
      public String getGoPackage() {
         return this.goPackage_;
      }

      @Override
      public ByteString getGoPackageBytes() {
         return ByteString.copyFromUtf8(this.goPackage_);
      }

      private void setGoPackage(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 64;
         this.goPackage_ = value;
      }

      private void clearGoPackage() {
         this.bitField0_ &= -65;
         this.goPackage_ = getDefaultInstance().getGoPackage();
      }

      private void setGoPackageBytes(ByteString value) {
         this.goPackage_ = value.toStringUtf8();
         this.bitField0_ |= 64;
      }

      @Override
      public boolean hasCcGenericServices() {
         return (this.bitField0_ & 128) != 0;
      }

      @Override
      public boolean getCcGenericServices() {
         return this.ccGenericServices_;
      }

      private void setCcGenericServices(boolean value) {
         this.bitField0_ |= 128;
         this.ccGenericServices_ = value;
      }

      private void clearCcGenericServices() {
         this.bitField0_ &= -129;
         this.ccGenericServices_ = false;
      }

      @Override
      public boolean hasJavaGenericServices() {
         return (this.bitField0_ & 256) != 0;
      }

      @Override
      public boolean getJavaGenericServices() {
         return this.javaGenericServices_;
      }

      private void setJavaGenericServices(boolean value) {
         this.bitField0_ |= 256;
         this.javaGenericServices_ = value;
      }

      private void clearJavaGenericServices() {
         this.bitField0_ &= -257;
         this.javaGenericServices_ = false;
      }

      @Override
      public boolean hasPyGenericServices() {
         return (this.bitField0_ & 512) != 0;
      }

      @Override
      public boolean getPyGenericServices() {
         return this.pyGenericServices_;
      }

      private void setPyGenericServices(boolean value) {
         this.bitField0_ |= 512;
         this.pyGenericServices_ = value;
      }

      private void clearPyGenericServices() {
         this.bitField0_ &= -513;
         this.pyGenericServices_ = false;
      }

      @Override
      public boolean hasDeprecated() {
         return (this.bitField0_ & 1024) != 0;
      }

      @Override
      public boolean getDeprecated() {
         return this.deprecated_;
      }

      private void setDeprecated(boolean value) {
         this.bitField0_ |= 1024;
         this.deprecated_ = value;
      }

      private void clearDeprecated() {
         this.bitField0_ &= -1025;
         this.deprecated_ = false;
      }

      @Override
      public boolean hasCcEnableArenas() {
         return (this.bitField0_ & 2048) != 0;
      }

      @Override
      public boolean getCcEnableArenas() {
         return this.ccEnableArenas_;
      }

      private void setCcEnableArenas(boolean value) {
         this.bitField0_ |= 2048;
         this.ccEnableArenas_ = value;
      }

      private void clearCcEnableArenas() {
         this.bitField0_ &= -2049;
         this.ccEnableArenas_ = true;
      }

      @Override
      public boolean hasObjcClassPrefix() {
         return (this.bitField0_ & 4096) != 0;
      }

      @Override
      public String getObjcClassPrefix() {
         return this.objcClassPrefix_;
      }

      @Override
      public ByteString getObjcClassPrefixBytes() {
         return ByteString.copyFromUtf8(this.objcClassPrefix_);
      }

      private void setObjcClassPrefix(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 4096;
         this.objcClassPrefix_ = value;
      }

      private void clearObjcClassPrefix() {
         this.bitField0_ &= -4097;
         this.objcClassPrefix_ = getDefaultInstance().getObjcClassPrefix();
      }

      private void setObjcClassPrefixBytes(ByteString value) {
         this.objcClassPrefix_ = value.toStringUtf8();
         this.bitField0_ |= 4096;
      }

      @Override
      public boolean hasCsharpNamespace() {
         return (this.bitField0_ & 8192) != 0;
      }

      @Override
      public String getCsharpNamespace() {
         return this.csharpNamespace_;
      }

      @Override
      public ByteString getCsharpNamespaceBytes() {
         return ByteString.copyFromUtf8(this.csharpNamespace_);
      }

      private void setCsharpNamespace(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 8192;
         this.csharpNamespace_ = value;
      }

      private void clearCsharpNamespace() {
         this.bitField0_ &= -8193;
         this.csharpNamespace_ = getDefaultInstance().getCsharpNamespace();
      }

      private void setCsharpNamespaceBytes(ByteString value) {
         this.csharpNamespace_ = value.toStringUtf8();
         this.bitField0_ |= 8192;
      }

      @Override
      public boolean hasSwiftPrefix() {
         return (this.bitField0_ & 16384) != 0;
      }

      @Override
      public String getSwiftPrefix() {
         return this.swiftPrefix_;
      }

      @Override
      public ByteString getSwiftPrefixBytes() {
         return ByteString.copyFromUtf8(this.swiftPrefix_);
      }

      private void setSwiftPrefix(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 16384;
         this.swiftPrefix_ = value;
      }

      private void clearSwiftPrefix() {
         this.bitField0_ &= -16385;
         this.swiftPrefix_ = getDefaultInstance().getSwiftPrefix();
      }

      private void setSwiftPrefixBytes(ByteString value) {
         this.swiftPrefix_ = value.toStringUtf8();
         this.bitField0_ |= 16384;
      }

      @Override
      public boolean hasPhpClassPrefix() {
         return (this.bitField0_ & 32768) != 0;
      }

      @Override
      public String getPhpClassPrefix() {
         return this.phpClassPrefix_;
      }

      @Override
      public ByteString getPhpClassPrefixBytes() {
         return ByteString.copyFromUtf8(this.phpClassPrefix_);
      }

      private void setPhpClassPrefix(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 32768;
         this.phpClassPrefix_ = value;
      }

      private void clearPhpClassPrefix() {
         this.bitField0_ &= -32769;
         this.phpClassPrefix_ = getDefaultInstance().getPhpClassPrefix();
      }

      private void setPhpClassPrefixBytes(ByteString value) {
         this.phpClassPrefix_ = value.toStringUtf8();
         this.bitField0_ |= 32768;
      }

      @Override
      public boolean hasPhpNamespace() {
         return (this.bitField0_ & 65536) != 0;
      }

      @Override
      public String getPhpNamespace() {
         return this.phpNamespace_;
      }

      @Override
      public ByteString getPhpNamespaceBytes() {
         return ByteString.copyFromUtf8(this.phpNamespace_);
      }

      private void setPhpNamespace(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 65536;
         this.phpNamespace_ = value;
      }

      private void clearPhpNamespace() {
         this.bitField0_ &= -65537;
         this.phpNamespace_ = getDefaultInstance().getPhpNamespace();
      }

      private void setPhpNamespaceBytes(ByteString value) {
         this.phpNamespace_ = value.toStringUtf8();
         this.bitField0_ |= 65536;
      }

      @Override
      public boolean hasPhpMetadataNamespace() {
         return (this.bitField0_ & 131072) != 0;
      }

      @Override
      public String getPhpMetadataNamespace() {
         return this.phpMetadataNamespace_;
      }

      @Override
      public ByteString getPhpMetadataNamespaceBytes() {
         return ByteString.copyFromUtf8(this.phpMetadataNamespace_);
      }

      private void setPhpMetadataNamespace(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 131072;
         this.phpMetadataNamespace_ = value;
      }

      private void clearPhpMetadataNamespace() {
         this.bitField0_ &= -131073;
         this.phpMetadataNamespace_ = getDefaultInstance().getPhpMetadataNamespace();
      }

      private void setPhpMetadataNamespaceBytes(ByteString value) {
         this.phpMetadataNamespace_ = value.toStringUtf8();
         this.bitField0_ |= 131072;
      }

      @Override
      public boolean hasRubyPackage() {
         return (this.bitField0_ & 262144) != 0;
      }

      @Override
      public String getRubyPackage() {
         return this.rubyPackage_;
      }

      @Override
      public ByteString getRubyPackageBytes() {
         return ByteString.copyFromUtf8(this.rubyPackage_);
      }

      private void setRubyPackage(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 262144;
         this.rubyPackage_ = value;
      }

      private void clearRubyPackage() {
         this.bitField0_ &= -262145;
         this.rubyPackage_ = getDefaultInstance().getRubyPackage();
      }

      private void setRubyPackageBytes(ByteString value) {
         this.rubyPackage_ = value.toStringUtf8();
         this.bitField0_ |= 262144;
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 524288) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 524288;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 524288;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -524289;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      public static DescriptorProtos.FileOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.FileOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.FileOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.FileOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.FileOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.FileOptions.Builder newBuilder(DescriptorProtos.FileOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1734)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$FileOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$FileOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$FileOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$FileOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.FileOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.FileOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "javaPackage_",
                  "javaOuterClassname_",
                  "optimizeFor_",
                  DescriptorProtos.FileOptions.OptimizeMode.internalGetVerifier(),
                  "javaMultipleFiles_",
                  "goPackage_",
                  "ccGenericServices_",
                  "javaGenericServices_",
                  "pyGenericServices_",
                  "javaGenerateEqualsAndHash_",
                  "deprecated_",
                  "javaStringCheckUtf8_",
                  "ccEnableArenas_",
                  "objcClassPrefix_",
                  "csharpNamespace_",
                  "swiftPrefix_",
                  "phpClassPrefix_",
                  "phpNamespace_",
                  "phpMetadataNamespace_",
                  "rubyPackage_",
                  "features_",
                  "uninterpretedOption_",
                  DescriptorProtos.UninterpretedOption.class
               };
               String info = "\u0001\u0015\u0000\u0001\u0001ϧ\u0015\u0000\u0001\u0002\u0001ဈ\u0000\bဈ\u0001\t᠌\u0005\nဇ\u0002\u000bဈ\u0006\u0010ဇ\u0007\u0011ဇ\b\u0012ဇ\t\u0014ဇ\u0003\u0017ဇ\n\u001bဇ\u0004\u001fဇ\u000b$ဈ\f%ဈ\r'ဈ\u000e(ဈ\u000f)ဈ\u0010,ဈ\u0011-ဈ\u00122ᐉ\u0013ϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.FileOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.FileOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.FileOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.FileOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.FileOptions defaultInstance = new DescriptorProtos.FileOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.FileOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.FileOptions, DescriptorProtos.FileOptions.Builder>
         implements DescriptorProtos.FileOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.FileOptions.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasJavaPackage() {
            return this.instance.hasJavaPackage();
         }

         @Override
         public String getJavaPackage() {
            return this.instance.getJavaPackage();
         }

         @Override
         public ByteString getJavaPackageBytes() {
            return this.instance.getJavaPackageBytes();
         }

         public DescriptorProtos.FileOptions.Builder setJavaPackage(String value) {
            this.copyOnWrite();
            this.instance.setJavaPackage(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearJavaPackage() {
            this.copyOnWrite();
            this.instance.clearJavaPackage();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setJavaPackageBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setJavaPackageBytes(value);
            return this;
         }

         @Override
         public boolean hasJavaOuterClassname() {
            return this.instance.hasJavaOuterClassname();
         }

         @Override
         public String getJavaOuterClassname() {
            return this.instance.getJavaOuterClassname();
         }

         @Override
         public ByteString getJavaOuterClassnameBytes() {
            return this.instance.getJavaOuterClassnameBytes();
         }

         public DescriptorProtos.FileOptions.Builder setJavaOuterClassname(String value) {
            this.copyOnWrite();
            this.instance.setJavaOuterClassname(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearJavaOuterClassname() {
            this.copyOnWrite();
            this.instance.clearJavaOuterClassname();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setJavaOuterClassnameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setJavaOuterClassnameBytes(value);
            return this;
         }

         @Override
         public boolean hasJavaMultipleFiles() {
            return this.instance.hasJavaMultipleFiles();
         }

         @Override
         public boolean getJavaMultipleFiles() {
            return this.instance.getJavaMultipleFiles();
         }

         public DescriptorProtos.FileOptions.Builder setJavaMultipleFiles(boolean value) {
            this.copyOnWrite();
            this.instance.setJavaMultipleFiles(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearJavaMultipleFiles() {
            this.copyOnWrite();
            this.instance.clearJavaMultipleFiles();
            return this;
         }

         @Deprecated
         @Override
         public boolean hasJavaGenerateEqualsAndHash() {
            return this.instance.hasJavaGenerateEqualsAndHash();
         }

         @Deprecated
         @Override
         public boolean getJavaGenerateEqualsAndHash() {
            return this.instance.getJavaGenerateEqualsAndHash();
         }

         @Deprecated
         public DescriptorProtos.FileOptions.Builder setJavaGenerateEqualsAndHash(boolean value) {
            this.copyOnWrite();
            this.instance.setJavaGenerateEqualsAndHash(value);
            return this;
         }

         @Deprecated
         public DescriptorProtos.FileOptions.Builder clearJavaGenerateEqualsAndHash() {
            this.copyOnWrite();
            this.instance.clearJavaGenerateEqualsAndHash();
            return this;
         }

         @Override
         public boolean hasJavaStringCheckUtf8() {
            return this.instance.hasJavaStringCheckUtf8();
         }

         @Override
         public boolean getJavaStringCheckUtf8() {
            return this.instance.getJavaStringCheckUtf8();
         }

         public DescriptorProtos.FileOptions.Builder setJavaStringCheckUtf8(boolean value) {
            this.copyOnWrite();
            this.instance.setJavaStringCheckUtf8(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearJavaStringCheckUtf8() {
            this.copyOnWrite();
            this.instance.clearJavaStringCheckUtf8();
            return this;
         }

         @Override
         public boolean hasOptimizeFor() {
            return this.instance.hasOptimizeFor();
         }

         @Override
         public DescriptorProtos.FileOptions.OptimizeMode getOptimizeFor() {
            return this.instance.getOptimizeFor();
         }

         public DescriptorProtos.FileOptions.Builder setOptimizeFor(DescriptorProtos.FileOptions.OptimizeMode value) {
            this.copyOnWrite();
            this.instance.setOptimizeFor(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearOptimizeFor() {
            this.copyOnWrite();
            this.instance.clearOptimizeFor();
            return this;
         }

         @Override
         public boolean hasGoPackage() {
            return this.instance.hasGoPackage();
         }

         @Override
         public String getGoPackage() {
            return this.instance.getGoPackage();
         }

         @Override
         public ByteString getGoPackageBytes() {
            return this.instance.getGoPackageBytes();
         }

         public DescriptorProtos.FileOptions.Builder setGoPackage(String value) {
            this.copyOnWrite();
            this.instance.setGoPackage(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearGoPackage() {
            this.copyOnWrite();
            this.instance.clearGoPackage();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setGoPackageBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setGoPackageBytes(value);
            return this;
         }

         @Override
         public boolean hasCcGenericServices() {
            return this.instance.hasCcGenericServices();
         }

         @Override
         public boolean getCcGenericServices() {
            return this.instance.getCcGenericServices();
         }

         public DescriptorProtos.FileOptions.Builder setCcGenericServices(boolean value) {
            this.copyOnWrite();
            this.instance.setCcGenericServices(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearCcGenericServices() {
            this.copyOnWrite();
            this.instance.clearCcGenericServices();
            return this;
         }

         @Override
         public boolean hasJavaGenericServices() {
            return this.instance.hasJavaGenericServices();
         }

         @Override
         public boolean getJavaGenericServices() {
            return this.instance.getJavaGenericServices();
         }

         public DescriptorProtos.FileOptions.Builder setJavaGenericServices(boolean value) {
            this.copyOnWrite();
            this.instance.setJavaGenericServices(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearJavaGenericServices() {
            this.copyOnWrite();
            this.instance.clearJavaGenericServices();
            return this;
         }

         @Override
         public boolean hasPyGenericServices() {
            return this.instance.hasPyGenericServices();
         }

         @Override
         public boolean getPyGenericServices() {
            return this.instance.getPyGenericServices();
         }

         public DescriptorProtos.FileOptions.Builder setPyGenericServices(boolean value) {
            this.copyOnWrite();
            this.instance.setPyGenericServices(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearPyGenericServices() {
            this.copyOnWrite();
            this.instance.clearPyGenericServices();
            return this;
         }

         @Override
         public boolean hasDeprecated() {
            return this.instance.hasDeprecated();
         }

         @Override
         public boolean getDeprecated() {
            return this.instance.getDeprecated();
         }

         public DescriptorProtos.FileOptions.Builder setDeprecated(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecated(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearDeprecated() {
            this.copyOnWrite();
            this.instance.clearDeprecated();
            return this;
         }

         @Override
         public boolean hasCcEnableArenas() {
            return this.instance.hasCcEnableArenas();
         }

         @Override
         public boolean getCcEnableArenas() {
            return this.instance.getCcEnableArenas();
         }

         public DescriptorProtos.FileOptions.Builder setCcEnableArenas(boolean value) {
            this.copyOnWrite();
            this.instance.setCcEnableArenas(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearCcEnableArenas() {
            this.copyOnWrite();
            this.instance.clearCcEnableArenas();
            return this;
         }

         @Override
         public boolean hasObjcClassPrefix() {
            return this.instance.hasObjcClassPrefix();
         }

         @Override
         public String getObjcClassPrefix() {
            return this.instance.getObjcClassPrefix();
         }

         @Override
         public ByteString getObjcClassPrefixBytes() {
            return this.instance.getObjcClassPrefixBytes();
         }

         public DescriptorProtos.FileOptions.Builder setObjcClassPrefix(String value) {
            this.copyOnWrite();
            this.instance.setObjcClassPrefix(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearObjcClassPrefix() {
            this.copyOnWrite();
            this.instance.clearObjcClassPrefix();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setObjcClassPrefixBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setObjcClassPrefixBytes(value);
            return this;
         }

         @Override
         public boolean hasCsharpNamespace() {
            return this.instance.hasCsharpNamespace();
         }

         @Override
         public String getCsharpNamespace() {
            return this.instance.getCsharpNamespace();
         }

         @Override
         public ByteString getCsharpNamespaceBytes() {
            return this.instance.getCsharpNamespaceBytes();
         }

         public DescriptorProtos.FileOptions.Builder setCsharpNamespace(String value) {
            this.copyOnWrite();
            this.instance.setCsharpNamespace(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearCsharpNamespace() {
            this.copyOnWrite();
            this.instance.clearCsharpNamespace();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setCsharpNamespaceBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setCsharpNamespaceBytes(value);
            return this;
         }

         @Override
         public boolean hasSwiftPrefix() {
            return this.instance.hasSwiftPrefix();
         }

         @Override
         public String getSwiftPrefix() {
            return this.instance.getSwiftPrefix();
         }

         @Override
         public ByteString getSwiftPrefixBytes() {
            return this.instance.getSwiftPrefixBytes();
         }

         public DescriptorProtos.FileOptions.Builder setSwiftPrefix(String value) {
            this.copyOnWrite();
            this.instance.setSwiftPrefix(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearSwiftPrefix() {
            this.copyOnWrite();
            this.instance.clearSwiftPrefix();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setSwiftPrefixBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setSwiftPrefixBytes(value);
            return this;
         }

         @Override
         public boolean hasPhpClassPrefix() {
            return this.instance.hasPhpClassPrefix();
         }

         @Override
         public String getPhpClassPrefix() {
            return this.instance.getPhpClassPrefix();
         }

         @Override
         public ByteString getPhpClassPrefixBytes() {
            return this.instance.getPhpClassPrefixBytes();
         }

         public DescriptorProtos.FileOptions.Builder setPhpClassPrefix(String value) {
            this.copyOnWrite();
            this.instance.setPhpClassPrefix(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearPhpClassPrefix() {
            this.copyOnWrite();
            this.instance.clearPhpClassPrefix();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setPhpClassPrefixBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setPhpClassPrefixBytes(value);
            return this;
         }

         @Override
         public boolean hasPhpNamespace() {
            return this.instance.hasPhpNamespace();
         }

         @Override
         public String getPhpNamespace() {
            return this.instance.getPhpNamespace();
         }

         @Override
         public ByteString getPhpNamespaceBytes() {
            return this.instance.getPhpNamespaceBytes();
         }

         public DescriptorProtos.FileOptions.Builder setPhpNamespace(String value) {
            this.copyOnWrite();
            this.instance.setPhpNamespace(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearPhpNamespace() {
            this.copyOnWrite();
            this.instance.clearPhpNamespace();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setPhpNamespaceBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setPhpNamespaceBytes(value);
            return this;
         }

         @Override
         public boolean hasPhpMetadataNamespace() {
            return this.instance.hasPhpMetadataNamespace();
         }

         @Override
         public String getPhpMetadataNamespace() {
            return this.instance.getPhpMetadataNamespace();
         }

         @Override
         public ByteString getPhpMetadataNamespaceBytes() {
            return this.instance.getPhpMetadataNamespaceBytes();
         }

         public DescriptorProtos.FileOptions.Builder setPhpMetadataNamespace(String value) {
            this.copyOnWrite();
            this.instance.setPhpMetadataNamespace(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearPhpMetadataNamespace() {
            this.copyOnWrite();
            this.instance.clearPhpMetadataNamespace();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setPhpMetadataNamespaceBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setPhpMetadataNamespaceBytes(value);
            return this;
         }

         @Override
         public boolean hasRubyPackage() {
            return this.instance.hasRubyPackage();
         }

         @Override
         public String getRubyPackage() {
            return this.instance.getRubyPackage();
         }

         @Override
         public ByteString getRubyPackageBytes() {
            return this.instance.getRubyPackageBytes();
         }

         public DescriptorProtos.FileOptions.Builder setRubyPackage(String value) {
            this.copyOnWrite();
            this.instance.setRubyPackage(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearRubyPackage() {
            this.copyOnWrite();
            this.instance.clearRubyPackage();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setRubyPackageBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setRubyPackageBytes(value);
            return this;
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.FileOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.FileOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.FileOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.FileOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.FileOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }
      }

      public static enum OptimizeMode implements Internal.EnumLite {
         SPEED(1),
         CODE_SIZE(2),
         LITE_RUNTIME(3);

         public static final int SPEED_VALUE = 1;
         public static final int CODE_SIZE_VALUE = 2;
         public static final int LITE_RUNTIME_VALUE = 3;
         private static final Internal.EnumLiteMap<DescriptorProtos.FileOptions.OptimizeMode> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.FileOptions.OptimizeMode>() {
            public DescriptorProtos.FileOptions.OptimizeMode findValueByNumber(int number) {
               return DescriptorProtos.FileOptions.OptimizeMode.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.FileOptions.OptimizeMode valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.FileOptions.OptimizeMode forNumber(int value) {
            switch (value) {
               case 1:
                  return SPEED;
               case 2:
                  return CODE_SIZE;
               case 3:
                  return LITE_RUNTIME;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.FileOptions.OptimizeMode> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.FileOptions.OptimizeMode.OptimizeModeVerifier.INSTANCE;
         }

         private OptimizeMode(int value) {
            this.value = value;
         }

         private static final class OptimizeModeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.FileOptions.OptimizeMode.OptimizeModeVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.FileOptions.OptimizeMode.forNumber(number) != null;
            }
         }
      }
   }

   public interface FileOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.FileOptions, DescriptorProtos.FileOptions.Builder> {
      boolean hasJavaPackage();

      String getJavaPackage();

      ByteString getJavaPackageBytes();

      boolean hasJavaOuterClassname();

      String getJavaOuterClassname();

      ByteString getJavaOuterClassnameBytes();

      boolean hasJavaMultipleFiles();

      boolean getJavaMultipleFiles();

      @Deprecated
      boolean hasJavaGenerateEqualsAndHash();

      @Deprecated
      boolean getJavaGenerateEqualsAndHash();

      boolean hasJavaStringCheckUtf8();

      boolean getJavaStringCheckUtf8();

      boolean hasOptimizeFor();

      DescriptorProtos.FileOptions.OptimizeMode getOptimizeFor();

      boolean hasGoPackage();

      String getGoPackage();

      ByteString getGoPackageBytes();

      boolean hasCcGenericServices();

      boolean getCcGenericServices();

      boolean hasJavaGenericServices();

      boolean getJavaGenericServices();

      boolean hasPyGenericServices();

      boolean getPyGenericServices();

      boolean hasDeprecated();

      boolean getDeprecated();

      boolean hasCcEnableArenas();

      boolean getCcEnableArenas();

      boolean hasObjcClassPrefix();

      String getObjcClassPrefix();

      ByteString getObjcClassPrefixBytes();

      boolean hasCsharpNamespace();

      String getCsharpNamespace();

      ByteString getCsharpNamespaceBytes();

      boolean hasSwiftPrefix();

      String getSwiftPrefix();

      ByteString getSwiftPrefixBytes();

      boolean hasPhpClassPrefix();

      String getPhpClassPrefix();

      ByteString getPhpClassPrefixBytes();

      boolean hasPhpNamespace();

      String getPhpNamespace();

      ByteString getPhpNamespaceBytes();

      boolean hasPhpMetadataNamespace();

      String getPhpMetadataNamespace();

      ByteString getPhpMetadataNamespaceBytes();

      boolean hasRubyPackage();

      String getRubyPackage();

      ByteString getRubyPackageBytes();

      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();
   }

   public static final class GeneratedCodeInfo
      extends GeneratedMessageLite<DescriptorProtos.GeneratedCodeInfo, DescriptorProtos.GeneratedCodeInfo.Builder>
      implements DescriptorProtos.GeneratedCodeInfoOrBuilder {
      public static final int ANNOTATION_FIELD_NUMBER = 1;
      private Internal.ProtobufList<DescriptorProtos.GeneratedCodeInfo.Annotation> annotation_ = emptyProtobufList();
      private static final DescriptorProtos.GeneratedCodeInfo DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.GeneratedCodeInfo> PARSER;

      private GeneratedCodeInfo() {
      }

      @Override
      public List<DescriptorProtos.GeneratedCodeInfo.Annotation> getAnnotationList() {
         return this.annotation_;
      }

      public List<? extends DescriptorProtos.GeneratedCodeInfo.AnnotationOrBuilder> getAnnotationOrBuilderList() {
         return this.annotation_;
      }

      @Override
      public int getAnnotationCount() {
         return this.annotation_.size();
      }

      @Override
      public DescriptorProtos.GeneratedCodeInfo.Annotation getAnnotation(int index) {
         return this.annotation_.get(index);
      }

      public DescriptorProtos.GeneratedCodeInfo.AnnotationOrBuilder getAnnotationOrBuilder(int index) {
         return this.annotation_.get(index);
      }

      private void ensureAnnotationIsMutable() {
         Internal.ProtobufList<DescriptorProtos.GeneratedCodeInfo.Annotation> tmp = this.annotation_;
         if (!tmp.isModifiable()) {
            this.annotation_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setAnnotation(int index, DescriptorProtos.GeneratedCodeInfo.Annotation value) {
         value.getClass();
         this.ensureAnnotationIsMutable();
         this.annotation_.set(index, value);
      }

      private void addAnnotation(DescriptorProtos.GeneratedCodeInfo.Annotation value) {
         value.getClass();
         this.ensureAnnotationIsMutable();
         this.annotation_.add(value);
      }

      private void addAnnotation(int index, DescriptorProtos.GeneratedCodeInfo.Annotation value) {
         value.getClass();
         this.ensureAnnotationIsMutable();
         this.annotation_.add(index, value);
      }

      private void addAllAnnotation(Iterable<? extends DescriptorProtos.GeneratedCodeInfo.Annotation> values) {
         this.ensureAnnotationIsMutable();
         AbstractMessageLite.addAll(values, this.annotation_);
      }

      private void clearAnnotation() {
         this.annotation_ = emptyProtobufList();
      }

      private void removeAnnotation(int index) {
         this.ensureAnnotationIsMutable();
         this.annotation_.remove(index);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.GeneratedCodeInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.GeneratedCodeInfo.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.GeneratedCodeInfo.Builder newBuilder(DescriptorProtos.GeneratedCodeInfo prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$GeneratedCodeInfo.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$GeneratedCodeInfo;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$GeneratedCodeInfo.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$GeneratedCodeInfo$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.GeneratedCodeInfo();
            case NEW_BUILDER:
               return new DescriptorProtos.GeneratedCodeInfo.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"annotation_", DescriptorProtos.GeneratedCodeInfo.Annotation.class};
               String info = "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.GeneratedCodeInfo> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.GeneratedCodeInfo.class) {
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

      public static DescriptorProtos.GeneratedCodeInfo getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.GeneratedCodeInfo> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.GeneratedCodeInfo defaultInstance = new DescriptorProtos.GeneratedCodeInfo();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.GeneratedCodeInfo.class, defaultInstance);
      }

      public static final class Annotation
         extends GeneratedMessageLite<DescriptorProtos.GeneratedCodeInfo.Annotation, DescriptorProtos.GeneratedCodeInfo.Annotation.Builder>
         implements DescriptorProtos.GeneratedCodeInfo.AnnotationOrBuilder {
         private int bitField0_;
         public static final int PATH_FIELD_NUMBER = 1;
         private Internal.IntList path_;
         private int pathMemoizedSerializedSize = -1;
         public static final int SOURCE_FILE_FIELD_NUMBER = 2;
         private String sourceFile_;
         public static final int BEGIN_FIELD_NUMBER = 3;
         private int begin_;
         public static final int END_FIELD_NUMBER = 4;
         private int end_;
         public static final int SEMANTIC_FIELD_NUMBER = 5;
         private int semantic_;
         private static final DescriptorProtos.GeneratedCodeInfo.Annotation DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.GeneratedCodeInfo.Annotation> PARSER;

         private Annotation() {
            this.path_ = emptyIntList();
            this.sourceFile_ = "";
         }

         @Override
         public List<Integer> getPathList() {
            return this.path_;
         }

         @Override
         public int getPathCount() {
            return this.path_.size();
         }

         @Override
         public int getPath(int index) {
            return this.path_.getInt(index);
         }

         private void ensurePathIsMutable() {
            Internal.IntList tmp = this.path_;
            if (!tmp.isModifiable()) {
               this.path_ = GeneratedMessageLite.mutableCopy(tmp);
            }
         }

         private void setPath(int index, int value) {
            this.ensurePathIsMutable();
            this.path_.setInt(index, value);
         }

         private void addPath(int value) {
            this.ensurePathIsMutable();
            this.path_.addInt(value);
         }

         private void addAllPath(Iterable<? extends Integer> values) {
            this.ensurePathIsMutable();
            AbstractMessageLite.addAll(values, this.path_);
         }

         private void clearPath() {
            this.path_ = emptyIntList();
         }

         @Override
         public boolean hasSourceFile() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getSourceFile() {
            return this.sourceFile_;
         }

         @Override
         public ByteString getSourceFileBytes() {
            return ByteString.copyFromUtf8(this.sourceFile_);
         }

         private void setSourceFile(String value) {
            Class<?> valueClass = value.getClass();
            this.bitField0_ |= 1;
            this.sourceFile_ = value;
         }

         private void clearSourceFile() {
            this.bitField0_ &= -2;
            this.sourceFile_ = getDefaultInstance().getSourceFile();
         }

         private void setSourceFileBytes(ByteString value) {
            this.sourceFile_ = value.toStringUtf8();
            this.bitField0_ |= 1;
         }

         @Override
         public boolean hasBegin() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public int getBegin() {
            return this.begin_;
         }

         private void setBegin(int value) {
            this.bitField0_ |= 2;
            this.begin_ = value;
         }

         private void clearBegin() {
            this.bitField0_ &= -3;
            this.begin_ = 0;
         }

         @Override
         public boolean hasEnd() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public int getEnd() {
            return this.end_;
         }

         private void setEnd(int value) {
            this.bitField0_ |= 4;
            this.end_ = value;
         }

         private void clearEnd() {
            this.bitField0_ &= -5;
            this.end_ = 0;
         }

         @Override
         public boolean hasSemantic() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic getSemantic() {
            DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic result = DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic.forNumber(this.semantic_);
            return result == null ? DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic.NONE : result;
         }

         private void setSemantic(DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic value) {
            this.semantic_ = value.getNumber();
            this.bitField0_ |= 8;
         }

         private void clearSemantic() {
            this.bitField0_ &= -9;
            this.semantic_ = 0;
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.GeneratedCodeInfo.Annotation.Builder newBuilder(DescriptorProtos.GeneratedCodeInfo.Annotation prototype) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$GeneratedCodeInfo$Annotation.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$GeneratedCodeInfo$Annotation;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$GeneratedCodeInfo$Annotation.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$GeneratedCodeInfo$Annotation$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.GeneratedCodeInfo.Annotation();
               case NEW_BUILDER:
                  return new DescriptorProtos.GeneratedCodeInfo.Annotation.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{
                     "bitField0_",
                     "path_",
                     "sourceFile_",
                     "begin_",
                     "end_",
                     "semantic_",
                     DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic.internalGetVerifier()
                  };
                  String info = "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0001\u0000\u0001'\u0002ဈ\u0000\u0003င\u0001\u0004င\u0002\u0005᠌\u0003";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.GeneratedCodeInfo.Annotation> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.GeneratedCodeInfo.Annotation.class) {
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

         public static DescriptorProtos.GeneratedCodeInfo.Annotation getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.GeneratedCodeInfo.Annotation> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.GeneratedCodeInfo.Annotation defaultInstance = new DescriptorProtos.GeneratedCodeInfo.Annotation();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.GeneratedCodeInfo.Annotation.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.GeneratedCodeInfo.Annotation, DescriptorProtos.GeneratedCodeInfo.Annotation.Builder>
            implements DescriptorProtos.GeneratedCodeInfo.AnnotationOrBuilder {
            private Builder() {
               super(DescriptorProtos.GeneratedCodeInfo.Annotation.DEFAULT_INSTANCE);
            }

            @Override
            public List<Integer> getPathList() {
               return Collections.unmodifiableList(this.instance.getPathList());
            }

            @Override
            public int getPathCount() {
               return this.instance.getPathCount();
            }

            @Override
            public int getPath(int index) {
               return this.instance.getPath(index);
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder setPath(int index, int value) {
               this.copyOnWrite();
               this.instance.setPath(index, value);
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder addPath(int value) {
               this.copyOnWrite();
               this.instance.addPath(value);
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder addAllPath(Iterable<? extends Integer> values) {
               this.copyOnWrite();
               this.instance.addAllPath(values);
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder clearPath() {
               this.copyOnWrite();
               this.instance.clearPath();
               return this;
            }

            @Override
            public boolean hasSourceFile() {
               return this.instance.hasSourceFile();
            }

            @Override
            public String getSourceFile() {
               return this.instance.getSourceFile();
            }

            @Override
            public ByteString getSourceFileBytes() {
               return this.instance.getSourceFileBytes();
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder setSourceFile(String value) {
               this.copyOnWrite();
               this.instance.setSourceFile(value);
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder clearSourceFile() {
               this.copyOnWrite();
               this.instance.clearSourceFile();
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder setSourceFileBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.setSourceFileBytes(value);
               return this;
            }

            @Override
            public boolean hasBegin() {
               return this.instance.hasBegin();
            }

            @Override
            public int getBegin() {
               return this.instance.getBegin();
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder setBegin(int value) {
               this.copyOnWrite();
               this.instance.setBegin(value);
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder clearBegin() {
               this.copyOnWrite();
               this.instance.clearBegin();
               return this;
            }

            @Override
            public boolean hasEnd() {
               return this.instance.hasEnd();
            }

            @Override
            public int getEnd() {
               return this.instance.getEnd();
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder setEnd(int value) {
               this.copyOnWrite();
               this.instance.setEnd(value);
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder clearEnd() {
               this.copyOnWrite();
               this.instance.clearEnd();
               return this;
            }

            @Override
            public boolean hasSemantic() {
               return this.instance.hasSemantic();
            }

            @Override
            public DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic getSemantic() {
               return this.instance.getSemantic();
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder setSemantic(DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic value) {
               this.copyOnWrite();
               this.instance.setSemantic(value);
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Annotation.Builder clearSemantic() {
               this.copyOnWrite();
               this.instance.clearSemantic();
               return this;
            }
         }

         public static enum Semantic implements Internal.EnumLite {
            NONE(0),
            SET(1),
            ALIAS(2);

            public static final int NONE_VALUE = 0;
            public static final int SET_VALUE = 1;
            public static final int ALIAS_VALUE = 2;
            private static final Internal.EnumLiteMap<DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic>() {
               public DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic findValueByNumber(int number) {
                  return DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic.forNumber(number);
               }
            };
            private final int value;

            @Override
            public final int getNumber() {
               return this.value;
            }

            @Deprecated
            public static DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic valueOf(int value) {
               return forNumber(value);
            }

            public static DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic forNumber(int value) {
               switch (value) {
                  case 0:
                     return NONE;
                  case 1:
                     return SET;
                  case 2:
                     return ALIAS;
                  default:
                     return null;
               }
            }

            public static Internal.EnumLiteMap<DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic> internalGetValueMap() {
               return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
               return DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic.SemanticVerifier.INSTANCE;
            }

            private Semantic(int value) {
               this.value = value;
            }

            private static final class SemanticVerifier implements Internal.EnumVerifier {
               static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic.SemanticVerifier();

               @Override
               public boolean isInRange(int number) {
                  return DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic.forNumber(number) != null;
               }
            }
         }
      }

      public interface AnnotationOrBuilder extends MessageLiteOrBuilder {
         List<Integer> getPathList();

         int getPathCount();

         int getPath(int index);

         boolean hasSourceFile();

         String getSourceFile();

         ByteString getSourceFileBytes();

         boolean hasBegin();

         int getBegin();

         boolean hasEnd();

         int getEnd();

         boolean hasSemantic();

         DescriptorProtos.GeneratedCodeInfo.Annotation.Semantic getSemantic();
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.GeneratedCodeInfo, DescriptorProtos.GeneratedCodeInfo.Builder>
         implements DescriptorProtos.GeneratedCodeInfoOrBuilder {
         private Builder() {
            super(DescriptorProtos.GeneratedCodeInfo.DEFAULT_INSTANCE);
         }

         @Override
         public List<DescriptorProtos.GeneratedCodeInfo.Annotation> getAnnotationList() {
            return Collections.unmodifiableList(this.instance.getAnnotationList());
         }

         @Override
         public int getAnnotationCount() {
            return this.instance.getAnnotationCount();
         }

         @Override
         public DescriptorProtos.GeneratedCodeInfo.Annotation getAnnotation(int index) {
            return this.instance.getAnnotation(index);
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder setAnnotation(int index, DescriptorProtos.GeneratedCodeInfo.Annotation value) {
            this.copyOnWrite();
            this.instance.setAnnotation(index, value);
            return this;
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder setAnnotation(int index, DescriptorProtos.GeneratedCodeInfo.Annotation.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setAnnotation(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder addAnnotation(DescriptorProtos.GeneratedCodeInfo.Annotation value) {
            this.copyOnWrite();
            this.instance.addAnnotation(value);
            return this;
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder addAnnotation(int index, DescriptorProtos.GeneratedCodeInfo.Annotation value) {
            this.copyOnWrite();
            this.instance.addAnnotation(index, value);
            return this;
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder addAnnotation(DescriptorProtos.GeneratedCodeInfo.Annotation.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addAnnotation(builderForValue.build());
            return this;
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder addAnnotation(int index, DescriptorProtos.GeneratedCodeInfo.Annotation.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addAnnotation(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder addAllAnnotation(Iterable<? extends DescriptorProtos.GeneratedCodeInfo.Annotation> values) {
            this.copyOnWrite();
            this.instance.addAllAnnotation(values);
            return this;
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder clearAnnotation() {
            this.copyOnWrite();
            this.instance.clearAnnotation();
            return this;
         }

         public DescriptorProtos.GeneratedCodeInfo.Builder removeAnnotation(int index) {
            this.copyOnWrite();
            this.instance.removeAnnotation(index);
            return this;
         }
      }
   }

   public interface GeneratedCodeInfoOrBuilder extends MessageLiteOrBuilder {
      List<DescriptorProtos.GeneratedCodeInfo.Annotation> getAnnotationList();

      DescriptorProtos.GeneratedCodeInfo.Annotation getAnnotation(int index);

      int getAnnotationCount();
   }

   public static final class MessageOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.MessageOptions, DescriptorProtos.MessageOptions.Builder>
      implements DescriptorProtos.MessageOptionsOrBuilder {
      private int bitField0_;
      public static final int MESSAGE_SET_WIRE_FORMAT_FIELD_NUMBER = 1;
      private boolean messageSetWireFormat_;
      public static final int NO_STANDARD_DESCRIPTOR_ACCESSOR_FIELD_NUMBER = 2;
      private boolean noStandardDescriptorAccessor_;
      public static final int DEPRECATED_FIELD_NUMBER = 3;
      private boolean deprecated_;
      public static final int MAP_ENTRY_FIELD_NUMBER = 7;
      private boolean mapEntry_;
      public static final int DEPRECATED_LEGACY_JSON_FIELD_CONFLICTS_FIELD_NUMBER = 11;
      private boolean deprecatedLegacyJsonFieldConflicts_;
      public static final int FEATURES_FIELD_NUMBER = 12;
      private DescriptorProtos.FeatureSet features_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.MessageOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.MessageOptions> PARSER;

      private MessageOptions() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      @Override
      public boolean hasMessageSetWireFormat() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public boolean getMessageSetWireFormat() {
         return this.messageSetWireFormat_;
      }

      private void setMessageSetWireFormat(boolean value) {
         this.bitField0_ |= 1;
         this.messageSetWireFormat_ = value;
      }

      private void clearMessageSetWireFormat() {
         this.bitField0_ &= -2;
         this.messageSetWireFormat_ = false;
      }

      @Override
      public boolean hasNoStandardDescriptorAccessor() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public boolean getNoStandardDescriptorAccessor() {
         return this.noStandardDescriptorAccessor_;
      }

      private void setNoStandardDescriptorAccessor(boolean value) {
         this.bitField0_ |= 2;
         this.noStandardDescriptorAccessor_ = value;
      }

      private void clearNoStandardDescriptorAccessor() {
         this.bitField0_ &= -3;
         this.noStandardDescriptorAccessor_ = false;
      }

      @Override
      public boolean hasDeprecated() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public boolean getDeprecated() {
         return this.deprecated_;
      }

      private void setDeprecated(boolean value) {
         this.bitField0_ |= 4;
         this.deprecated_ = value;
      }

      private void clearDeprecated() {
         this.bitField0_ &= -5;
         this.deprecated_ = false;
      }

      @Override
      public boolean hasMapEntry() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public boolean getMapEntry() {
         return this.mapEntry_;
      }

      private void setMapEntry(boolean value) {
         this.bitField0_ |= 8;
         this.mapEntry_ = value;
      }

      private void clearMapEntry() {
         this.bitField0_ &= -9;
         this.mapEntry_ = false;
      }

      @Deprecated
      @Override
      public boolean hasDeprecatedLegacyJsonFieldConflicts() {
         return (this.bitField0_ & 16) != 0;
      }

      @Deprecated
      @Override
      public boolean getDeprecatedLegacyJsonFieldConflicts() {
         return this.deprecatedLegacyJsonFieldConflicts_;
      }

      /** @deprecated */
      private void setDeprecatedLegacyJsonFieldConflicts(boolean value) {
         this.bitField0_ |= 16;
         this.deprecatedLegacyJsonFieldConflicts_ = value;
      }

      /** @deprecated */
      private void clearDeprecatedLegacyJsonFieldConflicts() {
         this.bitField0_ &= -17;
         this.deprecatedLegacyJsonFieldConflicts_ = false;
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 32;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 32;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -33;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      public static DescriptorProtos.MessageOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MessageOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MessageOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MessageOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MessageOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MessageOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MessageOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MessageOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MessageOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MessageOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MessageOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MessageOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MessageOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.MessageOptions.Builder newBuilder(DescriptorProtos.MessageOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$MessageOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$MessageOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$MessageOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$MessageOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.MessageOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.MessageOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "messageSetWireFormat_",
                  "noStandardDescriptorAccessor_",
                  "deprecated_",
                  "mapEntry_",
                  "deprecatedLegacyJsonFieldConflicts_",
                  "features_",
                  "uninterpretedOption_",
                  DescriptorProtos.UninterpretedOption.class
               };
               String info = "\u0001\u0007\u0000\u0001\u0001ϧ\u0007\u0000\u0001\u0002\u0001ဇ\u0000\u0002ဇ\u0001\u0003ဇ\u0002\u0007ဇ\u0003\u000bဇ\u0004\fᐉ\u0005ϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.MessageOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.MessageOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.MessageOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.MessageOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.MessageOptions defaultInstance = new DescriptorProtos.MessageOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.MessageOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.MessageOptions, DescriptorProtos.MessageOptions.Builder>
         implements DescriptorProtos.MessageOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.MessageOptions.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasMessageSetWireFormat() {
            return this.instance.hasMessageSetWireFormat();
         }

         @Override
         public boolean getMessageSetWireFormat() {
            return this.instance.getMessageSetWireFormat();
         }

         public DescriptorProtos.MessageOptions.Builder setMessageSetWireFormat(boolean value) {
            this.copyOnWrite();
            this.instance.setMessageSetWireFormat(value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder clearMessageSetWireFormat() {
            this.copyOnWrite();
            this.instance.clearMessageSetWireFormat();
            return this;
         }

         @Override
         public boolean hasNoStandardDescriptorAccessor() {
            return this.instance.hasNoStandardDescriptorAccessor();
         }

         @Override
         public boolean getNoStandardDescriptorAccessor() {
            return this.instance.getNoStandardDescriptorAccessor();
         }

         public DescriptorProtos.MessageOptions.Builder setNoStandardDescriptorAccessor(boolean value) {
            this.copyOnWrite();
            this.instance.setNoStandardDescriptorAccessor(value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder clearNoStandardDescriptorAccessor() {
            this.copyOnWrite();
            this.instance.clearNoStandardDescriptorAccessor();
            return this;
         }

         @Override
         public boolean hasDeprecated() {
            return this.instance.hasDeprecated();
         }

         @Override
         public boolean getDeprecated() {
            return this.instance.getDeprecated();
         }

         public DescriptorProtos.MessageOptions.Builder setDeprecated(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecated(value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder clearDeprecated() {
            this.copyOnWrite();
            this.instance.clearDeprecated();
            return this;
         }

         @Override
         public boolean hasMapEntry() {
            return this.instance.hasMapEntry();
         }

         @Override
         public boolean getMapEntry() {
            return this.instance.getMapEntry();
         }

         public DescriptorProtos.MessageOptions.Builder setMapEntry(boolean value) {
            this.copyOnWrite();
            this.instance.setMapEntry(value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder clearMapEntry() {
            this.copyOnWrite();
            this.instance.clearMapEntry();
            return this;
         }

         @Deprecated
         @Override
         public boolean hasDeprecatedLegacyJsonFieldConflicts() {
            return this.instance.hasDeprecatedLegacyJsonFieldConflicts();
         }

         @Deprecated
         @Override
         public boolean getDeprecatedLegacyJsonFieldConflicts() {
            return this.instance.getDeprecatedLegacyJsonFieldConflicts();
         }

         @Deprecated
         public DescriptorProtos.MessageOptions.Builder setDeprecatedLegacyJsonFieldConflicts(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecatedLegacyJsonFieldConflicts(value);
            return this;
         }

         @Deprecated
         public DescriptorProtos.MessageOptions.Builder clearDeprecatedLegacyJsonFieldConflicts() {
            this.copyOnWrite();
            this.instance.clearDeprecatedLegacyJsonFieldConflicts();
            return this;
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.MessageOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.MessageOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.MessageOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }
      }
   }

   public interface MessageOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.MessageOptions, DescriptorProtos.MessageOptions.Builder> {
      boolean hasMessageSetWireFormat();

      boolean getMessageSetWireFormat();

      boolean hasNoStandardDescriptorAccessor();

      boolean getNoStandardDescriptorAccessor();

      boolean hasDeprecated();

      boolean getDeprecated();

      boolean hasMapEntry();

      boolean getMapEntry();

      @Deprecated
      boolean hasDeprecatedLegacyJsonFieldConflicts();

      @Deprecated
      boolean getDeprecatedLegacyJsonFieldConflicts();

      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();
   }

   public static final class MethodDescriptorProto
      extends GeneratedMessageLite<DescriptorProtos.MethodDescriptorProto, DescriptorProtos.MethodDescriptorProto.Builder>
      implements DescriptorProtos.MethodDescriptorProtoOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_;
      public static final int INPUT_TYPE_FIELD_NUMBER = 2;
      private String inputType_;
      public static final int OUTPUT_TYPE_FIELD_NUMBER = 3;
      private String outputType_;
      public static final int OPTIONS_FIELD_NUMBER = 4;
      private DescriptorProtos.MethodOptions options_;
      public static final int CLIENT_STREAMING_FIELD_NUMBER = 5;
      private boolean clientStreaming_;
      public static final int SERVER_STREAMING_FIELD_NUMBER = 6;
      private boolean serverStreaming_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.MethodDescriptorProto DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.MethodDescriptorProto> PARSER;

      private MethodDescriptorProto() {
         this.name_ = "";
         this.inputType_ = "";
         this.outputType_ = "";
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         return this.name_;
      }

      @Override
      public ByteString getNameBytes() {
         return ByteString.copyFromUtf8(this.name_);
      }

      private void setName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.name_ = value;
      }

      private void clearName() {
         this.bitField0_ &= -2;
         this.name_ = getDefaultInstance().getName();
      }

      private void setNameBytes(ByteString value) {
         this.name_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public boolean hasInputType() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getInputType() {
         return this.inputType_;
      }

      @Override
      public ByteString getInputTypeBytes() {
         return ByteString.copyFromUtf8(this.inputType_);
      }

      private void setInputType(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 2;
         this.inputType_ = value;
      }

      private void clearInputType() {
         this.bitField0_ &= -3;
         this.inputType_ = getDefaultInstance().getInputType();
      }

      private void setInputTypeBytes(ByteString value) {
         this.inputType_ = value.toStringUtf8();
         this.bitField0_ |= 2;
      }

      @Override
      public boolean hasOutputType() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public String getOutputType() {
         return this.outputType_;
      }

      @Override
      public ByteString getOutputTypeBytes() {
         return ByteString.copyFromUtf8(this.outputType_);
      }

      private void setOutputType(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 4;
         this.outputType_ = value;
      }

      private void clearOutputType() {
         this.bitField0_ &= -5;
         this.outputType_ = getDefaultInstance().getOutputType();
      }

      private void setOutputTypeBytes(ByteString value) {
         this.outputType_ = value.toStringUtf8();
         this.bitField0_ |= 4;
      }

      @Override
      public boolean hasOptions() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public DescriptorProtos.MethodOptions getOptions() {
         return this.options_ == null ? DescriptorProtos.MethodOptions.getDefaultInstance() : this.options_;
      }

      private void setOptions(DescriptorProtos.MethodOptions value) {
         value.getClass();
         this.options_ = value;
         this.bitField0_ |= 8;
      }

      private void mergeOptions(DescriptorProtos.MethodOptions value) {
         value.getClass();
         if (this.options_ != null && this.options_ != DescriptorProtos.MethodOptions.getDefaultInstance()) {
            this.options_ = DescriptorProtos.MethodOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
         } else {
            this.options_ = value;
         }

         this.bitField0_ |= 8;
      }

      private void clearOptions() {
         this.options_ = null;
         this.bitField0_ &= -9;
      }

      @Override
      public boolean hasClientStreaming() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public boolean getClientStreaming() {
         return this.clientStreaming_;
      }

      private void setClientStreaming(boolean value) {
         this.bitField0_ |= 16;
         this.clientStreaming_ = value;
      }

      private void clearClientStreaming() {
         this.bitField0_ &= -17;
         this.clientStreaming_ = false;
      }

      @Override
      public boolean hasServerStreaming() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public boolean getServerStreaming() {
         return this.serverStreaming_;
      }

      private void setServerStreaming(boolean value) {
         this.bitField0_ |= 32;
         this.serverStreaming_ = value;
      }

      private void clearServerStreaming() {
         this.bitField0_ &= -33;
         this.serverStreaming_ = false;
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MethodDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MethodDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MethodDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MethodDescriptorProto.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.MethodDescriptorProto.Builder newBuilder(DescriptorProtos.MethodDescriptorProto prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1734)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$MethodDescriptorProto.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$MethodDescriptorProto;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$MethodDescriptorProto.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$MethodDescriptorProto$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.MethodDescriptorProto();
            case NEW_BUILDER:
               return new DescriptorProtos.MethodDescriptorProto.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"bitField0_", "name_", "inputType_", "outputType_", "options_", "clientStreaming_", "serverStreaming_"};
               String info = "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0001\u0001ဈ\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0004ᐉ\u0003\u0005ဇ\u0004\u0006ဇ\u0005";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.MethodDescriptorProto> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.MethodDescriptorProto.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.MethodDescriptorProto getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.MethodDescriptorProto> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.MethodDescriptorProto defaultInstance = new DescriptorProtos.MethodDescriptorProto();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.MethodDescriptorProto.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.MethodDescriptorProto, DescriptorProtos.MethodDescriptorProto.Builder>
         implements DescriptorProtos.MethodDescriptorProtoOrBuilder {
         private Builder() {
            super(DescriptorProtos.MethodDescriptorProto.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasName() {
            return this.instance.hasName();
         }

         @Override
         public String getName() {
            return this.instance.getName();
         }

         @Override
         public ByteString getNameBytes() {
            return this.instance.getNameBytes();
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setName(String value) {
            this.copyOnWrite();
            this.instance.setName(value);
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setNameBytes(value);
            return this;
         }

         @Override
         public boolean hasInputType() {
            return this.instance.hasInputType();
         }

         @Override
         public String getInputType() {
            return this.instance.getInputType();
         }

         @Override
         public ByteString getInputTypeBytes() {
            return this.instance.getInputTypeBytes();
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setInputType(String value) {
            this.copyOnWrite();
            this.instance.setInputType(value);
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder clearInputType() {
            this.copyOnWrite();
            this.instance.clearInputType();
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setInputTypeBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setInputTypeBytes(value);
            return this;
         }

         @Override
         public boolean hasOutputType() {
            return this.instance.hasOutputType();
         }

         @Override
         public String getOutputType() {
            return this.instance.getOutputType();
         }

         @Override
         public ByteString getOutputTypeBytes() {
            return this.instance.getOutputTypeBytes();
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setOutputType(String value) {
            this.copyOnWrite();
            this.instance.setOutputType(value);
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder clearOutputType() {
            this.copyOnWrite();
            this.instance.clearOutputType();
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setOutputTypeBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setOutputTypeBytes(value);
            return this;
         }

         @Override
         public boolean hasOptions() {
            return this.instance.hasOptions();
         }

         @Override
         public DescriptorProtos.MethodOptions getOptions() {
            return this.instance.getOptions();
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setOptions(DescriptorProtos.MethodOptions value) {
            this.copyOnWrite();
            this.instance.setOptions(value);
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setOptions(DescriptorProtos.MethodOptions.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOptions(builderForValue.build());
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder mergeOptions(DescriptorProtos.MethodOptions value) {
            this.copyOnWrite();
            this.instance.mergeOptions(value);
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder clearOptions() {
            this.copyOnWrite();
            this.instance.clearOptions();
            return this;
         }

         @Override
         public boolean hasClientStreaming() {
            return this.instance.hasClientStreaming();
         }

         @Override
         public boolean getClientStreaming() {
            return this.instance.getClientStreaming();
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setClientStreaming(boolean value) {
            this.copyOnWrite();
            this.instance.setClientStreaming(value);
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder clearClientStreaming() {
            this.copyOnWrite();
            this.instance.clearClientStreaming();
            return this;
         }

         @Override
         public boolean hasServerStreaming() {
            return this.instance.hasServerStreaming();
         }

         @Override
         public boolean getServerStreaming() {
            return this.instance.getServerStreaming();
         }

         public DescriptorProtos.MethodDescriptorProto.Builder setServerStreaming(boolean value) {
            this.copyOnWrite();
            this.instance.setServerStreaming(value);
            return this;
         }

         public DescriptorProtos.MethodDescriptorProto.Builder clearServerStreaming() {
            this.copyOnWrite();
            this.instance.clearServerStreaming();
            return this;
         }
      }
   }

   public interface MethodDescriptorProtoOrBuilder extends MessageLiteOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasInputType();

      String getInputType();

      ByteString getInputTypeBytes();

      boolean hasOutputType();

      String getOutputType();

      ByteString getOutputTypeBytes();

      boolean hasOptions();

      DescriptorProtos.MethodOptions getOptions();

      boolean hasClientStreaming();

      boolean getClientStreaming();

      boolean hasServerStreaming();

      boolean getServerStreaming();
   }

   public static final class MethodOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.MethodOptions, DescriptorProtos.MethodOptions.Builder>
      implements DescriptorProtos.MethodOptionsOrBuilder {
      private int bitField0_;
      public static final int DEPRECATED_FIELD_NUMBER = 33;
      private boolean deprecated_;
      public static final int IDEMPOTENCY_LEVEL_FIELD_NUMBER = 34;
      private int idempotencyLevel_;
      public static final int FEATURES_FIELD_NUMBER = 35;
      private DescriptorProtos.FeatureSet features_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.MethodOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.MethodOptions> PARSER;

      private MethodOptions() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      @Override
      public boolean hasDeprecated() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public boolean getDeprecated() {
         return this.deprecated_;
      }

      private void setDeprecated(boolean value) {
         this.bitField0_ |= 1;
         this.deprecated_ = value;
      }

      private void clearDeprecated() {
         this.bitField0_ &= -2;
         this.deprecated_ = false;
      }

      @Override
      public boolean hasIdempotencyLevel() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.MethodOptions.IdempotencyLevel getIdempotencyLevel() {
         DescriptorProtos.MethodOptions.IdempotencyLevel result = DescriptorProtos.MethodOptions.IdempotencyLevel.forNumber(this.idempotencyLevel_);
         return result == null ? DescriptorProtos.MethodOptions.IdempotencyLevel.IDEMPOTENCY_UNKNOWN : result;
      }

      private void setIdempotencyLevel(DescriptorProtos.MethodOptions.IdempotencyLevel value) {
         this.idempotencyLevel_ = value.getNumber();
         this.bitField0_ |= 2;
      }

      private void clearIdempotencyLevel() {
         this.bitField0_ &= -3;
         this.idempotencyLevel_ = 0;
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 4;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 4;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -5;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      public static DescriptorProtos.MethodOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MethodOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MethodOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MethodOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MethodOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.MethodOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.MethodOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MethodOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MethodOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MethodOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MethodOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.MethodOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.MethodOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.MethodOptions.Builder newBuilder(DescriptorProtos.MethodOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$MethodOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$MethodOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$MethodOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$MethodOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.MethodOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.MethodOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "deprecated_",
                  "idempotencyLevel_",
                  DescriptorProtos.MethodOptions.IdempotencyLevel.internalGetVerifier(),
                  "features_",
                  "uninterpretedOption_",
                  DescriptorProtos.UninterpretedOption.class
               };
               String info = "\u0001\u0004\u0000\u0001!ϧ\u0004\u0000\u0001\u0002!ဇ\u0000\"᠌\u0001#ᐉ\u0002ϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.MethodOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.MethodOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.MethodOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.MethodOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.MethodOptions defaultInstance = new DescriptorProtos.MethodOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.MethodOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.MethodOptions, DescriptorProtos.MethodOptions.Builder>
         implements DescriptorProtos.MethodOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.MethodOptions.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasDeprecated() {
            return this.instance.hasDeprecated();
         }

         @Override
         public boolean getDeprecated() {
            return this.instance.getDeprecated();
         }

         public DescriptorProtos.MethodOptions.Builder setDeprecated(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecated(value);
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder clearDeprecated() {
            this.copyOnWrite();
            this.instance.clearDeprecated();
            return this;
         }

         @Override
         public boolean hasIdempotencyLevel() {
            return this.instance.hasIdempotencyLevel();
         }

         @Override
         public DescriptorProtos.MethodOptions.IdempotencyLevel getIdempotencyLevel() {
            return this.instance.getIdempotencyLevel();
         }

         public DescriptorProtos.MethodOptions.Builder setIdempotencyLevel(DescriptorProtos.MethodOptions.IdempotencyLevel value) {
            this.copyOnWrite();
            this.instance.setIdempotencyLevel(value);
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder clearIdempotencyLevel() {
            this.copyOnWrite();
            this.instance.clearIdempotencyLevel();
            return this;
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.MethodOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.MethodOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.MethodOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }
      }

      public static enum IdempotencyLevel implements Internal.EnumLite {
         IDEMPOTENCY_UNKNOWN(0),
         NO_SIDE_EFFECTS(1),
         IDEMPOTENT(2);

         public static final int IDEMPOTENCY_UNKNOWN_VALUE = 0;
         public static final int NO_SIDE_EFFECTS_VALUE = 1;
         public static final int IDEMPOTENT_VALUE = 2;
         private static final Internal.EnumLiteMap<DescriptorProtos.MethodOptions.IdempotencyLevel> internalValueMap = new Internal.EnumLiteMap<DescriptorProtos.MethodOptions.IdempotencyLevel>() {
            public DescriptorProtos.MethodOptions.IdempotencyLevel findValueByNumber(int number) {
               return DescriptorProtos.MethodOptions.IdempotencyLevel.forNumber(number);
            }
         };
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static DescriptorProtos.MethodOptions.IdempotencyLevel valueOf(int value) {
            return forNumber(value);
         }

         public static DescriptorProtos.MethodOptions.IdempotencyLevel forNumber(int value) {
            switch (value) {
               case 0:
                  return IDEMPOTENCY_UNKNOWN;
               case 1:
                  return NO_SIDE_EFFECTS;
               case 2:
                  return IDEMPOTENT;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<DescriptorProtos.MethodOptions.IdempotencyLevel> internalGetValueMap() {
            return internalValueMap;
         }

         public static Internal.EnumVerifier internalGetVerifier() {
            return DescriptorProtos.MethodOptions.IdempotencyLevel.IdempotencyLevelVerifier.INSTANCE;
         }

         private IdempotencyLevel(int value) {
            this.value = value;
         }

         private static final class IdempotencyLevelVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new DescriptorProtos.MethodOptions.IdempotencyLevel.IdempotencyLevelVerifier();

            @Override
            public boolean isInRange(int number) {
               return DescriptorProtos.MethodOptions.IdempotencyLevel.forNumber(number) != null;
            }
         }
      }
   }

   public interface MethodOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.MethodOptions, DescriptorProtos.MethodOptions.Builder> {
      boolean hasDeprecated();

      boolean getDeprecated();

      boolean hasIdempotencyLevel();

      DescriptorProtos.MethodOptions.IdempotencyLevel getIdempotencyLevel();

      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();
   }

   public static final class OneofDescriptorProto
      extends GeneratedMessageLite<DescriptorProtos.OneofDescriptorProto, DescriptorProtos.OneofDescriptorProto.Builder>
      implements DescriptorProtos.OneofDescriptorProtoOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_;
      public static final int OPTIONS_FIELD_NUMBER = 2;
      private DescriptorProtos.OneofOptions options_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.OneofDescriptorProto DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.OneofDescriptorProto> PARSER;

      private OneofDescriptorProto() {
         this.name_ = "";
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         return this.name_;
      }

      @Override
      public ByteString getNameBytes() {
         return ByteString.copyFromUtf8(this.name_);
      }

      private void setName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.name_ = value;
      }

      private void clearName() {
         this.bitField0_ &= -2;
         this.name_ = getDefaultInstance().getName();
      }

      private void setNameBytes(ByteString value) {
         this.name_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public boolean hasOptions() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.OneofOptions getOptions() {
         return this.options_ == null ? DescriptorProtos.OneofOptions.getDefaultInstance() : this.options_;
      }

      private void setOptions(DescriptorProtos.OneofOptions value) {
         value.getClass();
         this.options_ = value;
         this.bitField0_ |= 2;
      }

      private void mergeOptions(DescriptorProtos.OneofOptions value) {
         value.getClass();
         if (this.options_ != null && this.options_ != DescriptorProtos.OneofOptions.getDefaultInstance()) {
            this.options_ = DescriptorProtos.OneofOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
         } else {
            this.options_ = value;
         }

         this.bitField0_ |= 2;
      }

      private void clearOptions() {
         this.options_ = null;
         this.bitField0_ &= -3;
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.OneofDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.OneofDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.OneofDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.OneofDescriptorProto.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.OneofDescriptorProto.Builder newBuilder(DescriptorProtos.OneofDescriptorProto prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$OneofDescriptorProto.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$OneofDescriptorProto;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$OneofDescriptorProto.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$OneofDescriptorProto$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.OneofDescriptorProto();
            case NEW_BUILDER:
               return new DescriptorProtos.OneofDescriptorProto.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"bitField0_", "name_", "options_"};
               String info = "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0001\u0001ဈ\u0000\u0002ᐉ\u0001";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.OneofDescriptorProto> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.OneofDescriptorProto.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.OneofDescriptorProto getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.OneofDescriptorProto> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.OneofDescriptorProto defaultInstance = new DescriptorProtos.OneofDescriptorProto();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.OneofDescriptorProto.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.OneofDescriptorProto, DescriptorProtos.OneofDescriptorProto.Builder>
         implements DescriptorProtos.OneofDescriptorProtoOrBuilder {
         private Builder() {
            super(DescriptorProtos.OneofDescriptorProto.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasName() {
            return this.instance.hasName();
         }

         @Override
         public String getName() {
            return this.instance.getName();
         }

         @Override
         public ByteString getNameBytes() {
            return this.instance.getNameBytes();
         }

         public DescriptorProtos.OneofDescriptorProto.Builder setName(String value) {
            this.copyOnWrite();
            this.instance.setName(value);
            return this;
         }

         public DescriptorProtos.OneofDescriptorProto.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.OneofDescriptorProto.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setNameBytes(value);
            return this;
         }

         @Override
         public boolean hasOptions() {
            return this.instance.hasOptions();
         }

         @Override
         public DescriptorProtos.OneofOptions getOptions() {
            return this.instance.getOptions();
         }

         public DescriptorProtos.OneofDescriptorProto.Builder setOptions(DescriptorProtos.OneofOptions value) {
            this.copyOnWrite();
            this.instance.setOptions(value);
            return this;
         }

         public DescriptorProtos.OneofDescriptorProto.Builder setOptions(DescriptorProtos.OneofOptions.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOptions(builderForValue.build());
            return this;
         }

         public DescriptorProtos.OneofDescriptorProto.Builder mergeOptions(DescriptorProtos.OneofOptions value) {
            this.copyOnWrite();
            this.instance.mergeOptions(value);
            return this;
         }

         public DescriptorProtos.OneofDescriptorProto.Builder clearOptions() {
            this.copyOnWrite();
            this.instance.clearOptions();
            return this;
         }
      }
   }

   public interface OneofDescriptorProtoOrBuilder extends MessageLiteOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasOptions();

      DescriptorProtos.OneofOptions getOptions();
   }

   public static final class OneofOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.OneofOptions, DescriptorProtos.OneofOptions.Builder>
      implements DescriptorProtos.OneofOptionsOrBuilder {
      private int bitField0_;
      public static final int FEATURES_FIELD_NUMBER = 1;
      private DescriptorProtos.FeatureSet features_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.OneofOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.OneofOptions> PARSER;

      private OneofOptions() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 1;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 1;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -2;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      public static DescriptorProtos.OneofOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.OneofOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.OneofOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.OneofOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.OneofOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.OneofOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.OneofOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.OneofOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.OneofOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.OneofOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.OneofOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.OneofOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.OneofOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.OneofOptions.Builder newBuilder(DescriptorProtos.OneofOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$OneofOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$OneofOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$OneofOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$OneofOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.OneofOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.OneofOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"bitField0_", "features_", "uninterpretedOption_", DescriptorProtos.UninterpretedOption.class};
               String info = "\u0001\u0002\u0000\u0001\u0001ϧ\u0002\u0000\u0001\u0002\u0001ᐉ\u0000ϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.OneofOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.OneofOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.OneofOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.OneofOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.OneofOptions defaultInstance = new DescriptorProtos.OneofOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.OneofOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.OneofOptions, DescriptorProtos.OneofOptions.Builder>
         implements DescriptorProtos.OneofOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.OneofOptions.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.OneofOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.OneofOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.OneofOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }
      }
   }

   public interface OneofOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.OneofOptions, DescriptorProtos.OneofOptions.Builder> {
      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();
   }

   public static final class ServiceDescriptorProto
      extends GeneratedMessageLite<DescriptorProtos.ServiceDescriptorProto, DescriptorProtos.ServiceDescriptorProto.Builder>
      implements DescriptorProtos.ServiceDescriptorProtoOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private String name_;
      public static final int METHOD_FIELD_NUMBER = 2;
      private Internal.ProtobufList<DescriptorProtos.MethodDescriptorProto> method_;
      public static final int OPTIONS_FIELD_NUMBER = 3;
      private DescriptorProtos.ServiceOptions options_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.ServiceDescriptorProto DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.ServiceDescriptorProto> PARSER;

      private ServiceDescriptorProto() {
         this.name_ = "";
         this.method_ = emptyProtobufList();
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         return this.name_;
      }

      @Override
      public ByteString getNameBytes() {
         return ByteString.copyFromUtf8(this.name_);
      }

      private void setName(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.name_ = value;
      }

      private void clearName() {
         this.bitField0_ &= -2;
         this.name_ = getDefaultInstance().getName();
      }

      private void setNameBytes(ByteString value) {
         this.name_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public List<DescriptorProtos.MethodDescriptorProto> getMethodList() {
         return this.method_;
      }

      public List<? extends DescriptorProtos.MethodDescriptorProtoOrBuilder> getMethodOrBuilderList() {
         return this.method_;
      }

      @Override
      public int getMethodCount() {
         return this.method_.size();
      }

      @Override
      public DescriptorProtos.MethodDescriptorProto getMethod(int index) {
         return this.method_.get(index);
      }

      public DescriptorProtos.MethodDescriptorProtoOrBuilder getMethodOrBuilder(int index) {
         return this.method_.get(index);
      }

      private void ensureMethodIsMutable() {
         Internal.ProtobufList<DescriptorProtos.MethodDescriptorProto> tmp = this.method_;
         if (!tmp.isModifiable()) {
            this.method_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setMethod(int index, DescriptorProtos.MethodDescriptorProto value) {
         value.getClass();
         this.ensureMethodIsMutable();
         this.method_.set(index, value);
      }

      private void addMethod(DescriptorProtos.MethodDescriptorProto value) {
         value.getClass();
         this.ensureMethodIsMutable();
         this.method_.add(value);
      }

      private void addMethod(int index, DescriptorProtos.MethodDescriptorProto value) {
         value.getClass();
         this.ensureMethodIsMutable();
         this.method_.add(index, value);
      }

      private void addAllMethod(Iterable<? extends DescriptorProtos.MethodDescriptorProto> values) {
         this.ensureMethodIsMutable();
         AbstractMessageLite.addAll(values, this.method_);
      }

      private void clearMethod() {
         this.method_ = emptyProtobufList();
      }

      private void removeMethod(int index) {
         this.ensureMethodIsMutable();
         this.method_.remove(index);
      }

      @Override
      public boolean hasOptions() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public DescriptorProtos.ServiceOptions getOptions() {
         return this.options_ == null ? DescriptorProtos.ServiceOptions.getDefaultInstance() : this.options_;
      }

      private void setOptions(DescriptorProtos.ServiceOptions value) {
         value.getClass();
         this.options_ = value;
         this.bitField0_ |= 2;
      }

      private void mergeOptions(DescriptorProtos.ServiceOptions value) {
         value.getClass();
         if (this.options_ != null && this.options_ != DescriptorProtos.ServiceOptions.getDefaultInstance()) {
            this.options_ = DescriptorProtos.ServiceOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
         } else {
            this.options_ = value;
         }

         this.bitField0_ |= 2;
      }

      private void clearOptions() {
         this.options_ = null;
         this.bitField0_ &= -3;
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ServiceDescriptorProto parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ServiceDescriptorProto.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.ServiceDescriptorProto.Builder newBuilder(DescriptorProtos.ServiceDescriptorProto prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$ServiceDescriptorProto.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$ServiceDescriptorProto;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$ServiceDescriptorProto.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$ServiceDescriptorProto$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.ServiceDescriptorProto();
            case NEW_BUILDER:
               return new DescriptorProtos.ServiceDescriptorProto.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"bitField0_", "name_", "method_", DescriptorProtos.MethodDescriptorProto.class, "options_"};
               String info = "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0001\u0002\u0001ဈ\u0000\u0002Л\u0003ᐉ\u0001";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.ServiceDescriptorProto> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.ServiceDescriptorProto.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.ServiceDescriptorProto getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.ServiceDescriptorProto> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.ServiceDescriptorProto defaultInstance = new DescriptorProtos.ServiceDescriptorProto();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.ServiceDescriptorProto.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.ServiceDescriptorProto, DescriptorProtos.ServiceDescriptorProto.Builder>
         implements DescriptorProtos.ServiceDescriptorProtoOrBuilder {
         private Builder() {
            super(DescriptorProtos.ServiceDescriptorProto.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasName() {
            return this.instance.hasName();
         }

         @Override
         public String getName() {
            return this.instance.getName();
         }

         @Override
         public ByteString getNameBytes() {
            return this.instance.getNameBytes();
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder setName(String value) {
            this.copyOnWrite();
            this.instance.setName(value);
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder setNameBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setNameBytes(value);
            return this;
         }

         @Override
         public List<DescriptorProtos.MethodDescriptorProto> getMethodList() {
            return Collections.unmodifiableList(this.instance.getMethodList());
         }

         @Override
         public int getMethodCount() {
            return this.instance.getMethodCount();
         }

         @Override
         public DescriptorProtos.MethodDescriptorProto getMethod(int index) {
            return this.instance.getMethod(index);
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder setMethod(int index, DescriptorProtos.MethodDescriptorProto value) {
            this.copyOnWrite();
            this.instance.setMethod(index, value);
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder setMethod(int index, DescriptorProtos.MethodDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setMethod(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder addMethod(DescriptorProtos.MethodDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addMethod(value);
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder addMethod(int index, DescriptorProtos.MethodDescriptorProto value) {
            this.copyOnWrite();
            this.instance.addMethod(index, value);
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder addMethod(DescriptorProtos.MethodDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addMethod(builderForValue.build());
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder addMethod(int index, DescriptorProtos.MethodDescriptorProto.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addMethod(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder addAllMethod(Iterable<? extends DescriptorProtos.MethodDescriptorProto> values) {
            this.copyOnWrite();
            this.instance.addAllMethod(values);
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder clearMethod() {
            this.copyOnWrite();
            this.instance.clearMethod();
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder removeMethod(int index) {
            this.copyOnWrite();
            this.instance.removeMethod(index);
            return this;
         }

         @Override
         public boolean hasOptions() {
            return this.instance.hasOptions();
         }

         @Override
         public DescriptorProtos.ServiceOptions getOptions() {
            return this.instance.getOptions();
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder setOptions(DescriptorProtos.ServiceOptions value) {
            this.copyOnWrite();
            this.instance.setOptions(value);
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder setOptions(DescriptorProtos.ServiceOptions.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setOptions(builderForValue.build());
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder mergeOptions(DescriptorProtos.ServiceOptions value) {
            this.copyOnWrite();
            this.instance.mergeOptions(value);
            return this;
         }

         public DescriptorProtos.ServiceDescriptorProto.Builder clearOptions() {
            this.copyOnWrite();
            this.instance.clearOptions();
            return this;
         }
      }
   }

   public interface ServiceDescriptorProtoOrBuilder extends MessageLiteOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      List<DescriptorProtos.MethodDescriptorProto> getMethodList();

      DescriptorProtos.MethodDescriptorProto getMethod(int index);

      int getMethodCount();

      boolean hasOptions();

      DescriptorProtos.ServiceOptions getOptions();
   }

   public static final class ServiceOptions
      extends GeneratedMessageLite.ExtendableMessage<DescriptorProtos.ServiceOptions, DescriptorProtos.ServiceOptions.Builder>
      implements DescriptorProtos.ServiceOptionsOrBuilder {
      private int bitField0_;
      public static final int FEATURES_FIELD_NUMBER = 34;
      private DescriptorProtos.FeatureSet features_;
      public static final int DEPRECATED_FIELD_NUMBER = 33;
      private boolean deprecated_;
      public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption> uninterpretedOption_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.ServiceOptions DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.ServiceOptions> PARSER;

      private ServiceOptions() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      @Override
      public boolean hasFeatures() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public DescriptorProtos.FeatureSet getFeatures() {
         return this.features_ == null ? DescriptorProtos.FeatureSet.getDefaultInstance() : this.features_;
      }

      private void setFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         this.features_ = value;
         this.bitField0_ |= 1;
      }

      private void mergeFeatures(DescriptorProtos.FeatureSet value) {
         value.getClass();
         if (this.features_ != null && this.features_ != DescriptorProtos.FeatureSet.getDefaultInstance()) {
            this.features_ = DescriptorProtos.FeatureSet.newBuilder(this.features_).mergeFrom(value).buildPartial();
         } else {
            this.features_ = value;
         }

         this.bitField0_ |= 1;
      }

      private void clearFeatures() {
         this.features_ = null;
         this.bitField0_ &= -2;
      }

      @Override
      public boolean hasDeprecated() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public boolean getDeprecated() {
         return this.deprecated_;
      }

      private void setDeprecated(boolean value) {
         this.bitField0_ |= 2;
         this.deprecated_ = value;
      }

      private void clearDeprecated() {
         this.bitField0_ &= -3;
         this.deprecated_ = false;
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
         return this.uninterpretedOption_;
      }

      public List<? extends DescriptorProtos.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
         return this.uninterpretedOption_;
      }

      @Override
      public int getUninterpretedOptionCount() {
         return this.uninterpretedOption_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
         return this.uninterpretedOption_.get(index);
      }

      public DescriptorProtos.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(int index) {
         return this.uninterpretedOption_.get(index);
      }

      private void ensureUninterpretedOptionIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption> tmp = this.uninterpretedOption_;
         if (!tmp.isModifiable()) {
            this.uninterpretedOption_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.set(index, value);
      }

      private void addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(value);
      }

      private void addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
         value.getClass();
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.add(index, value);
      }

      private void addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
         this.ensureUninterpretedOptionIsMutable();
         AbstractMessageLite.addAll(values, this.uninterpretedOption_);
      }

      private void clearUninterpretedOption() {
         this.uninterpretedOption_ = emptyProtobufList();
      }

      private void removeUninterpretedOption(int index) {
         this.ensureUninterpretedOptionIsMutable();
         this.uninterpretedOption_.remove(index);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ServiceOptions parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ServiceOptions parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.ServiceOptions parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.ServiceOptions.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.ServiceOptions.Builder newBuilder(DescriptorProtos.ServiceOptions prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$ServiceOptions.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$ServiceOptions;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$ServiceOptions.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$ServiceOptions$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.ServiceOptions();
            case NEW_BUILDER:
               return new DescriptorProtos.ServiceOptions.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"bitField0_", "deprecated_", "features_", "uninterpretedOption_", DescriptorProtos.UninterpretedOption.class};
               String info = "\u0001\u0003\u0000\u0001!ϧ\u0003\u0000\u0001\u0002!ဇ\u0001\"ᐉ\u0000ϧЛ";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.ServiceOptions> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.ServiceOptions.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.ServiceOptions getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.ServiceOptions> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.ServiceOptions defaultInstance = new DescriptorProtos.ServiceOptions();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.ServiceOptions.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.ExtendableBuilder<DescriptorProtos.ServiceOptions, DescriptorProtos.ServiceOptions.Builder>
         implements DescriptorProtos.ServiceOptionsOrBuilder {
         private Builder() {
            super(DescriptorProtos.ServiceOptions.DEFAULT_INSTANCE);
         }

         @Override
         public boolean hasFeatures() {
            return this.instance.hasFeatures();
         }

         @Override
         public DescriptorProtos.FeatureSet getFeatures() {
            return this.instance.getFeatures();
         }

         public DescriptorProtos.ServiceOptions.Builder setFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.setFeatures(value);
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder setFeatures(DescriptorProtos.FeatureSet.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setFeatures(builderForValue.build());
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder mergeFeatures(DescriptorProtos.FeatureSet value) {
            this.copyOnWrite();
            this.instance.mergeFeatures(value);
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder clearFeatures() {
            this.copyOnWrite();
            this.instance.clearFeatures();
            return this;
         }

         @Override
         public boolean hasDeprecated() {
            return this.instance.hasDeprecated();
         }

         @Override
         public boolean getDeprecated() {
            return this.instance.getDeprecated();
         }

         public DescriptorProtos.ServiceOptions.Builder setDeprecated(boolean value) {
            this.copyOnWrite();
            this.instance.setDeprecated(value);
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder clearDeprecated() {
            this.copyOnWrite();
            this.instance.clearDeprecated();
            return this;
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList() {
            return Collections.unmodifiableList(this.instance.getUninterpretedOptionList());
         }

         @Override
         public int getUninterpretedOptionCount() {
            return this.instance.getUninterpretedOptionCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption getUninterpretedOption(int index) {
            return this.instance.getUninterpretedOption(index);
         }

         public DescriptorProtos.ServiceOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder setUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(value);
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption value) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, value);
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder addUninterpretedOption(DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(builderForValue.build());
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder addUninterpretedOption(int index, DescriptorProtos.UninterpretedOption.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addUninterpretedOption(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder addAllUninterpretedOption(Iterable<? extends DescriptorProtos.UninterpretedOption> values) {
            this.copyOnWrite();
            this.instance.addAllUninterpretedOption(values);
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder clearUninterpretedOption() {
            this.copyOnWrite();
            this.instance.clearUninterpretedOption();
            return this;
         }

         public DescriptorProtos.ServiceOptions.Builder removeUninterpretedOption(int index) {
            this.copyOnWrite();
            this.instance.removeUninterpretedOption(index);
            return this;
         }
      }
   }

   public interface ServiceOptionsOrBuilder
      extends GeneratedMessageLite.ExtendableMessageOrBuilder<DescriptorProtos.ServiceOptions, DescriptorProtos.ServiceOptions.Builder> {
      boolean hasFeatures();

      DescriptorProtos.FeatureSet getFeatures();

      boolean hasDeprecated();

      boolean getDeprecated();

      List<DescriptorProtos.UninterpretedOption> getUninterpretedOptionList();

      DescriptorProtos.UninterpretedOption getUninterpretedOption(int index);

      int getUninterpretedOptionCount();
   }

   public static final class SourceCodeInfo
      extends GeneratedMessageLite<DescriptorProtos.SourceCodeInfo, DescriptorProtos.SourceCodeInfo.Builder>
      implements DescriptorProtos.SourceCodeInfoOrBuilder {
      public static final int LOCATION_FIELD_NUMBER = 1;
      private Internal.ProtobufList<DescriptorProtos.SourceCodeInfo.Location> location_ = emptyProtobufList();
      private static final DescriptorProtos.SourceCodeInfo DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.SourceCodeInfo> PARSER;

      private SourceCodeInfo() {
      }

      @Override
      public List<DescriptorProtos.SourceCodeInfo.Location> getLocationList() {
         return this.location_;
      }

      public List<? extends DescriptorProtos.SourceCodeInfo.LocationOrBuilder> getLocationOrBuilderList() {
         return this.location_;
      }

      @Override
      public int getLocationCount() {
         return this.location_.size();
      }

      @Override
      public DescriptorProtos.SourceCodeInfo.Location getLocation(int index) {
         return this.location_.get(index);
      }

      public DescriptorProtos.SourceCodeInfo.LocationOrBuilder getLocationOrBuilder(int index) {
         return this.location_.get(index);
      }

      private void ensureLocationIsMutable() {
         Internal.ProtobufList<DescriptorProtos.SourceCodeInfo.Location> tmp = this.location_;
         if (!tmp.isModifiable()) {
            this.location_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setLocation(int index, DescriptorProtos.SourceCodeInfo.Location value) {
         value.getClass();
         this.ensureLocationIsMutable();
         this.location_.set(index, value);
      }

      private void addLocation(DescriptorProtos.SourceCodeInfo.Location value) {
         value.getClass();
         this.ensureLocationIsMutable();
         this.location_.add(value);
      }

      private void addLocation(int index, DescriptorProtos.SourceCodeInfo.Location value) {
         value.getClass();
         this.ensureLocationIsMutable();
         this.location_.add(index, value);
      }

      private void addAllLocation(Iterable<? extends DescriptorProtos.SourceCodeInfo.Location> values) {
         this.ensureLocationIsMutable();
         AbstractMessageLite.addAll(values, this.location_);
      }

      private void clearLocation() {
         this.location_ = emptyProtobufList();
      }

      private void removeLocation(int index) {
         this.ensureLocationIsMutable();
         this.location_.remove(index);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.SourceCodeInfo parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.SourceCodeInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.SourceCodeInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.SourceCodeInfo.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.SourceCodeInfo.Builder newBuilder(DescriptorProtos.SourceCodeInfo prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1734)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$SourceCodeInfo.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$SourceCodeInfo;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$SourceCodeInfo.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$SourceCodeInfo$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.SourceCodeInfo();
            case NEW_BUILDER:
               return new DescriptorProtos.SourceCodeInfo.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{"location_", DescriptorProtos.SourceCodeInfo.Location.class};
               String info = "\u0001\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.SourceCodeInfo> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.SourceCodeInfo.class) {
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

      public static DescriptorProtos.SourceCodeInfo getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.SourceCodeInfo> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.SourceCodeInfo defaultInstance = new DescriptorProtos.SourceCodeInfo();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.SourceCodeInfo.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.SourceCodeInfo, DescriptorProtos.SourceCodeInfo.Builder>
         implements DescriptorProtos.SourceCodeInfoOrBuilder {
         private Builder() {
            super(DescriptorProtos.SourceCodeInfo.DEFAULT_INSTANCE);
         }

         @Override
         public List<DescriptorProtos.SourceCodeInfo.Location> getLocationList() {
            return Collections.unmodifiableList(this.instance.getLocationList());
         }

         @Override
         public int getLocationCount() {
            return this.instance.getLocationCount();
         }

         @Override
         public DescriptorProtos.SourceCodeInfo.Location getLocation(int index) {
            return this.instance.getLocation(index);
         }

         public DescriptorProtos.SourceCodeInfo.Builder setLocation(int index, DescriptorProtos.SourceCodeInfo.Location value) {
            this.copyOnWrite();
            this.instance.setLocation(index, value);
            return this;
         }

         public DescriptorProtos.SourceCodeInfo.Builder setLocation(int index, DescriptorProtos.SourceCodeInfo.Location.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setLocation(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.SourceCodeInfo.Builder addLocation(DescriptorProtos.SourceCodeInfo.Location value) {
            this.copyOnWrite();
            this.instance.addLocation(value);
            return this;
         }

         public DescriptorProtos.SourceCodeInfo.Builder addLocation(int index, DescriptorProtos.SourceCodeInfo.Location value) {
            this.copyOnWrite();
            this.instance.addLocation(index, value);
            return this;
         }

         public DescriptorProtos.SourceCodeInfo.Builder addLocation(DescriptorProtos.SourceCodeInfo.Location.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addLocation(builderForValue.build());
            return this;
         }

         public DescriptorProtos.SourceCodeInfo.Builder addLocation(int index, DescriptorProtos.SourceCodeInfo.Location.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addLocation(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.SourceCodeInfo.Builder addAllLocation(Iterable<? extends DescriptorProtos.SourceCodeInfo.Location> values) {
            this.copyOnWrite();
            this.instance.addAllLocation(values);
            return this;
         }

         public DescriptorProtos.SourceCodeInfo.Builder clearLocation() {
            this.copyOnWrite();
            this.instance.clearLocation();
            return this;
         }

         public DescriptorProtos.SourceCodeInfo.Builder removeLocation(int index) {
            this.copyOnWrite();
            this.instance.removeLocation(index);
            return this;
         }
      }

      public static final class Location
         extends GeneratedMessageLite<DescriptorProtos.SourceCodeInfo.Location, DescriptorProtos.SourceCodeInfo.Location.Builder>
         implements DescriptorProtos.SourceCodeInfo.LocationOrBuilder {
         private int bitField0_;
         public static final int PATH_FIELD_NUMBER = 1;
         private Internal.IntList path_;
         private int pathMemoizedSerializedSize = -1;
         public static final int SPAN_FIELD_NUMBER = 2;
         private Internal.IntList span_;
         private int spanMemoizedSerializedSize = -1;
         public static final int LEADING_COMMENTS_FIELD_NUMBER = 3;
         private String leadingComments_;
         public static final int TRAILING_COMMENTS_FIELD_NUMBER = 4;
         private String trailingComments_;
         public static final int LEADING_DETACHED_COMMENTS_FIELD_NUMBER = 6;
         private Internal.ProtobufList<String> leadingDetachedComments_;
         private static final DescriptorProtos.SourceCodeInfo.Location DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.SourceCodeInfo.Location> PARSER;

         private Location() {
            this.path_ = emptyIntList();
            this.span_ = emptyIntList();
            this.leadingComments_ = "";
            this.trailingComments_ = "";
            this.leadingDetachedComments_ = GeneratedMessageLite.emptyProtobufList();
         }

         @Override
         public List<Integer> getPathList() {
            return this.path_;
         }

         @Override
         public int getPathCount() {
            return this.path_.size();
         }

         @Override
         public int getPath(int index) {
            return this.path_.getInt(index);
         }

         private void ensurePathIsMutable() {
            Internal.IntList tmp = this.path_;
            if (!tmp.isModifiable()) {
               this.path_ = GeneratedMessageLite.mutableCopy(tmp);
            }
         }

         private void setPath(int index, int value) {
            this.ensurePathIsMutable();
            this.path_.setInt(index, value);
         }

         private void addPath(int value) {
            this.ensurePathIsMutable();
            this.path_.addInt(value);
         }

         private void addAllPath(Iterable<? extends Integer> values) {
            this.ensurePathIsMutable();
            AbstractMessageLite.addAll(values, this.path_);
         }

         private void clearPath() {
            this.path_ = emptyIntList();
         }

         @Override
         public List<Integer> getSpanList() {
            return this.span_;
         }

         @Override
         public int getSpanCount() {
            return this.span_.size();
         }

         @Override
         public int getSpan(int index) {
            return this.span_.getInt(index);
         }

         private void ensureSpanIsMutable() {
            Internal.IntList tmp = this.span_;
            if (!tmp.isModifiable()) {
               this.span_ = GeneratedMessageLite.mutableCopy(tmp);
            }
         }

         private void setSpan(int index, int value) {
            this.ensureSpanIsMutable();
            this.span_.setInt(index, value);
         }

         private void addSpan(int value) {
            this.ensureSpanIsMutable();
            this.span_.addInt(value);
         }

         private void addAllSpan(Iterable<? extends Integer> values) {
            this.ensureSpanIsMutable();
            AbstractMessageLite.addAll(values, this.span_);
         }

         private void clearSpan() {
            this.span_ = emptyIntList();
         }

         @Override
         public boolean hasLeadingComments() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getLeadingComments() {
            return this.leadingComments_;
         }

         @Override
         public ByteString getLeadingCommentsBytes() {
            return ByteString.copyFromUtf8(this.leadingComments_);
         }

         private void setLeadingComments(String value) {
            Class<?> valueClass = value.getClass();
            this.bitField0_ |= 1;
            this.leadingComments_ = value;
         }

         private void clearLeadingComments() {
            this.bitField0_ &= -2;
            this.leadingComments_ = getDefaultInstance().getLeadingComments();
         }

         private void setLeadingCommentsBytes(ByteString value) {
            this.leadingComments_ = value.toStringUtf8();
            this.bitField0_ |= 1;
         }

         @Override
         public boolean hasTrailingComments() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getTrailingComments() {
            return this.trailingComments_;
         }

         @Override
         public ByteString getTrailingCommentsBytes() {
            return ByteString.copyFromUtf8(this.trailingComments_);
         }

         private void setTrailingComments(String value) {
            Class<?> valueClass = value.getClass();
            this.bitField0_ |= 2;
            this.trailingComments_ = value;
         }

         private void clearTrailingComments() {
            this.bitField0_ &= -3;
            this.trailingComments_ = getDefaultInstance().getTrailingComments();
         }

         private void setTrailingCommentsBytes(ByteString value) {
            this.trailingComments_ = value.toStringUtf8();
            this.bitField0_ |= 2;
         }

         @Override
         public List<String> getLeadingDetachedCommentsList() {
            return this.leadingDetachedComments_;
         }

         @Override
         public int getLeadingDetachedCommentsCount() {
            return this.leadingDetachedComments_.size();
         }

         @Override
         public String getLeadingDetachedComments(int index) {
            return this.leadingDetachedComments_.get(index);
         }

         @Override
         public ByteString getLeadingDetachedCommentsBytes(int index) {
            return ByteString.copyFromUtf8(this.leadingDetachedComments_.get(index));
         }

         private void ensureLeadingDetachedCommentsIsMutable() {
            Internal.ProtobufList<String> tmp = this.leadingDetachedComments_;
            if (!tmp.isModifiable()) {
               this.leadingDetachedComments_ = GeneratedMessageLite.mutableCopy(tmp);
            }
         }

         private void setLeadingDetachedComments(int index, String value) {
            Class<?> valueClass = value.getClass();
            this.ensureLeadingDetachedCommentsIsMutable();
            this.leadingDetachedComments_.set(index, value);
         }

         private void addLeadingDetachedComments(String value) {
            Class<?> valueClass = value.getClass();
            this.ensureLeadingDetachedCommentsIsMutable();
            this.leadingDetachedComments_.add(value);
         }

         private void addAllLeadingDetachedComments(Iterable<String> values) {
            this.ensureLeadingDetachedCommentsIsMutable();
            AbstractMessageLite.addAll(values, this.leadingDetachedComments_);
         }

         private void clearLeadingDetachedComments() {
            this.leadingDetachedComments_ = GeneratedMessageLite.emptyProtobufList();
         }

         private void addLeadingDetachedCommentsBytes(ByteString value) {
            this.ensureLeadingDetachedCommentsIsMutable();
            this.leadingDetachedComments_.add(value.toStringUtf8());
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.SourceCodeInfo.Location parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.SourceCodeInfo.Location.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.SourceCodeInfo.Location.Builder newBuilder(DescriptorProtos.SourceCodeInfo.Location prototype) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$SourceCodeInfo$Location.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$SourceCodeInfo$Location;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$SourceCodeInfo$Location.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$SourceCodeInfo$Location$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.SourceCodeInfo.Location();
               case NEW_BUILDER:
                  return new DescriptorProtos.SourceCodeInfo.Location.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{"bitField0_", "path_", "span_", "leadingComments_", "trailingComments_", "leadingDetachedComments_"};
                  String info = "\u0001\u0005\u0000\u0001\u0001\u0006\u0005\u0000\u0003\u0000\u0001'\u0002'\u0003ဈ\u0000\u0004ဈ\u0001\u0006\u001a";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.SourceCodeInfo.Location> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.SourceCodeInfo.Location.class) {
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

         public static DescriptorProtos.SourceCodeInfo.Location getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.SourceCodeInfo.Location> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.SourceCodeInfo.Location defaultInstance = new DescriptorProtos.SourceCodeInfo.Location();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.SourceCodeInfo.Location.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.SourceCodeInfo.Location, DescriptorProtos.SourceCodeInfo.Location.Builder>
            implements DescriptorProtos.SourceCodeInfo.LocationOrBuilder {
            private Builder() {
               super(DescriptorProtos.SourceCodeInfo.Location.DEFAULT_INSTANCE);
            }

            @Override
            public List<Integer> getPathList() {
               return Collections.unmodifiableList(this.instance.getPathList());
            }

            @Override
            public int getPathCount() {
               return this.instance.getPathCount();
            }

            @Override
            public int getPath(int index) {
               return this.instance.getPath(index);
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder setPath(int index, int value) {
               this.copyOnWrite();
               this.instance.setPath(index, value);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder addPath(int value) {
               this.copyOnWrite();
               this.instance.addPath(value);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder addAllPath(Iterable<? extends Integer> values) {
               this.copyOnWrite();
               this.instance.addAllPath(values);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder clearPath() {
               this.copyOnWrite();
               this.instance.clearPath();
               return this;
            }

            @Override
            public List<Integer> getSpanList() {
               return Collections.unmodifiableList(this.instance.getSpanList());
            }

            @Override
            public int getSpanCount() {
               return this.instance.getSpanCount();
            }

            @Override
            public int getSpan(int index) {
               return this.instance.getSpan(index);
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder setSpan(int index, int value) {
               this.copyOnWrite();
               this.instance.setSpan(index, value);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder addSpan(int value) {
               this.copyOnWrite();
               this.instance.addSpan(value);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder addAllSpan(Iterable<? extends Integer> values) {
               this.copyOnWrite();
               this.instance.addAllSpan(values);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder clearSpan() {
               this.copyOnWrite();
               this.instance.clearSpan();
               return this;
            }

            @Override
            public boolean hasLeadingComments() {
               return this.instance.hasLeadingComments();
            }

            @Override
            public String getLeadingComments() {
               return this.instance.getLeadingComments();
            }

            @Override
            public ByteString getLeadingCommentsBytes() {
               return this.instance.getLeadingCommentsBytes();
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder setLeadingComments(String value) {
               this.copyOnWrite();
               this.instance.setLeadingComments(value);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder clearLeadingComments() {
               this.copyOnWrite();
               this.instance.clearLeadingComments();
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder setLeadingCommentsBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.setLeadingCommentsBytes(value);
               return this;
            }

            @Override
            public boolean hasTrailingComments() {
               return this.instance.hasTrailingComments();
            }

            @Override
            public String getTrailingComments() {
               return this.instance.getTrailingComments();
            }

            @Override
            public ByteString getTrailingCommentsBytes() {
               return this.instance.getTrailingCommentsBytes();
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder setTrailingComments(String value) {
               this.copyOnWrite();
               this.instance.setTrailingComments(value);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder clearTrailingComments() {
               this.copyOnWrite();
               this.instance.clearTrailingComments();
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder setTrailingCommentsBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.setTrailingCommentsBytes(value);
               return this;
            }

            @Override
            public List<String> getLeadingDetachedCommentsList() {
               return Collections.unmodifiableList(this.instance.getLeadingDetachedCommentsList());
            }

            @Override
            public int getLeadingDetachedCommentsCount() {
               return this.instance.getLeadingDetachedCommentsCount();
            }

            @Override
            public String getLeadingDetachedComments(int index) {
               return this.instance.getLeadingDetachedComments(index);
            }

            @Override
            public ByteString getLeadingDetachedCommentsBytes(int index) {
               return this.instance.getLeadingDetachedCommentsBytes(index);
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder setLeadingDetachedComments(int index, String value) {
               this.copyOnWrite();
               this.instance.setLeadingDetachedComments(index, value);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder addLeadingDetachedComments(String value) {
               this.copyOnWrite();
               this.instance.addLeadingDetachedComments(value);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder addAllLeadingDetachedComments(Iterable<String> values) {
               this.copyOnWrite();
               this.instance.addAllLeadingDetachedComments(values);
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder clearLeadingDetachedComments() {
               this.copyOnWrite();
               this.instance.clearLeadingDetachedComments();
               return this;
            }

            public DescriptorProtos.SourceCodeInfo.Location.Builder addLeadingDetachedCommentsBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.addLeadingDetachedCommentsBytes(value);
               return this;
            }
         }
      }

      public interface LocationOrBuilder extends MessageLiteOrBuilder {
         List<Integer> getPathList();

         int getPathCount();

         int getPath(int index);

         List<Integer> getSpanList();

         int getSpanCount();

         int getSpan(int index);

         boolean hasLeadingComments();

         String getLeadingComments();

         ByteString getLeadingCommentsBytes();

         boolean hasTrailingComments();

         String getTrailingComments();

         ByteString getTrailingCommentsBytes();

         List<String> getLeadingDetachedCommentsList();

         int getLeadingDetachedCommentsCount();

         String getLeadingDetachedComments(int index);

         ByteString getLeadingDetachedCommentsBytes(int index);
      }
   }

   public interface SourceCodeInfoOrBuilder extends MessageLiteOrBuilder {
      List<DescriptorProtos.SourceCodeInfo.Location> getLocationList();

      DescriptorProtos.SourceCodeInfo.Location getLocation(int index);

      int getLocationCount();
   }

   public static final class UninterpretedOption
      extends GeneratedMessageLite<DescriptorProtos.UninterpretedOption, DescriptorProtos.UninterpretedOption.Builder>
      implements DescriptorProtos.UninterpretedOptionOrBuilder {
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 2;
      private Internal.ProtobufList<DescriptorProtos.UninterpretedOption.NamePart> name_;
      public static final int IDENTIFIER_VALUE_FIELD_NUMBER = 3;
      private String identifierValue_;
      public static final int POSITIVE_INT_VALUE_FIELD_NUMBER = 4;
      private long positiveIntValue_;
      public static final int NEGATIVE_INT_VALUE_FIELD_NUMBER = 5;
      private long negativeIntValue_;
      public static final int DOUBLE_VALUE_FIELD_NUMBER = 6;
      private double doubleValue_;
      public static final int STRING_VALUE_FIELD_NUMBER = 7;
      private ByteString stringValue_;
      public static final int AGGREGATE_VALUE_FIELD_NUMBER = 8;
      private String aggregateValue_;
      private byte memoizedIsInitialized = 2;
      private static final DescriptorProtos.UninterpretedOption DEFAULT_INSTANCE;
      private static volatile Parser<DescriptorProtos.UninterpretedOption> PARSER;

      private UninterpretedOption() {
         this.name_ = emptyProtobufList();
         this.identifierValue_ = "";
         this.stringValue_ = ByteString.EMPTY;
         this.aggregateValue_ = "";
      }

      @Override
      public List<DescriptorProtos.UninterpretedOption.NamePart> getNameList() {
         return this.name_;
      }

      public List<? extends DescriptorProtos.UninterpretedOption.NamePartOrBuilder> getNameOrBuilderList() {
         return this.name_;
      }

      @Override
      public int getNameCount() {
         return this.name_.size();
      }

      @Override
      public DescriptorProtos.UninterpretedOption.NamePart getName(int index) {
         return this.name_.get(index);
      }

      public DescriptorProtos.UninterpretedOption.NamePartOrBuilder getNameOrBuilder(int index) {
         return this.name_.get(index);
      }

      private void ensureNameIsMutable() {
         Internal.ProtobufList<DescriptorProtos.UninterpretedOption.NamePart> tmp = this.name_;
         if (!tmp.isModifiable()) {
            this.name_ = GeneratedMessageLite.mutableCopy(tmp);
         }
      }

      private void setName(int index, DescriptorProtos.UninterpretedOption.NamePart value) {
         value.getClass();
         this.ensureNameIsMutable();
         this.name_.set(index, value);
      }

      private void addName(DescriptorProtos.UninterpretedOption.NamePart value) {
         value.getClass();
         this.ensureNameIsMutable();
         this.name_.add(value);
      }

      private void addName(int index, DescriptorProtos.UninterpretedOption.NamePart value) {
         value.getClass();
         this.ensureNameIsMutable();
         this.name_.add(index, value);
      }

      private void addAllName(Iterable<? extends DescriptorProtos.UninterpretedOption.NamePart> values) {
         this.ensureNameIsMutable();
         AbstractMessageLite.addAll(values, this.name_);
      }

      private void clearName() {
         this.name_ = emptyProtobufList();
      }

      private void removeName(int index) {
         this.ensureNameIsMutable();
         this.name_.remove(index);
      }

      @Override
      public boolean hasIdentifierValue() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getIdentifierValue() {
         return this.identifierValue_;
      }

      @Override
      public ByteString getIdentifierValueBytes() {
         return ByteString.copyFromUtf8(this.identifierValue_);
      }

      private void setIdentifierValue(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 1;
         this.identifierValue_ = value;
      }

      private void clearIdentifierValue() {
         this.bitField0_ &= -2;
         this.identifierValue_ = getDefaultInstance().getIdentifierValue();
      }

      private void setIdentifierValueBytes(ByteString value) {
         this.identifierValue_ = value.toStringUtf8();
         this.bitField0_ |= 1;
      }

      @Override
      public boolean hasPositiveIntValue() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public long getPositiveIntValue() {
         return this.positiveIntValue_;
      }

      private void setPositiveIntValue(long value) {
         this.bitField0_ |= 2;
         this.positiveIntValue_ = value;
      }

      private void clearPositiveIntValue() {
         this.bitField0_ &= -3;
         this.positiveIntValue_ = 0L;
      }

      @Override
      public boolean hasNegativeIntValue() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public long getNegativeIntValue() {
         return this.negativeIntValue_;
      }

      private void setNegativeIntValue(long value) {
         this.bitField0_ |= 4;
         this.negativeIntValue_ = value;
      }

      private void clearNegativeIntValue() {
         this.bitField0_ &= -5;
         this.negativeIntValue_ = 0L;
      }

      @Override
      public boolean hasDoubleValue() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public double getDoubleValue() {
         return this.doubleValue_;
      }

      private void setDoubleValue(double value) {
         this.bitField0_ |= 8;
         this.doubleValue_ = value;
      }

      private void clearDoubleValue() {
         this.bitField0_ &= -9;
         this.doubleValue_ = 0.0;
      }

      @Override
      public boolean hasStringValue() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public ByteString getStringValue() {
         return this.stringValue_;
      }

      private void setStringValue(ByteString value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 16;
         this.stringValue_ = value;
      }

      private void clearStringValue() {
         this.bitField0_ &= -17;
         this.stringValue_ = getDefaultInstance().getStringValue();
      }

      @Override
      public boolean hasAggregateValue() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public String getAggregateValue() {
         return this.aggregateValue_;
      }

      @Override
      public ByteString getAggregateValueBytes() {
         return ByteString.copyFromUtf8(this.aggregateValue_);
      }

      private void setAggregateValue(String value) {
         Class<?> valueClass = value.getClass();
         this.bitField0_ |= 32;
         this.aggregateValue_ = value;
      }

      private void clearAggregateValue() {
         this.bitField0_ &= -33;
         this.aggregateValue_ = getDefaultInstance().getAggregateValue();
      }

      private void setAggregateValueBytes(ByteString value) {
         this.aggregateValue_ = value.toStringUtf8();
         this.bitField0_ |= 32;
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(InputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.UninterpretedOption parseDelimitedFrom(InputStream input) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.UninterpretedOption parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
      }

      public static DescriptorProtos.UninterpretedOption parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
      }

      public static DescriptorProtos.UninterpretedOption.Builder newBuilder() {
         return DEFAULT_INSTANCE.createBuilder();
      }

      public static DescriptorProtos.UninterpretedOption.Builder newBuilder(DescriptorProtos.UninterpretedOption prototype) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.StackOverflowError
         //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
         //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
         //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:667)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
         //
         // Bytecode:
         // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$UninterpretedOption.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$UninterpretedOption;
         // 3: aload 0
         // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$UninterpretedOption.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
         // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$UninterpretedOption$Builder
         // a: areturn
      }

      @Override
      protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
         switch (method) {
            case NEW_MUTABLE_INSTANCE:
               return new DescriptorProtos.UninterpretedOption();
            case NEW_BUILDER:
               return new DescriptorProtos.UninterpretedOption.Builder();
            case BUILD_MESSAGE_INFO:
               Object[] objects = new Object[]{
                  "bitField0_",
                  "name_",
                  DescriptorProtos.UninterpretedOption.NamePart.class,
                  "identifierValue_",
                  "positiveIntValue_",
                  "negativeIntValue_",
                  "doubleValue_",
                  "stringValue_",
                  "aggregateValue_"
               };
               String info = "\u0001\u0007\u0000\u0001\u0002\b\u0007\u0000\u0001\u0001\u0002Л\u0003ဈ\u0000\u0004ဃ\u0001\u0005ဂ\u0002\u0006က\u0003\u0007ည\u0004\bဈ\u0005";
               return newMessageInfo(DEFAULT_INSTANCE, info, objects);
            case GET_DEFAULT_INSTANCE:
               return DEFAULT_INSTANCE;
            case GET_PARSER:
               Parser<DescriptorProtos.UninterpretedOption> parser = PARSER;
               if (parser == null) {
                  synchronized (DescriptorProtos.UninterpretedOption.class) {
                     parser = PARSER;
                     if (parser == null) {
                        parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                        PARSER = parser;
                     }
                  }
               }

               return parser;
            case GET_MEMOIZED_IS_INITIALIZED:
               return this.memoizedIsInitialized;
            case SET_MEMOIZED_IS_INITIALIZED:
               this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
               return null;
            default:
               throw new UnsupportedOperationException();
         }
      }

      public static DescriptorProtos.UninterpretedOption getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DescriptorProtos.UninterpretedOption> parser() {
         return DEFAULT_INSTANCE.getParserForType();
      }

      static {
         DescriptorProtos.UninterpretedOption defaultInstance = new DescriptorProtos.UninterpretedOption();
         DEFAULT_INSTANCE = defaultInstance;
         GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.UninterpretedOption.class, defaultInstance);
      }

      public static final class Builder
         extends GeneratedMessageLite.Builder<DescriptorProtos.UninterpretedOption, DescriptorProtos.UninterpretedOption.Builder>
         implements DescriptorProtos.UninterpretedOptionOrBuilder {
         private Builder() {
            super(DescriptorProtos.UninterpretedOption.DEFAULT_INSTANCE);
         }

         @Override
         public List<DescriptorProtos.UninterpretedOption.NamePart> getNameList() {
            return Collections.unmodifiableList(this.instance.getNameList());
         }

         @Override
         public int getNameCount() {
            return this.instance.getNameCount();
         }

         @Override
         public DescriptorProtos.UninterpretedOption.NamePart getName(int index) {
            return this.instance.getName(index);
         }

         public DescriptorProtos.UninterpretedOption.Builder setName(int index, DescriptorProtos.UninterpretedOption.NamePart value) {
            this.copyOnWrite();
            this.instance.setName(index, value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder setName(int index, DescriptorProtos.UninterpretedOption.NamePart.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.setName(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder addName(DescriptorProtos.UninterpretedOption.NamePart value) {
            this.copyOnWrite();
            this.instance.addName(value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder addName(int index, DescriptorProtos.UninterpretedOption.NamePart value) {
            this.copyOnWrite();
            this.instance.addName(index, value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder addName(DescriptorProtos.UninterpretedOption.NamePart.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addName(builderForValue.build());
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder addName(int index, DescriptorProtos.UninterpretedOption.NamePart.Builder builderForValue) {
            this.copyOnWrite();
            this.instance.addName(index, builderForValue.build());
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder addAllName(Iterable<? extends DescriptorProtos.UninterpretedOption.NamePart> values) {
            this.copyOnWrite();
            this.instance.addAllName(values);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder clearName() {
            this.copyOnWrite();
            this.instance.clearName();
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder removeName(int index) {
            this.copyOnWrite();
            this.instance.removeName(index);
            return this;
         }

         @Override
         public boolean hasIdentifierValue() {
            return this.instance.hasIdentifierValue();
         }

         @Override
         public String getIdentifierValue() {
            return this.instance.getIdentifierValue();
         }

         @Override
         public ByteString getIdentifierValueBytes() {
            return this.instance.getIdentifierValueBytes();
         }

         public DescriptorProtos.UninterpretedOption.Builder setIdentifierValue(String value) {
            this.copyOnWrite();
            this.instance.setIdentifierValue(value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder clearIdentifierValue() {
            this.copyOnWrite();
            this.instance.clearIdentifierValue();
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder setIdentifierValueBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setIdentifierValueBytes(value);
            return this;
         }

         @Override
         public boolean hasPositiveIntValue() {
            return this.instance.hasPositiveIntValue();
         }

         @Override
         public long getPositiveIntValue() {
            return this.instance.getPositiveIntValue();
         }

         public DescriptorProtos.UninterpretedOption.Builder setPositiveIntValue(long value) {
            this.copyOnWrite();
            this.instance.setPositiveIntValue(value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder clearPositiveIntValue() {
            this.copyOnWrite();
            this.instance.clearPositiveIntValue();
            return this;
         }

         @Override
         public boolean hasNegativeIntValue() {
            return this.instance.hasNegativeIntValue();
         }

         @Override
         public long getNegativeIntValue() {
            return this.instance.getNegativeIntValue();
         }

         public DescriptorProtos.UninterpretedOption.Builder setNegativeIntValue(long value) {
            this.copyOnWrite();
            this.instance.setNegativeIntValue(value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder clearNegativeIntValue() {
            this.copyOnWrite();
            this.instance.clearNegativeIntValue();
            return this;
         }

         @Override
         public boolean hasDoubleValue() {
            return this.instance.hasDoubleValue();
         }

         @Override
         public double getDoubleValue() {
            return this.instance.getDoubleValue();
         }

         public DescriptorProtos.UninterpretedOption.Builder setDoubleValue(double value) {
            this.copyOnWrite();
            this.instance.setDoubleValue(value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder clearDoubleValue() {
            this.copyOnWrite();
            this.instance.clearDoubleValue();
            return this;
         }

         @Override
         public boolean hasStringValue() {
            return this.instance.hasStringValue();
         }

         @Override
         public ByteString getStringValue() {
            return this.instance.getStringValue();
         }

         public DescriptorProtos.UninterpretedOption.Builder setStringValue(ByteString value) {
            this.copyOnWrite();
            this.instance.setStringValue(value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder clearStringValue() {
            this.copyOnWrite();
            this.instance.clearStringValue();
            return this;
         }

         @Override
         public boolean hasAggregateValue() {
            return this.instance.hasAggregateValue();
         }

         @Override
         public String getAggregateValue() {
            return this.instance.getAggregateValue();
         }

         @Override
         public ByteString getAggregateValueBytes() {
            return this.instance.getAggregateValueBytes();
         }

         public DescriptorProtos.UninterpretedOption.Builder setAggregateValue(String value) {
            this.copyOnWrite();
            this.instance.setAggregateValue(value);
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder clearAggregateValue() {
            this.copyOnWrite();
            this.instance.clearAggregateValue();
            return this;
         }

         public DescriptorProtos.UninterpretedOption.Builder setAggregateValueBytes(ByteString value) {
            this.copyOnWrite();
            this.instance.setAggregateValueBytes(value);
            return this;
         }
      }

      public static final class NamePart
         extends GeneratedMessageLite<DescriptorProtos.UninterpretedOption.NamePart, DescriptorProtos.UninterpretedOption.NamePart.Builder>
         implements DescriptorProtos.UninterpretedOption.NamePartOrBuilder {
         private int bitField0_;
         public static final int NAME_PART_FIELD_NUMBER = 1;
         private String namePart_;
         public static final int IS_EXTENSION_FIELD_NUMBER = 2;
         private boolean isExtension_;
         private byte memoizedIsInitialized = 2;
         private static final DescriptorProtos.UninterpretedOption.NamePart DEFAULT_INSTANCE;
         private static volatile Parser<DescriptorProtos.UninterpretedOption.NamePart> PARSER;

         private NamePart() {
            this.namePart_ = "";
         }

         @Override
         public boolean hasNamePart() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getNamePart() {
            return this.namePart_;
         }

         @Override
         public ByteString getNamePartBytes() {
            return ByteString.copyFromUtf8(this.namePart_);
         }

         private void setNamePart(String value) {
            Class<?> valueClass = value.getClass();
            this.bitField0_ |= 1;
            this.namePart_ = value;
         }

         private void clearNamePart() {
            this.bitField0_ &= -2;
            this.namePart_ = getDefaultInstance().getNamePart();
         }

         private void setNamePartBytes(ByteString value) {
            this.namePart_ = value.toStringUtf8();
            this.bitField0_ |= 1;
         }

         @Override
         public boolean hasIsExtension() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public boolean getIsExtension() {
            return this.isExtension_;
         }

         private void setIsExtension(boolean value) {
            this.bitField0_ |= 2;
            this.isExtension_ = value;
         }

         private void clearIsExtension() {
            this.bitField0_ &= -3;
            this.isExtension_ = false;
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(InputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseDelimitedFrom(InputStream input) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
         }

         public static DescriptorProtos.UninterpretedOption.NamePart.Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
         }

         public static DescriptorProtos.UninterpretedOption.NamePart.Builder newBuilder(DescriptorProtos.UninterpretedOption.NamePart prototype) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.StackOverflowError
            //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
            //
            // Bytecode:
            // 0: getstatic me/lucko/spark/lib/protobuf/DescriptorProtos$UninterpretedOption$NamePart.DEFAULT_INSTANCE Lme/lucko/spark/lib/protobuf/DescriptorProtos$UninterpretedOption$NamePart;
            // 3: aload 0
            // 4: invokevirtual me/lucko/spark/lib/protobuf/DescriptorProtos$UninterpretedOption$NamePart.createBuilder (Lme/lucko/spark/lib/protobuf/GeneratedMessageLite;)Lme/lucko/spark/lib/protobuf/GeneratedMessageLite$Builder;
            // 7: checkcast me/lucko/spark/lib/protobuf/DescriptorProtos$UninterpretedOption$NamePart$Builder
            // a: areturn
         }

         @Override
         protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
               case NEW_MUTABLE_INSTANCE:
                  return new DescriptorProtos.UninterpretedOption.NamePart();
               case NEW_BUILDER:
                  return new DescriptorProtos.UninterpretedOption.NamePart.Builder();
               case BUILD_MESSAGE_INFO:
                  Object[] objects = new Object[]{"bitField0_", "namePart_", "isExtension_"};
                  String info = "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0002\u0001ᔈ\u0000\u0002ᔇ\u0001";
                  return newMessageInfo(DEFAULT_INSTANCE, info, objects);
               case GET_DEFAULT_INSTANCE:
                  return DEFAULT_INSTANCE;
               case GET_PARSER:
                  Parser<DescriptorProtos.UninterpretedOption.NamePart> parser = PARSER;
                  if (parser == null) {
                     synchronized (DescriptorProtos.UninterpretedOption.NamePart.class) {
                        parser = PARSER;
                        if (parser == null) {
                           parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                           PARSER = parser;
                        }
                     }
                  }

                  return parser;
               case GET_MEMOIZED_IS_INITIALIZED:
                  return this.memoizedIsInitialized;
               case SET_MEMOIZED_IS_INITIALIZED:
                  this.memoizedIsInitialized = (byte)(arg0 == null ? 0 : 1);
                  return null;
               default:
                  throw new UnsupportedOperationException();
            }
         }

         public static DescriptorProtos.UninterpretedOption.NamePart getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<DescriptorProtos.UninterpretedOption.NamePart> parser() {
            return DEFAULT_INSTANCE.getParserForType();
         }

         static {
            DescriptorProtos.UninterpretedOption.NamePart defaultInstance = new DescriptorProtos.UninterpretedOption.NamePart();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(DescriptorProtos.UninterpretedOption.NamePart.class, defaultInstance);
         }

         public static final class Builder
            extends GeneratedMessageLite.Builder<DescriptorProtos.UninterpretedOption.NamePart, DescriptorProtos.UninterpretedOption.NamePart.Builder>
            implements DescriptorProtos.UninterpretedOption.NamePartOrBuilder {
            private Builder() {
               super(DescriptorProtos.UninterpretedOption.NamePart.DEFAULT_INSTANCE);
            }

            @Override
            public boolean hasNamePart() {
               return this.instance.hasNamePart();
            }

            @Override
            public String getNamePart() {
               return this.instance.getNamePart();
            }

            @Override
            public ByteString getNamePartBytes() {
               return this.instance.getNamePartBytes();
            }

            public DescriptorProtos.UninterpretedOption.NamePart.Builder setNamePart(String value) {
               this.copyOnWrite();
               this.instance.setNamePart(value);
               return this;
            }

            public DescriptorProtos.UninterpretedOption.NamePart.Builder clearNamePart() {
               this.copyOnWrite();
               this.instance.clearNamePart();
               return this;
            }

            public DescriptorProtos.UninterpretedOption.NamePart.Builder setNamePartBytes(ByteString value) {
               this.copyOnWrite();
               this.instance.setNamePartBytes(value);
               return this;
            }

            @Override
            public boolean hasIsExtension() {
               return this.instance.hasIsExtension();
            }

            @Override
            public boolean getIsExtension() {
               return this.instance.getIsExtension();
            }

            public DescriptorProtos.UninterpretedOption.NamePart.Builder setIsExtension(boolean value) {
               this.copyOnWrite();
               this.instance.setIsExtension(value);
               return this;
            }

            public DescriptorProtos.UninterpretedOption.NamePart.Builder clearIsExtension() {
               this.copyOnWrite();
               this.instance.clearIsExtension();
               return this;
            }
         }
      }

      public interface NamePartOrBuilder extends MessageLiteOrBuilder {
         boolean hasNamePart();

         String getNamePart();

         ByteString getNamePartBytes();

         boolean hasIsExtension();

         boolean getIsExtension();
      }
   }

   public interface UninterpretedOptionOrBuilder extends MessageLiteOrBuilder {
      List<DescriptorProtos.UninterpretedOption.NamePart> getNameList();

      DescriptorProtos.UninterpretedOption.NamePart getName(int index);

      int getNameCount();

      boolean hasIdentifierValue();

      String getIdentifierValue();

      ByteString getIdentifierValueBytes();

      boolean hasPositiveIntValue();

      long getPositiveIntValue();

      boolean hasNegativeIntValue();

      long getNegativeIntValue();

      boolean hasDoubleValue();

      double getDoubleValue();

      boolean hasStringValue();

      ByteString getStringValue();

      boolean hasAggregateValue();

      String getAggregateValue();

      ByteString getAggregateValueBytes();
   }
}
