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


public class ExtendableHT {

    String name;
    String full_text;
    int total_number_of_words = 0;
    int global_depth = 1;
    Bucket[] table = new Bucket[4];

    //constructor
    ExtendableHT(String n){
        name=n;
        Bucket buck = new Bucket(0);
        table[0]=buck;
        table[1]=buck;
        table[2]=buck;
        table[3]=buck;
    }

    public void resize(){

        System.out.println("entered resize()");

        int newLength = table.length << 1;
        global_depth++;
        Bucket[] newTable = new Bucket[newLength];
        for (int i=0; i < table.length; ++i) {
            //int ii = flipBitAtIndex(i,global_depth);
            int ii = i + table.length;
            newTable[i]=table[i];
            newTable[ii]=table[i];
        }
        table = newTable;

    }

    public void splitBucket(int i){

        System.out.println("entered splitBucket()");

        //set aside original bucket
        Bucket buck1 = table[i];
        //get list of indexes pointing to buck1:
        //number of indexes pointing to buck1
        int arrow_count = (int) Math.pow(2,global_depth - buck1.depth);
        //bits in common between all indexes pointing to buck1
        int local_bits = i & (((int)Math.pow(2, buck1.depth+1)) - 1);
        //all the bit minus the local bits
        int unlocal_bits = i >>> buck1.depth;

        //find all j pointing to Bucket
        for(int j = 0; j < table.length; j++){

            int jj = j & ((int) Math.pow(2,buck1.depth+1)-1);
            int ii = i & ((int) Math.pow(2,buck1.depth+1)-1);

            if(ii==jj) //if j shares local bits w i
            {
                //give each index a new bucket
                Bucket bbb = new Bucket(buck1.depth+1);
                table[j] = bbb;
            }
        }

        System.out.println("for loop 1 exit, splitBucket()");

        //read thru buck1 //add each word to correct bucket
        for(int j = 0; j < buck1.words.length; j++){
            if(buck1.words[j]!=null){
                Word thing = buck1.words[j];
                thing.haszbajt &= (((int)Math.pow(2, buck1.depth+2)) - 1);
                table[thing.haszbajt].addWord(buck1.words[j]);
            }//else{break;}
        }

        System.out.println("for loop 2 exit splitBucket()");

    }


    public int flipBitAtIndex(int x, int i){
        //this only works if the bit at [i] is 0
        //if 1, and x with complement of y
        //but for my purposes, this only gets
        //called when bit at [i] is 0, so
        //i dont have to worry abt the 1 case
        int y = 1 << i;
        x |= y;
        return x;
    }


    int generateHashCode(String word){

        int hashcode = 0;
        for(int i=0; i<word.length(); i++){
            hashcode = hashcode + ((i+1) * word.charAt(i));
        }

        return hashcode;
    }

    byte generateHashByte(String word){

        int hashcode = 0;
        for(int i=0; i<word.length(); i++){
            hashcode = hashcode + ((i+1) * word.charAt(i));
        }
        byte output = (byte) hashcode;

        return output;
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
                addWord(www);

                //make everything right of space the new rest_of_text
                rest_of_text = rest_of_text.substring(i+1,rest_of_text.length());
                i=0;
            }
        }
    }

    void addWord(Word w){

        System.out.println("entered addWord() XHT");

        int i = w.haszkod;
        i &= ((int) Math.pow(2,global_depth+1)-1);
        if(table[i].addWord(w) == -1){

            if(table[i].depth < global_depth){
                splitBucket(i);
                int new_i = w.haszkod;
                new_i &= ((int) Math.pow(2,global_depth)-1);
                table[new_i].addWord(w);
            }else{
                resize();
                splitBucket(i);
                int new_i = w.haszkod;
                new_i &= ((int) Math.pow(2,global_depth)-1);
                table[new_i].addWord(w);
            }
        }
    }

}
