package io.wispforest.owo.ext;

import java.util.Objects;
import java.util.Set;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.DataComponentMap.Builder.SimpleMap;
import net.minecraft.core.component.DataComponentPatch.Builder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class DerivedComponentMap implements DataComponentMap {
   private final DataComponentMap base;
   private final PatchedDataComponentMap delegate;

   public DerivedComponentMap(DataComponentMap base) {
      this.base = base;
      this.delegate = new PatchedDataComponentMap(base);
   }

   public static DataComponentMap reWrapIfNeeded(DataComponentMap original) {
      return (DataComponentMap)(original instanceof DerivedComponentMap derived ? new DerivedComponentMap(derived.base) : original);
   }

   public void derive(ItemStack owner) {
      this.delegate.restorePatch(DataComponentPatch.EMPTY);
      Builder builder = DataComponentPatch.builder();
      owner.getItem().deriveStackComponents(owner.getComponents(), builder);
      this.delegate.restorePatch(builder.build());
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> type) {
      return (T)this.delegate.get(type);
   }

   public Set<DataComponentType<?>> keySet() {
      return this.delegate.keySet();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof DerivedComponentMap thatDerived) {
         return Objects.equals(this.base, thatDerived.base);
      } else {
         return o instanceof SimpleMap simpleComponentMap ? Objects.equals(this.base, simpleComponentMap) : o == EMPTY && this.base == EMPTY;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.base);
   }
}
