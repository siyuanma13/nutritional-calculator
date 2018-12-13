import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author Siyuan Ma, Sapan Gupta
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
	public class FoodQuery {
		private Set<FilterRule> rules;
		private FoodData db;
		
		public FoodQuery(FoodData db) {
			rules = new HashSet<FilterRule>();
			this.db = db;
		}
		
		public FoodQuery(FoodData db, Set<FilterRule> rules) {
			this.rules = new HashSet<FilterRule>(rules);
			this.db = db;
		}

		public FoodQuery(FoodData db, List<String> strRules) {
			this.rules = new HashSet<FilterRule>();
			for( String strRule : strRules) {
				this.rules.add(new FilterRule(strRule));
			}
			this.db = db;
		}
		
		public void addFilter(FilterRule newRule) {
			this.rules.add(newRule);
		}
		
		public Set<FoodItem> results () {
			Set<FoodItem> resultSet = new HashSet<FoodItem>();
			boolean first=true;
			for(FilterRule rule : rules) {
				if(first) {
					resultSet.addAll(applyOneFilter(rule));
					first=false;
				} else {
					resultSet.retainAll(applyOneFilter(rule));
				}
			}
			return resultSet;
		}
		
		private Set<FoodItem> applyOneFilter(FilterRule filter) {
			if(filter.ntype==Nutrient.NAME) {
				return applyNameFilter(filter);
			}
			BPTree<Double,FoodItem> index = db.indexes.get(filter.ntype.name());
			List<FoodItem> list = index.rangeSearch(filter.numericLookupVal, filter.compareMethod);
			return new HashSet<FoodItem>(list);
		}
		
		private Set<FoodItem> applyNameFilter(FilterRule filter) {
			return new HashSet(db.filterByName(filter.lookupVal));
		}
		
		public List<FoodItem> orderedResults() {
			if(rules.size() == 1) {
				FilterRule onlyRule = rules.iterator().next();
				if(onlyRule.ntype == Nutrient.NAME) {
					return db.filterByName(onlyRule.lookupVal);
				} else {
					BPTree<Double, FoodItem> tree = db.indexes.get(onlyRule.ntype.name());
					return tree.rangeSearch(onlyRule.numericLookupVal, onlyRule.compareMethod);
				}
			}
			return new LinkedList<FoodItem>(this.results());
		}
	}
	
    // List of all the food items.
    private List<FoodItem> foodItemList;

    // Map of nutrients and their corresponding index
    protected HashMap<String, BPTree<Double, FoodItem>> indexes;
    private BPTree<String, FoodItem> nameIndex;

    
    //fileName of csv
    private String fileName = "";
    

    
    /**
     * Public constructor
     */
    public FoodData() {

    	this.foodItemList = new LinkedList<FoodItem>();
    	
    
    	
    	//Not sure if this is correct, or if we have to do other things in the constructor besides initialize it 
    	this.indexes = new HashMap<String, BPTree<Double, FoodItem>>();
    	this.indexes.put("CALORIES", new BPTree<Double, FoodItem>(7));
    	this.indexes.put("FAT", new BPTree<Double, FoodItem>(7));
    	this.indexes.put("CARBOHYDRATE", new BPTree<Double, FoodItem>(7));
    	this.indexes.put("FIBER", new BPTree<Double, FoodItem>(7));
    	this.indexes.put("PROTEIN", new BPTree<Double, FoodItem>(7));
    	this.nameIndex = new BPTree<String, FoodItem>(7);

    }
    
    
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     * 
     * Siyuan 12/5: Implemented parsing csv to populate the foodItemList field,
     * then alphabetically sorts it. Haven't done
     * anything to populate the indexes field. 
     */
    
    @Override
    public void loadFoodItems(String filePath) {

    	BufferedReader br = null;
    	String line = "";
    	
    	
    	//this.loadSuccessful = false;
    	try {
    		br = new BufferedReader(new FileReader(filePath));
    		
    		while ((line = br.readLine()) != null) {
    			
    			
    			
    			//parse by comma 
    			String[] lineArray = line.split(",");
    			
    			
    			//continue if line is not formatted correctly. Need this in here,
    			//otherwise the example CSV will throw an exception 
    			if (lineArray.length<12) {
    				
    				continue;
    			}
    		
    			//create food item to add to our FoodItemList 
    			FoodItem fItem = new FoodItem(lineArray[0], lineArray[1]);
    			
    			
    			//Insert row checking part 
    			//add nutrients to food items 
    			//Not sure if we should use the String for name or some type of Enum 
    			
    			fItem.addNutrient(lineArray[2].toUpperCase(), Double.parseDouble(lineArray[3]));
    			fItem.addNutrient(lineArray[4].toUpperCase(), Double.parseDouble(lineArray[5]));
    			fItem.addNutrient(lineArray[6].toUpperCase(), Double.parseDouble(lineArray[7]));
    			fItem.addNutrient(lineArray[8].toUpperCase(), Double.parseDouble(lineArray[9]));
    			fItem.addNutrient(lineArray[10].toUpperCase(), Double.parseDouble(lineArray[11]));
    			
    			//add food item to index 
    			this.indexes.get("CALORIES").insert(fItem.getNutrientValue("CALORIES"), fItem);
    			this.indexes.get("FAT").insert(fItem.getNutrientValue("FAT"), fItem);
    			this.indexes.get("CARBOHYDRATE").insert(fItem.getNutrientValue("CARBOHYDRATE"), fItem);
    			this.indexes.get("FIBER").insert(fItem.getNutrientValue("FIBER"), fItem);
    			this.indexes.get("PROTEIN").insert(fItem.getNutrientValue("PROTEIN"), fItem);
    			this.nameIndex.insert(fItem.getName(), fItem);
    			
    			
    			//add food item to list 
    			this.foodItemList.add(fItem);
    			
    			
    		}

			this.fileName=filePath;
			
    	} catch (Exception e) {

    		this.foodItemList = null;
    		//System.out.println("File does not exist!");
    		
    		
    		
    	} finally { //close the buffered reader
    		
    		try {
    			if (br!=null) {
    				br.close();
    			};
    		} catch (IOException e2) {
    			System.out.println(e2.getMessage());
    		}
    	}
    	
    	//sort food alphabetically by name. 
    	if (this.foodItemList!=null) {
	    	Collections.sort(this.foodItemList, 
	    			(food1, food2) -> food1.getName().toUpperCase().compareTo(food2.getName().toUpperCase())
	    			);
    	}
    	
    	
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
      
    	//Create a new List to return 
    	List<FoodItem> nameFilteredList = new LinkedList<FoodItem>();
    	
    	//make the substring case-insensitive
    	String searchString = substring.toUpperCase();
    	
    	for (int i = 0; i<this.foodItemList.size(); i++) {
    		if (this.foodItemList.get(i).getName().toUpperCase().contains(searchString)) {
    			nameFilteredList.add(this.foodItemList.get(i));
    		}
    	}
        return nameFilteredList;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
    	FoodQuery query = new FoodQuery(this, rules);
    	return query.orderedResults();
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     * 
     * Siyuan 12/5: Implemented this for the foodItemList field.
     * 
     * Add the foodItem alphabetically. This is in linear time.
     */
    
    @Override
    public void addFoodItem(FoodItem foodItem) {
    	
    	//boolean to see if we have to add to end
    	boolean notAddToEnd = false;
    	
    	//Adds food item based on name, in alphabetical order. 
    	
    	//if list is empty, add to beginning of list
    	if (this.foodItemList.size()==0) {
    		this.foodItemList.add(foodItem);
    		
    	} else {
	    	
	    	for (int i = 0; i<this.foodItemList.size(); i++) {
	    		if (this.foodItemList.get(i).getName().toUpperCase().compareTo(foodItem.getName().toUpperCase())>=0){
	    			this.foodItemList.add(i, foodItem);
	    			notAddToEnd = true;
	    			break;
	    		}
	    	}
	    	
	    	if (!notAddToEnd) {
	    		this.foodItemList.add(foodItem);
	    	}
    	}
    	//Add to the Nutrient indexes
    	Map<String,Double> gotNutrients = foodItem.getNutrients();
    	System.out.println("This map was stored in foodItem.nutrients: " + gotNutrients);
    	for(String s : gotNutrients.keySet() ) {
    		BPTree<Double,FoodItem> index = indexes.get(s);
    		if(index!=null)
    		{
    			index.insert(gotNutrients.get(s), foodItem);
    		}
    	}
    	//Add to NameIndex
    	nameIndex.insert(foodItem.getName(), foodItem);
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     * 
     * Siyuan 12/5: return foodItemList
     * 
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
    	
    	if (this.foodItemList!=null) {
        	return this.foodItemList;
    	}
    	
    	else {
    		return null;
    	}
        
    }
    
    
    /**
     * Save the list of food items in ascending order by name
     * Siyuan Ma 12/5: Saves in the filename, with items sorted by name.
     * Again, didn't do anything with the INDEX field.
     * @param filename name of the file where the data needs to be saved 
     */
    
    public void saveFoodItems(String filename) {
    	
    	//create a new Filewriter object 
    	FileWriter fileWriter = null;
    	
    	try {
    		fileWriter = new FileWriter(filename);
    		
    		for (FoodItem fItem : this.foodItemList) {
    			fileWriter.append(fItem.getID());
    			fileWriter.append(",");
    			fileWriter.append(fItem.getName());
    			fileWriter.append(",");
    			fileWriter.append("calories");
    			fileWriter.append(",");
    			fileWriter.append(String.valueOf(fItem.getNutrients().get("CALORIES")));
    			fileWriter.append(",");
    			fileWriter.append("fat");
    			fileWriter.append(",");
    			fileWriter.append(String.valueOf(fItem.getNutrients().get("FAT")));
    			fileWriter.append(",");
    			fileWriter.append("carbohydrate");
    			fileWriter.append(",");
    			fileWriter.append(String.valueOf(fItem.getNutrients().get("CARBOHYDRATE")));
    			fileWriter.append(",");
    			fileWriter.append("fiber");
    			fileWriter.append(",");
    			fileWriter.append(String.valueOf(fItem.getNutrients().get("FIBER")));
    			fileWriter.append(",");
    			fileWriter.append("protein");
    			fileWriter.append(",");
    			fileWriter.append(String.valueOf(fItem.getNutrients().get("PROTEIN")));
    			fileWriter.append(",");
    			fileWriter.append("\n");
    			
    		}
    		
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	} finally { //close the buffered reader 
    		try {
    			fileWriter.flush();
    			fileWriter.close();
    		} catch (IOException e) {
    			System.out.println(e.getMessage());

    		}
    	}
    	
    }
     
    public static void main (String[] args) {
    	
    	FoodData f = new FoodData();
    	
    	f.loadFoodItems("foodItems.csv");
    	List<FoodItem> t = f.getAllFoodItems();
    	
    	FoodItem toAdd = new FoodItem("id", "amer");
    	
    	f.addFoodItem(toAdd);
    	
    	f.saveFoodItems("siyTest2.csv");
    	
    	
    	
    	for (int i = 0; i < t.size(); i++) {
    		
    		System.out.println(t.get(i).getName());
    	}
    	
    	System.out.println("size: "+ t.size());
    	
    	List<FoodItem> filteredByName = f.filterByName("365EverydayValue_FatFreeMilkVitaminAD");
    	
    	System.out.println("\nfiltered test");
    	for (int i = 0; i<filteredByName.size(); i++) {
    		System.out.println(filteredByName.get(i).getName());
    	}
    	
    	System.out.println("\n");
    	
    
    	
    }

}