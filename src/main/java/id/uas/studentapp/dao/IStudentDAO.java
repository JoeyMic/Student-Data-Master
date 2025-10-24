package id.uas.studentapp.dao;

import java.util.List;
import id.uas.studentapp.model.Student;

public interface IStudentDAO {
    java.util.List<Student> getAllStudents();
    boolean addStudent(Student s);
    boolean updateStudent(Student s);
    boolean deleteStudent(int id);
    boolean swapStudentEmails(int idA, int idB);
}
