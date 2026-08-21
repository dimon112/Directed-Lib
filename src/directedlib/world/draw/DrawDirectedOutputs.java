package directedlib.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Eachable;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import directedlib.world.blocks.DirectedCrafter;

public class DrawDirectedOutputs extends DrawBlock {
    public static class OutputEntry {
        public Item item;
        public int relDir;
        public @Nullable TextureRegion region;

        public OutputEntry(Item item, int relDir, @Nullable TextureRegion region) {
            this.item = item;
            this.relDir = relDir;
            this.region = region;
        }
    }

    public String arrowRegionName = "pointer";
    public String sideRegionName = "";
    public String suffix = "-output";

    public @Nullable TextureRegion arrowRegion;
    public @Nullable TextureRegion sideRegion;

    public boolean drawSideRegion = true;
    public boolean tintWithItemColor = false;
    public boolean drawArrow = true;
    public boolean drawItem = true;
    public boolean drawWorld = false;

    public float iconSize = 6f;
    public float arrowSize = 6f;
    public float iconMargin = 3f;
    public float sideRegionInset = 0f;

    protected final Seq<OutputEntry> entries = new Seq<>(OutputEntry.class);

    @Override
    public void load(Block block) {
        arrowRegion = Core.atlas.find(arrowRegionName, Core.atlas.find("pointer", Core.atlas.find("launch-arrow")));

        if (!sideRegionName.isEmpty()) {
            sideRegion = Core.atlas.find(sideRegionName);
        } else {
            sideRegion = Core.atlas.find(block.name + suffix);
        }

        entries.clear();
        if (block instanceof DirectedCrafter crafter) {
            for (ObjectIntMap.Entry<Item> entry : crafter.itemOutputDirections.entries()) {
                if (entry.key != null && entry.value >= 0) {
                    TextureRegion specificRegion = Core.atlas.find(block.name + suffix + "-" + entry.key.name);
                    TextureRegion reg = specificRegion.found() ? specificRegion : (sideRegion != null && sideRegion.found() ? sideRegion : null);
                    entries.add(new OutputEntry(entry.key, entry.value, reg));
                }
            }
        }
    }

    public void drawSelected(Building build) {
        if (!(build.block instanceof DirectedCrafter crafter) || entries.size == 0) return;

        float offset = (crafter.size * Vars.tilesize) / 2f + iconMargin;

        for (int i = 0; i < entries.size; i++) {
            OutputEntry entry = entries.get(i);
            int rot = (build.rotation + entry.relDir) % 4;
            float drawX = build.x + Geometry.d4x(rot) * offset;
            float drawY = build.y + Geometry.d4y(rot) * offset;

            if (drawArrow && arrowRegion != null && arrowRegion.found()) {
                Draw.rect(arrowRegion, drawX, drawY, arrowSize, arrowSize, rot * 90f);
            }
            if (drawItem && entry.item.uiIcon != null) {
                Draw.rect(entry.item.uiIcon, drawX, drawY, iconSize, iconSize);
            }
        }
    }

    @Override
    public void draw(Building build) {
        if (!(build.block instanceof DirectedCrafter crafter) || entries.size == 0) return;

        if (drawSideRegion) {
            float offset = (crafter.size * Vars.tilesize) / 2f - sideRegionInset;
            for (int i = 0; i < entries.size; i++) {
                OutputEntry entry = entries.get(i);
                if (entry.region != null && entry.region.found()) {
                    int rot = (build.rotation + entry.relDir) % 4;
                    float drawX = build.x + Geometry.d4x(rot) * offset;
                    float drawY = build.y + Geometry.d4y(rot) * offset;

                    if (tintWithItemColor && entry.item != null) {
                        Draw.color(entry.item.color);
                    }
                    Draw.rect(entry.region, drawX, drawY, rot * 90f);
                    if (tintWithItemColor && entry.item != null) {
                        Draw.color();
                    }
                }
            }
        }

        if (drawWorld) {
            drawSelected(build);
        }
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (!(block instanceof DirectedCrafter crafter) || entries.size == 0) return;

        if (drawSideRegion) {
            float offset = (crafter.size * Vars.tilesize) / 2f - sideRegionInset;
            for (int i = 0; i < entries.size; i++) {
                OutputEntry entry = entries.get(i);
                if (entry.region != null && entry.region.found()) {
                    int rot = (plan.rotation + entry.relDir) % 4;
                    float drawX = plan.drawx() + Geometry.d4x(rot) * offset;
                    float drawY = plan.drawy() + Geometry.d4y(rot) * offset;

                    if (tintWithItemColor && entry.item != null) {
                        Draw.color(entry.item.color);
                    }
                    Draw.rect(entry.region, drawX, drawY, rot * 90f);
                    if (tintWithItemColor && entry.item != null) {
                        Draw.color();
                    }
                }
            }
        }

        float iconOffset = (crafter.size * Vars.tilesize) / 2f + iconMargin;

        for (int i = 0; i < entries.size; i++) {
            OutputEntry entry = entries.get(i);
            int rot = (plan.rotation + entry.relDir) % 4;
            float drawX = plan.drawx() + Geometry.d4x(rot) * iconOffset;
            float drawY = plan.drawy() + Geometry.d4y(rot) * iconOffset;

            if (drawArrow && arrowRegion != null && arrowRegion.found()) {
                Draw.rect(arrowRegion, drawX, drawY, arrowSize, arrowSize, rot * 90f);
            }
            if (drawItem && entry.item.uiIcon != null) {
                Draw.rect(entry.item.uiIcon, drawX, drawY, iconSize, iconSize);
            }
        }
    }
}