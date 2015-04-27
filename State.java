package EMS;

public class State {
	private double insideTemperature, tankTemperature, outsideTemp;
	private double energyCost, pv, pump_energy;
	private int status, timestep;
	private State childOn, childOff;
	
	public State(int timestep, double insideTemperature, double tankTemperature, double pump_energy, int status){
		if(timestep < 0 || timestep > Utilities.MAXSIZE){
			System.out.println("Invalid timestep!");
			System.exit(-1);
		}
		this.insideTemperature = insideTemperature;
		this.tankTemperature = tankTemperature;
		this.timestep = timestep;
		this.pump_energy = pump_energy;		
		this.status = status;
		
		outsideTemp = Utilities.getOutsideTemperature(timestep-1);
		pv = Utilities.getPVProduction(timestep-1);
	}
	
	public State createOnChild(){
		double predictedTemp = RModel.predictTemperature(outsideTemp, tankTemperature, insideTemperature, pv, pump_energy, 1);
		predictedTemp += this.getInsideTemperature();
		int timestep = this.timestep+1;

		double tankTemp = 1.0714*calculateEnergy(2, this.tankTemperature) + this.tankTemperature;
		State child = new State(timestep, predictedTemp, tankTemp, 0.007547755, 1);
		child.energyCost = 2*Utilities.getPrice(timestep);
		this.childOn = child;
		return child;
	}
	
	public State createOffChild(){
		double predictedTemp = RModel.predictTemperature(outsideTemp, tankTemperature, insideTemperature, pv, pump_energy, 1);
		predictedTemp += this.getInsideTemperature();
		int timestep = this.timestep+1;

		if(predictedTemp >= Utilities.getComfortTemperature(timestep)){
			double tankTemp = 1.0714*calculateEnergy(0, this.tankTemperature) + this.tankTemperature;
			
			State child = new State(timestep, predictedTemp, tankTemp, 0, 0);
			child.energyCost = 0;
			this.childOff = child;
			return child;
		}
		else{
			return null;
		}
	}
	
	private double calculateEnergy(double energyConsumed, double tank_temp){
		double amount, energyOutHP, energyToheating;
		
		energyOutHP = energyConsumed*(5.14 - 0.05968*tank_temp - energyConsumed*0.00089);
		if(status == 0){
			energyToheating = 2.61974 + 0.051993*tank_temp;			
		}
		else{
			energyToheating = 2.61928 + 0.051993*tank_temp;			
		}
		amount = energyOutHP - energyToheating;
		
		return amount;
	}
		
	public State getOnState(){
		return childOn;
	}
	
	public State getOffState(){
		return childOff;
	}
	
	public double getInsideTemperature(){
		return insideTemperature;
	}
	
	public double getTankTemperature(){
		return tankTemperature;
	}
	
	public double getOutSideTemperature(){
		return outsideTemp;
	}
	
	public double getPV(){
		return pv;
	}
	
	public int getStatus(){
		return status;
	}
	
	public double getEnergyCost(){
		return energyCost;
	}
	
	public double getEnergyPump(){
		return pump_energy;
	}
	
	public void updateEnergyCost(double childCost){
		energyCost += childCost;
	}
}
