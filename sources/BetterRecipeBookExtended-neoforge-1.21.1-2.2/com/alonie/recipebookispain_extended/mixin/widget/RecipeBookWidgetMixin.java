package com.alonie.recipebookispain_extended.mixin.widget;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.util.RecipeBookDebugLogger;
import com.alonie.recipebookispain_extended.RecipeBookIsPain;
import com.alonie.recipebookispain_extended.RecipeBookIsPainExtendedConfig;
import com.alonie.recipebookispain_extended.access.CreativeTabButtonAccess;
import com.alonie.recipebookispain_extended.access.RecipeBookScrollAccess;
import com.alonie.recipebookispain_extended.access.RecipeGroupButtonPlacement;
import com.alonie.recipebookispain_extended.access.RecipeGroupButtonPlacementAccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RecipeBookComponent.class})
public abstract class RecipeBookWidgetMixin implements RecipeBookScrollAccess {
   @Shadow
   @Final
   @Mutable
   private List<RecipeBookTabButton> tabButtons;
   @Shadow
   private RecipeBookTabButton selectedTab;
   @Shadow
   protected Minecraft minecraft;
   @Shadow
   private int width;
   @Shadow
   private int height;
   @Shadow
   private int xOffset;
   @Shadow
   private boolean widthTooNarrow;
   @Shadow
   private ClientRecipeBook book;
   @Shadow
   protected RecipeBookMenu menu;
   @Shadow
   private boolean visible;
   @Unique
   private static final ResourceLocation TEX_PAGE_BTNS = ResourceLocation.fromNamespaceAndPath(
      "recipe-book-is-pain-extended", "textures/rbip/recipe_book_buttons.png"
   );
   @Unique
   private static final int VANILLA_BOOK_W = 147;
   @Unique
   private static final int VANILLA_BOOK_H = 166;
   @Unique
   private static final int TAB_W = 35;
   @Unique
   private static final int TAB_H = 27;
   @Unique
   private static final int ROT_TAB_W = 27;
   @Unique
   private static final int ROT_TAB_H = 35;
   @Unique
   private static final int LEFT_SLOTS = 6;
   @Unique
   private static final int HORIZ_STEP = 27;
   @Unique
   private static final int PAGE_BTN_W = 14;
   @Unique
   private static final int PAGE_BTN_H = 13;
   @Unique
   private List<RecipeBookTabButton> rbip$creativeButtons;
   @Unique
   private Map<RecipeBookTabButton, CreativeModeTab> rbip$buttonToTab;
   @Unique
   private RecipeBookTabButton rbip$pinnedTab;
   @Unique
   private List<RecipeBookTabButton> rbip$pageableTabs;
   @Unique
   private int rbip$page;
   @Unique
   private int rbip$pageCount = 1;
   @Unique
   private int rbip$pageControlX;
   @Unique
   private int rbip$pageControlY;
   @Unique
   private boolean rbip$tabsNeedBuild = true;
   @Unique
   private static final Map<RecipeBookCategories, Set<CreativeModeTab>> brbe$cachedRecipeTabs = new HashMap<>();
   @Unique
   private static int brbe$cachedGeneration = -1;
   @Unique
   private static boolean brbe$cachedUnlockAll;
   @Unique
   private static final int SCROLL_PADDING = 20;

   @Unique
   private boolean rbip$isExpanded() {
      return BetterRecipeBook.ctx().config().expandedRecipeBook && !this.widthTooNarrow && this.visible;
   }

   @Unique
   private int rbip$getBookW() {
      if (this.rbip$isExpanded()) {
         int invImageWidth = 176;
         int leftPos = ((RecipeBookComponentAccessor)this).updateScreenPositionInvoker(this.width, invImageWidth);
         int bookLeft = (this.width - 147) / 2 - this.xOffset;
         return leftPos + invImageWidth - bookLeft;
      } else {
         return 147;
      }
   }

   @Unique
   private int rbip$getTopSlots() {
      if (!this.rbip$isExpanded()) {
         return 5;
      } else {
         int bookW = this.rbip$getBookW();
         return Math.max(0, (bookW - 12) / 27);
      }
   }

   @Unique
   private int rbip$getBottomSlots() {
      return this.rbip$getTopSlots();
   }

   @Unique
   private static boolean rbip$isSearchCategory(RecipeBookCategories cat) {
      return cat == RecipeBookCategories.CRAFTING_SEARCH
         || cat == RecipeBookCategories.FURNACE_SEARCH
         || cat == RecipeBookCategories.SMOKER_SEARCH
         || cat == RecipeBookCategories.BLAST_FURNACE_SEARCH;
   }

   @Unique
   private void rbip$ensureFields() {
      if (this.rbip$creativeButtons == null) {
         this.rbip$creativeButtons = new ArrayList<>();
      }

      if (this.rbip$buttonToTab == null) {
         this.rbip$buttonToTab = new HashMap<>();
      }

      if (this.rbip$pageableTabs == null) {
         this.rbip$pageableTabs = new ArrayList<>();
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"initVisuals"}
   )
   private void rbip$injectCreativeTabs(CallbackInfo ci) {
      this.rbip$tabsNeedBuild = true;
      RecipeBookIsPain.activeCreativeTab = null;
      RecipeBookIsPain.activeFurnaceType = null;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"updateTabs"}
   )
   private void rbip$afterUpdateTabs(CallbackInfo ci) {
      if (RecipeBookIsPainExtendedConfig.enabled()) {
         this.rbip$ensureFields();
         if (!this.rbip$creativeButtons.isEmpty()) {
            this.rbip$rebuildTabList();
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"render"}
   )
   private void rbip$renderTail(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (RecipeBookIsPainExtendedConfig.enabled()) {
         if (this.visible) {
            int scroll = RecipeBookIsPain.rbip$consumeScroll();
            if (scroll != 0 && this.rbip$pageCount > 1) {
               this.rbip$scrollPages(mouseX, mouseY, scroll);
            }

            if (this.rbip$pageCount > 1) {
               this.rbip$drawPageControls(gui, mouseX, mouseY);
            }

            if (this.minecraft.screen != null) {
               for (RecipeBookTabButton btn : this.tabButtons) {
                  if (btn.visible && btn.isMouseOver(mouseX, mouseY)) {
                     CreativeModeTab tab = this.rbip$buttonToTab.get(btn);
                     if (tab != null) {
                        gui.renderTooltip(this.minecraft.font, tab.getDisplayName(), mouseX, mouseY);
                     } else if (btn.getCategory() == RecipeBookCategories.CRAFTING_SEARCH) {
                        gui.renderTooltip(this.minecraft.font, CreativeModeTabs.searchTab().getDisplayName(), mouseX, mouseY);
                     }
                     break;
                  }
               }

               if (this.rbip$pageCount > 1 && rbip$isInside(mouseX, mouseY, this.rbip$pageControlX, this.rbip$pageControlY, 14, 13)
                  || rbip$isInside(mouseX, mouseY, this.rbip$pageControlX + 15, this.rbip$pageControlY, 14, 13)) {
                  gui.renderTooltip(this.minecraft.font, Component.literal(this.rbip$page + 1 + "/" + this.rbip$pageCount), mouseX, mouseY);
               }
            }
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   private void rbip$hotReload(GuiGraphics g, int mx, int my, float d, CallbackInfo ci) {
      if (this.rbip$tabsNeedBuild) {
         this.rbip$tabsNeedBuild = false;
         if (RecipeBookIsPainExtendedConfig.enabled()) {
            this.rbip$buildCreativeTabs();
         }
      }

      if (RecipeBookIsPainExtendedConfig.reloadIfChanged()) {
         RecipeBookIsPain.LOGGER.info("[RBIP] Config changed — reload");
         this.rbip$creativeButtons.clear();
         this.rbip$buttonToTab.clear();
         this.rbip$pinnedTab = null;
         this.rbip$pageableTabs = List.of();
         this.rbip$page = 0;
         this.rbip$pageCount = 1;
         RecipeBookIsPain.activeCreativeTab = null;
         RecipeBookIsPain.activeFurnaceType = null;
         RecipeBookIsPain.onConfigChanged();
         RecipeBookIsPain.ensureInitialized();

         for (CreativeModeTab tab : RecipeBookIsPain.CRAFTING_LIST) {
            RecipeBookTabButton btn = new RecipeBookTabButton(RecipeBookCategories.UNKNOWN);
            ((CreativeTabButtonAccess)btn).rbip$setCreativeTab(tab);
            this.rbip$creativeButtons.add(btn);
            this.rbip$buttonToTab.put(btn, tab);
         }

         this.rbip$invokeUpdateTabs();
      }
   }

   @Invoker("updateTabs")
   public abstract void rbip$invokeUpdateTabs();

   @Unique
   private void rbip$buildCreativeTabs() {
      this.rbip$ensureFields();
      RecipeBookIsPain.ensureInitialized();
      List<CreativeModeTab> creativeTabs = RecipeBookIsPain.CRAFTING_LIST;
      if (creativeTabs.isEmpty()) {
         RecipeBookDebugLogger.onRbipTabsBuilt(0, 0);
      } else {
         this.rbip$creativeButtons.clear();
         this.rbip$buttonToTab.clear();

         for (CreativeModeTab tab : creativeTabs) {
            RecipeBookTabButton btn = new RecipeBookTabButton(RecipeBookCategories.UNKNOWN);
            ((CreativeTabButtonAccess)btn).rbip$setCreativeTab(tab);
            this.rbip$creativeButtons.add(btn);
            this.rbip$buttonToTab.put(btn, tab);
         }

         this.rbip$rebuildTabList();
         RecipeBookIsPain.LOGGER.info("[RBIP] {} creative tabs (deferred build)", this.rbip$creativeButtons.size());
         RecipeBookDebugLogger.onRbipTabsBuilt(this.rbip$creativeButtons.size(), creativeTabs.size());
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"mouseClicked"},
      cancellable = true
   )
   private void rbip$handleClick(double mx, double my, int btn, CallbackInfoReturnable<Boolean> cir) {
      this.rbip$ensureFields();
      if (RecipeBookIsPainExtendedConfig.enabled() && btn == 0) {
         if (this.visible) {
            if (this.rbip$pageCount > 1) {
               int pcx = this.rbip$pageControlX;
               int pcy = this.rbip$pageControlY;
               if (rbip$isInside(mx, my, pcx, pcy, 14, 13) && this.rbip$page > 0) {
                  this.rbip$page--;
                  this.rbip$applyPagination(false);
                  cir.setReturnValue(true);
                  return;
               }

               if (rbip$isInside(mx, my, pcx + 15, pcy, 14, 13) && this.rbip$page < this.rbip$pageCount - 1) {
                  this.rbip$page++;
                  this.rbip$applyPagination(false);
                  cir.setReturnValue(true);
                  return;
               }
            }

            for (RecipeBookTabButton b : this.rbip$creativeButtons) {
               if (b.visible && b.isMouseOver(mx, my)) {
                  CreativeModeTab tab = this.rbip$buttonToTab.get(b);
                  if (tab != null) {
                     RecipeBookIsPain.LOGGER.info("[RBIP] Selected: {}", tab.getDisplayName().getString());
                     if (this.selectedTab != null && this.selectedTab != b) {
                        this.selectedTab.setStateTriggered(false);
                     }

                     b.setStateTriggered(true);
                     this.selectedTab = b;
                     RecipeBookIsPain.activeCreativeTab = tab;
                     String furnaceType = null;
                     if (this.menu instanceof AbstractFurnaceMenu furnaceMenu) {
                        RecipeBookIsPain.activeFurnaceType = RecipeBookIsPain.detectFurnaceType(furnaceMenu);
                        furnaceType = RecipeBookIsPain.activeFurnaceType.name();
                     } else {
                        RecipeBookIsPain.activeFurnaceType = null;
                     }

                     RecipeBookDebugLogger.onRbipTabSelected(tab.getDisplayName().getString(), furnaceType);
                     ((RecipeBookComponentAccessor)this).updateCollectionsInvoker(false);
                     cir.setReturnValue(true);
                     return;
                  }
               }
            }

            if (this.rbip$pinnedTab != null && this.rbip$pinnedTab.visible && this.rbip$pinnedTab.isMouseOver(mx, my)) {
               RecipeBookIsPain.activeCreativeTab = null;
               RecipeBookIsPain.activeFurnaceType = null;
               if (this.selectedTab != null && this.selectedTab != this.rbip$pinnedTab) {
                  this.selectedTab.setStateTriggered(false);
               }
               this.rbip$pinnedTab.setStateTriggered(true);
               this.selectedTab = this.rbip$pinnedTab;
               RecipeBookIsPain.LOGGER.info("[RBIP] Selected Search Tab -> cleared activeCreativeTab");
               ((RecipeBookComponentAccessor)this).updateCollectionsInvoker(false);
               cir.setReturnValue(true);
               return;
            }
         }
      }
   }

   @Override
   public boolean rbip$scrollPages(double mouseX, double mouseY, double verticalAmount) {
      if (this.rbip$pageCount <= 1 || verticalAmount == 0.0) {
         return false;
      } else if (!this.rbip$isMouseOverAnyVisibleTab(mouseX, mouseY)) {
         return false;
      } else {
         int next = this.rbip$page + (verticalAmount > 0.0 ? -1 : 1);
         next = Math.max(0, Math.min(next, this.rbip$pageCount - 1));
         if (next != this.rbip$page) {
            this.rbip$page = next;
            this.rbip$applyPagination(false);
            return true;
         } else {
            return false;
         }
      }
   }

   @Unique
   private void rbip$rebuildTabList() {
      this.rbip$ensureFields();
      RecipeBookTabButton search = null;

      for (RecipeBookTabButton btn : this.tabButtons) {
         if (!(btn instanceof CreativeTabButtonAccess access && access.rbip$getCreativeTab() != null)
            && !this.rbip$buttonToTab.containsKey(btn)
            && rbip$isSearchCategory(btn.getCategory())
            && search == null) {
            search = btn;
         }
      }

      this.rbip$pinnedTab = search;
      this.rbip$pageableTabs = new ArrayList<>();
      Set<CreativeModeTab> tabsWithRecipes = this.rbip$getTabsWithRecipes();

      for (RecipeBookTabButton btnx : this.rbip$creativeButtons) {
         CreativeModeTab tab = this.rbip$buttonToTab.get(btnx);
         if (tab != null && tabsWithRecipes.contains(tab)) {
            this.rbip$pageableTabs.add(btnx);
         }
      }

      this.rbip$applyPagination(true);
   }

   @Unique
   private Set<CreativeModeTab> rbip$getTabsWithRecipes() {
      RecipeBookCategories category = this.rbip$getSearchCategory();
      boolean currentUnlockAll = BetterRecipeBook.ctx().config() != null && BetterRecipeBook.ctx().config().newRecipes.unlockAll;
      if (brbe$cachedGeneration != RecipeBookIsPain.recipeGeneration || brbe$cachedUnlockAll != currentUnlockAll) {
         brbe$cachedRecipeTabs.clear();
         brbe$cachedGeneration = RecipeBookIsPain.recipeGeneration;
         brbe$cachedUnlockAll = currentUnlockAll;
      }

      return brbe$cachedRecipeTabs.computeIfAbsent(category, k -> this.rbip$computeTabsWithRecipes());
   }

   @Unique
   private Set<CreativeModeTab> rbip$computeTabsWithRecipes() {
      Set<CreativeModeTab> result = new HashSet<>();
      if (this.book == null) {
         return result;
      } else {
         List<RecipeCollection> collections = this.book.getCollection(this.rbip$getSearchCategory());
         if (collections == null) {
            return result;
         } else {
            for (RecipeCollection col : collections) {
               for (RecipeHolder<?> holder : col.getRecipes()) {
                  ItemStack resultStack = holder.value()
                     .getResultItem(this.minecraft.level == null ? Minecraft.getInstance().level.registryAccess() : this.minecraft.level.registryAccess());
                  if (!resultStack.isEmpty()) {
                     CreativeModeTab tab = RecipeBookIsPain.getCreativeTabForItem(resultStack);
                     if (tab != null) {
                        result.add(tab);
                     }
                  }
               }
            }

            return result;
         }
      }
   }

   @Unique
   private RecipeBookCategories rbip$getSearchCategory() {
      if (this.menu instanceof AbstractFurnaceMenu furnaceMenu) {
         if (furnaceMenu instanceof SmokerMenu) {
            return RecipeBookCategories.SMOKER_SEARCH;
         } else {
            return furnaceMenu instanceof BlastFurnaceMenu ? RecipeBookCategories.BLAST_FURNACE_SEARCH : RecipeBookCategories.FURNACE_SEARCH;
         }
      } else {
         return RecipeBookCategories.CRAFTING_SEARCH;
      }
   }

   @Unique
   private int rbip$getTotalSlots() {
      if (!RecipeBookIsPainExtendedConfig.enabled()) {
         return 6;
      } else {
         return this.rbip$isExpanded() ? 6 + this.rbip$getTopSlots() + this.rbip$getBottomSlots() : RecipeBookIsPainExtendedConfig.bottomNumber();
      }
   }

   @Unique
   private void rbip$applyPagination(boolean followCurrentTab) {
      int pinnedCount = this.rbip$pinnedTab == null ? 0 : 1;
      int groupsPerPage = Math.max(1, this.rbip$getTotalSlots() - pinnedCount);
      int slot = 0;
      if (this.rbip$pinnedTab != null) {
         this.rbip$pinnedTab.visible = true;
         this.rbip$placeTab(this.rbip$pinnedTab, slot);
         slot++;
      }

      this.rbip$pageCount = Math.max(1, (this.rbip$pageableTabs.size() + groupsPerPage - 1) / groupsPerPage);
      if (this.rbip$pageCount <= 1) {
         this.rbip$page = 0;
         this.rbip$pageControlX = this.rbip$getPageControlX();
         this.rbip$pageControlY = this.rbip$getPageControlY();

         for (RecipeBookTabButton btn : this.rbip$pageableTabs) {
            btn.visible = true;
            this.rbip$placeTab(btn, slot++);
         }

         this.tabButtons = this.rbip$buildFinalButtonList();
      } else {
         if (followCurrentTab && this.selectedTab != null) {
            int idx = this.rbip$pageableTabs.indexOf(this.selectedTab);
            if (idx >= 0) {
               this.rbip$page = idx / groupsPerPage;
            }
         }

         this.rbip$page = Math.max(0, Math.min(this.rbip$page, this.rbip$pageCount - 1));
         int start = this.rbip$page * groupsPerPage;
         int end = Math.min(start + groupsPerPage, this.rbip$pageableTabs.size());

         for (int i = 0; i < this.rbip$pageableTabs.size(); i++) {
            RecipeBookTabButton btn = this.rbip$pageableTabs.get(i);
            if (i >= start && i < end) {
               btn.visible = true;
               this.rbip$placeTab(btn, slot++);
            } else {
               btn.visible = false;
               ((RecipeGroupButtonPlacementAccess)btn).rbip$setPlacement(RecipeGroupButtonPlacement.NORMAL);
               btn.setWidth(35);
               btn.setHeight(27);
            }
         }

         this.rbip$pageControlX = this.rbip$getPageControlX();
         this.rbip$pageControlY = this.rbip$getPageControlY();
         this.tabButtons = this.rbip$buildFinalButtonList();
      }
   }

   @Unique
   private List<RecipeBookTabButton> rbip$buildFinalButtonList() {
      List<RecipeBookTabButton> result = new ArrayList<>();
      if (this.rbip$pinnedTab != null) {
         result.add(this.rbip$pinnedTab);
      }

      result.addAll(this.rbip$pageableTabs);
      return result;
   }

   @Unique
   private void rbip$placeTab(RecipeBookTabButton btn, int slot) {
      int topSlots = this.rbip$getTopSlots();
      int bottomSlots = this.rbip$getBottomSlots();
      if (slot < 6) {
         ((RecipeGroupButtonPlacementAccess)btn).rbip$setPlacement(RecipeGroupButtonPlacement.NORMAL);
         btn.setX(this.rbip$getTabX());
         btn.setY(this.rbip$getTabY() + 27 * slot);
         btn.setWidth(35);
         btn.setHeight(27);
      } else if (slot < 6 + topSlots) {
         int s = slot - 6;
         ((RecipeGroupButtonPlacementAccess)btn).rbip$setPlacement(RecipeGroupButtonPlacement.TOP);
         btn.setX(this.rbip$getTopTabX(s));
         btn.setY(this.rbip$getTopTabY());
         btn.setWidth(27);
         btn.setHeight(35);
      } else if (slot < 6 + topSlots + bottomSlots) {
         int s = slot - 6 - topSlots;
         ((RecipeGroupButtonPlacementAccess)btn).rbip$setPlacement(RecipeGroupButtonPlacement.BOTTOM);
         btn.setX(this.rbip$getBottomTabX(s));
         btn.setY(this.rbip$getBottomTabY());
         btn.setWidth(27);
         btn.setHeight(35);
      } else {
         ((RecipeGroupButtonPlacementAccess)btn).rbip$setPlacement(RecipeGroupButtonPlacement.NORMAL);
         btn.setX(this.rbip$getTabX());
         btn.setY(this.rbip$getTabY() + 27 * slot);
         btn.setWidth(35);
         btn.setHeight(27);
      }
   }

   @Unique
   private int rbip$getBookX() {
      return (this.width - 147) / 2 - this.xOffset;
   }

   @Unique
   private int rbip$getBookY() {
      return (this.height - 166) / 2;
   }

   @Unique
   private int rbip$getTabX() {
      return this.rbip$getBookX() - 30;
   }

   @Unique
   private int rbip$getTabY() {
      return this.rbip$getBookY() + 3;
   }

   @Unique
   private int rbip$getPageControlX() {
      return this.rbip$getBookX() - 28;
   }

   @Unique
   private int rbip$getPageControlY() {
      return this.rbip$getBookY() - 12;
   }

   @Unique
   private int rbip$getHorizontalTabStartX() {
      int bookW = this.rbip$getBookW();
      int topSlots = this.rbip$getTopSlots();
      return this.rbip$getBookX() + (bookW - topSlots * 27) / 2;
   }

   @Unique
   private int rbip$getTopTabX(int slot) {
      return this.rbip$getHorizontalTabStartX() + slot * 27;
   }

   @Unique
   private int rbip$getTopTabY() {
      return this.rbip$getBookY() - 35 + 5;
   }

   @Unique
   private int rbip$getBottomTabX(int slot) {
      return this.rbip$getHorizontalTabStartX() + slot * 27;
   }

   @Unique
   private int rbip$getBottomTabY() {
      return this.rbip$getBookY() + 166 - 5;
   }

   @Unique
   private void rbip$drawPageControls(GuiGraphics gui, int mouseX, int mouseY) {
      int px = this.rbip$pageControlX;
      int py = this.rbip$pageControlY;
      boolean la = this.rbip$page > 0;
      boolean lh = la && rbip$isInside(mouseX, mouseY, px, py, 14, 13);
      int lu = lh ? 28 : 0;
      int lv = la ? 0 : 13;
      gui.blit(TEX_PAGE_BTNS, px, py, lu, lv, 14, 13, 256, 256);
      boolean ra = this.rbip$page < this.rbip$pageCount - 1;
      boolean rh = ra && rbip$isInside(mouseX, mouseY, px + 15, py, 14, 13);
      int ru = 14 + (rh ? 28 : 0);
      int rv = ra ? 0 : 13;
      gui.blit(TEX_PAGE_BTNS, px + 15, py, ru, rv, 14, 13, 256, 256);
   }

   @Unique
   private static boolean rbip$isInside(double x, double y, int l, int t, int w, int h) {
      return x >= l && x < l + w && y >= t && y < t + h;
   }

   @Unique
   private static int[] rbip$box(int l, int t, int r, int b) {
      return new int[]{l, t, r, b};
   }

   @Unique
   private static int rbip$width(int[] box) {
      return box[2] - box[0];
   }

   @Unique
   private static int rbip$height(int[] box) {
      return box[3] - box[1];
   }

   @Unique
   private boolean rbip$isMouseOverAnyVisibleTab(double mouseX, double mouseY) {
      int[] topArea = null;
      int[] bottomArea = null;

      for (RecipeBookTabButton btn : this.tabButtons) {
         if (btn.visible) {
            RecipeGroupButtonPlacement p = ((RecipeGroupButtonPlacementAccess)btn).rbip$getPlacement();
            if (p == RecipeGroupButtonPlacement.TOP) {
               int[] expanded = this.rbip$expandBox(btn);
               topArea = topArea == null ? expanded : this.rbip$mergeBoxes(topArea, expanded);
            } else if (p == RecipeGroupButtonPlacement.BOTTOM) {
               int[] expanded = this.rbip$expandBox(btn);
               bottomArea = bottomArea == null ? expanded : this.rbip$mergeBoxes(bottomArea, expanded);
            } else if (rbip$isInside(mouseX, mouseY, btn.getX(), btn.getY(), btn.getWidth(), btn.getHeight())) {
               return true;
            }
         }
      }

      return topArea != null && rbip$isInside(mouseX, mouseY, topArea[0], topArea[1], rbip$width(topArea), rbip$height(topArea))
         ? true
         : bottomArea != null && rbip$isInside(mouseX, mouseY, bottomArea[0], bottomArea[1], rbip$width(bottomArea), rbip$height(bottomArea));
   }

   @Unique
   private int[] rbip$expandBox(RecipeBookTabButton btn) {
      return rbip$box(btn.getX(), btn.getY() - 20, btn.getX() + btn.getWidth(), btn.getY() + btn.getHeight() + 20);
   }

   @Unique
   private int[] rbip$mergeBoxes(int[] a, int[] b) {
      return rbip$box(Math.min(a[0], b[0]), Math.min(a[1], b[1]), Math.max(a[2], b[2]), Math.max(a[3], b[3]));
   }
}
