/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.util.notification.impl;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

/**
 * @author Angel Colina
 * @version 1.0
 */
public class StompSessionHandler extends StompSessionHandlerAdapter {
	
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
	}

}
