package com.teamresourceful.resourcefulconfig.api.types.entries;

import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;
import com.teamresourceful.resourcefulconfig.api.types.options.Option;
import com.teamresourceful.resourcefulconfig.common.utils.ModUtils;
import java.lang.reflect.Field;

public interface ResourcefulConfigFieldBackedValueEntry extends ResourcefulConfigValueEntry {
   Field field();

   Object instance();

   @Override
   default Object get() {
      try {
         return this.field().get(this.instance());
      } catch (IllegalAccessException var2) {
         return this.defaultOrElse(null);
      }
   }

   @Override
   default Object[] getArray() {
      if (!this.isArray()) {
         return new Object[0];
      } else {
         try {
            return (Object[])this.field().get(this.instance());
         } catch (IllegalAccessException var2) {
            return new Object[0];
         }
      }
   }

   @Override
   default boolean setArray(Object[] array) {
      try {
         for (Object o : array) {
            if (!this.type().test(o.getClass())) {
               return false;
            }
         }

         this.field().set(this.instance(), ModUtils.castArray(array, this.field().getType().componentType()));
         return true;
      } catch (Exception var6) {
         return false;
      }
   }

   @Override
   default byte getByte() {
      if (this.type() != EntryType.BYTE) {
         return 0;
      } else {
         try {
            return this.field().getByte(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse((byte)0);
         }
      }
   }

   @Override
   default boolean setByte(byte value) {
      if (this.type() != EntryType.BYTE) {
         return false;
      } else {
         try {
            EntryData options = this.options();
            if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
               this.reset();
               return false;
            } else {
               this.field().setByte(this.instance(), value);
               return true;
            }
         } catch (Exception var3) {
            return false;
         }
      }
   }

   @Override
   default short getShort() {
      if (this.type() != EntryType.SHORT) {
         return 0;
      } else {
         try {
            return this.field().getShort(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse((short)0);
         }
      }
   }

   @Override
   default boolean setShort(short value) {
      if (this.type() != EntryType.SHORT) {
         return false;
      } else {
         try {
            EntryData options = this.options();
            if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
               this.reset();
               return false;
            } else {
               this.field().setShort(this.instance(), value);
               return true;
            }
         } catch (Exception var3) {
            return false;
         }
      }
   }

   @Override
   default int getInt() {
      if (this.type() != EntryType.INTEGER) {
         return 0;
      } else {
         try {
            return this.field().getInt(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse(0);
         }
      }
   }

   @Override
   default boolean setInt(int value) {
      if (this.type() != EntryType.INTEGER) {
         return false;
      } else {
         try {
            EntryData options = this.options();
            if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
               this.reset();
               return false;
            } else {
               this.field().setInt(this.instance(), value);
               return true;
            }
         } catch (Exception var3) {
            return false;
         }
      }
   }

   @Override
   default long getLong() {
      if (this.type() != EntryType.LONG) {
         return 0L;
      } else {
         try {
            return this.field().getLong(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse(0L);
         }
      }
   }

   @Override
   default boolean setLong(long value) {
      if (this.type() != EntryType.LONG) {
         return false;
      } else {
         try {
            EntryData options = this.options();
            if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
               this.reset();
               return false;
            } else {
               this.field().setLong(this.instance(), value);
               return true;
            }
         } catch (Exception var4) {
            return false;
         }
      }
   }

   @Override
   default float getFloat() {
      if (this.type() != EntryType.FLOAT) {
         return 0.0F;
      } else {
         try {
            return this.field().getFloat(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse(0.0F);
         }
      }
   }

   @Override
   default boolean setFloat(float value) {
      if (this.type() != EntryType.FLOAT) {
         return false;
      } else {
         try {
            EntryData options = this.options();
            if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
               this.reset();
               return false;
            } else {
               this.field().setFloat(this.instance(), value);
               return true;
            }
         } catch (Exception var3) {
            return false;
         }
      }
   }

   @Override
   default double getDouble() {
      if (this.type() != EntryType.DOUBLE) {
         return 0.0;
      } else {
         try {
            return this.field().getDouble(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse(0.0);
         }
      }
   }

   @Override
   default boolean setDouble(double value) {
      if (this.type() != EntryType.DOUBLE) {
         return false;
      } else {
         try {
            EntryData options = this.options();
            if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
               this.reset();
               return false;
            } else {
               this.field().setDouble(this.instance(), value);
               return true;
            }
         } catch (Exception var4) {
            return false;
         }
      }
   }

   @Override
   default boolean getBoolean() {
      if (this.type() != EntryType.BOOLEAN) {
         return false;
      } else {
         try {
            return this.field().getBoolean(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse(false);
         }
      }
   }

   @Override
   default boolean setBoolean(boolean value) {
      if (this.type() != EntryType.BOOLEAN) {
         return false;
      } else {
         try {
            this.field().setBoolean(this.instance(), value);
            return true;
         } catch (Exception var3) {
            return false;
         }
      }
   }

   @Override
   default String getString() {
      if (this.type() != EntryType.STRING) {
         return "";
      } else {
         try {
            return (String)this.field().get(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse("");
         }
      }
   }

   @Override
   default boolean setString(String value) {
      if (this.type() != EntryType.STRING) {
         return false;
      } else if (value == null) {
         return false;
      } else {
         try {
            EntryData options = this.options();
            if (options.hasOption(Option.REGEX) && !options.getOption(Option.REGEX).matcher(value).matches()) {
               this.reset();
               return false;
            } else {
               this.field().set(this.instance(), value);
               return true;
            }
         } catch (Exception var3) {
            return false;
         }
      }
   }

   @Override
   default Enum<?> getEnum() {
      if (this.type() != EntryType.ENUM) {
         return null;
      } else {
         try {
            return (Enum<?>)this.field().get(this.instance());
         } catch (IllegalAccessException var2) {
            return this.defaultOrElse(null);
         }
      }
   }

   @Override
   default boolean setEnum(Enum<?> value) {
      if (this.type() != EntryType.ENUM) {
         return false;
      } else if (value == null) {
         return false;
      } else {
         try {
            this.field().set(this.instance(), value);
            return true;
         } catch (Exception var3) {
            return false;
         }
      }
   }
}
