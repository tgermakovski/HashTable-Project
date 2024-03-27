package com.company;

public class Article {

    String name;
    String url;
    String full_text;
    Hashtable ht = new Hashtable(null,null);

    Article(String n, String u){

        //subtract last 12 chars from name to get rid of " - wikipedia"
        //author's note: for some reason, this code doesn't do what its supposed to do upon execution
        //however, it also doesn't break anything either, so i'll keep it around for now lol.
        if(n.length() > 11 && n.substring((n.length()-12),(n.length()-1)).equals(" - Wikipedia")){
            name = n.substring(0,(n.length()-13));
        }else{
            name = n;
        }
        url = u;
    }

    int generateHashCode(String word){

        int hashcode = 0;
        for(int i=0; i<word.length(); i++){
            hashcode = hashcode + word.charAt(i);
        }

        return hashcode;
    }

    public void setFullText(String ft){
        full_text = ft;
    }

    public String getFullText(){
        return full_text;
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

    public String removeInvalidCharacters(String s){
        for(int i = 0; i < s.length(); i++) //for every char in the full text of the article
        {
            //if invalid char (not space or alphabet)
            char poop = s.charAt(i);
            if(!((poop > 64 && poop < 91) || (poop > 96 && poop < 122) || poop == 32)){
                //remove that char from the full text
                s = s.substring(0,i) + s.substring(i+1,s.length());
                i--;
            }
        }
        return s;
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

                System.out.println(word);

                //if capitalized and not acronym, uncapitalize
                /*author's note: first of all, this if() only checks if the first 2 chars are capitalized
                because I don't want to make a whole for loop to check every char. Secondly, when the
                program encounters a regular non-acronym word that's all caps for some reason, it treats
                it as an acronym. I don't know which is more prevalent, acronyms, or unnecessarily all
                capped words, so I just assume its acronyms and accept the trade off.
                 */
                if((word.charAt(0) >= 65 && word.charAt(0) <= 90) && !(word.length() > 1 && word.charAt(1) >= 65 && word.charAt(1) <= 90)){

                    System.out.println("before: " + word);

                    char[] wordChars = word.toCharArray();
                    wordChars[0] = (char) (word.charAt(0) + 32);
                    word = String.valueOf(wordChars);

                    System.out.println("after :" + word);

                }


                //make word obj and add it to hashtable
                Word www = new Word(null,null,word);
                int hashcode = generateHashCode(word);
                ht.placeInHashTable(www, hashcode);

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

                //if capitalized, uncapitalize
                if(word.charAt(0) >= 65 && word.charAt(0) <= 90){
                    //code goes here: turn string into char[], edit at index, change back into string, as suggested here? https://stackoverflow.com/questions/6952363/replace-a-character-at-a-specific-index-in-a-string
                }

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

}
