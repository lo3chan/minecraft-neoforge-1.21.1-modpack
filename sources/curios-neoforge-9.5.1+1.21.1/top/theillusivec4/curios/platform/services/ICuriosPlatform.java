package top.theillusivec4.curios.platform.services;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.ISlotType;

public interface ICuriosPlatform {
   Map<String, ISlotType> getItemStackSlots(ItemStack var1, @Nullable LivingEntity var2);

   boolean makesPiglinsNeutral(ItemStack var1, LivingEntity var2);

   boolean canWalkOnPowderedSnow(ItemStack var1, LivingEntity var2);

   boolean isEnderMask(ItemStack var1, Player var2, EnderMan var3);
}
