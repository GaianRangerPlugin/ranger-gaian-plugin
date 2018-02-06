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


import org.apache.ranger.plugin.policyengine.RangerAccessResourceImpl;



public class RangerGaianResource extends RangerAccessResourceImpl {
    public static final String KEY_SCHEMA = "schema";
    public static final String KEY_TABLE    = "table";
    public static final String KEY_COLUMN   = "column";

    private GaianResourceType resourceType = null;

    public RangerGaianResource(GaianResourceType resourceType, String schema) {
        this(resourceType, schema, null, null);
    }

    public RangerGaianResource(GaianResourceType resourceType, String schema, String table) {
        this(resourceType, schema, table, null);
    }

    public RangerGaianResource(GaianResourceType resourceType, String schema, String table, String column) {
        this.resourceType = resourceType;

        switch(resourceType) {
            case SCHEMA:
                if (schema == null) {
                    schema = "*";
                }
                setValue(KEY_SCHEMA, schema);
                break;

            case TABLE:
                setValue(KEY_SCHEMA, schema);
                setValue(KEY_TABLE, table);
                break;

            case COLUMN:
                setValue(KEY_SCHEMA, schema);
                setValue(KEY_TABLE, table);
                setValue(KEY_COLUMN, column);
                break;

            default:
                break;
        }
    }

    public GaianResourceType getResourceType() {
        return resourceType;
    }

    public String getSchema() {
        return getValue(KEY_SCHEMA);
    }

    public String getTable() {
        return getValue(KEY_TABLE);
    }

    public String getColumn() {
        return getValue(KEY_COLUMN);
    }

}

