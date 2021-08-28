//drop files and issue ffmpeg commands
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import drop.*;
SDrop drop;
ArrayList<AudioFile> audiofiles = new ArrayList<AudioFile>();
String ffmpegPath = null;

String[] currChans = null;
String currChanLayout = "No matching layout found!";
boolean matchfound = false;

long currTime;
int readInterval = 1000;
int progress = 0; //read log file and check for progress

void setup() {
  size(640, 480);
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


void draw() {

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


void dropEvent(DropEvent theDropEvent) {
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
    id = int( random(0, 1000000) );
  }

  void display(int index, int curroffsety) {
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

void updateLayouts() {
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
