package snownee.jade.addon.universal;

import com.google.common.math.IntMath;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.util.CommonProxy;

public abstract class ItemIterator<T> {
   public static final AtomicLong version = new AtomicLong();
   protected final Function<Accessor<?>, T> containerFinder;
   protected final int fromIndex;
   protected boolean finished;
   protected int currentIndex;

   protected ItemIterator(Function<Accessor<?>, T> containerFinder, int fromIndex) {
      this.containerFinder = containerFinder;
      this.currentIndex = this.fromIndex = fromIndex;
   }

   @Nullable
   public T find(Accessor<?> accessor) {
      return this.containerFinder.apply(accessor);
   }

   public final boolean isFinished() {
      return this.finished;
   }

   public long getVersion(T container) {
      return version.getAndIncrement();
   }

   public abstract Stream<ItemStack> populate(T var1, int var2);

   public void reset() {
      this.currentIndex = this.fromIndex;
      this.finished = false;
   }

   public void afterPopulate(int count) {
      this.currentIndex += count;
      if (count == 0 || this.currentIndex >= 10000) {
         this.finished = true;
      }
   }

   public float getCollectingProgress() {
      return 0.0F / 0.0F;
   }

   public static class ContainerItemIterator extends ItemIterator.SlottedItemIterator<Container> {
      public ContainerItemIterator(int fromIndex) {
         this(CommonProxy::findContainer, fromIndex);
      }

      public ContainerItemIterator(Function<Accessor<?>, Container> containerFinder, int fromIndex) {
         super(containerFinder, fromIndex);
      }

      protected int getSlotCount(Container container) {
         return container.getContainerSize();
      }

      protected ItemStack getItemInSlot(Container container, int slot) {
         return container.getItem(slot);
      }
   }

   public abstract static class SlotlessItemIterator<T> extends ItemIterator<T> {
      protected SlotlessItemIterator(Function<Accessor<?>, T> containerFinder, int fromIndex) {
         super(containerFinder, fromIndex);
      }

      @Override
      public Stream<ItemStack> populate(T container, int amount) {
         return this.populateRaw(container).skip(this.currentIndex).limit(amount);
      }

      protected abstract Stream<ItemStack> populateRaw(T var1);
   }

   public abstract static class SlottedItemIterator<T> extends ItemIterator<T> {
      protected float progress;

      public SlottedItemIterator(Function<Accessor<?>, T> containerFinder, int fromIndex) {
         super(containerFinder, fromIndex);
      }

      protected abstract int getSlotCount(T var1);

      protected abstract ItemStack getItemInSlot(T var1, int var2);

      @Override
      public Stream<ItemStack> populate(T container, int amount) {
         int slotCount = this.getSlotCount(container);
         int toIndex = IntMath.saturatedAdd(this.currentIndex, amount);
         if (toIndex >= slotCount) {
            toIndex = slotCount;
            this.finished = true;
            this.progress = 1.0F;
         } else {
            this.progress = (float)(this.currentIndex - this.fromIndex) / (slotCount - this.fromIndex);
         }

         return IntStream.range(this.currentIndex, toIndex).mapToObj(slot -> this.getItemInSlot(container, slot));
      }

      @Override
      public float getCollectingProgress() {
         return this.progress;
      }
   }
}
