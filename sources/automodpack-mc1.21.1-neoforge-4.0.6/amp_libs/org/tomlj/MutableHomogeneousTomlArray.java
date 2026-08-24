package amp_libs.org.tomlj;

import java.util.Optional;

final class MutableHomogeneousTomlArray extends MutableTomlArray {
   private TomlType type = null;

   MutableHomogeneousTomlArray(boolean tableArray) {
      super(tableArray);
   }

   @Override
   public boolean containsStrings() {
      return this.type == null || this.type == TomlType.STRING;
   }

   @Override
   public boolean containsLongs() {
      return this.type == null || this.type == TomlType.INTEGER;
   }

   @Override
   public boolean containsDoubles() {
      return this.type == null || this.type == TomlType.FLOAT;
   }

   @Override
   public boolean containsBooleans() {
      return this.type == null || this.type == TomlType.BOOLEAN;
   }

   @Override
   public boolean containsOffsetDateTimes() {
      return this.type == null || this.type == TomlType.OFFSET_DATE_TIME;
   }

   @Override
   public boolean containsLocalDateTimes() {
      return this.type == null || this.type == TomlType.LOCAL_DATE_TIME;
   }

   @Override
   public boolean containsLocalDates() {
      return this.type == null || this.type == TomlType.LOCAL_DATE;
   }

   @Override
   public boolean containsLocalTimes() {
      return this.type == null || this.type == TomlType.LOCAL_TIME;
   }

   @Override
   public boolean containsArrays() {
      return this.type == null || this.type == TomlType.ARRAY;
   }

   @Override
   public boolean containsTables() {
      return this.type == null || this.type == TomlType.TABLE;
   }

   public MutableHomogeneousTomlArray append(Object value, TomlPosition position) {
      if (value instanceof Integer) {
         value = ((Integer)value).longValue();
      }

      TomlType origType = this.type;
      Optional<TomlType> valueType = TomlType.typeFor(value);
      if (!valueType.isPresent()) {
         throw new IllegalArgumentException("Unsupported type " + value.getClass().getSimpleName());
      } else {
         if (this.type != null) {
            if (valueType.get() != this.type) {
               throw new TomlInvalidTypeException("Cannot add a " + TomlType.typeNameFor(value) + " to an array containing " + this.type.typeName() + "s");
            }
         } else {
            this.type = valueType.get();
         }

         try {
            super.append(value, position);
            return this;
         } catch (Throwable var6) {
            this.type = origType;
            throw var6;
         }
      }
   }
}
