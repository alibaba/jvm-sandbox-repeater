package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

/**
 * {@link Identity} 定义一次{@link Invocation}的标志；
 * <p>
 *
 * @author zhaoyb1990
 */
public class Identity implements java.io.Serializable{

    private final static String QUERY_STRING_COLLECTOR = "?";
    private final static String KEY_VALUE_SPLITTER = "&";
    private final static String KEY_VALUE_COLLECTOR = "=";
    private final static String HOST_SPLITTER = "://";
    private final static String LOCATION_SPLITTER = "/";

    /**
     * 唯一资源定位标志
     */
    private volatile String uri;
    private transient String scheme;
    private transient String location;
    private transient String endpoint;
    private transient Map<String, String> extra;

    private Identity() {}

    public Identity(String scheme, String location, String endpoint, Map<String, String> extra) {
        this.scheme = scheme;
        this.location = location;
        this.endpoint = endpoint;
        this.extra = extra;
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append(HOST_SPLITTER).append(Joiner.on("/").join(location, endpoint));
        if (extra != null && !extra.isEmpty()) {
            boolean firstKey = true;
            for (Map.Entry<String, String> entry : extra.entrySet()) {
                if (firstKey) {
                    firstKey = false;
                    sb.append(QUERY_STRING_COLLECTOR);
                } else {
                    sb.append(KEY_VALUE_SPLITTER);
                }
                sb.append(entry.getKey()).append(KEY_VALUE_COLLECTOR).append(entry.getValue());
            }
        }
        this.uri = sb.toString();
    }

    public Identity(String uri) {
        this.uri = uri;
        updateIfNecessary();

    }

    public String getUri() {
        updateIfNecessary();
        return uri;
    }

    public String getScheme() {
        updateIfNecessary();
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getLocation() {
        updateIfNecessary();
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEndpoint() {
        updateIfNecessary();
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Identity) {
            Identity identity = (Identity) obj;
            return this.uri.equals(identity.uri);
        } else {
            return false;
        }
    }

    private void update() {
        try {
            URI parsed = new URI(uri);
            scheme = parsed.getScheme();
            location = parsed.getAuthority();
            endpoint = parsed.getPath();
            String queryString = parsed.getQuery();
            if (null != queryString) {
                extra = Splitter.on(KEY_VALUE_SPLITTER)
                        .trimResults()
                        .withKeyValueSeparator(KEY_VALUE_COLLECTOR)
                        .split(queryString);
            }
        } catch (URISyntaxException e) {
            String[] schemeSplit = uri.split(HOST_SPLITTER);
            scheme = schemeSplit[0];
            if (schemeSplit.length > 1) {
                location = schemeSplit[1].split(LOCATION_SPLITTER)[0];
                endpoint = schemeSplit[1].replace(location, "");
            }
        }
    }

    private void updateIfNecessary() {
        if (scheme == null || location == null || endpoint == null) {
            update();
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{uri, scheme, location, endpoint, extra});
    }
}
