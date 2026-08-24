package fuzs.puzzleslib.impl.core.proxy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface EntityProxy {
   boolean canEquip(ItemStack var1, EquipmentSlot var2, LivingEntity var3);

   @Nullable
   MobSpawnType getMobSpawnReason(Mob var1);

   boolean isMobGriefingAllowed(ServerLevel var1, @Nullable Entity var2);

   Entity getPartEntityParent(Entity var1);

   boolean isFakePlayer(ServerPlayer var1);

   boolean isPiglinCurrency(ItemStack var1);
}
