package net.cibernet.alchemancy.mixin.accessors;

import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({VaultServerData.class})
public interface VaultServerDataAccessor {
   @Invoker
   Set<UUID> invokeGetRewardedPlayers();
}
