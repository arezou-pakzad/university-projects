public class Pause extends Inspectable{
    boolean isPaused = false;
    public Pause(){
        inspectableController = new PauseController(this);
        setCommands("Show the backPack",
        "Continue" , "Show status" , "Quit to main menu");
    }
}
class PauseView extends InspectableView{

    public PauseView(InspectableController inspectableController) {
        super(inspectableController);
    }
}
class PauseController extends InspectableController{
    PauseView pauseView = new PauseView(this);
    public PauseController(Pause pause){
        super(pause);
        setView(pauseView);
    }
    @Override
    public void runMethods(int command){
        if(command == 1){
            addMenu(BackPack.getBackPack().inspectableController);
        }
        else if(command == 2){
            this.back();
        }
        else if(command ==3){
            float energy = Person.getPerson().getStats().get(0).getCurrent();
            float health = Person.getPerson().getStats().get(1).getCurrent();
            String stat = "Energy : " + energy + "\n" + "Health : " + health + "\n" +
                    "Day : " + ((((PlaceView.nowTime - PlaceView.firstTime) / 1000000000) / 60) / 20) + "\n" +
                    "Money : " + Person.getPerson().getMoney();
            Popup.makePopup(stat);
        }
        else if(command == 4){
           Controller.clearStack();
           Game.view = Game.firstView;
           Game.changeScene(Game.firstView);
        }
    }
}