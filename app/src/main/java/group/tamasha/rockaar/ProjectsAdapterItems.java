package group.tamasha.rockaar;

public class ProjectsAdapterItems
{
    public String pname;
    public String pdetails;
    public String pcost;
    public String day;
    public String date;
    public String bids;

    ProjectsAdapterItems(String name, String details, String cost, String day, String date, String bids)
    {
        this.pname = name;
        this.pdetails = details;
        this.pcost = cost;
        this.day = day;
        this.date = date;
        this.bids = bids;
    }
}
