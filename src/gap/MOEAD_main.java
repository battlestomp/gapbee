//  MOEAD_main.java
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
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.Kursawe;
import jmetal.problems.ProblemFactory;
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

import operator.GAPMutation;
/**
 * This class executes the algorithm described in:
 *   H. Li and Q. Zhang, 
 *   "Multiobjective Optimization Problems with Complicated Pareto Sets,  MOEA/D 
 *   and NSGA-II". IEEE Trans on Evolutionary Computation, vol. 12,  no 2,  
 *   pp 284-302, April/2009.  
 */
public class MOEAD_main {
	public static Logger      logger_ ;      // Logger object
	public static FileHandler fileHandler_ ; // FileHandler object
	List<GapProblem> ListOfProblems = new ArrayList<GapProblem>();
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
	public void test(Problem   problem, int num)throws JMException, SecurityException, IOException, ClassNotFoundException{
		Algorithm algorithm ;         // The algorithm to use
		Operator  crossover ;         // Crossover operator
		Operator  mutation  ;         // Mutation operator

		QualityIndicator indicators ; // Object to get quality indicators

		HashMap  parameters ; // Operator parameters
		algorithm = new MOEAD(problem);

		// Algorithm parameters
		algorithm.setInputParameter("populationSize",50);
		algorithm.setInputParameter("maxEvaluations",15000);

		// Directory with the files containing the weight vectors used in 
		// Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
		// on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
		// of CS & EE, University of Essex, 02/2009.
		// http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
		algorithm.setInputParameter("dataDirectory",
				"/Users/antelverde/Softw/pruebas/data/MOEAD_parameters/Weight");

		algorithm.setInputParameter("finalSize", 300) ; // used by MOEAD_DRA

		algorithm.setInputParameter("T", 20) ;
		algorithm.setInputParameter("delta", 0.9) ;
		algorithm.setInputParameter("nr", 2) ;

		// Crossover operator 
		parameters = new HashMap() ;
		parameters.put("CR", 1.0) ;
		parameters.put("F", 0.5) ;
		crossover = new GAPDifferentialEvolutionCrossover(parameters);   
		// Mutation operator
		parameters = new HashMap() ;
		parameters.put("probability", 0.03) ;
		parameters.put("distributionIndex", 20.0) ;
		mutation = new GAPMutation(parameters, (GapProblem)problem);              

		algorithm.addOperator("crossover",crossover);
		algorithm.addOperator("mutation",mutation);

		// Execute the Algorithm
		long initTime = System.currentTimeMillis();
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;

		// Result messages 
//		logger_.info("Total execution time: "+estimatedTime + "ms");
//		logger_.info("Objectives values have been writen to file FUN");
//		population.printFeasibleFUN("FUN");
//		logger_.info("Variables values have been writen to file VAR");
//		population.printFeasibleVAR("VAR");      
		population.printFeasibleFUN("output/moead_a5");
		GapProblem GapProblem = (GapProblem)problem;
		
//		for(int i=0; i<population.size(); i++){
//			if (population.get(i).getOverallConstraintViolation() == 0){
//				GapProblem.showsolution(population.get(i));
//				break;
//			}
//		}
//		if (indicators != null) {
//			logger_.info("Quality indicators") ;
//			logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
//			logger_.info("EPSILON    : " + indicators.getEpsilon(population)) ;
//			logger_.info("GD         : " + indicators.getGD(population)) ;
//			logger_.info("IGD        : " + indicators.getIGD(population)) ;
//			logger_.info("Spread     : " + indicators.getSpread(population)) ;
//		} // if     
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
		System.out.println("problem:"+num+"  min_ojbect1:"+tempmax+"  time"+estimatedTime);	
	}
	public static void main(String [] args) throws JMException, SecurityException, IOException, ClassNotFoundException {
		Problem   problem   ;         // The problem to solve
		Algorithm algorithm ;         // The algorithm to use
		Operator  crossover ;         // Crossover operator
		Operator  mutation  ;         // Mutation operator

		QualityIndicator indicators ; // Object to get quality indicators

		HashMap  parameters ; // Operator parameters

		// Logger object and file to store log messages
		logger_      = Configuration.logger_ ;
		fileHandler_ = new FileHandler("MOEAD.log"); 
		logger_.addHandler(fileHandler_) ;

		indicators = null ;
		if (args.length == 1) {
			Object [] params = {"Real"};
			problem = (new ProblemFactory()).getProblem(args[0],params);
		} // if
		else if (args.length == 2) {
			Object [] params = {"Real"};
			problem = (new ProblemFactory()).getProblem(args[0],params);
			indicators = new QualityIndicator(problem, args[1]) ;
		} // if
		else { // Default problem
			problem = new Kursawe("Real", 3); 
		} // else


		MOEAD_main TestData = new MOEAD_main();
		String fileName = "./Data/gapa.txt";
		TestData.readFile(fileName);
		problem = new GapProblem("IntSolutionType",TestData.ListOfProblems.get(5));
		TestData.test(problem, 5);
		
		
//		MOEAD_main TestData1 = new MOEAD_main();
//		TestData1.readFile(fileName);
//		problem = new GapProblem("IntSolutionType",TestData1.ListOfProblems.get(1));
//		TestData1.test(problem, 1);
//		
//		MOEAD_main TestData2 = new MOEAD_main();
//		TestData2.readFile(fileName);
//		problem = new GapProblem("IntSolutionType",TestData2.ListOfProblems.get(2));
//		TestData2.test(problem, 2);
//		
		
//		for (int i=0; i<5; i++){
//			problem = new GapProblem("IntSolutionType",TestData.ListOfProblems.get(i));
//			TestData.test(problem, i);
//		}

	} //main
	
} // MOEAD_main
