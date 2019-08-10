
public class Bed extends Inspectable {
    public Bed(){
        setName("Bed");
        inspectableController = new BedController(this);
        setCommands("Sleep and Save Game", "Sleep without saving");

    }
    public void sleep(){
        Game.nextDay();
        Person.getPerson().sleep();
        inspectableController.showStats();
    }
    public void sleepAndHelp(){
        Person.getPerson().sleep();
        inspectableController.showStats();
        //and save which we are not gonna do anythnig about it in this phase :)
    }
}

class BedController extends InspectableController {
    private Bed bed;
    private BedView bedView = new BedView(this);
    public BedController(Bed bed1) {
        super(bed1);
        bed = bed1;
        setView(bedView);
    }
    @Override
    public void runMethods(int commandNum){
        if(commandNum == 1){
            bed.sleepAndHelp();
            return;
        }
        else if(commandNum == 2){
            bed.sleep();
            return;
        }
    }
}

class BedView extends InspectableView {
    private BedController bedController;

    public BedView(BedController bedController) {
        super(bedController);
        this.bedController = bedController;
    }
//    @Override
//    public void buildScene(){
//        Scene bedScene;
//        Group group = new Group();
//        ArrayList<String> commands = bedController.getInspectable().getCommands();
////        ImagePattern imagePattern = new ImagePattern(new Image("file:pattern.jpg"));
//        Button[] buttons = new Button[commands.size()];
//        final int commad;
//        for (int i = 0; i < buttons.length ; i++) {
//            buttons[i] = new Button(commands.get(i));
//            buttons[i].setLayoutX(180);
//            buttons[i].setLayoutY(20 + i * 50);
//            group.getChildren().add(buttons[i]);
//        }
//
//        Button back = new Button("Back");
//        buttons[0].setOnAction(e -> {
//            bedController.runMethods(1);
//            bedController.back();
//        });
//        buttons[1].setOnAction(e -> {
//            bedController.runMethods(2);
//            bedController.back();
//        });
//        back.setLayoutX(230);
//        back.setLayoutY(400);
//        group.getChildren().add(back);
//        back.setOnAction(e -> {
//            bedController.back();
//        });
//        bedScene = new Scene(group , 500 , 500);
////        bedScene.setFill(imagePattern);
//        this.scene = bedScene;
//    }

}
