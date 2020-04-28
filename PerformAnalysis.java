
public class PerformAnalysis {

	public static void main(String[] args) {
		
		long startTime = System.nanoTime();
		
		SurveyAnalyzer analyzer = new SurveyAnalyzer("");
		
		analyzer.generateNodesFromReferrals(); // must do this before performing any analysis
		
		final String onlyRespondentsFilename = "OnlyRespondents.txt";
		final String allDataFilename = "AllData.txt";
		final int allSize = analyzer.getNodes().size();
		final int respondentsSize = analyzer.getActualSize();
		
		// Generate file considering all nodes
		analyzer.generateNodesFile(allDataFilename, allSize, false);
		analyzer.generateConnectionsFile(allDataFilename, true);
		analyzer.generateVapeFile(allDataFilename, allSize, true);
		analyzer.generateGenderFile(allDataFilename, allSize, true);
		analyzer.generateAgeFile(allDataFilename, allSize, true);
		analyzer.generateInfluenceFile(allDataFilename, allSize, true);
		analyzer.generateEducationFile(allDataFilename, allSize, true);
		
		// Generate file considering only respondents
		analyzer.generateNodesFile(onlyRespondentsFilename, respondentsSize, false); // nodes of respondents only
		//analyzer.generateConnectionsFile(onlyRespondentsFilename, true);
		analyzer.generateVapeFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateGenderFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateAgeFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateInfluenceFile(onlyRespondentsFilename, respondentsSize, true);
		analyzer.generateEducationFile(onlyRespondentsFilename, respondentsSize, true);
		
		long endTime = System.nanoTime();
		double inSeconds = (endTime - startTime)/1000000000.0; // runtime in seconds
		
		System.out.println("Generated all files in " + inSeconds + " seconds.");
	}
}