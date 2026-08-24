package com.mcwfurnitures.kikoz.init;

import com.mcwfurnitures.kikoz.storage.ChairEntity;
import com.mcwfurnitures.kikoz.storage.ChairRenderer;
import com.mcwfurnitures.kikoz.storage.CouchEntity;
import com.mcwfurnitures.kikoz.storage.CouchRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityInit {
   public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, "mcwfurnitures");
   public static final DeferredHolder<EntityType<?>, EntityType<ChairEntity>> CHAIR = REGISTER.register(
      "chair", () -> Builder.of(ChairEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).build("mcwfurnitures:chair")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CouchEntity>> COUCH = REGISTER.register(
      "couch", () -> Builder.of(CouchEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).build("mcwfurnitures:couch")
   );

   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)CHAIR.get(), ChairRenderer::new);
      event.registerEntityRenderer((EntityType)COUCH.get(), CouchRenderer::new);
   }
}
