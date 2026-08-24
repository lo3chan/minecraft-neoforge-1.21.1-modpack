package net.blay09.mods.balm.api.compat.hudinfo;

@FunctionalInterface
public interface BlockInfoProvider {
   void apply(BlockInfoContext var1, HudInfoOutput var2);
}
