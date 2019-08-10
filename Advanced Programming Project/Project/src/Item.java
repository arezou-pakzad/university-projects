
public class Item extends Inspectable implements Cloneable {
    protected String type;
    protected String status;
    protected String description;
    protected int price;
    protected boolean isSpecial;
    public ItemController itemController = new ItemController(this);
    public Item() {
    }

    //methodai ke item : use , drop , status
    public String getType() {
        return type;
    }
    public Item(String name, String type, boolean isSpecial){
        this.name = name;
        this.type = type;
        this.setName(name);
        if(isSpecial){
            setCommands("Show Status",
                    "Drop this item",
                    "Use this item");
        }
        else{
            setCommands("Show Status",
                    "Drop this item");
        }
        inspectableController = itemController;
    }

    public String getStatus() {
        setStatus();
        return status;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        Item item = (Item) obj;
        return this.getName().equals(item.getName()) && this.getType().equals(item.getType());
        // TODO - override this for some subclasses
    }

    public int getPrice() {
        return price;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setStatus() { //should be overrided for each class
        status = "Name: " + getName()
                + "\nType: " + getType()
                + "\nPrice: " + price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void use(){

    }
    @Override
    public Cloneable clone() {
        return this;
    }
}

class ItemController extends InspectableController {
    Item item;
    ItemView itemView = new ItemView(this);

    ItemController(Item item) {
        super(item);
        this.item = item;
        inspectableView = itemView;
        setView(itemView);
    }
    @Override
    public void runMethods(int commandNum){
        if(commandNum <= 0 || commandNum > item.getCommands().size())
        {
            itemView.invalidInput();
        }
        else{
            if(commandNum == 1){
                if(item.status != null)
                    inspectableView.showStatus(item.getStatus());
            }
            else if(commandNum == 2){
                BackPack.getBackPack().removeItem(item, 1);
                System.out.println(item.getName() + "removed");
                BackPack.getBackPack().setCommands();
            }
            else if(commandNum == 3){
                item.use();
            }
        }
    }
}
class ItemView extends InspectableView {
    private ItemController itemController;

    public ItemView(ItemController itemController) {
        super(itemController);
        this.itemController = itemController;
    }

    public void showStatus(String status) {
        System.out.println(status);
    }
    public void invalidInput(){
        System.out.println("Invalid input");
    }
}
