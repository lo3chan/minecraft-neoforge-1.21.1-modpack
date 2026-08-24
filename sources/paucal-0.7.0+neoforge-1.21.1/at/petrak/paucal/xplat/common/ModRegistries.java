package at.petrak.paucal.xplat.common;

import at.petrak.paucal.api.PaucalAPI;
import at.petrak.paucal.xplat.common.advancement.BeContributorTrigger;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;

public class ModRegistries {
   public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create("paucal", Registries.TRIGGER_TYPE);
   public static final RegistrySupplier<BeContributorTrigger> BE_CONTRIBUTOR_TRIGGER = TRIGGER_TYPES.register("be_contributor", BeContributorTrigger::new);
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create("paucal", Registries.SOUND_EVENT);
   public static final RegistrySupplier<SoundEvent> HEADPAT_SOUND = SOUNDS.register(
      "headpat", () -> SoundEvent.createVariableRangeEvent(PaucalAPI.modLoc("dummy_headpat"))
   );
   public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create("paucal", Registries.CUSTOM_STAT);
   public static final RegistrySupplier<ResourceLocation> PLAYERS_PATTED = makeCustomStat("players_patted", StatFormatter.DEFAULT);
   public static final RegistrySupplier<ResourceLocation> HEADPATS_GOTTEN = makeCustomStat("headpats_gotten", StatFormatter.DEFAULT);

   private static RegistrySupplier<ResourceLocation> makeCustomStat(String pKey, StatFormatter pFormatter) {
      ResourceLocation rl = PaucalAPI.modLoc(pKey);
      return STATS.register(pKey, () -> rl);
   }
}
