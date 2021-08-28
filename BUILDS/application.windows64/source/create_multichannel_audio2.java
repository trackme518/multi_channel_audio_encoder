import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.File; 
import java.nio.file.Path; 
import java.nio.file.Paths; 
import drop.*; 
import controlP5.*; 
import java.io.*; 
import java.util.Arrays; 
import java.util.Map; 
import java.util.Set; 
import java.util.List; 
import java.util.Scanner; 
import java.util.regex.Pattern; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class create_multichannel_audio2 extends PApplet {

//drop files and issue ffmpeg commands





SDrop drop;
ArrayList<AudioFile> audiofiles = new ArrayList<AudioFile>();
String ffmpegPath = null;

String[] currChans = null;
String currChanLayout = "No matching layout found!";
boolean matchfound = false;

long currTime;
int readInterval = 1000;
int progress = 0; //read log file and check for progress

public void setup() {
  
  frameRate(30);

  OS = getOperatingSystem();
  println(OS);

  surface.setTitle("TRICK THE EAR - multichannel audio files generator");
  surface.setResizable(true);

  initLayouts();

  if (isWin) {
    ffmpegPath = dataPath("FFMPEG\\ffmpeg-4_4_essentials_build\\bin\\ffmpeg.exe"); //ffmpeg-4.4-full_build
    openFolderCmd = "explorer.exe \""+dataPath("")+"\\\" ";
  }
  if (isMac) {
    ffmpegPath = dataPath("FFMPEG")+"/ffmpeg";
    openFolderCmd = "open "+dataPath("")+"/";
    println("open folder cmd:");
    println(openFolderCmd);
  }
  
  println(ffmpegPath);
  Path path = Paths.get(ffmpegPath);
  File file = path.toFile();
  if ( file.isFile() ) {
    //killffmpegCmd = "taskkill /F /IM \""+ffmpegPath+"\"";
    println("ffmpeg library loaded");
    //println(killffmpegCmd);
  } else {
    println("ffmpeg is missing");
    ffmpegPath = null;
  }

  textSize(18);

  drop = new SDrop(this);
  stroke(255);

  intiGUI();

  currTime = millis();

}


public void draw() {

  //updateProcesses();
  
  background( yellow );

  fill(black);
  if (activeTab == "default") {
    
    updateProcesses();
    
    if ( audiofiles.size() == 0) {
      text("Drag and drop audio files", offset_right, offsetTop );
    } else {
      text("Audio layout: "+currChanLayout, offset_right, offsetTop);
      for (int i=0; i<audiofiles.size(); i++) {
        audiofiles.get(i).display(i, offsetTop + lineHeight);
      }
    }
  }

  if (activeTab == "help") {
    /*
    String status = "Avaliable layouts:\n";
     for ( int i=0; i < audiolayouts.size(); i++ ) {
     status += audiolayouts.get(i).name+"\n";
     }
     */
    String status = "Select 1-8 audio files and click export.\n";
    //status += "Two multichannel files will be created.\n";
    //status += "Upload both and use them in TrickTheEar audio player.\n";
    //status += "Mozilla firefox does not support AAC,\n";
    //status += "Safari does not support OGG.\n";
    //status += "So you need to upload both.";
    text( status, offset_right, offsetTop );
  }

}


public void dropEvent(DropEvent theDropEvent) {
  // returns true if the dropped object is a file or folder.
  if ( theDropEvent.isFile() ) {
    if ( !theDropEvent.file().isDirectory() ) {
      matchfound = false;
      audiofiles.add( new AudioFile( theDropEvent.file() ) );
      updateLayouts(); //get current audio layout and channel names
    }
  }
  // returns the DropTargetDropEvent, for further information see
  // http://java.sun.com/j2se/1.4.2/docs/api/java/awt/dnd/DropTargetDropEvent.html
  //println("dropTargetDropEvent()\t"+theDropEvent.dropTargetDropEvent());
}

class AudioFile { 
  //println("Size: " + f.length());
  //println("Full path: " + f.getAbsolutePath());
  File track;
  String name;
  String path;
  int id;

  AudioFile ( File input ) {  
    track = input;
    name = track.getName();
    path = track.getAbsolutePath();
    id = PApplet.parseInt( random(0, 1000000) );
  }

  public void display(int index, int curroffsety) {
    String currChan = "?";
    String currName = name;
    if ( name.length() > 17 ) {
      String[] basename = split(name, ".");
      String extension = "";
      if (basename.length>1) {
        extension = basename[1];
      }
      currName = name.substring(0, 14)+"..."+extension;
    }
    if ( currChans!= null ) {
      if ( currChans.length > index ) {
        currChan = currChans[index];
      }
    }
    text( currChan+" "+currName, offset_right + 60, lineHeight * (index) + curroffsety );

    float buttony = lineHeight * (index) + curroffsety - 18;
    if ( cp5.getController("removefile_"+id) == null ) {
      //cp5.addBang("removefile_"+id)
      cp5.addButton("removefile_"+id)
        .setLabel("del")
        .setPosition(offset_right, buttony  )
        .setSize(40, 20)
        .setColorLabel(black);
      ;
    } else {
      cp5.getController("removefile_"+id).setPosition( offset_right, buttony );
    }
  }
}

public void updateLayouts() {
  for ( int i=0; i < audiolayouts.size(); i++ ) {
    if ( audiofiles.size() == audiolayouts.get(i).numChan ) {
      currChans = audiolayouts.get(i).channels;
      currChanLayout = audiolayouts.get(i).name;
      matchfound = true;
      break;
    }
  }//end update data
  if (!matchfound) {
    currChans = null;
    currChanLayout = "No matching layout found!";
  }
}

ControlP5 cp5;
String activeTab = "default";

int quality = 128; //bitrate to be used with ffmpeg export
//float qualityogg = 0;
boolean autoquality = true;

int offsetTop = 60;
int lineHeight = 30;
int offset_right = 30;

int buttonYsize = 20;
boolean multichannel_input = false;
boolean ogg_export = true;
boolean aac_export = true;
/*
color mustard = color(255, 212, 73);
 color orange = color(249, 166, 32);
 color blue = color(168, 213, 226);
 color green = color(84, 140, 47);
 color darkgreen = color(16, 73, 17);
 */

int pink = color(255, 99, 146);
int yellow = color(255, 228, 94);
int beige = color(249, 249, 249);
int blue = color(127, 200, 248);
int jeans = color(90, 169, 230);
int black = color(0);
int white = color(255);
int orange = color(255, 128, 0);

//List inputList;
//ScrollableList cp5fileslist;
Textarea inputList;

public void intiGUI() {
  cp5 = new ControlP5(this);
  PFont p = createFont("Verdana", 16);
  ControlFont font = new ControlFont(p);
  cp5.setFont(font);

  cp5.setColorForeground(pink); //0xffaa0000
  cp5.setColorBackground(blue);
  cp5.setColorActive(jeans);
  //cp5.setColorLabel( color(0) );
  //255, 212, 73

  //cp5.getController("slider").moveTo("global");

  cp5.getTab("default")
    .activateEvent(true)
    .setColorActive(pink)
    .setLabel("inputs")
    .setId(1)
    ;

  cp5.addTab("settings")
    .setColorBackground(color(0, 160, 100))
    .setColorLabel(color(255))
    .setColorActive(orange)
    .activateEvent(true)
    .setId(2)
    ;

  cp5.addTab("help")
    .setColorBackground(jeans)
    .setColorLabel(color(255))
    .setColorActive(pink)
    .activateEvent(true)
    .setId(3)
    ;
  //-----------------------------

  cp5.addButton("export")
    .setPosition(offset_right, height-buttonYsize*2)
    .setSize(70, buttonYsize)
    .setColorLabel(black);
  ;
  cp5.addButton("clear")
    .setPosition(offset_right*2+70, height-buttonYsize*2)
    .setSize(70, buttonYsize)
    .setColorLabel(black);
  ;
  //---------------------------
  cp5.addSlider("quality")
    .setBroadcast(false)
    .setPosition(offset_right, offsetTop-buttonYsize)
    .setRange(64, 512)
    .setSize(200, buttonYsize)
    .moveTo("settings")
    .setColorLabel(black)
    .snapToTickMarks(true)
    .setNumberOfTickMarks(8)
    .setValue(quality)
    .setLabel("quality")
    .setBroadcast(true)
    ;
  /*
  cp5.addSlider("qualityogg")
   .setBroadcast(false)
   .setPosition(offset_right, offsetTop+buttonYsize)
   .setRange(0, 10)
   .setSize(200, buttonYsize)
   .moveTo("settings")
   .setColorLabel(black)
   .setValue(qualityogg)
   .setLabel("OGG quality")
   .setBroadcast(true)
   ;
   */
  cp5.addToggle("autoquality")
    .setPosition(offset_right, offsetTop+buttonYsize)
    .setSize(50, buttonYsize)
    .setValue(autoquality)
    .setMode(ControlP5.SWITCH)
    .setColorLabel(black)
    .setLabel("Automatic quality: "+autoquality)
    .moveTo("settings")
    ;

  cp5.addToggle("multichannel_input")
    .setPosition(offset_right, offsetTop+buttonYsize*4)
    .setSize(50, buttonYsize)
    .setValue(multichannel_input)
    .setMode(ControlP5.SWITCH)
    .setColorLabel(black)
    .setLabel("multichannel input: "+multichannel_input)
    .moveTo("settings")
    ;

  cp5.addToggle("ogg_export")
    .setBroadcast(false)
    .setPosition(offset_right, offsetTop+buttonYsize*7)
    .setSize(50, buttonYsize)
    .setValue(multichannel_input)
    .setMode(ControlP5.SWITCH)
    .setColorLabel(black)
    .setLabel("ogg export: "+ogg_export)
    .setValue(ogg_export)
    .setBroadcast(true)
    .moveTo("settings")
    ;

  cp5.addToggle("aac_export")
    .setBroadcast(false)
    .setPosition(offset_right, offsetTop+buttonYsize*10)
    .setSize(50, buttonYsize)
    .setValue(multichannel_input)
    .setMode(ControlP5.SWITCH)
    .setColorLabel(black)
    .setLabel("aac export: "+aac_export)
    .setValue(aac_export)
    .setBroadcast(true)
    .moveTo("settings")
    ;
  //----------------------------------
  cp5.addButton("tricktheear_web") 
    .setPosition(width-offset_right-60, height-buttonYsize*2)
    .setSize(60, buttonYsize)
    .setColorLabel(black)
    .moveTo("global")
    .setLabel("about");
}

public void quality(int val) {
  quality = val;
  //make sure we stay in avaliable values
  if ( quality%64 != 0 ) {
    quality = 128;
  }
  //println(quality);
}

public void autoquality(boolean val) {
  autoquality = val;
  cp5.getController("autoquality").setLabel("Automatic quality: "+autoquality);
}

public void ogg_export(boolean val) {
  ogg_export = val;
  cp5.getController("ogg_export").setLabel("ogg export: "+ogg_export);
}

public void aac_export(boolean val) {
  aac_export = val;
  cp5.getController("aac_export").setLabel("aac export: "+aac_export);
}

public void tricktheear_web() {
  link("https://www.tricktheear.eu");
}

public void multichannel_input(boolean val) {
  multichannel_input = val;
  cp5.getController("multichannel_input").setLabel("multichannel input: "+multichannel_input);
}
//-stats_period 2.5 //update every 2.5sec
public void export() {
  if ( audiofiles.size() > 0 ) {

    println(createcmd("aac") );
    
    if (aac_export) {
      processes.add( new RunnableThread( "Aac file", createcmd("aac") ) );
    }

    if (ogg_export) {
      processes.add( new RunnableThread( "Ogg file", createcmd("ogg") ) );
    }
    
    //processes.add( new RunnableThread( "Ogg file", new String[] { ffmpegPath, "-h" } ) );

    clear(); //remove files and controls for files
  } else {
    println("error - no matching audio layout found");
  }
}
/*
  if (!dataFolderOpened) {
 dataFolderOpened = true;
 runCmd(openFolderCmd);
 }
 */


public void clear() {
  for (int i=0; i<audiofiles.size(); i++) {
    int currIndex = audiofiles.get(i).id;
    cp5.getController("removefile_"+currIndex).remove();
  }
  audiofiles.clear();
  updateLayouts();
}

public void controlEvent(ControlEvent theControlEvent) {
  String eventName = theControlEvent.getName();
  //println("got an event from "+ eventName );

  String removeprefix = "removefile_";

  if (  eventName.indexOf(removeprefix) != -1 ) {
    int fileindex = PApplet.parseInt( eventName.substring(removeprefix.length(), eventName.length() ) );
    println("remove "+fileindex);
    for (int i=0; i<audiofiles.size(); i++) {
      if ( audiofiles.get(i).id == fileindex) {
        audiofiles.remove(i);
        cp5.getController(eventName).remove();
        updateLayouts();
      }
    }
    //audiofiles.remove(fileindex);
  }

  if (theControlEvent.isTab()) {
    activeTab = theControlEvent.getTab().getName();
    /*
    switch(tabName) {
     case "default": 
     println("Zero");  // Does not execute
     break;
     case "settings": 
     println("One");  // Prints "One"
     break;
     }
     */
    println("got an event from tab : "+theControlEvent.getTab().getName()+" with id "+theControlEvent.getTab().getId());
    //if()
  }
}
 //for returning value from exec shell
 //for returning value from exec shell




Map<String, String[] > layouts = new HashMap<String, String[]>();

ArrayList<Audiolayout> audiolayouts = new ArrayList<Audiolayout>();

String outputFile;
//String cmd = ""; //not used
String[] cmdsAsArray;

//String[] cmds = ;

String killffmpegCmd = "taskkill /F /IM ffmpeg.exe"; //forcefully kill all processess associated with ffmpeg - may lead to corruption - should check progress first

public void initLayouts() {

  layouts.put("mono", new String[] {"FC"});
  layouts.put("stereo", new String[] {"FL", "FR"});
  //layouts.put("2.1", new String[] {"FL", "FR", "LFE"});
  layouts.put("3.0", new String[] {"FL", "FR", "FC"}); // OK
  //layouts.put("3.0(back)", new String[] {"FL", "FR", "BC"});
  layouts.put("4.0", new String[] {"FL", "FR", "FC", "BC"}); // 4.0 not supported by ogg vorbis

  //layouts.put("quad", new String[] {"FL", "FR", "BL", "BR"});
  //layouts.put("quad(side)", new String[] {"FL", "FR", "SL", "SR"});
  //layouts.put("3.1", new String[] {"FL", "FR", "FC", "LFE"}); // 3.1 not supported by vorbis

  layouts.put("5.0", new String[] {"FL", "FR", "FC", "BL", "BR"});
  //layouts.put("5.0(side)", new String[] {"FL", "FR", "FC", "SL", "SR"});
  //layouts.put("4.1", new String[] {"FL", "FR", "FC", "LFE", "BC"});
  //layouts.put("5.1", new String[] {"FL", "FR", "FC", "LFE", "BL", "BR"});
  //layouts.put("5.1(side)", new String[] {"FL", "FR", "FC", "LFE", "SL", "SR"});
  layouts.put("6.0", new String[] {"FL", "FR", "FC", "BC", "SL", "SR"}); //not supported by Vorbis
  //layouts.put("6.0(front)", new String[] {"FL", "FR", "FLC", "FRC", "SL", "SR"});
  //layouts.put("hexagonal", new String[] {"FL", "FR", "FC", "BL", "BR", "BC"});
  //layouts.put("6.1", new String[] {"FL", "FR", "FC", "LFE", "BC", "SL", "SR"});
  //layouts.put("6.1(back)", new String[] {"FL", "FR", "FC", "LFE", "BL", "BR", "BC"});
  //layouts.put("6.1(front)", new String[] {"FL", "FR", "LFE", "FLC", "FRC", "SL", "SR"});
  layouts.put("7.0", new String[] {"FL", "FR", "FC", "BL", "BR", "SL", "SR"}); //not supported by Vorbis
  //layouts.put("7.0(front)", new String[] {"FL", "FR", "FC", "FLC", "FRC", "SL", "SR"});
  layouts.put("7.1", new String[] {"FL", "FR", "FC", "LFE", "BL", "BR", "SL", "SR"}); // OK
  //layouts.put("7.1(wide)", new String[] {"FL", "FR", "FC", "LFE", "BL", "BR", "FLC", "FRC"});
  //layouts.put("7.1(wide-side)", new String[] {"FL", "FR", "FC", "LFE", "FLC", "FRC", "SL", "SR"});
  //layouts.put("octagonal", new String[] {"FL", "FR", "FC", "BL", "BR", "BC", "SL", "SR"});
  layouts.put("hexadecagonal", new String[] {"FL", "FR", "FC", "BL", "BR", "BC", "SL", "SR", "TFL", "TFC", "TFR", "TBL", "TBC", "TBR", "WL", "WR"});
  //layouts.put("downmix", new String[] {"DL", "DR"});

  //List keys = new ArrayList(layouts.keySet());
  //for (int i = 0; i < keys.size(); i++) {
  Set<String> set = layouts.keySet();
  for (String s : set) {
    audiolayouts.add( new Audiolayout( layouts.get(s).length, s, layouts.get(s) ) );
    //println( audiolayouts.get( audiolayouts.size()-1 ).name );//layouts.get(obj)[0]
    // do stuff here
  }
}

class Audiolayout {
  String[] channels;
  String name;
  int numChan;
  String chans = "";

  Audiolayout ( int numChan, String name, String[] channels ) {  
    this.numChan = numChan;
    this.name = name;
    this.channels = channels;
    for (int ch = 0; ch < channels.length; ch++ ) {
      chans += ch+".0-"+channels[ch] + "|";
    }
    chans = chans.substring( 0, chans.length()-1 ); //get rid of last | delimeter
  }
}

public String[] debugcmd() {
  String[] myout = {"\""+ffmpegPath+"\"", "-h", "formats"};
  return myout;
}

//ffprobe -i <INPUT> -show_entries format=duration -v quiet -of csv="p=0"

public String[] createcmd(String code) {
  ArrayList<String> cmds = new ArrayList<String>();

  if ( audiofiles == null ) {
    return null;
  }
  if ( audiofiles.size() == 0 ) { //|| 
    return null;
  }
  //String cmd = " -progress \""+dataPath("ffmpeg_log_"+millis()/1000+".txt")+"\" ";

  String firstname = audiofiles.get(0).name;
  String[] basename = split( firstname, ".");
  outputFile = "export_"+basename[0]+"_"+hour()+"_"+minute()+"_"+second()+"_"+PApplet.parseInt( random(0, 10000) ) ;
  String osdel = ""; //operation system depandent use of quates in commands
  if (isMac) {
    osdel = "";
  } 
  if (isWin) {
    osdel = "\"";
  } 

  cmds.add(osdel+ffmpegPath+osdel);

  String[] inputs = new String[audiofiles.size()];
  //String inputs =""; //paths to inputs
  String[] inputsmap = new String[audiofiles.size()]; 
  //String inputsmap = ""; //channel mapping of inputs
  String outputsmap = ""; //channel mapping of output
  String currChanLayout = "";

  boolean matchfound = false;


  for ( int i=0; i < audiolayouts.size(); i++ ) {
    if ( audiofiles.size() == audiolayouts.get(i).numChan ) {
      outputsmap = audiolayouts.get(i).chans;
      currChanLayout = audiolayouts.get(i).name;
      matchfound = true;
      break;
    }
  }

  if (!matchfound) {
    println("no match found in audio layouts. Returing");
    return null;
  }


  for (int i=0; i<audiofiles.size(); i++) {
    inputs[i] = "\""+audiofiles.get(i).path+"\""; 
    inputsmap[i] = "["+i+":a]";
    cmds.add( "-i" );
    cmds.add(osdel+audiofiles.get(i).path+osdel );
  }

  int currquality = quality;
  if (autoquality) {
    currquality = audiofiles.size()*64;
  }

  //if individual audio sources are specified map their first channel to audio channels of single output - also create two copies of this filter to pipe into two outputs
  if (!multichannel_input) {
    cmds.add("-filter_complex");
    cmds.add(osdel+"join=inputs="+audiofiles.size()+":channel_layout="+currChanLayout+":map="+outputsmap + "[out]"+osdel);
    cmds.add("-map");
    cmds.add(osdel+"[out]"+osdel);
  }

  cmds.add("-y");
  cmds.add("-codec:a");

  if (code == "aac") {
    //println("COMMAND AAC");
    cmds.add("aac");
    cmds.add("-b:a");
    cmds.add(currquality+"k");
    cmds.add("-movflags");
    cmds.add("+faststart");
    cmds.add(osdel+dataPath(outputFile+"."+code)+osdel);
  } else if (code == "ogg") {
    //println("COMMAND OGG");
    cmds.add("libvorbis");
    cmds.add("-b:a");
    cmds.add(currquality+"k");
    cmds.add(osdel+dataPath(outputFile+"."+code)+osdel);
  }

  String[] currCmds = cmds.toArray( new String[cmds.size()]);
  return currCmds; //retrun the commands as array
}

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

public void runCmd(String cmdstr) {
  try {
    Runtime.getRuntime().exec(cmdstr);
    //Process Return = Runtime.getRuntime().exec(cmdstr); //run the shell file
    //println(Return);
  } 
  catch (IOException e) {
    e.printStackTrace();
  }
}

public void runCmdReturn(String[] args) {
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

public void cmdTest(String[] args) {
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



//import java.SystemUtils;
//import java.ProcessHandle;
//import java.Process.ProcessHandle;

//import java.util.stream.Stream;
//import java.util.Optional;
//import java.util.stream.Stream;

ArrayList<RunnableThread> processes = new ArrayList<RunnableThread>(); //keep track of currently running processes

public void updateProcesses() {
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
    id = PApplet.parseInt( random(0, 1000000) );

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
            duration = PApplet.parseFloat(hms[0]) * 3600 +PApplet.parseFloat(hms[1]) *   60 + PApplet.parseFloat(hms[2]);
          }

        }

        found = match(line, "(?<=time=)[\\d:.]*");
        if (found != null) { 
          String[] params = found[0].split(":");
          if ( params.length >1) {
            progress = ( PApplet.parseFloat(params[0]) * 3600 + PApplet.parseFloat(params[1]) * 60 + PApplet.parseFloat(params[2]) ) / duration;
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
  public void settings() {  size(640, 480); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "create_multichannel_audio2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
