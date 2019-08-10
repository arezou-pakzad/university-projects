import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
//TODO- fix!!

public class List extends Inspectable {
    private ArrayList<Item> items = new ArrayList<>();
    private WorkshopController workshopController;
    private ClinicController clinicController;
    private StoreController storeController;
    private RanchController ranchController;
    private LaboratoryController laboratoryController;

    public List(String name, ArrayList<Item> items) {
        setName(name);
        inspectableController = new ListController(this);
        this.items = items;
        setCommands(stringItems());
    }

    public LaboratoryController getLaboratoryController() {
        return laboratoryController;
    }

    public void setLaboratoryController(LaboratoryController laboratoryController) {
        this.laboratoryController = laboratoryController;
    }

    public StoreController getStoreController() {
        return storeController;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setStoreController(StoreController storeController) {
        this.storeController = storeController;
    }

    public ClinicController getClinicController() {
        return clinicController;
    }

    public void setClinicController(ClinicController clinicController) {
        this.clinicController = clinicController;
    }

    public WorkshopController getWorkshopController() {
        return workshopController;
    }

    public void setWorkshopController(WorkshopController workshopController) {
        this.workshopController = workshopController;
    }

    public RanchController getRanchController() {
        return ranchController;
    }

    public void setRanchController(RanchController ranchController) {
        this.ranchController = ranchController;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public String[] stringItems() {
        String[] commands = new String[items.size() + 1];
        if (items.size() != 0) {
            if (items.get(0) instanceof Animal) {

                for (int i = 0; i < items.size(); i++) {
                    commands[i] = new String();
                    commands[i] = items.get(i).getType();
                }
                return commands;
            }
            else {
                // String[] commands = new String[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    commands[i] = new String();
                    commands[i] = items.get(i).getName() + items.get(i).getType();
                }
                return commands;
            }
        }
        return commands;

    }
}

class ListController extends InspectableController{
    private List list;
    private ListView listView = new ListView(this);
    ListController(List list){
        super(list);
        this.list = list;
        setView(listView);
    }
    @Override
    public void runMethods(int commandNum){
        if(whichMethod == false) {
            //yani inke statuse
            Item item = list.getItems().get(commandNum - 1);
            item.setStatus();
//            item.setStatus();
            inspectableView.showStatus(item.getStatus());
//            Tool item = list.getItems().get(commandNum - 1);
//            item.setStatus();
//            inspectableView.showStatus(item.getStatus());
        }
        else {
            if (!storesBuying) {
                if (buyItem) {
                    Item item = list.getItems().get(commandNum - 1);
                    if (!BackPack.getBackPack().isFulled()) {
                        if (labMethod){

                            boolean answer = ConfirmBox.display("Laboratory" , "You will buy " + item.getName() + " for " + item.getPrice() + "Y/N");
                            if(answer){
                                Item finalItem1 = item;
                                list.getLaboratoryController().buy((Machine) finalItem1);
                                list.setCommands(list.stringItems());
                                listView = new ListView(this);
                                setView(listView);
                                MachinePlaceView machinePlaceView = new MachinePlaceView(Barn.getBarn().getMachinePlace().inspectableController);
                                MachinePlace.getMachinePlace().setCommands();
                                System.out.println(PlaceView.barnMachines.size());
                                for (int i = 0; i <PlaceView.barnMachines.size() ; i++) {
                                    if(PlaceView.barnMachines.get(i) instanceof ImageView){
                                        ImageView temp = (ImageView)PlaceView.barnMachines.get(i);
                                        if(temp.getImage() == null){
                                            System.out.println("file:icons/" + (finalItem1.getName()) + ".png");
                                            temp.setImage(new Image("file:icons/" + (finalItem1.getName()) + ".png"));
                                            break;
                                        }
                                    }
                                }
                                MachinePlace.getMachinePlace().inspectableController.setView(machinePlaceView);
                                buyItem = false;
                            }
                            if(!answer){
                                buyItem = false;
                            }
                            buyItem = false;
                            labMethod = false;
                            buyItem = false;
                        }
                        else{
                            item = list.getItems().get(commandNum - 1);
                            if (!BackPack.getBackPack().isFulled()) {
                                //listView.askTheCustomer(item.getName(), item.getPrice());
                                Boolean answer = ConfirmBox.display("Buy","You will buy " + item.getName() + " for " + item.getPrice() + "Y/N");
                                if(answer){
                                    Item finalItem = item;
                                    list.getClinicController().buy(finalItem);
                                    buyItem = false;
                                }
                                else {
                                    buyItem = false;
                                }
                                buyItem = false;
                            }
                        }
                    }
                    else { //meaning that backPack is full
                        Popup.fullBackPack();

                    }

                }
                else if(buyAnimal == true){ //hope it works
                    String name = PopUpasker.display("Store" , "Choose a name");
                    Animal animal1 = list.getRanchController().getRanch().getItemstoSell().get(commandNum - 1);
                    Animal animal = null;
                    System.out.println(animal1.getType());
                    if(animal1.getType().equals("Cow")){
                        animal = Holder.getCow();
                    }
                    else if(animal1.getType().equals("Sheep")){
                        animal = Holder.getSheep();
                        System.out.println("sheep");
                    }
                    else if(animal1.getType().equals("Chicken")){
                        animal = Holder.getChicken();
                        System.out.println("chicken");
                    }
                    if(!(animal == null)) {
                        for (int i = 0; i < Barn.getBarn().getAnimalPlaces().size(); i++) {
                            System.out.println(animal.getType() + " " + Barn.getBarn().getAnimalPlaces().get(i).getName());
                            if (Barn.getBarn().getAnimalPlaces().get(i).getName().contains(animal.getType())) {
                                if (!(Barn.getBarn().getAnimalPlaces().get(i).isFull())) { //bug
                                    ArrayList<Animal> animalPlaceAnimal = Barn.getBarn().getAnimalPlaces().get(i).getAnimals();
                                    if (!name.equals("")) {
                                        animal.setName(name);
                                        for (int j = 0; j < animalPlaceAnimal.size(); j++) {
                                            if (name.equals(animalPlaceAnimal.get(j).getName())) {
                                                Popup.invalidTarget();
                                                return;
                                            }
                                        }
                                        Barn.getBarn().getAnimalPlaces().get(i).addAnimal(animal);
                                        for (int j = 0; j < Barn.getBarn().getAnimalPlaces().get(i).getAnimals().size(); j++) {
                                            System.out.println(Barn.getBarn().getAnimalPlaces().get(i).getAnimals().get(j).getName() + " " + Barn.getBarn().getAnimalPlaces().get(i).getAnimals().get(j).getType());
                                        }
                                    }
                                } else {
                                    Popup.fullBarn();
                                }
                                Barn.getBarn().getAnimalPlaces().get(i).setCommands();
                                System.out.println(Barn.getBarn().getAnimalPlaces().get(i).getAnimals().size());
                                return;
                            }

                        }
                    }
                }
                else {
                    // System.out.println("here");
                    Item item = list.getItems().get(commandNum - 1);
                    Tool tool = (Tool) item;
                    System.out.println(tool.getName());
                    HashMap<String , Integer> make = new HashMap<>();
                    for (Item items : tool.getItemNeeded().keySet()) {
                        make.put(items.getName() , tool.getItemNeeded().get(items));
                    }
                    if(BackPack.backPackController.takeMultipleItems(make))
                        list.getWorkshopController().makeTool((Tool) item);
                    whichMethod = false;
                }
            }

            else{//yani tu store ha mikhad kharid kone
                Item item = list.getItems().get(commandNum - 1);
                if (!BackPack.getBackPack().isFulled()) {
                    Stage stage = new Stage();
                    Scene scene;
                    boolean answer = ConfirmBox.display("Store" ,"You will buy " + item.getName() + " for " + item.getPrice() + "Y/N");
                    Text text = new Text("You will buy " + item.getName() + " for " + item.getPrice() + "Y/N");
                    if(answer){
                        list.getStoreController().buy(item);
                        buyItem = false;
                    }
                    else{
                        buyItem = false;
                    }
                    storesBuying = false;
                    whichMethod = false;
                }
                else{
                    AlertBox.display("full" , "The backPack is fulled!");
                }
            }
            whichMethod = false;
        }
    }

}

class ListView extends InspectableView {
    public ListView(InspectableController inspectableController) {
        super(inspectableController);
    }

    public void askTheCustomer(String name, int price) {
        System.out.println("You wil buy " + name + " for " + price + " Y/N ?");
    }

    public void invaliTarget() {
        System.out.println("Invalid target");
    }
    public void showAnimals(ArrayList<Animal> animals){
        System.out.println("Enter the number of animal you want to buy.");
        for (int i = 0; i < animals.size() ; i++) {
            System.out.println(i + 1  +  ". " + animals.get(i).getName());

        }
    }
    public void setName(){
        System.out.println("Choose a name");
    }
}