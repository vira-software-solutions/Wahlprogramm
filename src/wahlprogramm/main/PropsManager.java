/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package main;

import java.io.IOException;
import java.util.Properties;

public final class PropsManager {
    public static final Properties Props = new Properties();
    private static boolean IsInitialized=false;

    public static final void init() throws IOException {
        if(IsInitialized){
            return;
        }

        IsInitialized = true;
        Props.load(PropsManager.class.getResource("/propsManager/app.config").openStream());
    }
}
