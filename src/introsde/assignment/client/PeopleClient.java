package introsde.assignment.client;

import introsde.assignment.soap.HealthProfileHistory;
import introsde.assignment.soap.Measure;
import introsde.assignment.soap.MeasureCreate;
import introsde.assignment.soap.MeasureType;
import introsde.assignment.soap.MeasureTypes;
import introsde.assignment.soap.MeasureUpdate;
import introsde.assignment.soap.People;
import introsde.assignment.soap.PeopleList;
import introsde.assignment.soap.People_Service;
import introsde.assignment.soap.Person;
import introsde.assignment.soap.PersonCreate;
import introsde.assignment.soap.PersonUpdate;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class PeopleClient {

	public static String clientTitle = 
			"\n############################################################################################################"+
			"\n### IntroSDE 2015 Assignment 3 "+
			"\n### Author:  Daniel Bruzual"+
			"\n###"+
			"\n### WSDL URL: %s"+
			"\n###\n";
	
	public static String clientEnd =
			"\n\n############################################################################################################"+
			"\n### All tests done. Thanks for your time." +
			"\n############################################################################################################";
	
	public static String testTitle = 
			"\n############################################################################################################\n"+
			"#  [Test %2d]       %s\n"+
			"#  Methods used:   %s\n"+
			"#  Method numbers: %s\n"+
			"#  Response: \n";
	public static int testCase = 1;
	
	public static String personJoinTableHeader = 
			" -----------------------------------------------------------------------------------------------------------\n"
			+String.format(" %5s%15s%15s%15s%12s%12s%20s%10s", "ID", "First Name", "Last Name", "Birthdate", "mType", "mValue", "Date Registered", "mid")
			+"\n -----------------------------------------------------------------------------------------------------------";
	public static String personJoinRow = " %5d%15s%15s%15s%12s%12s%20s%10d";
	public static String personJoinEmptyMeasureRow = " %5d%15s%15s%15s%12s%12s%20s%10s";
	

	public static String personTableHeader = 
			" -------------------------------------------------------\n"
			+String.format(" %5s%15s%15s%15s", "ID", "First Name", "Last Name", "Birthdate")
			+"\n -------------------------------------------------------";
	public static String personTableRow = " %5d%15s%15s%15s";

	
	public static String measureTypesTableHeader = 
			" ----------------------------------\n"
			+String.format(" %15s%15s", "Measure Type", "Value Type")
			+"\n ----------------------------------";
	public static String measureTypesTableRow = " %15s%15s";
	
	
	public static String measureTableHeader = 
			" ----------------------------------------------------------------\n"
			+String.format(" %5s%20s%15s%20s", "mid", "Measure Type", "Value", "Date Registered")
			+"\n ----------------------------------------------------------------";
	public static String measureTableRow = " %5d%20s%15s%20s";
	
	/*
	 * 
	 */
	public static void main(String[] args){
		if(args.length == 0){
			System.err.println("Please specify the location of the WSDL file");
			System.exit(1);
		}
		String wsdlLocation = args[0];
		People_Service peopleService = null;
		try{
			peopleService = new People_Service(new URL(wsdlLocation));
		} catch (MalformedURLException e){
			System.err.println("Could not connect to server. Malformed URI: "+wsdlLocation);
			System.exit(1);
		}
		
		People peoplePort = peopleService.getPeopleImplPort();

		//Print server information
		System.out.println(String.format(clientTitle, wsdlLocation));
		
		//Run test cases
		/**********************************************************************
		 * T0:	Get all people in database
		 */
		PeopleList peopleList = peoplePort.readPersonList();
		List<Person> people = peopleList.getPeople();
		System.out.println(String.format(testTitle, testCase++, 
				"Get all people in the database, along with their current health profile. Store the first.",
				"readPersonList()",
				"#1"));
		printPeopleWithMeasures(people);
		
		/**********************************************************************
		 * T1: 	Read `first` person
		 */
		Person first = people.get(0);
		Person repeatFirst = peoplePort.readPerson(first.getId());
		System.out.println(String.format(testTitle, testCase++,
				"Read the `first` person again.",
				"readPerson("+people.get(0).getId()+")",
				"#2"));
		printPersonWithMeasures(repeatFirst);
		
		/**********************************************************************
		 * T2:	Update `first` person's name and verify it was changed 
		 */
		PersonUpdate pUpdate = new PersonUpdate();
		pUpdate.setId(first.getId());
		pUpdate.setBirthdate((XMLGregorianCalendar) first.getBirthdate().clone());
		pUpdate.getBirthdate().setYear(pUpdate.getBirthdate().getYear()+1);
		pUpdate.setFirstname(new StringBuilder(first.getFirstname()).reverse().toString());
		pUpdate.setLastname(first.getLastname());
		Person responseP = peoplePort.updatePerson(pUpdate);
		
		System.out.println(String.format(testTitle, testCase++,
				"Change the `first` person's name and birthdate and check it was updated.",
				"updatePerson(Person<id: "+pUpdate.getId()+
				",firstname: "+pUpdate.getFirstname()+
				", lastname: "+pUpdate.getLastname()+
				", birthdate: "+complexBirthdateToString(pUpdate.getBirthdate())+">)",
				"#3"));
		System.out.println("Before:");
		printPerson(first);
		System.out.println("After:");
		printPerson(responseP);
		
		/**********************************************************************
		 * T3:   Read measureTypes
		 */
		MeasureTypes mTResponse = peoplePort.readMeasureTypes();
		List<MeasureType> measureTypes = mTResponse.getMeasureTypes();
		System.out.println(String.format(testTitle, testCase++,
				"Read all measure types.",
				"readMeasureTypes()",
				"#7"));
		printMeasureTypes(measureTypes);
		
		/**********************************************************************
		 * T4:	Read measure history
		 */
		String methods = ""; 
		for(int i=0; i<measureTypes.size(); i++){
			methods += "readPersonHistory("+first.getId()+", "+measureTypes.get(i).getMeasureType()+"), ";
		}
		System.out.println(String.format(testTitle, testCase++,
				"Read measure history of all types for `first` person.",
				methods.substring(0, methods.length()-2),
				"#6"));
		for(MeasureType mT: measureTypes){
			String targetMeasureType = mT.getMeasureType();
			HealthProfileHistory historyResponse = peoplePort.readPersonHistory(first.getId(), targetMeasureType);
			List<Measure> history = historyResponse.getHealthProfileHistory();
			printMeasures(history);
		}
		
		
		/**********************************************************************
		 * T3:	Create person "Chuck Norris" with Health Profile.
		 */
		PersonCreate pCreate = new PersonCreate();
		MeasureCreate mCreate = new MeasureCreate();
		mCreate.setMeasureType(measureTypes.get(measureTypes.size()-1).getMeasureType());
		mCreate.setMeasureValue("166");
		pCreate.getCurrentHealth().add(mCreate);
		pCreate.setFirstname("Chuck");
		pCreate.setLastname("Norris");
		GregorianCalendar c = new GregorianCalendar(1940, 3, 10);
		XMLGregorianCalendar birthdate;
		try {
			birthdate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			pCreate.setBirthdate(birthdate);
		} catch (DatatypeConfigurationException e) {
		}
		Person chuck = peoplePort.createPerson(pCreate);
		System.out.println(String.format(testTitle, testCase++,
				"Create person `Chuck Norris` with Health Profile.",
				"createPerson(Person<firstname: "+pCreate.getFirstname()
				+", lastname: "+pCreate.getLastname()
				+", birthdate: "+complexBirthdateToString(pCreate.getBirthdate())
				+", currentHealth: Measure<type: "
				+mCreate.getMeasureType()+", value: "+mCreate.getMeasureValue()+"> >)",
				"#4"));
		printPersonWithMeasures(chuck);
		
		/**********************************************************************
		 * T4:	Create a measure for "Chuck Norris"
		 */
		mCreate = new MeasureCreate();
		mCreate.setMeasureType(measureTypes.get(0).getMeasureType());
		mCreate.setMeasureValue("68");
		Measure chuckWeight = peoplePort.savePersonMeasure(chuck.getId(), mCreate);
		System.out.println(String.format(testTitle, testCase++,
				"Add a new measure for the newly created person (`Chuck Norris`).",
				"savePersonMeasure("+chuck.getId()+", Measure<type: "+mCreate.getMeasureType()+", value: "+mCreate.getMeasureValue()+">)",
				"#9"));
		printMeasure(chuckWeight);
		
		/**********************************************************************
		 * T5:	Update the measure's value
		 */
		MeasureUpdate mUpdate = new MeasureUpdate();
		mUpdate.setMeasureType(measureTypes.get(0).getMeasureType());
		mUpdate.setMeasureValue("78");
		mUpdate.setMid(chuckWeight.getMid());
		Measure updatedM = peoplePort.updatePersonMeasure(chuck.getId(), mUpdate);
		System.out.println(String.format(testTitle, testCase++,
				"Update the measure's value.",
				"updatePersonMeasure("+chuck.getId()+", Measure<mid: "+mUpdate.getMid()+", type: "+mUpdate.getMeasureType()+", value: "+mUpdate.getMeasureValue()+">)",
				"#10"));
		printMeasure(updatedM);
		
		/**********************************************************************
		 * T6:	Read measure to verify it was updated
		 */
		Measure readUpdatedM = peoplePort.readPersonMeasure(chuck.getId(), 
					updatedM.getMeasureType().getMeasureType(), updatedM.getMid());
		System.out.println(String.format(testTitle, testCase++,
				"Re-read the measure to make sure it was updated.",
				"readPersonMeasure("+chuck.getId()+", "+updatedM.getMeasureType().getMeasureType()+", "+updatedM.getMid()+")",
				"#8")); 
		printMeasure(readUpdatedM);
		
		/**********************************************************************
		 * T7:	Delete Chuck....if that is even possible.
		 */
		System.out.println(String.format(testTitle, testCase++,
				"Delete `Chuck Norris`",
				"deletePerson("+chuck.getId()+"), readPerson("+chuck.getId()+") to confirm it was deleted",
				"#5"));
		System.out.println("Deleting person...");
		boolean res = peoplePort.deletePerson(chuck.getId());
		if(res){
			System.out.println("\tPerson deleted successfully");
		}
		System.out.println("\nDouble-checking that person is not in db...");
		Person checkDelete = peoplePort.readPerson(chuck.getId());
		if(checkDelete == null){
			System.out.println("\tPerson with id `"+chuck.getId()+"` could not be found.");
		}
		
		//All tests ready
		System.out.println(clientEnd);
	}
	
	private static void printPeopleWithMeasures(List<Person> people){
		System.out.println(personJoinTableHeader);
		for(Person p: people){
			printPersonWithMeasuresRow(p);
		}
		System.out.println("");
	}
	
	private static void printPerson(Person p){
		String firstname = p.getFirstname();
		String lastname = p.getLastname();
		Long id = p.getId();
		XMLGregorianCalendar cal = p.getBirthdate();
		SimpleDateFormat mFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		mFormatter.setTimeZone(cal.toGregorianCalendar().getTimeZone());
		formatter.setTimeZone(cal.toGregorianCalendar().getTimeZone());
		String dateString = formatter.format(cal.toGregorianCalendar().getTime());

		System.out.println(personTableHeader);
		System.out.println(String.format(personTableRow, id, firstname, lastname, dateString));
		System.out.println("");
	}
	
	private static void printPersonWithMeasures(Person p){
		System.out.println(personJoinTableHeader);
		printPersonWithMeasuresRow(p);	
		System.out.println("");
	}
	
	private static void printPersonWithMeasuresRow(Person p){
		String firstname = p.getFirstname();
		String lastname = p.getLastname();
		Long id = p.getId();
		XMLGregorianCalendar cal = p.getBirthdate();
		SimpleDateFormat mFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		mFormatter.setTimeZone(cal.toGregorianCalendar().getTimeZone());
		formatter.setTimeZone(cal.toGregorianCalendar().getTimeZone());
		String dateString = formatter.format(cal.toGregorianCalendar().getTime());
		if(p.getCurrentHealth() == null || p.getCurrentHealth().isEmpty()){
			System.out.println(String.format(personJoinEmptyMeasureRow, id, firstname, lastname, dateString, "", "", "", ""));
		} else {
			for(Measure m : p.getCurrentHealth()){
				String mDate = mFormatter.format(m.getDateRegistered().toGregorianCalendar().getTime());
				System.out.println(String.format(personJoinRow, id, firstname, lastname, dateString, m.getMeasureType().getMeasureType(), m.getMeasureValue(), mDate, m.getMid()));
			}
		}
	}
	
	private static void printMeasureTypes(List<MeasureType> mTypes){
		System.out.println(measureTypesTableHeader);
		for(MeasureType m: mTypes){
			String name = m.getMeasureType();
			String valueType = m.getMeasureValueType();
			System.out.println(String.format(measureTypesTableRow, name, valueType));
		}
		System.out.println("");
	}
	
	private static void printMeasures(List<Measure> measures){
		System.out.println(measureTableHeader);
		for(Measure m: measures){
			printMeasureRow(m);
		}
		System.out.println("");
	}
	
	private static void printMeasure(Measure measure){
		System.out.println(measureTableHeader);
		printMeasureRow(measure);
		System.out.println("");
	}
	
	private static void printMeasureRow(Measure m){
		Long mid = m.getMid();
		String mType = m.getMeasureType().getMeasureType();
		String mValue = m.getMeasureValue();
		XMLGregorianCalendar cal = m.getDateRegistered();
		SimpleDateFormat mFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		String dateReg = mFormatter.format(cal.toGregorianCalendar().getTime());
		System.out.println(String.format(measureTableRow, mid, mType, mValue, dateReg));
	}
	
	private static String complexBirthdateToString(XMLGregorianCalendar greg){
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(greg.toGregorianCalendar().getTime());
	}
}
