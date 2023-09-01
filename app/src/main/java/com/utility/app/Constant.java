package com.utility.app;

public class Constant {
    public static String BaseURL = "https://backend-uar.azurewebsites.net/"; //client
    public static final String sdkVersion = "1.0.0";
    //< START EVENT'S NAME>
    public static final String CHAT_IMAGE = "utilityar_image";
    public static final String CHAT_BACK_TO_VIDEO = "utilityar_backtovideo";
    public static final String STROKE_UPDATE = "stroke_update";
    public static final String CLEAR_DRAWING = "clear_board";
    //< END EVENT'S NAME>

    public static final String endpoint = "https://utility-ar.communication.azure.com";//US
//    public static final String endpoint = "https://ukutilar.communication.azure.com";//UK

    public static final String STORAGE_CONTAINER = "utilityar-storage";

    //NON INDIA
    //    public static final String connectionString = "DefaultEndpointsProtocol=https;AccountName=utar;AccountKey=tWHRstq9mdQZSyJbe4hK1i+ejEHgZaRoYcAz1IlbuZcd2Eo43gkT/TH0oIvtdEL7FUdtSE+Z8XiR+ASt5jKTzQ==;EndpointSuffix=core.windows.net";
//    public static final String imagePath = "https://utar.blob.core.windows.net/utilityar-storage/";

    //india 2
//    public static final String connectionString = "DefaultEndpointsProtocol=https;AccountName=uarfastx;AccountKey=bLj1EsUHuwTCzzT2AOAYBugn4Leqp4YfjAVc4slQDNQKv24CnIEudRQsH6lW+iOlrTvfOi5OICnc+AStog+ztg==;EndpointSuffix=core.windows.net";
//    public static final String imagePath = "https://uarfastx.blob.core.windows.net/utilityar-storage/";

    //UK 3
    public static final String connectionString = "DefaultEndpointsProtocol=https;AccountName=northeuropear;AccountKey=339kCknemCobYc2IwyopICznsm2RBZII6ExedQzTRQfLDs+Jgdb2RdmdhOQroib6aOlbmz5PErF4+AStmp0nRA==;EndpointSuffix=core.windows.net";
    public static final String imagePath = "https://northeuropear.blob.core.windows.net/utilityar-storage/";

    public static final String APPLICATION_ID = "Utility-AR-CHAT";
    public static final String SDK_NAME = "azure-communication-com.azure.android.communication.chat";
}
