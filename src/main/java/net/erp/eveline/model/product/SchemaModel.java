package net.erp.eveline.model.product;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SchemaModel {
    private Type type;
    private Quantification quantification;
    private Hazard hazard;

    public Type getType() {
        return type;
    }

    public SchemaModel setType(final Type type) {
        this.type = type;
        return this;
    }

    public Quantification getQuantification() {
        return quantification;
    }

    public SchemaModel setQuantification(final Quantification quantification) {
        this.quantification = quantification;
        return this;
    }

    public Hazard getHazard() {
        return hazard;
    }

    public SchemaModel setHazard(final Hazard hazard) {
        this.hazard = hazard;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SchemaModel that = (SchemaModel) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .append(quantification, that.quantification)
                .append(hazard, that.hazard)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(quantification)
                .append(hazard)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("type", type)
                .append("quantification", quantification)
                .append("hazard", hazard)
                .toString();
    }
}
