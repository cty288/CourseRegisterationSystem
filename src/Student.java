import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.omg.CORBA.TypeCodePackage.BadKind;

public class Student extends User implements StudentMethods, Serializable {
	private long studentId;
	
	//a list saves all course registered by the student (safe copy)
	private ArrayList<Course> coursesRegistered=new ArrayList<Course>();
	
	transient private Scanner scanner=new Scanner(System.in);
	//Constructors
	public Student() {};
	public Student(String name, String pwd, String firstName, String lastName) {
		super(name,pwd,firstName,lastName);
		coursesRegistered=new ArrayList<Course>();
		studentId=generateId();
	}
	
	//return student id
	public long getStudentId() {
		return studentId;
	}
	
	/*Overridden from User class:
	 * While an admin can see the details of all courses, a student can only see
	 * course names, id, and section number
	 */
	@Override
	public void showAllCourses() {
		CourseSystem.drawBoldCuttingLine();
		//get a safe copy of all courses
		ArrayList<Course> courses=CourseSystem.getCoursesCopy();
		//print all courses
		for(int i=0;i<courses.size();i++) {
			System.out.println((i+1)+ "."+courses.get(i).getBriefCourseInformation());
		}
		CourseSystem.drawThinCuttingLine();
		System.out.println("Above is a list of all courses");
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	
	//generate a random id for the student, and avoid conflict with other students
	private long generateId() {
		Random random=new Random(System.currentTimeMillis());
		//generate a random id from 0-9999
		long id=random.nextInt(10000);
		//if the id conflicts with another student, generate a new id
		while(CourseSystem.findStudent(id)!=null) {
			id=random.nextInt(10000);
		}
		return id;
	}
	
	/*register a course for the student.
	 * The student can only register a course that is not full
	 * Additionally, the student can only register one course with the same name
	 * Student needs to verify his/her name before registering
	 */
	@Override
	public void registerCourse(String courseName, int section) {
		CourseSystem.drawBoldCuttingLine();
		Course course=CourseSystem.findCourse(courseName, section);
		boolean isConflict=false;
		//If this course exists
		if(course!=null) {
			//Make sure the student hasn't registered the same course before
			for (Course c : coursesRegistered) {
				if(c.getCourseName().equals(courseName)) {
					System.out.println("You can't register the same course twice!");
					isConflict=true;
				}
			}
			if(!isConflict) {
				//The course should not be full
				if(course.getCurrentStudentNumber()<course.getMaxStudentNumber()) {
					System.out.println("You are able to register this course, confirm your name to register: ");
					String name = scanner.nextLine();
					//Register the course if the name is correct
					if(name.equals(this.getFullName())) {
						course.register(this);
						coursesRegistered.add(course);
						System.out.println("Successfully registered "+courseName+" section "+section+"!");
					}else {
						System.out.println("Sorry, the name is incorrect. Please return to the menu and try again!");
					}
				}else {
					System.out.println("Sorry, this course is already full!");
				}
			}
		}else {
			System.out.println("Unable to find this course, please make sure you inputted the correct course");
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	
	//The menu of the student
	@Override
	protected void showMenu() {
		CourseSystem.drawBoldCuttingLine("Menu");
		System.out.println("Welcome to the course registeration system!");
		System.out.println("Name: "+this.getFullName()+"   Student ID: "+this.getStudentId()+"   Username: "+this.getUserName()+"\n");
		System.out.println("1. View all courses\n2. View all available courses (not full)\n3. Register a course\n4. Withdraw a course\n5. View my courses\n6. Exit");
		CourseSystem.drawBoldCuttingLine();
		System.out.println("Please input an option: ");
		scanner=new Scanner(System.in);
		while(true) {
			String menuInput=scanner.nextLine();
			if(menuInput.equals("1")) { //Show all courses
				showAllCourses();
				break;
			}else if(menuInput.equals("2")) { //View courses that are not full
				showAllAvailableCourses();
				break;
			}else if(menuInput.equals("3")) { //Register a course
				try {
					System.out.println("Please input the name of the course: ");
					String name=scanner.nextLine();
					System.out.println("Please input the section number for this course: ");
					int section=Integer.parseInt(scanner.nextLine());
					registerCourse(name, section);
				} catch (Exception e) {
					CourseSystem.drawBoldCuttingLine("Failed to register the course");
					System.out.println("Please input a number for section number!");
					backToMenuOption();
				}
			}else if(menuInput.equals("4")) { //Withdraw a course
				System.out.println("Please input the name of the course: ");
				String name=scanner.nextLine();
				withdrawCourse(name);
			}else if(menuInput.equals("5")) { //Show all courses registered by the student
				showRegisteredCourses();
			}else if(menuInput.equals("6")) { //Exit and save data
				CourseSystem.saveAndExit();
				break;
			}else {
				System.out.println("Please input a correct option! Try again: ");
			}
		}
	}
	
	//return a list of course registered by the student
	public ArrayList<Course> getCoursesRegistered() {
		return coursesRegistered;
	}
	
	//returns the information of a student: full name and student id
	@Override
	public String getInfo() {
		return "Name: "+getFullName()+"  ID: "+studentId;
	}
	
	//Print a list of courses that are not full
	@Override
	public void showAllAvailableCourses() {
		ArrayList<Course> courses= CourseSystem.getCoursesCopy();
		CourseSystem.drawBoldCuttingLine();
		for(int i=0;i<courses.size();i++) {
			if(courses.get(i).getCurrentStudentNumber()<courses.get(i).getMaxStudentNumber()) {
				//Print available course infomation, current student number, and maximum student number
				System.out.println((i+1)+"."+courses.get(i).getBriefCourseInformation()+
						"  Registered students: "+courses.get(i).getCurrentStudentNumber()+"/"+courses.get(i).getMaxStudentNumber());
			}
		}
		CourseSystem.drawThinCuttingLine();
		System.out.println("Above is the list of courses that are not full");
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	
	//Withdraw a course given the course name. Students are required to enter their names before withdrawing
	@Override
	public void withdrawCourse(String courseName) {
		CourseSystem.drawBoldCuttingLine();
		boolean isFind=false;
		Course courseFind = null;
		//Find if this course is registered by the student
		for (Course course : coursesRegistered) {
			if(course.getCourseName().equals(courseName)) {
				isFind=true;
				courseFind=course;
				break;
			}
		}
		//if the course is found, verify the student's name
		if(isFind) {
			System.out.println("Please enter your name to varify: ");
			String name=scanner.nextLine();
			if(name.equals(this.getFullName())) {
				//Withdrawl
				this.coursesRegistered.remove(courseFind);
				courseFind.withdraw(this);
				System.out.println("Successfully dropped this course!");
				courseFind.onDropped(this);
			}else {
				System.out.println("Sorry, the name is incorrect, please return to the menu and try again!");
			}
		}else {
			System.out.println("Sorry, we are unable to find this course, please make sure you've registered this course \nor entered the correct course name!");
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	
	//Print a list of courses registered by the student
	@Override
	public void showRegisteredCourses() {
		CourseSystem.drawBoldCuttingLine();
		//Make sure the student has registered at least one course
		if(coursesRegistered.size()>0) {
			for (int i = 0; i < coursesRegistered.size(); i++) {
				System.out.println((i+1)+"."+coursesRegistered.get(i).getBriefCourseInformation()+"  Location: "+coursesRegistered.get(i).getLocation()+"  Instructor: "+coursesRegistered.get(i).getInstructorName());
			}
			CourseSystem.drawThinCuttingLine();
			System.out.println("Above is the list of all courses you have registered.");
		}else {
			System.out.println("You haven't registered any courses yet.");
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	

}
