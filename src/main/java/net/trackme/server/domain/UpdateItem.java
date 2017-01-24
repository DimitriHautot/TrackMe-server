package net.trackme.server.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Dimitri (24/01/2017)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItem {
    private String name;
    private Object value;
}
