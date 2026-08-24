package at.petrak.hexcasting.api.advancements;

import at.petrak.hexcasting.api.HexAPI;
import java.util.function.BiConsumer;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

public class HexAdvancementTriggers {
   public static final OvercastTrigger OVERCAST_TRIGGER = new OvercastTrigger();
   public static final SpendMediaTrigger SPEND_MEDIA_TRIGGER = new SpendMediaTrigger();
   public static final FailToCastGreatSpellTrigger FAIL_GREAT_SPELL_TRIGGER = new FailToCastGreatSpellTrigger();

   public static void registerTriggers(BiConsumer<CriterionTrigger<?>, ResourceLocation> r) {
      r.accept(OVERCAST_TRIGGER, HexAPI.modLoc("overcast"));
      r.accept(SPEND_MEDIA_TRIGGER, HexAPI.modLoc("spend_media"));
      r.accept(FAIL_GREAT_SPELL_TRIGGER, HexAPI.modLoc("fail_to_cast_great_spell"));
   }
}
