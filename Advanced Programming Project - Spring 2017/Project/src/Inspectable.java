import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class Inspectable implements Expressible {
    protected String name;
    protected ArrayList<String> commands = new ArrayList<>();
    public InspectableController inspectableController; //bayad dar har class az hamoon jens controller new beshe
    public String getName() {
        return name;
    }
    protected void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    //    public void setCommands(ArrayList<String> commands) {
//        this.commands = commands;
//    }
    public void setCommands(String ... commands) {
        this.commands = new ArrayList<>();
        for (int i = 0; i < commands.length && commands[i] != null; i++)
            this.commands.add(commands[i]);
    }
}

class InspectableController extends Controller {
    protected Inspectable inspectable;
    protected InspectableView inspectableView; //= new InspectableView(this);

    @Override
    public void setView(View view) {
        super.setView(view);
        if (view instanceof InspectableView) {
            InspectableView inspectableView = (InspectableView) view;
            this.inspectableView = inspectableView;
        }
    }

    //TODO - DELETE this constructor
    InspectableController() {
        super();
    }

    InspectableController(Inspectable inspectable, InspectableView inspectableView) {
        super(inspectable, inspectableView);
        this.inspectable = inspectable;
    }
    InspectableController(Inspectable inspectable) {
        super(inspectable);
        this.inspectable = inspectable;
        setView(inspectableView);
    }

    @Override
    public void showMenu() {
        inspectableView.showMenu(inspectable.getName(), inspectable.getCommands());
    }

    @Override
    public int scan() {
        int scanReport = super.scan();
        if (scanReport == 1 || scanReport == -1)
            return scanReport;
        if (command.equals("WhereAmI")) {
//            showMenu();
            return 1;
        }
        // any other commands --- ADD HERE
        try {
            int commandNum = Integer.parseInt(command);
            if (commandNum > inspectable.commands.size() || commandNum <= 0)
                return -1;
            runMethods(commandNum);
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }
    public void runMethods(int commandNum) { //change name -- sucks!
        // should be overrided
    }

    public char scanQuestion(){
        Scanner Qscanner = new Scanner(System.in);
        String input = Qscanner.next();
        if (input.length() > 1) {
            if (input.equals("back"))
                return 'N';
            else
                System.out.println("Invalid Input");//just for test
        }
        else if (input.charAt(0) == 'N')
            return 'N';
        else if (input.charAt(0) == 'Y')
            return 'Y';
        return 'N';
    }

    protected boolean buy(String name, int price) {
        return ConfirmBox.display("Buy Tree", "You will buy a " + name +" Tree for " + price + " Gil, is this okay?");
    }

    void showStatus(String status){
        System.out.println("Status:");
        inspectableView.showStatus(status);
    }

    Inspectable getInspectable() {
        return inspectable;
    }
}

class InspectableView extends View {
    private InspectableController inspectableController;
    // these two should be set in subclass
    protected String imageName;
    protected String buttonFormat;
    protected int width = 500, height = 500;
    public InspectableView(InspectableController inspectableController) {
        super(inspectableController);
        this.inspectableController = inspectableController;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setButtonFormat(String buttonFormat) {
        this.buttonFormat = buttonFormat;
    }

    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void buildScene() {
        Scene scene;
        VBox group = new VBox();
        group.setAlignment(Pos.CENTER);
        group.setSpacing(20);
        group.setPrefWidth(200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        ArrayList<String> commands = inspectableController.getInspectable().getCommands();
        Button[] buttons = new Button[commands.size()];
        for (int i = 0; i < buttons.length ; i++) {
            buttons[i] = new Button(commands.get(i));
            buttons[i].setFont(Font.font("Comic Sans Ms", 20));
            // TODO - button ha bayad vasate safhe bashan (x eshoon)
            buttons[i].setLayoutX(180 - (buttons[i].getBoundsInLocal().getMaxX()) / 2);
            buttons[i].setLayoutY(20 + i * 50);
            buttons[i].setAlignment(Pos.CENTER);
            group.getChildren().add(buttons[i]);
            buttons[i].setMinWidth(group.getPrefWidth());
            // set pattern for each button here
            final int finalI = i + 1;
            buttons[i].setOnAction(event -> {
                inspectableController.runMethods(finalI);
//                System.out.println("method number " + finalI  +" run");
//                inspectableController.back();
            });
        }
        Button back = new Button("Back");
        back.setFont(Font.font("Comic Sans Ms", 20));
        back.setLayoutY(80);
        back.setLayoutX(10);
        back.setOnAction(event -> inspectableController.back());
        group.getChildren().addAll(back);
        scene = new Scene(group, 500 , 500);
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/backGround2.png"));
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());
        group.setStyle("-fx-background-color: transparent;");
        this.scene = scene;
    }

    void showMenu(String inspectableName, ArrayList<String> commands) {
        System.out.println(inspectableName + ":");
        for (int i = 0; i < commands.size(); i++)
            System.out.println(i + 1 + ". " + commands.get(i));
        System.out.println("");
    }

    public void showCollected(String name , int num , String status) {
        System.out.println(num + " " + "name" + " were collected. " + status);
    }
    public void showStatus(String status){
        Popup.makePopup("Status:\n" + status );
        System.out.println(status);
        System.out.println("");
    }

    public void buy(String name, int price) {
        System.out.println("You will buy a " + name + " for " + price + " Gil, is this okay?");
    }
    public void invalid(){
        System.out.println("Invalid Input");
    }
}
