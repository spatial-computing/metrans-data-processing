package edu.usc.imsc.metrans.ws;


import edu.usc.imsc.metrans.database.OracleDbHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/generalinfo"
@Path("/generalinfo")
public class GeneralInfoWS {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGeneralInfo() {
        GeneralInfo info = new GeneralInfo();

        info.setNumBusLines(140);
        info.setNumBusStops(5000);
        info.setNumDataPoints(OracleDbHelper.getEstimateCount());

        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("numbusline")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getNumBusLines() {
        int count = OracleDbHelper.getEstimateCount();

        return Response.status(200).entity(count).build();

    }
}

