package dev.corgitaco.dataanchor.data;

import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public interface TrackedData<T> extends Supplier<T> {
   @Nullable
   default CompoundTag save() {
      try {
         return (CompoundTag)toTag(this);
      } catch (IllegalAccessException var2) {
         throw new RuntimeException(var2);
      }
   }

   default void load(CompoundTag tag) {
      try {
         updateExistingFromTag(tag, this);
      } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   static Tag toTag(Object obj) throws IllegalAccessException {
      if (obj instanceof Integer) {
         return IntTag.valueOf((Integer)obj);
      } else if (obj instanceof Boolean) {
         return ByteTag.valueOf((byte)((Boolean)obj ? 1 : 0));
      } else if (obj instanceof Long) {
         return LongTag.valueOf((Long)obj);
      } else if (obj instanceof Float) {
         return FloatTag.valueOf((Float)obj);
      } else if (obj instanceof Double) {
         return DoubleTag.valueOf((Double)obj);
      } else if (obj instanceof Byte) {
         return ByteTag.valueOf((Byte)obj);
      } else if (obj instanceof Short) {
         return ShortTag.valueOf((Short)obj);
      } else if (obj instanceof String) {
         return StringTag.valueOf((String)obj);
      } else if (obj instanceof int[]) {
         return new IntArrayTag((int[])obj);
      } else if (obj instanceof long[]) {
         return new LongArrayTag((long[])obj);
      } else if (obj instanceof byte[]) {
         return new ByteArrayTag((byte[])obj);
      } else if (obj instanceof float[] floats) {
         ListTag list = new ListTag();

         for (float aFloat : floats) {
            list.add(FloatTag.valueOf(aFloat));
         }

         return list;
      } else if (obj instanceof double[]) {
         ListTag list = new ListTag();

         for (double aDouble : (double[])obj) {
            list.add(DoubleTag.valueOf(aDouble));
         }

         return list;
      } else if (obj instanceof boolean[]) {
         ListTag list = new ListTag();

         for (boolean b : (boolean[])obj) {
            list.add(ByteTag.valueOf((byte)(b ? 1 : 0)));
         }

         return list;
      } else if (obj instanceof short[]) {
         ListTag list = new ListTag();

         for (short s : (short[])obj) {
            list.add(ShortTag.valueOf(s));
         }

         return list;
      } else if (obj instanceof char[]) {
         return StringTag.valueOf(new String((char[])obj));
      } else if (obj instanceof Collection) {
         ListTag listTag = new ListTag();

         for (Object o : (Collection)obj) {
            listTag.add(toTag(o));
         }

         return listTag;
      } else if (!(obj instanceof Map)) {
         if (obj instanceof Enum) {
            return StringTag.valueOf(((Enum)obj).name());
         } else {
            CompoundTag tag = new CompoundTag();

            for (Field declaredField : obj.getClass().getDeclaredFields()) {
               int modifiers = declaredField.getModifiers();
               if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                  declaredField.setAccessible(true);
                  Object obj1 = declaredField.get(obj);
                  if (obj1 != null) {
                     tag.put(declaredField.getName(), toTag(obj1));
                  }
               }
            }

            return tag;
         }
      } else {
         CompoundTag compoundTag = new CompoundTag();

         for (Entry<?, ?> entry : ((Map)obj).entrySet()) {
            compoundTag.put(entry.getKey().toString(), toTag(entry.getValue()));
         }

         return compoundTag;
      }
   }

   static <T> T updateExistingFromTag(Tag tag, T obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
      if (tag instanceof CompoundTag compoundTag) {
         for (Field declaredField : obj.getClass().getDeclaredFields()) {
            int modifiers = declaredField.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
               declaredField.setAccessible(true);
               String name = declaredField.getName();
               if (declaredField.isAnnotationPresent(SerializedName.class)) {
                  SerializedName annotation = declaredField.getAnnotation(SerializedName.class);
                  name = annotation.value();
                  if (!compoundTag.contains(name) && annotation.alternate().length > 0) {
                     for (String s : annotation.alternate()) {
                        if (compoundTag.contains(s)) {
                           name = s;
                           break;
                        }
                     }
                  }
               }

               Class<?> declaringClass = declaredField.getType();
               if (declaringClass.isPrimitive()) {
                  if (declaringClass == boolean.class || declaringClass == Boolean.class) {
                     declaredField.set(obj, compoundTag.getBoolean(name));
                  }

                  if (declaringClass == int.class || declaringClass == Integer.class) {
                     declaredField.set(obj, compoundTag.getInt(name));
                  }

                  if (declaringClass == long.class || declaringClass == Long.class) {
                     declaredField.set(obj, compoundTag.getLong(name));
                  }

                  if (declaringClass == float.class || declaringClass == Float.class) {
                     declaredField.set(obj, compoundTag.getFloat(name));
                  }

                  if (declaringClass == double.class || declaringClass == Double.class) {
                     declaredField.set(obj, compoundTag.getDouble(name));
                  }

                  if (declaringClass == byte.class || declaringClass == Byte.class) {
                     declaredField.set(obj, compoundTag.getByte(name));
                  }

                  if (declaringClass == short.class || declaringClass == Short.class) {
                     declaredField.set(obj, compoundTag.getShort(name));
                  }

                  if (declaringClass == char.class || declaringClass == Character.class) {
                     declaredField.set(obj, (char)compoundTag.getInt(name));
                  }
               }

               if (declaringClass == String.class) {
                  declaredField.set(obj, compoundTag.getString(name));
               }

               if (declaringClass.isInstance(Collection.class)) {
                  if (declaredField.get(obj) instanceof Collection<?> collection) {
                     collection.clear();

                     for (Tag tag1 : compoundTag.getList(name, compoundTag.getId())) {
                        ((Collection<Object>)collection).add(fromTag(tag1, null));
                     }
                  }

                  declaredField.set(obj, compoundTag.getList(name, compoundTag.getId()));
               }

               if (declaredField.getType().isEnum()) {
                  declaredField.set(obj, Enum.valueOf(declaredField.getType(), compoundTag.getString(name)));
               }

               if (declaredField.getType().isArray()) {
                  if (declaringClass == int[].class) {
                     declaredField.set(obj, compoundTag.getIntArray(name));
                  }

                  if (declaringClass == long[].class) {
                     declaredField.set(obj, compoundTag.getLongArray(name));
                  }

                  if (declaringClass == byte[].class) {
                     declaredField.set(obj, compoundTag.getByteArray(name));
                  }

                  if (declaringClass == float[].class) {
                     ListTag list = compoundTag.getList(name, 5);
                     float[] result = new float[list.size()];

                     for (int i = 0; i < list.size(); i++) {
                        FloatTag tag1 = (FloatTag)list.get(i);
                        result[i] = tag1.getAsFloat();
                     }

                     declaredField.set(obj, result);
                  }

                  if (declaringClass == double[].class) {
                     ListTag list = compoundTag.getList(name, 6);
                     double[] result = new double[list.size()];

                     for (int i = 0; i < list.size(); i++) {
                        DoubleTag tag1 = (DoubleTag)list.get(i);
                        result[i] = tag1.getAsDouble();
                     }

                     declaredField.set(obj, result);
                  }

                  if (declaringClass == boolean[].class) {
                     ListTag list = compoundTag.getList(name, 1);
                     boolean[] result = new boolean[list.size()];

                     for (int i = 0; i < list.size(); i++) {
                        ByteTag tag1 = (ByteTag)list.get(i);
                        result[i] = tag1.getAsByte() == 1;
                     }

                     declaredField.set(obj, result);
                  }

                  if (declaringClass == short[].class) {
                     ListTag list = compoundTag.getList(name, 2);
                     short[] result = new short[list.size()];

                     for (int i = 0; i < list.size(); i++) {
                        ShortTag tag1 = (ShortTag)list.get(i);
                        result[i] = tag1.getAsShort();
                     }

                     declaredField.set(obj, result);
                  }

                  if (declaringClass == char[].class) {
                     ListTag list = compoundTag.getList(name, 3);
                     char[] result = new char[list.size()];

                     for (int i = 0; i < list.size(); i++) {
                        IntTag tag1 = (IntTag)list.get(i);
                        result[i] = (char)tag1.getAsInt();
                     }

                     declaredField.set(obj, result);
                  }

                  if (declaringClass.isInstance(Map.class)) {
                     declaredField.set(obj, fromTag(compoundTag.get(name), declaredField.getType()));
                  }
               }
            }
         }
      }

      return obj;
   }

   static <T> T fromTag(Tag tag, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
      if (tag instanceof IntTag intTag) {
         return (T)intTag.getAsInt();
      } else if (tag instanceof LongTag longTag) {
         return (T)longTag.getAsLong();
      } else if (tag instanceof FloatTag floatTag) {
         return (T)floatTag.getAsFloat();
      } else if (tag instanceof DoubleTag doubleTag) {
         return (T)doubleTag.getAsDouble();
      } else if (tag instanceof ByteTag byteTag) {
         return (T)byteTag.getAsByte();
      } else if (tag instanceof ShortTag shortTag) {
         return (T)shortTag.getAsShort();
      } else if (tag instanceof StringTag stringTag) {
         return (T)(clazz.isEnum() ? Enum.valueOf(clazz, stringTag.getAsString()) : stringTag.getAsString());
      } else if (tag instanceof IntArrayTag intArrayTag) {
         return (T)intArrayTag.getAsIntArray();
      } else if (tag instanceof LongArrayTag longArrayTag) {
         return (T)longArrayTag.getAsLongArray();
      } else if (tag instanceof ByteArrayTag byteArrayTag) {
         return (T)byteArrayTag.getAsByteArray();
      } else if (tag instanceof CompoundTag compoundTag) {
         Map<String, Tag> entries = new HashMap<>();

         for (String key : compoundTag.getAllKeys()) {
            entries.put(key, compoundTag.get(key));
         }

         T t = clazz.getConstructor().newInstance();

         for (Field declaredField : clazz.getDeclaredFields()) {
            int modifiers = declaredField.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
               declaredField.setAccessible(true);
               String name = getName(declaredField, entries);
               Tag tag1 = entries.get(name);
               if (tag1 != null) {
                  declaredField.set(t, fromTag(tag1, declaredField.getType()));
               }
            }
         }

         return t;
      } else if (!(tag instanceof ListTag listTag)) {
         return null;
      } else {
         T t = clazz.getConstructor().newInstance();
         if (t instanceof Collection collection) {
            for (Tag tag1 : listTag) {
               collection.add(fromTag(tag1, clazz));
            }
         }

         return t;
      }
   }

   private static String getName(Field declaredField, Map<String, Tag> entries) {
      String name = declaredField.getName();
      if (declaredField.isAnnotationPresent(SerializedName.class)) {
         SerializedName annotation = declaredField.getAnnotation(SerializedName.class);
         name = annotation.value();
         if (!entries.containsKey(name) && annotation.alternate().length > 0) {
            for (String s : annotation.alternate()) {
               if (entries.containsKey(s)) {
                  name = s;
                  break;
               }
            }
         }
      }

      return name;
   }
}
