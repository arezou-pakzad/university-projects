import java.util.HashMap;

class Greenhouse extends Place {
    private boolean isRepaired = false;
    private int repairMoney = 2000;
    private HashMap<Item, Integer> repairMaterials = new HashMap<>();

    {
        repairMaterials.put(Holder.getOakLumber(), 20);
        repairMaterials.put(Holder.getIronOre(), 20);
    }
    private Fields fields = new Fields(4);
    private WeatherMachine weatherMachine = new WeatherMachine(this, "Weather Machine", "Weather Machine", true, 1);

    Greenhouse() {
        super("Greenhouse");
        setInspectableObjects(weatherMachine, fields);
        setRepairMaterials();
        placeController = new PlaceController(this) {
            @Override
            void showMenu() {
                System.out.println("1. Repair GreenHouse"); // TODO - create a method in view
            }

            @Override
            int scan() {
                command = scanner.nextLine();
                if (!command.equals("1"))
                    return -1;
                HashMap<String, Integer> materials = getMaterials();
                if (materials != null) {
                    repair();
                }
                else if (materials == null) {
                    HashMap<String, Integer> repairmaterials = new HashMap<>();
                    Person.getPerson().putMoney(3000);
                    repairmaterials.put("Gil", repairMoney);
                    for (Item item :
                            repairMaterials.keySet()) {
                        repairmaterials.put(item.getName(), repairMaterials.get(item));
                    }
                    placeView.notEnoughMaterials(repairmaterials, this.place.getName());
                    back();
                }
                return 1;
            }
        };
    }

    public Fields getFields() {
        return fields;
    }

    public void setRepairMaterials() {
//        repairMaterials.put(Holder.oldLumer, 20);
//        repairMaterials.put(Holder.adamantiumOre, 20);
    }

    void repair() {
        isRepaired =true;
        Person.getPerson().takeMoney(repairMoney);
        BackPack backPack = BackPack.getBackPack();
        for (Item item : repairMaterials.keySet()) {
            backPack.removeItem(item, repairMaterials.get(item));
        }
        placeController.back();
        placeController = new PlaceController(this);
        Controller.addMenu(placeController);
    }

    private HashMap<String, Integer> getMaterials() { // creating <String, Integer> HashMap from repairMaterials
        boolean hasMaterial = true;
        if (Person.getPerson().getMoney() < repairMoney)
            hasMaterial = false;
        BackPack backPack = BackPack.getBackPack();
        for (Item item :
                repairMaterials.keySet()) {
            if (backPack.itemNum(item) < repairMaterials.get(item)) {
//                System.out.println(item.getName() + " " + repairMaterials.get(item));
                hasMaterial = false;
                break;
            }
        }
        if (!hasMaterial)
            return null;
        HashMap<String, Integer> materials = new HashMap<>();
        Person.getPerson().putMoney(3000);
        materials.put("Gil", repairMoney);
        for (Item item:
                repairMaterials.keySet()) {
            materials.put(item.getName(), repairMaterials.get(item));
        }
        return materials;
    }

    public boolean isRepaired() {
        return isRepaired;
    }

//    public Machine getWeatherMachine() {
//        return weatherMachine;
//    }
}

