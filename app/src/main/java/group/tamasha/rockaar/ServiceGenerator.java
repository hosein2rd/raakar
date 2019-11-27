package group.tamasha.rockaar;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

//    private static OkHttpClient httpClient = new OkHttpClient.Builder()
//            .addInterceptor(new Interceptor())
//            .build();

    private static Retrofit retrofit;

    public static <S> S createService(Class<S> serviceClass) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://raakar.ir/")
                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient)
                .build();
        return retrofit.create(serviceClass);
    }
}
