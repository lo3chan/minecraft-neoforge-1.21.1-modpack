package snownee.jade.mixin;

import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CanItemPerformAbility.class})
public interface CanItemPerformAbilityAccess {
   @Accessor
   ItemAbility getAbility();
}
