import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Scanner;

class Gym extends Inspectable {
    ArrayList<Exercise> energyExercises = Holder.gymEnergyItems;
    ArrayList<Exercise> healthExercises = Holder.gymHealthItems;

    public Gym() {
        setName("Gym");
        setCommands("Energy", "Health");
        inspectableController = new GymController(this);
    }

    public void addEnergyExercise(Exercise exercise){
        energyExercises.add(exercise);
    }

    public void removeEnergyExercise(Exercise exercise){
        energyExercises.remove(exercise);
    }

    public void addHealthExercise(Exercise exercise){
        healthExercises.add(exercise);
    }

    public void removeHealthExercise(Exercise exercise){
        healthExercises.remove(exercise);
    }
}

class GymController extends InspectableController {
    private Gym gym;
    private GymView gymView = new GymView(this);
    public GymController(Gym gym){
        super(gym);
        this.gym = gym;
        setView(gymView);
    }

    @Override
    public void runMethods(int commandNum) {
        ArrayList<Exercise> exerciseField = new ArrayList<>();
        if (commandNum == 1)
            exerciseField = gym.energyExercises;
        else if (commandNum == 2)
            exerciseField = gym.healthExercises;
        ArrayList<String> commands1 = new ArrayList<>();
        int number = MultiChoicesPopUps.display("Gym","Choose your exercise" ,"Max amount" , "Refill rate" , "Consumption rate");
//        int scan1 = super.scan();
        //TODO- yekari vase in scane in vasat bokon
        Exercise exercise = exerciseField.get(number);
        Stat energyDec = exercise.getEnergyDecrease();
        int exercisePrice = exercise.getPrice();
        ArrayList<String> commands2 = new ArrayList<>();
        commands2.add("Status");
        commands2.add("Train");
        String result = "";
        int number2 = MultiChoicesPopUps.display("Gym" , "Choose" , "Status" , "Train");
        if (number2 == 0) {
            if (number == 0) {
                String stat = "Current " + exercise.getExercise().getType() + " : " + Person.getPerson().getSingleStat(exercise.getExercise()).getMaximum() + " ";
                String condition = Person.getPerson().getSingleStat(exercise.getExercise()).canImproveMax() ? "Training add : " + exercise.getExercise().getMaximum() : "Can't exercise";
                result = stat + condition;
            } else if (number == 1) {
                String stat = "Current " + exercise.getExercise().getType() + " : " + Person.getPerson().getSingleStat(exercise.getExercise()).getRefillRate() + " ";
                String condition = (Person.getPerson().getSingleStat(exercise.getExercise()).canImproveRefill()) ? "Training add : " + Person.getPerson().getSingleStat(exercise.getExercise()).getRefillRate() : "Can't exercise";
                result = stat + condition;

            } else if (number == 2) {
                String stat = "Current " + exercise.getExercise().getType() + " : " + Person.getPerson().getSingleStat(exercise.getExercise()).getConsumptionRate();
                String condition = Person.getPerson().getSingleStat(exercise.getExercise()).canImproveConsumption() ? "Training add : " + exercise.getExercise().getConsumptionRate() : "Can't exercise";

            }
            Popup.makePopup(result);

        }
        else if (number2 == 1)
        {
            boolean flag = false;
            boolean answer = ConfirmBox.display("Gym","This training will cost you " + exercisePrice +" Gil. Is this okay? (Y/N)");
            if (answer)
            {
                if (exercise.getExercise().getMaximum() > 0 && Person.getPerson().getSingleStat(exercise.getExercise()).canImproveMax()){
                    if (energyDec.getCurrent()*(-1) <= Person.getPerson().getStats().get(0).getCurrent() && exercisePrice <= Person.getPerson().getMoney()) {
                        Person.getPerson().changeStats(exercise.getExercise());
                        Person.getPerson().changeStats(energyDec);
                        Person.getPerson().takeMoney(exercisePrice);
                        flag = true;
                    }

                }
                if (exercise.getExercise().getRefillRate() > 0 && Person.getPerson().getSingleStat(exercise.getExercise()).canImproveRefill())
                {
                    if (energyDec.getCurrent()*(-1) <= Person.getPerson().getStats().get(0).getCurrent() && exercisePrice <= Person.getPerson().getMoney()) {
                        Person.getPerson().changeStats(exercise.getExercise());
                        Person.getPerson().changeStats(energyDec);
                        Person.getPerson().takeMoney(exercisePrice);
                        flag = true;
                    }
                }
                if (exercise.getExercise().getConsumptionRate() > 0 && Person.getPerson().getSingleStat(exercise.getExercise()).canImproveConsumption()){
                    if (energyDec.getCurrent()*(-1) <= Person.getPerson().getStats().get(0).getCurrent() && exercisePrice <= Person.getPerson().getMoney()) {
                        Person.getPerson().changeStats(exercise.getExercise());
                        Person.getPerson().changeStats(energyDec);
                        Person.getPerson().takeMoney(exercisePrice);
                        flag = true;

                    }
                }
                else
                    Popup.makePopup("You can't exercise");
                if (flag) {
                    exercise.update();
                    flag = false;
                }
            }
            else
                back();
        }
//        int scan1 = super.scan();
    }
}

class GymView extends InspectableView {
    GymController gymController;
    public GymView(InspectableController inspectableController) {
        super(inspectableController);
        gymController = (GymController) inspectableController;
    }

    public void showStatStatus(String exerciseName, float personStat, float exerciseAdd, boolean canExercise){
        System.out.println("Current " + exerciseName + ": " + personStat);
        if (canExercise)
            System.out.println("Training add: " + exerciseAdd);
        else
            cantExercise();
    }

    public void exerciseQuestion(int price){
        System.out.println("This training will cost you " + price +" Gil. Is this okay? (Y/N)");
    }

    public void cantExercise(){
        System.out.println("You can't exercise!");
    }
}
