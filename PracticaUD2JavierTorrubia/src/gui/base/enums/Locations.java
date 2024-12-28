package gui.base.enums;

public enum Locations {
    ZARAGOZA("Zaragoza"),
    MADRID("Madrid"),
    SEVILLA("Sevilla"),
    VALENCIA("Valencia"),
    BARCELONA("Barcelona"),
    LLEIDA("Lérida"),
    GIRONA("Girona"),
    PALMA("Palma"),
    TARRAGONA("Tarragona");

    private String value;

    Locations(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
