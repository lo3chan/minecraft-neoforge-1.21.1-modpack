package dev.latvian.mods.rhino;

import java.util.Iterator;

class SlotMapContainer implements SlotMap {
   private static final int LARGE_HASH_SIZE = 2000;
   protected SlotMap map;

   SlotMapContainer(int initialSize) {
      if (initialSize > 2000) {
         this.map = new HashSlotMap();
      } else {
         this.map = new EmbeddedSlotMap();
      }
   }

   @Override
   public int size() {
      return this.map.size();
   }

   public int dirtySize() {
      return this.map.size();
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public Slot modify(Object key, int index, int attributes) {
      this.checkMapSize();
      return this.map.modify(key, index, attributes);
   }

   @Override
   public <S extends Slot> S compute(Object key, int index, SlotMap.SlotComputer<S> c) {
      return this.map.compute(key, index, c);
   }

   @Override
   public Slot query(Object key, int index) {
      return this.map.query(key, index);
   }

   @Override
   public void add(Slot newSlot) {
      this.checkMapSize();
      this.map.add(newSlot);
   }

   @Override
   public Iterator<Slot> iterator() {
      return this.map.iterator();
   }

   public long readLock() {
      return 0L;
   }

   public void unlockRead(long stamp) {
   }

   protected void checkMapSize() {
      if (this.map instanceof EmbeddedSlotMap && this.map.size() >= 2000) {
         this.map = new HashSlotMap(this.map);
      }
   }
}
