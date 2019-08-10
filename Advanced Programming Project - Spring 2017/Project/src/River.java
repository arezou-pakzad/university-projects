//TODO- fishing probability with fishing rod
public class River extends Inspectable {
    public River()
    {   setName("River");
        setCommands("Start Fishing");
        inspectableController = new RiverController(this);
    }
    public int fish(Tool tool) { //toole fishing Rod nadarim ke ;/(ehtemalan bayad chek konim ke esmesh fishing rod hast ya na
        //class probibility ro misazim (bayad chek konim ke backpack ye jae khali dare
        int numberOfFish = Constants.generateProbability(0, 20);

        for(int i = 0 ; i < numberOfFish ; i++){
            BackPack.getBackPack().addItem(new Product("Fish" , "Fish" , false)); //be nazaram tu classe animal tu constructoresh ham esmesho behesh bedim ham typo
        }
        return numberOfFish;
    }
}
class RiverController extends InspectableController {
    private River river;
    private RiverView riverView = new RiverView(this);
//    private RiverView riverView = new RiverView();
    public RiverController(River river1){
        super(river1);
        river = river1;
        setView(riverView);
//        inspectableView = riverView;
    }
    @Override
    public void runMethods(int commandNum){
        if(commandNum == 1){
            Tool tool = (Tool) BackPack.getBackPack().backPackController.getItemFromBackPack("FishingRod", 1);
            if (tool == null)
                return;
            // TODO - implement
        }
    }
}
class RiverView extends InspectableView{

    public RiverView(InspectableController inspectableController) {
        super(inspectableController);
    }
}
