package lt.LinasJu.Entities.Nodes;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Parameter implements Serializable {
    private String key;
    private String value;
}
