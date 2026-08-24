package net.mehvahdjukaar.moonlight.api.resources;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

@Deprecated(
   forRemoval = true
)
public class RecipeConverter {
   private static final Map<Class<?>, RecipeConverter> CONVERTERS = new HashMap<>();
   private final List<Field> fieldToConvert;

   private RecipeConverter(List<Field> fields) {
      this.fieldToConvert = fields;
   }

   @Nullable
   private <R, T extends BlockType> R convert(R param1, T param2, T param3, Item param4, String param5) throws IllegalAccessException {
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
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.fieldToConvert Ljava/util/List;
      // 004: invokeinterface java/util/List.iterator ()Ljava/util/Iterator; 1
      // 009: astore 6
      // 00b: aload 6
      // 00d: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 012: ifeq 235
      // 015: aload 6
      // 017: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 01c: checkcast java/lang/reflect/Field
      // 01f: astore 7
      // 021: aload 7
      // 023: aload 1
      // 024: invokevirtual java/lang/reflect/Field.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 027: astore 8
      // 029: aload 8
      // 02b: astore 9
      // 02d: bipush 0
      // 02e: istore 10
      // 030: aload 9
      // 032: iload 10
      // 034: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ java/util/List, java/util/Map, java/lang/Record, java/util/Optional ]
      // 039: tableswitch 448 -1 3 448 35 140 341 380
      // 05c: aload 9
      // 05e: checkcast java/util/List
      // 061: astore 11
      // 063: bipush 0
      // 064: istore 12
      // 066: aload 11
      // 068: invokeinterface java/util/List.listIterator ()Ljava/util/ListIterator; 1
      // 06d: astore 13
      // 06f: aload 13
      // 071: invokeinterface java/util/ListIterator.hasNext ()Z 1
      // 076: ifeq 0a0
      // 079: aload 13
      // 07b: invokeinterface java/util/ListIterator.next ()Ljava/lang/Object; 1
      // 080: astore 14
      // 082: aload 0
      // 083: aload 2
      // 084: aload 3
      // 085: aload 14
      // 087: invokevirtual net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.tryConverting (Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Ljava/lang/Object;)Ljava/lang/Object;
      // 08a: astore 15
      // 08c: aload 15
      // 08e: ifnull 09d
      // 091: bipush 1
      // 092: istore 12
      // 094: aload 13
      // 096: aload 15
      // 098: invokeinterface java/util/ListIterator.set (Ljava/lang/Object;)V 2
      // 09d: goto 06f
      // 0a0: iload 12
      // 0a2: ifne 0c2
      // 0a5: new java/lang/RuntimeException
      // 0a8: dup
      // 0a9: ldc "Failed to convert some fields for recipe %s from type %s to type %s"
      // 0ab: bipush 3
      // 0ac: anewarray 2
      // 0af: dup
      // 0b0: bipush 0
      // 0b1: aload 1
      // 0b2: aastore
      // 0b3: dup
      // 0b4: bipush 1
      // 0b5: aload 2
      // 0b6: aastore
      // 0b7: dup
      // 0b8: bipush 2
      // 0b9: aload 3
      // 0ba: aastore
      // 0bb: invokestatic java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 0be: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 0c1: athrow
      // 0c2: goto 232
      // 0c5: aload 9
      // 0c7: checkcast java/util/Map
      // 0ca: astore 12
      // 0cc: aload 12
      // 0ce: astore 13
      // 0d0: bipush 0
      // 0d1: istore 14
      // 0d3: new java/util/HashSet
      // 0d6: dup
      // 0d7: aload 13
      // 0d9: invokeinterface java/util/Map.entrySet ()Ljava/util/Set; 1
      // 0de: invokespecial java/util/HashSet.<init> (Ljava/util/Collection;)V
      // 0e1: invokevirtual java/util/HashSet.iterator ()Ljava/util/Iterator;
      // 0e4: astore 15
      // 0e6: aload 15
      // 0e8: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 0ed: ifeq 169
      // 0f0: aload 15
      // 0f2: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 0f7: checkcast java/util/Map$Entry
      // 0fa: astore 16
      // 0fc: aload 16
      // 0fe: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 103: astore 17
      // 105: aload 16
      // 107: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 10c: astore 18
      // 10e: aload 0
      // 10f: aload 2
      // 110: aload 3
      // 111: aload 17
      // 113: invokevirtual net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.tryConverting (Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Ljava/lang/Object;)Ljava/lang/Object;
      // 116: astore 19
      // 118: aload 0
      // 119: aload 2
      // 11a: aload 3
      // 11b: aload 18
      // 11d: invokevirtual net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.tryConverting (Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Ljava/lang/Object;)Ljava/lang/Object;
      // 120: astore 20
      // 122: aload 19
      // 124: ifnonnull 12c
      // 127: aload 20
      // 129: ifnull 139
      // 12c: aload 13
      // 12e: aload 17
      // 130: invokeinterface java/util/Map.remove (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 135: pop
      // 136: bipush 1
      // 137: istore 14
      // 139: aload 19
      // 13b: ifnull 157
      // 13e: aload 13
      // 140: aload 19
      // 142: aload 20
      // 144: ifnull 14c
      // 147: aload 20
      // 149: goto 14e
      // 14c: aload 18
      // 14e: invokeinterface java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 153: pop
      // 154: goto 166
      // 157: aload 20
      // 159: ifnull 166
      // 15c: aload 16
      // 15e: aload 20
      // 160: invokeinterface java/util/Map$Entry.setValue (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 165: pop
      // 166: goto 0e6
      // 169: iload 14
      // 16b: ifne 18b
      // 16e: new java/lang/RuntimeException
      // 171: dup
      // 172: ldc "Failed to convert some fields for recipe %s from type %s to type %s"
      // 174: bipush 3
      // 175: anewarray 2
      // 178: dup
      // 179: bipush 0
      // 17a: aload 1
      // 17b: aastore
      // 17c: dup
      // 17d: bipush 1
      // 17e: aload 2
      // 17f: aastore
      // 180: dup
      // 181: bipush 2
      // 182: aload 3
      // 183: aastore
      // 184: invokestatic java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 187: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 18a: athrow
      // 18b: goto 232
      // 18e: aload 9
      // 190: checkcast java/lang/Record
      // 193: astore 13
      // 195: aload 8
      // 197: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 19a: invokestatic net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.getOrCreateConverter (Ljava/lang/Class;)Lnet/mehvahdjukaar/moonlight/api/resources/RecipeConverter;
      // 19d: astore 14
      // 19f: aload 14
      // 1a1: ifnull 1b2
      // 1a4: aload 14
      // 1a6: aload 8
      // 1a8: aload 2
      // 1a9: aload 3
      // 1aa: aload 4
      // 1ac: aload 5
      // 1ae: invokevirtual net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.convert (Ljava/lang/Object;Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Lnet/minecraft/world/item/Item;Ljava/lang/String;)Ljava/lang/Object;
      // 1b1: pop
      // 1b2: goto 232
      // 1b5: aload 9
      // 1b7: checkcast java/util/Optional
      // 1ba: astore 14
      // 1bc: aload 14
      // 1be: invokevirtual java/util/Optional.isPresent ()Z
      // 1c1: ifeq 232
      // 1c4: aload 14
      // 1c6: invokevirtual java/util/Optional.get ()Ljava/lang/Object;
      // 1c9: astore 8
      // 1cb: aload 8
      // 1cd: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 1d0: invokestatic net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.getOrCreateConverter (Ljava/lang/Class;)Lnet/mehvahdjukaar/moonlight/api/resources/RecipeConverter;
      // 1d3: astore 15
      // 1d5: aload 15
      // 1d7: ifnull 1f6
      // 1da: aload 7
      // 1dc: bipush 1
      // 1dd: invokevirtual java/lang/reflect/Field.setAccessible (Z)V
      // 1e0: aload 7
      // 1e2: aload 1
      // 1e3: aload 15
      // 1e5: aload 8
      // 1e7: aload 2
      // 1e8: aload 3
      // 1e9: aload 4
      // 1eb: aload 5
      // 1ed: invokevirtual net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.convert (Ljava/lang/Object;Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Lnet/minecraft/world/item/Item;Ljava/lang/String;)Ljava/lang/Object;
      // 1f0: invokestatic java/util/Optional.of (Ljava/lang/Object;)Ljava/util/Optional;
      // 1f3: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 1f6: goto 232
      // 1f9: aload 0
      // 1fa: aload 2
      // 1fb: aload 3
      // 1fc: aload 8
      // 1fe: invokevirtual net/mehvahdjukaar/moonlight/api/resources/RecipeConverter.tryConverting (Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Lnet/mehvahdjukaar/moonlight/api/set/BlockType;Ljava/lang/Object;)Ljava/lang/Object;
      // 201: astore 15
      // 203: aload 15
      // 205: ifnonnull 22a
      // 208: new java/lang/RuntimeException
      // 20b: dup
      // 20c: ldc "Failed to convert item %s for recipe %s from type %s to type %s"
      // 20e: bipush 4
      // 20f: anewarray 2
      // 212: dup
      // 213: bipush 0
      // 214: aload 8
      // 216: aastore
      // 217: dup
      // 218: bipush 1
      // 219: aload 1
      // 21a: aastore
      // 21b: dup
      // 21c: bipush 2
      // 21d: aload 2
      // 21e: aastore
      // 21f: dup
      // 220: bipush 3
      // 221: aload 3
      // 222: aastore
      // 223: invokestatic java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 226: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 229: athrow
      // 22a: aload 7
      // 22c: aload 1
      // 22d: aload 15
      // 22f: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 232: goto 00b
      // 235: aload 1
      // 236: areturn
   }

   @Nullable
   private <V, T extends BlockType> V tryConverting(T originalMat, T destinationMat, V value) {
      if (value instanceof ItemStack stack) {
         Item item = BlockType.changeItemType(stack.getItem(), originalMat, destinationMat);
         return (V)(item == null ? null : item.getDefaultInstance());
      } else if (value instanceof Item il) {
         return (V)BlockType.changeItemType(il, originalMat, destinationMat);
      } else {
         return (V)(value instanceof Ingredient ing ? this.convertIngredients(originalMat, destinationMat, ing) : null);
      }
   }

   @Nullable
   private <T extends BlockType> Ingredient convertIngredients(T originalMat, T destinationMat, Ingredient ing) {
      for (ItemStack in : ing.getItems()) {
         Item it = in.getItem();
         if (it != Items.BARRIER) {
            ItemLike i = BlockType.changeItemType(it, originalMat, destinationMat);
            if (i != null) {
               return Ingredient.of(new ItemLike[]{i});
            }
         }
      }

      return null;
   }

   @Nullable
   public static <T extends BlockType, R extends Recipe<?>> R createSimilar(R recipe, T originalMat, T destinationMat, Item unlockItem, @Nullable String id) {
      recipe = (R)RPUtils.readRecipe(RPUtils.writeRecipe(recipe));
      Class<?> clazz = recipe.getClass();
      RecipeConverter conv = getOrCreateConverter(clazz);
      if (conv == null) {
         throw new RuntimeException("Failed to convert recipe of class " + clazz);
      } else {
         try {
            return conv.convert(recipe, originalMat, destinationMat, unlockItem, id);
         } catch (Exception var8) {
            Moonlight.LOGGER.error("Recipe conversion error: {}", var8.getMessage());
            return null;
         }
      }
   }

   @Nullable
   private static RecipeConverter getOrCreateConverter(Class<?> clazz) {
      return CONVERTERS.computeIfAbsent(clazz, c -> {
         try {
            List<Field> fields = findFieldsByType(clazz, ItemStack.class, Item.class, Ingredient.class, Record.class);
            fields.forEach(f -> f.setAccessible(true));
            return new RecipeConverter(fields);
         } catch (Exception var3) {
            return null;
         }
      });
   }

   private static List<Field> findFieldsByType(Class<?> clazz, Class<?>... targetTypes) {
      List<Field> foundFields = new ArrayList<>();

      for (Field field : clazz.getDeclaredFields()) {
         field.setAccessible(true);
         Class<?> fieldType = field.getType();

         for (Class<?> targetType : targetTypes) {
            if (targetType.isAssignableFrom(fieldType)) {
               foundFields.add(field);
               break;
            }

            if (List.class.isAssignableFrom(fieldType)) {
               ParameterizedType listType = (ParameterizedType)field.getGenericType();
               Class<?> listElementType = (Class<?>)listType.getActualTypeArguments()[0];
               if (listElementType.equals(targetType)) {
                  foundFields.add(field);
                  break;
               }
            } else if (Map.class.isAssignableFrom(fieldType)) {
               ParameterizedType mapType = (ParameterizedType)field.getGenericType();
               Class<?> mapKeyType = (Class<?>)mapType.getActualTypeArguments()[0];
               Class<?> mapValueType = (Class<?>)mapType.getActualTypeArguments()[1];
               if (targetType.isAssignableFrom(mapKeyType) || targetType.isAssignableFrom(mapValueType)) {
                  foundFields.add(field);
                  break;
               }
            }
         }
      }

      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null) {
         foundFields.addAll(findFieldsByType(superClass, targetTypes));
      }

      return foundFields;
   }
}
