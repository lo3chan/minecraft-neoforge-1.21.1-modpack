package amp_libs.org.antlr.v4.runtime.atn;

import amp_libs.org.antlr.v4.runtime.Lexer;
import amp_libs.org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerChannelAction implements LexerAction {
   private final int channel;

   public LexerChannelAction(int channel) {
      this.channel = channel;
   }

   public int getChannel() {
      return this.channel;
   }

   @Override
   public LexerActionType getActionType() {
      return LexerActionType.CHANNEL;
   }

   @Override
   public boolean isPositionDependent() {
      return false;
   }

   @Override
   public void execute(Lexer lexer) {
      lexer.setChannel(this.channel);
   }

   @Override
   public int hashCode() {
      int hash = MurmurHash.initialize();
      hash = MurmurHash.update(hash, this.getActionType().ordinal());
      hash = MurmurHash.update(hash, this.channel);
      return MurmurHash.finish(hash, 2);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof LexerChannelAction) ? false : this.channel == ((LexerChannelAction)obj).channel;
      }
   }

   @Override
   public String toString() {
      return String.format("channel(%d)", this.channel);
   }
}
