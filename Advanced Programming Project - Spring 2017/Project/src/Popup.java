import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Popup {
    static char answer = 'N';
    public static void askTheCostumer(){

    }
    public static void  showStatus(Item item){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        String status = item.getStatus();
        Text textStatus = new Text(status);
        textStatus.setFont(Font.font("Comic Sans Ms", 20));
        Button back = new Button("OK");
        back.setFont(Font.font("Comic Sans Ms", 20));
        VBox vBox = new VBox(textStatus, back);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(vBox, 400 , 200);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        back.setOnAction(e -> {
            stage.close();
        });

        stage.show();

    }
    public static void  fullBackPack(){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        String status = "Back Pack is Fulled";
        Text textStatus = new Text(status);
        textStatus.setFont(Font.font("Comic Sans Ms", 20));
        Button back = new Button("OK");
        back.setFont(Font.font("Comic Sans Ms", 20));
        VBox vBox = new VBox(textStatus, back);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(vBox, 400 , 200);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        back.setOnAction(e -> {
            stage.close();
        });
        stage.show();
    }
    public static void  invalidTarget(){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        String status = "Invalid Target";
        Text textStatus = new Text(status);
        textStatus.setFont(Font.font("Comic Sans Ms", 20));
        Button back = new Button("OK");
        back.setFont(Font.font("Comic Sans Ms", 20));
        VBox vBox = new VBox(textStatus, back);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(vBox, 400 , 200);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        back.setOnAction(e -> {
            stage.close();
        });
        stage.show();
    }
    public static void makePopup(String textString){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        String status = textString;
        Text textStatus = new Text(status);
        textStatus.setFont(Font.font("Comic Sans Ms", 20));
        Button back = new Button("OK");
        back.setFont(Font.font("Comic Sans Ms", 20));
        VBox vBox = new VBox(textStatus, back);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(vBox, 400 , 200);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        back.setOnAction(e -> {
            stage.close();
        });
        stage.show();
    }
    public static void fullBarn() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        String status = "Barn is fulled";
        Text textStatus = new Text(status);
        textStatus.setFont(Font.font("Comic Sans Ms", 20));
        Button back = new Button("OK");
        back.setFont(Font.font("Comic Sans Ms", 20));
        VBox vBox = new VBox(textStatus, back);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(vBox, 400, 200);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        back.setOnAction(e -> {
            stage.close();
        });
        stage.show();
    }
}
