package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class HexRegistries {
   public static final ResourceKey<Registry<ActionRegistryEntry>> ACTION = ResourceKey.createRegistryKey(HexAPI.modLoc("action"));
   public static final ResourceKey<Registry<SpecialHandler.Factory<?>>> SPECIAL_HANDLER = ResourceKey.createRegistryKey(HexAPI.modLoc("special_handler"));
   public static final ResourceKey<Registry<IotaType<?>>> IOTA_TYPE = ResourceKey.createRegistryKey(HexAPI.modLoc("iota_type"));
   public static final ResourceKey<Registry<Arithmetic>> ARITHMETIC = ResourceKey.createRegistryKey(HexAPI.modLoc("arithmetic"));
   public static final ResourceKey<Registry<ContinuationFrame.Type<?>>> CONTINUATION_TYPE = ResourceKey.createRegistryKey(HexAPI.modLoc("continuation_type"));
   public static final ResourceKey<Registry<EvalSound>> EVAL_SOUND = ResourceKey.createRegistryKey(HexAPI.modLoc("eval_sound"));
}
