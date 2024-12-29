package gui.base.enums;

public enum Types {
    TALLER("Taller"),
    CONFERENCIA("Conferencia"),
    EXPOSICION("Exposición"),
    FERIA("Feria"),
    CHARLA("Charla");

    private String value;

    Types(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
