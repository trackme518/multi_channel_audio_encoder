import java.util.Scanner;
import java.util.regex.Pattern;

//import java.SystemUtils;
//import java.ProcessHandle;
//import java.Process.ProcessHandle;

//import java.util.stream.Stream;
//import java.util.Optional;
//import java.util.stream.Stream;

ArrayList<RunnableThread> processes = new ArrayList<RunnableThread>(); //keep track of currently running processes

void updateProcesses() {
  //check all processes if they have finished

  ArrayList<RunnableThread> running = new ArrayList<RunnableThread>();


  for (int i =0; i<processes.size(); i ++) {
    if ( processes.get(i).finished != true ) {
      running.add( processes.get(i) );
      //get current progress in percent - trim to 3 decimal places if necessary
      String percent = String.valueOf( processes.get(i).progress );
      if ( percent.length() > 4 ) {
        percent = percent.substring(0, 4);
      }
      //processes.get(i).threadName
      text( processes.get(i).threadName + " " + percent +"%", width - 250, lineHeight * (i) + offsetTop );
    } else {
      text( processes.get(i).threadName + " DONE", width - 250, lineHeight * (i) + offsetTop );
    }
  }
  //remove finished processes from the list
  processes = running;
  //println("size of processes after purge "+ processes.size() );
}

//class that implmenets multi threading and shell commands
class RunnableThread implements Runnable {
  private Thread t;
  String threadName;
  String[] commands;

  boolean running = false;
  boolean started = false;
  boolean finished = false;

  int id;


  ProcessBuilder pb;
  Process p;
  long pid;

  float progress = 0; //values retrieved from command output - ffmpeg specific
  float duration = 0;

  RunnableThread( String name, String[] myCmds) {
    threadName = name;
    commands = myCmds;
    id = int( random(0, 1000000) );

    //threadName = nf(id, 6);

    System.out.println("Creating " +  threadName );

    //start terminal command
    try {
      pb = new ProcessBuilder( commands );
      pb.redirectErrorStream(true);
      p = pb.start();
      //not working - processing is using old Java 8...sad I need java 9 for this :-D
      //ProcessHandle processHandle = p.toHandle();
      // System.out.printf("PID: %s%n", processHandle.pid());
      //System.out.printf("isAlive: %s%n", processHandle.isAlive());
      //https://www.logicbig.com/tutorials/core-java-tutorial/java-9-changes/process-handle.html
      //pid = ProcessHandle.current().pid();
      //pid = p.pid();
    }
    catch (Exception e) {
      System.out.println(e);
    }

    start(); //start the new thread
  }

  public void run() {
    System.out.println("Running " +  threadName );
    running = true;
    //try {
    //System.out.println("Thread: " + threadName + "started");
    //Thread.sleep(1);

    //catch the output of ffmpeg from error stream and parse it for progress 

    try {
      BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while (true) {
        line = r.readLine();
        if (line == null) { 
          break;
        }
        System.out.println(line);

        //Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
        String[] found = match(line, "(?<=Duration: )[^,]*");
        if (found != null) { 
          String[] hms = found[0].split(":");
          if ( hms.length > 1 ) { 
            duration = float(hms[0]) * 3600 +float(hms[1]) *   60 + float(hms[2]);
          }

        }

        found = match(line, "(?<=time=)[\\d:.]*");
        if (found != null) { 
          String[] params = found[0].split(":");
          if ( params.length >1) {
            progress = ( float(params[0]) * 3600 + float(params[1]) * 60 + float(params[2]) ) / duration;
          }
          //println( "match: "+found[0] );
        }
      }
    }
    catch (Exception e) {
      println(e);
    }
    
    if(!dataFolderOpened){
      runCmd(openFolderCmd);
      dataFolderOpened = true;
    }
    
    finished = true;
    Thread.currentThread().interrupt(); //will stop the thread and notify owner - me, but i does not exists anymore so it did not notify anybody...i guess
    //Thread.currentThread().destroy(); //depracated 
    return;
  }

  public void start () {
    System.out.println("Starting " +  threadName );
    if (t == null) {
      t = new Thread (this, threadName);
      t.start ();
      started = true;
    }
  }
}
