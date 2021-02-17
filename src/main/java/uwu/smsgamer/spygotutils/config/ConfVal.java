package uwu.smsgamer.spygotutils.config;

import lombok.Getter;

/**
 * Object to store and get configuration values with little
 * to no performance (all gotten on start and reload).
 * Also dynamic so developers can add as much as they want
 * and they automatically get added to the config files.
 *
 * @author Sms_Gamer_3808 (Shoghi Simon)
 */
@Getter
public class ConfVal<T> {
    public T value;
    public T dVal;
    public final String name;
    public final String config;

    public ConfVal(String name, T defaultVal) {
        this(name, "config", defaultVal);
    }

    public ConfVal(String name, String config, T defaultVal) {
        this.name = name;
        this.config = config;
        this.dVal = defaultVal;
        ConfigManager.getInstance().setConfVal(this, defaultVal);
    }

    /*public void setValue(T val) {
        value = val;
        ConfigManager.getConfig(config).set(name, value);
        ConfigManager.needToSave = true;
    }*/

    public T getValue() {
        if (value == null) return dVal;
        else return value;
    }

    public byte getByte() {
        return ((Number) value).byteValue();
    }

    public short getShort() {
        return ((Number) value).shortValue();
    }

    public int getInt() {
        return ((Number) value).intValue();
    }

    public long getLong() {
        return ((Number) value).longValue();
    }

    public float getFloat() {
        return ((Number) value).floatValue();
    }

    public double getDouble() {
        return ((Number) value).doubleValue();
    }
}
