package bee;

import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import operator.GAPMutation;
import operator.UniformCrossover;
import gap.GapProblem;

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
	void Update(int pos){
		int pos1 = pos;
		int pos2 = PseudoRandom.randInt(0,Food.Sources.size()-1);
	    while ((pos1 == pos2) && (Food.Sources.size()>1)) {
	        pos2 = PseudoRandom.randInt(0,Food.Sources.size()-1);
	        }
	    Solution cur = new Solution(Food.Sources.get(pos1));
	    Solution next = new Solution(Food.Sources.get(pos2));
	}
	Solution UpdateSolution(Solution cur, Solution next){
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		HashMap parameters; // Operator parameters
		
		Solution newsolution = new Solution(cur);
		parameters = new HashMap();
		parameters.put("probability", 1.00);
		parameters.put("distributionIndex", 20.0);
		crossover = new UniformCrossover(parameters);

		parameters = new HashMap();
	    parameters.put("probability", 0.03) ;
	    parameters.put("distributionIndex", 20.0) ;     
	    mutation = new GAPMutation(parameters, (GapProblem)problem_);
	    
		return newsolution; 
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
