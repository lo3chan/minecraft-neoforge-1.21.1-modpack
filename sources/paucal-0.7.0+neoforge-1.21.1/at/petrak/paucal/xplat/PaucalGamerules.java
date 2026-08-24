package at.petrak.paucal.xplat;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.Key;

public class PaucalGamerules extends GameRules {
   public static final Key<BooleanValue> ALLOW_HEADPATS = register("paucal:allowHeadpats", Category.PLAYER, BooleanValue.create(true));

   public static void init() {
   }
}
