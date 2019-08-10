public class WeatherMachine extends Machine {
    private Season season;
    private Greenhouse greenhouse;
    public WeatherMachine(Greenhouse greenhouse, String name, String type, boolean isSpecial, int power) {
        super("Weather Machine");
        this.greenhouse = greenhouse;
        setCommands("Spring Weather" , "Summer Weather" , "Autumn Weather" , "Tropical Weather");
        inspectableController = new WeatherMachineController(this);
    }

    public void setSeason(Season season) {
        this.season = season;
        greenhouse.getFields().setSeason(season);
    }

    public Season getSeason() {
        return season;
    }
}

class WeatherMachineController extends MachineController {
    private WeatherMachine weatherMachine;
    private InspectableView weatherMachineView = new InspectableView(this);
    WeatherMachineController(WeatherMachine machine) {
        super(machine);
        weatherMachine = machine;
        setView(weatherMachineView);
    }
    @Override
    public void runMethods(int commandNum){
        if(commandNum <= 0 || commandNum > weatherMachine.getCommands().size()){
            System.out.println("Invalid Target");
            return;
        }
        else
            switch (commandNum){
                case 0 :
                    weatherMachine.setSeason(Season.Spring);
                    break;
                case 1 :
                    weatherMachine.setSeason(Season.Summer);
                    break;
                case 2 :
                    weatherMachine.setSeason(Season.Autumn);
                    break;
                case 3 :
                    weatherMachine.setSeason(Season.Winter);
                    break;
                case 4 :
                    weatherMachine.setSeason(Season.Tropical);
                    break;
            }
        back();
    }
}
