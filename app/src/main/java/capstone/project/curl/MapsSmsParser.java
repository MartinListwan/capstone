package capstone.project.curl;

import capstone.project.curl.Models.MapsApi.NavigationModel;

public class MapsSmsParser {

    public static NavigationModel ParseSmsForNavigation(String apiResponseForNavigation){
        return new NavigationModel(apiResponseForNavigation);
    }


}
