package traben.entity_texture_features.features.property_reading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFDirectory;
import traben.entity_texture_features.utils.ETFUtils2;

public class TrueRandomProvider implements ETFApi.ETFVariantSuffixProvider {
   private final int[] suffixes;
   private final String packname;
   protected ETFApi.ETFVariantSuffixProvider.EntityRandomSeedFunction entityRandomSeedFunction = ETFEntityRenderState::optifineId;

   private TrueRandomProvider(String secondPack, int[] suffixes) {
      this.suffixes = suffixes;
      this.packname = secondPack;
   }

   @Nullable
   public static TrueRandomProvider of(ResourceLocation vanillaIdentifier) {
      ResourceManager resources = Minecraft.getInstance().getResourceManager();
      ResourceLocation second = ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, 2));
      if (second == null) {
         return null;
      } else {
         String secondPack = resources.getResource(second).<String>map(Resource::sourcePackId).orElse(null);
         String vanillaPack = resources.getResource(vanillaIdentifier).<String>map(Resource::sourcePackId).orElse(null);
         if (secondPack != null && secondPack.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(secondPack, vanillaPack))) {
            List<Integer> suffixes = new ArrayList<>();
            suffixes.add(1);
            suffixes.add(2);
            boolean notAllowSkip = !ETF.config().getConfig().optifine_allowWeirdSkipsInTrueRandom;

            for (int i = 3; i < suffixes.size() + 10; i++) {
               if (ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, i)) != null) {
                  suffixes.add(i);
               } else if (notAllowSkip) {
                  break;
               }
            }

            if (suffixes.get(suffixes.size() - 1) != suffixes.size()) {
               ETFUtils2.logWarn(
                  "Random suffixes ["
                     + suffixes
                     + "] are not sequential for "
                     + vanillaIdentifier
                     + " in pack "
                     + secondPack
                     + " this is not recommended but has been enabled in the optifine compat settings."
               );
            }

            return new TrueRandomProvider(secondPack, suffixes.stream().mapToInt(ix -> ix).toArray());
         } else {
            return null;
         }
      }
   }

   @Nullable
   public String getPackName() {
      return this.packname;
   }

   @Override
   public boolean entityCanUpdate(UUID uuid) {
      return false;
   }

   @Override
   public Set<Integer> getAllSuffixes() {
      return Arrays.stream(this.suffixes).boxed().collect(Collectors.toSet());
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public int getSuffixForETFEntity(ETFEntityRenderState entityToBeTested) {
      return entityToBeTested == null ? 0 : this.suffixes[Math.abs(this.entityRandomSeedFunction.toInt(entityToBeTested)) % this.suffixes.length];
   }

   @Override
   public void setRandomSupplier(ETFApi.ETFVariantSuffixProvider.EntityRandomSeedFunction entityRandomSeedFunction) {
      if (entityRandomSeedFunction != null) {
         this.entityRandomSeedFunction = entityRandomSeedFunction;
      }
   }
}
