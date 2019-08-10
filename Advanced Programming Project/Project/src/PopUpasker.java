import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Aida on 7/13/2017.
 */
public class PopUpasker extends PopUps {

        static String answer;


        public static  String display(String title, String message) {
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle(title);
            window.setMaxWidth(500);
            Label label = new Label();
            label.setText(message);
            label.setFont(Font.font("Comic Sans Ms", 20));
            HBox hBox = new HBox();
            //hBox.setStyle("-fx-background-color: transparent;");
            TextField textField = new TextField();
            hBox.getChildren().add(textField);
            textField.setOnAction(e -> {
                answer = textField.getText();
                window.close();
            });
            Button button = new Button("OK");
            hBox.getChildren().add(button);
            button.setOnAction(event -> {
                answer = textField.getText();
                window.close();
            });
            hBox.setAlignment(Pos.CENTER);
            hBox.setPrefWidth(100);
            hBox.setPadding(new Insets(20,20,20,20));
            hBox.setSpacing(10);
            hBox.setStyle("-fx-background-color: transparent;");
            VBox layout = new VBox(20);
            layout.getChildren().addAll(label, hBox);
            layout.setStyle("-fx-background-color: transparent;");
            layout.setAlignment(Pos.CENTER);
            Scene scene = new Scene(layout , 500 , 500);
            scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
            window.setScene(scene);
            ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround.png"));
            scene.setFill(imagePattern);
            window.showAndWait();
            return answer;
        }

}
