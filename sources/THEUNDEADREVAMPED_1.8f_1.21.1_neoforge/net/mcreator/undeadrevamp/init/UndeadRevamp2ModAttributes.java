package net.mcreator.undeadrevamp.init;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class UndeadRevamp2ModAttributes {
   public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, "undead_revamp2");
   public static final DeferredHolder<Attribute, Attribute> CHEROATTACKSPEED = REGISTRY.register(
      "cheroattackspeed", () -> new RangedAttribute("attribute.undead_revamp2.cheroattackspeed", 12.0, 1.0, 20.0).setSyncable(true)
   );
   public static final DeferredHolder<Attribute, Attribute> RETURNVAULEUNDEAD = REGISTRY.register(
      "returnvauleundead", () -> new RangedAttribute("attribute.undead_revamp2.returnvauleundead", 1.0, 0.0, 3.0).setSyncable(true)
   );
   public static final DeferredHolder<Attribute, Attribute> BOSSLOADER = REGISTRY.register(
      "bossloader", () -> new RangedAttribute("attribute.undead_revamp2.bossloader", 0.0, 0.0, 1.0).setSyncable(true)
   );

   @SubscribeEvent
   public static void addAttributes(EntityAttributeModificationEvent event) {
      List.of(
            (EntityType)UndeadRevamp2ModEntities.THEBEARTAMER.get(),
            (EntityType)UndeadRevamp2ModEntities.THEHEAVY.get(),
            (EntityType)UndeadRevamp2ModEntities.THEROD.get(),
            (EntityType)UndeadRevamp2ModEntities.THEWOLF.get()
         )
         .stream()
         .filter(DefaultAttributes::hasSupplier)
         .map(entityType -> (EntityType)entityType)
         .collect(Collectors.toList())
         .forEach(entity -> event.add(entity, CHEROATTACKSPEED));
      List.of((EntityType)UndeadRevamp2ModEntities.CLOGGER.get())
         .stream()
         .filter(DefaultAttributes::hasSupplier)
         .map(entityType -> (EntityType)entityType)
         .collect(Collectors.toList())
         .forEach(entity -> event.add(entity, RETURNVAULEUNDEAD));
      List.of((EntityType)UndeadRevamp2ModEntities.CLOGGER.get())
         .stream()
         .filter(DefaultAttributes::hasSupplier)
         .map(entityType -> (EntityType)entityType)
         .collect(Collectors.toList())
         .forEach(entity -> event.add(entity, BOSSLOADER));
   }
}
