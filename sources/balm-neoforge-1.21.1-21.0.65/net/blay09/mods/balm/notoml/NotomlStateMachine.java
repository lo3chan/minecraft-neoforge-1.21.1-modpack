package net.blay09.mods.balm.notoml;

public class NotomlStateMachine {
   private NotomlParserState state = NotomlParserState.None;

   public void transition(NotomlParserState state) {
      this.state = state;
   }

   public void end() {
      this.state = null;
   }

   public boolean next(NotomlParseBuffer buffer, NotomlTokenConsumer consumer) {
      try {
         this.state.next(this, buffer, consumer);
      } catch (Exception var4) {
         consumer.emitError(new NotomlError(var4.getMessage()).at(buffer.getLine()));
         buffer.readUntil("\n");
         this.state = NotomlParserState.None;
      }

      return this.state != null;
   }
}
