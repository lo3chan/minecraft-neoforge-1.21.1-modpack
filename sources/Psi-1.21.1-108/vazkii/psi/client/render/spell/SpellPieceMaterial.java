package vazkii.psi.client.render.spell;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.psi.api.ClientPsiAPI;

@OnlyIn(Dist.CLIENT)
public final class SpellPieceMaterial {
   public static final DeferredRegister<Material> SPELL_PIECE_MATERIAL = DeferredRegister.create(ClientPsiAPI.SPELL_PIECE_MATERIAL, "psi");
   public static final DeferredHolder<Material, Material> CROSS_CONNECTOR = SPELL_PIECE_MATERIAL.register(
      "cross_connector", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/cross_connector"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_SAVED_VECTOR = SPELL_PIECE_MATERIAL.register(
      "selector_saved_vector", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_saved_vector"))
   );
   public static final DeferredHolder<Material, Material> TRICK_DETONATE = SPELL_PIECE_MATERIAL.register(
      "trick_detonate", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_detonate"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SAVE_VECTOR = SPELL_PIECE_MATERIAL.register(
      "trick_save_vector", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_save_vector"))
   );
   public static final DeferredHolder<Material, Material> TRICK_DEBUG = SPELL_PIECE_MATERIAL.register(
      "trick_debug", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_debug"))
   );
   public static final DeferredHolder<Material, Material> TRICK_DEBUG_SPAMLESS = SPELL_PIECE_MATERIAL.register(
      "trick_debug_spamless", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_debug_spamless"))
   );
   public static final DeferredHolder<Material, Material> CONSTANT_NUMBER = SPELL_PIECE_MATERIAL.register(
      "constant_number", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/constant_number"))
   );
   public static final DeferredHolder<Material, Material> CONNECTOR = SPELL_PIECE_MATERIAL.register(
      "connector", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/connector"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ENTITY_LOOK = SPELL_PIECE_MATERIAL.register(
      "operator_entity_look", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_entity_look"))
   );
   public static final DeferredHolder<Material, Material> TRICK_ADD_MOTION = SPELL_PIECE_MATERIAL.register(
      "trick_add_motion", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_add_motion"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ENTITY_POSITION = SPELL_PIECE_MATERIAL.register(
      "operator_entity_position", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_entity_position"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_RAYCAST = SPELL_PIECE_MATERIAL.register(
      "operator_vector_raycast", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_raycast"))
   );
   public static final DeferredHolder<Material, Material> TRICK_EXPLODE = SPELL_PIECE_MATERIAL.register(
      "trick_explode", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_explode"))
   );
   public static final DeferredHolder<Material, Material> ERROR_SUPPRESSOR = SPELL_PIECE_MATERIAL.register(
      "error_suppressor", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/error_suppressor"))
   );
   public static final DeferredHolder<Material, Material> ERROR_CATCH = SPELL_PIECE_MATERIAL.register(
      "error_catch", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/error_catch"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_FOCAL_POINT = SPELL_PIECE_MATERIAL.register(
      "selector_focal_point", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_focal_point"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_RULER_VECTOR = SPELL_PIECE_MATERIAL.register(
      "selector_ruler_vector", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_ruler_vector"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_ITEMS = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_items", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_items"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_LIVING = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_living", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_living"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_ENEMIES = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_enemies", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_enemies"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_ANIMALS = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_animals", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_animals"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_PROJECTILES = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_projectiles",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_projectiles"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_CHARGES = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_charges", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_charges"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_FALLING_BLOCKS = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_falling_blocks",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_falling_blocks"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_GLOWING = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_glowing", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_glowing"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_PLAYERS = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_players", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_players"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_VEHICLES = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_vehicles", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_vehicles"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ENTITY_MOTION = SPELL_PIECE_MATERIAL.register(
      "operator_entity_motion", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_entity_motion"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ENTITY_AXIAL_LOOK = SPELL_PIECE_MATERIAL.register(
      "operator_entity_axial_look",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_entity_axial_look"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_CLOSEST_TO_POINT = SPELL_PIECE_MATERIAL.register(
      "operator_closest_to_point",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_closest_to_point"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_RANDOM_ENTITY = SPELL_PIECE_MATERIAL.register(
      "operator_random_entity", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_random_entity"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_FOCUSED_ENTITY = SPELL_PIECE_MATERIAL.register(
      "operator_focused_entity", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_focused_entity"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_LIST_ADD = SPELL_PIECE_MATERIAL.register(
      "operator_list_add", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_list_add"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_LIST_REMOVE = SPELL_PIECE_MATERIAL.register(
      "operator_list_remove", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_list_remove"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_CLOSEST_TO_LINE = SPELL_PIECE_MATERIAL.register(
      "operator_closest_to_line", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_closest_to_line"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ENTITY_HEALTH = SPELL_PIECE_MATERIAL.register(
      "operator_entity_health", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_entity_health"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ENTITY_RAYCAST = SPELL_PIECE_MATERIAL.register(
      "operator_entity_raycast", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_entity_raycast"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ENTITY_HEIGHT = SPELL_PIECE_MATERIAL.register(
      "operator_entity_height", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_entity_height"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_BLOCK_BROKEN = SPELL_PIECE_MATERIAL.register(
      "selector_block_broken", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_block_broken"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_BLOCK_SIDE_BROKEN = SPELL_PIECE_MATERIAL.register(
      "selector_block_side_broken",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_block_side_broken"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_ATTACK_TARGET = SPELL_PIECE_MATERIAL.register(
      "selector_attack_target", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_attack_target"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_ITEM_COUNT = SPELL_PIECE_MATERIAL.register(
      "selector_item_count", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_item_count"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_LOOPCAST_INDEX = SPELL_PIECE_MATERIAL.register(
      "selector_loopcast_index", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_loopcast_index"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_MODULUS = SPELL_PIECE_MATERIAL.register(
      "operator_modulus", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_modulus"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_INTEGER_DIVIDE = SPELL_PIECE_MATERIAL.register(
      "operator_integer_divide", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_integer_divide"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_SNEAK_STATUS = SPELL_PIECE_MATERIAL.register(
      "selector_sneak_status", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_sneak_status"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_TICK_TIME = SPELL_PIECE_MATERIAL.register(
      "selector_tick_time", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_tick_time"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_TPS = SPELL_PIECE_MATERIAL.register(
      "selector_tps", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_tps"))
   );
   public static final DeferredHolder<Material, Material> TRICK_DELAY = SPELL_PIECE_MATERIAL.register(
      "trick_delay", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_delay"))
   );
   public static final DeferredHolder<Material, Material> TRICK_DIE = SPELL_PIECE_MATERIAL.register(
      "trick_die", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_die"))
   );
   public static final DeferredHolder<Material, Material> TRICK_EVALUATE = SPELL_PIECE_MATERIAL.register(
      "trick_evaluate", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_evaluate"))
   );
   public static final DeferredHolder<Material, Material> TRICK_BREAK_LOOP = SPELL_PIECE_MATERIAL.register(
      "trick_break_loop", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_break_loop"))
   );
   public static final DeferredHolder<Material, Material> CONSTANT_WRAPPER = SPELL_PIECE_MATERIAL.register(
      "constant_wrapper", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/constant_wrapper"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_SUM = SPELL_PIECE_MATERIAL.register(
      "operator_sum", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_sum"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_SUBTRACT = SPELL_PIECE_MATERIAL.register(
      "operator_subtract", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_subtract"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_MULTIPLY = SPELL_PIECE_MATERIAL.register(
      "operator_multiply", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_multiply"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_DIVIDE = SPELL_PIECE_MATERIAL.register(
      "operator_divide", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_divide"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ABSOLUTE = SPELL_PIECE_MATERIAL.register(
      "operator_absolute", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_absolute"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_INVERSE = SPELL_PIECE_MATERIAL.register(
      "operator_inverse", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_inverse"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ROOT = SPELL_PIECE_MATERIAL.register(
      "operator_root", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_root"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_SQUARE = SPELL_PIECE_MATERIAL.register(
      "operator_square", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_square"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_CUBE = SPELL_PIECE_MATERIAL.register(
      "operator_cube", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_cube"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_POWER = SPELL_PIECE_MATERIAL.register(
      "operator_power", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_power"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_SQUARE_ROOT = SPELL_PIECE_MATERIAL.register(
      "operator_square_root", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_square_root"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_LOG = SPELL_PIECE_MATERIAL.register(
      "operator_log", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_log"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_CEILING = SPELL_PIECE_MATERIAL.register(
      "operator_ceiling", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_ceiling"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_FLOOR = SPELL_PIECE_MATERIAL.register(
      "operator_floor", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_floor"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ROUND = SPELL_PIECE_MATERIAL.register(
      "operator_round", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_round"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_MAX = SPELL_PIECE_MATERIAL.register(
      "operator_max", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_max"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_MIN = SPELL_PIECE_MATERIAL.register(
      "operator_min", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_min"))
   );
   public static final DeferredHolder<Material, Material> CONSTANT_E = SPELL_PIECE_MATERIAL.register(
      "constant_e", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/constant_e"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_SIN = SPELL_PIECE_MATERIAL.register(
      "operator_sin", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_sin"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_COS = SPELL_PIECE_MATERIAL.register(
      "operator_cos", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_cos"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ASIN = SPELL_PIECE_MATERIAL.register(
      "operator_asin", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_asin"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_ACOS = SPELL_PIECE_MATERIAL.register(
      "operator_acos", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_acos"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_DOT_PRODUCT = SPELL_PIECE_MATERIAL.register(
      "operator_vector_dot_product",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_dot_product"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_GAMMA_FUNCTION = SPELL_PIECE_MATERIAL.register(
      "operator_gamma_function", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_gamma_function"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_PLANAR_NORMAL_VECTOR = SPELL_PIECE_MATERIAL.register(
      "operator_planar_normal_vector",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_planar_normal_vector"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_ROTATE = SPELL_PIECE_MATERIAL.register(
      "operator_vector_rotate", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_rotate"))
   );
   public static final DeferredHolder<Material, Material> CONSTANT_PI = SPELL_PIECE_MATERIAL.register(
      "constant_pi", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/constant_pi"))
   );
   public static final DeferredHolder<Material, Material> CONSTANT_TAU = SPELL_PIECE_MATERIAL.register(
      "constant_tau", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/constant_tau"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_SIGNUM = SPELL_PIECE_MATERIAL.register(
      "operator_extract_sign", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_extract_sign"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_ABSOLUTE = SPELL_PIECE_MATERIAL.register(
      "operator_vector_absolute", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_absolute"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_SIGNUM = SPELL_PIECE_MATERIAL.register(
      "operator_vector_extract_sign",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_extract_sign"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_SUM = SPELL_PIECE_MATERIAL.register(
      "operator_vector_sum", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_sum"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_SUBTRACT = SPELL_PIECE_MATERIAL.register(
      "operator_vector_subtract", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_subtract"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_MULTIPLY = SPELL_PIECE_MATERIAL.register(
      "operator_vector_multiply", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_multiply"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_DIVIDE = SPELL_PIECE_MATERIAL.register(
      "operator_vector_divide", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_divide"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_CROSS_PRODUCT = SPELL_PIECE_MATERIAL.register(
      "operator_vector_cross_product",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_cross_product"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_NORMALIZE = SPELL_PIECE_MATERIAL.register(
      "operator_vector_normalize",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_normalize"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_NEGATE = SPELL_PIECE_MATERIAL.register(
      "operator_vector_negate", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_negate"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_MAGNITUDE = SPELL_PIECE_MATERIAL.register(
      "operator_vector_magnitude",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_magnitude"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_CONSTRUCT = SPELL_PIECE_MATERIAL.register(
      "operator_vector_construct",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_construct"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_EXTRACT_X = SPELL_PIECE_MATERIAL.register(
      "operator_vector_extract_x",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_extract_x"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_EXTRACT_Y = SPELL_PIECE_MATERIAL.register(
      "operator_vector_extract_y",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_extract_y"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_EXTRACT_Z = SPELL_PIECE_MATERIAL.register(
      "operator_vector_extract_z",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_extract_z"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_MAXIMUM = SPELL_PIECE_MATERIAL.register(
      "operator_vector_piecewise_maximum",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_piecewise_maximum"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_MINIMUM = SPELL_PIECE_MATERIAL.register(
      "operator_vector_piecewise_minimum",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_piecewise_minimum"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_RAYCAST_AXIS = SPELL_PIECE_MATERIAL.register(
      "operator_vector_raycast_axis",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_raycast_axis"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_VECTOR_PROJECT = SPELL_PIECE_MATERIAL.register(
      "operator_vector_project", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_vector_project"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_BLOCK_LIGHT = SPELL_PIECE_MATERIAL.register(
      "operator_block_light", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_block_light"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_BLOCK_HARDNESS = SPELL_PIECE_MATERIAL.register(
      "operator_block_hardness", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_block_hardness"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_BLOCK_COMPARATOR_STRENGTH = SPELL_PIECE_MATERIAL.register(
      "operator_block_comparator_strength",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_block_comparator_strength"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_BLOCK_SIDE_SOLIDITY = SPELL_PIECE_MATERIAL.register(
      "operator_block_side_solidity",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_block_side_solidity"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_BLOCK_MINING_LEVEL = SPELL_PIECE_MATERIAL.register(
      "operator_block_mining_level",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_block_mining_level"))
   );
   public static final DeferredHolder<Material, Material> TRICK_BREAK_BLOCK = SPELL_PIECE_MATERIAL.register(
      "trick_break_block", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_break_block"))
   );
   public static final DeferredHolder<Material, Material> TRICK_BREAK_IN_SEQUENCE = SPELL_PIECE_MATERIAL.register(
      "trick_break_in_sequence", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_break_in_sequence"))
   );
   public static final DeferredHolder<Material, Material> TRICK_PLACE_BLOCK = SPELL_PIECE_MATERIAL.register(
      "trick_place_block", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_place_block"))
   );
   public static final DeferredHolder<Material, Material> TRICK_PLACE_IN_SEQUENCE = SPELL_PIECE_MATERIAL.register(
      "trick_place_in_sequence", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_place_in_sequence"))
   );
   public static final DeferredHolder<Material, Material> TRICK_MOVE_BLOCK = SPELL_PIECE_MATERIAL.register(
      "trick_move_block", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_move_block"))
   );
   public static final DeferredHolder<Material, Material> TRICK_COLLAPSE_BLOCK = SPELL_PIECE_MATERIAL.register(
      "trick_collapse_block", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_collapse_block"))
   );
   public static final DeferredHolder<Material, Material> TRICK_MOVE_BLOCK_SEQUENCE = SPELL_PIECE_MATERIAL.register(
      "trick_move_block_sequence",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_move_block_sequence"))
   );
   public static final DeferredHolder<Material, Material> TRICK_COLLAPSE_BLOCK_SEQUENCE = SPELL_PIECE_MATERIAL.register(
      "trick_collapse_block_sequence",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_collapse_block_sequence"))
   );
   public static final DeferredHolder<Material, Material> TRICK_CONJURE_BLOCK = SPELL_PIECE_MATERIAL.register(
      "trick_conjure_block", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_conjure_block"))
   );
   public static final DeferredHolder<Material, Material> TRICK_CONJURE_LIGHT = SPELL_PIECE_MATERIAL.register(
      "trick_conjure_light", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_conjure_light"))
   );
   public static final DeferredHolder<Material, Material> TRICK_CONJURE_BLOCK_SEQUENCE = SPELL_PIECE_MATERIAL.register(
      "trick_conjure_block_sequence",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_conjure_block_sequence"))
   );
   public static final DeferredHolder<Material, Material> TRICK_PARTICLE_TRAIL = SPELL_PIECE_MATERIAL.register(
      "trick_particle_trail", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_particle_trail"))
   );
   public static final DeferredHolder<Material, Material> TRICK_BLINK = SPELL_PIECE_MATERIAL.register(
      "trick_blink", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_blink"))
   );
   public static final DeferredHolder<Material, Material> TRICK_MASS_BLINK = SPELL_PIECE_MATERIAL.register(
      "trick_mass_blink", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_mass_blink"))
   );
   public static final DeferredHolder<Material, Material> TRICK_MASS_ADD_MOTION = SPELL_PIECE_MATERIAL.register(
      "trick_mass_add_motion", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_mass_add_motion"))
   );
   public static final DeferredHolder<Material, Material> TRICK_MASS_EXODUS = SPELL_PIECE_MATERIAL.register(
      "trick_mass_exodus", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_mass_exodus"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_IS_ELYTRA_FLYING = SPELL_PIECE_MATERIAL.register(
      "selector_is_elytra_flying",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_is_elytra_flying"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_RANDOM = SPELL_PIECE_MATERIAL.register(
      "operator_random", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_random"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SMITE = SPELL_PIECE_MATERIAL.register(
      "trick_smite", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_smite"))
   );
   public static final DeferredHolder<Material, Material> TRICK_BLAZE = SPELL_PIECE_MATERIAL.register(
      "trick_blaze", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_blaze"))
   );
   public static final DeferredHolder<Material, Material> TRICK_TORRENT = SPELL_PIECE_MATERIAL.register(
      "trick_torrent", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_torrent"))
   );
   public static final DeferredHolder<Material, Material> TRICK_OVERGROW = SPELL_PIECE_MATERIAL.register(
      "trick_overgrow", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_overgrow"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SPEED = SPELL_PIECE_MATERIAL.register(
      "trick_speed", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_speed"))
   );
   public static final DeferredHolder<Material, Material> TRICK_HASTE = SPELL_PIECE_MATERIAL.register(
      "trick_haste", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_haste"))
   );
   public static final DeferredHolder<Material, Material> TRICK_STRENGTH = SPELL_PIECE_MATERIAL.register(
      "trick_strength", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_strength"))
   );
   public static final DeferredHolder<Material, Material> TRICK_JUMP_BOOST = SPELL_PIECE_MATERIAL.register(
      "trick_jump_boost", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_jump_boost"))
   );
   public static final DeferredHolder<Material, Material> TRICK_WATER_BREATHING = SPELL_PIECE_MATERIAL.register(
      "trick_water_breathing", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_water_breathing"))
   );
   public static final DeferredHolder<Material, Material> TRICK_FIRE_RESISTANCE = SPELL_PIECE_MATERIAL.register(
      "trick_fire_resistance", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_fire_resistance"))
   );
   public static final DeferredHolder<Material, Material> TRICK_INVISIBILITY = SPELL_PIECE_MATERIAL.register(
      "trick_invisibility", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_invisibility"))
   );
   public static final DeferredHolder<Material, Material> TRICK_REGENERATION = SPELL_PIECE_MATERIAL.register(
      "trick_regeneration", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_regeneration"))
   );
   public static final DeferredHolder<Material, Material> TRICK_RESISTANCE = SPELL_PIECE_MATERIAL.register(
      "trick_resistance", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_resistance"))
   );
   public static final DeferredHolder<Material, Material> TRICK_NIGHT_VISION = SPELL_PIECE_MATERIAL.register(
      "trick_night_vision", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_night_vision"))
   );
   public static final DeferredHolder<Material, Material> TRICK_WITHER = SPELL_PIECE_MATERIAL.register(
      "trick_wither", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_wither"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SLOWNESS = SPELL_PIECE_MATERIAL.register(
      "trick_slowness", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_slowness"))
   );
   public static final DeferredHolder<Material, Material> TRICK_WEAKNESS = SPELL_PIECE_MATERIAL.register(
      "trick_weakness", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_weakness"))
   );
   public static final DeferredHolder<Material, Material> TRICK_IGNITE = SPELL_PIECE_MATERIAL.register(
      "trick_ignite", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_ignite"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_EIDOS_CHANGELOG = SPELL_PIECE_MATERIAL.register(
      "selector_eidos_changelog", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_eidos_changelog"))
   );
   public static final DeferredHolder<Material, Material> TRICK_EIDOS_ANCHOR = SPELL_PIECE_MATERIAL.register(
      "trick_eidos_anchor", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_eidos_anchor"))
   );
   public static final DeferredHolder<Material, Material> TRICK_EIDOS_REVERSAL = SPELL_PIECE_MATERIAL.register(
      "trick_eidos_reversal", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_eidos_reversal"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_TIME = SPELL_PIECE_MATERIAL.register(
      "selector_time", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_time"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_ATTACKER = SPELL_PIECE_MATERIAL.register(
      "selector_attacker", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_attacker"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_DAMAGE_TAKEN = SPELL_PIECE_MATERIAL.register(
      "selector_damage_taken", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_damage_taken"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_SUCCESS_COUNTER = SPELL_PIECE_MATERIAL.register(
      "selector_sucession_counter",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_sucession_counter"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_CASTER_BATTERY = SPELL_PIECE_MATERIAL.register(
      "selector_caster_battery", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_caster_battery"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_CASTER_ENERGY = SPELL_PIECE_MATERIAL.register(
      "selector_caster_energy", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_caster_energy"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_ITEM_PRESENCE = SPELL_PIECE_MATERIAL.register(
      "selector_item_presence", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_item_presence"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_BLOCK_PRESENCE = SPELL_PIECE_MATERIAL.register(
      "selector_block_presence", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_block_presence"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SWITCH_TARGET_SLOT = SPELL_PIECE_MATERIAL.register(
      "trick_switch_target_slot", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_switch_target_slot"))
   );
   public static final DeferredHolder<Material, Material> TRICK_CHANGE_SLOT = SPELL_PIECE_MATERIAL.register(
      "trick_change_slot", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_change_slot"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_NEARBY_SMELTABLES = SPELL_PIECE_MATERIAL.register(
      "selector_nearby_smeltables",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_nearby_smeltables"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SMELT_BLOCK = SPELL_PIECE_MATERIAL.register(
      "trick_smelt_block", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_smelt_block"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SMELT_ITEM = SPELL_PIECE_MATERIAL.register(
      "trick_smelt_item", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_smelt_item"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SMELT_BLOCK_SEQUENCE = SPELL_PIECE_MATERIAL.register(
      "trick_smelt_block_sequence",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_smelt_block_sequence"))
   );
   public static final DeferredHolder<Material, Material> TRICK_INFUSION = SPELL_PIECE_MATERIAL.register(
      "trick_infusion", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_infusion"))
   );
   public static final DeferredHolder<Material, Material> TRICK_GREATER_INFUSION = SPELL_PIECE_MATERIAL.register(
      "trick_greater_infusion", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_greater_infusion"))
   );
   public static final DeferredHolder<Material, Material> TRICK_EBONY_IVORY = SPELL_PIECE_MATERIAL.register(
      "trick_ebony_ivory", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_ebony_ivory"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_LIST_EXCLUSION = SPELL_PIECE_MATERIAL.register(
      "operator_list_exclusion", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_list_exclusion"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_LIST_INTERSECTION = SPELL_PIECE_MATERIAL.register(
      "operator_list_intersection",
      () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_list_intersection"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_LIST_SIZE = SPELL_PIECE_MATERIAL.register(
      "operator_list_size", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_list_size"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_LIST_UNION = SPELL_PIECE_MATERIAL.register(
      "operator_list_union", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_list_union"))
   );
   public static final DeferredHolder<Material, Material> OPERATOR_LIST_INDEX = SPELL_PIECE_MATERIAL.register(
      "operator_list_index", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/operator_list_index"))
   );
   public static final DeferredHolder<Material, Material> SELECTOR_CASTER = SPELL_PIECE_MATERIAL.register(
      "selector_caster", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/selector_caster"))
   );
   public static final DeferredHolder<Material, Material> TRICK_PLAY_SOUND = SPELL_PIECE_MATERIAL.register(
      "trick_play_sound", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_play_sound"))
   );
   public static final DeferredHolder<Material, Material> TRICK_TILL = SPELL_PIECE_MATERIAL.register(
      "trick_till", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_till"))
   );
   public static final DeferredHolder<Material, Material> TRICK_TILL_SEQUENCE = SPELL_PIECE_MATERIAL.register(
      "trick_till_sequence", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_till_sequence"))
   );
   public static final DeferredHolder<Material, Material> TRICK_SPIN_CHAMBER = SPELL_PIECE_MATERIAL.register(
      "trick_spin_chamber", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_spin_chamber"))
   );
   public static final DeferredHolder<Material, Material> TRICK_RUSSIAN_ROULETTE = SPELL_PIECE_MATERIAL.register(
      "trick_russian_roulette", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_russian_roulette"))
   );
   public static final DeferredHolder<Material, Material> TRICK_CONJURE_CIRCLE = SPELL_PIECE_MATERIAL.register(
      "trick_conjure_circle", () -> new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath("psi", "spell/trick_conjure_circle"))
   );
}
