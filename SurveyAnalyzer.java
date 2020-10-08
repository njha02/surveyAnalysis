import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
			
			int capacity = mySheet.getPhysicalNumberOfRows() + 50;
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
					
					varVals[colCount] = cell.getStringCellValue().toLowerCase().trim();
					
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
				
				if (!newSurveyTaker.getName().equals("n/a"))
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
	
	// reference GeeksForGeeks
	public ArrayList<Node> mostReferred(int valOrMore) {

		ArrayList<Node> popularNodes = new ArrayList<Node>();
		Map<String, Integer> map = new HashMap<String, Integer>();

		for (int i = 0; i < actualSize; i++) {
			
			String[] nameTokens = nodes.get(i).getReferrals();
			
			for (int j = 0; j < nameTokens.length; j++) {
				
				String name = nameTokens[j];
				
				if (map.containsKey(name)) {
					
					int freq = map.get(name);
					freq++;
					map.put(name, freq);
				
				} else {
					
					map.put(name, 1);
				}
			}
		}

		//int max_count = 0;
		
		for (Entry<String, Integer> val : map.entrySet()) {
			
			if (val.getValue() >= valOrMore) {
				
				popularNodes.add(getNodeWithName(val.getKey()));
				//max_count = val.getValue();
			}
		}
		
		return popularNodes;
	}
	
	public void generateNodesFromReferrals() {
		
		System.out.println("**The following referrals need to take the survey**");
		
		for (int i = 0; i < actualSize; i++) {
			
			String[] nameTokens = nodes.get(i).getReferrals();
			
			for (int j = 0; j < nameTokens.length; j++) {
				
				String name = nameTokens[j];
				Node referralNode = getNodeWithName(name);
				
				if (referralNode != null) {

					referralNode.addTie();
					referralNode.addStrength(3-j);
				}
				
				if (!haveSurveyTaker(name)) {
					
					System.out.println(name);
					Node newSurveyTaker = new Node(name, nodes.size()+1, 3-j);
					nodes.add(newSurveyTaker);
				}
			}
		}		
	}
	
	public void generateNodesFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			if (upToNodeWithID != nodes.size()) {
				
				writer.write("*Network VapeSocialNetworkOnlyRespondents\n");
			
			} else {
				
				writer.write("*Network VapeSocialNetwork\n");
			}
			
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				// ID numbers
				//writer.write(nodes.get(i).getID()+" \""+nodes.get(i).getID()+"\" 0.5 0.5 ");
				writer.write(nodes.get(i).getID()+" \""+nodes.get(i).getName()+"\" 0.5 0.5 ");
				
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
	
	// only can call when generateNodesFromReferrals() has been called
	public void generateArcMatrixFile(String toFile, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			int[][] mat = new int[nodes.size()][nodes.size()];
			
			writer.write("*Matrix with dimensions: " + nodes.size() + "x" + nodes.size() + "\n");
			
			for (int i = 0; i < actualSize; i++) {
				
				String[] nameTokens = nodes.get(i).getReferrals();
				
				for (int j = 0; j < nameTokens.length; j++) {
					
					Node connection = getNodeWithName(nameTokens[j]);
					mat[i][connection.getID()-1] = 1;
				}
			}
			
			for (int[] a : mat) {
				
				for (int n : a) {
					
					//writer.write(n + " ");
				}
				
				//writer.write("\n");
			}
			
			writer.write("\n");
			writer.write("\n");
			writer.write("\n");
			
			writer.write("[");
			
			for (int[] a : mat) {
				
				for (int n : a) {
					
					writer.write(n + " ");
				}
				
				writer.write("; ");
			}
			
			writer.write("]");
			
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
				
				} else {
					
					writer.write("1\n");
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
			
			writer.write("\n");
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
	
	public void generateTiesFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			writer.write("*Partition NumberOfTies\n");
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			double averageTie = getTotalTiesWithGender("male")[1];
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				if (nodes.get(i).getTies() > averageTie) {
					
					writer.write("1\n");
				
				} else if (nodes.get(i).getTies() < averageTie) {
					
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
	
	public void generateStrengthFile(String toFile, int upToNodeWithID, boolean append) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile, append);
			
			writer.write("*Partition AvgTieStrength\n");
			writer.write("*vertices " + upToNodeWithID + "\n");
			
			double averageStrength = getTotalStrengthWithGender("male")[1];
			
			for (int i = 0; i < upToNodeWithID; i++) {
				
				System.out.println("YIIIRGE " + nodes.get(i).calculateAverageStrength() + "    " + averageStrength);
				
				if (nodes.get(i).calculateAverageStrength() > averageStrength) {
					
					writer.write("1\n");
				
				} else if (nodes.get(i).calculateAverageStrength() < averageStrength) {
					
					writer.write("0\n");
				
				} else {
					
					writer.write("2\n");
					//System.out.println("EQUALLLL " + nodes.get(i).calculateAverageStrength() + "    " + averageStrength);
				}
			}
			
			writer.write("\n");
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Writing data to an Excel file
	 * using the Apache POI library.
	 *
	 * Reference www.codejava.net
	 */
	public void generateMainStats(String sheetName) throws IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("MainAnalysis");
        
        // vape stats
        int vM = getNodesWithGender("true", "male")[0];
        int vF = getNodesWithGender("true", "female")[0];
        int vHI = getNodesWithInfluence("true", "true")[0];
        int vNHI = getNodesWithInfluence("true", "false")[0];
        int v9 = getNodesWithGrade("true", "9th grade")[0];
        int v10 = getNodesWithGrade("true", "10th grade")[0];
        int v11 = getNodesWithGrade("true", "11th grade")[0];
        int v12 = getNodesWithGrade("true", "12th grade")[0];
        int v17 = getNodesWithAge("true", "Age 17 and under")[0];
        int v18 = getNodesWithAge("true", "Age 18+")[0];
        
        // don't vape stats
        int dvM = getNodesWithGender("false", "male")[0];
        int dvF = getNodesWithGender("false", "female")[0];
        int dvHI = getNodesWithInfluence("false", "true")[0];
        int dvNHI = getNodesWithInfluence("false", "false")[0];
        int dv9 = getNodesWithGrade("false", "9th grade")[0];
        int dv10 = getNodesWithGrade("false", "10th grade")[0];
        int dv11 = getNodesWithGrade("false", "11th grade")[0];
        int dv12 = getNodesWithGrade("false", "12th grade")[0];
        int dv17 = getNodesWithAge("false", "Age 17 and under")[0];
        int dv18 = getNodesWithAge("false", "Age 18+")[0];
        
        // total stats
        int tM = getNodesWithGender("true", "male")[1];
        int tF = getNodesWithGender("true", "female")[1];
        int tHI = getNodesWithInfluence("true", "true")[1];
        int tNHI = getNodesWithInfluence("true", "false")[1];
        int t9 = getNodesWithGrade("true", "9th grade")[1];
        int t10 = getNodesWithGrade("true", "10th grade")[1];
        int t11 = getNodesWithGrade("true", "11th grade")[1];
        int t12 = getNodesWithGrade("true", "12th grade")[1];
        int t17 = getNodesWithAge("true", "Age 17 and under")[1];
        int t18 = getNodesWithAge("true", "Age 18+")[1];
        
        // percentage stats -- Vape
        double pvM = (1.0*vM / actualSize) * 100.0;
        double pvF = (1.0*vF / actualSize) * 100.0;
        double pvHI = (1.0*vHI / actualSize) * 100.0;
        double pvNHI = (1.0*vNHI / actualSize) * 100.0;
        double pv9 = (1.0*v9 / actualSize) * 100.0;
        double pv10 = (1.0*v10 / actualSize) * 100.0;
        double pv11 = (1.0*v11 / actualSize) * 100.0;
        double pv12 = (1.0*v12 / actualSize) * 100.0;
        double pv17 = (1.0*v17 / actualSize) * 100.0;
        double pv18 = (1.0*v18 / actualSize) * 100.0;
        
        // percentage stats -- Don't Vape
        double pdvM = (1.0*dvM / actualSize) * 100.0;
        double pdvF = (1.0*dvF / actualSize) * 100.0;
        double pdvHI = (1.0*dvHI / actualSize) * 100.0;
        double pdvNHI = (1.0*dvNHI / actualSize) * 100.0;
        double pdv9 = (1.0*dv9 / actualSize) * 100.0;
        double pdv10 = (1.0*dv10 / actualSize) * 100.0;
        double pdv11 = (1.0*dv11 / actualSize) * 100.0;
        double pdv12 = (1.0*dv12 / actualSize) * 100.0;
        double pdv17 = (1.0*dv17 / actualSize) * 100.0;
        double pdv18 = (1.0*dv18 / actualSize) * 100.0;
        
        Object[][] tableData = {
           
           {"", "Male", "Female", "Heavily Influenced", "Not Heavily Influenced", "9th grade", "10th grade", "11th grade", "12th grade", "Age 17 and under", "Age 18+"},
           {"Vape", vM, vF, vHI, vNHI, v9, v10, v11, v12, v17, v18},
           {"", pvM, pvF, pvHI, pvNHI, pv9, pv10, pv11, pv12, pv17, pv18},
           {"Don't Vape", dvM, dvF, dvHI, dvNHI, dv9, dv10, dv11, dv12, dv17, dv18},
           {"", pdvM, pdvF, pdvHI, pdvNHI, pdv9, pdv10, pdv11, pdv12, pdv17, pdv18},
           {"Total", tM, tF, tHI, tNHI, t9, t10, t11, t12, t17, t18},
        };
 
        int rowCount = 0;
         
        for (Object[] rowData : tableData) {
            
        	Row row = sheet.createRow(++rowCount);
             
            int columnCount = 0;
             
            for (Object field : rowData) {
                
            	Cell cell = row.createCell(++columnCount);
                
            	if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                	
                	double val = round((Double)(field), 2);
                    cell.setCellValue(val);
                }
            }
        }
        
        try (FileOutputStream outputStream = new FileOutputStream(sheetName + ".xlsx")) {
            workbook.write(outputStream);
        }
	}
	
	/**
	 * Writing data to an Excel file
	 * using the Apache POI library.
	 *
	 * Reference www.codejava.net
	 */
	public void generateTiesExcel(String sheetName) throws IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("TiesStrengthAnalysis");
        
        double[] maleAndVape = getTiesWithGenderAndVape("true", "male");
        double[] femaleAndVape = getTiesWithGenderAndVape("true", "female");
        
        double[] maleAndNoVape = getTiesWithGenderAndVape("false", "male");
        double[] femaleAndNoVape = getTiesWithGenderAndVape("false", "female");
        
        double[] maleTotals = getTotalTiesWithGender("male");
        double[] femaleTotals = getTotalTiesWithGender("female");
        
        double vM = maleAndVape[0];
        double vF = femaleAndVape[0];
        double vT = femaleAndVape[1];
        
        double dvM = maleAndNoVape[0];
        double dvF = femaleAndNoVape[0];
        double dvT = femaleAndNoVape[1];
        
        double tM = maleTotals[0];
        double tF = femaleTotals[0];
        double tT = femaleTotals[1];
        
        Object[][] tableData = {
           
           {"", "Male", "Female", "Total"},
           {"Vape", vM, vF, vT},
           {"Don't Vape", dvM, dvF, dvT},
           {"Total", tM, tF, tT},
        };
 
        int rowCount = 0;
         
        for (Object[] rowData : tableData) {
            
        	Row row = sheet.createRow(++rowCount);
             
            int columnCount = 0;
             
            for (Object field : rowData) {
                
            	Cell cell = row.createCell(++columnCount);
                
            	if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                	
                	double val = round((Double)(field), 2);
                    cell.setCellValue(val);
                }
            }
        }
        
        try (FileOutputStream outputStream = new FileOutputStream(sheetName + ".xlsx")) {
            workbook.write(outputStream);
        }
	}
	
	/**
	 * Writing data to an Excel file
	 * using the Apache POI library.
	 *
	 * Reference www.codejava.net
	 */
	public void generateStrengthExcel(String sheetName) throws IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("TiesStrengthAnalysis");
        
        double[] maleAndVape = getStrengthWithGenderAndVape("true", "male");
        double[] femaleAndVape = getStrengthWithGenderAndVape("true", "female");
        
        double[] maleAndNoVape = getStrengthWithGenderAndVape("false", "male");
        double[] femaleAndNoVape = getStrengthWithGenderAndVape("false", "female");
        
        double[] maleTotals = getTotalStrengthWithGender("male");
        double[] femaleTotals = getTotalStrengthWithGender("female");
        
        double vM = maleAndVape[0];
        double vF = femaleAndVape[0];
        double vT = femaleAndVape[1];
        
        double dvM = maleAndNoVape[0];
        double dvF = femaleAndNoVape[0];
        double dvT = femaleAndNoVape[1];
        
        double tM = maleTotals[0];
        double tF = femaleTotals[0];
        double tT = femaleTotals[1];
        
        Object[][] tableData = {
           
           {"", "Male", "Female", "Total"},
           {"Vape", vM, vF, vT},
           {"Don't Vape", dvM, dvF, dvT},
           {"Total", tM, tF, tT},
        };
 
        int rowCount = 0;
         
        for (Object[] rowData : tableData) {
            
        	Row row = sheet.createRow(++rowCount);
             
            int columnCount = 0;
             
            for (Object field : rowData) {
                
            	Cell cell = row.createCell(++columnCount);
                
            	if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                	
                	double val = round((Double)(field), 2);
                    cell.setCellValue(val);
                }
            }
        }
        
        try (FileOutputStream outputStream = new FileOutputStream(sheetName + ".xlsx")) {
            workbook.write(outputStream);
        }
	}

	private double[] getTiesWithGenderAndVape(String vape, String gender) {

		int countForGender = 0; // for the average calculation
		int totalTiesForGender = 0;
		
		int countForTotal = 0; // for the average calculation
		int sumTiesForTotal = 0;

		for (int i = 0; i < actualSize; i++) {

			Node n = nodes.get(i);
			
			if (n.getFrequency().equals(vape)) {
				
				countForTotal++;
				sumTiesForTotal += n.getTies();
				
				if (n.getGender().equals(gender)) {
					
					countForGender++;
					totalTiesForGender += n.getTies();
				}
			}
		}
		
		double genderAverage = round((1.0*totalTiesForGender) / countForGender, 2);
		double totalAverage = round((1.0*sumTiesForTotal) / countForTotal, 2); // for first two rows in table 3
		
		double[] result = { genderAverage, totalAverage };
		return result;
	}
	
	private double[] getTotalTiesWithGender(String gender) {

		int countForGender = 0; // for the average calculation
		int totalTiesForGender = 0;
		
		int sumTiesForTotal = 0;

		for (int i = 0; i < actualSize; i++) {

			Node n = nodes.get(i);
			sumTiesForTotal += n.getTies();
			
			if (n.getGender().equals(gender)) {
				
				countForGender++;
				totalTiesForGender += n.getTies();
			}
		}
		
		double genderAverage = round((1.0*totalTiesForGender) / countForGender, 2);
		double totalAverage = round((1.0*sumTiesForTotal) / actualSize, 2);
		
		double[] result = { genderAverage, totalAverage };
		return result;
	}
	
	private double[] getStrengthWithGenderAndVape(String vape, String gender) {

		int countForGender = 0; // for the average calculation
		int totalStrengthForGender = 0;
		
		int countForTotal = 0; // for the average calculation
		int sumStrengthForTotal = 0;

		for (int i = 0; i < actualSize; i++) {

			Node n = nodes.get(i);
			
			if (n.getFrequency().equals(vape)) {
				
				countForTotal++;
				sumStrengthForTotal += n.calculateAverageStrength();
				
				if (n.getGender().equals(gender)) {
					
					countForGender++;
					totalStrengthForGender += n.calculateAverageStrength();
				}
			}
		}
		
		double genderAverage = round((1.0*totalStrengthForGender) / countForGender, 2);
		double totalAverage = round((1.0*sumStrengthForTotal) / countForTotal, 2); // for first two rows in table 3
		
		double[] result = { genderAverage, totalAverage };
		return result;
	}
	
	private double[] getTotalStrengthWithGender(String gender) {

		int countForGender = 0; // for the average calculation
		int totalStrengthForGender = 0;
		
		int sumStrengthForTotal = 0;

		for (int i = 0; i < actualSize; i++) {

			Node n = nodes.get(i);
			sumStrengthForTotal += n.calculateAverageStrength();
			
			if (n.getGender().equals(gender)) {
				
				countForGender++;
				totalStrengthForGender += n.calculateAverageStrength();
			}
		}
		
		double genderAverage = round((1.0*totalStrengthForGender) / countForGender, 2);
		double totalAverage = round((1.0*sumStrengthForTotal) / actualSize, 2);
		
		double[] result = { genderAverage, totalAverage };
		return result;
	}
	
	public void generateAnalysis(String toFile) {
		
		try {
			
			FileWriter writer = new FileWriter(toFile);
			
			writer.write("**VAPE &**\n");
			writer.write("Male: " + getNodesWithGender("true", "male")[0] + "\n");
			writer.write("Female: " + getNodesWithGender("true", "female")[0] + "\n");
			writer.write("Heavily Influenced: " + getNodesWithInfluence("true", "true")[0] + "\n");
			writer.write("NOT Heavily Influenced: " + getNodesWithInfluence("true", "false")[0] + "\n");
			writer.write("9th Grade: " + getNodesWithGrade("true", "9th grade")[0] + "\n");
			writer.write("10th Grade: " + getNodesWithGrade("true", "10th grade")[0] + "\n");
			writer.write("11th Grade: " + getNodesWithGrade("true", "11th grade")[0] + "\n");
			writer.write("12th Grade: " + getNodesWithGrade("true", "12th grade")[0] + "\n");
			writer.write("Age 17 and under: " + getNodesWithAge("true", "Age 17 and under")[0] + "\n");
			writer.write("Age 18+: " + getNodesWithAge("true", "Age 18+")[0] + "\n");
			
			writer.write("\n");
			
			writer.write("**DO NOT VAPE &**\n");
			writer.write("Male: " + getNodesWithGender("false", "male")[0] + "\n");
			writer.write("Female: " + getNodesWithGender("false", "female")[0] + "\n");
			writer.write("Heavily Influenced: " + getNodesWithInfluence("false", "true")[0] + "\n");
			writer.write("NOT Heavily Influenced: " + getNodesWithInfluence("false", "false")[0] + "\n");
			writer.write("9th Grade: " + getNodesWithGrade("false", "9th grade")[0] + "\n");
			writer.write("10th Grade: " + getNodesWithGrade("false", "10th grade")[0] + "\n");
			writer.write("11th Grade: " + getNodesWithGrade("false", "11th grade")[0] + "\n");
			writer.write("12th Grade: " + getNodesWithGrade("false", "12th grade")[0] + "\n");
			writer.write("Age 17 and under: " + getNodesWithAge("false", "Age 17 and under")[0] + "\n");
			writer.write("Age 18+: " + getNodesWithAge("false", "Age 18+")[0] + "\n");
			
			writer.write("\n");
			
			writer.write("**Calculate Totals**\n");
			writer.write("Vapers: " + getTotalInfluence()[0] + "\n");
			writer.write("Don't Vape: " + getTotalInfluence()[1] + "\n");
			writer.write("Male: " + getNodesWithGender("true", "male")[1] + "\n");
			writer.write("Female: " + getNodesWithGender("true", "female")[1] + "\n");
			writer.write("Heavily Influenced: " + getNodesWithInfluence("true", "true")[1] + "\n");
			writer.write("NOT Heavily Influenced: " + getNodesWithInfluence("true", "false")[1] + "\n");
			writer.write("9th Grade: " + getNodesWithGrade("true", "9th grade")[1] + "\n");
			writer.write("10th Grade: " + getNodesWithGrade("true", "10th grade")[1] + "\n");
			writer.write("11th Grade: " + getNodesWithGrade("true", "11th grade")[1] + "\n");
			writer.write("12th Grade: " + getNodesWithGrade("true", "12th grade")[1] + "\n");
			writer.write("Age 17 and under: " + getNodesWithAge("true", "Age 17 and under")[1] + "\n");
			writer.write("Age 18+: " + getNodesWithAge("true", "Age 18+")[1] + "\n");

			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 1st element -- vapers & 2nd element nonvapers
	private int[] getTotalInfluence() {
		
		int vapers = 0;
		int nonvapers = 0;
		
		for (int i = 0; i < actualSize; i++) {
			
			if (nodes.get(i).getFrequency().equals("true"))
				vapers++;
			else
				nonvapers++;
		}
		
		int[] result = {vapers, nonvapers};
		return result;
	}
	
	// first element is with vape constraint and second element is for total calculation
	private int[] getNodesWithAge(String vape, String age) {
		
		int count = 0;
		int total = 0;
		
		if (age.equals("Age 17 and under")) {
			
			for (int i = 0; i < actualSize; i++) {
				
				Node n = nodes.get(i);
				double nodeAge = Double.parseDouble(n.getAge());
				
				if (nodeAge <= 17)
					total++;
				if (n.getFrequency().equals(vape) && nodeAge <= 17)
					count++;
			}
		
		} else if (age.equals("Age 18+")) {
			
			for (int i = 0; i < actualSize; i++) {
				
				Node n = nodes.get(i);
				double nodeAge = Double.parseDouble(n.getAge());
				
				if (nodeAge >= 18)
					total++;
				if (n.getFrequency().equals(vape) && nodeAge >= 18)
					count++;
			}
		}
		
		int[] result = {count, total};
		return result;
	}
	
	private int[] getNodesWithGrade(String vape, String grade) {
		
		int count = 0;
		int total = 0;
		
		for (int i = 0; i < actualSize; i++) {
			
			Node n = nodes.get(i);
			
			if (n.getGrade().equals(grade))
				total++;
			if (n.getFrequency().equals(vape) && n.getGrade().equals(grade))
				count++;
		}
		
		int[] result = {count, total};
		return result;
	}
	
	private int[] getNodesWithGender(String vape, String gender) {
		
		int count = 0;
		int total = 0;
		
		for (int i = 0; i < actualSize; i++) {
			
			Node n = nodes.get(i);
			
			if (n.getGender().equals(gender))
				total++;
			if (n.getFrequency().equals(vape) && n.getGender().equals(gender))
				count++;
		}
		
		int[] result = {count, total};
		return result;
	}
	
	private int[] getNodesWithInfluence(String vape, String heavilyInfluenced) {
		
		int count = 0;
		int total = 0;
		
		for (int i = 0; i < actualSize; i++) {
			
			Node n = nodes.get(i);
			
			if (n.getInfluence().equals(heavilyInfluenced))
				total++;
			if (n.getFrequency().equals(vape) && n.getInfluence().equals(heavilyInfluenced))
				count++;
		}

		int[] result = {count, total};
		return result;
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
	
	// From: https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
	private static double round(double value, int places) {
	    
		if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
