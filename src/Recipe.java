
import java.util.ArrayList;
import java.util.List;

public class Recipe {
    // Instance Variables
    private String name;
    private List<String> steps;
    private List<IngredientLine> ingredients;
    private String imgFilePath;

    // Constructor
    public Recipe (String name, List<String> steps, List<IngredientLine> ingredients, String imgFilePath) {
        this.name = name;
        this.steps = steps;
        this.ingredients = ingredients;
        this.imgFilePath = imgFilePath;
    }

    // Getter Methods
    public String getName() {
        return name;
    }
    public List<String> getSteps() {
        return steps;
    }
    public List<IngredientLine> getIngredients(){
        return ingredients;
    }
    public String getImgFilePath() {
        return imgFilePath;
    }

    /**
     * Returns ingredient lines that are missing or insufficient in inventory
     * 
     * @param userFridge
     * @return List of missing IngredientLines
     */
    public List<IngredientLine> missingIngredients(Fridge userFridge) {
        List<IngredientLine> missing = new ArrayList<>();
        for (IngredientLine ingredient : ingredients) {
            for (FoodItem item : userFridge.getInventoryByName().values()) {
                if (ingredient.getName().equals(item.getNormalizedName())) {
                    if (ingredient.getAmount() > item.getQuantity()) {
                        double missingQuantity = ingredient.getAmount() - item.getQuantity();
                        missing.add(new IngredientLine(ingredient.getName(), missingQuantity, ingredient.getUnit()));
                    }
                }
            }
        }
        return missing;
    }

    /**
     * Checks whether the fridge contains enough ingredients to cook the recipe
     * 
     * @param userFridge
     * @return true if recipe can be made, false otherwise
     */
    public boolean canMake(Fridge userFridge) {
        boolean canCook = true;

        for (IngredientLine line : ingredients) {
                String name = line.getName();
                double requiredQuantity = line.getAmount();

                if (!userFridge.getInventoryByName().containsKey(name) || userFridge.getInventoryByName().get(name).getQuantity() < requiredQuantity) {
                    canCook = false;
                    break;
                }
        }

        return canCook;
    }

    /**
     * Cooks the recipe by deducting the required ingredients from the fride
     * 
     * @param userFridge
     * @return true if recipe was cooked successfully, false otherwise
     */
    public boolean cookRecipe(Fridge userFridge) {
        if (canMake(userFridge)) {
            for (IngredientLine line : ingredients) {
                String name = line.getName();
                double requiredQuantity = line.getAmount();

                FoodItem item = userFridge.getInventoryByName().get(name);
                item.setQuantity(item.getQuantity() - requiredQuantity);
            }
            return true;
        } 
        else {
            return false;
        }
    }
}
