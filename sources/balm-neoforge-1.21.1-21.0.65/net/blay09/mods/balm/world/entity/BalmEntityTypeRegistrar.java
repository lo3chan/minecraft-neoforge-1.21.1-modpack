package net.blay09.mods.balm.world.entity;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType.Builder;

public interface BalmEntityTypeRegistrar {
   <T extends Entity> BalmEntityTypeRegistration<T> register(String var1, Supplier<Builder<T>> var2);

   void addAlias(ResourceLocation var1, ResourceLocation var2);

   void addAlias(String var1, String var2);
}
