package amp_libs.org.tomlj;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class MutableTomlArray implements TomlArray {
   private final List<MutableTomlArray.Element> elements = new ArrayList<>();
   private final boolean isTableArray;

   static MutableTomlArray create(TomlVersion version) {
      return create(version, false);
   }

   static MutableTomlArray create(TomlVersion version, boolean tableArray) {
      return (MutableTomlArray)(version.after(TomlVersion.V0_5_0) ? new MutableTomlArray(tableArray) : new MutableHomogeneousTomlArray(tableArray));
   }

   MutableTomlArray(boolean isTableArray) {
      this.isTableArray = isTableArray;
   }

   boolean isTableArray() {
      return this.isTableArray;
   }

   @Override
   public boolean containsStrings() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsLongs() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsDoubles() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsBooleans() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsOffsetDateTimes() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsLocalDateTimes() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsLocalDates() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsLocalTimes() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsArrays() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public boolean containsTables() {
      throw new UnsupportedOperationException("Deprecated (after 0.5.0, arrays are heterogeneous)");
   }

   @Override
   public int size() {
      return this.elements.size();
   }

   @Override
   public boolean isEmpty() {
      return this.elements.isEmpty();
   }

   @Override
   public Object get(int index) {
      return this.elements.get(index).value;
   }

   @Override
   public TomlPosition inputPositionOf(int index) {
      return this.elements.get(index).position;
   }

   MutableTomlArray append(Object value, TomlPosition position) {
      if (value instanceof Integer) {
         value = ((Integer)value).longValue();
      }

      if (!TomlType.typeFor(value).isPresent()) {
         throw new IllegalArgumentException("Unsupported type " + value.getClass().getSimpleName());
      } else {
         this.elements.add(new MutableTomlArray.Element(value, position));
         return this;
      }
   }

   @Override
   public List<Object> toList() {
      return this.elements.stream().map(e -> e.value).collect(Collectors.toList());
   }

   private static class Element {
      final Object value;
      final TomlPosition position;

      private Element(Object value, TomlPosition position) {
         this.value = value;
         this.position = position;
      }
   }
}
