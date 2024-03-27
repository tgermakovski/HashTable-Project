package com.company;

public class Bucket {

    Word[] words = new Word[10000];
    int depth;

    Bucket(int d){
        depth=d;
    }

    int addWord(Word x) {

        System.out.println("entered addWord() Bucket");

        for(int i = 0; i < words.length; i = i+2) {
            if(words[i] == x){ //if already there
                words[i].freq++;
                return i;
            }
            if(words[i] == null){
                words[i]=x;
                return i;
            }
        }
        return -1; //full
    }

}
