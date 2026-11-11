package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.TeacherDao;
import com.littlehorizons.erp.model.DocumentType;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTeacherDao extends BaseJdbcDao implements TeacherDao {

    private static final String SELECT_COLUMNS = "SELECT id_profesor, tipo_documento, numero_documento, "
            + "primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, correo_electronico, telefono, "
            + "especializacion, fecha_contratacion, estado FROM profesores ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY primer_apellido, primer_nombre";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE id_profesor = ?";
    private static final String INSERT = "INSERT INTO profesores (tipo_documento, numero_documento, primer_nombre, "
            + "segundo_nombre, primer_apellido, segundo_apellido, correo_electronico, telefono, especializacion, "
            + "fecha_contratacion, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE profesores SET tipo_documento = ?, numero_documento = ?, "
            + "primer_nombre = ?, segundo_nombre = ?, primer_apellido = ?, segundo_apellido = ?, "
            + "correo_electronico = ?, telefono = ?, especializacion = ?, fecha_contratacion = ?, estado = ? "
            + "WHERE id_profesor = ?";
    private static final String DELETE = "DELETE FROM profesores WHERE id_profesor = ?";

    @Override
    public List<Teacher> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapTeacher(resultSet));
    }

    @Override
    public Teacher findById(int teacherId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapTeacher(resultSet), teacherId);
    }

    @Override
    public int insert(Teacher teacher) {
        return executeInsert(INSERT, teacher.getDocumentType().getDbValue(), teacher.getDocumentNumber(),
                teacher.getFirstName(), teacher.getMiddleName(), teacher.getLastName(), teacher.getSecondLastName(),
                teacher.getEmail(), teacher.getPhone(), teacher.getSpecialization(), teacher.getHireDate(),
                teacher.getStatus().getDbValue());
    }

    @Override
    public void update(Teacher teacher) {
        executeUpdate(UPDATE, teacher.getDocumentType().getDbValue(), teacher.getDocumentNumber(),
                teacher.getFirstName(), teacher.getMiddleName(), teacher.getLastName(), teacher.getSecondLastName(),
                teacher.getEmail(), teacher.getPhone(), teacher.getSpecialization(), teacher.getHireDate(),
                teacher.getStatus().getDbValue(), teacher.getId());
    }

    @Override
    public void delete(int teacherId) {
        executeUpdate(DELETE, teacherId);
    }

    private Teacher mapTeacher(ResultSet resultSet) throws SQLException {
        Teacher teacher = new Teacher(DocumentType.fromDbValue(resultSet.getString("tipo_documento")),
                resultSet.getString("numero_documento"), resultSet.getString("primer_nombre"),
                resultSet.getString("primer_apellido"), resultSet.getString("correo_electronico"),
                getDate(resultSet, "fecha_contratacion"));
        teacher.setId(resultSet.getInt("id_profesor"));
        teacher.setMiddleName(resultSet.getString("segundo_nombre"));
        teacher.setSecondLastName(resultSet.getString("segundo_apellido"));
        teacher.setPhone(resultSet.getString("telefono"));
        teacher.setSpecialization(resultSet.getString("especializacion"));
        teacher.setStatus(TeacherStatus.fromDbValue(resultSet.getString("estado")));
        return teacher;
    }
}
