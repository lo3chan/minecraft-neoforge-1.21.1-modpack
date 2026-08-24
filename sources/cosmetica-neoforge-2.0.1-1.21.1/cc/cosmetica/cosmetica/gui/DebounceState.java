package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Component;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;

public class DebounceState<T> extends State<T> {
   private final State<T> instant;
   private final long debounceTime;
   private long time;
   private volatile long lastUpdateId;

   public DebounceState(T initialValue, long debounceTime) {
      super(initialValue);
      this.instant = new State(initialValue);
      this.debounceTime = debounceTime;
      this.time = System.currentTimeMillis() - debounceTime;
      if (debounceTime < 100L) {
         throw new IllegalArgumentException("Debounce time must be at least 100ms");
      }
   }

   public T acquireInstant(Component component) {
      return (T)this.instant.acquire(component);
   }

   public T peek() {
      return (T)this.instant.peek();
   }

   public void set(T value) {
      if (!this.instant.peek().equals(value)) {
         this.instant.set(value);
         long theTime = System.currentTimeMillis();
         this.lastUpdateId = theTime;
         if (theTime - this.time > this.debounceTime) {
            super.set(this.instant.peek());
            this.time = theTime;
         } else {
            CompletableFuture.runAsync(() -> {
               try {
                  Thread.sleep(this.debounceTime);
               } catch (InterruptedException var4) {
                  throw new RuntimeException("Setting debounced state", var4);
               }

               Minecraft.getInstance().execute(() -> {
                  if (this.lastUpdateId == theTime) {
                     super.set(this.instant.peek());
                     this.time = theTime;
                  }
               });
            });
         }
      }
   }

   public void setNow(T value) {
      this.instant.set(value);
      super.set(value);
   }
}
