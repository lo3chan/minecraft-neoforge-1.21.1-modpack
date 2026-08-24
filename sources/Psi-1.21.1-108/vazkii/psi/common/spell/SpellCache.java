package vazkii.psi.common.spell;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.ISpellCache;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.core.handler.ConfigHandler;

public final class SpellCache implements ISpellCache {
   public static final SpellCache instance = new SpellCache();
   public static final Map<UUID, CompiledSpell> map = new LinkedHashMap<UUID, CompiledSpell>() {
      @Override
      protected boolean removeEldestEntry(Entry<UUID, CompiledSpell> eldest) {
         return this.size() > (Integer)ConfigHandler.COMMON.spellCacheSize.get();
      }
   };

   @Override
   public CompiledSpell getCompiledSpell(Spell spell) {
      if (map.containsKey(spell.uuid)) {
         return map.get(spell.uuid);
      } else {
         Optional<CompiledSpell> result = new SpellCompiler().compile(spell).left();
         return result.<CompiledSpell>map(compSpell -> {
            map.put(spell.uuid, compSpell);
            return (CompiledSpell)compSpell;
         }).orElse(null);
      }
   }
}
