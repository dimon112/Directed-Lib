package directedlib.world.blocks;

import arc.Core;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;

public class DirectedTileCrafter extends DirectedCrafter {
    public Attribute attribute = Attribute.heat;
    public float baseEfficiency = 1f;
    public float boostScale = 1f;
    public float maxBoost = 1f;
    public float minEfficiency = 0f;
    public float displayEfficiencyScale = 1f;
    public boolean displayEfficiency = true;

    public DirectedTileCrafter(String name) {
        super(name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        if (displayEfficiency) {
            float eff = baseEfficiency + Math.min(maxBoost, boostScale * sumAttribute(attribute, x, y));
            drawPlaceText(Core.bundle.format("bar.efficiency", (int) (eff * 100f * displayEfficiencyScale)), x, y, valid);
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return super.canPlaceOn(tile, team, rotation) &&
            (minEfficiency <= 0 || baseEfficiency + boostScale * sumAttribute(attribute, tile.x, tile.y) >= minEfficiency);
    }

    @Override
    public void setBars() {
        super.setBars();
        if (displayEfficiency) {
            addBar("efficiency", (DirectedTileCrafterBuild entity) ->
                new Bar(
                    () -> Core.bundle.format("bar.efficiency", (int) (entity.efficiencyScale() * 100f * displayEfficiencyScale)),
                    () -> Pal.accent,
                    () -> entity.efficiencyScale()
                )
            );
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.tiles, attribute, baseEfficiency <= 0f, boostScale, false);
    }

    public class DirectedTileCrafterBuild extends DirectedCrafterBuild {
        public float attrEfficiency = 1f;

        @Override
        public float efficiencyScale() {
            return attrEfficiency;
        }

        @Override
        public void pickedUp() {
            attrEfficiency = 0f;
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            attrEfficiency = baseEfficiency + Math.min(maxBoost, boostScale * sumAttribute(attribute, tile.x, tile.y));
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(attrEfficiency);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            attrEfficiency = read.f();
        }
    }
}