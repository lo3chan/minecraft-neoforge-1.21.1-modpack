package vazkii.psi.common.attribute.base;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "psi"
)
public final class ModAttributes {
   public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, "psi");
   public static final DeferredHolder<Attribute, Attribute> TOTAL_PSI = ATTRIBUTES.register(
      "total_psi", () -> new RangedAttribute("attribute.psi.total_psi", 5000.0, 0.0, 2.147483647E9).setSyncable(true)
   );
   public static final DeferredHolder<Attribute, Attribute> REGEN = ATTRIBUTES.register(
      "regen", () -> new RangedAttribute("attribute.psi.regen", 25.0, 0.0, 2.147483647E9).setSyncable(true)
   );

   @SubscribeEvent
   public static void addAttributesToPlayer(EntityAttributeModificationEvent event) {
      event.add(EntityType.PLAYER, TOTAL_PSI);
      event.add(EntityType.PLAYER, REGEN);
   }
}
