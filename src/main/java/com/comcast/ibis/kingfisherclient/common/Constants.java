package com.comcast.ibis.kingfisherclient.common;

/**
 * The type Constants.
 */
public class Constants {
    /**
     * The constant AUTHORITY_SERVICE.
     */
    public static final String AUTHORITY_SERVICE = "https://authority.prod.ibis.comcast.com";

    /**
     * The constant KINGFISHER_SERVICE.
     */
    public static final String KINGFISHER_SERVICE = "kingfisher.prod.ibis.comcast.com";

    /**
     * The enum Redirector type.
     */
    public enum RedirectorType {
        /**
         * Bigsur redirector type.
         */
        bigsur,
        /**
         * Toledo redirector type.
         */
        toledo,
        /**
         * None redirector type.
         */
        none
    }
}

