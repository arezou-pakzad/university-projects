public class InspectablePlace extends Place {
    Inspectable inspectable;
    InspectablePlaceController inspectablePlacecontroller = new InspectablePlaceController(this);

    InspectablePlace(String name, Inspectable inspectable) {
        super(name);
        this.inspectable = inspectable;
        placeController = inspectablePlacecontroller;
        inspectablePlacecontroller.setInspectableController(inspectable.inspectableController);
    }

    public Inspectable getInspectable() {
        return inspectable;
    }

}

class InspectablePlaceController extends PlaceController {
    InspectablePlace inspectablePlace;
    InspectableController inspectableController;
    InspectablePlaceController(InspectablePlace inspectablePlace) {
        super(inspectablePlace);
        this.inspectablePlace = inspectablePlace;
    }

    public void setInspectableController(InspectableController inspectableController) {
        this.inspectableController = inspectableController;
    }

    @Override
    public void showMenu() {
        addMenu(inspectablePlace.getInspectable().inspectableController);
    }
}
