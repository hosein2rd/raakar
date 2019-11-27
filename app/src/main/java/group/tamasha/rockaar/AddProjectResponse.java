package group.tamasha.rockaar;


import com.google.gson.annotations.SerializedName;

public class AddProjectResponse {
    @SerializedName("success")
    private boolean success;


    public boolean isSuccess() {
        return success;
    }

    public String err;
    public String getError(){
        return err;
    }
}
