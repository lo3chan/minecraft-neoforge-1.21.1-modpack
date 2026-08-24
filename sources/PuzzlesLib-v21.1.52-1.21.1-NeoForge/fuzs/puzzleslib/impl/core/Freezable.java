package fuzs.puzzleslib.impl.core;

public interface Freezable {
   void freeze();

   boolean isFrozen();

   default void isWritableOrThrow() {
      if (this.isFrozen()) {
         throw new IllegalStateException(this.getClass().getSimpleName() + " is already frozen");
      }
   }

   default void isFrozenOrThrow() {
      if (!this.isFrozen()) {
         throw new IllegalStateException(this.getClass().getSimpleName() + " is not yet frozen");
      }
   }
}
