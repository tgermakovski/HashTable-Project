package com.company;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;


public class Main implements ActionListener {

    static JLabel enterLabel;
    static JTextField userInput;
    static JButton button;
    static JLabel outputLabel;

    public static void main(String[] a) throws Exception {


        JPanel panel = new JPanel();
        JFrame frame = new JFrame();
        frame.setSize(600, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);
        panel.setLayout(null);
        frame.getContentPane().setBackground(Color.GREEN);

        enterLabel = new JLabel("Arli input topic: ");
        enterLabel.setBounds(10, 20, 80, 25);
        panel.add(enterLabel);

        userInput = new JTextField(20);
        userInput.setBounds(100, 20, 165, 25);
        panel.add(userInput);

        button = new JButton("compare");
        button.setBounds(285, 20, 200, 25);
        button.addActionListener(new Main());
        panel.add(button);

        outputLabel = new JLabel("");
        outputLabel.setBounds(10, 50, 500, 25);
        panel.add(outputLabel);

        frame.setVisible(true);
        userInput.setVisible(true);
        button.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String user_url = "https://en.wikipedia.org/wiki/" + userInput.getText();

        Hashtable[] articles2 = new Hashtable[100];  // [100], change to [15] for faster testing

        String[] articles3 = new String[100];

        String[] articles4 = new String[1000];

        int[] flatSquare = new int[articles3.length* articles3.length]; //similarities in a compressed grid

        int[] superFlat = new int[articles4.length* articles4.length];

        //articles2 = parseXML();

        Document doc;
        try {

            //global freq table //make this updatable and persistent
            ExtendableHT3 global_dictionary = new ExtendableHT3();

            //user url
            doc = Jsoup.connect(user_url).get();
            String title = doc.title();
            String text = doc.body().text();

            //add some spaces to the end of the text to avoid edge-case exceptions
            text = text + "               ";
            text = charCheck(text);
            Hashtable wiki_u = new Hashtable(title,user_url);
            wiki_u.full_text=text;

            //remove all characters except for spaces and latin alphabet characters
            wiki_u.removeInvalidCharacters();
            //break mega text up into words and store in a hashtable
            wiki_u.breakIntoWords();

            //do the same for persistent global
            global_dictionary.full_text = wiki_u.full_text;
            //global_dictionary.removeInvalidCharacters();
            //global_dictionary.breakIntoWords();


            //link hopping
            int index_last_article = -1;
            boolean enough = false;
            int count5=0;
            String all_names = wiki_u.name + " ";
            Random ayn = new Random();
            Elements links = doc.select("a[href]");

            do{
                for(Element link : links){
                    if(legitWiki2(link.attr("href"))){
                        count5++;
                        String l_url = "https://en.wikipedia.org" + link.attr("href");
                        doc = Jsoup.connect(l_url).get();
                        title = doc.title();

                        if(index_last_article >= (articles4.length - 1)){
                            enough=true;
                        }else if(!(all_names.contains(title))) {

                            text = doc.body().text();
                            text = text + "               ";
                            text = charCheck(text);
                            Hashtable wiki_jump = new Hashtable(title, l_url);
                            wiki_jump.full_text = text;
                            wiki_jump.removeInvalidCharacters();
                            wiki_jump.breakIntoWords();

                            System.out.println(wiki_jump.name);
                            articles4[index_last_article+1] = wiki_jump.url;
                            String index_string = String.valueOf(index_last_article+1);
                            String ser_file_name = "_" + index_string + "_.txt";
                            FileOutputStream f = new FileOutputStream(new File(ser_file_name));
                            ObjectOutputStream o = new ObjectOutputStream(f);
                            o.writeObject(wiki_jump);
                            o.close();
                            f.close();

                            //make sure to also update global

                            //System.out.println("same for global   " + title);
                            global_dictionary.full_text = global_dictionary.full_text + text;
                            //global_dictionary.removeInvalidCharacters();
                            //global_dictionary.breakIntoWords();


                            index_last_article++;
                            all_names = all_names + "_" + wiki_jump.name;
                        }
                    }
                    if(count5==5){
                        count5=0;
                        break;
                    }
                }

                int rand = ayn.nextInt(index_last_article+1);
                String new_link = articles4[rand];
                //String new_link = articles2[rand].url;
                doc = Jsoup.connect(new_link).get();
                links = doc.select("a[href]");

            }while(!enough);

            System.out.println("exiting loop");

            global_dictionary.full_text = "n n " + global_dictionary.full_text;
            //global_dictionary.removeInvalidCharacters();
            global_dictionary.breakIntoWords();

            /*
            //do the same for each of the 10 wiki articles: jsoup url, parse text, add text to obj and mod
            for(int j = 0; j< articles.length; j++){
                doc = Jsoup.connect(articles[j].url).get();
                String new_title = doc.title();
                String new_text = doc.body().text();
                new_text = new_text + "               ";
                articles[j].name = new_title;
                articles[j].setFullText(new_text);
                articles[j].breakIntoWords();
            }*/


            /*
            //create global dictionary
            Hashtable global_dictionary = new Hashtable("global_dictionary","www.yo-mama.com");
            //for each wiki
            for(int i = 0; i< articles2.length; i++) {
                //put each word in global dictionary

                FileInputStream fii = new FileInputStream(new File("_" + i + "_.txt")); //if not work, replace i w String.valueOf(i)
                ObjectInputStream oii = new ObjectInputStream(fii);
                Hashtable nigg = (Hashtable) oii.readObject();
                System.out.println("               putting in the global dictionary of yo mama: " + nigg.name);
                nigg.breakIntoWords(global_dictionary);
                oii.close();
                fii.close();

                //articles2[i].breakIntoWords(global_dictionary);
            }
            */

            //serialization shit
            /*
            FileOutputStream f = new FileOutputStream(new File("myObjects.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            for(int i = 0; i<articles2.length; i++){
                o.writeObject(articles2[i]);
            }

            o.close();
            f.close();

            FileInputStream fi = new FileInputStream(new File("myObjects.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);

            for(int i = 0; i<articles2.length; i++){
                Hashtable nigg = (Hashtable) oi.readObject();
                System.out.println(nigg.name);
            }

            oi.close();
            fi.close(); */



            //calculation 100% case for similarity
            //longest wikipedia articles are ~15k words long. to be safe, bump that up to 25k
            int longest_wiki_length = 25000;
            int greatest_similarity_possible = longest_wiki_length * global_dictionary.total_number_of_words;
            //just kidding, that metric is way to extreme, keeps giving me 0%.
            int conversion_factor = 50;  //greatest_similarity_possible / 100;


            int similarity_raw_total;
            int similarity_percentage;
            int most_similar = 0;
            int most_similar_index = 0;

            //compare each article to each article
            for(int j = 0; j < articles4.length; j++){

                FileInputStream fjjj = new FileInputStream(new File("_" + j + "_.txt"));
                ObjectInputStream ojjj = new ObjectInputStream(fjjj);
                Hashtable nigg = (Hashtable) ojjj.readObject();

                for(int i = j; i< articles4.length; i++){
                        System.out.println("j:" +j+ " i:" +i);

                        if(i==j)
                        {
                            superFlat[(articles4.length*j)+i]=100000;
                        }
                        else {

                            FileInputStream fiii = new FileInputStream(new File("_" + i + "_.txt"));
                            ObjectInputStream oiii = new ObjectInputStream(fiii);
                            Hashtable nogg = (Hashtable) oiii.readObject();

                            similarity_raw_total = nogg.compare(nigg, global_dictionary); //comment out temporariliy for debugging purposes
                            int inverse_sim = (1000000 / similarity_raw_total)-1;
                            //similarity_percentage = similarity_raw_total / conversion_factor;
                            if(similarity_raw_total > most_similar){
                                most_similar = similarity_raw_total;
                                most_similar_index = i;
                            }
                            System.out.println(nigg.name);
                            System.out.println(" AND ");
                            System.out.println(nogg.name);
                            System.out.println(" ARE ");
                            System.out.println(similarity_raw_total + " sim units similar");
                            //System.out.println("For context, Russia and Vladimir Putin are 13% similar");
                            System.out.println("  ... ");
                            System.out.println("  ... ");
                            System.out.println("  ... ");

                            flatSquare[(articles4.length*j)+i]=similarity_raw_total;
                            flatSquare[(articles4.length*i)+j]=similarity_raw_total;


                            oiii.close();
                            fiii.close();
                        }

                }
                ojjj.close();
                fjjj.close();
            }

            FileInputStream fiiii = new FileInputStream(new File("_" + most_similar_index + "_.txt"));
            ObjectInputStream oiiii = new ObjectInputStream(fiiii);
            Hashtable nugg = (Hashtable) oiiii.readObject();

            System.out.println("  ... ");
            System.out.println("  ... ");
            System.out.println("  ... ");
            System.out.println(wiki_u.name);
            System.out.println(" is most similar to ");
            System.out.println(nugg.name);
            String gui_outpoop = wiki_u.name + " ... is most similar to ... " + nugg.name;

            oiiii.close();
            fiiii.close();

            //randomly pick 5 indexes of articles3[] between 0 and 99
            int[] cluster1 = new int[articles3.length];
            int[] cluster2 = new int[articles3.length];
            int[] cluster3 = new int[articles3.length];
            int[] cluster4 = new int[articles3.length];
            int[] cluster5 = new int[articles3.length];
            int center1 = ayn.nextInt(articles3.length);
            int center2 = ayn.nextInt(articles3.length);
            int center3 = ayn.nextInt(articles3.length);
            int center4 = ayn.nextInt(articles3.length);
            int center5 = ayn.nextInt(articles3.length); //make loop and if to check for repeats


            //for each wiki, assign to nearest cluster center
            for(int i = 0; i < articles3.length; i++)
            {
                int closest_cluster = 0;
                int similarity = 0;

                //check how close to cluster 1, then 2, 3, 4, 5
                if(flatSquare[(100 * i) + center1] > similarity)
                {
                    similarity = flatSquare[(100*i)+center1];
                    closest_cluster = center1;
                }
                if(flatSquare[(100*i)+center2]>similarity){
                    similarity = flatSquare[(100*i)+center2];
                    closest_cluster = center2;
                }
                if(flatSquare[(100*i)+center3]>similarity){
                    similarity = flatSquare[(100*i)+center3];
                    closest_cluster = center3;
                }
                if(flatSquare[(100*i)+center4]>similarity){
                    similarity = flatSquare[(100*i)+center4];
                    closest_cluster = center4;
                }
                if(flatSquare[(100*i)+center5]>similarity){
                    //similarity = flatSquare[(100*i)+center5];
                    closest_cluster = center5;
                }

                //add articles3[i] to that cluster //change so article3 contains names not urls
                if(closest_cluster==center1){
                    cluster1[i]=i;
                }
                if(closest_cluster==center2){
                    cluster2[i]=i;
                }
                if(closest_cluster==center3){
                    cluster3[i]=i;
                }
                if(closest_cluster==center4){
                    cluster4[i]=i;
                }
                if(closest_cluster==center5){
                    cluster5[i]=i;
                }

            }

            System.out.println("cluster 1 contains: ");
            for(int ind=0;ind<cluster1.length;ind++){
                if(cluster1[ind]>0){
                    System.out.println(articles3[cluster1[ind]]);
                }
            }
            System.out.println("cluster 2 contains: ");
            for(int ind=0;ind<cluster2.length;ind++){
                if(cluster2[ind]>0){
                    System.out.println(articles3[cluster2[ind]]);
                }
            }
            System.out.println("cluster 3 contains: ");
            for(int ind=0;ind<cluster3.length;ind++){
                if(cluster3[ind]>0){
                    System.out.println(articles3[cluster3[ind]]);
                }
            }
            System.out.println("cluster 4 contains: ");
            for(int ind=0;ind<cluster4.length;ind++){
                if(cluster4[ind]>0){
                    System.out.println(articles3[cluster4[ind]]);
                }
            }
            System.out.println("cluster 5 contains: ");
            for(int ind=0;ind<cluster5.length;ind++){
                if(cluster5[ind]>0){
                    System.out.println(articles3[cluster5[ind]]);
                }
            }

            String garigulla = arrayToString(flatSquare) + " (cluster1) " + arrayToString(cluster1) + " (cluster2) " + arrayToString(cluster2) + " (cluster3) " + arrayToString(cluster3) + " (cluster4) " + arrayToString(cluster4) + " (cluster5) " + arrayToString(cluster5);
            System.out.println(garigulla);
            garigulla = garigulla + " (articles3) " + arrayToString(articles3);
            System.out.println(garigulla);
            File simTable = new File("C:/temp/simTable6.txt");
            FileWriter ffww = new FileWriter(simTable);
            ffww.write(garigulla);
            ffww.close();

            /*
            String oogabooga = arrayToString(articles3);
            File articlelinks = new File("C:/temp/articlelinks.txt");
            FileWriter ffwwal = new FileWriter(articlelinks);
            ffwwal.write(oogabooga);
            ffwwal.close();

             */


            outputLabel.setText(gui_outpoop);

        } catch (IOException ee) {
            ee.printStackTrace();
        } catch (ClassNotFoundException ee) {
            ee.printStackTrace();
        }

    }

    public String arrayToString(int[] array){
        String poop = "";
        for(int i=0;i<array.length;i++){
            poop = poop + "<" + i + "." + array[i] + ">";
        }
        return poop;
    }

    public String arrayToString(String[] array){
        String poop = "";
        for(int i=0;i<array.length;i++){
            poop = poop + "<" + i + "." + array[i] + ">";
        }
        return poop;
    }

    public Hashtable[] parseXML(){

        //parse xml file
        File inputFile = new File("src\\com\\company\\Wikipedia.xml\\"); //"C:\\Users\\tgerm\\ProjectPantaleev\\src\\com\\company\\" + file
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException parserConfigurationException) {
            parserConfigurationException.printStackTrace();
        } catch (SAXException saxException) {
            saxException.printStackTrace();
        }
        com.company.CSC365Handler handler = new com.company.CSC365Handler();
        try {
            saxParser.parse(inputFile, handler);
        } catch (SAXException saxException) {
            saxException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return handler.articles;

    }

    public boolean legitWiki(String s){
        if(s.contains("orghttps") || s.contains("/Wikipedia:") || s.contains("wikimedia")){
            return false;
        }
        if(s.contains("Help:") || s.contains("Template") || s.contains("Uncategorized")){
            return false;
        }
        if(s.contains("Unused") || s.contains("Wanted") || s.contains("Pages")){
            return false;
        }
        if(s.contains("Special:") || s.contains("%")){
            return false;
        }
        return true;
    }

    public boolean legitWiki2(String s){
        if(!s.contains("/wiki/")){
            return false;
        }
        for(int i = 6; i < s.length(); i++){
            if(!(s.charAt(i) > 64 && s.charAt(i) < 91)){
                if(!(s.charAt(i) > 96 && s.charAt(i) < 123)){
                    if(!(s.charAt(i)==47) && !(s.charAt(i)==95)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String charCheck(String text){
        if(text.length()>1000){  //500 //2000
            return text.substring(0,999); //499 //1999
        }
        return text;
    }

    //show all wikis
    //for given wiki, cluster
    //for given cluster, all wikis
    //for two wiki, similarities
    //for wiki, most similar

}

