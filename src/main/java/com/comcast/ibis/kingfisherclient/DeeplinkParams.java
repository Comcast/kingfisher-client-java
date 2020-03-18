package com.comcast.ibis.kingfisherclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

/**
 * The type Deeplink params.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeeplinkParams {

    private boolean enableDebugMode;
    private boolean dryRun;
    private Map<String, String> additionalParams;
    private boolean disableVoiceOut;
    private String voiceGuidanceMode;
    private boolean notStackable;
    private boolean enableScreensaver;

    private DeeplinkParams(DeeplinkParamsBuilder builder) {
        this.enableDebugMode = builder.enableDebugMode;
        this.dryRun = builder.dryRun;
        this.additionalParams = builder.additionalParams;
        this.notStackable = builder.notStackable;
        this.voiceGuidanceMode = builder.voiceGuidanceMode;
        this.disableVoiceOut = builder.disableVoiceOut;
        this.enableScreensaver = builder.enableScreenSaver;
    }

    /**
     * New builder deeplink params builder.
     *
     * @return the deeplink params builder
     */
    public static DeeplinkParamsBuilder newBuilder() { return new DeeplinkParamsBuilder(); }


    /**
     * The type Deeplink params builder.
     */
    public static class DeeplinkParamsBuilder {
        private boolean enableDebugMode;
        private boolean dryRun;
        private Map<String, String> additionalParams;
        private boolean disableVoiceOut;
        private String voiceGuidanceMode;
        private boolean notStackable;
        private boolean enableScreenSaver;

        /**
         * Instantiates a new Deeplink params builder.
         */
        public DeeplinkParamsBuilder() { }

        /**
         * Sets enable debug mode.
         *
         * @param enableDebugMode the enable debug mode
         * @return the enable debug mode
         */
        public DeeplinkParamsBuilder setEnableDebugMode(Boolean enableDebugMode) {
            this.enableDebugMode = enableDebugMode;
            return this;
        }

        /**
         * Sets enable screen saver.
         *
         * @param enableScreenSaver the enable screen saver
         * @return the enable screen saver
         */
        public DeeplinkParamsBuilder setEnableScreenSaver(Boolean enableScreenSaver) {
            this.enableScreenSaver = enableScreenSaver;
            return this;
        }

        /**
         * Sets disable voice out.
         *
         * @param disableVoiceOut the disable voice out
         * @return the disable voice out
         */
        public DeeplinkParamsBuilder setDisableVoiceOut(Boolean disableVoiceOut) {
            this.disableVoiceOut = disableVoiceOut;
            return this;
        }

        /**
         * Sets not stackable.
         *
         * @param notStackable the not stackable
         * @return the not stackable
         */
        public DeeplinkParamsBuilder setNotStackable(Boolean notStackable) {
            this.notStackable = notStackable;
            return this;
        }

        /**
         * Sets dry run.
         *
         * @param dryRun the dry run
         * @return the dry run
         */
        public DeeplinkParamsBuilder setDryRun(Boolean dryRun) {
            this.dryRun = dryRun;
            return this;
        }

        /**
         * Sets voice guidance mode.
         *
         * @param voiceGuidanceMode the voice guidance mode
         * @return the voice guidance mode
         */
        public DeeplinkParamsBuilder setVoiceGuidanceMode(String voiceGuidanceMode) {
            this.voiceGuidanceMode = voiceGuidanceMode;
            return this;
        }

        /**
         * Sets additional params.
         *
         * @param additionalParams the additional params
         * @return the additional params
         */
        public DeeplinkParamsBuilder setAdditionalParams(Map<String, String> additionalParams) {
            this.additionalParams = additionalParams;
            return this;
        }

        /**
         * Build deeplink params.
         *
         * @return the deeplink params
         */
        public DeeplinkParams build() {
            return new DeeplinkParams(this);
        }

    }
}
