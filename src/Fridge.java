
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Fridge {
    // Instance Variables
    private HashMap<String, FoodItem> inventoryByName;
    private TreeMap<LocalDate, List<FoodItem>> expirationIndex;
    private List<Recipe> recipes;

    // Constructor
    public Fridge(){
        inventoryByName = new HashMap<>();
        expirationIndex = new TreeMap<>();
        recipes = new ArrayList<>();
    }
    public Fridge(HashMap<String, FoodItem> inventory){
        this();
        // Adds inventory elements into the expirationIndex map
        for(FoodItem item : inventory.values()){
           addDateItem(item);
        }
    }

    // Getter Methods
    public HashMap<String, FoodItem> getInventoryByName() {
        return inventoryByName;
    }
    public TreeMap<LocalDate, List<FoodItem>> getExpirationIndex() {
        return expirationIndex;
    }
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Adds a FoodItem to the expirationIndex map
     * 
     * @param item the FoodItem to be added
     */
   private void addDateItem(FoodItem item) {
    LocalDate date = item.getExpirationDate();

    if (expirationIndex.containsKey(date)) {
        expirationIndex.get(date).add(item);
    } 
    else {
        List<FoodItem> list = new ArrayList<>();
        list.add(item);
        expirationIndex.put(date, list);
    }
}

    /**
     * Removes a FoodItem from the expirationIndex map
     * 
     * @param item the FoodItem to be removed
     * @return true if the item was removed, false otherwise
     */
    private boolean removeDateItem(FoodItem item) {
        LocalDate date = item.getExpirationDate();

        boolean removed = false;

        if (expirationIndex.containsKey(date)) {
            List<FoodItem> itemList = expirationIndex.get(date);
            removed = itemList.remove(item);
            if (itemList.isEmpty()) {
                expirationIndex.remove(date);
            }
        }

        return removed;
    }

    /**
     * Adds a FoodItem to the fridge inventory
     * 
     * @param item the FoodItem to be added
     */
    public void addFood(FoodItem item){
        String name = item.getNormalizedName();
        
        if(inventoryByName.containsKey(name)){
            FoodItem originalFood = inventoryByName.get(name);
            originalFood.setQuantity(originalFood.getQuantity() + item.getQuantity());
        }
        else {
            inventoryByName.put(item.getNormalizedName(), item);
        }
        addDateItem(item);
    }

    /**
     * Removes a FoodItem from the fridge inventory
     * 
     * @param item the FoodItem to be removed
     * @return true if the item was removed, false otherwise
     */
    public boolean removeFood(FoodItem item) {
        String name = item.getNormalizedName();
        boolean removed = false;

        if (inventoryByName.containsKey(name)) {
            FoodItem originalFood = inventoryByName.get(name);
            double newQuantity = originalFood.getQuantity() - item.getQuantity();

            if (newQuantity > 0) {
                originalFood.setQuantity(newQuantity);
            } 
            else {
                inventoryByName.remove(name);
                removeDateItem(item);
                removed = true;
            }
        }
        return removed;
        
    }

    /**
     * Adds a Recipe to the fridge recipe list
     * 
     * @param recipe the Recipe to be added
     */
    public void addRecipe(Recipe recipe){
        recipes.add(recipe);
    }

    /**
     * Removes a Recipe from the fridge recipe list
     * 
     * @param recipe the Recipe to be removed
     * @return true if the recipe was removed, false otherwise
     */
    public boolean removeRecipe(Recipe recipe) {
        boolean removed = false;
        
        if (recipes.contains(recipe)) {
            recipes.remove(recipe);
            removed = true;
        }
        
        return removed;
    }

    /**
     * Returns a list of FoodItems that are expiring within the given number of days
     * 
     * @param days the number of days
     * @return a list of FoodItems that are expiring within the given number of days
     */
    public List<FoodItem> getExpiringSoon(int days) {
        List<FoodItem> result = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (FoodItem item : inventoryByName.values()) {
            long d = item.getDaysUntilExpiration();

            if (d >= 0 && d <= days) {  // include today + next N days
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Returns a list of FoodItems that are expired
     * 
     * @return a list of FoodItems that are expired
     */
     public List<FoodItem> getExpired() {
        List<FoodItem> expired = new ArrayList<>();     
        LocalDate today = LocalDate.now();

        for (Map.Entry<LocalDate, List<FoodItem>> entry : expirationIndex.entrySet()) {
            LocalDate expirationDate = entry.getKey();
            if (expirationDate.isBefore(today) || expirationDate.isEqual(today)) {
                expired.addAll(entry.getValue());
            }
        }
        return expired;
    }
}