package operator;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.operators.mutation.Mutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import jmetal.encodings.solutionType.IntSolutionType;

import gap.GapProblem;
/**
 * This class implements a swap mutation. The solution type of the solution
 * must be Permutation.
 */
public class GAPMutation extends Mutation{
	/**
	 * Valid solution types to apply this operator 
	 */
	private static final List VALID_TYPES = Arrays.asList(IntSolutionType.class) ;

	private Double mutationProbability_ = null ;
	private GapProblem Gap;
	/** 
	 * Constructor
	 */
	public GAPMutation(HashMap<String, Object> parameters, GapProblem Problem) {    
		super(parameters) ;
		Gap = Problem;
		if (parameters.get("probability") != null)
			mutationProbability_ = (Double) parameters.get("probability") ;  		
	} // Constructor


	/**
	 * Constructor
	 */
	//public SwapMutation(Properties properties) {
	//  this();
	//} // Constructor

	/**
	 * Performs the operation
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException 
	 */
	public void doMutation(double probability, Solution solution) throws JMException {   
		if (solution.getType().getClass() == IntSolutionType.class) {
			if (PseudoRandom.randDouble() < probability) {
				for(int i=0; i<Gap.GetNumofAgents(); i++){
					double randnum = PseudoRandom.randDouble();
					if (randnum <= 0.20) {
						int itemp = (int)solution.getDecisionVariables()[i].getValue();
						while(true){
							int ilower = (int)solution.getDecisionVariables()[i].getLowerBound();
							int iupper = (int)solution.getDecisionVariables()[i].getUpperBound();
							int irnd = PseudoRandom.randInt(ilower, iupper);
							if (irnd != itemp){
								solution.getDecisionVariables()[i].setValue(irnd);
								break;
							}
						}	  
					}
					else if(randnum>0.20 && randnum<=0.50){
						solution.getDecisionVariables()[i].setValue(Gap.GetLessRequired(i));
					}
					else if(randnum>0.50){
						solution.getDecisionVariables()[i].setValue(Gap.GetLessRecources(i));
					}
				}		 
			}

		} // if
		else  {
			Configuration.logger_.severe("SwapMutation.doMutation: invalid type. " +
					""+ solution.getDecisionVariables()[0].getVariableType());

			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".doMutation()") ;
		}
	} // doMutation

	/**
	 * Executes the operation
	 * @param object An object containing the solution to mutate
	 * @return an object containing the mutated solution
	 * @throws JMException 
	 */
	public Object execute(Object object) throws JMException {
		Solution solution = (Solution)object;

		if (!VALID_TYPES.contains(solution.getType().getClass())) {
			Configuration.logger_.severe("SwapMutation.execute: the solution " +
					"is not of the right type. The type should be 'Binary', " +
					"'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if 


		this.doMutation(mutationProbability_, solution);
		return solution;
	} // execute  
} // SwapMutation
