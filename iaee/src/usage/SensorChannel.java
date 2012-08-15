package usage;

import java.io.Serializable;

public class SensorChannel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1141426307999231105L;
    private final Integer nummer;
    private final UnityType unity;
    private final Double value;

    public Integer getNummer() {
        return nummer;
    }

    public UnityType getUnity() {
        return unity;
    }

    public Double getValue() {
        return value;
    }

    /**
     * @param nummer
     * @param unity
     * @param value
     */
    public SensorChannel(Integer nummer, UnityType unity, Double value) {

        this.nummer = nummer;
        this.unity = unity;
        this.value = value;
    }

}
