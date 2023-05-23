package ua.foxminded.university.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.repository.UserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Integer findUserIdByEmail(String email) {
        String queryString = "SELECT user_id FROM users WHERE email = :email";
        return (Integer) entityManager
                .createNativeQuery(queryString)
                .setParameter("email", email)
                .getSingleResult();
    }

    @Override
    @Transactional
    public void deactivateUserAccountById(Integer id) {
        entityManager.createNativeQuery("UPDATE users SET isEnabled = FALSE WHERE user_id = ?")
                .setParameter(1, id)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void insertUser(String firstName, String lastName, String email, String password) {
        String sql = "INSERT INTO users (first_name, last_name, email, password) " +
                "VALUES (:firstName, :lastName, :email, :password)";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);
        query.setParameter("email", email);
        query.setParameter("password", password);

        query.executeUpdate();
    }

    @Override
    @Transactional
    public void insertStudent(Integer userId, Integer groupId) {
        String studentRole = "STUDENT";

        String insertStudent = "INSERT INTO students (user_id, group_id) " +
                "VALUES (:user_id, :group_id);";

        Query insertStudentQuery = entityManager.createNativeQuery(insertStudent);
        insertStudentQuery.setParameter("user_id", userId);
        insertStudentQuery.setParameter("group_id", groupId);
        insertStudentQuery.executeUpdate();

        setRoleById(userId, studentRole);

    }

    @Override
    @Transactional
    public void insertTeacher(Integer userId) {
        String teacherRole = "TEACHER";

        String insertTeacher = "INSERT INTO teachers (user_id) " +
                "VALUES (:user_id);";

        Query insertStudentQuery = entityManager.createNativeQuery(insertTeacher);
        insertStudentQuery.setParameter("user_id", userId);
        insertStudentQuery.executeUpdate();

        setRoleById(userId, teacherRole);

    }

    @Override
    @Transactional
    public void insertAdmin(Integer userId) {
        String adminRole = "ADMIN";

        String insertAdmin = "INSERT INTO admins (user_id) " +
                "VALUES (:user_id);";

        insertUserWithTeacherOrAdminRole(userId, insertAdmin);

        setRoleById(userId, adminRole);
    }

    private void insertUserWithTeacherOrAdminRole(Integer userId, String insertQueryString) {
        Query insertStudentQuery = entityManager.createNativeQuery(insertQueryString);
        insertStudentQuery.setParameter("user_id", userId);
        insertStudentQuery.executeUpdate();
    }

    private void setRoleById(Integer userId, String role) {
        String insertRole = "INSERT INTO user_role (user_id, role) " +
                "VALUES(:user_id, '" + role + "')";
        Query insertRoleQuery = entityManager.createNativeQuery(insertRole);
        insertRoleQuery.setParameter("user_id", userId);
        insertRoleQuery.executeUpdate();
    }

}
