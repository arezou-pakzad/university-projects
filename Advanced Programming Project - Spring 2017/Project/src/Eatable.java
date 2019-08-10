
import java.util.ArrayList;
import java.util.Scanner;

class Eatable extends Product {

    private ArrayList<Stat> stats = new ArrayList<>();

    public Eatable(String name, String type, boolean isSpecial) {
        super(name, type, isSpecial);
    }

    public Eatable(String name, String type, int price, int health, int energy) {
        super(name, type, false);
        setPrice(price);
        // TODO - set stat changings for each eatable @Arezou
    }

    public void setStat(ArrayList<Stat> stats){
        this.stats = stats;
    }

    public ArrayList<Stat> getStats(){
        return this.stats;
    }

    @Override
    public void setStatus(){
        if (type.equals("Medicine"))
            this.status = "It heals " + stats.get(0).getCurrent() + " health points.";
        else if (type.equals("Ingredient"))
            this.status = "Can be used while cooking certain foods.";
        else
            this.status = "A " + name +"! It can be used while cooking.";
    }

    public void addStat(Stat stat){
        stats.add(stat);
    }

    public void use(){
        Person.getPerson().eat(this);
    }

    public static void buildCustom(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose it's type:");
        String type = scanner.nextLine();
        System.out.println("Enter a name:");
        String name = scanner.nextLine();
        //TODO-while reply is acceptable
        System.out.println("can you eat it anywhere?(Y/N)");
        String reply = scanner.next();
        boolean isSpecial = false;
        if (reply.equals('Y'))
            isSpecial = true;
        System.out.println("enter its price:");
        int price = scanner.nextInt();
        System.out.println("enter stat information:");
        System.out.print("health current: ");
        int healthCurrent = scanner.nextInt();
        System.out.print(" health maximum: ");
        int healthMax = scanner.nextInt();
        System.out.print(" health refill rate: ");
        int healthRefill = scanner.nextInt();
        System.out.print(" health consumption rate: ");
        int healthConsumption = scanner.nextInt();
        System.out.println("\n");

        System.out.print("energy current: ");
        int energyCurrent = scanner.nextInt();
        System.out.print(" energy maximum: ");
        int energyMax = scanner.nextInt();
        System.out.print(" energy refill rate: ");
        int energyRefill = scanner.nextInt();
        System.out.print(" energy consumption rate: ");
        int energyConsumption = scanner.nextInt();
        Stat energy = new Stat("Energy");
        Stat health = new Stat("Health");
        energy.setCurrent(energyCurrent);
        energy.setMaximum(energyMax);
        energy.setRefillRate(energyRefill);
        energy.setConsumptionRate(energyConsumption);

        health.setCurrent(healthCurrent);
        health.setMaximum(healthMax);
        health.setRefillRate(healthRefill);
        health.setConsumptionRate(healthConsumption);

        ArrayList features = new ArrayList<>();
        features.add(name);
        features.add(type);
        features.add(isSpecial);
        features.add(energy);
        features.add(health);
        features.add(price);
        Holder.customEatables.put(name, features);
    }
}