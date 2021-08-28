import java.io.*; //for returning value from exec shell
import java.util.Arrays; //for returning value from exec shell
import java.util.Map;
import java.util.Set;
import java.util.List;

Map<String, String[] > layouts = new HashMap<String, String[]>();

ArrayList<Audiolayout> audiolayouts = new ArrayList<Audiolayout>();

String outputFile;
//String cmd = ""; //not used
String[] cmdsAsArray;

//String[] cmds = ;

String killffmpegCmd = "taskkill /F /IM ffmpeg.exe"; //forcefully kill all processess associated with ffmpeg - may lead to corruption - should check progress first

void initLayouts() {

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

String[] debugcmd() {
  String[] myout = {"\""+ffmpegPath+"\"", "-h", "formats"};
  return myout;
}

//ffprobe -i <INPUT> -show_entries format=duration -v quiet -of csv="p=0"

String[] createcmd(String code) {
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
  outputFile = "export_"+basename[0]+"_"+hour()+"_"+minute()+"_"+second()+"_"+int( random(0, 10000) ) ;
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
