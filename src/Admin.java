import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Admin extends User implements AdminMethods {
	Scanner scanner=new Scanner(System.in);
	
	
	//Constructors for initialize an admin
	public Admin() {
		super("Admin","Admin001","Admin","");
	}
	
	public Admin(String userName, String password, String firstName, String lastName) {
		super(userName,password,firstName,lastName);
	}
	
	
	//Show all Courses are moved to the User Class

	
	//print a list of all classes that are full
	@Override
	public void showFullCourses() {
		CourseSystem.drawBoldCuttingLine("Full Course List");
		int count=0;
		for (Course course : CourseSystem.getCoursesCopy()) {
			if(course.isFull()) {
				System.out.println(course.getBriefCourseInformation());
				count++;
			}
		}
		//No full courses in the system, just show something to the user
		if(count==0) {
			System.out.println("All courses are not full");
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}

	//Show the list of registered students for a specific course.
	@Override
	public void showCourseStudentNameList(String courseName, int section) {
		//Find the course from the given name and section
		Course course=CourseSystem.findCourse(courseName, section);
		CourseSystem.drawBoldCuttingLine();
		//if successfully find such a course
		if(course!=null) {
			System.out.println("Student list: ");
			System.out.println(course.getAllStudentInfo());
			CourseSystem.drawBoldCuttingLine();
			backToMenuOption();
		}else {
			System.out.println("Fail to find this course, please make sure you \ninputed the right course name and section number");
			CourseSystem.drawBoldCuttingLine();
			backToMenuOption();
		}
	}

	//Print a list of course registered by a specific student
	@Override
	public void showCoursesByStudent(String firstName, String lastName) {
		Student student=CourseSystem.findStudent(firstName, lastName);
		CourseSystem.drawBoldCuttingLine();
		if(student!=null) {
			ArrayList<Course> course=student.getCoursesRegistered();
			System.out.println("The student has registered: ");
			for (Course c : course) {
				System.out.println(c.getBriefCourseInformation());
			}
		}else {
			System.out.println("Failed to find the student, please make sure you inputed the correct name");
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	
	//Write to a file that includes all full courses
	//The file is named "full_courses.txt"
	@Override
	public void writeFullCourses() {
		CourseSystem.drawBoldCuttingLine();
		System.out.println("Writing full courses to \"full_courses.txt\"...");
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter("full_courses.txt"));
			for (Course course : CourseSystem.getCoursesCopy()) {
				if(course.isFull()) {
					writer.write(course.getBriefCourseInformation()+"\n");
				}
			}
			writer.close();
			System.out.println("Successfully wrote full courses!");
		} catch (IOException e) {
			System.out.println("Failed to write full courses "+e.toString());
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}

	//delete a course according to the given course name and section
	@Override
	public void deleteCourse(String courseName, int sectionNumber) {
		CourseSystem.drawBoldCuttingLine();
		Course course = CourseSystem.findCourse(courseName, sectionNumber);
		if(course!=null) {
			//drop this course for all students who registered this course
			course.onDeleted();
			//remove this course from the system
			CourseSystem.removeCourse(course);
			System.out.println("Successfully removed the course! The students who registered this course are automatically removed from this course");
		}else {
			System.out.println("Please make sure you inputed the correct course name and section number!");
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	
	//Create a new course in the course system. Return the course created.
	@Override
	public void createCourse(String name, String id, int maxStudentNum, String instructor, int sectionNum,
			String location) {
		Course course = null;
		//If there exists a course that has the same name with the inputed name
		//Assign the same id to the new course
		ArrayList<Course> sameNameCourseList = CourseSystem.findCourse(name);
		if(sameNameCourseList.size()>0) {
			//Find if the section number conflicts with the existing numbers
			if(CourseSystem.findCourse(name,sectionNum)!=null) {
				System.out.println("Sorry, the course name and section number conflict with an existing course, \nplease try again!");
				CourseSystem.drawBoldCuttingLine();
				backToMenuOption();
				return;
			}else {
				//The course's name is the same with existing courses, but section number is different
				//In this case, assign the same id to the new course, instead of assigning the id specified by the user
				String newCourseId=sameNameCourseList.get(0).getCourseId();
				System.out.println("There exist other classes with the same name as "+name+",\nso we have reassigned a Course ID for this course: "+newCourseId);
				System.out.println("...\n...\n...\n...");
				course=new Course(name,newCourseId,maxStudentNum,instructor,sectionNum,location);
			}
		}else {
			//Check if the input is valid
			if(name.equals("") || id.equals("") || instructor.equals("") || location .equals("")) {
				System.out.println("Invalid course info!");
				backToMenuOption();
				return;
			}
			course=new Course(name,id,maxStudentNum,instructor,sectionNum,location);
		}
		CourseSystem.addCourse(course);
		System.out.println("Successfully created the course! Below is the course info: ");
		CourseSystem.drawThinCuttingLine();
		System.out.println(course.getCompleteCourseInformation());
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}

	//Edit any information of the course, except for course id, section number, and name
	@Override
	public void editCourse(String courseName, int sectionNumber) {
		Course course = CourseSystem.findCourse(courseName, sectionNumber);
		CourseSystem.drawBoldCuttingLine();
		//if the course exists
		if(course!=null) {
			String input="";
			//stop editing when the user press 4
			while(true) {
				System.out.println("Please select an option by inputting the option number");
				System.out.println("1. Maximim student number\n2. Course instructor\n3. Course location\n4. Stop editing");
				CourseSystem.drawThinCuttingLine();
				input=scanner.nextLine();
				//break the loop when 4 is pressed
				if(input.equals("4")) { //stop editing
					break;
				}else if(input.equals("1")) { //change max student number
					System.out.println("Please input the new maximum student number: ");
					int number=Integer.parseInt(scanner.nextLine());
					//Make sure the max student number >= current student number
					if(course.getCurrentStudentNumber()<=number) {
						course.setMaxStudentNumber(number);
						System.out.println("maximum student number edited successfully!");
					}else {
						System.out.println("The maximum student number must be greater than or equal to the current registered student number!");
					}
					
				}else if(input.equals("2")) { //change instructor name
					System.out.println("Please input the new instructor name: ");
					course.setInstructorName(scanner.nextLine());
					System.out.println("instructor name edited successfully!");
				}else if(input.equals("3")) {//change location
					System.out.println("Please input the new course location: ");
					course.setLocation(scanner.nextLine());
					System.out.println("location edited successfully!");
				}else { //input the wrong key
					System.out.println("Please input the current option and try again!");
				}
				CourseSystem.drawThinCuttingLine();
			}
			System.out.println("Course edited successfully!\nThe new course information is: "+course.getCompleteCourseInformation());
			course.onEdited();
		}else { //if the course is not exist
			System.out.println("Please make sure you inputed the correct course name and section number!");
		}
		//update this course in student's system
		
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}

	@Override
	public void showCourseInfo(String courseId) {
		CourseSystem.drawBoldCuttingLine();
		ArrayList<Course> courses=CourseSystem.findCourseById(courseId);
		//There exists at least one course with this id
		if(courses!=null && courses.size()>0) {
			//only one course has this id
			if(courses.size()==1) {
				System.out.println("Below is the detailed information for this course: ");
				CourseSystem.drawThinCuttingLine("Course infomation");
				System.out.println(courses.get(0).getCompleteCourseInformation());
			}else { //More than one course have this id
				//First show a list of option
				for (int i=0; i<courses.size(); i++) {
					System.out.println((i+1)+"."+courses.get(i).getBriefCourseInformation());
				}
				CourseSystem.drawThinCuttingLine();
				System.out.println("Above is a list of courses found with this id,\nplease input an option to view details");
				try {
					//check if the input is valid
					int input=Integer.parseInt(scanner.nextLine());
					if(input>0 && input<=courses.size()) {
						//show detailed information
						CourseSystem.drawThinCuttingLine();
						System.out.println("Below is the detailed information for this course: ");
						System.out.println(courses.get(input-1).getCompleteCourseInformation());
					}else {
						System.out.println("The option is out of range!");
					}
				} catch (Exception e) {
					System.out.println("Please input a number!");
				}
			}
		}else { //Failed to find a course
			System.out.println("Failed to find this course, please make sure you inputed a correct course id!");
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	
	//Register a student to the course system
	@Override
	public void registerStudent() {
		CourseSystem.drawBoldCuttingLine();
		System.out.println("To register a student to the system, please enter the student's first name first: ");
		String first= scanner.nextLine();
		System.out.println("Please enter the last name: ");
		String last = scanner.nextLine();
		//Make sure the username does not conflict with others
		System.out.println("Please enter the username: ");
		String username = scanner.nextLine();
		
		if(CourseSystem.findStudent(username)==null) {
			System.out.println("Please enter the password: ");
			String pwd= scanner.nextLine();
			Student student=new Student(username,pwd,first,last);
			CourseSystem.addStudent(student);
			CourseSystem.drawThinCuttingLine();
			System.out.println("Successfully registered the student! Below is the information: \n");
			System.out.println(student.getInfo());
		}else {
			System.out.println("Sorry, the username conflicts with others. Please return to the menu and try again!");
		}
		
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	
	//Draw menu for admin
	@Override
	protected void showMenu() {
		CourseSystem.drawBoldCuttingLine("Menu");
		System.out.println("Welcome to the course registeration system!\n");
		System.out.println("1. Course Management\n2. View Reports\n3. Exit\n");
		CourseSystem.drawBoldCuttingLine();
		System.out.println("Please input an option: ");
		while(true) {
			String menuInput=scanner.nextLine();
			if(menuInput.equals("1")) { //Course Management panel
				showCourseManagementPanel();
				break;
			}else if(menuInput.equals("2")) { //View Reports Panel
				showReportsPanel();
				break;
			}else if(menuInput.equals("3")) { //Exit and save data
				CourseSystem.saveAndExit();
				break;
			}else {
				System.out.println("Please input a correct option! Try again: ");
			}
		}
	}
	
	//Open the menu for course management
	private void showCourseManagementPanel() {
		CourseSystem.drawBoldCuttingLine("Course Management Panel");
		System.out.println("\n1. Create a new course\n2. Delete a course\n3. Edit a course\n"+
		"4. Display Information for a course\n5. Register a student to the system\n6. Back to menu\n");
		CourseSystem.drawBoldCuttingLine();
		System.out.println("Please input an option: ");
		
		String input=scanner.nextLine();
		if(input.equals("1")) { //Create a new course
			try {
				System.out.println("Please input the name of the course: ");
				String name=scanner.nextLine();
				System.out.println("Please input the course ID: ");
				String courseId=scanner.nextLine();
				System.out.println("Please input the max number of student for this course: ");
				int max=Integer.parseInt(scanner.nextLine());
				System.out.println("Please input the instructor name: ");
				String instructor = scanner.nextLine();
				System.out.println("Please input the section number for this course: ");
				int section=Integer.parseInt(scanner.nextLine());
				System.out.println("Please input the location for this course: ");
				String location=scanner.nextLine();
				createCourse(name, courseId, max, instructor, section, location);
				
			} catch (Exception e) {
				CourseSystem.drawBoldCuttingLine("Failed to create the course");
				System.out.println("Please input a number for section number and maximum student number!");
				backToMenuOption();
			}
			
		}else if(input.equals("2")) { //Delete a course
			try {
				System.out.println("Please input the name of the course: ");
				String name=scanner.nextLine();
				System.out.println("Please input the section number for this course: ");
				int section=Integer.parseInt(scanner.nextLine());
				deleteCourse(name, section);
			} catch (Exception e) {
				CourseSystem.drawBoldCuttingLine("Failed to delete the course "+e.toString());
				backToMenuOption();
			}
			
		}else if(input.equals("3")) { //Edit a course
			try {
				System.out.println("Please input the name of the course: ");
				String name=scanner.nextLine();
				System.out.println("Please input the section number for this course: ");
				int section=Integer.parseInt(scanner.nextLine());
				editCourse(name, section);
			} catch (Exception e) {
				CourseSystem.drawBoldCuttingLine("Failed to edit the course");
				System.out.println("Please input a number for section number!");
				backToMenuOption();
			}
			
		}else if(input.equals("4")){ //Display info for a given course by course id
			System.out.println("To display information, please input the course ID: ");
			String id=scanner.nextLine();
			showCourseInfo(id);
		}else if(input.equals("5")) { //register a student to the system
			registerStudent();
		}else if(input.equals("6")) {
			showMenu();
		}else{
			System.out.println("Please input a correct option!");
			CourseSystem.drawBoldCuttingLine();
			backToMenuOption();
		}
	}
	
	//open the meny for reports
	private void showReportsPanel() {
		CourseSystem.drawBoldCuttingLine("Reports Panel");
		System.out.println("\n1. View all courses\n2. View all full courses\n3. Write to a file the list of full course\n"+
		"4. View names of the students registered in a course\n5. View the list of courses registered by a student\n6. Sort the courses based on the number of registered students\n7. Back to menu\n");
		CourseSystem.drawBoldCuttingLine();
		System.out.println("Please input an option: ");
		
		String input=scanner.nextLine();
		if(input.equals("1")) { //View all courses
			showAllCourses();
		}else if(input.equals("2")) { //view all full courses
			showFullCourses();
		}else if(input.equals("3")) { //Write a file includes all full courses
			writeFullCourses();
		}else if(input.equals("4")){ //view student list for a specific course
			try {
				System.out.println("Please input the name of the course: ");
				String name=scanner.nextLine();
				System.out.println("Please input the section number for this course: ");
				int section=Integer.parseInt(scanner.nextLine());
				showCourseStudentNameList(name, section);
			} catch (Exception e) {
				CourseSystem.drawBoldCuttingLine("Failed to show the course");
				System.out.println("Please input a number for section number!");
				backToMenuOption();
			}
		}else if(input.equals("5")) { //View list of courses registered by a student
			System.out.println("Please input the student's first name: ");
			String firstName=scanner.nextLine();
			System.out.println("Please input the student's last name: ");
			String lastName=scanner.nextLine();
			showCoursesByStudent(firstName, lastName);
			
		}else if(input.equals("6")) { //Sort courses
			sortCourses();
		}else if(input.equals("7")) {
			showMenu();
		}else{
			System.out.println("Please input a correct option!");
			CourseSystem.drawBoldCuttingLine();
			backToMenuOption();
		}
	}

	//Sort courses by student number, and print the sorted courses list
	@Override
	public void sortCourses() {
		CourseSystem.drawBoldCuttingLine();
		//Sort courses using CourseSystem's method, which returns a list of sorted courses
		ArrayList<Course> newCourses=CourseSystem.sortCoursesByStudentNumber();
		//Course list is not empty
		if(newCourses!=null) {
			for (Course course : newCourses) {
				System.out.println(course.getBriefCourseInformation()+"  Student number: "+course.getCurrentStudentNumber());
			}
			CourseSystem.drawThinCuttingLine();
			System.out.println("Successfully sorted! Above is the new list of courses");
		}else {
			System.out.println("There is no course in the system!");
		}
		CourseSystem.drawBoldCuttingLine();
		backToMenuOption();
	}
	

}
