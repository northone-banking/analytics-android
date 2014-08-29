package com.segment.analytics.internal.integrations;

import android.app.Activity;
import android.content.Context;
import com.flurry.android.Constants;
import com.flurry.android.FlurryAgent;
import com.segment.analytics.AnalyticsContext;
import com.segment.analytics.Traits;
import com.segment.analytics.internal.payload.IdentifyPayload;
import com.segment.analytics.internal.payload.ScreenPayload;
import com.segment.analytics.internal.payload.TrackPayload;
import com.segment.analytics.json.JsonMap;

import static com.segment.analytics.internal.Utils.isNullOrEmpty;

/**
 * Flurry is the most popular analytics tool for mobile apps because it has a wide assortment of
 * features. It also helps you advertise to the right audiences with your apps.
 *
 * @see <a href="http://www.flurry.com/">Flurry</a>
 * @see <a href="https://segment.io/docs/integrations/flurry/">Flurry Integration</a>
 * @see <a href="http://support.flurry.com/index.php?title=Analytics/GettingStarted/Android">Flurry
 * Android SDK</a>
 */
public class FlurryIntegrationAdapter extends AbstractIntegrationAdapter<Void> {
  String apiKey;

  @Override public void initialize(Context context, JsonMap settings)
      throws InvalidConfigurationException {
    apiKey = settings.getString("apiKey");
    FlurryAgent.setContinueSessionMillis(settings.getInteger("sessionContinueSeconds"));
    FlurryAgent.setCaptureUncaughtExceptions(settings.getBoolean("captureUncaughtExceptions"));
    FlurryAgent.setUseHttps(settings.getBoolean("useHttps"));
  }

  @Override public void onActivityStarted(Activity activity) {
    super.onActivityStarted(activity);
    FlurryAgent.onStartSession(activity, apiKey);
  }

  @Override public void onActivityStopped(Activity activity) {
    super.onActivityStopped(activity);
    FlurryAgent.onEndSession(activity);
  }

  @Override public void screen(ScreenPayload screen) {
    super.screen(screen);
    // todo: verify behaviour here, iOS SDK only does pageView, not event
    FlurryAgent.onPageView();
    FlurryAgent.logEvent(screen.event(), screen.properties().toStringMap());
  }

  @Override public void track(TrackPayload track) {
    super.track(track);
    FlurryAgent.logEvent(track.event(), track.properties().toStringMap());
  }

  @Override public void identify(IdentifyPayload identify) {
    super.identify(identify);
    Traits traits = identify.traits();
    FlurryAgent.setUserId(identify.userId());
    Integer age = traits.age();
    if (age != null) {
      FlurryAgent.setAge(age);
    }
    String gender = traits.gender();
    if (!isNullOrEmpty(gender)) {
      if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("m")) {
        FlurryAgent.setGender(Constants.MALE);
      } else if (gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("f")) {
        FlurryAgent.setGender(Constants.FEMALE);
      } else {
        FlurryAgent.setGender(Constants.UNKNOWN);
      }
    }
    AnalyticsContext.Location location = identify.context().location();
    if (location != null && location.latitude() != null && location.longitude() != null) {
      FlurryAgent.setLocation(location.latitude().floatValue(), location.longitude().floatValue());
    }
  }

  @Override public Void getUnderlyingInstance() {
    return null;
  }

  @Override public String className() {
    return "com.flurry.android.FlurryAgent";
  }

  @Override public String key() {
    return "Flurry";
  }
}
