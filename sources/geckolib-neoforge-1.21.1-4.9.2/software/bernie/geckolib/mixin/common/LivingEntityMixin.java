package software.bernie.geckolib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.GeckoLibConstants;

@Mixin({LivingEntity.class})
public class LivingEntityMixin {
   @WrapOperation(
      method = {"equipmentHasChanged"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
      )}
   )
   public boolean geckolib$allowLazyStackIdParity(ItemStack remoteStack, ItemStack localStack, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{remoteStack, localStack})
         && ((Number)remoteStack.getOrDefault(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get(), -2147483648))
            .equals(localStack.getOrDefault(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get(), -2147483648));
   }
}
