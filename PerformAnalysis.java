import java.io.IOException;

public class PerformAnalysis {

	public static void main(String[] args) throws IOException {
		
		long startTime = System.nanoTime();
		
		SurveyAnalyzer analyzer = new SurveyAnalyzer("/Users/NalinJha/Downloads/SocialNetworkSurveyResults.xlsx");
		
		analyzer.generateNodesFromReferrals(); // must do this before performing any analysis
		
		System.out.println();
		
		System.out.println("**Nodes with 5 or more referrals**");
				
		for (Node n : analyzer.mostReferred(5)) {
			
			System.out.println(n.getName() + " " + n.getTies());
		}
		
		final String onlyRespondentsFilename = "OnlyRespondents.txt";
		final String allDataFilename = "AllData.txt";
		final String analysisFilename = "Analysis.txt";
		final int allSize = analyzer.getNodes().size();
		final int respondentsSize = analyzer.getActualSize();
		
		// Generate Analysis File
		analyzer.generateAnalysis(analysisFilename);
		
		// Generate Analysis Spreadsheet
		analyzer.generateMainStats("MainStats");
		analyzer.generateTiesExcel("TieStats");
		analyzer.generateStrengthExcel("StrengthStats");
		
		// Generate file considering all nodes
		analyzer.generateNodesFile(allDataFilename, allSize, false);
		analyzer.generateConnectionsFile(allDataFilename, true);
		analyzer.generateVapeFile(allDataFilename, allSize, true);
		analyzer.generateGenderFile(allDataFilename, allSize, true);
		analyzer.generateAgeFile(allDataFilename, allSize, true);
		analyzer.generateInfluenceFile(allDataFilename, allSize, true);
		analyzer.generateEducationFile(allDataFilename, allSize, true);
		analyzer.generateStrengthFile(allDataFilename, allSize, true);
		analyzer.generateTiesFile(allDataFilename, allSize, true);
		
		// Generate file considering only respondents
		analyzer.generateNodesFile(onlyRespondentsFilename, respondentsSize, false); // nodes of respondents only
		//analyzer.generateConnectionsFile(onlyRespondentsFilename, true);
		analyzer.generateVapeFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateGenderFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateAgeFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateInfluenceFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateEducationFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateStrengthFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateTiesFile(onlyRespondentsFilename, respondentsSize, true);
				
		System.out.println();
		
		System.out.println("**Name -> Ties -> Total Strength -> Average Strength**");
		
		for (Node n : analyzer.getNodes()) {
			
			System.out.println(n.getName() + " -> " + n.getTies() + " -> " + n.getStrengthSum() + " -> " + n.calculateAverageStrength());
		}
		
		long endTime = System.nanoTime();
		double inSeconds = (endTime - startTime)/1000000000.0; // runtime in seconds
		
		System.out.println();
		
		System.out.println("Generated all files in " + inSeconds + " seconds.");
	}
}
