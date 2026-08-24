package net.cibernet.alchemancy.properties;

import java.util.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber({Dist.CLIENT})
public class LightningBoltProperty extends Property {
   private static final Random random = new Random();
   private static float sparkColor = 0.0F;

   @Override
   public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target) {
      LightningBolt lightningbolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(target.level());
      if (lightningbolt != null) {
         lightningbolt.moveTo(target.position());
         lightningbolt.setCause(user instanceof ServerPlayer ? (ServerPlayer)user : null);
         target.level().addFreshEntity(lightningbolt);
         this.damageOrConsumeItem(target.level(), user, weapon, EquipmentSlot.MAINHAND, 20);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ARGB32.lerp(sparkColor, 28325, 11927551);
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   private static void onClientTick(Pre event) {
      if (random.nextFloat() < (sparkColor > 0.0F ? 0.075F : 0.025F)) {
         sparkColor = 1.0F;
      } else {
         sparkColor = Math.max(0.0F, sparkColor - 0.033333335F);
      }
   }
}
