package dev.latvian.mods.rhino.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.Supplier;

public interface TypeInfoFactory {
   TypeInfoFactory GLOBAL = new ClassValueTypeInfoFactory();

   TypeInfo create(Class<?> var1);

   VariableTypeInfo create(TypeVariable<?> var1);

   default TypeInfo create(Type param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 1
      // 01: astore 2
      // 02: bipush 0
      // 03: istore 3
      // 04: aload 2
      // 05: iload 3
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ java/lang/Class, java/lang/reflect/ParameterizedType, java/lang/reflect/GenericArrayType, java/lang/reflect/TypeVariable, java/lang/reflect/WildcardType ]
      // 0b: tableswitch 215 -1 4 215 37 54 94 121 138
      // 30: aload 2
      // 31: checkcast java/lang/Class
      // 34: astore 4
      // 36: aload 0
      // 37: aload 4
      // 39: invokeinterface dev/latvian/mods/rhino/type/TypeInfoFactory.create (Ljava/lang/Class;)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // 3e: goto e5
      // 41: aload 2
      // 42: checkcast java/lang/reflect/ParameterizedType
      // 45: astore 5
      // 47: aload 0
      // 48: aload 5
      // 4a: invokeinterface java/lang/reflect/ParameterizedType.getRawType ()Ljava/lang/reflect/Type; 1
      // 4f: invokeinterface dev/latvian/mods/rhino/type/TypeInfoFactory.create (Ljava/lang/reflect/Type;)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // 54: aload 0
      // 55: aload 5
      // 57: invokeinterface java/lang/reflect/ParameterizedType.getActualTypeArguments ()[Ljava/lang/reflect/Type; 1
      // 5c: invokeinterface dev/latvian/mods/rhino/type/TypeInfoFactory.createArray ([Ljava/lang/reflect/Type;)[Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // 61: invokeinterface dev/latvian/mods/rhino/type/TypeInfo.withParams ([Ldev/latvian/mods/rhino/type/TypeInfo;)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // 66: goto e5
      // 69: aload 2
      // 6a: checkcast java/lang/reflect/GenericArrayType
      // 6d: astore 6
      // 6f: aload 0
      // 70: aload 6
      // 72: invokeinterface java/lang/reflect/GenericArrayType.getGenericComponentType ()Ljava/lang/reflect/Type; 1
      // 77: invokeinterface dev/latvian/mods/rhino/type/TypeInfoFactory.create (Ljava/lang/reflect/Type;)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // 7c: invokeinterface dev/latvian/mods/rhino/type/TypeInfo.asArray ()Ldev/latvian/mods/rhino/type/TypeInfo; 1
      // 81: goto e5
      // 84: aload 2
      // 85: checkcast java/lang/reflect/TypeVariable
      // 88: astore 7
      // 8a: aload 0
      // 8b: aload 7
      // 8d: invokeinterface dev/latvian/mods/rhino/type/TypeInfoFactory.create (Ljava/lang/reflect/TypeVariable;)Ldev/latvian/mods/rhino/type/VariableTypeInfo; 2
      // 92: goto e5
      // 95: aload 2
      // 96: checkcast java/lang/reflect/WildcardType
      // 99: astore 8
      // 9b: aload 8
      // 9d: invokeinterface java/lang/reflect/WildcardType.getLowerBounds ()[Ljava/lang/reflect/Type; 1
      // a2: astore 9
      // a4: aload 9
      // a6: arraylength
      // a7: ifne d5
      // aa: aload 8
      // ac: invokeinterface java/lang/reflect/WildcardType.getUpperBounds ()[Ljava/lang/reflect/Type; 1
      // b1: astore 10
      // b3: aload 10
      // b5: arraylength
      // b6: ifeq c2
      // b9: aload 10
      // bb: bipush 0
      // bc: aaload
      // bd: ldc java/lang/Object
      // bf: if_acmpne c8
      // c2: getstatic dev/latvian/mods/rhino/type/TypeInfo.NONE Ldev/latvian/mods/rhino/type/TypeInfo;
      // c5: goto e5
      // c8: aload 0
      // c9: aload 10
      // cb: bipush 0
      // cc: aaload
      // cd: invokeinterface dev/latvian/mods/rhino/type/TypeInfoFactory.create (Ljava/lang/reflect/Type;)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // d2: goto e5
      // d5: aload 0
      // d6: aload 9
      // d8: bipush 0
      // d9: aaload
      // da: invokeinterface dev/latvian/mods/rhino/type/TypeInfoFactory.create (Ljava/lang/reflect/Type;)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // df: goto e5
      // e2: getstatic dev/latvian/mods/rhino/type/TypeInfo.NONE Ldev/latvian/mods/rhino/type/TypeInfo;
      // e5: areturn
   }

   default TypeInfo[] createArray(Type[] array) {
      if (array.length == 0) {
         return TypeInfo.EMPTY_ARRAY;
      } else {
         TypeInfo[] arr = new TypeInfo[array.length];

         for (int i = 0; i < array.length; i++) {
            arr[i] = this.create(array[i]);
         }

         return arr;
      }
   }

   default TypeInfo safeCreate(Supplier<Type> supplier) {
      try {
         return this.create(supplier.get());
      } catch (Throwable var3) {
         return TypeInfo.NONE;
      }
   }

   default TypeInfo[] safeCreateArray(Supplier<Type[]> supplier) {
      try {
         return this.createArray(supplier.get());
      } catch (Throwable var3) {
         return TypeInfo.EMPTY_ARRAY;
      }
   }
}
