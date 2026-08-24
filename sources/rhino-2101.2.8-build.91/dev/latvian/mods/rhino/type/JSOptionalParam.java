package dev.latvian.mods.rhino.type;

public record JSOptionalParam(String name, TypeInfo type, boolean optional) {
   public JSOptionalParam(String name, TypeInfo type) {
      this(name, type, false);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      this.append(TypeStringContext.DEFAULT, sb);
      return sb.toString();
   }

   public void append(TypeStringContext ctx, StringBuilder sb) {
      if (!this.name.isEmpty()) {
         sb.append(this.name);
         if (this.optional) {
            sb.append('?');
         }

         sb.append(':');
         ctx.appendSpace(sb);
      }

      ctx.append(sb, this.type);
      if (this.optional && this.name.isEmpty()) {
         sb.append('?');
      }
   }
}
