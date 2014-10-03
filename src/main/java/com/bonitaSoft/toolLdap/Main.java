package com.bonitaSoft.toolLdap;

import com.bonitaSoft.tools.LocalStorage;
import com.bonitaSoft.tools.TestCallbackHandler;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.logging.*;

/**
 * Created by Fabrice on 02/10/2014.
 */
public class Main {

    protected static Logger logger = Logger.getLogger("com.bonitaSoft.toolLdap.Main");

    protected static Console console = System.console();

    private static final String fNEW_LINE = System.getProperty("line.separator");

    private static final String strAppName = "BonitaSoft LDAP tool testing";

    private static LocalStorage localStorage = new LocalStorage();

    public static void main(String[] args) {
        init();

        scenario();

        finaly();
    }

    public static Boolean scenario() {
        String pathJassFile = getPathJassFile();

        if (pathJassFile == null) {
            return false;
        } else {
            localStorage.set("pathJassFile", pathJassFile);

        }

        String userName = getInfo("user name for test", "myUserName");

        if (userName == null) {
            return false;
        } else {
            localStorage.set("userName", userName);
        }

        String password = getInfo("password for test", "myPassword");

        if (password == null) {
            return false;
        } else {
            localStorage.set("password", password);
        }

        String idConf = getInfo("JAAS context name", "myConf");

        if (idConf == null) {
            return false;
        } else {
            localStorage.set("idConf", idConf);
        }

        System.setProperty("java.security.auth.login.config", pathJassFile);

        try {
            LoginContext lc = new LoginContext(idConf, new TestCallbackHandler(userName, password));
            lc.login();
            message("Login success.", false, true);
        } catch (LoginException e) {
            message("Login fail, error : "+e.getMessage(), false, true);
            logger.severe(e.getCause().toString());
        }

        String strRetourYN = message("You want to retry ? : ", true, true);
        if (strRetourYN.equals("yes")||(strRetourYN.equals("y"))) {
            Boolean boolRetour = scenario();
        }

        return true;
    }

    public static String getPathJassFile() {
        String pathJassFile;

        pathJassFile = message("Please indicate your path for the JAAS configuration file (ex : C:\\config.ldap) : ", true, true);

        File f = new File(pathJassFile);
        if (f.exists()) {
            String strRetour = message("Your path for JAAS file is correct ? (yes or no) - " + pathJassFile + " : ", true, true);
            if (!strRetour.equals("yes")&&(!strRetour.equals("y"))) {
                pathJassFile = getPathJassFile();
            }
        } else {
            String strRetour = message("Your path for JAAS file is wrong, try again ? (yes or no) - " + pathJassFile + " : ", true, true);
            if (strRetour.equals("yes")||(strRetour.equals("y"))) {
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

        if (!strRetour.equals("")) {
            String strRetourYN = message("Your " + label + " is correct ? (yes or no) - " + strRetour + " : ", true, true);
            if (!strRetourYN.equals("yes")&&(!strRetourYN.equals("y"))) {
                strRetour = getInfo(label, example);
            }
        } else {
            String strRetourYN = message("Your user name is wrong, try again ? (yes or no) - " + strRetour + " : ", true, true);
            if (strRetourYN.equals("yes")||(strRetourYN.equals("y"))) {
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
            strRetour = console.readLine(msg);
            logger.info(strRetour);
        } else {
            console.printf(msg);
            console.printf(fNEW_LINE);
            if (inLog) {
                logger.info(msg);
            }
        }

        return strRetour;
    }

    private static String readFile(String file) {
        try {
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(file));

            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            message("Error : "+e.getMessage(), false, true);
            return null;
        } catch (IOException e) {
            message("Error : "+e.getMessage(), false, true);
            return null;
        }
    }
}
