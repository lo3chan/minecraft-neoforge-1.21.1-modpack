package com.iafenvoy.origins.util.wrapper;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public interface Mutable<T> {
   T get();

   void set(T var1);

   static <T> Mutable<T> of(T value) {
      return new Mutable.Constant<>(value);
   }

   static <T> Mutable<T> access(Supplier<T> reader, Consumer<T> writer) {
      return new Mutable.Access<>(reader, writer);
   }

   static Mutable.Stack stack(ItemStack initialStack) {
      return new Mutable.Stack(initialStack);
   }

   public static class Access<T> implements Mutable<T> {
      private final Supplier<T> reader;
      private final Consumer<T> writer;

      public Access(Supplier<T> reader, Consumer<T> writer) {
         this.reader = reader;
         this.writer = writer;
      }

      @Override
      public T get() {
         return this.reader.get();
      }

      @Override
      public void set(T value) {
         this.writer.accept(value);
      }
   }

   public static class Constant<T> implements Mutable<T> {
      private T value;

      public Constant(T value) {
         this.value = value;
      }

      @Override
      public T get() {
         return this.value;
      }

      @Override
      public void set(T value) {
         this.value = value;
      }
   }

   public static class Stack implements Mutable<ItemStack> {
      private ItemStack value;

      public Stack(ItemStack value) {
         this.value = value;
      }

      public ItemStack get() {
         return this.value;
      }

      public void set(ItemStack value) {
         this.value = value;
      }

      public SlotAccess toSlotAccess() {
         return SlotAccess.of(this::get, this::set);
      }
   }
}
