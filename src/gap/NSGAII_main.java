//  NSGAII_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package gap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import gap.GapProblem;

import operator.GAPMutation;
import operator.UniformCrossover;

/**
 * Class to configure and execute the NSGA-II algorithm.
 * 
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba "On the Effect
 * of the Steady-State Selection Scheme in Multi-Objective Genetic Algorithms"
 * 5th International Conference, EMO 2009, pp: 183-197. April 2009)
 */

public class NSGAII_main {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object
	public List<GapProblem> ListOfProblems = new ArrayList<GapProblem>();
	int NumofProblem = 0;

	/**
	 * @param args
	 *            Command line arguments.
	 * @throws JMException
	 * @throws IOException
	 * @throws SecurityException
	 *             Usage: three options -
	 *             jmetal.metaheuristics.nsgaII.NSGAII_main -
	 *             jmetal.metaheuristics.nsgaII.NSGAII_main problemName -
	 *             jmetal.metaheuristics.nsgaII.NSGAII_main problemName
	 *             paretoFrontFile
	 */

	public void readFile(String fileName) throws IOException {
		File f = new File(fileName);
		FileReader file = new FileReader(f);
		BufferedReader br = new BufferedReader(file);
		String strtemp = br.readLine();
		NumofProblem = Integer.parseInt(strtemp.trim());
		for (int index = 1; index <= NumofProblem; index++) {
			GapProblem gap = new GapProblem();
			strtemp = br.readLine();
			String stringarray1[] = strtemp.split(" ");
			int iAgents = Integer.parseInt(stringarray1[1].trim());
			int iJobs = Integer.parseInt(stringarray1[2].trim());
			gap.SetNumofAgents(iAgents);
			gap.SetNumofJobs(iJobs);
			gap.initArray();
			for (int i = 0; i < iAgents; i++) { // setcost
				String sjobs[] = new String[iJobs];
				int Lenofsjobs = 0;
				while (Lenofsjobs < iJobs) {
					strtemp = br.readLine();
					String stringarray2[] = strtemp.split(" ");
					System.arraycopy(stringarray2, 1, sjobs, Lenofsjobs,
							stringarray2.length - 1);
					Lenofsjobs = Lenofsjobs + stringarray2.length - 1;
				}
				for (int j = 0; j < iJobs; j++) {
					int ivalue = Integer.parseInt(sjobs[j].trim());
					gap.SetArrayCost(i, j, ivalue);
				}
			}
			for (int i = 0; i < iAgents; i++) { // set resource consume
				String sjobs[] = new String[iJobs];
				int Lenofsjobs = 0;
				while (Lenofsjobs < iJobs) {
					strtemp = br.readLine();
					String stringarray2[] = strtemp.split(" ");
					System.arraycopy(stringarray2, 1, sjobs, Lenofsjobs,
							stringarray2.length - 1);
					Lenofsjobs = Lenofsjobs + stringarray2.length - 1;
				}
				for (int j = 0; j < iJobs; j++) {
					int ivalue = Integer.parseInt(sjobs[j].trim());
					gap.SetArrayComsuption(i, j, ivalue);
				}
			}
			String sconstraints[] = new String[iAgents];
			int lenofconstraint = 0;
			while(lenofconstraint<iAgents){
				strtemp = br.readLine();
				String stringarray3[] = strtemp.split(" "); // set constraint	
				System.arraycopy(stringarray3, 1, sconstraints, lenofconstraint, stringarray3.length-1);
				lenofconstraint = lenofconstraint + stringarray3.length - 1;
			}
			for (int k = 0; k < iAgents; k++) {
				int ivalue = Integer.parseInt(sconstraints[k].trim());
				gap.SetConstraint(k, ivalue);
			}
			ListOfProblems.add(gap);
		}
		br.close();
		file.close();
	}

	public static void main(String[] args) throws JMException,
	SecurityException, IOException, ClassNotFoundException {
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
		algorithm = new NSGAII(problem);
		// algorithm = new ssNSGAII(problem);

		int NumofPupulations = 100;
		
		// Algorithm parameters
		algorithm.setInputParameter("populationSize", NumofPupulations);
		algorithm.setInputParameter("maxEvaluations", 1500000);

		// Mutation and Crossover for Real codification
		parameters = new HashMap();
		parameters.put("probability", 0.90);
		parameters.put("distributionIndex", 20.0);
		crossover = new UniformCrossover(parameters);
		crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);
		
		parameters = new HashMap();
	    
	    parameters.put("probability", 0.03) ;
	    parameters.put("distributionIndex", 20.0) ;
		//mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);        
	    mutation = new GAPMutation(parameters, problem);

		// Selection Operator
	    parameters = null ;
	    //BinaryTournament
	    //selection = SelectionFactory.getSelectionOperator("RandomSelection", parameters) ;   
	    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;          
		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		 algorithm.setInputParameter("indicators", null) ;
		 
		long initTime = System.currentTimeMillis();
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;
		System.out.printf("Total execution time: " + estimatedTime + "ms;" + "populationszie:" + population.size()+"\n");
		population.printVariablesToFile("./output/VAR");
		population.printFeasibleFUN("./output/nsga");
		
		double tempmax = Double.MAX_VALUE;
		int tag=0;
		for(int i=0; i<population.size(); i++){
			Solution solution = population.get(i);
			if (solution.getOverallConstraintViolation()==0){
				if (solution.getObjective(0)<tempmax){
					tempmax = solution.getObjective(0);
					tag = i;
				}	
			}
		}
		System.out.println("min_ojbect1:"+tempmax+"tag:"+tag);
		
		
	} // if
} // NSGAII_main
