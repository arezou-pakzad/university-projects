import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;

public class Holder {
    //Woods*********
    static Stat health = new Stat("Health");
    static Stat energy = new Stat("Energy");

    public static Mineral getBranch() {
        Mineral branch = new Mineral("Wood", "Branch", 1);
        branch.setPrice(20);
        return branch;
    }

    public static Mineral getOldLumber() {
        Mineral oldLumber = new Mineral("Wood", "Old Lumber", 2);
        oldLumber.setPrice(80);
        return oldLumber;
    }

    public static Mineral getPineLumber() {
        Mineral pineLumber = new Mineral("Wood", "Pine Lumber", 3);
        pineLumber.setPrice(250);
        return pineLumber;
    }

    public static Mineral getOakLumber() {
        Mineral oakLumber = new Mineral("Wood", "Oak Lumber", 4);
        oakLumber.setPrice(1000);
        return oakLumber;

    }
    //Rocks***********

    public static Mineral getStone() {
        Mineral stone = new Mineral("Rock", "Stone", 1);
        stone.setPrice(20);
        return stone;
    }

    public static Mineral getIronOre() {
        Mineral ironOre = new Mineral("Rock", "Iron Ore", 2);
        ironOre.setPrice(80);
        return ironOre;
    }

    public static Mineral getSilverOre() {
        Mineral silverOre = new Mineral("Rock", "Silver Ore", 3);
        silverOre.setPrice(250);
        return silverOre;
    }

    public static Mineral getAdamantiumOre() {
        Mineral adamantiumOre = new Mineral("Rock", "Adamantium Ore", 4);
        adamantiumOre.setPrice(1000);
        return adamantiumOre;
    }
    //Wool**************

    public static Product getWool() {
        Product wool = new Product("Wool", "Wool", false);
        wool.setDescription("Sheep wool. By using a spinning wheel you can turn it into a thread.");
        wool.setPrice(200);
        return wool;

    }
    //Thread************

    public static Product getThread() {
        Product thread = new Product("Thread", "Thread", false);
        thread.setDescription("Can be used to create specific items at the workshop.");
        thread.setPrice(300);
        return thread;
    }


    public static Eatable getAlfalfa() {
        Eatable alfalfa = new Eatable("Alfalfa", "Cow and Sheep Food", false);
        alfalfa.setPrice(20);
        alfalfa.setDescription("Food for Cows and Sheep");
        return alfalfa;
    }

    public static Eatable getSeed() {
        Eatable seed = new Eatable("Seed", "Chicken Food", false);
        seed.setDescription("Food for Chicken");
        seed.setPrice(10);
        return seed;
    }

    //Plant*************
    public static Eatable getPeach() {
        Eatable peach = new Eatable("Peach", "Fruit", true);

        health.setCurrent(10);
        energy.setCurrent(20);
        peach.setPrice(15);
        peach.addStat(health);
        return peach;
    }

    public static Eatable getPeer() {
        Eatable peer = new Eatable("Peer", "Fruit", true);
        health.setCurrent(15);
        energy.setCurrent(20);
        peer.setPrice(15);
        peer.addStat(energy);
        return peer;
    }

    public static Eatable getGarlic() {
        Eatable garlic = new Eatable("Garlic", "Vegetable", true);
        health.setCurrent(0);
        health.setMaximum(2);
        garlic.setPrice(15);
        garlic.addStat(health);
        return garlic;
    }

    public static Eatable getPea() {
        Eatable pea = new Eatable("Pea", "Vegetable", true);
        pea.setPrice(10);
        health.setCurrent(0);
        health.setMaximum(2);
        health.setMaximum(5);
        energy.setCurrent(0);
        energy.setMaximum(2);
        pea.addStat(energy);
        return pea;
    }

    public static Eatable getLettuce() {
        Eatable lettuce = new Eatable("Lettuce", "Vegetable", true);
        health.setCurrent(10);
        energy.setCurrent(20);
        lettuce.addStat(health);
        lettuce.setPrice(10);
        return lettuce;
    }

    public static Eatable getEggplant() {
        Eatable eggplant = new Eatable("Eggplant", "Vegetable", true);
        eggplant.setPrice(20);
        health.setCurrent(10);
        energy.setCurrent(20);
        eggplant.addStat(health);
        eggplant.addStat(energy);

        return eggplant;
    }

    public static Eatable getLemon() {
        Eatable lemon = new Eatable("Lemon", "Fruit", true);
        health.setCurrent(15);
        energy.setCurrent(20);
        lemon.setPrice(15);
        lemon.addStat(health);
        return lemon;
    }

    public static Eatable getPomegranate() {
        Eatable pomegranate = new Eatable("Pomegranate", "Fruit", true);
        energy.setCurrent(15);
        health.setCurrent(15);
        health.setMaximum(0);
        energy.setMaximum(5);
        pomegranate.addStat(energy);
        pomegranate.addStat(health);
        pomegranate.setPrice(25);
        return pomegranate;
    }

    public static Eatable getCucumber() {
        Eatable cucumber = new Eatable("Cucumber", "Vegetable", true);
        health.setCurrent(10);
        energy.setCurrent(20);
        cucumber.addStat(health);
        cucumber.setPrice(10);
        return cucumber;
    }

    public static Eatable getWatermelon() {
        Eatable watermelon = new Eatable("Watermelon", "Fruit", true);
        energy.setCurrent(15);
        health.setCurrent(15);
        health.setMaximum(0);
        energy.setMaximum(5);
        health.setCurrent(10);
        energy.setCurrent(50);
        energy.setMaximum(10);
        watermelon.addStat(energy);
        watermelon.addStat(health);
        watermelon.setPrice(80);
        return watermelon;
    }

    public static Eatable getOnion() {
        Eatable onion = new Eatable("Onion", "Vegetable", true);
        health.setCurrent(0);
        health.setMaximum(5);
        onion.addStat(health);
        onion.setPrice(15);
        return onion;
    }

    public static Eatable getTurnip() {
        Eatable turnip = new Eatable("Turnip", "Vegetable", true);
        energy.setCurrent(15);
        health.setCurrent(15);
        health.setCurrent(0);
        energy.setCurrent(0);
        health.setMaximum(3);
        energy.setMaximum(3);
        turnip.addStat(energy);
        turnip.addStat(health);
        turnip.setPrice(15);

        return turnip;
    }

    public static Eatable getApple() {
        Eatable apple = new Eatable("Apple", "Fruit", true);
        health.setCurrent(0);
        energy.setCurrent(10);
        health.setMaximum(5);
        energy.setMaximum(0);
        apple.addStat(energy);
        apple.addStat(health);
        apple.setPrice(20);

        return apple;
    }

    public static Eatable getOrange() {
        Eatable orange = new Eatable("Orange", "Fruit", true);
        health.setCurrent(10);
        energy.setCurrent(0);
        health.setMaximum(0);
        energy.setMaximum(5);
        orange.addStat(energy);
        orange.addStat(health);
        orange.setPrice(20);

        return orange;
    }

    public static Eatable getPotato() {
        Eatable potato = new Eatable("Potato", "Vegetable", true);
        health.setCurrent(-5);
        energy.setCurrent(10);
        health.setMaximum(0);
        energy.setMaximum(0);
        potato.addStat(energy);
        potato.addStat(health);
        potato.setPrice(25);
        return potato;
    }

    public static Eatable getCarrot() {
        Eatable carrot = new Eatable("Carrot", "Vegetable", true);
        carrot.addStat(health);
        carrot.setPrice(25);
        health.setCurrent(5);
        energy.setCurrent(5);
        health.setMaximum(0);
        energy.setMaximum(0);
        return carrot;
    }

    public static Eatable getTomato() {
        Eatable tomato = new Eatable("Tomato", "Vegetable", true);
        tomato.addStat(energy);
        tomato.addStat(health);
        tomato.setPrice(10);
        health.setCurrent(10);
        energy.setCurrent(40);
        health.setMaximum(0);
        energy.setMaximum(5);
        return tomato;
    }

    public static Eatable getMelon() {
        Eatable melon = new Eatable("Melon", "Fruit", true);
        melon.addStat(energy);
        melon.addStat(health);
        melon.setPrice(60);

        return melon;
    }

    public static Eatable getPineapple() {
        Eatable pineapple = new Eatable("Pineapple", "Fruit", true);
        health.setCurrent(15);
        energy.setCurrent(15);
        health.setMaximum(15);
        energy.setMaximum(15);
        pineapple.addStat(energy);
        pineapple.addStat(health);
        pineapple.setPrice(150);

        return pineapple;
    }

    public static Eatable getStrawberry() {
        Eatable strawberry = new Eatable("Strawberry", "Fruit", true);
        health.setCurrent(10);
        energy.setCurrent(10);
        health.setMaximum(5);
        energy.setMaximum(5);
        strawberry.addStat(energy);
        strawberry.addStat(health);
        strawberry.setPrice(50);

        return strawberry;
    }

    public static Eatable getPepper() {
        Eatable pepper = new Eatable("Pepper", "Vegetable", true);
        health.setCurrent(0);
        energy.setCurrent(10);
        health.setMaximum(5);
        energy.setMaximum(0);
        pepper.addStat(energy);
        pepper.addStat(health);

        pepper.setPrice(25);
        return pepper;
    }

    public static Eatable getJunk() {
        Eatable junk = new Eatable("Junk", "Fruit", true);
        return junk;
    }
    //Crop*************************************

    public static Crop getGarlicCrop() {
        Crop garlicCrop = new Crop("Garlic", Season.Spring, 1);
        garlicCrop.setPrice(15);
        return garlicCrop;
    }

    public static Crop getPeaCrop() {
        Crop peaCrop = new Crop("Pea", Season.Spring, 3);
        peaCrop.setPrice(10);
        return peaCrop;
    }

    public static Crop getLettuceCrop() {
        Crop lettuceCrop = new Crop("Lettuce", Season.Spring, 1);
        lettuceCrop.setPrice(10);
        return lettuceCrop;
    }

    public static Crop getEggplantCrop() {
        Crop eggplantCrop = new Crop("Eggplant", Season.Spring, 3);
        eggplantCrop.setPrice(20);
        return eggplantCrop;
    }

    public static Crop getCucumberCrop() {
        Crop cucumberCrop = new Crop("Cucumber", Season.Summer, 3);
        cucumberCrop.setPrice(10);
        return cucumberCrop;
    }

    public static Crop getWatermelonCrop() {
        Crop watermelonCrop = new Crop("Watermelon", Season.Summer, 3);
        watermelonCrop.setPrice(80);
        return watermelonCrop;
    }

    public static Crop getOnionCrop() {
        Crop onionCrop = new Crop("Onion", Season.Summer, 1);
        onionCrop.setPrice(15);
        return onionCrop;
    }

    public static Crop getTurnipCrop() {
        Crop turnipCrop = new Crop("Turnip", Season.Summer, 1);
        turnipCrop.setPrice(15);
        return turnipCrop;
    }

    public static Crop getPotatoCrop() {
        Crop potatoCrop = new Crop("Potato", Season.Autumn, 1);
        potatoCrop.setPrice(25);
        return potatoCrop;
    }

    public static Crop getCarrotCrop() {
        Crop carrotCrop = new Crop("Carrot", Season.Autumn, 1);
        carrotCrop.setPrice(25);
        return carrotCrop;
    }

    public static Crop getTomatoCrop() {
        Crop tomatoCrop = new Crop("Tomato", Season.Autumn, 3);
        tomatoCrop.setPrice(10);
        return tomatoCrop;
    }

    public static Crop getMelonCrop() {
        Crop melonCrop = new Crop("Melon", Season.Autumn, 3);
        melonCrop.setPrice(60);
        return melonCrop;
    }

    public static Crop getPineappleCrop() {
        Crop pineappleCrop = new Crop("Pineapple", Season.Tropical, 3);
        pineappleCrop.setPrice(150);
        return pineappleCrop;
    }

    public static Crop getStrawberryCrop() {
        Crop strawberryCrop = new Crop("Strawberry", Season.Tropical, 3);
        strawberryCrop.setPrice(50);
        return strawberryCrop;
    }

    public static Crop getPepperCrop() {
        Crop pepperCrop = new Crop("Pepper", Season.Tropical, 3);
        pepperCrop.setPrice(25);
        return pepperCrop;
    }

    //Cooking Utensils***************
    static Utensil fryingPan = new Utensil("Frying Pan", 10);
    static Utensil knife = new Utensil("Knife", 20);
    static Utensil mixer = new Utensil("Mixer", 10);
    static Utensil pot = new Utensil("Pot", 20);
    static Utensil stove = new Utensil("Stove", 5);

    static {
        HashMap<Item, Integer> madeOf = new HashMap<>();
        madeOf.put(getIronOre(), 7);
        fryingPan.setItemNeeded(madeOf);
        madeOf = new HashMap<>();
        madeOf.put(getIronOre(), 3);
        madeOf.put(getOldLumber(), 2);
        knife.setItemNeeded(madeOf);
        madeOf = new HashMap<>();
        madeOf.put(getSilverOre(), 5);
        madeOf.put(getPineLumber(), 3);
        mixer.setItemNeeded(madeOf);
        madeOf = new HashMap<>();
        madeOf.put(getStone(), 10);
        pot.setItemNeeded(madeOf);
        madeOf = new HashMap<>();
        madeOf.put(getAdamantiumOre(), 3);
        madeOf.put(getOakLumber(), 3);
        stove.setItemNeeded(madeOf);
    }
    //Cooking Ingredient*******************

    public static Product getOil() {
        Product oil = new Product("Oil", "Ingredient", false);
        oil.setPrice(30);
        return oil;
    }

    public static Product getFlour() {
        Product flour = new Product("Flour", "Ingredient", false);
        flour.setPrice(20);
        return flour;
    }

    public static Product getSugar() {
        Product sugar = new Product("Sugar", "Ingredient", false);
        sugar.setPrice(10);
        return sugar;
    }

    public static Product getSalt() {
        Product salt = new Product("Salt", "Ingredient", false);
        salt.setPrice(10);
        return salt;
    }

    public static Product getCowMeat() {
        Product cowMeat = new Product("Cow Meat", "Ingredient", false);
        cowMeat.setPrice(100);
        return cowMeat;
    }

    public static Product getSheepMeat() {
        Product sheepMeat = new Product("Sheep Meat", "Ingredient", false);
        sheepMeat.setPrice(150);
        return sheepMeat;
    }

    public static Product getChickenMeat() {
        Product chickenMeat = new Product("Chicken Meat", "Ingredient", false);
        chickenMeat.setPrice(70);
        return chickenMeat;
    }

    public static Product getFishMeat() {
        Product fishMeat = new Product("Fish Meat", "Ingredient", false);
        fishMeat.setPrice(200);
        return fishMeat;
    }

    public static Product getEgg() {
        Product egg = new Product("Egg", "Ingredient", false);
        egg.setPrice(30);
        return egg;
    }

    public static Eatable getMilk() {
        Eatable milk = new Eatable("Milk", "Food", true);
        energy.setCurrent(20);
        health.setCurrent(20);
        milk.addStat(energy);
        milk.addStat(health);
        milk.setPrice(60);
        return milk;
    }

    public static Eatable getBread() {
        Eatable bread = new Eatable("Bread", "Food", true);
        energy.setCurrent(10);
        health.setCurrent(5);
        bread.addStat(energy);
        bread.addStat(health);
        bread.setPrice(20);
        return bread;
    }


    public static Eatable getCheese() {

        Eatable cheese = new Eatable("Cheese", "Food", true);
        energy.setCurrent(30);
        health.setCurrent(30);
        cheese.setPrice(150);
        cheese.addStat(energy);
        return cheese;
    }
    //Medicine***********************

    public static Eatable getNormalMedicine() {
        Eatable normalMedicine = new Eatable("Normal Medicine", "Medicine", true);
        health.setCurrent(100);
        normalMedicine.addStat(health);
        return normalMedicine;
    }

    public static Eatable getStrongMedicine() {
        Eatable strongMedicine = new Eatable("Strong Medicine", "Medicine", true);
        health.setCurrent(250);
        strongMedicine.addStat(health);
        return strongMedicine;
    }

    public static Eatable getSuperMedicine() {
        Eatable superMedicine = new Eatable("Super Medicine", "Medicine", true);
        health.setCurrent(500);
        superMedicine.addStat(health);
        return superMedicine;
    }
    //Food*************

    public static Eatable getFrenchFries() {
        Eatable frenchFries = new Eatable("French Fries", "Food", true);
        energy.setCurrent(100);
        health.setCurrent(0);
        energy.setMaximum(0);
        health.setMaximum(-15);
        frenchFries.addStat(energy);
        frenchFries.addStat(health);
        return frenchFries;
    }

    public static Eatable getShiraziSalad() {
        Eatable shiraziSalad = new Eatable("Shirazi Salad", "Food", true);
        energy.setCurrent(40);
        health.setCurrent(60);
        energy.setMaximum(0);
        health.setMaximum(10);
        shiraziSalad.addStat(energy);
        shiraziSalad.addStat(health);
        return shiraziSalad;
    }

    public static Eatable getCheeseCake() {
        Eatable cheeseCake = new Eatable("Cheese Cake", "Food", true);
        energy.setCurrent(80);
        health.setCurrent(0);
        energy.setMaximum(0);
        health.setMaximum(0);
        cheeseCake.addStat(energy);
        return cheeseCake;
    }

    public static Eatable getPizza() {
        Eatable pizza = new Eatable("Pizza", "Food", true);
        energy.setCurrent(100);
        health.setCurrent(10);
        energy.setMaximum(0);
        health.setMaximum(-10);
        pizza.addStat(energy);
        pizza.addStat(health);
        return pizza;
    }

    public static Eatable getMirzaGhasemi() {
        Eatable mirzaGhasemi = new Eatable("MirzaGhasemi", "Food", true);
        energy.setCurrent(70);
        health.setCurrent(30);
        energy.setMaximum(0);
        health.setMaximum(10);
        mirzaGhasemi.addStat(energy);
        mirzaGhasemi.addStat(health);
        return mirzaGhasemi;
    }

    //Recipes*************************

    public static Recipe getShiraziSaladRecipe() {
        ArrayList<Tool> utensils = new ArrayList<>();
        HashMap<Product, Integer> ingredients = new HashMap<>();
        utensils.add(knife);
        ingredients.put(getCucumber(), 3);
        ingredients.put(getTomato(), 3);
        ingredients.put(getLemon(), 1);
        ingredients.put(getOnion(), 3);
        shiraziSaladRecipe = new Recipe("ShiraziSalad", getShiraziSalad());
        shiraziSaladRecipe.setTools(utensils);
        shiraziSaladRecipe.setIngredients(ingredients);
        return shiraziSaladRecipe;
    }

    public static Recipe getCheeseCakeRecipe() {
        ArrayList<Tool> utensils = new ArrayList<>();
        HashMap<Product, Integer> ingredients = new HashMap<>();
        utensils.add(pot);
        utensils.add(mixer);
        utensils.add(stove);
        ingredients.put(getMilk(), 3);
        ingredients.put(getCheese(), 2);
        ingredients.put(getEgg(), 3);
        ingredients.put(getSugar(), 3);
        cheeseCakeRecipe = new Recipe("CheeseCake", getCheeseCake());
        cheeseCakeRecipe.setTools(utensils);
        cheeseCakeRecipe.setIngredients(ingredients);
        return cheeseCakeRecipe;
    }

    public static Recipe getMirzaGhasemiRecipe() {
        ArrayList<Tool> utensils = new ArrayList<>();
        HashMap<Product, Integer> ingredients = new HashMap<>();
        utensils.add(knife);
        utensils.add(fryingPan);
        ingredients.put(getEggplant(), 3);
        ingredients.put(getOil(), 1);
        ingredients.put(getGarlic(), 3);
        ingredients.put(getTomato(), 2);
        ingredients.put(getEgg(), 2);
        ingredients.put(getSalt(), 1);
        mirzaGhasemiRecipe = new Recipe("MirzaGhasemi", getMirzaGhasemi());
        mirzaGhasemiRecipe.setTools(utensils);
        mirzaGhasemiRecipe.setIngredients(ingredients);
        return mirzaGhasemiRecipe;
    }

    public static Recipe getFrenchFriesRecipe() {
        ArrayList<Tool> utensils = new ArrayList<>();
        HashMap<Product, Integer> ingredients = new HashMap<>();
        utensils.add(knife);
        utensils.add(fryingPan);
        ingredients.put(getPotato(), 1);
        ingredients.put(getOil(), 1);
        ingredients.put(getSalt(), 1);
        frenchFriesRecipe = new Recipe("French Fries", getPizza());
        frenchFriesRecipe.setTools(utensils);
        frenchFriesRecipe.setIngredients(ingredients);
        return frenchFriesRecipe;
    }

    public static Recipe getPizzaRecipe() {
        ArrayList<Tool> utensils = new ArrayList<>();
        HashMap<Product, Integer> ingredients = new HashMap<>();
        utensils.add(knife);
        utensils.add(stove);
        ingredients.put(getCheese(), 2);
        ingredients.put(getChickenMeat(), 1);
        ingredients.put(getSalt(), 1);
        ingredients.put(getFlour(), 2);
        pizzaRecipe = new Recipe("Pizza", getPizza());
        pizzaRecipe.setTools(utensils);
        pizzaRecipe.setIngredients(ingredients);
        return pizzaRecipe;
    }

    static Recipe shiraziSaladRecipe;
    static Recipe cheeseCakeRecipe;
    static Recipe mirzaGhasemiRecipe;
    static Recipe pizzaRecipe;
    static Recipe frenchFriesRecipe;
    static ArrayList<Recipe> recipeArrayList = new ArrayList<>();

    public static void setDefaultRecipe(){
        recipeArrayList = new ArrayList<>();
        recipeArrayList.add(frenchFriesRecipe);
        recipeArrayList.add(shiraziSaladRecipe);
        recipeArrayList.add(cheeseCakeRecipe);
        recipeArrayList.add(mirzaGhasemiRecipe);
        recipeArrayList.add(pizzaRecipe);
    }

    static Recipes recipes = new Recipes();
    //Tool*************************
    static HashMap<Item, Integer> madeOf = new HashMap<>();
    static HashMap<Item, Integer> repairWith = new HashMap<>();

    public static Tool getStoneShovel() {
        Tool stoneShovel = new Tool("Shovel", "Stone", false, 1);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getBranch(), 5);
        madeOf.put(getStone(), 5);
        repairWith.put(getBranch(), 2);
        repairWith.put(getStone(), 2);
        stoneShovel.setItemNeeded(madeOf);
        stoneShovel.setRepairNeeded(repairWith);
        stoneShovel.setPrice(100);
        stoneShovel.setEnergyReducer(150);
        stoneShovel.setRepairMoney(10);

        return stoneShovel;
    }

    public static Tool getIronShovel() {
        Tool ironShovel = new Tool("Shovel", "Iron", false, 2);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getIronOre(), 4);
        madeOf.put(getOldLumber(), 4);
        repairWith.put(getIronOre(), 2);
        repairWith.put(getOldLumber(), 2);
        ironShovel.setItemNeeded(madeOf);
        ironShovel.setRepairNeeded(repairWith);
        ironShovel.setPrice(500);
        ironShovel.setEnergyReducer(80);
        ironShovel.setRepairMoney(50);
        return ironShovel;
    }

    public static Tool getSilverShovel() {
        Tool silverShovel = new Tool("Shovel", "Silver", false, 3);

        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getPineLumber(), 3);
        madeOf.put(getSilverOre(), 3);
        repairWith.put(getPineLumber(), 1);
        repairWith.put(getSilverOre(), 1);
        silverShovel.setItemNeeded(madeOf);
        silverShovel.setRepairNeeded(repairWith);
        silverShovel.setPrice(1000);
        silverShovel.setEnergyReducer(40);
        silverShovel.setRepairMoney(100);
        return silverShovel;
    }

    public static Tool getAdamantiumShovel() {
        Tool adamantiumShovel = new Tool("Shovel", "Adamantium", false, 4);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getOakLumber(), 2);
        madeOf.put(getAdamantiumOre(), 2);
        repairWith.put(getOakLumber(), 1);
        repairWith.put(getAdamantiumOre(), 1);
        adamantiumShovel.setItemNeeded(madeOf);
        adamantiumShovel.setRepairNeeded(repairWith);
        adamantiumShovel.setPrice(4000);
        adamantiumShovel.setEnergyReducer(20);
        adamantiumShovel.setRepairMoney(400);
        return adamantiumShovel;
    }

    static Tool stonePickaxe = new Tool("Pickaxe", "Stone", false, 1);

    public static Tool getStonePickaxe() {
        Tool ironPickaxe = new Tool("Pickaxe", "Iron", false, 2);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getBranch(), 5);
        madeOf.put(getStone(), 10);
        repairWith.put(getBranch(), 2);
        repairWith.put(getStone(), 5);
        stonePickaxe.setItemNeeded(madeOf);
        stonePickaxe.setRepairNeeded(repairWith);
        stonePickaxe.setPrice(200);
        stonePickaxe.setRepairMoney(20);
        return stonePickaxe;
    }

    public static Tool getSilverPickaxe() {
        Tool silverPickaxe = new Tool("Pickaxe", "Silver", false, 3);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getPineLumber(), 3);
        madeOf.put(getSilverOre(), 6);
        repairWith.put(getPineLumber(), 1);
        repairWith.put(getSilverOre(), 3);
        silverPickaxe.setItemNeeded(madeOf);
        silverPickaxe.setRepairNeeded(repairWith);
        silverPickaxe.setPrice(2000);
        silverPickaxe.setRepairMoney(200);
        return silverPickaxe;
    }

    public static Tool getAdamantiumPickaxe() {
        Tool adamantiumPickaxe = new Tool("Pickaxe", "Adamantium", false, 4);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getOakLumber(), 2);
        madeOf.put(getAdamantiumOre(), 4);
        repairWith.put(getOakLumber(), 1);
        repairWith.put(getAdamantiumOre(), 2);
        adamantiumPickaxe.setItemNeeded(madeOf);
        adamantiumPickaxe.setRepairNeeded(repairWith);
        adamantiumPickaxe.setPrice(7000);
        adamantiumPickaxe.setRepairMoney(700);
        return adamantiumPickaxe;
    }

    public static Tool getStoneAxe() {
        Tool stoneAxe = new Tool("Axe", "Stone", false, 1);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getBranch(), 10);
        madeOf.put(getStone(), 5);
        repairWith.put(getBranch(), 5);
        repairWith.put(getStone(), 2);
        stoneAxe.setItemNeeded(madeOf);
        stoneAxe.setRepairNeeded(repairWith);
        stoneAxe.setPrice(200);
        stoneAxe.setRepairMoney(20);
        return stoneAxe;
    }

    public static Tool getIronAxe() {
        Tool ironAxe = new Tool("Axe", "Iron", false, 2);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getIronOre(), 8);
        madeOf.put(getOldLumber(), 4);
        repairWith.put(getIronOre(), 4);
        repairWith.put(getOldLumber(), 2);
        ironAxe.setItemNeeded(madeOf);
        ironAxe.setRepairNeeded(repairWith);
        ironAxe.setPrice(800);
        ironAxe.setRepairMoney(80);
        return ironAxe;
    }

    public static Tool getSilverAxe() {
        Tool silverAxe = new Tool("Axe", "Silver", false, 3);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getPineLumber(), 6);
        madeOf.put(getSilverOre(), 3);
        repairWith.put(getPineLumber(), 3);
        repairWith.put(getSilverOre(), 1);
        silverAxe.setItemNeeded(madeOf);
        silverAxe.setRepairNeeded(repairWith);
        silverAxe.setPrice(2000);
        silverAxe.setRepairMoney(200);
        return silverAxe;
    }

    public static Tool getAdamantiumAxe() {
        Tool adamantiumAxe = new Tool("Axe", "Adamantium", false, 4);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getOakLumber(), 4);
        madeOf.put(getAdamantiumOre(), 2);
        repairWith.put(getOakLumber(), 2);
        repairWith.put(getAdamantiumOre(), 1);
        adamantiumAxe.setItemNeeded(madeOf);
        adamantiumAxe.setRepairNeeded(repairWith);
        adamantiumAxe.setPrice(7000);
        adamantiumAxe.setRepairMoney(700);
        return adamantiumAxe;
    }

    //Watering Can*****************************
    public static WateringCan getSmallWateringCan() {
        WateringCan smallWateringCan = new WateringCan("Watering Can", "Small", 1, 1, 1);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getBranch(), 5);
        repairWith.put(getBranch(), 2);
        smallWateringCan.setItemNeeded(madeOf);
        smallWateringCan.setRepairNeeded(repairWith);
        smallWateringCan.setPrice(50);
        smallWateringCan.setEnergyReducer(40);
        smallWateringCan.setRepairMoney(5);
        return smallWateringCan;
    }

    public static WateringCan getOldWateringCan() {
        WateringCan oldWateringCan = new WateringCan("Watering Can", "Old", 2, 2, 2);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getOldLumber(), 4);
        repairWith.put(getOldLumber(), 2);
        oldWateringCan.setItemNeeded(madeOf);
        oldWateringCan.setRepairNeeded(repairWith);
        oldWateringCan.setPrice(200);
        oldWateringCan.setEnergyReducer(30);
        oldWateringCan.setRepairMoney(20);
        return oldWateringCan;
    }

    public static WateringCan getOakWateringCan() {
        WateringCan oakWateringCan = new WateringCan("Watering Can", "Oak", 4, 8, 9);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getOakLumber(), 2);
        repairWith.put(getOakLumber(), 1);
        oakWateringCan.setItemNeeded(madeOf);
        oakWateringCan.setRepairNeeded(repairWith);
        oakWateringCan.setPrice(2000);
        oakWateringCan.setEnergyReducer(10);
        oakWateringCan.setRepairMoney(200);
        return oakWateringCan;
    }

    public static WateringCan getStoneWateringCan() {
        WateringCan stoneWateringCan = new WateringCan("Watering Can", "Stone", 1, 1, 2);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getStone(), 5);
        repairWith.put(getStone(), 2);
        stoneWateringCan.setItemNeeded(madeOf);
        stoneWateringCan.setRepairNeeded(repairWith);
        stoneWateringCan.setPrice(50);
        stoneWateringCan.setEnergyReducer(80);
        stoneWateringCan.setRepairMoney(5);
        return stoneWateringCan;
    }

    public static WateringCan getIronWateringCan() {
        WateringCan ironWateringCan = new WateringCan("Watering Can", "Iron", 2, 2, 4);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getIronOre(), 4);
        repairWith.put(getIronOre(), 2);
        ironWateringCan.setItemNeeded(madeOf);
        ironWateringCan.setRepairNeeded(repairWith);
        ironWateringCan.setPrice(200);
        ironWateringCan.setEnergyReducer(60);
        ironWateringCan.setRepairMoney(20);
        return ironWateringCan;
    }

    public static WateringCan getSilverWateringCan() {
        WateringCan silverWateringCan = new WateringCan("Watering Can", "Silver", 3, 3, 8);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getSilverOre(), 3);
        repairWith.put(getSilverOre(), 1);
        silverWateringCan.setItemNeeded(madeOf);
        silverWateringCan.setRepairNeeded(repairWith);
        silverWateringCan.setPrice(500);
        silverWateringCan.setEnergyReducer(20);
        silverWateringCan.setRepairMoney(50);
        return silverWateringCan;
    }

    public static WateringCan getAdamantiumWateringCan() {
        WateringCan adamantiumWateringCan = new WateringCan("Watering Can", "Adamantium", 4, 9, 18);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getAdamantiumOre(), 2);
        repairWith.put(getAdamantiumOre(), 1);
        adamantiumWateringCan.setItemNeeded(madeOf);
        adamantiumWateringCan.setRepairNeeded(repairWith);
        adamantiumWateringCan.setPrice(2000);
        adamantiumWateringCan.setEnergyReducer(20);
        adamantiumWateringCan.setRepairMoney(200);
        return adamantiumWateringCan;
    }

    public static WateringCan getPineWateringCan() {
        WateringCan pineWateringCan = new WateringCan("Watering Can", "Pine", 3, 3, 4);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getPineLumber(), 3);
        repairWith.put(getPineLumber(), 1);
        pineWateringCan.setItemNeeded(madeOf);
        pineWateringCan.setRepairNeeded(repairWith);
        pineWateringCan.setPrice(500);
        pineWateringCan.setEnergyReducer(20);
        pineWateringCan.setRepairMoney(50);
        return pineWateringCan;

    }

    //Fishing Rod****************
    public static FishingRod getSmallFishingRod() {
        FishingRod smallFishingRod = new FishingRod("Fishing Rod", "Small", 1, 40);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getBranch(), 15);
        madeOf.put(getThread(), 2);
        repairWith.put(getBranch(), 5);
        repairWith.put(getThread(), 1);
        smallFishingRod.setItemNeeded(madeOf);
        smallFishingRod.setRepairNeeded(repairWith);
        smallFishingRod.setPrice(100);
        smallFishingRod.setRepairMoney(10);
        return smallFishingRod;
    }

    public static FishingRod getOldFishingRod() {
        FishingRod oldFishingRod = new FishingRod("Fishing Rod", "Old", 2, 60);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getOldLumber(), 10);
        madeOf.put(getThread(), 3);
        repairWith.put(getOldLumber(), 4);
        repairWith.put(getThread(), 2);
        oldFishingRod.setItemNeeded(madeOf);
        oldFishingRod.setRepairNeeded(repairWith);
        oldFishingRod.setPrice(300);
        oldFishingRod.setRepairMoney(30);

        return oldFishingRod;
    }

    public static FishingRod getPineFishingRod() {
        FishingRod pineFishingRod = new FishingRod("Fishing Rod", "Pine", 3, 80);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getPineLumber(), 6);
        madeOf.put(getThread(), 4);
        repairWith.put(getPineLumber(), 3);
        repairWith.put(getThread(), 1);
        pineFishingRod.setItemNeeded(madeOf);
        pineFishingRod.setRepairNeeded(repairWith);
        pineFishingRod.setPrice(500);
        pineFishingRod.setRepairMoney(50);

        return pineFishingRod;
    }

    public static FishingRod getOakFishingRod() {
        FishingRod oakFishingRod = new FishingRod("Fishing Rod", "Oak", 4, 100);
        madeOf = new HashMap<>();
        repairWith = new HashMap<>();
        madeOf.put(getOakLumber(), 3);
        madeOf.put(getThread(), 5);
        repairWith.put(getOakLumber(), 2);
        repairWith.put(getThread(), 1);
        oakFishingRod.setItemNeeded(madeOf);
        oakFishingRod.setRepairNeeded(repairWith);
        oakFishingRod.setPrice(2000);
        oakFishingRod.setRepairMoney(200);
        return oakFishingRod;
    }

    //Machines********************************
    static HashMap<Item, Integer> inputs = new HashMap<>();
    static HashMap<Product, Integer> outputs = new HashMap<>();

    public static Machine getJuicer() {
        Machine juicer = new Machine("Juicer");
        juicer.setDescription("A machine to extract juice from fruits and vegetables.");
        juicer.setPrice(1000);
        return juicer;
    }

    public static Machine getSpinningWheel() {
        Machine spinningWheel = new Machine("Spinning Wheel");
        spinningWheel.setDescription("A machine to make threads out of wool.");
        spinningWheel.setPrice(2000);
        inputs = new HashMap<>();
        inputs.put(getWool(), 1);
        outputs = new HashMap<>();
        outputs.put(getThread(), 1);
        spinningWheel.setInputs(inputs);
        spinningWheel.setOutputs(outputs);
        return spinningWheel;
    }

    public static Machine getCheeseMaker() {
        Machine cheeseMaker = new Machine("Cheese Maker");
        cheeseMaker.setDescription("A machine to create cheese from milk.");
        cheeseMaker.setPrice(3000);
        inputs = new HashMap<>();
        inputs.put(getMilk(), 1);
        outputs = new HashMap<>();
        outputs.put(getCheese(), 1);
        cheeseMaker.setInputs(inputs);
        cheeseMaker.setOutputs(outputs);
        return cheeseMaker;
    }

//    public static Machine getTomatoPaste() {
//        Machine tomatoPaste = new Machine("Tomato Paste");
//        tomatoPaste.setDescription("A machine to create tomato paste from tomato.");
//        tomatoPaste.setPrice(1000);
//        inputs = new HashMap<>();
//        inputs.put(getTomato(), 2);
////        outputs.put(, 1);//????
//        tomatoPaste.setInputs(inputs);
//        tomatoPaste.setOutputs(outputs);
//        return tomatoPaste;
//    }
    //Animals*****************

    public static Tool getMilker() {
        Tool milker = new Tool("Milker", "Tool", false, 0);
        return milker;
    }

    public static Tool getScissors() {
        Tool scissors = new Tool("Scissors", "Tool", false, 0);
        return scissors;
    }

    public static Animal getCow() {
        Animal cow = new Animal("Cow", "Milk this Cow", getMilker());
        cownum++;
        cow.setName("Cow Num" + cownum);
        return cow;
    }

    public static Animal getSheep() {
        Animal sheep = new Animal("Sheep", "Sheer this sheep", getScissors());
        sheepnum++;
        sheep.setName("Sheep Num" + sheepnum);
        return sheep;
    }

    public static Animal getChicken() {
        Animal chicken = new Animal("Chicken", "Collect eggs");
        chickennum++;
        chicken.setName("Chicken Num" + chickennum);
        return chicken;
    }

    static int cownum = 0;
    static int sheepnum = 0;
    static int chickennum = 0;
    //************************************
    /*..............Custom...............*/
    //Eatable, fruit, medicine, plant, foods************************************
    static HashMap<String, ArrayList> customEatables = new HashMap<>();

    private static Eatable newCustomEatable(String name, String type, boolean isSpecial, Stat energy, Stat health, int price) {
        //TODO- set kardane image
        Eatable newEatable = new Eatable(name, type, true);
        newEatable.addStat(energy);
        newEatable.addStat(health);
        newEatable.setPrice(price);
        return newEatable;
    }

    public static Eatable getCustomEatable(ArrayList features) {
        Eatable newEatable = newCustomEatable((String) features.get(0), (String) features.get(1), (boolean) features.get(2), (Stat) features.get(3), (Stat) features.get(4), (int) features.get(5));
        return newEatable;
    }

    public static Eatable getNewEatable(String name) {
        Eatable newEatable = null;
        for (String s : customEatables.keySet()) {
            if (s.equals(name))
                newEatable = getCustomEatable(customEatables.get(s));
        }
        return newEatable;
    }

    //Crop************************************
    static HashMap<String, ArrayList> customCrops = new HashMap<>();

    private static Crop newCustomCrop(String name, Season season, int harvestLeft, int price) {
        //TODO- set kardane image
        Crop newCrop = new Crop(name, season, harvestLeft);
        newCrop.setPrice(price);
        return newCrop;
    }

    public static Crop getCustomCrop(ArrayList features) {
        Crop newCrop = newCustomCrop((String) features.get(0), (Season) features.get(1), (int) features.get(2), (int) features.get(3));
        return newCrop;
    }

    public static Crop getNewCrop(String name) {
        Crop newCrop = null;
        for (String s : customCrops.keySet()) {
            if (s.equals(name))
                newCrop = getCustomCrop(customCrops.get(s));
        }
        return newCrop;
    }

    //Cooking ingredient, Products************************************
    static HashMap<String, ArrayList> customProducts = new HashMap<>();

    private static Product newCustomProduct(String name, String type, boolean isSpecial, int price) {
        //TODO- set kardane image
        Product newProduct = new Product(name, type, false);
        newProduct.setPrice(price);
        return newProduct;
    }

    public static Product getCustomProduct(ArrayList features) {
        Product newProduct = newCustomProduct((String) features.get(0), (String) features.get(1), (boolean) features.get(2), (int) features.get(3));
        return newProduct;
    }

    public static Product getNewProduct(String name) {
        Product newProduct = null;
        for (String s : customProducts.keySet()) {
            if (s.equals(name))
                newProduct = getCustomProduct(customProducts.get(s));
        }
        return newProduct;
    }

    //Recipe***************************
    static HashMap<String, ArrayList> customRecipes = new HashMap<>();

    private static Recipe newCustomRecipe(String name, Eatable food, ArrayList<Tool> utensils, HashMap<Product, Integer> ingredients) {
        //TODO- set kardane image
        Recipe newRecipe = new Recipe(name, food);
        return newRecipe;
    }

    public static Recipe getCustomRecipe(ArrayList features) {
        Recipe newRecipe = newCustomRecipe((String) features.get(0), (Eatable) features.get(1), (ArrayList<Tool>) features.get(2), (HashMap<Product, Integer>) features.get(3));
        return newRecipe;
    }

    public static Recipe getNewRecipe(String name) {
        Recipe newRecipe = null;
        for (String s : customRecipes.keySet()) {
            if (s.equals(name))
                newRecipe = getCustomRecipe(customRecipes.get(s));
        }
        return newRecipe;
    }

    //Execise**************************
    static HashMap<String, ArrayList> customExercise = new HashMap<>();

    private static Exercise newCustomExercise(String name, Stat exercise, float energyDec, int price, int priceUpdate, int energyDecUpdate) {
        //TODO- set kardane image
        Exercise newExercise = new Exercise(name, price, energyDec, exercise);
        newExercise.setUpdates(priceUpdate, energyDecUpdate);
        return newExercise;
    }

    public static Exercise getCustomExercise(ArrayList features) {
        Exercise newExercise = newCustomExercise((String)features.get(0),(Stat) features.get(1), (float) features.get(2), (int) features.get(3), (int) features.get(4), (int) features.get(5));
        return newExercise;
    }

    public static Exercise getNewExercise(String name) {
        Exercise newExercise = null;
        for (String s : customExercise.keySet()) {
            if (s.equals(name))
                newExercise = getCustomExercise(customExercise.get(s));
        }
        return newExercise;
    }

    //Machines********************
    static HashMap<String, ArrayList> customMachines = new HashMap<>();

    private static Machine newCustomMachine(String name, HashMap<Item, Integer> inputs, HashMap<Product, Integer> outputs, int price) {
        //TODO- set kardane image
        Machine newMachine = new Machine(name);
        newMachine.setPrice(price);
        newMachine.setInputs(inputs);
        newMachine.setOutputs(outputs);
        return newMachine;
    }

    public static Machine getCustomMachine(ArrayList features) {
        Machine newMachine = newCustomMachine((String) features.get(0), (HashMap<Item, Integer>) features.get(1), (HashMap<Product, Integer>) features.get(2), (int) features.get(3));
        return newMachine;
    }

    public static Machine getNewMachine(String name) {
        Machine newMachine = null;
        for (String s : customMachines.keySet()) {
            if (s.equals(name))
                newMachine = getCustomMachine(customMachines.get(s));
        }
        return newMachine;
    }

    //Tools*********************
    static HashMap<String, ArrayList> customTools = new HashMap<>();

    private static Tool newCustomTool(String name, float breakProb) {
        //TODO- set kardane image
        //TODO- tool ya utensil joft handle shavad aya?
        Tool newTool = new Utensil(name, breakProb);
        return newTool;
    }

    public static Tool getCustomTool(ArrayList features) {
        Tool newTool = newCustomTool((String) features.get(0), (float) features.get(1));
        return newTool;
    }

    public static Tool getNewTool(String name) {
        Tool newTool = null;
        for (String s : customTools.keySet()) {
            if (s.equals(name))
                newTool = getCustomTool(customTools.get(s));
        }
        return newTool;
    }

    //Person*************************//TODO-check kon behtar ham mishe zad ya na
    static HashMap<String, ArrayList> customPerson = new HashMap<>();

    private static Person newCustomPerson(String name, int money, ArrayList<Stat> stats, int capacity) {
        //TODO- set kardane image
        Person newPerson = new Person();
        newPerson.setStats(stats);
        newPerson.setMoney(money);
        newPerson.setName(name);
        //TODO- in baya davaz she chon har adami backpacke khodesh bayad avaz she na backpacke kolli
        BackPack.getBackPack().setMaxCapacity(capacity);
        return newPerson;
    }

    public static Person getCustomPerson(ArrayList features) {
        Person newPerson = newCustomPerson((String) features.get(0), (int) features.get(1), (ArrayList<Stat>) features.get(2), (int) features.get(3));
        return newPerson;
    }

    public static Person getNewPerson(String name) {
        Person newPerson = null;
        for (String s : customPerson.keySet()) {
            if (s.equals(name))
                newPerson = getCustomPerson(customPerson.get(s));
        }
        return newPerson;
    }
    //*************************************
    /* Default initializer*/
    //**************************************
    public static ArrayList<Item> generalStoreItems;
    public static void setGeneralStoreItems(ArrayList<Item> items) {
        Holder.generalStoreItems = items;
    }
    public static ArrayList<Item> defaultGeneralStoreItems(){
        ArrayList<Item> generalStore = new ArrayList<>();
        generalStore.add(getPeach());
        generalStore.add(getPeer());
        generalStore.add(getGarlic());
        generalStore.add(getPea());
        generalStore.add(getLettuce());
        generalStore.add(getEggplant());
        generalStore.add(getPomegranate());
        generalStore.add(getCucumber());
        generalStore.add(getWatermelon());
        generalStore.add(getOnion());
        generalStore.add(getTurnip());
        generalStore.add(getPotato());
        generalStore.add(getCarrot());
        generalStore.add(getTomato());
        generalStore.add(getMelon());
        generalStore.add(getPineapple());
        generalStore.add(getStrawberry());
        generalStore.add(getPepper());
        generalStore.add(getSmallFishingRod());
        generalStore.add(getOldFishingRod());
        generalStore.add(getPineFishingRod());
        generalStore.add(getOakFishingRod());

        generalStore.add(getSmallWateringCan());
        generalStore.add(getOldWateringCan());
        generalStore.add(getPineWateringCan());
        generalStore.add(getOakFishingRod());

        return generalStore;
    }
    public static ArrayList<Item> groceryStoreItems;
    public static void setGroceryStoreItems(ArrayList<Item> items) {
        Holder.groceryStoreItems = items;
    }
    public static ArrayList<Item> defaultGroceriesStoreItems(){
        ArrayList<Item> groceriesStore = new ArrayList<>();
        groceriesStore.add(getPeach());
        groceriesStore.add(getPeer());
        groceriesStore.add(getGarlic());
        groceriesStore.add(getPea());
        groceriesStore.add(getLettuce());
        groceriesStore.add(getEggplant());
        groceriesStore.add(getPomegranate());
        groceriesStore.add(getCucumber());
        groceriesStore.add(getWatermelon());
        groceriesStore.add(getOnion());
        groceriesStore.add(getTurnip());
        groceriesStore.add(getPotato());
        groceriesStore.add(getCarrot());
        groceriesStore.add(getTomato());
        groceriesStore.add(getMelon());
        groceriesStore.add(getPineapple());
        groceriesStore.add(getStrawberry());
        groceriesStore.add(getPepper());
        groceriesStore.add(getCarrotCrop());
        groceriesStore.add(getCucumberCrop());
        groceriesStore.add(getOnionCrop());

        return groceriesStore;

    }
    public static ArrayList<Item> butcheryItems;
    public static void setButcheryItems(ArrayList<Item> items) {
        Holder.butcheryItems = items;
    }
    public static ArrayList<Item> defaultButchery(){
        ArrayList<Item> butchery = new ArrayList<>();
        butchery.add(getCowMeat());
        butchery.add(getSheepMeat());
        butchery.add(getChickenMeat());
        butchery.add(getFishMeat());
        return butchery;
    }
    public static ArrayList<Eatable> cafeItems;
    public static void setCafeItems(ArrayList<Eatable> items) {
        Holder.cafeItems = items;
    }
    public static void addCafeItems(ArrayList<Eatable> items) {
        Holder.cafeItems = defaultCafe();
        for (int i = 0; i < items.size(); i++)
            cafeItems.add(items.get(i));
    }
    public static ArrayList<Eatable> defaultCafe(){
        ArrayList<Eatable> cafe = new ArrayList<>();
        cafe.add(getPizza());
        cafe.add(getMirzaGhasemi());
        cafe.add(getShiraziSalad());
        cafe.add(getFrenchFries());
        cafe.add(getCheeseCake());
        return cafe;
    }
    public static ArrayList<Item> clinicItems;
    public static void setClinicItems(ArrayList<Item> items) {
        Holder.clinicItems = items;
    }

    public static void addClinicItems(ArrayList<Item> items) {
        Holder.clinicItems = defaultClinic();
        for (int i = 0; i < items.size(); i++)
            clinicItems.add(items.get(i));
    }
    public static ArrayList<Item> defaultClinic(){
        ArrayList<Item> clinic = new ArrayList<>();
        clinic.add(getNormalMedicine());
        clinic.add(getStrongMedicine());
        clinic.add(getSuperMedicine());
        return clinic;
    }
    public static ArrayList<Item> laboratoryItems;
    public static void setLaboratoryItems(ArrayList<Item> items) {
        Holder.laboratoryItems = items;
    }
    public static ArrayList<Item> defaultLabratory(){
        ArrayList<Item> machines = new ArrayList<>();
        machines.add(getJuicer());
        machines.add(getCheeseMaker());
        machines.add(getSpinningWheel());
        return machines;
    }
    public static void addLaboratoryItems(ArrayList<Machine> items) {
        Holder.laboratoryItems = defaultLabratory();
        for (int i = 0; i < items.size(); i++)
            laboratoryItems.add(items.get(i));
    }

    public static ArrayList<Tool> workshopItems;
    public static void setWorkshopItems(ArrayList<Tool> items) {
        Holder.workshopItems = items;
    }
    //kamel nist Item hash vali kharkarie!!:||
    public static ArrayList<Tool> defaultWorkShop(){
        ArrayList<Tool> workshop = new ArrayList<>();
        workshop.add(getStoneAxe());
        workshop.add(getSilverAxe());
        workshop.add(getAdamantiumAxe());
        workshop.add(getIronAxe());
        workshop.add(getAdamantiumPickaxe());
        workshop.add(getSilverPickaxe());
        workshop.add(getStonePickaxe());
        workshop.add(getAdamantiumShovel());
        workshop.add(getSilverShovel());
        workshop.add(getIronShovel());
        workshop.add(getStoneShovel());
        workshop.add(getAdamantiumWateringCan());
        workshop.add(getOldWateringCan());
        workshop.add(getPineWateringCan());
        workshop.add(getOakFishingRod());
        return workshop;
    }
    public static ArrayList<Animal> ranchItems;
    public static void setRanchItems(ArrayList<Animal> items) {
        Holder.ranchItems = items;
    }
    public static ArrayList<Animal> defaultRanch(){
        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(getCow());
        animals.add(getSheep());
        animals.add(getChicken());
        return animals;
    }
    public static ArrayList<Exercise> gymEnergyItems;
    public static void setGymEnergyItems(ArrayList<Exercise> items) {
        Holder.gymEnergyItems = items;
    }

    public static void addGymEnergyItems(ArrayList<Exercise> items) {
        Holder.gymEnergyItems = defaultGymEnergy();
        for (int i = 0; i < items.size(); i++)
            gymEnergyItems.add(items.get(i));
    }
    public static ArrayList<Exercise> defaultGymEnergy(){
        ArrayList<Exercise> energyExercises = new ArrayList<>();
        Stat maxAmount = new Stat("Energy");
        Stat refillRate = new Stat("Energy");
        Stat consumptionRate = new Stat("Energy");
        maxAmount.setMaximum(100);
        maxAmount.setType("maximum");
        refillRate.setRefillRate(10);
        refillRate.setType("refill rate");
        consumptionRate.setConsumptionRate(-5);
        consumptionRate.setType("consumption rate");

        energyExercises.add(new Exercise("MaxAmount",100, -50, maxAmount));
        energyExercises.add(new Exercise("RefillRate",100, -50, refillRate));
        energyExercises.add(new Exercise("ConsumptionRate",100, -50, consumptionRate));

        return energyExercises;
    }
    public static ArrayList<Exercise> gymHealthItems;
    public static void setGymHealthItems(ArrayList<Exercise> items) {
        Holder.gymHealthItems = items;
    }

    public static void addGymHealthItems(ArrayList<Exercise> items) {
        Holder.gymHealthItems = defaultGymHealth();
        for (int i = 0; i < items.size(); i++)
            gymHealthItems.add(items.get(i));
    }
    public static ArrayList<Exercise> defaultGymHealth(){
        ArrayList<Exercise> healthExercises = new ArrayList<>();
        Stat maxAmount = new Stat("Health");
        Stat refillRate = new Stat("Health");
        Stat consumptionRate = new Stat("Health");
        maxAmount.setMaximum(50);
        maxAmount.setType("maximum");
        refillRate.setRefillRate(10);
        refillRate.setType("refill rate");
        consumptionRate.setConsumptionRate(-5);
        consumptionRate.setType("consumption rate");

        healthExercises.add(new Exercise("MaxAmount",100, -50, maxAmount));
        healthExercises.add(new Exercise("RefillRate",100, -50, refillRate));
        healthExercises.add(new Exercise("ConsumptionRate",100, -50, consumptionRate));

        return healthExercises;
    }

    public static ArrayList<Tree> treesItem = new ArrayList<>();
    public static ArrayList<Tree> defaultTrees(){
        ArrayList<Tree> trees = new ArrayList<>();
        trees.add(new Tree("Peach Tree", Season.Spring, 100, getPeach()));
        trees.add(new Tree("Cucumber Tree", Season.Spring, 100, getCucumber()));
        trees.add(new Tree("Lemon Tree", Season.Summer, 200, getLemon()));
        trees.add(new Tree("Pomegranate Tree", Season.Summer, 200, getPomegranate()));
        trees.add(new Tree("Apple Tree", Season.Autumn, 100, getApple()));
        trees.add(new Tree("Orange Tree", Season.Autumn, 100, getOrange()));
        return trees;
    }
    public static void setTreesItem(ArrayList<Tree> items){treesItem = items;}

    public static void defaultPerson(){
        Game.playerName = "Farmer";
        Person.getPerson().setMoney(5000);
        Stat energy = new Stat("Energy");
        Stat health = new Stat("Health");
        ArrayList<Stat> stats = new ArrayList<>();
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

        Person.getPerson().setStats(stats);
        BackPack.getBackPack().setMaxCapacity(10);
    }

    public static void setNewPerson(String name, int money, ArrayList<Stat> stats, int capacity){
        Game.playerName = name;
        Person.getPerson().setMoney(money);
        Person.getPerson().setStats(stats);
        BackPack.getBackPack().setMaxCapacity(10);
    }

    public static void setToDefault(){
        setGeneralStoreItems(defaultGeneralStoreItems());
        setGroceryStoreItems(defaultGroceriesStoreItems());
        setClinicItems(defaultClinic());
        setGymEnergyItems(defaultGymEnergy());
        setGymHealthItems(defaultGymHealth());
        setLaboratoryItems(defaultLabratory());
        setButcheryItems(defaultButchery());
        setRanchItems(defaultRanch());
        setCafeItems(defaultCafe());
        setWorkshopItems(defaultWorkShop());
        setTreesItem(defaultTrees());
        setDefaultRecipe();
        defaultPerson();
    }
    //*********************************************************
    public static ArrayList<Item> possibleMachineItems(){
        ArrayList<Item> items = new ArrayList<>();
        items.add(getBranch());
        items.add(getCheese());
        items.add(getStone());
        items.add(getApple());
        items.add(getMilk());

        return items;
    }

    public static ArrayList<Product> possibleIngredients(){
        ArrayList<Product> items = new ArrayList<>();
        items.add(getOil());
        items.add(getSalt());
        items.add(getPepper());
        items.add(getCheese());
        items.add(getMilk());
        items.add(getOnion());
        items.add(getGarlic());
        items.add(getChickenMeat());
        items.add(getCowMeat());
        items.add(getSheepMeat());
        items.add(getTomato());
        items.add(getPotato());
        items.add(getCarrot());
        items.add(getEgg());
        items.add(getEggplant());
        items.add(getCucumber());
        items.add(getFlour());
        items.add(getLemon());
        items.add(getLettuce());
        return items;
    }

    public static ArrayList<Utensil> possibleUtensils(){
        ArrayList<Utensil> items = new ArrayList<>();
        items.add(knife);
        items.add(mixer);
        items.add(fryingPan);
        items.add(pot);
        items.add(stove);
        return items;
    }
    //*************************
}

