import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import giantBomb.GiantBombAPI;
import myObjects.GameListObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import steam.AppsEntry;
import steam.HtmlParser;
import steam.SteamAppListEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SteamGameListerMain {

    public static void main(String arg[]) throws IOException, InterruptedException, UnsupportedEncodingException {
        final String separator = "~";
        final GiantBombAPI gbApi = new GiantBombAPI();

        String getAppUrl = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
        String response = makeGetRequest(getAppUrl);
        SteamAppListEntity appObject = new Gson().fromJson(response, new TypeToken<SteamAppListEntity>() {
        }.getType());

        System.out.println("Number of Apps: " + appObject.getApplist().getApps().size());

        final HashMap<Integer, GameListObject> masterList = getMasterList();
        List<AppsEntry> steamAppList = createSteamList(appObject.getApplist().getApps());

        ExecutorService executor = Executors.newFixedThreadPool(50);
        for (final AppsEntry entry : steamAppList) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!masterList.containsKey(entry.getAppid())) {
                        GameListObject newEntry = new GameListObject();
                        newEntry.setGameTitle(entry.getName().replaceAll("~", "-"));
                        newEntry.setAppId(entry.getAppid());
                        newEntry.setUrl(String.format("http://store.steampowered.com/app/%d", newEntry.getAppId()));

                        StringBuffer sb = new StringBuffer();
                        sb.append(newEntry.getUrl() + "\n");
                        try {
                            HtmlParser parser = new HtmlParser();
                            parser.fetchHtmlData(newEntry.getUrl());
                            if (parser.htmlText.contains("<title>Welcome to Steam")
                                    || parser.htmlText.contains("Site Error")) {
                                return;
                            }
                            if (parser.htmlText.contains("agecheck/app")) {
                                newEntry.setReleaseDate("#AGE_CHECK_HIT");
                                throw new Exception("Age Check Page was hit.");
                            }
                            parser.setTagsByCss(".details_block");
                            Elements tags = parser.getTags();
                            for (Element tag : tags) {
                                final String text = tag.text();
                                if (text.contains("Release Date")) {
                                    final int begIndx = text.lastIndexOf("Date: ") + 6;
                                    String releaseDate = text.substring(begIndx);
                                    if (releaseDate.length() > 12 && releaseDate.matches(".*?\\d")) {
                                        releaseDate = releaseDate.substring(0, 12);
                                    }
                                    newEntry.setReleaseDate(releaseDate);
                                    // GIANT BOMB Release Date Test
                                    newEntry.setReleaseDateGB(gbApi.getReleaseDateByGameName(newEntry.getGameTitle()));
                                }
                            }
                        } catch (Throwable e) {
                            sb.append("Did not find Release date for: " + newEntry.getGameTitle() + "\n" + e.getLocalizedMessage()
                                    +"\n"+e.getStackTrace()[0]
                                    +"\n"+e.getStackTrace()[1]
                                    +"\n"+e.getStackTrace()[2]
                                    +"\n"+e.getStackTrace()[3]
                            );
                            System.out.println(sb.toString());
                        } finally {
                            if(newEntry.getReleaseDate() != null) {
                                System.out.println("Adding New Game to List: "+newEntry.getGameTitle() + " -- "+newEntry.getUrl());
                                masterList.put(newEntry.getAppId(), newEntry);
                            }
                        }
                    } else {
                        try {
                            GameListObject existingGame = masterList.get(entry.getAppid());
                            if(existingGame.getReleaseDate().equals("null")){
                                System.out.println("Removing Game from List: "+existingGame.getGameTitle() + " -- "+existingGame.getUrl());
                                masterList.remove(existingGame.getAppId());
                            }
                            if (existingGame.getReleaseDate().length() > 12) {
                                existingGame.setReleaseDate(existingGame.getReleaseDate().substring(0, 12));
                            }
                            if (existingGame.getReleaseDateGB().equals("#NEED_DATA")) {
                                existingGame.setReleaseDateGB(gbApi.getReleaseDateByGameName(existingGame.getGameTitle()));
                                masterList.put(entry.getAppid(), existingGame);
                            }
                        }catch(Exception e){
                            System.err.println("Issue Found while trying to get Giant Bomb Info.");
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.MINUTES);

        File file = new File("masterCSV.csv");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
        }
        System.out.println("Writing Game Objects to CSV.");
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Game Name" + separator + "Release Date" + separator + "GB_ReleaseDate" + separator + "AppId" + separator + "Url\n");

            for (GameListObject obj : masterList.values()) {
                bw.write(obj.getGameTitle() + separator
                        + obj.getReleaseDate() + separator
                        + obj.getReleaseDateGB() + separator
                        + obj.getAppId() + separator
                        + obj.getUrl() + "\n");

            }
        }
    }

    public static String makeGetRequest(String url){
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(clientConfig);
        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        client.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 5000);
        client.setFollowRedirects(true);

        WebResource resource = client.resource(url);
        ClientResponse response = resource.get(ClientResponse.class);

        int status = response.getStatus();
        String stringResponse = "";
        try{
            stringResponse = response.getEntity(String.class);
            if (stringResponse == null){
                System.err.println("String response returned 'null'. Failing Test.");
            }
        }catch(IllegalArgumentException e){
            if(e.getMessage().contains("Error parsing media type ''"))
                System.err.println("GET response does not have a content type.");
            else
                System.err.println("IllegalArgumentException occurred.");
        }
        return stringResponse;
    }

    public static HashMap<Integer, GameListObject> getMasterList() throws IOException {
        String line = "";
        String cvsSplitBy = "~";
        HashMap<Integer, GameListObject> myObjList = new HashMap<>();

        try(FileReader fr = new FileReader(new File("masterCSV.csv"));
                BufferedReader br = new BufferedReader(fr)) {
            while ((line = br.readLine()) != null) {
                if(line.contains("Release Date")){
                    continue;
                }
                GameListObject myGameObj = new GameListObject();
                String[] gameCSVSplit = line.split(cvsSplitBy);

                myGameObj.setGameTitle(gameCSVSplit[0]);
                myGameObj.setReleaseDate(gameCSVSplit[1]);
                myGameObj.setReleaseDateGB(gameCSVSplit[2]);
                myGameObj.setAppId(Integer.parseInt(gameCSVSplit[3]));
                myGameObj.setUrl(gameCSVSplit[4]);
                myObjList.put(myGameObj.getAppId(), myGameObj);
            }
        }
        System.out.println("Retrieved List from CSV. Contained: "+myObjList.size()+" Items.");
        return myObjList;
    }

    public static List<AppsEntry> createSteamList(List<AppsEntry> appList){
        List<AppsEntry> filteredList = new ArrayList<>();
        for(AppsEntry entry : appList) {
            if (entry.getName().contains("Trailer") && !entry.getName().contains("Truck")
                    || entry.getName().contains("SoundTrack")) {
                continue;
            }
            filteredList.add(entry);
        }
        System.out.println("Filtered Steam App List.");
        return filteredList;
    }

}