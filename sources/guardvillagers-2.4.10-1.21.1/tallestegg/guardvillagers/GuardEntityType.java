package tallestegg.guardvillagers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tallestegg.guardvillagers.common.entities.Guard;

public class GuardEntityType {
   public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "guardvillagers");
   public static final DeferredHolder<EntityType<?>, EntityType<Guard>> GUARD = ENTITIES.register(
      "guard",
      () -> Builder.of(Guard::new, MobCategory.MISC).sized(0.6F, 1.9F).ridingOffset(-0.7F).setShouldReceiveVelocityUpdates(true).build("guardvillagersguard")
   );
}
