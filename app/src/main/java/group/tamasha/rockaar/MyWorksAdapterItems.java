package group.tamasha.rockaar;

public class MyWorksAdapterItems
{
    public String delete;
    public String mname;
    public String mdetails;
    public String amount_of_bids;
    public String date;

    MyWorksAdapterItems(String delete, String name, String details, String amount_of_bids, String date)
    {
        this.delete = delete;
        this.mname = name;
        this.mdetails = details;
        this.amount_of_bids = amount_of_bids;
        this.date = date;
    }
}
