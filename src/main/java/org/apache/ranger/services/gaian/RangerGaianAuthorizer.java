/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ranger.services.gaian;

import java.util.Set;
import org.apache.ranger.plugin.audit.RangerDefaultAuditHandler;
import org.apache.ranger.plugin.service.RangerBasePlugin;
import org.apache.ranger.plugin.policyengine.RangerAccessRequestImpl;
import org.apache.ranger.plugin.policyengine.RangerAccessRequest;
import org.apache.ranger.plugin.policyengine.RangerAccessResult;
import org.apache.ranger.plugin.util.RangerPerfTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.logging.Log;


public class RangerGaianAuthorizer implements GaianAuthorizer {
    private static final Logger LOG = LoggerFactory.getLogger(RangerGaianAuthorizer.class);
    private static final Log PERF_GAIANAUTH_REQUEST_LOG = RangerPerfTracer.getPerfLogger("gaianauth.request");
    private static boolean isDebugEnabled = LOG.isDebugEnabled();
    private static volatile RangerBasePlugin gaianPlugin = null;

    @Override
    public void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("==> RangerGaianPlugin.init()");
        }

        RangerBasePlugin plugin = gaianPlugin;

        if (plugin == null) {
            synchronized (RangerGaianPlugin.class) {
                plugin = gaianPlugin;

                if (plugin == null) {
                    plugin = new RangerGaianPlugin();
                    plugin.init();
                    plugin.setResultProcessor(new RangerDefaultAuditHandler());
                    gaianPlugin = plugin;

                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("<== RangerGaianPlugin.init()");
        }
    }

    @Override
    public boolean isAuthorized(QueryContext queryContext) throws GaianAuthorizationException{
        boolean isAuthorized = true;
        GaianResourceType resourceType = getGaianResourceType(queryContext.getResourceType());
        String accessType = queryContext.getActionType();
        if (!accessType.equals("SELECT")) {
            throw new GaianAuthorizationException("GaianAccessType is invalid!");
        }
        if (isDebugEnabled) {
            LOG.debug("==> isAuthorized( " + queryContext + " )");
        }
        RangerPerfTracer perf = null;

        if(RangerPerfTracer.isPerfTraceEnabled(PERF_GAIANAUTH_REQUEST_LOG)) {
            perf = RangerPerfTracer.getPerfTracer(PERF_GAIANAUTH_REQUEST_LOG, "RangerGaianAuthorizer.isAuthorized(queryContext=" + queryContext + ")");
        }

        if (resourceType == GaianResourceType.COLUMN) {

            for (String col : queryContext.getColumns()) {
                RangerGaianResource resource = new RangerGaianResource(resourceType, queryContext.getSchema(),
                        queryContext.getTableName(), col);


                RangerAccessRequest request = new RangerGaianAccessRequest(resource, accessType, queryContext.getUser(), queryContext.getUserGroups());

                RangerAccessResult result = gaianPlugin.isAccessAllowed(request);

                if (result == null || !result.getIsAllowed()) {
                    isAuthorized = false;
                }
            }
        } else if (resourceType == GaianResourceType.TABLE) {
            RangerGaianResource resource = new RangerGaianResource(resourceType, queryContext.getSchema(),
                    queryContext.getTableName());


            RangerAccessRequest request = new RangerGaianAccessRequest(resource, accessType, queryContext.getUser(), queryContext.getUserGroups());

            RangerAccessResult result = gaianPlugin.isAccessAllowed(request);

            if (result == null || !result.getIsAllowed()) {
                isAuthorized = false;
            }
        } else if (resourceType == GaianResourceType.SCHEMA) {
            RangerGaianResource resource = new RangerGaianResource(resourceType, queryContext.getSchema());


            RangerAccessRequest request = new RangerGaianAccessRequest(resource, accessType, queryContext.getUser(), queryContext.getUserGroups());

            RangerAccessResult result = gaianPlugin.isAccessAllowed(request);

            if (result == null || !result.getIsAllowed()) {
                isAuthorized = false;
            }
        } else {
            throw new GaianAuthorizationException("GaianResourceType is invalid!");
        }


        RangerPerfTracer.log(perf);

        if (isDebugEnabled) {
            LOG.debug("<== isAuthorized Returning value :: " + isAuthorized);
        }


        return isAuthorized;
    }

    public GaianResourceType getGaianResourceType(String resourceType) {
        switch (resourceType) {
            case "SCHEMA":
                return GaianResourceType.SCHEMA;
            case "TABLE":
                return GaianResourceType.TABLE;
            case "COLUMN":
                return GaianResourceType.COLUMN;
            default:
                return GaianResourceType.NONE;
        }
    }

    @Override
    public void cleanUp() {
        if (isDebugEnabled) {
            LOG.debug("==> cleanUp ");
        }
    }

}

enum GaianResourceType { NONE, SCHEMA, TABLE, COLUMN };

class RangerGaianPlugin extends RangerBasePlugin {
    RangerGaianPlugin() {
        super("gaian", "gaian");
    }
}

class RangerGaianAccessRequest extends RangerAccessRequestImpl {

    public RangerGaianAccessRequest(RangerGaianResource resource, String accessType, String user,
                                    Set<String> userGroups) {
        super(resource, accessType, user, userGroups);
    }

}
