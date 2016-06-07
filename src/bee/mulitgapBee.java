package bee;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;


class Source{
	int NumFoodSources;
	int MaxLimit; 
	int[]  LimitSources;
	SolutionSet Sources;
	double[]  SourcesProbability;
	int[] SourcesDominations;   		//支配其它解数量
	int[] SourcesDominationeds; 	//被其它解支配解数量
	Source(int nfoods, int mlimit){
		 NumFoodSources = nfoods;
		 MaxLimit = mlimit;
		 LimitSources = new int[NumFoodSources];
		 Sources = new SolutionSet(NumFoodSources);
		 SourcesProbability = new double[NumFoodSources];
		 SourcesDominations = new int[NumFoodSources];
		 SourcesDominationeds = new int[NumFoodSources];
	}
}

public class mulitgapBee extends Algorithm {
	Source Food;
	int Iterations;
	public mulitgapBee(Problem problem) {
		super (problem) ;
	}
	
	void initialize() throws ClassNotFoundException, JMException{
		int nfoods = ((Integer) getInputParameter("numberfoods")).intValue();
		int nlimit = ((Integer) getInputParameter("numberlimit")).intValue();
		Iterations = ((Integer) getInputParameter("iterations")).intValue();
		Food = new Source(nfoods, nlimit);
		Solution newSolution;
		for (int i = 0; i < nfoods; i++) {
			newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			Food.Sources.add(newSolution);
		}
	}
	void SendEmployedBees(){
		for (int i=0; i<Food.NumFoodSources; i++){
			
		}
	}
	void SendOnlookerBees(){
		
		
	}
	void SendScoutBees(){
		
	}
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
