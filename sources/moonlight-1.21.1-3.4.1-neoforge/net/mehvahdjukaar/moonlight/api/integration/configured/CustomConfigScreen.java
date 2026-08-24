package net.mehvahdjukaar.moonlight.api.integration.configured;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.configured.api.IConfigEntry;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.api.ValueEntry;
import com.mrcrayfish.configured.client.screen.ConfigScreen;
import com.mrcrayfish.configured.client.screen.ConfigScreen.BooleanItem;
import com.mrcrayfish.configured.client.screen.ConfigScreen.ConfigItem;
import com.mrcrayfish.configured.client.screen.ConfigScreen.FolderItem;
import com.mrcrayfish.configured.client.screen.ListMenuScreen.Item;
import com.mrcrayfish.configured.client.screen.widget.IconButton;
import com.mrcrayfish.configured.impl.neoforge.NeoForgeConfig;
import com.mrcrayfish.configured.impl.neoforge.NeoForgeValue;
import com.mrcrayfish.configured.util.ConfigHelper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.platform.ForgeConfigHolder;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;

public abstract class CustomConfigScreen extends ConfigScreen {
   @Nullable
   private static final Field FORGE_CONFIG = findFieldOrNull(NeoForgeConfig.class, "config");
   @Nullable
   private static final Field BUTTON_ON_PRESS = findFieldOrNull(Button.class, "onPress");
   @Nullable
   private static final Field FOLDER_ENTRY = findFieldOrNull(ConfigScreen.class, "folderEntry");
   @Nullable
   private static final Field CONFIG_VALUE_HOLDER = findFieldOrNull(ConfigItem.class, "holder");
   @Nullable
   private static final Field BOOLEAN_ITEM_BUTTON = findFieldOrNull(BooleanItem.class, "button");
   @Nullable
   private static final Field LABEL = findFieldOrNull(IconButton.class, "label");
   protected final String modId;
   protected final Map<String, ItemStack> icons = new HashMap<>();
   public final ForgeConfigHolder mlConfig;
   public final ItemStack mainIcon;
   private int ticks = 0;

   @Nullable
   static Method findMethodOrNull(Class<?> c, String methodName) {
      Method field = null;

      try {
         field = ObfuscationReflectionHelper.findMethod(c, methodName, new Class[0]);
      } catch (Exception var4) {
      }

      return field;
   }

   @Nullable
   static Field findFieldOrNull(Class<?> c, String fieldName) {
      Field field = null;

      try {
         field = ObfuscationReflectionHelper.findField(c, fieldName);
         field.setAccessible(true);
      } catch (Exception var4) {
         if (PlatHelper.isDev()) {
            throw new RuntimeException("Failed to find field: " + fieldName + " in class: " + c.getName());
         }
      }

      return field;
   }

   protected CustomConfigScreen(CustomConfigSelectScreen parent, IModConfig config) {
      this(parent.getModId(), parent.getMainIcon(), parent.getTitle(), parent, config);
   }

   protected CustomConfigScreen(String modId, ItemStack mainIcon, Component title, Screen parent, IModConfig config) {
      super(parent, title, config);
      this.modId = modId;
      this.mainIcon = mainIcon;
      this.mlConfig = ForgeConfigHolder.getFromForgeConfig(this.getForgeConfig(config));
   }

   private ModConfig getForgeConfig(IModConfig config) {
      try {
         FORGE_CONFIG.setAccessible(true);
         return (ModConfig)FORGE_CONFIG.get(config);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   protected void constructEntries(List<Item> entries) {
      super.constructEntries(entries);
      List<Item> entriesCopy = new ArrayList<>(entries);
      ListIterator<Item> iter = entriesCopy.listIterator();
      entries.clear();

      while (iter.hasNext()) {
         Item e = iter.next();
         if (e.getLabel().toLowerCase(Locale.ROOT).equals(this.getEnabledKeyword())) {
            iter.remove();
            entries.add(e);
         }
      }

      entries.addAll(entriesCopy);
   }

   public ItemStack getIcon(String... path) {
      String last = path[path.length - 1];
      if (path.length > 1 && last.equals(this.getEnabledKeyword())) {
         last = path[path.length - 2];
      }

      last = last.toLowerCase(Locale.ROOT).replace("_", " ");
      if (!this.icons.containsKey(last)) {
         String formatted = last.toLowerCase(Locale.ROOT).replace(" ", "_");
         Optional<net.minecraft.world.item.Item> item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(this.modId, formatted));
         item.ifPresent(value -> this.addIcon(last, value.asItem().getDefaultInstance()));
      }

      return this.icons.getOrDefault(last, ItemStack.EMPTY);
   }

   private void addIcon(String s, ItemStack i) {
      this.icons.put(s, i);
   }

   protected void init() {
      super.init();
      this.list.replaceEntries(this.replaceItems(this.list.children()));
      Collection<Item> temp = this.replaceItems(this.entries);
      this.entries = new ArrayList<>(temp);
      if (this.saveButton != null && BUTTON_ON_PRESS != null) {
         try {
            OnPress oldOnPress = (OnPress)BUTTON_ON_PRESS.get(this.saveButton);
            OnPress press = onPress -> {
               oldOnPress.onPress(onPress);
               this.trySyncToServer();
               this.onSave();
            };
            BUTTON_ON_PRESS.set(this.saveButton, press);
         } catch (Exception var5) {
            if (PlatHelper.isDev()) {
               throw new RuntimeException("Failed to set save button");
            }
         }

         if (LABEL != null) {
            int changedEntries = this.getChangedConfigs(this.folderEntry);

            try {
               String s = changedEntries == 0 ? "" : " (§3" + changedEntries + "§r)";
               LABEL.set(this.saveButton, Component.literal(Component.translatable("configured.gui.save").getString() + s));
            } catch (Exception var4) {
               if (PlatHelper.isDev()) {
                  throw new RuntimeException("Failed to set save button label");
               }
            }
         }
      }
   }

   public int getChangedConfigs(IConfigEntry entry) {
      if (!entry.isLeaf()) {
         int count = 0;

         for (IConfigEntry child : entry.getChildren()) {
            count += this.getChangedConfigs(child);
         }

         return count;
      } else {
         IConfigValue<?> value = entry.getValue();
         return value != null && value.isChanged() ? 1 : 0;
      }
   }

   private Collection<Item> replaceItems(Collection<Item> originals) {
      ArrayList<Item> newList = new ArrayList<>();

      for (Item c : originals) {
         if (c instanceof FolderItem f) {
            CustomConfigScreen.FolderWrapper wrapper = this.wrapFolderItem(f);
            if (wrapper != null) {
               newList.add(wrapper);
               continue;
            }
         } else if (c instanceof BooleanItem b) {
            CustomConfigScreen.BooleanWrapper wrapper = this.wrapBooleanItem(b);
            if (wrapper != null) {
               newList.add(wrapper);
               continue;
            }
         }

         newList.add(c);
      }

      return newList;
   }

   private void trySyncToServer() {
      if (ConfigHelper.isPlayingGame()) {
         Player player = ConfigHelper.getClientPlayer();
         if (ConfigHelper.isOperator(player)) {
            this.mlConfig.sendChangedConfigToServer();
         }
      }
   }

   public abstract void onSave();

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      Lighting.setupFor3DItems();
      int titleWidth = this.font.width(this.title) + 35;
      graphics.renderFakeItem(this.mainIcon, this.width / 2 + titleWidth / 2 - 17, 2);
      graphics.renderFakeItem(this.mainIcon, this.width / 2 - titleWidth / 2, 2);
   }

   public void tick() {
      super.tick();
      this.ticks++;
   }

   @Nullable
   private CustomConfigScreen.FolderWrapper wrapFolderItem(FolderItem old) {
      try {
         String oldName = old.getLabel();
         IConfigEntry found = null;

         for (IConfigEntry e : this.folderEntry.getChildren()) {
            if (!(e instanceof ValueEntry)) {
               String n = Component.literal(ConfigScreen.createLabel(e.getEntryName())).getString();
               if (n.equals(oldName)) {
                  found = e;
                  break;
               }
            }
         }

         if (found != null) {
            return new CustomConfigScreen.FolderWrapper(found, oldName);
         }
      } catch (Exception var7) {
         Moonlight.LOGGER.error("Failed to wrap folder entry for config screen: ", var7);
      }

      return null;
   }

   public abstract CustomConfigScreen.Factory getSubScreenFactory();

   public String getEnabledKeyword() {
      return "enabled";
   }

   @Nullable
   private CustomConfigScreen.BooleanWrapper wrapBooleanItem(BooleanItem old) {
      try {
         IConfigValue<Boolean> holder = (IConfigValue<Boolean>)CONFIG_VALUE_HOLDER.get(old);
         ValueEntry found = null;

         for (IConfigEntry e : this.folderEntry.getChildren()) {
            if (e instanceof ValueEntry value && holder == value.getValue()) {
               found = value;
            }
         }

         if (found != null) {
            String[] path = ((NeoForgeValue)holder).configValue.getPath().toArray(String[]::new);
            ItemStack icon = this.getIcon(path);
            return new CustomConfigScreen.BooleanWrapper(holder, icon);
         }
      } catch (Exception var7) {
         Moonlight.LOGGER.error("error");
      }

      return null;
   }

   private static void rotateItem(int ticks, float partialTicks, PoseStack s, BakedModel m) {
      if (ticks != 0) {
         if (m.usesBlockLight()) {
            s.mulPose(Axis.YP.rotation((ticks + partialTicks) * 0.017453292F * 10.0F));
         } else {
            float scale = 1.0F + 0.1F * Mth.sin((ticks + partialTicks) * 0.017453292F * 20.0F);
            s.scale(scale, scale, scale);
         }
      }
   }

   private class BooleanWrapper extends BooleanItem {
      private static final int ICON_SIZE = 12;
      private final ItemStack item;
      protected final int iconOffset;
      protected Button button;
      private int ticks = 0;
      private int lastTick = 1;

      public BooleanWrapper(IConfigValue<Boolean> holder, ItemStack item) {
         super(CustomConfigScreen.this, holder);

         try {
            this.button = (Button)CustomConfigScreen.BOOLEAN_ITEM_BUTTON.get(this);
         } catch (Exception var5) {
         }

         this.button.setMessage(Component.literal(""));
         this.item = item;
         this.iconOffset = item.isEmpty() ? 0 : 7;
      }

      public BooleanWrapper(IConfigValue<Boolean> holder) {
         this(holder, ItemStack.EMPTY);
      }

      public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
         this.button.setMessage(Component.literal(""));
         super.render(graphics, index, top, left, width, height, mouseX, mouseY, hovered, partialTicks);
         hovered = this.button.isMouseOver(mouseX, mouseY);
         if (this.lastTick < CustomConfigScreen.this.ticks) {
            this.ticks = Math.max(0, this.ticks + (hovered ? 1 : -2)) % 36;
            if (!hovered && this.ticks > 17) {
               this.ticks %= 18;
            }
         }

         this.lastTick = CustomConfigScreen.this.ticks;
         int iconX = this.iconOffset + (int)(this.button.getX() + Math.ceil((this.button.getWidth() - 12) / 2.0F));
         int iconY = (int)(this.button.getY() + Math.ceil((this.button.getHeight() - 12) / 2.0F));
         boolean on = (Boolean)this.holder.get();
         ResourceLocation iconRes = on ? CustomConfigSelectScreen.ON_ICON : CustomConfigSelectScreen.OFF_ICON;
         graphics.blitSprite(iconRes, iconX, iconY, 12, 12);
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         if (!this.item.isEmpty()) {
            int light = on ? 15728880 : 0;
            int center = (int)(this.button.getX() + this.button.getWidth() / 2.0F);
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            RenderUtil.renderGuiItemRelative(
               graphics.pose(),
               this.item,
               center - 8 - this.iconOffset,
               top + 2,
               renderer,
               (s, m) -> CustomConfigScreen.rotateItem(this.ticks, partialTicks, s, m),
               light,
               OverlayTexture.NO_OVERLAY
            );
         }
      }

      public void onResetValue() {
         this.button.setMessage(Component.literal(""));
      }
   }

   @FunctionalInterface
   public interface Factory {
      CustomConfigScreen create(String var1, ItemStack var2, Component var3, Screen var4, IModConfig var5);
   }

   private class FolderWrapper extends FolderItem {
      private final ItemStack icon;
      protected final Button button;
      protected final boolean light;
      private int ticks = 0;
      private int lastTick = 1;

      private FolderWrapper(IConfigEntry folderEntry, String label) {
         super(CustomConfigScreen.this, folderEntry);
         this.button = Button.builder(
               Component.literal(label).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE),
               onPress -> {
                  Component newTitle = CustomConfigScreen.this.title.plainCopy().append(" > " + label);
                  CustomConfigScreen sc = CustomConfigScreen.this.getSubScreenFactory()
                     .create(CustomConfigScreen.this.modId, CustomConfigScreen.this.mainIcon, newTitle, CustomConfigScreen.this, CustomConfigScreen.this.config);

                  try {
                     CustomConfigScreen.FOLDER_ENTRY.set(sc, folderEntry);
                  } catch (Exception var7) {
                  }

                  CustomConfigScreen.this.minecraft.setScreen(sc);
               }
            )
            .bounds(10, 5, 44, 20)
            .build();
         ItemStack i = CustomConfigScreen.this.getIcon(label.toLowerCase(Locale.ROOT));
         this.icon = i.isEmpty() ? CustomConfigScreen.this.mainIcon : i;
         this.light = this.getFolderEnabledValue(folderEntry);
      }

      private boolean getFolderEnabledValue(IConfigEntry entry) {
         for (IConfigEntry c : entry.getChildren()) {
            IConfigValue<?> value = c.getValue();
            if (value != null && value.getName().equals(CustomConfigScreen.this.getEnabledKeyword()) && value.get() instanceof Boolean b) {
               return b;
            }
         }

         return true;
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.button);
      }

      public void render(GuiGraphics graphics, int x, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
         int light = this.light ? 15728880 : 0;
         if (this.lastTick < CustomConfigScreen.this.ticks) {
            this.ticks = Math.max(0, this.ticks + (hovered ? 1 : -2)) % 36;
         }

         this.lastTick = CustomConfigScreen.this.ticks;
         this.button.setX(left - 1);
         this.button.setY(top);
         this.button.setWidth(width);
         this.button.render(graphics, mouseX, mouseY, partialTicks);
         int center = this.button.getX() + width / 2;
         ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
         RenderUtil.renderGuiItemRelative(
            graphics.pose(),
            this.icon,
            center + 95 - 17,
            top + 2,
            renderer,
            (s, m) -> CustomConfigScreen.rotateItem(this.ticks, partialTicks, s, m),
            light,
            OverlayTexture.NO_OVERLAY
         );
         RenderUtil.renderGuiItemRelative(
            graphics.pose(),
            this.icon,
            center - 95,
            top + 2,
            renderer,
            (s, m) -> CustomConfigScreen.rotateItem(this.ticks, partialTicks, s, m),
            light,
            OverlayTexture.NO_OVERLAY
         );
      }
   }
}
