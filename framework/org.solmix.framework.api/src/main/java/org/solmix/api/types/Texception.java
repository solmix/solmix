/*
 * ========THE SOLMIX PROJECT=====================================
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.api.types;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-25 solmix-api
 */
public enum Texception
{
    DEFAULT(0) ,
    OBJECT_IS_NULL(1) ,
    IO_EXCEPTION(2) ,
    ILLEGAL_ARGUMENT(3) ,
    CLASS_CAST_EXCEPTION(4) ,
    NO_SUCH_ALGORITHM(5) ,
    OBJECT_TYPE_NOT_ADAPTED(6) ,
    NO_SUPPORT(7) ,
    CAN_NOT_INSTANCE(8) ,
    NO_FOUND(9) ,
    NOT_DIRECT_USE(10) ,
    ILLEGAL_ACCESS(11) ,
    INVOKE_EXCEPTION(12) ,
    REFLECTION_EXCEPTION(13) ,
    UN_SUPPORTEDEN_CODING(14) ,

    DS_DSFILE_NOT_FOUND(1001) ,
    DS_DSCONFIG_OBJECT_TYPE_ERROR(1002) ,
    DS_NO_FONUN_DATASOURCE(1003) ,
    DS_NO_OPERATION_DEFINED(1004) ,
    DS_BEAN_FILTER_EXCEPTION(1010) ,
    DS_BEAN_CONVERT_EXCEPTION(1011) ,
    DS_NO_RESPONSE_DATA(1021) ,
    DS_LOAD_NOT_LOADING(1020) ,
    DS_UPDATE_WITHOUT_PK(1021) ,
    DS_NO_SUPPORT_OPERATION_TYPE(1022) ,
    DS_DSCONFIG_ERROR(1023) ,
    DS_REQUEST_ALREADY_STARTED(1024) ,
    DS_GENERAT_SCHEMA_EXCEPTION(1025) ,
    OSGI_SERVICE_UNAVAILABLE(2001) ,
    OSGI_BULEPRINT_INI_FAILE(2002) ,
    TRANSACTION_NOT_STARTED(2100),
    TRANSACTION_MUST_END_BEFORE_SEND(2101),
    TRANSACTION_ROLLBACK_FAILTURE(2102),
    TRANSACTION_BREAKEN(2103),

    POOL_BORROW_OBJECT_FAILD(2500) ,
    POOL_INVALID_OBJECT_TYPE(2501) ,
    POOL_UNABLE_BIND_OBJECT(2502) ,
    
    PARSER_VM_FILE_EXCEPTION(2600),

    XML_JAXB_MARSHAL(3001) ,
    XML_JAXB_UNMARSHAL(3002) ,
    XML_CREATE_DOCUMENT(3003),
    V_NO_SUCH_VALIDATIONEVENT_IMP(3501) ,
    V_CONDITION_DISSATISFY(3502) ,
    V_VALIDATION_FAILED(3503) ,

    SECURITY_DENIED(4001) ,
    SQL_NO_DEFINED_DBNAME(5501) ,
    SQL_NO_DEFINED_DBTYPE(5502) ,
    SQL_DELE_WITH_NO_CONDITION(5503) ,
    SQL_BUILD_SQL_ERROR(5504) ,
    SQL_DATASOURCE_CACHE_EXCEPTION(5505) ,
    SQL_NO_CONNECTION(5506) ,
    SQL_ROLLBACK_EXCEPTION(5507) ,
    SQL_COMMIT_EXCEPTION(5508) ,
    SQL_FREE_EXCEPTION(5509) ,
    SQL_SQLEXCEPTION(5510) ,

    JPA_NO_ENTITY(6001) ,
    JPA_JPAEXCEPTION(6002) ,
    JPA_NO_EMF(6002) ,

    JS_JSON_GENERATION_ERROR(7001) ,
    JS_JSON_MAPPING_ERROR(7002) ,

    SERVLET_UPLOAD_FILE(8001) ,
    SERVLET_NULL_REQUEST(8002) ,
    SERVLET_NO_RPC_REQUEST(8003) ,
    SERVLET_QUERYPARM_DECODE(8004) ,
    SERVLET_CLIENT_MUST_RESUBMIT(8005) ,
    SERVLET_REQ_TRANSACTION_IS_NULL(8006) ,
    SERVLET_MIME_TYPE_ERROR(8007) ,
    SERVLET_REQ_ALREADY_COMMITED(8009) ,
    REQ_NO_DATASOURCE(8010) ,

    VELOCITY_EVALUATE_EXCEPTION(9001) ,
    APP_CONFIG_DENIED(9002) ,
    APP_NO_DS_OR_OPERATION_DEFIEND(9003);

    private int value;

    Texception(int value)
    {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Texception fromValue(int v) {
        for (Texception c : Texception.values()) {
            if (c.value() == v) {
                return c;
            }
        }
        throw new IllegalArgumentException(Integer.toString(v));
    }
}
