package eu.transkribus.core.util;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.InvalidUserInputException;
import eu.transkribus.core.model.beans.ATrpModel;
import eu.transkribus.core.model.beans.TrpP2PaLA;

public class ModelUtil {
	private static final Logger logger = LoggerFactory.getLogger(ModelUtil.class);
	
	/**
	 * Updates all editable properties from the given map in the model m
	 */
	public static ATrpModel updateModelFromMap(ATrpModel m, Map<String, String> modelAsMap) throws InvalidUserInputException {
		// update name:
		String newName = modelAsMap.get(ATrpModel.NAME_COL);
		if (newName != null) {
			if (StringUtils.isEmpty(newName)) {
				throw new InvalidUserInputException("Name of model cannot be empty!");
			}
			m.setName(newName);
		}
		
		// update description:
		String newDesc = modelAsMap.get(ATrpModel.DESCRIPTION_COL);
		if (newDesc != null) {
			m.setDescription(newDesc);
		}
		
		return m;
	}	
	
	public static Map<String, String> parseModelJsonAsMap(String modelJson) {
		return GsonUtil.toMapWithStringValues(modelJson);
	}
	
	public static int parseModelId(Map<String, String> modelAsMap) {
		try {
			return Integer.parseInt(modelAsMap.get(ATrpModel.MODEL_ID_VARIABLE_NAME));	
		} catch (Exception e) {
			logger.warn("Could not parse model id from model object as map: "+modelAsMap.get(ATrpModel.MODEL_ID_VARIABLE_NAME));
			return -1;
		}
	}
	
	public static <T extends ATrpModel> String getType(Class<T> clazz) {
		if (clazz.equals(TrpP2PaLA.class)) {
			return TrpP2PaLA.TYPE;
		}
		else {
			throw new IllegalArgumentException("Cannot find model type for class: "+clazz);
		}
	}
	
	public static <T extends ATrpModel> String getType2(Class<T> clazz) {
		try {
			return getType(clazz);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static Class<? extends ATrpModel> getModelClass(String type) {
		if (StringUtils.isEmpty(type)) {
			throw new IllegalArgumentException("Type cannot be empty!");
		}
		
		switch (type) {
		case TrpP2PaLA.TYPE:
			return TrpP2PaLA.class;		
		default:
			throw new IllegalArgumentException("No manager found for model type: "+type);
		}
	}
	
	public static Class<? extends ATrpModel> getModelClass2(String type) {
		try {
			return getModelClass(type);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static GenericEntity createGenericListEntity(List models, String type) {
		if (StringUtils.isEmpty(type)) {
			throw new IllegalArgumentException("Type cannot be empty!");
		}
		
		switch (type) {
		case TrpP2PaLA.TYPE:
			return new GenericEntity<List<TrpP2PaLA>>(models) {};
		default:
			throw new IllegalArgumentException("Invalid type: "+type);
		}
	}
	
	public static GenericType createGenericType(String type) {
		if (StringUtils.isEmpty(type)) {
			throw new IllegalArgumentException("Type cannot be empty!");
		}
		
		switch (type) {
		case TrpP2PaLA.TYPE:
			return new GenericType<List<TrpP2PaLA>>(){};
		default:
			throw new IllegalArgumentException("Invalid type: "+type);
		}
	}


	
	
	
}
