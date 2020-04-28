import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SurveyAnalyzer {

	private ArrayList<Node> nodes;
	private int actualSize; // size of nodes disregarding those who haven't taken survey
	
	public SurveyAnalyzer(String excelFilename) {
		
		File myFile = new File(excelFilename);
		
		try {
			
			FileInputStream fis = new FileInputStream(myFile);
			XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIterator = mySheet.iterator();
			
			int capacity = mySheet.getPhysicalNumberOfRows() + 10;
			nodes = new ArrayList<Node>(capacity);
			
			rowIterator.next(); // skip the headings
			int rowNum = 1;
			final int colsToRead = 10;
			String[] varVals = new String[colsToRead];
			
			while (rowIterator.hasNext()) {
				
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int colCount = 0;
				
				while (cellIterator.hasNext() && colCount < colsToRead) {
					
					Cell cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					
					varVals[colCount] = cell.getStringCellValue().toLowerCase();
					
					colCount++;
				}
				
				String consent = varVals[2];
				String name = varVals[3];
				String age = varVals[4];
				String grade = varVals[5];
				String gender = varVals[6];
				String referrals = varVals[7];
				String influence = varVals[8];
				String frequency = varVals[9];
				
				int id = rowNum;
				
				Node newSurveyTaker = new Node(consent,age,grade,gender,referrals,influence,frequency, name, id);
				nodes.add(newSurveyTaker);
				
				rowNum++;
			}
			
			actualSize = nodes.size();
			myWorkBook.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		
		String output = "";
		
		for (int i = 0; i < nodes.size(); i++) {
			
			output += nodes.get(i) + "\n";
		}
		
		return output;
	}
	
	public void generateNodesFromReferrals() {
			
		for (int i = 0; i < actualSize; i++) {
			
			String[] nameTokens = nodes.get(i).getReferrals();
			
			for (int j = 0; j < nameTokens.length; j++) {
				
				String name = nameTokens[j];
				
				if (!haveSurveyTaker(name)) {
					
					Node newSurveyTaker = new Node(name, nodes.size()+1);
					nodes.add(newSurveyTaker);
				}
			}
		}
	}
	
	public void generateNodesFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			if (upToNodeWithID != nodes.size()) {
				
				writer.write("*Network VapeSocialNetworkOnlyRespondents");
			
			} else {
				
				writer.write("*Network VapeSocialNetwork");
			}
			
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				// ID numbers
				writer.write(nodes.get(i).getID()+" \""+nodes.get(i).getID()+"\" 0.5 0.5 ");
				
				// Gender Shape
				if (nodes.get(i).getGender().equals("male")) {
					
					writer.write("box ");
				
				} else if (nodes.get(i).getGender().equals("female")) {
					
					writer.write("diamond ");
				}
				
				writer.write("bc ");
				
				// Education Boundary Color
				String grade = nodes.get(i).getGrade().split("\\.")[0].replaceAll("[^0-9]", "");
				
				switch(grade) {
				case "8":
					writer.write("Gray15 ");
					break;
				case "9":
					writer.write("Gray25 ");
					break;
				case "10":
					writer.write("Gray35 ");
					break;
				case "11":
					writer.write("Gray45 ");
					break;
				case "12":
					writer.write("Gray55 ");
					break;
				case "13":
					writer.write("Gray65 ");
					break;
				case "14":
					writer.write("Gray75 ");
					break;
				case "15":
					writer.write("Gray85 ");
					break;
				case "16":
					writer.write("Gray90 ");
					break;
				case "17":
					writer.write("Gray95 ");
					break;
				default:
					writer.write("Black ");
				}
				
				writer.write("ic ");
				
				// Vape/Influence interior color
				boolean v = Boolean.parseBoolean(nodes.get(i).getFrequency());
				boolean inf = Boolean.parseBoolean(nodes.get(i).getInfluence());
				
				if (nodes.get(i).getFrequency().equals("2"))
					writer.write("White");
				else if (v&&inf)
					writer.write("Red");
				else if (!v&&inf)
					writer.write("Blue");
				else if (v&&!inf)
					writer.write("Melon");
				else if (!v&&!inf)
					writer.write("LightCyan");
				
				writer.write("\n");
			}
			
			writer.write("\n");
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// only can call when generateNodesFromReferrals() has been called
	public void generateConnectionsFile(String toFile, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			writer.write("*Arcs\n");
			
			for (int i = 0; i < actualSize; i++) {
				
				String[] nameTokens = nodes.get(i).getReferrals();
				
				for (int j = 0; j < nameTokens.length; j++) {
					
					Node connection = getNodeWithName(nameTokens[j]);
					writer.write((i+1) + " " + connection.getID() + " " + (j+1) + "\n");
				}
			}
			
			writer.write("\n");
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateVapeFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			writer.write("*Partition Vape\n");
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				if (nodes.get(i).getFrequency().equals("false")) {
					
					writer.write("0\n");
				
				} else if (nodes.get(i).getFrequency().equals("true")) {
					
					writer.write("1\n");
				
				} else {
					
					writer.write("2\n");
				}
			}
			
			writer.write("\n");
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateInfluenceFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			writer.write("*Partition Influence\n");
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				if (nodes.get(i).getInfluence().equals("false")) {
					
					writer.write("0\n");
				
				} else if (nodes.get(i).getInfluence().equals("true")) {
					
					writer.write("1\n");
				
				} else {
					
					writer.write("2\n");
				}
			}
			
			writer.write("\n");
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateEducationFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			writer.write("*Partition Education\n");
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				String age = nodes.get(i).getGrade();
				writer.write(age.replaceAll("[^0-9]", "")+"\n");
			}
			
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateAgeFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			writer.write("*Partition Age\n");
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				String age = nodes.get(i).getAge().split("\\.")[0];
				writer.write(age + "\n");
			}
			
			writer.write("\n");
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateGenderFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			writer.write("*Partition Gender\n");
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				if (nodes.get(i).getGender().equals("male")) {
					
					writer.write("1\n");
				
				} else if (nodes.get(i).getGender().equals("female")) {
					
					writer.write("0\n");
				
				} else {
					
					writer.write("2\n");
				}
			}
			
			writer.write("\n");
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Node> getNodes() {
		
		return nodes;
	}
	
	public int getActualSize() {
		
		return actualSize;
	}
	
	private boolean haveSurveyTaker(String name) {
		
		for (int i = 0; i < nodes.size(); i++) {
			
			if (nodes.get(i).getName().equals(name)) {
				
				return true;
			}
		}
		
		return false;
	}
	
	private Node getNodeWithName(String name) {
		
		for (int i = 0; i < nodes.size(); i++) {
			
			if (nodes.get(i).getName().equals(name)) {
				
				return nodes.get(i);
			}
		}
		
		return null;
	}
}
