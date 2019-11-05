import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AprioriAlgorithm {

	// Variables
	private String dataFile; 
    private int numItems, numTransactions; 
    private double minSup, minCon, dataPercent;
    private ArrayList<ArrayList<Integer>> dataset; 
    private Map<String, Integer> fatherFreqList;
    private Set<Integer> items;
    private ArrayList<ArrayList<String>>combination;
    
	// Constructor 
	public AprioriAlgorithm (String dataFile, double minSup, double minCon, double dataPercent){
		this.dataFile = dataFile;
		this.minSup = minSup;    	
		this.minCon = minCon;
		this.dataPercent =dataPercent;
		this.dataset = new ArrayList<ArrayList<Integer>>();
		this.fatherFreqList =  new HashMap<String, Integer>();
		this.items = new HashSet<Integer>();
		intialize();
	}
	
	// Read data from file
	public void intialize() {
    	numItems = 0;
    	numTransactions=0;
		BufferedReader data_in;
		ArrayList<Integer> transation;
		try {
			double numOfLines = calcNumOfLines(dataFile);
			double maxRead = (int) numOfLines * dataPercent;
			data_in = new BufferedReader(new FileReader(dataFile));
			while (data_in.ready() && numTransactions < maxRead) {    
				transation = new ArrayList<Integer>();
	    		String line=data_in.readLine();
	    		numTransactions++;
	    		StringTokenizer t = new StringTokenizer(line," ");
	    		while (t.hasMoreTokens()) {
	    			int x = Integer.parseInt(t.nextToken());
	    			transation.add(x);
	    			items.add(x);
	    		}
	    		dataset.add(transation);
	    	}
			numItems = items.size();
			//output config info to the user
			System.out.println("Input configuration: "+numItems+" items, "+numTransactions+" transactions, ");
			System.out.println("minsup = "+minSup*100+"%");
			System.out.println("minCon = "+minCon*100+"%");
			/*System.out.println("Dataset = "+ dataset.size());
			for(int i=0; i< 10; i++) {
				System.out.println(dataset.get(i));
			}*/
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Calculate number of liene 
	public double calcNumOfLines(String filename) {
		InputStream is;
		 double count = 0.00;
			try {
				is = new BufferedInputStream(new FileInputStream(filename));
		        byte[] c = new byte[1024];
		        int readChars = 0;
		        while ((readChars = is.read(c)) != -1) {
		            for (int i = 0; i < readChars; ++i) {
		                if (c[i] == '\n') {
		                ++count;
		            }
		        }
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	// Calculate frequency of each item alone
	private Map<String, Integer> calculateFirst(){
		ArrayList<Integer> currentTrans;
		Map<String, Integer> itemsFreq = new HashMap<>();
		for(int i=0; i<dataset.size(); i++) {
			currentTrans = dataset.get(i);
			for(int j=0; j<currentTrans.size(); j++) {
				String item = currentTrans.get(j).toString();
				if (!itemsFreq.containsKey(item)) {
					itemsFreq.put(item, 1);
				} else {
					int value = itemsFreq.get(item);
					value++;
					itemsFreq.put(item, value);
				}
			}
		}
		return itemsFreq;
	}
	
	// Calculate frequency of all items
	private Map<String, Integer> generateItemsFrequency(Map<String, Integer> firstItemsList, int step){
		Map<String, Integer> itemsFrequency =  new HashMap<String, Integer>();
		Map<String, Integer> removedItems =  new HashMap<String, Integer>();

		// Remove all elements less than minimum support
		for (String i : firstItemsList.keySet()) {
			int value = firstItemsList.get(i);
			if((value / (double) (numTransactions)) >= minSup) {
				itemsFrequency.put(i, value);
			}else {
				removedItems.put(i, value);
			}
	    }
		
		// Add all new in father list
		for (String i : itemsFrequency.keySet()) {
			int value = firstItemsList.get(i);
			fatherFreqList.put(i, value);
	    }
		
		// Complete till 2 items remains
		if (! (itemsFrequency.size() == 2 || itemsFrequency.size() == 0)) {
			generateNewItemList(removedItems, itemsFrequency, step+1);
		}
		return fatherFreqList;
	}
	
	// Generate the new list with it's frequency
	private void generateNewItemList(Map<String, Integer> removedItems, Map<String, Integer> itemsFrequency, int step) {
		Map<String, Integer> newItemsFreq =  new HashMap<String, Integer>();
		combination =  new ArrayList<ArrayList<String>>();
        int data[]=new int[step];
        // Copy to items to arraylist
        ArrayList<Integer> elements = new ArrayList<Integer>();
        for (String i : itemsFrequency.keySet()) {
        	elements.add(Integer.parseInt(i));
        }
 		
 		/*System.out.println(" -------------------- Sup ------------------");
		for (String i : itemsFrequency.keySet()) {
			int value = itemsFrequency.get(i);
			System.out.println("[" + i + ", " + value + "]");
	    }
		System.out.println(" -------------------- Sup ------------------");
 		*/
 		getCombination(elements,data, 0, itemsFrequency.size()-1, 0, step); 		

 		newItemsFreq = calcItemsFreq();
 		
 		/*System.out.println(" -------------------- Comb ------------------");
 		for (int i=0; i< combination.size(); i++) {
        	System.out.println(combination.get(i));
        }
 		System.out.println(" -------------------- Comb ------------------");
 		
 		System.out.println(" ------------------- New ------------------- ");
		for (String i : newItemsFreq.keySet()) {
			int value = newItemsFreq.get(i);
			System.out.println("[" + i + ", " + value + "]");
	    }
		System.out.println(" ------------------- New ------------------- ");
        */
 		generateItemsFrequency(newItemsFreq,step);
	}

	// Get all Combination of current items
	public void getCombination(ArrayList<Integer> arr, int data[], int start, int end, int index, int r) { 
		// Current combination is ready to be printed, print it 
        if (index == r) { 
        	ArrayList<String> items = new ArrayList<String>();
            for (int j=0; j<r; j++) {
            	items.add(Integer.toString(data[j]));
            }
            combination.add(items);
            return; 
        } 
        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
        	//System.out.println(arr.get(i));
            data[index] = arr.get(i);
            getCombination(arr, data, i+1, end, index+1, r); 
        }
    } 
	
	// Calculate frequency for some items
	public Map<String, Integer> calcItemsFreq(){
		Map<String, Integer> newItemsFreq = new HashMap<String, Integer>();
		for(int i=0; i< combination.size();i++) {
			int value = 0;
			String itemString = "";
			for (String s : combination.get(i)){
				itemString +=s;
			}
			for(int j=0; j< dataset.size();j++) {
				ArrayList<Integer> currentTrans = dataset.get(j);
				boolean here = true;
				for(int s=0; s < combination.get(i).size(); s++) {
					String string = combination.get(i).get(s);
					if(!currentTrans.contains(Integer.parseInt(string))) {
                		here = false;
                	}
                }
				if(here)
					value++;
			}
			if(value !=0)
				newItemsFreq.put(itemString, value);
		}
		return newItemsFreq;
	}
	
	// Generate Association Rule
	public Map<String, Integer> generateAssociationRule(Map<String, Integer> itemsFreq) {
		Map<String, Integer> associationRules = new HashMap<String, Integer>();

		return associationRules;
	}
	
	// Execute all Algorithm steps
	public void executeApriori(){
		
		// First Step
		Map<String, Integer> firstItemsList = calculateFirst();
		/*System.out.println(" ------------------------ First --------------------------------- ");
		for (int i : firstItemsList.keySet()) {
	      System.out.println("key: " + i + " value: " + firstItemsList.get(i));
	    }*/
		
		Map<String, Integer> itemsFrequency = generateItemsFrequency(firstItemsList,1);
		System.out.println(" ------------------------ Father --------------------------------- ");
		for (String i : itemsFrequency.keySet()) {
	      System.out.println(i + "  =>" + itemsFrequency.get(i));
	    }
		
		//Map<String, Integer> associationRules = generateItemsFrequency(itemsFrequency);
		/*System.out.println(" ------------------------ Association --------------------------------- ");
		for (int i : associationRules.keySet()) {
	      System.out.println("key: " + i + " value: " + associationRules.get(i));
	    }*/
		
		
	}
	
	public static void main(String[] args) {
		AprioriAlgorithm obj = new AprioriAlgorithm("data-set.txt", 0.21, 0.5, 0.00005);
		obj.executeApriori();
	}
}
