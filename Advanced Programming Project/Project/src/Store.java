import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Store extends Inspectable {
    protected ArrayList<Item> itemsToSell = new ArrayList<>();
    protected String name;
    protected StoreController storeController = new StoreController(this);
    HashMap<String, Integer> hashMapItemsToSell;
    ArrayList<Item> itemstoSell;
    private List storeList;
    Store(String name , ArrayList<Item> items){ //az tu holder arraylistaro midim behesh
        setItemstoSell(items);
        setName(name);
        setCommands("Check this shop",
                "Buy an item" ,
                "Sell an item");
        inspectableController = storeController;
        storeList = new List(name, items);
        storeList.setStoreController(storeController); //TODO ino bayad bezanam
    }


    public ArrayList<Item> getItemstoSell() {
        return itemstoSell;
    }
    public void setItemstoSell(ArrayList<Item> itemstoSell) {
        this.itemstoSell = itemstoSell;
    }
    public ArrayList<Item> getItemsToSell(){
        return itemstoSell;
    }
    public HashMap<String, Integer> getHashMapItemsToSell() {
        return hashMapItemsToSell;
    }

    public void setHashMapItemsToSell(HashMap<String, Integer> hashMapItemsToSell) {
        this.hashMapItemsToSell = hashMapItemsToSell;
    }

    public List getList() {
        return storeList;
    }

    public void setList(List list) {
        this.storeList = list;
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

class StoreView extends InspectableView{
    private StoreController storeController;

    public StoreView(StoreController storeController) {
        super(storeController);
        this.storeController = storeController;
    }

    public void checkThisShop(ArrayList<Item> items) {
        for(int i = 0 ; i < items.size() ; i++){
            System.out.println(i + 1 + " " + items.get(i).getName());
        }
    }
    public void invalidInput(){
        System.out.println("invalid Input");
    }
    public void askTheCustomer(String name , int number , int money){
        System.out.println("you will buy x" + number + " " + name + " for" + " " + money + " Gil. Is this ok ? (Y/N)");
    }

    public void askTheCustomer(String name , int number , int money , boolean whichMethod){
        System.out.println("you will sell x" + number + " " + name + " for" + " " + money + " Gil. Is this ok ? (Y/N)");
    }
    public void askTheNumber(){
        System.out.println("How many?");
    }

}
class StoreController extends InspectableController {
    private Store store;
    private StoreView storeView = new StoreView(this);
    private PlaceOfAnimalPlaces placeOfAnimalPlaces = new PlaceOfAnimalPlaces(new ArrayList<AnimalPlace>()); //TODO ino bayad az hodler biari
    StoreController(Store store) {
        super(store);
        this.store = store;
        setView(storeView);
    }

    public void checkThisShop(){
    }

    private void buyAnItem() {
    }

    @Override
    public void runMethods(int commandNum){
        if(commandNum == 1){
            InspectableController inspectableController = store.getList().inspectableController;
            addMenu(inspectableController);
        }
        else if(commandNum == 2) { //in ghalate alan
            storesBuying = true;
            whichMethod = true;
            InspectableController inspectableController = store.getList().inspectableController;
            addMenu(inspectableController); //injaro bayad ba kimia chek koni
            // ://inam ke selle
        }
        else if(commandNum == 3) {
            if (!store.getName().equals("Butchery")) {
                try {
                    Item item = BackPackController.getItemFromBackPack();
                    String answer = PopUpasker.display("Store" , "How many");
                    try {
                        int number = Integer.parseInt(answer);
                        if (number <= BackPack.getBackPack().itemIntegerHashMap.get(item.getName())) { //yani be in tedad dare tu kifesh be salamati

                            boolean sure = ConfirmBox.display("Store" , "you will sell x " + number  + " " +  item.getName() + " for" + " " + item.getPrice() * number + " Gil. Is this ok ? (Y/N)");
                            if (sure) {
                                BackPack.getBackPack().removeItem(item, number);
                                Person.getPerson().putMoney(item.getPrice() * number);
                            } else if (!sure) {
                                return;
                            } else
                                storeView.invalidInput();
                        }
                    } catch (Exception e) {
                        Popup.makePopup("You don't have themte");
                    }

                } catch (Exception e) {
                    storeView.invalidInput();
                }

            }
        }
        else //age butchery bud
        {
            InspectableController inspectableController = placeOfAnimalPlaces.inspectableController;
            addMenu(inspectableController);
        }
    }
    public void buy(Item item){
        store.buyItem(item);
    }
}