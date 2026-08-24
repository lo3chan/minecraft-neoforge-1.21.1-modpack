package fuzs.eternalnether.world.entity.monster;

import com.google.common.collect.ImmutableMap;
import fuzs.eternalnether.init.ModSoundEvents;
import java.util.Map;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;

public class Wex extends Vex {
   private static final Map<SoundEvent, SoundEvent> SOUND_EVENTS = ImmutableMap.of(
      SoundEvents.VEX_AMBIENT,
      (SoundEvent)ModSoundEvents.WEX_AMBIENT.value(),
      SoundEvents.VEX_DEATH,
      (SoundEvent)ModSoundEvents.WEX_DEATH.value(),
      SoundEvents.VEX_HURT,
      (SoundEvent)ModSoundEvents.WEX_HURT.value(),
      SoundEvents.VEX_CHARGE,
      (SoundEvent)ModSoundEvents.WEX_CHARGE.value()
   );

   public Wex(EntityType<? extends Vex> entityType, Level level) {
      super(entityType, level);
   }

   protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficultyInstance) {
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.ATTACK_DAMAGE, 4.0);
   }

   public void playSound(SoundEvent event, float volume, float pitch) {
      super.playSound(SOUND_EVENTS.getOrDefault(event, event), volume, pitch);
   }
}
