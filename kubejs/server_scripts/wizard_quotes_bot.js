// KubeJS Server Script - Council Wizard Quotes Bot (Every ~15 Minutes)

const WIZARD_QUOTES = [
    { author: "Jim Darkmagic", text: "I appear from behind a smoke bomb that I clearly threw myself, coughing slightly." },
    { author: "Jim Darkmagic", text: "I have a dove that comes out of my sleeve... and honestly, I don't know where it came from." },
    { author: "Jim Darkmagic", text: "I pull out a handful of glitter, blow it into the air, and take a dramatic bow." },
    { author: "Jim Darkmagic", text: "I don't just cast a spell, Chris—I put on a show." },
    { author: "Jim Darkmagic", text: "I fire a blast of pure arcane force, then check to see if anyone in the front row was watching." },
    { author: "Jim Darkmagic", text: "I'd help you carry that, Omin, but I have delicate wizard hands and an intense aversion to manual labor." },
    { author: "Jim Darkmagic", text: "I take three steps back, adjust my cloak, and let Binwin take the blunt force trauma." },
    { author: "Jim Darkmagic", text: "Is it dead? Great. Then as the person who contributed emotional support and visual aesthetics, I claim first pick of the loot." },
    { author: "Taako", text: "You're going to have to be a little more specific, my dude. I have a lot of spells that blow things up." },
    { author: "Adaine Abernanth", text: "I take out the 100gp pearl and I cast Identify on the mood in this room." },
    { author: "Arthur Aguefort", text: "What is an elf going to do? Live forever at you?" },
    { author: "Edwin Odesseiron", text: "Elminster this, Elminster that. Give ME two thousand years and a pointy hat and I'll kick his arse!" },
    { author: "Edwin Odesseiron", text: "Please don't disturb me while I plot to overthrow you." },
    { author: "Goldfinch the Indomitable", text: "Dark magic is bad for the complexion." },
    { author: "Goldfinch the Indomitable", text: "Don't take shit from other wizards." },
    { author: "Goldfinch the Indomitable", text: "The council are a bunch of stuck up posers. I'M ONLY TECHNICALLY ON IT, IT DOES NOT COUNT." },
    { author: "Goldfinch the Indomitable", text: "Fuck the council." },
    { author: "Goldfinch the Indomitable", text: "The bigger the tower, the bigger the wand." },
    { author: "Goldfinch the Indomitable", text: "Have you conquered nine realms? No? I don't want to hear your shit then." },
    { author: "Goldfinch the Indomitable", text: "Enough about the martial caster divide. If we didn't have martials, we wouldn't have anyone to feel superior towards." },
    { author: "Goldfinch the Indomitable", text: "Waah waah my god only gave me healing magic… GET A BETTER GOD DUMBASS." },
    { author: "Goldfinch the Indomitable", text: "I don't trust Druids. Can't go ten feet without one shifting and spooking the shit out of you. They do that shit on purpose." }
];

let lastQuoteIndex = -1;

ServerEvents.loaded(event => {
    let server = event.server;

    // Run every 18000 ticks (~15 minutes)
    server.scheduleRepeatingInTicks(18000, callback => {
        // Only broadcast if there are online players
        if (!server.players || server.players.isEmpty()) return;

        let idx = Math.floor(Math.random() * WIZARD_QUOTES.length);
        if (idx === lastQuoteIndex && WIZARD_QUOTES.length > 1) {
            idx = (idx + 1) % WIZARD_QUOTES.length;
        }
        lastQuoteIndex = idx;

        let quote = WIZARD_QUOTES[idx];
        let formatted = Component.literal('§6§l[The Council] §d§o"' + quote.text + '" §7— §e§l' + quote.author);
        server.tell(formatted);
    });
});
