/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.create.index.automatically;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.lang3.StringEscapeUtils;
/**
 *
 * @author ASOS
 */
public class putIndex {
    
    static String urlServer = "http://localhost:9200";
    
    private static putIndex Instance = null;

    public static putIndex getInstance() {
        if (Instance == null) {
            Instance = new putIndex();
        }
        return Instance;
    }

    
    public static void main(String[] args) throws MalformedURLException, IOException, ParseException, InterruptedException {
        
       /* String indexName = "denemeyap";
        putIndex.getInstance().putIndex(urlServer, indexName);*/
       
        ArrayList<String> indexList = putIndex.getInstance().indexNameAdjustment();
        for (int i = 0; i < indexList.size(); i++) {
            String indexName = indexList.get(i);
            putIndex.getInstance().putIndex(urlServer, indexName);
            Thread.sleep(2000);
        }
       
    }

    
    private void putIndex(String urlServer, String indexName) throws MalformedURLException, IOException, ParseException {
        String urly = urlServer+"/"+indexName;
        URL obj = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        
        String indexSettings = putIndex.getInstance().readAndParseJsonFile("indexSettings.json").toString();
 
        wr.writeBytes(indexSettings);
        wr.flush();
        wr.close();

        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            System.out.println("Response Code : " + HttpResult);
            System.out.println("İndex Oluşturma İşlemi Başarılı : " + HttpResult);
        } else {
            System.out.println("İndex Oluşturma İşlemi Hatalı " + con.getResponseMessage());

        }

        BufferedReader iny = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = iny.readLine()) != null) {
            response.append(output);
        }
        iny.close();

        System.out.println(response.toString());
    }
    
    private void deleteIndex(String urlServer, String indexName) throws MalformedURLException, IOException, ParseException {
        String urly = urlServer+"/"+indexName;
        URL obj = new URL(urly);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("DELETE");
        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);

        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            System.out.println("Response Code : " + HttpResult);
            System.out.println("İndex Silme İşlemi Başarılı : " + HttpResult);
        } else {
            System.out.println("İndex Silme İşlemi Hatalı " + con.getResponseMessage());

        }
    }
    
    private org.json.simple.JSONObject readAndParseJsonFile(String fileName) throws ParseException {
        try {
            StringBuilder sb = new StringBuilder();
            Scanner readfile = new Scanner(new FileReader(getClass().getClassLoader().getResource("queriesJson/" + fileName).getFile()));
            while (readfile.hasNext()) {
                sb.append(readfile.next());
            }
            JSONParser queryParser = new JSONParser();
            return (org.json.simple.JSONObject) queryParser.parse(sb.toString());
        } catch (IOException e) {
            System.err.println("Error while reading file " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Error while parsing json " + e.getMessage());
        }
        return null;
    }
    
    public ArrayList<String> indexNameAdjustment() throws UnsupportedEncodingException, IOException {
        ArrayList<String> list = new ArrayList<>();

        try {
            File file = new File(getClass().getClassLoader().getResource("indexList/indexList.yml").getFile());
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                list.add(editText(line));
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(putIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public String editText(String text){
        text = StringEscapeUtils.unescapeHtml4(text);
        text=text.toLowerCase();
        text=text.replace(" ", "");
        text=text.replace("ı", "i");
        text = text.replaceAll("[^a-zA-Z0-9 -]", "");
        return text;
    }

}
