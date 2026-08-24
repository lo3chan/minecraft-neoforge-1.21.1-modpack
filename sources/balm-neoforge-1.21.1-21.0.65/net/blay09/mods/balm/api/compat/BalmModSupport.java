package net.blay09.mods.balm.api.compat;

import net.blay09.mods.balm.api.compat.hudinfo.BalmModSupportHudInfo;
import net.blay09.mods.balm.api.compat.trinkets.BalmModSupportTrinkets;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.BalmModSupportRecipeViewer;

public interface BalmModSupport {
   BalmModSupportTrinkets trinkets();

   BalmModSupportHudInfo hudInfo();

   BalmModSupportRecipeViewer recipeViewers();
}
