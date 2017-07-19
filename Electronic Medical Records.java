package assignment2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import assignment2.Patient.Insurance;

/* ACADEMIC INTEGRITY STATEMENT
 * 
 * By submitting this file, we state that all group members associated
 * with the assignment understand the meaning and consequences of cheating, 
 * plagiarism and other academic offenses under the Code of Student Conduct 
 * and Disciplinary Procedures (see www.mcgill.ca/students/srr for more information).
 * 
 * By submitting this assignment, we state that the members of the group
 * associated with this assignment claim exclusive credit as the authors of the
 * content of the file (except for the solution skeleton provided).
 * 
 * In particular, this means that no part of the solution originates from:
 * - anyone not in the assignment group
 * - Internet resources of any kind.
 * 
 * This assignment is subject to inspection by plagiarism detection software.
 * 
 * Evidence of plagiarism will be forwarded to the Faculty of Science's disciplinary
 * officer.
 */


/* A basic command line interface for an Electronic Medical Record System.
 * 
 * The simplest way to complete this assignment is to perform 1 functionality at a time. Start
 * with the code for the EMR constructor to import all data and then perform tasks 1-10
 * 		1.	Add a new patient to the EMR system
 *  	2.	Add a new Doctor to the EMR system
 *  	3.	Record new patient visit to the department
 *  	4.	Edit patient information
 *  	5.	Display list of all Patient IDs
 *  	6.	Display list of all Doctor IDs
 *  	7.	Print a Doctor's record
 *  	8.	Print a Patient's record
 *  	9.	Exit and save modifications
 * 	
 *	Complete the code provided as part of the assignment package. Fill in the \\TODO sections
 *  
 *  Do not change any of the function signatures. However, you can write additional helper functions 
 *  and test functions if you want.
 *  
 *  Do not define any new classes. Do not import any data structures. Do not call the sort functions
 *  of ArrayList class. Implement your own sorting functions and implement your own search function.
 *  
 *  Make sure your entire solution is in this file.
 *  
 *  We have simplified the task of reading the data from the Excel files. Instead of reading directly
 *  from Excel, each Sheet of the Excel file is saved as a comma separated file (csv) 
 * 
 */

public class EMR
{
	private String aDoctorFilePath;
	private String aPatientFilePath;
	private String aVisitsFilePath;
	private ArrayList<Doctor> doctorList = new ArrayList<Doctor>();
	private ArrayList<Patient> patientList = new ArrayList<Patient>();

	/**
	 * Used to invoke the EMR command line interface. You only need to change
	 * the 3 filepaths.
	 */
	public static void main(String[] args) throws IOException
	{
		EMR system = new EMR("./Data/Doctors.csv","./Data/Patients.csv","./Data/Visits.csv");
		system.displayMenu();
	}


	/**
	 * You don't have to modify the constructor, nor its code
	 * @param pDoctorFilePath
	 * @param pPatientFilePath
	 * @param pVisitsFilePath
	 */
	public EMR(String pDoctorFilePath, String pPatientFilePath, String pVisitsFilePath){
		this.aDoctorFilePath = pDoctorFilePath;
		this.aPatientFilePath = pPatientFilePath;
		this.aVisitsFilePath = pVisitsFilePath;

		importDoctorsInfo(this.aDoctorFilePath);
		importPatientInfo(this.aPatientFilePath);
		importVisitData(this.aVisitsFilePath);

		sortDoctors(this.doctorList);
		sortPatients(this.patientList);
	}

	/**
	 * This method should sort the doctorList in time O(n^2). It should sort the Doctors
	 * based on their ID 
	 */
	private void sortDoctors(ArrayList<Doctor> docs){
		//TODO: Fill code here
		//if the doctor list has no elements, return from this method
		if(docs.isEmpty())
		{
			System.out.println("Empty Doctors");
			return;
		}
		//if the doctor list has a size of 1, it's already sorted
		//so return from the method
		if (docs.size() == 0 || docs.size() == 1)
		{
			return;
		}

		int start;
		Doctor temp;

		for(int i = 0; i < docs.size(); i++)
		{
			//start position at 0
			start = i;
			for(int j = i + 1; j < docs.size(); j++)
			{
				//if the element at i+1 is greater than the previous element at i
				//then the starting element is the element at i+1
				if(docs.get(j).getID() < docs.get(start).getID())
				{
					start = j;
				}
			}
			//swap the elements of i and i+1
			temp = docs.get(start);
			docs.set(start, docs.get(i));
			docs.set(i, temp);
		}
	}

	/**
	 * This method should sort the patientList in time O(n log n). It should sort the 
	 * patients based on the hospitalID
	 */
	private void sortPatients(ArrayList<Patient> patients){
		//TODO: Fill code here
		//if the file has no elements return
		if(patients.isEmpty())
		{
			System.out.println("This patient file is empty");
			return;
		}

		//if the file has a size of 1, it is already sorted
		//so return
		if (patients.size() == 1)
		{
			return; 
		}
		//two new arraylists for the left and right side of the patientList
		ArrayList<Patient> left = new ArrayList<Patient>();
		ArrayList<Patient> right = new ArrayList<Patient>();
		int center;

		if (patients.size() > 1)
		{
			//center of the list
			center = patients.size()/2;

			//from position 0 to the center, put all values from the patientList
			//into the left arrayList
			for(int i = 0; i < center; i++)
			{
				left.add(patients.get(i));
			}

			//from the center to the end of the patientList, put all values from the patientList
			//into the right arrayList
			for(int i = center; i < patients.size(); i++)
			{
				right.add(patients.get(i));
			}

			// Sort the left and right halves of the arraylist.
			sortPatients(left);
			sortPatients(right);

			merge(left,right,patients);
		}
	}
	private void merge(ArrayList<Patient> left, ArrayList<Patient> right, ArrayList<Patient> patients)
	{
		int leftIndex = 0;
		int rightIndex = 0;
		int finalIndex = 0;

		while(leftIndex < left.size() && rightIndex < right.size()) 
		{
			//if the hospitalID in the left array at a certain position is less than
			//the hospitalID on the right, put the left element in the final array and
			//increment the position on the left and increment the final position.
			if( Long.valueOf(left.get(leftIndex).getHospitalID())
					<= Long.valueOf(right.get(rightIndex).getHospitalID())) 
			{
				patients.set(finalIndex,left.get(leftIndex));
				leftIndex++;
				finalIndex++;
			}
			//do the opposite
			else 
			{
				patients.set(finalIndex, right.get(rightIndex));
				rightIndex++;
				finalIndex++;
			}

		}

		ArrayList<Patient> restArray;
		int restIndex;
		if (leftIndex >= left.size()) {
			// The left arraylist has been used up...
			restArray = right;
			restIndex = rightIndex;
		}
		else {
			// The right arraylist has been used up...
			restArray = left;
			restIndex = leftIndex;
		}

		// Copy the rest of whichever arraylist (left or right) was
		// not used up.
		for (int i = restIndex; i< restArray.size(); i++) {
			patients.set(finalIndex, restArray.get(i));
			finalIndex++;
		}
	}


	/**
	 * This method adds takes in the path of the Doctor sheet csv file and imports
	 * all doctors data into the doctorList ArrayList
	 */
	private ArrayList<Doctor> importDoctorsInfo(String doctorFilePath){
		//TODO: Fill code here
		try{

			//reading the file
			BufferedReader reader = new BufferedReader(new FileReader(doctorFilePath));
			String currentLine = reader.readLine();

			while( currentLine != null)
			{
				//read the first Line
				currentLine = reader.readLine();

				if(currentLine != null)
				{
					//split the csv into several parts by splitting
					//every line by a comma
					String[] parts = currentLine.split(",");

					for(int i = 0; i < parts.length; i++)
					{
						parts[i] = parts[i].trim();
					}
					//allocate the designate parts to the variables
					String firstName = parts[0];
					String lastName = parts[1];
					String specialty = parts[2];
					Long x = Long.parseLong(parts[3]);

					//create a new doctor object and add it to the arrayList
					Doctor d = new Doctor(firstName, lastName, specialty, x);
					doctorList.add(d);
				}
			}
			reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return doctorList;
	}

	/**
	 * This method adds takes in the path of the Patient sheet csv file and imports
	 * all Patient data into the patientList ArrayList
	 */
	private ArrayList<Patient> importPatientInfo(String patientFilePath){
		//TODO: Fill code here
		try{

			BufferedReader reader = new BufferedReader(new FileReader(patientFilePath));
			String currentLine = reader.readLine();

			while( currentLine != null)
			{
				currentLine = reader.readLine();

				if(currentLine != null)
				{
					//split the csv into several parts by splitting
					//every line by a comma
					String[] parts = currentLine.split(",");

					for(int i = 0; i < parts.length; i++)
					{
						parts[i] = parts[i].trim();
					}
					//allocate the designate parts to the variables
					String firstName = parts[0];
					String lastName = parts[1];
					double height = Double.parseDouble(parts[2]);
					Insurance patientType = null;
					Insurance newPatientType = null;
					String type = parts[3];
					if("none".compareTo(type.toLowerCase()) == 0)
					{
						patientType = Insurance.NONE;
					}
					else if("ramq".compareTo(type.toLowerCase()) == 0)
					{
						patientType = Insurance.RAMQ;
					}
					else if("private".compareTo(type.toLowerCase()) == 0)
					{
						patientType = Insurance.Private;
					}
					else
					{
						patientType = null;
					}
					newPatientType = patientType;
					String gender = parts[4];
					Long hospitalID = Long.parseLong(parts[5]);
					String DOB = parts[6];
					//create a new patient object and add it to the arrayList
					Patient p = new Patient(firstName, lastName, height, gender, newPatientType, hospitalID, DOB);
					patientList.add(p);
				}
			}

			reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return patientList;
	}

	/**
	 * This method adds takes in the path of the Visit sheet csv file and imports
	 * every Visit data. It appends Visit objects to their respective Patient
	 */
	private void importVisitData(String visitsFilePath){

		try{

			BufferedReader reader = new BufferedReader(new FileReader(visitsFilePath));
			String currentLine = reader.readLine();
			Doctor doc;
			Patient patient;
			while( currentLine != null)
			{
				currentLine = reader.readLine();

				if(currentLine != null)
				{
					//split the csv into several parts by splitting
					//every line by a comma
					String[] parts = currentLine.split(",");

					for(int i = 0; i < parts.length; i++)
					{
						parts[i] = parts[i].trim();
					}
					//allocate the designate parts to the variables
					Long hospitalID = Long.parseLong(parts[0]);
					Long doctorID = Long.parseLong(parts[1]);

					Doctor d = this.findDoctor(doctorID);

					if(d == null)
					{
						System.out.println("There's no match for the doctorID in the Visits CSV");
						return;
					}
					else
					{
						doc = d;
					}

					Patient p = this.findPatient(hospitalID);

					if(p == null)
					{
						System.out.println("There's no match for the hospitalID in the Visits CSV");
						return;
					}
					else
					{
						patient = p;
					}
					String date = parts[2];
					String note = parts[3];

					//create a new patient object and add it to the arrayList
					Visit newVisit = new Visit(doc, patient, date, note);
					patient.aVisitList.add(newVisit);
				}
			}
			reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	/**
	 * This method uses an infinite loop to simulate the interface of the EMR system.
	 * A user should be able to select 10 options. The loop terminates when a user 
	 * chooses option 10: EXIT. You do not have to modify this code.
	 */
	public void displayMenu(){
		System.out.println();
		System.out.println("****************************************************************");
		System.out.println();
		System.out.println("Welcome to The Royal Victoria EMR Interface V1.0");
		System.out.println("");
		System.out.println("This system will allow you to access and modify the health records of the hospital");
		System.out.println();
		System.out.println("****************************************************************");
		System.out.println();

		Scanner scan = new Scanner(System.in);
		boolean exit = false;
		while(!exit){

			System.out.println("Please select one of the following options and click enter:");
			System.out.println("   (1) Add a new patient to the EMR system\n" +
					"   (2) Add a new Doctor to the EMR system\n" +
					"   (3) Record new patient visit to the department\n" +
					"   (4) Edit patient information\n" +
					"   (5) Display list of all Patient IDs\n" +
					"   (6) Display list of all Doctor IDs\n" +
					"   (7) Print a Doctor's record\n" +
					"   (8) Print a Patient's record\n" +
					"   (9) Exit and save modifications\n");
			System.out.print("   ENTER YOUR SELECTION HERE: ");

			int choice = 0;
			try{
				choice = Integer.parseInt(scan.next());
			}
			catch(Exception e){
				;
			}

			System.out.println("\n");

			switch(choice){
			case 1: 
				option1();
				break;
			case 2: 
				option2();
				break;
			case 3: 
				option3();
				break;
			case 4: 
				option4();
				break;
			case 5: 
				option5();
				break;
			case 6: 
				option6();
				break;
			case 7: 
				option7();
				break;
			case 8: 
				option8();
				break;
			case 9: 
				option9();
				break;	
			default:
				System.out.println("   *** ERROR: You entered an invalid input, please try again ***\n");
				break;
			}
		}
	}

	/**
	 * This method adds a patient to the end of the patientList ArrayList. It 
	 * should ask the user to provide all the input to create a Patient object. The 
	 * user should not be able to enter empty values. The input should be supplied
	 * to the addPatient method
	 */
	private void option1(){
		//TODO: Fill code here. Ask the user to supply by command-line values for all the variables below.
		Scanner scanNew = new Scanner(System.in);
		System.out.println("Please enter your Firstname: ");
		String answer1 = scanNew.nextLine();
		while(answer1.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your firstname: ");
			answer1 = scanNew.nextLine();
		}
		String firstname = answer1;

		System.out.println("Please enter your lastname: ");
		String answer2 = scanNew.nextLine();
		while(answer2.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your Lastname: ");
			answer2 = scanNew.nextLine();
		}
		String lastname = answer2;

		System.out.println("Please enter your height: ");
		String answer3 = scanNew.nextLine();
		while(answer3.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your height: ");
			answer3 = scanNew.nextLine();
		}
		double newAnswer3 = Double.parseDouble(answer3);
		double height = newAnswer3;

		System.out.println("Please enter your gender: ");
		String answer4 = scanNew.nextLine();
		while(answer4.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your gender: ");
			answer4 = scanNew.nextLine();
		}
		String Gender = answer4;


		Insurance ins = null;
		while(true)
		{	
			System.out.println("Please enter your insurance type: ");
			String answer5 = scanNew.nextLine();

			String type = answer5;
			if("none".compareTo(type.toLowerCase()) == 0)
			{
				ins = Insurance.NONE;
				break;
			}
			else if("ramq".compareTo(type.toLowerCase()) == 0)
			{
				ins = Insurance.RAMQ;
				break;
			}
			else if("private".compareTo(type.toLowerCase()) == 0)
			{
				ins = Insurance.Private;
				break;
			}

			System.out.println("You enetered nothing or your entry was invalid.");
		}
		Insurance type = ins;

		System.out.println("Please enter your hospital ID: ");
		String answer6 = scanNew.nextLine();
		while(answer6.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your hospital ID: ");
			answer6 = scanNew.nextLine();
		}
		Long newAnswer6 = Long.parseLong(answer6);
		Long hospitalID = newAnswer6;

		System.out.println("Please enter your date of birth: ");
		String answer7 = scanNew.nextLine();
		while(answer7.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your Date of Birth: ");
			answer7 = scanNew.nextLine();
		}
		String DOB = answer7;

		addPatient(firstname, lastname, height, Gender, type, hospitalID, DOB);
	}

	/**
	 * This method adds a patient object to the end of the patientList ArrayList. 
	 */
	private void addPatient(String firstname, String lastname, double height, String Gender, Insurance type, Long hospitalID, String DOB){
		//TODO: Fill code here
		Patient patient = new Patient(firstname, lastname, height, Gender, type, hospitalID, DOB);
		patientList.add(patientList.size()-1, patient);

	}


	/**
	 * This method adds a doctor to the end of the doctorList ArrayList. It 
	 * should ask the user to provide all the input to create a Doctor object. The 
	 * user should not be able to enter empty values.
	 */
	private void option2(){
		//TODO: Fill code here. Ask the user to supply by command-line values for all the variables below.
		Scanner scan1 = new Scanner(System.in);
		System.out.println("Please enter your firstname: ");
		String answer1 = scan1.nextLine();
		while(answer1.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your firstname: ");
			answer1 = scan1.nextLine();
		}
		String firstname = answer1;

		System.out.println("Please enter your lastname: ");
		String answer2 = scan1.nextLine();
		while(answer2.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your lastname: ");
			answer2 = scan1.nextLine();
		}
		String lastname = answer2;

		System.out.println("Please enter your specialty: ");
		String answer3 = scan1.nextLine();
		while(answer3.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your specialty: ");
			answer3 = scan1.nextLine();
		}
		String specialty = answer3;

		System.out.println("Please enter your id: ");
		String answer4 = scan1.nextLine();
		while(answer4.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your id: ");
			answer4 = scan1.nextLine();
		}
		Long doctor_id = Long.parseLong(answer4);

		addDoctor(firstname, lastname, specialty, doctor_id);
	}

	/**
	 * This method adds a doctor to the end of the doctorList ArrayList.
	 */
	private void addDoctor(String firstname, String lastname, String specialty, Long docID){
		//TODO: Fill code here
		Doctor doctor = new Doctor(firstname, lastname, specialty, docID);
		doctorList.add(doctorList.size()-1, doctor);
	}

	/**
	 * This method creates a Visit record. 
	 */
	private void option3(){
		//TODO: Fill code here. Ask the user to supply by command-line values for all the variables below.
		Scanner scan2 = new Scanner(System.in);

		Doctor newDoctor;
		String answer;
		while(true)
		{
			System.out.println("Please enter your doctor id: ");
			answer = scan2.nextLine();

			if(!answer.isEmpty())
			{
				Long value = Long.valueOf(answer);
				newDoctor = this.findDoctor(value);
				if(newDoctor != null)
				{
					break;
				}
				else
				{
					System.out.print("We don't have a doctor with such that id.");
				}
			}
			else
			{
				System.out.println("You enetered nothing.");
			}
		}

		Patient newPatient;
		String newAnswer;
		while(true)
		{
			System.out.println("Please enter the patient ID: ");
			newAnswer = scan2.nextLine();

			if(!newAnswer.isEmpty())
			{
				Long value = Long.valueOf(newAnswer);
				newPatient = this.findPatient(value);
				if(newPatient != null)
				{
					break;
				}
				else
				{
					System.out.print("We don't have a patient with such that id.");
				}
			}
			else
			{
				System.out.println("You enetered nothing.");
			}
		}

		System.out.println("Please enter the date of your visit: ");
		String answer3 = scan2.nextLine();
		while(answer3.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter your lastname: ");
			answer3 = scan2.nextLine();
		}
		String date = answer3;

		System.out.println("Please enter any notes: ");
		String answer4 = scan2.nextLine();
		while(answer4.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter any notes: ");
			answer4 = scan2.nextLine();
		}
		String note = answer4;

		recordPatientVisit(newDoctor, newPatient, date, note);
	}

	/**
	 * This method creates a Visit record. It adds the Visit to a Patient object.
	 */
	private void recordPatientVisit(Doctor doctor, Patient patient, String date, String note){
		//TODO: Fill code here. Remember, the visit objects are stored within the Patient objects.
		if(doctor == null || patient == null)
		{
			System.out.println("This visit can't be created due to the invalid patient and doctor variables");
			return;
		}
		Visit visit = new Visit(doctor, patient, date, note);
		patient.aVisitList.add(visit);
	}

	/**
	 * This method edits a Patient record. Only the firstname, lastname, height,
	 * Insurance type, and date of birth could be changed. You should ask the user to supply the input.
	 */
	private void option4(){
		//TODO: These are the 5 values that could change. You must ask the user to input new values 
		// for each of the 5 variables
		Scanner scan3 = new Scanner(System.in);

		String newFirstname = null;
		System.out.println("Please enter a new first name: ");
		String answer1 = scan3.nextLine();

		while(answer1.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter a new first name: ");
			answer1 = scan3.nextLine();
		}
		newFirstname = answer1;

		String newLastname = null;
		System.out.println("Please enter a new last name: ");
		String answer2 = scan3.nextLine();

		while(answer2.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter a new last name: ");
			answer2 = scan3.nextLine();
		}
		newLastname = answer2;

		double newHeight = 0;
		System.out.println("Please enter a new height: ");
		String answer3 = scan3.nextLine();

		while(answer3.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter a new height: ");
			answer3 = scan3.nextLine();
		}
		double newAnswer3 = Double.parseDouble(answer3);
		newHeight = newAnswer3;

		String newGender = null;

		System.out.println("Please enter a new gender: ");
		String answer4 = scan3.nextLine();

		while(answer4.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter a new gender: ");
			answer4 = scan3.nextLine();
		}
		newGender = answer4;

		Insurance newType = null;
		while(true)
		{	
			System.out.println("Please enter your insurance type: ");
			String answer5 = scan3.nextLine();

			String type = answer5;
			if("none".compareTo(type.toLowerCase()) == 0)
			{
				newType = Insurance.NONE;
				break;
			}
			else if("ramq".compareTo(type.toLowerCase()) == 0)
			{
				newType = Insurance.RAMQ;
				break;
			}
			else if("private".compareTo(type.toLowerCase()) == 0)
			{
				newType = Insurance.Private;
				break;
			}

			System.out.println("You enetered nothing or your entry was invalid.");
		}
		Insurance type1 = newType;

		String newDOB = null;
		System.out.println("Please enter a new date of birth: ");
		String answer7 = scan3.nextLine();

		while(answer7.isEmpty())
		{
			System.out.println("You enetered nothing. Please enter a new date of birth: ");
			answer7 = scan3.nextLine();
		}
		newDOB = answer7;

		Patient newPatient;
		Long newID = null;
		String newAnswer;
		while(true)
		{
			System.out.println("Please enter the patient ID: ");
			newAnswer = scan3.nextLine();

			if(!newAnswer.isEmpty())
			{
				Long value = Long.valueOf(newAnswer);
				newPatient = this.findPatient(value);
				if(newPatient != null)
				{
					break;
				}
				else
				{
					System.out.print("We don't have a patient with such that id.");
				}
			}
			else
			{
				System.out.println("You enetered nothing.");
			}
		}
		newID = Long.parseLong(newAnswer);

		editPatient(newFirstname, newLastname, newHeight, newGender, type1, newDOB, newID);
	}

	/**
	 * This method edits a Patient record. Only the firstname, lastname, height, 
	 * Insurance type, address could be changed, and date of birth. 
	 */
	private void editPatient(String firstname, String lastname, double height, String gender, Insurance type, String DOB, Long ID){
		//TODO: Fill code here
		Patient p1 = this.findPatient(ID);
		if(p1 == null)
		{
			System.out.println("Patient doesn't exist");
			return;
		}
		p1.setFirstName(firstname);
		p1.setLastName(lastname);
		p1.setHeight(height);
		p1.setGender(gender);
		p1.setInsurance(type);
		p1.setDateOfBirth(DOB);

	}

	/**
	 * This method should first sort the patientList and then print to screen 
	 * one Patient at a time by calling the displayPatients() method
	 */
	private void option5(){
		sortPatients(this.patientList);
		displayPatients(this.patientList);
	}

	/**
	 * This method should print to screen 
	 * one Patient at a time by calling the Patient toString() method
	 */
	private void displayPatients(ArrayList<Patient> patients){
		//TODO: Fill code here. Loop through all patients and call toString method
		for(int i = 0; i < patients.size(); i++)
		{
			String printMe = patients.get(i).toString();
			System.out.println();
			System.out.println(printMe);
		}
	}

	/**
	 * This method should first sort the doctorList and then print to screen 
	 * one Doctor at a time by calling the displayDoctors() method
	 */
	private void option6(){
		sortDoctors(this.doctorList);
		displayDoctors(this.doctorList);
	}

	/**
	 * This method should first sort the doctorList and then print to screen 
	 * one Doctor at a time by calling the Doctor toString() method
	 */
	private void displayDoctors(ArrayList<Doctor> docs){
		//TODO: Fill code here
		for(int i = 0; i < docs.size(); i++)
		{
			String printMe = docs.get(i).toString();
			System.out.println();
			System.out.println(printMe);
		}
	}


	/**
	 * This method should ask the user to supply an id of the patient they want info about
	 */
	private void option8(){
		//TODO: ask the user to specify the id of the patient
		Scanner patScan = new Scanner(System.in);
		Patient newPatient;

		while(true)
		{
			System.out.println("Please enter the id of the patient you seek: ");
			String answer = patScan.nextLine();

			if(!answer.isEmpty())
			{
				Long value = Long.valueOf(answer);
				newPatient = this.findPatient(value);
				if(newPatient != null)
				{
					break;
				}
				else
				{
					System.out.print("We don't have a patient with such that id.");
				}
			}
			else
			{
				System.out.println("You enetered nothing.");
			}
		}

		Long hospitalID = Long.valueOf(newPatient.getHospitalID());
		printPatientRecord(hospitalID);

	}

	/**
	 * This method should call the toString method of a specific Patient. It should
	 * also list all the patient's Visit objects sorted in order by date (earliest first). For
	 * every Visit, the doctor's firstname, lastname and id should be printed as well.
	 */
	private void printPatientRecord(Long patientID){
		//TODO: Fill code here
		Patient searchP = this.findPatient(patientID);
		if(searchP == null)
		{
			System.out.println("The patient you seek doesn't exist in this record");
			return;
		}
		else
		{
			System.out.println("Patients: ");
			System.out.println(searchP.toString());
			System.out.println();
			System.out.println("Visits: ");
			//sort the visitList
			sortVisitsDate(searchP.aVisitList);
			for(int i = 0; i < searchP.aVisitList.size(); i++)
			{
				String printMe = searchP.aVisitList.get(i).toString();
				System.out.println(printMe);
				System.out.println();
			}
		}
	}

	private void sortVisitsDate(ArrayList<Visit> visits){
		//TODO: Fill code here

		if(visits.isEmpty())
		{
			System.out.println("Empty Visit List");
			return;
		}
		if(visits.size() == 1)
		{
			System.out.println("Array is sorted");
			return;
		}

		int start;
		Visit temp;

		for(int i = 0; i < visits.size(); i++)
		{
			start = i;
			for(int j = i + 1; j < visits.size(); j++)
			{
				if(visits.get(j).getDate().compareTo(visits.get(start).getDate()) < 0)
				{
					start = j;
				}
			}
			temp = visits.get(start);
			visits.set(start, visits.get(i));
			visits.set(i, temp);
		}
	}


	/**
	 * This method should ask the user to supply an id of a doctor they want info about
	 */
	private void option7(){
		//TODO: ask the user to specify the id of the doctor
		Scanner sc = new Scanner(System.in);

		Doctor newDoctor;
		String answer;
		while(true)
		{
			System.out.println("Please enter your doctor id: ");
			answer = sc.nextLine();

			if(!answer.isEmpty())
			{
				Long value = Long.valueOf(answer);
				newDoctor = this.findDoctor(value);
				if(newDoctor != null)
				{
					break;
				}
				else
				{
					System.out.print("We don't have a doctor with such that id. ");
				}
			}
			else
			{
				System.out.println("You enetered nothing. ");
			}
		}

		Long doc_id = Long.parseLong(answer);

		Doctor d = findDoctor(doc_id);
		printDoctorRecord(d);
	}

	/**
	 * Searches in O(log n) time the doctorList to find the correct doctor with doctorID = id
	 * @param id
	 * @return
	 */
	private Doctor findDoctor(Long id){
		//TODO: Fill code here
		if(id == null)
		{
			System.out.println("This id is null");
			return null;
		}

		int low = 0;
		int high = doctorList.size()-1;
		int mid = (low + high) / 2;
		boolean found = false;

		//sorting the doctor list
		sortDoctors(doctorList);

		while (low <= high) 
		{
			mid = (low + high)/2;
			Long midVal = doctorList.get(mid).getID();

			if( midVal.compareTo(id) == 0)
			{
				found = true;
				break;
			}
			else if(midVal.compareTo(id) < 0)
			{
				low = mid + 1;
			}
			else
			{
				high = mid - 1;
			}
		}

		if(found)
		{
			return doctorList.get(mid);
		}
		else
		{
			return null;
		}

	}

	private Patient findPatient(Long id){
		//TODO: Fill code here
		if(id == null)
		{
			System.out.println("invalid id");
			return null;
		}

		int low = 0;
		int high = patientList.size()-1;
		int mid = (low + high) / 2;
		boolean found = false;

		//sorting the patient list
		sortPatients(patientList);

		while (low <= high) 
		{
			mid = (low + high) / 2;
			Long midVal = Long.parseLong(patientList.get(mid).getHospitalID());

			if(midVal.compareTo(id) == 0)
			{
				found = true;
				break;
			}
			else if (midVal.compareTo(id) < 0)
			{
				low = mid + 1;
			}
			else
			{
				high = mid - 1;
			}

		}

		if(found)
		{
			return patientList.get(mid);
		}
		else
		{
			return null;
		}
	}

	/**
	 * This method should call the toString() method of a specific Doctor. It should
	 * also find and list all the patients that a Doctor has seen by calling their toString()
	 * method as well. It should also list the date that the doctor saw a particular patient
	 */
	private void printDoctorRecord(Doctor d){
		//TODO: Fill code here}
		if(d == null)
		{
			System.out.println("This doctor doesn't exist");
			return;
		}

		System.out.println("Doctor: ");
		System.out.println(d.toString());
		System.out.println();
		System.out.println("Patients: ");
		for(int i = 0; i < patientList.size(); i++)
		{
			Patient p = patientList.get(i);
			for(int j = 0; j < p.aVisitList.size(); j++)
			{
				if(p.aVisitList.get(j).getDoctor() == d)
				{
					String printMe = p.toString() + " " + p.aVisitList.get(j).getDate();
					System.out.println(printMe);
					System.out.println();			
				}
				//System.out.println("This doctor hasn't seen this patient");
			}
		}
	}

	/**
	 * This method should be invoked from the command line interface if the user
	 * would like to quit the program. This method should export all the Doctor, Patient and 
	 * Visit data by overwriting the contents of the 3 original files.
	 */
	private void option9(){
		exitAndSave();
	}


	/**
	 * Export all the Doctor, Patient and Visit data by overwriting the contents of the 3 original csv files.
	 */
	private void exitAndSave(){
		//TODO: Fill code here
		try
		{
			csvWriteDoctor("./Data/Doctors.csv");
			csvWritePatient("./Data/Patients.csv");
			csvWriteVisit("./Data/Visits.csv");
		} 

		catch (IOException e) 
		{
			e.printStackTrace();
		}
		System.exit(0);
	}

	private void csvWriteDoctor(String doctorFilePath) throws IOException
	{
		FileWriter fw = new FileWriter(doctorFilePath);
		BufferedWriter bw = new BufferedWriter(fw);

		try
		{
			bw.write("Firstname");
			bw.write(",");
			bw.write("Lastname");
			bw.write(",");
			bw.write("Specialty");
			bw.write(",");
			bw.write("DoctorID");
			bw.newLine();
			for(int i = 0; i < doctorList.size(); i++)
			{
				bw.append(this.doctorList.get(i).getFirstName());
				bw.append(",");
				bw.append(this.doctorList.get(i).getLastName());
				bw.append(",");
				bw.append(this.doctorList.get(i).getSpecialty());
				bw.append(",");
				bw.append(this.doctorList.get(i).getID() + "");
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

	}

	private void csvWritePatient(String patientFilePath) throws IOException
	{
		FileWriter fw = new FileWriter(patientFilePath);
		BufferedWriter bw = new BufferedWriter(fw);

		try
		{
			bw.write("FirstName");
			bw.write(",");
			bw.write("LastName");
			bw.write(",");
			bw.write("Height (cm)");
			bw.write(",");
			bw.write("Insurance");
			bw.write(",");
			bw.write("Gender");
			bw.write(",");
			bw.write("HospitalID");
			bw.write(",");
			bw.write("Date of Birth (dd-mm-yyyy)");
			bw.newLine();

			for(int i = 0; i < patientList.size(); i++)
			{
				bw.append(this.patientList.get(i).getFirstName());
				bw.append(",");
				bw.append(this.patientList.get(i).getLastName());
				bw.append(",");
				bw.append(this.patientList.get(i).getHeight() + "");
				bw.append(",");
				bw.append(this.patientList.get(i).getInsurance() + "");
				bw.append(",");
				bw.append(this.patientList.get(i).getGender());
				bw.append(",");
				bw.append(this.patientList.get(i).getHospitalID());
				bw.append(",");
				bw.append(this.patientList.get(i).getDateOfBirth());	
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

	}

	private void csvWriteVisit(String visitsFilePath) throws IOException
	{
		FileWriter fw = new FileWriter(visitsFilePath);
		BufferedWriter bw = new BufferedWriter(fw);

		try
		{
			bw.write("HospitalID");
			bw.write(",");
			bw.write("DoctorID");
			bw.write(",");
			bw.write("Date");
			bw.write(",");
			bw.write("DoctorNote");
			bw.newLine();
			for(int i = 0; i < patientList.size(); i++)
			{
				Patient p = patientList.get(i);
				for(int j = 0; j < p.aVisitList.size(); j++)
				{
					bw.append(p.aVisitList.get(j).getPatient().getHospitalID());
					bw.append(",");
					bw.append(p.aVisitList.get(j).getDoctor().getID() + "");
					bw.append(",");
					bw.append(p.aVisitList.get(j).getDate());
					bw.append(",");
					bw.append(p.aVisitList.get(j).getNote());
					bw.newLine();
				}
			}
			bw.flush();
			bw.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

	}

}

/**
 * This simple class just keeps the information about
 * a Patient together. You will have to Modify this class
 * and fill in missing data.
 */
class Patient
{
	public enum Insurance {RAMQ, Private, NONE};

	private String aFirstName;
	private String aLastName;
	private double aHeight;
	private String aGender;
	private Insurance aInsurance;
	private Long aHospitalID;
	private String aDateOfBirth; //ex. 12-31-1988 (Dec. 31st, 1988)
	ArrayList<Visit> aVisitList;

	public Patient(String pFirstName, String pLastName, double pHeight, String pGender, Insurance pInsurance,
			Long pHospitalID, String pDateOfBirth)
	{
		//TODO: Fill code here
		this.aFirstName = pFirstName;
		this.aLastName = pLastName;
		this.aHeight = pHeight;
		this.aGender = pGender;
		this.aInsurance = pInsurance;
		this.aHospitalID = pHospitalID;
		this.aDateOfBirth = pDateOfBirth;
		this.aVisitList = new ArrayList<Visit>();
	}

	public String getFirstName()
	{
		//TODO: Fill code here
		return this.aFirstName;
	}

	public String getLastName()
	{
		//TODO: Fill code here
		return this.aLastName;
	}

	public String getHospitalID()
	{
		//TODO: Fill code here
		return this.aHospitalID + "";
	}

	public String getDateOfBirth()
	{
		//TODO: Fill code here
		return this.aDateOfBirth;
	}

	//i added this myself
	public double getHeight()
	{
		return this.aHeight;
	}

	public String getGender()
	{
		return this.aGender;
	}

	public Insurance getInsurance()
	{
		return this.aInsurance;
	}

	public void addVisit(String vDate, Doctor vDoctor){
		//TODO: Fill code here
		String vNote = "";
		aVisitList.add(new Visit(vDoctor, this, vDate, vNote));

	}

	public void setFirstName(String fname){
		this.aFirstName = fname;
	}

	public void setLastName(String lname){
		this.aLastName = lname;
	}

	public void setHeight(double height){
		this.aHeight = height;
	}

	public void setGender(String gender){
		this.aGender = gender;
	}

	public void setInsurance(Insurance type){
		this.aInsurance = type;
	}

	public void setDateOfBirth(String dob){
		this.aDateOfBirth = dob;
	}

	/**
	 * This method should print all the Patient's info. "ID, Lastname, Firstname, etc..."
	 */
	public String toString(){
		//TODO: Fill code here
		return this.aFirstName + " " + this.aLastName + " " + this.aHeight + " "
		+ this.aGender + " " + this.aInsurance + " " + this.aHospitalID + " " + this.aDateOfBirth;
	}
}

/**
 * This simple class just keeps the information about
 * a Doctor together. Do modify this class as needed.
 */
class Doctor
{
	private String aFirstName;
	private String aLastName;
	private String aSpecialty; 
	private Long aID;

	public Doctor(String pFirstName, String pLastName, String pSpecialty, Long ID)
	{
		//TODO: Fill code here
		this.aFirstName = pFirstName;
		this.aLastName = pLastName;
		this.aSpecialty = pSpecialty;
		this.aID = ID;
	}

	public String getFirstName()
	{
		//TODO: Fill code here
		return this.aFirstName;
	}

	public String getLastName()
	{
		//TODO: Fill code here
		return this.aLastName;
	}

	public String getSpecialty(){
		//TODO: Fill code here
		return this.aSpecialty;
	}

	public Long getID(){
		//TODO: Fill code here

		return this.aID;
	}

	/**
	 * This method should print all the Doctor's info. "ID, Lastname, Firstname, Specialty"
	 */
	public String toString(){
		//TODO: Fill code here
		return this.aFirstName + " " + this.aLastName + " " + this.aSpecialty + " " + this.aID;
	}

}

/**
 * This simple class just keeps the information about
 * a Visit together. Do modify this class as needed.
 */
class Visit
{
	private Doctor aDoctor;
	private Patient aPatient;
	private String aDate; 
	private String anote;

	public Visit(Doctor vDoctor, Patient vPatient, String vDate, String vNote)
	{
		this.aDoctor = vDoctor;
		this.aPatient = vPatient;
		this.aDate = vDate;
		this.anote = vNote;
	}

	public Doctor getDoctor()
	{
		//TODO: Fill code here
		return this.aDoctor;
	}

	public Patient getPatient()
	{
		//TODO: Fill code here
		return this.aPatient;
	}

	public String getDate(){
		//TODO: Fill code here
		return this.aDate;
	}

	public String getNote(){
		//TODO: Fill code here
		return this.anote;
	}

	public String toString(){
		//TODO: Fill code here
		return this.aDoctor + " " + this.aPatient + " " + this.aDate + " " + this.anote;
	}

}