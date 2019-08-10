public class Forest extends Place {
    private River river = new River();
    private Cave cave = new Cave();
    private Minerals rocks = new Minerals("Rocks");
    private Minerals woods = new Minerals("Woods");

    Forest() {
        super("Forest");
        woods.setMineralTypes(Holder.getBranch(), Holder.getOldLumber(), Holder.getPineLumber(), Holder.getOakLumber());
        rocks.setMineralTypes(Holder.getStone());
        setInspectableObjects(woods, rocks, river);
    }

    @Override
    void setAccessiblePlaces(Place... places) {
        accessiblePlaces.add(cave);
        cave.setAccessiblePlaces(this);
        super.setAccessiblePlaces(places);
    }
}

class Cave extends Place {
    private Minerals rocks = new Minerals("Rocks");
    Cave() {
        super("Cave");
        rocks.setMineralTypes(Holder.getStone(), Holder.getIronOre(), Holder.getSilverOre(), Holder.getAdamantiumOre());
        setInspectableObjects(rocks);
    }

}
