package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.ResourceDao;
import com.littlehorizons.erp.model.Resource;
import com.littlehorizons.erp.model.ResourceStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcResourceDao extends BaseJdbcDao implements ResourceDao {

    private static final String SELECT_COLUMNS = "SELECT r.id_recurso, r.codigo_recurso, r.nombre_recurso, "
            + "r.id_categoria, c.nombre_categoria, r.descripcion, r.fecha_adquisicion, r.ubicacion_fisica, "
            + "r.estado_recurso FROM recursos r JOIN categorias_recurso c ON c.id_categoria = r.id_categoria ";
    private static final String FIND_ALL = SELECT_COLUMNS + "ORDER BY r.codigo_recurso";
    private static final String FIND_BY_ID = SELECT_COLUMNS + "WHERE r.id_recurso = ?";
    private static final String INSERT = "INSERT INTO recursos (codigo_recurso, nombre_recurso, id_categoria, "
            + "descripcion, fecha_adquisicion, ubicacion_fisica, estado_recurso) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE recursos SET codigo_recurso = ?, nombre_recurso = ?, "
            + "id_categoria = ?, descripcion = ?, fecha_adquisicion = ?, ubicacion_fisica = ?, estado_recurso = ? "
            + "WHERE id_recurso = ?";
    private static final String DELETE = "DELETE FROM recursos WHERE id_recurso = ?";

    @Override
    public List<Resource> findAll() {
        return queryForList(FIND_ALL, resultSet -> mapResource(resultSet));
    }

    @Override
    public Resource findById(int resourceId) {
        return queryForObject(FIND_BY_ID, resultSet -> mapResource(resultSet), resourceId);
    }

    @Override
    public int insert(Resource resource) {
        return executeInsert(INSERT, resource.getCode(), resource.getName(), resource.getCategoryId(),
                resource.getDescription(), resource.getAcquisitionDate(), resource.getLocation(),
                resource.getStatus().getDbValue());
    }

    @Override
    public void update(Resource resource) {
        executeUpdate(UPDATE, resource.getCode(), resource.getName(), resource.getCategoryId(),
                resource.getDescription(), resource.getAcquisitionDate(), resource.getLocation(),
                resource.getStatus().getDbValue(), resource.getId());
    }

    @Override
    public void delete(int resourceId) {
        executeUpdate(DELETE, resourceId);
    }

    private Resource mapResource(ResultSet resultSet) throws SQLException {
        Resource resource = new Resource(resultSet.getString("codigo_recurso"), resultSet.getString("nombre_recurso"),
                resultSet.getInt("id_categoria"), resultSet.getString("ubicacion_fisica"));
        resource.setId(resultSet.getInt("id_recurso"));
        resource.setCategoryName(resultSet.getString("nombre_categoria"));
        resource.setDescription(resultSet.getString("descripcion"));
        resource.setAcquisitionDate(getDate(resultSet, "fecha_adquisicion"));
        resource.setStatus(ResourceStatus.fromDbValue(resultSet.getString("estado_recurso")));
        return resource;
    }
}
