package net.blay09.mods.balm.api;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public class DeferredObject<T> {
   private final ResourceLocation id;
   private final Supplier<T> supplier;
   private final Supplier<Boolean> canResolveFunc;
   protected T object;

   protected DeferredObject(ResourceLocation id) {
      this(id, () -> null, () -> false);
   }

   public DeferredObject(ResourceLocation id, Supplier<T> supplier) {
      this(id, supplier, () -> false);
   }

   public DeferredObject(ResourceLocation id, Supplier<T> supplier, Supplier<Boolean> canResolveFunc) {
      this.id = id;
      this.supplier = supplier;
      this.canResolveFunc = canResolveFunc;
   }

   public static <T> DeferredObject<T> of(ResourceLocation identifier, T instance) {
      return new DeferredObject<>(identifier, () -> instance).resolveImmediately();
   }

   protected void set(T object) {
      this.object = object;
   }

   public boolean canResolve() {
      return this.canResolveFunc.get();
   }

   public T resolve() {
      if (this.object == null) {
         this.object = this.supplier.get();
      }

      return this.object;
   }

   public T get() {
      if (this.object == null) {
         if (this.canResolve()) {
            return this.resolve();
         } else {
            throw new IllegalStateException("Tried to access deferred object before it was resolved.");
         }
      } else {
         return this.object;
      }
   }

   public DeferredObject<T> resolveImmediately() {
      this.resolve();
      return this;
   }

   public ResourceLocation getIdentifier() {
      return this.id;
   }
}
