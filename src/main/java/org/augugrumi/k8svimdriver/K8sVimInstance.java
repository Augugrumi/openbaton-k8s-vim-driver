package org.augugrumi.k8svimdriver;

import javax.persistence.*;

import org.openbaton.catalogue.nfvo.images.BaseNfvImage;
import org.openbaton.catalogue.nfvo.images.NFVImage;
import org.openbaton.catalogue.nfvo.networks.BaseNetwork;
import org.openbaton.catalogue.nfvo.networks.Network;
import org.openbaton.catalogue.nfvo.viminstances.BaseVimInstance;
import org.openbaton.catalogue.nfvo.viminstances.GenericVimInstance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class K8sVimInstance extends GenericVimInstance {
    // TODO add specific for our k8s vim instance
    private String address = "0.0.0.0";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
