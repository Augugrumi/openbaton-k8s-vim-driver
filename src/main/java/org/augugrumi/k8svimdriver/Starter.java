package org.augugrumi.k8svimdriver;

import org.openbaton.plugin.PluginStarter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;

public class Starter {
    public static void main(String[] args)
        throws NoSuchMethodException, IOException, InstantiationException, TimeoutException,
        IllegalAccessException, InvocationTargetException, InterruptedException {
        if (args.length == 4) {
            PluginStarter.registerPlugin(
                K8sVimDriver.class,
                args[0],
                args[1],
                Integer.parseInt(args[2]),
                Integer.parseInt(args[3]));
        } else {
            PluginStarter.registerPlugin(K8sVimDriver.class, "kubernetes", "localhost", 5672, 10);
        }
    }
}
