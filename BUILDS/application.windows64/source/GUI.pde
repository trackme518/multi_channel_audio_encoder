import controlP5.*;
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

color pink = color(255, 99, 146);
color yellow = color(255, 228, 94);
color beige = color(249, 249, 249);
color blue = color(127, 200, 248);
color jeans = color(90, 169, 230);
color black = color(0);
color white = color(255);
color orange = color(255, 128, 0);

//List inputList;
//ScrollableList cp5fileslist;
Textarea inputList;

void intiGUI() {
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

void quality(int val) {
  quality = val;
  //make sure we stay in avaliable values
  if ( quality%64 != 0 ) {
    quality = 128;
  }
  //println(quality);
}

void autoquality(boolean val) {
  autoquality = val;
  cp5.getController("autoquality").setLabel("Automatic quality: "+autoquality);
}

void ogg_export(boolean val) {
  ogg_export = val;
  cp5.getController("ogg_export").setLabel("ogg export: "+ogg_export);
}

void aac_export(boolean val) {
  aac_export = val;
  cp5.getController("aac_export").setLabel("aac export: "+aac_export);
}

void tricktheear_web() {
  link("https://www.tricktheear.eu");
}

void multichannel_input(boolean val) {
  multichannel_input = val;
  cp5.getController("multichannel_input").setLabel("multichannel input: "+multichannel_input);
}
//-stats_period 2.5 //update every 2.5sec
void export() {
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


void clear() {
  for (int i=0; i<audiofiles.size(); i++) {
    int currIndex = audiofiles.get(i).id;
    cp5.getController("removefile_"+currIndex).remove();
  }
  audiofiles.clear();
  updateLayouts();
}

void controlEvent(ControlEvent theControlEvent) {
  String eventName = theControlEvent.getName();
  //println("got an event from "+ eventName );

  String removeprefix = "removefile_";

  if (  eventName.indexOf(removeprefix) != -1 ) {
    int fileindex = int( eventName.substring(removeprefix.length(), eventName.length() ) );
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
