/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc_mysql;

import objects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author william
 */
public class SQLUser {

    /*
    CREATE TABLE pcf_user (
        id_user INT AUTO_INCREMENT PRIMARY KEY,
        photo_user BOOLEAN DEFAULT FALSE,
        name_user VARCHAR(50) NOT NULL,

        email_user VARCHAR(50) UNIQUE NOT NULL,
        password_user VARCHAR(100) NOT NULL,
        province_user VARCHAR(50) NOT NULL,

        township_user VARCHAR(50) NOT NULL,
        short_presentation_user VARCHAR(500) NOT NULL,
        long_presentation_user VARCHAR(1000) -- Opcional
    );
     */
    private Connection connection;
    private PreparedStatement pstm;

    private String table;
    private String[] columns;
    private String uniqueKey;
    private String primaryKey;

    public SQLUser(Connection connection) {
        this.connection = connection;
        this.table = "pcf_user";
        this.primaryKey = "id_user";
        this.columns = new String[]{"photo_user", // 0
                "name_user", // 1
                "email_user", // 2
                "password_user", // 3
                "province_user", // 4
                "township_user", // 5
                "short_presentation_user", // 6
                "long_presentation_user"}; // 7
        this.uniqueKey = columns[2];
    }

    // REGISTRO: Operaciones Futuras; Hecho
    public int getUserId(User user) {
        try {
            pstm = connection.prepareStatement("SELECT " + primaryKey + " FROM " + table + " WHERE " + uniqueKey + " = ?;");

            pstm.setString(1, user.getEmail());

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) { // Si encuentra una sola coincidencia es que existe, lógicamente.
                    return rst.getInt(primaryKey);
                }
            }
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    // Hecho
    public String getUserEmail(int userId) {
        try {
            pstm = connection.prepareStatement("SELECT " + uniqueKey + " FROM " + table + " WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, userId);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) { // Si encuentra una sola coincidencia es que existe, lógicamente.
                    return rst.getString(uniqueKey);
                }
            }
            return "";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    // Hecho
    public String getUserName(int userId) {
        try {
            pstm = connection.prepareStatement("SELECT " + columns[1] + " FROM " + table + " WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, userId);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) { // Si encuentra una sola coincidencia es que existe, lógicamente.
                    return rst.getString(columns[1]);
                }
            }
            return "Cuenta Eliminada"; // Si userId es null, es que el Usuario ya no existe
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Cuenta Eliminada";
    }

    // REGISTRO: Registro del Usuario; Hecho
    public String registerUser(User user) { // Para el REGISTRO, obviamente
        try {
            pstm = connection.prepareStatement("INSERT INTO " + table + " ("
                    + columns[0] + ", "
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", "
                    + columns[5] + ", "
                    + columns[6] + ", "
                    + columns[7] + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

            pstm.setBoolean(1, user.hasPhoto());
            pstm.setString(2, user.getName());
            pstm.setString(3, user.getEmail());
            pstm.setString(4, user.getPassword());
            pstm.setString(5, user.getProvince());
            pstm.setString(6, user.getTownship());
            pstm.setString(7, user.getShortPresentation());
            pstm.setString(8, user.getLongPresentation());

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

    public boolean deleteUser(User user) { // Desde el Perfil del propio Usuario
        try {
            pstm = connection.prepareStatement("DELETE FROM " + table + " WHERE " + uniqueKey + " = ?;");

            pstm.setString(1, user.getEmail());

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Modificar Usuario; Hecho
    public String updateUser(User user) { // Desde el Perfil (Solo el propio usuario)
        try {
            pstm = connection.prepareStatement("UPDATE " + table + " SET "
                    + columns[0] + " = ?, "
                    + columns[1] + " = ?, "
                    + columns[3] + " = ?, "
                    + columns[4] + " = ?, "
                    + columns[5] + " = ?, "
                    + columns[6] + " = ?, "
                    + columns[7] + " = ? WHERE " + uniqueKey + " = ?;");

            pstm.setBoolean(1, user.hasPhoto());
            pstm.setString(2, user.getName());
            pstm.setString(3, user.getPassword());
            pstm.setString(4, user.getProvince());
            pstm.setString(5, user.getTownship());
            pstm.setString(6, user.getShortPresentation());
            pstm.setString(7, user.getLongPresentation());

            pstm.setString(8, user.getEmail());

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

    // LOGIN: Comprobación de exitencia del Usuario; Hecho
    public User confirmUser(User user) { // PARA EL LOGIN - 1.Name 2.Email 3.Pass
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + columns[1] + " = ? AND " + columns[2] + " = ? AND " + columns[3] + " = ?;");

            pstm.setString(1, user.getName());
            pstm.setString(2, user.getEmail());
            pstm.setString(3, user.getPassword());

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idUser = rst.getInt(primaryKey);
                    boolean photo = rst.getBoolean(columns[0]);
                    String name = rst.getString(columns[1]);
                    String confirmedEmail = rst.getString(columns[2]);
                    String password = rst.getString(columns[3]);
                    String province = rst.getString(columns[4]);
                    String township = rst.getString(columns[5]);
                    String shortPresentation = rst.getString(columns[6]);
                    String longPresentation = rst.getString(columns[7]);

                    return new User(idUser, photo, name, confirmedEmail, password, province, township, shortPresentation, longPresentation);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de busqueda.");
        }
        return new User();
    }

    /*
        // LOGIN: Recogida de Datos del Usuario; Hecho
        public User userDataLogin(User user) {
            try {
                pstm = connection.prepareStatement("SELECT "
                        + primaryKey + ", "
                        + columns[0] + ", "
                        + columns[1] + ", "
                        + columns[2] + ", "
                        + columns[3] + ", "
                        + columns[4] + ", "
                        + columns[5] + ", "
                        + columns[6] + ", "
                        + columns[7] + " FROM " + table + " WHERE " + uniqueKey + " = ? AND " + columns[3] + " = ?;");

                pstm.setString(1, user.getEmail());
                pstm.setString(2, user.getPassword());

                try (ResultSet rst = pstm.executeQuery()) {
                    while (rst.next()) {

                        int idUser = rst.getInt(primaryKey);
                        boolean photo = rst.getBoolean(columns[0]);
                        String name = rst.getString(columns[1]);
                        String confirmedEmail = rst.getString(columns[2]);
                        String password = rst.getString(columns[3]);
                        String province = rst.getString(columns[4]);
                        String township = rst.getString(columns[5]);
                        String shortPresentation = rst.getString(columns[6]);
                        String longPresentation = rst.getString(columns[7]);

                        return new User(idUser, photo, name, confirmedEmail, password, province, township, shortPresentation, longPresentation);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Fallo de inserción.");
            }
            return null;
        }
    */
    // Ver Perfil: Recogida de Datos del Usuario; Hecho
    public User takeUserPerfil(int idUser) {
        User user = new User();
        try {
            pstm = connection.prepareStatement("SELECT "
                    + columns[0] + ", "
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", "
                    + columns[5] + ", "
                    + columns[6] + ", "
                    + columns[7] + " FROM " + table + " WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, idUser);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    boolean photo = rst.getBoolean(columns[0]);
                    String name = rst.getString(columns[1]);
                    String confirmedEmail = rst.getString(columns[2]);
                    String password = rst.getString(columns[3]);
                    String province = rst.getString(columns[4]);
                    String township = rst.getString(columns[5]);
                    String shortPresentation = rst.getString(columns[6]);
                    String longPresentation = rst.getString(columns[7]);

                    return new User(idUser, photo, name, confirmedEmail, password, province, township, shortPresentation, longPresentation);
                }
            }
            user.setIdUser(-1); // <- Si no lo encuentra. Devuelve Usuario con idUser = -1
            return user;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return user; // <- Si Excepción. Devuelve Usuario con idUser = 0
        }
    }

    // Para el Motor de Busquedas;
    public LinkedList<User> searchMultipleUsers(LinkedList<String> parameters, int idLoggedUser) { // 0.NombreCurso/none, 1.Provincia/none, 2.Municipio/none, 3.Tag/none, 4.Tag?, 5.Tag?, etc.
        LinkedList<User> list = new LinkedList();
        try {

            String selection;

            if (!parameters.get(3).equals("none")) { // Si buscamos Tags: Buscamos Profesores.
                selection = "SELECT pcf_user.id_user, pcf_user.photo_user, pcf_user.name_user, pcf_user.email_user,"
                        + " pcf_user.province_user, pcf_user.township_user, pcf_user.short_presentation_user, pcf_user.long_presentation_user"
                        + " FROM pcf_user, pcf_course, pcf_course_tag, pcf_tag_defines_course" // De estas 4 tablas: Cursos, tags, tag-cursos, usuarios
                        + " WHERE pcf_course.id_course = pcf_tag_defines_course.id_course" // Siempre que curso sea definido por una tag
                        + " AND pcf_course_tag.id_tag = pcf_tag_defines_course.id_tag" // Y tag defina a un curso
                        + " AND pcf_course.id_teacher_user = pcf_user.id_user" // Y Profesor sea Usuario
                        + " AND pcf_course.hidden_course = false"; // Y curso no este oculto
            } else { // Si no, buscamos a cualquiera.
                selection = "SELECT pcf_user.id_user, pcf_user.photo_user, pcf_user.name_user, pcf_user.email_user,"
                        + " pcf_user.province_user, pcf_user.township_user, pcf_user.short_presentation_user, pcf_user.long_presentation_user"
                        + " FROM pcf_user"
                        + " WHERE pcf_user.name_user LIKE '%'"; // Por el "WHERE"
            }

            selection += " AND pcf_user.id_user != " + idLoggedUser;

            if (!parameters.get(0).equals("none")) { // Nombre Curso
                selection += " AND pcf_user.name_user LIKE '" + parameters.get(0) + "%'";
            }
            if (!parameters.get(1).equals("none")) { // Provincia
                selection += " AND pcf_user.province_user LIKE '" + parameters.get(1) + "%'";
            }
            if (!parameters.get(2).equals("none")) { // Municipio
                selection += " AND pcf_user.township_user LIKE '" + parameters.get(2) + "%'";
            }

            if (!parameters.get(3).equals("none")) { // Primera Tag
                selection += " AND pcf_course_tag.word_tag LIKE '" + parameters.get(3) + "%'";
            }
            for (int i = 4; i < parameters.size(); i++) {
                selection += " AND pcf_course_tag.word_tag LIKE '" + parameters.get(i) + "%'"; // Si hay más etiquetas...
            }

            pstm = connection.prepareStatement(selection + " GROUP BY pcf_user.id_user"
                    + " ORDER BY pcf_user.name_user;");

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idUser = rst.getInt(primaryKey);
                    boolean photo = rst.getBoolean(columns[0]);
                    String name = rst.getString(columns[1]);
                    String confirmedEmail = rst.getString(columns[2]);
                    String province = rst.getString(columns[4]);
                    String township = rst.getString(columns[5]);
                    String shortPresentation = rst.getString(columns[6]);
                    String longPresentation = rst.getString(columns[7]);

                    list.add(new User(idUser, photo, name, confirmedEmail, province, township, shortPresentation, longPresentation));
                }
                return list;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // CORREOS EN MASA: AULA VIRTUAL
    public LinkedList<Integer> getCourseStudents(int courseId) {
        try {
            LinkedList<Integer> studentsIds = new LinkedList<>();

            pstm = connection.prepareStatement("SELECT pcf_user.id_user FROM " + table + ", pcf_course, pcf_user_receives_course" +
                    " WHERE pcf_user_receives_course.id_user = pcf_user.id_user" +
                    " AND pcf_user_receives_course.id_course = pcf_course.id_course" +
                    " AND " + courseId + " = ?" +
                    " GROUP BY pcf_user.id_user;");

            pstm.setInt(1, courseId);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    studentsIds.add(rst.getInt(primaryKey));
                }
            }
            return studentsIds;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public LinkedList<User> listStudents(int idCourse) {
        try {
            LinkedList<User> students = new LinkedList<>();

            pstm = connection.prepareStatement("SELECT pcf_user.id_user FROM " + table + ", pcf_course, pcf_user_receives_course" +
                    " WHERE pcf_user_receives_course.id_user = pcf_user.id_user" +
                    " AND pcf_user_receives_course.id_course = pcf_course.id_course" +
                    " AND " + idCourse + " = ?" +
                    " GROUP BY pcf_user.id_user;");

            pstm.setInt(1, idCourse);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idUser = rst.getInt(primaryKey);
                    boolean photo = rst.getBoolean(columns[0]);
                    String name = rst.getString(columns[1]);
                    String confirmedEmail = rst.getString(columns[2]);
                    String province = rst.getString(columns[4]);
                    String township = rst.getString(columns[5]);
                    String shortPresentation = rst.getString(columns[6]);
                    String longPresentation = rst.getString(columns[7]);

                    students.add(new User(idUser, photo, name, confirmedEmail, province, township, shortPresentation, longPresentation));
                }
            }
            return students;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
