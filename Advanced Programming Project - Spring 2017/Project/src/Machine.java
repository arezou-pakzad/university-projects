import java.util.ArrayList;
import java.util.HashMap;

public class Machine extends Tool {
    HashMap<Item , Integer> inputs = new HashMap<>();
    HashMap<Product, Integer> outputs = new HashMap<>();
    //be gomanam machine ha, isS
    public Machine(String name) {
        super(name, "Machine", false, 0);
        inspectableController = new MachineController(this);
        setCommands("Status", "Use this Machine");

    }

    public void setInputs(HashMap<Item, Integer> inputs) {
        this.inputs = inputs;
    }

    public HashMap<String, Integer> getInputs() {
        HashMap<String, Integer> inputNames = new HashMap<>();
        for (Item item : inputs.keySet())
            inputNames.put(item.getName(), inputs.get(item));
        return inputNames;
    }

    public HashMap<Product, Integer> getOutputs() {
        return outputs;
    }

    public void setOutputs(HashMap<Product, Integer> outputs) {
        this.outputs = outputs;
    }

    @Override
    public void use(){
        for (Product product:outputs.keySet())
            BackPack.getBackPack().addItem(product, outputs.get(product));
    }

    public void use(ArrayList<Eatable> eatables){
        Stat juiceEnergy = new Stat("Energy");
        Stat juiceHealth = new Stat("Health");
        float energy = 0;
        float health = 0;
        String juiceName = eatables.get(0).getName();
        for (int i = 0; i < eatables.size(); i++)
        {
            energy += eatables.get(i).getStats().get(0).getCurrent();
            health += eatables.get(i).getStats().get(1).getCurrent();
            if (i > 0)
                juiceName = juiceName + eatables.get(i).getName();
        }
        energy /= eatables.size();
        health /= eatables.size();
        juiceName = juiceName + " Juice";

        juiceEnergy.setCurrent(energy);
        juiceHealth.setCurrent(health);
        ArrayList<Stat> juiceStats = new ArrayList<>();
        juiceStats.add(juiceEnergy);
        juiceStats.add(juiceHealth);

        Eatable juice = new Eatable(juiceName, "Juice", true);
        BackPack.getBackPack().addItem(juice);
    }
    public void setDescription(String description){
        this.description = description;
    }

    public void setStatus(String description){
        this.status = description;
    }
}
class MachineController extends InspectableController {
    private Machine machine;
    private MachineView machineView = new MachineView(this);
    //beza bebinam be view ehtiaji peida mikonam ya na
    MachineController(Machine machine) {
        super(machine);
        this.machine = machine;
        setView(machineView);
    }

    @Override
    public void runMethods(int commandNum){
        if(commandNum <= 0 || commandNum > machine.getCommands().size()){
            machineView.invalidInput();
        }
        else{
            if(commandNum == 1){
                //machineView.showStatus(machine.getDescription() , machine.getName());
                Popup.showStatus(machine);
            }
            else if(commandNum == 2 && !machine.getName().equals("Juicer")){
                HashMap<String, Integer> inputHashMap = machine.getInputs();
                if (BackPack.getBackPack().backPackController.takeMultipleItems(inputHashMap))
                    machine.use();
            }
            else if (commandNum == 2 && machine.getName().equals("Juicer")){
                Eatable fruit = (Eatable) BackPack.getBackPack().backPackController.getItemFromBackPack("Fruit");
                if (fruit == null)
                    return;
                ArrayList<Eatable> input = new ArrayList<Eatable>();
                input.add(fruit);
                machine.use(input);
            }
        }
    }

}
class MachineView extends InspectableView {
    private MachineController machineController;

    public MachineView(MachineController machineController) {
        super(machineController);
        this.machineController = machineController;
    }

    public void showStatus(String status , String name){
        System.out.println(name + ":");
        System.out.println(status);
    }
    public void invalidInput(){
        System.out.println("Invalid Target");
    }
}
