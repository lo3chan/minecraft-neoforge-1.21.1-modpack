package amp_libs.org.antlr.v4.runtime.misc;

import java.util.List;

public interface IntSet {
   void add(int var1);

   IntSet addAll(IntSet var1);

   IntSet and(IntSet var1);

   IntSet complement(IntSet var1);

   IntSet or(IntSet var1);

   IntSet subtract(IntSet var1);

   int size();

   boolean isNil();

   @Override
   boolean equals(Object var1);

   boolean contains(int var1);

   void remove(int var1);

   List<Integer> toList();

   @Override
   String toString();
}
