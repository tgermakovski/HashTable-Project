package com.company;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Scanner;

public class Hashtable implements Serializable {

    String name;
    String url;
    String full_text;
    Word[] table = new Word[1000000];
    int total_number_of_words = 0;

    Hashtable(String n, String u){name=n; url=u;}

    int generateHashCode(String word){

        int hashcode = 0;
        for(int i=0; i<word.length(); i++){
            hashcode = hashcode + ((i+1) * word.charAt(i));
        }
        if(hashcode>999999){
            hashcode= 999999;
        }

        return hashcode;
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
                Word www = new Word(null,null,word);
                int hashcode = generateHashCode(word);
                placeInHashTable(www, hashcode);

                //make everything right of space the new rest_of_text
                rest_of_text = rest_of_text.substring(i+1,rest_of_text.length());
                i=0;
            }
        }
    }

    public void breakIntoWords(Hashtable hhh){
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
                Word www = new Word(null,null,word);
                int hashcode = generateHashCode(word);
                hhh.placeInHashTable(www, hashcode);

                //make everything right of space the new rest_of_text
                rest_of_text = rest_of_text.substring(i+1,rest_of_text.length());
                i=0;
            }
        }
    }

    void placeInHashTable(Word word, int index){

        //if index is empty
        if(table[index] == null){
            table[index] = word;
            table[index].prev = null;
            table[index].next = null;
            total_number_of_words++;
        }else{
            if(table[index].val.equals(word.val)){
                //System.out.println("word already in hashtable!");
                table[index].freq++;
            }else{
                table[index].addToEnd(word, 1, this);
            }
        }

    }


    int incrementSim(String v, int freq1, int freq2, ExtendableHT3 global){


        int inc = 0;
        int global_freq = 0;
        int ii = generateHashCode(v);
        ii &= ((int) (Math.pow(2,global.global_depth) - 1));

        try{

        //grab text from given file
        File original_file = new File("C:/temp/gd" + ii + ".txt"); //String.valueOf(ii)
        String original_text = " ";

        //if(original_file.exists() && !original_file.isDirectory()){
            Scanner scanner = new Scanner(original_file);
            while (scanner.hasNextLine()) {  //get text from file into string
                original_text = original_text.concat(scanner.nextLine() + "\n");
                if (original_text.contains("<w>" + v + "</w>")) {
                    Buck original_buck = global.buckFromString(original_text);
                    global_freq = original_buck.findFreq(v);
                    break;
                }
            }

        //}


        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        if(freq1>freq2){
            freq1=freq2;
        }
        if(global_freq > 0){
            inc = freq1 * global.total_number_of_words / global_freq;
        }
        return inc;

    }

    int incrementSimOldVersion(String v, int freq1, int freq2, ExtendableHT2 global){


        int inc = 0;
        int global_freq = 0;

        /*

        //step thru global to find v's global freq
        int ii = generateHashCode(v);
        //if given index not null
        if(global.table[ii]!=null){
            //if value at given index
            if(global.table[ii].val.equals(v)){
                global_freq = global.table[ii].freq;
            }else{
                //walk through linked list
                boolean poop = true;
                Word w = global.table[ii];
                do{
                    //if null
                    if(w.next == null){
                        poop = !poop;
                    }else{
                        //if found
                        if(w.next.val.equals(v)){
                            global_freq = w.next.freq;
                            poop = !poop;
                        }else{
                            w = w.next;
                        }
                    }
                }while(poop);
            }
        }

        //inc = freq1 or 2, whichever is lower, times global.totalwordcount, over global freq
        if(freq1>freq2){
            freq1=freq2;
        }
        if(global_freq > 0){
        inc = freq1 * global.total_number_of_words / global_freq;
        }

         */


        //return inc;
        return 10;
    }


    int compare(Hashtable hhh, ExtendableHT3 global){

        int sim = 0;
        int num_in_common = 0;
        int num_in_this = total_number_of_words;
        int num_in_hhh = hhh.total_number_of_words;

        //linear traverse this.table
        for(int i =0; i<table.length; i++){

            //walk thru linked list of hashtable1[i]
            for(Word ww = table[i]; ww!=null; ww=ww.next){
                String v = ww.val;

                //look for v in hhh
                int ii = generateHashCode(v);
                //walk through linked list of hashtable2[i]
                for(Word w = hhh.table[ii]; w!=null; w=w.next){

                    //if found
                    if(w.val.equals(v)){

                        //increment similarity
                        sim = sim + incrementSim(v,ww.freq, w.freq, global);
                        num_in_common = lowerInt(ww, w, num_in_common);
                    }
                }
            }
        }

        int divide_by = (num_in_this + num_in_hhh - num_in_common);
        if(divide_by < 1){
            divide_by = 1;
        }
        sim = sim / divide_by;
        return sim;
    }


    int lowerInt(Word a, Word b, int numincom){

        if(a.freq < b.freq){
            return numincom + a.freq;
        }else{
            return numincom + b.freq;
        }

    }


}
