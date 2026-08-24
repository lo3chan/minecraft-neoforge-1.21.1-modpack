package com.teamresourceful.resourcefulconfig.api.types.entries;

public interface ResourcefulConfigValueEntry extends ResourcefulConfigEntry {
   Object defaultValue();

   Class<?> objectType();

   default <T> T defaultOrElse(T value) {
      Object defaultValue = this.defaultValue();
      return (T)(defaultValue == null ? value : defaultValue);
   }

   boolean isArray();

   Object get();

   Object[] getArray();

   boolean setArray(Object[] var1);

   byte getByte();

   boolean setByte(byte var1);

   short getShort();

   boolean setShort(short var1);

   int getInt();

   boolean setInt(int var1);

   long getLong();

   boolean setLong(long var1);

   float getFloat();

   boolean setFloat(float var1);

   double getDouble();

   boolean setDouble(double var1);

   boolean getBoolean();

   boolean setBoolean(boolean var1);

   String getString();

   boolean setString(String var1);

   Enum<?> getEnum();

   boolean setEnum(Enum<?> var1);
}
