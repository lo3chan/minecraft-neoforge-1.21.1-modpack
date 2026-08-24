package vazkii.psi.api.spell;

public enum EnumPieceType {
   SELECTOR,
   OPERATOR,
   CONSTANT,
   CONNECTOR,
   MODIFIER,
   TRICK,
   ERROR_HANDLER;

   public boolean isTrick() {
      return this == TRICK || this == MODIFIER;
   }
}
