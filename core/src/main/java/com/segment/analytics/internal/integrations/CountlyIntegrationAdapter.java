package com.segment.analytics.internal.integrations;

import android.app.Activity;
import android.content.Context;
import com.segment.analytics.Properties;
import com.segment.analytics.internal.payload.ScreenPayload;
import com.segment.analytics.internal.payload.TrackPayload;
import com.segment.analytics.json.JsonMap;
import ly.count.android.api.Countly;

import static com.segment.analytics.internal.Utils.nullOrDefault;

/**
 * Countly is a general-purpose analytics tool for your mobile apps, with reports like traffic
 * sources, demographics, event tracking and segmentation.
 *
 * @see <a href="https://count.ly/">Countly</a>
 * @see <a href="https://segment.io/docs/integrations/countly/">Countly Integration</a>
 * @see <a href="https://github.com/Countly/countly-sdk-android">Countly Android SDK</a>
 */
public class CountlyIntegrationAdapter extends AbstractIntegrationAdapter<Countly> {

  @Override public void initialize(Context context, JsonMap settings)
      throws InvalidConfigurationException {
    Countly.sharedInstance()
        .init(context, settings.getString("serverUrl"), settings.getString("appKey"));
  }

  @Override public Countly getUnderlyingInstance() {
    return Countly.sharedInstance();
  }

  @Override public String className() {
    return "ly.count.android.api.Countly";
  }

  @Override public String key() {
    return "Countly";
  }

  @Override public void onActivityStarted(Activity activity) {
    super.onActivityStarted(activity);
    Countly.sharedInstance().onStart();
  }

  @Override public void onActivityStopped(Activity activity) {
    super.onActivityStopped(activity);
    Countly.sharedInstance().onStop();
  }

  @Override public void track(TrackPayload track) {
    super.track(track);
    event(track.event(), track.properties());
  }

  @Override public void screen(ScreenPayload screen) {
    super.screen(screen);
    event(String.format(VIEWED_EVENT_FORMAT, screen.event()), screen.properties());
  }

  private void event(String name, Properties properties) {
    Integer count = properties.getInteger("count");
    Double sum = properties.getDouble("sum");
    Countly.sharedInstance()
        .recordEvent(name, properties.toStringMap(), nullOrDefault(count, 1),
            nullOrDefault(sum, 0d));
  }
}
