package com.mrcrayfish.configured.impl.neoforge;

import com.mrcrayfish.configured.client.screen.list.IListConfigValue;
import com.mrcrayfish.configured.client.screen.list.IListType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.ListValueSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import org.jetbrains.annotations.Nullable;

public class NeoForgeListValue<T> extends NeoForgeValue<List<T>> implements IListConfigValue<T> {
   @Nullable
   protected final Function<List<T>, List<T>> converter;

   public NeoForgeListValue(ConfigValue<List<T>> configValue, ValueSpec valueSpec) {
      super(configValue, valueSpec);
      this.converter = this.createConverter(configValue);
   }

   @Nullable
   private Function<List<T>, List<T>> createConverter(ConfigValue<List<T>> configValue) {
      List<T> original = (List<T>)configValue.get();
      if (original instanceof ArrayList) {
         return ArrayList::new;
      } else {
         return original instanceof LinkedList ? LinkedList::new : null;
      }
   }

   public void set(List<T> value) {
      this.valueSpec.correct(value);
      super.set((T)(new ArrayList<T>(value)));
   }

   @Nullable
   public List<T> getConverted() {
      return this.converter != null ? this.converter.apply(this.get()) : null;
   }

   @Override
   public IListType<T> getListType() {
      return null;
   }

   @Override
   public String createPropertyValue() {
      if (this.valueSpec instanceof ListValueSpec listSpec) {
         Supplier<?> supplier = listSpec.getNewElementSupplier();
         if (supplier != null) {
            Object newElement = supplier.get();
            if (newElement != null) {
               return newElement.toString();
            }
         }
      }

      return "";
   }
}
