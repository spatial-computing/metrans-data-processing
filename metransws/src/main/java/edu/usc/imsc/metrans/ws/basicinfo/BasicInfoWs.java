package edu.usc.imsc.metrans.ws.basicinfo;

import edu.usc.imsc.metrans.database.OracleDbHelper;
import edu.usc.imsc.metrans.ws.GeneralInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/main/basicinfo")
public class BasicInfoWs {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverallBasicInfo() {
        GeneralInfo info = new GeneralInfo();

        info.setNumBusLines(140);
        info.setNumBusStops(5000);
        info.setNumDataPoints(OracleDbHelper.getEstimateCount());

        return Response.status(200).entity(info).build();
    }
}
