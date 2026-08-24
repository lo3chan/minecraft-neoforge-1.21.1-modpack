package at.petrak.hexcasting.common.items.magic;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.utils.MathUtils;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.api.utils.NBTHelper;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public abstract class ItemMediaHolder extends Item implements MediaHolderItem {
   public static final String TAG_MEDIA = "hexcasting:media";
   public static final String TAG_MAX_MEDIA = "hexcasting:start_media";
   public static final TextColor HEX_COLOR = TextColor.fromRgb(11767539);
   private static final DecimalFormat PERCENTAGE = new DecimalFormat("####");
   private static final DecimalFormat DUST_AMOUNT = new DecimalFormat("###,###.##");

   public ItemMediaHolder(Properties pProperties) {
      super(pProperties);
   }

   public static ItemStack withMedia(ItemStack stack, long media, long maxMedia) {
      Item item = stack.getItem();
      if (item instanceof ItemMediaHolder) {
         NBTHelper.putLong(stack, "hexcasting:media", media);
         NBTHelper.putLong(stack, "hexcasting:start_media", maxMedia);
      }

      return stack;
   }

   @Override
   public long getMedia(ItemStack stack) {
      return NBTHelper.hasInt(stack, "hexcasting:media") ? NBTHelper.getInt(stack, "hexcasting:media") : NBTHelper.getLong(stack, "hexcasting:media");
   }

   @Override
   public long getMaxMedia(ItemStack stack) {
      return NBTHelper.hasInt(stack, "hexcasting:start_media")
         ? NBTHelper.getInt(stack, "hexcasting:start_media")
         : NBTHelper.getLong(stack, "hexcasting:start_media");
   }

   @Override
   public void setMedia(ItemStack stack, long media) {
      NBTHelper.putLong(stack, "hexcasting:media", MathUtils.clamp(media, 0L, this.getMaxMedia(stack)));
   }

   public boolean isBarVisible(ItemStack pStack) {
      return this.getMaxMedia(pStack) > 0L;
   }

   public int getBarColor(ItemStack pStack) {
      long media = this.getMedia(pStack);
      long maxMedia = this.getMaxMedia(pStack);
      return MediaHelper.mediaBarColor(media, maxMedia);
   }

   public int getBarWidth(ItemStack pStack) {
      long media = this.getMedia(pStack);
      long maxMedia = this.getMaxMedia(pStack);
      return MediaHelper.mediaBarWidth(media, maxMedia);
   }

   public boolean canBeDepleted() {
      return false;
   }

   public void appendHoverText(ItemStack pStack, TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      long maxMedia = this.getMaxMedia(pStack);
      if (maxMedia > 0L) {
         long media = this.getMedia(pStack);
         float fullness = this.getMediaFullness(pStack);
         TextColor color = TextColor.fromRgb(MediaHelper.mediaBarColor(media, maxMedia));
         MutableComponent mediamount = Component.literal(DUST_AMOUNT.format((float)media / 10000.0F));
         MutableComponent percentFull = Component.literal(PERCENTAGE.format(100.0F * fullness) + "%");
         MutableComponent maxCapacity = Component.translatable("hexcasting.tooltip.media", new Object[]{DUST_AMOUNT.format((float)maxMedia / 10000.0F)});
         mediamount.withStyle(style -> style.withColor(HEX_COLOR));
         maxCapacity.withStyle(style -> style.withColor(HEX_COLOR));
         percentFull.withStyle(style -> style.withColor(color));
         pTooltipComponents.add(Component.translatable("hexcasting.tooltip.media_amount.advanced", new Object[]{mediamount, maxCapacity, percentFull}));
      }

      super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
   }

   static {
      PERCENTAGE.setRoundingMode(RoundingMode.DOWN);
   }
}
