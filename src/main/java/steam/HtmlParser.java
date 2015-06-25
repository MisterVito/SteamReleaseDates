package steam;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class HtmlParser {

    // Setup up http client config
    public static ClientConfig config  = new DefaultClientConfig();
    public Client client;
    public String clientBaseUrl;
    WebResource resource;
    ClientResponse response;
    int responseCode;
    public String htmlText;
    Document htmlDoc;
    Elements tags;

    public HtmlParser() {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public X509Certificate[] getAcceptedIssuers(){return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType){}
            public void checkServerTrusted(X509Certificate[] certs, String authType){}
        }};

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { return true; }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hv,sc));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Failed to get SSLContext " + e.getMessage());
        } catch (KeyManagementException e){
            System.err.println("Failed to get SSL Key " + e.getMessage());
        }

        client = Client.create(config);
        client.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 5000);
        if(Config.DEBUG) { client.addFilter( new LoggingFilter(System.out) ); }
    }

    /**
     * Fetches the html doc and parse it in object so that other methods can traverse that easily
     *
     * @param url
     * @return
     */
    public HtmlParser fetchHtmlData(String url) {
        resource = client.resource(url);
        response = resource.cookie(new Cookie("lastagecheckage", "1-January-1988", "/", "store.steampowered.com"))
                .cookie(new Cookie("birthtime", "568022401", "/", "store.steampowered.com"))
                .get(ClientResponse.class);
        htmlText = response.getEntity(String.class);
        responseCode = response.getStatus();
        assertEquals(responseCode,200,"API Timeout Error");
        htmlDoc = Jsoup.parse(htmlText);
        assertTrue(htmlDoc.hasText(), "Html Doc has text.");
        return this;
    }

    /**
     * Fetches the html doc with requested headers for search engine bot tests.
     * @param url
     * @param headers
     * @return
     */
    public HtmlParser fetchHtmlDataAsSearchBots(String url,Map<String, String> headers) {
        resource = client.resource(url);
        for(String headerName : headers.keySet()) {
            resource.header(headerName,headers.get(headerName));
        }
        response = resource.get(ClientResponse.class);
        htmlText = response.getEntity(String.class);
        responseCode = response.getStatus();
        htmlDoc = Jsoup.parse(htmlText);
        return this;
    }

    public HtmlParser fetchHTMLDataFromString(String data) {
        htmlText = data;
        htmlDoc = Jsoup.parse(htmlText);
        return this;
    }

    public MultivaluedMap<String, String> getUrlOptions(String url){
        resource = client.resource(url);
        response = resource.options(ClientResponse.class);
        if(response.getStatus() != 500){
            return response.getHeaders();
        }else{
            return null;
        }
    }

    public Document getHtmlDoc(){
        return htmlDoc;
    }

    public Elements getTags(){
        return this.tags;
    }

    public String getClientBaseUrl() {
        return clientBaseUrl;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int code) {
        responseCode = code;
    }

    public HtmlParser setTagsByCss(String query){
        tags = htmlDoc.select(query);
        assertTrue(tags.size() > 0, "Query returned tags.");
        return this;
    }
}
