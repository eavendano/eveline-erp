package net.erp.eveline.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ActivateProductModel {
    private String id;
    private Boolean enabled;
    private String lastUser;

    public String getId() {
        return id;
    }

    public ActivateProductModel setId(String id) {
        this.id = id;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public ActivateProductModel setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public ActivateProductModel setLastUser(String lastUser) {
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
