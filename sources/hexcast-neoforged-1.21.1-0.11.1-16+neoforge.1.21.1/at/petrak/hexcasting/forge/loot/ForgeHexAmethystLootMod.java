package at.petrak.hexcasting.forge.loot;

import at.petrak.hexcasting.common.loot.AmethystReducerFunc;
import at.petrak.hexcasting.common.loot.HexLootHandler;
import at.petrak.hexcasting.forge.lib.ForgeHexLootMods;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class ForgeHexAmethystLootMod extends LootModifier {
   public static final Supplier<MapCodec<ForgeHexAmethystLootMod>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.mapCodec(
         inst -> codecStart(inst).and(Codec.DOUBLE.fieldOf("shardDelta").forGetter(it -> it.shardDelta)).apply(inst, ForgeHexAmethystLootMod::new)
      )
   );
   public final double shardDelta;

   public ForgeHexAmethystLootMod(LootItemCondition[] conditionsIn, double shardDelta) {
      super(conditionsIn);
      this.shardDelta = shardDelta;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      LootTable injectPool = context.getLevel()
         .getServer()
         .reloadableRegistries()
         .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, HexLootHandler.TABLE_INJECT_AMETHYST_CLUSTER));
      injectPool.getRandomItemsRaw(context, generatedLoot::add);
      ObjectListIterator var4 = generatedLoot.iterator();

      while (var4.hasNext()) {
         ItemStack stack = (ItemStack)var4.next();
         AmethystReducerFunc.doStatic(stack, context, this.shardDelta);
      }

      return generatedLoot;
   }

   public MapCodec<ForgeHexAmethystLootMod> codec() {
      return (MapCodec<ForgeHexAmethystLootMod>)ForgeHexLootMods.AMETHYST.get();
   }
}
