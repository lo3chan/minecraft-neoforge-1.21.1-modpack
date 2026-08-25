// KubeJS Player Attribute Safeguard
// Ensures player generic.step_height is never corrupted to 0 or negative values by mods like The Undead Revamped.

PlayerEvents.loggedIn(event => {
    let player = event.player;
    let stepHeightAttr = player.getAttribute('minecraft:generic.step_height');
    if (stepHeightAttr) {
        let baseVal = stepHeightAttr.getBaseValue();
        if (baseVal < 0.6) {
            stepHeightAttr.setBaseValue(0.6);
            console.log(`[KubeJS-Safety] Restored step_height from ${baseVal} to 0.6 for player ${player.getUsername()}`);
        }
    }
});

PlayerEvents.respawned(event => {
    let player = event.player;
    let stepHeightAttr = player.getAttribute('minecraft:generic.step_height');
    if (stepHeightAttr) {
        let baseVal = stepHeightAttr.getBaseValue();
        if (baseVal < 0.6) {
            stepHeightAttr.setBaseValue(0.6);
            console.log(`[KubeJS-Safety] Restored step_height on respawn from ${baseVal} to 0.6 for player ${player.getUsername()}`);
        }
    }
});
