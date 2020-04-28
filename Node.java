
public class Node {

	private String consent;
	private String age;
	private String grade;
	private String gender;
	private String[] referrals;
	private String influence;
	private String frequency;
	private String name;
	private int id;
	
	public Node(String c, String a, String gr, String gen, String ref, String i, String f, String n, int num) {
		
		consent = c;
		age = a;
		grade = gr;
		gender = gen;
		referrals = ref.split(", ");
		influence = i;
		frequency = f;
		name = n;
		id = num;
	}
	
	public Node(String n, int num) { // constructor used for nodes created through referrals
		
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
	}
	
	public String toString() {
		
		return id+". "+name +" ==> "+referrals[0]+", "+referrals[1]+", "+referrals[2];
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
}