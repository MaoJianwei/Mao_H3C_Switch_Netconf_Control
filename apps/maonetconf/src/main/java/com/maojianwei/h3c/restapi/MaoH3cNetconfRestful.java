package com.maojianwei.h3c.restapi;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maojianwei.h3c.api.MaoH3cNetconfService;
import com.maojianwei.h3c.api.MaoH3cTrustMode;
import org.onosproject.rest.AbstractWebResource;
import org.slf4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;

import static com.maojianwei.h3c.api.MaoH3cPortDirection.in;
import static com.maojianwei.h3c.api.MaoH3cPortDirection.out;
import static com.maojianwei.h3c.api.MaoH3cTrustMode.DSCP;
import static com.maojianwei.h3c.api.MaoH3cTrustMode.Dot1p;
import static com.maojianwei.h3c.api.MaoH3cTrustMode.Untrust;
import static com.maojianwei.h3c.ctl.MaoH3cMessageCodec.COMMAND_SUCCESS;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.onlab.util.Tools.readTreeFromStream;
import static org.slf4j.LoggerFactory.getLogger;

@Path("maoh3c")
public class MaoH3cNetconfRestful extends AbstractWebResource {

    private final Logger log = getLogger(getClass());

    // HTTP response code
    private static final int SUCCESS = Response.Status.OK.getStatusCode();
    private static final int INVALID_INPUT_PARAM = BAD_REQUEST.getStatusCode();
    private static final int INVALID_SERVICE_UNAVAILABLE = SERVICE_UNAVAILABLE.getStatusCode();
    private static final int COMMAND_FAIL = INTERNAL_SERVER_ERROR.getStatusCode();


    private static final String LR_INTERFACE_INDEX = "ifIndex";
    private static final String LR_INTERFACE_DIRECTION = "direction";
    private static final String LR_INTERFACE_RATE = "CIR";
    private static final String LR_INTERFACE_BURST = "CBS";

    /**
     * Update limit of port rate.
     *
     * @param stream JSON of port-limit-rate config
     * @return JSON of err-code and err-msg.
     * @onos.rsModel PortLimitRate
     */
    @POST
    @Path("port-limit-rate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPortLimitRate(InputStream stream) {

        ObjectNode result = new ObjectMapper().createObjectNode();

        ObjectNode jsonTree = readStream(stream, result);
        if (jsonTree == null){
            return Response.status(INTERNAL_SERVER_ERROR).entity(result.toString()).build();
        }


        JsonNode ifIndexNode = jsonTree.get(LR_INTERFACE_INDEX);
        JsonNode directionNode = jsonTree.get(LR_INTERFACE_DIRECTION);
        JsonNode rateNode = jsonTree.get(LR_INTERFACE_RATE);
        JsonNode burstNode = jsonTree.get(LR_INTERFACE_BURST);

        if (ifIndexNode == null || directionNode == null || rateNode == null || burstNode == null) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", LR_INTERFACE_INDEX + ", " + LR_INTERFACE_DIRECTION + ", " + LR_INTERFACE_RATE + ", " + LR_INTERFACE_BURST);
            return Response.status(BAD_REQUEST).entity(result.toString()).build();
        }


        int ifIndex = ifIndexNode.asInt();
        String direction = directionNode.asText();
        int rate = rateNode.asInt();
        int burst = burstNode.asInt();


        if (!(ifIndex > 0 && ifIndex < 53)) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", LR_INTERFACE_INDEX + " range 1~52");
            return Response.status(BAD_REQUEST).entity(result.toString()).build();
        }
        if (!(direction.equals(in.name()) || direction.equals(out.name()))) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", LR_INTERFACE_DIRECTION + " is in/out");
            return Response.status(BAD_REQUEST).entity(result.toString()).build();

        }
        if (!(rate >= 8 && rate <= 1048576 && rate % 8 == 0)) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", LR_INTERFACE_RATE + " range 8-1048576, times 8");
            return Response.status(BAD_REQUEST).entity(result.toString()).build();

        }
        if (!(burst >= 512 && burst <= 134217728 && burst % 512 == 0)) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", LR_INTERFACE_BURST + " range 512-134217728, times 512");
            return Response.status(BAD_REQUEST).entity(result.toString()).build();
        }


        MaoH3cNetconfService h3cService = get(MaoH3cNetconfService.class);
        if (h3cService == null) {
            result.put("code", INVALID_SERVICE_UNAVAILABLE).put("msg", "MaoH3cNetconfService not found");
            return Response.status(SERVICE_UNAVAILABLE).entity(result.toString()).build();
        }


        String error = h3cService.setPortLimitRate(ifIndex, direction.equals(in.name()) ? in : out, rate, burst);

        return checkSetReply(error, result) ?
                ok(result.toString()).build() :
                Response.status(INTERNAL_SERVER_ERROR).entity(result.toString()).build();
    }


    private static final String PRIMAP_INTERFACE_INDEX = "ifIndex";
    private static final String PRIMAP_PRIORITY = "priority";
    private static final String PRIMAP_TRUST_MODE = "trustMode";

    /**
     * Update priority of input port.
     *
     * @param stream JSON of port-priority config
     * @return JSON of err-code and err-msg.
     * @onos.rsModel PortPriority
     */
    @POST
    @Path("port-priority")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPortPriority(InputStream stream) {

        ObjectNode result = new ObjectMapper().createObjectNode();

        ObjectNode jsonTree = readStream(stream, result);
        if (jsonTree == null){
            return Response.status(INTERNAL_SERVER_ERROR).entity(result.toString()).build();
        }



        JsonNode ifIndexNode = jsonTree.get(PRIMAP_INTERFACE_INDEX);
        JsonNode priorityNode = jsonTree.get(PRIMAP_PRIORITY);
        JsonNode trustModeNode = jsonTree.get(PRIMAP_TRUST_MODE);

        if (ifIndexNode == null || priorityNode == null || trustModeNode == null) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", PRIMAP_INTERFACE_INDEX + ", " + PRIMAP_PRIORITY + ", " + PRIMAP_TRUST_MODE);
            return Response.status(BAD_REQUEST).entity(result.toString()).build();
        }


        int ifIndex = ifIndexNode.asInt();
        int priority = priorityNode.asInt();
        String trustMode = trustModeNode.asText();


        if (!(ifIndex > 0 && ifIndex < 53)) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", PRIMAP_INTERFACE_INDEX + " range 1~52");
            return Response.status(BAD_REQUEST).entity(result.toString()).build();
        }
        if (!(priority >= 0 && priority <= 7)) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", PRIMAP_PRIORITY + " range 0-7");
            return Response.status(BAD_REQUEST).entity(result.toString()).build();
        }
        if (!(trustMode.equals(Dot1p.name()) || trustMode.equals(DSCP.name()))) {
            result.put("code", INVALID_INPUT_PARAM).put("msg", PRIMAP_TRUST_MODE + " is Untrust/Dot1p/DSCP, Untrust not support now");
            return Response.status(BAD_REQUEST).entity(result.toString()).build();
        }


        MaoH3cNetconfService h3cService = get(MaoH3cNetconfService.class);
        if (h3cService == null) {
            result.put("code", INVALID_SERVICE_UNAVAILABLE).put("msg", "MaoH3cNetconfService not found");
            return Response.status(SERVICE_UNAVAILABLE).entity(result.toString()).build();
        }

        MaoH3cTrustMode mode = trustMode.equals(Untrust.name()) ? Untrust : (trustMode.equals(Dot1p.name()) ? Dot1p : DSCP);
        String error = h3cService.setPortPriority(ifIndex, mode, priority);

        return checkSetReply(error, result) ?
                ok(result.toString()).build() :
                Response.status(INTERNAL_SERVER_ERROR).entity(result.toString()).build();
    }



    private ObjectNode readStream(InputStream stream, ObjectNode result) {
        try {
            return readTreeFromStream(mapper(), stream);
        } catch (Exception e) {
            log.warn("Mao can not readTreeFromStream, {}, {}, {}", e.getClass().getCanonicalName(), e.getMessage(), e.getCause());
            result.put("code", COMMAND_FAIL).put("msg", "Mao can not readTreeFromStream. request cannot be parsed");
            return null;
        }
    }

    private boolean checkSetReply(String error, ObjectNode result) {
        if (error.equals(COMMAND_SUCCESS)) {
            result.put("code", SUCCESS).put("msg", COMMAND_SUCCESS);
            return true;
        } else {
            result.put("code", COMMAND_FAIL).put("msg", error);
            return false;
        }
    }


    //    /**
//     * Get alarms. Returns a list of alarms
//     *
//     * @param includeCleared (optional) include recently cleared alarms in response
//     * @param devId          (optional) include only for specified device
//     * @return JSON encoded set of alarms
//     */
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAlarms(@DefaultValue("false") @QueryParam("includeCleared") boolean includeCleared,
//                              @DefaultValue("") @QueryParam("devId") String devId
//    ) {
//
//        log.debug("Requesting all alarms, includeCleared={}", includeCleared);
//        AlarmService service = get(AlarmService.class);
//
//        Iterable<Alarm> alarms;
//        if (StringUtils.isBlank(devId)) {
//            alarms = includeCleared
//                    ? service.getAlarms()
//                    : service.getActiveAlarms();
//        } else {
//            alarms = service.getAlarms(DeviceId.deviceId(devId));
//        }
//        ObjectNode result = new ObjectMapper().createObjectNode();
//        result.set("alarms", new AlarmCodec().encode(alarms, this));
//        return ok(result.toString()).build();
//
//    }
}
