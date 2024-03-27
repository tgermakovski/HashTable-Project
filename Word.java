package com.company;
import java.io.Serializable;

public class Word implements Serializable {

    Word next;
    Word prev;
    String val;
    int freq = 1;
    int haszkod;
    byte haszbajt = (byte) haszkod;

    Word(Word n, Word p, String v){
        next=n;
        prev=p;
        val=v;
        haszkod = generateHashCode(val);
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

    void addToEnd(Word w, int num_of_call, Hashtable hhh){

        if(val.equals(w.val) && num_of_call > 1){
            //System.out.println("already in hashtable");
            freq++;
        }else{

            if(next==null){
                next=w;
                w.prev = this;
                w.next = null;
                hhh.total_number_of_words++;
            }else{
                num_of_call++;
                next.addToEnd(w, num_of_call, hhh);
            }

        }
    }

}
