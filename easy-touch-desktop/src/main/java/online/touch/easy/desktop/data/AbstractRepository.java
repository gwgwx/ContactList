/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.data;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import online.touch.easy.common.model.Identifiable;

/**
 *
 * @author George-2014
 * @param <T>
 */
public abstract class AbstractRepository<T extends Identifiable> implements Serializable {

    private static final Logger LOG = Logger.getLogger(AbstractRepository.class.getName());

    private final Class<T> entityClass;

    protected final javax.ws.rs.client.WebTarget webTarget;
    private final javax.ws.rs.client.Client client;
    private static final String BASE_URI = "http://localhost:8080/easy-touch/api";

    public AbstractRepository(Class<T> entityClass, String path) {
        this.entityClass = entityClass;
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path(path);
    }

    public void create(T entity) {
        Response response = webTarget
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(javax.ws.rs.client.Entity.entity(entity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
        try {
            if (response.getStatus() == 200) {
            }
        } finally {
            response.close();
        }
    }

    public T edit(T entity) {
        Response response = webTarget
                .path(java.text.MessageFormat.format("{0}", new Object[]{entity.getId()}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .put(javax.ws.rs.client.Entity.entity(entity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
        try {
            if (response.getStatus() == 200) {
            }
        } finally {
            response.close();
        }
        return entity;
    }

    public void saveOrUpdate(T entity) {
        if (entity.getId() != null) {
            edit(entity);
        } else {
            create(entity);
        }
    }

    public void remove(T entity) {
        Response response = webTarget
                .path(java.text.MessageFormat.format("{0}", new Object[]{entity.getId()}))
                .request()
                .delete();
        
        try {
            if (response.getStatus() == 200) {
            }
        } finally {
            response.close();
        }
    }

    public T find(Long id) {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(entityClass);
    }

//    public T findByUid(String uid) {
//        return super.get(uid);
//    }
////    @Transactional
//    public int getRowCount(Set<AbstractSpecification> filterValues) {
//        return super.getAll().size();
//    }
    public <T> T findAll(Class<T> responseType) throws javax.ws.rs.ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

}
