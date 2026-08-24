package net.mehvahdjukaar.moonlight.api.integration.configured;

import com.mojang.blaze3d.platform.Lighting;
import com.mrcrayfish.configured.api.ConfigType;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.screen.ModConfigSelectionScreen;
import com.mrcrayfish.configured.client.screen.ListMenuScreen.Item;
import com.mrcrayfish.configured.client.screen.ModConfigSelectionScreen.FileItem;
import com.mrcrayfish.configured.client.screen.widget.IconButton;
import com.mrcrayfish.configured.impl.neoforge.NeoForgeConfig;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.platform.configs.platform.ForgeConfigHolder;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class CustomConfigSelectScreen extends ModConfigSelectionScreen {
   public static final ResourceLocation ON_ICON = MoonlightIcons.YES;
   public static final ResourceLocation OFF_ICON = MoonlightIcons.NO;
   private static final Field FILE_ITEM_BUTTON = CustomConfigScreen.findFieldOrNull(FileItem.class, "modifyButton");
   private static final Field FILE_ITEM_CONFIG = CustomConfigScreen.findFieldOrNull(FileItem.class, "config");
   private final BiFunction<CustomConfigSelectScreen, IModConfig, CustomConfigScreen> configScreenFactory;
   private final ItemStack mainIcon;
   private final String modId;
   private final String modURL;

   public CustomConfigSelectScreen(
      String modId,
      ItemStack mainIcon,
      String displayName,
      Screen parent,
      BiFunction<CustomConfigSelectScreen, IModConfig, CustomConfigScreen> configScreenFactory,
      ModConfigHolder... specs
   ) {
      this(modId, mainIcon, displayName, parent, configScreenFactory, createConfigMap(specs));
   }

   public CustomConfigSelectScreen(
      String modId,
      ItemStack mainIcon,
      String displayName,
      Screen parent,
      BiFunction<CustomConfigSelectScreen, IModConfig, CustomConfigScreen> configScreenFactory,
      Map<ConfigType, Set<IModConfig>> configMap
   ) {
      super(parent, Component.literal(displayName), configMap);
      this.configScreenFactory = configScreenFactory;
      this.mainIcon = mainIcon;
      this.modId = modId;
      ModContainer container = (ModContainer)ModList.get().getModContainerById(modId).get();
      this.modURL = container.getModInfo().getModURL().map(URL::getPath).orElse(null);
   }

   public static ResourceLocation ensureNotNull(ResourceLocation background) {
      return background == null ? ResourceLocation.parse("minecraft:textures/gui/options_background.png") : background;
   }

   public ItemStack getMainIcon() {
      return this.mainIcon;
   }

   public String getModId() {
      return this.modId;
   }

   public static void registerConfigScreen(String modId, Function<Screen, CustomConfigSelectScreen> screenSelectFactory) {
      ModContainer container = (ModContainer)ModList.get().getModContainerById(modId).get();
      container.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(a, s) -> (Screen)screenSelectFactory.apply(s));
   }

   private static Map<ConfigType, Set<IModConfig>> createConfigMap(ModConfigHolder... specs) {
      Map<ConfigType, Set<IModConfig>> modConfigMap = new EnumMap<>(ConfigType.class);

      for (ModConfigHolder ss : specs) {
         ForgeConfigHolder s = (ForgeConfigHolder)ss;
         ModConfig modConfig = s.getModConfig();
         NeoForgeConfig forgeConfig = new NeoForgeConfig(modConfig);
         Set<IModConfig> set = modConfigMap.computeIfAbsent(forgeConfig.getType(), a -> new HashSet<>());
         set.add(forgeConfig);
      }

      return modConfigMap;
   }

   private static ConfigType getType(ForgeConfigHolder s) {
      net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType t = s.getConfigType();
      if (t == net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType.CLIENT) {
         return ConfigType.CLIENT;
      } else {
         return t == net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType.COMMON ? ConfigType.UNIVERSAL : ConfigType.UNIVERSAL;
      }
   }

   protected void constructEntries(List<Item> entries) {
      super.constructEntries(entries);

      for (Item i : entries) {
         if (i instanceof FileItem item) {
            try {
               FILE_ITEM_BUTTON.setAccessible(true);
               FILE_ITEM_CONFIG.setAccessible(true);
               FILE_ITEM_BUTTON.set(i, this.createModifyButton((IModConfig)FILE_ITEM_CONFIG.get(item)));
            } catch (IllegalAccessException var6) {
            }
         }
      }
   }

   private Button createModifyButton(IModConfig config) {
      String langKey = "configured.gui.modify";
      return new IconButton(
         0, 0, 33, 0, 60, Component.translatable(langKey), onPress -> Minecraft.getInstance().setScreen((Screen)this.configScreenFactory.apply(this, config))
      );
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      Lighting.setupFor3DItems();
      int titleWidth = this.font.width(this.title) + 35;
      graphics.renderFakeItem(this.mainIcon, this.width / 2 + titleWidth / 2 - 17, 2);
      graphics.renderFakeItem(this.mainIcon, this.width / 2 - titleWidth / 2, 2);
      if (this.modURL != null && MthUtils.isWithinRectangle(this.width / 2 - 90, 2, 180, 16, mouseX, mouseY)) {
         graphics.renderTooltip(
            this.font, this.font.split(Component.translatable("gui.moonlight.open_mod_page", new Object[]{this.modId}), 200), mouseX, mouseY
         );
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.modURL != null && MthUtils.isWithinRectangle(this.width / 2 - 90, 2, 180, 16, (int)mouseX, (int)mouseY)) {
         Style style = Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, this.modURL));
         this.handleComponentClicked(style);
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }
}
