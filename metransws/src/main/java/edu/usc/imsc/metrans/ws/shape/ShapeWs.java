package edu.usc.imsc.metrans.ws.shape;

import edu.usc.imsc.metrans.Config;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsStoreProvider;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Trip;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;


@Path("/main/shape")
public class ShapeWs {
    @GET
    @Path("all")
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
        ArrayList<ArrayList<ShapePointInfo>> infos = new ArrayList<>();
        TreeSet<String> shapeIds = new TreeSet<>();

        try {
            GtfsStore gtfsStore = GtfsStoreProvider.getGtfsStore();
            Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(routeId));
            if (route != null) {
                ArrayList<Trip> trips = gtfsStore.getRouteTrips().get(route.getId().getId());
                if (trips != null) {
                    for (Trip trip : trips) {
                        shapeIds.add(trip.getShapeId().getId());
                    }
                } else {
                    System.err.println("Can NOT find trips for routeId=" + routeId);
                }

            } else {
                System.err.println("Can NOT find route for routeId=" + routeId);
            }

            for (String shapeId : shapeIds) {
                ArrayList<ShapePointInfo> shapePointInfos = new ArrayList<>();

                ArrayList<ShapePoint> shapePoints = gtfsStore.getShapeShapePoints().get(shapeId);
                if (shapePoints != null) {
                    for (ShapePoint shapePoint : shapePoints) {
                        ShapePointInfo shapePointInfo = new ShapePointInfo();

                        shapePointInfo.setShapeId(shapeId);
                        shapePointInfo.setShapePointLat(shapePoint.getLat());
                        shapePointInfo.setShapePointLon(shapePoint.getLon());
                        shapePointInfo.setShapePointSequence(shapePoint.getSequence());

                        shapePointInfos.add(shapePointInfo);
                    }

                    shapePointInfos.sort(new Comparator<ShapePointInfo>() {
                        @Override
                        public int compare(ShapePointInfo o1, ShapePointInfo o2) {
                            return o1.getShapePointSequence() - o2.getShapePointSequence();
                        }
                    });

                    infos.add(shapePointInfos);
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding route for routeId=" + routeId + ":" + e.getMessage());
            e.printStackTrace();
        }

        return Response.status(200).entity(infos).build();
    }
}
