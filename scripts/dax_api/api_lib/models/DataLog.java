package scripts.dax_api.api_lib.models;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;

@DoNotRename
public class DataLog {

    @DoNotRename
    private final String id;

    @DoNotRename
    private final String timeStamp;

    @DoNotRename
    private final String group;

    @DoNotRename
    private final String user;

    @DoNotRename
    private final String source;

    @DoNotRename
    private final String propertyName;

    @DoNotRename
    private final double value;

    public DataLog(final String id, final String timeStamp, final String group, final String user, final String source, final String propertyName, final double value) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.group = group;
        this.user = user;
        this.source = source;
        this.propertyName = propertyName;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getGroup() {
        return group;
    }

    public String getUser() {
        return user;
    }

    public String getSource() {
        return source;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
