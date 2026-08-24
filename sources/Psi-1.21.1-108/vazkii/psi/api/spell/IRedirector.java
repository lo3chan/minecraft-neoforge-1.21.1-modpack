package vazkii.psi.api.spell;

public interface IRedirector extends IGenericRedirector {
   SpellParam.Side getRedirectionSide();

   @Override
   default SpellParam.Side remapSide(SpellParam.Side inputSide) {
      return this.getRedirectionSide();
   }
}
