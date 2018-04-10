/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package eu.transkribus.core.util;

import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * This class is based on logback-classic's (version 1.2.3) SiftingAppender.<br>
 * In Transkribus worker modules each job has its own logfile in addition to the log file written via a RollingFileAppender by the worker module itself.<br>
 * The standard SiftingAppender will create an additional appender catching all LoggingEvents where no jobId is set via MDC and the resulting log file is huge and useless.<br>
 * <br>
 * This implementation checks if a particular LoggingEvent falls back to the defaultValue and discards it if that is the case, i.e. the event is ignored.<br>
 * <b>WARNING: never configure a logger with this as sole appender-ref as discarded logging events will not be handled then. This is meant to be used in addition only!<b>
 * 
 * @author philip
 *
 */
public class DiscriminatingSiftingAppender extends SiftingAppender {

	@Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }
        String discriminatingValue = super.getDiscriminator().getDiscriminatingValue(event);
        
        //MODIFICATION START
        if(super.getDiscriminator() instanceof MDCBasedDiscriminator) {
        	//get the defaultValue of the Discriminator as configured
        	final String defaultValue = ((MDCBasedDiscriminator)super.getDiscriminator()).getDefaultValue();
        	//check if the the discriminator has assigned its defaultValue to the event
        	if(discriminatingValue.equals(defaultValue)) {
        		//You shall not pass!
        		return;
        	}
        }
        //MODIFICATION END
        
        long timestamp = getTimestamp(event);

        Appender<ILoggingEvent> appender = appenderTracker.getOrCreate(discriminatingValue, timestamp);
        // marks the appender for removal as specified by the user
        if (eventMarksEndOfLife(event)) {
            appenderTracker.endOfLife(discriminatingValue);
        }
        appenderTracker.removeStaleComponents(timestamp);
        appender.doAppend(event);
	}

}