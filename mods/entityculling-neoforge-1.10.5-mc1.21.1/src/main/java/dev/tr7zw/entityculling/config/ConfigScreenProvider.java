/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.mc.ComponentProvider
 *  dev.tr7zw.transition.mc.GeneralUtil
 *  dev.tr7zw.trender.gui.GuiDescription
 *  dev.tr7zw.trender.gui.client.AbstractConfigScreen
 *  dev.tr7zw.trender.gui.client.AbstractConfigScreen$OptionInstance
 *  dev.tr7zw.trender.gui.client.BackgroundPainter
 *  dev.tr7zw.trender.gui.widget.WButton
 *  dev.tr7zw.trender.gui.widget.WGridPanel
 *  dev.tr7zw.trender.gui.widget.WListPanel
 *  dev.tr7zw.trender.gui.widget.WPanel
 *  dev.tr7zw.trender.gui.widget.WTabPanel
 *  dev.tr7zw.trender.gui.widget.WTextField
 *  dev.tr7zw.trender.gui.widget.WToggleButton
 *  dev.tr7zw.trender.gui.widget.WWidget
 *  dev.tr7zw.trender.gui.widget.data.Insets
 *  dev.tr7zw.trender.gui.widget.icon.Icon
 *  dev.tr7zw.trender.gui.widget.icon.ItemIcon
 *  lombok.Generated
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  org.jetbrains.annotations.NotNull
 */
package dev.tr7zw.entityculling.config;

import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.versionless.Config;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.trender.gui.GuiDescription;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WListPanel;
import dev.tr7zw.trender.gui.widget.WPanel;
import dev.tr7zw.trender.gui.widget.WTabPanel;
import dev.tr7zw.trender.gui.widget.WTextField;
import dev.tr7zw.trender.gui.widget.WToggleButton;
import dev.tr7zw.trender.gui.widget.WWidget;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.trender.gui.widget.icon.Icon;
import dev.tr7zw.trender.gui.widget.icon.ItemIcon;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
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
        return new ConfigScreen(parent).createScreen();
    }

    @Generated
    private ConfigScreenProvider() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static class ConfigScreen
    extends AbstractConfigScreen {
        public ConfigScreen(Screen previous) {
            super((Component)ComponentProvider.translatable((String)"text.entityculling.title"), previous);
            WGridPanel root = new WGridPanel(8);
            root.setBackgroundPainter(BackgroundPainter.VANILLA);
            root.setInsets(Insets.ROOT_PANEL);
            this.setRootPanel((WPanel)root);
            WTabPanel wTabPanel = new WTabPanel();
            EntityCullingModBase inst = EntityCullingModBase.instance;
            ArrayList<AbstractConfigScreen.OptionInstance> generalOptions = new ArrayList<AbstractConfigScreen.OptionInstance>();
            generalOptions.add(this.getOnOffOption("text.entityculling.renderNametagsThroughWalls", () -> inst.config.renderNametagsThroughWalls, b -> {
                inst.config.renderNametagsThroughWalls = b;
            }));
            generalOptions.add(this.getOnOffOption("text.entityculling.tickCulling", () -> inst.config.tickCulling, b -> {
                inst.config.tickCulling = b;
            }));
            generalOptions.add(this.getOnOffOption("text.entityculling.disableF3", () -> inst.config.disableF3, b -> {
                inst.config.disableF3 = b;
            }));
            generalOptions.add(this.getOnOffOption("text.entityculling.skipEntityCulling", () -> inst.config.skipEntityCulling, b -> {
                inst.config.skipEntityCulling = b;
            }));
            generalOptions.add(this.getOnOffOption("text.entityculling.skipBlockEntityCulling", () -> inst.config.skipBlockEntityCulling, b -> {
                inst.config.skipBlockEntityCulling = b;
            }));
            generalOptions.add(this.getOnOffOption("text.entityculling.forceDisplayCulling", () -> inst.config.forceDisplayCulling, b -> {
                inst.config.forceDisplayCulling = b;
            }));
            generalOptions.add(this.getOnOffOption("text.entityculling.solidLeaves", () -> inst.config.solidLeaves, b -> {
                inst.config.solidLeaves = b;
            }));
            WListPanel generalOptionList = this.createOptionList(generalOptions);
            generalOptionList.setGap(-1);
            generalOptionList.setSize(280, 180);
            wTabPanel.add((WWidget)generalOptionList, b -> b.title((Component)ComponentProvider.translatable((String)"text.entityculling.tab.general_options")));
            ArrayList<Map.Entry> entities = new ArrayList<Map.Entry>(BuiltInRegistries.ENTITY_TYPE.entrySet());
            entities.sort(Comparator.comparing(ConfigScreen::getStringEntity));
            ArrayList<Map.Entry> blockEntities = new ArrayList<Map.Entry>(BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet());
            blockEntities.sort(Comparator.comparing(ConfigScreen::getStringBlockEntity));
            WListPanel tickCullList = new WListPanel(entities, () -> new WToggleButton(ComponentProvider.EMPTY), (s, l) -> {
                l.setLabel(((EntityType)s.getValue()).getDescription());
                l.setToolip((Component)ComponentProvider.literal((String)ConfigScreen.getStringEntity(s)));
                l.setToggle(inst.config.tickCullingWhitelist.contains(ConfigScreen.getStringEntity(s)));
                l.setOnToggle(b -> {
                    if (b.booleanValue()) {
                        inst.config.tickCullingWhitelist.add(ConfigScreen.getStringEntity(s));
                        inst.tickCullWhitelists.add((EntityType)s.getValue());
                    } else {
                        inst.config.tickCullingWhitelist.remove(ConfigScreen.getStringEntity(s));
                        inst.tickCullWhitelists.remove(s.getValue());
                    }
                    inst.writeConfig();
                });
            });
            tickCullList.setGap(-1);
            tickCullList.setInsets(new Insets(2, 4));
            WGridPanel tickCullTab = new WGridPanel(20);
            tickCullTab.add((WWidget)tickCullList, 0, 0, 17, 7);
            WTextField tickCullSearchField = new WTextField();
            tickCullSearchField.setChangedListener(s -> {
                tickCullList.setFilter(e -> ConfigScreen.getStringEntity(e).toLowerCase().contains(s.toLowerCase()));
                tickCullList.layout();
            });
            tickCullTab.add((WWidget)tickCullSearchField, 0, 7, 17, 1);
            wTabPanel.add((WWidget)tickCullTab, b -> b.title((Component)ComponentProvider.translatable((String)"text.entityculling.tab.tick_culling")).icon((Icon)new ItemIcon(Items.REPEATER)).tooltip(new Component[]{ComponentProvider.translatable((String)"text.entityculling.tab.tick_culling.tooltip")}));
            WListPanel entityCullList = new WListPanel(entities, () -> new WToggleButton(ComponentProvider.EMPTY), (s, l) -> {
                l.setLabel(((EntityType)s.getValue()).getDescription());
                l.setToolip((Component)ComponentProvider.literal((String)ConfigScreen.getStringEntity(s)));
                l.setToggle(inst.config.entityWhitelist.contains(ConfigScreen.getStringEntity(s)));
                l.setOnToggle(b -> {
                    if (b.booleanValue()) {
                        inst.config.entityWhitelist.add(ConfigScreen.getStringEntity(s));
                        inst.entityWhitelist.add((EntityType)s.getValue());
                    } else {
                        inst.config.entityWhitelist.remove(ConfigScreen.getStringEntity(s));
                        inst.entityWhitelist.remove(s.getValue());
                    }
                    inst.writeConfig();
                });
            });
            entityCullList.setGap(-1);
            entityCullList.setInsets(new Insets(2, 4));
            WGridPanel entityCullTab = new WGridPanel(20);
            entityCullTab.add((WWidget)entityCullList, 0, 0, 17, 7);
            WTextField entityCullSearchField = new WTextField();
            entityCullSearchField.setChangedListener(s -> {
                entityCullList.setFilter(e -> ConfigScreen.getStringEntity(e).toLowerCase().contains(s.toLowerCase()));
                entityCullList.layout();
            });
            entityCullTab.add((WWidget)entityCullSearchField, 0, 7, 17, 1);
            wTabPanel.add((WWidget)entityCullTab, b -> b.title((Component)ComponentProvider.translatable((String)"text.entityculling.tab.entity_culling")).icon((Icon)new ItemIcon(Items.PIG_SPAWN_EGG)).tooltip(new Component[]{ComponentProvider.translatable((String)"text.entityculling.tab.entity_culling.tooltip")}));
            WListPanel blockEntityCullList = new WListPanel(blockEntities, () -> new WToggleButton(ComponentProvider.EMPTY), (s, l) -> {
                l.setLabel((Component)ComponentProvider.literal((String)ConfigScreen.getStringBlockEntity(s)));
                l.setToggle(inst.config.blockEntityWhitelist.contains(ConfigScreen.getStringBlockEntity(s)));
                l.setOnToggle(b -> {
                    if (b.booleanValue()) {
                        inst.config.blockEntityWhitelist.add(ConfigScreen.getStringBlockEntity(s));
                        inst.blockEntityWhitelist.add((BlockEntityType)s.getValue());
                    } else {
                        inst.config.blockEntityWhitelist.remove(ConfigScreen.getStringBlockEntity(s));
                        inst.blockEntityWhitelist.remove(s.getValue());
                    }
                    inst.writeConfig();
                });
            });
            blockEntityCullList.setGap(-1);
            blockEntityCullList.setInsets(new Insets(2, 4));
            WGridPanel blockEntityCullTab = new WGridPanel(20);
            blockEntityCullTab.add((WWidget)blockEntityCullList, 0, 0, 17, 7);
            WTextField blockEntityCullSearchField = new WTextField();
            blockEntityCullSearchField.setChangedListener(s -> {
                blockEntityCullList.setFilter(e -> ConfigScreen.getStringBlockEntity(e).toLowerCase().contains(s.toLowerCase()));
                blockEntityCullList.layout();
            });
            blockEntityCullTab.add((WWidget)blockEntityCullSearchField, 0, 7, 17, 1);
            wTabPanel.add((WWidget)blockEntityCullTab, b -> b.title((Component)ComponentProvider.translatable((String)"text.entityculling.tab.block_entity_culling")).icon((Icon)new ItemIcon(Items.CHEST)).tooltip(new Component[]{ComponentProvider.translatable((String)"text.entityculling.tab.block_entity_culling.tooltip")}));
            root.add((WWidget)wTabPanel, 0, 2);
            WButton doneButton = new WButton(CommonComponents.GUI_DONE);
            doneButton.setOnClick(() -> {
                this.save();
                GeneralUtil.setScreen((Screen)previous);
            });
            root.add((WWidget)doneButton, 0, 25, 6, 2);
            WButton resetButton = new WButton((Component)ComponentProvider.translatable((String)"controls.reset"));
            resetButton.setOnClick(() -> {
                this.reset();
                root.layout();
            });
            root.add((WWidget)resetButton, 36, 25, 6, 2);
            root.validate((GuiDescription)this);
            root.setHost((GuiDescription)this);
        }

        @NotNull
        private static String getStringEntity(Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> a) {
            return a.getKey().location().toString();
        }

        @NotNull
        private static String getStringBlockEntity(Map.Entry<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>> a) {
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

