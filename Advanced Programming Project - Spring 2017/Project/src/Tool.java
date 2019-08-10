import java.util.ArrayList;
import java.util.HashMap;

public class Tool extends Item {

    private boolean isBroken = false;
    protected float breakProbablility;
    private ArrayList<Stat> stats;
    private ArrayList<Item> madeOf;
    private int repairMoney;
    private HashMap<Item , Integer> repairNeeded;
    private HashMap<Item , Integer> itemNeeded;
    private int EnergyReducer;
    private int power;
    public Tool(String name, String type, boolean isSpecial,int power) {
        super(name, type, isSpecial);
        this.power = power;
        inspectableController = new ToolController(this);
    }
    public void setPower(int power) {
        this.power = power;
    }

    public int getRepairMoney() {
        return repairMoney;
    }

    public void setRepairMoney(int repairMoney) {
        this.repairMoney = repairMoney;
    }



    public HashMap<Item, Integer> getRepairNeeded() {
        return repairNeeded;
    }

    public void setRepairNeeded(HashMap<Item, Integer> repairNeeded) {
        this.repairNeeded = repairNeeded;
    }


    public int getItemNeededSize() {
        int num = 0;
        for(Item item : itemNeeded.keySet()){
            num += itemNeeded.get(item);
        }
        return num;
    }
    public void setEnergyReducer(int energyReducer) {
        EnergyReducer = energyReducer;
    }

    public HashMap<Item, Integer> getItemNeeded() {
        return itemNeeded;
    }

    public void setItemNeeded(HashMap<Item, Integer> itemNeeded) {
        this.itemNeeded = itemNeeded;
    }

    public int getEnergyReducer() {
        return EnergyReducer;
    }

    public void setBroken(boolean broken) {
        isBroken = broken;
    }

    public void setStats(ArrayList<Stat> stats) {
        this.stats = stats;
    }

    public void setMadeOf(ArrayList<Item> madeOf) {
        this.madeOf = madeOf;
    }

    public boolean isBroken() {

        return isBroken;
    }

    public int getPower() {
        return power;
    }

    public ArrayList<Stat> getStats() {
        return stats;
    }

    public ArrayList<Item> getMadeOf() {
        return madeOf;
    }
    public void reportBroken(String name) {

    }

    public boolean check() {
        if (isBroken) {
            ToolController toolController = (ToolController) inspectableController;
            toolController.broken();
            return false;
        }
        return true;
    }

    public void setStatus(){
        if (isBroken())
            status = "A " + type + " " + name + "." + "\nEnergy required for each use: " + getEnergyReducer() + "\n Broken";
        else
            status = "A " + type + " " + name + "." + "\nEnergy required for each use: " + getEnergyReducer() + "\nNot broken";
    }

}
class ToolView extends ItemView{
    private ToolController toolController;

    public ToolView(ToolController toolController) {
        super(toolController);
        this.toolController = toolController;
    }

    public void printTheDetail(String staus){
        System.out.println(staus);
    }

    public void showBroken(String toolName) {
        System.out.println(toolName + " is Broken");
    }

    public void showEmpty(String name) {
        System.out.println(name + " is empty");
    }
}
class ToolController extends ItemController{
    private Tool tool;
    private ToolView toolView = new ToolView(this);
    public ToolController(Tool tool1){
        super(tool1);
        inspectableView = toolView;
        tool = tool1;

    }
    public void runMethods(int commandNum){
        if(commandNum <= 0 || commandNum > tool.getCommands().size()){
          toolView.invalidInput();
        }
        else{
            if(commandNum == 1){
              //un tabe hashmape tu backpack
            }
        }
    }

    public void broken() {
        toolView.showBroken(tool.name);
    }

    public void empty() {
        toolView.showEmpty(tool.name);
    }
}