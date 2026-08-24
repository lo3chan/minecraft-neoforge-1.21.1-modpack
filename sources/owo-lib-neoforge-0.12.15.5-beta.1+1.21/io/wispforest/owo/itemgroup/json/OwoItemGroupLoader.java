package io.wispforest.owo.itemgroup.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import io.wispforest.owo.moddata.ModDataConsumer;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.callback.AddCallback;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class OwoItemGroupLoader implements ModDataConsumer {
   public static final OwoItemGroupLoader INSTANCE = new OwoItemGroupLoader();
   private static final Map<ResourceLocation, JsonObject> BUFFERED_GROUPS = new HashMap<>();

   private OwoItemGroupLoader() {
   }

   public static void onGroupCreated(CreativeModeTab group) {
      ResourceLocation groupId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group);
      if (BUFFERED_GROUPS.containsKey(groupId)) {
         INSTANCE.acceptParsedFile(groupId, BUFFERED_GROUPS.remove(groupId));
      }
   }

   @Override
   public void acceptParsedFile(ResourceLocation id, JsonObject json) {
      ResourceLocation targetGroupId = ResourceLocation.parse(GsonHelper.getAsString(json, "target_group"));
      CreativeModeTab searchGroup = null;

      for (CreativeModeTab group : CreativeModeTabs.allTabs()) {
         if (BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group).equals(targetGroupId)) {
            searchGroup = group;
            break;
         }
      }

      if (searchGroup == null) {
         BUFFERED_GROUPS.put(targetGroupId, json);
      } else {
         CreativeModeTab targetGroup = searchGroup;
         JsonArray tabsArray = GsonHelper.getAsJsonArray(json, "tabs", new JsonArray());
         ArrayList<ItemGroupTab> tabs = new ArrayList<>();
         tabsArray.forEach(
            jsonElement -> {
               if (jsonElement.isJsonObject()) {
                  JsonObject tabObject = jsonElement.getAsJsonObject();
                  ResourceLocation texture = ResourceLocation.parse(GsonHelper.getAsString(tabObject, "texture", ItemGroupTab.DEFAULT_TEXTURE.toString()));
                  TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(GsonHelper.getAsString(tabObject, "tag")));
                  Item icon = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse(GsonHelper.getAsString(tabObject, "icon")));
                  String name = GsonHelper.getAsString(tabObject, "name");
                  tabs.add(
                     new ItemGroupTab(
                        Icon.of(icon),
                        OwoItemGroup.ButtonDefinition.tooltipFor(targetGroup, "tab", name),
                        (context, entries) -> BuiltInRegistries.ITEM.stream().filter(item -> item.builtInRegistryHolder().is(tag)).forEach(entries::accept),
                        texture,
                        false
                     )
                  );
               }
            }
         );
         JsonArray buttonsArray = GsonHelper.getAsJsonArray(json, "buttons", new JsonArray());
         ArrayList<ItemGroupButton> buttons = new ArrayList<>();
         buttonsArray.forEach(jsonElement -> {
            if (jsonElement.isJsonObject()) {
               JsonObject buttonObject = jsonElement.getAsJsonObject();
               String link = GsonHelper.getAsString(buttonObject, "link");
               String name = GsonHelper.getAsString(buttonObject, "name");
               int u = GsonHelper.getAsInt(buttonObject, "texture_u");
               int v = GsonHelper.getAsInt(buttonObject, "texture_v");
               int textureWidth = GsonHelper.getAsInt(buttonObject, "texture_width", 64);
               int textureHeight = GsonHelper.getAsInt(buttonObject, "texture_height", 64);
               String textureId = GsonHelper.getAsString(buttonObject, "texture", null);
               ResourceLocation texture = textureId == null ? ItemGroupButton.ICONS_TEXTURE : ResourceLocation.parse(textureId);
               buttons.add(ItemGroupButton.link(targetGroup, Icon.of(texture, u, v, textureWidth, textureHeight), name, link));
            }
         });
         if (targetGroup instanceof WrapperGroup wrapper) {
            wrapper.addTabs(tabs);
            wrapper.addButtons(buttons);
            if (GsonHelper.getAsBoolean(json, "extend", false)) {
               wrapper.markExtension();
            }
         } else {
            WrapperGroup wrapperx = new WrapperGroup(targetGroup, targetGroupId, tabs, buttons);
            wrapperx.initialize();
            if (GsonHelper.getAsBoolean(json, "extend", false)) {
               wrapperx.markExtension();
            }

            BuiltInRegistries.ITEM
               .stream()
               .filter(item -> ((OwoItemExtensions)item).owo$group() == targetGroup)
               .forEach(item -> ((OwoItemExtensions)item).owo$setGroup((Supplier<CreativeModeTab>)(() -> wrapper)));
         }
      }
   }

   @Override
   public String getDataSubdirectory() {
      return "item_group_tabs";
   }

   public static void initItemGroupCallback() {
      BuiltInRegistries.CREATIVE_MODE_TAB.addCallback((AddCallback)(registry, rawId, id, group) -> onGroupCreated(group));
   }
}
