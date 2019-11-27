package group.tamasha.rockaar;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface Client {

    @Multipart
    @POST("addProject")
    Call<AddProjectResponse> addProject(
            @Header("token") String token,
            @Part MultipartBody.Part file,
            @Part("amount") long amount,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("category") RequestBody category,
            @Part("deadline") RequestBody deadline
    );

    @Multipart
    @POST("sendFinalProject")
    Call<AddProjectResponse> sendFinalProject(
            @Header("token") String token,
            @Part("projectOwnerUsername") RequestBody projectOwnerUsername,
            @Part("projectIndex") RequestBody projectIndex,
            @Part("finalLink") RequestBody finalLink,
            @Part MultipartBody.Part finalFile
    );

    @Multipart
    @POST("changeProfilePhoto")
    Call<AddProjectResponse> changeProfilePhoto(
            @Header("token") String token,
            @Part MultipartBody.Part photo
    );

    @Multipart
    @POST("changeProfilePhoto")
    Call<AddProjectResponse> deleteProfilePhoto(
            @Header("token") String token,
            @Part("myImg") String photo
    );

    @Multipart
    @POST("rate")
    Call<AddProjectResponse> rate(
            @Header("token") String token,
            @Part("point") RequestBody point,
            @Part("offerer") RequestBody offerer,
            @Part("projectIndex") RequestBody projectIndex
    );

}
