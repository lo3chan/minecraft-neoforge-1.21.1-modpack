package com.teamresourceful.resourcefulconfig.common.compat.minecraft;

import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;
import com.teamresourceful.resourcefulconfig.mixins.common.DedicatedServerAccessor;
import com.teamresourceful.resourcefulconfig.mixins.common.SettingsAccessor;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;

public class DedicatedServerEntry implements ResourcefulConfigValueEntry {
   private final EntryType type;
   private final EntryData options;
   private final String defaultValue;
   private final Supplier<String> getter;
   private final Function<String, Boolean> setter;

   public DedicatedServerEntry(String id, boolean defaultValue, BiConsumer<DedicatedServer, Boolean> onUpdate) {
      this(id, EntryType.BOOLEAN, String.valueOf(defaultValue), (server, value) -> onUpdate.accept(server, value.getBoolean()));
   }

   public DedicatedServerEntry(String id, int defaultValue, BiConsumer<DedicatedServer, Integer> onUpdate) {
      this(id, EntryType.INTEGER, String.valueOf(defaultValue), (server, value) -> onUpdate.accept(server, value.getInt()));
   }

   public DedicatedServerEntry(String id, String defaultValue, BiConsumer<DedicatedServer, String> onUpdate) {
      this(id, EntryType.STRING, defaultValue, (server, value) -> onUpdate.accept(server, value.getString()));
   }

   public DedicatedServerEntry(String id, EntryType entryType, String defaultValue, BiConsumer<DedicatedServer, DedicatedServerEntry> onUpdate) {
      this(
         id,
         entryType,
         EntryData.builder().translation(id, "rconfig.server.properties." + id).comment("", "rconfig.server.properties." + id + ".desc").build(),
         defaultValue,
         onUpdate
      );
   }

   public DedicatedServerEntry(
      String id, EntryType entryType, EntryData options, String defaultValue, BiConsumer<DedicatedServer, DedicatedServerEntry> onUpdate
   ) {
      this.type = entryType;
      this.options = options;
      this.defaultValue = defaultValue;
      this.getter = () -> ((SettingsAccessor)Objects.requireNonNull(DedicatedServerInfo.getServer()).getProperties()).invokeGetStringRaw(id);
      this.setter = newValue -> {
         DedicatedServer server = DedicatedServerInfo.getServer();
         if (server == null) {
            return false;
         } else {
            ((DedicatedServerAccessor)server).getSettings().update(properties -> {
               Properties newProps = ((SettingsAccessor)server.getProperties()).invokeCloneProperties();
               newProps.setProperty(id, newValue);
               return new DedicatedServerProperties(newProps);
            });
            onUpdate.accept(server, this);
            return true;
         }
      };
      if (entryType == EntryType.ENUM || EntryType.OBJECT == entryType) {
         throw new IllegalArgumentException("EntryType cannot be ENUM or OBJECT");
      }
   }

   @Override
   public Object defaultValue() {
      return switch (this.type) {
         case BOOLEAN -> Boolean.parseBoolean(this.defaultValue);
         case BYTE -> Byte.parseByte(this.defaultValue);
         case SHORT -> Short.parseShort(this.defaultValue);
         case INTEGER -> Integer.parseInt(this.defaultValue);
         case LONG -> Long.parseLong(this.defaultValue);
         case FLOAT -> Float.parseFloat(this.defaultValue);
         case DOUBLE -> Double.parseDouble(this.defaultValue);
         case STRING -> this.defaultValue;
         default -> null;
      };
   }

   @Override
   public Class<?> objectType() {
      return switch (this.type) {
         case BOOLEAN -> boolean.class;
         case BYTE -> byte.class;
         case SHORT -> short.class;
         case INTEGER -> int.class;
         case LONG -> long.class;
         case FLOAT -> float.class;
         case DOUBLE -> double.class;
         case STRING -> String.class;
         default -> null;
      };
   }

   @Override
   public boolean isArray() {
      return false;
   }

   @Override
   public Object get() {
      return switch (this.type) {
         case BOOLEAN -> Boolean.parseBoolean(this.getter.get());
         case BYTE -> Byte.parseByte(this.getter.get());
         case SHORT -> Short.parseShort(this.getter.get());
         case INTEGER -> Integer.parseInt(this.getter.get());
         case LONG -> Long.parseLong(this.getter.get());
         case FLOAT -> Float.parseFloat(this.getter.get());
         case DOUBLE -> Double.parseDouble(this.getter.get());
         case STRING -> (String)this.getter.get();
         default -> null;
      };
   }

   @Override
   public Object[] getArray() {
      return new Object[0];
   }

   @Override
   public boolean setArray(Object[] array) {
      return false;
   }

   @Override
   public byte getByte() {
      if (this.type != EntryType.BYTE) {
         return 0;
      } else {
         try {
            return Byte.parseByte(this.getter.get());
         } catch (NumberFormatException var2) {
            return 0;
         }
      }
   }

   @Override
   public boolean setByte(byte value) {
      return this.type != EntryType.BYTE ? false : this.setter.apply(String.valueOf((int)value));
   }

   @Override
   public short getShort() {
      if (this.type != EntryType.SHORT) {
         return 0;
      } else {
         try {
            return Short.parseShort(this.getter.get());
         } catch (NumberFormatException var2) {
            return 0;
         }
      }
   }

   @Override
   public boolean setShort(short value) {
      return this.type != EntryType.SHORT ? false : this.setter.apply(String.valueOf((int)value));
   }

   @Override
   public int getInt() {
      if (this.type != EntryType.INTEGER) {
         return 0;
      } else {
         try {
            return Integer.parseInt(this.getter.get());
         } catch (NumberFormatException var2) {
            return 0;
         }
      }
   }

   @Override
   public boolean setInt(int value) {
      return this.type != EntryType.INTEGER ? false : this.setter.apply(String.valueOf(value));
   }

   @Override
   public long getLong() {
      if (this.type != EntryType.LONG) {
         return 0L;
      } else {
         try {
            return Long.parseLong(this.getter.get());
         } catch (NumberFormatException var2) {
            return 0L;
         }
      }
   }

   @Override
   public boolean setLong(long value) {
      return this.type != EntryType.LONG ? false : this.setter.apply(String.valueOf(value));
   }

   @Override
   public float getFloat() {
      if (this.type != EntryType.FLOAT) {
         return 0.0F;
      } else {
         try {
            return Float.parseFloat(this.getter.get());
         } catch (NumberFormatException var2) {
            return 0.0F;
         }
      }
   }

   @Override
   public boolean setFloat(float value) {
      return this.type != EntryType.FLOAT ? false : this.setter.apply(String.valueOf(value));
   }

   @Override
   public double getDouble() {
      if (this.type != EntryType.DOUBLE) {
         return 0.0;
      } else {
         try {
            return Double.parseDouble(this.getter.get());
         } catch (NumberFormatException var2) {
            return 0.0;
         }
      }
   }

   @Override
   public boolean setDouble(double value) {
      return this.type != EntryType.DOUBLE ? false : this.setter.apply(String.valueOf(value));
   }

   @Override
   public boolean getBoolean() {
      return this.type != EntryType.BOOLEAN ? false : Boolean.parseBoolean(this.getter.get());
   }

   @Override
   public boolean setBoolean(boolean value) {
      return this.type != EntryType.BOOLEAN ? false : this.setter.apply(String.valueOf(value));
   }

   @Override
   public String getString() {
      return this.type != EntryType.STRING ? null : this.getter.get();
   }

   @Override
   public boolean setString(String value) {
      return this.type != EntryType.STRING ? false : this.setter.apply(value);
   }

   @Override
   public Enum<?> getEnum() {
      return null;
   }

   @Override
   public boolean setEnum(Enum<?> value) {
      return false;
   }

   @Override
   public EntryType type() {
      return this.type;
   }

   @Override
   public EntryData options() {
      return this.options;
   }

   @Override
   public void reset() {
      this.setter.apply(this.defaultValue);
   }
}
