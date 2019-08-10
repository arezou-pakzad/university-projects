import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.HashMap;

class KitchenView extends InspectableView {
    public KitchenView(InspectableController inspectableController) {
        super(inspectableController);
    }

    public void cookQuestion(){
        System.out.println("\nDo you want to cook this meal? (Y/N)");
    }
}

class KitchenController extends InspectableController {
    private Kitchen kitchen;
    private KitchenView kitchenView = new KitchenView(this);


    public KitchenController(Kitchen kitchen) {
        super(kitchen);
        this.kitchen = kitchen;
        setView(kitchenView);
    }

    @Override
    public void runMethods(int commandNum){
        if (commandNum == 1) {
            if (!BackPack.getBackPack().isFulled()) {
                System.out.println(kitchen.getRecipes().getRecipes().size());
                addMenu(kitchen.getRecipes().inspectableController);
                //kitchen.getRecipes().inspectableController.showMenu();
                if (menus.peek() instanceof RecipeController){
                    kitchenView.cookQuestion();
                    char answer = scanQuestion();
                    if (answer == 'Y')
                        cook();
                    back();
                }
            }
            else
                BackPack.getBackPack().backPackController.fulled();
        }
        else if (commandNum == 2){
            ToolShelfController toolShelfController = (ToolShelfController)kitchen.getToolShelf().inspectableController;
            addMenu(toolShelfController);
        }
        else if (commandNum == 3){
            addMenu(kitchen.getRecipes().inspectableController);
            //kitchen.getRecipes().inspectableController.showMenu();
        }
        else
            System.out.println("Invalid Input!");//just for test....view should handle it
    }

    public boolean cook(){
        System.out.println("COOK!");
        RecipeController recipeController = (RecipeController)menus.peek();
        ArrayList<String> toolsNeeded = recipeController.getRecipe().getTools();
        for (int i = 0; i < toolsNeeded.size(); i++) {
            if (!kitchen.getToolShelf().hasFixed(toolsNeeded.get(i))) {
                return false;
            }
        }

        HashMap<String, Integer> ingredientHashMap = recipeController.getRecipe().getIngredients();
        if (BackPack.getBackPack().backPackController.takeMultipleItems(ingredientHashMap))
            BackPack.getBackPack().addItem(recipeController.getRecipe().getFood());
        //report if broken(generate probability)
        return true;
    }
}

class Kitchen extends Inspectable {
    public ToolShelf getToolShelf() {
        return toolShelf;
    }
    private ToolShelf toolShelf = new ToolShelf();
    private Recipes recipes = Holder.recipes;
    public Kitchen(){
        setName("Kitchen");
        setCommands("Cook a meal", "Check Tool Shelf", "Check recipes");
        inspectableController = new KitchenController(this);
    }

    public Recipes getRecipes() {
        return recipes;
    }

    public void setRecipes(Recipes recipes) {
        this.recipes = recipes;
    }

//        public ToolShelf getToolShelf() {
//        return toolShelf;
//    }

}


//class ToolShelf extends Inspectable {
//    public ArrayList<Utensil> getKitchenTools() {
//        return kitchenTools;
//    }
//
//    private ArrayList<Utensil> kitchenTools = new ArrayList<>(); // boolean -> isBought
//    private int itemNums = 5;
//    ToolShelf() {
//        setName("Tool Shelf");
//        // add kitchen tools to the arraylist
//        inspectableController = new ToolShelfController(this);
//        setCommands();
//    }
//
//    @Override
//    public void setCommands(String... commands) {
//        this.commands = new ArrayList<>();
//        String[] cmds = new String [itemNums];
//        for (int i = 0; i < itemNums; i++)
//            cmds[i] = kitchenTools.get(i).getName() + " isBought: " + kitchenTools.get(i).isBought();
//        super.setCommands(cmds);
//    }
//
//    public void buy(Utensil utensil) {
//        utensil.buy();
//        setCommands();
//    }
//}
//
//class ToolShelfController extends InspectableController {
//    ToolShelf toolShelf;
//
//    ToolShelfController(ToolShelf toolShelf) {
//        super(toolShelf);
//        this.toolShelf = toolShelf;
//    }
//
//    @Override
//    public void runMethods(int commandNum) {
//        Utensil utensil = toolShelf.getKitchenTools().get(commandNum - 1);
//        if (utensil.isBought())
//            addMenu(utensil.inspectableController);
//        else {
//            Utensil utensil1 = (Utensil) BackPackController.getItemFromBackPack(utensil.getName(), 1);
//            if (utensil1 == null)
//                return;
//            toolShelf.buy(utensil);
//        }
//    }
//
//}

class ToolShelf extends Inspectable {
    public ArrayList<Utensil> getKitchenTools() {
        return kitchenTools;
    }

    private ArrayList<Utensil> kitchenTools = new ArrayList<>(); // boolean -> isBought
    private int itemNums = 5;
    ToolShelf() {
        setName("Tool Shelf");
        kitchenTools.add(Holder.fryingPan); //TODO - clone
        kitchenTools.add(Holder.knife);
        kitchenTools.add(Holder.mixer);
        kitchenTools.add(Holder.pot);
        kitchenTools.add(Holder.stove);

        inspectableController = new ToolShelfController(this);
        setCommands();
    }

    @Override
    public void setCommands(String... commands) {
        this.commands = new ArrayList<>();
        String[] cmds = new String [itemNums];
        for (int i = 0; i < itemNums; i++)
            cmds[i] = kitchenTools.get(i).getName() + " -> has utensil: " + kitchenTools.get(i).isBought();
        super.setCommands(cmds);
    }

    public void buy(Utensil utensil) {
        Utensil oldUtensil = null;
        for (int i = 0; i < itemNums; i++)
            if (kitchenTools.get(i).getName().equals(utensil.getName())) {
                oldUtensil = kitchenTools.get(i);
                break;
            }
        if (oldUtensil == null)
            return;
        kitchenTools.remove(oldUtensil);
        kitchenTools.add(utensil);
        setCommands();
    }

    public boolean hasFixed(String itemName) {
        for (int i = 0; i < itemNums; i++) {
            if (kitchenTools.get(i).getName().equals(itemName) && kitchenTools.get(i).isBought())
                return true;
        }
        return false;
    }

}
class ToolShelfView extends InspectableView{

    public ToolShelfView(InspectableController inspectableController) {
        super(inspectableController);
    }
}
class ToolShelfController extends InspectableController {
    ToolShelf toolShelf;
    ToolShelfView toolShelfView = new ToolShelfView(this);
    ToolShelfController(ToolShelf toolShelf) {
        super(toolShelf);
        this.toolShelf = toolShelf;
        setView(toolShelfView);
    }

    @Override
    public void runMethods(int commandNum) {
        Utensil utensil = toolShelf.getKitchenTools().get(commandNum - 1);
        if (utensil.isBought())
            addMenu(utensil.inspectableController);
        else {
            Utensil utensil1 = (Utensil) BackPackController.getItemFromBackPack(utensil.getName(), 1);
            if (utensil1 == null)
                return;
            toolShelf.buy(utensil1);
        }
    }

}


class Utensil extends Tool {
    private boolean isBought = true;
    Utensil(String name, float breakProb) {
        super(name, "Utensil", false, 0);
        breakProbablility = breakProb;
        setCommands("Status",
                "Replace this tool",
                "Remove this tool");
        inspectableController = new UtensilController(this);
    }

    public boolean isBought() {
        return isBought;
    }

    @Override
    public void setStatus() {
        status = "A cooking utensil. ";
        if (isBroken())
            status += " Broken";
        else
            status += " unbroken";
        status += "Breaking probability: " + breakProbablility;
    }
    public void buy() {
        isBought = true;
    }
}

class UtensilController extends InspectableController {
    Utensil utensil;
    UtensilView utensilView = new UtensilView(this);
    UtensilController(Utensil utensil) {
        super(utensil);
        this.utensil = utensil;
        setView(utensilView);
    }

    @Override
    public void runMethods(int commandNum) {
        switch (commandNum) {
            case 1:
                utensil.getStatus();
                return;
            case 2:

        }
    }
}
class UtensilView extends InspectableView{

    public UtensilView(InspectableController inspectableController) {
        super(inspectableController);
    }
}
