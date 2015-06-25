package giantBomb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import giantBomb.entity.GbSearchMainEntity;
import giantBomb.entity.ResultsEntry;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GiantBombAPI {

    private String API_KEY = "6d07235c0d52db622c1e2ff5a033d00ed3fc51aa";
    String URL = "http://www.giantbomb.com/api";
    boolean slowDown = false;

    public String searchGame(String query) throws UnsupportedEncodingException, InterruptedException {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(clientConfig);
        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        client.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 5000);
        client.setFollowRedirects(true);

        String encodedQ = URLEncoder.encode(query, "UTF-8");
        String endPoint = "/search/?"+getApiKeyParam()+"&format=json&query="+encodedQ+"&resources=game";

        WebResource resource = client.resource(URL+endPoint);
        ClientResponse response = resource.get(ClientResponse.class);

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

    private String getApiKeyParam(){
        return "api_key="+API_KEY;
    }

    public GbSearchMainEntity getSearchEntityObject(String response){
        return new Gson().fromJson(response, new TypeToken<GbSearchMainEntity>(){}.getType());
    }

    public String getReleaseDateByGameName(String gameName) throws UnsupportedEncodingException, InterruptedException {
        if(slowDown){
            return "#NEED_DATA";
        }
        String response = this.searchGame(gameName);
        if(response.contains("Slow down cowboy")){
            if(!slowDown){
                System.out.println("Told to Slow Down");
                slowDown = true;
            }
            return "#NEED_DATA";
        }
        GbSearchMainEntity sObj = this.getSearchEntityObject(response);
        for (ResultsEntry result : sObj.getResults()) {
            if (result.getName().equalsIgnoreCase(gameName)) {
                String orginal_date = result.getOriginal_release_date();
                if(StringUtils.isBlank(orginal_date)){
                    return "#NO_RELEASE_DATE";
                }
                String releaseDate = orginal_date.substring(0, orginal_date.length() - 9);
                System.out.println("Found Release date for '"+gameName+"' = "+releaseDate);
                return releaseDate;
            }
        }
        return "#NOT_FOUND";
    }

}
