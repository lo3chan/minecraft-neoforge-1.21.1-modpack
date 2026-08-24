package jeresources.api;

import jeresources.api.conditionals.LightLevel;
import jeresources.api.drop.LootDrop;
import jeresources.api.render.IMobRenderHook;
import jeresources.api.render.IScissorHook;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootTable;

public interface IMobRegistry {
   void register(LivingEntity var1, LightLevel var2, int var3, int var4, String[] var5, ResourceKey<LootTable> var6);

   void register(LivingEntity var1, LightLevel var2, int var3, int var4, ResourceKey<LootTable> var5);

   void register(LivingEntity var1, LightLevel var2, int var3, String[] var4, ResourceKey<LootTable> var5);

   void register(LivingEntity var1, LightLevel var2, int var3, ResourceKey<LootTable> var4);

   void register(LivingEntity var1, LightLevel var2, String[] var3, ResourceKey<LootTable> var4);

   void register(LivingEntity var1, LightLevel var2, ResourceKey<LootTable> var3);

   void register(LivingEntity var1, ResourceKey<LootTable> var2);

   void register(LivingEntity var1, LightLevel var2, int var3, int var4, String[] var5, LootDrop... var6);

   void register(LivingEntity var1, LightLevel var2, int var3, int var4, LootDrop... var5);

   void register(LivingEntity var1, LightLevel var2, int var3, String[] var4, LootDrop... var5);

   void register(LivingEntity var1, LightLevel var2, int var3, LootDrop... var4);

   void register(LivingEntity var1, LightLevel var2, String[] var3, LootDrop... var4);

   void register(LivingEntity var1, LightLevel var2, LootDrop... var3);

   void register(LivingEntity var1, LootDrop... var2);

   void registerScissorHook(Class var1, IScissorHook var2);

   void registerRenderHook(Class<? extends LivingEntity> var1, IMobRenderHook var2);
}
