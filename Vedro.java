package com.company;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;  // Import the File class

public class Vedro{

    int depth;
    int local_i;
    byte[] words = new byte[1000];
    byte[] freqs = new byte[words.length];
    File file;

    Vedro(int d, int l){
        depth=d;
        local_i=l;
    }

    int addWord(byte w){

        for(int i = 0; i < words.length; i++) {
            if(words[i] == w){ //if already there
                freqs[i]++;
                return i;
            }
            if(words[i] == 0){ //if null
                words[i]=w;
                return i;
            }
        }
        return -1; //full
    }

    int findFreq(byte w){

        for(int i = 0; i < words.length; i++) {
            if(words[i] == w){ //if found
                return freqs[i];
            }
            if(words[i] == 0){ //if null
                words[i]=w;
                return 0;
            }
        }
        return 0; //full
    }

    String thisToString(){

        String output = "<d>" + String.valueOf(depth) + "</d><l>" + String.valueOf(local_i) + "</l>\n";
        for(int i =0; i<words.length; i++){

            output = output + "<i>" + String.valueOf(i) + "</i>";
            output = output + "<w>" + String.valueOf(words[i]) + "</w>";
            output = output + "<f>" + String.valueOf(freqs[i]) + "</f>\n";

        }
        return output;
    }

    int thisToFile(){
        try {
            String s = thisToString();
            String file_name = "_gd" + String.valueOf(local_i) + "_.txt";
            FileWriter writer = new FileWriter(file_name);
            writer.write(s);
            writer.close();
            return local_i;
        }catch(IOException ee){ee.printStackTrace();}
        return -1;
    }

}
