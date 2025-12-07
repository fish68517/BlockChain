package com.dlmgroup.collectorcoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.dlmgroup.collectorcoin.jbpm.CollectorCoinjBPMProcessClientAPI;
import com.dlmgroup.collectorcoin.models.ProjectListing;

import static com.dlmgroup.collectorcoin.WebSocketConfiguration.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RepositoryEventHandler(ProjectListing.class)
public class EventHandler {
	// private static final Logger log = LoggerFactory.getLogger(EventHandler.class);

	private final SimpMessagingTemplate websocket;
	private final EntityLinks entityLinks;

	private CollectorCoinjBPMProcessClientAPI jBPMProcessClient = new CollectorCoinjBPMProcessClientAPI();

	@Autowired
	public EventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

  // The below code did not work for me - the websocket messages are being sent from controllers instead
	// @HandleBeforeCreate
	// @HandleBeforeSave
	// private void createNewProjectListing(ProjectListing listing) {
	// 	String newListingProcessID = "";

	// 	newListingProcessID = jBPMProcessClient.startNewProjectListingProcess(listing);
	// 	listing.setProcessId(newListingProcessID);
	// }

	// @HandleAfterCreate
	// public void newListing(ProjectListing listing) {

  //   System.out.println("HEREEEEEEEE " + MESSAGE_PREFIX);

	// 	log.info("************************************* newListing ADDED (GENERATED ID): " + listing.getId()); // added
	// 	log.info("************************************* newListing ADDED (VIN NUMBER): " + listing.getVIN()); // added

	// 	this.websocket.convertAndSend(
	// 			MESSAGE_PREFIX + "/newProjectListing", getPath(listing));
	// }

	// @HandleAfterDelete
	// public void deleteListing(ProjectListing listing) {
	// 	this.websocket.convertAndSend(
	// 			MESSAGE_PREFIX + "/deleteProjectListing", getPath(listing));
	// }

	// @HandleAfterSave
	// public void updateListing(ProjectListing listing) {
	// 	this.websocket.convertAndSend(
	// 			MESSAGE_PREFIX + "/updateProjectListing", getPath(listing));
	// }

	/**
	 * Take an {@link ProjectListingOLD} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param listing
	 */
	private String getPath(ProjectListing listing) {
		return this.entityLinks.linkForItemResource(listing.getClass(),
				listing.getId()).toUri().getPath();
	}

}
