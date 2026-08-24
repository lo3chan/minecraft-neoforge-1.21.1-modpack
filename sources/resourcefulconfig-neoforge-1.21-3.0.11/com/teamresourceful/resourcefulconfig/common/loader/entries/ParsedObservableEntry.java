package com.teamresourceful.resourcefulconfig.common.loader.entries;

import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;
import com.teamresourceful.resourcefulconfig.api.types.options.Option;
import com.teamresourceful.resourcefulconfig.common.config.ParsingUtils;
import com.teamresourceful.resourcefulconfig.common.utils.ModUtils;
import java.lang.reflect.Field;

public record ParsedObservableEntry(EntryType type, Class<?> objectType, Observable<?> observable, EntryData options, Object defaultValue)
   implements ResourcefulConfigValueEntry {
   public static ParsedObservableEntry of(EntryType type, Field field, Object instance) {
      Observable<?> observable = (Observable<?>)ParsingUtils.getField(field, instance);
      Object defaultValue = observable.get();
      return new ParsedObservableEntry(
         type,
         observable.type().isArray() ? observable.type().getComponentType() : observable.type(),
         observable,
         EntryData.of(field, observable.type()),
         defaultValue
      );
   }

   @Override
   public boolean isArray() {
      return this.observable.type().isArray();
   }

   @Override
   public Object get() {
      return this.observable.get();
   }

   @Override
   public Object[] getArray() {
      return !this.isArray() ? new Object[0] : (Object[])this.observable.get();
   }

   @Override
   public boolean setArray(Object[] array) {
      for (Object o : array) {
         if (!this.type().test(o.getClass())) {
            return false;
         }
      }

      this.observable.setAndCast(ModUtils.castArray(array, this.objectType));
      return true;
   }

   @Override
   public byte getByte() {
      Object value = this.get();
      return value instanceof Byte ? (Byte)value : this.defaultOrElse((byte)0);
   }

   @Override
   public boolean setByte(byte value) {
      if (this.type != EntryType.BYTE) {
         return false;
      } else {
         EntryData options = this.options();
         if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
            this.reset();
            return false;
         } else {
            this.observable.setAndCast(value);
            return true;
         }
      }
   }

   @Override
   public short getShort() {
      Object value = this.get();
      return value instanceof Short ? (Short)value : this.defaultOrElse((short)0);
   }

   @Override
   public boolean setShort(short value) {
      if (this.type != EntryType.SHORT) {
         return false;
      } else {
         EntryData options = this.options();
         if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
            this.reset();
            return false;
         } else {
            this.observable.setAndCast(value);
            return true;
         }
      }
   }

   @Override
   public int getInt() {
      Object value = this.get();
      return value instanceof Integer ? (Integer)value : this.defaultOrElse(0);
   }

   @Override
   public boolean setInt(int value) {
      if (this.type != EntryType.INTEGER) {
         return false;
      } else {
         EntryData options = this.options();
         if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
            this.reset();
            return false;
         } else {
            this.observable.setAndCast(value);
            return true;
         }
      }
   }

   @Override
   public long getLong() {
      Object value = this.get();
      return value instanceof Long ? (Long)value : this.defaultOrElse(0L);
   }

   @Override
   public boolean setLong(long value) {
      if (this.type != EntryType.LONG) {
         return false;
      } else {
         EntryData options = this.options();
         if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
            this.reset();
            return false;
         } else {
            this.observable.setAndCast(value);
            return true;
         }
      }
   }

   @Override
   public float getFloat() {
      Object value = this.get();
      return value instanceof Float ? (Float)value : this.defaultOrElse(0.0F);
   }

   @Override
   public boolean setFloat(float value) {
      if (this.type != EntryType.FLOAT) {
         return false;
      } else {
         EntryData options = this.options();
         if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
            this.reset();
            return false;
         } else {
            this.observable.setAndCast(value);
            return true;
         }
      }
   }

   @Override
   public double getDouble() {
      Object value = this.get();
      return value instanceof Double ? (Double)value : this.defaultOrElse(0.0);
   }

   @Override
   public boolean setDouble(double value) {
      if (this.type != EntryType.DOUBLE) {
         return false;
      } else {
         EntryData options = this.options();
         if (options.hasOption(Option.RANGE) && !options.inRange(value)) {
            this.reset();
            return false;
         } else {
            this.observable.setAndCast(value);
            return true;
         }
      }
   }

   @Override
   public boolean getBoolean() {
      return this.get() instanceof Boolean bool ? bool : this.defaultOrElse(false);
   }

   @Override
   public boolean setBoolean(boolean value) {
      if (this.type != EntryType.BOOLEAN) {
         return false;
      } else {
         this.observable.setAndCast(value);
         return true;
      }
   }

   @Override
   public String getString() {
      Object value = this.get();
      return value instanceof String ? (String)value : this.defaultOrElse("");
   }

   @Override
   public boolean setString(String value) {
      if (this.type != EntryType.STRING) {
         return false;
      } else {
         this.observable.setAndCast(value);
         return true;
      }
   }

   @Override
   public Enum<?> getEnum() {
      Object value = this.get();
      return value instanceof Enum ? (Enum)value : null;
   }

   @Override
   public boolean setEnum(Enum<?> value) {
      if (this.type != EntryType.ENUM) {
         return false;
      } else {
         this.observable.setAndCast(value);
         return true;
      }
   }

   @Override
   public void reset() {
      if (this.isArray()) {
         this.setArray((Object[])this.defaultValue);
      } else {
         switch (this.type) {
            case BYTE:
               this.setByte((Byte)this.defaultValue);
               break;
            case SHORT:
               this.setShort((Short)this.defaultValue);
               break;
            case INTEGER:
               this.setInt((Integer)this.defaultValue);
               break;
            case LONG:
               this.setLong((Long)this.defaultValue);
               break;
            case FLOAT:
               this.setFloat((Float)this.defaultValue);
               break;
            case DOUBLE:
               this.setDouble((Double)this.defaultValue);
               break;
            case BOOLEAN:
               this.setBoolean((Boolean)this.defaultValue);
               break;
            case STRING:
               this.setString((String)this.defaultValue);
               break;
            case ENUM:
               this.setEnum((Enum<?>)this.defaultValue);
               break;
            case OBJECT:
               throw new IllegalStateException("Object cannot be in a value entry!");
         }
      }
   }
}
