package net.blay09.mods.balm.neoforge.compat;

import java.util.function.Supplier;
import net.blay09.mods.balm.api.BalmRuntime;
import net.blay09.mods.balm.api.compat.BalmModSupport;
import net.blay09.mods.balm.api.compat.hudinfo.BalmModSupportHudInfo;
import net.blay09.mods.balm.api.compat.trinkets.BalmModSupportTrinkets;
import net.blay09.mods.balm.common.compat.NoopTrinkets;
import net.blay09.mods.balm.common.compat.TrinketsMultiplexer;
import net.blay09.mods.balm.common.compat.hudinfo.CommonBalmModSupportHudInfo;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.BalmModSupportRecipeViewer;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.CommonBalmModSupportRecipeViewer;

public class NeoForgeBalmModSupport implements BalmModSupport {
   private final Supplier<BalmModSupportTrinkets> trinkets;
   private final CommonBalmModSupportHudInfo hudInfo = new CommonBalmModSupportHudInfo();
   private final CommonBalmModSupportRecipeViewer recipeViewers = new CommonBalmModSupportRecipeViewer();

   public NeoForgeBalmModSupport(BalmRuntime<?> runtime) {
      this.trinkets = runtime.<BalmModSupportTrinkets>modProxy()
         .with("curios", "net.blay09.mods.balm.neoforge.compat.trinkets.CuriosIntegration")
         .withMultiplexer(TrinketsMultiplexer::new)
         .withFallback(new NoopTrinkets())
         .buildLazily();
   }

   @Override
   public BalmModSupportTrinkets trinkets() {
      return this.trinkets.get();
   }

   @Override
   public BalmModSupportHudInfo hudInfo() {
      return this.hudInfo;
   }

   @Override
   public BalmModSupportRecipeViewer recipeViewers() {
      return this.recipeViewers;
   }
}
