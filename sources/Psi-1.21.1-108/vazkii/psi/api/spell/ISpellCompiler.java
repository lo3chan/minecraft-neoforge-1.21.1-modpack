package vazkii.psi.api.spell;

import com.mojang.datafixers.util.Either;

public interface ISpellCompiler {
   Either<CompiledSpell, SpellCompilationException> compile(Spell var1);
}
