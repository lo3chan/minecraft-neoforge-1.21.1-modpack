package net.blay09.mods.balm.client.renderer.entity;

import java.util.function.Supplier;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface BalmEntityRendererRegistrar {
   <T extends Entity> void register(Holder<? extends EntityType<? extends T>> var1, EntityRendererProvider<? super T> var2);

   <T extends Entity> void register(String var1, Supplier<? extends EntityType<? extends T>> var2, EntityRendererProvider<? super T> var3);
}
