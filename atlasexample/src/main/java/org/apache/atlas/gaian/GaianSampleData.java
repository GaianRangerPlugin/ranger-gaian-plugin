package org.apache.atlas.gaian;



import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasException;
import org.apache.atlas.model.instance.*;
import org.apache.atlas.model.typedef.AtlasEnumDef;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.type.AtlasTypeUtil;
import org.apache.atlas.utils.AuthenticationUtil;
import org.apache.commons.collections.CollectionUtils;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Create sample Gaian entities & types
 */
    public class GaianSampleData {

    // Launcher - this class will normally be run at cmd line
    public static void main(String[] args) throws Exception {
        String[] basicAuthUsernamePassword = null;


        if (args.length <1)
        {
            System.out.println("\nNo atlas server specified: ");
            System.exit(1);
        }

        // URL of the server?
        String[] urls = args[0].split(",");

        //basicAuthUsernamePassword = AuthenticationUtil.getBasicAuthenticationInput();
        basicAuthUsernamePassword = new String[]{"admin", "admin"};


        GaianSampleData sampledata;

        sampledata = new GaianSampleData(urls, basicAuthUsernamePassword);

        try {
            sampledata.createTypes();
        } catch  (Exception e) { System.out.println("Failed to create types - probably exist"); }

        try {
            sampledata.createEntities();
        } catch (Exception e) { System.out.println("Failed to create entities - probably exist"); }


        try {
            sampledata.createRelationships();
        } catch (Exception e) { System.out.println("Failed to create entities - probably exist"); }
    }


    // Constructors will create a AtlasClientV2 instance to use for entity/type manipulation
    private final AtlasClientV2 atlasClientV2;

    GaianSampleData(String[] urls, String[] basicAuthUsernamePassword) {
        atlasClientV2 = new AtlasClientV2(urls,basicAuthUsernamePassword);
    }

    GaianSampleData(String[] urls) throws AtlasException {
        atlasClientV2 = new AtlasClientV2(urls);
    }


    // Simple wrapper
    void createTypes() throws Exception {
        AtlasTypesDef atlasTypesDef = createTypeDefinitions();

        System.out.println("\nCreating gaian types: ");
        atlasClientV2.createAtlasTypeDefs(atlasTypesDef);


    }

    void createRelationships() throws Exception {
        System.out.println("\nCreating gaian relationships: ");


        // Now let's try creating some relationships.
    // V1 = hardcoded!
    //AtlasRelationship relationship = new AtlasRelationship;
    //atlasClientV2.createRelationship()
    // WIP ...
    }

    // The real work on creating the types
    // For Gaian we will have a schema (similar to database in hive - though not really used yet), a table, and columns.
    // No constraints are added for now, and containment is managed using relationships
    // Definitions are simple.. may need to be extended in future

    AtlasTypesDef createTypeDefinitions() throws Exception {

        // Gaian Schema
        AtlasEntityDef schemaType   = AtlasTypeUtil.createClassTypeDef("gaianSchema", "Gaian Schema", "1.0", null);
                AtlasTypeUtil.createUniqueRequiredAttrDef("name", "string");
                AtlasTypeUtil.createUniqueRequiredAttrDef("qualifiedName", "string");
                AtlasTypeUtil.createOptionalAttrDef("comment", "string");

        // Gaian Column
        AtlasEntityDef colType  = AtlasTypeUtil.createClassTypeDef("gaianColumn", "Gaian Column", "1.0", null);
                AtlasTypeUtil.createOptionalAttrDef("name", "string");
                AtlasTypeUtil.createOptionalAttrDef("type", "string");
                // Column position probably needs to be managed as part of the database
                //AtlasTypeUtil.createOptionalAttrDef("position", "string");
                AtlasTypeUtil.createOptionalAttrDef("comment", "string");

        // Gaian Table
        AtlasEntityDef tblType  = AtlasTypeUtil.createClassTypeDef("gaianTable", "Gaian Table", "1.0", Collections.singleton("DataSet"));
                AtlasTypeUtil.createUniqueRequiredAttrDef("name", "string");
                AtlasTypeUtil.createUniqueRequiredAttrDef("qualifiedName", "string");
                AtlasTypeUtil.createOptionalAttrDef("comment", "string");

        return AtlasTypeUtil.getTypesDef(Collections.<AtlasEnumDef>emptyList(),
                Collections.<AtlasStructDef>emptyList(),
                Arrays.asList(), // none of these
                Arrays.asList(schemaType, colType, tblType));
    }

    void createEntities() throws Exception {
        System.out.println("\nCreating sample entities: ");

        // Database entities
        createSchema("GAIAN", "GAIAN", "Gaian Database Schema");

        // Table
        createTable("VEMPLOYEE", "GAIAN.VEMPLOYEE","Employee Table");

        // Column entities
        createColumn("FISTNAME", "String", "GAIAN.VEMPLOYEE.FIRSTNAME","First Name");
        createColumn("LASTNAME", "String", "GAIAN.VEMPLOYEE.LASTNAME","Last Name");
        createColumn("BIRTH_DATE", "String", "GAIAN.VEMPLOYEE.BIRTH_DATE","Birth Date");

        // Note - no relationships created at this point
    }

    // Actually create the instance at the backend (code from QuickStartV2)
    private AtlasEntity createInstance(AtlasEntity entity, String[] traitNames) throws Exception {
        AtlasEntity ret = null;
        EntityMutationResponse  response = atlasClientV2.createEntity(new AtlasEntity.AtlasEntityWithExtInfo(entity));
        List<AtlasEntityHeader> entities = response.getEntitiesByOperation(EntityMutations.EntityOperation.CREATE);

        if (CollectionUtils.isNotEmpty(entities)) {
            AtlasEntity.AtlasEntityWithExtInfo getByGuidResponse = atlasClientV2.getEntityByGuid(entities.get(0).getGuid());
            ret = getByGuidResponse.getEntity();
            System.out.println("Created entity of type [" + ret.getTypeName() + "], guid: " + ret.getGuid());
        }

        return ret;
    }

    AtlasEntity createSchema(String name, String qualifiedName,String comment)
            throws Exception {
        AtlasEntity entity = new AtlasEntity("gaianSchema");

        entity.setAttribute("name", name);
        entity.setAttribute("qualifiedName", qualifiedName);

        entity.setAttribute("comment", comment);

        // No trait support for now
        return createInstance(entity,null);
    }


    // create a Gaian Column
    AtlasEntity createColumn(String name, String dataType, String qualifiedName,String comment) throws Exception {

        AtlasEntity entity = new AtlasEntity("gaianColumn");
        entity.setAttribute("name", name);
        entity.setAttribute("qualifiedName", qualifiedName);

        entity.setAttribute("dataType", dataType);
        entity.setAttribute("comment", comment);

        return createInstance(entity, null);
    }

    // create a Gaian Table
    AtlasEntity createTable(String name, String qualifiedName, String comment) throws Exception {
        AtlasEntity entity = new AtlasEntity("gaianTable");

        entity.setAttribute("name", name);
        entity.setAttribute("qualifiedName", qualifiedName);

        entity.setAttribute("comment", comment);

        return createInstance(entity, null);

    }



}
