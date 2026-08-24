package vazkii.psi.common.spell.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.common.spell.constant.PieceConstantE;
import vazkii.psi.common.spell.constant.PieceConstantNumber;
import vazkii.psi.common.spell.constant.PieceConstantPi;
import vazkii.psi.common.spell.constant.PieceConstantTau;
import vazkii.psi.common.spell.constant.PieceConstantWrapper;
import vazkii.psi.common.spell.operator.block.PieceOperatorBlockComparatorStrength;
import vazkii.psi.common.spell.operator.block.PieceOperatorBlockHardness;
import vazkii.psi.common.spell.operator.block.PieceOperatorBlockLightLevel;
import vazkii.psi.common.spell.operator.block.PieceOperatorBlockMiningLevel;
import vazkii.psi.common.spell.operator.block.PieceOperatorBlockSideSolidity;
import vazkii.psi.common.spell.operator.entity.PieceOperatorClosestToLine;
import vazkii.psi.common.spell.operator.entity.PieceOperatorClosestToPoint;
import vazkii.psi.common.spell.operator.entity.PieceOperatorEntityAxialLook;
import vazkii.psi.common.spell.operator.entity.PieceOperatorEntityHealth;
import vazkii.psi.common.spell.operator.entity.PieceOperatorEntityHeight;
import vazkii.psi.common.spell.operator.entity.PieceOperatorEntityLook;
import vazkii.psi.common.spell.operator.entity.PieceOperatorEntityMotion;
import vazkii.psi.common.spell.operator.entity.PieceOperatorEntityPosition;
import vazkii.psi.common.spell.operator.entity.PieceOperatorEntityRaycast;
import vazkii.psi.common.spell.operator.entity.PieceOperatorFocusedEntity;
import vazkii.psi.common.spell.operator.entity.PieceOperatorListAdd;
import vazkii.psi.common.spell.operator.entity.PieceOperatorListRemove;
import vazkii.psi.common.spell.operator.entity.PieceOperatorRandomEntity;
import vazkii.psi.common.spell.operator.list.PieceOperatorListExclusion;
import vazkii.psi.common.spell.operator.list.PieceOperatorListIndex;
import vazkii.psi.common.spell.operator.list.PieceOperatorListIntersection;
import vazkii.psi.common.spell.operator.list.PieceOperatorListSize;
import vazkii.psi.common.spell.operator.list.PieceOperatorListUnion;
import vazkii.psi.common.spell.operator.number.PieceOperatorAbsolute;
import vazkii.psi.common.spell.operator.number.PieceOperatorCeiling;
import vazkii.psi.common.spell.operator.number.PieceOperatorCube;
import vazkii.psi.common.spell.operator.number.PieceOperatorDivide;
import vazkii.psi.common.spell.operator.number.PieceOperatorFloor;
import vazkii.psi.common.spell.operator.number.PieceOperatorGammaFunc;
import vazkii.psi.common.spell.operator.number.PieceOperatorIntegerDivide;
import vazkii.psi.common.spell.operator.number.PieceOperatorInverse;
import vazkii.psi.common.spell.operator.number.PieceOperatorLog;
import vazkii.psi.common.spell.operator.number.PieceOperatorMax;
import vazkii.psi.common.spell.operator.number.PieceOperatorMin;
import vazkii.psi.common.spell.operator.number.PieceOperatorModulus;
import vazkii.psi.common.spell.operator.number.PieceOperatorMultiply;
import vazkii.psi.common.spell.operator.number.PieceOperatorPower;
import vazkii.psi.common.spell.operator.number.PieceOperatorRandom;
import vazkii.psi.common.spell.operator.number.PieceOperatorRoot;
import vazkii.psi.common.spell.operator.number.PieceOperatorRound;
import vazkii.psi.common.spell.operator.number.PieceOperatorSignum;
import vazkii.psi.common.spell.operator.number.PieceOperatorSquare;
import vazkii.psi.common.spell.operator.number.PieceOperatorSquareRoot;
import vazkii.psi.common.spell.operator.number.PieceOperatorSubtract;
import vazkii.psi.common.spell.operator.number.PieceOperatorSum;
import vazkii.psi.common.spell.operator.number.trig.PieceOperatorAcos;
import vazkii.psi.common.spell.operator.number.trig.PieceOperatorAsin;
import vazkii.psi.common.spell.operator.number.trig.PieceOperatorCos;
import vazkii.psi.common.spell.operator.number.trig.PieceOperatorSin;
import vazkii.psi.common.spell.operator.vector.PieceOperatorPlanarNormalVector;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorAbsolute;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorConstruct;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorCrossProduct;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorDivide;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorDotProduct;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorExtractX;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorExtractY;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorExtractZ;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorMagnitude;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorMaximum;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorMinimum;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorMultiply;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorNegate;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorNormalize;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorProject;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorRaycast;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorRaycastAxis;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorRotate;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorSignum;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorSubtract;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorSum;
import vazkii.psi.common.spell.other.PieceConnector;
import vazkii.psi.common.spell.other.PieceCrossConnector;
import vazkii.psi.common.spell.other.PieceErrorCatch;
import vazkii.psi.common.spell.other.PieceErrorSuppressor;
import vazkii.psi.common.spell.selector.PieceSelectorAttackTarget;
import vazkii.psi.common.spell.selector.PieceSelectorAttacker;
import vazkii.psi.common.spell.selector.PieceSelectorBlockBroken;
import vazkii.psi.common.spell.selector.PieceSelectorBlockPresence;
import vazkii.psi.common.spell.selector.PieceSelectorBlockSideBroken;
import vazkii.psi.common.spell.selector.PieceSelectorCaster;
import vazkii.psi.common.spell.selector.PieceSelectorDamageTaken;
import vazkii.psi.common.spell.selector.PieceSelectorEidosChangelog;
import vazkii.psi.common.spell.selector.PieceSelectorFocalPoint;
import vazkii.psi.common.spell.selector.PieceSelectorItemCount;
import vazkii.psi.common.spell.selector.PieceSelectorItemPresence;
import vazkii.psi.common.spell.selector.PieceSelectorLoopcastIndex;
import vazkii.psi.common.spell.selector.PieceSelectorRulerVector;
import vazkii.psi.common.spell.selector.PieceSelectorSavedVector;
import vazkii.psi.common.spell.selector.PieceSelectorSneakStatus;
import vazkii.psi.common.spell.selector.PieceSelectorTickTime;
import vazkii.psi.common.spell.selector.PieceSelectorTime;
import vazkii.psi.common.spell.selector.PieceSelectorTps;
import vazkii.psi.common.spell.selector.entity.PieceSelectorCasterBattery;
import vazkii.psi.common.spell.selector.entity.PieceSelectorCasterEnergy;
import vazkii.psi.common.spell.selector.entity.PieceSelectorIsElytraFlying;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyAnimals;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyCharges;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyEnemies;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyFallingBlocks;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyGlowing;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyItems;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyLiving;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyPlayers;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyProjectiles;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbySmeltables;
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbyVehicles;
import vazkii.psi.common.spell.selector.entity.PieceSelectorSuccessCounter;
import vazkii.psi.common.spell.trick.PieceTrickBlaze;
import vazkii.psi.common.spell.trick.PieceTrickBreakLoop;
import vazkii.psi.common.spell.trick.PieceTrickChangeSlot;
import vazkii.psi.common.spell.trick.PieceTrickDebug;
import vazkii.psi.common.spell.trick.PieceTrickDebugSpamless;
import vazkii.psi.common.spell.trick.PieceTrickDelay;
import vazkii.psi.common.spell.trick.PieceTrickDetonate;
import vazkii.psi.common.spell.trick.PieceTrickDie;
import vazkii.psi.common.spell.trick.PieceTrickEidosAnchor;
import vazkii.psi.common.spell.trick.PieceTrickEidosReversal;
import vazkii.psi.common.spell.trick.PieceTrickEvaluate;
import vazkii.psi.common.spell.trick.PieceTrickExplode;
import vazkii.psi.common.spell.trick.PieceTrickOvergrow;
import vazkii.psi.common.spell.trick.PieceTrickParticleTrail;
import vazkii.psi.common.spell.trick.PieceTrickPlaySound;
import vazkii.psi.common.spell.trick.PieceTrickRussianRoulette;
import vazkii.psi.common.spell.trick.PieceTrickSaveVector;
import vazkii.psi.common.spell.trick.PieceTrickSmite;
import vazkii.psi.common.spell.trick.PieceTrickSpinChamber;
import vazkii.psi.common.spell.trick.PieceTrickSwitchTargetSlot;
import vazkii.psi.common.spell.trick.PieceTrickTorrent;
import vazkii.psi.common.spell.trick.block.PieceTrickBreakBlock;
import vazkii.psi.common.spell.trick.block.PieceTrickBreakInSequence;
import vazkii.psi.common.spell.trick.block.PieceTrickCollapseBlock;
import vazkii.psi.common.spell.trick.block.PieceTrickCollapseBlockSequence;
import vazkii.psi.common.spell.trick.block.PieceTrickConjureBlock;
import vazkii.psi.common.spell.trick.block.PieceTrickConjureBlockSequence;
import vazkii.psi.common.spell.trick.block.PieceTrickConjureLight;
import vazkii.psi.common.spell.trick.block.PieceTrickMoveBlock;
import vazkii.psi.common.spell.trick.block.PieceTrickMoveBlockSequence;
import vazkii.psi.common.spell.trick.block.PieceTrickPlaceBlock;
import vazkii.psi.common.spell.trick.block.PieceTrickPlaceInSequence;
import vazkii.psi.common.spell.trick.block.PieceTrickSmeltBlock;
import vazkii.psi.common.spell.trick.block.PieceTrickSmeltBlockSequence;
import vazkii.psi.common.spell.trick.block.PieceTrickTill;
import vazkii.psi.common.spell.trick.block.PieceTrickTillSequence;
import vazkii.psi.common.spell.trick.entity.PieceTrickAddMotion;
import vazkii.psi.common.spell.trick.entity.PieceTrickBlink;
import vazkii.psi.common.spell.trick.entity.PieceTrickConjureCircle;
import vazkii.psi.common.spell.trick.entity.PieceTrickIgnite;
import vazkii.psi.common.spell.trick.entity.PieceTrickMassAddMotion;
import vazkii.psi.common.spell.trick.entity.PieceTrickMassBlink;
import vazkii.psi.common.spell.trick.entity.PieceTrickMassExodus;
import vazkii.psi.common.spell.trick.entity.PieceTrickSmeltItem;
import vazkii.psi.common.spell.trick.infusion.PieceTrickEbonyIvory;
import vazkii.psi.common.spell.trick.infusion.PieceTrickGreaterInfusion;
import vazkii.psi.common.spell.trick.infusion.PieceTrickInfusion;
import vazkii.psi.common.spell.trick.potion.PieceTrickFireResistance;
import vazkii.psi.common.spell.trick.potion.PieceTrickHaste;
import vazkii.psi.common.spell.trick.potion.PieceTrickInvisibility;
import vazkii.psi.common.spell.trick.potion.PieceTrickJumpBoost;
import vazkii.psi.common.spell.trick.potion.PieceTrickNightVision;
import vazkii.psi.common.spell.trick.potion.PieceTrickRegeneration;
import vazkii.psi.common.spell.trick.potion.PieceTrickResistance;
import vazkii.psi.common.spell.trick.potion.PieceTrickSlowness;
import vazkii.psi.common.spell.trick.potion.PieceTrickSpeed;
import vazkii.psi.common.spell.trick.potion.PieceTrickStrength;
import vazkii.psi.common.spell.trick.potion.PieceTrickWaterBreathing;
import vazkii.psi.common.spell.trick.potion.PieceTrickWeakness;
import vazkii.psi.common.spell.trick.potion.PieceTrickWither;

public final class ModSpellPieces {
   public static final DeferredRegister<Class<? extends SpellPiece>> SPELL_PIECES = DeferredRegister.create(PsiAPI.SPELL_PIECE_REGISTRY_TYPE_KEY, "psi");
   public static final DeferredRegister<Collection<Class<? extends SpellPiece>>> ADVANCEMENT_GROUPS = DeferredRegister.create(
      PsiAPI.ADVANCEMENT_GROUP_REGISTRY_KEY, "psi"
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceCrossConnector>> CROSS_CONNECTOR = SPELL_PIECES.register(
      "cross_connector", () -> PieceCrossConnector.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorSavedVector>> SELECTOR_SAVED_VECTOR = SPELL_PIECES.register(
      "selector_saved_vector", () -> PieceSelectorSavedVector.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickDetonate>> TRICK_DETONATE = SPELL_PIECES.register(
      "trick_detonate", () -> PieceTrickDetonate.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSaveVector>> TRICK_SAVE_VECTOR = SPELL_PIECES.register(
      "trick_save_vector", () -> PieceTrickSaveVector.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> MEMORY_MANAGEMENT = ADVANCEMENT_GROUPS.register(
      "memory_management", () -> Arrays.asList(PieceTrickSaveVector.class, PieceTrickDetonate.class, PieceSelectorSavedVector.class, PieceCrossConnector.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorCaster>> SELECTOR_CASTER = SPELL_PIECES.register(
      "selector_caster", () -> PieceSelectorCaster.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickDebug>> TRICK_DEBUG = SPELL_PIECES.register(
      "trick_debug", () -> PieceTrickDebug.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickDebugSpamless>> TRICK_DEBUG_SPAMLESS = SPELL_PIECES.register(
      "trick_debug_spamless", () -> PieceTrickDebugSpamless.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> TUTORIAL_1 = ADVANCEMENT_GROUPS.register(
      "tutorial1", () -> Arrays.asList(PieceSelectorCaster.class, PieceTrickDebug.class, PieceTrickDebugSpamless.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceConstantNumber>> CONSTANT_NUMBER = SPELL_PIECES.register(
      "constant_number", () -> PieceConstantNumber.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceConnector>> CONNECTOR = SPELL_PIECES.register(
      "connector", () -> PieceConnector.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> TUTORIAL_2 = ADVANCEMENT_GROUPS.register(
      "tutorial2", () -> Arrays.asList(PieceConstantNumber.class, PieceConnector.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorEntityLook>> OPERATOR_ENTITY_LOOK = SPELL_PIECES.register(
      "operator_entity_look", () -> PieceOperatorEntityLook.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickAddMotion>> TRICK_ADD_MOTION = SPELL_PIECES.register(
      "trick_add_motion", () -> PieceTrickAddMotion.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> TUTORIAL_3 = ADVANCEMENT_GROUPS.register(
      "tutorial3", () -> Arrays.asList(PieceTrickAddMotion.class, PieceOperatorEntityLook.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorEntityPosition>> OPERATOR_ENTITY_POSITION = SPELL_PIECES.register(
      "operator_entity_position", () -> PieceOperatorEntityPosition.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorRaycast>> OPERATOR_VECTOR_RAYCAST = SPELL_PIECES.register(
      "operator_vector_raycast", () -> PieceOperatorVectorRaycast.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickExplode>> TRICK_EXPLODE = SPELL_PIECES.register(
      "trick_explode", () -> PieceTrickExplode.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceErrorSuppressor>> ERROR_SUPPRESSOR = SPELL_PIECES.register(
      "error_suppressor", () -> PieceErrorSuppressor.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceErrorCatch>> ERROR_CATCH = SPELL_PIECES.register(
      "error_catch", () -> PieceErrorCatch.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> TUTORIAL_4 = ADVANCEMENT_GROUPS.register(
      "tutorial4",
      () -> Arrays.asList(
         PieceTrickExplode.class, PieceOperatorEntityPosition.class, PieceOperatorVectorRaycast.class, PieceErrorSuppressor.class, PieceErrorCatch.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorFocalPoint>> SELECTOR_FOCAL_POINT = SPELL_PIECES.register(
      "selector_focal_point", () -> PieceSelectorFocalPoint.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorRulerVector>> SELECTOR_RULER_VECTOR = SPELL_PIECES.register(
      "selector_ruler_vector", () -> PieceSelectorRulerVector.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> PROJECTILES = ADVANCEMENT_GROUPS.register(
      "projectiles", () -> Arrays.asList(PieceSelectorFocalPoint.class, PieceSelectorRulerVector.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyItems>> SELECTOR_NEARBY_ITEMS = SPELL_PIECES.register(
      "selector_nearby_items", () -> PieceSelectorNearbyItems.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyLiving>> SELECTOR_NEARBY_LIVING = SPELL_PIECES.register(
      "selector_nearby_living", () -> PieceSelectorNearbyLiving.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyEnemies>> SELECTOR_NEARBY_ENEMIES = SPELL_PIECES.register(
      "selector_nearby_enemies", () -> PieceSelectorNearbyEnemies.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyAnimals>> SELECTOR_NEARBY_ANIMALS = SPELL_PIECES.register(
      "selector_nearby_animals", () -> PieceSelectorNearbyAnimals.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyProjectiles>> SELECTOR_NEARBY_PROJECTILES = SPELL_PIECES.register(
      "selector_nearby_projectiles", () -> PieceSelectorNearbyProjectiles.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyCharges>> SELECTOR_NEARBY_CHARGES = SPELL_PIECES.register(
      "selector_nearby_charges", () -> PieceSelectorNearbyCharges.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyFallingBlocks>> SELECTOR_NEARBY_FALLING_BLOCKS = SPELL_PIECES.register(
      "selector_nearby_falling_blocks", () -> PieceSelectorNearbyFallingBlocks.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyGlowing>> SELECTOR_NEARBY_GLOWING = SPELL_PIECES.register(
      "selector_nearby_glowing", () -> PieceSelectorNearbyGlowing.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyPlayers>> SELECTOR_NEARBY_PLAYERS = SPELL_PIECES.register(
      "selector_nearby_players", () -> PieceSelectorNearbyPlayers.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbyVehicles>> SELECTOR_NEARBY_VEHICLES = SPELL_PIECES.register(
      "selector_nearby_vehicles", () -> PieceSelectorNearbyVehicles.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorEntityMotion>> OPERATOR_ENTITY_MOTION = SPELL_PIECES.register(
      "operator_entity_motion", () -> PieceOperatorEntityMotion.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorEntityAxialLook>> OPERATOR_ENTITY_AXIAL_LOOK = SPELL_PIECES.register(
      "operator_entity_axial_look", () -> PieceOperatorEntityAxialLook.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorClosestToPoint>> OPERATOR_CLOSEST_TO_POINT = SPELL_PIECES.register(
      "operator_closest_to_point", () -> PieceOperatorClosestToPoint.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorRandomEntity>> OPERATOR_RANDOM_ENTITY = SPELL_PIECES.register(
      "operator_random_entity", () -> PieceOperatorRandomEntity.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorFocusedEntity>> OPERATOR_FOCUSED_ENTITY = SPELL_PIECES.register(
      "operator_focused_entity", () -> PieceOperatorFocusedEntity.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorListAdd>> OPERATOR_LIST_ADD = SPELL_PIECES.register(
      "operator_list_add", () -> PieceOperatorListAdd.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorListRemove>> OPERATOR_LIST_REMOVE = SPELL_PIECES.register(
      "operator_list_remove", () -> PieceOperatorListRemove.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorClosestToLine>> OPERATOR_CLOSEST_TO_LINE = SPELL_PIECES.register(
      "operator_closest_to_line", () -> PieceOperatorClosestToLine.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorEntityHealth>> OPERATOR_ENTITY_HEALTH = SPELL_PIECES.register(
      "operator_entity_health", () -> PieceOperatorEntityHealth.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorEntityRaycast>> OPERATOR_ENTITY_RAYCAST = SPELL_PIECES.register(
      "operator_entity_raycast", () -> PieceOperatorEntityRaycast.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorEntityHeight>> OPERATOR_ENTITY_HEIGHT = SPELL_PIECES.register(
      "operator_entity_height", () -> PieceOperatorEntityHeight.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> ENTITIES_INTRO = ADVANCEMENT_GROUPS.register(
      "entities_intro",
      () -> Arrays.asList(
         PieceOperatorClosestToPoint.class,
         PieceSelectorNearbyItems.class,
         PieceSelectorNearbyLiving.class,
         PieceSelectorNearbyEnemies.class,
         PieceSelectorNearbyAnimals.class,
         PieceSelectorNearbyProjectiles.class,
         PieceSelectorNearbyCharges.class,
         PieceSelectorNearbyFallingBlocks.class,
         PieceSelectorNearbyGlowing.class,
         PieceSelectorNearbyPlayers.class,
         PieceSelectorNearbyVehicles.class,
         PieceOperatorEntityMotion.class,
         PieceOperatorEntityAxialLook.class,
         PieceOperatorRandomEntity.class,
         PieceOperatorFocusedEntity.class,
         PieceOperatorListAdd.class,
         PieceOperatorListRemove.class,
         PieceOperatorClosestToLine.class,
         PieceOperatorEntityHealth.class,
         PieceOperatorEntityRaycast.class,
         PieceOperatorEntityHeight.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorBlockBroken>> SELECTOR_BLOCK_BROKEN = SPELL_PIECES.register(
      "selector_block_broken", () -> PieceSelectorBlockBroken.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorBlockSideBroken>> SELECTOR_BLOCK_SIDE_BROKEN = SPELL_PIECES.register(
      "selector_block_side_broken", () -> PieceSelectorBlockSideBroken.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorAttackTarget>> SELECTOR_ATTACK_TARGET = SPELL_PIECES.register(
      "selector_attack_target", () -> PieceSelectorAttackTarget.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorItemCount>> SELECTOR_ITEM_COUNT = SPELL_PIECES.register(
      "selector_item_count", () -> PieceSelectorItemCount.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> TOOL_CASTING = ADVANCEMENT_GROUPS.register(
      "tool_casting",
      () -> Arrays.asList(PieceSelectorBlockBroken.class, PieceSelectorBlockSideBroken.class, PieceSelectorAttackTarget.class, PieceSelectorItemCount.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorLoopcastIndex>> SELECTOR_LOOPCAST_INDEX = SPELL_PIECES.register(
      "selector_loopcast_index", () -> PieceSelectorLoopcastIndex.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorModulus>> OPERATOR_MODULUS = SPELL_PIECES.register(
      "operator_modulus", () -> PieceOperatorModulus.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorIntegerDivide>> OPERATOR_INTEGER_DIVIDE = SPELL_PIECES.register(
      "operator_integer_divide", () -> PieceOperatorIntegerDivide.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> LOOPCASTING = ADVANCEMENT_GROUPS.register(
      "loopcasting", () -> Arrays.asList(PieceSelectorLoopcastIndex.class, PieceOperatorModulus.class, PieceOperatorIntegerDivide.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorSneakStatus>> SELECTOR_SNEAK_STATUS = SPELL_PIECES.register(
      "selector_sneak_status", () -> PieceSelectorSneakStatus.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorTickTime>> SELECTOR_TICK_TIME = SPELL_PIECES.register(
      "selector_tick_time", () -> PieceSelectorTickTime.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorTps>> SELECTOR_TPS = SPELL_PIECES.register(
      "selector_tps", () -> PieceSelectorTps.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickDelay>> TRICK_DELAY = SPELL_PIECES.register(
      "trick_delay", () -> PieceTrickDelay.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickDie>> TRICK_DIE = SPELL_PIECES.register(
      "trick_die", () -> PieceTrickDie.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickEvaluate>> TRICK_EVALUATE = SPELL_PIECES.register(
      "trick_evaluate", () -> PieceTrickEvaluate.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickBreakLoop>> TRICK_BREAK_LOOP = SPELL_PIECES.register(
      "trick_break_loop", () -> PieceTrickBreakLoop.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceConstantWrapper>> CONSTANT_WRAPPER = SPELL_PIECES.register(
      "constant_wrapper", () -> PieceConstantWrapper.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> FLOW_CONTROL = ADVANCEMENT_GROUPS.register(
      "flow_control",
      () -> Arrays.asList(
         PieceTrickDelay.class,
         PieceSelectorSneakStatus.class,
         PieceSelectorTickTime.class,
         PieceSelectorTps.class,
         PieceTrickDie.class,
         PieceTrickEvaluate.class,
         PieceTrickBreakLoop.class,
         PieceConstantWrapper.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorSum>> OPERATOR_SUM = SPELL_PIECES.register(
      "operator_sum", () -> PieceOperatorSum.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorSubtract>> OPERATOR_SUBTRACT = SPELL_PIECES.register(
      "operator_subtract", () -> PieceOperatorSubtract.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorMultiply>> OPERATOR_MULTIPLY = SPELL_PIECES.register(
      "operator_multiply", () -> PieceOperatorMultiply.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorDivide>> OPERATOR_DIVIDE = SPELL_PIECES.register(
      "operator_divide", () -> PieceOperatorDivide.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorAbsolute>> OPERATOR_ABSOLUTE = SPELL_PIECES.register(
      "operator_absolute", () -> PieceOperatorAbsolute.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorInverse>> OPERATOR_INVERSE = SPELL_PIECES.register(
      "operator_inverse", () -> PieceOperatorInverse.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorRoot>> OPERATOR_ROOT = SPELL_PIECES.register(
      "operator_root", () -> PieceOperatorRoot.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> NUMBERS_INTRO = ADVANCEMENT_GROUPS.register(
      "numbers_intro",
      () -> Arrays.asList(
         PieceOperatorSum.class,
         PieceOperatorSubtract.class,
         PieceOperatorMultiply.class,
         PieceOperatorDivide.class,
         PieceOperatorAbsolute.class,
         PieceOperatorInverse.class,
         PieceOperatorRoot.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorSquare>> OPERATOR_SQUARE = SPELL_PIECES.register(
      "operator_square", () -> PieceOperatorSquare.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorCube>> OPERATOR_CUBE = SPELL_PIECES.register(
      "operator_cube", () -> PieceOperatorCube.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorPower>> OPERATOR_POWER = SPELL_PIECES.register(
      "operator_power", () -> PieceOperatorPower.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorSquareRoot>> OPERATOR_SQUARE_ROOT = SPELL_PIECES.register(
      "operator_square_root", () -> PieceOperatorSquareRoot.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorLog>> OPERATOR_LOG = SPELL_PIECES.register(
      "operator_log", () -> PieceOperatorLog.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorCeiling>> OPERATOR_CEILING = SPELL_PIECES.register(
      "operator_ceiling", () -> PieceOperatorCeiling.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorFloor>> OPERATOR_FLOOR = SPELL_PIECES.register(
      "operator_floor", () -> PieceOperatorFloor.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorRound>> OPERATOR_ROUND = SPELL_PIECES.register(
      "operator_round", () -> PieceOperatorRound.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorMax>> OPERATOR_MAX = SPELL_PIECES.register(
      "operator_max", () -> PieceOperatorMax.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorMin>> OPERATOR_MIN = SPELL_PIECES.register(
      "operator_min", () -> PieceOperatorMin.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceConstantE>> CONSTANT_E = SPELL_PIECES.register(
      "constant_e", () -> PieceConstantE.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> SECONDARY_OPERATORS = ADVANCEMENT_GROUPS.register(
      "secondary_operators",
      () -> Arrays.asList(
         PieceOperatorSquare.class,
         PieceOperatorCube.class,
         PieceOperatorPower.class,
         PieceOperatorSquareRoot.class,
         PieceOperatorLog.class,
         PieceOperatorCeiling.class,
         PieceOperatorFloor.class,
         PieceOperatorRound.class,
         PieceOperatorMax.class,
         PieceOperatorMin.class,
         PieceConstantE.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorSin>> OPERATOR_SIN = SPELL_PIECES.register(
      "operator_sin", () -> PieceOperatorSin.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorCos>> OPERATOR_COS = SPELL_PIECES.register(
      "operator_cos", () -> PieceOperatorCos.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorAsin>> OPERATOR_ASIN = SPELL_PIECES.register(
      "operator_asin", () -> PieceOperatorAsin.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorAcos>> OPERATOR_ACOS = SPELL_PIECES.register(
      "operator_acos", () -> PieceOperatorAcos.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorDotProduct>> OPERATOR_VECTOR_DOT_PRODUCT = SPELL_PIECES.register(
      "operator_vector_dot_product", () -> PieceOperatorVectorDotProduct.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorGammaFunc>> OPERATOR_GAMMA_FUNCTION = SPELL_PIECES.register(
      "operator_gamma_function", () -> PieceOperatorGammaFunc.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorPlanarNormalVector>> OPERATOR_PLANAR_NORMAL_VECTOR = SPELL_PIECES.register(
      "operator_planar_normal_vector", () -> PieceOperatorPlanarNormalVector.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorRotate>> OPERATOR_VECTOR_ROTATE = SPELL_PIECES.register(
      "operator_vector_rotate", () -> PieceOperatorVectorRotate.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceConstantPi>> CONSTANT_PI = SPELL_PIECES.register(
      "constant_pi", () -> PieceConstantPi.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceConstantTau>> CONSTANT_TAU = SPELL_PIECES.register(
      "constant_tau", () -> PieceConstantTau.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorSignum>> OPERATOR_SIGNUM = SPELL_PIECES.register(
      "operator_extract_sign", () -> PieceOperatorSignum.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorAbsolute>> OPERATOR_VECTOR_ABSOLUTE = SPELL_PIECES.register(
      "operator_vector_absolute", () -> PieceOperatorVectorAbsolute.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorSignum>> OPERATOR_VECTOR_SIGNUM = SPELL_PIECES.register(
      "operator_vector_extract_sign", () -> PieceOperatorVectorSignum.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> TRIGONOMETRY = ADVANCEMENT_GROUPS.register(
      "trigonometry",
      () -> Arrays.asList(
         PieceConstantPi.class,
         PieceOperatorSin.class,
         PieceOperatorCos.class,
         PieceOperatorAsin.class,
         PieceOperatorAcos.class,
         PieceOperatorVectorDotProduct.class,
         PieceOperatorGammaFunc.class,
         PieceOperatorPlanarNormalVector.class,
         PieceOperatorVectorRotate.class,
         PieceConstantTau.class,
         PieceOperatorSignum.class,
         PieceOperatorVectorAbsolute.class,
         PieceOperatorVectorSignum.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorSum>> OPERATOR_VECTOR_SUM = SPELL_PIECES.register(
      "operator_vector_sum", () -> PieceOperatorVectorSum.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorSubtract>> OPERATOR_VECTOR_SUBTRACT = SPELL_PIECES.register(
      "operator_vector_subtract", () -> PieceOperatorVectorSubtract.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorMultiply>> OPERATOR_VECTOR_MULTIPLY = SPELL_PIECES.register(
      "operator_vector_multiply", () -> PieceOperatorVectorMultiply.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorDivide>> OPERATOR_VECTOR_DIVIDE = SPELL_PIECES.register(
      "operator_vector_divide", () -> PieceOperatorVectorDivide.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorCrossProduct>> OPERATOR_VECTOR_CROSS_PRODUCT = SPELL_PIECES.register(
      "operator_vector_cross_product", () -> PieceOperatorVectorCrossProduct.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorNormalize>> OPERATOR_VECTOR_NORMALIZE = SPELL_PIECES.register(
      "operator_vector_normalize", () -> PieceOperatorVectorNormalize.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorNegate>> OPERATOR_VECTOR_NEGATE = SPELL_PIECES.register(
      "operator_vector_negate", () -> PieceOperatorVectorNegate.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorMagnitude>> OPERATOR_VECTOR_MAGNITUDE = SPELL_PIECES.register(
      "operator_vector_magnitude", () -> PieceOperatorVectorMagnitude.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorConstruct>> OPERATOR_VECTOR_CONSTRUCT = SPELL_PIECES.register(
      "operator_vector_construct", () -> PieceOperatorVectorConstruct.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorExtractX>> OPERATOR_VECTOR_EXTRACT_X = SPELL_PIECES.register(
      "operator_vector_extract_x", () -> PieceOperatorVectorExtractX.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorExtractY>> OPERATOR_VECTOR_EXTRACT_Y = SPELL_PIECES.register(
      "operator_vector_extract_y", () -> PieceOperatorVectorExtractY.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorExtractZ>> OPERATOR_VECTOR_EXTRACT_Z = SPELL_PIECES.register(
      "operator_vector_extract_z", () -> PieceOperatorVectorExtractZ.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorMaximum>> OPERATOR_VECTOR_MAXIMUM = SPELL_PIECES.register(
      "operator_vector_piecewise_maximum", () -> PieceOperatorVectorMaximum.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorMinimum>> OPERATOR_VECTOR_MINIMUM = SPELL_PIECES.register(
      "operator_vector_piecewise_minimum", () -> PieceOperatorVectorMinimum.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> VECTORS_INTRO = ADVANCEMENT_GROUPS.register(
      "vectors_intro",
      () -> Arrays.asList(
         PieceOperatorVectorConstruct.class,
         PieceOperatorVectorSum.class,
         PieceOperatorVectorSubtract.class,
         PieceOperatorVectorMultiply.class,
         PieceOperatorVectorDivide.class,
         PieceOperatorVectorCrossProduct.class,
         PieceOperatorVectorNormalize.class,
         PieceOperatorVectorNegate.class,
         PieceOperatorVectorMagnitude.class,
         PieceOperatorVectorExtractX.class,
         PieceOperatorVectorExtractY.class,
         PieceOperatorVectorExtractZ.class,
         PieceOperatorVectorMaximum.class,
         PieceOperatorVectorMinimum.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorRaycastAxis>> OPERATOR_VECTOR_RAYCAST_AXIS = SPELL_PIECES.register(
      "operator_vector_raycast_axis", () -> PieceOperatorVectorRaycastAxis.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorVectorProject>> OPERATOR_VECTOR_PROJECT = SPELL_PIECES.register(
      "operator_vector_project", () -> PieceOperatorVectorProject.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorBlockLightLevel>> OPERATOR_BLOCK_LIGHT = SPELL_PIECES.register(
      "operator_block_light", () -> PieceOperatorBlockLightLevel.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorBlockHardness>> OPERATOR_BLOCK_HARDNESS = SPELL_PIECES.register(
      "operator_block_hardness", () -> PieceOperatorBlockHardness.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorBlockComparatorStrength>> OPERATOR_BLOCK_COMPARATOR_STRENGTH = SPELL_PIECES.register(
      "operator_block_comparator_strength", () -> PieceOperatorBlockComparatorStrength.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorBlockSideSolidity>> OPERATOR_BLOCK_SIDE_SOLIDITY = SPELL_PIECES.register(
      "operator_block_side_solidity", () -> PieceOperatorBlockSideSolidity.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorBlockMiningLevel>> OPERATOR_BLOCK_MINING_LEVEL = SPELL_PIECES.register(
      "operator_block_mining_level", () -> PieceOperatorBlockMiningLevel.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickBreakBlock>> TRICK_BREAK_BLOCK = SPELL_PIECES.register(
      "trick_break_block", () -> PieceTrickBreakBlock.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickBreakInSequence>> TRICK_BREAK_IN_SEQUENCE = SPELL_PIECES.register(
      "trick_break_in_sequence", () -> PieceTrickBreakInSequence.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickPlaceBlock>> TRICK_PLACE_BLOCK = SPELL_PIECES.register(
      "trick_place_block", () -> PieceTrickPlaceBlock.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickPlaceInSequence>> TRICK_PLACE_IN_SEQUENCE = SPELL_PIECES.register(
      "trick_place_in_sequence", () -> PieceTrickPlaceInSequence.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> BLOCK_WORKS = ADVANCEMENT_GROUPS.register(
      "block_works",
      () -> Arrays.asList(
         PieceTrickBreakInSequence.class,
         PieceOperatorVectorRaycastAxis.class,
         PieceOperatorVectorProject.class,
         PieceOperatorBlockLightLevel.class,
         PieceOperatorBlockHardness.class,
         PieceOperatorBlockComparatorStrength.class,
         PieceOperatorBlockSideSolidity.class,
         PieceOperatorBlockMiningLevel.class,
         PieceTrickBreakBlock.class,
         PieceTrickPlaceBlock.class,
         PieceTrickPlaceInSequence.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickMoveBlock>> TRICK_MOVE_BLOCK = SPELL_PIECES.register(
      "trick_move_block", () -> PieceTrickMoveBlock.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickCollapseBlock>> TRICK_COLLAPSE_BLOCK = SPELL_PIECES.register(
      "trick_collapse_block", () -> PieceTrickCollapseBlock.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickMoveBlockSequence>> TRICK_MOVE_BLOCK_SEQUENCE = SPELL_PIECES.register(
      "trick_move_block_sequence", () -> PieceTrickMoveBlockSequence.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickCollapseBlockSequence>> TRICK_COLLAPSE_BLOCK_SEQUENCE = SPELL_PIECES.register(
      "trick_collapse_block_sequence", () -> PieceTrickCollapseBlockSequence.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> BLOCK_MOVEMENT = ADVANCEMENT_GROUPS.register(
      "block_movement",
      () -> Arrays.asList(PieceTrickMoveBlock.class, PieceTrickCollapseBlock.class, PieceTrickMoveBlockSequence.class, PieceTrickCollapseBlockSequence.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickConjureBlock>> TRICK_CONJURE_BLOCK = SPELL_PIECES.register(
      "trick_conjure_block", () -> PieceTrickConjureBlock.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickConjureLight>> TRICK_CONJURE_LIGHT = SPELL_PIECES.register(
      "trick_conjure_light", () -> PieceTrickConjureLight.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickConjureBlockSequence>> TRICK_CONJURE_BLOCK_SEQUENCE = SPELL_PIECES.register(
      "trick_conjure_block_sequence", () -> PieceTrickConjureBlockSequence.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickParticleTrail>> TRICK_PARTICLE_TRAIL = SPELL_PIECES.register(
      "trick_particle_trail", () -> PieceTrickParticleTrail.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> BLOCK_CONJURATION = ADVANCEMENT_GROUPS.register(
      "block_conjuration",
      () -> Arrays.asList(PieceTrickConjureBlock.class, PieceTrickConjureLight.class, PieceTrickConjureBlockSequence.class, PieceTrickParticleTrail.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickBlink>> TRICK_BLINK = SPELL_PIECES.register(
      "trick_blink", () -> PieceTrickBlink.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickMassBlink>> TRICK_MASS_BLINK = SPELL_PIECES.register(
      "trick_mass_blink", () -> PieceTrickMassBlink.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickMassAddMotion>> TRICK_MASS_ADD_MOTION = SPELL_PIECES.register(
      "trick_mass_add_motion", () -> PieceTrickMassAddMotion.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickMassExodus>> TRICK_MASS_EXODUS = SPELL_PIECES.register(
      "trick_mass_exodus", () -> PieceTrickMassExodus.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorIsElytraFlying>> SELECTOR_IS_ELYTRA_FLYING = SPELL_PIECES.register(
      "selector_is_elytra_flying", () -> PieceSelectorIsElytraFlying.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> MOVEMENT = ADVANCEMENT_GROUPS.register(
      "movement",
      () -> Arrays.asList(
         PieceTrickBlink.class, PieceTrickMassBlink.class, PieceTrickMassAddMotion.class, PieceTrickMassExodus.class, PieceSelectorIsElytraFlying.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorRandom>> OPERATOR_RANDOM = SPELL_PIECES.register(
      "operator_random", () -> PieceOperatorRandom.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSmite>> TRICK_SMITE = SPELL_PIECES.register(
      "trick_smite", () -> PieceTrickSmite.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickBlaze>> TRICK_BLAZE = SPELL_PIECES.register(
      "trick_blaze", () -> PieceTrickBlaze.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickTorrent>> TRICK_TORRENT = SPELL_PIECES.register(
      "trick_torrent", () -> PieceTrickTorrent.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickOvergrow>> TRICK_OVERGROW = SPELL_PIECES.register(
      "trick_overgrow", () -> PieceTrickOvergrow.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> ELEMENTAL_ARTS = ADVANCEMENT_GROUPS.register(
      "elemental_arts",
      () -> Arrays.asList(PieceTrickSmite.class, PieceOperatorRandom.class, PieceTrickBlaze.class, PieceTrickTorrent.class, PieceTrickOvergrow.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSpeed>> TRICK_SPEED = SPELL_PIECES.register(
      "trick_speed", () -> PieceTrickSpeed.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickHaste>> TRICK_HASTE = SPELL_PIECES.register(
      "trick_haste", () -> PieceTrickHaste.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickStrength>> TRICK_STRENGTH = SPELL_PIECES.register(
      "trick_strength", () -> PieceTrickStrength.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickJumpBoost>> TRICK_JUMP_BOOST = SPELL_PIECES.register(
      "trick_jump_boost", () -> PieceTrickJumpBoost.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickWaterBreathing>> TRICK_WATER_BREATHING = SPELL_PIECES.register(
      "trick_water_breathing", () -> PieceTrickWaterBreathing.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickFireResistance>> TRICK_FIRE_RESISTANCE = SPELL_PIECES.register(
      "trick_fire_resistance", () -> PieceTrickFireResistance.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickInvisibility>> TRICK_INVISIBILITY = SPELL_PIECES.register(
      "trick_invisibility", () -> PieceTrickInvisibility.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickRegeneration>> TRICK_REGENERATION = SPELL_PIECES.register(
      "trick_regeneration", () -> PieceTrickRegeneration.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickResistance>> TRICK_RESISTANCE = SPELL_PIECES.register(
      "trick_resistance", () -> PieceTrickResistance.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickNightVision>> TRICK_NIGHT_VISION = SPELL_PIECES.register(
      "trick_night_vision", () -> PieceTrickNightVision.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> POSITIVE_EFFECTS = ADVANCEMENT_GROUPS.register(
      "positive_effects",
      () -> Arrays.asList(
         PieceTrickSpeed.class,
         PieceTrickHaste.class,
         PieceTrickStrength.class,
         PieceTrickJumpBoost.class,
         PieceTrickWaterBreathing.class,
         PieceTrickFireResistance.class,
         PieceTrickInvisibility.class,
         PieceTrickRegeneration.class,
         PieceTrickResistance.class,
         PieceTrickNightVision.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickWither>> TRICK_WITHER = SPELL_PIECES.register(
      "trick_wither", () -> PieceTrickWither.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSlowness>> TRICK_SLOWNESS = SPELL_PIECES.register(
      "trick_slowness", () -> PieceTrickSlowness.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickWeakness>> TRICK_WEAKNESS = SPELL_PIECES.register(
      "trick_weakness", () -> PieceTrickWeakness.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickIgnite>> TRICK_IGNITE = SPELL_PIECES.register(
      "trick_ignite", () -> PieceTrickIgnite.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> NEGATIVE_EFFECTS = ADVANCEMENT_GROUPS.register(
      "negative_effects", () -> Arrays.asList(PieceTrickWither.class, PieceTrickSlowness.class, PieceTrickWeakness.class, PieceTrickIgnite.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorEidosChangelog>> SELECTOR_EIDOS_CHANGELOG = SPELL_PIECES.register(
      "selector_eidos_changelog", () -> PieceSelectorEidosChangelog.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickEidosAnchor>> TRICK_EIDOS_ANCHOR = SPELL_PIECES.register(
      "trick_eidos_anchor", () -> PieceTrickEidosAnchor.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickEidosReversal>> TRICK_EIDOS_REVERSAL = SPELL_PIECES.register(
      "trick_eidos_reversal", () -> PieceTrickEidosReversal.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> EIDOS_REVERSAL = ADVANCEMENT_GROUPS.register(
      "eidos_reversal", () -> Arrays.asList(PieceTrickEidosReversal.class, PieceSelectorEidosChangelog.class, PieceTrickEidosAnchor.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorTime>> SELECTOR_TIME = SPELL_PIECES.register(
      "selector_time", () -> PieceSelectorTime.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorAttacker>> SELECTOR_ATTACKER = SPELL_PIECES.register(
      "selector_attacker", () -> PieceSelectorAttacker.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorDamageTaken>> SELECTOR_DAMAGE_TAKEN = SPELL_PIECES.register(
      "selector_damage_taken", () -> PieceSelectorDamageTaken.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorSuccessCounter>> SELECTOR_SUCCESS_COUNTER = SPELL_PIECES.register(
      "selector_sucession_counter", () -> PieceSelectorSuccessCounter.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorCasterBattery>> SELECTOR_CASTER_BATTERY = SPELL_PIECES.register(
      "selector_caster_battery", () -> PieceSelectorCasterBattery.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorCasterEnergy>> SELECTOR_CASTER_ENERGY = SPELL_PIECES.register(
      "selector_caster_energy", () -> PieceSelectorCasterEnergy.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> EXOSUIT_CASTING = ADVANCEMENT_GROUPS.register(
      "exosuit_casting",
      () -> Arrays.asList(
         PieceSelectorTime.class,
         PieceSelectorAttacker.class,
         PieceSelectorDamageTaken.class,
         PieceSelectorSuccessCounter.class,
         PieceSelectorCasterBattery.class,
         PieceSelectorCasterEnergy.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorItemPresence>> SELECTOR_ITEM_PRESENCE = SPELL_PIECES.register(
      "selector_item_presence", () -> PieceSelectorItemPresence.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorBlockPresence>> SELECTOR_BLOCK_PRESENCE = SPELL_PIECES.register(
      "selector_block_presence", () -> PieceSelectorBlockPresence.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSwitchTargetSlot>> TRICK_SWITCH_TARGET_SLOT = SPELL_PIECES.register(
      "trick_switch_target_slot", () -> PieceTrickSwitchTargetSlot.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickChangeSlot>> TRICK_CHANGE_SLOT = SPELL_PIECES.register(
      "trick_change_slot", () -> PieceTrickChangeSlot.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> DETECTION_DYNAMICS = ADVANCEMENT_GROUPS.register(
      "detection_dynamics",
      () -> Arrays.asList(PieceTrickSwitchTargetSlot.class, PieceSelectorItemPresence.class, PieceSelectorBlockPresence.class, PieceTrickChangeSlot.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceSelectorNearbySmeltables>> SELECTOR_NEARBY_SMELTABLES = SPELL_PIECES.register(
      "selector_nearby_smeltables", () -> PieceSelectorNearbySmeltables.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSmeltBlock>> TRICK_SMELT_BLOCK = SPELL_PIECES.register(
      "trick_smelt_block", () -> PieceTrickSmeltBlock.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSmeltItem>> TRICK_SMELT_ITEM = SPELL_PIECES.register(
      "trick_smelt_item", () -> PieceTrickSmeltItem.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSmeltBlockSequence>> TRICK_SMELT_BLOCK_SEQUENCE = SPELL_PIECES.register(
      "trick_smelt_block_sequence", () -> PieceTrickSmeltBlockSequence.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> SMELTERY = ADVANCEMENT_GROUPS.register(
      "smeltery",
      () -> Arrays.asList(PieceTrickSmeltItem.class, PieceSelectorNearbySmeltables.class, PieceTrickSmeltBlock.class, PieceTrickSmeltBlockSequence.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickInfusion>> TRICK_INFUSION = SPELL_PIECES.register(
      "trick_infusion", () -> PieceTrickInfusion.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickGreaterInfusion>> TRICK_GREATER_INFUSION = SPELL_PIECES.register(
      "trick_greater_infusion", () -> PieceTrickGreaterInfusion.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickEbonyIvory>> TRICK_EBONY_IVORY = SPELL_PIECES.register(
      "trick_ebony_ivory", () -> PieceTrickEbonyIvory.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> INFUSION = ADVANCEMENT_GROUPS.register(
      "infusion", () -> List.of(PieceTrickInfusion.class)
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> GREATER_INFUSION = ADVANCEMENT_GROUPS.register(
      "greater_infusion", () -> Arrays.asList(PieceTrickGreaterInfusion.class, PieceTrickEbonyIvory.class)
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorListExclusion>> OPERATOR_LIST_EXCLUSION = SPELL_PIECES.register(
      "operator_list_exclusion", () -> PieceOperatorListExclusion.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorListIntersection>> OPERATOR_LIST_INTERSECTION = SPELL_PIECES.register(
      "operator_list_intersection", () -> PieceOperatorListIntersection.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorListSize>> OPERATOR_LIST_SIZE = SPELL_PIECES.register(
      "operator_list_size", () -> PieceOperatorListSize.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorListUnion>> OPERATOR_LIST_UNION = SPELL_PIECES.register(
      "operator_list_union", () -> PieceOperatorListUnion.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceOperatorListIndex>> OPERATOR_LIST_INDEX = SPELL_PIECES.register(
      "operator_list_index", () -> PieceOperatorListIndex.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> LIST_OPERATIONS = ADVANCEMENT_GROUPS.register(
      "list_operations",
      () -> Arrays.asList(
         PieceOperatorListExclusion.class,
         PieceOperatorListIntersection.class,
         PieceOperatorListSize.class,
         PieceOperatorListUnion.class,
         PieceOperatorListIndex.class
      )
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickPlaySound>> TRICK_PLAY_SOUND = SPELL_PIECES.register(
      "trick_play_sound", () -> PieceTrickPlaySound.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickTill>> TRICK_TILL = SPELL_PIECES.register(
      "trick_till", () -> PieceTrickTill.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickTillSequence>> TRICK_TILL_SEQUENCE = SPELL_PIECES.register(
      "trick_till_sequence", () -> PieceTrickTillSequence.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickSpinChamber>> TRICK_SPIN_CHAMBER = SPELL_PIECES.register(
      "trick_spin_chamber", () -> PieceTrickSpinChamber.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickRussianRoulette>> TRICK_RUSSIAN_ROULETTE = SPELL_PIECES.register(
      "trick_russian_roulette", () -> PieceTrickRussianRoulette.class
   );
   public static final DeferredHolder<Class<? extends SpellPiece>, Class<PieceTrickConjureCircle>> TRICK_CONJURE_CIRCLE = SPELL_PIECES.register(
      "trick_conjure_circle", () -> PieceTrickConjureCircle.class
   );
   public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> MISC_TRICKS = ADVANCEMENT_GROUPS.register(
      "misc_tricks",
      () -> Arrays.asList(
         PieceTrickPlaySound.class,
         PieceTrickTill.class,
         PieceTrickTillSequence.class,
         PieceTrickSpinChamber.class,
         PieceTrickRussianRoulette.class,
         PieceTrickConjureCircle.class
      )
   );
}
