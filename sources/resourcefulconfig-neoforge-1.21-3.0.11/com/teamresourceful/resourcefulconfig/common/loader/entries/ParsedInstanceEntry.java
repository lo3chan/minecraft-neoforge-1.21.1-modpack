package com.teamresourceful.resourcefulconfig.common.loader.entries;

import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigFieldBackedValueEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;
import com.teamresourceful.resourcefulconfig.common.config.ParsingUtils;
import java.lang.reflect.Field;

public record ParsedInstanceEntry(EntryType type, Field field, EntryData options, Object instance, Object defaultValue)
   implements ResourcefulConfigFieldBackedValueEntry {
   public ParsedInstanceEntry(EntryType type, Field field, Object instance) {
      this(type, field, EntryData.of(field, field.getType()), instance, ParsingUtils.getField(field, instance));
   }

   @Override
   public Class<?> objectType() {
      return this.isArray() ? this.field.getType().getComponentType() : this.field.getType();
   }

   @Override
   public boolean isArray() {
      return this.field.getType().isArray();
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
