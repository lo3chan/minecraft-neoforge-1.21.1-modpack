package net.blay09.mods.balm.neoforge.world.entity.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.blay09.mods.balm.world.entity.internal.AbstractBalmEntityTypeRegistrarImpl;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

public class NeoForgeBalmEntityTypeRegistrar extends AbstractBalmEntityTypeRegistrarImpl {
   private final String namespace;

   public NeoForgeBalmEntityTypeRegistrar(BalmRegistrar registrar, String namespace) {
      super(registrar, namespace);
      this.namespace = namespace;
   }

   @Override
   protected <T extends Entity> void registerDefaultAttributes(Holder<EntityType<T>> entityType, Supplier<Builder> attributes) {
      NeoForgeBalmEntityTypeRegistrar.Registrations registrations = this.getActiveRegistrations();
      registrations.attributeSuppliers.put(entityType, () -> attributes.get().build());
   }

   @Override
   protected <T extends Entity> void registerSpawnPlacement(
      Holder<EntityType<T>> entityType, SpawnPlacementType spawnPlacementType, Types heightmapType, Supplier<SpawnPredicate<T>> attributesFunction
   ) {
      NeoForgeBalmEntityTypeRegistrar.Registrations registrations = this.getActiveRegistrations();
      registrations.spawnPlacements
         .add(new NeoForgeBalmEntityTypeRegistrar.SpawnPlacementRegistration<>(entityType, spawnPlacementType, heightmapType, attributesFunction.get()));
   }

   private NeoForgeBalmEntityTypeRegistrar.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespace, NeoForgeBalmEntityTypeRegistrar.Registrations.class);
   }

   public static class Registrations {
      public final Map<Holder<EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier>> attributeSuppliers = new HashMap<>();
      public final List<NeoForgeBalmEntityTypeRegistrar.SpawnPlacementRegistration<? extends Entity>> spawnPlacements = new ArrayList<>();

      @SubscribeEvent
      public void registerAttributes(EntityAttributeCreationEvent event) {
         for (Entry<Holder<EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier>> entry : this.attributeSuppliers.entrySet()) {
            event.put((EntityType)entry.getKey().value(), entry.getValue().get());
         }
      }

      @SubscribeEvent
      public void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
         for (NeoForgeBalmEntityTypeRegistrar.SpawnPlacementRegistration<? extends Entity> entry : this.spawnPlacements) {
            event.register((EntityType)entry.entityType.value(), entry.spawnPlacementType, entry.heightmapType, entry.predicate, Operation.REPLACE);
         }
      }
   }

   public record SpawnPlacementRegistration<T extends Entity>(
      Holder<EntityType<T>> entityType, SpawnPlacementType spawnPlacementType, Types heightmapType, SpawnPredicate<? extends Entity> predicate
   ) {
   }
}
