package com.bonitaSoft.toolLdap;

import com.bonitaSoft.tools.LocalStorage;
import com.bonitaSoft.tools.ToolCallbackHandler;

import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Scanner;
import java.util.logging.*;

/**
 * Created by Fabrice on 02/10/2014.
 */
public class Main {

    protected static Logger logger = Logger.getLogger("com.bonitaSoft.toolLdap.Main");

    private static final String strAppName = "BonitaSoft LDAP tool testing";

    private static LocalStorage localStorage = new LocalStorage();

    private static boolean idem = false;

    public static void main(String[] args) {
        init();

        scenario();

        finaly();
    }

    public static Boolean scenario() {
        if(!idem){
            message("-----------------------", false, false);
            message("Infos:", false, false);

            //---------------------------
            //JAAS FILE
            String pathJassFile = getPathJassFile();
            System.setProperty("java.security.auth.login.config", pathJassFile);

            if (pathJassFile == null) {
                message("Fail, impossible to load jass file.", false, true);
                return false;
            } else {
                localStorage.set("pathJassFile", pathJassFile);
            }

            //---------------------------
            //USER NAME
            String userName = getInfo("user name for test", "myUserName");

            if (userName == null) {
                return false;
            } else {
                localStorage.set("userName", userName);
            }

            //---------------------------
            //PASSWORD
            String password = getInfo("password for test", "myPassword");

            if (password == null) {
                return false;
            } else {
                localStorage.set("password", password);
            }

            //---------------------------
            //CONF
            String idConf = getInfo("JAAS context name", "BonitaAuthentication-1");

            if (idConf == null) {
                return false;
            } else {
                localStorage.set("idConf", idConf);
            }

            message("-----------------------", false, false);
            message("Context:", false, false);
            message("- jaas file: "+pathJassFile, false, false);
            message("- user name: "+userName, false, false);
            message("- user password: "+password, false, false);
            message("- JAAS context name: "+idConf, false, false);
        }

        message("-----------------------", false, false);
        LoginContext lc = null;
        try {
            Configuration config = java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Configuration>() {
                public Configuration run() {
                    return Configuration.getConfiguration();
                }
            });
            config.refresh();
            lc = new LoginContext(localStorage.get("idConf"), new ToolCallbackHandler(localStorage.get("userName"), localStorage.get("password")));
            lc.login();
            message("Response:", false, false);
            message("Login success.", false, true);
        } catch (LoginException e) {
            message("Response:", false, false);
            message("Login fail, error : " + e.getMessage(), false, true);
        }finally {
            if(lc != null){
                try {
                    lc.logout();
                } catch (LoginException e) {
                    e.printStackTrace();
                }
            }
        }

        //LOOP
        idem = false;
        String strRetourYN = message("You want to retry ? (idem by default, yes or no) : ", true, true);
        if (strRetourYN.equals("idem")||(strRetourYN.isEmpty())) {
            idem = true;
            Boolean boolRetour = scenario();
        }else if(strRetourYN.equals("yes")||(strRetourYN.equals("y"))){
            Boolean boolRetour = scenario();
        }

        return true;
    }

    public static String getPathJassFile() {
        String pathJassFile;

        pathJassFile = message("Please indicate your path for the JAAS configuration file (ex : C:\\config.ldap) : ", true, true);

        File f = new File(pathJassFile);
        if (!f.exists()) {
            String strRetourYN = message("Your path for JAAS file is wrong, try again ? (yes by default or no) - " + pathJassFile + " : ", true, true);
            if (strRetourYN.equals("yes")||(strRetourYN.equals("y"))&&(!strRetourYN.isEmpty())) {
                pathJassFile = getPathJassFile();
            } else {
                pathJassFile = null;
            }
        }

        return pathJassFile;
    }

    public static String getInfo(String label, String example) {
        String strRetour;

        strRetour = message("Please indicate your " + label + " (ex : " + example + ") : ", true, true);

        if (strRetour.equals("")) {
            String strRetourYN = message("Your user name is wrong, try again ? (yes by default or no) - " + strRetour + " : ", true, true);
            if (strRetourYN.equals("yes")||(strRetourYN.equals("y"))&&(!strRetourYN.isEmpty())) {
                strRetour = getInfo(label, example);
            } else {
                strRetour = null;
            }
        }

        return strRetour;
    }

    public static void init() {
        File theDir = new File("logs");
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }

        File tmpDir = new File("tmps");
        // if the directory does not exist, create it
        if (!tmpDir.exists()) {
            try {
                tmpDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }

        try {
            Handler fh = new FileHandler("logs/toolLdap.%g.log", 9000000, 4, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setLevel(Level.FINE);
        logger.setUseParentHandlers(false);

        message("-----------------------", false, false);
        message("Starting " + strAppName, false, false);
        message("-----------------------", false, false);
        message("Welcome!", false, true);
        message("Interuption as possible with Ctrl+C.", false, true);
    }

    public static void finaly() {
        message("-----------------------", false, false);
        message("Finish toolLdap", false, false);
        message("-----------------------", false, false);
    }

    public static String message(String msg, Boolean retour, Boolean inLog) {
        String strRetour = null;
        if (retour) {
            logger.info(msg);
            System.out.print(msg);
            Scanner scanner = new Scanner(System.in);
            strRetour = scanner.nextLine();
            logger.info(strRetour);
        } else {
            System.out.println(msg);
            if (inLog) {
                logger.info(msg);
            }
        }

        return strRetour;
    }
}
