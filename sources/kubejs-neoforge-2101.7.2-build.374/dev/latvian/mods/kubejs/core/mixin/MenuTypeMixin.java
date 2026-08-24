package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.MenuTypeKJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({MenuType.class})
public abstract class MenuTypeMixin implements MenuTypeKJS {
   @Unique
   private ResourceKey<MenuType<?>> kjs$registryKey;
   @Unique
   private String kjs$id;

   @Override
   public ResourceKey<MenuType<?>> kjs$getKey() {
      if (this.kjs$registryKey == null) {
         this.kjs$registryKey = MenuTypeKJS.super.kjs$getKey();
      }

      return this.kjs$registryKey;
   }

   @Override
   public String kjs$getId() {
      if (this.kjs$id == null) {
         this.kjs$id = MenuTypeKJS.super.kjs$getId();
      }

      return this.kjs$id;
   }
}
