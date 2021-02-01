package net.erp.eveline.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ActiveProviderModel {

    private String id;
    private Boolean enabled;
    private String lastUser;

    public String getId() {
        return id;
    }

    public ActiveProviderModel setId(String id) {
        this.id = id;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public ActiveProviderModel setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public ActiveProviderModel setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("id", id)
                .append("enabled", enabled)
                .append("lastUser", lastUser)
                .toString();
    }
}
