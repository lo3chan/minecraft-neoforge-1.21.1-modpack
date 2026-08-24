package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.block.BlockModificationKubeEvent;
import dev.latvian.mods.kubejs.item.ItemModificationKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeUtils;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

public class UtilsJS {
   public static final RandomSource RANDOM = RandomSource.create();
   public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
   public static final Predicate<Object> ALWAYS_TRUE = o -> true;

   @Nullable
   public static Object wrap(@Nullable Object o, JSObjectType type) {
      if (o != null
         && !(o instanceof WrappedJS)
         && !(o instanceof Number)
         && !(o instanceof Character)
         && !(o instanceof String)
         && !(o instanceof Enum)
         && (!o.getClass().isPrimitive() || o.getClass().isArray())) {
         if (o instanceof CharSequence || o instanceof ResourceLocation) {
            return o.toString();
         } else if (o instanceof EndTag || o instanceof JsonNull) {
            return null;
         } else if (o instanceof Wrapper w) {
            return wrap(w.unwrap(), type);
         } else if (o instanceof NumericTag tag) {
            return tag.getAsNumber();
         } else if (o instanceof StringTag tag) {
            return tag.getAsString();
         } else if (o instanceof Tag) {
            return o;
         } else if (o instanceof Map) {
            return o;
         } else if (o instanceof Iterable<?> itr) {
            if (!type.checkList()) {
               return null;
            } else {
               ArrayList<Object> list = new ArrayList<>();

               for (Object o1 : itr) {
                  list.add(o1);
               }

               return list;
            }
         } else if (o.getClass().isArray()) {
            return type.checkList() ? ListJS.ofArray(o) : null;
         } else if (o instanceof JsonPrimitive json) {
            return JsonIO.toPrimitive(json);
         } else if (!(o instanceof JsonObject json)) {
            return o;
         } else if (!type.checkMap()) {
            return null;
         } else {
            HashMap<String, Object> map = new HashMap<>(json.size());

            for (Entry<String, JsonElement> entry : json.entrySet()) {
               map.put(entry.getKey(), entry.getValue());
            }

            return map;
         }
      } else {
         return o;
      }
   }

   public static <T> Predicate<T> onMatchDo(Predicate<T> predicate, Consumer<T> onMatch) {
      return t -> {
         boolean match = predicate.test(t);
         if (match) {
            onMatch.accept(t);
         }

         return match;
      };
   }

   public static void postModificationEvents() {
      BlockEvents.MODIFICATION.post(ScriptType.STARTUP, new BlockModificationKubeEvent());
      ItemEvents.MODIFICATION.post(ScriptType.STARTUP, new ItemModificationKubeEvent());
   }

   public static String toMappedTypeString(Type type) {
      if (type instanceof Class<?> clz) {
         String mapped = clz.getName();
         int ld = mapped.lastIndexOf(46);
         return ld == -1 ? mapped : mapped.substring(ld + 1);
      } else if (type instanceof ParameterizedType paramType) {
         StringBuilder sb = new StringBuilder();
         Type owner = paramType.getOwnerType();
         if (owner != null) {
            sb.append(toMappedTypeString(owner));
            sb.append('.');
         }

         sb.append(toMappedTypeString(TypeUtils.getRawType(paramType)));
         Type[] args = paramType.getActualTypeArguments();
         if (args.length > 0) {
            sb.append('<');

            for (int i = 0; i < args.length; i++) {
               if (i > 0) {
                  sb.append(", ");
               }

               sb.append(toMappedTypeString(args[i]));
            }

            sb.append('>');
         }

         return sb.toString();
      } else if (type instanceof GenericArrayType arrType) {
         return toMappedTypeString(arrType.getGenericComponentType()) + "[]";
      } else if (type instanceof TypeVariable<?> typeVar) {
         StringBuilder sbx = new StringBuilder(typeVar.getName());
         Type[] bounds = typeVar.getBounds();
         if (bounds.length > 0 && (bounds.length != 1 || !Object.class.equals(bounds[0]))) {
            sbx.append(" extends ");

            for (int i = 0; i < bounds.length; i++) {
               if (i > 0) {
                  sbx.append(" & ");
               }

               sbx.append(toMappedTypeString(bounds[i]));
            }
         }

         return sbx.toString();
      } else if (!(type instanceof WildcardType wildcard)) {
         String className = type == null ? "null" : type.getClass().getName();
         throw new IllegalArgumentException(
            "Expected a Class, ParameterizedType, GenericArrayType, TypeVariable or WildcardType, but <" + type + "> is of type " + className
         );
      } else {
         StringBuilder sbx = new StringBuilder().append("?");
         Type[] lowerBounds = wildcard.getLowerBounds();
         Type[] upperBounds = wildcard.getUpperBounds();
         if (lowerBounds.length > 1 || lowerBounds.length == 1 && lowerBounds[0] != null) {
            sbx.append(" super ");

            for (int i = 0; i < lowerBounds.length; i++) {
               if (i > 0) {
                  sbx.append(" & ");
               }

               sbx.append(toMappedTypeString(lowerBounds[i]));
            }
         } else if (upperBounds.length > 1 || upperBounds.length == 1 && !Object.class.equals(upperBounds[0])) {
            sbx.append(" extends ");

            for (int i = 0; i < upperBounds.length; i++) {
               if (i > 0) {
                  sbx.append(" & ");
               }

               sbx.append(toMappedTypeString(upperBounds[i]));
            }
         }

         return sbx.toString();
      }
   }

   @Nullable
   public static CreativeModeTab findCreativeTab(ResourceLocation id) {
      return (CreativeModeTab)BuiltInRegistries.CREATIVE_MODE_TAB.get(id);
   }
}
