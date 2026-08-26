// Priority: 100
// Hex Casting 1.21.1 Wall Scroll Preservation & Recovery Patch

EntityEvents.drops(event => {
    // Intercept when a hexcasting:wall_scroll entity is destroyed
    let typeStr = '' + event.entity.type
    if (typeStr === 'hexcasting:wall_scroll' || typeStr.endsWith(':wall_scroll')) {
        let nbt = event.entity.nbt
        if (nbt && nbt.contains('scroll')) {
            let scrollTag = nbt.getCompound('scroll')
            let item = Item.of(scrollTag)
            if (item && !item.isEmpty()) {
                // Ensure the dropped item is not lost even if vanilla hanging drop discarded it
                event.addDrop(item)
            }
        }
    }
})
