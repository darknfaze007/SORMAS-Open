package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.visit.VisitDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VisitFacadeRetro {

    @GET("visits/all/{since}")
    Call<List<VisitDto>> pullAllSince(@Path("since") long since);

    @POST("visits/push")
    Call<Integer> pushAll(@Body List<VisitDto> dtos);

    @GET("visits/uuids")
    Call<List<String>> pullUuids();
}
