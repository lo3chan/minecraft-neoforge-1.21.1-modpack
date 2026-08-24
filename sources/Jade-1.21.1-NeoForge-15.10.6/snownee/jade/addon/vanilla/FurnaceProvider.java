package snownee.jade.addon.vanilla;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.mixin.AbstractFurnaceBlockEntityAccess;

public enum FurnaceProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, FurnaceProvider.Data> {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      FurnaceProvider.Data data = this.decodeFromData(accessor).orElse(null);
      if (data != null) {
         IElementHelper helper = IElementHelper.get();
         tooltip.add(helper.item(data.inventory.get(0)));
         tooltip.append(helper.item(data.inventory.get(1)));
         tooltip.append(helper.spacer(4, 0));
         tooltip.append(helper.progress((float)data.progress / data.total).translate(new Vec2(-2.0F, 0.0F)));
         tooltip.append(helper.item(data.inventory.get(2)));
      }
   }

   public FurnaceProvider.Data streamData(BlockAccessor accessor) {
      AbstractFurnaceBlockEntityAccess access = (AbstractFurnaceBlockEntityAccess)accessor.getBlockEntity();
      AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity)accessor.getBlockEntity();
      return new FurnaceProvider.Data(
         access.getCookingProgress(), access.getCookingTotalTime(), List.of(furnace.getItem(0), furnace.getItem(1), furnace.getItem(2))
      );
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, FurnaceProvider.Data> streamCodec() {
      return FurnaceProvider.Data.STREAM_CODEC;
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_FURNACE;
   }

   public record Data(int progress, int total, List<ItemStack> inventory) {
      public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceProvider.Data> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.VAR_INT,
         FurnaceProvider.Data::progress,
         ByteBufCodecs.VAR_INT,
         FurnaceProvider.Data::total,
         ItemStack.OPTIONAL_LIST_STREAM_CODEC,
         FurnaceProvider.Data::inventory,
         FurnaceProvider.Data::new
      );
   }
}
