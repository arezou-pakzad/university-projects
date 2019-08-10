class Pond extends Inspectable {
    public Pond(){
        inspectableController = new PondController(this);
        setName("Pond");
        setCommands("Fill a Watering Can");
    }
    public void fillWateringCan(WateringCan wateringCan) {
        wateringCan.fill();
    }
}

class PondController extends InspectableController{
    private Pond pond;
    public PondController(Pond pond){
        super(pond);
        this.pond = pond;
        setView(new InspectableView(this));
    }

    @Override
    public void runMethods(int commandNum) {
        if (commandNum == 1) {
            WateringCan wateringCan = (WateringCan)BackPack.getBackPack().backPackController.getItemFromBackPack("WateringCan", 1);
            if (wateringCan == null)
                return;
            pond.fillWateringCan(wateringCan);
        }
    }
}
