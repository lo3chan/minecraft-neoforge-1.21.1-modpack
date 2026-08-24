package at.petrak.hexcasting.xplat;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.client.ClientCastingStack;
import at.petrak.hexcasting.common.msgs.IMessage;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface IClientXplatAbstractions {
   IClientXplatAbstractions INSTANCE = find();

   void sendPacketToServer(IMessage var1);

   void setRenderLayer(Block var1, RenderType var2);

   void initPlatformSpecific();

   <T extends Entity> void registerEntityRenderer(EntityType<? extends T> var1, EntityRendererProvider<T> var2);

   void registerItemProperty(Item var1, ResourceLocation var2, ItemPropertyFunction var3);

   ClientCastingStack getClientCastingStack(Player var1);

   void setFilterSave(AbstractTexture var1, boolean var2, boolean var3);

   void restoreLastFilter(AbstractTexture var1);

   private static IClientXplatAbstractions find() {
      List<Provider<IClientXplatAbstractions>> providers = ServiceLoader.load(IClientXplatAbstractions.class).stream().toList();
      if (providers.size() != 1) {
         String names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
         throw new IllegalStateException("There should be exactly one IClientXplatAbstractions implementation on the classpath. Found: " + names);
      } else {
         Provider<IClientXplatAbstractions> provider = providers.get(0);
         HexAPI.LOGGER.debug("Instantiating client xplat impl: " + provider.type().getName());
         return provider.get();
      }
   }
}
