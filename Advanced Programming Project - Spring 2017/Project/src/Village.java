import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Village extends Place {
    private Market market = new Market();
    private Workshop workshop = new Workshop(Holder.workshopItems);
    private Clinic clinic = new Clinic(Holder.clinicItems);
    private Cafe cafe = new Cafe();
    private Ranch ranch = new Ranch(Holder.ranchItems);
    private Gym gym = new Gym();
    private Laboratory laboratory = new Laboratory();
    Village() {
        super("Village");
        setAccessiblePlaces();
        setInspectableObjects(workshop, clinic, ranch, gym, laboratory);
    }

    @Override
    void setAccessiblePlaces(Place... places) {
        accessiblePlaces.add(market);
        accessiblePlaces.add(cafe);
        for (int i = 0; i < accessiblePlaces.size(); i++)
            accessiblePlaces.get(i).setAccessiblePlaces(this);
        super.setAccessiblePlaces(places);
    }
}

class Market extends Place {
    private Store groceriesStore = new Store("Groceries Store" , Holder.groceryStoreItems);
    private Store generalStore = new Store("General Store" , Holder.generalStoreItems);
    private Store butchery = new Store("Butchery" , Holder.butcheryItems);
    Market() {
        super("Market");
        setInspectableObjects(groceriesStore, generalStore, butchery);
    }
}

 class Workshop extends Inspectable {

    //private ArrayList<Task> tasks;
    private ArrayList<Tool> tools = new ArrayList<>();
    List workShopList;
    public Workshop(ArrayList<Tool> tools){
        inspectableController = new WorkshopController(this);
        setName("WorkShop");
        this.tools = tools;
        setCommands("Check this shop","Make a tool", "Repair a tool" ,"Disassemble a tool");

        workShopList = new List("Workshop" , toolToItem());
        workShopList.setWorkshopController((WorkshopController)inspectableController); //ishala ke bug nadare
    }
    public ArrayList<Item> toolToItem(){
        ArrayList <Item> items = new ArrayList<>();
        for(int i = 0 ; i < tools.size() ; i++){
            items.add((Item)tools.get(i));
        }
        return items;
    }
    public void setTools(ArrayList<Tool> tools) {
        this.tools = tools;
    }

    public boolean makeTool(Tool tool) {
        if(!BackPack.getBackPack().isFulled()){
            BackPack.getBackPack().addItem(tool);
            return true;
        }
        return false;
    }

    public void repairTool(Tool tool) {
        Person.getPerson().takeMoney(tool.getRepairMoney());
        HashMap<String , Integer> repairing = new HashMap <String, Integer>();
        for (Item item:tool.getRepairNeeded().keySet()) {
            repairing.put(item.getName() , tool.getRepairNeeded().get(item));
        }
        if(BackPack.backPackController.takeMultipleItems(repairing));
           tool.setBroken(false);
    }

    public List getWorkShopList() {
        return workShopList;
    }

    public void disassembleTool(Tool tool) {
        if(tool.getItemNeeded().size() != 0) {
            if (BackPack.getBackPack().backPackSize() + tool.getItemNeededSize() <= BackPack.getBackPack().getCapacity()) {
                int sum = 0;
                for (Item item : tool.getItemNeeded().keySet()) {
                    int eachMoney = 0;
                    for(int i = 0 ; i < tool.getMadeOf().size() ; i++){
                        if(tool.getMadeOf().get(i).getName().equals(item)){
                            eachMoney = tool.getMadeOf().get(i).getPrice();
                            break;
                        }
                    }
                    if (tool.isBroken()) {
                        sum += (eachMoney / 2);
                    } else {
                        sum += eachMoney;
                    }
                }
                Person.getPerson().takeMoney(sum);
            }
        }
    }

    public void addTool(Tool tool){
        tools.add(tool);
    }

    public void removeTool(Tool tool){
        tools.remove(tool);
    }

//    public void checkThisShop() {
//
//    }

}
class WorkshopController extends InspectableController{
    private Workshop workshop;
    private WorkShopView workShopView = new WorkShopView(this);
    WorkshopController(Workshop workshop){
        super(workshop);
        this.workshop = workshop;
        setView(workShopView);
    }
    @Override
    public void runMethods(int commandNum){
        if(commandNum == 1){
            InspectableController inspectableController = workshop.getWorkShopList().inspectableController;
            addMenu(workshop.getWorkShopList().inspectableController);
        }
        else if(commandNum == 2){
            whichMethod = true;
            InspectableController inspectableController = workshop.getWorkShopList().inspectableController;
            addMenu(workshop.getWorkShopList().inspectableController);
        }
        else if(commandNum == 3){
            Tool brokenTool = (Tool) BackPack.getBackPack().backPackController.getItemFromBackPack(true);
            workshop.repairTool(brokenTool);

        }
        else if(commandNum == 4){
            Tool dissassembleTool = (Tool) BackPack.getBackPack().backPackController.getItemFromBackPack();
            workshop.disassembleTool(dissassembleTool);
        }
        else{
            //prints the invalid target(dunno where the hell)
        }
    }
    public boolean makeTool(Tool tool) {
        return workshop.makeTool(tool);
    }
}
class WorkShopView extends InspectableView{
    private WorkshopController workshopController;

    public WorkShopView(WorkshopController workshopController) {
        super(workshopController);
        this.workshopController = workshopController;
    }
}

class Clinic extends Inspectable {
    ArrayList<Item> itemstoSell;
    HashMap<String, Integer> hashMapItemsToSell;
    private List clinicList;
    Clinic(ArrayList<Item> items) { // Holder.clinicItems
        setItemstoSell(items);
        clinicList = new List("Clinic", items);
        setName("Clinic");
        setCommands("Check this shop", "Buy an item", "Heal up");
        ClinicController clinicController = new ClinicController(this);
        inspectableController = clinicController;
        clinicList.setClinicController(clinicController);
    }

    public ArrayList<Item> getItemstoSell() {
        return itemstoSell;
    }

    public void setItemstoSell(ArrayList<Item> itemstoSell) {
        this.itemstoSell = itemstoSell;
    }

    public HashMap<String, Integer> getHashMapItemsToSell() {
        return hashMapItemsToSell;
    }

    public void setHashMapItemsToSell(HashMap<String, Integer> hashMapItemsToSell) {
        this.hashMapItemsToSell = hashMapItemsToSell;
    }

    public List getClinicList() {
        return clinicList;
    }

    public void setClinicList(List clinicList) {
        this.clinicList = clinicList;
    }

    public boolean buyItem(Item item) {
       BackPack.getBackPack().addItem(item);
        return true;
    }

    public void addItem(Item item){
        itemstoSell.add(item);
    }

    public void removeItem(Item item){
        itemstoSell.remove(item);
    }
}

class ClinicController extends InspectableController{
    private Clinic clinic;
    private ClinicView clinicView = new ClinicView(this);
    ClinicController(Clinic clinic1) {
        super(clinic1);
        clinic = clinic1;
        setView(clinicView);
    }
    @Override
    public void runMethods(int commandNum){
        if(commandNum == 1){
            InspectableController inspectableController = clinic.getClinicList().inspectableController;
            addMenu(inspectableController);
        }
        else if(commandNum == 2){ //in ghalate alan
            buyItem = true;
            whichMethod = true;
            InspectableController inspectableController = clinic.getClinicList().inspectableController;
            addMenu(inspectableController); //injaro bayad ba kimia chek koni

        }
        else if(commandNum == 3){
            try {
                boolean answer = ConfirmBox.display("Clinic" , "Do you want to heal up for 500? Y/N");
                if(answer) {
                    if(Person.getPerson().getMoney() < 500){
                        Popup.makePopup("Not enough money");

                    }
                    else {
                        Person.getPerson().heal();
                        Person.getPerson().takeMoney(500);

                    }
                }
            }
            catch (Exception e){
                clinicView.invaldTarget();
            }

        }
        else{
            clinicView.invaldTarget();
        }
    }
    public void buy(Item item){
        clinic.buyItem(item);
    }
}
class ClinicView extends InspectableView{
    private ClinicController clinicController;

    public ClinicView(ClinicController clinicController) {
        super(clinicController);
        this.clinicController = clinicController;
    }

    public void askTheCustomer(){
        System.out.println("You will Heal up for 500 Gil. Is this okay? (Y/N) ");
    }
    public void invaldTarget(){
        System.out.println("Invalid Target");
    }
}

class Cafe extends Place {
    static Cafe cafe;
    private DiningTable diningTable = new DiningTable(Holder.defaultCafe());
    Cafe() {
        super("Cafe");
        cafe = this;
        setInspectableObjects(diningTable);
    }
}
class Ranch extends Inspectable {
    //    private ArrayList<Task> tasks;
    ArrayList<Animal> itemstoSell;
    private List ranchList;
    HashMap<String, Integer> hashMapItemsToSell;
    Ranch(ArrayList<Animal> animals) {
        setName("Ranch");
//        //Animal animal = new Animal("dunno" , new Tool("kooft" , "dard" , false , 1));
//        animal.setName("shenghel");
//        animal.type = "Cow";
//        animals.add(animal);
        setItemstoSell(animals);
        ranchList = new List("Ranch", animalToItem());
        setCommands("Check this shop", "Buy an Item", "Buy an animal", "Increase my barn capacity");
        RanchController ranchController = new RanchController(this);
        inspectableController = ranchController;
        ranchList.setRanchController(ranchController);
    }
    public ArrayList<Item> animalToItem(){
        ArrayList<Item> animals = new ArrayList<>();
        for(int i = 0 ; i < itemstoSell.size() ; i++){
            animals.add(itemstoSell.get(i));
        }
        return animals;
    }
    public ArrayList<Animal> getItemstoSell() {
        return itemstoSell;
    }

    public void setItemstoSell(ArrayList<Animal> itemstoSell) {
        this.itemstoSell = itemstoSell;
    }
    public HashMap<String, Integer> getHashMapItemsToSell() {
        return hashMapItemsToSell;
    }

    public void setHashMapItemsToSell(HashMap<String, Integer> hashMapItemsToSell) {
        this.hashMapItemsToSell = hashMapItemsToSell;
    }

    public List getRanchList() {
        return ranchList;
    }

    public void setRanchList(List ranchList) {
        this.ranchList = ranchList;
    }

    public boolean buyItem(Animal item) { //TODO inja jahaname
        BackPack.getBackPack().addItem(item);
        return true;
    }
}
class RanchController extends InspectableController{
    private Ranch ranch;
    private RanchView ranchView = new RanchView(this);
    RanchController(Ranch ranch1) {
        super(ranch1);
        ranch = ranch1;
        setView(ranchView);
    }
    @Override
    public void runMethods(int commandNum){
        if(commandNum == 1){
            InspectableController inspectableController = ranch.getRanchList().inspectableController;
            addMenu(inspectableController);
        }
        else if(commandNum == 2){ //hich etefaghi nemiofte chon buy item dg che koofie tu ranch ? :|
            //gotta be checked
            InspectableController inspectableController = ranch.getRanchList().inspectableController;
            addMenu(inspectableController); //injaro bayad ba kimia chek koni

        }
        else if(commandNum == 3){
            whichMethod  = true;
            buyAnimal = true;
            System.out.println("choose an animal");
            InspectableController inspectableController = ranch.getRanchList().inspectableController;
            addMenu(inspectableController);
        }
        else if(commandNum == 4){
            ArrayList<String> commands = new ArrayList<>();
            String[] ButtonCommands = new String[Barn.getBarn().getAnimalPlaces().size()];//(String[]) commands.toArray();
            for (int i = 0; i < Barn.getBarn().getAnimalPlaces().size(); i++) {
                //commands.add(Barn.getBarn().getAnimalPlaces().get(i).getName());
                ButtonCommands[i] = Barn.getBarn().getAnimalPlaces().get(i).getName();
            }

            int answer = MultiChoicesPopUps.display("Barn" , "Choose an animal Place" , ButtonCommands);
            int number = Integer.parseInt(PopUpasker.display("Animal Place" , "Choose a number"));
            boolean bought = ConfirmBox.display("Animal Place" , "Are you sure you want to expand your animal place by " + 100 * number + " ? Y/N");
            if(bought && Person.getPerson().getMoney() >= 100 * number){
                for (int i = 0; i < Barn.getBarn().getAnimalPlaces().size() ; i++) {
                    if(Barn.getBarn().getAnimalPlaces().get(i).getName().equals(ButtonCommands[i])){
                        Person.getPerson().takeMoney(100 * number);
                        Barn.getBarn().getAnimalPlaces().get(i).increaseCapacity(number);
                        break;
                    }
                }
            }
            else if(Person.getPerson().getMoney() < 100 * number){
                Popup.makePopup("Not enough money");
            }
        }
    }
    public void buy(Animal item){
        ranch.buyItem(item);
    }

    public Ranch getRanch() {
        return ranch;
    }
}
class RanchView extends InspectableView {
    private RanchController ranchController;
    public RanchView(RanchController ranchController) {
        super(ranchController);
        this.ranchController = ranchController;
    }

    public void invaldTarget(){
        System.out.println("Invalid Target");
    }
}

class DiningTable extends Inspectable {
    public static boolean whichMethod = false;
    public DiningTableSupport diningTableSupport;
    DiningTableController diningTableController = new DiningTableController(this);
    public ArrayList<Eatable> menus = new ArrayList<>();
    DiningTable(ArrayList<Eatable> foods) {
        setName("Dining Table");
        menus = foods;
        diningTableSupport = new DiningTableSupport(foods);
        setCommands("Check the Menu", "Buy a meal");
        inspectableController = diningTableController;
    }
}

class DiningTableController extends InspectableController {
    DiningTable diningTable;
    DiningTableView diningTableView = new DiningTableView(this);
    DiningTableController(DiningTable diningTable) {
        super(diningTable);
        this.diningTable = diningTable;
        setView(diningTableView);
    }

    public void showMenu() {
        for (int i = 0; i < diningTable.menus.size(); i++)
            diningTable.menus.get(i).inspectableController.showMenu();
    }

    @Override
    public void runMethods(int commandNum) {
        switch (commandNum) {
            case 1:
                addMenu(diningTable.diningTableSupport.inspectableController);
                return;
            case 2:
                whichMethod = true;
                buyItem = true;
                addMenu(diningTable.diningTableSupport.inspectableController);
                return;
        }
    }
}
class DiningTableSupport extends Inspectable{
    ArrayList<Eatable> foods = new ArrayList<>();
    DiningTableSupport(ArrayList<Eatable> foods1){
        foods = foods1;
        inspectableController = new DiningTableSupportController(this);
        setCommands(recipesAsCommands());
    }
    public String[] recipesAsCommands(){
        String[] names = new String[foods.size()];
        for(int i = 0 ; i < foods.size() ; i++){
            names[i] = new String();
            names[i] = foods.get(i).getName();
        }
        return names;
    }
}
class DiningTableSupportController extends InspectableController{
    DiningTableSupport diningTableSupport;
    DiningTableSupportView diningTableSupportView = new DiningTableSupportView(this);
    DiningTableSupportController(DiningTableSupport diningTableSupport1){
        super(diningTableSupport1);
        diningTableSupport = diningTableSupport1;
        setView(diningTableSupportView);
    }
    @Override
    public void runMethods(int commandNum){
        if(commandNum <= 0 || commandNum > diningTableSupport.getCommands().size()){
            inspectableView.invalid();
        }
        else{
            Eatable food = diningTableSupport.foods.get(commandNum - 1);
            InspectableController inspectableController = diningTableSupport.foods.get(commandNum - 1).inspectableController;
            if(whichMethod == false){
                RecipeController recipeController = (RecipeController) food.inspectableController;
                recipeController.showRecipe();

            }
            else{
                Person.getPerson().takeMoney(food.getPrice());
                Person.getPerson().eat(food);
                whichMethod = false;
            }
        }
    }
}
class DiningTableView extends InspectableView {
    DiningTableController diningTableController;
    public DiningTableView(InspectableController inspectableController) {
        super(inspectableController);
        this.diningTableController = (DiningTableController)inspectableController;
    }
}
class DiningTableSupportView extends InspectableView{
    DiningTableSupportController diningTableSupportController;
    public DiningTableSupportView(InspectableController inspectableController) {
        super(inspectableController);
        this.diningTableSupportController = (DiningTableSupportController) inspectableController;

    }
}