import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class WorkingThread extends Thread {
    private String routerLocalIp;
    private boolean stop = false;
    private String routerPublicIp;
    private int queryDelay;

    public WorkingThread(String routerLocalIp, int queryDelay){
        this.routerLocalIp = routerLocalIp;
        this.queryDelay = queryDelay;
    }

    public void run(){
        URL url = null;
        HttpURLConnection httpURLConnection;
        BufferedOutputStream BufferedOutputStream;
        BufferedInputStream bufferedInputStream;
        JSONObject jsonObject;
        try {
            url = new URL("http://"+this.routerLocalIp+"/ws");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        while(!stop){
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);  //Allow to write a request payload
                httpURLConnection.setRequestProperty("Content-Type", "application/x-sah-ws-4-call+json");   //Content type accepted by the router
                BufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                BufferedOutputStream.write("{\"service\":\"NMC\",\"method\":\"getWANStatus\",\"parameters\":{}}".getBytes());    //Send parameters to router to get WLAN infos
                BufferedOutputStream.flush();
                BufferedOutputStream.close();
                bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                switch(httpURLConnection.getResponseCode()){
                    case 200:
                        jsonObject = new JSONObject(new String(bufferedInputStream.readAllBytes()));
                        bufferedInputStream.close();
                        routerPublicIp = getRouterPublicIpFromJSON(jsonObject);
                        System.out.println(routerPublicIp);
                        break;
                    default:
                        break;
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
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
}
