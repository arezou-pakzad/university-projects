import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

class PlaceView extends View {

    public static Media media1 = new Media(new File("/Users/Aida/lastOfUs/team_26/media/cafe.mp3").toURI().toString());
    public static MediaPlayer mediaPlayer1 = new MediaPlayer(media1);
    static Media farm = new Media(new File("/Users/Aida/lastOfUs/team_26/media/apSong.mp3").toURI().toString());
    public static MediaPlayer mediaPlayer = new MediaPlayer(farm);
    public static boolean isPlaying = true;
    ProgressBar progressBar = new ProgressBar();
    ProgressBar health = new ProgressBar(0.0);
    private PlaceController placeController;
    public Text time = new Text();
    public Text stat = new Text();
    public PlaceView(PlaceController placeController) {
        super(placeController);
        this.placeController = placeController;
        progressBar.setLayoutX(300);
        progressBar.setLayoutY(30);
        stat.setLayoutX(400);
        stat.setLayoutY(90);
        health.setLayoutX(700);
        health.setLayoutY(30);
        progressBar.setMaxSize(Person.getPerson().getStats().get(0).getMaximum(), 30D);
        mediaPlayer.play();

    }

    HashMap<Node, String> interactables;
    Button pause = new Button("Pause");
    static int daysPassed = 0;
    public static ArrayList<ImageView> barnMachines = new ArrayList<>();
    void whereAmI(String place, ArrayList<String> accessiblePlaces, ArrayList<String> inspectableObjects) {
        System.out.println("You are currently in " + place);
        System.out.println("Places you can go:");
        for (int i = 0; i < accessiblePlaces.size(); i++)
            System.out.println(i+1 + ". " + accessiblePlaces.get(i));
        System.out.println("");
        System.out.println("Objects you can interact with: ");
        for (int i = 0; i < inspectableObjects.size(); i++)
            System.out.println (i+1 + ". " + inspectableObjects.get(i));
        System.out.println("");
    }

    private ScrollPane scrollPane = new ScrollPane();
    protected static final String AVATAR_FRONT = "avatarFront.gif",
            AVATAR_BACK = "avatarBack.gif",
            AVATAR_RIGHT = "avatarRight.gif",
            AVATAR_LEFT = "avatarLeft.gif";
    protected String address = AVATAR_FRONT;

    protected Image heroImage;
    //    protected static ImageView hero = getImageView(AVATAR_FRONT);
    protected ImageView hero;
//    static Circle circle = new Circle()

    boolean running, goNorth, goSouth, goEast, goWest;
    private Node currentlyIntersects = null;
    private double noInteractionX, NoInteractionY;

    public void handleArrowKeys() {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:    goNorth = true; address = AVATAR_BACK; hero.setImage(getImage(address));break;
                case DOWN:  goSouth = true; address = AVATAR_FRONT;hero.setImage(getImage(address)); break;
                case LEFT:  goWest  = true; address = AVATAR_LEFT;hero.setImage(getImage(address)); break;
                case RIGHT: goEast  = true; address = AVATAR_RIGHT;hero.setImage(getImage(address)); break;
                case SHIFT: running = true; break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case UP:    goNorth = false; break;
                case DOWN:  goSouth = false; break;
                case LEFT:  goWest  = false; break;
                case RIGHT: goEast  = false; break;
                case SHIFT: running = false; break;
            }
        });


        scene.setOnKeyTyped(event -> {
            System.out.println("TYPED SOMETHING");
            System.out.println(event.getCode().getName());
//            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.G) {
//                System.out.println("enter is typed");
            if (currentlyIntersects == null)
                return;
//                System.out.println("GO!");
            hero.relocate(noInteractionX, NoInteractionY);
            System.out.println("Relocated to: " + noInteractionX + "," + NoInteractionY);
            placeController.goTo(interactables.get(currentlyIntersects));
            currentlyIntersects = null;
            goEast = goNorth = goWest = goSouth = false;
//            }
        });
    }
    public static long nowTime;
    public boolean daySetter = false;
    static public long firstTime = System.nanoTime();

    public AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            time.setScaleX(3);
            time.setScaleY(3);
            time.setStroke(Color.CORAL);
            nowTime = now;
            int dx = 0, dy = 0;
            if (goNorth) dy -= 1;
            if (goSouth) dy += 1;
            if (goEast)  dx += 1;
            if (goWest)  dx -= 1;
            if (running) { dx *= 4; dy *= 4; }
            moveHeroBy(dx, dy);
            time.setText(daysPassed + " : " + ((nowTime - firstTime) / 1000000000 / 60 / 3) % 24 + " : " + ((nowTime - firstTime) / 1000000000 /3 ) % 60 );
            if(((now - firstTime) / 1000000000 / 60) >= 72 ){ //20 minutes have passed
                Game.nextDay();
            }
            stat.setText("Energy : " + Person.getPerson().getStats().get(0).getCurrent() + "                         " + "Health : " + Person.getPerson().getStats().get(1).getCurrent());
            stat.setScaleX(2);
            stat.setScaleY(2);
            stat.setStroke(Color.DEEPPINK);
            progressBar.setProgress(Person.getPerson().getStats().get(0).getCurrent() / Person.getPerson().getStats().get(0).getMaximum());
            health.setProgress(Person.getPerson().getStats().get(1).getCurrent() / Person.getPerson().getStats().get(1).getMaximum());

        }
    };

    private void moveHeroBy(int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        final double cx = hero.getBoundsInLocal().getWidth()  / 2;
        final double cy = hero.getBoundsInLocal().getHeight() / 2;

        double x = cx + hero.getLayoutX() + dx;
        double y = cy + hero.getLayoutY() + dy;

        moveHeroTo(x, y);
    }
    private void moveHeroTo(double x, double y) {
        final double cx = hero.getBoundsInLocal().getWidth()  / 2;
        final double cy = hero.getBoundsInLocal().getHeight() / 2;

        //TODO - az mane ha rad nashe

        boolean collides = false;
        for (Node icon : interactables.keySet()) {
            if (icon.getBoundsInParent().intersects(hero.getBoundsInParent())) {
                currentlyIntersects = icon;
                collides = true;
            }
        }
        if (!collides) {
            currentlyIntersects = null;
            if (nowTime % 10 == 0) {
                noInteractionX = x;
                NoInteractionY = y;
            }
        }
        if (x - cx >= 0 &&
                x + cx <= W &&
                y - cy >= 0 &&
                y + cy <= H) hero.relocate(x - cx, y - cy);
    }
    @Override
    public void buildScene() {
        interactables = new HashMap<>();
        switch (placeController.place.getName()) {
            case "Farm":
                scene = farmScene();
                break;
            case "Barn":
                scene = barnScene();
                break;
            case "Greenhouse":
                scene = greenHouseScene();
                break;
            case "Home":
                scene = homeScene();
                break;
            case "Village":
                scene = villageScene();
                break;
            case "Market":
                scene = marketScene();
                break;
            case "Forest":
                scene = forestScene();
                break;
            case "Cave":
                scene = caveScene();
                break;
            case "Cafe":
                scene = cafeScene();
                break;

        }
        handleArrowKeys();
    }


    public Scene farmScene(){
        Scene scene;
        Group group = new Group();
        Image image = Game.getImage("farm1.png");
        ImageView house = Game.getImageView("house.png");
        house.setLayoutX(150);
        house.setLayoutY(100);
        ImageView pond = Game.getImageView("watergrass.png");
        pond.setLayoutX(900);
        pond.setLayoutY(100);
        ImageView barn = Game.getImageView("barn.png");
        barn.setLayoutX(400);
        barn.setLayoutY(100);
        ImageView greenHouse = Game.getImageView("greenhouse.png");
        greenHouse.setLayoutX(760);
        greenHouse.setLayoutY(100);
        ImageView[][] fields = new ImageView[3][3];
        Image[][] fruits = new Image[3][3];
        ImageView[][] treeFields = new ImageView[3][2];

        Farm farm = (Farm) this.placeController.place;
        Fields realFields = farm.getFields();
        for (int i = 0; i < 3 ; i++) {
            for(int j = 0 ; j < 3 ; j++) {
                if (realFields.getField(i * 3 + j) instanceof EmptyField) {
                    EmptyField emptyField = (EmptyField) realFields.getField(i * 3 + j);
                    if (!emptyField.isPlowed())
                        fields[i][j] = Game.getImageView("OurField.png");
                    else
                        fields[i][j] = Game.getImageView("shokhm.PNG");
                    group.getChildren().add(fields[i][j]);
                }
                else {
                    FullField fullField = (FullField) realFields.getField(i * 3 + j);
                    fields[i][j] = Game.getImageView("shokhm.png");
                    switch (fullField.getCrop().getName()) {
                        case "Tomato":
                            fruits[i][j] = Game.getImage("tomato.png");
                            break;
                        case "Strawberry":
                            fruits[i][j] = Game.getImage("Strawberry.png");
                            break;
                        case "Watermelon":
                            fruits[i][j] = Game.getImage("WaterMelon.PNG");
                            break;
                        case "Carrot":
                            fruits[i][j] = Game.getImage("Carrot.PNG");
                            break;
                        default:
                            fruits[i][j] = Game.getImage("apple.png");
                    }
                    group.getChildren().add(fields[i][j]);
                    for (int k = 0; k < 3; k++) {
                        for (int l = 0; l < 3; l++) {
                            ImageView imageView = new ImageView(fruits[i][j]);
                            imageView.setLayoutX(700 + 100 * i + 33 * k);
                            imageView.setLayoutY(300 + 100 * j + 33 * l);
                            group.getChildren().add(imageView);
                        }
                    }
                }
                fields[i][j].setLayoutX(700 + 100 * i);
                fields[i][j].setLayoutY(300 + 100 * j);
                interactables.put(fields[i][j] , "Fields");
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                treeFields[i][j] = Game.getImageView("treeField.png");
                treeFields[i][j].setLayoutX(200 + i * 130);
                treeFields[i][j].setLayoutY(300 + j * 190);
                group.getChildren().add(treeFields[i][j]);
                interactables.put(treeFields[i][j] , "Fruit Garden");
            }
        }
        interactables.put(house, "Home");
        interactables.put(pond, "Pond");
        interactables.put(barn, "Barn");
        interactables.put(greenHouse, "Greenhouse");
        ImageView hero = getImageView("avatarFront.gif");
        this.hero = hero;
        group.getChildren().add(this.hero);
        ImageView village = new ImageView(new Image("file:icons/door.png"));
        ImageView village2 = new ImageView(new Image("file:icons/door.png"));
        village.setLayoutX(520);
        village2.setLayoutX(620);
        village.setLayoutY(50);
        village2.setLayoutY(50);
        interactables.put(village , "Village");
        interactables.put(village2 , "Village");
        Text text = new Text("Village");
        text.setLayoutX(580);
        text.setLayoutY(130);
        ImageView forest1 = new ImageView(new Image("file:icons/forestTree.png"));
        ImageView forest2 = new ImageView(new Image("file:icons/forestTree.png"));
        forest1.setLayoutX(510);
        forest2.setLayoutX(610);
        forest1.setLayoutY(900);
        forest2.setLayoutY(900);
        Text text1 = new Text("Forest");
        interactables.put(forest1 , "Forest");
        interactables.put(forest2 , "Forest");
        text1.setLayoutX(570);
        text1.setLayoutY(850);
        time.setLayoutX(1100);
        time.setLayoutY(100);
        group.getChildren().addAll(house, pond, barn, greenHouse, village , village2, text, text1 , forest1 , forest2 , time);
        ImagePattern pattern = new ImagePattern(image);
        scene = new Scene(group , W , H);
        scene.setFill(pattern);
        group.getChildren().add(stat);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;
    }
    public Scene barnScene(){
        Scene scene;
        Group group = new Group();
        barnMachines = new ArrayList<>();
        Image image = new Image("file:icons/ourBarn.png");
        ImageView[] cows = new ImageView[5];
        ImageView[] cowsPlace = new ImageView[5];
        ImageView[] sheep = new ImageView[5];
        ImageView[] sheepPlace = new ImageView[5];
        ImageView[] chickens = new ImageView[10];
        ImageView[] chickensPlace = new ImageView[10];
        ImageView[] machines = new ImageView[5];
        time.setLayoutX(1100);
        time.setLayoutY(100);
        for (int i = 0; i < 5; i++) {
            cowsPlace[i] = new ImageView(new Image("file:icons/hay.png"));
            cows[i] = new ImageView();
            cows[i].setImage(new Image("file:icons/one.gif")); //in khato felan bardar!
            cowsPlace[i].setLayoutX(700 + 100 * i);
            cows[i].setLayoutX(700 + 100 * i);
            group.getChildren().addAll(cowsPlace[i],cows[i]);
            interactables.put(cows[i] , "Cows");
        }
        for(int i = 0 ; i < 5 ; i++){
            sheepPlace[i] = new ImageView(new Image("file:icons/hay.png"));
            sheep[i] = new ImageView();
            sheep[i].setImage(new Image("file:icons/two.gif"));
            sheepPlace[i].setLayoutX(50 + 100 * i);
            sheep[i].setLayoutX(70 + 100 * i);
            group.getChildren().addAll(sheepPlace[i],sheep[i]);
            interactables.put(sheep[i], "Sheep");
        }
        for(int i = 0 ; i < 10 ; i++){
            chickens[i] = new ImageView();
            chickensPlace[i] = new ImageView(new Image("file:icons/hay1.png"));
            chickens[i].setImage(new Image("file:icons/three.gif"));

            if(i <= 4){
                chickensPlace[i].setLayoutX(60 + 70 * i);
                chickensPlace[i].setLayoutY(530);
                chickens[i].setLayoutX(50 + 70 * i);
                chickens[i].setLayoutY(500);
            }
            else{
                chickensPlace[i].setLayoutX(60 + 70 * (9 - i));
                chickensPlace[i].setLayoutY(630);
                chickens[i].setLayoutX(50 + 70 * (9 - i ));
                chickens[i].setLayoutY(600);
            }
            group.getChildren().addAll(chickensPlace[i],chickens[i]);
            interactables.put(chickens[i] , "Chickens");
        }
        for(int i = 0 ; i < 5 ; i++){
            machines[i] = new ImageView();
            machines[i].setLayoutY(500);
            machines[i].setLayoutX(800 + i * 80);
            group.getChildren().add(machines[i]);
            interactables.put(machines[i] , "Machines");
            barnMachines.add(machines[i]);
        }
        final int[] a = {0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    a[0] = getRandomAnimation();
                }
            }
        });
        final Transition[] transition = new Transition[1];
        cows[0].setOnMouseClicked(e -> {
            transition[0] = move(a[0], cows[0]);
            System.out.println(transition[0]);
            transition[0].play();
        });
        Text door = new Text("Farm");
        door.setLayoutX(570);
        door.setLayoutY(800);
        door.setScaleX(2);
        door.setScaleY(2);
        group.getChildren().add(door);
        interactables.put(door , "Farm");
        scene = new Scene(group , 1200 , 900);
        ImagePattern imagePattern = new ImagePattern(image);
        ImageView hero = getImageView("avatarFront.gif");
        this.hero = hero;
        group.getChildren().addAll(this.hero, time);
        scene.setFill(imagePattern);
        group.getChildren().add(stat);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;
    }

    private Transition move(int number , ImageView node) {
        Random r = new Random();
        double x =600 + 600 * r.nextDouble();
        double y = 50 * r.nextDouble();
        LineTo lineTo = new LineTo(node.getLayoutX() , node.getLayoutY());
        LineTo lineTo1 = new LineTo(x , y);
        Path path = new Path();
        MoveTo moveTo = new MoveTo(node.getLayoutX(), node.getLayoutY());
        path.getElements().add(moveTo);
        path.getElements().addAll(lineTo1, lineTo);
        path.getElements().add(moveTo);
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(3000));
        pathTransition.setNode(node);
        pathTransition.setPath(path);
        pathTransition.setCycleCount(4);
        pathTransition.setAutoReverse(false);
        return pathTransition;
    }

    private int getRandomAnimation() {
        Random r = new Random();
        return r.nextInt(5);
    }

    public Scene forestScene(){
        ArrayList<Location> locations = new ArrayList<>();
        Scene scene;
        Group group = new Group();
        interactables = new HashMap<>();
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/jungle.png"));
        ImageView cave = new ImageView(new Image("file:icons/theCave.png"));
        Random r = new Random();
        int woodNum = r.nextInt(15);
        ImageView woods[] = new ImageView[woodNum];
        System.out.println(woodNum);
        for(int i = 0 ; i < woodNum ; i++){
            woods[i] = new ImageView(new Image("file:icons/woods.png"));
            double x , y;
            x = 200 + (800) * r.nextDouble();
            y = 200 + 600 * r.nextDouble();
            Location location = new Location(x, y);
            for(int j = 0 ; j < locations.size() ; j++){
                if(location.equals(locations.get(j))){
                    x = 200 + (800) * r.nextDouble();
                    y = 200 + 600 * r.nextDouble();
                    location = new Location(x, y);
                }
            }
            locations.add(location);
            woods[i].setLayoutX(x);
            woods[i].setLayoutY(y);
            group.getChildren().addAll(woods[i]);
            interactables.put(woods[i] , "Woods");
        }
        int rockNum = r.nextInt(15);
        group.getChildren().add(cave);
        cave.setLayoutX(100);
        time.setLayoutX(1100);
        time.setLayoutY(100);
        group.getChildren().add(time);
        cave.setLayoutY(100);
        // group.getChildren().add(woods);
        ImageView[] rocks = new ImageView[rockNum];
        for (int i = 0; i < rockNum; i++) {
            rocks[i] = new ImageView(new Image("file:icons/rocks.png"));
            double x , y;
            x = 200 + (800) * r.nextDouble();
            y = 200 + 600 * r.nextDouble();
            Location location = new Location(x, y);
            for(int j = 0 ; j < locations.size() ; j++){
                if(location.equals(locations.get(j))){
                    x = 200 + (800) * r.nextDouble();
                    y = 200 + 600 * r.nextDouble();
                    location = new Location(x, y);
                }
            }
            locations.add(location);
            rocks[i].setLayoutX(x);
            rocks[i].setLayoutY(y);
            group.getChildren().add(rocks[i]);
            interactables.put(rocks[i] , "Rocks");
        }
        int kondeNum = r.nextInt(15);
        ImageView[] konde = new ImageView[kondeNum];
        for (int i = 0; i < kondeNum; i++) {
            konde[i] = new ImageView(new Image("file:icons/kondee.png"));
            double x , y;
            x = 200 + (800) * r.nextDouble();
            y = 200 + 600 * r.nextDouble();
            Location location = new Location(x, y);
            for(int j = 0 ; j < locations.size() ; j++){
                if(location.equals(locations.get(j))){
                    x = 200 + (800) * r.nextDouble();
                    y = 200 + 600 * r.nextDouble();
                    location = new Location(x, y);
                }
            }
            locations.add(location);
            konde[i].setLayoutX(x);
            konde[i].setLayoutY(y);
            group.getChildren().add(konde[i]);
            interactables.put(konde[i] , "Woods");
        }
        ImageView[] doors = new ImageView[4];
        Image image = new Image("file:icons/door.png");
        for (int i = 0; i < 4 ; i++) {
            doors[i] = new ImageView(image);
            group.getChildren().add(doors[i]);
            doors[i].setLayoutY(10);
            doors[i].setLayoutX(400 + i * 100);
        }
        interactables.put(doors[0] , "Farm");
        interactables.put(doors[1] , "Farm");
        Text text = new Text("Farm");
        text.setLayoutX(470);
        text.setLayoutY(100);
        interactables.put(doors[2] , "Village");
        interactables.put(doors[3] , "Village");
        Text text1 = new Text("Village");
        text1.setLayoutX(670);
        text1.setLayoutY(100);
        ImageView river = new ImageView(new Image("file:icons/river.png"));
        river.setScaleX(1.3);
        river.setLayoutX(300);
        river.setLayoutY(900);
        group.getChildren().add(river);

        interactables.put(river , "River");
        interactables.put(cave, "Cave");
        ImageView hero = getImageView("avatarFront.gif");
        this.hero = hero;
        group.getChildren().addAll(this.hero, text, text1);
        scene = new Scene(group, 1200, 1000);
        scene.setFill(imagePattern);
        group.getChildren().add(stat);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;

    }
    public Scene caveScene(){
        Scene scene;
        Group group = new Group();
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/Cave.png"));
        Random r = new Random();
        interactables = new HashMap<>();
        int rocksNum = r.nextInt(100);
        ImageView[] rocks = new ImageView[rocksNum];
        for(int i = 0 ; i < rocksNum ; i++){
            rocks[i] = new ImageView(new Image("file:icons/rocks.png"));
            double x = 10 + ( 1090 ) * r.nextDouble();
            double y = 10 + (890) * r.nextDouble();
            rocks[i].setLayoutX(x);
            rocks[i].setLayoutY(y);
            interactables.put(rocks[i], "Rocks");
            group.getChildren().add(rocks[i]);

        }
        Image image = new Image("file:icons/caveDoor.png");
        ImageView door1 = new ImageView(image);
        ImageView door2 = new ImageView(image);
        door1.setLayoutX(500);
        door1.setLayoutY(10);
        door2.setLayoutX(600);
        time.setLayoutX(1100);
        time.setLayoutY(100);
        group.getChildren().add(time);
        door2.setLayoutY(10);
        interactables.put(door1 , "Forest");
        interactables.put(door2, "Forest");
        group.getChildren().addAll(door1 , door2);
        scene = new Scene(group);
        scene.setFill(imagePattern);
        group.getChildren().add(stat);
        ImageView hero = getImageView("avatarFront.gif");
        this.hero = hero;
        group.getChildren().add(this.hero);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;

    }
    public Scene homeScene(){
        Scene scene;
        interactables = new HashMap<>();
        Image farsh = new Image("file:icons/newHome.png");
        ImageView bed = Game.getImageView("bed.png");
        bed.setLayoutX(49);
        bed.setLayoutY(250);
        bed.setScaleX(1.3);
        bed.setScaleY(2);
        ImageView door = new ImageView(new Image("file:icons/littleDoor.png"));
        door.setLayoutX(600);
        door.setLayoutY(800);
        door.setScaleX(3);
        door.setScaleY(3);

        interactables.put(door , "Farm");
        ImageView storageBox = Game.getImageView("StorageBox.png");
        storageBox.setLayoutX(50);
        storageBox.setLayoutY(600);
        ImageView kitchen = Game.getImageView("Kitchen.png");
        kitchen.setLayoutX(1050);
        kitchen.setLayoutY(250);
        kitchen.setScaleX(2);
        kitchen.setScaleY(2);
        Group group = new Group(bed , storageBox , kitchen);
        time.setLayoutX(1100);
        time.setLayoutY(100);
        group.getChildren().add(stat);
        group.getChildren().add(time);
        scene = new Scene(group , 1200, 1000);
        interactables.put(bed, "Bed");
        interactables.put(storageBox , "Storage Box");
        interactables.put(kitchen , "Kitchen");
        ImageView hero = getImageView("avatarFront.gif");
        this.hero = hero;
        group.getChildren().addAll(this.hero, door);
        this.hero.setLayoutX(600);
        this.hero.setLayoutY(900);
        ImagePattern imagePattern = new ImagePattern(farsh);
        scene.setFill(imagePattern);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;
    }
    public Scene greenHouseScene(){
        Scene scene;
        interactables = new HashMap<>();
        Group group = new Group();
        ImageView[][] fields = new ImageView[2][2];
        for(int i = 0 ; i < 2 ; i++){
            for(int j = 0 ; j < 2 ; j++){
                fields[i][j] = new ImageView(new Image("file:icons/fields.png"));
                fields[i][j].setLayoutX(500 + i * 100);
                fields[i][j].setLayoutY(400 + j * 100);
                group.getChildren().add(fields[i][j]);
                interactables.put(fields[i][j] , "Fields");
            }
        }
        ImageView weatherMachine = new ImageView(new Image("file:icons/weatherMachine.png"));
        interactables.put(weatherMachine , "Weather Machine");
        weatherMachine.setLayoutX(900);
        weatherMachine.setLayoutY(500);
        Text text = new Text("don't laugh, it's a weather machine!");
        text.setStroke(Color.BLACK);
        text.setX(800);
        text.setY(440);
        text.setScaleX(1);
        ImageView door = new ImageView(new Image("file:icons/littleDoor.png"));
        door.setLayoutX(550);
        door.setLayoutY(1000);
        door.setScaleX(3);
        time.setLayoutX(1100);
        time.setLayoutY(100);
        group.getChildren().add(time);
        door.setScaleY(3);
        interactables.put(door , "Farm");
        group.getChildren().add(door);
        scene = new Scene(group , 1200 , 1000);
        scene.setFill(new ImagePattern(new Image("file:icons/backGroundGreenHouse.png")));
        ImageView hero = getImageView("avatarFront.gif");
        this.hero = hero;
        group.getChildren().add(stat);
        group.getChildren().add(this.hero);
        group.getChildren().addAll( weatherMachine, text);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;

    }
    public Scene villageScene(){
        Scene scene;
        mediaPlayer.play();
        Group group = new Group();
        ImageView cafe = new ImageView(new Image("file:icons/cafe.png"));
        interactables = new HashMap<>();
        interactables.put(cafe, "Cafe");
        cafe.setLayoutX(1000);
        cafe.setLayoutY(80);
        ImageView market = new ImageView(new Image("file:icons/Market.png"));
        interactables.put(market , "Market");
        market.setLayoutX(780);
        market.setLayoutY(65);
        market.setScaleX(2);
        market.setScaleY(2.3);
        ImageView lab = new ImageView(new Image("file:icons/lab.png"));
        interactables.put(lab, "Laboratory");
        lab.setLayoutX(270);
        lab.setLayoutY(90);
        ImageView ranch = new ImageView(new Image("file:icons/dam.png"));
        interactables.put(ranch , "Ranch");
        ranch.setX(80);
        ranch.setLayoutY(90);
        ImageView clinic = new ImageView(new Image("file:icons/clinic.gif"));
        interactables.put(clinic , "Clinic");
        clinic.setLayoutX(1030);
        clinic.setLayoutY(800);
        ImageView gym = new ImageView(new Image("file:icons/gym.png"));
        interactables.put(gym , "Gym");
        gym.setLayoutX(160);
        gym.setLayoutY(750);
        gym.setScaleX(0.3);
        gym.setScaleY(0.3);
        ImageView workShop = new ImageView(new Image("file:icons/workShop.png"));
        workShop.setLayoutX(0);
        workShop.setLayoutY(750);
        workShop.setScaleX(0.3);
        group.getChildren().add(stat);
        time.setLayoutX(1100);
        time.setLayoutY(100);
        group.getChildren().add(time);
        workShop.setScaleY(0.3);
        ImageView door = new ImageView(new Image("file:icons/goToFarm.png"));
        door.setLayoutX(43);
        door.setLayoutY(478);
        interactables.put(door , "Farm");
        group.getChildren().addAll(door);
        interactables.put(workShop, "WorkShop");
        group.getChildren().addAll(clinic, cafe , market , lab, ranch, gym, workShop);
        scene = new Scene(group , 1200 , 1000);
        scene.setFill(new ImagePattern(new Image("file:icons/newVillage.png")));
        ImageView hero = getImageView("avatarFront.gif");
        this.hero = hero;
        group.getChildren().add(this.hero);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;
    }
    public Scene marketScene(){
        interactables = new HashMap<>();
        ImageView generalStore = new ImageView(new Image("file:icons/generalStore.png"));
        interactables.put(generalStore , "General Store");
        generalStore.setLayoutX(700);
        generalStore.setLayoutY(100);
        Text text = new Text("General Store");
        text.setLayoutX(750);
        text.setLayoutY(350);
        text.setStroke(Color.BLACK);
        ImageView butchery = new ImageView(new Image("file:icons/butchery.png"));
        interactables.put(butchery, "Butchery");
        Text text1 = new Text("Butchery");
        text1.setLayoutX(450);
        text1.setLayoutY(650);
        text1.setStroke(Color.BLACK);
        butchery.setLayoutX(400);
        butchery.setLayoutY(400);
        ImageView groceries = new ImageView(new Image("file:icons/groceries.png"));
        groceries.setLayoutX(700);
        Text text2 = new Text("Groceries Store");
        text2.setLayoutX(750);
        text2.setLayoutY(950);
        text2.setStroke(Color.BLACK);
        groceries.setLayoutY(700);
        interactables.put(groceries , "Groceries Store");
        Group group = new Group(generalStore , butchery , groceries, text , text1, text2);
        scene = new Scene(group , 1200 , 1000);
        scene.setFill(new ImagePattern(new Image("file:icons/Store.png")));
        ImageView hero = getImageView("avatarFront.gif");
        time.setLayoutX(1100);
        time.setLayoutY(100);
        ImageView door = new ImageView(new Image("file:icons/theDoor.png"));
        door.setLayoutX(600);
        door.setLayoutY(950);
        group.getChildren().add(door);
        interactables.put(door, "Village");
        group.getChildren().add(time);
        this.hero = hero;
        this.hero.setLayoutX(500);
        this.hero.setLayoutY(900);
        group.getChildren().add(stat);
        group.getChildren().add(this.hero);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;
    }
    public Scene cafeScene(){
        Game.cafeView = Cafe.cafe.placeController.placeView;
        mediaPlayer.stop();
        final boolean[] cafePlaying = {false};
        Scene scene;
        ImagePattern imagePattern = new ImagePattern(new Image("file:icons/kafe.png"));
        ImageView table = new ImageView(new Image("file:icons/desk.png"));
        interactables = new HashMap<>();
        interactables.put(table, "Dining Table");
        ImageView missionBoard = new ImageView(new Image("file:icons/missionBoard.png"));
        Group group = new Group(missionBoard);
        table.setLayoutX(370);
        table.setLayoutY(930);
        missionBoard.setLayoutX(100);
        missionBoard.setLayoutY(100);
        ImageView piano = new ImageView(new Image("file:icons/pianoo.png"));
        group.getChildren().add(piano);
        piano.setLayoutX(920);
        piano.setLayoutY(170);
        piano.setScaleX(1.5);
        piano.setScaleY(1.5);
        piano.setOnMouseClicked(e -> {
            if(!cafePlaying[0]){
                cafePlaying[0] = true;
                mediaPlayer1.play();
            }

            else{
                cafePlaying[0] = false;
                mediaPlayer1.stop();
            }

        });
        interactables.put(missionBoard, "Mission Board");
        scene = new Scene(group , 1200 , 1000);
        ImageView hero = getImageView("avatarFront.gif");
        time.setLayoutX(500);
        time.setLayoutY(1100);
        this.hero = hero;
        ImageView door = new ImageView(new Image("file:icons/theDoor.png"));
        door.setLayoutX(600);
        door.setLayoutY(930);
        group.getChildren().add(door);
        group.getChildren().add(stat);
        interactables.put(door, "Village");
        group.getChildren().add(this.hero);
        group.getChildren().add(time);
        scene.setFill(imagePattern);
        Pause pauseObject = new Pause();
        pause.setLayoutX(1100);
        pause.setLayoutY(300);
        group.getChildren().add(pause);
        group.getChildren().addAll(progressBar, health);
        pause.setOnAction(e -> {
            Controller.addMenu(pauseObject.inspectableController);
        });
        return scene;
    }
}
