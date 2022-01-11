package com.intech.yayabureau.Interface;




import com.intech.yayabureau.Models.ResponseStk;
import com.intech.yayabureau.Models.Result;
import com.intech.yayabureau.Models.ResultStk;
import com.intech.yayabureau.Models.StkQuery;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/stk_register")
    Call<ResponseStk> stk_push(@Body Map<String,Object> pushStk);

    @POST("/stk_callback2")
    Call<ResultStk>  getResponse();

    @GET("/")
    Call<Result>  getResult();

    @POST("/stk/query2")
    Call<StkQuery>   stk_Query  (@Body Map<String ,String> stkQuey);


}
