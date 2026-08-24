package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSCommon;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.client.highlight.HighlightRenderer;
import dev.latvian.mods.kubejs.item.ModifyItemTooltipsKubeEvent;
import dev.latvian.mods.kubejs.net.KubeServerData;
import dev.latvian.mods.kubejs.net.NetworkKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.NetworkEvents;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.script.data.GeneratedDataStage;
import dev.latvian.mods.kubejs.script.data.VirtualAssetPack;
import dev.latvian.mods.kubejs.text.tooltip.ItemTooltipData;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.Nullable;

public class KubeJSClient extends KubeJSCommon {
   public static final ResourceLocation WHITE_TEXTURE = ResourceLocation.parse("textures/misc/white.png");
   public static final ResourceLocation RECIPE_BUTTON_TEXTURE = ResourceLocation.parse("textures/gui/recipe_button.png");
   public static final Map<GeneratedDataStage, VirtualAssetPack> CLIENT_PACKS = new EnumMap<>(GeneratedDataStage.class);
   public static List<ItemTooltipData> clientItemTooltips = List.of();
   private static final char[] POWER;

   public static void reloadClientScripts() {
      KubeJS.getClientScriptManager().reload();
      ArrayList<ItemTooltipData> list = new ArrayList<>();
      ItemEvents.MODIFY_TOOLTIPS.post(ScriptType.CLIENT, new ModifyItemTooltipsKubeEvent(list::add));
      clientItemTooltips = List.copyOf(list);
   }

   public static void copyDefaultOptionsFile(File optionsFile) {
      if (!optionsFile.exists()) {
         Path defOptions = KubeJSPaths.CONFIG.resolve("defaultoptions.txt");
         if (Files.exists(defOptions)) {
            try {
               KubeJS.LOGGER.info("Loaded default options from kubejs/config/defaultoptions.txt");
               PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(optionsFile), StandardCharsets.UTF_8));
               printwriter.println("version:" + SharedConstants.getCurrentVersion().getDataVersion().getVersion());
               printwriter.print(Files.readString(defOptions));
               printwriter.close();
            } catch (IOException var3) {
               KubeJS.LOGGER.error("Failed to save default options to options.txt!");
            }
         }
      }
   }

   @Override
   public void handleDataFromServerPacket(String channel, @Nullable CompoundTag data) {
      if (NetworkEvents.DATA_RECEIVED.hasListeners(channel)) {
         NetworkEvents.DATA_RECEIVED.post(ScriptType.CLIENT, channel, new NetworkKubeEvent(Minecraft.getInstance().player, channel, data));
      }
   }

   @Nullable
   @Override
   public Player getClientPlayer() {
      return Minecraft.getInstance().player;
   }

   @Override
   public void generateTypings(CommandSourceStack source) {
      source.sendSuccess(() -> Component.literal("WIP!"), false);
   }

   @Override
   public void reloadConfig() {
      super.reloadConfig();
      ClientProperties.reload();
   }

   @Override
   public void reloadStartupScripts(boolean dedicated) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         CreativeModeTabs.CACHED_PARAMETERS = null;
         CreativeModeTabs.tryRebuildTabContents(
            mc.player.connection.enabledFeatures(),
            mc.player.canUseGameMasterBlocks() && (Boolean)mc.options.operatorItemsTab().get(),
            mc.level.registryAccess()
         );
      }
   }

   @Override
   public void export(List<ExportablePackResources> packs) {
      for (PackResources pack : Minecraft.getInstance().getResourceManager().listPacks().toList()) {
         if (pack instanceof ExportablePackResources e && !packs.contains(e)) {
            packs.add(e);
         }
      }
   }

   @Override
   public void openErrors(ScriptType type) {
      this.runInMainThread(() -> Minecraft.getInstance().setScreen(new KubeJSErrorScreen(null, type.console, true)));
   }

   @Override
   public void openErrors(ScriptType type, List<ConsoleLine> errors, List<ConsoleLine> warnings) {
      this.runInMainThread(() -> Minecraft.getInstance().setScreen(new KubeJSErrorScreen(null, type, null, errors, warnings, true)));
   }

   @Override
   public void runInMainThread(Runnable runnable) {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null) {
         mc.execute(runnable);
      } else {
         runnable.run();
      }
   }

   @Override
   public void updateServerData(KubeServerData data) {
      KubeSessionData sessionData = KubeSessionData.of(Minecraft.getInstance());
      if (sessionData != null) {
         sessionData.sync(data);
      }
   }

   @Override
   public String getWebServerWindowTitle() {
      Minecraft mc = Minecraft.getInstance();
      return mc.getGameProfile().getName() + ", " + mc.kjs$getTitle();
   }

   public static void loadPostChains(Minecraft mc) {
      HighlightRenderer.INSTANCE.loadPostChains(mc);
   }

   public static void resizePostChains(int width, int height) {
      HighlightRenderer.INSTANCE.resizePostChains(width, height);
   }

   public static String formatNumber(int count) {
      if (Screen.hasAltDown()) {
         return String.format("%,d", count);
      } else {
         int index = 0;
         if (count > 9999) {
            while (count / 1000 != 0) {
               count /= 1000;
               index++;
            }
         }

         return index > 0 ? "" + count + POWER[index - 1] : String.valueOf(count);
      }
   }

   public static int drawStackSize(GuiGraphics graphics, Font font, int size, int x, int y, int color, boolean dropShadow) {
      String str = formatNumber(size);
      int w = font.width(str);
      float scale = ClientProperties.get().shrinkStackSizeText ? (str.length() >= 4 ? 0.5F : (str.length() == 3 ? 0.75F : 1.0F)) : 1.0F;
      graphics.pose().pushPose();
      graphics.pose().translate((int)(x + 16.0F - (w - 1.0F) * scale), (int)(y + 16.0F - 7.0F * scale), 0.0F);
      graphics.pose().scale(scale, scale, 1.0F);
      int s = graphics.drawString(font, str, 0.0F, 0.0F, color, dropShadow);
      graphics.pose().popPose();
      return Mth.ceil(s * scale);
   }

   static {
      for (GeneratedDataStage stage : GeneratedDataStage.values()) {
         CLIENT_PACKS.put(stage, new VirtualAssetPack(stage, () -> RegistryAccessContainer.BUILTIN));
      }

      POWER = new char[]{'K', 'M', 'B', 'T'};
   }
}
