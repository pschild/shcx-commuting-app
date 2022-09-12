package de.pschild.shcxcommutingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.GsonBuilder;
import dagger.hilt.android.AndroidEntryPoint;
import de.pschild.shcxcommutingapp.api.CommuterApi;
import de.pschild.shcxcommutingapp.api.model.AuthResult;
import de.pschild.shcxcommutingapp.api.model.CommutingStatusResult;
import de.pschild.shcxcommutingapp.api.model.Credentials;
import de.pschild.shcxcommutingapp.util.Logger;
import de.pschild.shcxcommutingapp.util.operators.RetryWithDelay;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;


enum CommutingState {
  START("START"),
  END("END"),
  CANCELLED("CANCELLED");

  public final String label;

  private CommutingState(String label) {
    this.label = label;
  }
}

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  @Inject
  CommuterApi api;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Scheduler.schedule(this);

    this.retrieveValidToken()
        .flatMap(authResult -> updateStatus(authResult.token, CommutingState.START))
        .subscribe();
  }

  // <SharedPreferences>
  private void saveAuth(AuthResult authResult) {
    Logger.log(getApplicationContext(), "Saving auth to local prefs");
    final SharedPreferences prefs = getApplicationContext().getSharedPreferences("secrets",
        Context.MODE_PRIVATE);
    final SharedPreferences.Editor edit = prefs.edit();
    final String json = new GsonBuilder().create().toJson(authResult);
    edit.putString("auth", json);
    edit.apply();
  }

  private AuthResult loadAuth() {
    Logger.log(getApplicationContext(), "Loading auth from local prefs");
    SharedPreferences prefs = getApplicationContext().getSharedPreferences("secrets",
        Context.MODE_PRIVATE);
    final String json = prefs.getString("auth", null);
    return new GsonBuilder().create().fromJson(json, AuthResult.class);
  }

  private void removeAuth() {
    Logger.log(getApplicationContext(), "Removing auth from local prefs");
    final SharedPreferences prefs = getApplicationContext().getSharedPreferences("secrets",
        Context.MODE_PRIVATE);
    final SharedPreferences.Editor edit = prefs.edit();
    edit.remove("auth");
    edit.apply();
  }

  @Nullable
  private AuthResult validateAuth() {
    final AuthResult authResult = this.loadAuth();
    if (authResult == null) {
      return null;
    }
    if (new DateTime(authResult.expiresAt).isBeforeNow()) {
      this.removeAuth();
      return null;
    }
    return authResult;
  }
  // </SharedPreferences>

  private Observable<AuthResult> retrieveValidToken() {
    final AuthResult localAuth = this.validateAuth();
    if (localAuth != null) {
      Logger.log(getApplicationContext(), "Local auth is valid until " + localAuth.expiresAt);
      return Observable.just(localAuth);
    }
    Logger.log(getApplicationContext(), "Local auth is invalid");
    return this.login()
        .doOnNext(this::saveAuth);
  }

  private Observable<AuthResult> login() {
    Logger.log(this, "Request: login");
    return this.api
        .login(new Credentials(BuildConfig.user, BuildConfig.password))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .doOnNext(res -> Logger.log(getApplicationContext(),
            "Success. token: " + res.token + ", expiresAt: " + res.expiresAt))
        .doOnError(res -> Logger.log(getApplicationContext(), "Error: " + res.getMessage()))
        .retryWhen(new RetryWithDelay(3, 3000));
  }

  private Observable<CommutingStatusResult> updateStatus(String authToken, CommutingState state) {
    Logger.log(this, "Request: updateStatus");
    return this.api
        .updateStatus("Bearer " + authToken, state.label)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .doOnNext(res -> Logger.log(getApplicationContext(), "Success"))
        .doOnError(res -> Logger.log(getApplicationContext(), "Error: " + res.getMessage()))
        .retryWhen(new RetryWithDelay(3, 3000));
  }
}
