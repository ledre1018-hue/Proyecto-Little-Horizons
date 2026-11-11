package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.GuardianDao;
import com.littlehorizons.erp.model.DocumentType;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.StudentGuardian;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcGuardianDao extends BaseJdbcDao implements GuardianDao {

    private static final String SELECT_COLUMNS = "SELECT a.id_acudiente, a.tipo_documento, a.numero_documento, "
            + "a.primer_nombre, a.segundo_nombre, a.primer_apellido, a.segundo_apellido, a.parentesco, "
            + "a.telefono_principal, a.telefono_secundario, a.correo_electronico, a.direccion, a.ocupacion, "
            + "a.es_responsable_financiero FROM acudientes a ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY a.primer_apellido, a.primer_nombre";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE a.id_acudiente = ?";
    private static final String FIND_BY_DOCUMENT = SELECT_COLUMNS + "WHERE a.numero_documento = ?";
    private static final String FIND_BY_STUDENT = "SELECT a.id_acudiente, a.tipo_documento, a.numero_documento, "
            + "a.primer_nombre, a.segundo_nombre, a.primer_apellido, a.segundo_apellido, a.parentesco, "
            + "a.telefono_principal, a.telefono_secundario, a.correo_electronico, a.direccion, a.ocupacion, "
            + "a.es_responsable_financiero, ea.id_estudiante, ea.es_principal, ea.es_contacto_emergencia "
            + "FROM acudientes a JOIN estudiante_acudiente ea ON ea.id_acudiente = a.id_acudiente "
            + "WHERE ea.id_estudiante = ? ORDER BY ea.es_principal DESC";
    private static final String INSERT = "INSERT INTO acudientes (tipo_documento, numero_documento, "
            + "primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, parentesco, telefono_principal, "
            + "telefono_secundario, correo_electronico, direccion, ocupacion, es_responsable_financiero) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE acudientes SET tipo_documento = ?, numero_documento = ?, "
            + "primer_nombre = ?, segundo_nombre = ?, primer_apellido = ?, segundo_apellido = ?, parentesco = ?, "
            + "telefono_principal = ?, telefono_secundario = ?, correo_electronico = ?, direccion = ?, "
            + "ocupacion = ?, es_responsable_financiero = ? WHERE id_acudiente = ?";
    private static final String DELETE = "DELETE FROM acudientes WHERE id_acudiente = ?";
    private static final String INSERT_LINK = "INSERT INTO estudiante_acudiente (id_estudiante, id_acudiente, "
            + "es_principal, es_contacto_emergencia) VALUES (?, ?, ?, ?)";

    @Override
    public List<Guardian> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapGuardian(resultSet));
    }

    @Override
    public Guardian findById(int guardianId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapGuardian(resultSet), guardianId);
    }

    @Override
    public Guardian findByDocument(String documentNumber) {
        return queryForObject(FIND_BY_DOCUMENT, resultSet -> mapGuardian(resultSet), documentNumber);
    }

    @Override
    public List<StudentGuardian> findByStudent(int studentId) {
        return queryForList(FIND_BY_STUDENT, resultSet -> new StudentGuardian(resultSet.getInt("id_estudiante"),
                mapGuardian(resultSet), resultSet.getBoolean("es_principal"),
                resultSet.getBoolean("es_contacto_emergencia")), studentId);
    }

    @Override
    public int insert(Guardian guardian) {
        return executeInsert(INSERT, guardianValues(guardian));
    }

    @Override
    public void update(Guardian guardian) {
        Object[] values = guardianValues(guardian);
        executeUpdate(UPDATE, values[0], values[1], values[2], values[3], values[4], values[5], values[6],
                values[7], values[8], values[9], values[10], values[11], values[12], guardian.getId());
    }

    @Override
    public void delete(int guardianId) {
        executeUpdate(DELETE, guardianId);
    }

    @Override
    public void linkToStudent(StudentGuardian link) {
        executeUpdate(INSERT_LINK, link.getStudentId(), link.getGuardian().getId(), link.isPrimaryGuardian(),
                link.isEmergencyContact());
    }

    private Object[] guardianValues(Guardian guardian) {
        return new Object[]{guardian.getDocumentType().getDbValue(), guardian.getDocumentNumber(),
                guardian.getFirstName(), guardian.getMiddleName(), guardian.getLastName(),
                guardian.getSecondLastName(), guardian.getRelationship(), guardian.getPhone(),
                guardian.getSecondaryPhone(), guardian.getEmail(), guardian.getAddress(), guardian.getOccupation(),
                guardian.isFinancialResponsible()};
    }

    private Guardian mapGuardian(ResultSet resultSet) throws SQLException {
        Guardian guardian = new Guardian(DocumentType.fromDbValue(resultSet.getString("tipo_documento")),
                resultSet.getString("numero_documento"), resultSet.getString("primer_nombre"),
                resultSet.getString("primer_apellido"), resultSet.getString("parentesco"),
                resultSet.getString("telefono_principal"));
        guardian.setId(resultSet.getInt("id_acudiente"));
        guardian.setMiddleName(resultSet.getString("segundo_nombre"));
        guardian.setSecondLastName(resultSet.getString("segundo_apellido"));
        guardian.setSecondaryPhone(resultSet.getString("telefono_secundario"));
        guardian.setEmail(resultSet.getString("correo_electronico"));
        guardian.setAddress(resultSet.getString("direccion"));
        guardian.setOccupation(resultSet.getString("ocupacion"));
        guardian.setFinancialResponsible(resultSet.getBoolean("es_responsable_financiero"));
        return guardian;
    }
}
