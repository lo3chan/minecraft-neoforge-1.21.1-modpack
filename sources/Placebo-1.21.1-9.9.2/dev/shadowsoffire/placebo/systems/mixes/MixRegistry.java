package dev.shadowsoffire.placebo.systems.mixes;

import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.PlaceboClient;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionBrewing.Mix;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class MixRegistry extends DynamicRegistry<JsonMix<?>> {
   public static final MixRegistry INSTANCE = new MixRegistry();

   public MixRegistry() {
      super(Placebo.LOGGER, "brewing_mixes", true, false);
   }

   @Override
   protected void registerBuiltinCodecs() {
      this.registerDefaultCodec(Placebo.loc("mix"), JsonMix.CODEC);
   }

   @Override
   protected void beginReload(DynamicRegistry.ReloadType type) {
      for (PotionBrewing brewing : resolveBrewing()) {
         this.removeAll(brewing);
      }

      super.beginReload(type);
   }

   @Override
   protected void onReload(DynamicRegistry.ReloadType type) {
      for (PotionBrewing brewing : resolveBrewing()) {
         this.addAll(brewing);
      }

      super.onReload(type);
   }

   public static void applyMixes() {
      for (PotionBrewing brewing : resolveBrewing()) {
         INSTANCE.addAll(brewing);
      }
   }

   private static List<PotionBrewing> resolveBrewing() {
      List<PotionBrewing> registries = new ArrayList<>();
      if (FMLEnvironment.dist.isClient()) {
         registries.add(PlaceboClient.getBrewingRegistry());
      }

      if (ServerLifecycleHooks.getCurrentServer() != null) {
         registries.add(ServerLifecycleHooks.getCurrentServer().potionBrewing());
      }

      return registries;
   }

   private static List<Mix<?>> getMixList(PotionBrewing brewing, JsonMix.Type type) {
      return switch (type) {
         case POTION -> brewing.potionMixes;
         case CONTAINER -> brewing.containerMixes;
      };
   }

   private static void makeMutable(PotionBrewing brewing) {
      brewing.containerMixes = new ArrayList(brewing.containerMixes);
      brewing.potionMixes = new ArrayList(brewing.potionMixes);
   }

   private void removeAll(@Nullable PotionBrewing brewing) {
      if (brewing != null) {
         makeMutable(brewing);
         this.getValues().forEach(mix -> getMixList(brewing, mix.type()).remove(mix.mix()));
      }
   }

   private void addAll(@Nullable PotionBrewing brewing) {
      if (brewing != null) {
         makeMutable(brewing);
         this.getValues().forEach(mix -> getMixList(brewing, mix.type()).add(mix.mix()));
      }
   }
}
