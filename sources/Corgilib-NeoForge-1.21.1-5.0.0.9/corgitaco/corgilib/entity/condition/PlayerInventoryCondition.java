package corgitaco.corgilib.entity.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.entity.ItemStackCheck;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlayerInventoryCondition implements Condition {
   public static final Codec<PlayerInventoryCondition> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(ItemStackCheck.CODEC.listOf().fieldOf("has").forGetter(wearingCondition -> wearingCondition.stackChecks))
         .apply(builder, PlayerInventoryCondition::new)
   );
   private final List<ItemStackCheck> stackChecks;
   private final Map<Item, ItemStackCheck> itemItemStackCheckMap;

   public PlayerInventoryCondition(List<ItemStackCheck> stackChecks) {
      if (stackChecks.isEmpty()) {
         throw new IllegalArgumentException("No item stack checks were specified.");
      } else {
         this.stackChecks = stackChecks;
         this.itemItemStackCheckMap = new Object2ObjectOpenHashMap();

         for (ItemStackCheck stackCheck : stackChecks) {
            Item item = stackCheck.getItem();
            if (this.itemItemStackCheckMap.containsKey(item)) {
               throw new UnsupportedOperationException("Found another check for an already existing item.");
            }

            this.itemItemStackCheckMap.put(item.asItem(), stackCheck);
         }
      }
   }

   @Override
   public boolean passes(ConditionContext conditionContext) {
      if (conditionContext.entity() instanceof Player) {
         for (ItemStack item : ((Player)conditionContext.entity()).getInventory().items) {
            if (this.itemItemStackCheckMap.containsKey(item.getItem())) {
               ItemStackCheck itemStackCheck = this.itemItemStackCheckMap.get(item.getItem());
               if (!itemStackCheck.test(item)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public Codec<? extends Condition> codec() {
      return CODEC;
   }
}
