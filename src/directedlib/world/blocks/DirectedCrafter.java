package mymod.world.blocks;

import arc.struct.ObjectIntMap;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;

public class DirectedCrafter extends GenericCrafter {
    
      //0 = Front, 1 = Left, 2 = Back, 3 = Right blehh
    
    public ObjectIntMap<Item> itemOutputDirections = new ObjectIntMap<>();

    public DirectedCrafter(String name) {
        super(name);
        rotate = true;
        quickRotate = true;
    }

    public class DirectedCrafterBuild extends GenericCrafterBuild {

        @Override
        public boolean dump(Item todump) {
            if (todump == null || !items.has(todump) || proximity.size == 0) {
                return false;
            }

            if (itemOutputDirections.containsKey(todump)) {
                int relDir = itemOutputDirections.get(todump, -1);
                if (relDir >= 0) {
                    int targetDir = (rotation + relDir) % 4;
                    int dumpOffset = dump;

                    for (int i = 0; i < proximity.size; i++) {
                        Building other = proximity.get((i + dumpOffset) % proximity.size);
                        Tile selfEdge = Edges.getFacingEdge(this, other);
                        Tile otherEdge = Edges.getFacingEdge(other, this);

                        // Match adjacent edge direction with target cardinal direction
                        if (selfEdge != null && otherEdge != null && selfEdge.relativeTo(otherEdge) == targetDir) {
                            if (other.team == team && other.acceptItem(this, todump)) {
                                other.handleItem(this, todump);
                                items.remove(todump, 1);
                                incrementDump(proximity.size);
                                return true;
                            }
                        }
                        incrementDump(proximity.size);
                    }
                    return false;
                }
            }

            return super.dump(todump);
        }
    }
}