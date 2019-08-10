
class AnimalController extends InspectableController{
    private AnimalView animalView = new AnimalView(this);
    private Animal animal;
    public AnimalController(Animal animal) {
        super(animal);
        this.animal = animal;
        setView(animalView);
    }
    @Override
    public void runMethods(int commandNum){
        if (commandNum == 1) {
            Popup.showStatus(animal);
        }
        else if (commandNum == 2) {
            Eatable animalFood;
            if (animal.getName().equals("Cow") || animal.getName().equals("Sheep"))
                animalFood = (Eatable) BackPackController.getItemFromBackPack("Cow and Sheep Food");
            else
                animalFood = (Eatable) BackPackController.getItemFromBackPack("Animal Food");
            if (animalFood == null)
                return;
            animal.feed(animalFood);
        }
        else if (commandNum == 3) {
            Eatable animalMedicine = (Eatable) BackPackController.getItemFromBackPack("Animal Medicine");
            if (animalMedicine == null)
                return;
            animal.heal(animalMedicine);
        }
        else if (commandNum == 4){
            if (!animal.isToolNeeded()) {
                Product product = animal.getProduct();
                BackPack.getBackPack().addItem((Item)product);
            }
            else if (animal.isToolNeeded()) {
                Product product;
                do {
                    Tool tool = (Tool) BackPackController.getItemFromBackPack(animal.getTool().getName());
                    if (tool == null)
                        return;
                    product = animal.getProduct(tool);
                } while (product == null);
                BackPack.getBackPack().addItem((Item) product);
            }
        }
    }
    public void showStatus(){
        animal.setStatus();
        animalView.showStatus(animal.getStatus());
    }
}

class Animal extends Item {
    private boolean isHealthy;
    private boolean fedToday;
    private int daysToSheer;
    private Product product;//TODO- make it product"s"
    private String howToGetProducts; // for chickens => "Collect eggs" for Cows => "Milk this Cow" .... (needed for costumize animals)
    private Tool tool;

    public Animal(String type, String howToGetProducts, Tool tool) {
        this.type = type;
        toolNeeded = true;
        this.tool = tool;
        this.howToGetProducts = howToGetProducts;
        setCommands("Status", "Feed", "Heal", howToGetProducts);
        inspectableController = new AnimalController(this);
    }

    public Animal(String type, String howToGetProducts) {
        this.type = type;
        toolNeeded = false;
        this.tool = null;
        this.howToGetProducts = howToGetProducts;
        setCommands("Status", "Feed", "Heal", howToGetProducts);
    }

    public boolean feed(Eatable animalFood) {
        if (animalFood.getType().equals("Cow and Sheep Food") && name.equals("Cow")) {
            fedToday = true;
            return true;
        }
        if (animalFood.getType().equals("Chicken Food") && name.equals("Chicken")) {
            fedToday = true;
            return true;
        }
        return false;
    }

    public void heal(Eatable animalMedicine) {
        if (animalMedicine.getType().equals("Animal Medicine"))
            isHealthy = true;
    }

    public void setHealthy(boolean healthy) {
        isHealthy = healthy;
    }

    public Product getProduct(Tool tool) {
        if (tool.getName().equals("Milker") && this.getType().equals("Cow") && product != null) {
            Product temp = product;
            product = null;
            return temp;
        }
        if (tool.getName().equals("Scissors") && this.getType().equals("Sheep") && product != null && daysToSheer == 0) {
            Product temp = product;
            product = null;
            return temp;
        }
        return null;
    }

    public void makeProducts(){
        //make products
    }
    public Product getProduct() {
        if (product != null) {
            Product temp = product;
            product = null;
            return temp;
        }
        return null;
    }


    public String getStatus() {
        String status = new String();
        if (this.getType().equals("Sheep"))
            status = "is Healthy : " + isHealthy + "\nFed Today : " + fedToday + "\nDays to sheer : " + daysToSheer;
        else
            status = "is Healthy : " + isHealthy + "\nFed Today : " + fedToday;
        return status;
    }

    public boolean isToolNeeded() {
        return toolNeeded;
    }

    private boolean toolNeeded;

    public Tool getTool() {
        return tool;
    }

    public void nextDay(){
        if (name.equals("Chicken"))
            product = Holder.getEgg();
        else if (name.equals("Cow"))
            product = Holder.getMilk();
        else if (name.equals("Sheep"))
            product = Holder.getWool();
    }
}

class AnimalProduct extends Product {
    public AnimalProduct(String name, String type, boolean isSpecial) {
        super(name, type, isSpecial);
    }
}
class AnimalView extends InspectableView{
    public AnimalView(InspectableController inspectableController) {
        super(inspectableController);
    }

    public void showStatus(String status){
        System.out.println(status);
    }

}