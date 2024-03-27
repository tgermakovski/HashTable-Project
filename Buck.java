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
import java.io.File;

public class Buck{

    int depth;
    int local_i;
    String[] words = new String[100]; //60
    int[] freqs = new int[words.length];

    Buck(int d, int l){
        depth=d;
        local_i=l;
    }

    int addWord(String w)
    {

        for(int i = 0; i < words.length; i++) {
            if(words[i] != null && words[i].equals(w)){ //if already there
                freqs[i]++;
                return i;
            }
            if(words[i] == null){ //if null
                words[i]=w;
                freqs[i]++;
                return i;
            }
        }
        return -1; //full
    }

    int findFreq(String w){

        for(int i = 0; i < words.length; i++) {
            if(words[i].equals(w)){ //if found
                return freqs[i];
            }
            if(words[i] == null){ //if null
                words[i]=w;
                return 0;
            }
        }
        return 0; //full
    }

    String thisToString(){

        String output = "<d>" + String.valueOf(depth) + "</d><l>" + String.valueOf(local_i) + "</l>\n";
        for(int i =0; i<words.length; i++){

            if(words[i]==null){break;}

            output = output + "<i>" + i + "</i>";
            output = output + "<w>" + words[i] + "</w>";
            output = output + "<f>" + freqs[i] + "</f>\n";

        }
        return output;
    }

    int thisToFile(){
        try {
            String s = thisToString();
            String file_name = "gd" + local_i + ".txt";
            File f = new File("C:/temp/" + file_name);
            FileWriter fw = new FileWriter(f,false);
            fw.write(s);
            fw.close();
            return local_i;
        }
        catch (Exception e) {
            e.getStackTrace();
        }
        return -1;
    }

}

