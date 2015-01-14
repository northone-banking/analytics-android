/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Segment, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.segment.analytics;

import android.content.Context;
import com.segment.analytics.internal.Cartographer;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.segment.analytics.internal.Utils.NullableConcurrentHashMap;
import static com.segment.analytics.internal.Utils.createMap;
import static com.segment.analytics.internal.Utils.isNullOrEmpty;
import static com.segment.analytics.internal.Utils.toISO8601Date;
import static java.util.Collections.unmodifiableMap;

/**
 * A class representing information about a user.
 * <p/>
 * Traits can be anything you want, but some of them have semantic meaning and we treat them in
 * special ways. For example, whenever we see an email trait, we expect it to be the user's email
 * address. And we'll send this on to integrations that need an email, like Mailchimp. For that
 * reason, you should only use special traits for their intended purpose.
 * <p/>
 * Traits are persisted to disk, and will be remembered between application and system reboots.
 */
public class Traits extends ValueMap {
  private static final String ADDRESS_KEY = "address";
  private static final String ADDRESS_CITY_KEY = "city";
  private static final String ADDRESS_COUNTRY_KEY = "country";
  private static final String ADDRESS_POSTAL_CODE_KEY = "postalCode";
  private static final String ADDRESS_STATE_KEY = "state";
  private static final String ADDRESS_STREET_KEY = "street";
  private static final String AVATAR_KEY = "avatar";
  private static final String CREATED_AT_KEY = "createdAt";
  private static final String DESCRIPTION_KEY = "description";
  private static final String EMAIL_KEY = "email";
  private static final String FAX_KEY = "fax";
  private static final String ANONYMOUS_ID_KEY = "anonymousId";
  private static final String ID_KEY = "userId";
  private static final String NAME_KEY = "name";
  private static final String PHONE_KEY = "phone";
  private static final String WEBSITE_KEY = "website";
  // For Identify Calls
  private static final String AGE_KEY = "age";
  private static final String BIRTHDAY_KEY = "birthday";
  private static final String FIRST_NAME_KEY = "firstName";
  private static final String GENDER_KEY = "gender";
  private static final String LAST_NAME_KEY = "lastName";
  private static final String TITLE_KEY = "title";
  private static final String USERNAME_KEY = "username";
  // For Group calls
  private static final String EMPLOYEES_KEY = "employees";
  private static final String INDUSTRY_KEY = "industry";

  /**
   * Create a new Traits instance. Analytics client can be called on any thread, so this instance is
   * thread safe.
   */
  public static Traits create(Context context) {
    Traits traits = new Traits(new NullableConcurrentHashMap<String, Object>());
    traits.putAnonymousId(UUID.randomUUID().toString());
    return traits;
  }

  /** For deserialization from disk by {@link Traits.Cache}. */
  private Traits(Map<String, Object> delegate) {
    super(delegate);
  }

  /**
   * This instance is not thread safe. {@link Traits} are  meant to be attached to a single call and
   * discarded. If the client is keeping a reference to a {@link Traits} instance that may be
   * accessed by multiple threads, they should synchronize access to this instance.
   */
  public Traits() {
    // Public Constructor
  }

  public Traits unmodifiableCopy() {
    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(this);
    return new Traits(unmodifiableMap(map));
  }

  /**
   * Private API, users should call {@link com.segment.analytics.Analytics#identify(String)}
   * instead. Note that this is unable to enforce it, users can easily do traits.put(id, ..);
   */
  public Traits putUserId(String id) {
    return putValue(ID_KEY, id);
  }

  public String userId() {
    return getString(ID_KEY);
  }

  public Traits putAnonymousId(String id) {
    return putValue(ANONYMOUS_ID_KEY, id);
  }

  public String anonymousId() {
    return getString(ANONYMOUS_ID_KEY);
  }

  String userIdOrAnonymousId() {
    String userId = userId();
    return (isNullOrEmpty(userId)) ? anonymousId() : userId;
  }

  public Traits putAddress(String city, String country, String postalCode, String state,
      String street) {
    Map<String, String> address = createMap();
    address.put(ADDRESS_CITY_KEY, city);
    address.put(ADDRESS_COUNTRY_KEY, country);
    address.put(ADDRESS_POSTAL_CODE_KEY, postalCode);
    address.put(ADDRESS_STATE_KEY, state);
    address.put(ADDRESS_STREET_KEY, street);
    return putValue(ADDRESS_KEY, address);
  }

  public Traits putAvatar(String avatar) {
    return putValue(AVATAR_KEY, avatar);
  }

  String avatar() {
    return getString(AVATAR_KEY);
  }

  public Traits putCreatedAt(String createdAt) {
    return putValue(CREATED_AT_KEY, createdAt);
  }

  public Traits putDescription(String description) {
    return putValue(DESCRIPTION_KEY, description);
  }

  public Traits putEmail(String email) {
    return putValue(EMAIL_KEY, email);
  }

  public String email() {
    return getString(EMAIL_KEY);
  }

  public Traits putFax(String fax) {
    return putValue(FAX_KEY, fax);
  }

  public Traits putName(String name) {
    return putValue(NAME_KEY, name);
  }

  public String name() {
    String name = getString(NAME_KEY);
    if (isNullOrEmpty(name)) {
      StringBuilder stringBuilder = new StringBuilder();
      String firstName = getString(FIRST_NAME_KEY);
      boolean appendSpace = false;
      if (!isNullOrEmpty(firstName)) {
        appendSpace = true;
        stringBuilder.append(firstName);
      }

      String lastName = getString(LAST_NAME_KEY);
      if (!isNullOrEmpty(lastName)) {
        if (appendSpace) stringBuilder.append(' ');
        stringBuilder.append(lastName);
      }
      return stringBuilder.toString();
    } else {
      return name;
    }
  }

  public Traits putPhone(String phone) {
    return putValue(PHONE_KEY, phone);
  }

  public Traits putWebsite(String website) {
    return putValue(WEBSITE_KEY, website);
  }

  public Traits putAge(int age) {
    return putValue(AGE_KEY, age);
  }

  public int age() {
    return getInt(AGE_KEY, 0);
  }

  public Traits putBirthday(Date birthday) {
    return putValue(BIRTHDAY_KEY, toISO8601Date(birthday));
  }

  public Traits putFirstName(String firstName) {
    return putValue(FIRST_NAME_KEY, firstName);
  }

  public Traits putGender(String gender) {
    return putValue(GENDER_KEY, gender);
  }

  public String gender() {
    return getString(GENDER_KEY);
  }

  public Traits putLastName(String lastName) {
    return putValue(LAST_NAME_KEY, lastName);
  }

  public Traits putTitle(String title) {
    return putValue(TITLE_KEY, title);
  }

  public Traits putUsername(String username) {
    return putValue(USERNAME_KEY, username);
  }

  public String username() {
    return getString(USERNAME_KEY);
  }

  public Traits putEmployees(long employees) {
    return putValue(EMPLOYEES_KEY, employees);
  }

  public Traits putIndustry(String industry) {
    return putValue(INDUSTRY_KEY, industry);
  }

  @Override public Traits putValue(String key, Object value) {
    super.putValue(key, value);
    return this;
  }

  static class Cache extends ValueMap.Cache<Traits> {
    private static final String TRAITS_CACHE_PREFIX = "traits-";

    Cache(Context context, Cartographer cartographer, String tag) {
      super(context, cartographer, TRAITS_CACHE_PREFIX + tag, Traits.class);
    }

    @Override public Traits create(Map<String, Object> map) {
      // Analytics client can be called on any thread, so this instance should be thread safe.
      return new Traits(new NullableConcurrentHashMap<>(map));
    }
  }
}
