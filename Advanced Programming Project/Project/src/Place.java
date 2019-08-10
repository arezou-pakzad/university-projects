import java.util.*;

public abstract class Place implements Expressible {
    public static Farm farm;
    protected ArrayList<Place> accessiblePlaces = new ArrayList<>();
    protected ArrayList<Inspectable> inspectableObjects = new ArrayList<>(); // TODO: set inspectable objects for each place
    private String name;
    public PlaceController placeController = new PlaceController(this);
    Place(String name) {
        this.name = name;
    }

    static void init() { //place instances initialization
        Farm farm = new Farm();
        Farm.farm = farm;
        Village village = new Village();
        Forest forest = new Forest();
        farm.setAccessiblePlaces(village, forest);
        village.setAccessiblePlaces(farm, forest);
        forest.setAccessiblePlaces(farm, village);
        Person.getPerson().setPlace(farm);
        Controller.init(farm.placeController);
    }

    void setAccessiblePlaces(Place ... places) {
        for (int i = 0; i < places.length; i++)
            accessiblePlaces.add(places[i]);
        //        for (int i = 0; i < places.length; i++) {
        //            places[i].accessiblePlaces.add(this);
        //        }
    }

    void setInspectableObjects(Inspectable ... inspectables) {
        for (int i = 0; i < inspectables.length; i++)
            inspectableObjects.add(inspectables[i]);
    }
    public String getName() {
        return name;
    }

    public ArrayList<String> getAccessiblePlaces() {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < accessiblePlaces.size(); i++)
            names.add(accessiblePlaces.get(i).name);
        return names;
    }

    public ArrayList<String> getInspectableObjects() {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < inspectableObjects.size(); i++)
            names.add(inspectableObjects.get(i).getName());
        return names;
    }

    public PlaceController goTo(String desPlace) {
        for (int i = 0; i < accessiblePlaces.size(); i++) {
            if (accessiblePlaces.get(i).getName().equals(desPlace))
                return accessiblePlaces.get(i).placeController;
        }
        return null;
    }
    public InspectableController inspect(String inspectableName) {
        for (int i = 0; i < inspectableObjects.size(); i++)
            if (inspectableObjects.get(i).getName().equals(inspectableName))
                return inspectableObjects.get(i).inspectableController;
        return null;
    }

    public PlaceController getAccessiblePlaceController(String expressibleName) {
        for (Place place:
                accessiblePlaces) {
            if (place.getName().equals(expressibleName))
                return place.placeController;
        }
        return null;
    }

    public InspectableController getInspectableObjectController(String expressibleName) {
        for (Inspectable inspectable :
                inspectableObjects) {
            if (inspectable.getName().equals(expressibleName))
                return inspectable.inspectableController;
        }
        return null;
    }

}

class PlaceController extends Controller {
    protected Place place;
    protected PlaceView placeView = new PlaceView(this);

    PlaceController(Place place) {
        super(place);
        this.place = place;
        setView(placeView);
//        placeView.buildScene();
    }

    public Place getPlace() {
        return place;
    }

    @Override
    void showMenu() {
        placeView.whereAmI(place.getName(), place.getAccessiblePlaces(), place.getInspectableObjects());
    }

    @Override
    void help() {
        super.help();
        ArrayList<String> accessiblePlaces = place.getAccessiblePlaces();
        ArrayList<String> inspectableObjects = place.getInspectableObjects();
        for (int i = 0; i < accessiblePlaces.size(); i++)
            System.out.println(accessiblePlaces.get(i));
        for (int i = 0; i < inspectableObjects.size(); i++)
            System.out.println(inspectableObjects.get(i));
    }

    @Override
    int scan() {
        int scanReport = super.scan();
        if (scanReport == 1 || scanReport == -1)
            return scanReport;
//        if (command.matches("GoTo \\s+")) {
        if (command.startsWith("GoTo")) {
            String[] desPlace = command.split(" ");
            ArrayList<String> accessiblePlaces = place.getAccessiblePlaces();
            if (!accessiblePlaces.contains(desPlace[1]))
                return -1;
            PlaceController desPlaceController = place.goTo(desPlace[1]);
            if (menus.contains(desPlaceController))
                back();
            else {
                Place nextPlace = desPlaceController.place;
                if (nextPlace instanceof Farm || nextPlace instanceof Village || nextPlace instanceof Forest) {
                    back();
                    addMenu(desPlaceController);
                } else
                    addMenu(desPlaceController);
            }
            return 1;
        }
//        if (command.matches("Inspect \\s+")) {
        if (command.startsWith("Inspect")) {
            String object = command.substring(command.indexOf(" ") + 1);
            ArrayList<String> inspectableObjects = place.getInspectableObjects();
            if (!inspectableObjects.contains(object))
                return -1;
            InspectableController inspectableController = place.inspect(object);
            addMenu(inspectableController);
            return 1;
        }
        return -1;
    }

    public void goTo(String expressibleName) {
        if (place.getAccessiblePlaces().contains(expressibleName))
            Controller.addMenu(place.getAccessiblePlaceController(expressibleName));
        else if (place.getInspectableObjects().contains(expressibleName))
            Controller.addMenu(place.getInspectableObjectController(expressibleName));
        else
            System.out.println("NAME NOT VALID");
        //throw exception
    }

    @Override
    public void refreshScene() {
        Farm.farm.placeController.placeView.buildScene();
    }
}
