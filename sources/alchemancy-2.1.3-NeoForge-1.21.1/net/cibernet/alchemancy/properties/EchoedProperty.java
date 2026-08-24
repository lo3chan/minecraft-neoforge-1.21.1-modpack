package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.cibernet.alchemancy.data.save.AlchemancyServerData;
import net.cibernet.alchemancy.registries.AlchemancyDataAttachments;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.EchoEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class EchoedProperty extends Property {
   public static final int ECHO_DURATION = 20;

   @Override
   public void modifyAttackDamage(Entity user, ItemStack weapon, Pre event) {
      EchoEffect.applyEffectToEntity(
         event.getEntity(), user, weapon, event.getSource().getSourcePosition(), event.getOriginalDamage() * 0.25F, 20, EchoEffect.EffectType.DAMAGE
      );
   }

   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      EchoEffect.applyEffectToEntity(target, user, weapon, damageSource.getSourcePosition(), 0.0F, 20, EchoEffect.EffectType.ATTACK);
   }

   @Override
   public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target) {
      EchoEffect.applyEffectToEntity(target, user, weapon, user == null ? target.position() : user.position(), 0.0F, 20, EchoEffect.EffectType.CRIT);
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      EchoEffect.applyEffectToEntity(target, source, stack, damageSource.getSourcePosition(), 0.0F, 20, EchoEffect.EffectType.ACTIVATE);
   }

   @Override
   public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack) {
      EchoEffect.applyBlockEffectToEntity(target, level, position, stack, 20);
   }

   @SubscribeEvent
   private static void onEntityTick(Post event) {
      Entity target = event.getEntity();
      ArrayList<EchoEffect> activeEchoes = new ArrayList<>((Collection<? extends EchoEffect>)target.getData(AlchemancyDataAttachments.ECHO_EFFECTS));
      boolean dirty = false;
      long timer = AlchemancyServerData.getGlobalTimer();
      if (!(target instanceof LivingEntity living && living.isDeadOrDying())) {
         for (EchoEffect echo : List.copyOf(activeEchoes)) {
            if (target.level() instanceof ServerLevel serverLevel && timer == echo.triggerAtTimestamp() - Math.min(8L, timer - echo.startTime())) {
               serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, target.getX(), target.getY(0.5), target.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
               serverLevel.playSeededSound(
                  null,
                  target.position().x(),
                  target.position().y(),
                  target.position().z(),
                  (SoundEvent)AlchemancySoundEvents.ECHOED.value(),
                  target.getSoundSource(),
                  1.0F,
                  1.0F,
                  target.getRandom().nextLong()
               );
            }

            if (timer >= echo.triggerAtTimestamp()) {
               echo.triggerServerside(target);
               activeEchoes.remove(echo);
               dirty = true;
            }
         }

         if (dirty) {
            target.setData(AlchemancyDataAttachments.ECHO_EFFECTS, activeEchoes);
         }
      }
   }

   @Override
   public int getPriority() {
      return 2147483647;
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.5F, 675936, 675936, 2744299);
   }
}
