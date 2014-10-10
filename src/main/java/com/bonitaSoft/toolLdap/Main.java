package com.bonitaSoft.toolLdap;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import com.bonitaSoft.tools.TestCallbackHandler;

/**
 * @author Fabrice Rosito
 * @author Antoine Mottier
 */
public class Main {

    /** Folder to store log files */
    private static final String LOGS_FOLDER = "logs";

    /** Max log file size in byte before rotation */
    private static final int MAX_LOG_FILE_SIZE = 9000000;

    /** Logger to generate log file */
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /** Line separator used for log files */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** Application name */
    private static final String APP_NAME = "Bonitasoft LDAP testing tool";

    /** Access to Operating System console */
    private static final Console console = System.console();

    /**
     * Main program method. Doesn't require any arguments.
     *
     * @param args
     *            unused
     */
    public static void main(String[] args) {
	init();

	scenario();

	exiting();
    }

    /**
     * Initialize logs system and display welcome message.
     */
    private static void init() {
	// Test if the logs directory exist, if not create it
	File logsDir = new File(LOGS_FOLDER);

	if (logsDir.exists() == false) {
	    try {
		logsDir.mkdir();
	    } catch (SecurityException se) {
		System.err.println("Failed to create folder for logs file. Program will terminate.");
		System.exit(-1);
	    }
	}

	// Create log file and configure logger
	try {
	    Handler fh = new FileHandler("logs/toolLdap.%g.log", MAX_LOG_FILE_SIZE, 4, true);
	    fh.setFormatter(new SimpleFormatter());
	    LOGGER.addHandler(fh);
	} catch (IOException e) {
	    System.err.println("Failed to create logs file. Program will terminate.");
	    System.exit(-1);
	}
	LOGGER.setLevel(Level.FINE);
	LOGGER.setUseParentHandlers(false);

	consolePrintMessage("-----------------------");
	consolePrintMessage("Starting " + APP_NAME);
	consolePrintMessage("To quit hit Ctrl+C at anytime.");
	consolePrintMessage("-----------------------");
    }

    /**
     * Display exit message and exist with error code 0.
     */
    private static void exiting() {
	consolePrintMessage("-----------------------");
	consolePrintMessage("Existing " + APP_NAME);
	consolePrintMessage("-----------------------");
	System.exit(0);
    }

    /**
     * Actual program main code.
     */
    private static void scenario() {
	// Get the JAAS configuration file location
	String pathJassFile = askUserForJassCfgFilePath();

	// Get the username for the user we try to authenticate with on the LDAP server
	String username = consoleReadUserInputWithExample("user name", "myUserName");

	// Get the password for the user we try to authenticate with on the LDAP server
	String password = consoleReadUserInputWithExample("password", "myPassword");

	// Get the JAAS login context from user input
	// String jaasLoginContextName = consoleReadUserInputWithExample("JAAS login context name",
	// "BonitaAuthentication-1");
	String jaasLoginContextName = "BonitaAuthentication-1";

	// Set the JVM property for JAAS configuration with the JAAS configuration file path
	System.setProperty("java.security.auth.login.config", pathJassFile);

	try {
	    LoginContext lc = new LoginContext(jaasLoginContextName, new TestCallbackHandler(username, password));
	    lc.login();
	    consolePrintMessage("Configuration (" + pathJassFile + " - " + jaasLoginContextName
		    + ") has been successfully tested with provided username (" + username + ") and password");
	} catch (LoginException e) {
	    consolePrintMessage("Login fail, error : " + e.getMessage());
	}

	boolean tryAgain = consoleYesNoQuestion("Do you want to test another configuration? [Y,n]");
	if (tryAgain) {
	    scenario();
	}
    }

    private static void consolePrintMessage(String promptMessage) {
	console.printf(promptMessage + LINE_SEPARATOR);
	LOGGER.info(promptMessage);
    }

    private static String consoleReadUserInputWithExample(String inputDescription, String valueExample) {
	String userInput = null;

	String promptMessage = null;

	if ((valueExample != null) && (valueExample.isEmpty() == false)) {
	    promptMessage = inputDescription + " (example: " + valueExample + "): ";
	} else {
	    promptMessage = inputDescription;
	}

	LOGGER.info(promptMessage);

	userInput = console.readLine(promptMessage);
	LOGGER.info(userInput);

	if ((userInput == null) || userInput.isEmpty()) {

	    boolean tryAgain = consoleYesNoQuestion("Empty value is not a valid value for: " + inputDescription
		    + ". Enter another value? [Y/n]");

	    if (tryAgain) {
		userInput = consoleReadUserInputWithExample(inputDescription, valueExample);
	    } else {
		exiting();
	    }
	}

	return userInput;
    }

    private static boolean consoleYesNoQuestion(String promptMessage) {
	boolean result = false;

	String userInput;

	do {
	    userInput = console.readLine(promptMessage);
	} while ((testIsYes(userInput) == false) && (testIsNo(userInput) == false));

	if (testIsYes(userInput)) {
	    result = true;
	}

	return result;
    }

    private static boolean testIsYes(String userInput) {
	boolean isYes = ((userInput == null) || (userInput.isEmpty() == true) || userInput.equalsIgnoreCase("y") || userInput
		.equalsIgnoreCase("yes"));
	return isYes;
    }

    private static boolean testIsNo(String userInput) {
	boolean isNo;
	if (userInput == null) {
	    isNo = false;
	} else {
	    isNo = ((userInput.equalsIgnoreCase("n")) || (userInput.equalsIgnoreCase("no")));
	}
	return isNo;
    }

    private static String askUserForJassCfgFilePath() {
	String jaasFilePath;

	jaasFilePath = consoleReadUserInputWithExample("Path for the JAAS configuration file", "C:\\config.ldap");

	File f = new File(jaasFilePath);
	if (f.exists() == false) {
	    boolean tryAgain = consoleYesNoQuestion("JAAS file path is invalid (file doesn't exist), try again? [Y,n]");
	    if (tryAgain) {
		jaasFilePath = askUserForJassCfgFilePath();
	    } else {
		exiting();
	    }
	}

	return jaasFilePath;
    }

}
