package lt.LinasJu.Entities.Nodes;

import lombok.Getter;
import lombok.Setter;

import java.util.StringJoiner;

@Getter
@Setter
public class ShapePoint {
    private Float x;
    private Float y;
    private Float z;

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",");
        if (x != null) {
            joiner.add(String.valueOf(x));
        }
        if (y != null) {
            joiner.add(String.valueOf(y));
        }
        if (z != null) {
            joiner.add(String.valueOf(z));
        }
        return joiner.toString();
    }
}
