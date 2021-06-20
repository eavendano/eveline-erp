package net.erp.eveline.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ActiveInventoryModel {
    private String id;
    private Boolean enabled;
    private String lastUser;

    public String getId() {
        return id;
    }

    public ActiveInventoryModel setId(final String id) {
        this.id = id;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public ActiveInventoryModel setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public ActiveInventoryModel setLastUser(final String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ActiveInventoryModel that = (ActiveInventoryModel) o;

        return new EqualsBuilder().append(id, that.id).append(enabled, that.enabled).append(lastUser, that.lastUser).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(enabled).append(lastUser).toHashCode();
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
