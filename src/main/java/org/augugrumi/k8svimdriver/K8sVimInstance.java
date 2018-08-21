package org.augugrumi.k8svimdriver;

import javax.persistence.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openbaton.catalogue.nfvo.images.BaseNfvImage;
import org.openbaton.catalogue.nfvo.images.NFVImage;
import org.openbaton.catalogue.nfvo.networks.BaseNetwork;
import org.openbaton.catalogue.nfvo.networks.Network;
import org.openbaton.catalogue.nfvo.viminstances.BaseVimInstance;
import org.openbaton.catalogue.nfvo.viminstances.GenericVimInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class K8sVimInstance extends GenericVimInstance {

    private String address = System.getenv("HARBOR_ADDRESS") == null ? "0.0.0.0" : System.getenv("HARBOR_ADDRESS");

    private String sendGET(String address) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();

    }

    private String buildRequest(String...args) {
        return String.join("", args);
    }

    K8sVimInstance () {
        super();
        // FIXME use a proper logger
        System.out.println("Harbor address: " + address);

        List<BaseNfvImage> images = new ArrayList<>();
        try {
            String response =
                    sendGET(buildRequest(getAddress(), HarborConstants.LIST));
            JsonObject jsonResponse = (JsonObject) new JsonParser().parse(response);
            if (jsonResponse.get("result").getAsString().equals("ok")) {
                JsonArray jsonArrayImages = jsonResponse.getAsJsonArray("content");
                K8sImage k8sImage;
                String name;
                for(JsonElement obj : jsonArrayImages) {
                    name = obj.getAsString();
                    k8sImage = new K8sImage();
                    // FIXME I do not know what I am doing here
                    k8sImage.setId(name);
                    k8sImage.setExtId(name);
                    images.add(k8sImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        addAllImages(images);
    }

    String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address + HarborConstants.VNF;
    }
}
