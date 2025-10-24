<<<<<<< HEAD
# Student-Data-Master
Project UAS Implementasi PBO by Joey Michael Ariska NIM 2211510801
=======
Student Management App - UAS (SQLite variant)
---------------------------------------------

This project is prepared for the UAS assignment. It's an SQLite-based Java Swing application with:
- CRUD operations
- Transaction example (swapStudentEmails)
- JasperReport export to PDF (range by student_id)
- Automatic screenshot captures saved to /captures on actions

Setup:
1. Java 11+, Maven installed.
2. Unzip project and open in your IDE (IntelliJ/Eclipse) or use command line.
3. The SQLite database file will be created automatically at `data/studentdb.db`
   or you can pre-create it and run `sql/create_student_table.sql` using sqlite3.
4. Put your logo and background image files into `src/main/resources/logo.png` and `src/main/resources/background.jpg`
   (placeholders are included — replace them with real images).
5. Build with: mvn clean package
6. Run with your IDE or: mvn exec:java -Dexec.mainClass="id.uas.studentapp.Main"

Files included in this ZIP:
- pom.xml
- sql/create_student_table.sql
- src/main/java/... (Java source files)
- src/main/resources/student_report.jrxml
- src/main/resources/logo.png (placeholder - replace)
- src/main/resources/background.jpg (placeholder - replace)
- captures/ (empty folder where screenshots will be saved)

Good luck!

>>>>>>> d05aa0e (Initial commit - Student Management App)
