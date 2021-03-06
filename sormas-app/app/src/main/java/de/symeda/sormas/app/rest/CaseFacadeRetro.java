package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface CaseFacadeRetro {

    @GET("cases/all/{since}")
    Call<List<CaseDataDto>> pullAllSince(@Path("since") long since);

    @POST("cases/push")
    Call<Integer> pushAll(@Body List<CaseDataDto> dtos);

    @GET("cases/uuids")
    Call<List<String>> pullUuids();
}
