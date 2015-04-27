package EMS;

import org.rosuda.JRI.Rengine;

public class RModel {
	
	private static Rengine re;
	
	public static void startEngine(){
		re = new Rengine(null, false, null);		
		// the engine creates R is a new thread, and waits until it is ready
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            System.exit(0);
        } 
        System.out.println("R is loaded");
	}
	
	public static void stopEngine(){
        re.end(); 
        System.out.println("R is terminated");
	}
	
	public static double predictTemperature(double out, double tank, double in, double pv, double pump, int status){		
		double temperature;           	
    	re.eval("row = data.frame(OutsideTemp="+out+",TankTemp="+tank+",InsideTemp="+in+",EnergyCirculationPump="+pump+",GreenEnergy="+pv+")");
    	
    	// depending on the state predict the delta temperature
        if(status == 0){
        	re.eval("load(\"C:/Users/Admin/Documents/off_model.rda\")");
        	temperature = re.eval("pdt=predict(off_model,row)").asDouble();
        }   	
        else{
        	re.eval("load(\"C:/Users/Admin/Documents/on_model.rda\")");
        	temperature = re.eval("pdt=predict(on_model,row)").asDouble();
        }
        
    	return temperature;
	}
}
