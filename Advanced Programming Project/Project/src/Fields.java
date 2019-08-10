import java.util.ArrayList;

public class Fields extends Inspectable {
    public ArrayList<Field> getFields() {
        return fields;
    }

    private ArrayList<Field> fields = new ArrayList<>();
    int MAX_FIELDS = 9;
    Fields() {
        setName("Fields");
        inspectableController = new FieldsController(this);
        for (int i = 0; i < MAX_FIELDS; i++)
            fields.add(new EmptyField(this));
        setCommands();
    }
    Fields(int fieldsNum) {
        setName("Fields");
        inspectableController = new FieldsController(this);
        MAX_FIELDS = fieldsNum;
        for (int i = 0; i < MAX_FIELDS; i++)
            fields.add(new EmptyField(this));
        setCommands();
    }
    public void setCommands() {
        String[] fieldsNames = new String[MAX_FIELDS];
        for (int i = 0; i < fields.size(); i++) {
            fieldsNames[i] = fields.get(i).getName();
            System.out.println(fieldsNames[i]);
        }
        setCommands(fieldsNames);
    }

    public Field getField(int fieldNum) {
        return fields.get(fieldNum);
    }

    public void setSeason(Season season) {
        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).setSeason(season);
        }
    }
    public void destroyCrops(Field field) {
        int index = fields.indexOf(field);
        fields.remove(index);
        fields.add(index, new EmptyField(this));
    }
    public void plantSeed(Field field, Crop seed) {
        int index = fields.indexOf(field);
        fields.remove(index);
        seed.plant();
        fields.add(index, new FullField(this, seed)); //TODO - change name when it is planted
        setCommands();
    }

    public void nextDay() {
        for (int i = 0; i < MAX_FIELDS; i++) {
            if (fields.get(i) instanceof FullField) {
                FullField fullField = (FullField) fields.get(i);
                fullField.getCrop().nextDay();
            }
        }
    }
}

class FieldsController extends InspectableController {
    private Fields fields;
    FieldsController(Fields fields) {
        super(fields);
        this.fields = fields;
        setView(new InspectableView(this));
    }

    @Override
    public void showMenu() {
        fields.setCommands();
        super.showMenu();
    }

    @Override
    public void runMethods(int commandNum) {
        Field field = fields.getField(commandNum - 1);
        addMenu(field.inspectableController);
    }
}

class Field extends Inspectable {
    private int fieldsNum = 3 * 3;
    protected Fields fields;
    private boolean wateredToday;
    private Season season;

    //    private ArrayList<Task> tasks;
    Field(Fields fields) { // decide if it gets the crop or seed or ...?
        this.fields = fields;
        inspectableController = new FieldController(this);
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Fields getFields() {
        return fields;
    }

    public boolean isWateredToday() {
        return wateredToday;
    }

    public void waterThisField() {
        wateredToday = true;
        // TODO - change the level of water in watering can
    }
}

class FullField extends Field {
    private final Crop crop;
    private final static int cropNum = 9;
    FullField(Fields fields, Crop crop) {
        super(fields);
        this.crop = crop;
        setName(crop.getName() + " Field");
        setCommands("Status",
                "Water this field",
                "Harvest crops",
                "Destroy crops");
        inspectableController = new FullFieldController(this);
    }
    public Crop getCrop() {
        return crop;
    }

    public void harvest() {
        for (int i = 0; i < cropNum; i++) { // TODO - add a method to storage that gets item and number
            BackPack.getBackPack().addItem(crop);
        }
        fields.destroyCrops(this);
    }
}

class EmptyField extends Field {
    private boolean plowed = false;

    EmptyField(Fields fields) {
        super(fields);
        setName("Empty Field");
        setCommands("Status",
                "Plow this field",
                "Water this field",
                "Plant seeds");
        inspectableController = new EmptyFieldController(this);
    }

    public boolean isPlowed() {
        return plowed;
    }

    public void plow() {
        plowed = true;
    }

}

class FieldController extends InspectableController {
    protected Field field;
    protected FieldView fieldView = new FieldView(this);
    FieldController(Field field) {
        super(field);
        this.field = field;
        setView(fieldView);
    }
    void waterThisField() {
        WateringCan wateringCan = (WateringCan) BackPackController.getItemFromBackPack("Watering Can", 1);
        //TODO - if the watering can was empty
//        if (wateringCan.isBroken())
//            ;///
//        if (wateringCan.getWaterLevel() == 0)
//            ////
        field.waterThisField();
    }

}

class FullFieldController extends FieldController {
    private FullField fullField;
    FullFieldController(FullField fullField) {
        super(fullField);
        this.fullField = fullField;
        //setInspectableView
    }

    void showStatus() {
        String status;
        String daysUntilFullGrowth;
        String daysUntilSpoilage = null;
        String cropHarvestsLeft;
        if (fullField.getCrop().getDaysToGrow() == 0)
            daysUntilFullGrowth = "Fully grown!";
        else
            daysUntilFullGrowth = fullField.getCrop().getDaysToGrow() + " day(s)";
        if (fullField.getCrop().getDaysToSpoilage() == 0)
            daysUntilSpoilage = "Spoiled!"; //TODO - command??
        else
            daysUntilSpoilage = fullField.getCrop().getDaysToSpoilage() + " day(s)";
        if (fullField.getCrop().getHarvestLeft() == 0)
            cropHarvestsLeft = "Harvested"; //TODO - command
        else
            cropHarvestsLeft = fullField.getCrop().getHarvestLeft() + " time(s)";
        status = "Crop Type: " + fullField.getCrop().getName()
                + "\nCrop season: " + fullField.getCrop().getSeason()
                + "\nDays until full growth: " + daysUntilFullGrowth
                + "\nWatered today: " + fullField.isWateredToday()
                + "\nDays until spoilage: " + daysUntilSpoilage
                + "\nCrop harvests left: " + cropHarvestsLeft;
        Popup.makePopup(status);
    }
    @Override
    public void runMethods(int commandNum) {
        switch (commandNum) {
            case 1:
                showStatus();
                return;
            case 2:
                waterThisField();
                return;
            case 3:
                HarvestCrops();
                return;
            case 4:
                destroyCrops();
                return;
        }
    }

    private void destroyCrops() {
        Fields fields = field.getFields();
        fields.destroyCrops(field);
        Farm.farm.getFields().inspectableController.refreshScene();
        Farm.farm.placeController.refreshScene();
    }

    private void HarvestCrops() {
        if (BackPack.getBackPack().isFulled()) {
            fieldView.fullBackPack();
            return;
        }
        if (fullField.getCrop().getDaysToGrow() != 0) {
            fieldView.notFullyGrown();
            return;
        }
        fullField.harvest();
        Farm.farm.getFields().inspectableController.refreshScene();
        Farm.farm.placeController.refreshScene();
        back();
    }
}

class EmptyFieldController extends FieldController {
    private EmptyField emptyField;
    EmptyFieldController(EmptyField emptyField) {
        super(emptyField);
        this.emptyField = emptyField;
        //setInspectableView
    }

    void showStatus() {
        String status;
        status = "Plowed: " + emptyField.isPlowed() + "\nWatered Today: " + emptyField.isWateredToday();
        showStatus(status);
    }

    @Override
    public void runMethods(int commandNum) {
        switch (commandNum) {
            case 1:
                showStatus();
                return;
            case 2:
                plowField();
                return;
            case 3:
                waterThisField();
                return;
            case 4:
                plantSeeds();
                return;
        }
    }

    private void plantSeeds() {
        if (!emptyField.isPlowed()) {
            fieldView.notPlowed();
            return;
        }
        Crop seed = (Crop) BackPackController.getItemFromBackPack("Seed", "Crop");
        if (seed == null)
            return;
        Fields fields = field.getFields();
        fields.plantSeed(field, seed);
        Farm.farm.getFields().inspectableController.refreshScene();
        Farm.farm.placeController.refreshScene();
        back();
    }

    private void plowField() {
        Tool shovel = (Tool) BackPackController.getItemFromBackPack("Shovel", 1);
        if (shovel == null) {
            System.out.println("shovel is null");
            return;
        }
        //TODO - check if it is broken
        emptyField.plow();
        Farm.farm.placeController.refreshScene();
    }
}

enum Season {
    Spring,
    Summer,
    Autumn,
    Winter,
    Tropical
}

class FieldView extends InspectableView {
    private FieldController fieldController;

    public FieldView(FieldController fieldController) {
        super(fieldController);
        this.fieldController = fieldController;
    }

    void notFullyGrown() {
        Popup.makePopup("Crops are not fully grown yet");
    }
    void notPlowed() {

        Popup.makePopup("Field is not plowed");

    }
}

class Crop extends Item {
    private Season season;
    private int daysToGrow;
    private int daysToSpoilage = 3;
    private int harvestLeft;
    private boolean planted = false;
    private final int growDays = 3; //tedad roozayi ke tool mikeshe ta fully grown beshe
    private Eatable product; //TODO - set product for each item
    Crop(String name, Season season, int harvestLeft) {
        daysToGrow = growDays;
        inspectableController = new InspectableController(this); //TODO - modify later
        this.name = name;
        this.season = season;
        this.harvestLeft = harvestLeft;
        this.type = "Crop";
    }
    public void plant() {
        planted = true;
    }

    @Override
    public String getName() {
        if (!planted)
            return "Seed";
        else
            return name;
    }

    public Season getSeason() {
        return season;
    }
    public int getDaysToGrow() {
        return daysToGrow;
    }

    public int getDaysToSpoilage() {
        return daysToSpoilage;
    }

    public int getHarvestLeft() {
        return harvestLeft;
    }

    //    private ArrayList<Task> tasks;
    //    private ArrayList<FruitVegetable> products;

    @Override
    public int getPrice() {
        if (!planted)
            return price * 5;
        else
            return price;
    }

    public void nextDay() {
        if (daysToSpoilage == 0) {
            product = Holder.getJunk();
            return;
        }
        if (daysToGrow == 0) {
            daysToSpoilage--;
            return;
        }
        daysToGrow--;
    }
}