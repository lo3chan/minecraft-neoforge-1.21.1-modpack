package dev.latvian.mods.kubejs.util;

import java.util.ArrayList;

public class ErrorStack {
   public static final ErrorStack NONE = new ErrorStack() {
      @Override
      public void push(Object parent) {
      }

      @Override
      public void setKey(Object key) {
      }

      @Override
      public void setKey(int index) {
      }

      @Override
      public void pop() {
      }
   };
   private final ArrayList<Object> parents = new ArrayList<>(2);
   private final ArrayList<Object> keys = new ArrayList<>(2);

   public void push(Object parent) {
      this.parents.add(parent);
      this.keys.add("?");
   }

   public void setKey(Object key) {
      this.keys.set(this.keys.size() - 1, key);
   }

   public void setKey(int index) {
      this.keys.set(this.keys.size() - 1, index);
   }

   public void pop() {
      this.parents.removeLast();
      this.keys.removeLast();
   }

   @Override
   public String toString() {
      if (this.keys.size() <= 1) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();

         for (Object key : this.keys) {
            sb.append('[');
            sb.append(key);
            sb.append(']');
         }

         return sb.toString();
      }
   }

   public String atString() {
      String str = this.toString();
      return str.isEmpty() ? "" : " @ " + str;
   }

   public String stringAt() {
      String str = this.toString();
      return str.isEmpty() ? "" : str + " @ ";
   }
}
