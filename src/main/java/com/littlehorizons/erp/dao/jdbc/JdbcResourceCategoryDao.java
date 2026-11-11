package com.littlehorizons.erp.dao.jdbc;

import com.littlehorizons.erp.dao.ResourceCategoryDao;
import com.littlehorizons.erp.model.ResourceCategory;

import java.util.List;

public class JdbcResourceCategoryDao extends BaseJdbcDao implements ResourceCategoryDao {

    private static final String FIND_ALL = "SELECT id_categoria, nombre_categoria, descripcion "
            + "FROM categorias_recurso ORDER BY id_categoria";

    @Override
    public List<ResourceCategory> findAll() {
        return queryForList(FIND_ALL, resultSet -> new ResourceCategory(resultSet.getInt("id_categoria"),
                resultSet.getString("nombre_categoria"), resultSet.getString("descripcion")));
    }
}
