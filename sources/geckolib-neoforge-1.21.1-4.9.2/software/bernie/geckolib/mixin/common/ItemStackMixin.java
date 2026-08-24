package software.bernie.geckolib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.InternalUtil;

@Mixin({ItemStack.class})
public class ItemStackMixin {
   @WrapOperation(
      method = {"split"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"
      )}
   )
   public ItemStack geckolib$removeGeckolibIdOnCopy(ItemStack instance, int count, Operation<ItemStack> original) {
      ItemStack copy = (ItemStack)original.call(new Object[]{instance, count});
      if (count < instance.getCount() && copy.has(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get())) {
         copy.remove(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get());
      }

      return copy;
   }

   @WrapOperation(
      method = {"isSameItemSameComponents"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"
      )}
   )
   private static boolean geckolib$skipGeckolibIdOnCompare(Object a, Object b, Operation<Boolean> original) {
      if ((Boolean)original.call(new Object[]{a, b})) {
         return true;
      } else {
         return a instanceof PatchedDataComponentMap components && b instanceof PatchedDataComponentMap components2
            ? InternalUtil.areComponentsMatchingIgnoringGeckoLibId(components, components2)
            : false;
      }
   }
}
