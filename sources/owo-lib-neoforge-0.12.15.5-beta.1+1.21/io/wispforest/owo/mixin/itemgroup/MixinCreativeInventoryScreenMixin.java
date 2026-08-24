package io.wispforest.owo.mixin.itemgroup;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(
   value = {CreativeModeInventoryScreen.class},
   priority = 1100
)
public abstract class MixinCreativeInventoryScreenMixin {
   @Shadow
   private static CreativeModeTab selectedTab;
   private static final Int2ObjectMap<CreativeModeTab> owo$selectedTabForPage = new Int2ObjectOpenHashMap();
   private static boolean owo$calledFromInit = false;
   @Shadow(
      remap = false
   )
   @Final
   private List<CreativeTabsScreenPage> pages;
   @Shadow(
      remap = false
   )
   private CreativeTabsScreenPage currentPage;

   @Shadow
   protected abstract void selectTab(CreativeModeTab var1);

   @Unique
   private int currentPageIndex() {
      return this.pages.indexOf(this.currentPage);
   }
}
