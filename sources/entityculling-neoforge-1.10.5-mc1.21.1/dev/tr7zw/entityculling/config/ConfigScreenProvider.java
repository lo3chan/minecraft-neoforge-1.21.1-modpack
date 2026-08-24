package dev.tr7zw.entityculling.config;

import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.versionless.Config;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen.OptionInstance;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WListPanel;
import dev.tr7zw.trender.gui.widget.WTabPanel;
import dev.tr7zw.trender.gui.widget.WTextField;
import dev.tr7zw.trender.gui.widget.WToggleButton;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.trender.gui.widget.icon.ItemIcon;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import lombok.Generated;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public final class ConfigScreenProvider {
   public static Screen createConfigScreen(Screen parent) {
      return new ConfigScreenProvider.ConfigScreen(parent).createScreen();
   }

   @Generated
   private ConfigScreenProvider() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   private static class ConfigScreen extends AbstractConfigScreen {
      public ConfigScreen(Screen previous) {
         super(ComponentProvider.translatable("text.entityculling.title"), previous);
         WGridPanel root = new WGridPanel(8);
         root.setBackgroundPainter(BackgroundPainter.VANILLA);
         root.setInsets(Insets.ROOT_PANEL);
         this.setRootPanel(root);
         WTabPanel wTabPanel = new WTabPanel();
         EntityCullingModBase inst = EntityCullingModBase.instance;
         List<OptionInstance> generalOptions = new ArrayList<>();
         generalOptions.add(
            this.getOnOffOption(
               "text.entityculling.renderNametagsThroughWalls", () -> inst.config.renderNametagsThroughWalls, b -> inst.config.renderNametagsThroughWalls = b
            )
         );
         generalOptions.add(this.getOnOffOption("text.entityculling.tickCulling", () -> inst.config.tickCulling, b -> inst.config.tickCulling = b));
         generalOptions.add(this.getOnOffOption("text.entityculling.disableF3", () -> inst.config.disableF3, b -> inst.config.disableF3 = b));
         generalOptions.add(
            this.getOnOffOption("text.entityculling.skipEntityCulling", () -> inst.config.skipEntityCulling, b -> inst.config.skipEntityCulling = b)
         );
         generalOptions.add(
            this.getOnOffOption(
               "text.entityculling.skipBlockEntityCulling", () -> inst.config.skipBlockEntityCulling, b -> inst.config.skipBlockEntityCulling = b
            )
         );
         generalOptions.add(
            this.getOnOffOption("text.entityculling.forceDisplayCulling", () -> inst.config.forceDisplayCulling, b -> inst.config.forceDisplayCulling = b)
         );
         generalOptions.add(this.getOnOffOption("text.entityculling.solidLeaves", () -> inst.config.solidLeaves, b -> inst.config.solidLeaves = b));
         WListPanel<OptionInstance, WGridPanel> generalOptionList = this.createOptionList(generalOptions);
         generalOptionList.setGap(-1);
         generalOptionList.setSize(280, 180);
         wTabPanel.add(generalOptionList, b -> b.title(ComponentProvider.translatable("text.entityculling.tab.general_options")));
         List<Entry<ResourceKey<EntityType<?>>, EntityType<?>>> entities = new ArrayList<>(BuiltInRegistries.ENTITY_TYPE.entrySet());
         entities.sort(Comparator.comparing(ConfigScreenProvider.ConfigScreen::getStringEntity));
         List<Entry<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>>> blockEntities = new ArrayList<>(BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet());
         blockEntities.sort(Comparator.comparing(ConfigScreenProvider.ConfigScreen::getStringBlockEntity));
         WListPanel<Entry<ResourceKey<EntityType<?>>, EntityType<?>>, WToggleButton> tickCullList = new WListPanel(
            entities, () -> new WToggleButton(ComponentProvider.EMPTY), (s, l) -> {
               l.setLabel(((EntityType)s.getValue()).getDescription());
               l.setToolip(ComponentProvider.literal(getStringEntity(s)));
               l.setToggle(inst.config.tickCullingWhitelist.contains(getStringEntity(s)));
               l.setOnToggle(b -> {
                  if (b) {
                     inst.config.tickCullingWhitelist.add(getStringEntity(s));
                     inst.tickCullWhitelists.add((EntityType<?>)s.getValue());
                  } else {
                     inst.config.tickCullingWhitelist.remove(getStringEntity(s));
                     inst.tickCullWhitelists.remove(s.getValue());
                  }

                  inst.writeConfig();
               });
            }
         );
         tickCullList.setGap(-1);
         tickCullList.setInsets(new Insets(2, 4));
         WGridPanel tickCullTab = new WGridPanel(20);
         tickCullTab.add(tickCullList, 0, 0, 17, 7);
         WTextField tickCullSearchField = new WTextField();
         tickCullSearchField.setChangedListener(s -> {
            tickCullList.setFilter(e -> getStringEntity(e).toLowerCase().contains(s.toLowerCase()));
            tickCullList.layout();
         });
         tickCullTab.add(tickCullSearchField, 0, 7, 17, 1);
         wTabPanel.add(
            tickCullTab,
            b -> b.title(ComponentProvider.translatable("text.entityculling.tab.tick_culling"))
               .icon(new ItemIcon(Items.REPEATER))
               .tooltip(new Component[]{ComponentProvider.translatable("text.entityculling.tab.tick_culling.tooltip")})
         );
         WListPanel<Entry<ResourceKey<EntityType<?>>, EntityType<?>>, WToggleButton> entityCullList = new WListPanel(
            entities, () -> new WToggleButton(ComponentProvider.EMPTY), (s, l) -> {
               l.setLabel(((EntityType)s.getValue()).getDescription());
               l.setToolip(ComponentProvider.literal(getStringEntity(s)));
               l.setToggle(inst.config.entityWhitelist.contains(getStringEntity(s)));
               l.setOnToggle(b -> {
                  if (b) {
                     inst.config.entityWhitelist.add(getStringEntity(s));
                     inst.entityWhitelist.add((EntityType<?>)s.getValue());
                  } else {
                     inst.config.entityWhitelist.remove(getStringEntity(s));
                     inst.entityWhitelist.remove(s.getValue());
                  }

                  inst.writeConfig();
               });
            }
         );
         entityCullList.setGap(-1);
         entityCullList.setInsets(new Insets(2, 4));
         WGridPanel entityCullTab = new WGridPanel(20);
         entityCullTab.add(entityCullList, 0, 0, 17, 7);
         WTextField entityCullSearchField = new WTextField();
         entityCullSearchField.setChangedListener(s -> {
            entityCullList.setFilter(e -> getStringEntity(e).toLowerCase().contains(s.toLowerCase()));
            entityCullList.layout();
         });
         entityCullTab.add(entityCullSearchField, 0, 7, 17, 1);
         wTabPanel.add(
            entityCullTab,
            b -> b.title(ComponentProvider.translatable("text.entityculling.tab.entity_culling"))
               .icon(new ItemIcon(Items.PIG_SPAWN_EGG))
               .tooltip(new Component[]{ComponentProvider.translatable("text.entityculling.tab.entity_culling.tooltip")})
         );
         WListPanel<Entry<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>>, WToggleButton> blockEntityCullList = new WListPanel(
            blockEntities, () -> new WToggleButton(ComponentProvider.EMPTY), (s, l) -> {
               l.setLabel(ComponentProvider.literal(getStringBlockEntity(s)));
               l.setToggle(inst.config.blockEntityWhitelist.contains(getStringBlockEntity(s)));
               l.setOnToggle(b -> {
                  if (b) {
                     inst.config.blockEntityWhitelist.add(getStringBlockEntity(s));
                     inst.blockEntityWhitelist.add((BlockEntityType<?>)s.getValue());
                  } else {
                     inst.config.blockEntityWhitelist.remove(getStringBlockEntity(s));
                     inst.blockEntityWhitelist.remove(s.getValue());
                  }

                  inst.writeConfig();
               });
            }
         );
         blockEntityCullList.setGap(-1);
         blockEntityCullList.setInsets(new Insets(2, 4));
         WGridPanel blockEntityCullTab = new WGridPanel(20);
         blockEntityCullTab.add(blockEntityCullList, 0, 0, 17, 7);
         WTextField blockEntityCullSearchField = new WTextField();
         blockEntityCullSearchField.setChangedListener(s -> {
            blockEntityCullList.setFilter(e -> getStringBlockEntity(e).toLowerCase().contains(s.toLowerCase()));
            blockEntityCullList.layout();
         });
         blockEntityCullTab.add(blockEntityCullSearchField, 0, 7, 17, 1);
         wTabPanel.add(
            blockEntityCullTab,
            b -> b.title(ComponentProvider.translatable("text.entityculling.tab.block_entity_culling"))
               .icon(new ItemIcon(Items.CHEST))
               .tooltip(new Component[]{ComponentProvider.translatable("text.entityculling.tab.block_entity_culling.tooltip")})
         );
         root.add(wTabPanel, 0, 2);
         WButton doneButton = new WButton(CommonComponents.GUI_DONE);
         doneButton.setOnClick(() -> {
            this.save();
            GeneralUtil.setScreen(previous);
         });
         root.add(doneButton, 0, 25, 6, 2);
         WButton resetButton = new WButton(ComponentProvider.translatable("controls.reset"));
         resetButton.setOnClick(() -> {
            this.reset();
            root.layout();
         });
         root.add(resetButton, 36, 25, 6, 2);
         root.validate(this);
         root.setHost(this);
      }

      @NotNull
      private static String getStringEntity(Entry<ResourceKey<EntityType<?>>, EntityType<?>> a) {
         return a.getKey().location().toString();
      }

      @NotNull
      private static String getStringBlockEntity(Entry<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>> a) {
         return a.getKey().location().toString();
      }

      public void save() {
         EntityCullingModBase.instance.writeConfig();
      }

      public void reset() {
         EntityCullingModBase.instance.config = new Config();
         this.save();
      }
   }
}
