//package com.example.go4lunch;
//
//import com.example.go4lunch.repository.MapService;
//
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class MapClient {
//
//    private static Retrofit buildRetrofit() {
//        return new Retrofit.Builder()
//                .baseUrl("https://maps.googleapis.com/maps/api/place/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//    }
//
//    public static MapService getInstance() {
//        return buildRetrofit().create(MapService.class);
//    }
//}
