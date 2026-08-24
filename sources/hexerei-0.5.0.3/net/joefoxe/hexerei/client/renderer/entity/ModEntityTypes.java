package net.joefoxe.hexerei.client.renderer.entity;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.HexereiPaintingEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModChestBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "hexerei",
   bus = Bus.MOD
)
public class ModEntityTypes {
   public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "hexerei");
   public static final DeferredHolder<EntityType<?>, EntityType<BroomEntity>> BROOM = ENTITY_TYPES.register(
      "broom",
      () -> Builder.of(BroomEntity::new, MobCategory.MISC)
         .sized(1.175F, 0.3625F)
         .setShouldReceiveVelocityUpdates(true)
         .setTrackingRange(10)
         .updateInterval(1)
         .build(HexereiUtil.getResource("broom").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ModBoatEntity>> HEXEREI_BOAT = ENTITY_TYPES.register(
      "boat",
      () -> Builder.of(ModBoatEntity::new, MobCategory.MISC).sized(1.175F, 0.3625F).setTrackingRange(10).build(HexereiUtil.getResource("boat").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<ModChestBoatEntity>> HEXEREI_CHEST_BOAT = ENTITY_TYPES.register(
      "chest_boat",
      () -> Builder.of(ModChestBoatEntity::new, MobCategory.MISC)
         .sized(1.175F, 0.3625F)
         .setTrackingRange(10)
         .build(HexereiUtil.getResource("chest_boat").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CrowEntity>> CROW = ENTITY_TYPES.register(
      "crow",
      () -> Builder.of(CrowEntity::new, MobCategory.CREATURE)
         .sized(0.375F, 0.5F)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .build(HexereiUtil.getResource("crow").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<OwlEntity>> OWL = ENTITY_TYPES.register(
      "owl",
      () -> Builder.of(OwlEntity::new, MobCategory.CREATURE)
         .sized(0.5F, 0.65F)
         .setTrackingRange(64)
         .setUpdateInterval(1)
         .build(HexereiUtil.getResource("owl").toString())
   );
   public static final DeferredHolder<EntityType<?>, EntityType<HexereiPaintingEntity>> BOOK_CANVAS = ENTITY_TYPES.register(
      "book_canvas",
      () -> Builder.of(HexereiPaintingEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(10)
         .updateInterval(2147483647)
         .build(HexereiUtil.getResource("book_canvas").toString())
   );

   public static void register(IEventBus eventBus) {
      ENTITY_TYPES.register(eventBus);
   }

   @SubscribeEvent
   public static void addEntityAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)CROW.get(), CrowEntity.createAttributes());
      event.put((EntityType)OWL.get(), OwlEntity.createAttributes());
   }

   static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> addEntityWithEgg(
      String name, int color1, int color2, float width, float height, EntityFactory<T> factory, MobCategory kind
   ) {
      EntityType<T> type = Builder.of(factory, kind).setTrackingRange(64).setUpdateInterval(1).sized(width, height).build("hexerei:" + name);
      ModItems.ITEMS.register(name + "_spawn_egg", () -> new SpawnEggItem(type, color1, color2, new Properties()));
      return ENTITY_TYPES.register(name, () -> type);
   }
}
