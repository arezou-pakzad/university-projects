import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

// Aida's path: "/Users/Aida/project/icons/";
// Kimia's path: "/Users/appe/Desktop/AP/AP Project/phase 2/team_26/Project/icons/";
// Arezou's path: "/Users/arezou/Documents/University/Advanced programming/Project phase2 & 3/custom/Project/icons/";

public class Game extends Application {

    public static String playerName;

    public static void main(String[] args) {
        BackPack.getBackPack().addItem(Holder.getIronShovel());
        BackPack.getBackPack().addItem(Holder.getStoneAxe());
        BackPack.getBackPack().addItem(Holder.getWatermelonCrop());
        launch(args);
    }

    static void nextDay() {
        Popup.makePopup("The next Day");
        Farm.farm.nextDay();
        Person.getPerson().sleep();
    }

    public static final double W = 800, H = 600;

    private final static String projectPath = "/Users/Aida/Desktop/ap_last/team_26/Project/icons/";

    public static ImageView getImageView(String fileName) {
        File file = new File(projectPath + fileName);
        Image image = new Image(file.toURI().toString());
        return new ImageView(image);
    }

    public static Image getImage(String fileName) {
        File file = new File(projectPath + fileName);
        return new Image(file.toURI().toString());
    }

    public static View view;
    public static View firstView;
    public static Stage stage;
    public static View cafeView;

    private static Scene firstScene() {
        Text welcome = new Text("Welcome to the game");
        welcome.setX(350);
        welcome.setY(200);
        welcome.setScaleY(10);
        welcome.setScaleX(5);
        InnerShadow is = new InnerShadow();
        is.setOffsetX(1.0f);
        is.setOffsetY(1.0f);

        welcome.setEffect(is);
        welcome.setStroke(Color.rgb(226, 44, 4));
        welcome.setFill(Color.rgb(226, 44, 4));
        Button singlePlayerButton = new Button("Single player");

        singlePlayerButton.setLayoutX(300);
        singlePlayerButton.setLayoutY(450);
        Button exit = new Button("Exit");
        exit.setLayoutX(325);
        exit.setLayoutY(400);

        Button customButton = new Button("Custom");
        customButton.setLayoutX(300);
        customButton.setLayoutY(500);

        exit.setOnAction(e -> closeProgram());

        stage.setOnCloseRequest(event -> {
            event.consume();
            closeProgram();
        });

        singlePlayerButton.setOnAction(e -> {
            Holder.setToDefault();
            Place.init();
        });

        customButton.setOnAction(e -> {
            Custom.main.setScene();
        });

        VBox buttons = new VBox(welcome, singlePlayerButton, customButton, exit);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPrefWidth(150);
        buttons.setPadding(new Insets(20,20,20,20));
        buttons.setSpacing(10);
        buttons.setStyle("-fx-background-color: transparent;");
        exit.setMinWidth(buttons.getPrefWidth());
        customButton.setMinWidth(buttons.getPrefWidth());
        singlePlayerButton.setMinWidth(buttons.getPrefWidth());
        buttons.setLayoutX(300);
        buttons.setLayoutY(300);
        Group group = new Group(welcome, buttons);
        Scene scene = new Scene(group, W, H);
        scene.setFill(Color.TRANSPARENT);
        Image image = getImage("1.jpg");
        ImagePattern imagePattern = new ImagePattern(image);
        scene.setFill(imagePattern);
        scene.getStylesheets().add(Game.class.getResource("style.css").toExternalForm());

        return scene;
    }

    public static void setScene(Scene scene) {
//        Game.scene = scene;
        stage.setScene(scene);
        stage.show();
    }

    public static void changeScene(View view) {
        if (Game.view instanceof PlaceView) {
            PlaceView currentView = (PlaceView) Game.view;
            currentView.timer.stop();
        }
        if(Game.view == cafeView){
            PlaceView.mediaPlayer1.stop();
            PlaceView.mediaPlayer.play();
        }
        if(view == cafeView){
            PlaceView.mediaPlayer.stop();
        }
        if(view != cafeView){

            PlaceView.mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    PlaceView.mediaPlayer.seek(Duration.ZERO);
                }
            });
            PlaceView.mediaPlayer.play();
        }

        Game.view = view;
        setScene(view.getScene());
        if (view instanceof PlaceView) {
            PlaceView placeView = (PlaceView) view;
            placeView.timer.start();
        }

    }

    private static void closeProgram() {
        boolean answer = ConfirmBox.display("Exit", "Do you really want to exit?");
        if (answer)
            System.exit(0);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Custom.stage = stage;
        firstView = new View() {
            @Override
            public void buildScene() {
                scene = firstScene();
            }
        };
        firstView.buildScene();
        changeScene(firstView);
        view = firstView;
        stage.show();
    }
}
interface Expressible {
    //    abstract void showMenu();
}

class Controller {
    public static boolean whichMethod = false;
    public static boolean buyItem = false;
    public static boolean labMethod = false;
    public static boolean buyAnimal = false;
    public static boolean storesBuying = false; // YEKI IN GHANDARO DOROS KONE FOR GODS SAKE
    protected static Stack<Controller> menus = new Stack<>();
    public static Stack<InspectableController> backPackMenus = new Stack<>();
    static Scanner scanner = new Scanner(System.in);
    // public BackPackController backPackController = new BackPackController(BackPack.getBackPack());
    // add person and his/hers backpack
    static protected Person person = Person.getPerson();
    protected static String command;
    protected Expressible expressible;

    public void setView(View view) {
        this.view = view;
    }

    private View view;

    public View getView() {
        if (view.getScene() == null)
            view.buildScene();
        return view;
    }
    //TODO - DELETE THIS
    Controller() {

    }
    public Controller(Expressible expressible, View view) {
        this.expressible = expressible;
        this.view = view;
    }
    public Controller(Expressible expressible) {
        this.expressible = expressible;
    }

    static void init(PlaceController placeController) { // start the game in 'place'
        addMenu(placeController);
    }

    static void addMenu(Controller controller) {
        menus.push(controller);
        Game.changeScene(controller.getView());
        if (controller instanceof PlaceController)
        {
            PlaceController placeController = (PlaceController) controller;
            Person.getPerson().setPlace(placeController.getPlace());
        }
        if (controller instanceof InspectablePlaceController) {
            InspectablePlaceController placeController = (InspectablePlaceController) controller;
            menus.push(placeController.inspectableController);
        }
    }

    public void refreshScene() {
        view.buildScene();
        Game.changeScene(menus.peek().getView());
    }
    public static void clearStack(){
        PlaceView.nowTime = PlaceView.firstTime;
        menus.clear();
    }
    int scan() { // return: 1 -> command done   0 -> not yet    -1 -> invalid
        //should be overrided in subclasses
        command = scanner.nextLine();
        if (command.equals("back")) {
            // if there was one expressible left -> DONT GO BACK
            if (menus.size() <= 1) {
                System.out.println("Can't Go Back :(");
                return -1;
            }
            back();
            return 1;
        }
        else if (command.equals("help")) {
            help();
            return 1;
        }
        else if (command.equals("Stats")) {
            showStats();
            return 1;
        }
        else if(command.equals("Backpack")){
            addMenu(BackPack.getBackPack().inspectableController);
            return 1;
        }
        return 0;
    }
    void showMenu() { //should be overrided in subclasses
    }
    void help() { // TODO - check
        view.help();
    }
    void back() {
        if (menus.size() <= 0)
            return;
        menus.pop();
        Game.changeScene(menus.peek().getView());
        if (menus.size() > 0 && menus.peek() instanceof InspectablePlaceController)
            menus.pop();
    }
    public void showStats(){
        ArrayList<Stat> stats = Person.getPerson().getStats();
        String[] details = new String[stats.size() + 1];
        for (int i = 0; i < stats.size(); i++){
            details[i] = stats.get(i).getName() + " : " + stats.get(i).getCurrent() + " of " + stats.get(i).getMaximum();
        }
        details[stats.size()] = "Money : " + person.getMoney();
        view.showStats(details);
    }
    static void continousScan() {
        if (menus.empty()) {
            //            System.out.println("Game Over");
            return;
        }
        Controller controller = menus.peek();
        controller.showMenu();
        System.out.println("menus:");
        showStack(menus);
//        System.out.println("backpack:");
//        for (Controller backcontroller : backPackMenus) {
//            System.out.println(backcontroller.expressible.getClass().getName());
//        }
//        System.out.println("");
        int scanReport = controller.scan();
        while (scanReport != 1) {
            if (scanReport == -1)
                System.out.println("Invalid command!"); // TODO - check exact command for invalid input
//            else if (scanReport == 0)
            scanReport = controller.scan();
        }
        continousScan();
    }
    static void showStack(Stack<Controller> stack){
        for (Controller controller : stack) {
            System.out.println(controller.expressible.getClass().getName());
        }
        System.out.println("");
    }
}
