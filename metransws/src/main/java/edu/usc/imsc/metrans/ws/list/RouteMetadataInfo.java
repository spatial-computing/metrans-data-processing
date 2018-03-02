package edu.usc.imsc.metrans.ws.list;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RouteMetadataInfo {
    private String display_name;
    private String id;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
