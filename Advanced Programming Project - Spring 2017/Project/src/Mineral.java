import java.util.ArrayList;
import java.util.HashMap;

public class Mineral extends Product {
    public  int collectPower;
    private Tool tool;
    Mineral(String name, String type, int collectPower) {//, Tool tool) { //TODO - set tool for each mineral
        super(name, type, false);
        this.tool = tool;
        this.collectPower = collectPower;
    }
    public Tool getTool() {
        return tool;
    }
    public void setTool(Tool tool){
        this.tool = tool;
    }
    public void setStatus(){
        if(this.getType().equals("Branch")){
            status = " Weak tree branch. Can be used to create specific items at the workshop.";
        }
        else if(this.getType().equals("Old Lumber")){
            status = " Normal wood gotten from a “Name of the source tree” tree. Can be used to create specific items at the workshop";
        }
        else if(this.getType().equals("Pine Lumber")){
            status = " Strong wood gotten from a “Name of the source tree” tree. Can be used to create specific items at the workshop";
        }
        else if(this.getType().equals("Oak Lumber")){
            status = " Very Strong wood gotten from a “Name of the source tree” tree. Can be used to create specific items at the workshop";
        }
    }
}

class Minerals extends Inspectable {
    private HashMap<Mineral, Integer> minerals = new HashMap<>();
    private ArrayList<Mineral> mineralTypes = new ArrayList<>();

    Minerals(String name) {
        setName(name);
        inspectableController = new MineralsController(this);
    }

    public void setMineralTypes(Mineral... mineralTypes) {
        for (int i = 0; i < mineralTypes.length; i++) {
            this.mineralTypes.add(mineralTypes[i]);
            this.minerals.put(mineralTypes[i], 0);
            if(mineralTypes[i].getType().equals("Old Lumber")){
                mineralTypes[i].setTool(Holder.getStoneAxe());
            }
            else if(mineralTypes[i].getType().equals("Pine Lumber")){
                mineralTypes[i].setTool(Holder.getSilverAxe());
            }
            else if(mineralTypes[i].getType().equals("Oak Lumber")){
                mineralTypes[i].setTool(Holder.getIronAxe());
            }
            else if(mineralTypes[i].getType().equals("Iron Ore")){
                mineralTypes[i].setTool(Holder.getStonePickaxe());
            }
            else if(mineralTypes[i].getType().equals("Silver Ore")){
                mineralTypes[i].setTool(Holder.getSilverPickaxe());
            }
            else if (mineralTypes[i].getType().equals("Adamantium Ore")){
                mineralTypes[i].setTool(Holder.getAdamantiumPickaxe());
            }

        }
        String[] commands = new String[4];
        for (int i = 0; i < mineralTypes.length && mineralTypes[i] != null; i++)
            commands[i] = "Collect " + mineralTypes[i].getType();
        setCommands(commands);
    }

    public ArrayList<Mineral> getMineralTypes() {
        return mineralTypes;
    }

    public HashMap<Mineral, Integer> getMinerals() {
        return minerals;
    }

    public int collect(Mineral mineral) {
        //choose an mineral -> Tool from backpack
        if (!mineral.getType().equals("Branch") && !mineral.getType().equals("Stone")) {
            Tool tool = (Tool) BackPackController.getItemFromBackPack(mineral.getTool().getName(), mineral.getTool().getType());
            int number = Constants.specialCollector(tool.getPower(), mineral.collectPower, 2.5F);
            System.out.println(mineral.collectPower);
            BackPack.getBackPack().addItem(mineral, number);
            return number;
        }
        else{
            int number = Constants.collectStoneProbability();
            BackPack.getBackPack().addItem(mineral , number);
            return number;
        }
    }
}
class MineralsController extends InspectableController {
    Minerals minerals;
    MineralsView mineralsView = new MineralsView(this);
    MineralsController(Minerals minerals) {
        super(minerals);
        this.minerals = minerals;
        setView(mineralsView);
    }

    @Override
    public void runMethods(int commandNum) {
        Mineral mineral = minerals.getMineralTypes().get(commandNum - 1);
        String effects = " "; // TODO - EFFECTS???
        String pm = minerals.collect(mineral) + " "+ mineral.getType() + "were collected\nEffects: " + effects;
        Popup.makePopup(pm);
    }
}

class MineralsView extends InspectableView {
    private MineralsController mineralsController;

    public MineralsView(MineralsController mineralsController) {
        super(mineralsController);
        this.mineralsController = mineralsController;
    }

    void collect(int number, String nameType, String effects) {
        System.out.println(number + " " + nameType + "(s) were collected");
        System.out.println("Effects: " + effects);
    }
}
