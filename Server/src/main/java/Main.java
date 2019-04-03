import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import com.twilio.Twilio;


import java.util.*;
import java.net.*;
import java.io.*;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static spark.Spark.*;

public class Main {
    public static final String SMMRY = "http://api.smmry.com/&SM_API_KEY=&SM_URL=x";
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";
    public static final String GOOGLE_API_KEY = "&key=";
    private final static String DUCKDUCKGO_SEARCH_URL = "https://duckduckgo.com/html/?q=";


    public enum Mode {
        driving, walking, bicycling, transit;
    }

    public static void main(String[] args) {

        get("/", (req, res) -> "Hello Web");
        post("/sms", (req, res) -> {
            res.type("application/xml");
            System.out.println(getPostMessage(req.body()));
            String str = sendResponse(getPostMessage(req.body()));

            Body body = new Body
                    .Builder(str)
                    .build();
            Message sms = new Message
                    .Builder()
                    .body(body)
                    .build();
            MessagingResponse twiml = new MessagingResponse
                    .Builder()
                    .message(sms)
                    .build();
            return twiml.toXml();
        });
      }
    public static  String sendResponse(String message){
        message = message.toLowerCase();
        String[] sp = message.split("%25");
        if(sp[0].equals("answerbox")){
            return getSearchResults(sp[1]);
        }
        else if(sp[0].equals("directions")){
            Mode mode;
            if(sp[1].equals("driving")){
                mode = Mode.driving;
            }
            else if(sp[1].equals("walking")){
                mode = Mode.walking;
            }
            else if(sp[1].equals("bicycling")){
                mode = Mode.bicycling;
            }
            else if(sp[1].equals("transit")){
                mode = Mode.transit;
            }
            else{
                return "x";
            }
            try{
                return directions(mode,sp[2],sp[3]);
            }
            catch (Exception e){

            }

        }
        else if(sp[0].equals("smmry")){
            try{

                sp[1]= sp[1].replace("%3a", ":");
                sp[1]= sp[1].replace("%2f", "/");
                String s2 = smmry(sp[1]);
                s2 = s2.replaceAll("\\\\\"", "");

                List<String> list = JSONparser(s2,"sm_api_content",34,34 );
                String s = list.get(1);
                while(s.length()>1000){
                    s = s.substring(0, s.lastIndexOf('.'));
                }

                String resp = "feature:smmry|"+s+".";
                return resp;

            }
            catch (Exception e){
                System.out.println(e.toString());
            }
        }
        return "x";
    }


    public static String getPostMessage(String body){
        StringBuilder sb = new StringBuilder();
        String[] s = body.split("Body");
        int i = 1;
        while(s[1].charAt(i)!='&'){
            sb.append(s[1].charAt(i));
            i++;
        }

        return sb.toString();
    }

    public static String smmry(String website)throws Exception{
        StringBuilder sb = new StringBuilder();
        String s = SMMRY.replace("x",website);
        URL url = new URL(s);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        System.out.println(url);
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();

        return sb.toString();
    }

    public static String getSearchResults(String query){
        Document doc = null;
        StringBuilder sb = new StringBuilder();
        try {
            doc = Jsoup.connect(DUCKDUCKGO_SEARCH_URL + query).get();
            Elements results = doc.getElementById("links").getElementsByClass("results_links");

            int count =0;
            for(Element result: results){

                Element title = result.getElementsByClass("links_main").first().getElementsByTag("a").first();
                String s = "\nURL:" + title.attr("href");

                if(s.contains("github")||s.contains("wiki")||s.contains("dictionary")||s.contains("knowyourmeme")){

                    sb.append(s+"#");
                    sb.append("Snippet:" + result.getElementsByClass("result__snippet").first().text()+"|");
                    count++;
                }
                if(count>6||sb.length()>1200){
                    sb.insert(0,"feature:answerbox|");
                    return sb.toString();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(sb.length()==0){
            return "No relevent Search Results";
        }
        sb.insert(0,"feature:answerbox|");
        return sb.toString();
    }

    public static String directions(Mode mode, String origin, String destination ) throws Exception {
        String googleMapsCall = "https://maps.googleapis.com/maps/api/directions/json?";
        googleMapsCall+="origin="+origin;
        googleMapsCall+="&destination="+ destination;
        if(mode!= Mode.driving){
            googleMapsCall+= "&Mode="+mode.toString();
        }
        googleMapsCall= googleMapsCall.replaceAll(" ", "\\+");
        StringBuilder result = new StringBuilder();
        System.out.print(googleMapsCall+GOOGLE_API_KEY);
        URL url = new URL(googleMapsCall+ GOOGLE_API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        String s = result.toString();

        s = s.replaceAll("\\/","");
        s = s.replaceAll("\\\\","");
        s = s.replaceAll(" u003cbu003e"," ");
        s = s.replaceAll("u003cbu003e","");
        s = s.replaceAll("u003cdiv style=", "");
        s = s.trim();

        return stringProcessingDirections(s);
    }

    public static String stringProcessingDirections(String s){
        StringBuilder sb = new StringBuilder();
        List<String> l = JSONparser(s,"start_address", 34,34);
        sb.append("start_address:"+ l.get(0)+"|");
        l = JSONparser(s,"end_address", 34,34);
        sb.append("end_address:"+ l.get(0)+"|");
        l = JSONparser(s,"duration", 123,125);
        l= JSONparser(l.get(0),"text", 34,34);
        sb.append("duration:"+ l.get(0)+"|");
        List<String> l2 = new ArrayList<>();
        List<String> l3 = new ArrayList<>();

        l = JSONparser(s,"html_instructions", 34,34);
        l2 = JSONparser(s,"duration", 123,125);
        l3 = JSONparser(s,"distance", 123,125);

        for(int i =0; i < l.size();i++){
            sb.append(l.get(i)+"%");
            List<String> l4 = JSONparser(l2.get(i+1),"text",34,34);
            sb.append(l4.get(0)+"%");
            l4 = JSONparser(l3.get(i),"text",34,34);
            sb.append(l4.get(0));
            sb.append("|");
        }
        sb.insert(0,"feature:directions");
        return sb.toString();

}

    public static List<String> JSONparser(String s, String parseElement, int splitter1, int splitter2){
        String[] sp = s.split(parseElement);
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i<sp.length;i++){
            int lower = 1, higher = 1;

            while(sp[i].charAt(lower)!= (char)splitter1){
                lower++;
            }
            lower++;
            higher = lower;
            while(sp[i].charAt(higher)!= (char)splitter2){
                sb.append(sp[i].charAt(higher));
                higher++;
            }
            list.add(sb.toString());
            sb = new StringBuilder();
        }
        return list;
    }

}