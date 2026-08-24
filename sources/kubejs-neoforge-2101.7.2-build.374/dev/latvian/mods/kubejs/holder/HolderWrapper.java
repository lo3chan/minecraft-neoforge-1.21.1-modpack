package dev.latvian.mods.kubejs.holder;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Direct;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.jetbrains.annotations.Nullable;

public interface HolderWrapper {
   TypeInfo HOLDER = TypeInfo.of(Holder.class);
   TypeInfo HOLDER_SET = TypeInfo.of(HolderSet.class);

   static Holder<?> wrap(KubeJSContext cx, Object from, TypeInfo param) {
      if (from instanceof Holder<?> h) {
         return h;
      } else if (from == null) {
         throw Context.reportRuntimeError("Can't interpret 'null' as a Holder", cx);
      } else {
         Registry<?> registry = cx.lookupRegistry(param, from);
         if (!ID.isKey(from)) {
            Holder<?> h = registry.wrapAsHolder(Cast.to(from));
            if (h instanceof Direct) {
               Class<?> baseClass = cx.lookupRegistryType(param, from).baseClass();
               if (!baseClass.isInstance(from)) {
                  throw Context.reportRuntimeError(
                     "Can't interpret '" + from + "' as Holder: can't cast object to '" + baseClass.getName() + "' of " + registry.key().location(), cx
                  );
               }
            }

            return h;
         } else {
            ResourceLocation id = ID.mc(from);
            Optional<? extends Reference<?>> holder = registry.getHolder(id);
            return (Holder<?>)(holder.isEmpty() ? DeferredHolder.create(registry.key(), id) : (Holder)holder.get());
         }
      }
   }

   static Reference<?> wrapRef(KubeJSContext cx, Object from, TypeInfo param) {
      Holder<?> h = wrap(cx, from, param);
      if (h.getDelegate() instanceof Reference<?> ref) {
         return ref;
      } else if (h instanceof Direct) {
         throw Context.reportRuntimeError("Can't interpret '" + from + "' as a Reference Holder: cannot obtain its registry id", cx);
      } else {
         Registry<?> registry = cx.lookupRegistry(param, from);
         return Reference.createStandAlone(Cast.to(registry.holderOwner()), h.getKey());
      }
   }

   static HolderSet<?> wrapSet(KubeJSContext cx, Object from, TypeInfo param) {
      Registry<?> registry = cx.lookupRegistry(param, from);
      HolderSet<?> simpleHolders = wrapSimpleSet(registry, from);
      if (simpleHolders != null) {
         return simpleHolders;
      } else if (from instanceof Iterable<?> itr) {
         Builder<HolderSet<?>> allDirects = Stream.builder();
         ArrayList<HolderSet<?>> complex = new ArrayList<>();

         for (Object elem : itr) {
            HolderSet<?> wrapped = wrapSet(cx, elem, param);
            if (wrapped instanceof net.minecraft.core.HolderSet.Direct direct) {
               allDirects.accept(direct);
            } else {
               complex.add(wrapped);
            }
         }

         List<? extends Holder<?>> compressedDirects = allDirects.build().flatMap(HolderSet::stream).distinct().toList();
         if (compressedDirects.isEmpty()) {
            return (HolderSet<?>)(switch (complex.size()) {
               case 0 -> HolderSet.empty();
               case 1 -> (HolderSet)complex.getFirst();
               default -> new OrHolderSet(complex);
            });
         } else if (complex.isEmpty()) {
            return HolderSet.direct(compressedDirects);
         } else {
            complex.add(HolderSet.direct(compressedDirects));
            return new OrHolderSet(complex);
         }
      } else {
         Holder holder = (Holder)cx.jsToJava(from, HOLDER.withParams(new TypeInfo[]{param}));
         return HolderSet.direct(new Holder[]{holder});
      }
   }

   @Nullable
   static <T> HolderSet<T> wrapSimpleSet(Registry<T> registry, Object from) {
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
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 000: aload 1
      // 001: astore 2
      // 002: bipush 0
      // 003: istore 3
      // 004: aload 2
      // 005: iload 3
      // 006: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/minecraft/core/HolderSet, net/minecraft/core/Holder, dev/latvian/mods/rhino/regexp/NativeRegExp, java/util/regex/Pattern, dev/latvian/mods/kubejs/core/RegistryObjectKJS, net/minecraft/tags/TagKey, net/minecraft/resources/ResourceKey, net/minecraft/resources/ResourceLocation, java/lang/CharSequence, java/lang/CharSequence ]
      // 00b: tableswitch 478 -1 9 478 57 68 110 133 153 173 213 278 306 334
      // 044: aload 2
      // 045: checkcast net/minecraft/core/HolderSet
      // 048: astore 4
      // 04a: aload 4
      // 04c: goto 1ed
      // 04f: aload 2
      // 050: checkcast net/minecraft/core/Holder
      // 053: astore 5
      // 055: aload 5
      // 057: aload 0
      // 058: invokeinterface net/minecraft/core/Registry.holderOwner ()Lnet/minecraft/core/HolderOwner; 1
      // 05d: invokeinterface net/minecraft/core/Holder.canSerializeIn (Lnet/minecraft/core/HolderOwner;)Z 2
      // 062: ifne 06a
      // 065: bipush 2
      // 066: istore 3
      // 067: goto 004
      // 06a: bipush 1
      // 06b: anewarray 1
      // 06e: dup
      // 06f: bipush 0
      // 070: aload 5
      // 072: aastore
      // 073: invokestatic net/minecraft/core/HolderSet.direct ([Lnet/minecraft/core/Holder;)Lnet/minecraft/core/HolderSet$Direct;
      // 076: goto 1ed
      // 079: aload 2
      // 07a: checkcast dev/latvian/mods/rhino/regexp/NativeRegExp
      // 07d: astore 6
      // 07f: aload 0
      // 080: invokeinterface net/minecraft/core/Registry.asLookup ()Lnet/minecraft/core/HolderLookup$RegistryLookup; 1
      // 085: aload 6
      // 087: invokestatic dev/latvian/mods/kubejs/util/RegExpKJS.wrap (Ljava/lang/Object;)Ljava/util/regex/Pattern;
      // 08a: invokestatic dev/latvian/mods/kubejs/holder/RegExHolderSet.of (Lnet/minecraft/core/HolderLookup$RegistryLookup;Ljava/util/regex/Pattern;)Lnet/minecraft/core/HolderSet;
      // 08d: goto 1ed
      // 090: aload 2
      // 091: checkcast java/util/regex/Pattern
      // 094: astore 7
      // 096: aload 0
      // 097: invokeinterface net/minecraft/core/Registry.asLookup ()Lnet/minecraft/core/HolderLookup$RegistryLookup; 1
      // 09c: aload 7
      // 09e: invokestatic dev/latvian/mods/kubejs/holder/RegExHolderSet.of (Lnet/minecraft/core/HolderLookup$RegistryLookup;Ljava/util/regex/Pattern;)Lnet/minecraft/core/HolderSet;
      // 0a1: goto 1ed
      // 0a4: aload 2
      // 0a5: checkcast dev/latvian/mods/kubejs/core/RegistryObjectKJS
      // 0a8: astore 8
      // 0aa: aload 0
      // 0ab: aload 8
      // 0ad: invokeinterface dev/latvian/mods/kubejs/core/RegistryObjectKJS.kjs$asHolder ()Lnet/minecraft/core/Holder; 1
      // 0b2: invokestatic dev/latvian/mods/kubejs/holder/HolderWrapper.wrapSimpleSet (Lnet/minecraft/core/Registry;Ljava/lang/Object;)Lnet/minecraft/core/HolderSet;
      // 0b5: goto 1ed
      // 0b8: aload 2
      // 0b9: checkcast net/minecraft/tags/TagKey
      // 0bc: astore 9
      // 0be: aload 9
      // 0c0: aload 0
      // 0c1: invokeinterface net/minecraft/core/Registry.key ()Lnet/minecraft/resources/ResourceKey; 1
      // 0c6: invokevirtual net/minecraft/tags/TagKey.isFor (Lnet/minecraft/resources/ResourceKey;)Z
      // 0c9: ifne 0d2
      // 0cc: bipush 6
      // 0ce: istore 3
      // 0cf: goto 004
      // 0d2: aload 0
      // 0d3: aload 9
      // 0d5: invokeinterface net/minecraft/core/Registry.getTag (Lnet/minecraft/tags/TagKey;)Ljava/util/Optional; 2
      // 0da: invokestatic dev/latvian/mods/kubejs/holder/HolderWrapper.orEmpty (Ljava/util/Optional;)Lnet/minecraft/core/HolderSet;
      // 0dd: goto 1ed
      // 0e0: aload 2
      // 0e1: checkcast net/minecraft/resources/ResourceKey
      // 0e4: astore 10
      // 0e6: aload 10
      // 0e8: aload 0
      // 0e9: invokeinterface net/minecraft/core/Registry.key ()Lnet/minecraft/resources/ResourceKey; 1
      // 0ee: invokevirtual net/minecraft/resources/ResourceKey.isFor (Lnet/minecraft/resources/ResourceKey;)Z
      // 0f1: ifne 0fa
      // 0f4: bipush 7
      // 0f6: istore 3
      // 0f7: goto 004
      // 0fa: aload 10
      // 0fc: aload 0
      // 0fd: invokeinterface net/minecraft/core/Registry.key ()Lnet/minecraft/resources/ResourceKey; 1
      // 102: invokevirtual net/minecraft/resources/ResourceKey.cast (Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;
      // 105: aload 0
      // 106: dup
      // 107: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 10a: pop
      // 10b: invokedynamic apply (Lnet/minecraft/core/Registry;)Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, net/minecraft/core/Registry.getHolder (Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;, (Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional; ]
      // 110: invokevirtual java/util/Optional.flatMap (Ljava/util/function/Function;)Ljava/util/Optional;
      // 113: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/holder/HolderWrapper.lambda$wrapSimpleSet$0 (Lnet/minecraft/core/Holder;)Lnet/minecraft/core/HolderSet$Direct;, (Lnet/minecraft/core/Holder$Reference;)Lnet/minecraft/core/HolderSet$Direct; ]
      // 118: invokevirtual java/util/Optional.map (Ljava/util/function/Function;)Ljava/util/Optional;
      // 11b: invokestatic dev/latvian/mods/kubejs/holder/HolderWrapper.orEmpty (Ljava/util/Optional;)Lnet/minecraft/core/HolderSet;
      // 11e: goto 1ed
      // 121: aload 2
      // 122: checkcast net/minecraft/resources/ResourceLocation
      // 125: astore 11
      // 127: aload 0
      // 128: aload 11
      // 12a: invokeinterface net/minecraft/core/Registry.getHolder (Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional; 2
      // 12f: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/holder/HolderWrapper.lambda$wrapSimpleSet$1 (Lnet/minecraft/core/Holder;)Lnet/minecraft/core/HolderSet$Direct;, (Lnet/minecraft/core/Holder$Reference;)Lnet/minecraft/core/HolderSet$Direct; ]
      // 134: invokevirtual java/util/Optional.map (Ljava/util/function/Function;)Ljava/util/Optional;
      // 137: invokestatic dev/latvian/mods/kubejs/holder/HolderWrapper.orEmpty (Ljava/util/Optional;)Lnet/minecraft/core/HolderSet;
      // 13a: goto 1ed
      // 13d: aload 2
      // 13e: checkcast java/lang/CharSequence
      // 141: astore 12
      // 143: aload 12
      // 145: invokeinterface java/lang/CharSequence.isEmpty ()Z 1
      // 14a: ifne 153
      // 14d: bipush 9
      // 14f: istore 3
      // 150: goto 004
      // 153: invokestatic net/minecraft/core/HolderSet.empty ()Lnet/minecraft/core/HolderSet;
      // 156: goto 1ed
      // 159: aload 2
      // 15a: checkcast java/lang/CharSequence
      // 15d: astore 13
      // 15f: aload 13
      // 161: invokeinterface java/lang/CharSequence.toString ()Ljava/lang/String; 1
      // 166: astore 14
      // 168: aload 14
      // 16a: bipush 0
      // 16b: invokevirtual java/lang/String.charAt (I)C
      // 16e: lookupswitch 94 3 35 52 47 83 64 34
      // 190: aload 0
      // 191: invokeinterface net/minecraft/core/Registry.asLookup ()Lnet/minecraft/core/HolderLookup$RegistryLookup; 1
      // 196: aload 14
      // 198: bipush 1
      // 199: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 19c: invokestatic dev/latvian/mods/kubejs/holder/NamespaceHolderSet.of (Lnet/minecraft/core/HolderLookup$RegistryLookup;Ljava/lang/String;)Lnet/minecraft/core/HolderSet;
      // 19f: goto 1ed
      // 1a2: aload 0
      // 1a3: invokeinterface net/minecraft/core/Registry.key ()Lnet/minecraft/resources/ResourceKey; 1
      // 1a8: aload 14
      // 1aa: bipush 1
      // 1ab: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 1ae: invokestatic net/minecraft/resources/ResourceLocation.parse (Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;
      // 1b1: invokestatic net/minecraft/tags/TagKey.create (Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/tags/TagKey;
      // 1b4: astore 15
      // 1b6: aload 0
      // 1b7: aload 15
      // 1b9: invokeinterface net/minecraft/core/Registry.getOrCreateTag (Lnet/minecraft/tags/TagKey;)Lnet/minecraft/core/HolderSet$Named; 2
      // 1be: goto 1ed
      // 1c1: aload 0
      // 1c2: aload 1
      // 1c3: invokestatic dev/latvian/mods/kubejs/util/RegExpKJS.wrap (Ljava/lang/Object;)Ljava/util/regex/Pattern;
      // 1c6: invokestatic dev/latvian/mods/kubejs/holder/HolderWrapper.wrapSimpleSet (Lnet/minecraft/core/Registry;Ljava/lang/Object;)Lnet/minecraft/core/HolderSet;
      // 1c9: goto 1ed
      // 1cc: aload 14
      // 1ce: invokestatic net/minecraft/resources/ResourceLocation.read (Ljava/lang/String;)Lcom/mojang/serialization/DataResult;
      // 1d1: invokeinterface com/mojang/serialization/DataResult.result ()Ljava/util/Optional; 1
      // 1d6: aload 0
      // 1d7: invokedynamic apply (Lnet/minecraft/core/Registry;)Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/holder/HolderWrapper.lambda$wrapSimpleSet$2 (Lnet/minecraft/core/Registry;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/core/HolderSet;, (Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/core/HolderSet; ]
      // 1dc: invokevirtual java/util/Optional.map (Ljava/util/function/Function;)Ljava/util/Optional;
      // 1df: aconst_null
      // 1e0: invokevirtual java/util/Optional.orElse (Ljava/lang/Object;)Ljava/lang/Object;
      // 1e3: checkcast net/minecraft/core/HolderSet
      // 1e6: goto 1ed
      // 1e9: aconst_null
      // 1ea: goto 1ed
      // 1ed: areturn
   }

   private static <T> HolderSet<T> orEmpty(Optional<? extends HolderSet<T>> holder) {
      return (HolderSet<T>)holder.orElse(HolderSet.empty());
   }
}
