package com.wudaokou.easylearn.retrofit;

import com.wudaokou.easylearn.data.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendService {
    @POST("/api/history/search")
    public Call<List<BackendObject>> postHistorySearch(@Header ("Authorization") String backendToken,
                                                           @Body HistoryParam param);

    @GET("/api/history/search")
    public Call<List<BackendObject>> getHistorySearch(@Header ("Authorization") String backendToken);

    @DELETE("/api/history/search")
    public Call<BackendObject> deleteAllHistorySearch(@Header ("Authorization") String backendToken);

    @POST("/api/history/star")
    public Call<BackendObject> starEntity(@Header ("Authorization") String backendToken,
                                          @Body HistoryParam param);

    @DELETE("/api/history/{id}")
    public Call<BackendObject> cancelStarEntity(@Header ("Authorization") String backendToken,
                                                @Path("id") int id);

    @POST("/api/history/info")
    public Call<BackendObject> postClickEntity(@Header ("Authorization") String backendToken,
                                               @Body HistoryParam param);

    @GET("/api/history/info")
    public Call<List<BackendObject>> getHistoryEntity(@Header ("Authorization") String backendToken);

    @GET("/api/history/star")
    public Call<List<BackendObject>> getHistoryStar(@Header ("Authorization") String backendToken);

    @FormUrlEncoded
    @PUT("/api/question/star")
    public Call<String> onStarQuestion(@Header ("Authorization") String backendToken,
                                       @Field("starOrUnstar") boolean starOrUnstar,
                                       @Field("id") int id,
                                       @Field("qAnswer") String qAnswer,
                                       @Field("qBody") String qBody,
                                       @Field("label") String label,
                                       @Field("course") String course); //课程名称大写

    @GET("/api/question/star")
    public Call<List<Question>>getStarQuestion(@Header ("Authorization") String backendToken);


    @FormUrlEncoded
    @PUT("/api/question/count")
    public Call<String> putQuestionCount(@Header ("Authorization") String backendToken,
                                         @Field("id") int id,
                                         @Field("wrong") boolean wrong,
                                         @Field("qAnswer") String qAnswer,
                                         @Field("qBody") String qBody,
                                         @Field("label") String label,
                                         @Field("course") String course);  //课程名称大写

    @GET("/api/question/recommend")
    public Call<List<Question>> getRecommendQuestions(@Header("Authorization") String backendToken,
                                               @Query("course") String course,
                                               @Query("number") int number,
                                               @Query("openEduId") String openEduId);

    @GET("/popular_search_keys")
    public Call<List<BackendObject>> getPopularSearchKeys();

    @POST("/login")
    Call<LoginReturn> userLogin(@Body LoginParam loginParam);

    @POST("/register")
    Call<LoginReturn> userRegister(@Body LoginParam loginParam);

    @POST("/checkUsername")
    Call<CheckUsernameReturn> checkUsername(@Body LoginParam loginParam);

    @PUT("/changePassword")
    Call<String> changePassword(@Body ChangePassParam changePassParam);
}
