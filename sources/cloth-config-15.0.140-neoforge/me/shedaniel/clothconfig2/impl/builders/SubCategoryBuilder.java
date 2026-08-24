package me.shedaniel.clothconfig2.impl.builders;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SubCategoryBuilder
   extends FieldBuilder<List<AbstractConfigListEntry>, SubCategoryListEntry, SubCategoryBuilder>
   implements List<AbstractConfigListEntry> {
   private final List<AbstractConfigListEntry> entries;
   private Function<List<AbstractConfigListEntry>, Optional<Component[]>> tooltipSupplier = list -> Optional.empty();
   private boolean expanded = false;

   public SubCategoryBuilder(Component resetButtonKey, Component fieldNameKey) {
      super(resetButtonKey, fieldNameKey);
      this.entries = Lists.newArrayList();
   }

   @Override
   public void requireRestart(boolean requireRestart) {
      throw new UnsupportedOperationException();
   }

   public SubCategoryBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
      this.tooltipSupplier = list -> tooltipSupplier.get();
      return this;
   }

   public SubCategoryBuilder setTooltipSupplier(Function<List<AbstractConfigListEntry>, Optional<Component[]>> tooltipSupplier) {
      this.tooltipSupplier = tooltipSupplier;
      return this;
   }

   public SubCategoryBuilder setTooltip(Optional<Component[]> tooltip) {
      this.tooltipSupplier = list -> tooltip;
      return this;
   }

   public SubCategoryBuilder setTooltip(Component... tooltip) {
      this.tooltipSupplier = list -> Optional.ofNullable(tooltip);
      return this;
   }

   public SubCategoryBuilder setExpanded(boolean expanded) {
      this.expanded = expanded;
      return this;
   }

   @NotNull
   public SubCategoryListEntry build() {
      SubCategoryListEntry entry = new SubCategoryListEntry(this.getFieldNameKey(), this.entries, this.expanded);
      entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
      return this.finishBuilding(entry);
   }

   @Override
   public int size() {
      return this.entries.size();
   }

   @Override
   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.entries.contains(o);
   }

   @NotNull
   @Override
   public Iterator<AbstractConfigListEntry> iterator() {
      return this.entries.iterator();
   }

   @Override
   public Object[] toArray() {
      return this.entries.toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return (T[])this.entries.toArray(a);
   }

   public boolean add(AbstractConfigListEntry abstractConfigListEntry) {
      return this.entries.add(abstractConfigListEntry);
   }

   @Override
   public boolean remove(Object o) {
      return this.entries.remove(o);
   }

   @Override
   public boolean containsAll(@NotNull Collection<?> c) {
      return this.entries.containsAll(c);
   }

   @Override
   public boolean addAll(@NotNull Collection<? extends AbstractConfigListEntry> c) {
      return this.entries.addAll(c);
   }

   @Override
   public boolean addAll(int index, @NotNull Collection<? extends AbstractConfigListEntry> c) {
      return this.entries.addAll(index, c);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return this.entries.removeAll(c);
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return this.entries.retainAll(c);
   }

   @Override
   public void clear() {
      this.entries.clear();
   }

   public AbstractConfigListEntry get(int index) {
      return this.entries.get(index);
   }

   public AbstractConfigListEntry set(int index, AbstractConfigListEntry element) {
      return this.entries.set(index, element);
   }

   public void add(int index, AbstractConfigListEntry element) {
      this.entries.add(index, element);
   }

   public AbstractConfigListEntry remove(int index) {
      return this.entries.remove(index);
   }

   @Override
   public int indexOf(Object o) {
      return this.entries.indexOf(o);
   }

   @Override
   public int lastIndexOf(Object o) {
      return this.entries.lastIndexOf(o);
   }

   @NotNull
   @Override
   public ListIterator<AbstractConfigListEntry> listIterator() {
      return this.entries.listIterator();
   }

   @NotNull
   @Override
   public ListIterator<AbstractConfigListEntry> listIterator(int index) {
      return this.entries.listIterator(index);
   }

   @NotNull
   @Override
   public List<AbstractConfigListEntry> subList(int fromIndex, int toIndex) {
      return this.entries.subList(fromIndex, toIndex);
   }
}
