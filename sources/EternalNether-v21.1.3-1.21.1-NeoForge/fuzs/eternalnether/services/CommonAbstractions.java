package fuzs.eternalnether.services;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface CommonAbstractions {
   CommonAbstractions INSTANCE = (CommonAbstractions)ServiceProviderHelper.load(CommonAbstractions.class);

   boolean isPiglinCurrency(ItemStack var1);

   boolean canDisableShield(ItemStack var1, ItemStack var2, LivingEntity var3, LivingEntity var4);
}
