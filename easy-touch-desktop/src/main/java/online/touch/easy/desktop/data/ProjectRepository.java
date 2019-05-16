/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.data;

import online.touch.easy.common.model.Project;



/**
 *
 * @author Georgios
 */
public class ProjectRepository extends AbstractRepository<Project> {
    
    private static ProjectRepository instance = null;

    private ProjectRepository() {
        super(Project.class, "projects");
    }

    public static ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepository();
        }
        return instance;
    }


}
