package net.bettercombat.config;

public class FallbackConfig {
   public int schema_version;
   public String blacklist_item_id_regex;
   public FallbackConfig.CompatibilitySpecifier[] fallback_compatibility;
   public FallbackConfig.CompatibilitySpecifier[] ranged_weapons;

   public static FallbackConfig createDefault() {
      FallbackConfig object = new FallbackConfig();
      object.schema_version = 1;
      object.blacklist_item_id_regex = "pickaxe";
      object.fallback_compatibility = new FallbackConfig.CompatibilitySpecifier[]{
         new FallbackConfig.CompatibilitySpecifier("claymore|great_sword|greatsword", "bettercombat:claymore"),
         new FallbackConfig.CompatibilitySpecifier("great_hammer|greathammer|war_hammer|warhammer|maul", "bettercombat:hammer"),
         new FallbackConfig.CompatibilitySpecifier("double_axe|doubleaxe|war_axe|waraxe|great_axe|greataxe", "bettercombat:double_axe"),
         new FallbackConfig.CompatibilitySpecifier("scythe", "bettercombat:scythe"),
         new FallbackConfig.CompatibilitySpecifier("halberd", "bettercombat:halberd"),
         new FallbackConfig.CompatibilitySpecifier("glaive", "bettercombat:glaive"),
         new FallbackConfig.CompatibilitySpecifier("spear", "bettercombat:spear"),
         new FallbackConfig.CompatibilitySpecifier("lance", "bettercombat:lance"),
         new FallbackConfig.CompatibilitySpecifier("anchor", "bettercombat:anchor"),
         new FallbackConfig.CompatibilitySpecifier("battlestaff|battle_staff", "bettercombat:battlestaff"),
         new FallbackConfig.CompatibilitySpecifier("claw", "bettercombat:claw"),
         new FallbackConfig.CompatibilitySpecifier("fist|gauntlet", "bettercombat:fist"),
         new FallbackConfig.CompatibilitySpecifier("trident|javelin|impaled", "bettercombat:trident"),
         new FallbackConfig.CompatibilitySpecifier("katana", "bettercombat:katana"),
         new FallbackConfig.CompatibilitySpecifier("rapier", "bettercombat:rapier"),
         new FallbackConfig.CompatibilitySpecifier("sickle", "bettercombat:sickle"),
         new FallbackConfig.CompatibilitySpecifier("soul_knife", "bettercombat:soul_knife"),
         new FallbackConfig.CompatibilitySpecifier("dagger|knife", "bettercombat:dagger"),
         new FallbackConfig.CompatibilitySpecifier("staff|wand|sceptre|stave|rod", "bettercombat:wand"),
         new FallbackConfig.CompatibilitySpecifier("mace|hammer|flail", "bettercombat:mace"),
         new FallbackConfig.CompatibilitySpecifier("axe", "bettercombat:axe"),
         new FallbackConfig.CompatibilitySpecifier("coral_blade", "bettercombat:coral_blade"),
         new FallbackConfig.CompatibilitySpecifier("twin_blade|twinblade", "bettercombat:twin_blade"),
         new FallbackConfig.CompatibilitySpecifier("cutlass|scimitar|machete", "bettercombat:cutlass"),
         new FallbackConfig.CompatibilitySpecifier("sword|blade", "bettercombat:sword")
      };
      object.ranged_weapons = new FallbackConfig.CompatibilitySpecifier[]{
         new FallbackConfig.CompatibilitySpecifier("two_handed_crossbow", "bettercombat:crossbow_two_handed_heavy"),
         new FallbackConfig.CompatibilitySpecifier("two_handed_bow", "bettercombat:bow_two_handed_heavy")
      };
      return object;
   }

   public static FallbackConfig migrate(FallbackConfig oldConfig, FallbackConfig newConfig) {
      newConfig.fallback_compatibility = oldConfig.fallback_compatibility;
      return newConfig;
   }

   public static class CompatibilitySpecifier {
      public String item_id_regex;
      public String weapon_attributes;

      public CompatibilitySpecifier() {
      }

      public CompatibilitySpecifier(String item_id_regex, String weapon_attributes) {
         this.item_id_regex = item_id_regex;
         this.weapon_attributes = weapon_attributes;
      }
   }
}
