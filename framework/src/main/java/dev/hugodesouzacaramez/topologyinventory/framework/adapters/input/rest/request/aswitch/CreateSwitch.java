package dev.hugodesouzacaramez.topologyinventory.framework.adapters.input.rest.request.aswitch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Location;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Model;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.SwitchType;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Vendor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CreateSwitch {

    @JsonProperty
    private Vendor vendor;

    @JsonProperty
    private Model model;

    @JsonProperty
    private String ip;

    @JsonProperty
    private Location location;

    @JsonProperty
    private SwitchType switchType;
}
