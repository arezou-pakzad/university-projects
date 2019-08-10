import java.util.ArrayList;

class Laboratory extends Inspectable {
    private ArrayList<Item> availableMachines = Holder.laboratoryItems;
    private List machineList = new List("Machines", availableMachines);
    public Laboratory() {
        setName("Laboratory");
        setCommands("Check machines", "Build a machine");
        inspectableController = new LaboratoryController(this);
    }

    public void buyMachine(Machine machine) {
        for (int i = 0; i < availableMachines.size(); i++){
            if (machine.getName().equals(availableMachines.get(i).getName())){
                availableMachines.remove(i);
                MachinePlace.getMachinePlace().addMachine(machine);
                break;
            }

        }
        MachinePlace.getMachinePlace().setCommands();
        for (int i = 0; i < MachinePlace.getMachinePlace().getMachines().size(); i++) {
            System.out.println(MachinePlace.getMachinePlace().getMachines().get(i).getName());
        }
    }

    public ArrayList<Item> getAvailableMachines() {
        return availableMachines;
    }

    public void setAvailableMachines(ArrayList<Item> availableMachines) {
        this.availableMachines = availableMachines;
    }

    public List getMachineList() {
        return machineList;
    }

    public void setMachineList(List machineList) {
        this.machineList = machineList;
    }

    public void addMachine(Machine machine){
        availableMachines.add(machine);
    }

    public void removeMachine(Machine machine){
        availableMachines.remove(machine);
    }
}

class LaboratoryController extends InspectableController {
    Laboratory laboratory;
    LaboratoryView labroratoryView = new LaboratoryView(this);
    public LaboratoryController(Laboratory laboratory) {
        super(laboratory);
        this.laboratory = laboratory;
        laboratory.getMachineList().setLaboratoryController(this);
        setView(labroratoryView);
    }

    @Override
    public void runMethods(int commandNum) {
        if (commandNum == 1) {
            InspectableController inspectableController = laboratory.getMachineList().inspectableController;
            addMenu(laboratory.getMachineList().inspectableController);
        }
        else if (commandNum == 2) {
            whichMethod = true;
            buyItem = true;
            labMethod = true;
            InspectableController inspectableController = laboratory.getMachineList().inspectableController;
            addMenu(inspectableController);
        }
    }

    public void buy(Machine machine) {
        if (Person.getPerson().getMoney() >= machine.getPrice() && BackPack.getBackPack().backPackController.takeMultipleItems(machine.getInputs())) {
            laboratory.buyMachine(machine);
            Person.getPerson().takeMoney(machine.getPrice());
        }
        else
            Popup.makePopup("You can't build this machine!");
    }
}

class LaboratoryView extends InspectableView {
    public LaboratoryView(InspectableController inspectableController) {
        super(inspectableController);
    }

    public void buildQuestion(String machineName, int machinePrice) {
        System.out.println("Do you want to build " + machineName + " for " + machinePrice + " Gil? (Y/N)");
    }
}
