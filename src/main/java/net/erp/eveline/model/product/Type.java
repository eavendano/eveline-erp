package net.erp.eveline.model.product;

public enum Type {
    PHISICAL, // Any tangible product that users or employees can manipulate.
    DIGITAL, // Any digital product like certificates or images distributed by digital format, like emais, orders, etc.
    SERVICES, // Non digital charge by the system, like fees, manual labor, security checks, warranties, donations, etc.
    DOWNGRADE, // Used for items being returned and allowing them to be reusable through a different workflow.
    DISCARD // Used for items being returned and absolutely discard it.
}
