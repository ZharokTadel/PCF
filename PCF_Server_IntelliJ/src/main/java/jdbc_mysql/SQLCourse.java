/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc_mysql;

import objects.Course;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import objects.Resource;
import objects.Unit;
import tools.Conversions;

/**
 * @author william
 */
public class SQLCourse {
    /*
    CREATE TABLE pcf_course(
        id_course INT AUTO_INCREMENT PRIMARY KEY,
        name_course VARCHAR(50) NOT NULL,
        short_presentation_course VARCHAR(500) NOT NULL,
        long_presentation_course VARCHAR(1000), -- Opcional
        start_date_course DATE NOT NULL,
        end_date_course DATE NOT NULL, -- Para el acceso al Aula Virtual
        hidden_course BOOLEAN DEFAULT true, -- Para el Motor de Búsquedas
        closed_course BOOLEAN DEFAULT false, -- Da acceso al Aula Virtual de forma prematura y da por finalizado el curso: Debe ser ejecutado manualmente por el profesor
        id_teacher_user INT NOT NULL
    );
    ALTER TABLE pcf_course ADD UNIQUE KEY(name_course, id_teacher_user); -- Para evitar duplicados: Seguridad en el registro
     */

    private Connection connection;
    private PreparedStatement pstm;

    private Conversions conversions;

    private String table;
    private String[] columns;
    private String primaryKey;
    private String teacherKey;

    public SQLCourse(Connection connection) {
        this.conversions = new Conversions();

        this.connection = connection;
        this.table = "pcf_course";
        this.columns = new String[]{"id_course", // 0
                "name_course", // 1
                "short_presentation_course", // 2
                "long_presentation_course", // 3
                "start_date_course", // 4
                "end_date_course", // 5
                "hidden_course", // 6
                "closed_course", // 7
                "id_teacher_user"}; // 8
        this.primaryKey = columns[0];
        this.teacherKey = columns[8];
    }

    public int getIdCourse(Course course) {
        int idCourse = -1;
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table
                    + " WHERE " + columns[1] + " = ?" // (Nombre, idProfesor) => Clave Única
                    + " AND " + columns[8] + " = ?;");

            pstm.setString(1, course.getName());
            pstm.setInt(2, course.getIdTeacher());

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    idCourse = rst.getInt(columns[0]);
                }
                return idCourse;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idCourse;
    }

    public boolean isStudentRegistered(int idUser, int idCourse) { // Un usuario no puede apuntarse multiples veces a un mismo curso
        try {
            /*
            CREATE TABLE pcf_user_receives_course(
                id_user INT,
                id_course INT
            );
             */
            pstm = connection.prepareStatement("SELECT * FROM pcf_user_receives_course WHERE id_user = ? AND id_course = ?;");

            pstm.setInt(1, idUser);
            pstm.setInt(2, idCourse);

            try (ResultSet rsts = pstm.executeQuery()) {
                while (rsts.next()) { // Si encuentra una sola coincidencia es que ya existe
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String registerStudent(int idUser, int idCourse) {
        try {
            /*
            CREATE TABLE pcf_user_receives_course(
                id_user INT,
                id_course INT
            );
             */
            pstm = connection.prepareStatement("INSERT INTO pcf_user_receives_course (id_user, id_course) VALUES (?,?);");

            pstm.setInt(1, idUser);
            pstm.setInt(2, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, duplicate);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public String registerCourseNewTags(LinkedList<String> tags) { // Para el REGISTRO
        int index = 0;
        try {
            for (index = 0; index < tags.size(); index++) { // Guardo las Tags
                pstm = connection.prepareStatement("INSERT INTO pcf_course_tag (word_tag) VALUES (?);");
                pstm.setString(1, tags.get(index));
                pstm.executeUpdate();
                pstm.close();
            }
            return "Ok";
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            LinkedList<String> restOfTheTags = new LinkedList<>(tags.subList(index + 1, tags.size())); // Si ya existe -> recursivo
            registerCourseNewTags(restOfTheTags);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public ArrayList<Integer> getTagsIds(LinkedList<String> tags) {
        try {
            ArrayList<Integer> idTags = new ArrayList();
            pstm = connection.prepareStatement("SELECT * FROM pcf_course_tag;");

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    for (String str : tags) {
                        if (rst.getString("word_tag").equals(str)) { // Compruebo coincidencias
                            idTags.add(rst.getInt("id_tag"));
                        }
                    }
                }

                return idTags;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void registerCourseTagsRelation(int idCourse, ArrayList<Integer> tagsIds) {
        int index = 0;
        try {
            for (index = 0; index < tagsIds.size(); index++) {
                pstm = connection.prepareStatement("INSERT INTO pcf_tag_defines_course (id_tag, id_course) VALUES (?,?);"); // Almaceno la relación (Curso <---> Tag)

                pstm.setInt(1, tagsIds.get(index));
                pstm.setInt(2, idCourse);

                pstm.executeUpdate();
                pstm.close();
            }
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            ArrayList<Integer> restOfTagsIds = new ArrayList<>(tagsIds.subList(index + 1, tagsIds.size()));
            registerCourseTagsRelation(idCourse, restOfTagsIds);
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // REGISTRO CURSO: HECHO
    public String registerCourse(Course course, LinkedList<String> tags) { // Para el REGISTRO
        try {
            pstm = connection.prepareStatement("INSERT INTO " + table + " ("
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", "
                    + columns[5] + ", " // [6][7] <- hidden DEFAULT true
                    + columns[8] + ") VALUES (?, ?, ?, ?, ?, ?);");

            pstm.setString(1, course.getName());
            pstm.setString(2, course.getShortPresentation());
            pstm.setString(3, course.getLongPresentation());
            pstm.setDate(4, course.getStartDate());
            pstm.setDate(5, course.getEndDate());
            pstm.setInt(6, course.getIdTeacher());

            pstm.executeUpdate();
            pstm.close();

            int idCourse = getIdCourse(course); // Recojo el id del Curso para las siguientes operaciones

            tags.add("*"); // Tag que todos los cursos debe tener

            registerCourseNewTags(tags); // Registro las Tags en la BBDD
            ArrayList<Integer> idsTags = getTagsIds(tags); // Recojo las ids de las Tags
            registerCourseTagsRelation(idCourse, idsTags); // Registro la relación entre el Curso y las Tags

            return String.valueOf(idCourse);
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, duplicate);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // UPDATE CURSO (Para modificaciones y Publicaciones): HECHO
    public String updateCourse(Course course, LinkedList<String> tags) {
        try {
            pstm = connection.prepareStatement("UPDATE " + table + " SET "
                    + columns[1] + " = ?, "
                    + columns[2] + " = ?, "
                    + columns[3] + " = ?, "
                    + columns[4] + " = ?, "
                    + columns[5] + " = ?, "
                    + columns[6] + " = ?, " // [7] <- closed DEFAULT true
                    + columns[8] + " = ? WHERE " + primaryKey + " = ?;");

            pstm.setString(1, course.getName());
            pstm.setString(2, course.getShortPresentation());
            pstm.setString(3, course.getLongPresentation());
            pstm.setDate(4, course.getStartDate());
            pstm.setDate(5, course.getEndDate());
            pstm.setBoolean(6, course.isHidden());
            pstm.setInt(7, course.getIdTeacher());

            pstm.setInt(8, course.getIdCourse());

            pstm.executeUpdate();
            pstm.close();

            if (!tags.isEmpty()) {
                registerCourseNewTags(tags); // Registro las Nuevas Tags en la BBDD
                ArrayList<Integer> idsTags = getTagsIds(tags); // Recojo las ids de las Tags
                registerCourseTagsRelation(course.getIdCourse(), idsTags); // Registro la relación entre el Curso y las Tags
            }

            return "Ok";
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, duplicate);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // OPEN CURSO: HECHO
    public boolean openCourse(int idCourse) {
        try {
            pstm = connection.prepareStatement("UPDATE " + table + " SET " + columns[7] + " = false WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    // CLOSE CURSO: HECHO
    public boolean closeCourse(int idCourse) {
        try {
            pstm = connection.prepareStatement("UPDATE " + table + " SET " + columns[7] + " = true WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    // FINAL CURSO
    public boolean deleteStudents(int idCourse) { // ON DELETE CASCADE
        try {
            pstm = connection.prepareStatement("DELETE FROM pcf_user_receives_course WHERE id_course = ?;");

            pstm.setInt(1, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean deleteCourse(int idCourse) { // ON DELETE CASCADE
        try {
            pstm = connection.prepareStatement("DELETE FROM pcf_course WHERE id_course = ?;");

            pstm.setInt(1, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // BUSCAR CURSO: HECHO
    public Course searchCourse(int idCourse) {
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + primaryKey + " = ?;");
            pstm.setInt(1, idCourse);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    String name = rst.getString(columns[1]);
                    String shortPresentation = rst.getString(columns[2]);
                    String longPresentation = rst.getString(columns[3]);
                    Date startDate = rst.getDate(columns[4]);
                    Date endDate = rst.getDate(columns[5]);
                    boolean hidden = rst.getBoolean(columns[6]);
                    boolean closed = rst.getBoolean(columns[7]);
                    int idTeacher = rst.getInt(columns[8]);

                    return new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher);
                }
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // Lista Cursos que Imparto: HECHO
    public LinkedList<Course> searchCoursesITeach(int idTeacher) {
        LinkedList<Course> courses = new LinkedList();
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + teacherKey + " = ? ORDER BY pcf_course.start_date_course;");
            pstm.setInt(1, idTeacher);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idCourse = rst.getInt(columns[0]);
                    String name = rst.getString(columns[1]);
                    String shortPresentation = rst.getString(columns[2]);
                    String longPresentation = rst.getString(columns[3]);
                    Date startDate = rst.getDate(columns[4]);
                    Date endDate = rst.getDate(columns[5]);
                    boolean hidden = rst.getBoolean(columns[6]);
                    boolean closed = rst.getBoolean(columns[7]);

                    courses.add(new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher));
                }
                return courses;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // Lista Cursos que Recibo: HECHO
    public LinkedList<Course> searchCoursesIReceive(int studentId) {
        LinkedList<Course> courses = new LinkedList();

        try {
            pstm = connection.prepareStatement("SELECT pcf_course.id_course, pcf_course.name_course, pcf_course.short_presentation_course, pcf_course.long_presentation_course,"
                    + " pcf_course.start_date_course, pcf_course.end_date_course, pcf_course.hidden_course, pcf_course.closed_course,  pcf_course.id_teacher_user"
                    + " FROM " + table + ", pcf_user_receives_course"
                    + " WHERE pcf_course.id_course = pcf_user_receives_course.id_course"
                    + " AND pcf_course.end_date_course > CURRENT_DATE()" // Si la fecha de finalización ha pasado no se recoge
                    + " AND pcf_user_receives_course.id_user = ?"
                    + " ORDER BY pcf_course.start_date_course;");
            pstm.setInt(1, studentId);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idCourse = rst.getInt(columns[0]);
                    String name = rst.getString(columns[1]);
                    String shortPresentation = rst.getString(columns[2]);
                    String longPresentation = rst.getString(columns[3]);
                    Date startDate = rst.getDate(columns[4]);
                    Date endDate = rst.getDate(columns[5]);
                    boolean hidden = rst.getBoolean(columns[6]);
                    boolean closed = rst.getBoolean(columns[7]);
                    int idTeacher = rst.getInt(columns[8]);

                    courses.add(new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher));
                }
                return courses;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // Para el Motor de Busquedas: HECHO
    public LinkedList<Course> searchMultipleCourses(LinkedList<String> parameters) { // 0.CurrentDate, 1.NombreCurso/none, 2.Provincia/none, 3.Municipio/none, 4.Tag/none, 5.Tag?, 6.Tag?, etc.
        LinkedList<Course> courses = new LinkedList();
        java.sql.Date sqlCoursestartDate = conversions.convertStringToDate(parameters.get(0));

        try {
            String selection = "SELECT pcf_course.id_course, pcf_course.name_course, pcf_course.short_presentation_course, pcf_course.long_presentation_course,"
                    + " pcf_course.start_date_course, pcf_course.end_date_course, pcf_course.hidden_course, pcf_course.closed_course,  pcf_course.id_teacher_user"
                    + " FROM " + table + ", pcf_course_tag, pcf_tag_defines_course, pcf_user" // De estas 4 tablas: Cursos, tags, tag-cursos, usuarios
                    + " WHERE pcf_course.id_course = pcf_tag_defines_course.id_course" // Siempre que curso sea definido por una tag
                    + " AND pcf_course_tag.id_tag = pcf_tag_defines_course.id_tag" // Y tag defina a un curso
                    + " AND pcf_course.id_teacher_user = pcf_user.id_user" // Y Profesor sea Usuario
                    + " AND pcf_course.hidden_course = false"
                    + " AND pcf_course.start_date_course > '" + sqlCoursestartDate + "'"; // Y la fecha de inicio no se haya pasado

            if (!parameters.get(1).equals("none")) { // Nombre Curso
                selection += " AND pcf_course.name_course LIKE '" + parameters.get(1) + "%'";
            }
            if (!parameters.get(2).equals("none")) { // Provincia
                selection += " AND pcf_user.province_user LIKE '" + parameters.get(2) + "%'";
            }
            if (!parameters.get(3).equals("none")) { // Municipio
                selection += " AND pcf_user.township_user LIKE '" + parameters.get(3) + "%'";
            }
            if (!parameters.get(4).equals("none")) { // Primera Tag
                selection += " AND pcf_course_tag.word_tag LIKE '" + parameters.get(4) + "%'";
            } else {
                selection += " AND pcf_course_tag.word_tag LIKE '%'"; // La etiqueta que TODOS los cursos tienen
            }

            for (int i = 5; i < parameters.size(); i++) {
                selection += " AND pcf_course_tag.word_tag LIKE '" + parameters.get(i) + "%'"; // Si hay más etiquetas...
            }

            pstm = connection.prepareStatement(selection + " GROUP BY pcf_course.id_course"
                    + " ORDER BY pcf_course.start_date_course;");

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idCourse = rst.getInt(columns[0]);
                    String name = rst.getString(columns[1]);
                    String shortPresentation = rst.getString(columns[2]);
                    String longPresentation = rst.getString(columns[3]);
                    Date startDate = rst.getDate(columns[4]);
                    Date endDate = rst.getDate(columns[5]);
                    boolean hidden = rst.getBoolean(columns[6]);
                    boolean closed = rst.getBoolean(columns[7]);
                    int idTeacher = rst.getInt(columns[8]);

                    courses.add(new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher));
                }
                return courses;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public LinkedHashMap<Unit, LinkedList<Resource>> consultVirtualClassTeacher(int idCourseVirtualClass) {
        LinkedHashMap<Unit, LinkedList<Resource>> virtualClassMap = new LinkedHashMap<>();
        Unit unit = new Unit();
        LinkedList<Resource> resources = new LinkedList<>();

        try {
            pstm = connection.prepareStatement("SELECT *" +
                    " FROM pcf_course, pcf_unit" +
                    " LEFT JOIN pcf_resource" +
                    " ON pcf_unit.id_unit = pcf_resource.id_unit" +
                    " WHERE pcf_course.id_course = pcf_unit.id_course" +
                    " AND pcf_course.id_course = ?" +
                    " ORDER BY pcf_unit.order_unit ASC," +
                    " pcf_resource.order_resource ASC;");
            pstm.setInt(1, idCourseVirtualClass);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    // TODOS LOS DATOS DE TEMA

                    int idUnit = rst.getInt("pcf_unit.id_unit");
                    String titleUnit = rst.getString("pcf_unit.title_unit");
                    int orderUnit = rst.getInt("pcf_unit.order_unit");
                    boolean hiddenUnit = rst.getBoolean("pcf_unit.hidden_unit");
                    int percentageExercises = rst.getInt("pcf_unit.percentage_exercises");
                    int percentageControls = rst.getInt("pcf_unit.percentage_controls");
                    int percentageExams = rst.getInt("pcf_unit.percentage_exams");
                    int percentageTests = rst.getInt("pcf_unit.percentage_tests");
                    int idCourse = rst.getInt("pcf_unit.id_course");

                    if (unit.getIdUnit() != idUnit) {
                        unit = new Unit(idUnit, titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);
                        resources = new LinkedList<>();
                    }
                    // TODOS LOS DATOS DE RECURSOS
                    int idResource = rst.getInt("pcf_resource.id_resource");
                    String titleResource = rst.getString("pcf_resource.title_resource");
                    String presentationResource = rst.getString("pcf_resource.presentation_resource");
                    String typeResource = rst.getString("pcf_resource.type_resource");
                    int orderResource = rst.getInt("pcf_resource.order_resource");
                    boolean hiddenResource = rst.getBoolean("pcf_resource.hidden_resource");
                    int idUnitResource = rst.getInt("pcf_resource.id_unit");

                    Resource resource = new Resource(idResource, titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnitResource);
                    resources.add(resource);

                    virtualClassMap.put(unit, resources);
                }
                return virtualClassMap;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public LinkedHashMap<Unit, LinkedList<Resource>> consultVirtualClassStudent(int idCourseVirtualClass) {
        LinkedHashMap<Unit, LinkedList<Resource>> virtualClassMap = new LinkedHashMap<>();
        Unit unit = new Unit();
        LinkedList<Resource> resources = new LinkedList<>();

        try {
            pstm = connection.prepareStatement("SELECT *" +
                    " FROM pcf_course, pcf_unit" +
                    " LEFT JOIN pcf_resource" +
                    " ON pcf_unit.id_unit = pcf_resource.id_unit" +
                    " WHERE pcf_course.id_course = pcf_unit.id_course" +
                    " AND pcf_unit.hidden_unit = false" +
                    " AND pcf_resource.hidden_resource = false" +
                    " AND pcf_course.id_course = ?" +
                    " ORDER BY pcf_unit.order_unit ASC," +
                    " pcf_resource.order_resource ASC;");
            pstm.setInt(1, idCourseVirtualClass);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    // TODOS LOS DATOS DE TEMA

                    int idUnit = rst.getInt("pcf_unit.id_unit");
                    String titleUnit = rst.getString("pcf_unit.title_unit");
                    int orderUnit = rst.getInt("pcf_unit.order_unit");
                    boolean hiddenUnit = rst.getBoolean("pcf_unit.hidden_unit");
                    int percentageExercises = rst.getInt("pcf_unit.percentage_exercises");
                    int percentageControls = rst.getInt("pcf_unit.percentage_controls");
                    int percentageExams = rst.getInt("pcf_unit.percentage_exams");
                    int percentageTests = rst.getInt("pcf_unit.percentage_tests");
                    int idCourse = rst.getInt("pcf_unit.id_course");

                    if (unit.getIdUnit() != idUnit) {
                        unit = new Unit(idUnit, titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);
                        resources = new LinkedList<>();
                    }
                    // TODOS LOS DATOS DE RECURSOS
                    int idResource = rst.getInt("pcf_resource.id_resource");
                    String titleResource = rst.getString("pcf_resource.title_resource");
                    String presentationResource = rst.getString("pcf_resource.presentation_resource");
                    String typeResource = rst.getString("pcf_resource.type_resource");
                    int orderResource = rst.getInt("pcf_resource.order_resource");
                    boolean hiddenResource = rst.getBoolean("pcf_resource.hidden_resource");
                    int idUnitResource = rst.getInt("pcf_resource.id_unit");

                    Resource resource = new Resource(idResource, titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnitResource);
                    resources.add(resource);

                    virtualClassMap.put(unit, resources);
                }
                return virtualClassMap;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
