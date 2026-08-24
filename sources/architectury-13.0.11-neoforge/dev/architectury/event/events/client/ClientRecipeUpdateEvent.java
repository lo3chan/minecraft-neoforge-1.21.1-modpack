package dev.architectury.event.events.client;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ClientRecipeUpdateEvent {
   Event<ClientRecipeUpdateEvent> EVENT = EventFactory.createLoop();

   void update(RecipeManager var1);
}
