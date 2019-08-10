
class Exercise {
    private String name;
    private int price;
    private int priceUpdate = 50;
    private int energyDecreaseUpdate = -50;
    private Stat energyDecrease = new Stat("Energy");
    private Stat exercise;

    public Exercise(String name,int price, float energyDec, Stat exercise) {
        this.price = price;
        this.energyDecrease.setCurrent(energyDec);
        this.exercise = exercise;
    }

    public void setUpdates(int priceUpdate, int energyDecreaseUpdate){
        this.priceUpdate = priceUpdate;
        this.energyDecreaseUpdate = energyDecreaseUpdate;
    }

    public Stat getExercise() {
        return exercise;
    }

    public void setExercise(Stat exercise) {
        this.exercise = exercise;
    }

    public void update(){
        price += priceUpdate;
        energyDecrease.setCurrent(energyDecrease.getCurrent() + energyDecreaseUpdate);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPriceUpdate() {
        return priceUpdate;
    }

    public void setPriceUpdate(int priceUpdate) {
        this.priceUpdate = priceUpdate;
    }

    public int getEnergyDecreaseUpdate() {
        return energyDecreaseUpdate;
    }

    public void setEnergyDecreaseUpdate(int energyDecreaseUpdate) {
        this.energyDecreaseUpdate = energyDecreaseUpdate;
    }

    public Stat getEnergyDecrease() {
        return energyDecrease;
    }

    public void setEnergyDecrease(Stat energyDecrease) {
        this.energyDecrease = energyDecrease;
    }
}
