package com.company;
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
import java.io.FileWriter;
import java.util.Scanner;


public class ExtendableHT2 {

    String full_text;
    int total_number_of_words = 0;
    int global_depth = 2;
    int[] file_names = new int[4];

    //constructor
    ExtendableHT2(){
        Vedro ved = new Vedro(1,0);
        Vedro ved2 = new Vedro(1,1);
        int file_name = ved.thisToFile();
        int file_name2 = ved2.thisToFile();
        file_names[0]=file_name; //check if file name == -1"
        file_names[1]=file_name2;
        file_names[2]=file_name;
        file_names[3]=file_name2;
    }

    public String removeInvalidCharacters(){
        for(int i = 0; i < full_text.length(); i++) //for every char in the full text of the article
        {
            //if invalid char (not space or alphabet)
            char poop = full_text.charAt(i);
            if(!((poop > 64 && poop < 91) || (poop > 96 && poop < 122) || poop == 32)){
                //remove that char from the full text
                full_text = full_text.substring(0,i) + full_text.substring(i+1,full_text.length());
                i--;
            }
        }
        return full_text;
    }

    public void breakIntoWords(){
        String rest_of_text = full_text;
        for(int i = 0; i < rest_of_text.length(); i++) //for every char in the full text of the article
        {
            //if char is blank space
            char poop = rest_of_text.charAt(i);
            if(poop == 32){
                //make everything left of space a word
                String word = rest_of_text.substring(0,i);
                word = word.toLowerCase();
                //System.out.println(word);

                //make word obj and add it to hashtable
                System.out.println("breaking off word XHT2    "+word);
                Word www = new Word(null,null,word);
                addWord(www);

                //make everything right of space the new rest_of_text
                rest_of_text = rest_of_text.substring(i+1,rest_of_text.length());
                i=0;
            }
        }
    }

    void addWord(Word w){

        int i = w.haszkod;
        i &= ((int) Math.pow(2,global_depth)-1);
        //get that file
        try {

            //grab text from given original file
            File file_og = new File("C:\\Users\\tgerm\\MyJavaProjects\\ProjectWikipedia2\\src\\com\\company\\" + "_gd" + file_names[i] + "_.txt"); //String.valueOf(i)
            String text_og = " ";
            Scanner scanner = new Scanner(file_og);
            while(scanner.hasNextLine()){  //get text from file into string
                text_og = text_og.concat(scanner.nextLine() + "\n");
            }

            //make bucket obj from text
            Vedro vej = vedroFromString(text_og, scanner);

            //try adding, if full, resize and/or split
            if(vej.addWord(w.haszbajt)==-1){
                if(vej.depth==global_depth){
                    resize();
                }
                split(i);
                vej.addWord(w.haszbajt);
            }else{
                //delete original, make vej new file
                file_og.delete();
                int interesno = vej.thisToFile();
                file_names[interesno] = interesno;
            }

        }catch (IOException e){
            e.printStackTrace();
        }


        //make into bucket
        //if full (if contains i>smtg)
        //resize or split
        //else add to bucket
        //uuuuuuggggggggggggggggggghhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    }

    void resize(){

        System.out.println("entering resize");

        int newLength = file_names.length << 1;
        global_depth++;
        int[] newTable = new int[newLength];
        for (int i=0; i < file_names.length; ++i) {
            int ii = i + file_names.length;
            newTable[i]=file_names[i];
            newTable[ii]=file_names[i];
        }
        file_names = newTable;
    }

    String getTag(String full, String start_tag, String end_tag){
        int start_i = full.indexOf(start_tag);
        int end_i = full.indexOf(end_tag);
        return full.substring(start_i+3,end_i);
    }

    Vedro vedroFromString(String text_og, Scanner scanner){

        //grab data from the string
        int depth = Integer.parseInt(getTag(text_og,"<d>","</d>"));
        int local = Integer.parseInt(getTag(text_og,"<l>","</l>"));
        byte[] wordss = new byte[1000];
        byte[] freqss = new byte[1000];
        while(scanner.hasNextLine()){
            int ii = Integer.parseInt(getTag(scanner.nextLine(), "<i>","</i>"));
            int ww = Integer.parseInt(getTag(scanner.nextLine(), "<w>","</w>"));
            int ff = Integer.parseInt(getTag(scanner.nextLine(), "<f>","</f>"));
            wordss[ii]= (byte) ww;
            freqss[ii]= (byte) ff;
        }
        Vedro vej = new Vedro(depth, local);
        vej.words = wordss;
        vej.freqs = freqss;
        return vej;
    }

    void split(int i){

        System.out.println("entering split");

        try {

            //grab text from given original file
            File file_og = new File("_gd" + i + "_.txt"); //String.valueOf(i)
            String text_og = " ";
            Scanner scanner = new Scanner(file_og);
            while(scanner.hasNextLine()){  //get text from file into string
                text_og = text_og.concat(scanner.nextLine() + "\n");
            }

            //delete original file
            file_og.delete();

            //grab data from the string
            Vedro vej = vedroFromString(text_og, scanner);

            //new buckets array
            Vedro[] newBuckets = new Vedro[file_names.length];

            //find all indexes j pointing to Bucket
            for(int j = 0; j < file_names.length; j++){
                int jj = j & ((int) Math.pow(2,vej.depth)-1);
                int ii = i & ((int) Math.pow(2,vej.depth)-1);
                if(ii==jj) //if j shares local bits w i
                {
                    //give it a new bucket
                    Vedro vei = new Vedro(vej.depth+1,j);
                    newBuckets[j] = vei;
                }
            }

            //walk thru text
            //for each line
            //get index from the word byte and
            //put in appropriate newBuckets[_];
            for(int j = 0; j < vej.words.length; j++){
                if(vej.words[j]!=0){
                    byte thing = vej.words[j];
                    thing &= (((int)Math.pow(2, vej.depth+1)) - 1);
                    newBuckets[thing].addWord(vej.words[j]);
                }//else{break;}
            }

            //now bucket turn back into files and make sure HTX array updated, points to new buckets
            for(int j = 0; j<newBuckets.length; j++){
                if(newBuckets[j]!=null){
                    file_names[j] = newBuckets[j].thisToFile();
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
