package dev.latvian.mods.kubejs.fluid;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface FluidWrapper {
   TypeInfo TYPE_INFO = TypeInfo.of(FluidStack.class);
   TypeInfo FLUID_TYPE_INFO = TypeInfo.of(Fluid.class);
   TypeInfo INGREDIENT_TYPE_INFO = TypeInfo.of(FluidIngredient.class);
   TypeInfo SIZED_INGREDIENT_TYPE_INFO = TypeInfo.of(SizedFluidIngredient.class);
   SizedFluidIngredient EMPTY_SIZED = new SizedFluidIngredient(FluidIngredient.empty(), 1000);
   DataResult<FluidStack> EMPTY_STACK_RESULT = DataResult.success(FluidStack.EMPTY);
   DataResult<FluidIngredient> EMPTY_INGREDIENT_RESULT = DataResult.success(FluidIngredient.empty());
   DataResult<SizedFluidIngredient> EMPTY_SIZED_RESULT = DataResult.success(EMPTY_SIZED);

   @HideFromJS
   static DataResult<FluidStack> tryWrap(Context cx, Object from) {
      while (from instanceof Wrapper) {
         Wrapper w = (Wrapper)from;
         from = w.unwrap();
      }

      Object var9 = from;
      byte var3 = 0;

      while (true) {
         DataResult var10000;
         switch (SwitchBootstraps.typeSwitch<"typeSwitch",FluidStack,Fluid,Fluid,FluidIngredient,SizedFluidIngredient>(var9, var3)) {
            case -1:
               var10000 = EMPTY_STACK_RESULT;
               break;
            case 0:
               FluidStack s = (FluidStack)var9;
               var10000 = s.isEmpty() ? EMPTY_STACK_RESULT : DataResult.success(s);
               break;
            case 1: {
               Fluid fluid = (Fluid)var9;
               if (!fluid.kjs$isEmpty()) {
                  var3 = 2;
                  continue;
               }

               var10000 = EMPTY_STACK_RESULT;
               break;
            }
            case 2: {
               Fluid fluid = (Fluid)var9;
               var10000 = DataResult.success(new FluidStack(fluid, 1000));
               break;
            }
            case 3:
               FluidIngredient i = (FluidIngredient)var9;
               throw new KubeRuntimeException("Using FluidIngredient in places where FluidStack is expected is dangerous and unsupported!")
                  .source(SourceLine.of(cx));
            case 4:
               SizedFluidIngredient sized = (SizedFluidIngredient)var9;
               throw new KubeRuntimeException("Using SizedFluidIngredient in places where FluidStack is expected is dangerous and unsupported!")
                  .source(SourceLine.of(cx));
            default:
               var10000 = parseString(cx, RegistryAccessContainer.of(cx).nbt(), from.toString());
         }

         return var10000;
      }
   }

   @HideFromJS
   static FluidStack wrap(Context cx, Object from) {
      return (FluidStack)tryWrap(cx, from)
         .getOrThrow(error -> new KubeRuntimeException("Failed to read FluidStack from %s: %s".formatted(from, error)).source(SourceLine.of(cx)));
   }

   static FluidIngredient ingredientOf(FluidIngredient of) {
      return of;
   }

   @Info("Returns an ingredient that accepts the given set of fluids under the given component filter.")
   static FluidIngredient ingredientOf(HolderSet<Fluid> base, DataComponentMap data) {
      return ingredientOf(base, data, false);
   }

   @Info("Returns an ingredient that accepts the given set of items under the given (optionally strict) component filter.")
   static FluidIngredient ingredientOf(HolderSet<Fluid> base, DataComponentMap data, boolean strict) {
      return DataComponentFluidIngredient.of(strict, data, base);
   }

   @HideFromJS
   static DataResult<FluidIngredient> tryWrapIngredient(Context cx, Object from) {
      while (from instanceof Wrapper) {
         Wrapper w = (Wrapper)from;
         from = w.unwrap();
      }

      RegistryAccessContainer registries = RegistryAccessContainer.of(cx);

      return switch (from) {
         case null -> EMPTY_INGREDIENT_RESULT;
         case FluidStack stack when stack.isEmpty() -> EMPTY_INGREDIENT_RESULT;
         case Fluid fluid when fluid.kjs$isEmpty() -> EMPTY_INGREDIENT_RESULT;
         case FluidIngredient in when in.isEmpty() -> EMPTY_INGREDIENT_RESULT;
         case FluidStack stackx -> DataResult.success(FluidIngredient.of(new FluidStack[]{stackx}));
         case Fluid fluidx -> DataResult.success(FluidIngredient.of(new Fluid[]{fluidx}));
         case FluidIngredient inx -> DataResult.success(inx);
         case SizedFluidIngredient s -> DataResult.success(s.ingredient());
         default -> ingredientOfString(cx, registries.nbt(), from.toString());
      };
   }

   @HideFromJS
   static FluidIngredient wrapIngredient(Context cx, Object from) {
      return (FluidIngredient)tryWrapIngredient(cx, from)
         .getOrThrow(error -> new KubeRuntimeException("Failed to read FluidIngredient from %s: %s".formatted(from, error)).source(SourceLine.of(cx)));
   }

   static SizedFluidIngredient sizedIngredientOf(SizedFluidIngredient of) {
      return of;
   }

   static SizedFluidIngredient sizedIngredientOf(FluidIngredient in, int amount) {
      return new SizedFluidIngredient(in, amount);
   }

   @HideFromJS
   static DataResult<SizedFluidIngredient> tryWrapSizedIngredient(Context cx, Object o) {
      RegistryAccessContainer registries = RegistryAccessContainer.of(cx);

      return switch (o) {
         case null -> EMPTY_SIZED_RESULT;
         case FluidStack stack when stack.isEmpty() -> EMPTY_SIZED_RESULT;
         case Fluid fluid when fluid.kjs$isEmpty() -> EMPTY_SIZED_RESULT;
         case FluidIngredient in when in.isEmpty() -> EMPTY_SIZED_RESULT;
         case FluidStack stackx -> DataResult.success(SizedFluidIngredient.of(stackx));
         case Fluid fluidx -> DataResult.success(SizedFluidIngredient.of(fluidx, 1000));
         case FluidIngredient inx -> DataResult.success(new SizedFluidIngredient(inx, 1000));
         case SizedFluidIngredient s -> DataResult.success(s);
         default -> sizedIngredientOfString(cx, registries.nbt(), o.toString());
      };
   }

   @HideFromJS
   static SizedFluidIngredient wrapSizedIngredient(Context cx, Object from) {
      return (SizedFluidIngredient)tryWrapSizedIngredient(cx, from)
         .getOrThrow(error -> new KubeRuntimeException("Failed to read SizedFluidIngredient from %s: %s".formatted(from, error)).source(SourceLine.of(cx)));
   }

   @Info("Returns a FluidStack of the input")
   static FluidStack of(FluidStack o) {
      return o;
   }

   @Info("Returns a FluidStack of the input, with the specified amount")
   static FluidStack of(FluidStack o, int amount) {
      o.setAmount(amount);
      return o;
   }

   @Info("Returns a FluidStack of the input, with the specified data components")
   static FluidStack of(FluidStack o, DataComponentMap components) {
      o.applyComponents(components);
      return o;
   }

   @Info("Returns a FluidStack of the input, with the specified amount and data components")
   static FluidStack of(FluidStack o, int amount, DataComponentMap components) {
      o.setAmount(amount);
      o.applyComponents(components);
      return o;
   }

   static FluidStack water() {
      return water(1000);
   }

   static FluidStack lava() {
      return lava(1000);
   }

   static FluidStack water(int amount) {
      return new FluidStack(Fluids.WATER, amount);
   }

   static FluidStack lava(int amount) {
      return new FluidStack(Fluids.LAVA, amount);
   }

   static Fluid getType(ResourceLocation id) {
      return (Fluid)BuiltInRegistries.FLUID.get(id);
   }

   static List<String> getTypes() {
      ArrayList<String> types = new ArrayList<>();

      for (Fluid fluid : BuiltInRegistries.FLUID) {
         types.add(fluid.kjs$getId());
      }

      return types;
   }

   static FluidStack getEmpty() {
      return FluidStack.EMPTY;
   }

   static boolean exists(ResourceLocation id) {
      return BuiltInRegistries.FLUID.containsKey(id);
   }

   static ResourceLocation getId(Fluid fluid) {
      return BuiltInRegistries.FLUID.getKey(fluid);
   }

   static <T> DataResult<T> readWithContext(Context cx, DynamicOps<Tag> registryOps, String s, FluidWrapper.ReadFn<T> fn, String name) {
      try {
         StringReader reader = new StringReader(s);
         reader.skipWhitespace();
         return fn.read(registryOps, reader);
      } catch (CommandSyntaxException var6) {
         return DataResult.error(() -> "Error parsing %s from string: %s".formatted(name, var6));
      }
   }

   static DataResult<FluidStack> parseString(Context cx, DynamicOps<Tag> registryOps, String s) {
      return switch (s) {
         case "", "-", "empty", "minecraft:empty" -> DataResult.success(FluidStack.EMPTY);
         default -> readWithContext(cx, registryOps, s, FluidWrapper::read, "FluidStack");
      };
   }

   static DataResult<FluidStack> read(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() != '-') {
         DataResult<Integer> amount = readFluidAmount(reader);
         DataResult<Holder<Fluid>> fluid = ID.read(reader).flatMap(FluidWrapper::findFluid);
         DataResult<FluidStack> fluidStack = fluid.apply2(FluidStack::new, amount);
         char next = reader.canRead() ? reader.peek() : 0;
         return next != '[' && next != '{' ? fluidStack : fluidStack.flatMap(stack -> {
            try {
               DataComponentPatch components = DataComponentWrapper.readPatch(registryOps, reader);
               stack.applyComponents(components);
               return DataResult.success(stack);
            } catch (CommandSyntaxException var4x) {
               return DataResult.error(var4x::getMessage);
            }
         });
      } else {
         return DataResult.success(FluidStack.EMPTY);
      }
   }

   static DataResult<FluidIngredient> ingredientOfString(Context cx, DynamicOps<Tag> registryOps, String s) {
      return switch (s) {
         case "", "-", "empty", "minecraft:empty" -> EMPTY_INGREDIENT_RESULT;
         default -> readWithContext(cx, registryOps, s, FluidWrapper::readIngredient, "FluidIngredient");
      };
   }

   @HideFromJS
   static DataResult<FluidIngredient> readIngredient(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.main.ClassWriter.classLambdaToJava(ClassWriter.java:302)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual com/mojang/brigadier/StringReader.canRead ()Z
      // 004: ifeq 010
      // 007: aload 1
      // 008: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 00b: bipush 45
      // 00d: if_icmpne 014
      // 010: getstatic dev/latvian/mods/kubejs/fluid/FluidWrapper.EMPTY_INGREDIENT_RESULT Lcom/mojang/serialization/DataResult;
      // 013: areturn
      // 014: aload 1
      // 015: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 018: lookupswitch 113 3 35 36 47 94 64 69
      // 03c: aload 1
      // 03d: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 040: aload 1
      // 041: invokestatic dev/latvian/mods/kubejs/util/ID.read (Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // 044: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, net/minecraft/tags/FluidTags.create (Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/tags/TagKey;, (Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/tags/TagKey; ]
      // 049: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 04e: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, net/neoforged/neoforge/fluids/crafting/FluidIngredient.tag (Lnet/minecraft/tags/TagKey;)Lnet/neoforged/neoforge/fluids/crafting/FluidIngredient;, (Lnet/minecraft/tags/TagKey;)Lnet/neoforged/neoforge/fluids/crafting/FluidIngredient; ]
      // 053: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 058: astore 2
      // 059: aload 2
      // 05a: goto 101
      // 05d: aload 1
      // 05e: invokevirtual com/mojang/brigadier/StringReader.skip ()V
      // 061: aload 1
      // 062: invokevirtual com/mojang/brigadier/StringReader.readUnquotedString ()Ljava/lang/String;
      // 065: astore 3
      // 066: new dev/latvian/mods/kubejs/fluid/NamespaceFluidIngredient
      // 069: dup
      // 06a: aload 3
      // 06b: invokespecial dev/latvian/mods/kubejs/fluid/NamespaceFluidIngredient.<init> (Ljava/lang/String;)V
      // 06e: invokestatic com/mojang/serialization/DataResult.success (Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;
      // 071: astore 2
      // 072: aload 2
      // 073: goto 101
      // 076: aload 1
      // 077: invokestatic dev/latvian/mods/kubejs/util/RegExpKJS.tryRead (Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // 07a: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/fluid/RegExFluidIngredient.<init> (Ljava/util/regex/Pattern;)V, (Ljava/util/regex/Pattern;)Lnet/neoforged/neoforge/fluids/crafting/FluidIngredient; ]
      // 07f: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 084: astore 2
      // 085: aload 2
      // 086: goto 101
      // 089: aload 1
      // 08a: invokestatic dev/latvian/mods/kubejs/util/ID.read (Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // 08d: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/fluid/FluidWrapper.findFluid (Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/serialization/DataResult;, (Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/serialization/DataResult; ]
      // 092: invokeinterface com/mojang/serialization/DataResult.flatMap (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 097: astore 3
      // 098: aload 1
      // 099: invokevirtual com/mojang/brigadier/StringReader.canRead ()Z
      // 09c: ifeq 0a6
      // 09f: aload 1
      // 0a0: invokevirtual com/mojang/brigadier/StringReader.peek ()C
      // 0a3: goto 0a7
      // 0a6: bipush 0
      // 0a7: istore 4
      // 0a9: iload 4
      // 0ab: bipush 91
      // 0ad: if_icmpeq 0b7
      // 0b0: iload 4
      // 0b2: bipush 123
      // 0b4: if_icmpne 0f1
      // 0b7: aload 0
      // 0b8: aload 1
      // 0b9: invokestatic dev/latvian/mods/kubejs/component/DataComponentWrapper.readPredicate (Lcom/mojang/serialization/DynamicOps;Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/core/component/DataComponentPredicate;
      // 0bc: astore 5
      // 0be: aload 5
      // 0c0: getstatic net/minecraft/core/component/DataComponentPredicate.EMPTY Lnet/minecraft/core/component/DataComponentPredicate;
      // 0c3: if_acmpeq 0d8
      // 0c6: aload 3
      // 0c7: aload 5
      // 0c9: invokedynamic apply (Lnet/minecraft/core/component/DataComponentPredicate;)Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, dev/latvian/mods/kubejs/fluid/FluidWrapper.lambda$readIngredient$5 (Lnet/minecraft/core/component/DataComponentPredicate;Lnet/minecraft/core/Holder;)Lnet/neoforged/neoforge/fluids/crafting/FluidIngredient;, (Lnet/minecraft/core/Holder;)Lnet/neoforged/neoforge/fluids/crafting/FluidIngredient; ]
      // 0ce: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 0d3: astore 2
      // 0d4: aload 2
      // 0d5: goto 101
      // 0d8: goto 0f1
      // 0db: astore 5
      // 0dd: aload 5
      // 0df: dup
      // 0e0: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 0e3: pop
      // 0e4: invokedynamic get (Lcom/mojang/brigadier/exceptions/CommandSyntaxException;)Ljava/util/function/Supplier; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, com/mojang/brigadier/exceptions/CommandSyntaxException.getMessage ()Ljava/lang/String;, ()Ljava/lang/String; ]
      // 0e9: invokestatic com/mojang/serialization/DataResult.error (Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;
      // 0ec: astore 2
      // 0ed: aload 2
      // 0ee: goto 101
      // 0f1: aload 3
      // 0f2: invokedynamic apply ()Ljava/util/function/Function; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, net/neoforged/neoforge/fluids/crafting/FluidIngredient.single (Lnet/minecraft/core/Holder;)Lnet/neoforged/neoforge/fluids/crafting/FluidIngredient;, (Lnet/minecraft/core/Holder;)Lnet/neoforged/neoforge/fluids/crafting/FluidIngredient; ]
      // 0f7: invokeinterface com/mojang/serialization/DataResult.map (Ljava/util/function/Function;)Lcom/mojang/serialization/DataResult; 2
      // 0fc: astore 2
      // 0fd: aload 2
      // 0fe: goto 101
      // 101: areturn
   }

   static DataResult<SizedFluidIngredient> sizedIngredientOfString(Context cx, DynamicOps<Tag> registryOps, String s) {
      return switch (s) {
         case "", "-", "empty", "minecraft:empty" -> EMPTY_SIZED_RESULT;
         default -> readWithContext(cx, registryOps, s, FluidWrapper::readSizedIngredient, "SizedFluidIngredient");
      };
   }

   static DataResult<SizedFluidIngredient> readSizedIngredient(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
      if (!reader.canRead()) {
         return EMPTY_SIZED_RESULT;
      } else {
         DataResult<Integer> amount = readFluidAmount(reader);
         DataResult<FluidIngredient> ingredient = readIngredient(registryOps, reader);
         return ingredient.apply2(SizedFluidIngredient::new, amount);
      }
   }

   @HideFromJS
   static DataResult<Holder<Fluid>> findFluid(ResourceLocation id) {
      return BuiltInRegistries.FLUID
         .getHolder(id)
         .<DataResult>map(DataResult::success)
         .orElseGet(() -> DataResult.error(() -> "Fluid with ID " + id + " does not exist!"))
         .map(Function.identity());
   }

   @HideFromJS
   static DataResult<Integer> readFluidAmount(StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
         double amountd = reader.readDouble();
         reader.skipWhitespace();
         if (reader.canRead() && (reader.peek() == 'b' || reader.peek() == 'B')) {
            reader.skip();
            reader.skipWhitespace();
            amountd *= 1000.0;
         }

         if (reader.canRead() && reader.peek() == '/') {
            reader.skip();
            reader.skipWhitespace();
            amountd /= reader.readDouble();
         }

         int amount = (int)amountd;
         reader.expect('x');
         reader.skipWhitespace();
         return amount < 1 ? DataResult.error(() -> "Fluid amount smaller than 1 is not allowed!") : DataResult.success(amount);
      } else {
         return DataResult.success(1000);
      }
   }

   public interface ReadFn<T> {
      DataResult<T> read(DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException;
   }
}
