package dev.shadowsoffire.placebo.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry.IntrusiveBase;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class WeightedItemStack extends IntrusiveBase {
   public static final Codec<WeightedItemStack> CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            OptionalStackCodec.INSTANCE.fieldOf("stack").forGetter(w -> w.stack),
            Weight.CODEC.fieldOf("weight").forGetter(IntrusiveBase::getWeight),
            Codec.FLOAT.optionalFieldOf("drop_chance", -1.0F).forGetter(w -> w.dropChance)
         )
         .apply(inst, WeightedItemStack::new)
   );
   public static final Codec<List<WeightedItemStack>> LIST_CODEC = CODEC.listOf();
   final ItemStack stack;
   final float dropChance;

   public WeightedItemStack(ItemStack stack, Weight weight, float dropChance) {
      super(weight);
      this.stack = stack;
      this.dropChance = dropChance;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public String toString() {
      return "Stack: " + this.stack.toString() + " @ Weight: " + this.getWeight().asInt();
   }

   public void apply(LivingEntity entity, EquipmentSlot slot) {
      entity.setItemSlot(slot, this.stack.copy());
      if (this.dropChance >= 0.0F && entity instanceof Mob mob) {
         mob.setDropChance(slot, this.dropChance);
      }
   }
}
