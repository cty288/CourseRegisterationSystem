import java.io.Serializable;
import java.util.ArrayList;

import javax.jws.Oneway;


public class Course implements Serializable{
	//All required fields of a course class
	private String courseName;
	private String courseId;
	private int maxStudentNumber;
	private ArrayList<Student> students;
	private String instructorName;
	private int sectionNumber;
	private String location;
	private int currentStudentNumber;
	
	public Course() {}
	
	//set course name, id, max student number, instructor name, section number, and location when creating a course object
	public Course(String name, String id, int maxStudentNum, String instructor, int sectionNum,
			String location) {
		courseName=name;
		courseId=id;
		maxStudentNumber=maxStudentNum;
		this.sectionNumber=sectionNum;
		instructorName=instructor;
		this.location=location;
		students=new ArrayList<Student>();
		currentStudentNumber=0;
	}
	
	//getters and setters for the course's information. Only provides setters for the fields that allowed to change by the admin
	public String getCourseId() {
		return courseId;
	}
	
	public String getCourseName() {
		return courseName;
	}
	
	public int getMaxStudentNumber() {
		return maxStudentNumber;
	}
	
	public void setMaxStudentNumber(int maxStudentNumber) {
		this.maxStudentNumber = maxStudentNumber;
	}
	
	public String getInstructorName() {
		return instructorName;
	}
	
	public void setInstructorName(String instructorName) {
		this.instructorName = instructorName;
	}
	
	public int getSectionNumber() {
		return sectionNumber;
	}
	
	public void setSectionNumber(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public int getCurrentStudentNumber() {
		return currentStudentNumber;
	}
	//end of getters and setters
	
	
	
	//returns a String that contains all information of this course
	public String getCompleteCourseInformation() {
		return "Course Name: "+courseName+"\nCourse ID: "+courseId+"\nMaximum Number of Student: "
				+maxStudentNumber+"\nCurrent Registered Students Number: "+currentStudentNumber
				+"\nCourse Instructor: "+instructorName
				+"\nSection Number: "+sectionNumber+"\nCourse Location: "+location+"\nStudent Name List: \n"+getAllStudentInfo();
	}
	

	//returns a brief course information (only course name, course section, and course id)
	public String getBriefCourseInformation() {
		return this.courseName+"  "+this.courseId+"  "+"Section "+sectionNumber;
	}
	
	//returns a String containing a list of all students registered for this course
	public String getAllStudentInfo(){
		String nameList="";
		for (Student student : students) {
			nameList+=student.getInfo()+"\n";
		}
		return nameList;
	}
	
	//Returns true if successfully register a student, otherwise returns false
	public boolean register(Student student) {
		if(currentStudentNumber<maxStudentNumber) {
			students.add(student);
			currentStudentNumber++;
			return true;
		}
		return false;
	}
	
	//withdraw a student from this course
	public void withdraw(Student student) {
		students.remove(student);
		currentStudentNumber--;
	}
	
	//return a copy of this object
	public Course copy(){
		Course temp=new Course(this.courseName,this.courseId,this.maxStudentNumber,instructorName,
				sectionNumber,location);
		for (Student student : students) {
			temp.register(student);
		}
		return temp;
	}
	
	//return if this course is full
	public boolean isFull() {
		return currentStudentNumber>=maxStudentNumber;
	}
	
	//Once the course is deleted, all students who registered this course will be dropped from this course
	public void onDeleted() {
		for (Student student : students) {
			Course courseRemoved=null;
			Student studentInSystem=CourseSystem.findStudent(student.getFirstName(),student.getLastName());
			for (Course course : studentInSystem.getCoursesRegistered()) {
				if(course.getCourseName().equals(this.getCourseName())
						&& course.getSectionNumber()==this.sectionNumber) {
					//To avoid ConcurrentModificationException
					courseRemoved=course;
					break;
				}
			}
			if(courseRemoved!=null) {
				studentInSystem.getCoursesRegistered().remove(courseRemoved);			}
		}
	}
	
	//Once the course is edited, update the information in student's system
	public void onEdited() {
		for (Student student : students) {
			Course courseEdited=null;
			Student studentInSystem=CourseSystem.findStudent(student.getFirstName(),student.getLastName());
			for (Course course : studentInSystem.getCoursesRegistered()) {
				if(course.getCourseName().equals(this.getCourseName())
						&& course.getSectionNumber()==this.sectionNumber) {
					//To avoid ConcurrentModificationException
					courseEdited=course;
					break;
				}
			}
			if(courseEdited!=null) {
				//update
				studentInSystem.getCoursesRegistered().remove(courseEdited);
				studentInSystem.getCoursesRegistered().add(this);
			}
		}
	}
	
	//update the course arrayList in the CourseSystem when a student drops a course
	public void onDropped(Student student) {
		Course courseInSystem=CourseSystem.findCourse(this.courseName, this.sectionNumber);
		Student studentFind=null;
		for (Student stu : courseInSystem.students) {
			if(stu.getFirstName().equals(student.getFirstName())&&
					stu.getLastName().equals(student.getLastName())) {
				studentFind=stu;
				break;
			}
		}
		if(studentFind!=null) {
			courseInSystem.students.remove(studentFind);
		}
	}
}
