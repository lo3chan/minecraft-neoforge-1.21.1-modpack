package com.aetherteam.aether.world.processor;

import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherPosRuleTests {
   public static final DeferredRegister<PosRuleTestType<?>> POS_RULE_TESTS = DeferredRegister.create(BuiltInRegistries.POS_RULE_TEST.key(), "aether");
   public static final Supplier<PosRuleTestType<BorderBoxPosTest>> BORDER_BOX = POS_RULE_TESTS.register("border_box", () -> () -> BorderBoxPosTest.CODEC);
}
