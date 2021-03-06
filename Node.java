import java.util.ArrayList;

public class Node {

	private String consent;
	private String age;
	private String grade;
	private String gender;
	private String[] referrals;
	private ArrayList<Node> refList;
	private String influence;
	private String frequency;
	private String name;
	private int id;
	private int ties;
	private int strengthSum;
	private int vapeTies;
	private int genHappy;
	private int peerHappy;
	private int happyGoLucky;
	private int neverSeemHappy;
	private double happyRate;
	
	public Node(String c, String a, String gr, String gen, String ref, String i, String f, String n, int num, String[] h) {
		
		consent = c;
		age = a;
		grade = gr;
		gender = gen;
		referrals = cleanReferrals(ref.split(", "));
		influence = i;
		frequency = f;
		name = n;
		id = num;
		ties = 0;
		strengthSum = 0;
		vapeTies = 0;
		refList = new ArrayList<Node>(3);
		genHappy = Integer.parseInt(""+h[0].charAt(0));
		peerHappy = Integer.parseInt(""+h[1].charAt(0));
		happyGoLucky = Integer.parseInt(""+h[2].charAt(0));
		neverSeemHappy = Integer.parseInt(""+h[3].charAt(0));
		happyRate = (genHappy+peerHappy+happyGoLucky+(7-neverSeemHappy))/4.0;
	}
	
	public Node(String n, int num, int s) { // constructor used for nodes created through referrals
		
		consent = "2";
		age = "2";
		grade = "2";
		gender = "2";
		referrals = new String[3];
		referrals[0] = referrals[1] = referrals[2] = "2";
		influence = "2";
		frequency = "2";
		name = n;
		id = num;
		ties = 1;
		strengthSum = s;
		vapeTies = 0;
		refList = new ArrayList<Node>(3);
	}
	
	public String toString() {
		
		return id+". "+name +" ==> "+referrals[0]+", "+referrals[1]+", "+referrals[2];
	}
	
	public void addTie() {
		
		this.ties += 1;
	}
	
	public void addVapeTie() {
		
		this.vapeTies += 1;
	}
	
	public double percentTiesToVapers() {
						
		int additionalTotalTies = 0; // at most 3 (refer 3 people who did not refer back)
				
		for (Node ref : refList) {
			
			if (ref != null && ref.getRefList() != null && !ref.getRefList().contains(this)) {
				
				additionalTotalTies++;
			
			} else {
				
				continue;
			}
		}
		
		int totalTies = ties + additionalTotalTies;
		
		if (totalTies == 0 && vapeTies == 0)
			return 0;
		if (totalTies == 0)
			return 2;
		
		return (1.0*vapeTies) / totalTies;
	}
	
	public void addStrength(int val) {
		
		this.strengthSum += val;
	}
	
	public double calculateAverageStrength() {
		
		double result = (1.0*strengthSum) / ties;
		
		if (Double.isNaN(result)) {
			
			return 0;
		}
		
		return result;
	}

	public String getConsent() {
		return consent;
	}

	public void setConsent(String consent) {
		this.consent = consent;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String[] getReferrals() {
		return referrals;
	}

	public void setReferrals(String[] referrals) {
		this.referrals = referrals;
	}
	
	public ArrayList<Node> getRefList() {
		return refList;
	}

	public void addToRefList(Node n) {
		refList.add(n);
	}

	public String getInfluence() {
		return influence;
	}

	public void setInfluence(String influence) {
		this.influence = influence;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public int getTies() {
		
		return ties;
	}
	
	public void setTies(int ties) {
		
		this.ties = ties;
	}
	
	public int getVapeTies() {
		
		return vapeTies;
	}
	
	public void setVapeTies(int vTies) {
		
		this.vapeTies = vTies;
	}
	
	public int getStrengthSum() {
		
		return strengthSum;
	}
	
	public void setStrengthSum(int strengthSum) {
		
		this.strengthSum = strengthSum;
	}
	
	public int getGenHappy() {
		return genHappy;
	}

	public void setGenHappy(int genHappy) {
		this.genHappy = genHappy;
	}

	public int getPeerHappy() {
		return peerHappy;
	}

	public void setPeerHappy(int peerHappy) {
		this.peerHappy = peerHappy;
	}

	public int getHappyGoLucky() {
		return happyGoLucky;
	}

	public void setHappyGoLucky(int happyGoLucky) {
		this.happyGoLucky = happyGoLucky;
	}

	public int getNeverSeemHappy() {
		return neverSeemHappy;
	}

	public void setNeverSeemHappy(int neverSeemHappy) {
		this.neverSeemHappy = neverSeemHappy;
	}
	
	public double getHappyRate() {
		return happyRate;
	}

	private String[] cleanReferrals(String[] r) {
		
		int count = r.length;
		
		for (String s : r)
			if (s.equals("n/a"))
				count--;
		
		String[] newRef = new String[count];
		int newRefIndex = 0;
		
		for (int i = 0; i < r.length; i++) {
			
			if (!r[i].equals("n/a")) {
				
				newRef[newRefIndex] = r[i].trim();
				newRefIndex++;
			}
		}
		
		return newRef;
	}
}
