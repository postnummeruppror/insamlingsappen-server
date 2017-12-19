package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2017-12-12
 */
public class FindSamplesByTag implements Query<Root, Collection<LocationSample>> {

  private Map<String, Pattern> mustTags = new HashMap<>();
  private Map<String, Pattern> mustNotTags = new HashMap<>();

  public FindSamplesByTag() {
  }

  public FindSamplesByTag(String... mustKeyValues) {
    this(false, mustKeyValues);
  }

  public FindSamplesByTag(boolean includeDeprecated, String... mustKeyValues) {
    for (int i=0; i<mustKeyValues.length; i+=2) {
      mustTags.put(mustKeyValues[i], Pattern.compile(mustKeyValues[i+1]));
    }
    if (!includeDeprecated) {
      mustNotTags.put("deprecated", Pattern.compile("^true$"));
    }
  }

  @Override
  public Collection<LocationSample> query(Root root, Date date) throws Exception {
    Set<LocationSample> locationSamples = new HashSet<>();
    for (LocationSample locationSample : root.getLocationSamples().values()) {

      boolean match = true;
      if (!mustNotTags.isEmpty()) {
        for (Map.Entry<String, Pattern> requiredMustNotTag : mustNotTags.entrySet()) {
          String value = locationSample.getTag(requiredMustNotTag.getKey());
          if (value == null || requiredMustNotTag.getValue().matcher(value).matches()) {
            match = false;
            break;
          }
        }
      }

      if (match) {
        for (Map.Entry<String, Pattern> requiredTag : mustTags.entrySet()) {
          String value = locationSample.getTag(requiredTag.getKey());
          if (value == null || !requiredTag.getValue().matcher(value).matches()) {
            match = false;
            break;
          }
        }
      }

      if (match) {
        locationSamples.add(locationSample);
      }
    }

    return locationSamples;
  }

  public Map<String, Pattern> getMustTags() {
    return mustTags;
  }

  public Map<String, Pattern> getMustNotTags() {
    return mustNotTags;
  }
}
