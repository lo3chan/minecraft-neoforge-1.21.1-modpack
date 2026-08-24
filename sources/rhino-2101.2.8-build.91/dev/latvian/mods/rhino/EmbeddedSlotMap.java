package dev.latvian.mods.rhino;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class EmbeddedSlotMap implements SlotMap {
   private static final int INITIAL_SLOT_SIZE = 4;
   private Slot[] slots;
   private Slot firstAdded;
   private Slot lastAdded;
   private int count;

   private static void copyTable(Slot[] oldSlots, Slot[] newSlots) {
      for (Slot slot : oldSlots) {
         while (slot != null) {
            Slot nextSlot = slot.next;
            addKnownAbsentSlot(newSlots, slot);
            slot = nextSlot;
         }
      }
   }

   private static void addKnownAbsentSlot(Slot[] addSlots, Slot slot) {
      int insertPos = getSlotIndex(addSlots.length, slot.indexOrHash);
      slot.next = addSlots[insertPos];
      addSlots[insertPos] = slot;
   }

   private static int getSlotIndex(int tableSize, int indexOrHash) {
      return indexOrHash & tableSize - 1;
   }

   @Override
   public int size() {
      return this.count;
   }

   @Override
   public boolean isEmpty() {
      return this.count == 0;
   }

   @Override
   public Iterator<Slot> iterator() {
      return new EmbeddedSlotMap.Iter(this.firstAdded);
   }

   @Override
   public Slot query(Object key, int index) {
      if (this.slots == null) {
         return null;
      } else {
         int indexOrHash = key != null ? key.hashCode() : index;
         int slotIndex = getSlotIndex(this.slots.length, indexOrHash);

         for (Slot slot = this.slots[slotIndex]; slot != null; slot = slot.next) {
            Object skey = slot.name;
            if (indexOrHash == slot.indexOrHash && Objects.equals(key, skey)) {
               return slot;
            }
         }

         return null;
      }
   }

   @Override
   public Slot modify(Object key, int index, int attributes) {
      int indexOrHash = key != null ? key.hashCode() : index;
      Slot slot = null;
      if (this.slots != null) {
         int slotIndex = getSlotIndex(this.slots.length, indexOrHash);

         for (slot = this.slots[slotIndex]; slot != null; slot = slot.next) {
            Object skey = slot.name;
            if (indexOrHash == slot.indexOrHash && Objects.equals(key, skey)) {
               break;
            }
         }

         if (slot != null) {
            return slot;
         }
      }

      Slot newSlot = new Slot(key, index, attributes);
      this.createNewSlot(newSlot);
      return newSlot;
   }

   private void createNewSlot(Slot newSlot) {
      if (this.count == 0) {
         this.slots = new Slot[4];
      }

      if (4 * (this.count + 1) > 3 * this.slots.length) {
         Slot[] newSlots = new Slot[this.slots.length * 2];
         copyTable(this.slots, newSlots);
         this.slots = newSlots;
      }

      this.insertNewSlot(newSlot);
   }

   @Override
   public <S extends Slot> S compute(Object key, int index, SlotMap.SlotComputer<S> c) {
      int indexOrHash = key != null ? key.hashCode() : index;
      if (this.slots != null) {
         int slotIndex = getSlotIndex(this.slots.length, indexOrHash);
         Slot prev = this.slots[slotIndex];

         Slot slot;
         for (slot = prev; slot != null && (indexOrHash != slot.indexOrHash || !Objects.equals(slot.name, key)); slot = slot.next) {
            prev = slot;
         }

         if (slot != null) {
            S newSlot = c.compute(key, index, slot);
            if (newSlot == null) {
               this.removeSlot(slot, prev, slotIndex);
            } else if (!Objects.equals(slot, newSlot)) {
               if (prev == slot) {
                  this.slots[slotIndex] = newSlot;
               } else {
                  prev.next = newSlot;
               }

               newSlot.next = slot.next;
               if (slot == this.firstAdded) {
                  this.firstAdded = newSlot;
               } else {
                  Slot ps = this.firstAdded;

                  while (ps != null && ps.orderedNext != slot) {
                     ps = ps.orderedNext;
                  }

                  if (ps != null) {
                     ps.orderedNext = newSlot;
                  }
               }

               newSlot.orderedNext = slot.orderedNext;
               if (slot == this.lastAdded) {
                  this.lastAdded = newSlot;
               }
            }

            return newSlot;
         }
      }

      S newSlot = c.compute(key, index, null);
      if (newSlot != null) {
         this.createNewSlot(newSlot);
      }

      return newSlot;
   }

   private void removeSlot(Slot slot, Slot prev, int slotIndex) {
      this.count--;
      if (prev == slot) {
         this.slots[slotIndex] = slot.next;
      } else {
         prev.next = slot.next;
      }

      if (slot == this.firstAdded) {
         prev = null;
         this.firstAdded = slot.orderedNext;
      } else {
         prev = this.firstAdded;

         while (prev.orderedNext != slot) {
            prev = prev.orderedNext;
         }

         prev.orderedNext = slot.orderedNext;
      }

      if (slot == this.lastAdded) {
         this.lastAdded = prev;
      }
   }

   @Override
   public void add(Slot newSlot) {
      if (this.slots == null) {
         this.slots = new Slot[4];
      }

      this.insertNewSlot(newSlot);
   }

   private void insertNewSlot(Slot newSlot) {
      this.count++;
      if (this.lastAdded != null) {
         this.lastAdded.orderedNext = newSlot;
      }

      if (this.firstAdded == null) {
         this.firstAdded = newSlot;
      }

      this.lastAdded = newSlot;
      addKnownAbsentSlot(this.slots, newSlot);
   }

   private static final class Iter implements Iterator<Slot> {
      private Slot next;

      Iter(Slot slot) {
         this.next = slot;
      }

      @Override
      public boolean hasNext() {
         return this.next != null;
      }

      public Slot next() {
         Slot ret = this.next;
         if (ret == null) {
            throw new NoSuchElementException();
         } else {
            this.next = this.next.orderedNext;
            return ret;
         }
      }
   }
}
