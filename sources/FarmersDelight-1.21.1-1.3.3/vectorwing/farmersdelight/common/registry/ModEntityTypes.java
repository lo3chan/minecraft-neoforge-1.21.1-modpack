package vectorwing.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.entity.RottenTomatoEntity;

public class ModEntityTypes {
   public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "farmersdelight");
   public static final Supplier<EntityType<RottenTomatoEntity>> ROTTEN_TOMATO = ENTITIES.register(
      "rotten_tomato",
      () -> Builder.of(RottenTomatoEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("rotten_tomato")
   );
}
