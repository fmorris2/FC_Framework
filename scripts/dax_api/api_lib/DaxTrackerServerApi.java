package scripts.dax_api.api_lib;

import java.io.IOException;
import java.net.HttpURLConnection;

import scripts.dax_api.api_lib.models.DataLog;
import scripts.dax_api.api_lib.models.DataLogRequest;
import scripts.dax_api.api_lib.models.DaxCredentialsProvider;
import scripts.dax_api.api_lib.models.ListSearch;
import scripts.dax_api.api_lib.models.Period;
import scripts.dax_api.api_lib.models.PropertyStats;
import scripts.dax_api.api_lib.models.ServerResponse;
import scripts.dax_api.api_lib.models.SourceHighScore;
import scripts.dax_api.api_lib.models.UserHighScore;
import scripts.dax_api.api_lib.utils.IOHelper;
import scripts.dax_api.walker_engine.Loggable;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class DaxTrackerServerApi implements Loggable {

    private static DaxTrackerServerApi webTrackerServerApi;

    public static DaxTrackerServerApi getInstance() {
        return webTrackerServerApi != null ? webTrackerServerApi : (webTrackerServerApi = new DaxTrackerServerApi());
    }

    private static final String TRACKER_ENDPOINT = "https://api.dax.cloud";


    private DaxCredentialsProvider daxCredentialsProvider;

    private DaxTrackerServerApi() {

    }

    public void setDaxCredentialsProvider(final DaxCredentialsProvider daxCredentialsProvider) {
        this.daxCredentialsProvider = daxCredentialsProvider;
    }

    public ListSearch sourcesOnline(final String propertyName, final String user, final Period period) {
        ServerResponse serverResponse;
        final Escaper escaper = UrlEscapers.urlFormParameterEscaper();
        try {
            serverResponse = IOHelper.get(
                    TRACKER_ENDPOINT + "/tracker/sources/online?propertyName=" + escaper.escape(propertyName)
                            + "&user=" + escaper.escape(user)
                            + (period != null ? "&period=" + period : ""),
                    daxCredentialsProvider
            );
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }

        if (serverResponse.getCode() != HttpURLConnection.HTTP_OK) {
            log("ERROR: " + new JsonParser().parse(serverResponse.getContents()).getAsJsonObject().get("message").getAsString());
            return null;
        }

        return new Gson().fromJson(serverResponse.getContents(), ListSearch.class);
    }

    public ListSearch usersOnline(final String propertyName, final Period period) {
        ServerResponse serverResponse;
        final Escaper escaper = UrlEscapers.urlFormParameterEscaper();
        try {
            serverResponse = IOHelper.get(
                    TRACKER_ENDPOINT + "/tracker/users/online?"
                            + (propertyName != null ? "&propertyName=" + escaper.escape(propertyName) : "")
                            + (period != null ? "&period=" + period : ""),
                    daxCredentialsProvider
            );
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }

        if (serverResponse.getCode() != HttpURLConnection.HTTP_OK) {
            log("ERROR: " + new JsonParser().parse(serverResponse.getContents()).getAsJsonObject().get("message").getAsString());
            return null;
        }

        return new Gson().fromJson(serverResponse.getContents(), ListSearch.class);
    }

    public SourceHighScore topSources(final String user, final String propertyName, final Period period) {
        ServerResponse serverResponse;
        final Escaper escaper = UrlEscapers.urlFormParameterEscaper();
        try {
            serverResponse = IOHelper.get(
                    TRACKER_ENDPOINT + "/tracker/sources/top?propertyName=" + escaper.escape(propertyName)
                            + "&user=" + user
                            + (period != null ? "&period=" + period : ""),
                    daxCredentialsProvider
            );
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }

        if (serverResponse.getCode() != HttpURLConnection.HTTP_OK) {
            log("ERROR: " + new JsonParser().parse(serverResponse.getContents()).getAsJsonObject().get("message").getAsString());
            return null;
        }

        return new Gson().fromJson(serverResponse.getContents(), SourceHighScore.class);
    }

    public UserHighScore topUsers(final String propertyName, final Period period) {
        ServerResponse serverResponse;
        final Escaper escaper = UrlEscapers.urlFormParameterEscaper();
        try {
            serverResponse = IOHelper.get(
                    TRACKER_ENDPOINT + "/tracker/users/top?propertyName=" + escaper.escape(propertyName)
                            + (period != null ? "&period=" + period : ""),
                    daxCredentialsProvider
            );
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }

        if (serverResponse.getCode() != HttpURLConnection.HTTP_OK) {
            log("ERROR: " + new JsonParser().parse(serverResponse.getContents()).getAsJsonObject().get("message").getAsString());
            return null;
        }

        return new Gson().fromJson(serverResponse.getContents(), UserHighScore.class);
    }

    public PropertyStats getStats(final String user, final String source, final String propertyName) {
        ServerResponse serverResponse;
        final Escaper escaper = UrlEscapers.urlFormParameterEscaper();
        try {
            serverResponse = IOHelper.get(
                    TRACKER_ENDPOINT + "/tracker/data?user=" + escaper.escape(user)
                            + "&propertyName=" + escaper.escape(propertyName)
                            + (source != null ? "&source=" + escaper.escape(source) : ""),
                    daxCredentialsProvider
            );
        } catch (final IOException e) {
            return null;
        }

        if (serverResponse.getCode() != HttpURLConnection.HTTP_OK) {
            log("ERROR: " + new JsonParser().parse(serverResponse.getContents()).getAsJsonObject().get("message").getAsString());
            return null;
        }

        return new Gson().fromJson(serverResponse.getContents(), PropertyStats.class);
    }

    public DataLog log(final String user, final String source, final String propertyName, final double value) {
        ServerResponse serverResponse;
        try {
            serverResponse = IOHelper.post(
                    new JsonParser().parse(new Gson().toJson(new DataLogRequest(user, source, propertyName, value))).getAsJsonObject(),
                    TRACKER_ENDPOINT + "/tracker/data",
                    daxCredentialsProvider
            );
        } catch (final IOException e) {
            return null;
        }

        if (serverResponse.getCode() != HttpURLConnection.HTTP_OK) {
            log("ERROR: " + new JsonParser().parse(serverResponse.getContents()).getAsJsonObject().get("message").getAsString());
            return null;
        }

        return new Gson().fromJson(serverResponse.getContents(), DataLog.class);
    }


    @Override
    public String getName() {
        return "daxTracker";
    }

}
