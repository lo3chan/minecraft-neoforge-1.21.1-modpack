package io.wispforest.owo.serialization.format.nbt;

record IdentityHolder<T>(T t) {
   @Override
   public boolean equals(Object obj) {
      return obj != null && obj.getClass() == this.getClass() ? this.t == ((IdentityHolder)obj).t : false;
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.t);
   }

   @Override
   public String toString() {
      return "IdentityHolder[t=" + this.t + "]";
   }
}
