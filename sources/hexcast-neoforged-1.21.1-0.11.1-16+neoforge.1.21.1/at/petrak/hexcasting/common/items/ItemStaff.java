package at.petrak.hexcasting.common.items;

import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.List;
import kotlin.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemStaff extends Item {
   public static final ResourceLocation FUNNY_LEVEL_PREDICATE = ResourceLocation.fromNamespaceAndPath("hexcasting", "funny_level");

   public ItemStaff(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (player.isShiftKeyDown()) {
         if (world.isClientSide()) {
            player.playSound(HexSounds.STAFF_RESET, 1.0F, 1.0F);
         } else if (player instanceof ServerPlayer serverPlayer) {
            IXplatAbstractions.INSTANCE.clearCastingData(serverPlayer);
            MsgClearSpiralPatternsS2C packet = new MsgClearSpiralPatternsS2C(player.getUUID());
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer, packet);
            IXplatAbstractions.INSTANCE.sendPacketTracking(serverPlayer, packet);
         }
      }

      if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
         CastingVM harness = IXplatAbstractions.INSTANCE.getStaffcastVM(serverPlayer, hand);
         List<ResolvedPattern> patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(serverPlayer);
         Pair<List<CompoundTag>, CompoundTag> descs = harness.generateDescs();
         IXplatAbstractions.INSTANCE
            .sendPacketToPlayer(serverPlayer, new MsgOpenSpellGuiS2C(hand, patterns, (List<CompoundTag>)descs.getFirst(), (CompoundTag)descs.getSecond(), 0));
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      return InteractionResultHolder.success(player.getItemInHand(hand));
   }
}
