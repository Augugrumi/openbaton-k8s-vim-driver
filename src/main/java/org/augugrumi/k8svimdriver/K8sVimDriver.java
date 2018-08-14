package org.augugrumi.k8svimdriver;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class K8sVimDriver extends VimDriver {

    /**
     * Logging utility field
     */
    private static final Logger LOGGER = Logger.getLogger(K8sVimDriver.class.getName());

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
        try {
            sendGET(buildRequest(((K8sVimInstance) vimInstance).getAddress(),
                    HarborConstants.LAUNCH,
                    name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Server();
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
        return null;
    }

    @Override
    public List<DeploymentFlavour> listFlavors(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("listFlavors");
        return null;
    }

    @Override
    public BaseVimInstance refresh(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("refresh");
        return null;
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
        return null;
    }

    @Override
    public DeploymentFlavour addFlavor(BaseVimInstance vimInstance, DeploymentFlavour deploymentFlavour) throws VimDriverException {
        LOGGER.info("addFlavor");
        return null;
    }

    @Override
    public BaseNfvImage addImage(BaseVimInstance vimInstance, BaseNfvImage image, byte[] imageFile) throws VimDriverException {
        LOGGER.info("addImage");
        LOGGER.info("launchInstance");
        try {
            sendPOST(buildRequest(((K8sVimInstance) vimInstance).getAddress(),
                    HarborConstants.CREATE,
                    ((K8sImage)image).getId()), imageFile); //TODO do something better?
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public BaseNfvImage addImage(BaseVimInstance vimInstance, BaseNfvImage image, String image_url) throws VimDriverException {
        LOGGER.info("addImage");
        return null;
    }

    @Override
    public BaseNfvImage updateImage(BaseVimInstance vimInstance, BaseNfvImage image) throws VimDriverException {
        LOGGER.info("updateImage");

        return null;
    }

    @Override
    public BaseNfvImage copyImage(BaseVimInstance vimInstance, BaseNfvImage image, byte[] imageFile) throws VimDriverException {
        LOGGER.info("copyImage");
        return null;
    }

    @Override
    public boolean deleteImage(BaseVimInstance vimInstance, BaseNfvImage image) throws VimDriverException {
        LOGGER.info("deleteImage");
        try {
            sendGET(buildRequest(((K8sVimInstance) vimInstance).getAddress(),
                    HarborConstants.DELETE,
                    ((K8sImage)image).getId()));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public DeploymentFlavour updateFlavor(BaseVimInstance vimInstance, DeploymentFlavour deploymentFlavour) throws VimDriverException {
        LOGGER.info("updateFlavor");
        return null;
    }

    @Override
    public boolean deleteFlavor(BaseVimInstance vimInstance, String extId) throws VimDriverException {
        LOGGER.info("deleteFlavor");
        return false;
    }

    @Override
    public Subnet createSubnet(BaseVimInstance vimInstance, BaseNetwork createdNetwork, Subnet subnet) throws VimDriverException {
        LOGGER.info("createSubnet");
        return null;
    }

    @Override
    public BaseNetwork updateNetwork(BaseVimInstance vimInstance, BaseNetwork network) throws VimDriverException {
        LOGGER.info("updateNetwork");
        return null;
    }

    @Override
    public Subnet updateSubnet(BaseVimInstance vimInstance, BaseNetwork updatedNetwork, Subnet subnet) throws VimDriverException {
        LOGGER.info("updateSubnet");
        return null;
    }

    @Override
    public List<String> getSubnetsExtIds(BaseVimInstance vimInstance, String network_extId) throws VimDriverException {
        LOGGER.info("getSubnetsExtIds");
        return null;
    }

    @Override
    public boolean deleteSubnet(BaseVimInstance vimInstance, String existingSubnetExtId) throws VimDriverException {
        LOGGER.info("deleteSubnet");
        return false;
    }

    @Override
    public boolean deleteNetwork(BaseVimInstance vimInstance, String extId) throws VimDriverException {
        LOGGER.info("deleteNetwork");
        return false;
    }

    @Override
    public BaseNetwork getNetworkById(BaseVimInstance vimInstance, String id) throws VimDriverException {
        LOGGER.info("getNetworkById");
        return null;
    }

    @Override
    public Quota getQuota(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("getQuota");
        return null;
    }

    @Override
    public String getType(BaseVimInstance vimInstance) throws VimDriverException {
        LOGGER.info("getType");
        return null;
    }
}
