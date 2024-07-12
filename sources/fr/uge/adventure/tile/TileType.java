package fr.uge.adventure.tile;

public enum TileType {	
    // decoration
    ALGAE(false), CLOUD(false), FLOWER(false), FOLIAGE(false), GRASS(false), LADDER(false), LILY(false),
    PLANK(false), REED(false), ROAD(false), SPROUT(false), TILE(false), TRACK(false), VINE(false), WATER(false),
    // obstacle
    BED(true), BOG(true), BOMB(true), BRICK(true), CHAIR(false), CLIFF(true), DOOR(true), FENCE(true),
    FORT(true), GATE(true), HEDGE(true), HOUSE(true), HUSK(true), HUSKS(true), LOCK(true), MONITOR(false),
    PIANO(true), PILLAR(true), PIPE(true), ROCK(true), RUBBLE(true), SHELL(true), SIGN(false), SPIKE(true),
    STATUE(true), STUMP(true), TABLE(true), TOWER(true), TREE(true), TREES(true), WALL(true);

    private final boolean isCollidable;

    TileType(boolean isCollidable) {
        this.isCollidable = isCollidable;
    }

    public boolean isCollidable() {
        return isCollidable;
    }
}
