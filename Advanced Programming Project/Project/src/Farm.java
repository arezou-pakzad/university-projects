import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Farm extends Place {
    public Fields getFields() {
        return fields;
    }

    private Fields fields = new Fields();
    private FruitGarden fruitGarden = new FruitGarden();
    private Pond pond = new Pond();
    private Greenhouse greenhouse = new Greenhouse();
    private Barn barn = new Barn();
    private Home home = new Home();
    Farm() {
        super("Farm");
        setInspectableObjects(fields, pond, fruitGarden);
        setAccessiblePlaces();
    }

    @Override
    void setAccessiblePlaces(Place... places) {
        accessiblePlaces.add(barn);
        accessiblePlaces.add(home);
        accessiblePlaces.add(greenhouse);
        for (int i = 0; i < accessiblePlaces.size(); i++)
            accessiblePlaces.get(i).setAccessiblePlaces(this);
        super.setAccessiblePlaces(places);
    }

    void nextDay() {
        fields.nextDay();
        barn.nextDay();
    }
}

class Barn extends Place {
    private AnimalPlace Cows = new AnimalPlace("Cows", 5);
    private AnimalPlace Sheep = new AnimalPlace("Sheep", 5);
    private AnimalPlace Chickens = new AnimalPlace("Chickens", 10);
    private MachinePlace machinePlace = MachinePlace.getMachinePlace();
    private static Barn barn;
    Barn() {
        super("Barn");
        setInspectableObjects(Cows, Sheep, Chickens, machinePlace);
        barn = this;
        // machinePlace.addMachine(Holder.getTomatoPaste());
    }

    public static Barn getBarn(){
        return barn;
    }

    public ArrayList<AnimalPlace> getAnimalPlaces(){
        ArrayList<AnimalPlace> animalPlaces = new ArrayList<>();
        animalPlaces.add(Cows);
        animalPlaces.add(Sheep);
        animalPlaces.add(Chickens);
        return animalPlaces;
    }

    public MachinePlace getMachinePlace(){
        return machinePlace;
    }

    public void nextDay(){
        Cows.nextDay();
        Sheep.nextDay();
        Chickens.nextDay();
    }
}

class Home extends Place {
    private Kitchen kitchen = new Kitchen();
    private StorageBox storageBox = new StorageBox((int)Integer.MAX_VALUE);
    private Bed bed = new Bed();
    Home() {
        super("Home");
        setInspectableObjects(storageBox); // add bed and kitchen later
        setInspectableObjects(bed, kitchen);
    }
}

class AnimalPlace extends Inspectable{
    public static boolean comeFrom = false;
    public ArrayList<Animal> animals = new ArrayList<>();
    private int capacity;
    private boolean isFull;
    AnimalPlace(String name, int capacity) {
        setName(name);
        inspectableController = new AnimalPlaceController(this);
        setCommands();
        this.capacity = capacity;
        isFull = false;
    }
    public void increaseCapacity(int number){
        capacity += number;
    }
    //avale barname mogheye new kardan aval setAnimalso mizani badesh setCommandso
    public void setCommands() {
        if (getAnimals() != null) {
            String[] animalNames = new String[getAnimals().size()];
            for (int i = 0; i < getAnimals().size(); i++) {
                animalNames[i] = getAnimals().get(i).getName();
                System.out.println(animalNames[i] = getAnimals().get(i).getName());
            }
            setCommands(animalNames);
        }
    }
    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public boolean isFull() {
        System.out.println(animals.size());
        if (animals.size() == capacity)
            return true;
        return false;
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(ArrayList<Animal> animals) {
        this.animals = animals;
    }
    public void removeAnimal(Animal animal){
        animals.remove(animal);
    }
    public void nextDay(){
        for (int i = 0; i < animals.size(); i++)
            animals.get(i).nextDay();
    }
}
class AnimalPlaceController extends InspectableController {
    private AnimalPlace animalPlace;
    private AnimalPlaceView animalPlaceView = new AnimalPlaceView(this);
    AnimalPlaceController(AnimalPlace animalPlace) {
        super(animalPlace);
        this.animalPlace = animalPlace;
        setView(animalPlaceView);
    }
    public void removeAnimal(Animal animal){
        animalPlace.removeAnimal(animal);
    }
    @Override
    public void showMenu() {
        animalPlace.setCommands();
        super.showMenu();
    }

    @Override
    public void runMethods(int commandNum) {
        try {
            if (!animalPlace.comeFrom) {
                Animal animal = animalPlace.getAnimals().get(commandNum - 1);
                addMenu(animal.inspectableController);
            } else {
                animalPlace.comeFrom = false;
                Animal animal = animalPlace.getAnimals().get(commandNum - 1);
                animalPlaceView.selling();
                Scanner scanner = new Scanner(System.in);
                String whichMethod = scanner.next(); //khob try catch gozashtim dg
                if(Integer.parseInt(whichMethod) == 1){
                   AnimalController animalController = (AnimalController) animal.inspectableController;
                   animalController.showStatus();
                }
                else if(Integer.parseInt(whichMethod) == 2){
                    animalPlaceView.ask(animal.getName() , animal.getPrice());
                    char reply = scanQuestion();
                    if(reply == 'N'){
                        return;
                    }
                    else if(reply == 'Y'){
                        Person.getPerson().putMoney(animal.getPrice());
                        removeAnimal(animal);
                    }
                    else
                        inspectableView.invalid();
                }
                else if(whichMethod.equals("back"))
                    return;
            }
        }
        catch (Exception e){
            System.out.println("invalid target!");
            //felan nothing
        }
    }
}
class AnimalPlaceView extends InspectableView{
    AnimalPlaceController animalPlaceController;
    public AnimalPlaceView(InspectableController inspectableController) {
        super(inspectableController);
        animalPlaceController = (AnimalPlaceController) inspectableController;
    }

    public void selling(){
        System.out.println("1. Status");
        System.out.println("2. Sell this animal");
    }
    public void ask(String name , int price){
        System.out.println("You will sell " + name + " for " + price + " Gil. Is this okay? (Y/N) ");
    }
}
class PlaceOfAnimalPlaces extends Inspectable{
    ArrayList<AnimalPlace> animalPlaces = new ArrayList<>(); //TODO use class Holder
    PlaceOfAnimalPlaces(ArrayList<AnimalPlace> animalPlaces1){
        setName("Animals"); //masalan
        animalPlaces = animalPlaces1;
        setCommands(commandsToStrings());
        inspectableController = new PlaceOfAnimalPlacesController(this);
    }
    public String[] commandsToStrings(){
        String[] animals = new String[animalPlaces.size()];
        for (int i = 0; i <animalPlaces.size() ; i++) {
            animals[i] = animalPlaces.get(i).getName();
        }
        return animals;
    }
}
class PlaceOfAnimalPlacesController extends InspectableController{
    private PlaceOfAnimalPlaces placeOfAnimalPlaces;
    PlaceOfAnimalPlacesController(PlaceOfAnimalPlaces placeOfAnimalPlaces){
        super(placeOfAnimalPlaces);
        this.placeOfAnimalPlaces = placeOfAnimalPlaces;
    }
    public void runMethods(int commandNum){
        if(commandNum <= 0 || commandNum > placeOfAnimalPlaces.getCommands().size()){
            inspectableView.invalid();
        }
        else{
            AnimalPlace.comeFrom = true;
            AnimalPlace animalPlace = placeOfAnimalPlaces.animalPlaces.get(commandNum - 1);
            addMenu(animalPlace.inspectableController);
        }

    }
}

class MachinePlace extends Inspectable{
    private ArrayList<Machine> machines = new ArrayList<>();
    static private MachinePlace machinePlace = new MachinePlace();
    MachinePlace() {
        setName("Machines");
        inspectableController = new MachinePlaceController(this);
    }

    public static MachinePlace getMachinePlace() {
        return machinePlace;
    }

    public void setCommands() {
        String[] machineNames = new String[machines.size()];
        for (int i = 0; i < machines.size(); i++) {
            machineNames[i] = machines.get(i).getName();
        }
        setCommands(machineNames);
    }

    public void addMachine(Machine machine) {
        machines.add(machine);
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }
}
class MachinePlaceView extends InspectableView{
    MachinePlaceController machinePlaceController;
    public MachinePlaceView(InspectableController inspectableController) {
        super(inspectableController);
        machinePlaceController = (MachinePlaceController) inspectableController;
    }
}
class MachinePlaceController extends InspectableController {
    private MachinePlace machinePlace;
    private MachinePlaceView machineView = new MachinePlaceView(this);
    MachinePlaceController(MachinePlace machinePlace) {
        super(machinePlace);
        this.machinePlace = machinePlace;
        setView(machineView);
    }


    @Override
    public void showMenu() {
        machinePlace.setCommands();
        super.showMenu();
    }

    @Override
    public void runMethods(int commandNum) {
        Machine machine = machinePlace.getMachines().get(commandNum - 1);
        addMenu(machine.inspectableController);
    }
}
