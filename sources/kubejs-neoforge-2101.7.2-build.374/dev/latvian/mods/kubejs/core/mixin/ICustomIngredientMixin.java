package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.CustomIngredientKJS;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ICustomIngredient.class})
public interface ICustomIngredientMixin extends CustomIngredientKJS {
   @Shadow
   @Override
   Stream<ItemStack> getItems();
}
