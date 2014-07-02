package com.upptalk.jinglertpengine;

/**
 * @author bhlangonijr
 *         Date: 5/21/14
 *         Time: 3:45 PM
 */
public class ServerTest {


    public static void main(String[] args) {

        System.out.println("Classpath: " + Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        System.setProperty("jinglertpengine.home",
                Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        Main.main(new String[]{"start", "jinglertpengine-test.xml", "log4j.xml"});

    }

}
