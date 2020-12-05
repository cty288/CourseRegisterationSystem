import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.plaf.FontUIResource;

//A class responsible to store and search all registered students, courses
//Also responsible for serialization and deserialization
public class CourseSystem {
	private static Scanner scanner=new Scanner(System.in);
	//File Location
	private static String courseCSVLocation="MyUniversityCourses.csv";
	//All registered students
	private static ArrayList<Student> students = new ArrayList<Student>();
	//Stores all courses
	private static ArrayList<Course> courses = new ArrayList<Course>();
	
	//If the current user is a student, save the Student object here.
	private static Student currentUser=null;
	
	/*Initialize the program (read file)
	 * If the program is first used, read the csv file
	 * Otherwise, read the .ser file
	 */
	public static void Init() {
		File courseFile=new File("courses.ser");
		File studentFile=new File("students.ser");
		//If they exist, read them
		if(courseFile.exists()&&studentFile.exists()) {
		      
			try {
				 FileInputStream courseStream = new FileInputStream("courses.ser");
				 ObjectInputStream courseInputStream = new ObjectInputStream(courseStream);
			      
			      FileInputStream studentStream = new FileInputStream("students.ser");
			      ObjectInputStream studentInputStream = new ObjectInputStream(studentStream);
			      
			      students=(ArrayList<Student>)studentInputStream.readObject();
			      courses=(ArrayList<Course>)courseInputStream.readObject();  
				
			} catch (FileNotFoundException e) {
				System.out.println("Failed to read file "+e.toString());
			} catch (IOException e) {
				System.out.println("Failed to read file "+e.toString());
			} catch (ClassNotFoundException e) {
				System.out.println("Failed to read file "+e.toString());
			}
		         
		}else {
			//otherwise read from csv
			readCourses();
		}
		login();
	}
	
	
	
	//Login to the system, and show the menu
	public static void login() {
		drawBoldCuttingLine();
		System.out.println("Welcome to the course registeration system\nIf you are an administrator - please input 1\nIf you are a student - please input 2 ");
		String userChoose=scanner.nextLine();
		while(!((userChoose.equals("1")) || (userChoose.equals("2")))) {
			System.out.println("Please input a valid key!");
			userChoose=scanner.nextLine();
		}
		//admin login
		if(userChoose.equals("1")) {
			while(true) {
				drawThinCuttingLine();
				System.out.println("Please input your username: ");
				String name = scanner.nextLine();
				System.out.println("Please input your password: ");
				String pwd=scanner.nextLine();
				if(name.equals("Admin") && pwd.equals("Admin001")) {
					break;
				}
				System.out.println("Incorrect username or password!");
			}
			drawThinCuttingLine();
			Admin admin=new Admin();
			System.out.println("Login success!");
			drawBoldCuttingLine();
			admin.showMenu();

		}else { //student login
			Student student=null;
			while(true) {
				System.out.println("Please input your username: ");
				String name = scanner.nextLine();
				System.out.println("Please input your password: ");
				String pwd=scanner.nextLine();
				//Find the student
				Student studentFind=CourseSystem.findStudent(name);
				if(studentFind!=null) {
					//Verify that the password is correct
					if(studentFind.verifyPassword(pwd)) {
						//correct password
						student=studentFind;
						break;
					}else {
						//incorrect password
						System.out.println("Incorrect username or password!");
					}
				}else {
					//failed to find the user
					System.out.println("Incorrect username or password!");
				}
			}
			//Login success (if the program runs here, login must be success)
			currentUser=student;
			drawThinCuttingLine();
			System.out.println("Login Success!");
			drawBoldCuttingLine();
			currentUser.showMenu();
			
		}
	}
	
	//Get courses list (safe copy)
	public static ArrayList<Course> getCoursesCopy(){
		ArrayList<Course> output=new ArrayList<Course>();
		for (Course course : courses) {
			output.add(course.copy());
		}
		return output;
	}
	
	//Add a student to the list of all students
	public static void addStudent(Student student) {
		students.add(student);
	}
	
	//A method to specify the location of the csv file
	public static void setCourseCSVLocation(String courseCSVLocation) {
		CourseSystem.courseCSVLocation = courseCSVLocation;
	}
	
	//Add a course to the list of all courses
	public static void addCourse(Course course) {
		courses.add(course);
	}
	
	//The initialization of the program when first used. 
	//Load the course csv into the courses arrayList
	public static void readCourses() { 
		//read course csv
		try {
			String data="";
			BufferedReader reader=new BufferedReader(new FileReader(courseCSVLocation));
			//The first line is heading, so skip the first line and start reading from the second line
			reader.readLine();
			//format: Course_Name,Course_Id,Maximum_Students,Current_Students,List_Of_Names,Course_Instructor,Course_Section_Number,Course_Location
			//read the csv line by line and create corresponding Course object
			while(((data=reader.readLine())!=null)) { //read until nothing to read
				int nextIndex=0;
				int lastIndex=0;
				ArrayList<String> dataFields=new ArrayList<String>();
				//Find next element between commas and extract it and put it into the array list
				while((nextIndex=data.indexOf(",",nextIndex+1))!=-1) { //has next element
					dataFields.add(data.substring(lastIndex,nextIndex)); //extract the data between commas
					lastIndex=nextIndex+1; //the next element will start at the first letter after the comma;
				}
				//get the last element (location)
				dataFields.add(data.substring(lastIndex));
				//Assign the data stored in the arrayList into a Course object
				Course course=new Course(dataFields.get(0),dataFields.get(1),Integer.parseInt(dataFields.get(2)),
						dataFields.get(5),Integer.parseInt(dataFields.get(6)),dataFields.get(7));
				courses.add(course);
			}
			reader.readLine();
		}catch (FileNotFoundException e) { //File not found exception catch
			System.out.println("Failed to find the file");
		} catch (IOException e) { //failed to read exception catch
			System.out.println("Failed to read the file");
		}
	}
	
	
	
	
	//draw bold and thin cutting line ("==================")
	public static void drawBoldCuttingLine() {
		System.out.println("===============================================");
	}
	public static void drawBoldCuttingLine(String heading) {
		System.out.println("================"+heading+"====================");
	}
	
	public static void drawThinCuttingLine() {
		System.out.println("----------------------------------");
	}
	
	public static void drawThinCuttingLine(String heading) {
		System.out.println("-----------"+heading+"-------------");
	}
	

	
	//Find a Course object according to the given course name and section number
	public static Course findCourse(String courseName, int section) {
		//Find all courses that have the same courseName
		ArrayList<Course> list= findCourse(courseName);
		
		//Find the course with the specific section number
		if(list.size()>0) {
			for (Course course : list) {
				if(course.getSectionNumber() == section) {
					return course;
				}
			}
		}
		//If there is no such a course, just return null
		return null;
	}
	
	//Find all Courses that has the same name, and return the list
	public static ArrayList<Course> findCourse(String courseName){
		ArrayList<Course> list=new ArrayList<Course>();
		//Find all courses that have the same courseName
		for (Course course : courses) {
			if(course.getCourseName().equals(courseName)) {
				list.add(course);
			}
		}
		return list;
	}
	
	//Find a Course object according to the given id and section number
		public static Course findCourseById(String id, int section) {
			//Find all courses that have the same id
			ArrayList<Course> list= findCourseById(id);
			
			//Find the course with the specific section number
			if(list.size()>0) {
				for (Course course : list) {
					if(course.getSectionNumber()==section) {
						return course;
					}
				}
			}
			//If there is no such a course, just return null
			return null;
		}
		
		//Find all Courses that has the same name, and return the list
		public static ArrayList<Course> findCourseById(String id) {
			ArrayList<Course> list=new ArrayList<Course>();
			//Find all courses that have the same id+
			for (Course course : courses) {
				if(course.getCourseId().equals(id)) {
					list.add(course);
				}
			}
			return list;
		}
		
		//Find a Student object, given the full name.
		//Return the Student object found
		public static Student findStudent(String firstName, String lastName) {
			for (Student student : students) {
				if(student.getFullName().equals(firstName+" "+lastName)) {
					return student;
				}
			}
			return null;
		}
		
		//Find a student object, given the student id; return the Student object found
		//Each student has a unique id
		public static Student findStudent(long id) {
			for (Student student : students) {
				if(student.getStudentId()==id) {
					return student;
				}
			}
			return null;
		}
		
		//Find a student using username
		public static Student findStudent(String userName) {
			
			for (int i = 0; i < students.size(); i++) {
				if(students.get(i).getUserName().equals(userName)) {
					return students.get(i);
				}
			}
			return null;
		}
		
		//remove a Course object from the list of all courses
		public static void removeCourse(Course course) {
			courses.remove(course);
		}
		
		//Sort "courses" data field by student number, and return this field
		public static ArrayList<Course> sortCoursesByStudentNumber(){
			//if there's no course in the list
			if(courses.size()==0) {
				return null;
			}
			//bubble sort
			for(int i=0; i<courses.size()-1; i++) {
				for(int j=0; j<courses.size()-1-i;j++) {
					if(courses.get(j).getCurrentStudentNumber()<courses.get(j+1).getCurrentStudentNumber()) {
						Course temp = courses.get(j);
						courses.set(j, courses.get(j+1));
						courses.set(j+1, temp);
					}
				}
			}
			return courses;
		}
		

		
		//Save all data and exit the program
		public static void saveAndExit() {
			System.out.println("Saving data...");
			save();
			System.out.println("See you next time!");
			//Exit the program
			System.exit(0);
		}
		
		//save the program
		public static void save() {
			try {
				//Serialize courses and students list
				FileOutputStream studentStream=new FileOutputStream("students.ser");
				FileOutputStream courseStream=new FileOutputStream("courses.ser");
				ObjectOutputStream studentObjectOutputStream=new ObjectOutputStream(studentStream);
				ObjectOutputStream courseObjectOutputStream=new ObjectOutputStream(courseStream);
				studentObjectOutputStream.writeObject(students);
				courseObjectOutputStream.writeObject(courses);
				studentObjectOutputStream.close();
				courseObjectOutputStream.close();
				studentStream.close();
				courseStream.close();
			} catch (FileNotFoundException e) {
				System.out.println("Failed to save data! "+e.toString());
			} catch (IOException e) {
				System.out.println("Failed to save data! "+e.toString());
			}
		}

}
