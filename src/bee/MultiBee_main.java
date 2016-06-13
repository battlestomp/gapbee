package bee;

import gap.GapProblem;
import gap.NSGAII_main;

import java.io.IOException;
import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import jmetal.util.Ranking;



public class MultiBee_main {

	public static SolutionSet getpf(SolutionSet solutions){
		SolutionSet FeasibleSolution = new SolutionSet(solutions.size());
		for (int i=0; i<solutions.size(); i++) {
	        if (solutions.get(i).getOverallConstraintViolation() == 0.0) {
	        	FeasibleSolution.add(solutions.get(i));
	        }
		}
		// Ranking the union
		Ranking ranking = new Ranking(FeasibleSolution);
		int index = 0;
		SolutionSet front = null;
		// Obtain the next front
		front = ranking.getSubfront(index);
		return front;
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, JMException {
		// TODO Auto-generated method stub
		GapProblem problem; // The problem to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator

		HashMap parameters; // Operator parameters
		QualityIndicator indicators; // Object to get quality indicators

		NSGAII_main TestData = new NSGAII_main();
		String fileName = "./Data/gapa.txt";
		TestData.readFile(fileName);
		problem = new GapProblem("IntSolutionType",TestData.ListOfProblems.get(1));
		algorithm = new mulitgapBee(problem);
		algorithm.setInputParameter("numberfoods",100);
		algorithm.setInputParameter("numberlimit", 50);
		algorithm.setInputParameter("iterations", 1000000);
		long initTime = System.currentTimeMillis();
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;
		System.out.printf("Total execution time: " + estimatedTime + "ms;" + "populationszie:" + population.size()+"\n");
		
		getpf(population).printFeasibleFUN("./output/bee");
	}

}
