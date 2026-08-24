package mezz.jei.gui.overlay;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.network.packets.PacketRequestCheatPermission;
import mezz.jei.common.platform.IPlatformConfigHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;

public class ConfigButtonController implements IIconButtonController {
   private final IDrawable normalIcon;
   private final IDrawable cheatIcon;
   private final BooleanSupplier isListDisplayed;
   private final IClientToggleState toggleState;
   private final IInternalKeyMappings keyBindings;

   public ConfigButtonController(BooleanSupplier isListDisplayed, IClientToggleState toggleState, IInternalKeyMappings keyBindings) {
      Textures textures = Internal.getTextures();
      this.normalIcon = textures.getConfigButtonIcon();
      this.cheatIcon = textures.getConfigButtonCheatIcon();
      this.isListDisplayed = isListDisplayed;
      this.toggleState = toggleState;
      this.keyBindings = keyBindings;
   }

   @Override
   public void updateState(IButtonState state) {
      if (this.toggleState.isCheatItemsEnabled()) {
         state.setIcon(this.cheatIcon);
      } else {
         state.setIcon(this.normalIcon);
      }
   }

   @Override
   public void getTooltips(ITooltipBuilder tooltip) {
      tooltip.add(Component.translatable("jei.tooltip.config"));
      if (!this.toggleState.isOverlayEnabled()) {
         tooltip.add(Component.translatable("jei.tooltip.ingredient.list.disabled").withStyle(ChatFormatting.GOLD));
         tooltip.addKeyUsageComponent("jei.tooltip.ingredient.list.disabled.how.to.fix", this.keyBindings.getToggleOverlay());
      } else if (!this.isListDisplayed.getAsBoolean()) {
         tooltip.add(Component.translatable("jei.tooltip.not.enough.space").withStyle(ChatFormatting.GOLD));
      }

      if (this.toggleState.isCheatItemsEnabled()) {
         tooltip.add(Component.translatable("jei.tooltip.cheat.mode.button.enabled").withStyle(ChatFormatting.RED));
         if (!this.keyBindings.getToggleCheatMode().isUnbound()) {
            tooltip.addKeyUsageComponent("jei.tooltip.cheat.mode.how.to.disable.hotkey", this.keyBindings.getToggleCheatMode());
         } else if (!this.keyBindings.getToggleCheatModeConfigButton().isUnbound()) {
            tooltip.addKeyUsageComponent("jei.tooltip.cheat.mode.how.to.disable.hover.config.button.hotkey", this.keyBindings.getToggleCheatModeConfigButton());
         }
      }
   }

   @Override
   public boolean onPress(IJeiUserInput input) {
      if (this.toggleState.isOverlayEnabled()) {
         if (!input.isSimulate()) {
            if (input.is(this.keyBindings.getToggleCheatModeConfigButton())) {
               this.toggleState.toggleCheatItemsEnabled();
               if (this.toggleState.isCheatItemsEnabled()) {
                  IConnectionToServer serverConnection = Internal.getServerConnection();
                  serverConnection.sendPacketToServer(PacketRequestCheatPermission.INSTANCE);
               }
            } else {
               openSettings();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static void openSettings() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         IPlatformConfigHelper configHelper = Services.PLATFORM.getConfigHelper();
         Optional<Screen> configScreen = configHelper.getConfigScreen();
         if (configScreen.isPresent()) {
            mc.setScreen(configScreen.get());
         } else {
            Component message = getMissingConfigScreenMessage(configHelper);
            mc.player.displayClientMessage(message, false);
         }
      }
   }

   private static Component getMissingConfigScreenMessage(IPlatformConfigHelper configHelper) {
      return Component.translatable("jei.message.configured")
         .setStyle(
            Style.EMPTY
               .withColor(ChatFormatting.DARK_BLUE)
               .withUnderlined(true)
               .withClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured"))
         )
         .append("\n")
         .append(
            Component.translatable("jei.message.config.folder")
               .setStyle(
                  Style.EMPTY
                     .withColor(ChatFormatting.WHITE)
                     .withUnderlined(true)
                     .withClickEvent(new ClickEvent(Action.OPEN_FILE, configHelper.createJeiConfigDir().toAbsolutePath().toString()))
               )
         );
   }
}
