package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.util.MixinUtils;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({DataComponentHolder.class})
public interface DataComponentHolderMixin {
   @WrapOperation(
      method = {"get"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/core/component/DataComponentMap;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
      )}
   )
   default <T> T get(DataComponentMap instance, DataComponentType<? extends T> dataComponentType, Operation<T> original) {
      return MixinUtils.getDataComponent(this, dataComponentType, (T)original.call(new Object[]{instance, dataComponentType}));
   }

   @WrapOperation(
      method = {"getOrDefault"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/core/component/DataComponentMap;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
      )}
   )
   default <T> T getOrDefault(DataComponentMap instance, DataComponentType<? extends T> component, T defaultValue, Operation<T> original) {
      return MixinUtils.getDataComponent(this, component, (T)original.call(new Object[]{instance, component, defaultValue}));
   }

   @WrapOperation(
      method = {"has"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/core/component/DataComponentMap;has(Lnet/minecraft/core/component/DataComponentType;)Z"
      )}
   )
   default boolean has(DataComponentMap instance, DataComponentType<?> component, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{instance, component}) || MixinUtils.getDataComponent(this, component, null) != null;
   }
}
