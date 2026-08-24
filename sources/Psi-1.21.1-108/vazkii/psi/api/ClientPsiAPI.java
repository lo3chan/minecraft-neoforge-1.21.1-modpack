package vazkii.psi.api;

import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.RegistryBuilder;

@OnlyIn(Dist.CLIENT)
public class ClientPsiAPI {
   public static final ResourceKey<Registry<Material>> SPELL_PIECE_MATERIAL = ResourceKey.createRegistryKey(PsiAPI.location("spell_piece_material_key"));
   public static final Registry<Material> SPELL_PIECE_MATERIAL_REGISTRY = new RegistryBuilder(SPELL_PIECE_MATERIAL).create();
}
