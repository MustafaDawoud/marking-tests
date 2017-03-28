package ca.uwo.eng.se2205b.lab2.model;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MarkingTests.StudentTests.class,
        MarkingTests.CourseTests.class,
        MarkingTests.DepartmentTest.class
})
public class MarkingTests {


    /**
     * Update this class to work with the student's Student interface
     */
    public static class WrappedStudent {

        private Student base;

        WrappedStudent(@Nonnull Student base) {
            // Adjust this constructor to properly initialize a student :(
            this.base = base;
        }

        WrappedStudent(String firstName, String lastName, long studentId, @Nullable Department department) {
            // Adjust this constructor to properly initialize a student :(
            this.base = new RealStudent(firstName, lastName, studentId, department);
        }

        Student getBase() {
            return this.base;
        }

        String getFirstName() {
            // Get the first name of the student
            return base.getFirstName();
        }


        void setFirstName(@Nonnull String name) {
            // Set the first name
            base.setFirstName(name);
        }

        String getLastName() {
            // Last name of the student
            return base.getLastName();
        }

        void setLastName(@Nonnull String name) {
            // Set the last name
            base.setLastName(name);
        }

        /**
         * Get the student's ID
         * @return
         */
        long getStudentId() {
            return base.getStudentId();
        }

        List<Course> getCourses() {
            return base.getCourses();
        }

        void addCourse(Course newCourse) {

            base.addCourse(newCourse);
        }

        Course dropCourse(Course newCourse) {

            return base.dropCourse(newCourse);
        }

        Department getDepartment() {
            return base.getDepartment();
        }

        void setDepartment(@Nullable Department newDept) {
            base.setDepartment(newDept);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Student) {
                return Objects.equals(this.base, obj);
            } else if (obj instanceof WrappedStudent) {
                return Objects.equals(this.base, ((WrappedStudent) obj).base);
            } else {
                return false;
            }
        }
    }

    public static class Model {

        @Rule
        public ErrorCollector collector = new ErrorCollector();

        private final List<Department> departments;

        protected Department cee;
        protected Department ece;
        protected Department am;

        protected Course es1036;
        protected Course se2205;

        protected Course es1022;

        protected Course am1413;
        protected final List<Course> courses;

        protected WrappedStudent jsmith;
        protected WrappedStudent smclach;
        protected WrappedStudent gwilder;
        protected WrappedStudent rweasle;
        protected WrappedStudent mpham;
        protected WrappedStudent gtakei;
        protected WrappedStudent rnader;
        protected WrappedStudent jtarzan;

        private final List<WrappedStudent> students;

        Model() {
            departments = ProvidedModelFactory.createModel();

            loadDepartments();
            loadCourses();
            loadStudents();

            courses = Stream.of(es1036, se2205, es1022, am1413)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            students = Stream.of(jsmith, smclach, gwilder, rweasle, mpham, gtakei, rnader, jtarzan)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        protected WrappedStudent getStudent(Predicate<WrappedStudent> check) {
            return students.stream().filter(check)
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Could not find Student for criteria"));
        }

        protected Department getDepartment(Predicate<Department> check) {
            return departments.stream().filter(check)
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Could not find Department for criteria"));
        }

        protected Course getCourse(Predicate<Course> check) {
            return courses.stream().filter(check)
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Could not find Course for criteria"));
        }

        private static <T> Matcher<T> notNull() {
            return (Matcher<T>)not(is(nullValue()));
        }

        private void loadDepartments() {
            assertNotNull("Could not load departments from ProvidedModelFactory#createModel()", departments);

            for (Department d: departments) {
                String lowerName = d.getName().toLowerCase().trim();

                if (lowerName.contains("ece") || lowerName.contains("electric")) {
                    ece = d;
                } else if (lowerName.contains("cee") || lowerName.contains("civil") || lowerName.contains("environ")) {
                    cee = d;
                } else if (lowerName.contains("am") || lowerName.contains("applied") || lowerName.contains("math")) {
                    am = d;
                } else {
                    collector.checkThat("WARNING: Found unknown department: " + d + ", check spelling of names.", d, not(is(nullValue())));
                }
            }

            collector.checkThat("WARNING: Could not find ECE department in model", ece, notNull());
            collector.checkThat("WARNING: Could not find CEE department in model", cee, notNull());
            collector.checkThat("WARNING: Could not find AM department in model", am, notNull());
        }

        protected <T> List<T> loadList(String message, List<T> from) {
            if (message != null) {
                collector.checkThat("WARNING: " + message, from, not(is(nullValue())));
            } else {
                collector.checkThat("WARNING: Found List that should be empty, not null", from, not(is(nullValue())));
            }
            if (from == null) {
                return Collections.emptyList();
            } else {
                return from;
            }
        }

        protected <T> List<T> loadList(List<T> from) {
            return loadList(null, from);
        }

        private void loadCourses() {

            // Try and load them from the current departments:
            Map<String, Function<Course, Course>> loadMap = new HashMap<>(4);
            loadMap.put("ES1036", c -> es1036 = c);
            loadMap.put("SE2205", c -> se2205 = c);
            loadMap.put("ES1022", c -> es1022 = c);
            loadMap.put("AM1413", c -> am1413 = c);

            for (Department d: departments) {
                List<Course> courses = loadList("Department returned null courses, not empty.", d.getCourses());

                for (Course c: courses) {
                    Function<Course, Course> loader = loadMap.get(c.getCourseCode().toUpperCase().trim().substring(0, 6));
                    collector.checkThat("WARNING: Unknown course found: " + c, loader, not(is(nullValue())));
                    if (loader != null) {
                        loader.apply(c);
                    }
                }
            }

            if (es1022 == null || es1036 == null || se2205 == null || am1413 == null) {
                // Fallback to the student's and their courses..

                for (Department d : departments) {
                    List<Student> students = loadList("Department returned null students List, not empty.",
                            d.getEnrolledStudents());

                    for (Student s: students) {
                        WrappedStudent ws = new WrappedStudent(s);

                        for (Course c: ws.getCourses()) {
                            Function<Course, Course> loader = loadMap.get(c.getCourseCode().toUpperCase().trim().substring(0, 6));
                            collector.checkThat("WARNING: Unknown course found: " + c, loader, not(is(nullValue())));
                            if (loader != null) {
                                loader.apply(c);
                            }
                        }
                    }
                }
            }

            collector.checkThat("WARNING: Could not find ES1022 in model", es1022, not(is(nullValue())));
            collector.checkThat("WARNING: Could not find ES1036 in model", es1036, not(is(nullValue())));
            collector.checkThat("WARNING: Could not find SE2205 in model", se2205, not(is(nullValue())));
            collector.checkThat("WARNING: Could not find AM1413 in model", am1413, not(is(nullValue())));
        }

        private void loadStudents() {
            Map<Long, Function<WrappedStudent, WrappedStudent>> loadMap = new HashMap<>(8);
            loadMap.put(1111L, s -> jsmith = s);
            loadMap.put(2222L, s -> smclach = s);
            loadMap.put(3333L, s -> gwilder = s);
            loadMap.put(4444L, s -> rweasle = s);
            loadMap.put(5555L, s -> mpham = s);
            loadMap.put(6666L, s -> gtakei = s);
            loadMap.put(7777L, s -> rnader = s);
            loadMap.put(8888L, s -> jtarzan = s);

            for (Department d: departments) {
                List<Student> students = loadList("Department returned null students List, not empty.",
                        d.getEnrolledStudents());

                for (Student s: students) {
                    WrappedStudent ws = new WrappedStudent(s);
                    Function<WrappedStudent, WrappedStudent> loader = loadMap.get(ws.getStudentId());
                    collector.checkThat("WARNING: Unknown student found: " + s, loader, not(is(nullValue())));
                    if (loader != null) {
                        loader.apply(ws);
                    }
                }
            }

            List<WrappedStudent> all = Arrays.asList(jsmith, smclach, gwilder, rweasle, mpham, gtakei, rnader, jtarzan);
            if (all.contains(null)) {
                System.err.println("Students were not properly placed into departments, thus were not all initialized, falling back to courses.");

                for (Department d: departments) {
                    List<Course> courses = loadList("Department returned null courses List, not empty.",
                            d.getCourses());
                    for (Course c : courses) {
                        List<Student> students = loadList("Course returned null students List, not empty.",
                                c.getEnrolledStudents());
                        for (Student s: students) {
                            WrappedStudent ws = new WrappedStudent(s);
                            Function<WrappedStudent, WrappedStudent> loader = loadMap.get(ws.getStudentId());
                            collector.checkThat("WARNING: Unknown student found: " + s, loader, not(is(nullValue())));
                            if (loader != null) {
                                loader.apply(ws);
                            }
                        }
                    }
                }
            }

            collector.checkThat("WARNING: jsmith not found", jsmith, not(is(nullValue())));
            collector.checkThat("WARNING: smclach not found", smclach, not(is(nullValue())));
            collector.checkThat("WARNING: gwilder not found", gwilder, not(is(nullValue())));
            collector.checkThat("WARNING: rweasle not found", rweasle, not(is(nullValue())));
            collector.checkThat("WARNING: mpham not found", mpham, not(is(nullValue())));
            collector.checkThat("WARNING: gtakei not found", gtakei, not(is(nullValue())));
            collector.checkThat("WARNING: rnader not found", rnader, not(is(nullValue())));
            collector.checkThat("WARNING: jtarzan not found", jtarzan, not(is(nullValue())));
        }


    }

    static <T> void assertContains(Collection<T> expected, Collection<? extends T> actual) {
        for (T e: expected) {
            assertTrue("Could not find " + e + " in " + actual, actual.contains(e));
        }

        assertEquals("Collections to not match in any order:\n" + expected + "\n\t!=\n" + actual, expected.size(), actual.size());
    }


    public static class StudentTests extends Model {

        @Test
        public void names() {
            WrappedStudent jsmith = getStudent(_i -> true);

            assertEquals("First name incorrect", "John", jsmith.getFirstName());
            assertEquals("Last name incorrect", "Smith", jsmith.getLastName());

            jsmith.setFirstName("Bob");
            assertEquals("Could not change First name", "Bob", jsmith.getFirstName());

            jsmith.setLastName("Bob");
            assertEquals("Could not change Last name", "Bob", jsmith.getLastName());
        }

        @Test
        public void courses() {
            WrappedStudent student = getStudent(_i -> true);
            WrappedStudent otherStudent = getStudent( Predicate.<WrappedStudent>isEqual(student).negate() );

            Course notEnrolledCourse = getCourse(c -> !loadList(student.getCourses()).contains(c));

            try {
                student.getCourses().add(notEnrolledCourse);
                fail("Able to modify courses outside");
            } catch (UnsupportedOperationException uoe) {
                // expected
            }

            List<Course> previous = new ArrayList<>(student.getCourses());
            previous.add(notEnrolledCourse);

            List<Student> c_prevStudents = new ArrayList<>(notEnrolledCourse.getEnrolledStudents());
            c_prevStudents.add(student.getBase());

            student.addCourse(notEnrolledCourse);

            assertContains(previous, student.getCourses());
            assertContains(c_prevStudents, notEnrolledCourse.getEnrolledStudents());

            // No change:
            student.addCourse(notEnrolledCourse);

            assertContains(previous, student.getCourses());
            assertContains(c_prevStudents, notEnrolledCourse.getEnrolledStudents());

            student.dropCourse(notEnrolledCourse);
            previous.remove(notEnrolledCourse);
            c_prevStudents.remove(student.getBase());

            assertContains(previous, student.getCourses());
            assertContains(c_prevStudents, notEnrolledCourse.getEnrolledStudents());
        }

        @Test
        public void equals() {
            WrappedStudent student = getStudent(_i -> true);
            WrappedStudent otherStudent = getStudent( s -> s != student );

            assertFalse(student.equals(otherStudent));

            WrappedStudent checkStudent = new WrappedStudent("Bob",
                    "Builder",
                    4444,
                    am);
            checkStudent.addCourse(se2205);

            WrappedStudent copyCat = new WrappedStudent("Bob",
                    "Builder",
                    4444,
                    am);
            copyCat.addCourse(se2205);

            assertEquals("Two identical students are not equal", checkStudent, copyCat);
        }

        @Test
        public void department() {
            WrappedStudent student = getStudent(_i -> true);
            Department dept = getDepartment(d -> loadList(d.getEnrolledStudents()).contains(student.getBase()));
            Department otherDept = getDepartment(d -> d != dept );

            assertEquals(dept, student.getDepartment());

            student.setDepartment(otherDept);
            assertEquals(otherDept, student.getDepartment());
            assertTrue("Student did not change department", otherDept.getEnrolledStudents().contains(student.getBase()));

            student.setDepartment(null);
            assertNull("Can not make student homeless", student.getDepartment());
            assertFalse("student is still in original", otherDept.getEnrolledStudents().contains(student.getBase()));
        }
    }

    /**
     * Test the {@link Course} implementation
     */
    public static class CourseTests extends Model {

        /**
         * Test the name property
         */
        @Test
        public void name() {
            Course course = getCourse(_i -> true);

            course.setName("aaaa");
            assertEquals("aaaa", course.getName());
        }

        /**
         * Test department interactions
         */
        @Test
        public void department() {
            Course course = getCourse(_i -> true);
            Department dept = getDepartment(d -> loadList(d.getCourses()).contains(course));
            assertEquals(dept, course.getDepartment());

            Department otherDept = getDepartment(d -> d != dept );

            course.setDepartment(otherDept);
            assertEquals(otherDept, course.getDepartment());
            assertTrue("Department does not contain course",
                    otherDept.getCourses().contains(course));

            course.setDepartment(null);
            assertNull("Can not make course homeless", course.getDepartment());
        }

        @Test
        public void equals() {
            Course check = new RealCourse("Test", "te2222", ece, 5);
            Course copyCat = new RealCourse("Test", "te2222", ece, 5);

            WrappedStudent toEnroll = getStudent(_i -> true);
            toEnroll.addCourse(check);
            toEnroll.addCourse(copyCat);

            assertEquals("Two identical courses are not equal", check, copyCat);

            assertNotEquals("Two different courses are equal", check, se2205);
        }

        /**
         * Test that adding/removing students behaves
         */
        @Test
        public void students() {
            Course course = getCourse(_i -> true);
            WrappedStudent studentNotEnrolled = getStudent(s -> !loadList(course.getEnrolledStudents()).contains(s.getBase()));
            Department dept = getDepartment(d -> loadList(d.getCourses()).contains(course));
            assertEquals(dept, course.getDepartment());

            try {
                course.getEnrolledStudents().add(studentNotEnrolled.getBase());
                fail("Able to modify students outside methods");
            } catch (UnsupportedOperationException uoe) {
                // expected
            }

            List<Student> course_students = new ArrayList<>(course.getEnrolledStudents());
            List<Course> student_courses = new ArrayList<>(studentNotEnrolled.getCourses());

            course_students.add(studentNotEnrolled.getBase());
            student_courses.add(course);

            course.enrollStudent(studentNotEnrolled.getBase());
            assertContains(course_students, course.getEnrolledStudents());
            assertContains(student_courses, studentNotEnrolled.getCourses());

            // Should be no change
            course.enrollStudent(studentNotEnrolled.getBase());
            assertContains(course_students, course.getEnrolledStudents());
            assertContains(student_courses, studentNotEnrolled.getCourses());

            // Add another student
            WrappedStudent anotherNotEnrolled = getStudent(s ->
                    s != studentNotEnrolled && !loadList(course.getEnrolledStudents()).contains(s.getBase()));

            List<Course> anotherStudent_courses = new ArrayList<>(anotherNotEnrolled.getCourses());

            anotherStudent_courses.add(course);
            course_students.add(anotherNotEnrolled.getBase());

            course.enrollStudent(anotherNotEnrolled.getBase());
            assertContains(course_students, course.getEnrolledStudents());
            assertContains(anotherStudent_courses, anotherNotEnrolled.getCourses());
            assertContains(student_courses, studentNotEnrolled.getCourses());

            // Remove a student
            course_students.remove(studentNotEnrolled.getBase());
            student_courses.remove(course);

            course.removeStudent(studentNotEnrolled.getBase());

            assertContains(course_students, course.getEnrolledStudents());
            assertContains(student_courses, studentNotEnrolled.getCourses());
            assertContains(anotherStudent_courses, anotherNotEnrolled.getCourses());

            // Remove the same student
            course.removeStudent(studentNotEnrolled.getBase());

            assertContains(course_students, course.getEnrolledStudents());
            assertContains(student_courses, studentNotEnrolled.getCourses());
            assertContains(anotherStudent_courses, anotherNotEnrolled.getCourses());

            // Put that student back
            course_students.add(studentNotEnrolled.getBase());
            student_courses.add(course);

            course.enrollStudent(studentNotEnrolled.getBase());
            assertContains(course_students, course.getEnrolledStudents());
            assertContains(student_courses, studentNotEnrolled.getCourses());
            assertContains(anotherStudent_courses, anotherNotEnrolled.getCourses());

            // Make sure invalid modification doesn't change things
            WrappedStudent definiteNotEnrolled = getStudent(s ->
                    s != studentNotEnrolled && !course.getEnrolledStudents().contains(s.getBase()));
            course.removeStudent(definiteNotEnrolled.getBase());

            assertContains(course_students, course.getEnrolledStudents());
            assertContains(student_courses, studentNotEnrolled.getCourses());
            assertContains(anotherStudent_courses, anotherNotEnrolled.getCourses());

            // Check the exception
            Course toBreak = new RealCourse("test", "te2222", null, 5);
            WrappedStudent student = null;
            try {
                for (int i = 1; i < 10; ++i) {
                    student = new WrappedStudent("Bob " + i, "Builder", i * 11111L, null);
                    student.addCourse(toBreak);
                }
                fail("Should have thrown CourseMaxCapacityException");
            } catch (CourseMaxCapacityStoreException e) {
                assertNotNull("Could not add a single student, max was set to 5", student);
                assertEquals("Student in exception is incorrect", student.getBase(), e.getStudent());
                assertEquals("Course in exception is incorrect", toBreak, e.getCourse());
            }
        }

    }

    /**
     * Test the {@link Department} implementation.
     */
    public static class DepartmentTest extends Model {

        /**
         * Test the name property
         */
        @Test
        public void name() {
            Department dept = getDepartment(_i -> true);
            dept.setName("Bob");
            assertEquals("Bob", dept.getName());
        }

        /**
         * Test course changes
         */
        @Test
        public void courses() {
            Department dept = getDepartment(d -> !loadList(d.getCourses()).isEmpty());
            Course ownedCourse = getCourse(c -> loadList(dept.getCourses()).contains(c));

            List<Course> dept_courses = new ArrayList<>(dept.getCourses());

            assertContains(dept_courses, dept.getCourses());

            // Remove a course
            dept_courses.remove(ownedCourse);
            dept.removeCourse(ownedCourse);

            assertContains(dept_courses, dept.getCourses());
            assertNull("Course department not affected", ownedCourse.getDepartment());

            // Add a course
            dept_courses.add(ownedCourse);
            dept.addCourse(ownedCourse);
            assertContains(dept_courses, dept.getCourses());

            // Add the same course
            dept.addCourse(ownedCourse);
            assertContains(dept_courses, dept.getCourses());

            Course otherCourse = getCourse(c -> !dept.getCourses().contains(c));
            Department otherDept = getDepartment(d -> d.getCourses().contains(otherCourse));
            List<Course> otherDeptCourses = new ArrayList<>(otherDept.getCourses());

            dept_courses.add(otherCourse);
            otherDeptCourses.remove(otherCourse);
            dept.addCourse(otherCourse);

            assertEquals(dept, otherCourse.getDepartment());
            assertContains(Arrays.asList(ownedCourse, otherCourse), dept.getCourses());
            assertContains(otherDeptCourses, otherDept.getCourses());

            // check that if you remove from another dept, it doesn't affect this one
            otherDept.removeCourse(ownedCourse);
            assertEquals("Incorrect remove causes changes", dept, ownedCourse.getDepartment());
        }

        /**
         * Test student changes
         */
        @Test
        public void students() {
            WrappedStudent student = getStudent(_i -> true);
            WrappedStudent otherStudent = getStudent( s -> s != student && s.getDepartment() != student.getDepartment());

            Department dept = getDepartment(d -> loadList(d.getEnrolledStudents()).contains(student.getBase()));
            Department otherDept = getDepartment(d -> loadList(d.getEnrolledStudents()).contains(otherStudent.getBase()));

            List<Student> dept_students = new ArrayList<>(dept.getEnrolledStudents());
            List<Student> otherDept_students = new ArrayList<>(otherDept.getEnrolledStudents());

            assertContains(dept_students, dept.getEnrolledStudents());

            dept_students.add(otherStudent.getBase());
            otherDept_students.remove(otherStudent.getBase());

            dept.enrollStudent(otherStudent.getBase());

            assertEquals(dept, otherStudent.getDepartment());
            assertContains(dept_students, dept.getEnrolledStudents());
            assertContains(otherDept_students, otherDept.getEnrolledStudents());

            // Invalid remove:
            otherDept.removeStudent(otherStudent.getBase());
            assertEquals(dept, otherStudent.getDepartment());
            assertContains(dept_students, dept.getEnrolledStudents());
            assertContains(otherDept_students, otherDept.getEnrolledStudents());

            // Valid remove:
            dept_students.remove(otherStudent.getBase());

            dept.removeStudent(otherStudent.getBase());
            assertNull(otherStudent.getDepartment());
            assertContains(dept_students, dept.getEnrolledStudents());
            assertContains(otherDept_students, otherDept.getEnrolledStudents());

            // Remove dept via student
            dept_students.remove(student.getBase());

            student.setDepartment(null);

            assertNull(student.getDepartment());
            assertContains(dept_students, dept.getEnrolledStudents());
            assertContains(otherDept_students, otherDept.getEnrolledStudents());
        }

        @Test
        public void equals() {
            Department check = new RealDepartment("Test");
            Department copyCat = new RealDepartment("Test");

            assertEquals("Two identical departments are not equal", check, copyCat);

            assertNotEquals("Two different courses are equal", check, ece);
        }
    }




}
