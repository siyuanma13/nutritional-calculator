
public class FilterRule {
	public final Nutrient ntype;
	public final String compareMethod;
	public final String lookupVal;
	public final Double numericLookupVal;
	
	public FilterRule (String rule) {
		String[] pieces = rule.split(" ");
		Nutrient foundType = Nutrient.NAME;
		for ( Nutrient n : Nutrient.values()) {
			if(n.name().equals(pieces[0])) {
				foundType = n;
				break;
			}
		}
		this.ntype = foundType;
		this.compareMethod = pieces[1];
		this.lookupVal = pieces[2];
		if(!(ntype.equals(Nutrient.NAME))) {
			this.numericLookupVal = Double.valueOf(lookupVal);
		} else {
			this.numericLookupVal = null;
		}
	}
	
	public FilterRule (String ntypeStr, String compareMethod, String lookupVal) {
		Nutrient foundType = Nutrient.NAME;
		for (Nutrient n: Nutrient.values()) {
			if (n.name().equals(ntypeStr)) {
				foundType = n;
				break;
			}
		}
		this.ntype = foundType;
		this.compareMethod = compareMethod;
		this.lookupVal = lookupVal;
		if(!(ntype.equals(Nutrient.NAME))) {
			this.numericLookupVal = Double.valueOf(lookupVal);
		} else {
			this.numericLookupVal = null;
		}
	}
		
	public String toString() {
		return ntype.toString()+" " + compareMethod + " " +lookupVal;
	}
	
	
}


