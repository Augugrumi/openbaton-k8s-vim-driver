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
import java.util.logging.Logger;

public class K8sVimInstance extends BaseVimInstance {

    private static final Logger LOGGER = Logger.getLogger(K8sVimInstance.class.getName());

    private String address = System.getenv("HARBOR_ADDRESS") == null ? "0.0.0.0" : System.getenv("HARBOR_ADDRESS") + HarborConstants.VNF;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<K8sImage> images;

    K8sVimInstance () {
        super();
        LOGGER.info("Harbor address: " + address);
    }

    @Override
    public Set<? extends BaseNfvImage> getImages() {
        return images;
    }

    @Override
    public Set<? extends BaseNetwork> getNetworks() {
        return new HashSet<>();
    }

    @Override
    public void addAllNetworks(Collection<BaseNetwork> networks) {

    }

    @Override
    public void addAllImages(Collection<BaseNfvImage> images) {
        if (this.images == null)
            this.images = new HashSet<>();
        images.forEach(i -> this.images.add((K8sImage) i));
    }

    @Override
    public void removeAllNetworks(Collection<BaseNetwork> networks) {

    }

    @Override
    public void removeAllImages(Collection<BaseNfvImage> images) {
        this.images.clear();
    }

    @Override
    public void addImage(BaseNfvImage image) {

    }

    @Override
    public void addNetwork(BaseNetwork network) {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address + HarborConstants.VNF;
    }
}
