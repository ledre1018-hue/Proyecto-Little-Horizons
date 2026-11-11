package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.ResourceCategoryDao;
import com.littlehorizons.erp.dao.ResourceDao;
import com.littlehorizons.erp.model.Resource;
import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.ResourceStatus;
import com.littlehorizons.erp.util.BusinessRuleException;

import java.util.List;

/**
 * Business rules for the resource inventory.
 * The status Prestado is only set by the loan process, never by hand.
 */
public class ResourceController {

    private final ResourceDao resourceDao;
    private final ResourceCategoryDao categoryDao;

    public ResourceController(ResourceDao resourceDao, ResourceCategoryDao categoryDao) {
        this.resourceDao = resourceDao;
        this.categoryDao = categoryDao;
    }

    public List<Resource> listResources() {
        return resourceDao.findAll();
    }

    public List<Resource> listAvailableResources() {
        return resourceDao.findAll().stream()
                .filter(resource -> resource.isAvailable())
                .toList();
    }

    public List<ResourceCategory> listCategories() {
        return categoryDao.findAll();
    }

    public Resource findResource(int resourceId) {
        Resource resource = resourceDao.findById(resourceId);
        if (resource == null) {
            throw new BusinessRuleException("No existe un recurso con ID " + resourceId + ".");
        }
        return resource;
    }

    public int registerResource(Resource resource) {
        boolean codeExists = resourceDao.findAll().stream()
                .anyMatch(saved -> saved.getCode().equalsIgnoreCase(resource.getCode()));
        if (codeExists) {
            throw new BusinessRuleException("Ya existe un recurso con el código " + resource.getCode() + ".");
        }
        if (resource.getStatus() == ResourceStatus.BORROWED) {
            throw new BusinessRuleException("El estado Prestado solo se asigna al registrar un préstamo.");
        }
        return resourceDao.insert(resource);
    }

    public void updateResource(Resource resource) {
        Resource saved = findResource(resource.getId());
        boolean statusChanged = saved.getStatus() != resource.getStatus();
        if (statusChanged && (saved.getStatus() == ResourceStatus.BORROWED
                || resource.getStatus() == ResourceStatus.BORROWED)) {
            throw new BusinessRuleException("El estado Prestado solo cambia con préstamos y devoluciones.");
        }
        resourceDao.update(resource);
    }

    public void deleteResource(int resourceId) {
        Resource resource = findResource(resourceId);
        if (resource.getStatus() == ResourceStatus.BORROWED) {
            throw new BusinessRuleException("No se puede eliminar un recurso que está prestado.");
        }
        resourceDao.delete(resourceId);
    }
}
