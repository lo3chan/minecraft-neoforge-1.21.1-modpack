package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface MutableDataComponentHolderFunctions extends ComponentFunctions {
   default MutableDataComponentHolder kjs$getComponentHolder() {
      return (MutableDataComponentHolder)this;
   }

   @Override
   default DataComponentMap kjs$getComponentMap() {
      return this.kjs$getComponentHolder().getComponents();
   }

   @HideFromJS
   default <T> MutableDataComponentHolderFunctions kjs$override(DataComponentType<T> type, @Nullable T value) {
      MutableDataComponentHolder h = this.kjs$getComponentHolder();
      if (value != null && !Undefined.isUndefined(value)) {
         h.set(type, value);
      } else {
         h.remove(type);
      }

      return this;
   }

   default MutableDataComponentHolderFunctions kjs$set(Context cx, DataComponentMap components) {
      MutableDataComponentHolder h = this.kjs$getComponentHolder();
      h.applyComponents(components);
      return this;
   }

   default MutableDataComponentHolderFunctions kjs$patch(Context cx, DataComponentPatch components) {
      MutableDataComponentHolder h = this.kjs$getComponentHolder();
      h.applyComponents(components);
      return this;
   }
}
