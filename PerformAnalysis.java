package codeBundle;

public class PerformAnalysis {

	public static void main(String[] args) {
		
		long startTime = System.nanoTime();
		
		SurveyAnalyzer analyzer = new SurveyAnalyzer("/Users/NalinJha/Downloads/SocialNetworkSurveyResults.xlsx");
		
		analyzer.generateNodesFromReferrals(); // must do this before performing any analysis
		
		analyzer.generateConnectionsFile("arcs.txt");
		analyzer.generateVapeFile("vape.txt");
		analyzer.generateInfluenceFile("influence.txt");
		analyzer.generateEducationFile("education.txt");
		analyzer.generateAgeFile("age.txt");
		analyzer.generateGenderFile("gender.txt");
		analyzer.generateNodesFile("nodes.txt", analyzer.getNodes().size()); // all nodes
		analyzer.generateNodesFile("respondentsOnly.txt", analyzer.getActualSize()); // nodes of respondents only
		
		long endTime = System.nanoTime();
		double inSeconds = (endTime - startTime)/1000000000.0; // runtime in seconds
		
		System.out.println("Generated all files in " + inSeconds + " seconds.");
	}
}
