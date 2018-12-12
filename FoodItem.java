import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a food item with all its properties.
 * 
 * @author aka 
 */
public class FoodItem {
    // The name of the food item.
    private String name;
    
    // The id of the food item.
    private String id;
    
    // Map of nutrients and value.
    private HashMap<String, Double> nutrients;
    
    /**
     * Constructor
     * @param name name of the food item
     * @param id unique id of the food item 
     */
    public FoodItem(String id, String name) {
        // TODO : Complete
    	
    	this.id = id;
    	this.name = name; 
    	this.nutrients = new HashMap<String, Double>();
    	
    }
    
    /**
     * Gets the name of the food item
     * 
     * @return name of the food item
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the unique id of the food item
     * 
     * @return id of the food item
     */
    public String getID() {
        // TODO : Complete
        return this.id;
    }
    
    /**
     * Gets the nutrients of the food item
     * 
     * @return nutrients of the food item
     */
    public HashMap<String, Double> getNutrients() {
        // TODO : Complete
        return this.nutrients;
    }

    /**
     * Adds a nutrient and its value to this food. 
     * If nutrient already exists, updates its value.
     */
    public void addNutrient(String name, double value) {
        // TODO : Complete
    
    	this.nutrients.put(name, value);
    	
    	
    	return;
    }

    /**
     * Returns the value of the given nutrient for this food item. 
     * If not present, then returns 0.
     */
    public double getNutrientValue(String name) {
        // TODO : Complete
        return this.nutrients.get(name);
    }
    
	/**
	 * Returns the string representation for this item as its name
	 */
    @Override
    public String toString() {
    	return this.getName();
    }
}