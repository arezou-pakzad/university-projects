import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Aida on 7/13/2017.
 */
public class AlertBox {
    public static void display(String title, String message) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);//*
        window.setTitle(title);
        window.setMaxWidth(250);
        Label label = new Label();
        label.setText(message);

        Button closeButton = new Button("Ok");
        closeButton.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);\n" +
                "    -fx-background-radius: 30;\n" +
                "    -fx-background-insets: 0;\n" +
                "    -fx-text-fill: white;");
        closeButton.setOnAction(event -> window.close());

        VBox layout = new VBox(label, closeButton);
//        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));
        layout.setSpacing(10);
        layout.setPrefWidth(label.getMinWidth());
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();//*
    }
}
