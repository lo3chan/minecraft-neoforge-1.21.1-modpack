package net.cibernet.alchemancy.registries;

import java.util.function.UnaryOperator;
import net.cibernet.alchemancy.entity.CustomFallingBlock;
import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.cibernet.alchemancy.entity.InfusionFlask;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AlchemancyEntities {
   public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, "alchemancy");
   public static final DeferredHolder<EntityType<?>, EntityType<InfusedItemProjectile>> ITEM_PROJECTILE = register(
      "infused_item_projectile", InfusedItemProjectile::new, MobCategory.MISC, builder -> builder.sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CustomFallingBlock>> FALLING_BLOCK = register(
      "falling_block", CustomFallingBlock::new, MobCategory.MISC, builder -> builder.sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20)
   );
   public static final DeferredHolder<EntityType<?>, EntityType<InfusionFlask>> INFUSION_FLASK = register(
      "infusion_flask", InfusionFlask::new, MobCategory.MISC, builder -> builder.sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );

   private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(
      String key, EntityFactory<T> factory, MobCategory category, UnaryOperator<Builder<T>> params
   ) {
      return REGISTRY.register(key, () -> params.apply(Builder.of(factory, category)).build(key));
   }
}
