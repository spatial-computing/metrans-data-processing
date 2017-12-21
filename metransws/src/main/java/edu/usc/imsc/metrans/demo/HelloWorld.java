package edu.usc.imsc.metrans.demo;

import edu.usc.imsc.metrans.database.OracleDbHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

// The Java class will be hosted at the URI path "/helloworld"
@Path("/helloworld")
public class HelloWorld {
    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
//    @Produces("text/plain")
    @Produces(MediaType.TEXT_PLAIN)
    public String getClichedMessage() {
        // Return some cliched textual content
        return "Hello World";
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUserById(@PathParam("id") String id, @HeaderParam("name") String name) {
        int count = OracleDbHelper.getEstimateCount();

        return Response.status(200).entity("getUserById is called-  id : " + id + ", name: " + name + ", estimates count=" + count).build();

    }

    @POST
    @Path("postid")
    @Produces(MediaType.TEXT_PLAIN)
    public Response postUserById(@HeaderParam("id") String id) {

        return Response.status(200).entity("postUserById is called, id : " + id).build();

    }

//    @POST
//    @Path("/create")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response create(@FormParam("userid") String userid,
//                       @FormParam("pass") String pass) {
//
//    }

    @GET
    @Path("{year}/{month}/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserHistory(
            @PathParam("year") int year,
            @PathParam("month") int month,
            @PathParam("day") int day) {

        List<UserHistory> histories = new ArrayList<UserHistory>();
        for (int i = 0; i < 10; i++) {
            UserHistory hist = new UserHistory(year+i, month + i, day + i);
            histories.add(hist);
        }


        return Response.status(200).entity(histories).build();

//        String date = year + "/" + month + "/" + day;
//
//        return Response.status(200)
//                .entity("getUserHistory is called, year/month/day : " + date)
//                .build();

    }
}
