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


public class ExtendableHT3 {

    //VARIABLES
    String full_text;
    int total_number_of_words = 0;
    int global_depth = 2;
    int[] file_names = new int[4];

    //CONSTRUCTOR
    ExtendableHT3(){

        Buck buck0 = new Buck(1,0);
        Buck buck1 = new Buck(1,1);
        int file_name = buck0.thisToFile();
        int file_name2 = buck1.thisToFile();

        file_names[0]=file_name; //check if file name == -1?
        file_names[1]=file_name2;
        file_names[2]=file_name;
        file_names[3]=file_name2;
    }

    //GENERAL PURPOSE METHODS

    public String removeInvalidCharacters(){
        for(int i = 0; i < full_text.length(); i++) //for every char in the full text of the article
        {
            System.out.println(i);
            System.out.println(full_text.length());
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
                System.out.println("---------------------    "+word);
                Word www = new Word(null,null,word);
                doubleAddWord(www);

                //make everything right of space the new rest_of_text
                rest_of_text = rest_of_text.substring(i+1);
                i=0;
            }
        }
    }


    int generateHashCode(String word){

        int hashcode = 0;
        for(int i=0; i<word.length(); i++){
            hashcode = hashcode + ((i+1) * word.charAt(i));
        }
        if(hashcode > 999999){
            hashcode = 999999;
        }
        return hashcode;
    }


    String getTag(String full, String start_tag, String end_tag){
        int start_i = full.indexOf(start_tag);
        int end_i = full.indexOf(end_tag);
        return full.substring(start_i+3,end_i);
    }

    Buck buckFromString(String text){

        //grab data from the string
        int depth = Integer.parseInt(getTag(text,"<d>","</d>"));
        int local = Integer.parseInt(getTag(text,"<l>","</l>"));
        String[] wordss = new String[100]; //60
        int[] freqss = new int[100];

        while(text.contains("<i>")){
            int ii = Integer.parseInt(getTag(text,"<i>","</i>"));
            String ww = getTag(text, "<w>","</w>");
            int ff = Integer.parseInt(getTag(text, "<f>","</f>"));
            wordss[ii]= ww;
            freqss[ii]= ff;
            text = text.substring(text.indexOf("</f>")+3);
        }

        /*
        while(scanner.hasNextLine()){
            int ii = Integer.parseInt(getTag(scanner.nextLine(), "<i>","</i>"));
            String ww = getTag(scanner.nextLine(), "<w>","</w>");
            int ff = Integer.parseInt(getTag(scanner.nextLine(), "<f>","</f>"));
            wordss[ii]= ww;
            freqss[ii]= ff;
        }

         */
        Buck buck = new Buck(depth, local);
        buck.words = wordss;
        buck.freqs = freqss;
        return buck;
    }


    //EXTENDABILITY-SPECIFIC METHODS

    void doubleAddWord(Word w){
        if(!addWord(w)){
            addWord(w);
        }
        total_number_of_words++;
    }

    boolean addWord(Word w){

        //get index
        int i = w.haszkod;
        i &= ((int) Math.pow(2,global_depth)-1);

        //get file from that index
        try {
            //grab text from given original file
            File original_file = new File("C:/temp/gd" + file_names[i] + ".txt"); //String.valueOf(i)
            String original_text = " ";
            Scanner scanner = new Scanner(original_file);
            while(scanner.hasNextLine()){  //get text from file into string
                original_text = original_text.concat(scanner.nextLine() + "\n");
            }

            //make bucket obj from text
            Buck original_bucket = buckFromString(original_text);
            //try adding
            int added = original_bucket.addWord(w.val);
            if(added==-1){ //if full
                if(original_bucket.depth==global_depth){ //if glo-dep !> loc-dep
                    resize();
                }
                //i &= ((int) Math.pow(2,original_bucket.depth)-1);
                split(file_names[i]); //split(i);
                //addWord(w);
                return false;
            }else{
                //delete original, make original bucket new file // alternatively, just overwrite?
                original_file.delete();
                int interesno = original_bucket.thisToFile();
                file_names[interesno] = interesno;
                return true;
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return false;

    }

    void resize(){

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

    void split(int i){

        try {

            //grab text from given original file
            File original_file = new File("C:/temp/gd" + i + ".txt"); //String.valueOf(i)
            String original_text = " ";
            Scanner scanner = new Scanner(original_file);
            while(scanner.hasNextLine()){  //get text from file into string
                original_text = original_text.concat(scanner.nextLine() + "\n");
            }

            //delete original file
            original_file.delete();
            //grab data from the string
            Buck original_buck = buckFromString(original_text);
            //new buckets array
            Buck[] newBuckets = new Buck[file_names.length];
            //find all indexes j pointing to original bucket
            for(int j = 0; j < file_names.length; j++){
                int jj = j & ((int) Math.pow(2,original_buck.depth)-1);
                int ii = i & ((int) Math.pow(2,original_buck.depth)-1);
                if(ii==jj) //if j shares local bits w i
                {
                    //give it a new bucket
                    Buck newb = new Buck(original_buck.depth+1,j);
                    newBuckets[j] = newb;
                }
            }

            //walk thru original, put each word in its new bucket, update freq
            for(int j = 0; j < original_buck.words.length; j++){
                if(original_buck.words[j]!=null){
                    int indentured_servitude = generateHashCode(original_buck.words[j]) ;
                    indentured_servitude &= (((int)Math.pow(2, original_buck.depth+1)) - 1);
                    if(indentured_servitude< newBuckets.length){
                        newBuckets[indentured_servitude].addWord(original_buck.words[j]);
                        newBuckets[indentured_servitude].freqs[j] = original_buck.freqs[j]-1;
                    }
                }//else{break;}
            }

            //now buckets turn back into files, make sure HTX array updated, points to new buckets
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
