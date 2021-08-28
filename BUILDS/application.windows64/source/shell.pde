
String OS = "";
boolean isWin = false;
boolean isMac = false;
String openFolderCmd;
boolean dataFolderOpened = false;

public String getOperatingSystem() {
  String os = System.getProperty("os.name");
  os = os.toLowerCase();

  if ( os.indexOf("win") != -1 ) {
    isWin = true;
    println("is windows machine");
  }
  if ( os.indexOf("mac") != -1 ) {
    isMac = true;
    println("is mac machine");
  }
  // System.out.println("Using System Property: " + os);
  return os;
}

void runCmd(String cmdstr) {
  try {
    Runtime.getRuntime().exec(cmdstr);
    //Process Return = Runtime.getRuntime().exec(cmdstr); //run the shell file
    //println(Return);
  } 
  catch (IOException e) {
    e.printStackTrace();
  }
}

void runCmdReturn(String[] args) {
  //String output = "shell output: ";
  String output = "shell output: ";
  Runtime rt = Runtime.getRuntime();
  String[] commands = args;//{"ffmpeg", "-h"};
  try {
    Process proc = rt.exec(commands);
    //long pid = proc.pid();    
    BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

    // Read the output from the command
    //System.out.println("Here is the standard output of the command:\n");
    String s = null;
    while ((s = stdInput.readLine()) != null) {
      output += s;
      //println(s);
      //System.out.println(s);
    }

    // Read any errors from the attempted command
    //System.out.println("Here is the standard error of the command (if any):\n");
    while ((s = stdError.readLine()) != null) {
      output += s;
      //println(s);
      //System.out.println(s);
    }
  }
  catch (Exception e) {
    //println(e);
  }
  //return output;
}

void cmdTest(String[] args) {
  //String terminalApp = "";
  ProcessBuilder builder = null;
  String arg = String.join(" ", args);
  
  if (isWin) {
    //builder = new ProcessBuilder( cmdMerge );
    builder = new ProcessBuilder( "cmd.exe", "/c", arg );
    //terminalApp = "cmd.exe";
  }

  if (isMac) {
    //builder = new ProcessBuilder( "/bin/bash", arg );
  }

  //ProcessBuilder builder = new ProcessBuilder( terminalApp, "/c", arg ); // /b will run in background "/c"
  builder.redirectErrorStream(true);
  try {
    Process p = builder.start();
    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line;
    while (true) {
      line = r.readLine();
      if (line == null) { 
        break;
      }
      System.out.println(line);
    }
  }
  catch (Exception e) {
    println(e);
  }
}
