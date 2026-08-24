package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.lang.runtime.SwitchBootstraps;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

@Info("Various item related helper methods")
public interface ItemWrapper {
   ItemStack[] EMPTY_ARRAY;
   TypeInfo ITEM_TYPE_INFO;
   TypeInfo TYPE_INFO;
   @HideFromJS
   Lazy<List<String>> CACHED_ITEM_TYPE_LIST;
   @HideFromJS
   Lazy<Map<ResourceLocation, Collection<ItemStack>>> CACHED_ITEM_MAP;
   @HideFromJS
   Lazy<List<ItemStack>> CACHED_ITEM_LIST;

   @Info("Returns an ItemStack of the input")
   static ItemStack of(ItemStack in) {
      return in;
   }

   @Info("Returns an ItemStack of the input, with the specified data components")
   static ItemStack of(ItemStack in, DataComponentMap components) {
      in.applyComponents(components);
      return in;
   }

   @Info("Returns an ItemStack of the input, with the specified count")
   static ItemStack of(ItemStack in, int count) {
      return in.kjs$withCount(count);
   }

   @Info("Returns an ItemStack of the input, with the specified count and data components")
   static ItemStack of(ItemStack in, int count, DataComponentMap components) {
      in.setCount(count);
      in.applyComponents(components);
      return in;
   }

   @HideFromJS
   private static ItemStack wrapTrivial(Context cx, @Nullable Object from) {
      while (from instanceof Wrapper) {
         Wrapper w = (Wrapper)from;
         from = w.unwrap();
      }

      Object var9 = from;
      byte var3 = 0;

      while (true) {
         ItemStack var10000;
         switch (SwitchBootstraps.typeSwitch<"typeSwitch",ItemStack,ItemLike,Ingredient,SizedIngredient,ItemLike>(var9, var3)) {
            case -1:
               var10000 = ItemStack.EMPTY;
               break;
            case 0:
               ItemStack s = (ItemStack)var9;
               var10000 = s.isEmpty() ? ItemStack.EMPTY : s;
               break;
            case 1: {
               ItemLike i = (ItemLike)var9;
               if (i.asItem() != Items.AIR) {
                  var3 = 2;
                  continue;
               }

               var10000 = ItemStack.EMPTY;
               break;
            }
            case 2: {
               Ingredient i = (Ingredient)var9;
               throw new KubeRuntimeException("Use .first of an ingredient to get its ItemStack!").source(SourceLine.of(cx));
            }
            case 3:
               SizedIngredient sized = (SizedIngredient)var9;
               throw new KubeRuntimeException("Use .ingredient.first on a sized ingredient to get its ItemStack!").source(SourceLine.of(cx));
            case 4: {
               ItemLike i = (ItemLike)var9;
               var10000 = i.asItem().getDefaultInstance();
               break;
            }
            default:
               var10000 = null;
         }

         return var10000;
      }
   }

   @HideFromJS
   static DataResult<ItemStack> wrapResult(Context cx, @Nullable Object from) {
      if (from instanceof Wrapper w) {
         from = w.unwrap();
      }

      ItemStack trivial = wrapTrivial(cx, from);
      if (trivial != null) {
         return DataResult.success(trivial);
      } else {
         RegistryAccessContainer registries = RegistryAccessContainer.of(cx);
         if (!<unrepresentable>.$assertionsDisabled && from == null) {
            throw new AssertionError();
         } else {
            return switch (from) {
               case ResourceLocation id -> findItem(id).map(Holder::value).map(Item::getDefaultInstance);
               case JsonElement json -> parseJson(cx, registries.nbt(), json);
               case StringTag tag -> wrapResult(cx, tag.getAsString());
               case CharSequence charSequence -> {
                  String os = from.toString().trim();
                  String s = os;
                  ItemStack cached = registries.itemStackParseCache().get(os);
                  if (cached != null) {
                     yield DataResult.success(cached.copy());
                  } else {
                     int spaceIndex = os.indexOf(32);
                     int count;
                     if (spaceIndex >= 2 && os.indexOf(120) == spaceIndex - 1) {
                        count = Integer.parseInt(os.substring(0, spaceIndex - 1));
                        s = os.substring(spaceIndex + 1);
                     } else {
                        count = 1;
                     }

                     yield parseString(cx, registries.nbt(), s)
                        .map(stack -> stack.kjs$withCount(count))
                        .ifSuccess(stack -> registries.itemStackParseCache().put(os, stack.copy()));
                  }
               }
               default -> {
                  Map<String, Object> map = cx.optionalMapOf(from);
                  yield map != null
                     ? ItemStack.CODEC.parse(registries.java(), map)
                     : DataResult.error(() -> "Could not parse input %s for item stack".formatted(from));
               }
            };
         }
      }
   }

   @HideFromJS
   static ItemStack wrap(Context cx, @Nullable Object from) {
      ItemStack trivial = wrapTrivial(cx, from);
      return trivial != null
         ? trivial
         : (ItemStack)wrapResult(cx, from)
            .getOrThrow(error -> new KubeRuntimeException("Failed to read item stack from %s: %s".formatted(from, error)).source(SourceLine.of(cx)));
   }

   @HideFromJS
   static Item wrapItem(Context cx, @Nullable Object o) {
      return switch (o) {
         case null -> Items.AIR;
         case ItemLike item -> item.asItem();
         case CharSequence cs -> (Item)findItem(cs.toString())
            .getOrThrow(error -> new KubeRuntimeException("Failed to read item from %s: %s".formatted(cs, error)).source(SourceLine.of(cx)));
         default -> wrap(cx, o).getItem();
      };
   }

   static DataResult<Item> findItem(String s) {
      s = s.trim();

      return switch (s) {
         case "", "-", "air", "minecraft:air" -> DataResult.success(Items.AIR);
         default -> ResourceLocation.read(s).flatMap(ItemWrapper::findItem).map(Holder::value);
      };
   }

   @HideFromJS
   static DataResult<Holder<Item>> findItem(ResourceLocation id) {
      return BuiltInRegistries.ITEM
         .getHolder(id)
         .<DataResult>map(DataResult::success)
         .orElseGet(() -> DataResult.error(() -> "Item with ID " + id + " does not exist!"))
         .map(Function.identity());
   }

   @Info("Get a list of most items in the game. Items not in a creative tab are ignored")
   static List<ItemStack> getList() {
      return CACHED_ITEM_LIST.get();
   }

   @Info("Get a list of all the item ids in the game")
   static List<String> getTypeList() {
      return CACHED_ITEM_TYPE_LIST.get();
   }

   static Map<ResourceLocation, Collection<ItemStack>> getTypeToStackMap() {
      return CACHED_ITEM_MAP.get();
   }

   static Collection<ItemStack> getVariants(ItemStack item) {
      return getTypeToStackMap().get(item.kjs$getIdLocation());
   }

   @Info("Get the item that represents air/an empty slot")
   static ItemStack getEmpty() {
      return ItemStack.EMPTY;
   }

   @Info("Returns a Firework with the input properties")
   static Fireworks fireworks(Fireworks fireworks) {
      return fireworks;
   }

   @Info("Gets an Item from an item id")
   static Item getItem(ResourceLocation id) {
      return (Item)BuiltInRegistries.ITEM.get(id);
   }

   @Info("Gets an items id from the Item")
   static ResourceLocation getId(Item item) {
      return BuiltInRegistries.ITEM.getKey(item);
   }

   @Info("Checks if the provided item id exists in the registry")
   static boolean exists(ResourceLocation id) {
      return BuiltInRegistries.ITEM.containsKey(id);
   }

   @Info("Checks if the passed in object is an ItemStack.\nNote that this does not mean it will not function as an ItemStack if passed to something that requests one.\n")
   static boolean isItem(@Nullable Object o) {
      return o instanceof ItemStack;
   }

   static ItemStack playerHead(String name) {
      ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
      stack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap()));
      return stack;
   }

   static ItemStack playerHeadFromBase64(UUID uuid, String textureBase64) {
      if (uuid == null || uuid.equals(Util.NIL_UUID)) {
         throw new IllegalArgumentException("UUID can't be null!");
      } else if (textureBase64 != null && !textureBase64.isBlank()) {
         ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
         PropertyMap properties = new PropertyMap();
         properties.put("textures", new Property("textures", textureBase64));
         stack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.empty(), Optional.of(uuid), properties));
         return stack;
      } else {
         throw new IllegalArgumentException("Texture Base 64 can't be empty!");
      }
   }

   static ItemStack playerHeadFromUrl(String url) {
      JsonObject root = new JsonObject();
      JsonObject textures = new JsonObject();
      JsonObject skin = new JsonObject();
      skin.addProperty("url", url);
      textures.add("SKIN", skin);
      root.add("textures", textures);
      byte[] bytes = JsonUtils.toString(root).getBytes(StandardCharsets.UTF_8);
      return playerHeadFromBase64(UUID.nameUUIDFromBytes(bytes), Base64.getEncoder().encodeToString(bytes));
   }

   static ItemStack playerHeadFromSkinHash(String hash) {
      return playerHeadFromUrl("https://textures.minecraft.net/texture/" + hash);
   }

   static ItemAbility wrapItemAbility(Object object) {
      if (object instanceof ItemAbility ta) {
         return ta;
      } else {
         return object != null ? ItemAbility.get(object.toString()) : null;
      }
   }

   static boolean isItemStackLike(Object from) {
      return from instanceof ItemStack || from instanceof ItemLike;
   }

   static DataResult<ItemStack> parseJson(Context cx, DynamicOps<Tag> registryOps, @Nullable JsonElement json) {
      return switch (json) {
         case null -> DataResult.success(ItemStack.EMPTY);
         case JsonNull jsonNull -> DataResult.success(ItemStack.EMPTY);
         case JsonPrimitive primitive -> parseString(cx, registryOps, primitive.getAsString());
         case JsonObject obj -> ItemStack.OPTIONAL_CODEC.decode(JsonOps.INSTANCE, obj).map(Pair::getFirst);
         default -> DataResult.error(() -> "Could not parse item stack from JSON " + json);
      };
   }

   static DataResult<ItemStack> parseString(Context cx, DynamicOps<Tag> registryOps, String s) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.main.ClassWriter.classLambdaToJava(ClassWriter.java:302)
      //
      // Bytecode:
      // 00: aload 2
      // 01: astore 3
      // 02: bipush -1
      // 03: istore 4
      // 05: aload 3
      // 06: invokevirtual java/lang/String.hashCode ()I
      // 09: lookupswitch 103 4 0 43 45 58 96586 74 1768632829 90
      // 34: aload 3
      // 35: ldc ""
      // 37: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 3a: ifeq 70
      // 3d: bipush 0
      // 3e: istore 4
      // 40: goto 70
      // 43: aload 3
      // 44: ldc_w "-"
      // 47: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 4a: ifeq 70
      // 4d: bipush 1
      // 4e: istore 4
      // 50: goto 70
      // 53: aload 3
      // 54: ldc_w "air"
      // 57: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 5a: ifeq 70
      // 5d: bipush 2
      // 5e: istore 4
      // 60: goto 70
      // 63: aload 3
      // 64: ldc_w "minecraft:air"
      // 67: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 6a: ifeq 70
      // 6d: bipush 3
      // 6e: istore 4
      // 70: iload 4
      // 72: tableswitch 43 0 3 30 30 30 30
      // 90: getstatic net/minecraft/world/item/ItemStack.EMPTY Lnet/minecraft/world/item/ItemStack;
      // 93: invokestatic com/mojang/serialization/DataResult.success (Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;
      // 96: astore 5
      // 98: aload 5
      // 9a: goto c4
      // 9d: aload 0
      // 9e: aload 1
      // 9f: new com/mojang/brigadier/StringReader
      // a2: dup
      // a3: aload 2
      // a4: invokespecial com/mojang/brigadier/StringReader.<init> (Ljava/lang/String;)V
      // a7: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/ItemWrapper.read (Ldev/latvian/mods/rhino/Context;Lcom/mojang/serialization/DynamicOps;Lcom/mojang/brigadier/StringReader;)Lcom/mojang/serialization/DataResult;
      // aa: astore 5
      // ac: aload 5
      // ae: goto c4
      // b1: astore 6
      // b3: aload 6
      // b5: invokedynamic get (Lcom/mojang/brigadier/exceptions/CommandSyntaxException;)Ljava/util/function/Supplier; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, dev/latvian/mods/kubejs/plugin/builtin/wrapper/ItemWrapper.lambda$parseString$13 (Lcom/mojang/brigadier/exceptions/CommandSyntaxException;)Ljava/lang/String;, ()Ljava/lang/String; ]
      // ba: invokestatic com/mojang/serialization/DataResult.error (Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;
      // bd: astore 5
      // bf: aload 5
      // c1: goto c4
      // c4: areturn
   }

   static DataResult<ItemStack> read(Context cx, DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
      reader.skipWhitespace();
      if (reader.canRead() && reader.peek() != '-') {
         int count;
         if (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
            count = Mth.ceil(reader.readDouble());
            reader.skipWhitespace();
            reader.expect('x');
            reader.skipWhitespace();
            if (count < 1) {
               return DataResult.error(() -> "Item count smaller than 1 is not allowed!");
            }
         } else {
            count = 1;
         }

         DataResult<ItemStack> itemStack = ID.read(reader).flatMap(ItemWrapper::findItem).map(item -> new ItemStack(item, count));
         char next = reader.canRead() ? reader.peek() : 0;
         return next != '[' && next != '{' ? itemStack : itemStack.flatMap(stack -> {
            try {
               DataComponentPatch components = DataComponentWrapper.readPatch(registryOps, reader);
               stack.applyComponents(components);
               return DataResult.success(stack);
            } catch (CommandSyntaxException var4x) {
               return DataResult.error(var4x::getMessage);
            }
         });
      } else {
         return DataResult.success(ItemStack.EMPTY);
      }
   }

   static {
      if (<unrepresentable>.$assertionsDisabled) {
      }

      EMPTY_ARRAY = new ItemStack[0];
      ITEM_TYPE_INFO = TypeInfo.of(Item.class);
      TYPE_INFO = TypeInfo.of(ItemStack.class);
      CACHED_ITEM_TYPE_LIST = Lazy.of(() -> {
         ArrayList<String> cachedItemTypeList = new ArrayList<>();

         for (Item item : BuiltInRegistries.ITEM) {
            cachedItemTypeList.add(item.kjs$getId());
         }

         return cachedItemTypeList;
      });
      CACHED_ITEM_MAP = Lazy.map(map -> {
         Set<ItemStack> stackList = ItemStackLinkedSet.createTypeAndComponentsSet();
         stackList.addAll(CreativeModeTabs.searchTab().getDisplayItems());

         for (ItemStack stack : stackList) {
            if (!stack.isEmpty()) {
               map.computeIfAbsent(stack.getItem().kjs$getIdLocation(), _rl -> ItemStackLinkedSet.createTypeAndComponentsSet()).add(stack.kjs$withCount(1));
            }
         }

         for (String itemId : CACHED_ITEM_TYPE_LIST.get()) {
            ResourceLocation itemRl = ResourceLocation.parse(itemId);
            map.computeIfAbsent(itemRl, id -> Set.of(((Item)BuiltInRegistries.ITEM.get(id)).getDefaultInstance()));
         }
      });
      CACHED_ITEM_LIST = Lazy.of(() -> CACHED_ITEM_MAP.get().values().stream().flatMap(Collection::stream).toList());
   }
}
