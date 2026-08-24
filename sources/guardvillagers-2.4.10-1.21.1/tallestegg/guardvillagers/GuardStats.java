package tallestegg.guardvillagers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GuardStats {
   public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(Registries.CUSTOM_STAT, "guardvillagers");
   public static final DeferredHolder<ResourceLocation, ResourceLocation> GUARDS_MADE = STATS.register(
      "guards_made", () -> ResourceLocation.fromNamespaceAndPath("guardvillagers", "guards_made")
   );
}
