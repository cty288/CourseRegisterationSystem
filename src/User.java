import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class User implements Serializable {
	//All fields for a user
	private String userName;
	private String password;
	private String firstName;
	private String lastName;
	
	transient private Scanner scanner=new Scanner(System.in);
	
	public User() {};
	//Constructor for assigning fields
	public User(String userName, String password, String firstName, String lastName) {
		this.userName=userName;
		this.password=password;
		this.firstName=firstName;
		this.lastName=lastName;
	}
	//Getters and setters for data fields
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	//return the full name of a user for convenience
	public String getFullName() {
		return firstName+" "+lastName;
	}
	
	//back to the menu
	protected abstract void showMenu();
	
	
	//Return the information of the user (User name + full name)
	public String getInfo() {
		return "Username: "+userName+"   Full Name: "+getFullName();
	}
	
	//Return if the input passward is the same as the student's password
	public boolean verifyPassword(String password) {
		return password.equals(this.password);
	}
	
	//print a message that instruct the user to input any key to return to the menu
	public  void backToMenuOption() {
		Scanner scanner=new Scanner(System.in);
		System.out.println("Input any key to return to the menu");
		//enter anything to return to the menu
		scanner.nextLine();
		showMenu();
		//Save the data everytime return to the menu
		CourseSystem.save();
	}
	
		//Print all course and show their information
		//A list of course name, course id, and section number will be showed.
		//The user  can select a course and show more details (enrolled student's names, enrolled student's ids, number of registered students, and maximum capacity of this course)
		public void showAllCourses() {	
			CourseSystem.drawBoldCuttingLine();
			//get a safe copy of all courses
			ArrayList<Course> courses=CourseSystem.getCoursesCopy();
			//print all courses
			for(int i=0;i<courses.size();i++) {
				System.out.println((i+1)+ "."+courses.get(i).getBriefCourseInformation());
			}
			//print an "exit option"
			System.out.println((courses.size()+1)+".Exit");
			CourseSystem.drawBoldCuttingLine();
			System.out.println("Above is a list of all courses. To show the detail for a specific course, \ninput the corrosponding option next to course names");
			//Get and validate an integer from input
			int option=0;
			boolean success=false;
			while(!success) {
				try {
					option=scanner.nextInt();
					
					//if the input is out of range
					if(option<1 || option>courses.size()+1) {
						System.out.println("The number is out of range, please input a option number between 1 and "+(courses.size()+1));
						continue;
					}
					success=true;
				} catch (Exception e) {
					//if the input is not a number
					System.out.println("Please input a proper number of the option!");
					scanner.next();
				}
			}
			
			//Show the complete course information
			if(option<=courses.size()) { 
				//user choose to show the detail of a specific course
				Course temp=courses.get(option-1);
				CourseSystem.drawBoldCuttingLine();
				System.out.println("Course Name: "+temp.getCourseName());
				System.out.println("Course ID: "+temp.getCourseId());
				System.out.println("Section Number: "+temp.getSectionNumber());
				System.out.println("Registered Student Number: "+temp.getCurrentStudentNumber());
				System.out.println("Maximum Student: "+temp.getMaxStudentNumber());
				CourseSystem.drawThinCuttingLine("Student List");
				System.out.println(temp.getAllStudentInfo());
				//Press any key to return to the menu
				CourseSystem.drawBoldCuttingLine();
				backToMenuOption();
			}else { //user choose to exit
				showMenu();
			}
		}
}
