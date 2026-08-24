package net.blay09.mods.balm.core;

import java.util.function.Consumer;
import net.blay09.mods.balm.api.BalmRuntime;
import net.blay09.mods.balm.api.command.BalmArgumentTypeRegistrar;
import net.blay09.mods.balm.api.module.BalmModule;
import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistrar;
import net.blay09.mods.balm.core.particles.BalmParticleTypeRegistrar;
import net.blay09.mods.balm.platform.attachment.BalmDataAttachmentTypeRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmResourceConditionRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmResourceReloadListenerRegistrar;
import net.blay09.mods.balm.stats.BalmCustomStatRegistrar;
import net.blay09.mods.balm.world.entity.BalmEntityTypeRegistrar;
import net.blay09.mods.balm.world.entity.ai.village.poi.BalmPoiTypeRegistrar;
import net.blay09.mods.balm.world.entity.npc.villager.BalmVillagerTradeRegistrar;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.crafting.BalmRecipeTypeRegistrar;
import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class BalmRegistrars {
   private final BalmRuntime<?> runtime;
   private final String namespace;

   public BalmRegistrars(BalmRuntime<?> runtime, String namespace) {
      this.namespace = namespace;
      this.runtime = runtime;
   }

   public void menuTypes(Consumer<BalmMenuTypeRegistrar> initializer) {
      this.runtime.menuTypes(this.namespace, initializer);
   }

   public void entityTypes(Consumer<BalmEntityTypeRegistrar> initializer) {
      this.runtime.entityTypes(this.namespace, initializer);
   }

   public void particleTypes(Consumer<BalmParticleTypeRegistrar> initializer) {
      this.runtime.particleTypes(this.namespace, initializer);
   }

   public void customStats(Consumer<BalmCustomStatRegistrar> initializer) {
      this.runtime.customStats(this.namespace, initializer);
   }

   public void argumentTypes(Consumer<BalmArgumentTypeRegistrar> initializer) {
      this.runtime.argumentTypes(this.namespace, initializer);
   }

   public void resourceReloadListeners(Consumer<BalmResourceReloadListenerRegistrar> initializer) {
      this.runtime.resourceReloadListeners(this.namespace, initializer);
   }

   public void resourceConditions(Consumer<BalmResourceConditionRegistrar> initializer) {
      this.runtime.resourceConditions(this.namespace, initializer);
   }

   public void items(Consumer<BalmItemRegistrar> initializer) {
      this.runtime.items(this.namespace, initializer);
   }

   public void recipeTypes(Consumer<BalmRecipeTypeRegistrar> initializer) {
      this.runtime.recipeTypes(this.namespace, initializer);
   }

   public void dataComponentTypes(Consumer<BalmDataComponentTypeRegistrar> initializer) {
      this.runtime.dataComponentTypes(this.namespace, initializer);
   }

   public void dataAttachmentTypes(Consumer<BalmDataAttachmentTypeRegistrar> initializer) {
      this.runtime.dataAttachmentTypes(this.namespace, initializer);
   }

   public void creativeModeTabs(Consumer<BalmCreativeModeTabRegistrar> initializer) {
      this.runtime.creativeModeTabs(this.namespace, initializer);
   }

   public void blocks(Consumer<BalmBlockRegistrar> initializer) {
      this.runtime.blocks(this.namespace, initializer);
   }

   public void blockEntityTypes(Consumer<BalmBlockEntityTypeRegistrar> initializer) {
      this.runtime.blockEntityTypes(this.namespace, initializer);
   }

   public void poiTypes(Consumer<BalmPoiTypeRegistrar> initializer) {
      this.runtime.poiTypes(this.namespace, initializer);
   }

   public void villagerTrades(Consumer<BalmVillagerTradeRegistrar> initializer) {
      this.runtime.villagerTrades(this.namespace, initializer);
   }

   public BalmRegistrar registrar() {
      return this.runtime.registrar();
   }

   public <T> BalmRegistrar.Scoped<T> registrar(ResourceKey<? extends Registry<T>> registryKey) {
      return this.runtime.registrar(registryKey, this.namespace);
   }

   public <T> void registrar(ResourceKey<? extends Registry<T>> registryKey, Consumer<BalmRegistrar.Scoped<T>> initializer) {
      initializer.accept(this.runtime.registrar(registryKey, this.namespace));
   }

   public void registerModule(BalmModule module) {
      this.runtime.registerModule(this, module);
   }
}
