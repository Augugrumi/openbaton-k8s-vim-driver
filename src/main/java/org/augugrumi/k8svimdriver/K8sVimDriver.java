package org.augugrumi.k8svimdriver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.JSObject;
import org.openbaton.catalogue.mano.common.DeploymentFlavour;
import org.openbaton.catalogue.mano.descriptor.VNFDConnectionPoint;
import org.openbaton.catalogue.nfvo.*;
import org.openbaton.catalogue.nfvo.images.BaseNfvImage;
import org.openbaton.catalogue.nfvo.networks.BaseNetwork;
import org.openbaton.catalogue.nfvo.networks.Subnet;
import org.openbaton.catalogue.nfvo.viminstances.BaseVimInstance;
import org.openbaton.catalogue.security.Key;
import org.openbaton.exceptions.VimDriverException;
import org.openbaton.vim.drivers.interfaces.VimDriver;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class K8sVimDriver extends VimDriver {

    /**
     * Logging utility field
     */
    private static final Logger LOGGER = Logger.getLogger(K8sVimDriver.class.getName());


    // FIXME use one http client class to make request properly.
    // http://hc.apache.org/httpcomponents-client-4.5.x/index.html
    // https://jersey.github.io/
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
        LOGGER.info(result.toString());
        return result.toString();

    }

    private String sendPOST(String address, byte[] post) throws IOException {

        URL obj = new URL(address);
        HttpURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(post);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();

    }

    private String buildRequest(String...args) {
        return String.join("", args);
    }

    @Override
    public Server launchInstance(BaseVimInstance vimInstance, String name, String image, String flavor, String keypair, Set<VNFDConnectionPoint> networks, Set<String> secGroup, String userData) throws VimDriverException {
        LOGGER.info("launchInstance");
        LOGGER.info(vimInstance.getClass().getName());
        try {
            sendGET(buildRequest(((K8sVimInstance)vimInstance).getAddress(),
                    HarborConstants.LAUNCH,
                    name));
        } catch (IOException e) {
            LOGGER.warning(e.toString());
            e.printStackTrace();
        }
        Server s = new Server();
        s.setCreated(new Date());
        s.setFlavor(new DeploymentFlavour());
        s.setHostName("hostname");
        s.setHypervisorHostName("hypervisorhostname");

        // TODO create proper server result when Harbor cli output get parsed correctly
        return s;
    }

    @Override
    public List<Server> listServer(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("listServer");
        // TODO
        // I don't know how to do it -> useless?
        return new ArrayList<>();
    }

    @Override
    public Server rebuildServer(BaseVimInstance vimInstance, String serverId, String imageId) throws VimDriverException {
        LOGGER.info("rebuildServer");
        // TODO
        // I don't know how to do it -> useless?
        return null;
    }

    @Override
    public List<BaseNetwork> listNetworks(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("listNetworks");
        // TODO
        // I don't know how to do it -> useless?
        return null;
    }

    @Override
    public List<BaseNfvImage> listImages(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("listImages");
        List<BaseNfvImage> images = new ArrayList<>();
        try {
            String response =
                    sendGET(buildRequest("http://192.168.30.13:31115/vnf/", HarborConstants.LIST));
            LOGGER.warning(response);
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
            LOGGER.warning(e.toString());
            e.printStackTrace();
        }

        return images;
    }

    @Override
    public List<DeploymentFlavour> listFlavors(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("listFlavors");
        // TODO check if working
        // Copied from openbaton go-docker-driver
        return new ArrayList<>();
    }

    @Override
    public BaseVimInstance refresh(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("refresh");

        BaseNfvImage newRefresh = new BaseNfvImage();

        newRefresh.setId("refresh");
        newRefresh.setExtId("refreshext");
        newRefresh.setCreated(new Date());

        vimInstance.addAllImages(listImages(vimInstance));
        vimInstance.addImage(newRefresh);
        return vimInstance;
    }

    @Override
    public Server launchInstanceAndWait(BaseVimInstance vimInstance, String hostname, String image, String extId, String keyPair, Set<VNFDConnectionPoint> networks, Set<String> securityGroups, String s, Map<String, String> floatingIps, Set<Key> keys) throws VimDriverException {
        LOGGER.info("launchInstanceAndWait");
        return launchInstance(vimInstance, hostname, image, extId, keyPair, networks, securityGroups, s);
    }

    @Override
    public Server launchInstanceAndWait(BaseVimInstance vimInstance, String hostname, String image, String extId, String keyPair, Set<VNFDConnectionPoint> networks, Set<String> securityGroups, String s) throws VimDriverException {
        LOGGER.info("launchInstanceAndWait");
        return launchInstance(vimInstance, hostname, image, extId, keyPair, networks, securityGroups, s);
    }

    @Override
    public void deleteServerByIdAndWait(BaseVimInstance vimInstance, String id) throws VimDriverException {
        LOGGER.info("deleteServerByIdAndWait");
    }

    @Override
    public BaseNetwork createNetwork(BaseVimInstance vimInstance, BaseNetwork network) throws VimDriverException {
        LOGGER.info("createNetwork");
        throw new UnsupportedOperationException();
    }

    @Override
    public DeploymentFlavour addFlavor(BaseVimInstance vimInstance, DeploymentFlavour deploymentFlavour) throws VimDriverException {
        LOGGER.info("addFlavor");
        throw new UnsupportedOperationException();
    }

    @Override
    public BaseNfvImage addImage(BaseVimInstance vimInstance, BaseNfvImage image, byte[] imageFile) throws VimDriverException {
        LOGGER.info("addImage");

        /*try {
            sendPOST(buildRequest(((K8sVimInstance) vimInstance).getAddress(),
                    HarborConstants.CREATE,
                    ((K8sImage)image).getId()), imageFile); //TODO do something better?
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        vimInstance.addImage(image);

        return image;
    }

    @Override
    public BaseNfvImage addImage(BaseVimInstance vimInstance, BaseNfvImage image, String image_url) throws VimDriverException {
        LOGGER.info("addImage");

        vimInstance.addImage(image);

        return image;
    }

    @Override
    public BaseNfvImage updateImage(BaseVimInstance vimInstance, BaseNfvImage image) throws VimDriverException {
        LOGGER.info("updateImage");

        BaseNfvImage updatedImage = new BaseNfvImage();
        updatedImage.setExtId(image.getExtId() + "updatedimageext");
        updatedImage.setId(image.getId() + "updatedimage");
        updatedImage.setCreated(new Date());

        vimInstance.addImage(updatedImage);

        return updatedImage;
    }

    @Override
    public BaseNfvImage copyImage(BaseVimInstance vimInstance, BaseNfvImage image, byte[] imageFile) throws VimDriverException {
        LOGGER.info("copyImage");

        vimInstance.addImage(image);

        return image;
    }

    @Override
    public boolean deleteImage(BaseVimInstance vimInstance, BaseNfvImage image) throws VimDriverException {
        LOGGER.info("deleteImage");
        /*try {
            sendGET(buildRequest(((K8sVimInstance) vimInstance).getAddress(),
                    HarborConstants.DELETE,
                    ((K8sImage)image).getId()));
        } catch (IOException e) {
            return false;
        }*/
        return true;
    }

    @Override
    public DeploymentFlavour updateFlavor(BaseVimInstance vimInstance, DeploymentFlavour deploymentFlavour) throws VimDriverException {
        LOGGER.info("updateFlavor");
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteFlavor(BaseVimInstance vimInstance, String extId) throws VimDriverException {
        LOGGER.info("deleteFlavor");
        throw new UnsupportedOperationException();
    }

    @Override
    public Subnet createSubnet(BaseVimInstance vimInstance, BaseNetwork createdNetwork, Subnet subnet) throws VimDriverException {
        LOGGER.info("createSubnet");
        throw new UnsupportedOperationException();
    }

    @Override
    public BaseNetwork updateNetwork(BaseVimInstance vimInstance, BaseNetwork network) throws VimDriverException {
        LOGGER.info("updateNetwork");
        throw new UnsupportedOperationException();
    }

    @Override
    public Subnet updateSubnet(BaseVimInstance vimInstance, BaseNetwork updatedNetwork, Subnet subnet) throws VimDriverException {
        LOGGER.info("updateSubnet");
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getSubnetsExtIds(BaseVimInstance vimInstance, String network_extId) throws VimDriverException {
        LOGGER.info("getSubnetsExtIds");
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteSubnet(BaseVimInstance vimInstance, String existingSubnetExtId) throws VimDriverException {
        LOGGER.info("deleteSubnet");
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteNetwork(BaseVimInstance vimInstance, String extId) throws VimDriverException {
        LOGGER.info("deleteNetwork");
        throw new UnsupportedOperationException();
    }

    @Override
    public BaseNetwork getNetworkById(BaseVimInstance vimInstance, String id) throws VimDriverException {
        LOGGER.info("getNetworkById");
        throw new UnsupportedOperationException();
    }

    @Override
    public Quota getQuota(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("getQuota");
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("getType");
        return "K8sVimInstance";
    }
}
