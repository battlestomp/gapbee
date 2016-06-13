package gap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InitData {
	public List<GapProblem> ListOfProblems = new ArrayList<GapProblem>();
	public int NumofProblem = 0;
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
