package edu.usc.imsc.metrans.ws;

import edu.usc.imsc.metrans.Config;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;


// The Java class will be hosted at the URI path "/generalinfo"
@Path("/main")
public class MetransMainWs {
    @GET
    @Path("shape/all")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile() {
        File file = new File(Config.dataShapeFile);
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
                .build();
    }
}
