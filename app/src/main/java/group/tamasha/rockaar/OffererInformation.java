package group.tamasha.rockaar;

/**
 * Created by HoseinDoroud on 4/15/2018.
 */

public class OffererInformation {

    public String name;
    public String category;
    public String porjectOwnerUsername;
    public String projectIndex;

    public OffererInformation(String name, String categoty, String projectOwnerUsername, String projectIndex) {

        this.name = name;
        this.category = categoty;
        this.porjectOwnerUsername = projectOwnerUsername;
        this.projectIndex = projectIndex;

    }
}
