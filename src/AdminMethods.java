
public interface AdminMethods {
	//Print all courses and their information (declared in User class)
	public abstract void showAllCourses();
	//Print all courses that are already full
	public abstract void showFullCourses();
	//Print a list of student name for a course
	public abstract void showCourseStudentNameList(String courseName, int section);
	//Print all courses registered by a specific student
	public abstract void showCoursesByStudent(String firstName, String LastName);
	//Write to a file listing all full courses
	public abstract void writeFullCourses();
	//Delete a course, return the course deleted
	public abstract void deleteCourse(String courseName, int sectionNumber);
	//Create a new course
	public abstract void createCourse(String name, String id, int maxStudentNum, String instructor, int sectionNum,
			String location);
	//Edit a course, return the course edited
	public abstract void editCourse(String courseName, int sectionNumber);
	//Show information for a given course
	public abstract void showCourseInfo(String courseId);
	//register a student
	public abstract void registerStudent();
	//sort courses based on the number of registered students
	public abstract void sortCourses();
}
