package group.tamasha.rockaar;

/**
 * Created by HoseinDoroud on 3/19/2018.
 */

import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;


public class Interceptor implements okhttp3.Interceptor {

    private String TAG = "Interceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjVhYTkzOWE1MmE1Y2VjNDM2MDg4MDNiNSIsImp0aSI6ImJlNDkxZTNiLTcyZTAtNGQ1Zi04NTUyLWU4YTBmYTIxOWQzYyIsImlhdCI6MTUyMTQ2MTY3MCwiZXhwIjoxNTIxNDY1MjcwfQ.BFwMHGqgxuMKSTc2psShfRwQfk1U522Ch4GCgO8Q-CU";
        Log.i(TAG, "intercepting request");
        Request original = chain.request();
        Request requestBuilder = original;
        requestBuilder = original.newBuilder().addHeader("token", token).build();
        return chain.proceed(requestBuilder);
    }
}
