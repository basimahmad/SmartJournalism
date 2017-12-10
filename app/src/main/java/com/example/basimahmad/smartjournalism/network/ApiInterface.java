package com.example.basimahmad.smartjournalism.network;

/**
 * Created by Basim Ahmad on 12/10/2017.
 */
import java.util.List;

import com.example.basimahmad.smartjournalism.model.Message;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("inbox.json")
    Call<List<Message>> getInbox();

}