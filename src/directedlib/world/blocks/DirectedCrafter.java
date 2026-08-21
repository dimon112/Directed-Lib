package directedlib.world.blocks;

import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawMulti;
import directedlib.world.draw.DrawDirectedOutputs;

public class DirectedCrafter extends GenericCrafter {
    public ObjectIntMap<Item> itemOutputDirections = new ObjectIntMap<>();
    public @Nullable byte[] itemDirById;

    public DirectedCrafter(String name) {
        super(name);
        rotate = true;
        quickRotate = true;
    }

    @Override
    public void init() {
        super.init();

        int maxId = Vars.content.items().size;
        itemDirById = new byte[maxId];
        for (int i = 0; i < maxId; i++) {
            itemDirById[i] = -1;
        }

        for (ObjectIntMap.Entry<Item> entry : itemOutputDirections.entries()) {
            if (entry.key != null) {
                itemDirById[entry.key.id] = (byte) entry.value;
            }
        }
    }

    public class DirectedCrafterBuild extends GenericCrafterBuild {
        public final Seq<Building>[] sideProximity = new Seq[4];
        public final byte[] sideCdump = new byte[4];

        public DirectedCrafterBuild() {
            for (int i = 0; i < 4; i++) {
                sideProximity[i] = new Seq<>(Building.class);
            }
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            for (int i = 0; i < 4; i++) {
                sideProximity[i].clear();
            }

            for (int i = 0; i < proximity.size; i++) {
                Building other = proximity.get(i);
                Tile selfEdge = Edges.getFacingEdge(this, other);
                Tile otherEdge = Edges.getFacingEdge(other, this);

                if (selfEdge != null && otherEdge != null) {
                    int dir = selfEdge.relativeTo(otherEdge);
                    if (dir >= 0 && dir < 4) {
                        sideProximity[dir].add(other);
                    }
                }
            }
        }

        @Override
        public boolean dump(Item todump) {
            if (todump == null || !items.has(todump)) return false;

            int relDir = itemDirById != null && todump.id < itemDirById.length ? itemDirById[todump.id] : -1;

            if (relDir >= 0) {
                int targetDir = (rotation + relDir) % 4;
                Seq<Building> targets = sideProximity[targetDir];
                int size = targets.size;
                if (size == 0) return false;

                int offset = sideCdump[targetDir];
                for (int i = 0; i < size; i++) {
                    Building other = targets.get((i + offset) % size);
                    if (other.team == team && other.acceptItem(this, todump)) {
                        other.handleItem(this, todump);
                        items.remove(todump, 1);
                        sideCdump[targetDir] = (byte) ((offset + i + 1) % size);
                        return true;
                    }
                }
                return false;
            }

            return super.dump(todump);
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            if (drawer instanceof DrawMulti multi) {
                for (DrawBlock d : multi.drawers) {
                    if (d instanceof DrawDirectedOutputs directed) {
                        directed.drawSelected(this);
                    }
                }
            } else if (drawer instanceof DrawDirectedOutputs directed) {
                directed.drawSelected(this);
            }
        }
    }
}