package de.pschild.shcxcommutingapp.api;

import de.pschild.shcxcommutingapp.api.model.AuthResult;
import de.pschild.shcxcommutingapp.api.model.CommutingResult;
import de.pschild.shcxcommutingapp.api.model.CommutingStatusResult;
import de.pschild.shcxcommutingapp.api.model.Credentials;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommuterApi {
  @POST("authenticate")
  Observable<AuthResult> login(@Body Credentials post);

  @GET("commuter/from/{startLat},{startLng}/to/{destLat},{destLng}")
  Observable<CommutingResult> updatePosition(
      @Header("Authorization") String auth,
      @Path("startLat") double startLat,
      @Path("startLng") double startLng,
      @Path("destLat") double destLat,
      @Path("destLng") double destLng
  );

  @GET("commuter/commuting-state/{state}")
  Observable<CommutingStatusResult> updateStatus(
      @Header("Authorization") String auth,
      @Path("state") String state
  );
}
