package vpc.assembler;

import javax.annotation.Generated;

public class InvalidRegisterException extends Exception {
    private final String value;

    public InvalidRegisterException(String value) {
        this.value = value;
    }

    @Generated("by IDE")
    public String getValue() {
        return value;
    }
}
