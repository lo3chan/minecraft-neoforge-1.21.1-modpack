package tannyjung.tanshugetrees.init;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tannyjung.tanshugetrees.network.MenuStateUpdateMessage;
import tannyjung.tanshugetrees.world.inventory.TreeSummonerStaffGUIMenu;

public class TanshugetreesModMenus {
   public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, "tanshugetrees");
   public static final DeferredHolder<MenuType<?>, MenuType<TreeSummonerStaffGUIMenu>> TREE_SUMMONER_STAFF_GUI = REGISTRY.register(
      "tree_summoner_staff_gui", () -> IMenuTypeExtension.create(TreeSummonerStaffGUIMenu::new)
   );

   public interface MenuAccessor {
      Map<String, Object> getMenuState();

      Map<Integer, Slot> getSlots();

      default void sendMenuStateUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
         this.getMenuState().put(elementType + ":" + name, elementState);
         if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new MenuStateUpdateMessage(elementType, name, elementState), new CustomPacketPayload[0]);
         } else if (player.level().isClientSide) {
            if (Minecraft.getInstance().screen instanceof TanshugetreesModScreens.ScreenAccessor accessor && needClientUpdate) {
               accessor.updateMenuState(elementType, name, elementState);
            }

            PacketDistributor.sendToServer(new MenuStateUpdateMessage(elementType, name, elementState), new CustomPacketPayload[0]);
         }
      }

      default <T> T getMenuState(int elementType, String name, T defaultValue) {
         try {
            return (T)this.getMenuState().getOrDefault(elementType + ":" + name, defaultValue);
         } catch (ClassCastException var5) {
            return defaultValue;
         }
      }
   }
}
