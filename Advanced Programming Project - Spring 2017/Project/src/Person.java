import java.util.ArrayList;
import java.util.Stack;

public class Person {

    private ArrayList<Stat> stats = new ArrayList<>();//0->Energy 1->Health
    private String name = "Farmer";
    public BackPack backPack;
    private int money;
    private static Person person = new Person();
    //	private ArrayList<Task> tasks;
    private Place place;
    private Stack<Expressible> menu;
    {
        Stat energy = new Stat("Energy");
        Stat health = new Stat("Health");
        energy.setCurrent(1000);
        energy.setMaximum(1000);
        energy.setRefillRate(100);
        energy.setConsumptionRate(100);
        health.setCurrent(500);
        health.setMaximum(500);
        health.setConsumptionRate(100);
        health.setRefillRate(100);
        stats.add(energy);
        stats.add(health);

        money = 5000;
    }

    public void setPlace(Place place) {
        this.place = place;
        Stat energyToGo = new Stat("Energy");
        energyToGo.setCurrent(-10);
        this.changeStats(energyToGo);
    }

    public Place getPlace() {
        return place;
    }

    public void setStat(Stat stat) {
        this.stats.add(stat);
    }

    public static Person getPerson() {
        return person;
    }

    public ArrayList<Stat> getStats() {
        return stats;
    }

    public Stat getSingleStat(Stat stat){
        for (int i = 0; i < stats.size(); i++)
            if (stats.get(i).getName().equals(stat.getName()))
                return stats.get(i);
        return null;
    }

    public void changeStats(Stat stat){
        for (int i = 0;stats != null && i < stats.size() ; i++)
        {
            int res = stats.get(i).changeStat(stat);
            if (res == -1) {
                this.zeroEnergy();
                break;
            }
            else if (res == -2) {
                this.zeroHealth();
                break;
            }
        }
        this.updateEnergyMaxLimit();
    }

    private void zeroEnergy(){
        float maxE = stats.get(0).getMaximum();
        float currentEnergy = (3 * maxE) / 4;
        stats.get(0).setCurrent(currentEnergy);

        float maxH = stats.get(1).getMaximum();
        float healthDecrease = (-1 * maxH) / 4;
        Stat health = new Stat("Health");
        health.setCurrent(healthDecrease);
        this.changeStats(health);

        this.sleep();
        //TODO:faint???
    }

    private void zeroHealth(){
        float maxE = stats.get(0).getMaximum();
        float currentEnergy = maxE / 2;
        stats.get(0).setCurrent(currentEnergy);

        float maxH = stats.get(1).getMaximum();
        float currentHealth = maxH / 2;
        stats.get(1).setCurrent(currentHealth);

        for (int i = 1; i <= 7; i++)
            sleep();
        // TODO - sleep for 7 days!!!
    }

    private void updateEnergyMaxLimit(){
        if(stats != null){
            Stat energy = stats.get(0);
            Stat health = stats.get(1);
            float maxLimit = ((energy.getMaximum() / 2) * (health.getCurrent() / health.getMaximum())) + (energy.getMaximum() / 2);
            if (energy.getCurrent() > maxLimit)
                energy.setCurrent(maxLimit);
        }}

    public void sleep(){
        //TODO : sleep
        Stat energy = new Stat("Energy");
        energy.setCurrent(stats.get(0).getMaximum());
        changeStats(energy);
    }

    public Place whereAmI() {
        return this.place;
    }

    public void heal() {
        for (int i = 0; i < stats.size(); i++)
            if (stats.get(i).getName().equals("Health"))
                stats.get(i).heal();
    }

    public void setBackPack(BackPack backPack) {
        this.backPack = backPack;
    }

    public void putMoney(int money) {
        this.money += money;
    }

    public boolean takeMoney(int money) {
        if (money < this.money) {
            this.money -=money;
            return true;
        }
        return false;
    }

    public int getMoney() {
        return money;
    }

    public void eat(Eatable food){
        for (int i = 0; i < food.getStats().size(); i++)
            changeStats(food.getStats().get(i));
    }

    public void setStats(ArrayList<Stat> stats) {
        this.stats = stats;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public static void setPerson(Person person) {
        Person.person = person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Stat {

    private String name;
    private float maximum;
    private float current;
    private float refillRate;
    private float consumptionRate;
    private String type;
//	private ArrayList<Task> tasks;

    Stat(String name){
        this.setName(name);
    }

    public int changeStat(Stat stat) {
        //TODO- shart haye ezafe kardan barresi shavand
        if (stat.getName().equals(this.name)){
            if (stat.getMaximum() != 0)
                this.maximum += stat.getMaximum();
            if (stat.getRefillRate() != 0)
                this.refillRate += stat.getRefillRate();
            if (stat.getConsumptionRate() != 0)
                this.consumptionRate += stat.getConsumptionRate();
            if (stat.getCurrent() != 0)
                return this.addStat(stat.getCurrent());
        }
        return 0;
    }

    //-2 -> zeroHealth / -1 -> zeroEnergy / 0 -> one of them changed or nothing happened
    public int addStat(float nextStat)
    {
        if ((this.current + nextStat) >= this.maximum)
            this.current = maximum;
        else
            this.current += nextStat;
        if (this.current <= 0)
        {
            this.current = 0;
            if (this.name.equals("Energy"))
                return -1;
            else if (this.name.equals("Health"))
                return -2;
        }
        return 0;
    }

    public boolean canImproveMax() {
        if (name.equals("Energy") && maximum >= 2000)
            return false;
        if (name.equals("Health") && maximum >= 1000)
            return false;
        return true;
    }

    public boolean canImproveRefill() {
        if (refillRate >= 200)
            return false;
        return true;
    }

    public boolean canImproveConsumption() {
        if (consumptionRate > 0 && consumptionRate <= 50)
            return false;
        return true;
    }

    public void heal(){
        this.current = maximum;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setMaximum(float maximum){
        this.maximum = maximum;
    }

    public void setCurrent(float current){
        this.current = current;
    }

    public void setRefillRate(float refillRate){
        this.refillRate = refillRate;
    }

    public void setConsumptionRate(float consumptionRate){
        this.consumptionRate = consumptionRate;
    }

    public String getName(){
        return this.name;
    }

    public float getMaximum(){
        return this.maximum;
    }

    public float getCurrent(){
        return this.current;
    }

    public float getRefillRate(){
        return this.refillRate;
    }

    public float getConsumptionRate(){
        return this.consumptionRate;
    }

    public void setType(String type){
        this.type= type;
    }

    public String getType() {
        return type;
    }
}
