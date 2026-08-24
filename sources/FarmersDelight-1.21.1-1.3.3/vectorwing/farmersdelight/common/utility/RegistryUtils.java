package vectorwing.farmersdelight.common.utility;

import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegistryUtils {
   public static RegistryUtils.EnchantmentEffectComponents createEnchantmentEffectComponents(String modid) {
      return new RegistryUtils.EnchantmentEffectComponents(modid);
   }

   public static class EnchantmentEffectComponents extends DeferredRegister<DataComponentType<?>> {
      protected EnchantmentEffectComponents(String namespace) {
         super(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, namespace);
      }

      public <D> DeferredHolder<DataComponentType<?>, DataComponentType<D>> registerComponentType(String name, UnaryOperator<Builder<D>> builder) {
         return this.register(name, () -> builder.apply(DataComponentType.builder()).build());
      }
   }
}
