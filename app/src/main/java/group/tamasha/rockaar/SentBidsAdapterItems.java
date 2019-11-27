package group.tamasha.rockaar;

public class SentBidsAdapterItems
{
    public int color;
    public String delete;
    public String edit;
    public String name;
    public String details;
    public String status;
    public String date;

    SentBidsAdapterItems(int color,String edit, String delete, String name, String details, String status, String date)
    {
        this.delete = delete;
        this.name = name;
        this.details = details;
        this.status = status;
        this.date = date;
        this.edit = edit;
        this.color = color;
    }
}
