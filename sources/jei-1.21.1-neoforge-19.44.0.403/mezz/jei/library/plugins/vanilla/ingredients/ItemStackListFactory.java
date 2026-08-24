package mezz.jei.library.plugins.vanilla.ingredients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IJeiClientConfigs;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.common.util.StackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class ItemStackListFactory {
   private static final Logger LOGGER = LogManager.getLogger();

   public static List<ItemStack> create(StackHelper stackHelper, ItemStackHelper itemStackHelper) {
      IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
      IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
      boolean showHidden = clientConfig.showHiddenIngredients().getValue();
      List<ItemStack> itemList = new ArrayList<>();
      Set<Object> itemUidSet = new HashSet<>();
      Minecraft minecraft = Minecraft.getInstance();
      FeatureFlagSet features = Optional.ofNullable(minecraft.player)
         .map(p -> p.connection)
         .<FeatureFlagSet>map(ClientPacketListener::enabledFeatures)
         .orElse(FeatureFlagSet.of());
      boolean hasOperatorItemsTabPermissions = showHidden
         || (Boolean)minecraft.options.operatorItemsTab().get()
         || Optional.of(minecraft).map(m -> m.player).<Boolean>map(Player::canUseGameMasterBlocks).orElse(false);
      ClientLevel level = minecraft.level;
      if (level == null) {
         throw new NullPointerException("minecraft.level must be set before JEI fetches ingredients");
      } else {
         RegistryAccess registryAccess = level.registryAccess();
         ItemDisplayParameters displayParameters = new ItemDisplayParameters(features, hasOperatorItemsTabPermissions, registryAccess);

         for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
            if (tab.getType() != Type.CATEGORY) {
               LOGGER.debug("Skipping creative tab: '{}' because it is type: {}", tab.getDisplayName().getString(), tab.getType());
            } else {
               try {
                  tab.buildContents(displayParameters);
               } catch (LinkageError | RuntimeException var19) {
                  LOGGER.error(
                     "Item Group crashed while building contents.Items from this group will be missing from the JEI ingredient list: {}",
                     tab.getDisplayName().getString(),
                     var19
                  );
                  continue;
               }

               Collection<ItemStack> displayItems;
               Collection<ItemStack> searchTabDisplayItems;
               try {
                  displayItems = tab.getDisplayItems();
                  searchTabDisplayItems = tab.getSearchTabDisplayItems();
               } catch (LinkageError | RuntimeException var18) {
                  LOGGER.error(
                     "Item Group crashed while getting search tab display items.Some items from this group will be missing from the JEI ingredient list: {}",
                     tab.getDisplayName().getString(),
                     var18
                  );
                  continue;
               }

               if (displayItems.isEmpty() && searchTabDisplayItems.isEmpty()) {
                  Level logLevel = isKnownEmptyTab(tab) ? Level.DEBUG : Level.WARN;
                  LOGGER.log(
                     logLevel,
                     "Item Group has no display items and no search tab display items. Items from this group will be missing from the JEI ingredient list. {}",
                     tab.getDisplayName().getString()
                  );
               } else {
                  addFromTab(displayItems, "displayItems", tab, stackHelper, itemStackHelper, itemList, itemUidSet);
                  if (!displayItems.equals(searchTabDisplayItems)) {
                     addFromTab(searchTabDisplayItems, "searchTabDisplayItems", tab, stackHelper, itemStackHelper, itemList, itemUidSet);
                  }
               }
            }
         }

         if (showHidden) {
            addItemsFromRegistries(stackHelper, itemList, itemUidSet, features);
         }

         return itemList;
      }
   }

   private static boolean isKnownEmptyTab(CreativeModeTab tab) {
      return tab.getDisplayName().getContents() instanceof TranslatableContents translatableContents && translatableContents.getKey().equals("itemGroup.op");
   }

   private static void addFromTab(
      Collection<ItemStack> tabDisplayItems,
      String displayType,
      CreativeModeTab tab,
      StackHelper stackHelper,
      ItemStackHelper itemStackHelper,
      List<ItemStack> itemList,
      Set<Object> itemUidSet
   ) {
      Set<Object> tabUidSet = new HashSet<>();
      int added = 0;
      Set<Object> duplicateInTab = new HashSet<>();
      int duplicateInTabCount = 0;

      for (ItemStack itemStack : tabDisplayItems) {
         if (itemStack.isEmpty()) {
            String errorInfo = itemStackHelper.getErrorInfo(itemStack);
            LOGGER.error("Found an empty itemStack in '{}' creative tab's {}: {}", tab, displayType, errorInfo);
         } else if (!itemStackHelper.isValidIngredient(itemStack)) {
            String errorInfo = itemStackHelper.getErrorInfo(itemStack);
            LOGGER.error("Ignoring ingredient in '{}' creative tab's {} that is considered invalid: {}", tab, displayType, errorInfo);
         } else if (!itemStackHelper.isIngredientOnServer(itemStack)) {
            String errorInfo = itemStackHelper.getErrorInfo(itemStack);
            LOGGER.warn("Ignoring ingredient in '{}' creative tab's {} that isn't on the server: {}", tab, displayType, errorInfo);
         } else {
            Object itemKey = safeGetUid(stackHelper, itemStack);
            if (itemKey != null) {
               if (tabUidSet.contains(itemKey)) {
                  duplicateInTab.add(itemKey);
                  duplicateInTabCount++;
               }

               if (itemUidSet.add(itemKey)) {
                  tabUidSet.add(itemKey);
                  itemList.add(itemStack);
                  added++;
               }
            }
         }
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(
            "Added {}/{} new items from '{}' creative tab's {}",
            StringUtils.leftPad(Integer.toString(added), 4, ' '),
            StringUtils.leftPad(Integer.toString(tabDisplayItems.size()), 4, ' '),
            tab.getDisplayName().getString(),
            displayType
         );
      }

      if (duplicateInTabCount > 0) {
         Level level = Services.PLATFORM.getModHelper().isInDev() ? Level.WARN : Level.DEBUG;
         LOGGER.log(
            level,
            "{} duplicate items were found in '{}' creative tab's: {}\nThis may indicate that these types of item need a subtype interpreter added to JEI:\n{}",
            duplicateInTabCount,
            tab.getDisplayName().getString(),
            displayType,
            duplicateInTab.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]"))
         );
      }
   }

   private static void addItemsFromRegistries(StackHelper stackHelper, List<ItemStack> itemList, Set<Object> itemUidSet, FeatureFlagSet features) {
      List<ItemStack> itemStacks = RegistryUtil.getRegistry(Registries.ITEM)
         .asLookup()
         .filterFeatures(features)
         .listElements()
         .<ItemStack>map(ItemStack::new)
         .filter(i -> !i.isEmpty())
         .toList();
      int added = 0;

      for (ItemStack itemStack : itemStacks) {
         Object itemKey = safeGetUid(stackHelper, itemStack);
         if (itemKey != null && itemUidSet.add(itemKey)) {
            itemList.add(itemStack);
            added++;
         }
      }

      LOGGER.debug(
         "Added {}/{} new items from the item registry (this is run because ShowHiddenItems is set to true in JEI's config)", added, itemStacks.size()
      );
      itemStacks = RegistryUtil.getRegistry(Registries.BLOCK)
         .asLookup()
         .filterFeatures(features)
         .listElements()
         .map(Reference::value)
         .<ItemStack>map(ItemStack::new)
         .filter(i -> !i.isEmpty())
         .toList();
      added = 0;

      for (ItemStack itemStackx : itemStacks) {
         Object itemKey = safeGetUid(stackHelper, itemStackx);
         if (itemKey != null && itemUidSet.add(itemKey)) {
            itemList.add(itemStackx);
            added++;
         }
      }

      LOGGER.debug(
         "Added {}/{} new items from the block registry (this is run because ShowHiddenItems is set to true in JEI's config)", added, itemStacks.size()
      );
   }

   @Nullable
   private static Object safeGetUid(StackHelper stackHelper, ItemStack stack) {
      try {
         return stackHelper.getUidForStack(stack, UidContext.Ingredient);
      } catch (LinkageError | RuntimeException var4) {
         String stackInfo = ErrorUtil.getItemStackInfo(stack);
         LOGGER.error("Couldn't get unique name for itemStack {}", stackInfo, var4);
         return null;
      }
   }
}
