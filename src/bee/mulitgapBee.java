package bee;

import java.util.Comparator;
import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.OverallConstraintViolationComparator;
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
	int MaxDominated = 15;
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

class SolutionOperator{
	Solution cur;
	int flag;
}

public class mulitgapBee extends Algorithm {
	Source Food;
	int Iterations;
	GapProblem GAP;
	private static final Comparator dominance_ = new DominanceComparator();
	private static final Comparator constraint_ = new OverallConstraintViolationComparator();
	public mulitgapBee(Problem problem) {
		super (problem) ;
		GAP = (GapProblem)problem;
	}
	
	void initialize() throws ClassNotFoundException, JMException{
		int nfoods = ((Integer) getInputParameter("numberfoods")).intValue();
		int nlimit = ((Integer) getInputParameter("numberlimit")).intValue();
		Iterations = ((Integer) getInputParameter("iterations")).intValue();
		Food = new Source(nfoods, nlimit);
		Solution newSolution;
		for (int i = 0; i < nfoods; i++) {
			newSolution = new Solution(problem_);
			int[] d = GAP.Grasp();
			for(int j=0; j<d.length; j++){
				newSolution.getDecisionVariables()[j].setValue(d[j]);
			}
			problem_.evaluate(newSolution);
			//problem_.evaluateConstraints(newSolution);
			Food.Sources.add(newSolution);
		}
	}
	void SendEmployedBees() throws JMException{
		for (int i=0; i<Food.NumFoodSources; i++){
			Update(i);
		}
	}
	void SendOnlookerBees() throws JMException{
		caculatefitness();
		for (int i=0; i<Food.NumFoodSources; i++){
			if (PseudoRandom.randDouble()<=Food.SourcesProbability[i]){
		        Update(i);
		      }
		}
	}
	void SendScoutBees() throws JMException, ClassNotFoundException{
		for (int i=0; i<Food.NumFoodSources; i++){
			//if ((Food.LimitSources[i]>Food.MaxLimit) && (Food.SourcesDominationeds[i]>Food.MaxDominated) ){
			if (Food.LimitSources[i]>Food.MaxLimit){
				Solution newsolution = new Solution(problem_);
				problem_.evaluate(newsolution);
				problem_.evaluateConstraints(newsolution);
				Food.Sources.replace(i, newsolution);
				Food.LimitSources[i] = 0;
	        }
	      }		
	}
	
	void caculatefitness(){
		for (int i=0; i<Food.NumFoodSources; i++)
	    	Food.SourcesDominations[i]= 0;
		for (int i=0; i<Food.NumFoodSources; i++)
	    	Food.SourcesDominationeds[i]= 0;
		for (int i=0; i<Food.NumFoodSources; i++)
			for (int j=0; j<Food.NumFoodSources; j++){
				SolutionOperator result = betterSolution(Food.Sources.get(i), Food.Sources.get(j));
				  if (result.flag == -1)
	    	          Food.SourcesDominations[i] = Food.SourcesDominations[i] + 1;
	    	        else if (result.flag == 1)
	    	          Food.SourcesDominationeds[i] =  Food.SourcesDominationeds[i] + 1;
			}
		double[] SourcesFitness = new double[Food.NumFoodSources];
		for (int i=0; i<Food.NumFoodSources; i++)
			SourcesFitness[i] = 0;
		double TotalFitness = 0;
		for (int i=0; i<Food.NumFoodSources; i++){
			 SourcesFitness[i] = (double)Food.SourcesDominations[i]/(double)Food.NumFoodSources;
			 TotalFitness = TotalFitness + SourcesFitness[i];
		}
		for (int i=0; i<Food.NumFoodSources; i++){
			 Food.SourcesProbability[i] = SourcesFitness[i]/TotalFitness;
		}
	}
	void Update(int pos) throws JMException{
		int pos1 = pos;
		int pos2 = PseudoRandom.randInt(0,Food.Sources.size()-1);
	    while ((pos1 == pos2) && (Food.Sources.size()>1)) {
	        pos2 = PseudoRandom.randInt(0,Food.Sources.size()-1);
	        }
	    Solution cur = Food.Sources.get(pos1);
	    Solution next =Food.Sources.get(pos2);
	    
	    SolutionOperator mutationoperator = BeeMutation(cur);
		if (mutationoperator.flag == 1){
			Food.Sources.replace(pos1, mutationoperator.cur);
			Food.LimitSources[pos1] = 0;
			
			return;
		}
		
		SolutionOperator crossoveroperator = BeeCrossover(cur, next);
		if (crossoveroperator.flag == 1){
			Food.Sources.replace(pos1, crossoveroperator.cur);
			Food.LimitSources[pos1] = 0;
			return;
		}
		Food.LimitSources[pos1] = Food.LimitSources[pos1] + 1;
	}

	SolutionOperator BeeMutation(Solution cur) throws JMException{
		Operator mutation; // Mutation operator
		HashMap parameters; // Operator parameters
		parameters = new HashMap();
	    parameters.put("probability", 1.0) ;
	    parameters.put("distributionIndex", 20.0) ;     
	    mutation = new GAPMutation(parameters, GAP);
	    Solution newsolution = (Solution) mutation.execute(new Solution(cur));
	    GAP.evaluate(newsolution);
		return betterSolution(cur, newsolution);
		
	}
	SolutionOperator betterSolution(Solution cur, Solution next){
		SolutionOperator result = new SolutionOperator();
		result.flag =constraint_.compare(cur, next);
		 if (result.flag == 0) {
			 result.flag =dominance_.compare(cur, next);
		 }
		 if (result.flag == -1)
			 result.cur = cur;
		 else if (result.flag == 1)
			 result.cur = next;
		 return result;
	}
	SolutionOperator BeeCrossover(Solution cur, Solution next) throws JMException{
		Operator crossover; // Crossover operator
		HashMap parameters; // Operator parameters
		parameters = new HashMap();
		parameters.put("probability", 1.00);
		parameters.put("distributionIndex", 20.0);
		crossover = new UniformCrossover(parameters);
		Solution[] parents = new Solution[2];
		parents[0] =cur;
		parents[1] = next;
		Solution[] offSpring = (Solution[]) crossover.execute(parents);
		GAP.evaluate(offSpring[0]);
		return betterSolution(cur, offSpring[0]);
	}
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		// TODO Auto-generated method stub
	   initialize();
	   for (int i=0; i<Food.NumFoodSources; i++){
	      SendEmployedBees();
	      SendOnlookerBees();
	      SendScoutBees();
	    }
		return Food.Sources;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
