package bee;

import gap.GapProblem;
import gap.NSGAII_main;

import java.io.IOException;
import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

public class MultiBee_main {

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
		population.printFeasibleFUN("./output/bee");
	}

}
