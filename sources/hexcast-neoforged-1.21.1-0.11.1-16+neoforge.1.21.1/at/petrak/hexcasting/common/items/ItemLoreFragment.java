package at.petrak.hexcasting.common.items;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.lib.HexSounds;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemLoreFragment extends Item {
   public static final List<ResourceLocation> NAMES = List.of(
      HexAPI.modLoc("lore/cardamom1"),
      HexAPI.modLoc("lore/cardamom2"),
      HexAPI.modLoc("lore/cardamom3"),
      HexAPI.modLoc("lore/cardamom4"),
      HexAPI.modLoc("lore/cardamom5"),
      HexAPI.modLoc("lore/experiment1"),
      HexAPI.modLoc("lore/experiment2"),
      HexAPI.modLoc("lore/inventory")
   );
   public static final String CRITEREON_KEY = "grant";

   public ItemLoreFragment(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
      player.playSound(HexSounds.READ_LORE_FRAGMENT, 1.0F, 1.0F);
      ItemStack handStack = player.getItemInHand(usedHand);
      if (!(player instanceof ServerPlayer splayer)) {
         handStack.shrink(1);
         return InteractionResultHolder.success(handStack);
      } else {
         AdvancementHolder unfoundLore = null;
         ArrayList<ResourceLocation> shuffled = new ArrayList<>(NAMES);
         Collections.shuffle(shuffled);

         for (ResourceLocation advID : shuffled) {
            AdvancementHolder adv = splayer.server.getAdvancements().get(advID);
            if (adv != null && !splayer.getAdvancements().getOrStartProgress(adv).isDone()) {
               unfoundLore = adv;
               break;
            }
         }

         if (unfoundLore == null) {
            splayer.displayClientMessage(Component.translatable("item.hexcasting.lore_fragment.all"), true);
            splayer.giveExperiencePoints(20);
            level.playSound(null, player.position().x, player.position().y, player.position().z, HexSounds.READ_LORE_FRAGMENT, SoundSource.PLAYERS, 1.0F, 1.0F);
         } else {
            splayer.getAdvancements().award(unfoundLore, "grant");
         }

         CriteriaTriggers.CONSUME_ITEM.trigger(splayer, handStack);
         splayer.awardStat(Stats.ITEM_USED.get(this));
         handStack.shrink(1);
         return InteractionResultHolder.success(handStack);
      }
   }
}
