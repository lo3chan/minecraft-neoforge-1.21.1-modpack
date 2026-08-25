// Auto-announce exact death coordinates in chat to player upon dying
EntityEvents.death(event => {
    let entity = event.entity;
    if (entity.isPlayer()) {
        let x = Math.round(entity.x);
        let y = Math.round(entity.y);
        let z = Math.round(entity.z);
        let dim = entity.level.dimension.location().toString();
        let source = event.source.getLocalizedDeathMessage(entity).getString();

        // Send private message to player
        entity.tell(Component.literal(`§c§l[Death] §eYou died at §bX: ${x}, Y: ${y}, Z: ${z} §7(${dim})`));
        console.log(`[DeathCoords] ${entity.getUsername()} died at ${x}, ${y}, ${z} in ${dim}: ${source}`);
    }
});
