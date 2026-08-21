package directedlib;

import mindustry.mod.ClassMap;
import mindustry.mod.Mod;
import directedlib.world.blocks.DirectedCrafter;
import directedlib.world.blocks.DirectedTileCrafter;
import directedlib.world.draw.DrawDirectedOutputs;

public class DirectedLibMod extends Mod {

    public DirectedLibMod() {
        ClassMap.classes.put("DirectedCrafter", DirectedCrafter.class);
        ClassMap.classes.put("DirectedTileCrafter", DirectedTileCrafter.class);
        ClassMap.classes.put("DrawDirectedOutputs", DrawDirectedOutputs.class);
    }
}