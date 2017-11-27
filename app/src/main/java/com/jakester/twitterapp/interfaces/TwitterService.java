package com.jakester.twitterapp.interfaces;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Jake on 11/15/2017.
 */

public interface TwitterService {

    @GET("statuses/home_timeline.json")
    Observable<Response> getArticles(@Query("q") String query, @Query("page") int page,
                                     @Query("begin_date") String date, @Query("sort") String sortBy,
                                     @Query("fq") String newsDesk);

}
