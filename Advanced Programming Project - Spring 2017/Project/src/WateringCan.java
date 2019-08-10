

class WateringCan extends Tool {
    private int waterLevel;

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    private int maxLevel;
//    private int level;//TODO levels must be difined
    private int fieldNum;
    public boolean broken;
    //TODO - modify the constructor!!!
//        WateringCan(int level, int maxLevel, int fieldNum) {
////            new Tool();
//            this.setMaxLevel(maxLevel);
////            this.level = level;
//            this.fieldNum = fieldNum;
//        }
    WateringCan(String name, String type, int power, int maxLevel, int fieldNum) {
        super(name, type, false, power);
        this.setMaxLevel(maxLevel);
        this.fieldNum = fieldNum;
        broken = false;
    }

    //waterringcan ro por mikone
    public void fill() {
        waterLevel = maxLevel;
    }


    //age wateringcan khali bashe false barmigardune
    public boolean water() {
        if (waterLevel > 0) {
            waterLevel--;
            return true;
        }
        return false;
    }
//        public void setLevel(int level) {
//            this.level = level;
//        }

    public int getWaterLevel() {
        return waterLevel;
    }

    public int getFieldNum() {
        return fieldNum;
    }

//    public int getLevel() {
//        return level;
//        }

    @Override
    public boolean check() {
        boolean result = super.check();
        if (!result)
            return false;
        if (waterLevel == 0) {
            ToolController toolController = (ToolController) inspectableController;
            toolController.empty();
            return false;
        }
        return true;
    }

    public void setStatus(){
        if (isBroken())
            status = "A " + type + "sprinkler. With this sprinkler you can water a " + maxLevel + " range in one go. It has the capacity to water " + fieldNum +" fields of the insteractables."
                    +"\nWater level: " + waterLevel + " of " + maxLevel + "\nEnergy required for each use: " + getEnergyReducer() + "\nBroken";
        else
            status = "A " + type + "sprinkler. With this sprinkler you can water a " + maxLevel + " range in one go. It has the capacity to water " + fieldNum +" fields of the insteractables."
                    +"\nWater level: " + waterLevel + " of " + maxLevel + "\nEnergy required for each use: " + getEnergyReducer() + "\nBroken";
    }
//        public int getLevel() {
//            return level;
//        }
}
