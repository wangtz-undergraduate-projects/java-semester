package com.wudaokou.easylearn.retrofit;

import com.wudaokou.easylearn.data.EntityInfo;
import com.wudaokou.easylearn.data.KnowledgeCard;
import com.wudaokou.easylearn.data.Question;
import com.wudaokou.easylearn.data.RelatedSubject;
import com.wudaokou.easylearn.data.SearchResult;
import com.wudaokou.easylearn.retrofit.entityLink.JsonEntityLink;
import com.wudaokou.easylearn.retrofit.Answer;
import com.wudaokou.easylearn.retrofit.Answers;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EduKGService {

//    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    @GET("instanceList")
    Call<JSONArray<SearchResult>> instanceList(@Query("id") String id,
                                    @Query("course") String course,
                                    @Query("searchKey") String searchKey);

    @GET("infoByInstanceName")
    Call<JSONObject<EntityInfo>> infoByInstanceName(@Query("id") String id,
                                                    @Query("course") String course,
                                                    @Query("name") String name);

    @GET("questionListByUriName")
    Call<JSONArray<Question>> questionListByUriName(@Query("id") String id,
            @Query("uriName") String uriName);

    @FormUrlEncoded
    @POST("linkInstance")
    Call<JSONObject<JsonEntityLink>> linkInstance(@Field("id") String id,
                                                  @Field("course") String course,
                                                  @Field("context") String text);

    @FormUrlEncoded
    @POST("relatedsubject")
    Call<JSONObject<RelatedSubject>> relatedSubject(@Field("id") String id,
                                                   @Field("course") String course,
                                                   @Field("subjectName") String subjectName);

    @FormUrlEncoded
    @POST("getKnowledgeCard")
    Call<JSONObject<KnowledgeCard>> getKnowledgeCard(@Field("id") String id,
                                                    @Field("course") String course,
                                                    @Field("uri") String uri);

    @FormUrlEncoded
    @POST("user/login")
    Call<EduLoginRet> eduLogin(@Field("phone") String phone,
                               @Field("password") String password);

    @FormUrlEncoded
    @POST("inputQuestion")
    Call<JSONArray<Answer>> eduInputQuestion(@Field("id") String id,
            @Field("course") String course, @Field("inputQuestion") String inputQuestion);
}

