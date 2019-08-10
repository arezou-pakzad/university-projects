import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.File;
import java.util.HashMap;

abstract class View {
    protected Scene scene;
    protected Controller controller;

    public View(Controller controller) {
        this.controller = controller;
    }

    protected final static String projectPath = "/Users/Aida/Desktop/ap_last/team_26/Project/icons/";
    public static final double W = 1200, H = 1000;

    public static ImageView getImageView(String fileName) {
        File file = new File(projectPath + fileName);
        Image image = new Image(file.toURI().toString());
        return new ImageView(image);
    }

    public static Image getImage(String fileName) {
        File file = new File(projectPath + fileName);
        return new Image(file.toURI().toString());
    }

    public View() {
//        buildScene();
    }

    public Scene getScene() {
        return scene;
    }

    public abstract void buildScene();


    ////////
    void help() {
        System.out.println("WhereAmI");
        System.out.println("back");
        System.out.println("help");
        System.out.println("Stat");
    }

    static void invalidChoice(String itemName) {
        System.out.println("You have to choose a " + itemName);
    }

    static void showStats(String[] statDetails){
        for (int i = 0; i < statDetails.length; i++){
            System.out.println(statDetails[i]);
        }
    }

    public void fullBackPack() {
        System.out.println("Backpack is full");
    }

    public static void chooseFromBackPack(String itemName, int itemNum) {
        if (itemNum == 1)
            System.out.println("Choose a " + itemName + " from backpack");
        else
            System.out.println("Choose " + itemNum + itemName + "s from backpack");
    }

    void notEnoughMaterials(HashMap<String, Integer> materials, String name) { //material name for money is Gil
        System.out.print("You need ");
        for (String material :
                materials.keySet()) {
            System.out.print(materials.get(material) + " "  + material + "(s), ");
        }
        System.out.println("for " + name);
    }
}
