package eu.transkribus.core.model.beans.customtags;

import org.apache.commons.beanutils.Converter;

import eu.transkribus.core.model.beans.pagecontent.ColourSimpleType;

final class ColourSimpleTypeConverter implements Converter {
	@Override
	public <T> T convert(Class<T> type, Object value) {
		try {
			if (value==null)
				throw new IllegalArgumentException("value is null!");
			if (value instanceof String)
				return (T) ColourSimpleType.fromValue(((String) value).toLowerCase());
			else if (value instanceof ColourSimpleType)
				return (T) value;
			else
				throw new IllegalArgumentException("unknown type of value: "+value.getClass().getSimpleName());
		} catch (IllegalArgumentException e) {
			if (value != null)
				CustomTag.logger.warn("cannot find ColourSimpleType enum for value: "+value);
			return null;
		}
	}
}