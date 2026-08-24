package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.ingredient.NamespaceIngredient;
import dev.latvian.mods.kubejs.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

@Info("Various Ingredient related helper methods")
public interface IngredientWrapper {
   TypeInfo TYPE_INFO = TypeInfo.of(Ingredient.class);
   @Info("A completely empty ingredient that will only match air")
   Ingredient none = Ingredient.EMPTY;
   @Info("An ingredient that matches everything")
   Ingredient all = WildcardIngredient.INSTANCE.toVanilla();

   @Info("Returns an ingredient of the input")
   static Ingredient of(Ingredient ingredient) {
      return ingredient;
   }

   @Info("Returns an ingredient of the input, with the specified count")
   static SizedIngredient of(Ingredient ingredient, int count) {
      return ingredient.kjs$withCount(count);
   }

   @Info("Returns an ingredient that accepts the given set of items under the given component filter.")
   static Ingredient withData(HolderSet<Item> base, DataComponentMap data) {
      return withData(base, data, false);
   }

   @Info("Returns an ingredient that accepts the given set of items under the given (optionally strict) component filter.")
   static Ingredient withData(HolderSet<Item> base, DataComponentMap data, boolean strict) {
      return DataComponentIngredient.of(strict, data, base);
   }

   @HideFromJS
   private static Ingredient wrapTrivial(@Nullable Object from) {
      while (from instanceof Wrapper) {
         Wrapper w = (Wrapper)from;
         from = w.unwrap();
      }
      return switch (from) {
         case null -> Ingredient.EMPTY;
         case Ingredient id -> id;
         case ItemStack s when s.isEmpty() -> Ingredient.EMPTY;
         case ItemLike i when i.asItem() == Items.AIR -> Ingredient.EMPTY;
         case IngredientSupplierKJS ingr -> ingr.kjs$asIngredient();
         case ItemLike ix -> Ingredient.of(new ItemLike[]{ix});
         case TagKey var8 -> {
            TagKey var15 = var8;

            try {
               var15.registry();
            } catch (Throwable var13) {
               throw new MatchException(var13.toString(), var13);
            }

            var15 = var8;

            try {
               var17 = var15.location();
            } catch (Throwable var12) {
               throw new MatchException(var12.toString(), var12);
            }

            ResourceLocation var11 = var17;
            yield Ingredient.of(ItemTags.create(var11));
         }
         default -> null;
      };
   }

   @HideFromJS
   static DataResult<Ingredient> wrapResult(Context cx, @Nullable Object from) {
      while (from instanceof Wrapper) {
         Wrapper w = (Wrapper)from;
         from = w.unwrap();
      }

      Ingredient trivial = wrapTrivial(from);
      if (trivial != null) {
         return DataResult.success(trivial);
      } else if (from instanceof Pattern || from instanceof NativeRegExp) {
         String str = String.valueOf(from);
         return Optional.ofNullable(RegExpKJS.wrap(from))
            .<DataResult>map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "Invalid regex " + str))
            .map(RegExIngredient::new)
            .map(ICustomIngredient::toVanilla);
      } else if (from instanceof JsonElement json) {
         return parseJson(cx, json);
      } else if (from instanceof CharSequence) {
         return parseString(cx, from.toString());
      } else {
         List<?> list = ListJS.of(from);
         if (list != null) {
            List<Ingredient> results = new ArrayList<>(list.size());
            boolean failed = false;
            Builder<String> errors = Stream.builder();

            for (Object o1 : list) {
               DataResult<Ingredient> ingredient = wrapResult(cx, o1);
               ingredient.resultOrPartial().filter(ingr -> ingr != Ingredient.EMPTY).ifPresent(results::add);
               if (ingredient.isError()) {
                  failed = true;
                  errors.add(o1 + ": " + ((Error)ingredient.error().orElseThrow()).message());
               }
            }

            if (failed) {
               String msg = errors.build().collect(Collectors.joining("; "));
               return DataResult.error(() -> "Failed to parse ingredient list: " + msg);
            } else {
               return DataResult.success(switch (results.size()) {
                  case 0 -> Ingredient.EMPTY;
                  case 1 -> (Ingredient)results.getFirst();
                  default -> new CompoundIngredient(results).toVanilla();
               });
            }
         } else {
            Map<String, Object> map = cx.optionalMapOf(from);
            return map != null ? Ingredient.CODEC.parse(JavaOps.INSTANCE, map) : ItemWrapper.wrapResult(cx, from).map(ItemStackKJS::kjs$asIngredient);
         }
      }
   }

   @HideFromJS
   static Ingredient wrap(Context cx, @Nullable Object from) {
      Ingredient trivial = wrapTrivial(from);
      return trivial != null
         ? trivial
         : (Ingredient)wrapResult(cx, from)
            .getOrThrow(error -> new KubeRuntimeException("Failed to read ingredient from %s: %s".formatted(from, error)).source(SourceLine.of(cx)));
   }

   static boolean isIngredientLike(Object from) {
      return from instanceof Ingredient || from instanceof SizedIngredient || from instanceof ItemStack;
   }

   static DataResult<Ingredient> parseJson(Context cx, JsonElement json) {
      return switch (json) {
         case null -> DataResult.success(Ingredient.EMPTY);
         case JsonNull jsonNull -> DataResult.success(Ingredient.EMPTY);
         case JsonArray arr when arr.isEmpty() -> DataResult.success(Ingredient.EMPTY);
         case JsonPrimitive primitive -> wrapResult(cx, json.getAsString());
         default -> Ingredient.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);
      };
   }

   static DataResult<Ingredient> parseString(Context cx, String s) {
      return switch (s) {
         case "", "-", "air", "minecraft:air" -> DataResult.success(Ingredient.EMPTY);
         case "*" -> DataResult.success(all);
         default -> read(cx, new StringReader(s));
      };
   }

   static DataResult<Ingredient> read(Context cx, StringReader reader) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.main.ClassWriter.methodLambdaToJava(ClassWriter.java:979)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokestatic dev/latvian/mods/kubejs/util/RegistryAccessContainer.of (Ldev/latvian/mods/rhino/Context;)Ldev/latvian/mods/kubejs/util/RegistryAccessContainer;
      // 004: astore 2
      // 005: aload 1
      // 006: invokevirtual com/mojang/brigadier/StringReader.skipWhitespace ()V
      // 009: aload 1
      // 00a: invokevirtual com/mojang/brigadier/StringReader.canRead ()Z
      // 00d: ifne 017
      // 010: getstatic net/minecraft/world/item/crafting/Ingredient.EMPTY Lnet/minecraft/world/item/crafting/Ingredient;
      // 013: invokestatic com/mojang/serialization/DataResult.success (Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;
      // 016: areturn
      // 017: aload 1
      // 018: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 01b: lookupswitch 422 7 35 95 37 154 42 80 45 65 47 187 64 128 91 216
      // 05c: aload 1
      // 05d: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 060: getstatic net/minecraft/world/item/crafting/Ingredient.EMPTY Lnet/minecraft/world/item/crafting/Ingredient;
      // 063: invokestatic com/mojang/serialization/DataResult.success (Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;
      // 066: astore 3
      // 067: aload 3
      // 068: goto 249
      // 06b: aload 1
      // 06c: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 06f: getstatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.all Lnet/minecraft/world/item/crafting/Ingredient;
      // 072: invokestatic com/mojang/serialization/DataResult.success (Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;
      // 075: astore 3
      // 076: aload 3
      // 077: goto 249
      // 07a: aload 1
      // 07b: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 07e: aload 1
      // 07f: invokestatic dev/latvian/mods/kubejs/util/ID.read (Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // 082: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, net/minecraft/tags/ItemTags.create (Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/tags/TagKey;, (Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/tags/TagKey; ]
      // 087: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 08c: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, net/minecraft/world/item/crafting/Ingredient.of (Lnet/minecraft/tags/TagKey;)Lnet/minecraft/world/item/crafting/Ingredient;, (Lnet/minecraft/tags/TagKey;)Lnet/minecraft/world/item/crafting/Ingredient; ]
      // 091: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 096: astore 3
      // 097: aload 3
      // 098: goto 249
      // 09b: aload 1
      // 09c: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 09f: new dev/latvian/mods/kubejs/ingredient/NamespaceIngredient
      // 0a2: dup
      // 0a3: aload 1
      // 0a4: invokevirtual com/mojang/brigadier/StringReader.readUnquotedString ()Ljava/lang/String;
      // 0a7: invokespecial dev/latvian/mods/kubejs/ingredient/NamespaceIngredient.<init> (Ljava/lang/String;)V
      // 0aa: invokevirtual dev/latvian/mods/kubejs/ingredient/NamespaceIngredient.toVanilla ()Lnet/minecraft/world/item/crafting/Ingredient;
      // 0ad: invokestatic com/mojang/serialization/DataResult.success (Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;
      // 0b0: astore 3
      // 0b1: aload 3
      // 0b2: goto 249
      // 0b5: aload 1
      // 0b6: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 0b9: aload 1
      // 0ba: invokestatic dev/latvian/mods/kubejs/util/ID.read (Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // 0bd: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.lambda$read$6 (Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/serialization/DataResult;, (Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/serialization/DataResult; ]
      // 0c2: invokeinterface com/mojang/serialization/DataResult.flatMap (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 0c7: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.lambda$read$7 (Lnet/minecraft/world/item/CreativeModeTab;)Lnet/minecraft/world/item/crafting/Ingredient;, (Lnet/minecraft/world/item/CreativeModeTab;)Lnet/minecraft/world/item/crafting/Ingredient; ]
      // 0cc: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 0d1: astore 3
      // 0d2: aload 3
      // 0d3: goto 249
      // 0d6: aload 1
      // 0d7: invokestatic dev/latvian/mods/kubejs/util/RegExpKJS.tryRead (Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // 0da: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/ingredient/RegExIngredient.<init> (Ljava/util/regex/Pattern;)V, (Ljava/util/regex/Pattern;)Ldev/latvian/mods/kubejs/ingredient/RegExIngredient; ]
      // 0df: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 0e4: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, net/neoforged/neoforge/common/crafting/ICustomIngredient.toVanilla ()Lnet/minecraft/world/item/crafting/Ingredient;, (Ldev/latvian/mods/kubejs/ingredient/RegExIngredient;)Lnet/minecraft/world/item/crafting/Ingredient; ]
      // 0e9: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 0ee: astore 3
      // 0ef: aload 3
      // 0f0: goto 249
      // 0f3: aload 1
      // 0f4: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 0f7: aload 1
      // 0f8: invokevirtual com/mojang/brigadier/StringReader.skipWhitespace ()V
      // 0fb: aload 1
      // 0fc: invokevirtual com/mojang/brigadier/StringReader.canRead ()Z
      // 0ff: ifeq 10b
      // 102: aload 1
      // 103: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 106: bipush 93
      // 108: if_icmpne 116
      // 10b: getstatic net/minecraft/world/item/crafting/Ingredient.EMPTY Lnet/minecraft/world/item/crafting/Ingredient;
      // 10e: invokestatic com/mojang/serialization/DataResult.success (Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;
      // 111: astore 3
      // 112: aload 3
      // 113: goto 249
      // 116: new java/util/ArrayList
      // 119: dup
      // 11a: bipush 2
      // 11b: invokespecial java/util/ArrayList.<init> (I)V
      // 11e: astore 4
      // 120: aload 0
      // 121: aload 1
      // 122: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.read (Ldev/latvian/mods/rhino/Context;Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // 125: astore 5
      // 127: aload 5
      // 129: invokeinterface com/mojang/serialization/DataResult.isSuccess ()Z 1
      // 12e: ifeq 144
      // 131: aload 4
      // 133: aload 5
      // 135: invokeinterface com/mojang/serialization/DataResult.getOrThrow ()Ljava/lang/Object; 1
      // 13a: checkcast net/minecraft/world/item/crafting/Ingredient
      // 13d: invokevirtual java/util/ArrayList.add (Ljava/lang/Object;)Z
      // 140: pop
      // 141: goto 153
      // 144: aload 5
      // 146: invokedynamic get (Lcom/mojang/serialization/DataResult;)Ljava/util/function/Supplier; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.lambda$read$8 (Lcom/mojang/serialization/DataResult;)Ljava/lang/String;, ()Ljava/lang/String; ]
      // 14b: invokestatic com/mojang/serialization/DataResult.error (Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;
      // 14e: astore 3
      // 14f: aload 3
      // 150: goto 249
      // 153: aload 1
      // 154: invokevirtual com/mojang/brigadier/StringReader.skipWhitespace ()V
      // 157: aload 1
      // 158: invokevirtual com/mojang/brigadier/StringReader.canRead ()Z
      // 15b: ifeq 172
      // 15e: aload 1
      // 15f: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 162: bipush 44
      // 164: if_icmpne 172
      // 167: aload 1
      // 168: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 16b: aload 1
      // 16c: invokevirtual com/mojang/brigadier/StringReader.skipWhitespace ()V
      // 16f: goto 185
      // 172: aload 1
      // 173: invokevirtual com/mojang/brigadier/StringReader.canRead ()Z
      // 176: ifeq 188
      // 179: aload 1
      // 17a: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 17d: bipush 93
      // 17f: if_icmpne 185
      // 182: goto 188
      // 185: goto 120
      // 188: aload 1
      // 189: invokevirtual com/mojang/brigadier/StringReader.canRead ()Z
      // 18c: ifeq 198
      // 18f: aload 1
      // 190: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 193: bipush 93
      // 195: if_icmpeq 1a5
      // 198: invokedynamic get ()Ljava/util/function/Supplier; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.lambda$read$9 ()Ljava/lang/String;, ()Ljava/lang/String; ]
      // 19d: invokestatic com/mojang/serialization/DataResult.error (Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;
      // 1a0: astore 3
      // 1a1: aload 3
      // 1a2: goto 249
      // 1a5: aload 1
      // 1a6: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 1a9: aload 1
      // 1aa: invokevirtual com/mojang/brigadier/StringReader.skipWhitespace ()V
      // 1ad: new net/neoforged/neoforge/common/crafting/CompoundIngredient
      // 1b0: dup
      // 1b1: aload 4
      // 1b3: invokespecial net/neoforged/neoforge/common/crafting/CompoundIngredient.<init> (Ljava/util/List;)V
      // 1b6: invokevirtual net/neoforged/neoforge/common/crafting/CompoundIngredient.toVanilla ()Lnet/minecraft/world/item/crafting/Ingredient;
      // 1b9: invokestatic com/mojang/serialization/DataResult.success (Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;
      // 1bc: astore 3
      // 1bd: aload 3
      // 1be: goto 249
      // 1c1: aload 1
      // 1c2: invokestatic dev/latvian/mods/kubejs/util/ID.read (Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // 1c5: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/plugin/builtin/wrapper/ItemWrapper.findItem (Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/serialization/DataResult;, (Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/serialization/DataResult; ]
      // 1ca: invokeinterface com/mojang/serialization/DataResult.flatMap (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 1cf: astore 4
      // 1d1: aload 1
      // 1d2: invokevirtual com/mojang/brigadier/StringReader.canRead ()Z
      // 1d5: ifeq 1df
      // 1d8: aload 1
      // 1d9: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 1dc: goto 1e0
      // 1df: bipush 0
      // 1e0: istore 5
      // 1e2: iload 5
      // 1e4: bipush 91
      // 1e6: if_icmpeq 1f0
      // 1e9: iload 5
      // 1eb: bipush 123
      // 1ed: if_icmpne 22e
      // 1f0: aload 2
      // 1f1: invokevirtual dev/latvian/mods/kubejs/util/RegistryAccessContainer.nbt ()Lnet/minecraft/resources/RegistryOps;
      // 1f4: aload 1
      // 1f5: invokestatic dev/latvian/mods/kubejs/component/DataComponentWrapper.readPredicate (Lcom/mojang/serialization/DynamicOps;Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/core/component/DataComponentPredicate;
      // 1f8: astore 6
      // 1fa: aload 6
      // 1fc: getstatic net/minecraft/core/component/DataComponentPredicate.EMPTY Lnet/minecraft/core/component/DataComponentPredicate;
      // 1ff: if_acmpeq 215
      // 202: aload 4
      // 204: aload 6
      // 206: invokedynamic apply (Lnet/minecraft/core/component/DataComponentPredicate;)Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.lambda$read$10 (Lnet/minecraft/core/component/DataComponentPredicate;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/crafting/Ingredient;, (Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/crafting/Ingredient; ]
      // 20b: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 210: astore 3
      // 211: aload 3
      // 212: goto 249
      // 215: goto 22e
      // 218: astore 6
      // 21a: aload 6
      // 21c: dup
      // 21d: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 220: pop
      // 221: invokedynamic get (Lcom/mojang/brigadier/exceptions/CommandSyntaxException;)Ljava/util/function/Supplier; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, com/mojang/brigadier/exceptions/CommandSyntaxException.getMessage ()Ljava/lang/String;, ()Ljava/lang/String; ]
      // 226: invokestatic com/mojang/serialization/DataResult.error (Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;
      // 229: astore 3
      // 22a: aload 3
      // 22b: goto 249
      // 22e: aload 4
      // 230: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, net/minecraft/core/Holder.value ()Ljava/lang/Object;, (Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/Item; ]
      // 235: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 23a: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.lambda$read$11 (Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/crafting/Ingredient;, (Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/crafting/Ingredient; ]
      // 23f: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 244: astore 3
      // 245: aload 3
      // 246: goto 249
      // 249: areturn
   }

   @Info("Checks if the passed in object is an Ingredient.\nNote that this does not mean it will not function as an Ingredient if passed to something that requests one.\n")
   static boolean isIngredient(@Nullable Object o) {
      return o instanceof Ingredient;
   }

   static ItemStack first(Ingredient ingredient) {
      return ingredient.kjs$getFirst();
   }

   @Nullable
   static TagKey<Item> tagKeyOf(Ingredient in) {
      if (!in.isCustom() && in.getValues().length == 1 && in.getValues()[0] instanceof TagValue var1) {
         TagValue var10000 = var1;

         try {
            var6 = var10000.tag();
         } catch (Throwable var5) {
            throw new MatchException(var5.toString(), var5);
         }

         return var6;
      } else {
         return null;
      }
   }

   static boolean containsAnyTag(Ingredient in) {
      if (in.isCustom()) {
         return false;
      } else {
         for (Value value : in.getValues()) {
            if (value instanceof TagValue) {
               return true;
            }
         }

         return false;
      }
   }
}
