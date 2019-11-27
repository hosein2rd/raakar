package group.tamasha.rockaar;

public class MyBidsAdapterItems {
    public int mconfirm;
    public int mdecline;
    public String mname;
    public String mdescribtion;
    public String mdate;
    public String mbidprice;
    public String mdifprice;
    public String mbidtime;
    public String mdiftime;

    MyBidsAdapterItems(int mconfirm, int mdecline, String mname, String mdescribtion, String mdate, String mbidprice, String mdifprice, String mbidtime, String mdiftime)
    {
        this.mconfirm = mconfirm;
        this.mdecline = mdecline;
        this.mname = mname;
        this.mdescribtion = mdescribtion;
        this.mdate = mdate;
        this.mbidprice = mbidprice;
        this.mdifprice = mdifprice;
        this.mbidtime = mbidtime;
        this.mdiftime = mdiftime;
    }
}
