package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.StudentDao;
import com.littlehorizons.erp.model.DocumentType;
import com.littlehorizons.erp.model.Gender;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcStudentDao extends BaseJdbcDao implements StudentDao {

    private static final String SELECT_COLUMNS = "SELECT e.id_estudiante, e.tipo_documento, e.numero_documento, "
            + "e.primer_nombre, e.segundo_nombre, e.primer_apellido, e.segundo_apellido, e.fecha_nacimiento, "
            + "e.genero, e.direccion, e.telefono, e.correo_electronico, e.tipo_sangre, e.alergias, "
            + "e.condiciones_medicas, e.fecha_ingreso, e.estado FROM estudiantes e ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY e.primer_apellido, e.primer_nombre";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE e.id_estudiante = ?";
    private static final String FIND_BY_DOCUMENT = SELECT_COLUMNS + "WHERE e.numero_documento = ?";
    private static final String FIND_BY_GUARDIAN = SELECT_COLUMNS
            + "JOIN estudiante_acudiente ea ON ea.id_estudiante = e.id_estudiante WHERE ea.id_acudiente = ? "
            + "ORDER BY e.primer_nombre";
    private static final String INSERT = "INSERT INTO estudiantes (tipo_documento, numero_documento, "
            + "primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, fecha_nacimiento, genero, "
            + "direccion, telefono, correo_electronico, tipo_sangre, alergias, condiciones_medicas, fecha_ingreso, "
            + "estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_LINK = "INSERT INTO estudiante_acudiente (id_estudiante, id_acudiente, "
            + "es_principal, es_contacto_emergencia) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE estudiantes SET tipo_documento = ?, numero_documento = ?, "
            + "primer_nombre = ?, segundo_nombre = ?, primer_apellido = ?, segundo_apellido = ?, "
            + "fecha_nacimiento = ?, genero = ?, direccion = ?, telefono = ?, correo_electronico = ?, "
            + "tipo_sangre = ?, alergias = ?, condiciones_medicas = ?, fecha_ingreso = ?, estado = ? "
            + "WHERE id_estudiante = ?";
    private static final String DELETE = "DELETE FROM estudiantes WHERE id_estudiante = ?";

    @Override
    public List<Student> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapStudent(resultSet));
    }

    @Override
    public Student findById(int studentId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapStudent(resultSet), studentId);
    }

    @Override
    public Student findByDocument(String documentNumber) {
        return queryForObject(FIND_BY_DOCUMENT, resultSet -> mapStudent(resultSet), documentNumber);
    }

    @Override
    public List<Student> findByGuardian(int guardianId) {
        return queryForList(FIND_BY_GUARDIAN, resultSet -> mapStudent(resultSet), guardianId);
    }

    @Override
    public int insertWithGuardian(Student student, int guardianId, boolean emergencyContact) {
        String source = "JdbcStudentDao.insertWithGuardian";
        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                int studentId = insertAndGetId(connection, INSERT, studentValues(student));
                updateInTransaction(connection, INSERT_LINK, studentId, guardianId, true, emergencyContact);
                connection.commit();
                return studentId;
            } catch (SQLException e) {
                rollbackQuietly(connection, source);
                throw e;
            }
        } catch (SQLException e) {
            throw handleError(source, e);
        }
    }

    @Override
    public void update(Student student) {
        Object[] values = studentValues(student);
        executeUpdate(UPDATE, values[0], values[1], values[2], values[3], values[4], values[5], values[6],
                values[7], values[8], values[9], values[10], values[11], values[12], values[13], values[14],
                values[15], student.getId());
    }

    @Override
    public void delete(int studentId) {
        executeUpdate(DELETE, studentId);
    }

    private Object[] studentValues(Student student) {
        return new Object[]{student.getDocumentType().getDbValue(), student.getDocumentNumber(),
                student.getFirstName(), student.getMiddleName(), student.getLastName(), student.getSecondLastName(),
                student.getBirthDate(), student.getGender().getDbValue(), student.getAddress(), student.getPhone(),
                student.getEmail(), student.getBloodType(), student.getAllergies(), student.getMedicalConditions(),
                student.getAdmissionDate(), student.getStatus().getDbValue()};
    }

    private Student mapStudent(ResultSet resultSet) throws SQLException {
        Student student = new Student.Builder(DocumentType.fromDbValue(resultSet.getString("tipo_documento")),
                resultSet.getString("numero_documento"), resultSet.getString("primer_nombre"),
                resultSet.getString("primer_apellido"))
                .middleName(resultSet.getString("segundo_nombre"))
                .secondLastName(resultSet.getString("segundo_apellido"))
                .birthDate(getDate(resultSet, "fecha_nacimiento"))
                .gender(Gender.fromDbValue(resultSet.getString("genero")))
                .address(resultSet.getString("direccion"))
                .phone(resultSet.getString("telefono"))
                .email(resultSet.getString("correo_electronico"))
                .bloodType(resultSet.getString("tipo_sangre"))
                .allergies(resultSet.getString("alergias"))
                .medicalConditions(resultSet.getString("condiciones_medicas"))
                .admissionDate(getDate(resultSet, "fecha_ingreso"))
                .status(StudentStatus.fromDbValue(resultSet.getString("estado")))
                .build();
        student.setId(resultSet.getInt("id_estudiante"));
        return student;
    }
}
