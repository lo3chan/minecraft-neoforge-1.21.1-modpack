package jeresources.api;

public interface IJERPlugin {
   String entry_point = "jer_mod_plugin";

   void receive(IJERAPI var1);
}
