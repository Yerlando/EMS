package EMS;

public class Predictor {

	public int runPrediction(State root, int timestep){
		// parameter checking for errors
		if(root == null){
			System.out.println("Root cannot be null!");
			System.exit(-1);
		}
		if(timestep < 0 || timestep > Utilities.MAXSIZE){
			System.out.println("Invalid timestep!");
			System.exit(-1);
		}
		
		int depth;
		
		// calculate the depth of tree
		if(timestep <= Utilities.MAXSIZE-4*Utilities.WINDOWHOURS){
			depth = 4*Utilities.WINDOWHOURS;
		}
		else{
			depth = Utilities.MAXSIZE - timestep;
		}
		
		// generates a tree
		createChilds(root, depth);
		
		// if a predicted temperature is below than comfort temperature then return 1 to turn on the heating
		if(root.createOffChild() == null){
			return 1;
		}
		
		// traverse the built tree and predict the next action
		State pstate = evaluatePrediction(root, depth);
		return pstate.getStatus();
	}
	
	private static void createChilds(State root, int depth){
		if(depth > 0){
			State on = root.createOnChild();
			State off = root.createOffChild();
			
			// if root node's off child is null then don't go further
			if(root.getStatus() == -1 && off == null)
				return;
			
			depth--;
			
			if(on != null){
				createChilds(on, depth);
			}
			if(off != null){
				createChilds(off, depth);
			}
		}
	}
	
	private static State evaluatePrediction(State root, int depth){		
		if(depth != 0){
			State on = root.getOnState();
			State off = root.getOffState();
			depth--;
			
			// traverse child states and calculate the energy cost
			if(on != null){
				on.updateEnergyCost(evaluatePrediction(on, depth).getEnergyCost());
			}
			if(off != null){
				off.updateEnergyCost(evaluatePrediction(off, depth).getEnergyCost());
			}
			
			// check the best state with the smallest energy cost
			if(on == null && off != null){
				return off;
			}
			else if( on != null && off == null){
				return on;
			}
			else{
				if(on.getEnergyCost() < off.getEnergyCost()){
					return on;
				}
				else{
					return off;
				}
			}
		}
		else{
			return root;
		}
	}
}
