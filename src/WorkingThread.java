import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.json.JSONObject;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

public class WorkingThread extends Thread {
    private String routerLocalIp;
    private boolean stop = false;
    private String routerPublicIp;
    private int queryDelay;
    private String user;
    private String pass;
    private String hostname;
    private JSONObject jsonObject;

    public WorkingThread(String routerLocalIp, int queryDelay, String user, String pass, String hostname){
        this.routerLocalIp = routerLocalIp;
        this.queryDelay = queryDelay;
        this.user = user;
        this.pass = pass;
        this.hostname = hostname;
    }

    public void run(){
        URL queryUrl = null;
        HttpURLConnection httpURLConnection;
        BufferedOutputStream BufferedOutputStream;
        BufferedInputStream bufferedInputStream;
        try {
            queryUrl = new URL("http://"+this.routerLocalIp+"/ws");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        while(!stop){
            try {
                httpURLConnection = (HttpURLConnection) queryUrl.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);  //Allow to write a request payload
                httpURLConnection.setRequestProperty("Content-Type", "application/x-sah-ws-4-call+json");   //Content type accepted by the router
                BufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                BufferedOutputStream.write("{\"service\":\"NMC\",\"method\":\"getWANStatus\",\"parameters\":{}}".getBytes());    //Send parameters to router to get WLAN infos
                BufferedOutputStream.flush();
                BufferedOutputStream.close();
                bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                if (httpURLConnection.getResponseCode() == 200) {
                    jsonObject = new JSONObject(new String(bufferedInputStream.readAllBytes()));
                    bufferedInputStream.close();
                    if(!(getRouterPublicIpFromJSON(jsonObject).equals(routerPublicIp)))
                        sendIP();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(queryDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void quit(){
        stop = true;
    }

    private String getRouterPublicIpFromJSON(JSONObject jsonObject){
        return jsonObject.getJSONObject("data").getString("IPAddress");
    }

    public void sendIP() {
        URL dynHostUrl;
        BufferedInputStream bufferedInputStream;
        routerPublicIp = getRouterPublicIpFromJSON(jsonObject);
        try {
            dynHostUrl = new URL("https://www.ovh.com/nic/update?system=dyndns&hostname=" + hostname + "&myip=" + routerPublicIp);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) dynHostUrl.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder().encode(new String(user + ":" + pass).getBytes())));
            bufferedInputStream = new BufferedInputStream(httpsURLConnection.getInputStream());
            if (httpsURLConnection.getResponseCode() == 200)
                System.out.println("DynHost submission successfull");
            else
                System.out.println("DynHost submission failed");
            System.out.println(new String(bufferedInputStream.readAllBytes()));
        }catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
