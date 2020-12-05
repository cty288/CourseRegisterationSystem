
public interface StudentMethods {
	//register a course
	public abstract void registerCourse(String courseName, int section);
	//Show a list of all courses and their detailed (declared in user class, override in Student class)
	public abstract void showAllCourses();
	//Show a list of courses that are not full
	public abstract void showAllAvailableCourses();
	//Withdraw from a specific course (since a student can't register multiple course with the same name, section number is not needed to withdrawl)
	public abstract void withdrawCourse(String courseName);
	//Show a list of course registered by the student
	public abstract void showRegisteredCourses();
	
}
