import java.util.ArrayList;

public class StorageBox extends Storage {
    public StorageBox(int maxValue){
        super(Integer.MAX_VALUE);
        setName("Storage Box");
        setCommands("Put in item", "Take out item");
        inspectableController = new StorageBoxController(this);
        items = new ArrayList<Item>();
    }
}

class StorageBoxController extends StorageController {
    private StorageBox storageBox;
    private StorageView storageView = new StorageView(this);

    StorageBoxController(StorageBox storageBox) {
        super(storageBox);
        this.storageBox = storageBox;
        setView(storageView);
    }

    @Override
    public void runMethods(int commandNum) {
        if (commandNum == 1) {
            Item item = BackPackController.getItemFromBackPack(); //in tabe  graphic backPack dare!
            if (item == null)
                return;
            BackPack.getBackPack().removeItem(item, 1);
            storageBox.addItem(item);

        } else if (commandNum == 2) { //take out item
            storageView.getItems(storageBox.items);
            String[] commands = new String[storageBox.items.size()];
            for (int i = 0; i < storageBox.items.size(); i++)
                commands[i] = storageBox.items.get(i).getName();
            int number = MultiChoicesPopUps.display("Storage Box" , "Choose" , commands);
                for (int i = 0; i < commands.length; i++) {
                    if(number == i){
                        if(!BackPack.getBackPack().isFulled()) {
                            BackPack.getBackPack().addItem(storageBox.items.get(i));
                            BackPack.getBackPack().setCommands();
                            storageBox.removeItem(storageBox.items.get(i), 1);
                        }
                        else {
                            AlertBox.display("full" , "Back pack is fulled!");
                        }
                    }
                }
//                Button back = new Button("Back");
//                back.setLayoutX(10);
//                back.setLayoutY(10);
//                back.setOnAction(e -> {
//                    stage.close();
//                });
//                Scene scene;
//                group.getChildren().addAll(back);
//                scene = new Scene(group , 500 , 500);
//                stage.setScene(scene);
//                stage.show();

        }
    }
}
