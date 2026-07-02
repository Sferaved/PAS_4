package com.taxi_pas_4.utils.bugreport.mantis;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MantisApiService {

    @POST("issues")
    Call<MantisIssueModels.CreateIssueResponse> createIssue(
            @Body MantisIssueModels.CreateIssueRequest request
    );
}
