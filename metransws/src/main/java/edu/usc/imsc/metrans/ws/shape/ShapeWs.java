package edu.usc.imsc.metrans.ws.shape;

import edu.usc.imsc.metrans.Config;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;


// The Java class will be hosted at the URI path "/generalinfo"
@Path("/main")
public class ShapeWs {
    @GET
    @Path("shape/all")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getShapeFile() {
        File file = new File(Config.dataShapeFile);
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
                .build();
    }

    @GET
    @Path("route/{routeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShapesOfRoutes(@PathParam("routeId") int routeId) {

        return Response.status(200).build();
    }
}
