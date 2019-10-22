package com.sakib.uconnect.interfaces;


import com.sakib.uconnect.notification.MyResponse;
import com.sakib.uconnect.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAohMI8AY:APA91bE5jx7b7ezUxYT0hEYY8xEXB26v7CTwxWigKYJxOMqWukkRx4qztE_J_kMQ1_J6gFy0GlkVuH18vO9oNqF2MQpNMlQp-dFJ0Kv8IXMS8Pd0p369ct7D3dmpPlIw42LT5QoSpB8X"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}


