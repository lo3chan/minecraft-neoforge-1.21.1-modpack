package dev.latvian.mods.rhino;

public interface SlotMap extends Iterable<Slot> {
   int size();

   boolean isEmpty();

   Slot modify(Object var1, int var2, int var3);

   Slot query(Object var1, int var2);

   <S extends Slot> S compute(Object var1, int var2, SlotMap.SlotComputer<S> var3);

   void add(Slot var1);

   @FunctionalInterface
   public interface SlotComputer<S extends Slot> {
      S compute(Object var1, int var2, Slot var3);
   }
}
