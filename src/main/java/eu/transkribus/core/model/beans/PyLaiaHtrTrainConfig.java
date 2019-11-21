package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PyLaiaHtrTrainConfig extends HtrTrainConfig implements Serializable {
	private static final long serialVersionUID = -7105887429420356434L;
	
	public final static int DEFAULT_NUM_EPOCHS = 200;
	
	@Schema(description = "the number of epochs. A positive natural number.", required=true)
	protected Integer numEpochs = DEFAULT_NUM_EPOCHS;
	
	@Schema(description = "Optional. Can be used to specify an existing HTR to be used as starting point for the training. Provider string must match the one given.", required=false)
	protected Integer baseModelId;
	
	public PyLaiaHtrTrainConfig() {
		super();
	}

	public Integer getNumEpochs() {
		return numEpochs;
	}

	public void setNumEpochs(Integer numEpochs) {
		this.numEpochs = numEpochs;
	}

	public Integer getBaseModelId() {
		return baseModelId;
	}

	public void setBaseModelId(Integer baseModelId) {
		this.baseModelId = baseModelId;
	}
	
	
	
}
