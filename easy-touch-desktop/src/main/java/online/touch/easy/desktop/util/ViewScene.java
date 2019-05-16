/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.util;

/**
 * A View Scene loads inside the Main Scene. Various commands and options are
 * set on the Main Scene which are specific to the loaded view.
 *
 * @author George
 */
public abstract class ViewScene {

    private MainScene mainScene;

    protected MainScene getMainScene() {
        return mainScene;
    }

    public void setMainScene(MainScene mainScene) {
        this.mainScene = mainScene;
    }

}
