package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class EntityHelper {
   private EntityHelper() {
   }

   public static boolean canEquip(ItemStack itemStack, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
      Objects.requireNonNull(itemStack, "item stack is null");
      Objects.requireNonNull(equipmentSlot, "equipment slot is null");
      Objects.requireNonNull(livingEntity, "living entity is null");
      return ProxyImpl.get().canEquip(itemStack, equipmentSlot, livingEntity);
   }

   @Deprecated
   @Nullable
   public static MobSpawnType getMobSpawnReason(Entity entity) {
      Objects.requireNonNull(entity, "entity is null");
      return entity instanceof Mob mob ? ProxyImpl.get().getMobSpawnReason(mob) : null;
   }

   public static boolean isMobGriefingAllowed(ServerLevel serverLevel, @Nullable Entity entity) {
      Objects.requireNonNull(serverLevel, "server level is null");
      return ProxyImpl.get().isMobGriefingAllowed(serverLevel, entity);
   }

   public static Entity getPartEntityParent(Entity entity) {
      Objects.requireNonNull(entity, "entity is null");
      return ProxyImpl.get().getPartEntityParent(entity);
   }

   public static boolean isFakePlayer(ServerPlayer serverPlayer) {
      Objects.requireNonNull(serverPlayer, "server player is null");
      return ProxyImpl.get().isFakePlayer(serverPlayer);
   }

   public static boolean isPiglinCurrency(ItemStack itemStack) {
      return ProxyImpl.get().isPiglinCurrency(itemStack);
   }
}
