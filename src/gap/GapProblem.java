//  mQAP.java
//
//  Author:
//       Juan J. Durillo <juan@dps.uibk.ac.at>
//
//  Copyright (c) 2011 Juan J. Durillo
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.Int;
import jmetal.encodings.variable.Permutation;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 *  @author Juan J. Durillo
 *  @version 1.0
 *  This class implements the mQAP problem.
 *  Please notice that this class is also valid for the case m = 1 (mono-objective
 *  version of the problem)
 */
public class GapProblem extends Problem {

	/*与特定问题有关的属性*/
	private int NumofAgents = 0;
	private int NumofJobs = 0;
	private int ArrayCosts[][];
	private int ArrayComsuption[][];
	private int ArrayConstraint[];
	private int ArrayLessRequired[];
	private int ArrayLessRecources[];

	private double[] sbulambda;

	/*特定问题的初始参数设置函数*/
	public void SetNumofAgents(int num){
		NumofAgents = num;
	}
	public void SetNumofJobs(int num){
		NumofJobs = num;
	}
	public int GetNumofAgents(){
		return NumofAgents;
	}
	public int GetNumofJobs(){
		return NumofJobs;
	}
	public void initArray(){
		if (NumofAgents<=0||NumofJobs<=0)
			return;
		ArrayCosts = new int[NumofAgents][NumofJobs];
		ArrayComsuption = new int[NumofAgents][NumofJobs];
		ArrayConstraint = new int[NumofAgents];
		ArrayLessRequired = new int[NumofAgents];
		ArrayLessRecources = new int[NumofAgents]; 
	}
	public void SetArrayCost(int irow, int icol, int ivalue){
		ArrayCosts[irow][icol] = ivalue;
	}
	public void SetArrayComsuption(int irow, int icol, int ivalue){
		ArrayComsuption[irow][icol] = ivalue;
	}
	public void SetConstraint(int index, int ivalue){
		ArrayConstraint[index] = ivalue;
	}

	public void setlambda(double[] lambda){
		sbulambda = lambda;
	}

	public int GetArrayCost(int irow, int icol){
		return ArrayCosts[irow][icol];
	}
	public int GetArrayComsuption(int irow, int icol){
		return ArrayComsuption[irow][icol];
	}
	public int GetConstraint(int index){
		return ArrayConstraint[index];
	}

	public void CalculateLess(){
		Arrays.fill(ArrayLessRequired, 0); 
		Arrays.fill(ArrayLessRecources, 0); 
		for (int i=0; i<NumofAgents; i++)
			for(int j=0; j<NumofJobs; j++){
				if(ArrayCosts[ArrayLessRequired[i]][j] > ArrayCosts[i][j])
					ArrayLessRequired[i] = i;
				if (ArrayComsuption[ArrayLessRecources[i]][j] > ArrayComsuption[i][j])
					ArrayLessRecources[i] = i;
			}
	}

	public int GetLessRequired(int i){
		return ArrayLessRequired[i];
	}

	public int GetLessRecources(int i){
		return ArrayLessRecources[i];
	}
	/*打印问题的初始参数*/
	public void PrintData(){
		System.out.printf("%d,%d", NumofAgents, NumofJobs);
		System.out.println();
		for (int i=0; i<NumofAgents; i++){
			for(int j=0; j<NumofJobs; j++){
				System.out.printf("%d,", ArrayCosts[i][j]);
			}
			System.out.println();
		}
		for (int i=0; i<NumofAgents; i++){
			for(int j=0; j<NumofJobs; j++){
				System.out.printf("%d,", ArrayComsuption[i][j]);
			}
			System.out.println();
		}
		for (int i=0; i<NumofAgents; i++){
			System.out.printf("%d,", ArrayConstraint[i]);
		}
	}

	public GapProblem(){
	}
	public GapProblem(String solutionType, GapProblem problem) {
		NumofAgents = problem.GetNumofAgents();
		NumofJobs = problem.GetNumofJobs();
		initArray();
		for (int i=0; i<NumofAgents; i++){
			for (int j=0; j<NumofJobs; j++){
				ArrayCosts[i][j] = problem.GetArrayCost(i, j);
				ArrayComsuption[i][j] = problem.GetArrayComsuption(i, j);
			}
			ArrayConstraint[i] = problem.GetConstraint(i);
		}		
		//计算最小需求和最小资源消耗
		CalculateLess();
		numberOfVariables_  =   NumofJobs; // the permutation
		numberOfObjectives_ =  2;
		numberOfConstraints_=   1;
		problemName_        =   "GAP";

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		// Establishes upper and lower limits for the variables
		for (int var = 0; var < numberOfVariables_; var++)
		{
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = NumofAgents -1;
		}

		if (solutionType.compareTo("IntSolutionType") == 0)
			solutionType_ = new IntSolutionType(this);
		else
			try {
				throw new JMException("SolutionType must be Permutation") ;
			} catch (JMException e) {
				e.printStackTrace();
			}
	}
	public Boolean checksolution(Solution solution){
		int[] d = new int[numberOfVariables_];
		for(int i=0; i<d.length; i++){
			Int gen=(Int)solution.getDecisionVariables()[i];
			d[i] = (int)gen.getValue();
		}
		int[] AgentCosts = new int[NumofAgents];
		int[] AgentConsumption = new int[NumofAgents];
		Arrays.fill(AgentCosts, 0);  
		Arrays.fill(AgentConsumption, 0);  

		//计算成本和资源消耗
		for(int i=0; i<NumofJobs; i++){
			AgentCosts[d[i]] = AgentCosts[d[i]] + ArrayCosts[d[i]][i];
			AgentConsumption[d[i]] = AgentConsumption[d[i]] + ArrayComsuption[d[i]][i];
		}

		int[] ObjConstraint = Arrays.copyOf(AgentConsumption, AgentConsumption.length);
		//计算是否超出约束
		int Rind = 0;
		for (int j=0; j<NumofAgents; j++){
			if (ObjConstraint[j] < ArrayConstraint[j]){
				ObjConstraint[j] = 0;
			}else{
				ObjConstraint[j] = ObjConstraint[j] - ArrayConstraint[j];
				Rind = Rind + ObjConstraint[j];
			}
		}

		int obj0 = 0;
		int obj1 = 0;
		int mincomsumption = Integer.MAX_VALUE;
		int maxconsumption = Integer.MIN_VALUE;
		for (int j=0; j<NumofAgents; j++){
			obj0 = obj0 + AgentCosts[j];
			if (mincomsumption>AgentConsumption[j])
				mincomsumption = AgentConsumption[j];
			if (maxconsumption<AgentConsumption[j])
				maxconsumption = AgentConsumption[j];
		}	
		obj1 = maxconsumption - mincomsumption;
		System.out.print("solution:");
		for(int k=0; k<d.length; k++){
			System.out.printf("%2d, ", d[k]);
		}
		System.out.println();
		if (obj0 == (int)solution.getObjective(0) && obj1 == (int)solution.getObjective(1)){
			return true;
		}
		return false;
	}
	public void check(SolutionSet population){
		for (int i=0; i<population.size(); i++) {
			Solution solution = population.get(i);
			if (checksolution(solution) == false){
				System.out.printf("error");
			}
			if(solution.getOverallConstraintViolation() == 0.0){
				checksolution(solution);
			}
		}
	}
	public double GetProbability(List<Integer> ListAgents, int iagnet, int jtask){
		double probability = 0;
		if (ListAgents.size()==0)
			return probability;
		double numerator = 0;
		double denominator = 0;
		//numerator = (double)gap.GetArrayComsuption(iagnet, jtask)/gap.GetConstraint(iagnet);
		numerator = (double)GetConstraint(iagnet)/GetArrayComsuption(iagnet, jtask);
		for (int i=0; i<ListAgents.size(); i++)
			//denominator = denominator +  (double)gap.GetArrayComsuption(i, jtask)/gap.GetConstraint(i);
			denominator = denominator +  (double)GetConstraint(i)/GetArrayComsuption(i, jtask);
		probability = numerator/denominator;
		return probability;
	}
	public double GetPbyComsuption(List<Integer> ListAgents, GapProblem gap, int iagnet, int jtask){
		double probability = 0;
		if (ListAgents.size()==0)
			return probability;
		double numerator = 0;
		double denominator = 0;
		numerator = (double)gap.GetConstraint(iagnet)/gap.GetArrayComsuption(iagnet, jtask);
		for (int i=0; i<ListAgents.size(); i++)
			denominator = denominator +  (double)gap.GetConstraint(i)/gap.GetArrayComsuption(i, jtask);
		probability = numerator/denominator;
		return probability;
	}
	public int[] Grasp()   {
		List<Integer> serialization = new ArrayList<Integer>(GetNumofJobs());
		List<Integer> ListAgents = new ArrayList<Integer>(GetNumofAgents());
		int[] arrayresult = new int[GetNumofJobs()];
		int[] CurrentConstraint = new int[GetNumofAgents()];
		Arrays.fill(arrayresult, 0);
		for(int i=0; i<GetNumofJobs(); i++){
			serialization.add(i);
		}
		for(int i=0; i<GetNumofAgents(); i++){
			ListAgents.add(i);
		}
		Collections.shuffle(serialization);
		int itask = 0;
		double probability = 0;
		for (int i=0; i<10000; i++){
			if (itask==serialization.size())
				break;
			else{
				itask = 0;
				Arrays.fill(arrayresult, 0);
				ListAgents.clear();
				Arrays.fill(CurrentConstraint, 0);
				for(int j=0; j<GetNumofAgents(); j++){
					ListAgents.add(j);
				}
			}
			while(itask<serialization.size()){
				if (ListAgents.size()==0)
					break;
				int ranint = PseudoRandom.randInt(0, ListAgents.size()-1);
				int iagent = ListAgents.get(ranint);
				probability = GetPbyComsuption(ListAgents, this, iagent, serialization.get(itask));
				if (PseudoRandom.randDouble()<probability){
					int ctemp = CurrentConstraint[iagent] + GetArrayComsuption(iagent, serialization.get(itask));
					if (ctemp > GetConstraint(iagent)){
						ListAgents.remove(ListAgents.indexOf(iagent));
					}else{
						CurrentConstraint[iagent] = ctemp;
						arrayresult[serialization.get(itask)] = iagent;
						itask = itask+1;
					}
				}
			}	
		}
		return arrayresult;
	}
	public void Newsolution(Solution solution) throws JMException {
		int[] d = Grasp();
		for(int i=0; i<d.length; i++){
			solution.getDecisionVariables()[i].setValue(d[i]);
		}

		//Heuristic strategy = new Heuristic();
		//strategy.resultrepair(d, this);

		int[] AgentCosts = new int[NumofAgents];
		int[] AgentConsumption = new int[NumofAgents];
		Arrays.fill(AgentCosts, 0);  
		Arrays.fill(AgentConsumption, 0);  

		//计算成本和资源消耗
		for(int i=0; i<NumofJobs; i++){
			AgentCosts[d[i]] = AgentCosts[d[i]] + ArrayCosts[d[i]][i];
			AgentConsumption[d[i]] = AgentConsumption[d[i]] + ArrayComsuption[d[i]][i];
		}

		int[] ObjConstraint = Arrays.copyOf(AgentConsumption, AgentConsumption.length);
		//计算是否超出约束
		int Rind = 0;
		for (int j=0; j<NumofAgents; j++){
			if (ObjConstraint[j] < ArrayConstraint[j]){
				ObjConstraint[j] = 0;
			}else{
				ObjConstraint[j] = ObjConstraint[j] - ArrayConstraint[j];
				Rind = Rind + ObjConstraint[j];
			}
		}

		int obj0 = 0;
		int obj1 = 0;
		int mincomsumption = Integer.MAX_VALUE;
		int maxconsumption = Integer.MIN_VALUE;
		for (int j=0; j<NumofAgents; j++){
			obj0 = obj0 + AgentCosts[j];
			if (mincomsumption>AgentConsumption[j])
				mincomsumption = AgentConsumption[j];
			if (maxconsumption<AgentConsumption[j])
				maxconsumption = AgentConsumption[j];
		}	
		obj1 = maxconsumption - mincomsumption;
		solution.setObjective(0, obj0);
		solution.setObjective(1, obj1);	

		solution.setOverallConstraintViolation(Rind);


	}

	// evaluation of the problem
	public void evaluate(Solution solution) throws JMException {
		int[] d = new int[numberOfVariables_];
		for(int i=0; i<d.length; i++){
			Int gen=(Int)solution.getDecisionVariables()[i];
			d[i] = (int)gen.getValue();
		}
		int[] AgentCosts = new int[NumofAgents];
		int[] AgentConsumption = new int[NumofAgents];
		Arrays.fill(AgentCosts, 0);  
		Arrays.fill(AgentConsumption, 0);  

		//计算成本和资源消耗
		for(int i=0; i<NumofJobs; i++){
			AgentCosts[d[i]] = AgentCosts[d[i]] + ArrayCosts[d[i]][i];
			AgentConsumption[d[i]] = AgentConsumption[d[i]] + ArrayComsuption[d[i]][i];
		}

		int[] ObjConstraint = Arrays.copyOf(AgentConsumption, AgentConsumption.length);
		//计算是否超出约束;
		int Rind = 0;
		for (int j=0; j<NumofAgents; j++){
			if (ObjConstraint[j] < ArrayConstraint[j]){
				ObjConstraint[j] = 0;
			}else{
				ObjConstraint[j] = ObjConstraint[j] - ArrayConstraint[j];
				Rind = Rind + ObjConstraint[j];
			}
		}
		int obj0 = 0;
		int obj1 = 0;
		int  obj2 = 0;
		int mincomsumption = Integer.MAX_VALUE;
		int maxconsumption = Integer.MIN_VALUE;
		int avgconsumption = 0;
		for (int j=0; j<NumofAgents; j++){
			obj0 = obj0 + AgentCosts[j];
			avgconsumption = avgconsumption + AgentConsumption[j];
			if (mincomsumption>AgentConsumption[j])
				mincomsumption = AgentConsumption[j];
			if (maxconsumption<AgentConsumption[j])
				maxconsumption = AgentConsumption[j];
		}	
		avgconsumption = avgconsumption/NumofAgents;
		for (int j=0; j<NumofAgents; j++){
			obj2 = obj2 + Math.abs(AgentConsumption[j] - avgconsumption);
		}
		obj1 = maxconsumption - mincomsumption;
		solution.setObjective(0, obj0);
		solution.setObjective(1, obj2);	
		solution.setOverallConstraintViolation(Rind);
	}
	public double feasible(int[] result){
		int[] d = result;
		int[] AgentCosts = new int[NumofAgents];
		int[] AgentConsumption = new int[NumofAgents];
		Arrays.fill(AgentCosts, 0);  
		Arrays.fill(AgentConsumption, 0);  

		//计算成本和资源消耗
		for(int i=0; i<NumofJobs; i++){
			AgentCosts[d[i]] = AgentCosts[d[i]] + ArrayCosts[d[i]][i];
			AgentConsumption[d[i]] = AgentConsumption[d[i]] + ArrayComsuption[d[i]][i];
		}

		int[] ObjConstraint = Arrays.copyOf(AgentConsumption, AgentConsumption.length);
		//计算是否超出约束
		int Rind = 0;
		for (int j=0; j<NumofAgents; j++){
			if (ObjConstraint[j] < ArrayConstraint[j]){
				ObjConstraint[j] = 0;
			}else{
				ObjConstraint[j] = ObjConstraint[j] - ArrayConstraint[j];
				Rind = Rind + ObjConstraint[j];
			}
		}

		int obj0 = 0;
		int obj1 = 0;
		int  obj2 = 0;
		int mincomsumption = Integer.MAX_VALUE;
		int maxconsumption = Integer.MIN_VALUE;
		int avgconsumption = 0;
		for (int j=0; j<NumofAgents; j++){
			obj0 = obj0 + AgentCosts[j];
			avgconsumption = avgconsumption + AgentConsumption[j];
			if (mincomsumption>AgentConsumption[j])
				mincomsumption = AgentConsumption[j];
			if (maxconsumption<AgentConsumption[j])
				maxconsumption = AgentConsumption[j];
		}	
		obj1 = maxconsumption - mincomsumption;

		avgconsumption = avgconsumption/NumofAgents;
		
		for (int j=0; j<NumofAgents; j++){
			obj2 = obj2 + Math.abs(AgentConsumption[j] - avgconsumption);
		}
		
		if (Rind>0)
			return -1;
		return obj2;
	}
	public double relaxfitness(int[] result){
		int[] d = result;
		int[] AgentCosts = new int[NumofAgents];
		int[] AgentConsumption = new int[NumofAgents];
		Arrays.fill(AgentCosts, 0);  
		Arrays.fill(AgentConsumption, 0);  

		//计算成本和资源消耗
		for(int i=0; i<NumofJobs; i++){
			AgentCosts[d[i]] = AgentCosts[d[i]] + ArrayCosts[d[i]][i];
			AgentConsumption[d[i]] = AgentConsumption[d[i]] + ArrayComsuption[d[i]][i];
		}

		int[] ObjConstraint = Arrays.copyOf(AgentConsumption, AgentConsumption.length);
		//计算是否超出约束
		int Rind = 0;
		for (int j=0; j<NumofAgents; j++){
			if (ObjConstraint[j] < ArrayConstraint[j]){
				ObjConstraint[j] = 0;
			}else{
				ObjConstraint[j] = ObjConstraint[j] - ArrayConstraint[j];
				Rind = Rind + ObjConstraint[j];
			}
		}

		int obj0 = 0;
		int obj1 = 0;
		int  obj2 = 0;
		int mincomsumption = Integer.MAX_VALUE;
		int maxconsumption = Integer.MIN_VALUE;
		int avgconsumption = 0;
		for (int j=0; j<NumofAgents; j++){
			obj0 = obj0 + AgentCosts[j];
			avgconsumption = avgconsumption + AgentConsumption[j];
			if (mincomsumption>AgentConsumption[j])
				mincomsumption = AgentConsumption[j];
			if (maxconsumption<AgentConsumption[j])
				maxconsumption = AgentConsumption[j];
		}	
		obj1 = maxconsumption - mincomsumption;

		avgconsumption = avgconsumption/NumofAgents;
		
		for (int j=0; j<NumofAgents; j++){
			obj2 = obj2 + Math.abs(AgentConsumption[j] - avgconsumption);
		}
		//return obj0;
		//return obj2 + 50*Rind;
		if (sbulambda==null)
			return obj0 + 50*Rind;
		return obj0*sbulambda[0] + obj1*sbulambda[1] + 50*Rind;
	}

	public double fitness2(int[] result, double[] lambda){
		int[] d = result;
		int[] AgentCosts = new int[NumofAgents];
		int[] AgentConsumption = new int[NumofAgents];
		Arrays.fill(AgentCosts, 0);  
		Arrays.fill(AgentConsumption, 0);  

		//计算成本和资源消耗
		for(int i=0; i<NumofJobs; i++){
			AgentCosts[d[i]] = AgentCosts[d[i]] + ArrayCosts[d[i]][i];
			AgentConsumption[d[i]] = AgentConsumption[d[i]] + ArrayComsuption[d[i]][i];
		}

		int[] ObjConstraint = Arrays.copyOf(AgentConsumption, AgentConsumption.length);
		//计算是否超出约束
		int Rind = 0;
		for (int j=0; j<NumofAgents; j++){
			if (ObjConstraint[j] < ArrayConstraint[j]){
				ObjConstraint[j] = 0;
			}else{
				ObjConstraint[j] = ObjConstraint[j] - ArrayConstraint[j];
				Rind = Rind + ObjConstraint[j];
			}
		}

		int obj0 = 0;
		int obj1 = 0;
		int mincomsumption = Integer.MAX_VALUE;
		int maxconsumption = Integer.MIN_VALUE;
		for (int j=0; j<NumofAgents; j++){
			obj0 = obj0 + AgentCosts[j];
			if (mincomsumption>AgentConsumption[j])
				mincomsumption = AgentConsumption[j];
			if (maxconsumption<AgentConsumption[j])
				maxconsumption = AgentConsumption[j];
		}	
		obj1 = maxconsumption - mincomsumption;
		if (Rind>0)
			return -1;
		return obj0*lambda[0] + obj1*lambda[1];
	}

	public void showsolution(Solution solution){
		//checksolution(solution);
		//checkgreedy(solution);
		System.out.print("\nsolution: ");
		int[] d = new int[numberOfVariables_];
		for(int i=0; i<d.length; i++){
			Int gen=(Int)solution.getDecisionVariables()[i];
			d[i] = (int)gen.getValue();
		}
		for(int k=0; k<NumofJobs; k++){
			System.out.printf("%02d, ", d[k]);
		}
		System.out.println();
		int totalcost = 0;
		for (int i=0; i<NumofAgents; i++){
			int cost = 0;
			for(int j=0; j<NumofJobs; j++){
				if (d[j] == i){
					System.out.printf("%02d, ", ArrayCosts[i][j]);
					cost = cost + ArrayCosts[i][j];
				}
				else 
					System.out.printf("%02d, ", 0);
			}
			System.out.printf("%02d, \n", cost);
			totalcost = totalcost + cost;
		}
		System.out.println("---------------------------------------------totalcost=" + totalcost);
		int totalconsume = 0;
		for (int i=0; i<NumofAgents; i++){
			int consume = 0;
			for(int j=0; j<NumofJobs; j++){
				if (d[j] == i){
					System.out.printf("%02d, ", ArrayComsuption[i][j]);
					consume = consume + ArrayComsuption[i][j];
				}
				else 
					System.out.printf("%02d, ", 0);
			}
			System.out.printf("%2d, ", consume);
			System.out.printf("%2d, \n", ArrayConstraint[i]);
			totalconsume = totalconsume + consume;
		}
		System.out.println("----------------------------------------totalconsume=" + totalconsume);
	}

	
	public void showresult(int[] result){
		System.out.print("\nsolution: ");
		int[] d = result;
		for(int k=0; k<NumofJobs; k++){
			System.out.printf("%02d, ", d[k]);
		}
		System.out.println();
		int totalcost = 0;
		for (int i=0; i<NumofAgents; i++){
			int cost = 0;
			for(int j=0; j<NumofJobs; j++){
				if (d[j] == i){
					System.out.printf("%02d, ", ArrayCosts[i][j]);
					cost = cost + ArrayCosts[i][j];
				}
				else 
					System.out.printf("%02d, ", 0);
			}
			System.out.printf("%02d, \n", cost);
			totalcost = totalcost + cost;
		}
		System.out.println("---------------------------------------------totalcost=" + totalcost);
		int totalconsume = 0;
		for (int i=0; i<NumofAgents; i++){
			int consume = 0;
			for(int j=0; j<NumofJobs; j++){
				if (d[j] == i){
					System.out.printf("%02d, ", ArrayComsuption[i][j]);
					consume = consume + ArrayComsuption[i][j];
				}
				else 
					System.out.printf("%02d, ", 0);
			}
			System.out.printf("%2d, ", consume);
			System.out.printf("%2d, \n", ArrayConstraint[i]);
			totalconsume = totalconsume + consume;
		}
		System.out.println("----------------------------------------totalconsume=" + totalconsume);
	}
	
	
	public void showsample(int[] result){
		System.out.print("\nsolution: "+feasible(result) );
	}
} 
