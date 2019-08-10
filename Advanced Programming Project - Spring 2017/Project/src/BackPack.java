import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BackPack extends Storage implements Cloneable{
    private static int MAX_CAPACITY = 10;
    private static BackPack backPack = new BackPack(MAX_CAPACITY);
    public static BackPackController backPackController = new BackPackController(getBackPack());
    public BackPack(int capacity) {
        super(capacity);
        setName("Backpack");
        setCommands();
        inspectableController = new BackPackController(this);

    }
    public boolean isFulled(){
        return getCapacity() == getPackBackNumberOfItems();
    }
    public static BackPack getBackPack() {
        return backPack;
    }
    public static void setBackPack(BackPack backPack) {
        BackPack.backPack = backPack;
    }
    private int getPackBackNumberOfItems(){
        int num = 0;
        for (Item item : itemIntegerHashMap.keySet()) {
            num += itemIntegerHashMap.get(item);
        }
        return num;
    }
    public int backPackSize(){
        return itemIntegerHashMap.size();
    }
    @Override
    public boolean removeItem(Item item , int num){
        boolean result = super.removeItem(item , num);
        backPack.setCommands();
        return result;

    }
    @Override //sets the commands, and the updates items arrayList -- call with no args
    public void setCommands(String... commands) { // should be called when ever the hashMap is changed
        Iterator<Map.Entry<Item, Integer>> itemIterator = itemIntegerHashMap.entrySet().iterator();
        items = new ArrayList<>();
        this.commands = new ArrayList<>();
        while (itemIterator.hasNext()) {
            Map.Entry pair = itemIterator.next();
            Item item = (Item) pair.getKey();
            items.add(item);
            int num = (int) pair.getValue();
            if (item.getType() == null) {
                if (num == 1)
                    this.commands.add(item.getName());
                else
                    this.commands.add(item.getName() + " x" + num);
            }
            else {
                if (num == 1)
                    this.commands.add(item.getType() + " " + item.getName());
                else
                    this.commands.add(item.getType() + " " + item.getName() + " x" + num);
            }
        }
    }

    @Override
    public Cloneable clone() {
//        super.clone();
        BackPack backPack = new BackPack(getCapacity());
        for (Item item : itemIntegerHashMap.keySet())
            backPack.itemIntegerHashMap.put(item, this.itemIntegerHashMap.get(item));
        return backPack;
    }

    public static int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public static void setMaxCapacity(int maxCapacity) {
        MAX_CAPACITY = maxCapacity;
    }
}

class BackPackController extends StorageController {
    private static BackPack backPack;
    private StorageView storageView = new StorageView(this);

    BackPackController(BackPack backPack) {
        super(backPack);
        this.backPack = backPack;
        setView(storageView);
    }

    @Override
    public int scan() { // handle if the backpack is empty or full
        // TODO - handle back
        command = scanner.nextLine();
        if (command.equals("WhereAmI"))
            menus.peek().showMenu();
        if (command.equals("back")) {
            backPackMenus.clear();
            return 1;
        }
        try {
//            int commandNum = Integer.parseInt(command);
//            if (commandNum > backPack.itemIntegerHashMap.size() || commandNum <= 0)
//                return -1;
//            runMethods(commandNum);
//
//            return 1;
            int commandNum = Integer.parseInt(command);
            runMethods(commandNum);
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void runMethods(int commandNum) {
        addMenu(backPack.items.get(commandNum - 1).inspectableController);
//        Controller.backPackMenus.push(backPack.items.get(commandNum - 1).inspectableController);
        //gets the item, and pushes its controller to the backPack stack
    }

    static void openBackPack() {
        InspectableController backPackController = BackPack.getBackPack().inspectableController;
        backPackMenus.push(BackPack.getBackPack().inspectableController);
        backPackController.showMenu(); //override back
    }
    static void closeBackPack() {
        backPackMenus.clear();
    }

    public static Item getItemFromBackPack(String name, String type) {
        BackPackPopUp backPackPopUp = new BackPackPopUp(type, name);
        backPackPopUp.show();
        if (backPackPopUp.hasPressedBack())
            return null;
        return backPackPopUp.getChosenOne();
    }

    public static Item getItemFromBackPack(String itemName, int number) {
        BackPackPopUp backPackPopUp = new BackPackPopUp(number, itemName);
        backPackPopUp.show();
        return backPackPopUp.getChosenOne();
    }


    static Item getItemFromBackPack() { //gets an unspecific item from back pack by the number entered
        BackPackPopUp backPackPopUp = new BackPackPopUp();
        backPackPopUp.show();
        return backPackPopUp.getChosenOne();
//        openBackPack();
//        BackPackController backPackController = (BackPackController) BackPack.getBackPack().inspectableController;
//        backPackController.scan();
//        if (backPackMenus.size() == 0)
//            return null;
//        Inspectable inspectable = backPackMenus.peek().inspectable;
//        closeBackPack();
//        return (Item) inspectable;
    }

    static Item getItemFromBackPack(boolean typeChecking) {
        openBackPack();
        BackPackController backPackController = (BackPackController)BackPack.getBackPack().inspectableController;
        Inspectable inspectable;
        while (true) { //age nadashte bashe item ro bayad back bezane
            backPackController.scan();
            inspectable = backPackMenus.peek().inspectable;
            Tool tool = (Tool) inspectable;
//            System.out.println("got " + inspectable.getName());
            if (tool.isBroken()) {
//                if (inspectable instanceof Tool) {
//                    Tool tool = (Tool) inspectable;
//                    if (tool.isBroken());
//                    // toolView --> broken
//
                break;
            }
            else;
            //ye koofti chap mikone
        }
        closeBackPack();

        return (Item) inspectable;
    }

    public void fulled() {
        if (backPack.itemIntegerHashMap.size() >= backPack.getCapacity())
            storageView.fulled(backPack.getName());
    }
    static Item getItemFromBackPack(String type) {
        BackPackPopUp backPackPopUp = new BackPackPopUp(type, null);
        backPackPopUp.show();
        return backPackPopUp.getChosenOne();
    }

    public boolean takeMultipleItems(HashMap<String, Integer> items) {
        for (String itemName :
                items.keySet()) {
            Item item = getItemFromBackPack(itemName, items.get(itemName));
            if (item == null)
                return false;
            BackPack.getBackPack().removeItem(item, items.get(itemName));
        }
        return true;
    }
}
