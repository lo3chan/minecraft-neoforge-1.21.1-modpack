package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.essence.Essence;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public class AlchemancyEssence {
   private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath("alchemancy", "essence");
   public static final DeferredRegister<Essence> REGISTRY = DeferredRegister.create(KEY, "alchemancy");
   private static final Registry<Essence> SUPPLIER = REGISTRY.makeRegistry(registryBuilder -> registryBuilder.defaultKey(KEY).sync(true));
   public static final DeferredHolder<Essence, Essence> PYRO = REGISTRY.register("pyro", () -> new Essence(16727808));
   public static final DeferredHolder<Essence, Essence> AERO = REGISTRY.register("aero", () -> new Essence(16773264));
   public static final DeferredHolder<Essence, Essence> TERRA = REGISTRY.register("terra", () -> new Essence(9529914));
   public static final DeferredHolder<Essence, Essence> HYDRO = REGISTRY.register("hydro", () -> new Essence(25565));
   public static final DeferredHolder<Essence, Essence> ELECTRO = REGISTRY.register("electro", () -> new Essence(65530));
   public static final DeferredHolder<Essence, Essence> CRYO = REGISTRY.register("cryo", () -> new Essence(7527423));
   public static final DeferredHolder<Essence, Essence> PLASMA = REGISTRY.register("plasma", () -> new Essence(6684416));
   public static final DeferredHolder<Essence, Essence> DENDRO = REGISTRY.register("dendro", () -> new Essence(9553408));

   @Nullable
   public static Essence getEssence(ResourceLocation key) {
      return (Essence)SUPPLIER.get(key);
   }

   public static ResourceLocation getKeyFor(Essence Essence) {
      return SUPPLIER.getKey(Essence);
   }
}
