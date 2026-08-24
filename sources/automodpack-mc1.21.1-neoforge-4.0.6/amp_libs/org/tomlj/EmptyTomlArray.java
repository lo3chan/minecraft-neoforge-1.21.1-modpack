package amp_libs.org.tomlj;

import java.util.Collections;
import java.util.List;

final class EmptyTomlArray implements TomlArray {
   static final TomlArray EMPTY_ARRAY = new EmptyTomlArray();

   private EmptyTomlArray() {
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean containsStrings() {
      return false;
   }

   @Override
   public boolean containsLongs() {
      return false;
   }

   @Override
   public boolean containsDoubles() {
      return false;
   }

   @Override
   public boolean containsBooleans() {
      return false;
   }

   @Override
   public boolean containsOffsetDateTimes() {
      return false;
   }

   @Override
   public boolean containsLocalDateTimes() {
      return false;
   }

   @Override
   public boolean containsLocalDates() {
      return false;
   }

   @Override
   public boolean containsLocalTimes() {
      return false;
   }

   @Override
   public boolean containsArrays() {
      return false;
   }

   @Override
   public boolean containsTables() {
      return false;
   }

   @Override
   public Object get(int index) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
   }

   @Override
   public TomlPosition inputPositionOf(int index) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
   }

   @Override
   public List<Object> toList() {
      return Collections.emptyList();
   }
}
