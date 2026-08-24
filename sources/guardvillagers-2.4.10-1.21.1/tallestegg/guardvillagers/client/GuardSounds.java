package tallestegg.guardvillagers.client;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GuardSounds {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, "guardvillagers");
   public static final Holder<SoundEvent> GUARD_AMBIENT = SOUNDS.register(
      "entity.guard.ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("guardvillagers", "entity.guard.ambient"))
   );
   public static final Holder<SoundEvent> GUARD_DEATH = SOUNDS.register(
      "entity.guard.death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("guardvillagers", "entity.guard.death"))
   );
   public static final Holder<SoundEvent> GUARD_HURT = SOUNDS.register(
      "entity.guard.hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("guardvillagers", "entity.guard.hurt"))
   );
   public static final Holder<SoundEvent> GUARD_YES = SOUNDS.register(
      "entity.guard.yes", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("guardvillagers", "entity.guard.yes"))
   );
   public static final Holder<SoundEvent> GUARD_NO = SOUNDS.register(
      "entity.guard.no", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("guardvillagers", "entity.guard.no"))
   );
}
