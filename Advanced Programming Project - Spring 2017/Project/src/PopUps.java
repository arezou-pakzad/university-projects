import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

abstract class PopUps {
    Stage stage = new Stage();
    Scene scene;
    public void show() {
        stage.setScene(scene);
        stage.show();
    }
}

class BackPackPopUp extends PopUps {
    Label command;
    Label errorsLabel = new Label("");


    private BackPack backPack = BackPack.getBackPack();
    private int wantedNumber = 1;
    private String type;
    private String name;

    private boolean specificName = false;
    private boolean moreThanOne = false; // don't check the number
    private boolean specificType = false;

    private Item chosenOne;
    private boolean pressedBack = false;

    public BackPackPopUp() {
        command = new Label("Choose an item from backpack");
    }

    public BackPackPopUp(String name) {
        this.name = name;
        specificName = true;
        command = new Label("Choose a " + name + " from backpack");
        command.setLayoutX(70);
        command.setFont(Font.font("Comic Sans Ms", 20));
    }

    public BackPackPopUp(String type, String name) {
        this.type = type;
        specificType = true;
        if (name == null){
            command = new Label("Choose a " + type + " from backpack");
            command.setLayoutX(70);
            command.setFont(Font.font("Comic Sans Ms", 20));
        }

        else {
            this.name = name;
            specificName = true;

            command = new Label("Choose a " + type + " " + name + " from backpack");
            command.setLayoutX(70);
            command.setFont(Font.font("Comic Sans Ms", 20));
        }
    }

    public BackPackPopUp(int wantedNumber, String name) {
        this.wantedNumber = wantedNumber;
        this.name = name;
        moreThanOne = true;
        specificName = true;
        command = new Label("Choose " + wantedNumber + " " + name + "(s) from backpack");
        command.setLayoutX(70);
        command.setFont(Font.font("Comic Sans Ms", 20));
    }

    public boolean hasPressedBack() {
        return pressedBack;
    }

    public Item getChosenOne() {
        if (matches())
            return chosenOne;
        return null;
    }

    private boolean matches() {
        if (chosenOne == null) {
            System.out.println("null");
            return false;
        }
        if (!specificName && !specificType && !moreThanOne)
            return true;
        if (!specificName && specificType && !chosenOne.getType().equals(type)) {
            errorsLabel.setText("You have to choose a " + type);
            errorsLabel.setLayoutX(70);
            errorsLabel.setFont(Font.font("Comic Sans Ms", 20));
            return false;
        }

        if (!chosenOne.getName().equals(name)) {
            errorsLabel.setText("You have to choose a " + name);
            errorsLabel.setLayoutX(70);
            errorsLabel.setFont(Font.font("Comic Sans Ms", 20));
            return false;
        }
        if (specificType && !chosenOne.getType().equals(type)) {
            errorsLabel.setText("You have to choose a " + type + " " + name);
            errorsLabel.setLayoutX(70);
            errorsLabel.setFont(Font.font("Comic Sans Ms", 20));
            return false;
        }
        // TODO - modify this condition
        if (moreThanOne && backPack.itemIntegerHashMap.get(chosenOne) < wantedNumber) {
            errorsLabel.setText("There isn't enough " + name);
            errorsLabel.setLayoutX(70);
            errorsLabel.setFont(Font.font("Comic Sans Ms", 20));
            return false;
        }
        return true;
    }

    public void setChosenOne(int chosenOneNum) {
        chosenOne = backPack.items.get(chosenOneNum - 1);
        if (!matches()) {
            chosenOne = null;
            return;
        }
        stage.close();
        System.out.println("set: " + chosenOne.getName() + " " + chosenOne.getType());
    }

    @Override
    public void show() {
        buildScene();
//        super.show();
    }

    public void buildScene() {
        Scene scene;
        Group group = new Group();
        stage.initModality(Modality.APPLICATION_MODAL);
        command.setAlignment(Pos.TOP_CENTER);
        errorsLabel.setAlignment(Pos.TOP_RIGHT);
        backPack.setCommands();
        ArrayList<String> commands = backPack.getCommands();
        Button[] buttons = new Button[commands.size()];
        for (int i = 0; i < buttons.length ; i++) {
            buttons[i] = new Button(commands.get(i));
            buttons[i].setFont(Font.font("Comic Sans Ms", 20));
            // TODO - button ha bayad vasate safhe bashan (x eshoon)
            buttons[i].setLayoutX(180);/* - (buttons[i].getBoundsInLocal().getMaxX()) / 2);*/
            buttons[i].setLayoutY(40 + i * 50);
            buttons[i].setAlignment(Pos.CENTER);
            group.getChildren().add(buttons[i]);
            // set pattern for each button here
            final int finalI = i + 1;
            buttons[i].setOnAction(event -> {
                        setChosenOne(finalI);
                    }
            );
        }
        // TODO - handle back
        Button back = new Button("Back");
        group.setStyle("-fx-background-color: transparent;");
        back.setFont(Font.font("Comic Sans Ms", 20));
        back.setLayoutY(20);
        back.setLayoutX(100);
        back.setOnAction(event -> {
            stage.close();
            chosenOne = null;
        });
        scene = new Scene(group, 500, 500);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        group.getChildren().addAll(back, command, errorsLabel);

        stage.setScene(scene);
        stage.showAndWait();
    }

}

class MultiChoicesPopUps {
    static int answer;

    public static int display(String title, String message, String... commands) {
        System.out.println(commands.length);
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMaxWidth(500);
        Label label = new Label();
        label.setText(message);
        label.setFont(Font.font("Comic Sans Ms", 20));
        HBox hBox = new HBox();

        Button[] buttons = new Button[commands.length];
        for (int i = 0; i < commands.length; i++) {
            buttons[i] = new Button(commands[i]);
            buttons[i].setFont(Font.font("Comic Sans Ms", 20));
            final int finalI = i;
            hBox.getChildren().add(buttons[i]);
            buttons[i].setOnAction(event -> {
                answer = finalI;
                window.close();
            });
        }

        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(100);
        hBox.setPadding(new Insets(20,20,20,20));
        hBox.setSpacing(10);
        hBox.setStyle("-fx-background-color: transparent;");
        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: transparent;");
        layout.getChildren().addAll(label, hBox);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout , 500 , 500);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }
}

class ConfirmBox {
    static boolean answer;

    public static boolean display(String title, String message) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        Label label = new Label();
        label.setText(message);
        label.setFont(Font.font("Comic Sans Ms", 20));
        HBox hBox = new HBox();

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
        yesButton.setFont(Font.font("Comic Sans Ms", 20));
        noButton.setFont(Font.font("Comic Sans Ms", 20));
        yesButton.setOnAction(event -> {
            answer = true;
            window.close();
        });

        noButton.setOnAction(event -> {
            answer = false;
            window.close();
        });

        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(100);
        yesButton.setMinWidth(hBox.getPrefWidth());
        noButton.setMinWidth(hBox.getPrefWidth());
        hBox.setPadding(new Insets(20,20,20,20));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(noButton, yesButton);
        hBox.setStyle("-fx-background-color: transparent;");
        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: transparent;");
        layout.getChildren().addAll(label, hBox);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 400 , 400);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        window.setScene(scene);
        window.showAndWait();//*
        return answer;
    }
}

