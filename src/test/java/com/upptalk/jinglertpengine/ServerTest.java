package com.upptalk.jinglertpengine;

import org.junit.Test;

/**
 * @author bhlangonijr
 *         Date: 5/21/14
 *         Time: 3:45 PM
 */
public class ServerTest {


    @Test
    public void run() {

        System.out.println("Classpath: " + getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        System.setProperty("jinglertpengine.home",
                getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        Main.main(new String[]{"start", "jinglertpengine-test.xml", "log4j.xml"});

    }

}
