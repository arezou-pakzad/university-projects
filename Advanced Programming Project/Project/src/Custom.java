import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class Custom {
    static Stage stage;
    static ArrayList<Item> groceryStoreItems = Holder.defaultGroceriesStoreItems();
    static ArrayList<Item> groceryStoreRemoved = new ArrayList<>();
    static ArrayList<Item> generalStoreItems = Holder.defaultGeneralStoreItems();
    static ArrayList<Item> generalStoreRemoved = new ArrayList<>();
    static ArrayList personFeatures = new ArrayList();
    static ArrayList<Eatable> medicines = new ArrayList<>();
    static ArrayList<Eatable> removedMedicines = new ArrayList<>();
    static ArrayList<Exercise> energyGym = new ArrayList<>();
    static ArrayList<Exercise> removedEnergyGym = new ArrayList<>();
    static ArrayList<Exercise> healthGym = new ArrayList<>();
    static ArrayList<Exercise> removedHealthGym = new ArrayList<>();
    static ArrayList<Machine> machines = new ArrayList<>();
    static ArrayList<Machine> removedMachines = new ArrayList<>();
    static ArrayList<Eatable> cafeFoods = new ArrayList<>();
    static ArrayList<Eatable> removedCafeFoods = new ArrayList<>();
    static ArrayList<Recipe> recipes = new ArrayList<>();
    static ArrayList<Recipe> removedRecipes = new ArrayList<>();

    public static CustomView main = new CustomView("Main") {
        @Override
        void buildScene() {
            // button haye farm, village felan ina
            Button farmButton = new Button("Farm");
            farmButton.setFont(Font.font("Comic Sans Ms", 20));
            Button farmerButton = new Button("Farmer");
            farmerButton.setFont(Font.font("Comic Sans Ms", 20));
            Button villageButton = new Button("Village");
            villageButton.setFont(Font.font("Comic Sans Ms", 20));
            Button backButton = new Button("Exit Custom");
            backButton.setFont(Font.font("Comic Sans Ms", 20));
            Button runButton = new Button("Run");
            runButton.setFont(Font.font("Comic Sans Ms", 20));
            VBox buttons = new VBox();
            buttons.setStyle("-fx-background-color: transparent;");
            buttons.setAlignment(Pos.CENTER);
            buttons.setPrefWidth(100);
            farmButton.setMinWidth(buttons.getPrefWidth());
            farmerButton.setMinWidth(buttons.getPrefWidth());
            villageButton.setMinWidth(buttons.getPrefWidth());
            runButton.setMinWidth(buttons.getPrefWidth());
            backButton.setMinWidth(buttons.getPrefWidth());
            buttons.setPadding(new Insets(20,20,20,20));
            buttons.setSpacing(10);
            buttons.getChildren().addAll(farmButton, farmerButton, villageButton, runButton, backButton);
            Scene scene1 = new Scene(buttons, Game.W, Game.H);
            scene = scene1;
            //******************************************
            farmButton.setOnAction(e -> stage.setScene(getScene("Farm")));
            farmerButton.setOnAction(e -> stage.setScene(getScene("Farmer")));
            villageButton.setOnAction(e -> stage.setScene(getScene("Village")));
            backButton.setOnAction(e -> exitCustom());
            runButton.setOnAction(e -> runCustom());
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene.setFill(imagePattern);
            scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        }
    };

    public static CustomView farm = new CustomView("Farm") {
        @Override
        void buildScene() {
            //fruit trees
            Button recipesButton = new Button("Recipes");
            Button cropsButton = new Button("Crops");
            Button treesButton = new Button("Trees");
            Button backButton = new Button("Back");
            VBox buttons = new VBox();
            buttons.setAlignment(Pos.CENTER);
            buttons.setPrefWidth(100);
            buttons.setPadding(new Insets(20,20,20,20));
            buttons.setSpacing(10);
            recipesButton.setMinWidth(buttons.getPrefWidth());
            cropsButton.setMinWidth(buttons.getPrefWidth());
            treesButton.setMinWidth(buttons.getPrefWidth());
            backButton.setMinWidth(buttons.getPrefWidth());
            buttons.getChildren().addAll(recipesButton, cropsButton, treesButton, backButton);
            Scene scene1 = new Scene(buttons, Game.W, Game.H);
            buttons.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //****************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Main")));
            recipesButton.setOnAction(e -> stage.setScene(getScene("Recipe")));
            treesButton.setOnAction(e -> stage.setScene(getScene("Trees")));
        }
    };
    public static CustomView trees = new CustomView("Trees") {
        @Override
        void buildScene() {
            Button backButton = new Button("Back");
            Button addButton = new Button("Add");

            Label subLabel = new Label("add your new Tree!");
            Label nameLabel = new Label("Name: ");
            Label fieldNumLabel = new Label("Field Number(1-6): ");
            Label treePriceLabel = new Label("Tree Price: ");
            Label fruitPriceLabel = new Label("Fruit Price: ");
            Label healthLabel = new Label("Fruit Health Raise: ");
            Label energyLabel = new Label("Fruit Energy Raise: ");
            Label maxHealthLabel = new Label("Fruit Maximum Health Raise: ");
            Label maxEnergyLabel = new Label("Fruit Maximum energy Raise: ");

            TextField nameField = new TextField("");
            TextField fieldNumField = new TextField("0");
            TextField treePriceField = new TextField("0");
            TextField fruitPriceField = new TextField("0");
            TextField healthField = new TextField("0");
            TextField maxHealthField = new TextField("0");
            TextField energyField = new TextField("0");
            TextField maxEnergyField = new TextField("0");

            ObservableList<String> options =
                    FXCollections.observableArrayList(
                            "Spring",
                            "Summer",
                            "Autumn"
                    );
            ComboBox seasonCombo = new ComboBox(options);
            seasonCombo.setValue("Spring");

            HBox nameBox = new HBox(nameLabel, nameField);
            HBox fieldNumBox = new HBox(fieldNumLabel, fieldNumField);
            HBox treePriceBox = new HBox(treePriceLabel, treePriceField);
            HBox fruitPriceBox = new HBox(fruitPriceLabel, fruitPriceField);
            HBox healthBox = new HBox(healthLabel, healthField, maxHealthLabel, maxHealthField);
            HBox energyBox = new HBox(energyLabel, energyField, maxEnergyLabel, maxEnergyField);

            VBox addBox = new VBox(subLabel, nameBox, fieldNumBox, treePriceBox, fruitPriceBox, healthBox, energyBox, addButton);
            Scene scene1 = new Scene(addBox, Game.W, Game.H);
            scene = scene1;
            //*******************************

            ArrayList<Label> addedLabels = new ArrayList<>();
            ArrayList<Label> addedCheckLabels = new ArrayList<>();
            ArrayList<Button> addedButtons = new ArrayList<>();
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Farm")));
            addButton.setOnAction(e -> {
                boolean flag = true;
                int fruitPrice;
                int treePrice;
                float healthRaise;
                float energyRaise;
                float maxHealth;
                float maxEnergy;
                try {
                    ArrayList treeFeatures = new ArrayList();
                    ArrayList eatableFeatures = new ArrayList();
                    treeFeatures.add(0, nameField.getText());
                    String season = (String)seasonCombo.getValue();
                    Season treeSeason = Season.Spring;
                    if(season.equals("Spring"))
                        treeSeason = Season.Spring;
                    else if (season.equals("Summer"))
                        treeSeason = Season.Summer;
                    else if (season.equals("Autumn"))
                        treeSeason = Season.Autumn;
                    treeFeatures.add(1, treeSeason);

                    eatableFeatures.add(0, nameField.getText());
                    eatableFeatures.add(1, "Fruit");
                    eatableFeatures.add(2, true);
                    Stat energy = new Stat("Energy");
                    Stat health = new Stat("Health");

                    healthRaise = Float.parseFloat(healthField.getText());
                    energyRaise = Float.parseFloat(energyField.getText());
                    maxHealth = Float.parseFloat(maxHealthField.getText());
                    maxEnergy = Float.parseFloat(maxEnergyField.getText());
                    energy.setCurrent(energyRaise);
                    energy.setMaximum(maxEnergy);
                    health.setCurrent(healthRaise);
                    health.setMaximum(maxHealth);

                    fruitPrice = Integer.parseInt(fruitPriceField.getText());
                    treePrice = Integer.parseInt(treePriceField.getText());

                    eatableFeatures.add(3, energy);
                    eatableFeatures.add(4, health);
                    eatableFeatures.add(5, fruitPrice);
                    Holder.customEatables.put((String)treeFeatures.get(0), eatableFeatures);
                    treeFeatures.add(2, treePrice);
                    treeFeatures.add(3, Holder.getNewEatable((String)treeFeatures.get(0)));

                    int num = Integer.parseInt(fieldNumField.getText());
                    Tree tree = new Tree((String)treeFeatures.get(0), (Season)treeFeatures.get(1), (int)treeFeatures.get(2), (Eatable)treeFeatures.get(3));
                    if (num > 0 && num <= 6)
                        Holder.treesItem.add(num, tree);
                }catch (Exception c) {
                    c.printStackTrace();
                    flag = false;
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom tree\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
                /*if (flag){
                    addedLabels.add(new Label(nameField.getText()));
                    addedCheckLabels.add(new Label("✔"));
                    Button button = new Button("Remove");
                    addedButtons.add(button);
                    HBox hBox = new HBox(addedCheckLabels.get(addedCheckLabels.size() - 1), addedLabels.get(addedLabels.size() - 1), addedButtons.get(addedButtons.size() - 1));
                    boxes.getChildren().add(hBox);
                    maxEnergyField.setText("0");
                    maxHealthField.setText("0");
                    nameField.setText("");
                    healthField.setText("0");
                    energyField.setText("0");
                    priceField.setText("0");

                    for (int i = 0; i < addedButtons.size(); i++){
                        final int I = i;
                        addedButtons.get(i).setOnAction(event -> {
                            if (addedCheckLabels.get(I).getText().equals("✔︎")){
                                addedButtons.get(I).setText("Add");
                                addedCheckLabels.get(I).setText("✘");
                                groceryStoreRemoved.add(groceryStoreItems.get(I));
                            }
                            else {
                                addedButtons.get(I).setText("Remove");
                                addedCheckLabels.get(I).setText("✔︎");
                                groceryStoreRemoved.remove(groceryStoreItems.get(I));
                            }
                        });
                    }
                }*/

            });
        }
    };

    public static CustomView recipe = new CustomView("Recipe") {
        @Override
        void buildScene() {
            Button addButton = new Button("Add");
            Button backButton = new Button("Back");

            Label subLabel = new Label("Add your new recipe!");
            subLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label nameLabel = new Label("Name: ");
            nameLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label energyLabel = new Label("Energy increase/decrease: ");
            energyLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxEnergyLabel = new Label("Energy maximum amount increase/decrease: ");
            maxEnergyLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label healthLabel = new Label("Health increase/decrease: ");
            healthLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxHealthLabel = new Label("Health maximum amount increase/decrease: ");
            maxHealthLabel.setFont(Font.font("Comic Sans Ms", 20));

            TextField nameField = new TextField("");
            nameField.setMaxWidth(100);
            TextField energyField = new TextField("0");
            energyField.setMaxWidth(50);
            TextField maxEnergyField = new TextField("0");
            maxEnergyField.setMaxWidth(50);
            TextField healthField = new TextField("0");
            healthField.setMaxWidth(50);
            TextField maxHealthField = new TextField("0");
            maxHealthField.setMaxWidth(50);
            HBox nameBox = new HBox(nameLabel, nameField);
            nameBox.setStyle("-fx-background-color: transparent;");
            nameBox.setAlignment(Pos.CENTER);
            HBox energyBox = new HBox(energyLabel, energyField);
            energyBox.setAlignment(Pos.CENTER);
            energyBox.setStyle("-fx-background-color: transparent;");
            HBox maxEnergyBox = new HBox(maxEnergyLabel, maxEnergyField);
            maxEnergyBox.setAlignment(Pos.CENTER);
            maxEnergyBox.setStyle("-fx-background-color: transparent;");
            HBox healthBox = new HBox(healthLabel, healthField);
            healthBox.setAlignment(Pos.CENTER);
            healthBox.setStyle("-fx-background-color: transparent;");
            HBox maxHealthBox = new HBox(maxHealthLabel, maxHealthField);
            maxHealthBox.setAlignment(Pos.CENTER);
            maxHealthBox.setStyle("-fx-background-color: transparent;");
            //***************
            Label ingredientLabel = new Label("Ingredients: ");
            Label utensilLabel = new Label("Utensils: ");

            ArrayList<Label> ingredientNames = new ArrayList<>();
            ArrayList<Label> utensilNames = new ArrayList<>();
            ArrayList<Label> ingredientChecks = new ArrayList<>();
            ArrayList<Label> utensilChecks = new ArrayList<>();
            ArrayList<TextField> ingredientNums = new ArrayList<>();
            ArrayList<Button> ingredientButtons = new ArrayList<>();
            ArrayList<Button> utensilButtons = new ArrayList<>();

            ArrayList<Utensil> possibleUtensils = Holder.possibleUtensils();
            ArrayList<Product> possibleIngredients = Holder.possibleIngredients();
            HashMap<Product, Integer> ingridients = new HashMap<>();
            ArrayList<Utensil> utensils = new ArrayList<>();

            HBox boxes1 = new HBox();
            boxes1.setStyle("-fx-background-color: transparent;");
            VBox ingredientBoxes = new VBox();
            VBox utensilBoxes = new VBox();

            //***************

            for (int i = 0; i < possibleIngredients.size(); i++){
                Button button = new Button("Add");
                button.setMinWidth(100);
                ingredientButtons.add(button);
                Label label = new Label(possibleIngredients.get(i).getName());
                label.setFont(Font.font("Comic Sans Ms", 20));
                ingredientNames.add(label);
                Label checkLabel = new Label("✘");//✘✔
                checkLabel.setFont(Font.font("Comic Sans Ms", 20));
                ingredientChecks.add(checkLabel);
                TextField num = new TextField("0");
                num.setMaxWidth(50);
                ingredientNums.add(num);
                HBox box = new HBox(ingredientChecks.get(i), ingredientNames.get(i), ingredientNums.get(i), ingredientButtons.get(i));
                box.setStyle("-fx-background-color: transparent;");
                ingredientBoxes.getChildren().add(box);
                final int I = i;
                ingredientButtons.get(I).setOnAction(e -> {
                    int j = 0;
                    if (ingredientChecks.get(I).getText().equals("✔")){
                        ingredientButtons.get(I).setText("Add");
                        ingridients.remove(possibleIngredients.get(I));
                        ingredientChecks.get(I).setText("✘");
                        ingredientNums.get(I).setEditable(true);
                    }
                    else {
                        try {
                            j = Integer.parseInt(ingredientNums.get(I).getText());
                            if (j < 0)
                                throw new Exception();
                            ingredientButtons.get(I).setText("Remove");
                            ingredientNums.get(I).setEditable(false);
                            ingridients.put(possibleIngredients.get(I), j);
                            ingredientChecks.get(I).setText("✔︎");
                        }catch (Exception c){
                            AlertBox error = new AlertBox();
                            error.display("Error", "Oops! your custom recipe\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                        }
                    }
                });
            }

            for (int i = 0; i < possibleUtensils.size(); i++){
                Button button = new Button("Add");
                button.setMinWidth(100);
                utensilButtons.add(button);
                Label label = new Label(possibleUtensils.get(i).getName());
                label.setFont(Font.font("Comic Sans Ms", 20));
                utensilNames.add(label);
                Label checkLabel = new Label("✘");//✘✔
                checkLabel.setFont(Font.font("Comic Sans Ms", 20));
                utensilChecks.add(checkLabel);
                HBox box = new HBox(utensilChecks.get(i), utensilNames.get(i), utensilButtons.get(i));
                box.setStyle("-fx-background-color: transparent;");
                utensilBoxes.getChildren().add(box);
                final int I = i;
                utensilButtons.get(I).setOnAction(e -> {
                    if (utensilChecks.get(I).getText().equals("✔")){
                        utensilButtons.get(I).setText("Add");
                        utensils.remove(possibleUtensils.get(I));
                        utensilChecks.get(I).setText("✘");
                    }
                    else {
                        utensilButtons.get(I).setText("Remove");
                        utensils.add(possibleUtensils.get(I));
                        utensilChecks.get(I).setText("✔");
                    }
                });
            }
            //********
            boxes1.getChildren().addAll(ingredientLabel, ingredientBoxes, utensilLabel, utensilBoxes);
            VBox inputs = new VBox(subLabel, nameBox, energyBox, maxEnergyBox, healthBox, maxHealthBox, boxes1, addButton, backButton);
            inputs.setAlignment(Pos.CENTER);
            inputs.setStyle("-fx-background-color: transparent;");
            ScrollPane scrollPane = new ScrollPane(inputs);
//            scrollPane.getStylesheets().addAll(Game.class.getResource("style.css").toExternalForm());
//            scrollPane.setStyle("-fx-control-inner-background: transparent");
            Scene scene1 = new Scene(scrollPane, Game.W, Game.H);
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //**************************************************
            ArrayList<Label> addedLabels = new ArrayList<>();
            ArrayList<Label> addedCheckLabels = new ArrayList<>();
            ArrayList<Button> addedButtons = new ArrayList<>();

            backButton.setOnAction(e -> stage.setScene(getScene("Farm")));
            addButton.setOnAction(e -> {
                boolean flag = true;
                float healthRaise;
                float energyRaise;
                float maxHealth;
                float maxEnergy;
                try {
                    ArrayList eatableFeatures = new ArrayList();
                    ArrayList recipeFeatures = new ArrayList();
                    eatableFeatures.add(0, nameField.getText());
                    eatableFeatures.add(1, "Food");
                    eatableFeatures.add(2, true);

                    Stat energy = new Stat("Energy");
                    Stat health = new Stat("Health");

                    healthRaise = Float.parseFloat(healthField.getText());
                    energyRaise = Float.parseFloat(energyField.getText());
                    maxHealth = Float.parseFloat(maxHealthField.getText());
                    maxEnergy = Float.parseFloat(maxEnergyField.getText());
                    energy.setCurrent(energyRaise);
                    energy.setMaximum(maxEnergy);
                    health.setCurrent(healthRaise);
                    health.setMaximum(maxHealth);

                    eatableFeatures.add(3, energy);
                    eatableFeatures.add(4, health);
                    eatableFeatures.add(5, 0);
                    Holder.customEatables.put((String)eatableFeatures.get(0), eatableFeatures);

                    recipeFeatures.add(0, eatableFeatures.get(0));
                    recipeFeatures.add(1, Holder.getNewEatable((String)eatableFeatures.get(0)));
                    recipeFeatures.add(2, utensils);
                    recipeFeatures.add(3, ingridients);
                    Holder.customRecipes.put((String)recipeFeatures.get(0), recipeFeatures);
                    recipes.add(Holder.getNewRecipe((String)recipeFeatures.get(0)));
                }catch (Exception c) {
                    flag = false;
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom recipe\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
                if (flag)
                {
                    addedLabels.add(new Label(nameField.getText()));
                    addedCheckLabels.add(new Label("✔"));
                    Button button = new Button("Remove");
                    addedButtons.add(button);
                    HBox hBox = new HBox(addedCheckLabels.get(addedCheckLabels.size() - 1), addedLabels.get(addedLabels.size() - 1), addedButtons.get(addedButtons.size() - 1));
                    hBox.setStyle("-fx-background-color: transparent;");
                    inputs.getChildren().add(hBox);
                    //ersale etela'at baraye sakhte ghazaye jadid va add kardane un
                    nameField.setText("");
                    energyField.setText("");
                    maxEnergyField.setText("");
                    healthField.setText("");
                    maxHealthField.setText("");

                    for (int i = 0; i < addedButtons.size(); i++){
                        final int I = i;
                        addedButtons.get(i).setOnAction(event -> {
                            if (addedCheckLabels.get(I).getText().equals("✔︎")){
                                addedButtons.get(I).setText("Add");
                                addedCheckLabels.get(I).setText("✘");
                                removedRecipes.add(recipes.get(I));
                            }
                            else {
                                addedButtons.get(I).setText("Remove");
                                addedCheckLabels.get(I).setText("✔︎");
                                removedRecipes.remove(recipes.get(I));
                            }
                        });
                    }

                    for (int i = 0; i < ingredientButtons.size(); i++){
                        ingredientButtons.get(i).setText("Add");
                        ingredientNums.get(i).setText("0");
                        ingredientChecks.get(i).setText("✘");
                    }

                    for (int i = 0; i < utensilButtons.size(); i++){
                        utensilButtons.get(i).setText("Add");
                        utensilChecks.get(i).setText("✘");
                    }
                }

            });

        }
    };
//
//    public static CustomView trees = new CustomView("Trees") {
//        @Override
//        void buildScene() {
//
//        }
//    };

    public static CustomView farmer = new CustomView("Farmer") {
        @Override
        void buildScene() {
//            Button okButton = new Button("OK");
            Button backButton = new Button("Back");
            Label nameLabel = new Label("Name: ");
            nameLabel.setAlignment(Pos.CENTER_RIGHT);
            nameLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label moneyLabel = new Label("Money: ");
            moneyLabel.setAlignment(Pos.CENTER_RIGHT);
            moneyLabel.setFont(Font.font("Comic Sans Ms", 20));
            TextField nameField = new TextField("");
            nameField.setMaxWidth(100);
            nameField.setAlignment(Pos.CENTER_LEFT);
            nameField.setFont(Font.font("Comic Sans Ms", 20));
            TextField moneyField = new TextField("0");
            moneyField.setMaxWidth(100);
            moneyField.setFont(Font.font("Comic Sans Ms", 20));
            moneyField.setAlignment(Pos.CENTER_LEFT);
//            nameField.setMinWidth(50);
            HBox nameBox = new HBox(nameLabel, nameField);
            nameBox.setAlignment(Pos.CENTER);
            HBox moneyBox = new HBox(moneyLabel, moneyField);
            moneyBox.setAlignment(Pos.CENTER);
            Label capacityLabel = new Label("Backpack Capacity: ");
            capacityLabel.setAlignment(Pos.CENTER_RIGHT);
            capacityLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label capacityAcceptance = new Label("");
            capacityAcceptance.setFont(Font.font("Comic Sans Ms", 20));
            TextField capacityField = new TextField("0");
            capacityField.setMaxWidth(100);
            capacityField.setAlignment(Pos.CENTER_LEFT);
            capacityField.setFont(Font.font("Comic Sans Ms", 20));
//            capacityField.setMaxWidth(50);
            HBox capacityBox = new HBox(capacityLabel, capacityField, capacityAcceptance);
            capacityBox.setAlignment(Pos.CENTER);
            Label healthLabel = new Label("Health: ");
            healthLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label energyLabel = new Label("Energy: ");
            energyLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label currentLabel = new Label("Current: ");
            currentLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxLabel = new Label("Maximum: ");
            maxLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label refillLabel = new Label("RefillRate: ");
            refillLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label consumptionLabel = new Label("ConsumptionRate: ");
            consumptionLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label currentLabel1 = new Label("Current: ");
            currentLabel1.setFont(Font.font("Comic Sans Ms", 20));
            Label maxLabel1 = new Label("Maximum: ");
            maxLabel1.setFont(Font.font("Comic Sans Ms", 20));
            Label refillLabel1 = new Label("RefillRate: ");
            refillLabel1.setFont(Font.font("Comic Sans Ms", 20));
            Label consumptionLabel1 = new Label("ConsumptionRate: ");
            consumptionLabel1.setFont(Font.font("Comic Sans Ms", 20));
            TextField currentHealthField = new TextField("0");
            currentHealthField.setMaxWidth(50);
            currentHealthField.setFont(Font.font("Comic Sans Ms", 20));
            TextField maxHealthField = new TextField("0");
            maxHealthField.setMaxWidth(50);
            maxHealthField.setFont(Font.font("Comic Sans Ms", 20));
            TextField refillHealthField = new TextField("0");
            refillHealthField.setMaxWidth(50);
            refillHealthField.setFont(Font.font("Comic Sans Ms", 20));
            TextField consumptionHealthField = new TextField("0");
            consumptionHealthField.setFont(Font.font("Comic Sans Ms", 20));
            consumptionHealthField.setMaxWidth(50);
            TextField currentEnergyField = new TextField("0");
            currentEnergyField.setMaxWidth(50);
            currentEnergyField.setFont(Font.font("Comic Sans Ms", 20));
            TextField maxEnergyField = new TextField("0");
            maxEnergyField.setFont(Font.font("Comic Sans Ms", 20));
            maxEnergyField.setMaxWidth(50);
            TextField refillEnergyField = new TextField("0");
            refillEnergyField.setFont(Font.font("Comic Sans Ms", 20));
            refillEnergyField.setMaxWidth(50);
            TextField consumptionEnergyField = new TextField("0");
            consumptionEnergyField.setFont(Font.font("Comic Sans Ms", 20));
            consumptionEnergyField.setMaxWidth(50);
            HBox healthBox = new HBox();
            healthBox.getChildren().addAll(healthLabel, currentLabel, currentHealthField, maxLabel, maxHealthField);
            healthBox.setAlignment(Pos.CENTER);
            HBox healthBox2 = new HBox( refillLabel, refillHealthField, consumptionLabel, consumptionHealthField);
            healthBox2.setAlignment(Pos.CENTER);
            HBox energyBox = new HBox();
            energyBox.getChildren().addAll(energyLabel, currentLabel1, currentEnergyField, maxLabel1, maxEnergyField);
            energyBox.setAlignment(Pos.CENTER);
            HBox energyBox2 = new HBox(refillLabel1, refillEnergyField, consumptionLabel1, consumptionEnergyField);
            VBox inputs = new VBox();
            energyBox2.setAlignment(Pos.CENTER);
            inputs.getChildren().addAll(nameBox, moneyBox, capacityBox, healthBox, healthBox2, energyBox,  energyBox2, backButton);
            Scene scene1 = new Scene(inputs, Game.W, Game.H);
            inputs.setStyle("-fx-background-color: transparent;");
            energyBox.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //********************************************
            backButton.setOnAction(e -> {
                personFeatures.add(0, nameField.getText());
                int money;
                int capacity;
                ArrayList<Stat> stats = new ArrayList<>();
                Stat energy = new Stat("Energy");
                Stat health = new Stat("Health");
                float current;
                float maximum;
                float refillRate;
                float consumptionRate;

                try{
                    money = Integer.parseInt(moneyField.getText());
                    capacity = Integer.parseInt(capacityField.getText());

                    current = Float.parseFloat(currentEnergyField.getText());
                    maximum = Float.parseFloat(maxEnergyField.getText());
                    refillRate = Float.parseFloat(refillEnergyField.getText());
                    consumptionRate = Float.parseFloat(consumptionEnergyField.getText());
                    energy.setCurrent(current);
                    energy.setMaximum(maximum);
                    energy.setRefillRate(refillRate);
                    energy.setConsumptionRate(consumptionRate);
                    stats.add(energy);

                    current = Float.parseFloat(currentHealthField.getText());
                    maximum = Float.parseFloat(maxHealthField.getText());
                    refillRate = Float.parseFloat(refillHealthField.getText());
                    consumptionRate = Float.parseFloat(consumptionHealthField.getText());
                    health.setCurrent(current);
                    health.setMaximum(maximum);
                    health.setRefillRate(refillRate);
                    health.setConsumptionRate(consumptionRate);
                    stats.add(health);

                    personFeatures.add(1, money);
                    personFeatures.add(2, stats);
                    personFeatures.add(3, capacity);
                    stage.setScene(getScene("Main"));
                }catch (Exception c){
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom farmer\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
            });
            //********************************************

        }
    };

    public static CustomView village = new CustomView("Village") {
        @Override
        void buildScene() {
            Button clinicButton = new Button("Clinic");
            Button groceriesStoreButton = new Button("Groceries Store");
            Button generalStoreButton = new Button("General Store");
            Button laboratoryButton = new Button("Laboratory");
            Button gymButton = new Button("Gym");
            Button cafeButton = new Button("Cafe");
            Button backButton = new Button("Back");
            VBox buttons = new VBox();
            buttons.setAlignment(Pos.CENTER);
            buttons.setPrefWidth(100);
            buttons.setPadding(new Insets(20,20,20,20));
            buttons.setSpacing(10);
            clinicButton.setMinWidth(buttons.getPrefWidth());
            groceriesStoreButton.setMinWidth(buttons.getPrefWidth());
            generalStoreButton.setMinWidth(buttons.getPrefWidth());
            laboratoryButton.setMinWidth(buttons.getPrefWidth());
            gymButton.setMinWidth(buttons.getPrefWidth());
            cafeButton.setMinWidth(buttons.getPrefWidth());
            backButton.setMinWidth(buttons.getPrefWidth());
            buttons.getChildren().addAll(clinicButton, groceriesStoreButton, generalStoreButton, laboratoryButton, gymButton, cafeButton, backButton);
            Scene scene1 = new Scene(buttons, Game.W, Game.H);
            buttons.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //****************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Main")));
            clinicButton.setOnAction(e -> stage.setScene(getScene("Clinic")));
            gymButton.setOnAction(e -> stage.setScene(getScene("Gym")));
            cafeButton.setOnAction(e -> stage.setScene(getScene("Cafe")));
            laboratoryButton.setOnAction(e -> stage.setScene(getScene("Laboratory")));
            groceriesStoreButton.setOnAction(e -> stage.setScene(getScene("Groceries Store")));
            generalStoreButton.setOnAction(e -> stage.setScene(getScene("General Store")));
        }
    };

    public static CustomView clinic = new CustomView("Clinic") {
        @Override
        void buildScene() {
            Button addButton = new Button("Add");
            Button backButton = new Button("Back");
            backButton.setMinWidth(100);
            addButton.setMinWidth(100);

            Label subLabel = new Label("Create your new medicine!");
            Label nameLabel = new Label("Name: ");
            Label healthLabel = new Label("Health Raise: ");
            Label energyLabel = new Label("Energy Raise: ");
            Label priceLabel = new Label("Price: ");

            TextField nameField = new TextField("");
            nameField.setMaxWidth(50);
            TextField healthField = new TextField("0");
            healthField.setMaxWidth(50);
            TextField energyField = new TextField("0");
            energyField.setMaxWidth(50);
            TextField priceField = new TextField("0");
            priceField.setMaxWidth(50);

            HBox nameBox = new HBox(nameLabel, nameField);
            nameBox.setAlignment(Pos.CENTER);
            HBox healthBox = new HBox();
            healthBox.setAlignment(Pos.CENTER);
            healthBox.getChildren().addAll(healthLabel, healthField);
            HBox energyBox = new HBox();
            energyBox.setAlignment(Pos.CENTER);
            energyBox.getChildren().addAll(energyLabel, energyField);
            HBox priceBox = new HBox();
            priceBox.getChildren().addAll(priceLabel, priceField);
            priceBox.setAlignment(Pos.CENTER);
            VBox inputs = new VBox();
            inputs.setSpacing(10);
            inputs.setAlignment(Pos.CENTER);
            inputs.getChildren().addAll(subLabel, nameBox, healthBox, energyBox, priceBox, addButton, backButton);
            Scene scene1 = new Scene(inputs, Game.W, Game.H);
            energyBox.setStyle("-fx-background-color: transparent;");
            nameBox.setStyle("-fx-background-color: transparent;");
            healthBox.setStyle("-fx-background-color: transparent;");
            priceBox.setStyle("-fx-background-color: transparent;");
            inputs.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //****************************************
            ArrayList<Label> addedLabels = new ArrayList<>();
            ArrayList<Label> addedCheckLabels = new ArrayList<>();
            ArrayList<Button> addedButtons = new ArrayList<>();
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Village")));
            addButton.setOnAction(e -> {
                boolean flag = true;
                int price;
                float healthRaise;
                float energyRaise;
                try {
                    ArrayList medicineFeatures = new ArrayList();
                    medicineFeatures.add(0, nameField.getText());
                    medicineFeatures.add(1, "Medicine");
                    medicineFeatures.add(2, true);

                    Stat energy = new Stat("Energy");
                    Stat health = new Stat("Health");

                    healthRaise = Float.parseFloat(healthField.getText());
                    energyRaise = Float.parseFloat(energyField.getText());
                    energy.setCurrent(energyRaise);
                    health.setCurrent(healthRaise);

                    price = Integer.parseInt(priceField.getText());

                    medicineFeatures.add(3, energy);
                    medicineFeatures.add(4, health);
                    medicineFeatures.add(5, price);
                    Holder.customEatables.put((String)medicineFeatures.get(0), medicineFeatures);
                    medicines.add(Holder.getNewEatable((String)medicineFeatures.get(0)));
                }catch (Exception c) {
                    flag = false;
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom medicine\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
                if (flag){
                    addedLabels.add(new Label(nameField.getText()));
                    addedCheckLabels.add(new Label("✔"));
                    Button button = new Button("Remove");
                    addedButtons.add(button);
                    HBox hBox = new HBox(addedCheckLabels.get(addedCheckLabels.size() - 1), addedLabels.get(addedLabels.size() - 1), addedButtons.get(addedButtons.size() - 1));
                    inputs.getChildren().add(hBox);
                    priceField.setText("");
                    nameField.setText("");
                    healthField.setText("");
                    energyField.setText("");

                    for (int i = 0; i < addedButtons.size(); i++){
                        final int I = i;
                        addedButtons.get(i).setOnAction(event -> {
                            if (addedCheckLabels.get(I).getText().equals("✔︎")){
                                addedButtons.get(I).setText("Add");
                                addedCheckLabels.get(I).setText("✘");
                                removedMedicines.add(medicines.get(I));
                            }
                            else {
                                addedButtons.get(I).setText("Remove");
                                addedCheckLabels.get(I).setText("✔");
                                removedMedicines.remove(medicines.get(I));
                            }
                        });
                    }
                }
            });
        }
    };

    public static CustomView groceriesStore = new CustomView("Groceries Store") {
        @Override
        void buildScene() {
            Button backButton = new Button("Back");
            Button addButton = new Button("Add");

            Label subLabel = new Label("add your new Fruit or Vegetable to the list!");
            subLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label nameLabel = new Label("Name: ");
            nameLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label priceLabel = new Label("Price: ");
            priceLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label healthLabel = new Label("Health Raise: ");
            healthLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label energyLabel = new Label("Energy Raise: ");
            energyLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxHealthLabel = new Label("Maximum Health Raise: ");
            maxHealthLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxEnergyLabel = new Label("Maximum energy Raise: ");
            maxEnergyLabel.setFont(Font.font("Comic Sans Ms", 20));

            TextField nameField = new TextField("");
            nameField.setFont(Font.font("Comic Sans Ms", 20));
            nameField.setMaxWidth(100);
            TextField priceField = new TextField("0");
            priceField.setMaxWidth(50);
            priceField.setFont(Font.font("Comic Sans Ms", 20));
            TextField healthField = new TextField("0");
            healthField.setMaxWidth(50);
            healthField.setFont(Font.font("Comic Sans Ms", 20));
            TextField maxHealthField = new TextField("0");
            maxHealthField.setMaxWidth(50);
            maxHealthField.setFont(Font.font("Comic Sans Ms", 20));
            TextField energyField = new TextField("0");
            energyField.setMaxWidth(50);
            energyField.setFont(Font.font("Comic Sans Ms", 20));
            TextField maxEnergyField = new TextField("0");
            maxEnergyField.setMaxWidth(50);
            maxEnergyField.setFont(Font.font("Comic Sans Ms", 20));

            ObservableList<String> options =
                    FXCollections.observableArrayList(
                            "Fruit",
                            "Vegetable"
                    );
            ComboBox fruitVegetableCombo = new ComboBox(options);
            fruitVegetableCombo.setValue("Fruit");

            HBox nameBox = new HBox(nameLabel, nameField);
            nameBox.setStyle("-fx-background-color: transparent;");
            nameBox.setAlignment(Pos.CENTER);
            HBox priceBox = new HBox(priceLabel, priceField);
            priceBox.setAlignment(Pos.CENTER);
            priceBox.setStyle("-fx-background-color: transparent;");
            HBox healthBox = new HBox(healthLabel, healthField);
            healthBox.setAlignment(Pos.CENTER);
            healthBox.setStyle("-fx-background-color: transparent;");
            HBox maxHealthBox = new HBox(maxHealthLabel, maxHealthField);
            maxHealthBox.setAlignment(Pos.CENTER);
            maxHealthBox.setStyle("-fx-background-color: transparent;");
            HBox energyBox = new HBox(energyLabel, energyField, maxEnergyLabel, maxEnergyField);
            energyBox.setAlignment(Pos.CENTER);
            energyBox.setStyle("-fx-background-color: transparent;");
            VBox addBox = new VBox(subLabel, fruitVegetableCombo, nameBox, priceBox, healthBox, maxHealthBox, energyBox, addButton);
            addBox.setAlignment(Pos.CENTER);
            addBox.setStyle("-fx-background-color: transparent;");
            ArrayList<Button> buttons = new ArrayList<>();
            ArrayList<Label> labels = new ArrayList<>();
            ArrayList<Label> checkLabels = new ArrayList<>();
            VBox boxes = new VBox();
            boxes.setAlignment(Pos.CENTER);
            boxes.setStyle("-fx-background-color: transparent;");
            for (int i = 0; i < groceryStoreItems.size(); i++){
                Button button = new Button("Remove");
                buttons.add(button);
                Label label = new Label(groceryStoreItems.get(i).getName());
                labels.add(label);
                Label checkLabel = new Label("✔");//✘
                checkLabels.add(checkLabel);
                HBox box = new HBox(checkLabels.get(i), labels.get(i), buttons.get(i));
                box.setAlignment(Pos.CENTER);
                boxes.getChildren().add(box);
                final int I = i;
                buttons.get(I).setOnAction(e -> {
                    if (checkLabels.get(I).getText().equals("✔")){
                        buttons.get(I).setText("Add");
                        groceryStoreRemoved.add(groceryStoreItems.get(I));
                        checkLabels.get(I).setText("✘");
                    }
                    else {
                        buttons.get(I).setText("Remove");
                        groceryStoreRemoved.remove(groceryStoreItems.get(I));
                        checkLabels.get(I).setText("✔");
                    }
                });
            }
            boxes.getChildren().addAll(addBox ,backButton);
            ScrollPane scrollPane = new ScrollPane(boxes);
            Scene scene1 = new Scene(scrollPane, Game.W, Game.H);
            energyBox.setStyle("-fx-background-color: transparent;");
            nameBox.setStyle("-fx-background-color: transparent;");
            healthBox.setStyle("-fx-background-color: transparent;");
            priceBox.setStyle("-fx-background-color: transparent;");
            boxes.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //****************************************
            ArrayList<Label> addedLabels = new ArrayList<>();
            ArrayList<Label> addedCheckLabels = new ArrayList<>();
            ArrayList<Button> addedButtons = new ArrayList<>();
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Village")));
            addButton.setOnAction(e -> {
                boolean flag = true;
                int price;
                float healthRaise;
                float energyRaise;
                float maxHealth;
                float maxEnergy;
                try {
                    ArrayList eatableFeatures = new ArrayList();
                    eatableFeatures.add(0, nameField.getText());
                    eatableFeatures.add(1, fruitVegetableCombo.getValue());
                    eatableFeatures.add(2, true);

                    Stat energy = new Stat("Energy");
                    Stat health = new Stat("Health");

                    healthRaise = Float.parseFloat(healthField.getText());
                    energyRaise = Float.parseFloat(energyField.getText());
                    maxHealth = Float.parseFloat(maxHealthField.getText());
                    maxEnergy = Float.parseFloat(maxEnergyField.getText());
                    energy.setCurrent(energyRaise);
                    energy.setMaximum(maxEnergy);
                    health.setCurrent(healthRaise);
                    health.setMaximum(maxHealth);

                    price = Integer.parseInt(priceField.getText());

                    eatableFeatures.add(3, energy);
                    eatableFeatures.add(4, health);
                    eatableFeatures.add(5, price);
                    Holder.customEatables.put((String)eatableFeatures.get(0), eatableFeatures);
                    groceryStoreItems.add(Holder.getNewEatable((String)eatableFeatures.get(0)));
                }catch (Exception c) {
                    flag = false;
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom fruit/vegetable\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
                if (flag){
                    addedLabels.add(new Label(nameField.getText()));
                    addedCheckLabels.add(new Label("✔"));
                    Button button = new Button("Remove");
                    addedButtons.add(button);
                    HBox hBox = new HBox(addedCheckLabels.get(addedCheckLabels.size() - 1), addedLabels.get(addedLabels.size() - 1), addedButtons.get(addedButtons.size() - 1));
                    boxes.getChildren().add(hBox);
                    maxEnergyField.setText("");
                    maxHealthField.setText("");
                    nameField.setText("");
                    healthField.setText("");
                    energyField.setText("");
                    priceField.setText("");

                    for (int i = 0; i < addedButtons.size(); i++){
                        final int I = i;
                        addedButtons.get(i).setOnAction(event -> {
                            if (addedCheckLabels.get(I).getText().equals("✔︎")){
                                addedButtons.get(I).setText("Add");
                                addedCheckLabels.get(I).setText("✘");
                                groceryStoreRemoved.add(groceryStoreItems.get(I));
                            }
                            else {
                                addedButtons.get(I).setText("Remove");
                                addedCheckLabels.get(I).setText("✔︎");
                                groceryStoreRemoved.remove(groceryStoreItems.get(I));
                            }
                        });
                    }
                }

            });
        }
    };

    public static CustomView generalStore = new CustomView("General Store") {
        @Override
        void buildScene() {
            Button backButton = new Button("Back");

            ArrayList<Button> buttons = new ArrayList<>();
            ArrayList<Label> labels = new ArrayList<>();
            ArrayList<Label> checkLabels = new ArrayList<>();
            VBox boxes = new VBox();
            for (int i = 0; i < generalStoreItems.size(); i++){
                Button button = new Button("Remove");
                buttons.add(button);
                Label label = new Label(generalStoreItems.get(i).getName());
                labels.add(label);
                Label checkLabel = new Label("✔︎");//✘
                checkLabels.add(checkLabel);
                HBox box = new HBox(checkLabels.get(i), labels.get(i), buttons.get(i));
                box.setAlignment(Pos.CENTER);
                box.setStyle("-fx-background-color: transparent;");
                boxes.getChildren().add(box);
                final int I = i;
                buttons.get(I).setOnAction(e -> {
                    if (checkLabels.get(I).getText().equals("✔︎")){
                        buttons.get(I).setText("Add");
                        generalStoreRemoved.add(generalStoreItems.get(I));
                        checkLabels.get(I).setText("✘");
                    }
                    else {
                        buttons.get(I).setText("Remove");
                        generalStoreRemoved.remove(groceryStoreItems.get(I));
                        checkLabels.get(I).setText("✔︎");
                    }
                });
            }
            boxes.getChildren().add(backButton);
            Scene scene1 = new Scene(boxes, Game.W, Game.H);
            boxes.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Village")));
        }
    };

    public static CustomView laboratory = new CustomView("Laboratory") {
        @Override
        void buildScene() {
            Button backButton = new Button("Back");
            Button addButton = new Button("Add");
            Label subLabel = new Label("Create your new machine!");
            subLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label priceLabel = new Label("Price: ");
            priceLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label nameLabel = new Label("Name: ");
            nameLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label inputLabel = new Label("Inputs: ");
            inputLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label outputLabel = new Label("Outputs: ");
            outputLabel.setFont(Font.font("Comic Sans Ms", 20));

            TextField priceField = new TextField("0");
            TextField nameField = new TextField("");
            priceField.setAlignment(Pos.CENTER);
            nameField.setAlignment(Pos.CENTER);
            nameField.setMaxWidth(100);
            priceField.setMaxWidth(50);
            ArrayList<Label> inputNames = new ArrayList<>();
            ArrayList<Label> outputNames = new ArrayList<>();
            ArrayList<Label> inputChecks = new ArrayList<>();
            ArrayList<Label> outputChecks = new ArrayList<>();
            ArrayList<TextField> inputNums = new ArrayList<>();
            ArrayList<TextField> outputNums = new ArrayList<>();
            ArrayList<Button> inputButtons = new ArrayList<>();
            ArrayList<Button> outputButtons = new ArrayList<>();

            VBox boxes = new VBox();
            boxes.setAlignment(Pos.CENTER);
            HBox boxes1 = new HBox();
            boxes1.setAlignment(Pos.CENTER);
            VBox inputBoxes = new VBox();
            inputBoxes.setAlignment(Pos.CENTER);
            VBox outputBoxes = new VBox();
            outputBoxes.setAlignment(Pos.CENTER);

            ArrayList<Item> items = Holder.possibleMachineItems();
            HashMap<Item, Integer> inputs = new HashMap<>();
            HashMap<Item, Integer> outputs = new HashMap<>();

            for (int i = 0; i < items.size(); i++){
                Button button = new Button("Add");
                inputButtons.add(button);
                Label label = new Label(items.get(i).getName());
                inputNames.add(label);
                Label checkLabel = new Label("✘");//✘✔
                inputChecks.add(checkLabel);
                TextField num = new TextField("0");
                num.setMaxWidth(50);
                inputNums.add(num);
                HBox box = new HBox(inputChecks.get(i), inputNames.get(i), inputNums.get(i), inputButtons.get(i));
                inputBoxes.getChildren().add(box);
                final int I = i;
                inputButtons.get(I).setOnAction(e -> {
                    boolean flag = true;
                    int j = 0;
                    if (inputChecks.get(I).getText().equals("✔")){
                        inputButtons.get(I).setText("Add");
                        inputs.remove(items.get(I));
                        inputChecks.get(I).setText("✘");
                        inputNums.get(I).setEditable(true);
                    }
                    try {
                        j = Integer.parseInt(inputNums.get(I).getText());
                        if (j < 0)
                            throw new Exception();
                        inputButtons.get(I).setText("Remove");
                        inputNums.get(I).setEditable(false);
                        inputs.put(items.get(I), j);
                        inputChecks.get(I).setText("✔");
                    }catch (Exception c){
                        AlertBox error = new AlertBox();
                        error.display("Error", "Oops! your custom machine\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                    }
                });
            }

            for (int i = 0; i < items.size(); i++){
                Button button = new Button("Add");
                outputButtons.add(button);
                Label label = new Label(items.get(i).getName());
                outputNames.add(label);
                Label checkLabel = new Label("✘");//✘✔
                outputChecks.add(checkLabel);
                TextField num = new TextField("0");
                outputNums.add(num);
                num.setMaxWidth(50);
                HBox box = new HBox(outputChecks.get(i), outputNames.get(i), outputNums.get(i), outputButtons.get(i));
                outputBoxes.getChildren().add(box);
                final int I = i;
                outputButtons.get(I).setOnAction(e -> {
                    boolean flag = true;
                    int j = 0;
                    if (outputChecks.get(I).getText().equals("✔︎")){
                        outputButtons.get(I).setText("Add");
                        outputs.remove(items.get(I));
                        outputChecks.get(I).setText("✘");
                        outputNums.get(I).setEditable(true);
                    }
                    else {
                        try {
                            j = Integer.parseInt(outputNums.get(I).getText());
                            if (j < 0)
                                throw new Exception();
                            outputButtons.get(I).setText("Remove");
                            outputNums.get(I).setEditable(false);
                            outputs.put(items.get(I), j);
                            outputChecks.get(I).setText("✔︎");
                        }catch (Exception c){
                            AlertBox error = new AlertBox();
                            error.display("Error", "Oops! your custom machine\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                        }
                    }
                });
            }

            boxes1.getChildren().addAll( nameLabel, nameField);
            HBox box = new HBox(inputLabel, inputBoxes, outputLabel, outputBoxes);
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-background-color: transparent;");
            HBox hBox1 = new HBox(priceLabel, priceField);
            hBox1.setAlignment(Pos.CENTER);
            hBox1.setStyle("-fx-background-color: transparent;");
            boxes.getChildren().addAll(subLabel, boxes1,hBox1, box,addButton, backButton);
            Scene scene1 = new Scene(boxes, Game.W, Game.H);
            boxes1.setStyle("-fx-background-color: transparent;");
            boxes.setStyle("-fx-background-color: transparent;");
            inputBoxes.setStyle("-fx-background-color: transparent;");
            outputBoxes.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //****************************************
            ArrayList<Label> addedLabels = new ArrayList<>();
            ArrayList<Label> addedCheckLabels = new ArrayList<>();
            ArrayList<Button> addedButtons = new ArrayList<>();
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Village")));
            addButton.setOnAction(e -> {
                ArrayList machineFeatures = new ArrayList();
                boolean flag = true;
                int price;
                try {
                    price = Integer.parseInt(priceField.getText());
                    machineFeatures.add(0, nameField.getText());
                    machineFeatures.add(1, inputs);
                    machineFeatures.add(2, outputs);
                    machineFeatures.add(3, price);
                    Holder.customMachines.put((String)machineFeatures.get(0), machineFeatures);
                    machines.add(Holder.getNewMachine((String)machineFeatures.get(0)));
                }
                catch (Exception c){
                    flag = false;
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom machine\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
                if (flag){
                    addedLabels.add(new Label(nameField.getText()));
                    addedCheckLabels.add(new Label("✔"));
                    Button button = new Button("Remove");
                    addedButtons.add(button);
                    HBox hBox = new HBox(addedCheckLabels.get(addedCheckLabels.size() - 1), addedLabels.get(addedLabels.size() - 1), addedButtons.get(addedButtons.size() - 1));
                    boxes.getChildren().add(hBox);
                    priceField.setText("");
                    nameField.setText("");
                    for (int i = 0; i < inputNums.size(); i++){
                        inputNums.get(i).setText("");
                        outputNums.get(i).setText("");
                    }

                    for (int i = 0; i < addedButtons.size(); i++){
                        final int I = i;
                        addedButtons.get(i).setOnAction(event -> {
                            if (addedCheckLabels.get(I).getText().equals("✔")){
                                addedButtons.get(I).setText("Add");
                                addedCheckLabels.get(I).setText("✘");
                                removedMachines.add(machines.get(I));
                            }
                            else {
                                addedButtons.get(I).setText("Remove");
                                addedCheckLabels.get(I).setText("✔");
                                removedMachines.remove(machines.get(I));
                            }
                        });
                    }

                    for (int i = 0; i < inputButtons.size(); i++){
                        inputButtons.get(i).setText("Add");
                        inputNums.get(i).setText("0");
                        inputChecks.get(i).setText("✘");
                        outputButtons.get(i).setText("Add");
                        outputNums.get(i).setText("0");
                        outputChecks.get(i).setText("✘");
                    }
                }


            });
        }
    };

    public static CustomView gym = new CustomView("Gym") {
        @Override
        void buildScene() {
            Button healthButton = new Button("Health Exercise");
            Button energyButton = new Button("Energy Exercise");
            Button backButton = new Button("Back");
            VBox buttons = new VBox();
            buttons.setAlignment(Pos.CENTER);
            buttons.setSpacing(10);
            buttons.getChildren().addAll(healthButton, energyButton, backButton);
            Scene scene1 = new Scene(buttons, Game.W, Game.H);
            buttons.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Village")));
            healthButton.setOnAction(e -> stage.setScene(getScene("Health Exercise")));
            energyButton.setOnAction(e -> stage.setScene(getScene("Energy Exercise")));
        }
    };

    public static CustomView healthExercise = new CustomView("Health Exercise") {
        @Override
        void buildScene() {
            Button backButton = new Button("Back");
            Button addButton = new Button("Add");

            Label subLabel = new Label("Create your new health exercise!");
            subLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label priceLabel = new Label("Price: ");
            priceLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label nameLabel = new Label("Name: ");
            nameLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label energyDecLabel = new Label("Energy Decrease: ");
            energyDecLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxLabel = new Label("Maximum Amount increase per exercise: ");
            maxLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label refillRateLabel = new Label("Refill Rate increase per exercise: ");
            refillRateLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label consumptionRateLabel = new Label("Consumption Rate increase per exercise: ");
            consumptionRateLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label priceUpdateLabel = new Label("Price update: ");
            priceUpdateLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label energyUpdateLabel = new Label("Energy update: ");
            energyUpdateLabel.setFont(Font.font("Comic Sans Ms", 20));

            TextField nameField = new TextField("");
            nameField.setMaxWidth(100);
            TextField priceField = new TextField("0");
            priceField.setMaxWidth(100);
            TextField energyDecField = new TextField("0");
            energyDecField.setMaxWidth(100);
            TextField maxField = new TextField("0");
            maxField.setMaxWidth(100);
            TextField refillRateField = new TextField("0");
            refillRateField.setMaxWidth(100);
            TextField consumptionRateField = new TextField("0");
            consumptionRateField.setMaxWidth(100);
            TextField priceUpdateField = new TextField("0");
            priceUpdateField.setMaxWidth(100);
            TextField energyUpdateField = new TextField("0");
            energyUpdateField.setMaxWidth(100);

            HBox nameBox = new HBox(nameLabel, nameField);
            nameBox.setAlignment(Pos.CENTER);
            HBox priceBox = new HBox(priceLabel, priceField);
            priceBox.setAlignment(Pos.CENTER);
            HBox energyDecBox = new HBox(energyDecLabel, energyDecField);
            energyDecBox.setAlignment(Pos.CENTER);
            HBox maxBox = new HBox(maxLabel, maxField);
            maxBox.setAlignment(Pos.CENTER);
            HBox refillRateBox = new HBox(refillRateLabel, refillRateField);
            refillRateBox.setAlignment(Pos.CENTER);
            HBox consumptionRateBox = new HBox(consumptionRateLabel, consumptionRateField);
            consumptionRateBox.setAlignment(Pos.CENTER);
            HBox priceUpdateBox = new HBox(priceUpdateLabel, priceUpdateField);
            priceBox.setAlignment(Pos.CENTER);
            HBox energyUpdateBox = new HBox(energyUpdateLabel, energyUpdateField);
            energyDecBox.setAlignment(Pos.CENTER);
            VBox inputs = new VBox(subLabel, nameBox, priceBox, energyDecBox, maxBox, refillRateBox, consumptionRateBox, priceUpdateBox, energyUpdateBox, addButton, backButton);
            inputs.setAlignment(Pos.CENTER);
            inputs.setStyle("-fx-background-color: transparent;");
            Scene scene1 = new Scene(inputs, Game.W, Game.H);
            nameBox.setStyle("-fx-background-color: transparent;");
            priceBox.setStyle("-fx-background-color: transparent;");
            energyDecBox.setStyle("-fx-background-color: transparent;");
            maxBox.setStyle("-fx-background-color: transparent;");
            refillRateBox.setStyle("-fx-background-color: transparent;");
            consumptionRateBox.setStyle("-fx-background-color: transparent;");
            priceUpdateBox.setStyle("-fx-background-color: transparent;");
            priceUpdateBox.setAlignment(Pos.CENTER);
            energyUpdateBox.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //****************************************
            ArrayList<Label> addedLabels = new ArrayList<>();
            ArrayList<Label> addedCheckLabels = new ArrayList<>();
            ArrayList<Button> addedButtons = new ArrayList<>();
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Gym")));
            addButton.setOnAction(e -> {
                boolean flag = true;
                int price;
                float refillHealth;
                float maxHealth;
                float consumptionHealth;
                float energyDec;
                int priceUpdate;
                int energyUpdate;
                try {
                    ArrayList exerciseFeatures = new ArrayList();
                    exerciseFeatures.add(0, nameField.getText());

                    Stat health = new Stat("Health");

                    price = Integer.parseInt(priceField.getText());
                    energyDec = Float.parseFloat(energyDecField.getText());
                    maxHealth = Float.parseFloat(maxField.getText());
                    refillHealth = Float.parseFloat(refillRateField.getText());
                    consumptionHealth = Float.parseFloat(consumptionRateField.getText());
                    priceUpdate = Integer.parseInt(priceUpdateField.getText());
                    energyUpdate = Integer.parseInt(energyUpdateField.getText());
                    health.setMaximum(maxHealth);
                    health.setRefillRate(refillHealth);
                    health.setConsumptionRate(consumptionHealth);

                    exerciseFeatures.add(1, health);
                    exerciseFeatures.add(2, energyDec);
                    exerciseFeatures.add(3, price);
                    exerciseFeatures.add(4, priceUpdate);
                    exerciseFeatures.add(5, energyUpdate);
                    Holder.customExercise.put((String)exerciseFeatures.get(0), exerciseFeatures);
                    healthGym.add(Holder.getNewExercise((String)exerciseFeatures.get(0)));
                }catch (Exception c) {
                    flag = false;
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom exercise\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
                if (flag){
                    addedLabels.add(new Label(nameField.getText()));
                    addedCheckLabels.add(new Label("✔"));
                    Button button = new Button("Remove");
                    addedButtons.add(button);
                    HBox hBox = new HBox(addedCheckLabels.get(addedCheckLabels.size() - 1), addedLabels.get(addedLabels.size() - 1), addedButtons.get(addedButtons.size() - 1));
                    inputs.getChildren().add(hBox);
                    //ersale etela'at baraye sakhte ghazaye jadid va add kardane un
                    priceField.setText("");
                    nameField.setText("");
                    energyDecField.setText("");
                    maxField.setText("");
                    refillRateField.setText("");
                    consumptionRateField.setText("");

                    for (int i = 0; i < addedButtons.size(); i++){
                        final int I = i;
                        addedButtons.get(i).setOnAction(event -> {
                            if (addedCheckLabels.get(I).getText().equals("✔︎")){
                                addedButtons.get(I).setText("Add");
                                addedCheckLabels.get(I).setText("✘");
                                removedHealthGym.add(healthGym.get(I));
                            }
                            else {
                                addedButtons.get(I).setText("Remove");
                                addedCheckLabels.get(I).setText("✔︎");
                                removedHealthGym.remove(healthGym.get(I));
                            }
                        });
                    }
                }

            });

        }
    };

    public static CustomView energyExercise = new CustomView("Energy Exercise") {
        @Override
        void buildScene() {
            Button backButton = new Button("Back");
            Button addButton = new Button("Add");

            Label subLabel = new Label("Create your new energy exercise!");
            subLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label nameLabel = new Label("Name: ");
            nameLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label priceLabel = new Label("Price: ");
            priceLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label energyDecLabel = new Label("Energy Decrease: ");
            energyDecLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxLabel = new Label("Maximum Amount increase per exercise: ");
            maxLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label refillRateLabel = new Label("Refill Rate increase per exercise: ");
            refillRateLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label consumptionRateLabel = new Label("Consumption Rate increase per exercise: ");
            consumptionRateLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label priceUpdateLabel = new Label("Price update: ");
            priceUpdateLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label energyUpdateLabel = new Label("Energy update: ");
            energyDecLabel.setFont(Font.font("Comic Sans Ms", 20));

            TextField nameField = new TextField("");
            nameField.setMaxWidth(100);
            TextField priceField = new TextField("0");
            priceField.setMaxWidth(50);
            TextField energyDecField = new TextField("0");
            energyDecField.setMaxWidth(50);
            TextField maxField = new TextField("0");
            maxField.setMaxWidth(50);
            TextField refillRateField = new TextField("0");
            refillRateField.setMaxWidth(50);
            TextField consumptionRateField = new TextField("0");
            consumptionRateField.setMaxWidth(50);
            TextField priceUpdateField = new TextField("0");
            priceUpdateField.setMaxWidth(50);
            TextField energyUpdateField = new TextField("0");
            energyUpdateField.setMaxWidth(50);

            HBox nameBox = new HBox(nameLabel, nameField);
            nameBox.setAlignment(Pos.CENTER);
            HBox priceBox = new HBox(priceLabel, priceField);
            priceBox.setAlignment(Pos.CENTER);
            HBox energyDecBox = new HBox(energyDecLabel, energyDecField);
            energyDecBox.setAlignment(Pos.CENTER);
            HBox maxBox = new HBox(maxLabel, maxField);
            maxBox.setAlignment(Pos.CENTER);
            HBox refillRateBox = new HBox(refillRateLabel, refillRateField);
            refillRateBox.setAlignment(Pos.CENTER);
            HBox consumptionRateBox = new HBox(consumptionRateLabel, consumptionRateField);
            consumptionRateBox.setAlignment(Pos.CENTER);
            HBox priceUpdateBox = new HBox(priceUpdateLabel, priceUpdateField);
            priceUpdateBox.setAlignment(Pos.CENTER);
            HBox energyUpdateBox = new HBox(energyUpdateLabel, energyUpdateField);
            energyDecBox.setAlignment(Pos.CENTER);
            VBox inputs = new VBox(subLabel, nameBox, priceBox, energyDecBox, maxBox, refillRateBox, consumptionRateBox, priceUpdateBox, energyUpdateBox, addButton, backButton);
            inputs.setAlignment(Pos.CENTER);
            Scene scene1 = new Scene(inputs, Game.W, Game.H);
            inputs.setAlignment(Pos.CENTER);
            inputs.setStyle("-fx-background-color: transparent;");
            nameBox.setStyle("-fx-background-color: transparent;");
            priceBox.setStyle("-fx-background-color: transparent;");
            energyDecBox.setStyle("-fx-background-color: transparent;");
            maxBox.setStyle("-fx-background-color: transparent;");
            refillRateBox.setStyle("-fx-background-color: transparent;");
            consumptionRateBox.setStyle("-fx-background-color: transparent;");
            priceUpdateBox.setStyle("-fx-background-color: transparent;");
            energyUpdateBox.setStyle("-fx-background-color: transparent;");
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //****************************************
            ArrayList<Label> addedLabels = new ArrayList<>();
            ArrayList<Label> addedCheckLabels = new ArrayList<>();
            ArrayList<Button> addedButtons = new ArrayList<>();
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Gym")));
            addButton.setOnAction(e -> {
                boolean flag = true;
                int price;
                float refillHealth;
                float maxHealth;
                float consumptionHealth;
                float energyDec;
                int priceUpdate;
                int energyUpdate;
                try {
                    ArrayList exerciseFeatures = new ArrayList();
                    exerciseFeatures.add(0, nameField.getText());

                    Stat energy = new Stat("Energy");

                    price = Integer.parseInt(priceField.getText());
                    energyDec = Float.parseFloat(energyDecField.getText());
                    maxHealth = Float.parseFloat(maxField.getText());
                    refillHealth = Float.parseFloat(refillRateField.getText());
                    consumptionHealth = Float.parseFloat(consumptionRateField.getText());
                    priceUpdate = Integer.parseInt(priceUpdateField.getText());
                    energyUpdate = Integer.parseInt(energyUpdateField.getText());
                    energy.setMaximum(maxHealth);
                    energy.setRefillRate(refillHealth);
                    energy.setConsumptionRate(consumptionHealth);

                    exerciseFeatures.add(1, energy);
                    exerciseFeatures.add(2, energyDec);
                    exerciseFeatures.add(3, price);
                    exerciseFeatures.add(4, priceUpdate);
                    exerciseFeatures.add(5, energyUpdate);
                    Holder.customExercise.put((String)exerciseFeatures.get(0), exerciseFeatures);
                    energyGym.add(Holder.getNewExercise((String)exerciseFeatures.get(0)));
                }catch (Exception c) {
                    flag = false;
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom exercise\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
                if (flag)
                {
                    addedLabels.add(new Label(nameField.getText()));
                    addedCheckLabels.add(new Label("✔"));
                    Button button = new Button("Remove");
                    addedButtons.add(button);
                    HBox hBox = new HBox(addedCheckLabels.get(addedCheckLabels.size() - 1), addedLabels.get(addedLabels.size() - 1), addedButtons.get(addedButtons.size() - 1));
                    inputs.getChildren().add(hBox);
                    //ersale etela'at baraye sakhte ghazaye jadid va add kardane un
                    priceField.setText("");
                    nameField.setText("");
                    energyDecField.setText("");
                    maxField.setText("");
                    refillRateField.setText("");
                    consumptionRateField.setText("");

                    for (int i = 0; i < addedButtons.size(); i++){
                        final int I = i;
                        addedButtons.get(I).setOnAction(event -> {
                            if (addedCheckLabels.get(I).getText().equals("✔︎")){
                                addedButtons.get(I).setText("Add");
                                addedCheckLabels.get(I).setText("✘");
                                removedEnergyGym.add(energyGym.get(I));
                            }
                            else {
                                addedButtons.get(I).setText("Remove");
                                addedCheckLabels.get(I).setText("✔︎");
                                removedEnergyGym.remove(energyGym.get(I));
                            }
                        });
                    }
                }

            });

        }
    };


    public static CustomView cafe = new CustomView("Cafe") {
        @Override
        void buildScene() {
            Button menuButton = new Button("Menu");
            Button missionsButton = new Button("Missions");
            Button backButton = new Button("Back");
            VBox buttons = new VBox();
            buttons.getChildren().addAll(menuButton, missionsButton, backButton);
            Scene scene1 = new Scene(buttons, Game.W, Game.H);
            buttons.setStyle("-fx-background-color: transparent;");
            buttons.setAlignment(Pos.CENTER);
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Village")));
            menuButton.setOnAction(e -> stage.setScene(getScene("Menu")));
            missionsButton.setOnAction(e -> stage.setScene(getScene("Missions")));
        }
    };

    public static CustomView menu = new CustomView("Menu") {
        @Override
        void buildScene() {
            Button addButton = new Button("Add");
            Button backButton = new Button("Back");

            Label subLabel = new Label("Add your new food to the menu!");
            subLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label nameLabel = new Label("Name: ");
            nameLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label priceLabel = new Label("Price: ");
            priceLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label energyLabel = new Label("Energy increase/decrease: ");
            energyLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxEnergyLabel = new Label("Energy maximum amount increase/decrease: ");
            maxEnergyLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label healthLabel = new Label("Health increase/decrease: ");
            healthLabel.setFont(Font.font("Comic Sans Ms", 20));
            Label maxHealthLabel = new Label("Health maximum amount increase/decrease: ");
            maxHealthLabel.setFont(Font.font("Comic Sans Ms", 20));

            TextField priceField = new TextField("0");
            priceField.setMaxWidth(50);
            TextField nameField = new TextField("");
            nameField.setMaxWidth(100);
            TextField energyField = new TextField("0");
            energyField.setMaxWidth(50);
            TextField maxEnergyField = new TextField("0");
            maxEnergyField.setMaxWidth(50);
            TextField healthField = new TextField("0");
            healthField.setMaxWidth(50);
            TextField maxHealthField = new TextField("0");
            maxHealthField.setMaxWidth(50);

            HBox priceBox = new HBox(priceLabel, priceField);
            HBox nameBox = new HBox(nameLabel, nameField);
            HBox energyBox = new HBox(energyLabel, energyField);
            HBox maxEnergyBox = new HBox(maxEnergyLabel, maxEnergyField);
            HBox healthBox = new HBox(healthLabel, healthField);
            HBox maxHealthBox = new HBox(maxHealthLabel, maxHealthField);

            VBox inputs = new VBox(subLabel, priceBox, nameBox, energyBox, maxEnergyBox, healthBox, maxHealthBox, addButton, backButton);
            inputs.setAlignment(Pos.CENTER);
            inputs.setStyle("-fx-background-color: transparent;");
            Scene scene1 = new Scene(inputs, Game.W, Game.H);
            priceBox.setStyle("-fx-background-color: transparent;");
            priceBox.setAlignment(Pos.CENTER);
            nameBox.setStyle("-fx-background-color: transparent;");
            nameBox.setAlignment(Pos.CENTER);
            energyBox.setStyle("-fx-background-color: transparent;");
            energyBox.setAlignment(Pos.CENTER);
            maxEnergyBox.setStyle("-fx-background-color: transparent;");
            maxEnergyBox.setAlignment(Pos.CENTER);
            healthBox.setStyle("-fx-background-color: transparent;");
            healthBox.setAlignment(Pos.CENTER);
            maxHealthBox.setStyle("-fx-background-color: transparent;");
            maxHealthBox.setAlignment(Pos.CENTER);
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene1.setFill(imagePattern);
            scene1.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            scene = scene1;

            ArrayList<Label> addedLabels = new ArrayList<>();
            ArrayList<Label> addedCheckLabels = new ArrayList<>();
            ArrayList<Button> addedButtons = new ArrayList<>();
            //**************************************************
            backButton.setOnAction(e -> stage.setScene(getScene("Cafe")));
            addButton.setOnAction(e -> {
                boolean flag = true;
                int price;
                float healthRaise;
                float energyRaise;
                float maxHealth;
                float maxEnergy;
                try {
                    ArrayList eatableFeatures = new ArrayList();
                    eatableFeatures.add(0, nameField.getText());
                    eatableFeatures.add(1, "Food");
                    eatableFeatures.add(2, true);

                    Stat energy = new Stat("Energy");
                    Stat health = new Stat("Health");

                    healthRaise = Float.parseFloat(healthField.getText());
                    energyRaise = Float.parseFloat(energyField.getText());
                    maxHealth = Float.parseFloat(maxHealthField.getText());
                    maxEnergy = Float.parseFloat(maxEnergyField.getText());
                    energy.setCurrent(energyRaise);
                    energy.setMaximum(maxEnergy);
                    health.setCurrent(healthRaise);
                    health.setMaximum(maxHealth);

                    price = Integer.parseInt(priceField.getText());

                    eatableFeatures.add(3, energy);
                    eatableFeatures.add(4, health);
                    eatableFeatures.add(5, price);
                    Holder.customEatables.put((String)eatableFeatures.get(0), eatableFeatures);
                    cafeFoods.add(Holder.getNewEatable((String)eatableFeatures.get(0)));
                }catch (Exception c) {
                    flag = false;
                    AlertBox error = new AlertBox();
                    error.display("Error", "Oops! your custom fruit/vegetable\n can't be created!\n please check the numbers\n you've entered and then\n try again!");
                }
                if (flag)
                {
                    addedLabels.add(new Label(nameField.getText()));
                    addedCheckLabels.add(new Label("✔"));
                    Button button = new Button("Remove");
                    addedButtons.add(button);
                    HBox hBox = new HBox(addedCheckLabels.get(addedCheckLabels.size() - 1), addedLabels.get(addedLabels.size() - 1), addedButtons.get(addedButtons.size() - 1));
                    inputs.getChildren().add(hBox);
                    //ersale etela'at baraye sakhte ghazaye jadid va add kardane un
                    priceField.setText("");
                    nameField.setText("");
                    energyField.setText("");
                    maxEnergyField.setText("");
                    healthField.setText("");
                    maxHealthField.setText("");

                    for (int i = 0; i < addedButtons.size(); i++){
                        final int I = i;
                        addedButtons.get(i).setOnAction(event -> {
                            if (addedCheckLabels.get(I).getText().equals("✔︎")){
                                addedButtons.get(I).setText("Add");
                                addedCheckLabels.get(I).setText("✘");
                                removedCafeFoods.add(cafeFoods.get(I));
                            }
                            else {
                                addedButtons.get(I).setText("Remove");
                                addedCheckLabels.get(I).setText("✔︎");
                                removedCafeFoods.remove(cafeFoods.get(I));
                            }
                        });
                    }
                }

            });
        }
    };


    static {
        CustomView.views.add(main);
        CustomView.views.add(farm);
        CustomView.views.add(village);
        CustomView.views.add(farmer);
        CustomView.views.add(clinic);
        CustomView.views.add(gym);
        CustomView.views.add(healthExercise);
        CustomView.views.add(energyExercise);
        CustomView.views.add(generalStore);
        CustomView.views.add(groceriesStore);
        CustomView.views.add(laboratory);
        CustomView.views.add(cafe);
        CustomView.views.add(menu);
        CustomView.views.add(recipe);
        CustomView.views.add(trees);
    }

    public static void exitCustom(){
        Holder.setToDefault();
        boolean answer = ConfirmBox.display("Exit custom","Do you really want to exit custom game?");
        if (answer){
            Game.view = Game.firstView;
            Game.changeScene(Game.firstView);
        }
    }

    public static void runCustom(){
        ArrayList<Item> generalStore = copyArray(generalStoreItems);
        for (int i = 0; i < generalStoreRemoved.size(); i++)
            generalStore.remove(generalStoreRemoved.get(i));

        ArrayList<Item> groceryStore = copyArray(groceryStoreItems);
        for (int i = 0; i < groceryStoreRemoved.size(); i++)
            groceryStore.remove(groceryStoreRemoved.get(i));

        ArrayList<Item> clinic = copyArray(medicines);
        for (int i = 0; i < removedMedicines.size(); i++)
            clinic.remove(removedMedicines.get(i));

        ArrayList<Eatable> cafe = copyArray(cafeFoods);
        for (int i = 0; i < removedCafeFoods.size(); i++)
            cafe.remove(removedCafeFoods.get(i));

        ArrayList<Exercise> energyExercises = copyArray(energyGym);
        for (int i = 0; i < removedEnergyGym.size(); i++)
            energyExercises.remove(removedEnergyGym.get(i));

        ArrayList<Exercise> healthExercises = copyArray(healthGym);
        for (int i = 0; i < removedHealthGym.size(); i++)
            healthExercises.remove(removedHealthGym.get(i));

        ArrayList<Recipe> kitchenRecipes = copyArray(recipes);
        for (int i = 0; i < removedRecipes.size(); i++)
            kitchenRecipes.remove(removedRecipes.get(i));
        Holder.setDefaultRecipe();
        for (int i = 0; i < kitchenRecipes.size(); i++)
            Holder.recipeArrayList.add(kitchenRecipes.get(i));

        ArrayList<Machine> labMachines = copyArray(machines);
        for (int i = 0; i < removedMachines.size(); i++)
            labMachines.remove(removedMachines.get(i));
        //************************
        Holder.setGeneralStoreItems(generalStore);
        Holder.setGroceryStoreItems(groceryStore);
        Holder.addClinicItems(clinic);
        Holder.addGymEnergyItems(energyExercises);
        Holder.addGymHealthItems(healthExercises);
        Holder.addCafeItems(cafe);
        Holder.addLaboratoryItems(labMachines);
        //*************************
        Place.init();
    }

    private static ArrayList copyArray(ArrayList items){
        ArrayList newArray = new ArrayList<>();
        for (int i = 0; i < items.size(); i++)
            newArray.add(items.get(i));
        return newArray;
    }

}

abstract class CustomView {
    Scene scene;
    private String name;
    static ArrayList<CustomView> views = new ArrayList<>();

    CustomView(String name) {
        this.name = name;
        buildScene();
        scene.getStylesheets().add(Game.class.getResource("Style.css").toExternalForm());
    }

    abstract void buildScene();

    void setScene() {
        Custom.stage.setScene(scene);
    }

    Scene getScene(String name){
        for (int i = 0;  i < views.size(); i++)
            if (views.get(i).getName().equals(name))
                return views.get(i).scene;
        return null;
    }

    public String getName() {
        return name;
    }
}