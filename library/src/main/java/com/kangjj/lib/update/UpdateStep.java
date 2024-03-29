package com.kangjj.lib.update;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class UpdateStep {

    private String versionFrom;
    private String versionTo;
    private List<UpdateDb> updateDbs;

    public UpdateStep(Element element) {
        versionFrom = element.getAttribute("versionFrom");
        versionTo = element.getAttribute("versionTo");
        NodeList dbs = element.getElementsByTagName("updateDb");
        updateDbs = new ArrayList<>();
        for (int i = 0; i < dbs.getLength(); i++) {
            Element db = (Element) dbs.item(i);
            UpdateDb updateDb = new UpdateDb(db);
            updateDbs.add(updateDb);
        }
    }

    public String getVersionFrom() {
        return versionFrom;
    }

    public String getVersionTo() {
        return versionTo;
    }

    public List<UpdateDb> getUpdateDbs() {
        return updateDbs;
    }
}
