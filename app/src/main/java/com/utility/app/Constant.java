package com.utility.app;

public class Constant {
    public static String BaseURL = "https://backend-observation.azurewebsites.net/"; //client
    public static final String sdkVersion = "1.0.0";

    public static final String STORAGE_CONTAINER = "utilityar-observation";
    public static final String connectionString = "DefaultEndpointsProtocol=https;AccountName=northeuropear;AccountKey=339kCknemCobYc2IwyopICznsm2RBZII6ExedQzTRQfLDs+Jgdb2RdmdhOQroib6aOlbmz5PErF4+AStmp0nRA==;EndpointSuffix=core.windows.net";
    //intent PARAMS
    public static final String menuID = "menuID";
    public static final String procedureID = "procedureID";
    public static final String stepID = "stepID";
    public static final String SCREEN_FROM_STEPS = "from";
    public static final String SCREEN_FROM_STEPS_CAMERA = "from_camera_allow";
    public static final String STEP_INDEX = "stepindex";
    /*media types*/
    public static final String IMAGE = "IMAGE";
    public static final String VIDEO = "VIDEO";


    public enum userTrackerAction {
        SCREEN_OPEN {
            @Override
            public String toString() {
                return "SCREEN_OPEN";
            }
        },
        BUTTON_CLICKED {
            @Override
            public String toString() {
                return "BUTTON_CLICKED";
            }
        }, SELECT {
            @Override
            public String toString() {
                return "SELECT";
            }
        }, TEXT_INPUT {
            @Override
            public String toString() {
                return "TEXT_INPUT";
            }
        }, IMAGE_TAKEN {
            @Override
            public String toString() {
                return "IMAGE_TAKEN";
            }
        }, VIDEO_TAKEN {
            @Override
            public String toString() {
                return "VIDEO_TAKEN";
            }
        }, HOLO_SEEN {
            @Override
            public String toString() {
                return "HOLO_SEEN";
            }
        }, PRIORITY {
            @Override
            public String toString() {
                return "PRIORITY";
            }
        },
    }


    public enum Country {

        DE {
            @Override
            public String toString() {
                return "Germany";
            }
        },
        IT {
            @Override
            public String toString() {
                return "Italy";
            }
        },
        US {
            @Override
            public String toString() {
                return "United States";
            }
        }

    }
}
