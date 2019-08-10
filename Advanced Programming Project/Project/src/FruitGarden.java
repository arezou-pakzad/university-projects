import java.util.ArrayList;

public class FruitGarden extends Inspectable {

    private ArrayList<Tree> trees = new ArrayList<>();
    private final int treeNum = 6;
    FruitGarden() {
        setName("Fruit Garden");
        inspectableController = new FruitGardenController(this);
        trees = Holder.treesItem;
        setCommands();
    }

    public ArrayList<Tree> getTrees() {
        return trees;
    }

    @Override
    public void setCommands(String... commands) {
        this.commands = new ArrayList<>();
        for (int i = 0; i < treeNum; i++) {
            Tree tree = trees.get(i);
            if (tree.isBought())
                this.commands.add(tree.getName());
            else
                this.commands.add("Buy " + tree.getName());
        }
    }

    public void buyTree(Tree tree) {
        tree.buy();
        setCommands();
        Person.getPerson().takeMoney(tree.price);
    }
}

class FruitGardenController extends InspectableController {
    private FruitGarden fruitGarden;
    //    private InspectableView fruitGardenView = new InspectableView(this);
    FruitGardenController(FruitGarden fruitGarden) {
        super(fruitGarden);
        this.fruitGarden = fruitGarden;
        inspectableView = new InspectableView(this);
        setView(inspectableView);
    }

    @Override
    public void runMethods(int commandNum) {
        Tree tree = fruitGarden.getTrees().get(commandNum - 1);
        if (tree.isBought()) {
//            System.out.println(tree.getName() + " is bought. adding to stack");
            addMenu(tree.inspectableController);
        }
        else {
            if (buy(tree.getName(), tree.getPrice())) {
                fruitGarden.buyTree(tree);
                refreshScene();
            }
//            System.out.println("Tree bought");
        }
    }
}

class Tree extends Item {

    private Season season;
    private int fruitsNum = 0;
    private boolean isBought = false;
    private boolean wateredToday = false;
    private Eatable fruit;

    Tree(String name, Season season, int price, Eatable fruit) { //TODO - get price
        this.name = name;
        this.season = season;
        this.price = price;
        this.fruit = fruit;
        setCommands("Status",
                "Water this tree",
                "Collect fruits");
        inspectableController = new TreeController(this);
    }

    public Eatable getFruit() {
        return fruit;
    }

    public void setFruit(Eatable fruit) {
        this.fruit = fruit;
    }

    public boolean isBought() {
        return isBought;
    }

    public boolean isWateredToday() {
        return wateredToday;
    }

    public Season getSeason() {
        return season;
    }

    public int getFruitsNum() {
        return fruitsNum;
    }

    public void buy() {
        isBought = true;
    }

    public void waterTree() {
        wateredToday = true;
    }

    public void collectFruits() {
        BackPack backPack = BackPack.getBackPack();
        // new Fruit -> add fruit to tree fruitNum times :))
        fruitsNum = 0;
    }
}

class TreeController extends InspectableController {
    private Tree tree;
    private TreeView treeView = new TreeView(this);
    TreeController(Tree tree) {
        super(tree);
        this.tree = tree;
        setView(treeView);
    }

    @Override
    public void runMethods(int commandNum) {
        switch (commandNum) {
            case 1:
                showStatus();
                return;
            case 2:
                waterTree();
                return;
            case 3:
                collectFruits();
                return;
        }
    }

    private void collectFruits() {
        if (BackPack.getBackPack().isFulled()) {
            Popup.fullBackPack();
            return;
        }
        if (tree.getFruitsNum() == 0) {
            Popup.makePopup("There is no fruit under the tree");
            return;
        }
        tree.collectFruits();
    }

    private void waterTree() {
        WateringCan wateringCan = (WateringCan) BackPackController.getItemFromBackPack("Watering Can", 1);
        if (wateringCan == null)
            back();
        // TODO - check if the watering can is broken or doesn't have water
        tree.waterTree();
    }

    private void showStatus() {
        String status;
        boolean hasFruit = false;
        if (tree.getFruitsNum() > 0)
            hasFruit = true;
        status = "Season: " + tree.getSeason() + "\nWatered today: " + tree.isWateredToday()
                + "\nHas fruit: " + hasFruit;
        showStatus(status);
    }
}

class TreeView extends InspectableView {
    private TreeController treeController;

    public TreeView(TreeController treeController) {
        super(treeController);
        this.treeController = treeController;
    }

    public void noFruit() {
        System.out.println("There is no fruit under the tree");
    }
}