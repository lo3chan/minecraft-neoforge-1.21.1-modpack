package dev.latvian.mods.rhino;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class HashSlotMap implements SlotMap {
   private final LinkedHashMap<Object, Slot> map;

   public HashSlotMap() {
      this.map = new LinkedHashMap<>();
   }

   public HashSlotMap(SlotMap oldMap) {
      this.map = new LinkedHashMap<>(oldMap.size());

      for (Slot n : oldMap) {
         this.add(n.copySlot());
      }
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public Slot query(Object key, int index) {
      Object name = this.makeKey(key, index);
      return this.map.get(name);
   }

   @Override
   public Slot modify(Object key, int index, int attributes) {
      Object name = this.makeKey(key, index);
      return this.map.computeIfAbsent(name, n -> new Slot(key, index, attributes));
   }

   @Override
   public <S extends Slot> S compute(Object key, int index, SlotMap.SlotComputer<S> c) {
      Object name = this.makeKey(key, index);
      return (S)this.map.compute(name, (n, existing) -> c.compute(key, index, existing));
   }

   @Override
   public void add(Slot newSlot) {
      Object name = this.makeKey(newSlot);
      this.map.put(name, newSlot);
   }

   @Override
   public Iterator<Slot> iterator() {
      return this.map.values().iterator();
   }

   private Object makeKey(Object name, int index) {
      return name == null ? String.valueOf(index) : name;
   }

   private Object makeKey(Slot slot) {
      return slot.name == null ? String.valueOf(slot.indexOrHash) : slot.name;
   }
}
