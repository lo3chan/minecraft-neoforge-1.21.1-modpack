package dev.latvian.mods.kubejs.level.ruletest;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class AllMatchRuleTest extends RuleTest {
   public static final MapCodec<AllMatchRuleTest> CODEC = RuleTest.CODEC.listOf().fieldOf("rules").xmap(AllMatchRuleTest::new, t -> t.rules);
   public final List<RuleTest> rules;

   public AllMatchRuleTest() {
      this(new ArrayList<>());
   }

   public AllMatchRuleTest(List<RuleTest> rules) {
      this.rules = rules;
   }

   public boolean test(BlockState blockState, RandomSource random) {
      for (RuleTest test : this.rules) {
         if (!test.test(blockState, random)) {
            return false;
         }
      }

      return true;
   }

   protected RuleTestType<?> getType() {
      return KubeJSRuleTests.ALL_MATCH.get();
   }
}
