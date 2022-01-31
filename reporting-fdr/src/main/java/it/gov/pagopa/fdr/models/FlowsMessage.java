package it.gov.pagopa.fdr.models;

import it.gov.pagopa.fdr.servicewsdl.TipoIdRendicontazione;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowsMessage {

    private String idPA;
    private TipoIdRendicontazione[] flows;
}
