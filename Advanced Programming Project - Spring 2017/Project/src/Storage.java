import java.util.*;

public class Storage extends Inspectable {
    protected HashMap<Item, Integer> itemIntegerHashMap = new HashMap<>();
    protected ArrayList<Item> items = new ArrayList<>();

    public int getCapacity() {
        return capacity;
    }

    private int capacity;
    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public boolean removeItem(Item wantedItem, int wantedNum) {
        Iterator<Item> ourItems = items.iterator();
        while (ourItems.hasNext()) {
            if (ourItems.next().getName().equals(wantedItem.getName())) {
                ourItems.remove();
                // If you know it's unique, you could `break;` here
            }
        }
        Item hashItem = null;
        for (Item item : itemIntegerHashMap.keySet())
            if (item.equals(wantedItem)) {
                hashItem = item;
                break;
            }
        if (hashItem == null || itemIntegerHashMap.get(hashItem) < wantedNum)
            return false;
        int num = itemIntegerHashMap.get(hashItem);
        if (num > wantedNum)
            itemIntegerHashMap.replace(hashItem, num - wantedNum);
        if (num == wantedNum) {
            itemIntegerHashMap.remove(hashItem);
            items.remove(hashItem);
        }
        if (this instanceof BackPack)
            setCommands();

        return true;
    }

    public void addItem(Item item) {
        if (item instanceof Stackable) {
            if (itemIntegerHashMap.containsKey(item)) { // check if this works correctly
                int num = itemIntegerHashMap.get(item);
                itemIntegerHashMap.replace(item, num, num + 1);
            }
        }
        else {
            if (this instanceof BackPack && itemIntegerHashMap.size() >= capacity) {
                //notify the storage is full
                BackPackController backPackController = (BackPackController) inspectableController;
                backPackController.fulled();
                return;
            }
            itemIntegerHashMap.put(item, 1);
            items.add(item);
        }
        if (this instanceof BackPack)
            setCommands();
    }

    public void addItem(Item item, int itemNumber) {
        if (item instanceof Stackable) {
            if (itemIntegerHashMap.containsKey(item)) { // check if this works correctly
                int num = itemIntegerHashMap.get(item);
                itemIntegerHashMap.replace(item, num, num + itemNumber);
            }
        }
        else {
            if (this instanceof BackPack && itemIntegerHashMap.size() >= capacity) {
                //notify the storage is full
                BackPackController backPackController = (BackPackController) inspectableController;
                backPackController.fulled();
                return;
            }
            itemIntegerHashMap.put(item, itemNumber);
        }
        if (this instanceof BackPack)
            setCommands();
    }

    public int itemNum(Item item) {
        if (!items.contains(item))
            return 0;
        return itemIntegerHashMap.get(item);
    }
}

class StorageController extends InspectableController {
    private Storage storage;
    StorageController(Storage storage) {
        super(storage);
        this.storage = storage;
    }
}

class StorageView extends InspectableView {
    //public static StorageView storageView = new StorageView();

    public StorageView(InspectableController inspectableController) {
        super(inspectableController);
    }

    //public static StorageView getStorageView() {
    // return storageView;
    //  }

    public static void fulled(String storageName) {
        System.out.println(storageName + " is full");
    }
    public static void PrintDemanded(String name, int num){
        System.out.println("Choose a " + name + " to use");
    }
    public static void PrintDemandedWithType(String name, String type){
        System.out.println("Choose " + name + "from your backpack. It has to be " + type + " (or stronger one)" );
    }
    public void Status(Item item, ArrayList<Item> items){ //bayad tu view bashe be nazaram
        if(items.contains(item)){
            System.out.println(item.status); //vaziate kharabi va ehtemale kharabi(ke nemidunam koja piade sazi shode)

        }
        else{
            System.out.println("Invalid Input");
        }
    }
    public static void broken(){
        System.out.println("The item you chose is broken");
    }

    void choose(String itemName, int itemNum) {
        System.out.println("Choose " + itemName + " x" + itemNum);
    }
    void chooseNumber() {
        System.out.println("Choose the number of items to pick");
    }
    public void takeOutItem(){
        System.out.println("Choose item from your Storage box to take out.");
    }
    public void getItems(ArrayList<Item> items){
        for(int i = 0 ; i < items.size() ; i++){
            System.out.println(i + 1 + ". " + items.get(i).name);
        }
    }
}


