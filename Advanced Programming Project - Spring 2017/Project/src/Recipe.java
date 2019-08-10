import java.util.ArrayList;
import java.util.HashMap;

class RecipeView extends InspectableView{
    public RecipeView(InspectableController inspectableController) {
        super(inspectableController);
    }

    public void showMenu(String recipeName, ArrayList<String> toolNames, HashMap<String, Integer> ingredientNeeded, ArrayList<String> statNames, ArrayList<Float> statNums){
//        System.out.println("show menu recipe 1");
        System.out.println(recipeName + ":\n\n");
        System.out.println("Tools needed:\n");
        for (int i = 0; i < toolNames.size(); i++)
            System.out.println(toolNames.get(i));
        System.out.println("\ningredients:\n");
        for (String name : ingredientNeeded.keySet())
            System.out.println(name + " x" + ingredientNeeded.get(name));
        System.out.println("\nStats:\n");
        for (int i = 0; i < statNames.size(); i++)
        {
            if (statNums.get(i) > 0)
                System.out.println(statNames.get(i) + "+" + statNums.get(i));
            else
                System.out.println(statNames.get(i) + statNums.get(i));
        }
//        System.out.println("show menu recipe 1");
    }

}

class RecipeController extends InspectableController {
    private Recipe recipe;
    private RecipeView recipeView = new RecipeView(this);
    RecipeController(Recipe recipe) {
        super(recipe);
        this.recipe = recipe;
        setView(recipeView);
    }

    public void showRecipe(){
        ArrayList<String> toolNames = recipe.getTools();
        HashMap<String, Integer> ingredientNeeded = recipe.getIngredients();
        Eatable food = recipe.getFood();
        ArrayList<String> statNames = new ArrayList<String>();
        ArrayList<Float> statNums = new ArrayList<Float>();
        ArrayList<Stat> stats = food.getStats();
        for (int i = 0; i < stats.size(); i++)
            statNames.add(stats.get(i).getName());
        for (int i = 0; i < stats.size(); i++)
            statNums.add(stats.get(i).getCurrent());
        recipeView.showMenu(recipe.getName(), toolNames, ingredientNeeded, statNames, statNums);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void showMenu() {
        showRecipe();
    }

}

class Recipe extends Inspectable {

    private String name;
    private ArrayList<Tool> tools;
    protected HashMap<Product, Integer> ingredients = new HashMap<>();
    private Eatable food;

    public Recipe(String name, Eatable food) {
        setFood(food);
        setName(name);
        inspectableController = new RecipeController(this);
    }

    public void setTools(ArrayList<Tool> tools) {
        this.tools = tools;
    }

    public HashMap<String, Integer> getIngredients() {
        HashMap<String, Integer> ingridientNames = new HashMap<>();
        for (Item item : ingredients.keySet()){
            ingridientNames.put(item.getName(), ingredients.get(item));
        }
        return ingridientNames;
    }

    public void setIngredients(HashMap<Product, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public void setFood(Eatable food) {
        this.food = food;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Eatable getFood() {
        return food;
    }

    public ArrayList<String> getTools() {
        ArrayList<String> toolNames = new ArrayList<String>();
        for (int i = 0; i < tools.size(); i++)
            toolNames.add(tools.get(i).getName());
        return toolNames;
    }
}

//****************class Recipes**************************

class RecipesController extends InspectableController {
    private Recipes recipes;
    private RecipeView recipeView = new RecipeView(this);
    RecipesController(Recipes recipes) {
        super(recipes);
        this.recipes = recipes;
        setView(recipeView);
    }

    @Override
    public void showMenu() {
        recipes.setRecipes();
        super.showMenu();
        super.scan();
    }

    @Override
    public void runMethods(int commandNum) {
        Recipe recipe = recipes.getSingleRecipe(commandNum - 1);
        menus.add(recipe.inspectableController);
        recipe.inspectableController.showMenu();
    }

}

class Recipes extends Inspectable {
    private ArrayList<Recipe> recipes = new ArrayList<>();
    public Recipes() {
        setName("Recipes");
        inspectableController = new RecipesController(this);
        recipes = Holder.recipeArrayList;
        setRecipes();
    }

    public void setRecipes() {
        String[] recipeNames = new String[recipes.size()];
        for (int i = 0; i < recipes.size(); i++) {
        //    System.out.println(recipes.get(i).getName());
            recipeNames[i] = recipes.get(i).getName();
        }
        setCommands(recipeNames);
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public Recipe getSingleRecipe(int index) {
        return recipes.get(index);
    }

}
