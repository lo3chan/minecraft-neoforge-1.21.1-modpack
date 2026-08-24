package net.joefoxe.hexerei.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import org.jetbrains.annotations.NotNull;

public class SageSeedAdditionModifier extends LootModifier {
   private final Item addition;
   private final int count;
   public static final MapCodec<SageSeedAdditionModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> codecStart(instance)
         .and(
            instance.group(
               Codec.STRING.optionalFieldOf("addition", "").forGetter(d -> BuiltInRegistries.ITEM.getKey(d.addition).toString()),
               Codec.INT.optionalFieldOf("count", 1).forGetter(d -> d.count)
            )
         )
         .apply(instance, SageSeedAdditionModifier::new)
   );
   private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "hexerei"
   );
   private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<SageSeedAdditionModifier>> GRASS_DROPS = REGISTER.register(
      "sage_seed_drops", () -> CODEC
   );

   public SageSeedAdditionModifier(LootItemCondition[] lootItemConditions, String addition, Integer count) {
      super(lootItemConditions);
      this.addition = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse(addition));
      this.count = count;
   }

   public static void init(IEventBus eventBus) {
      REGISTER.register(eventBus);
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      generatedLoot.add(new ItemStack(this.addition, this.count));
      return generatedLoot;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return (MapCodec<? extends IGlobalLootModifier>)GRASS_DROPS.get();
   }
}
